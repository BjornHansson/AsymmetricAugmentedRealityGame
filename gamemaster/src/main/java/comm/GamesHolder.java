package comm;

import java.util.ArrayList;
import java.util.List;

import models.DefuseInformation;
import models.GameInfo;
import models.sub.Player;
import models.AllGamesInfo;

/**
 * Holds all the games
 */
public class GamesHolder {
	private List<Game> myGames = new ArrayList<Game>();
	private int gameIdsCounter = 0;

	/**
	 * Get specific information about a game
	 * 
	 * @param gameId
	 *            the game ID to get information about
	 * @return the found game info if found, else return null
	 */
	public GameInfo getGameInfo(int gameId) {
		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getId() == gameId) {
				return myGames.get(i).getGameInfo();
			}
		}
		// TODO Do not return null
		return null;
	}

	/**
	 * Start a new game
	 * 
	 * @param gameName
	 *            The new name of the game
	 */
	public int startGame(String gameName) {
		gameIdsCounter++;
		Game newGame = new Game();
		newGame.setId(gameIdsCounter);
		newGame.setName(gameName);
		myGames.add(newGame);
		return gameIdsCounter;
	}

	/**
	 * Get information about all games (historical and current)
	 */
	public AllGamesInfo getGamesInfo() {
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
