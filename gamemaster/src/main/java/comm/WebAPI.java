package comm;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

import models.GameInfo;
import models.DefuseInformation;
import models.GamesInformation;
import models.Player;
import models.SpecificGameInformation;

public class WebAPI {
	private static final Gson gson = new Gson();
	// TODO: Use real game data
	private GamesInformation gamesInformation = new GamesInformation(1337);

	public static void main(String[] args) {
		new WebAPI();
		while (true) {
			// Infinitive loop to keep server alive
		}
	}

	public WebAPI() {
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
			response.status(HttpURLConnection.HTTP_OK);
			GamesInformation gi = getGameInfo();
			return gson.toJson(gi);
		});

		/*
		 * Start a game
		 */
		post("/games", (request, response) -> {
			System.out.println("Start a game");
			response.status(HttpURLConnection.HTTP_CREATED);
			startGame();
			return "";
		});

		/*
		 * Get information on a specific game
		 */
		get("/games/:gameid", (request, response) -> {
			System.out.println("Get information on a specific game");
			response.status(HttpURLConnection.HTTP_OK);
			int gameId = Integer.parseInt(request.params("gameid"));
			SpecificGameInformation sgi = getGameInfo(gameId);
			return gson.toJson(sgi);
		});

		/*
		 * List all players in a game
		 */
		get("/games/:gameid/players", (request, response) -> {
			System.out.println("Join a game");
			int gameId = Integer.parseInt(request.params("gameid"));
			listPlayers(gameId);
			return null;
		});

		/*
		 * Join a game
		 */
		post("/games/:gameid/players", (request, response) -> {
			System.out.println("Join a game");
			response.status(HttpURLConnection.HTTP_CREATED);
			int gameId = Integer.parseInt(request.params("gameid"));
			String body = request.body();
			Player player = gson.fromJson(body, Player.class);
			joinGame(gameId, player.getId());
			return "";
		});

		/*
		 * Leave a game
		 */
		delete("/games/:gameid/:playerid", (request, response) -> {
			System.out.println("Leave a game");
			response.status(HttpURLConnection.HTTP_NO_CONTENT);
			int gameId = Integer.parseInt(request.params("gameid"));
			int playerId = Integer.parseInt(request.params("playerid"));
			leaveGame(gameId, playerId);
			return "";
		});

		/*
		 * Defuse a bomb
		 */
		post("/games/:gameid/defuses", (request, response) -> {
			System.out.println("Defuse a bomb");
			response.status(HttpURLConnection.HTTP_CREATED);
			int gameId = Integer.parseInt(request.params("gameid"));
			String body = request.body();
			Player player = gson.fromJson(body, Player.class);
			defuseBomb(gameId, player.getId());
			return "";
		});

		/*
		 * Get information on defusal attempts
		 */
		get("/games/:gameid/defuses", (request, response) -> {
			System.out.println("Get information on defusal attempts");
			response.status(HttpURLConnection.HTTP_OK);
			int gameId = Integer.parseInt(request.params("gameid"));
			DefuseInformation di = getDefuseInfo(gameId);
			return gson.toJson(di);
		});
	}

	private void listPlayers(int gameId) {
		// TODO Auto-generated method stub
		
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

	/* TEMPORARY STUFF */

	/**
	 * 
	 */
	private void startGame() {
		// TODO: Move to another file
		// TODO: Get real data from game
		GameInfo gi = new GameInfo();
		gi.setId(6666);
		gi.setName("Hodor");
		gamesInformation.addGame(gi);
	}

	/**
	 * 
	 */
	private GamesInformation getGameInfo() {
		// TODO: Move to another file
		// TODO: Get real data from game
		return gamesInformation;
	}

	/**
	 * 
	 * @param game
	 */
	private SpecificGameInformation getGameInfo(int game) {
		// TODO: Move to another file
		// TODO: Get real data from game
		SpecificGameInformation sgi = new SpecificGameInformation();
		sgi.setGameId(game);
		return sgi;
	}

	/**
	 * 
	 * @param game
	 */
	private DefuseInformation getDefuseInfo(int game) {
		// TODO: Move to another file
		return null;
	}

	/**
	 * 
	 * @param game
	 * @param player
	 */
	private void joinGame(int game, int player) {
		// TODO: Move to another file
	}

	/**
	 * 
	 * @param game
	 * @param player
	 */
	private void leaveGame(int game, int player) {
		// TODO: Move to another file
	}

	/**
	 * 
	 * @param game
	 * @param player
	 */
	private void defuseBomb(int game, int player) {
		// TODO: Move to another file
	}
}
