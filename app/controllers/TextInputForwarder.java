package controllers;

import org.sikessle.gameoflife.view.tui.TextView;

import play.libs.F.Callback;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class TextInputForwarder implements Callback<JsonNode> {

	private final TextView textUi;

	public TextInputForwarder(TextView textUi, WebSocket.In<JsonNode> in) {
		this.textUi = textUi;
		in.onMessage(this);
	}

	@Override
	public void invoke(JsonNode input) throws Throwable {
		String command = parseJsonInput(input);
		textUi.readAndInterpretFromArgument(command);
	}

	private String parseJsonInput(JsonNode input) {
		JsonNode jsonCommand = input.path("command");
		if (jsonCommand != null) {
			return jsonCommand.asText();
		}
		return null;
	}

}
