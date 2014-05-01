package controllers;

import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.view.tui.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.*;
import play.mvc.*;
import views.html.*;

public class GameOfLifeController extends Controller {

	private static GameSingleton game = GameSingleton.getInstance();
	private static GridController controller = game.getGridController();
	private static TextView textUi = game.getTextUI();
	private static GridControllerToJson controllerJson = new GridControllerToJson(
			controller);

	public static Result index() {
		return ok(index.render());
	}

	public static WebSocket<JsonNode> connectWebSocket() {
		return new GameWebSocket(textUi, controller);
	}

	public static Result getGrid() {
		return ok(controllerJson.getGridAsJson());
	}

	public static Result listGames() {
		return ok(controllerJson.getSavedGamesAsJson());
	}

	public static Result save(String gameName) {
		controller.saveGame(gameName);
		return ok();
	}

	public static Result load(String gameName) {
		controller.loadGame(gameName);
		return ok();
	}

	public static Result clearGrid() {
		controller.killAllCells();
		return ok();
	}

	public static Result stepOneGeneration() {
		controller.stepOneGeneration();
		return ok();
	}

	public static Result toggleCell(int row, int column) {
		if (controller.isCellAlive(row, column)) {
			controller.setCellToDeadAtPosition(row, column);
		} else {
			controller.setCellToLivingAtPosition(row, column);
		}

		return ok();
	}

	public static Result setGridSize(int rows, int columns) {
		controller.setGridSize(rows, columns);
		return ok();
	}

}
