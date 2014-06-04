package controllers;

import play.libs.F.Callback;
import play.mvc.WebSocket;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.message.ParseCommandMessage;
import controllers.message.WebSocketOutReadyMessage;

public class GameWebSocket extends WebSocket<JsonNode> {

	private final ActorRef gameRef;

	public GameWebSocket(ActorRef gameRef) {
		this.gameRef = gameRef;
	}

	@Override
	public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
		in.onMessage(new HandleInputCallback());
		gameRef.tell(new WebSocketOutReadyMessage(out), ActorRef.noSender());
	}

	private class HandleInputCallback implements Callback<JsonNode> {

		@Override
		public void invoke(JsonNode input) throws Throwable {
			gameRef.tell(new ParseCommandMessage(input), ActorRef.noSender());
		}

	}

}
