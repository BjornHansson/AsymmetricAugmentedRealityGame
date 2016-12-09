package models;

import java.util.ArrayList;
import java.util.List;

import models.sub.AllActions;
import models.sub.Player;

public class InformationSpecificGame {
	private int gameId;
	private String name;
	private boolean status = false;
	private int defuses = 0;
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

	public List<Player> getAllPlayers() {
		return allPlayers;
	}

	public void setAllPlayers(List<Player> allPlayers) {
		this.allPlayers = allPlayers;
	}

	public void removePlayer(int playerId) {
		for (int i = 0; i < allPlayers.size(); i++) {
			if (allPlayers.get(i).getId() == playerId) {
				allPlayers.remove(i);
			}
		}
	}

	public void addPlayer(Player newPlayer) {
		allPlayers.add(newPlayer);
	}
}
