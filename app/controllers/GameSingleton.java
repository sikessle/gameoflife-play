package controllers;

import org.sikessle.gameoflife.controller.impl.TextUIController;
import org.sikessle.gameoflife.model.BaseModule;
import org.sikessle.gameoflife.model.Grid;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GameSingleton {

	private static TextUIController ui;
	private static Grid grid;
	private static GameSingleton instance;

	private GameSingleton() {
		Injector injector = Guice.createInjector(new BaseModule());
		grid = injector.getInstance(Grid.class);
		ui = new TextUIController(grid);
	}

	public static GameSingleton getInstance() {
		if (instance == null) {
			instance = new GameSingleton();
		}
		return instance;
	}

	public TextUIController getUi() {
		return ui;
	}

	public Grid getGrid() {
		return grid;
	}

}
