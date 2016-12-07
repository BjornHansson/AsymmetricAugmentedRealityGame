package models;

import java.util.ArrayList;
import java.util.List;

import models.sub.AllActions;
import models.sub.Player;

/**
 * Contains information when using GET on /games/:id URL.
 */
public class GameInfo {
	private int gameId;
	private String name;
	private boolean status;
	private int defuses;
	private AllActions actions = new AllActions();
	private List<Player> allPlayers = new ArrayList<Player>();

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getDefuses() {
		return defuses;
	}

	public void setDefuses(int defuses) {
		this.defuses = defuses;
	}

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}

	public void addPlayer(Player newPlayer) {
		allPlayers.add(newPlayer);
	}
	
	public void removePlayer(int playerId){
		for (int j = 0; j < allPlayers.size(); j++) {
			if (playerId == allPlayers.get(j).getId()) {
				allPlayers.remove(j);				
			}
		}
	}
}
