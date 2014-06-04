package controllers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import play.libs.WS;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.Future;
import views.html.game;
import views.html.index;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Props;
import akka.pattern.AskableActorSelection;
import akka.util.Timeout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller {

	private static final String HIGHSCORE_SERVER = "http://de-htwg-sa-highscores.herokuapp.com";
	private static final ActorSystem actorSystem = ActorSystem
			.create("GameOfLife");
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Set<String> runningGameIds = new HashSet<String>();

	public static Result index() {
		return ok(index.render(runningGameIds));
	}

	public static Result createGame() {
		String gameId = createGameActorAndAddToList();
		ObjectNode result = mapper.createObjectNode();
		String gameUrl = routes.Application.playGame(gameId).absoluteURL(
				request());
		result.put("gameUrl", gameUrl);

		return ok(result);
	}

	private static String createGameActorAndAddToList() {
		UUID uuid = UUID.randomUUID();
		String gameId = uuid.toString();

		actorSystem.actorOf(Props.create(GameOfLifeActor.class), gameId);
		runningGameIds.add(gameId);

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

		ActorRef gameRef = getGameActorByGameId(gameId);
		return new GameWebSocket(gameRef);
	}

	private static boolean gameNotExists(String gameId) {
		return getGameActorByGameId(gameId) == null;
	}

	private static ActorRef getGameActorByGameId(String gameId) {
		ActorRef result = null;
		ActorSelection selection = actorSystem
				.actorSelection("/user/" + gameId);
		Timeout t = new Timeout(5, TimeUnit.SECONDS);
		AskableActorSelection asker = new AskableActorSelection(selection);
		Future<Object> fut = asker.ask(new Identify(1), t);
		ActorIdentity ident;
		try {
			ident = (ActorIdentity) Await.result(fut, t.duration());
			result = ident.getRef();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result createHighscore() {
		JsonNode data = request().body().asJson();
		WS.url(HIGHSCORE_SERVER).post(data);
		return ok();
	}

}
