package controllers;

import org.sikessle.gameoflife.controller.GridController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public final class GridControllerToJson {

	private static final ObjectMapper mapper = new ObjectMapper();
	private final GridController controller;

	public GridControllerToJson(GridController controller) {
		this.controller = controller;
	}

	public ArrayNode getGridAsJson() {
		ArrayNode gridJson = mapper.createArrayNode();
		boolean[][] cells = controller.getCells();

		for (int i = 0; i < controller.getNumberOfRows(); i++) {
			ArrayNode rowJson = mapper.createArrayNode();
			for (int j = 0; j < controller.getNumberOfColumns(); j++) {
				rowJson.add(cells[i][j]);
			}
			gridJson.add(rowJson);
		}

		return gridJson;
	}

	public ArrayNode getSavedGamesAsJson() {
		ArrayNode gamesJson = mapper.createArrayNode();
		for (String game : controller.listGames()) {
			gamesJson.add(game);
		}
		return gamesJson;
	}
}
