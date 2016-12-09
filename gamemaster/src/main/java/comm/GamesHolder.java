package comm;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import models.DefuseInformation;
import models.GamesCollection;
import models.InformationSpecificGame;
import models.StartGame;
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
	private List<InformationSpecificGame> myGames = new ArrayList<InformationSpecificGame>();

	/**
	 * Get specific information about a game
	 * 
	 * @param gameId
	 *            the game ID to get information about
	 * @return the found game info if found, else return null
	 */
	public InformationSpecificGame getInformationSpecificGame(int gameId) {
		InformationSpecificGame isg = new InformationSpecificGame();
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
	public StartGame startGame(String gameName) {
		myCurrentGameId++;

		InformationSpecificGame game = new InformationSpecificGame();
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

		StartGame sg = new StartGame();
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
	 * @param gameId
	 * @param playerName
	 * @return
	 */
	public Player joinGame(int gameId, String playerName) {
		myPlayersIdsCounter++;
		Player newPlayer = new Player();
		newPlayer.setName(playerName);
		newPlayer.setId(myPlayersIdsCounter);

		Action defuse = new Action();
		defuse.setMethod(HttpMethod.POST);
		defuse.setUrl("/games/" + gameId);
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
	 *            The players ID
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
