package comm;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

import java.net.HttpURLConnection;
import java.util.List;

import com.google.gson.Gson;

import models.BombsInGame;
import models.DefusesInformation;
import models.GamesCollection;
import models.SpecificBombInformation;
import models.SpecificDefuseInformation;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.GameName;
import models.sub.Player;

public class WebAPI {
	private static final Gson myGson = new Gson();
	private GamesHolder myGamesHolder;

	/**
	 * Constructor that allows a games holder as parameter
	 * 
	 * @param game
	 */
	public WebAPI(GamesHolder game) {
		myGamesHolder = game;
		init();
	}

	private void init() {
		System.out.println("Listening to stuff");
		port(8080);
		enableCORS("*", "*", "*");

		/*
		 * Get information about older and current games
		 */
		get("/games", (request, response) -> {
			System.out.println("Get information about older and current games");
			GamesCollection gc = myGamesHolder.getGamesCollection();
			return myGson.toJson(gc);
		});

		/*
		 * Start a game
		 */
		post("/games", (request, response) -> {
			System.out.println("Start a game");
			response.status(HttpURLConnection.HTTP_CREATED);
			String body = request.body();
			GameName gameName = myGson.fromJson(body, GameName.class);
			StartGameInformation sg = myGamesHolder.startGame(gameName.getName());
			return myGson.toJson(sg);
		});

		/*
		 * Get information on a specific game
		 */
		get("/games/:gameid", (request, response) -> {
			System.out.println("Get information on a specific game");
			int gameId = Integer.parseInt(request.params("gameid"));
			SpecificGameInformation isg = myGamesHolder.getInformationSpecificGame(gameId);
			return myGson.toJson(isg);
		});

		/*
		 * List all players in a game
		 */
		get("/games/:gameid/players", (request, response) -> {
			System.out.println("Join a game");
			int gameId = Integer.parseInt(request.params("gameid"));
			List<Player> players = myGamesHolder.listPlayers(gameId);
			return myGson.toJson(players);
		});

		/*
		 * Join a game
		 */
		post("/games/:gameid/players", (request, response) -> {
			System.out.println("Join a game");
			response.status(HttpURLConnection.HTTP_CREATED);
			int gameId = Integer.parseInt(request.params("gameid"));
			String body = request.body();
			Player postedPlayer = myGson.fromJson(body, Player.class);
			Player createdPlayer = myGamesHolder.joinGame(gameId, postedPlayer.getName());

			return myGson.toJson(createdPlayer);
		});

		/*
		 * Leave a game
		 */
		delete("/games/:gameid/players/:playerid", (request, response) -> {
			System.out.println("Leave a game");
			response.status(HttpURLConnection.HTTP_NO_CONTENT);
			int gameId = Integer.parseInt(request.params("gameid"));
			int playerId = Integer.parseInt(request.params("playerid"));
			myGamesHolder.leaveGame(gameId, playerId);
			return "";
		});

		/*
		 * Defuse a bomb
		 */
		post("/games/:gameid/defuses", (request, response) -> {
			System.out.println("Try to defuse a bomb");
			response.status(HttpURLConnection.HTTP_CREATED);
			int gameId = Integer.parseInt(request.params("gameid"));
			String body = request.body();
			Player player = myGson.fromJson(body, Player.class);
			SpecificDefuseInformation defuse = myGamesHolder.defuseBomb(gameId, player.getId());
			return myGson.toJson(defuse);
		});

		/*
		 * Get a list of defuse attempts
		 */
		get("/games/:gameid/defuses", (request, response) -> {
			System.out.println("Try to defuse a bomb");
			response.status(HttpURLConnection.HTTP_CREATED);
			int gameId = Integer.parseInt(request.params("gameid"));
			DefusesInformation defuses = myGamesHolder.getDefuses(gameId);
			return myGson.toJson(defuses);
		});

		/*
		 * Get information about all bombs in a game
		 */
		get("/games/:gameid/bombs", (request, response) -> {
			System.out.println("Get information on all bombs");
			int gameId = Integer.parseInt(request.params("gameid"));
			BombsInGame bombs = myGamesHolder.listAllBombs(gameId);
			return myGson.toJson(bombs);
		});

		/*
		 * Get information about a specific bomb in a game
		 */
		get("/games/:gameid/bombs/:bombid", (request, response) -> {
			System.out.println("Get information on all bombs");
			int gameId = Integer.parseInt(request.params("gameid"));
			int bombId = Integer.parseInt(request.params("bombid"));
			SpecificBombInformation bomb = myGamesHolder.getBombInformation(gameId, bombId);
			return myGson.toJson(bomb);
		});
	}

	private static void enableCORS(final String origin, final String methods, final String headers) {

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Request-Method", methods);
			response.header("Access-Control-Allow-Headers", headers);
			response.type("application/json");
		});
	}
}
