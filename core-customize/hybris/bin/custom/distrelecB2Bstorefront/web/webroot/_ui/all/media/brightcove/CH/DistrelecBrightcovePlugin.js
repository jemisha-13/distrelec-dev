(function() {
  var _experience
  , _videoPlayer
  , onTemplateReady
  , currentVideo
  , domainTrackId;
  var timeFactor = 30;
  var timeIntervall = 30;

 
  player = brightcove.api.getExperience();
  _experience = player.getModule(brightcove.api.modules.APIModules.EXPERIENCE);
  _videoPlayer = player.getModule(brightcove.api.modules.APIModules.VIDEO_PLAYER);
  
  if (_experience.getReady()) {
    initialize();
  } else {
    _experience.addEventListener(brightcove.api.events.ExperienceEvent.TEMPLATE_READY, initialize);
  }
 
  _experience.getExperienceID(function(pExperienceID) {
  });
  
  function initialize() {
    _videoPlayer.getCurrentVideo(function(pVideoDTO) {
      currentVideo = pVideoDTO;
    });
    addListeners();
  }

  function addListeners() {
	_videoPlayer.addEventListener(brightcove.api.events.MediaEvent.BEGIN, onMediaEventFired);
	_videoPlayer.addEventListener(brightcove.api.events.MediaEvent.COMPLETE, onMediaEventFired);
	_videoPlayer.addEventListener(brightcove.api.events.MediaEvent.PLAY, onMediaEventFired);
	_videoPlayer.addEventListener(brightcove.api.events.MediaEvent.PROGRESS, onMediaEventFired);
	_videoPlayer.addEventListener(brightcove.api.events.MediaEvent.STOP, onMediaEventFired);
	_videoPlayer.addEventListener(brightcove.api.events.MediaEvent.SEEK_NOTIFY, onMediaEventFired);
  }

  function onMediaEventFired(event) {
	var sendEventFlag = false;
    var eventType = event.type;
	switch(eventType) {
	  case 'mediaBegin':
		timeIntervall = timeFactor;
		sendEventFlag = true;
	  	break;
	  case 'mediaComplete':
		timeIntervall = timeFactor;
		sendEventFlag = true;
	  	break;
	  case 'mediaPlay':
		sendEventFlag = true;
	  	break;
	  case 'mediaProgress':
		var time = getCurrentPosition();
		if (time > timeIntervall) {
			sendEventFlag = true;
			timeIntervall += timeFactor;
		}
	  	break;
	  case 'mediaStop':
		sendEventFlag = true;
	  	break;
	  case 'mediaSeekNotify':
		var currentTime = event.position; 
		setDecimalTime(currentTime);
		sendEventFlag = true;
	  	break;
	  default:
		// do nothing
	}
	if (sendEventFlag) {
	  sendEvent(event);
	}
  }

  function setDecimalTime(myTime) {
	timeIntervall = myTime + (timeFactor - (myTime % timeFactor));
  }

  function sendEvent(event) {
    var myRequestUrl = createRequestUrl(event);
	xmlhttp = new XMLHttpRequest();
	xmlhttp.open('GET', myRequestUrl, true);
	xmlhttp.send();
  }
  
  function createRequestUrl(event) {
  	var myEventMapping = getEventMapping(event.type);
  	var myRequestUrl = 'https://wt.distrelec.com/580777942866624/wt.pl?p=324,st';
  	myRequestUrl = myRequestUrl + '&mi=' + getCurrentVideoName();
  	myRequestUrl = myRequestUrl + '&mk=' + myEventMapping;
  	myRequestUrl = myRequestUrl + '&mt1=' + getCurrentPosition();
  	myRequestUrl = myRequestUrl + '&mt2=' + getCurrentVideoLength();
  	myRequestUrl = myRequestUrl + '&mg=' + 'TAGS2DO';
  	myRequestUrl = myRequestUrl + '&x=' + getTimestamp();
  	return myRequestUrl;
  }
  
  function getEventMapping(eventType) {
  	var mapping = 'unknown';
	switch(eventType) {
	  case 'mediaBegin':
  		mapping = 'play';
	  	break;
	  case 'mediaComplete':
  		mapping = 'eof';
	  	break;
	  case 'mediaPlay':
  		mapping = 'play';
	  	break;
	  case 'mediaProgress':
  		mapping = 'pos';
	  	break;
	  case 'mediaStop':
  		mapping = 'pause';
	  	break;
	  case 'mediaSeekNotify':
  		mapping = 'seek';
	  	break;
	  default:
		// do nothing
	}
 	return mapping; 
  }
  
  function getCurrentVideoName() {
	return encodeURI(currentVideo.displayName);
  }
  
  function getCurrentVideoId() {
	return currentVideo.id;
  }
  
  function getCurrentPosition() {
  	return _videoPlayer.getVideoPosition();
  }
  
  function getCurrentVideoLength() {
  	return _videoPlayer.getVideoDuration();
  }
  
  function getTimestamp() {
  	return new Date().getTime();
  }  

}());
