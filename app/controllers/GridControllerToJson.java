package controllers;

import org.sikessle.gameoflife.controller.GridController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class GridControllerToJson {

	private static final ObjectMapper mapper = new ObjectMapper();
	private final GridController controller;

	public GridControllerToJson(GridController controller) {
		this.controller = controller;
	}

	public ObjectNode getGridAsJson() {
		ObjectNode gridJson = mapper.createObjectNode();

		appendCells(gridJson);
		appendGenerationStrategy(gridJson);
		appendNumberOfSteppedGenerations(gridJson);

		return gridJson;
	}

	private void appendCells(ObjectNode gridJson) {
		ArrayNode cellsJson = mapper.createArrayNode();
		boolean[][] cells = controller.getCells();

		for (int i = 0; i < controller.getNumberOfRows(); i++) {
			ArrayNode rowJson = mapper.createArrayNode();
			for (int j = 0; j < controller.getNumberOfColumns(); j++) {
				rowJson.add(cells[i][j]);
			}
			cellsJson.add(rowJson);
		}
		gridJson.put("cells", cellsJson);
	}

	private void appendGenerationStrategy(ObjectNode gridJson) {
		gridJson.put("generationStrategy",
				controller.getGenerationStrategyName());
	}

	private void appendNumberOfSteppedGenerations(ObjectNode gridJson) {
		gridJson.put("numberOfSteppedGenerations",
				controller.getNumberOfSteppedGenerations());
	}

	public ArrayNode getSavedGamesAsJson() {
		ArrayNode gamesJson = mapper.createArrayNode();
		for (String game : controller.listGames()) {
			gamesJson.add(game);
		}
		return gamesJson;
	}
}
