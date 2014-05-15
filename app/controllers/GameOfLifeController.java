package controllers;

import java.util.HashMap;
import java.util.Map;
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

public class GameOfLifeController extends Controller {

	private static Map<UUID, GameOfLife> gameCache = new HashMap<UUID, GameOfLife>();
	private static final String GAME_SESSION_KEY = "game";

	public static Result index() {
		initializeGameForThisSession();
		return ok(index.render());
	}

	private static void initializeGameForThisSession() {
		// creates a game if not existing. we can ommit the result.
		getGameFromSession();
	}

	public static WebSocket<JsonNode> connectWebSocket() {
		TextView textUi = getGameFromSession().getTextUI();
		return new GameWebSocket(textUi, getGridController());
	}

	private static GridController getGridController() {
		return getGameFromSession().getGridController();
	}

	private static GameOfLife getGameFromSession() {
		UUID gameKey = getGameKeyFromSessionOrCreate();

		ensureGameCacheHasGameWithKey(gameKey);

		return gameCache.get(gameKey);
	}

	private static UUID getGameKeyFromSessionOrCreate() {
		String gameKey = session(GAME_SESSION_KEY);

		if (gameKey == null) {
			UUID newGameKey = UUID.randomUUID();
			session(GAME_SESSION_KEY, newGameKey.toString());
			gameKey = newGameKey.toString();
		}

		return UUID.fromString(gameKey);
	}

	private static void ensureGameCacheHasGameWithKey(UUID gameKey) {
		if (gameCache.containsKey(gameKey)) {
			return;
		}
		GameOfLife game = new GameOfLife();
		gameCache.put(gameKey, game);
	}

	public static Result getGrid() {
		return ok(GridControllerToJson.getGridAsJson(getGridController()));
	}

	public static Result listGames() {
		return ok(GridControllerToJson.getSavedGamesAsJson(getGridController()));
	}

	public static Result save(String gameName) {
		getGridController().saveGame(gameName);
		return ok();
	}

	public static Result load(String gameName) {
		getGridController().loadGame(gameName);
		return ok();
	}

	public static Result clearGrid() {
		getGridController().killAllCells();
		return ok();
	}

	public static Result stepOneGeneration() {
		getGridController().stepOneGeneration();
		return ok();
	}

	public static Result toggleCell(int row, int column) {
		GridController controller = getGridController();

		if (controller.isCellAlive(row, column)) {
			controller.setCellToDeadAtPosition(row, column);
		} else {
			controller.setCellToLivingAtPosition(row, column);
		}

		return ok();
	}

	public static Result setGridSize(int rows, int columns) {
		getGridController().setGridSize(rows, columns);
		return ok();
	}

}
