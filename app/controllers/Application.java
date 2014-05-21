package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.view.tui.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import play.api.mvc.Session;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	private static Map<String, GameOfLife> gameCache = new HashMap<String, GameOfLife>();

	public static Result index() {
		Set<String> gameIds = gameCache.keySet();
		return ok(index.render(gameIds));
	}

	public static Result createGame() {
		String gameId = createGameInCache();

		return redirect(controllers.routes.Application.playGame(gameId));
	}

	private static String createGameInCache() {
		UUID uuid = UUID.randomUUID();
		String gameId = uuid.toString();

		GameOfLife game = new GameOfLife();
		gameCache.put(gameId, game);

		return gameId;
	}

	public static Result playGame(String gameId) {
		if (gameNotExists(gameId)) {
			return redirect(controllers.routes.Application.createGame());
		}
		return ok(game.render(gameId));
	}

	public static WebSocket<JsonNode> connectWebSocket(String gameId) {
		if (gameNotExists(gameId)) {
			return null;
		}

		GameOfLife game = gameCache.get(gameId);
		return new GameWebSocket(game.getTextUI(), game.getGridController());
	}

	private static boolean gameNotExists(String gameId) {
		return !gameCache.containsKey(gameId);
	}

}
