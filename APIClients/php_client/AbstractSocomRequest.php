<?php
require_once 'HTTP/Request2.php';

abstract class AbstractSocomRequest {
	protected $SocomBaseURL = 'http://127.0.0.1:7999/';
	protected $cookieJar;
	
	private $validateSocomUserURL = 'servlet/user/validateUser';
	private $logoutSocomUserURL = 'servlet/user/logout';
	
	
	abstract protected function initURLs();
	
	function __construct() {
		$this->cookieJar = true;		
		
		$this->validateSocomUserURL = $this->SocomBaseURL . $this->validateSocomUserURL;
		$this->logoutSocomUserURL = $this->SocomBaseURL . $this->logoutSocomUserURL;
		$this->initURLs();
	}
	
	function sendSocomRequest($SocomUrl, $SocomQueryVariables) {
		try {
			$request = new HTTP_Request2($SocomUrl, HTTP_Request2::METHOD_GET);
			$request->setCookieJar($this->cookieJar);
			$url = $request->getUrl();
			
			if($SocomQueryVariables != null) {
				foreach ($SocomQueryVariables as $variableName => $variableValue) {
				    $url->setQueryVariable($variableName, $variableValue);
				}
			}
			
			$jsonResponse = $request->send()->getBody();			
			$this->cookieJar = $request->getCookieJar();
			return json_decode($jsonResponse);
		} catch (HTTP_Request2_Exception $ex) {
		    echo $ex;
		}
	}
	
	function validateUser($SocomQueryVariables) {
		$this->sendSocomRequest($this->validateSocomUserURL, $SocomQueryVariables);
	}
	
	function logoutUser() {
		$this->sendSocomRequest($this->logoutSocomUserURL, null);
	}
}
?>
