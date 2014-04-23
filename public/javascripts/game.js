var GameOfLife = {
	
	socket: null,
	$grid: null,
	rows: 0,
	columns: 0,
		
	init: function() {
		this.$grid = $("#grid");
		this.connectSocket();
	},
	
	connectSocket: function() {
		var socketUrl = 'ws://' + window.location.host + '/socket';
		this.socket = new WebSocket(socketUrl, ['json']);
		
		GameOfLife.socket.onclose = GameOfLife.socketOnClose;
		GameOfLife.socket.onmessage = GameOfLife.socketOnMessage;
	},
	
	socketOnClose: function(event) {
		console.log("socket closed. reconnecting.")
		GameOfLife.connectSocket();
	},
	
	
	socketOnMessage: function(message) {
		GameOfLife.draw(JSON.parse(message.data));
	},
	
	draw: function(cells) {
		if (cells.length != GameOfLife.rows || cells[0].length != GameOfLife.columns) {
			GameOfLife.rows = cells.length;
			GameOfLife.columns = cells[0].length;
			GameOfLife.createDom(cells);
			GameOfLife.attachDomListener();
		}
		GameOfLife.drawCells(cells);
	},
	
	createDom: function(cells) {
		GameOfLife.$grid.html('');
		
		for (var i = 0; i < cells.length; i++) {
			var $row = GameOfLife.createRow(); 
			for (var j = 0; j < cells.length; j++) {
				var $cell = GameOfLife.createCell(i, j);
				$row.append($cell);
			}
			GameOfLife.$grid.append($row);
		}
	},
	
	attachDomListener: function() {
		GameOfLife.$grid.find('.cell').click(function(event) {
			var id = $(this).attr('id');
			var parts = id.split('-');
			var row = parts[1];
			var column = parts[2];
			GameOfLife.sendCommand('t', [row, column]);
		});
	},
	
	createRow: function() {
		return $('<div></div>');
	},
	
	createCell: function(row, column) {
		var size = GameOfLife.$grid.width() / GameOfLife.columns;
		return $('<div></div>')
			.addClass('cell')
			.attr('id', 'cell-' + row + '-' + column)
			.css('width', size)
			.css('height', size);
	},
	
	
	drawCells: function(cells) {
		for (var i = 0; i < cells.length; i++) {
			for (var j = 0; j < cells.length; j++) {
				GameOfLife.drawCell(i, j, cells[i][j]);
			}
		}
	},
	
	drawCell: function(row, column, isAlive) {
		var $cell = $('#cell-' + row + '-' + column);
		if (isAlive) {
			$cell.addClass('alive');
		} else {
			$cell.removeClass('alive');
		}
	},

	sendCommand: function(cmd, args) {
		var completeCommand = cmd;
		for (var i = 0; i < args.length; i++) {
			completeCommand += ' ' + args[i];
		}
		var data = {
				command: completeCommand
		};
		
		GameOfLife.socket.send(JSON.stringify(data));
	},
		
};

$(document).ready(function() {
	GameOfLife.init();
});
