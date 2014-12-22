using System;
using System.Web.Script.Serialization;

namespace c_sharp_client
{
	public class SocomAccess
	{
		private const string SocomBaseURL = "http://127.0.0.1:7999/";
		
		private const string ValidateUserURL = "servlet/user/validateUser";
		private const string LogoutUserURL = "servlet/user/logout";
		
		private const string UpdateAchievementProgressURL = "servlet/achievements/updateAchievementProgress";
		private const string GetAchievementProgressURL = "servlet/achievements/getAchievementProgress";
		private const string GetAchievementURL = "servlet/achievements/getAchievement";
		
		private SocomHttpRequest SocomRequest;
		private JavaScriptSerializer jsonParser;
		
		
		public SocomAccess ()
		{
			SocomRequest = new SocomHttpRequest();
			jsonParser = new JavaScriptSerializer();
		}
		
		public SocomResponse.User validateUser(string userName, string userPassword, string gameName, string gamePassword, string gameVersion)
		{
			string parameters = "?username=" + userName +
				"&password=" + userPassword +
				"&game=" + gameName +
				"&gamepassword=" + gamePassword +
				"&version=" + gameVersion;
			string jsonResponse = SocomRequest.SendRequest(SocomBaseURL + ValidateUserURL + parameters);			
			
			return (SocomResponse.User)jsonParser.Deserialize<SocomResponse.User>(jsonResponse);
		}
		
		public SocomResponse.Success logoutUser()
		{
			string jsonResponse = SocomRequest.SendRequest(SocomBaseURL + LogoutUserURL);
			
			return (SocomResponse.Success)jsonParser.Deserialize<SocomResponse.Success>(jsonResponse);
		}
		
		public SocomResponse.AchievementProgressMessage updateAchievementProgress(string achievementName, string gameName, string gamePassword, long counter)
		{
			string parameters = "?achievementname=" + achievementName +
				"&gamename=" + gameName +
				"&gamepassword=" + gamePassword +
				"&counter=" + counter;
			string jsonResponse = SocomRequest.SendRequest(SocomBaseURL + UpdateAchievementProgressURL + parameters);			
			
			return (SocomResponse.AchievementProgressMessage)jsonParser.Deserialize<SocomResponse.AchievementProgressMessage>(jsonResponse);
		}
		
		public SocomResponse.AchievementProgress getAchievementProgress(string achievementName, string gameName, string gamePassword)
		{
			string parameters = "?achievementname=" + achievementName +
				"&gamename=" + gameName +
				"&gamepassword=" + gamePassword;
			string jsonResponse = SocomRequest.SendRequest(SocomBaseURL + GetAchievementProgressURL + parameters);			
			
			return (SocomResponse.AchievementProgress)jsonParser.Deserialize<SocomResponse.AchievementProgress>(jsonResponse);
		}
		
		public SocomResponse.Achievement getAchievement(string achievementName, string gameName, string gamePassword)
		{
			string parameters = "?achievementname=" + achievementName +
				"&gamename=" + gameName +
				"&gamepassword=" + gamePassword;
			string jsonResponse = SocomRequest.SendRequest(SocomBaseURL + GetAchievementURL + parameters);			
			
			return (SocomResponse.Achievement)jsonParser.Deserialize<SocomResponse.Achievement>(jsonResponse);
		}
	}
}

