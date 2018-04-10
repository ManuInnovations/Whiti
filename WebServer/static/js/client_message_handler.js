/**
 * Functions for sending and receiving messages to and from signal server.
 * Uses jquery post messages.
 */


/**
 * Sends a message to create an event.
 * @param eventDetails Details for event. Includes name, code, description.
 */
function createEvent(eventDetails){
    $.post("/create_event.html", eventDetails, function(data,status){
      var response = JSON.parse(data)
      if (response.messageType == "error"){
          handleErrorFromServer(response.errorCode,response.errorMessage);
          return;
      }
      document.getElementById("title").innerHTML = "Event Created<br><br> Event Code: "+response.eventCode;
      document.getElementById("eventDetails").innerHTML = "";
    }).fail(function(){
      alert("Event creation failed");
    });
}

/**
 * Sends a connection message to the server. Should be called by "Join" button on click function.
 * Cookie contains uid, usertype, eventCode
 */
function connectToEvent(){
    $.post("/join_event.html", document.cookie, function(data,status){
      document.location.href = "event.html/"+getCookie("eventCode")
    })
    .fail(function(){
       alert("Could not join event");
    });
}


/**
 * Retrieves cookie elements
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

/**
 * Handles errors received from the server
 * @param errorCode Unique code for error
 * @param errorMessage Descriotion of error
 */
function handleErrorFromServer(errorCode, errorMessage){
    console.log("error from server: "+errorCode+ ": "+errorMessage);
    switch (errorCode){
        case "001":
            // user entered an event code that does not exist,  remain on join event page
            alert("Please enter a valid event code");
            break;
        case "002":
            // event has not been created yet
            alert("Event is still being created, please try again soon");
            break;
        case "003":
            // speaker of a selected language is no longer available, CURRENTLY NOT USED
            alert("The speaker of that language is no longer connected, please select another language");
            break;
        case "004":
            // message from client not recognised  CURRENTLY NOT USED
            break;
        case "006":
            // event code already exists, remain on create event page
            alert("Event code already being used, please use a different event code")
            break;
        case "007":
            // interpreter already exists for this language and event
            alert("There is already an interpreter interpreting for this language");
        default:
            console.log("Received unrecognised error from server: "+errorCode+ ": "+errorMessage);
            break;

    }

}
