using System;
using System.Net;
using System.IO;

namespace c_sharp_client
{
	public class SocomHttpRequest
	{
		private CookieContainer cookieContainer;
		
		public SocomHttpRequest()
		{
			cookieContainer = new CookieContainer();
		}
		
		public string SendRequest(string url)
		{
			HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
			request.CookieContainer = cookieContainer;
			
			HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			cookieContainer.Add(response.Cookies);
			
			String responseBody = getResponseBody(response);
			
			return responseBody;
		}
		
		private string getResponseBody(HttpWebResponse response) {
			Stream responseStream = response.GetResponseStream();
			StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.GetEncoding("utf-8"));
			
			return reader.ReadToEnd();
		}
	}
}

