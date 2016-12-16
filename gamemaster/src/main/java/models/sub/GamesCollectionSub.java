package models.sub;

/**
 * Represents a list of games.
 */
public class GamesCollectionSub {
	private int id;
	private String name;

	public int getGameId() {
		return id;
	}

	public void setGameId(int gameId) {
		this.id = gameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
