package de.tud.kom.socom.database.game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.socom.components.game.Game;
import de.tud.kom.socom.components.game.GameContext;
import de.tud.kom.socom.components.game.GameInstance;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.exceptions.ContextNotFoundException;
import de.tud.kom.socom.util.exceptions.CouldNotDeleteContextException;
import de.tud.kom.socom.util.exceptions.CouldNotDeleteGameException;
import de.tud.kom.socom.util.exceptions.CouldNotDeleteGameInstanceException;
import de.tud.kom.socom.util.exceptions.GameAlreadyExistException;
import de.tud.kom.socom.util.exceptions.GameContextAlreadyExistException;
import de.tud.kom.socom.util.exceptions.GameInstanceNotFoundException;
import de.tud.kom.socom.util.exceptions.GameNotAuthenticatedException;
import de.tud.kom.socom.util.exceptions.GameVersionAlreadyExistException;
import de.tud.kom.socom.util.exceptions.NoSNConnectionException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;

/**
 * 
 * @author rhaban
 * 
 */
public class HSQLGameDatabase extends HSQLDatabase implements GameDatabase {

	private static GameDatabase instance = new HSQLGameDatabase();

	private HSQLGameDatabase() {
		super();
	}

	public static GameDatabase getInstance() {
		return instance;
	}

	@Override
	public void addGame(Game game) throws SQLException, GameAlreadyExistException {
		long genreId = lazyInsert("gamegenres", game.getGenre());

		PreparedStatement selectQuery = db.getPreparedStatement("SELECT gameid FROM games WHERE name = ?;");

		PreparedStatement insertQuery = db.getPreparedStatement("INSERT INTO games (name, genre, password) VALUES (?, ?, ?);");
		selectQuery.setString(1, game.getName());
		if (selectQuery.executeQuery().next())
			throw new GameAlreadyExistException();

		insertQuery.setString(1, game.getName());
		insertQuery.setLong(2, genreId);
		insertQuery.setString(3, game.getPassword());
		insertQuery.execute();
	}

	@Override
	public void removeGame(String game) throws SQLException, CouldNotDeleteGameException {
		PreparedStatement deleteQuery = db.getPreparedStatement("DELETE FROM games WHERE name = ?;");
		deleteQuery.setString(1, game);
		try {
			deleteQuery.execute();
		} catch (SQLIntegrityConstraintViolationException e) {
			// There exists content in this context, so it cannot be deleted
			// without loosing the content
			throw new CouldNotDeleteGameException(game);
		}
	}

	@Override
	public Game getGame(long gameId) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT games.name, games.password, gamegenres.name AS genrename FROM gamegenres INNER JOIN "
				+ "games ON gamegenres.id = games.genre WHERE games.gameid = ?;");

		statement.setLong(1, gameId);
		ResultSet result = statement.executeQuery();
		if (!result.next())
			return null;

		Game game = new Game(gameId, result.getString("name"), result.getString("genrename"), result.getString("password"), new ArrayList<GameInstance>());

		statement = db.getPreparedStatement("SELECT gameinstances.version, gameinstances.description FROM gameinstances WHERE gameid = ?;");
		statement.setLong(1, gameId);
		result = statement.executeQuery();

		while (result.next()) {
			game.addInstance(new GameInstance(game.getName(), result.getString("version"), result.getString("description")));
		}

		return game;
	}

	@Override
	public void addInstance(long gameid, GameInstance gameinstance) throws SQLException, GameVersionAlreadyExistException {
		String version = gameinstance.getVersion();
		String description = gameinstance.getDescription();
		PreparedStatement selectQuery = db.getPreparedStatement("SELECT id FROM gameinstances WHERE gameid = ? AND version = ?;");
		PreparedStatement insertQuery = db.getPreparedStatement("INSERT INTO gameinstances (gameid, version, description, hits) "
				+ "VALUES (?, ?, ?, 0);");
		selectQuery.setLong(1, gameid);
		selectQuery.setString(2, version);
		if (selectQuery.executeQuery().next())
			throw new GameVersionAlreadyExistException();

		insertQuery.setLong(1, gameid);
		insertQuery.setString(2, version);
		insertQuery.setString(3, description);
		insertQuery.execute();
	}

	@Override
	public void removeInstance(long gameInstanceId) throws SQLException, CouldNotDeleteGameInstanceException {
		PreparedStatement deleteQuery = db.getPreparedStatement("DELETE FROM gameinstances WHERE id = ?;");
		deleteQuery.setLong(1, gameInstanceId);
		try {
			deleteQuery.execute();
		} catch (SQLIntegrityConstraintViolationException e) {
			// There exists content in this context, so it cannot be deleted
			// without loosing the content
			throw new CouldNotDeleteGameInstanceException(gameInstanceId);
		}
	}

	@Override
	public long getGameInstance(String gamename, String gameversion) throws SQLException, GameInstanceNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("SELECT " + 
					"gameinstances.id " + 
				"FROM " + 
					"gameinstances INNER JOIN games ON gameinstances.gameid = games.gameid " + 
				"WHERE " + 
					"games.name = ? AND gameinstances.version = ?;");
		statement.setString(1, gamename);
		statement.setString(2, gameversion);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			throw new GameInstanceNotFoundException();

		return result.getLong("id");
	}

	@Override
	public void addContext(long gameInstId, String extContextId, String name) throws SQLException, GameContextAlreadyExistException {
		PreparedStatement selectQuery = db.getPreparedStatement("SELECT id FROM gamecontexts WHERE externalid = ? AND gameinstid = ?;");
		PreparedStatement insertQuery = db.getPreparedStatement("INSERT INTO gamecontexts (externalid, gameinstid, name) VALUES (?,?,?);");
		selectQuery.setString(1, extContextId);
		selectQuery.setLong(2, gameInstId);
		if (selectQuery.executeQuery().next())
			throw new GameContextAlreadyExistException();

		insertQuery.setString(1, extContextId);
		insertQuery.setLong(2, gameInstId);
		insertQuery.setString(3, name);
		insertQuery.execute();
	}

	@Override
	public void removeContext(long gameInstId, String context) throws SQLException, ContextNotFoundException, CouldNotDeleteContextException {
		// Check if theses contexts exists in this game
		long contextId = getGameContextId(context, gameInstId);

		PreparedStatement deleteQuery = db.getPreparedStatement("DELETE FROM gamecontexts WHERE id = ?;");
		deleteQuery.setLong(1, contextId);
		try {
			deleteQuery.execute();
		} catch (SQLIntegrityConstraintViolationException e) {
			// There exists content in this context, so it cannot be deleted
			// without loosing the content
			throw new CouldNotDeleteContextException("Dependency-Violation.");
		}
	}

	@Override
	public long getGameContextId(String externalid, long gameInstance) throws SQLException, ContextNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("SELECT gamecontexts.id FROM gamecontexts WHERE gamecontexts.externalid = ? "
				+ "AND gamecontexts.gameinstid = ?;");
		statement.setString(1, externalid);
		statement.setLong(2, gameInstance);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			throw new ContextNotFoundException(externalid, gameInstance);

		return result.getLong("id");
	}

	@Override
	public GameContext getGameContext(long gameinstanceid, String contextId) throws SQLException, ContextNotFoundException {
		//FIXME multiple identical contextid's for different games
		String joinParent = " LEFT JOIN (SELECT externalid, id FROM gamecontexts) AS g1 ON g1.id = contextwithrelations.parentid ";
		String joinChild = " LEFT JOIN (SELECT externalid, id FROM gamecontexts) AS g2 ON g2.id = contextwithrelations.childid ";
		String table = "FROM ((gamecontexts "
				+ "LEFT JOIN (SELECT parentid, childid FROM gamecontextrelations) ON gamecontexts.id = childid OR gamecontexts.id = parentid) "
				+ "AS contextwithrelations " + joinParent + ") " + joinChild;
		String select = "SELECT id, externalid, description, name, autogenerated, g1.id AS parentid, g1.externalid AS parentexternalid, g2.id AS childid, g2.externalid AS childexternalid ";
		String where = " WHERE externalid = ? AND gameinstid = ?;";

		PreparedStatement statement = db.getPreparedStatement(select + table + where);
		statement.setString(1, contextId);
		statement.setLong(2, gameinstanceid);
		ResultSet rs = statement.executeQuery();

		if (!rs.next())
			throw new ContextNotFoundException();

		GameContext gs = null;
		do {
			if (gs == null)
				gs = new GameContext(rs.getString("externalid"), rs.getString("name"), rs.getBoolean("autogenerated"), new LinkedList<String>(),
						new LinkedList<String>(), rs.getString("description"));
			String contextName = gs.getIdent();
			String childId = rs.getString("childexternalid");
			String parentId = rs.getString("parentexternalid");
			if (childId == null || parentId == null)
				continue;
			if (childId.equals(contextName)) {
				gs.addPrevious(parentId);
			} else if (parentId.equals(contextName)) {
				gs.addNext(childId);
			}
		} while (rs.next());

		return gs;
	}

	@Override
	public List<GameContext> getGameContexts(long gameInstId) throws SQLException {
		List<GameContext> result = new ArrayList<GameContext>();

		PreparedStatement statement = db.getPreparedStatement("SELECT externalid FROM gamecontexts WHERE gameinstid = ?;");
		statement.setLong(1, gameInstId);
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			String id = rs.getString("externalid");
			try {
				result.add(getGameContext(gameInstId, id));
			} catch (ContextNotFoundException e) {
				// impossible
				LoggerFactory.getLogger().Error(e);
			}
		}
		return result;
	}

	@Override
	/**
	 * @return the id used for the scene
	 */
	public String autogenerateContext(long gameInstId, String name) throws SQLException, GameContextAlreadyExistException {
		String generatedId = name.replace(" ", "_").toLowerCase();

		PreparedStatement selectQuery = db.getPreparedStatement("SELECT id FROM gamecontexts WHERE externalid = ? AND gameinstid = ?;");
		selectQuery.setString(1, generatedId);
		selectQuery.setLong(2, gameInstId);
		if (selectQuery.executeQuery().next())
			return generatedId;

		PreparedStatement insertQuery = db.getPreparedStatement("INSERT INTO gamecontexts (externalid, gameinstid, name, "
				+ "autogenerated) VALUES (?, ?, ?, true);");
		insertQuery.setString(1, generatedId);
		insertQuery.setLong(2, gameInstId);
		insertQuery.setString(3, name);
		insertQuery.execute();
		return generatedId;
	}

	@Override
	public void addContextRelation(long gameInstId, String parent, String child, boolean autogenerated) throws SQLException, ContextNotFoundException {
		// Check if theses contexts exists in this game
		long parentId = getGameContextId(parent, gameInstId);
		long childId = getGameContextId(child, gameInstId);
		addContextRelation(gameInstId, parentId, childId, autogenerated);
	}

	@Override
	public void addContextRelation(long gameInstId, long parentId, long childId, boolean autogenerated) throws SQLException {
		PreparedStatement selectQuery = db.getPreparedStatement("SELECT parentid FROM gamecontextrelations WHERE parentid = ? AND childid = ?;");
		selectQuery.setLong(1, parentId);
		selectQuery.setLong(2, childId);
		if (selectQuery.executeQuery().next())
			return;

		PreparedStatement insertQuery = db.getPreparedStatement("INSERT INTO gamecontextrelations (parentid, childid, autogenerated)  VALUES  (?, ?, ?);");
		insertQuery.setLong(1, parentId);
		insertQuery.setLong(2, childId);
		insertQuery.setBoolean(3, autogenerated);
		insertQuery.execute();
	}

	@Override
	public boolean removeContextRelation(long gameInstId, String parent, String child) throws SQLException, ContextNotFoundException {
		// Check if theses contexts exists in this game
		long parentId = getGameContextId(parent, gameInstId);
		long childId = getGameContextId(child, gameInstId);

		PreparedStatement deleteQuery = db.getPreparedStatement("DELETE FROM gamecontextrelations WHERE parentid = ? AND childid = ?;");
		deleteQuery.setLong(1, parentId);
		deleteQuery.setLong(2, childId);
		deleteQuery.execute();

		return deleteQuery.getUpdateCount() > 0;
	}

	@Override
	public List<GameContext> getGameContextRelations(long gameInstId) throws SQLException {
		Hashtable<String, GameContext> table = new Hashtable<String, GameContext>();
		PreparedStatement statement = db.getPreparedStatement("SELECT parent.externalid AS parentID, parent.name AS parentName, parent.autogenerated, "
				+ "child.externalid AS childID, child.name AS childName, child.autogenerated FROM "
				+ "(gamecontextrelations INNER JOIN gamecontexts AS parent ON gamecontextrelations.parentid = parent.id) "
				+ "INNER JOIN gamecontexts AS child ON gamecontextrelations.childid = child.id WHERE parent.gameinstid = ? "
				+ "AND child.gameinstid = ?;");
		statement.setLong(1, gameInstId);
		statement.setLong(2, gameInstId);
		ResultSet result = statement.executeQuery();

		String parentId, childId;
		while (result.next()) {
			parentId = result.getString("parentID");
			childId = result.getString("childID");
			if (!table.containsKey(parentId))
				table.put(parentId, new GameContext(parentId, result.getString("parentName"), result.getBoolean("autogenerated"), new ArrayList<String>(),
						new ArrayList<String>()));
			if (!table.containsKey(childId))
				table.put(childId, new GameContext(childId, result.getString("childName"), result.getBoolean("autogenerated"), new ArrayList<String>(),
						new ArrayList<String>()));

			table.get(parentId).addNext(childId);
			table.get(childId).addPrevious(parentId);
		}

		List<GameContext> list = new ArrayList<GameContext>();
		for (GameContext context : table.values())
			list.add(context);

		return list;
	}
	
	@Override
	public GameContext getGameContextRelations(long gameInstId, String contextid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT * FROM gamecontexts WHERE externalid = ? AND gameinstid = ?;");
		statement.setString(1, contextid);
		statement.setLong(2, gameInstId);
		ResultSet rs = statement.executeQuery();
		rs.next();
		GameContext context = new GameContext(contextid, rs.getString("name"), rs.getBoolean("autogenerated"), new LinkedList<String>(), new LinkedList<String>(), rs.getString("description"));
		long cid = rs.getLong("id");
		
		statement = db.getPreparedStatement("SELECT * "
				+ "FROM gamecontextrelations INNER JOIN gamecontexts ON gamecontextrelations.childid = gamecontexts.id "
				+ "WHERE parentid = ?;");
		statement.setLong(1, cid);
		rs = statement.executeQuery();
		while(rs.next())
		{
			context.addNext(rs.getString("externalid"));
		}
		
		statement = db.getPreparedStatement("SELECT * "
				+ "FROM gamecontextrelations INNER JOIN gamecontexts ON gamecontextrelations.parentid = gamecontexts.id "
				+ "WHERE childid = ?;");
		statement.setLong(1, cid);
		rs = statement.executeQuery();
		while(rs.next())
		{
			context.addPrevious(rs.getString("externalid"));
		}
		
		return context;
	}

	@Override
	public long authenticateGame(String game, String gamepassword) throws SQLException, GameNotAuthenticatedException {
		PreparedStatement statement = db.getPreparedStatement("SELECT games.gameid FROM games WHERE games.name = ? AND games.password = ?;");
		statement.setString(1, game);
		statement.setString(2, gamepassword);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			throw new GameNotAuthenticatedException();

		return result.getLong("gameid");
	}

	@Override
	public long authenticateGameInstance(String game, String version, String gamepassword) throws SQLException, GameNotAuthenticatedException,
			GameInstanceNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("SELECT games.gameid FROM games WHERE games.name = ? AND games.password = ?;");
		statement.setString(1, game);
		statement.setString(2, gamepassword);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			throw new GameNotAuthenticatedException();

		return getGameInstance(game, version);
	}
	
	@Override
	public long authenticateGameInstance(long gameinstid, String gamepassword) throws SQLException, GameInstanceNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("SELECT " + 
				"gameinstances.id " + 
			"FROM " + 
				"gameinstances INNER JOIN games ON gameinstances.gameid = games.gameid " + 
			"WHERE " + 
				"gameinstances.id = ? AND games.password = ?;");
	statement.setLong(1, gameinstid);
	statement.setString(2, gamepassword);
	ResultSet result = statement.executeQuery();

	if (!result.next())
		throw new GameInstanceNotFoundException();

	return result.getLong("id");
	}

	@Override
	public boolean setInstanceImage(long instanceid, String imageFile) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE gameinstances SET image = ? WHERE id = ?;");
		statement.setString(1, imageFile);
		statement.setLong(2, instanceid);
		return  statement.executeUpdate() == 1;
	}

	@Override
	public boolean setContextImage(long instanceid, String contextid, String imageFile) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE gamecontexts SET image = ? WHERE gameinstid = ? AND externalid = ?;");
		statement.setString(1, imageFile);
		statement.setLong(2, instanceid);
		statement.setString(3, contextid);
		return statement.executeUpdate() == 1;
	}

	@Override
	public boolean setInstanceDescription(long gameInstanceId, String desc) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE gameinstances SET description = ? WHERE id = ?;");
		statement.setString(1, desc);
		statement.setLong(2, gameInstanceId);
		return statement.executeUpdate() == 1;
	}

	@Override
	public boolean setContextDescription(long gameinstanceid, String contextid, String description) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE gamecontexts SET description = ? WHERE externalid = ? AND gameinstid = ?;");
		statement.setString(1, description);
		statement.setString(2, contextid);
		statement.setLong(3, gameinstanceid);
		return statement.executeUpdate() == 1;
	}
	

	@Override
	public boolean addSNApp(String network, long gameId, long appId, String appSecret, boolean autofillRedirects,
			String token_redirect, String general_redirect) throws SQLException, GameAlreadyExistException {
		PreparedStatement statement = db.getPreparedStatement("SELECT appid FROM socialnetworkapps " +
				"WHERE gameid = ? AND sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setLong(1, gameId);
		statement.setString(2, network);
		ResultSet rs = statement.executeQuery();
		if(rs.next())
			throw new GameAlreadyExistException();
		rs.close();
		
		statement = db.getPreparedStatement("INSERT INTO socialnetworkapps " +
				"(gameid, sn, appid, appsecret, token_redirect_url, general_redirect_url) VALUES " +
				"(?,(SELECT id FROM socialnetworks WHERE name = ?),?,?,?,?)");
		statement.setLong(1, gameId);
		statement.setString(2, network);
		statement.setLong(3, appId);
		statement.setString(4, appSecret);
		if(autofillRedirects)
		{
			String baseUrl = ResourceLoader.buildPublicServerUrl();
			token_redirect = baseUrl + "/servlet/social/requestToken";
			general_redirect = baseUrl + "/web";
		}
		statement.setString(5, token_redirect);
		statement.setString(6, general_redirect);
		int rows = statement.executeUpdate();
		return rows == 1;
	}

	@Override
	public long getSNAppId(String socialnetwork, long gameinstid) throws SQLException, NoSNConnectionException {
		PreparedStatement statement = db.getPreparedStatement("SELECT appid FROM socialnetworkapps " +
				"WHERE gameid = (SELECT gameid FROM gameinstances WHERE id = ?)" +
				"AND sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setLong(1, gameinstid);
		statement.setString(2, socialnetwork);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new NoSNConnectionException();
		
		return rs.getLong(1);
	}

	@Override
	public String getSNTokenRedirectUrl(String socialnetwork, long gameinstid) throws SQLException, NoSNConnectionException{
		PreparedStatement statement = db.getPreparedStatement("SELECT token_redirect_url FROM socialnetworkapps " +
				"WHERE gameid = (SELECT gameid FROM gameinstances WHERE id = ?)" +
				"AND sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setLong(1, gameinstid);
		statement.setString(2, socialnetwork);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new NoSNConnectionException();
		
		return rs.getString(1);
	}

	@Override
	public String getSNAppSecret(String socialnetwork, long gameinstid) throws SQLException,
			NoSNConnectionException {
		PreparedStatement statement = db.getPreparedStatement("SELECT appsecret FROM socialnetworkapps " +
				"WHERE gameid = (SELECT gameid FROM gameinstances WHERE id = ?)" +
				"AND sn = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?));");
		statement.setLong(1, gameinstid);
		statement.setString(2, socialnetwork);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new NoSNConnectionException();
		
		return rs.getString(1);
	}

	@Override
	public String getGameIdentifier(long gameinstid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT REPLACE(LOWER(name),' ') FROM games " +
				"WHERE gameid = (SELECT gameid FROM gameinstances WHERE id = ?)");
		statement.setLong(1, gameinstid);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			return null;
		return rs.getString(1);
	}

	@Override
	public boolean connectSocialNetworkPage(long uid, long gameinstanceid, String network, String pageidentifier, String token) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("INSERT INTO socialnetworkpages " +
				"(snid, uid, gameinstanceid, pageidentifier, token) " +
				"VALUES ((SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?)), ?, ?, ?, ?);");
		statement.setString(1, network);
		statement.setLong(2, uid);
		statement.setLong(3, gameinstanceid);
		statement.setString(4, pageidentifier);
		statement.setString(5, token);
				
		return statement.executeUpdate() == 1;
	}

	@Override
	public String getSocialNetworkPageId(String network, long uid, long gameinstid) throws SQLException, SocialNetworkUnsupportedException {
		PreparedStatement statement = db.getPreparedStatement("SELECT pageidentifier FROM socialnetworkpages " +
				"WHERE snid = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?)) AND " +
				"uid = ? AND gid = ?;");
		statement.setString(1, network);
		statement.setLong(2, uid);
		statement.setLong(3, gameinstid);
		
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new SocialNetworkUnsupportedException(network + " (no page was found) ");
		
		return rs.getString(1);
	}

	@Override
	public String getSocialNetworkPageToken(String network, long uid, long gameinstid) throws SQLException, SocialNetworkUnsupportedException {
		PreparedStatement statement = db.getPreparedStatement("SELECT token FROM socialnetworkpages " +
				"WHERE snid = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?)) AND " +
				"uid = ? AND gid = ?;");
		statement.setString(1, network);
		statement.setLong(2, uid);
		statement.setLong(3, gameinstid);
		
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new SocialNetworkUnsupportedException(network + " (no page was found) ");
		
		return rs.getString(1);
	}

	@Override
	public String getGameIdentString(long gameinstid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT REPLACE(LOWER(name), ' ') FROM games " +
				"WHERE gameid = (SELECT gameid FROM gameinstances WHERE id = ?);");
		statement.setLong(1, gameinstid);
		
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			return "all";
		
		return rs.getString(1);
	}
}

