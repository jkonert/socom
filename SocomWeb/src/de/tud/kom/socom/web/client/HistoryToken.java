package de.tud.kom.socom.web.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

import de.tud.kom.socom.web.client.AppController.Presenters;
import de.tud.kom.socom.web.client.util.exceptions.InvalidCodeStateException;
import de.tud.kom.socom.web.client.util.exceptions.ParsingException;

/** encapsulated handling of #xyz/abc/whatever tokens of History. Accepts leading / as well: #/xyz/abc/
 * 
 * @author jkonert
 *
 */
 public final class HistoryToken
{

	public static final String SEPERATOR = "/";

	private static final int INDEX_GAME_PART = 0;
	private static final int INDEX_APP_PART = 1;
	private static final int INDEX_APP_PART_MODULE = 2;
	private static final int INDEX_APP_PART_MODULE_ACTION = 3;

	private static final int MAX_INDEX_SUPPORTED = 100;

	private static final String KEYVALUEPAIRS_SEPERATOR = "|";
	private static final String KEYVALUE_SEPERATOR = "=";
	 
	private String[] parts;
	
	public HistoryToken(String game, Presenters presenter, String... parts)
	{
		if (parts == null) parts = new String[0]; // for easier coding...
		this.parts = new String[parts.length+2];
		setGamePart(game, true);
		setPresenter(presenter, false);
		for (int i =0; i<parts.length; i++)
		{
			setPart(i+1, parts[i], false);
		}
	}
	
	private HistoryToken (String[] parts)
	{
		this.parts = parts;
	}
	
//	private HistoryToken()
//	{
//		parts = new String[0];
//	}
	
	/** Will not check the consistency of parameters but directly split and set the parts and return
	 * 
	 * @param tokenstring
	 * @return
	 */
	public static HistoryToken fromUrlHistoryTokenTrusted(String tokenstring)
	{
		String[] parts = null;
		if (tokenstring == null || tokenstring.equals("")) parts = new String[0];				
		while (tokenstring.startsWith(SEPERATOR))
		{
			tokenstring = tokenstring.substring(1);
		}
		while (tokenstring.endsWith(SEPERATOR))
		{
			tokenstring = tokenstring.substring(0, tokenstring.length()-1);
		}
		parts = tokenstring.split(SEPERATOR);		
		return new HistoryToken(parts);	
	}
	
	/**
	 * @throws ParsingException in case some parameters are not properly set
	 * @param tokenstring
	 * @return
	 */
	public static HistoryToken fromUrlHistoryToken(String tokenstring)
	{				
		HistoryToken tmp = HistoryToken.fromUrlHistoryTokenTrusted(tokenstring);
		tmp.setPresenter(tmp.getPresenter(Presenters.content), false); // only as a check
		// more checks to come...
		return tmp; 
	}
	
	/**  
	 * @return  the HistoryString similar to pattern #Game/Presenter/Module/Action/Parameter1/Parameter2.
	 */
	public String toHistoryTokenString()
	{
		StringBuilder st = new StringBuilder();
		StringBuilder st2 = new StringBuilder();
		for (String p: parts)
		{
			if (p == null) // buffer params in case only nulls follow...then do not add
			{
				st2.append(p).append(SEPERATOR);
				continue;
			}
			else if (st2.length() > 0) // append buffered params first then add the current one
			{
				st.append(st2);
				st2.delete(0, st2.length());
			}
			st.append(p).append(SEPERATOR); // add current parameter
		}
		if (st.length() > 0) st.deleteCharAt(st.length()-1);
		if (st.length() > 0 && !String.valueOf(st.charAt(0)).equals(SEPERATOR)) st.insert(0, SEPERATOR);  // add a leading "/"
		return st.toString();
	}
	
	/**
	 *  returns the Module part of  #Game/Presenter/Module/Action/Parameter1/Parameter2/Parameter3
	 * @return
	 */
	public String getGamePart()
	{
		return getPart(INDEX_GAME_PART);
	}
	
	public void setGamePart(String game, boolean clearChildParts)
	{		
		setPart(INDEX_GAME_PART, game, clearChildParts);
	}
	
	/**
	 *  returns the Presenter part of  #Game/Presenter/Module/Action/Parameter1/Parameter2/Parameter3  or NULL
	 * @return
	 */
	public Presenters getPresenter()
	{
		return Presenters.findAppPart(getPart(INDEX_APP_PART), null);
	}
	
	/**
	 *  returns the Presenter part of  #Game/Presenter/Module/Action/Parameter1/Parameter2/Parameter3  or the given default in case non found
	 * @return
	 */
	public Presenters getPresenter(Presenters defaultValue)
	{
		return Presenters.findAppPart(getPart(INDEX_APP_PART), defaultValue);
	}
	
	public void setPresenter(Presenters part, boolean clearChildParts)
	{
		if (part == null) throw new UnsupportedOperationException("Presenter should not be null");
		setPart(INDEX_APP_PART,part.name(), clearChildParts);
	}
	
	/**
	 *  returns the Module part of  #Game/Presenter/Module/Action/Parameter1/Parameter2/Parameter3
	 * @return
	 */
	public String getPresenterModule()
	{
		return getPart(INDEX_APP_PART_MODULE);
	}
	
	public void setPresenterModule(String module, boolean clearChildParts)
	{		
		setPart(INDEX_APP_PART_MODULE, module, clearChildParts);
	}
	
	/**
	 *  returns the Action part of  #Game/Presenter/Module/Action/Parameter1/Parameter2/Parameter3
	 * @return
	 */
	public String getPresenterModuleAction()
	{
		return getPart(INDEX_APP_PART_MODULE_ACTION);
	}
	
	public void setPresenterModuleAction(String action, boolean clearChildParts)
	{		
		setPart(INDEX_APP_PART_MODULE_ACTION, action, clearChildParts);
	}
	
	/** returns an array of all further parameters after #Presenter/Module/Action/
	 * 
	 * @return empty String[] or String[] containing all parameters after Action...
	 */
	public String[] getPresenterModuleActionParameters()
	{
		if (parts.length >= INDEX_APP_PART_MODULE_ACTION+1)
		{
			String[] params = new String[parts.length-INDEX_APP_PART_MODULE_ACTION];
			for (int i=0; i < params.length; i++)
			{
				params[i] = parts[i+INDEX_APP_PART_MODULE_ACTION];
			}
			return params;
		}
		else return new String[0];
	}
	
	
	/** returns athe requested parameter after #Presenter/Module/Action/  like e.g.  #Game/Presenter/Module/Action/Parameter0/Parameter1/Parameter2 
	 * @throws UnsupportedOperationException if index is < 0.
	 * @param index   index of parameter to return 0....n
	 * @return an empty string or the parameter in URL after the Action (starting with Parameter 0)
	 */
	public String getPresenterModuleActionParameter(int index)
	{
		if (index < 0) throw new InvalidCodeStateException("Index cannot be below zero ("+index+")");
		if (parts.length >= INDEX_APP_PART_MODULE_ACTION+index+1)
		{
			return parts[INDEX_APP_PART_MODULE_ACTION+index];
		}
		else return "";
	}
		
	
	public void setPresenterModuleActionParameter(int index, String value, boolean clearChildParts)
	{
		if (index < 0) throw new InvalidCodeStateException("Index cannot be below zero ("+index+")");
		setPart(INDEX_APP_PART_MODULE_ACTION+index,value,clearChildParts);
	}
	
	/** returns ONE of the parameters of the Presenter/Module/Action  parameters as key=value|key2=value2|.. parsed Map<String,String> of key->value pairs.
	 * Keys may not be null for this. Values may not contain any of the seperator chars. 
	 * @throws ParsingException in case no parsing is possible.  If only ONE value without any "=" is found the exception is thrown as well.
	 * @param index   index of parameter to return as a map 0....n
	 * @return an empty map in case the parameter is empty, otherwise am key/value map of the parsed parameter. values can be null
	 */
	public Map<String,String> getPresenterModuleActionParameterKeyValuePairs(int index)
	{
		String part = getPart(index);
		HashMap<String, String> map = new HashMap<String, String>();
		if (part.length() == 0) return map;
		String[] keyvaluepairs = part.split(KEYVALUEPAIRS_SEPERATOR);
		for (String kv:keyvaluepairs)
		{
			String[] keyvalue = kv.split(KEYVALUE_SEPERATOR);
			if (keyvalue.length != 2) throw new ParsingException("Key-Value_Pairs did not match the pattern <key>"+KEYVALUE_SEPERATOR+"<value>"+KEYVALUEPAIRS_SEPERATOR+"<key2>"+KEYVALUE_SEPERATOR+"<value2>");
			if (keyvalue[0] == null || keyvalue[0] == "") throw new ParsingException("KEY in Key-Value-Pair is missing ("+kv+")");
			if (map.containsKey(keyvalue[0])) throw new ParsingException("KEY is more than once given; not unique ("+kv+")");
			map.put(keyvalue[0],  keyvalue[1]);
		}
		return map;
	}
		
	
	public void setPresenterModuleActionParameterKeyValueMap(int index, Map<String, String> keyValuePairs, boolean clearChildParts)
	{
		if (keyValuePairs == null ||keyValuePairs.size() == 0) 
		{
			setPart(index, keyValuePairs == null?null:"", clearChildParts);
			return;
		}
		StringBuilder sb = new StringBuilder();				
		for (String key: keyValuePairs.keySet())
		{
			if (key.contains(KEYVALUE_SEPERATOR) || key.contains(KEYVALUEPAIRS_SEPERATOR)) throw new ParsingException("Key ("+key+") may not contain the chars for seperating Key-Value-Pairs ("+KEYVALUE_SEPERATOR+" "+KEYVALUEPAIRS_SEPERATOR+")");
			String value = keyValuePairs.get(key);
			if (value != null && (value.contains(KEYVALUE_SEPERATOR) || value.contains(KEYVALUEPAIRS_SEPERATOR))) throw new ParsingException("Value ("+value+") may not contain the chars for seperating Key-Value-Pairs ("+KEYVALUE_SEPERATOR+" "+KEYVALUEPAIRS_SEPERATOR+")");
			sb.append(key).append(KEYVALUE_SEPERATOR).append(value).append(KEYVALUEPAIRS_SEPERATOR);
		}
		if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
		setPart(index, sb.toString(), clearChildParts);
	}
	
	/** direct access to Token-Parts without semantics. 
	 * @throws UnsupportedOperationException if index is < 0.
	 * @param index  0...n  where 0 means the Presenter #Game/Presenter/Module/Action/Parameter0/Parameter1/Parameter2 
	 * @return  the requested part or an empty String
	 */
	public String getTokenPart(int index)
	{
		return getPart(index);
	}
	
	/**
	 * direct access to Token-Parts setting without semantics
	 * @param index 0...n   where 0 means the Presenter in #Game/Presenter/Module/Action/Parameter0/Parameter1/Parameter2 
	 * @param value
	 * @param clearChildParts
	 */
	public void setTokenPart(int index, String value, boolean clearChildParts)
	{
		if (index < 0) throw new InvalidCodeStateException("Index cannot be below zero ("+index+")");
		setPart(index,value,clearChildParts);
	}
	
	public HistoryToken clone()
	{
		return HistoryToken.fromUrlHistoryTokenTrusted(this.toHistoryTokenString());
	}


	private void setPart(int index, String value, boolean clearChildParts) {
		if (index > MAX_INDEX_SUPPORTED) throw new InvalidCodeStateException("index is to big ("+index+"). Supports maximum index of "+MAX_INDEX_SUPPORTED);
		if (value != null && value.contains(SEPERATOR)) throw new ParsingException("value may not contain Seperator characters ("+SEPERATOR+")");
		if (parts.length <= index)
		{ // resize array
			String[] tmp = new String[index+1];
			for (int i=0; i < parts.length; i++)
			{
				tmp[i] = parts[i];
			}
			parts = tmp;
		}
		parts[index] = value;
		if (clearChildParts)
		{
			for (int i=index+1; i < parts.length; i++)
			{
				parts[i] = null;
			}
		}		
	}

	private String getPart(int index) {
		if (index < 0) throw new InvalidCodeStateException("Index cannot be below zero ("+index+")");
		if (parts.length <= index) return "";
		String p = parts[index];
		return (p == null) ? "" : p;
	}

	public static HistoryToken fromValueChangeEvent(
		ValueChangeEvent<String> event) {
		return HistoryToken.fromUrlHistoryToken(event.getValue());
	}
	
}
