$(function() {
// The action listener to drop down a menu (for smaller screens eg mobile devices):
$('#dropClick').on('click',function() {
	$('#menuItems')[0].classList.toggle("show");
});

/**
 * Returns an object for what type of user started using whiti, at what time of day
 * and on which day. This is the data that will be sent to the server
 */
function storeTypeOfUser (type) {
	var userData = {
		userType: type,
		dateInformation: (new Date()).toLocaleDateString("en-US"),//(new Date()).toString(),
	};
	return userData;
}

/**
 * Any time the user joins whiti as a listener or interpreter, this on function will make a call to a function
 * that calculates what time they joined, and stores the time they joined, as well as what type of user they are
 * on a server. Also used to store user type in a cookie to be accessed for event joining.
 */
$('.role-buttons a').on('click',function(event){
    document.cookie = "userType="+this.id; // stores userType so can be accessed from any page
	var userData = storeTypeOfUser(this.id); // where type of id specifies if the user is a listener or interpreter
	console.log(userData);
	document.cookie = "dateInformation="+userData.dateInformation
	event.preventDefault(); // allow the data to be sent to the server before changing pages:
	submitUserData(userData);

});

/**
 * Sends user data to server
 */
function submitUserData(userData){
	$.ajax ({
		type: 'POST',
		url: '/index.html',//'http://rest.learncode.academy/api/engrwhitiapp/userlogs',
		data: userData,
		contentType: 'application/json',
		success: function (dataSent) {
			// data successfully sent to the server.
			window.location.href = "join_event.html";
		},
		error: function () {
			// error sending data to the server.
			console.log("ERROR: Failure to send user log to the server");
		}
	});
}

});
