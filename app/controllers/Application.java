package controllers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import play.libs.WS;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.game;
import views.html.index;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller {

	private static final String HIGHSCORE_SERVER = "http://de-htwg-sa-highscores.herokuapp.com";
	private static final ActorSystem actorSystem = ActorSystem
			.create("GameOfLife");
	private static final ObjectMapper mapper = new ObjectMapper();
	private static Map<String, ActorRef> gameActorCache = new ConcurrentHashMap<String, ActorRef>();

	public static Result index() {
		Set<String> gameIds = gameActorCache.keySet();
		return ok(index.render(gameIds));
	}

	public static Result createGame() {
		String gameId = createGameActorInCache();
		ObjectNode result = mapper.createObjectNode();
		String gameUrl = routes.Application.playGame(gameId).absoluteURL(
				request());
		result.put("gameUrl", gameUrl);

		return ok(result);
	}

	private static String createGameActorInCache() {
		UUID uuid = UUID.randomUUID();
		final String gameId = uuid.toString();

		ActorRef gameRef = actorSystem.actorOf(Props.create(GameOfLife.class));
		gameActorCache.put(gameId, gameRef);

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

		ActorRef gameRef = gameActorCache.get(gameId);
		return new GameWebSocket(gameRef);
	}

	private static boolean gameNotExists(String gameId) {
		return !gameActorCache.containsKey(gameId);
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result createHighscore() {
		JsonNode data = request().body().asJson();
		WS.url(HIGHSCORE_SERVER).post(data);
		return ok();
	}

}
