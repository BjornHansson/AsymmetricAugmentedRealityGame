package models;

import com.google.gson.annotations.SerializedName;

import models.sub.AllActions;

/**
 * @see <a href=
 *      "http://docs.aarg.apiary.io/#reference/0/games-collection/start-a-game">Apiary</a>
 */
public class StartGameInformation {
	@SerializedName("gameid")
	private int gameId;
	private String name;
	private AllActions actions;

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

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}
}
