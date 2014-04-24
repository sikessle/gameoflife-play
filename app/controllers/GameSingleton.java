package controllers;

import org.sikessle.gameoflife.BaseModule;
import org.sikessle.gameoflife.controller.GridController;
import org.sikessle.gameoflife.persistence.dummy.DummyModule;
import org.sikessle.gameoflife.view.tui.TextView;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GameSingleton {

	private static TextView ui;
	private static GridController controller;
	private static GameSingleton instance;

	private GameSingleton() {
		Injector injector = Guice.createInjector(new BaseModule(),
				new DummyModule());
		GridController controller = injector.getInstance(GridController.class);
		ui = new TextView(controller);
	}

	public static GameSingleton getInstance() {
		if (instance == null) {
			instance = new GameSingleton();
		}
		return instance;
	}

	public GridController getGridController() {
		return controller;
	}

	public TextView getTextUI() {
		return ui;
	}

}
