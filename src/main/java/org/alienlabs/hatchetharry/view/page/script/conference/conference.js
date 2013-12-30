var userId;
navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
var peer = new Peer({key: '2nij1esj8tjwz5mi'});

peer.on('open', function(id) {
  console.log('My peer ID is: ' + id);
  userId = id;
});

var fillUserId = function() {
	document.getElementById('userId').value = userId;
}

var callAudio = function(id) {
	play('ringtone');
	var call = peer.call($('#txtPhoneNumber').val(), window.localStream);
		// Set your video displays
	navigator.getUserMedia({audio: true, video: true}, function(stream){
        	// Set your video displays
		$('#my-video').prop('src', URL.createObjectURL(stream));
		window.localStream = stream;
		call.on('stream', function(stream){
        		jQuery('#their-video').prop('src', URL.createObjectURL(stream));
			window.localStream = stream;
      		});
      		// Get audio/video stream
                navigator.getUserMedia({audio: true, video: false}, function(stream){
        		pause('ringtone');
        		// Set your video displays
        		$('#my-video').prop('src', URL.createObjectURL(stream));
        		window.localStream = stream;
		}, function() { 
		console.log('error #1'); 
		pause('ringtone');
		pause('ringbacktone');
		});
      }, function() { 
		console.log('error #2'); 
		pause('ringtone');
		pause('ringbacktone');
	});
}

peer.on('call', function(call) {
	// Answer the call automatically (instead of prompting user) for demo purposes
	play('ringbacktone');
      	call.answer(window.localStream);
	call.on('stream', function(stream){
		pause('ringbacktone');
        	jQuery('#their-video').prop('src', URL.createObjectURL(stream));
      	});
});

peer.on('error', function(err){
    pause('ringtone');
    pause('ringbacktone');
    console.log('Failed to get local stream' ,err.message);
});

var hangup = function() {
	pause('ringtone');
	pause('ringbacktone');
	peer.disconnect();
	peer.destroy();
}

function play(id) {
	var a = document.getElementById(id);
	a.play();
}

function pause(id) {
	var a = document.getElementById(id);
	a.pause();
}
