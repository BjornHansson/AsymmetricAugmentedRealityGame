package comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import models.DefuseInformation;
import models.GameInfo;
import models.sub.Player;
import models.AllGamesInfo;

/**
 * Holds all the games
 */
public class GamesHolder {
	private List<GameInfo> myGames = new ArrayList<GameInfo>();
	private int myGameIdsCounter = 0;
	private int myCurrentGameId = 0;
	private int myPlayersIdsCounter = 0;

	/**
	 * Get specific information about a game
	 * 
	 * @param gameId
	 *            the game ID to get information about
	 * @return the found game info if found, else return null
	 */
	public GameInfo getGameInfo(int gameId) {
		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getGameId() == gameId) {
				return myGames.get(i);
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
		myGameIdsCounter++;
		GameInfo newGame = new GameInfo();
		newGame.setGameId(myGameIdsCounter);
		newGame.setName(gameName);
		myGames.add(newGame);
		myCurrentGameId  = myGameIdsCounter;
		return myGameIdsCounter;
	}

	/**
	 * creates AllGameInfo model and adds myGames list to the model 
	 * @return AllGamesInfo with list of myGames
	 */
	public AllGamesInfo getGamesInfo() {
		AllGamesInfo agi = new AllGamesInfo(myCurrentGameId);
		for (int i = 0; i < myGames.size(); i++) {
			agi.addGame(myGames.get(i));
		}
		return agi;
	}

	/**
	 * Get information about defuse attempt
	 * 
	 * @param game
	 *            The game ID to get defuse information about
	 */
	public DefuseInformation getDefuseInfo(int gameId) {
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
	public int joinGame(int gameId, String playerName) {
		myPlayersIdsCounter++;
		Player newPlayer = new Player(); 
		newPlayer.setName(playerName);
		newPlayer.setId(myPlayersIdsCounter);
		
		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getGameId() == gameId) {
				myGames.get(i).addPlayer(newPlayer);
			}
		}
		return myPlayersIdsCounter;
	}

	/**
	 * Leave a game
	 * 
	 * @param game
	 *            The game ID to leave
	 * @param player
	 *            The players ID
	 */
	public void leaveGame(int gameId, int player) {
	}

	/**
	 * Defuse a bomb
	 * 
	 * @param game
	 *            The game ID
	 * @param player
	 *            The players ID
	 */
	public void defuseBomb(int gameId, int player) {
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

	public int getCurrentGameId() {
		return myCurrentGameId;
	}
}
