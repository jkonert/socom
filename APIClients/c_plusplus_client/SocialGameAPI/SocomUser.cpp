#include "SocomUser.h"

SocomUser::SocomUser(string string_UID,string str_secretCode,string str_userName,string str_password){
	this->string_UID = string_UID;
	this->str_secretCode = str_secretCode;
	this->str_userName = str_userName;
	this->str_password = str_password;
}

string SocomUser::getUsername(){
	return this->str_userName;
}

string SocomUser::getPassword(){
	return this->str_password;
}

string SocomUser::getUID(){
	return this->string_UID;
}

string SocomUser::getSecretCode(){
	return this->str_secretCode;
}

string SocomUser::getCookie(){
	return this->str_cookie;
}
	
void SocomUser::setUsername(string str_userName){
	this->str_userName = str_userName;
}

void SocomUser::setPassword(string str_password){
	this->str_password = str_password;
}
	
void SocomUser::setUID(string string_UID){
	this->string_UID = string_UID;
}	

void SocomUser::setCookie(string str_cookie){
	this->str_cookie = str_cookie;
}
