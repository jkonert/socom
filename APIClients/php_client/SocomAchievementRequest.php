<?php
require_once 'AbstractSocomRequest.php';

class SocomAchievementRequest extends AbstractSocomRequest {

	private $updateAchievementProgressURL = 'servlet/achievements/updateAchievementProgress';
	private $getAchievementProgressURL = 'servlet/achievements/getAchievementProgress';

	protected function initURLs() {
		$this->updateAchievementProgressURL = $this->SocomBaseURL . $this->updateAchievementProgressURL;
		$this->getAchievementProgressURL = $this->SocomBaseURL . $this->getAchievementProgressURL;
	}

	function __construct() {
		parent::__construct();
	}
	
	function updateAchievementProgress($updateAchievementProgressQueryVariables, $validateSocomUserQueryVariables) {
		parent::validateUser($validateSocomUserQueryVariables);
		$jsonObject = parent::sendSocomRequest($this->updateAchievementProgressURL, $updateAchievementProgressQueryVariables);			
		parent::logoutUser();
		
		return $jsonObject;
	}
	
	function getAchievementProgress($getAchievementProgressQueryVariables, $validateSocomUserQueryVariables) {
		parent::validateUser($validateSocomUserQueryVariables);
		$jsonObject = parent::sendSocomRequest($this->getAchievementProgressURL, $getAchievementProgressQueryVariables);		
		parent::logoutUser();
		
		return $jsonObject;
	}
}
?>
