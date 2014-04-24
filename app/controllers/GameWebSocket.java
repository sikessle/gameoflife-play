package controllers;

import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.view.tui.TextView;

import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class GameWebSocket extends WebSocket<JsonNode> {

	private final TextView ui;
	private final GridController controller;

	public GameWebSocket(TextView ui, GridController controller) {
		this.ui = ui;
		this.controller = controller;
	}

	@Override
	public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
		new GridControllerObserver(controller, out);
		new TextInputForwarder(ui, in);
	}

}
