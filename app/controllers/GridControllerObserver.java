package controllers;

import java.util.Observable;
import java.util.Observer;

import org.sikessle.gameoflife.controller.GridController;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

import com.fasterxml.jackson.databind.JsonNode;

public class GridControllerObserver implements Observer {

	private final Out<JsonNode> out;
	private final GridController controller;

	public GridControllerObserver(GridController controller,
			WebSocket.Out<JsonNode> out) {
		this.out = out;
		this.controller = controller;
		controller.addObserver(this);
		sendJsonToOut();
	}

	@Override
	public void update(Observable o, Object arg) {
		sendJsonToOut();
	}

	private void sendJsonToOut() {
		out.write(GridControllerToJson.getGridAsJson(controller));
	}

}
