package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	private static GameSingleton game = GameSingleton.getInstance();

	public static Result index() {
		return ok(index.render());
	}

	public static WebSocket<JsonNode> connectWebSocket() {
		return new GameWebSocket(game.getUi(), game.getGrid());
	}

}
