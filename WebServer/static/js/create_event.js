$(function () {

	// Add js functionality to the mobile responsive menu:
	$('#dropClick').on('click',function() {
		$('#menuItems')[0].classList.toggle("show");
	});
	
	/**
     * Calls createEvent passing the event details for the vent to be created.
     * Called when user clicks "Create".
     */
	$('#submit').on('click',function() {
        var eventCode = $('#eventCode').val();
        var eventName = $('#eventName').val();
        if (eventName == ""){
            alert("Please enter an event name");
            return;
        }
        var eventDescription = $('#description').val();
        var eventDetails = {eventCode: eventCode, eventName: eventName, eventDescription: eventDescription};
        document.cookie = "eventDetails="+JSON.stringify(eventDetails); // used to remember event details in case existing eventcode is generated
        createEvent(eventDetails);
	});
    
    
});