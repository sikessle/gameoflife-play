package controllers;

import org.sikessle.gameoflife.controller.impl.TextUIController;
import org.sikessle.gameoflife.model.Grid;

import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class GameWebSocket extends WebSocket<JsonNode> {

	private final TextUIController ui;
	private final Grid grid;

	public GameWebSocket(TextUIController ui, Grid grid) {
		this.ui = ui;
		this.grid = grid;
	}

	@Override
	public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
		new GridObserver(grid, out);
		new TextInputForwarder(ui, in);
	}

}
