package controllers.message;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

import com.fasterxml.jackson.databind.JsonNode;

public class WebSocketOutReadyMessage {

	private final Out<JsonNode> out;

	public WebSocketOutReadyMessage(WebSocket.Out<JsonNode> out) {
		this.out = out;
	}

	public Out<JsonNode> getOut() {
		return out;
	}

}
