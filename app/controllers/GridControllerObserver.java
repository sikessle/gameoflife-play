package controllers;

import java.util.Observable;
import java.util.Observer;

import org.sikessle.gameoflife.controller.GridController;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GridControllerObserver implements Observer {

	private final Out<JsonNode> out;
	private final ObjectMapper mapper;
	private final GridController controller;

	public GridControllerObserver(GridController controller,
			WebSocket.Out<JsonNode> out) {
		this.controller = controller;
		this.out = out;
		mapper = new ObjectMapper();
		controller.addObserver(this);
		sendJsonToOut();
	}

	@Override
	public void update(Observable o, Object arg) {
		sendJsonToOut();
	}

	private void sendJsonToOut() {
		ArrayNode gridJson = getGridJson();
		out.write(gridJson);
	}

	private ArrayNode getGridJson() {
		ArrayNode gridJson = mapper.createArrayNode();
		boolean[][] cells = controller.getCells();
		int rows = controller.getNumberOfRows();
		int columns = controller.getNumberOfColumns();

		for (int i = 0; i < rows; i++) {
			ArrayNode rowJson = mapper.createArrayNode();
			for (int j = 0; j < columns; j++) {
				rowJson.add(cells[i][j]);
			}
			gridJson.add(rowJson);
		}

		return gridJson;
	}

}
