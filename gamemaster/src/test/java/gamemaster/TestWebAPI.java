package gamemaster;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import comm.WebAPI;
import logic.GamesHolder;
import models.BombsInGame;
import models.DefusesInformation;
import models.GamesCollection;
import models.SpecificBombInformation;
import models.SpecificDefuseInformation;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.GamesCollectionSub;
import models.sub.Player;

/**
 * Test the web API and the response structure, e.g. response can be converted
 * from JSON to Java.
 */
public class TestWebAPI {
	private static final String URL = "http://localhost:8090/";
	private static final Client CLIENT = ClientBuilder.newClient();
	private static final Gson GSON = new Gson();
	private static final int GAME_ID_TO_TEST = 1337;
	private static final String GAME_NAME_TO_TEST = "Björn and his merry bomb squad";
	private static final int PLAYER_ID_TO_TEST = 42;
	private static final String PLAYER_NAME_TO_TEST = "Hodor";
	private static final int BOMB_ID_TO_TEST = 1;

	@BeforeClass
	public static void onlyOnce() {
		// Setting up mock data
		GamesCollection gc = new GamesCollection();
		GamesCollectionSub g = new GamesCollectionSub();
		g.setGameId(GAME_ID_TO_TEST);
		g.setName(GAME_NAME_TO_TEST);
		gc.addGame(g);

		StartGameInformation sg = new StartGameInformation();
		sg.setGameId(GAME_ID_TO_TEST);
		sg.setName(GAME_NAME_TO_TEST);

		SpecificGameInformation isg = new SpecificGameInformation();
		isg.setGameId(GAME_ID_TO_TEST);

		Player player = new Player();
		player.setId(PLAYER_ID_TO_TEST);
		player.setName(PLAYER_NAME_TO_TEST);
		List<Player> players = new ArrayList<Player>();
		players.add(player);

		BombsInGame bombs = new BombsInGame();
		SpecificBombInformation bomb = new SpecificBombInformation();
		bomb.setName("Active bomb");
		bombs.addActive(bomb);

		SpecificDefuseInformation defuse = new SpecificDefuseInformation();
		DefusesInformation defuses = new DefusesInformation();
		defuses.addDefuses(defuse);

		GamesHolder mockedGame = mock(GamesHolder.class);
		when(mockedGame.getGamesCollection()).thenReturn(gc);
		when(mockedGame.getInformationSpecificGame(GAME_ID_TO_TEST)).thenReturn(isg);
		when(mockedGame.listPlayers(GAME_ID_TO_TEST)).thenReturn(players);
		when(mockedGame.listAllBombs(GAME_ID_TO_TEST)).thenReturn(bombs);
		when(mockedGame.getBombInformation(GAME_ID_TO_TEST, BOMB_ID_TO_TEST)).thenReturn(bomb);
		when(mockedGame.defuseBomb(GAME_ID_TO_TEST, PLAYER_ID_TO_TEST)).thenReturn(defuse);
		when(mockedGame.startGame(GAME_NAME_TO_TEST)).thenReturn(sg);
		when(mockedGame.joinGame(GAME_ID_TO_TEST, PLAYER_NAME_TO_TEST)).thenReturn(player);
		when(mockedGame.getDefuses(GAME_ID_TO_TEST)).thenReturn(defuses);

		new WebAPI(mockedGame);
	}

	@Test
	public void testCorsAllowed() {
		Response response = CLIENT.target(URL + "games").request(APPLICATION_JSON).get();
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Allow-Origin"));
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Allow-Headers"));
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Request-Method"));
	}

	@Test
	public void testResponseTypeIsJson() {
		Response response = CLIENT.target(URL + "games").request(APPLICATION_JSON).get();
		assertThat(response.getMediaType().getSubtype(), containsString("json"));
	}

	@Test
	public void testGetInformationAboutAllGames() {
		Response response = CLIENT.target(URL + "games").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		GamesCollection gi = GSON.fromJson(actualBody, GamesCollection.class);
		assertEquals(GAME_ID_TO_TEST, gi.getGames().get(0).getGameId());
		assertEquals(GAME_NAME_TO_TEST, gi.getGames().get(0).getName());
	}

	@Test
	public void testPostStartGame() {
		Entity<String> payload = Entity.json("{'name': '" + GAME_NAME_TO_TEST + "'}");
		Response response = CLIENT.target(URL + "games").request(APPLICATION_JSON).post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
		String actualBody = response.readEntity(String.class);
		StartGameInformation sg = GSON.fromJson(actualBody, StartGameInformation.class);
		assertEquals(GAME_ID_TO_TEST, sg.getGameId());
		assertEquals(GAME_NAME_TO_TEST, sg.getName());
	}

	@Test
	public void testGetInformationSpecificGame() {
		Response responseToTest = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST).request(APPLICATION_JSON).get();
		assertEquals(HttpURLConnection.HTTP_OK, responseToTest.getStatus());
		String actualBodyToTest = responseToTest.readEntity(String.class);
		SpecificGameInformation giToTest = GSON.fromJson(actualBodyToTest, SpecificGameInformation.class);
		assertEquals(GAME_ID_TO_TEST, giToTest.getGameId());
	}

	@Test
	public void testPostJoinGame() {
		Entity<String> payload = Entity.json("{'name': " + PLAYER_NAME_TO_TEST + "}");
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/players").request(APPLICATION_JSON)
				.post(payload);
		String actualBodyToTest = response.readEntity(String.class);
		Player p = GSON.fromJson(actualBodyToTest, Player.class);
		assertEquals(PLAYER_ID_TO_TEST, p.getId());
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testDeleteLeaveGame() {
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/players/" + PLAYER_ID_TO_TEST)
				.request(APPLICATION_JSON).delete();
		assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.getStatus());
	}

	@Test
	public void testGetListAllBombs() {
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/bombs").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		BombsInGame bombs = GSON.fromJson(actualBody, BombsInGame.class);
		assertEquals("Active bomb", bombs.getActive().get(0).getName());
	}

	@Test
	public void testGetBombInformation() {
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/bombs/1").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		SpecificBombInformation bomb = GSON.fromJson(actualBody, SpecificBombInformation.class);
		assertEquals("Active bomb", bomb.getName());
	}

	@Test
	public void testPostDefuseBomb() {
		Entity<String> payload = Entity.json("{'id': '" + PLAYER_ID_TO_TEST + "'}");
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/defuses").request(APPLICATION_JSON)
				.post(payload);
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
		SpecificBombInformation bomb = GSON.fromJson(actualBody, SpecificBombInformation.class);
		assertEquals(false, bomb.isDefused());
	}

	@Test
	public void testGetDefuseInformation() {
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/defuses").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		DefusesInformation defuses = GSON.fromJson(actualBody, DefusesInformation.class);
		assertEquals(1, defuses.getAttempts().size());
	}

	@Test
	public void testGetAllPlayersInAGame() {
		Response response = CLIENT.target(URL + "games/" + GAME_ID_TO_TEST + "/players").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		Type listType = new TypeToken<ArrayList<Player>>() {
		}.getType();
		List<Player> players = GSON.fromJson(actualBody, listType);
		assertEquals(PLAYER_ID_TO_TEST, players.get(0).getId());
	}
}
