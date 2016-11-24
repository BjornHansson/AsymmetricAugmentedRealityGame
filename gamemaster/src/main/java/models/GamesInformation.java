package models;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

/**
 * Contains information when using GET on /games URL.
 */
public class GamesInformation {
	private List<GameInfo> games = new ArrayList<GameInfo>();
	private Actions actions = new Actions();

	public GamesInformation(int currentGameId) {
		Action currentGame = new Action();
		currentGame.setUrl("/games/" + currentGameId);
		currentGame.setMethod(HttpMethod.GET);
		actions.setCurrentGame(currentGame);
	}

	public List<GameInfo> getGames() {
		return games;
	}

	public void setGames(List<GameInfo> games) {
		this.games = games;
	}

	public Actions getActions() {
		return actions;
	}

	public void setActions(Actions actions) {
		this.actions = actions;
	}

	public void addGame(GameInfo gi) {
		games.add(gi);
	}
}
