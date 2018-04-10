Each javascript file is connected to the html file with the same name except 
client_message_handler.js. 
All javascript files contain functionality for showing the mobile responsive 
menu.

File Specific Responsibilites:

client_message_handler.js -
Used to handle interactions with the python Flask server. Uses jquery post 
messages. Sends messages to create an event containing an event name, event 
code and event description. Sends message to join an event using an event code. 
Also handles error messages received from the server.

create_event.js -
Gets event details from text fields in create_event.html and passes details to 
client_message_handler.js so it can handle event creation with the server. 

event.js -
This JavaScript handles the connection of webRTC.
1. Function for accessing the cookies in our website
   * with the cookie we are able to keep track of:
     * userType - this is used to control the type of connection (Interpreter is
       sending data and Listener is receiving data)
     * eventCode - this is used to control what room to connect so the users are
       connected through the eventCode
     * trigger for streamVideo - this is used for the button to turn on/off the 
       video and supported with it's own function for triggering StreamVideo

2. RTCMultiConnection API
     * we use a cloud server for the API to help us with:
       * Specify what to receive such as video and audio with a boolean
       * Specify what type of connection such as broadcast/one-way
       * Specify whether to open or join connection - for interpreter they open 
         an event connection but for listener they are joining an event 
         connection as they should not be able to open an event

feedback.js -
Sends feedback entered by user to the server. Uses jquery post messages. Safety 
checks are done server side.

index.js -
Stores the type of user (listener or interpreter) in the "userType" cookie 
depending if the "Listener" or "Interpreter" button is clicked in index.html. 
Sends the user type to the server for user monitoring once Listener or 
Interpreter is clicked.

join_event.js -
When "Join" is clicked in join_event.html, retrieves event code from text field 
and passes to client_message_handler.js to let the user join the event with the 
given event code. Initialises streamVideo to true so event.js will start 
streaming audio when run. Sends event code to user monitoring server.

