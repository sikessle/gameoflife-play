package controllers.message;

import com.fasterxml.jackson.databind.JsonNode;

public class ParseCommandMessage {

	private final JsonNode command;

	public ParseCommandMessage(JsonNode input) {
		this.command = input;
	}

	public JsonNode getCommand() {
		return command;
	}

}
