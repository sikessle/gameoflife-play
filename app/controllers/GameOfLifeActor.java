package controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.sikessle.gameoflife.BaseModule;
import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.persistence.dummy.DummyModule;
import org.sikessle.gameoflife.view.gui.SwingView;
import org.sikessle.gameoflife.view.tui.TextView;

import play.mvc.WebSocket.Out;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Guice;
import com.google.inject.Injector;

import controllers.message.ParseCommandMessage;
import controllers.message.WebSocketOutReadyMessage;

public class GameOfLifeActor extends UntypedActor implements Observer {

	private final TextView ui;
	private final GridController controller;
	private final List<Out<JsonNode>> outSockets;

	public GameOfLifeActor() {
		Injector injector = Guice.createInjector(new BaseModule(),
				new DummyModule());
		controller = injector.getInstance(GridController.class);
		new SwingView(controller);
		ui = new TextView(controller);
		outSockets = new LinkedList<Out<JsonNode>>();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof WebSocketOutReadyMessage) {
			webSocketOutReadyMessage(message);
		} else if (message instanceof ParseCommandMessage) {
			parseCommandMessage(message);
		} else {
			unhandled(message);
		}
	}

	private void parseCommandMessage(Object untypedMessage) {
		ParseCommandMessage message = (ParseCommandMessage) untypedMessage;
		JsonNode jsonCommand = message.getCommand();
		String command = parseJsonCommand(jsonCommand);
		ui.readAndInterpretFromArgument(command);
	}

	private String parseJsonCommand(JsonNode command) {
		JsonNode jsonCommand = command.path("command");
		if (jsonCommand != null) {
			return jsonCommand.asText();
		}
		return null;
	}

	private void webSocketOutReadyMessage(Object untypedMessage) {
		this.outSockets.add(((WebSocketOutReadyMessage) untypedMessage)
				.getOut());
		controller.deleteObserver(this);
		controller.addObserver(this);
		sendBoardToOutSockets();
	}

	@Override
	public void update(Observable o, Object arg) {
		sendBoardToOutSockets();
	}

	private void sendBoardToOutSockets() {
		for (Out<JsonNode> out : outSockets) {
			out.write(GridControllerToJson.getGridAsJson(controller));
		}
	}

}
