package comm;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import models.BombInformation;
import models.BombsInGame;
import models.GamesCollection;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.Action;
import models.sub.AllActions;
import models.sub.GamesCollectionSub;
import models.sub.Parameter;
import models.sub.Player;

/**
 * Holds a game
 */
public class GamesHolder {
	private int myCurrentGameId = 0;
	private int myPlayersIdsCounter = 0;
	private List<SpecificGameInformation> myGames = new ArrayList<SpecificGameInformation>();

	/**
	 * Get specific information about a game
	 * 
	 * @param gameId
	 *            the game ID to get information about
	 * @return the found game info if found, else return null
	 */
	public SpecificGameInformation getInformationSpecificGame(int gameId) {
		SpecificGameInformation isg = new SpecificGameInformation();
		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getGameId() == gameId) {
				isg = myGames.get(i);
			}
		}

		AllActions actions = new AllActions();
		Action defuse = new Action();
		defuse.setUrl("/games/" + isg.getGameId() + "/defuse");
		defuse.setMethod(HttpMethod.POST);
		actions.setDefuse(defuse);
		Parameter param = new Parameter();
		param.setPlayerId("number");
		defuse.addParameter(param);
		// Only support for one player
		if (!isg.getAllPlayers().isEmpty()) {
			Action leave = new Action();
			leave.setUrl("/games/" + isg.getGameId() + "/" + isg.getAllPlayers().get(0).getId());
			leave.setMethod(HttpMethod.DELETE);
			actions.setLeaveGame(leave);
		}
		isg.setActions(actions);

		return isg;
	}

	/**
	 * Start a new game
	 * 
	 * @param gameName
	 *            The new name of the game
	 * @return the representation of a started game
	 */
	public StartGameInformation startGame(String gameName) {
		myCurrentGameId++;

		SpecificGameInformation game = new SpecificGameInformation();
		game.setGameId(myCurrentGameId);
		game.setName(gameName);
		myGames.add(game);

		GamesCollection gamesCollection = getGamesCollection();

		Action currentGame = new Action();
		currentGame.setMethod(HttpMethod.GET);
		currentGame.setUrl("/games/" + myCurrentGameId);
		AllActions actionsGc = new AllActions();
		actionsGc.setCurrentGame(currentGame);
		gamesCollection.setActions(actionsGc);

		StartGameInformation sg = new StartGameInformation();
		sg.setGameId(myCurrentGameId);
		sg.setName(gameName);

		Action registration = new Action();
		registration.setMethod(HttpMethod.POST);
		registration.setUrl("/games/" + myCurrentGameId);
		Parameter param = new Parameter();
		param.setName("string");
		registration.addParameter(param);

		Action information = new Action();
		information.setMethod(HttpMethod.GET);
		information.setUrl("/games/" + myCurrentGameId);

		AllActions actions = new AllActions();
		actions.setRegistration(registration);
		actions.setInformation(information);
		sg.setActions(actions);

		return sg;
	}

	/**
	 * Get a collection of all games
	 * 
	 * @return
	 */
	public GamesCollection getGamesCollection() {
		GamesCollection gamesCollection = new GamesCollection();
		for (int i = 0; i < myGames.size(); i++) {
			GamesCollectionSub sub = new GamesCollectionSub();
			sub.setGameId(myGames.get(i).getGameId());
			sub.setName(myGames.get(i).getName());
			gamesCollection.addGame(sub);
		}

		AllActions actions = new AllActions();
		Action action = new Action();
		action.setUrl("/games/" + myCurrentGameId);
		action.setMethod(HttpMethod.GET);
		actions.setCurrentGame(action);
		gamesCollection.setActions(actions);

		return gamesCollection;
	}

	/**
	 * Get information about all bombs in a game
	 * 
	 * @param game
	 *            The game ID
	 */
	public BombsInGame listAllBombs(int gameId) {
		// TODO: implement
		return null;
	}

	public BombInformation getBombInformation(int gameId, int bombId) {
		// TODO: implement
		return null;
	}

	/**
	 * Join a game
	 * 
	 * @param gameId
	 * @param playerName
	 * @return the player which joined the game
	 */
	public Player joinGame(int gameId, String playerName) {
		myPlayersIdsCounter++;
		Player newPlayer = new Player();
		newPlayer.setName(playerName);
		newPlayer.setId(myPlayersIdsCounter);

		Action defuse = new Action();
		defuse.setMethod(HttpMethod.POST);
		defuse.setUrl("/games/" + gameId + "/defuse");
		Parameter param = new Parameter();
		param.setPlayerId("number");
		defuse.addParameter(param);
		Action leave = new Action();
		leave.setMethod(HttpMethod.DELETE);
		leave.setUrl("/games/" + gameId + "/" + newPlayer.getId());
		AllActions actions = new AllActions();
		actions.setDefuse(defuse);
		actions.setLeaveGame(leave);
		newPlayer.setActions(actions);

		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getGameId() == gameId) {
				myGames.get(i).addPlayer(newPlayer);
			}
		}
		return newPlayer;
	}

	/**
	 * Leave a game
	 * 
	 * @param gameId
	 *            The game ID to leave
	 * @param playerId
	 *            The player ID
	 */
	public void leaveGame(int gameId, int playerId) {
		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getGameId() == gameId) {
				myGames.get(i).removePlayer(playerId);
			}
		}
	}

	/**
	 * Defuse a bomb
	 * 
	 * @param gameId
	 * @param bombId
	 * @param playerId
	 * @return
	 */
	public BombInformation defuseBomb(int gameId, int bombId, int playerId) {
		return null;
	}

	/**
	 * List all the players in a given game
	 * 
	 * @param gameId
	 *            The game ID
	 * @return list of players
	 */
	public List<Player> listPlayers(int gameId) {
		for (int i = 0; i < myGames.size(); i++) {
			if (myGames.get(i).getGameId() == gameId) {
				return myGames.get(i).getAllPlayers();
			}
		}
		// TODO: Do not return null
		return null;
	}

	public int getCurrentGameId() {
		return myCurrentGameId;
	}
}
