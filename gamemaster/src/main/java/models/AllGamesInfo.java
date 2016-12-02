package models;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import models.sub.Action;
import models.sub.AllActions;

/**
 * Contains information when using GET on /games URL.
 */
public class AllGamesInfo {
	private List<GameInfo> games = new ArrayList<GameInfo>();
	private AllActions actions = new AllActions();
	
	public AllGamesInfo(int currentGameId) {
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

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}

	public void addGame(GameInfo gi) {
		games.add(gi);
	}
}
