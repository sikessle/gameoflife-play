package controllers;

import org.sikessle.gameoflife.BaseModule;
import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.persistence.dummy.DummyModule;
import org.sikessle.gameoflife.view.tui.TextView;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GameOfLife {

	private final TextView ui;
	private final GridController controller;

	public GameOfLife() {
		Injector injector = Guice.createInjector(new BaseModule(),
				new DummyModule());
		controller = injector.getInstance(GridController.class);
		ui = new TextView(controller);
	}

	public GridController getGridController() {
		return controller;
	}

	public TextView getTextUI() {
		return ui;
	}

}
