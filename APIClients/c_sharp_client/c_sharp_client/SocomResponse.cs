using System;

namespace c_sharp_client
{
	public class SocomResponse
	{
		public struct Success
		{
			public string success { get; set; }
		}
		
		public struct User
		{
			public string uid { get; set; }
		}
		
		public struct AchievementProgress
		{
			public string userID { get; set; }
			public string achievementID { get; set; }
			public string timeCompleted { get; set; }
			public string counter { get; set; }
			public string counterMax { get; set; }
			public string isCompleted { get; set; }
			public string currentLevel { get; set; }
			public string maxLevel { get; set; }		
		}
		
		public struct AchievementProgressMessage
		{
			public string hasChanged { get; set; }
			public string achievementName { get; set; }
			public string message { get; set; }
		}
		
		public struct Achievement
		{
			public string achievementid { get; set; }
			public string gameid { get; set; }
			public string categoryname { get; set; }
			public string name { get; set; }
			public string description { get; set; }
			public string image { get; set; }
			public AchievementLevel[] levels { get; set; }
		}
		
		public struct AchievementLevel
		{
			public string levelid { get; set; }
			public string achievementid { get; set; }
			public string countermax { get; set; }
			public string level { get; set; }
			public string rewardPoints { get; set; }
			public AchievementReward[] rewards { get; set; }	
		}
		
		public struct AchievementReward
		{
			public string rewardID { get; set; }
			public string name { get; set; }
			public string description { get; set; }
			public string value { get; set; }			
		}
	}
}

