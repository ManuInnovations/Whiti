$(function() {

	// The action listener to drop down a menu (for smaller screens eg mobile devices):
	$('#dropClick').on('click',function() {
		$('#menuItems')[0].classList.toggle("show");
	});
    
    /**
     * Calls connectToEvent and sets entered event code as a cookie. Sets initial boolean values for
     * streaming video and audio. Also sends user data to server. Called when user clicks "Join"
     */
	$('#joinEvent a').on('click',function(){

		var eventCode = $('#eventCode').val();
		event.preventDefault();
        document.cookie = "eventCode="+eventCode;
        connectToEvent(); // connects to event through server
        document.cookie = "streamVideo=true";
        
		$.ajax ({
            type: 'POST',
            url: 'http://rest.learncode.academy/api/engrwhitiapp/eventlogs',
            data: eventCode,

            success: function (dataSent) {
                // data successfully sent to the server.
            },
            error: function () {
                // error sending data to the server.
                console.log("Failure to send user log to the server");
            }
        });
	});
});