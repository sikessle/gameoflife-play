package controllers;

import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.view.tui.TextView;

import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class GameWebSocket extends WebSocket<JsonNode> {

	private final TextView textUi;
	private final GridController controller;

	public GameWebSocket(TextView textUi, GridController controller) {
		this.textUi = textUi;
		this.controller = controller;
	}

	@Override
	public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
		new GridControllerObserver(controller, out);
		new TextInputForwarder(textUi, in);
	}

}
