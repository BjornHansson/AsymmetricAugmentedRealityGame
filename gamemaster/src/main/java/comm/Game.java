package comm;

import java.util.List;

import models.DefuseInformation;
import models.GamesInformation;
import models.Player;
import models.SpecificGameInformation;

// TODO: Get real data from game
public class Game {
	/**
	 * Get specific information about a game
	 * 
	 * @param game
	 *            the game ID to get information about
	 */
	public SpecificGameInformation getGameInfo(int game) {
		return null;
	}

	/**
	 * Start a new game
	 */
	public void startGame() {
		// TODO: Move to another file
		// TODO: Get real data from game
	}

	/**
	 * Get information about all games (historical and current)
	 */
	public GamesInformation getGamesInfo() {
		return null;
	}

	/**
	 * Get information about defuse attempt
	 * 
	 * @param game
	 *            The game ID to get defuse information about
	 */
	public DefuseInformation getDefuseInfo(int game) {
		return null;
	}

	/**
	 * Join a game
	 * 
	 * @param game
	 *            The game ID to join
	 * @param player
	 *            The players ID
	 */
	public void joinGame(int game, int player) {
	}

	/**
	 * Leave a game
	 * 
	 * @param game
	 *            The game ID to leave
	 * @param player
	 *            The players ID
	 */
	public void leaveGame(int game, int player) {
	}

	/**
	 * Defuse a bomb
	 * 
	 * @param game
	 *            The game ID
	 * @param player
	 *            The players ID
	 */
	public void defuseBomb(int game, int player) {
	}

	/**
	 * List all the players in a given game
	 * 
	 * @param gameId
	 *            The game ID
	 * @return list of players
	 */
	public List<Player> listPlayers(int gameId) {
		return null;
	}
}
