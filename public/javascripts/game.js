function Game() {
	
    var self = this;
    
	this.settings = {
			socketUrl: 'ws://' + window.location.host + '/socket', 
            socketTimeout: 1500,
	};
    
    this.gridData = {
        cells: [[]],
        rows: 0,
        columns: 0,
    };
    
    this.$grid = $("#grid");
    this.templateCell = $('#template-cell').html();
	
    // bootstrapping
    this.tweakMouseEvent();
    this.setupControls();
	this.socket = new this.Socket(this.settings);
    this.socket.channel.onmessage = function(msg) {
        self.drawGrid(msg)
    }
};

// utilites
Game.prototype.tweakMouseEvent = function() {
    var root = this;
    var leftButtonDown;
    
    $(document).mousedown(function(e) {
        if (e.which === 1) 
            leftButtonDown = true;
    });
    $(document).mouseup(function(e) {
        if (e.which === 1) 
            leftButtonDown = false;
    });
    $(document).mousemove(function(e) {
        if (e.which === 1 && !leftButtonDown) {
            e.which = 0;
        }
    });
};

Game.prototype.setupControls = function() {
    var root = this;
    
    // sliders
    var options = {
        min: 1,
        max: 100,
        step: 1,
        value: 1,
        tooltip: 'hide',
    };
    
    var rowsOptions = $.extend({}, options);
    rowsOptions.orientation = 'vertical';
    
    var columnsOptions = $.extend({}, options);
    columnsOptions.orientation = 'horizontal';
    
    $('.slider-rows').slider(rowsOptions).on('slide', function(event) {
        var rows = $(this).val();
        var columns = root.gridData.columns;
        root.socket.sendCommand('s', [rows, columns]);
    });
    
    $('.slider-columns').slider(columnsOptions).on('slide', function(event) {
        var rows = root.gridData.rows;
        var columns = $(this).val();
        root.socket.sendCommand('s', [rows, columns]);
    });
    
    // buttons
    $('.step-1').click(function() {
       root.socket.sendCommand('n');
    });
};


// Socket
Game.prototype.Socket = function(settings) {
    this.settings = settings;
	this.connect();
};

Game.prototype.Socket.prototype.connect = function() {
    this.channel = new WebSocket(this.settings.socketUrl, ['json']);
    this.channel.onclose = function() {
        console.log("socket closed. reconnecting in " + this.settings.socketTimeout + " ms");
        setTimeout(this.connect, this.settings.socketTimeout);
    };
    this.channel.onopen = function() {
        console.log('socket open');
    };
};

Game.prototype.Socket.prototype.sendCommand = function(cmd, args) {
	var completeCommand = cmd;
    if (args == null) {
        args = [];
    }
	for (var i = 0; i < args.length; i++) {
		completeCommand += ' ' + args[i];
	}
	var data = {
		command: completeCommand
	};
	
	this.channel.send(JSON.stringify(data));
};

// Drawing grid
Game.prototype.drawGrid = function(msg) {
    this.gridData.cells = JSON.parse(msg.data);
    if (this.isGridDimensionDifferent()) {
        this.gridData.rows = this.gridData.cells.length;
        this.gridData.columns = this.gridData.cells[0].length;
        this.createGridDom();
        this.bindEvents();
    }
    this.updateCells();
};

Game.prototype.isGridDimensionDifferent = function() {
    return this.gridData.rows != this.gridData.cells.length 
        || this.gridData.columns != this.gridData.cells[0].length;
};

Game.prototype.createGridDom = function() {
    console.log('creating dom');
    var cells = this.gridData.cells;
    var rows = this.gridData.rows;
    var columns = this.gridData.columns;
    this.$grid.html('');

    for (var i = 0; i < rows; i++) {
        var $row = this.createRow();
        for (var j = 0; j < columns; j++) {
            var $cell = this.createCell(i, j);
            $row.append($cell);
        }
        this.$grid.append($row);
    }
};

Game.prototype.createRow = function() {
    return $('<div></div>');
};

Game.prototype.createCell = function(row, column) {
	var $cell = $(this.templateCell);
	$cell.attr('id', 'cell-' + row + '-' + column);
	
	return $cell;
};

Game.prototype.bindEvents = function() {
    var root = this;
    var leftButtonDown;
    
    var toggleCell = function(event) {
        if (event.which === 1) {
    		var id = $(this).attr('id');
    		var parts = id.split('-');
    		var row = parts[1];
    		var column = parts[2];
    		root.socket.sendCommand('t', [row, column]);
        }
    };
    
	this.$grid.find('.cell').mousedown(toggleCell);
    this.$grid.find('.cell').mouseenter(toggleCell);
    
};

Game.prototype.updateCells = function() {
    var cells = this.gridData.cells;
    var rows = this.gridData.rows;
    var columns = this.gridData.columns;
    
    for (var i = 0; i < rows; i++) {
        for (var j = 0; j < columns; j++) {
            this.updateCell(i, j, cells[i][j]);
        }
    }
};

Game.prototype.updateCell = function(row, column, isAlive) {
    $cell = $('#cell-' + row + '-' + column);
	if (isAlive) {
		$cell.addClass('alive');
	} else {
		$cell.removeClass('alive');
	}
};





$(document).ready(function() {
	var game = new Game();
});
