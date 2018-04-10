/**
* Sends feedback to the server to be handled
* Can be expanded to safety check on this side but at current relies on server
* @param feedback the content of the message
*/
function submitFeedbackToServer(feedback){
	$.ajax({
	type:"POST",
	url:"/feedback.html",
	data: feedback,
	contentType: 'application/json',
	success: function(data,status){
		alert("Thank you for your feedback!");
	},
	error: function(data,status){
		alert("Couldn't submit feedback this time, try again later");
	}

});
}


$(function () {
	// Add js functionality to the mobile responsive menu:
	$('#dropClick').on('click',function() {
		$('#menuItems')[0].classList.toggle("show");
	});
	// just an action listener on the submit button, we can change this later on:
	$('#submitFeedback').on('click',function() {
		var textArea = $('#typeFeedback');
	 	//alert("You have typed : " + textArea.val());
		var message = textArea.val();
		var jsonForm = {'content':message};
		submitFeedbackToServer(
		JSON.stringify(jsonForm)
		);
		//textArea.val("");
	});
});
