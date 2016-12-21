package models;

import org.joda.time.DateTime;

import com.google.gson.annotations.SerializedName;

import models.sub.AllActions;

/**
 * @see <a href=
 *      "http://docs.aarg.apiary.io/#reference/0/bomb-actions/bomb-information">Apiary</a>
 */
public class SpecificBombInformation {
	private int id;
	private String name;
	private boolean defused;
	@SerializedName("explosion_at")
	private DateTime explosionAt;
	// Transient hides the field
	private transient int gameId;
	private AllActions actions = new AllActions();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefused() {
		return defused;
	}

	public void setDefused(boolean defused) {
		this.defused = defused;
	}

	public DateTime getExplosionAt() {
		return explosionAt;
	}

	public void setExplosionAt(DateTime explosionAt) {
		this.explosionAt = explosionAt;
	}

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
}
