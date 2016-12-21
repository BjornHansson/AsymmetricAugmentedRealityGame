package logic;

import static comm.WebAPI.SERVER_URL;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.joda.time.DateTime;

import models.BombsInGame;
import models.DefusesInformation;
import models.GamesCollection;
import models.SpecificBombInformation;
import models.SpecificDefuseInformation;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.Action;
import models.sub.AllActions;
import models.sub.GamesCollectionSub;
import models.sub.Parameter;
import models.sub.Player;
import opencv.ColoredObjectTrack;

/**
 * Holds current game and history of games
 */
public class GamesHolder {
	private int myCurrentGameId = 0;
	private int myPlayersIdsCounter = 0;
	private int myDefusesIdsCounter = 0;
	private List<SpecificGameInformation> myGames = new ArrayList<SpecificGameInformation>();
	private List<SpecificBombInformation> myBombs = new ArrayList<SpecificBombInformation>();
	private List<SpecificDefuseInformation> myDefuseAttempts = new ArrayList<SpecificDefuseInformation>();
	private ColoredObjectTrack coloredObjectTrack;

	/**
	 * Constructor
	 * 
	 * @param coloredObjectTrack
	 */
	public GamesHolder(ColoredObjectTrack coloredObjectTrack) {
		this.coloredObjectTrack = coloredObjectTrack;
	}

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
		Action information = new Action();
		information.setUrl(SERVER_URL + "/games/" + isg.getGameId());
		information.setMethod(HttpMethod.GET);
		actions.setInformation(information);

		Action defuse = new Action();
		defuse.setUrl(SERVER_URL + "/games/" + isg.getGameId() + "/defuses");
		defuse.setMethod(HttpMethod.POST);
		actions.setDefuse(defuse);
		Parameter param = new Parameter();
		param.setPlayerId("number");
		defuse.addParameter(param);

		Action defuses = new Action();
		defuses.setUrl(SERVER_URL + "/games/" + isg.getGameId() + "/defuses");
		defuses.setMethod(HttpMethod.GET);
		actions.setDefuses(defuses);

		// Only support for one player
		Action join = new Action();
		join.setUrl(SERVER_URL + "/games/" + isg.getGameId() + "/players");
		join.setMethod(HttpMethod.POST);
		Parameter param2 = new Parameter();
		param2.setName("string");
		join.addParameter(param2);
		actions.setJoinGame(join);
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
		currentGame.setUrl(SERVER_URL + "/games/" + myCurrentGameId);
		AllActions actionsGc = new AllActions();
		actionsGc.setCurrentGame(currentGame);
		gamesCollection.setActions(actionsGc);

		StartGameInformation sg = new StartGameInformation();
		sg.setGameId(myCurrentGameId);
		sg.setName(gameName);

		Action registration = new Action();
		registration.setMethod(HttpMethod.POST);
		registration.setUrl(SERVER_URL + "/games/" + myCurrentGameId);
		Parameter param = new Parameter();
		param.setName("string");
		registration.addParameter(param);

		Action information = new Action();
		information.setMethod(HttpMethod.GET);
		information.setUrl(SERVER_URL + "/games/" + myCurrentGameId);

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

		if (myGames.size() > 0) {
			AllActions actions = new AllActions();
			Action action = new Action();
			action.setUrl(SERVER_URL + "/games/" + myCurrentGameId);
			action.setMethod(HttpMethod.GET);
			actions.setCurrentGame(action);
			gamesCollection.setActions(actions);
		}

		return gamesCollection;
	}

	/**
	 * Get information about all bombs in a game
	 * 
	 * @param gameId
	 *            The game ID
	 */
	public BombsInGame listAllBombs(int gameId) {
		BombsInGame bombs = new BombsInGame();
		for (int i = 0; i < myBombs.size(); i++) {
			if (myBombs.get(i).getGameId() == gameId) {

				if (myBombs.get(i).isDefused()) {
					bombs.addDefused(myBombs.get(i));
				} else if (!myBombs.get(i).isDefused()) {
					bombs.addActive(myBombs.get(i));
				}
			}
		}
		return bombs;
	}

	/**
	 * Get specific information about a bomb
	 * 
	 * @param gameId
	 * @param bombId
	 * @return
	 */
	public SpecificBombInformation getBombInformation(int gameId, int bombId) {
		for (int i = 0; i < myBombs.size(); i++) {
			if (myBombs.get(i).getGameId() == gameId && myBombs.get(i).getId() == bombId) {
				return myBombs.get(i);
			}
		}
		return new SpecificBombInformation();
	}

	/**
	 * Defuse a bomb
	 * 
	 * @param gameId
	 * @param playerId
	 * @return
	 */
	public SpecificDefuseInformation defuseBomb(int gameId, int playerId) {
		SpecificDefuseInformation defuse = new SpecificDefuseInformation();

		for (int bombsIndex = 0; bombsIndex < myBombs.size(); bombsIndex++) {
			if (myBombs.get(bombsIndex).isDefused())
				continue;
			if (coloredObjectTrack.canDefuseBomb(myBombs.get(bombsIndex).getId())) {
				coloredObjectTrack.defuseBomb(myBombs.get(bombsIndex).getId());
				myBombs.get(bombsIndex).setDefused(true);
			}
			myDefusesIdsCounter++;
			defuse.setId(myDefusesIdsCounter);
			defuse.setDefused(myBombs.get(bombsIndex).isDefused());
			defuse.setPlayer(playerId);
			defuse.setWhen(myBombs.get(bombsIndex).getExplosionAt());
			defuse.setGameId(gameId);

			Action information = new Action();
			information.setMethod(HttpMethod.GET);
			information.setUrl(SERVER_URL + "/games/" + gameId + "/bombs/" + myBombs.get(bombsIndex).getId());
			AllActions actions = new AllActions();
			actions.setInformation(information);
			defuse.setActions(actions);

			myDefuseAttempts.add(defuse);

			if (myBombs.get(bombsIndex).isDefused())
				break;
		}

		return defuse;
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
		defuse.setUrl(SERVER_URL + "/games/" + gameId + "/defuse");
		Parameter param = new Parameter();
		param.setPlayerId("number");
		defuse.addParameter(param);
		AllActions actions = new AllActions();
		actions.setDefuse(defuse);
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
		return new ArrayList<Player>();
	}

	/**
	 * Get the current running game ID
	 * 
	 * @return
	 */
	public int getCurrentGameId() {
		return myCurrentGameId;
	}

	/**
	 * Add a bomb
	 * 
	 * @param bombId
	 * @param dateTime
	 */
	public void addBomb(int bombId, DateTime dateTime) {
		SpecificBombInformation bomb = new SpecificBombInformation();
		bomb.setId(bombId);
		bomb.setExplosionAt(dateTime);
		bomb.setGameId(myCurrentGameId);
		myBombs.add(bomb);
	}

	/**
	 * Get all defuses
	 * 
	 * @param gameId
	 * @return
	 */
	public DefusesInformation getDefuses(int gameId) {
		DefusesInformation defuses = new DefusesInformation();
		for (int i = 0; i < myDefuseAttempts.size(); i++) {
			if (myDefuseAttempts.get(i).getGameId() == gameId) {
				defuses.addDefuses(myDefuseAttempts.get(i));
			}
		}
		return defuses;
	}
}
