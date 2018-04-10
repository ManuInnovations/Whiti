var usertype = getCookie("userType");
var event = getCookie("eventCode");

var streamVideo = (getCookie("streamVideo")=="true");

// Set text of button
if (usertype=="listener"){
    if (streamVideo) document.getElementById("video").innerHTML = "Turn video off";
    else document.getElementById("video").innerHTML = "Turn video on";  
}

var connection;
startStreaming();

/**
 * Starts the media streaming
 */
function startStreaming(){
    connection = new RTCMultiConnection();
    connection.socketURL = 'https://rtcmulticonnection.herokuapp.com:443/';

    connection.sdpConstraints = {
        mandatory:{
            OfferToReceiveAudio: true,
            OfferToReceiveVideo: true
        }
    };
    
    this.disabled = true;

    connection.onstream = function(event) {
        if (usertype==="listener"){
            document.getElementById('remote-video-container').appendChild(event.mediaElement);
        }
    };
    
    if (usertype === "interpreter"){
        // allow broadcasting of media to server
        connection.session = {
            audio: true,
            video: streamVideo,
            oneway: true,
            broadcast:true
        };
        connection.open(event);
    }

    else if (usertype === "listener"){
        // will not capture/broadcast media
        connection.session = {
            audio: true,
            video: streamVideo,
            oneway: true,
            broadcast: false
        };
        connection.join(event);
    }
}

/**
 * Sets functions for buttons. Currently only one button for turning video on/off
 * Can easily add more buttons here.
 */
function setFunctionsForSwitches(id) {
    document.getElementById(id).addEventListener("click",function () {
        if (id=="video" && usertype=="listener") switchVideo();
    });
}

/**
 * Function for drop down menu
 */ 
var menu = document.getElementById("dropClick");
    menu.addEventListener("click",function () {
    document.getElementById("menuItems").classList.toggle("show");
});

/**
 * Turns video on or off
 */
function switchVideo (){
    streamVideo = !streamVideo;
    document.cookie = "streamVideo="+streamVideo.toString();
    location.reload();
}

setFunctionsForSwitches("video");

/**
* retrieves cookie elements
* @param cookieName
*/
function getCookie(cookieName) {
  var name = cookieName + "=";
  var ca = document.cookie.split(';');
  for(var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
        c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
    }
  }
  return "";
}
