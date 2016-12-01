package comm;

import models.GameInfo;

public class Game {

	// TODO add all members need for a game
	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GameInfo getGameInfo() {
		GameInfo sgi = new GameInfo();
		sgi.setGameId(id);
		sgi.setName(name);
		return sgi;
	}

	public void setName(String gameName) {
		this.name = gameName;
	}
}
