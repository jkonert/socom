#pragma once
#include <string>
#include <vector>
#include <QObject>
#include <SocomUser.h>
#include "Network.h"

static const LPCWSTR VALIDATEUSER = L"user/validateuser";
static const LPCWSTR CREATEUSER = L"user/createuser";

static const LPCWSTR GETFRIENDS = L"social/getNetworkFriends";
static const LPCWSTR PUBLISHONFEED = L"social/publishOnFeed";
static const LPCWSTR GETPROFILEDATA = L"social/getProfileData";

static const LPCWSTR Socom_SERVERADRESS = L"ktx-software.com";

static const LPCWSTR FACEBOOK = L"FACEBOOK";
static const LPCWSTR STUDIVZ = L"STUDIVZ";


using namespace std;


class SocomAccess : public QObject
{
Q_OBJECT
public:
	SocomAccess();
	bool loginToSocomMiddleware();
	bool loginToSocomAPI(string str_userName,string str_password);
	SocomUser* getActualUser();
	string getNetworkFriends(LPCWSTR wchar_NetworkName);
	bool postOnWall(string str_message,LPCWSTR wchar_NetworkName);
	bool createUser(string name,string game,string version);
	string getProfileData(LPCWSTR wchar_NetworkName);
private:
	Network httpConnection;
	vector<SocomUser> users;
	SocomUser *actualUser;
};

