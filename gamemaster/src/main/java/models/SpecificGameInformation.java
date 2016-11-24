package models;

/**
 * Contains information when using GET on /games/{id} URL.
 */
public class SpecificGameInformation {
	private int gameId;
	private String name;
	private boolean status;
	private int defuses;
	private Actions actions = new Actions();

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

	public boolean isStatus() {
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

	public Actions getActions() {
		return actions;
	}

	public void setActions(Actions actions) {
		this.actions = actions;
	}
}
