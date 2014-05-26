package controllers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.*;
import play.mvc.*;
import views.html.*;
import views.html.defaultpages.error;

public class Application extends Controller {

	private static final String HIGHSCORE_SERVER = "http://de-htwg-sa-highscores.herokuapp.com";
	private static Map<String, GameOfLife> gameCache = new ConcurrentHashMap<String, GameOfLife>();

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

	@BodyParser.Of(BodyParser.Json.class)
	public static Result createHighscore() {
		JsonNode data = request().body().asJson();
		System.out.println(data);
		WS.url(HIGHSCORE_SERVER).post(data);
		return ok();
	}

}
