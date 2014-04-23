package controllers;

import org.sikessle.gameoflife.controller.impl.TextUIController;

import play.libs.F.Callback;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class TextInputForwarder implements Callback<JsonNode> {

	private final TextUIController ui;

	public TextInputForwarder(TextUIController ui, WebSocket.In<JsonNode> in) {
		this.ui = ui;
		in.onMessage(this);
	}

	@Override
	public void invoke(JsonNode input) throws Throwable {
		String command = parseJsonInput(input);
		if (command != null) {
			ui.readAndInterpret(command);
		}
	}

	private String parseJsonInput(JsonNode input) {
		JsonNode jsonCommand = input.path("command");
		if (jsonCommand != null) {
			return jsonCommand.asText();
		}
		return null;
	}

}
