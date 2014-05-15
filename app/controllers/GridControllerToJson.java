package controllers;

import org.sikessle.gameoflife.controller.GridController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class GridControllerToJson {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private GridControllerToJson() {
	}

	public static ObjectNode getGridAsJson(GridController controller) {
		ObjectNode gridJson = MAPPER.createObjectNode();

		appendCells(gridJson, controller);
		appendGenerationStrategy(gridJson, controller);
		appendNumberOfSteppedGenerations(gridJson, controller);

		return gridJson;
	}

	private static void appendCells(ObjectNode gridJson,
			GridController controller) {
		ArrayNode cellsJson = MAPPER.createArrayNode();
		boolean[][] cells = controller.getCells();

		for (int i = 0; i < controller.getNumberOfRows(); i++) {
			ArrayNode rowJson = MAPPER.createArrayNode();
			for (int j = 0; j < controller.getNumberOfColumns(); j++) {
				rowJson.add(cells[i][j]);
			}
			cellsJson.add(rowJson);
		}
		gridJson.put("cells", cellsJson);
	}

	private static void appendGenerationStrategy(ObjectNode gridJson,
			GridController controller) {
		gridJson.put("generationStrategy",
				controller.getGenerationStrategyName());
	}

	private static void appendNumberOfSteppedGenerations(ObjectNode gridJson,
			GridController controller) {
		gridJson.put("numberOfSteppedGenerations",
				controller.getNumberOfSteppedGenerations());
	}

	public static ArrayNode getSavedGamesAsJson(GridController controller) {
		ArrayNode gamesJson = MAPPER.createArrayNode();
		for (String game : controller.listGames()) {
			gamesJson.add(game);
		}
		return gamesJson;
	}
}
