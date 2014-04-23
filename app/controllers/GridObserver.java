package controllers;

import java.util.Observable;
import java.util.Observer;

import org.sikessle.gameoflife.model.Grid;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GridObserver implements Observer {

	private final Out<JsonNode> out;
	private final ObjectMapper mapper;
	private final Grid grid;

	public GridObserver(Grid grid, WebSocket.Out<JsonNode> out) {
		this.grid = grid;
		this.out = out;
		mapper = new ObjectMapper();
		grid.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		ArrayNode gridJson = getGridJson();
		out.write(gridJson);
	}

	private ArrayNode getGridJson() {
		ArrayNode gridJson = mapper.createArrayNode();
		boolean[][] cells = grid.getCells();
		int rows = grid.getNumberOfRows();
		int columns = grid.getNumberOfColumns();

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
