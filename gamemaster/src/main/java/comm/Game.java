package comm;

import java.util.List;

import models.DefuseInformation;
import models.GamesInformation;
import models.Player;
import models.SpecificGameInformation;

// TODO: Get real data from game
public class Game {
	/**
	 * 
	 * @param game
	 */
	public SpecificGameInformation getGameInfo(int game) {
		return null;
	}

	/**
	 * 
	 */
	public void startGame() {
		// TODO: Move to another file
		// TODO: Get real data from game
	}

	/**
	 * 
	 */
	public GamesInformation getGameInfo() {
		return null;
	}

	/**
	 * 
	 * @param game
	 */
	public DefuseInformation getDefuseInfo(int game) {
		return null;
	}

	/**
	 * 
	 * @param game
	 * @param player
	 */
	public void joinGame(int game, int player) {
	}

	/**
	 * 
	 * @param game
	 * @param player
	 */
	public void leaveGame(int game, int player) {
	}

	/**
	 * 
	 * @param game
	 * @param player
	 */
	public void defuseBomb(int game, int player) {
	}

	public List<Player> listPlayers(int gameId) {
		return null;
	}
}
