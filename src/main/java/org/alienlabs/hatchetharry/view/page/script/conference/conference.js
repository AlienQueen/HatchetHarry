var userId;
var peer = new Peer({key: '2nij1esj8tjwz5mi'});

peer.on('open', function(id) {
  console.log('My peer ID is: ' + id);
  userId = id;
});

var fillUserId = function() {
	document.getElementById('userId').value = userId;
}

var callAudio = function(id) {
	var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
	play('ringtone');
	getUserMedia({video: false, audio: true}, function(stream) {
		var call = peer.call(document.getElementById('txtPhoneNumber').value, stream);
		  call.on('stream', function(remoteStream) {
		    // Show stream in some video/canvas element.
			pause('ringtone');
		  });
	}, function(err) {
		  pause('ringtone');
		  console.log('Failed to get local stream' ,err);
	});
}

var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
peer.on('call', function(call) {
  play('ringbacktone');
  getUserMedia({video: false, audio: true}, function(stream) {
    call.answer(stream); // Answer the call with an A/V stream.
    call.on('stream', function(remoteStream) {
      // Show stream in some video/canvas element.
    	pause('ringbacktone');
    });
  }, function(err) {
    pause('ringbacktone');
    console.log('Failed to get local stream' ,err);
  });
});

var hangup = function() {
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
