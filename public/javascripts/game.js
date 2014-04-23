$(document).ready(function() {
	
	var socketUrl = 'ws://' + window.location.host + '/socket';
	var socket = new WebSocket(socketUrl);
	
	socket.onmessage = function(msg) {
		var jsonMsg = JSON.parse(msg.data);
		console.log(jsonMsg[0]);
	};
	
	setTimeout(function() {
		socket.send(JSON.stringify({
			command: "d"
		}));
	}, 1000);
	
});
