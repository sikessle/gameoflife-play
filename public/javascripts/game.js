/*jslint browser: true*/
/*global $, jQuery, WebSocket, console*/

var Game = (function () {
    'use strict';

    /*
        A generic logger.
    */
    function Logger(debug) {
        this.debug = debug;
    }

    Logger.prototype.log = function (msg) {
        if (this.debug) {
            console.log(msg);
        }
    };

    /*
        The web socket for communication

        settings = {
            url, timeout, maxReconnectAttempts, debug
        }
    */
    function Socket(settings) {
        this.settings = $.extend({}, settings);
        this.socket = {};
        this.reconnectAttempts = 0;
        var logger = new Logger(settings.debug);
        this.log = logger.log.bind(logger);

        this.connect();
    }

    Socket.prototype.connect = function () {
        this.socket = new WebSocket(this.settings.url, ['json']);

        // bind socket events
        this.socket.onclose = this.onclose.bind(this);
        this.socket.onopen = this.onopen.bind(this);
    };

    Socket.prototype.onclose = function () {
        this.log("socket closed. reconnecting in " + this.settings.timeout + " ms");

        if (this.reconnectAttempts < this.settings.maxReconnectAttempts) {
            setTimeout(this.connect, this.settings.timeout);
            this.reconnectAttempts += 1;
        }
    };

    Socket.prototype.onopen = function () {
        this.log('socket open');
    };

    Socket.prototype.sendJson = function (msg) {
        var jsonMessage = JSON.stringify(msg);
        this.log('sending over socket: ' + jsonMessage);
        this.socket.send(jsonMessage);
    };

    Socket.prototype.setOnMessage = function (callback) {
        this.socket.onmessage = callback;
    };


    /*
        The main game communication
        settings:
        {
            debug: true,
            socket: {
                debug: true,
                url: 'ws://' + window.location.host + '/socket',
                timeout: 1500,
                maxReconnectAttempts: 3
            },
            selectors: {
                grid: '#grid',
                cellTemplate: '#template-cell',
                cell: '.cell',
                sliderRows: '.slider-rows',
                sliderColumns: '.slider-columns',
                stepOneBtn: '.step-1'
            },
            cellAliveClass: 'alive'
        }
    */
    function Game(settings) {
        this.settings = $.extend(true, {}, settings);

        this.socket = {};
        this.cells = [[]];
        this.rows = 0;
        this.columns = 0;

        var logger = new Logger(settings.debug);
        this.log = logger.log.bind(logger);

        this.$grid = $(this.settings.selectors.grid);
        this.templateCell = $(this.settings.selectors.cellTemplate).html();
    }

    Game.prototype.start = function () {
        this.tweakMouseEvent();
        this.setupControls();
        this.bindGridEvents();
        this.socket = new Socket(this.settings.socket);
        this.socket.setOnMessage(this.drawGrid.bind(this));
    };


    // to detect if mouse is pressed while hovering.
    Game.prototype.tweakMouseEvent = function () {
        var leftButtonDown;

        $(document).mousedown(function (e) {
            if (e.which === 1) {
                leftButtonDown = true;
            }
        });
        $(document).mouseup(function (e) {
            if (e.which === 1) {
                leftButtonDown = false;
            }
        });
        $(document).mousemove(function (e) {
            if (e.which === 1 && !leftButtonDown) {
                e.which = 0;
            }
        });
    };

    Game.prototype.setupControls = function () {
        // sliders
        var options = {
            min: 1,
            max: 100,
            step: 1,
            value: 1,
            tooltip: 'hide'
        },
            game = this,
            rowsOptions = $.extend({}, options),
            columnsOptions = $.extend({}, options);

        rowsOptions.orientation = 'vertical';
        columnsOptions.orientation = 'horizontal';


        $(this.settings.selectors.sliderRows).slider(rowsOptions).on('slide', function () {
            var rows = $(this).val(),
                columns = game.columns;
            game.sendCommand('s', [rows, columns]);
        });

        $(this.settings.selectors.sliderColumns).slider(columnsOptions).on('slide', function () {
            var rows = game.rows,
                columns = $(this).val();
            game.sendCommand('s', [rows, columns]);
        });

        // buttons
        $(this.settings.selectors.stepOneBtn).click(function () {
            game.sendCommand('n');
        });
    };

    Game.prototype.sendCommand = function (cmd, args) {
        var completeCommand = cmd;

        if (args === undefined || args === null) {
            args = [];
        }

        args.forEach(function (arg) {
            completeCommand += ' ' + arg;
        });

        this.socket.sendJson({
            command: completeCommand
        });
    };

    // Drawing grid
    Game.prototype.drawGrid = function (msg) {
        this.cells = JSON.parse(msg.data);
        if (this.isGridDimensionDifferent()) {
            this.rows = this.cells.length;
            this.columns = this.cells[0].length;
            this.createGridDom();
        }
        this.updateCells();
    };

    Game.prototype.isGridDimensionDifferent = function () {
        return this.rows !== this.cells.length
            || this.columns !== this.cells[0].length;
    };

    Game.prototype.createGridDom = function () {
        this.log('creating dom');

        var rows = this.rows,
            columns = this.columns,
            i,
            j,
            $cell,
            $row;

        this.$grid.html('');

        for (i = 0; i < rows; i += 1) {
            $row = this.createRow();
            for (j = 0; j < columns; j += 1) {
                $cell = this.createCell(i, j);
                $row.append($cell);
            }
            this.$grid.append($row);
        }
    };

    Game.prototype.createRow = function () {
        return $('<div></div>');
    };

    Game.prototype.createCell = function (row, column) {
        var $cell = $(this.templateCell);
        $cell.attr('id', 'cell-' + row + '-' + column);

        return $cell;
    };

    Game.prototype.bindGridEvents = function () {
        var game = this;

        function toggleCell(event) {
            if (event.which === 1) {
                var id = $(this).attr('id'),
                    parts = id.split('-'),
                    row = parts[1],
                    column = parts[2];

                game.sendCommand('t', [row, column]);
            }
        }

        this.$grid.on('mousedown', this.settings.selectors.cell, toggleCell);
        this.$grid.on('mouseenter', this.settings.selectors.cell, toggleCell);
    };

    Game.prototype.updateCells = function () {
        var i,
            j;

        for (i = 0; i < this.rows; i += 1) {
            for (j = 0; j < this.columns; j += 1) {
                this.updateCell(i, j, this.cells[i][j]);
            }
        }
    };

    Game.prototype.updateCell = function (row, column, isAlive) {
        var $cell = $('#cell-' + row + '-' + column);

        if (isAlive) {
            $cell.addClass(this.settings.cellAliveClass);
        } else {
            $cell.removeClass(this.settings.cellAliveClass);
        }
    };

    return Game;

}());

$(document).ready(function () {
    'use strict';

    var game = new Game({
        debug: true,
        socket: {
            debug: true,
            url: 'ws://' + window.location.host + '/socket',
            timeout: 1500,
            maxReconnectAttempts: 3
        },
        selectors: {
            grid: '#grid',
            cellTemplate: '#template-cell',
            cell: '.cell',
            sliderRows: '.slider-rows',
            sliderColumns: '.slider-columns',
            stepOneBtn: '.step-1'
        },
        cellAliveClass: 'alive'
    });

    game.start();
});
