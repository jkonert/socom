#include "SocomAccess.h"
#include <iostream>
#include <windows.h>

SocomAccess::SocomAccess(){
	//TODO read XML File with already registred users on this client
	SocomUser tempUser("9","3md54z7epl244f9rrtom_@!peg2lkh","Testuser","1234");
	users.insert(users.end(),tempUser);
}

string SocomAccess::getNetworkFriends(LPCWSTR wchar_NetworkName){
	//httpConnection.setHTTPHeaderAttribut("JSESSIONID=" + actualUser->getCookie());
	httpConnection.sendHTTPRequest(Socom_SERVERADRESS,GETFRIENDS,"");
	string str_friends = httpConnection.getHttpResult();
	OutputDebugStringA(str_friends.c_str());
	return str_friends;
}

bool SocomAccess::postOnWall(string str_message,LPCWSTR wchar_NetworkName){
	// TODO Test
	string str_messageAttribute = "message=";
	str_messageAttribute+= str_message;
	httpConnection.sendHTTPRequest(Socom_SERVERADRESS,PUBLISHONFEED,(char*)str_messageAttribute.c_str());
	string str_result = httpConnection.getHttpResult();
	OutputDebugStringA(str_result.c_str());
	return true;
}

bool SocomAccess::createUser(string str_name,string str_game,string str_version){
	string str_createAttribut = str_name;
	str_createAttribut+= "&";
	str_createAttribut+= str_game;
	str_createAttribut+= "&";
	str_createAttribut+= str_version;
	httpConnection.sendHTTPRequest(Socom_SERVERADRESS,CREATEUSER,(char*)str_createAttribut.c_str());
	// TODO error handling when user cannot be created
	return true;
}

string SocomAccess::getProfileData(LPCWSTR wchar_NetworkName){
	// TODO Filter the data from the specified network
	httpConnection.sendHTTPRequest(Socom_SERVERADRESS,GETPROFILEDATA,"");
	string str_profileData = httpConnection.getHttpResult();
	OutputDebugStringA(str_profileData.c_str());
	return str_profileData;
}

bool SocomAccess::loginToSocomMiddleware(){
	
	OutputDebugStringA("loginToSocom");
	string str_parameter;
	str_parameter+= "id=" + actualUser->getUID();
	str_parameter+= "&secret=" + actualUser->getSecretCode();
	//httpConnection.sendHTTPRequest(Socom_SERVERADRESS,VALIDATEUSER,"id=7&secret=iah5@5xq[1xp[c350o@n@d2o4xo1!n");
	httpConnection.sendHTTPRequest(Socom_SERVERADRESS,VALIDATEUSER,(char *)str_parameter.c_str());
	string str_cookie = httpConnection.getHttpResult();
	if(str_cookie.compare("{\"valid\":true}") == 0){
		actualUser->setCookie(httpConnection.getHttpHeaderAttribut("JSESSIONID=",';'));
		return true;
	}
	return false;
}

bool SocomAccess::loginToSocomAPI(string str_username,string str_password){
	for(int i = 0;i<users.size();i++){
		
		if(users[i].getUsername() == str_username && users[i].getPassword() == str_password){
			actualUser = &users[i];
			return true;
		}
	}
	return false;
}

SocomUser* SocomAccess::getActualUser(){
	return this->actualUser;
}
