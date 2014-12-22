#pragma once
#include <string>
using namespace std;

class SocomUser
{
public:
	SocomUser(string string_UID,string str_SecretCode,string str_userName,string str_password);
	string getUsername();
	string getPassword();
	string getUID();
	string getSecretCode();
	string getCookie();
	void setUsername(string str_userName);
	void setPassword(string str_password);
	void setUID(string string_UID);
	void setSecretCode(string str_secretCode);
	void setCookie(string str_cookie);
private:
	string string_UID;
	string str_secretCode;
	string str_userName;
	string str_password;
	string str_cookie;
	bool bool_loggedInSocom;
};

