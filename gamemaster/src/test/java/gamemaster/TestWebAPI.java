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

import comm.GamesHolder;
import comm.WebAPI;
import models.BombsInGame;
import models.GamesCollection;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.GamesCollectionSub;
import models.sub.Player;

/**
 * Test the web API and the response structure, e.g. response can be converted
 * from JSON to Java.
 */
public class TestWebAPI {
	private static final String URL = "http://localhost:8080/";
	private static final Client client = ClientBuilder.newClient();
	private static final Gson gson = new Gson();
	private static final int gameIdToTest = 1337;
	private static final String gameNameToTest = "Björn and his merry bomb squad";
	private static final int playerIdToTest = 42;
	private static final String playerNameToTest = "Hodor";

	@BeforeClass
	public static void onlyOnce() {
		// Setting up mock data
		GamesCollection gc = new GamesCollection();
		GamesCollectionSub g = new GamesCollectionSub();
		g.setGameId(gameIdToTest);
		g.setName(gameNameToTest);
		gc.addGame(g);

		StartGameInformation sg = new StartGameInformation();
		sg.setGameId(gameIdToTest);
		sg.setName(gameNameToTest);

		SpecificGameInformation isg = new SpecificGameInformation();
		isg.setGameId(gameIdToTest);

		Player player = new Player();
		player.setId(playerIdToTest);
		player.setName(playerNameToTest);
		List<Player> players = new ArrayList<Player>();
		players.add(player);

		BombsInGame bombs = new BombsInGame();

		GamesHolder mockedGame = mock(GamesHolder.class);
		when(mockedGame.getGamesCollection()).thenReturn(gc);
		when(mockedGame.getInformationSpecificGame(gameIdToTest)).thenReturn(isg);
		when(mockedGame.listPlayers(gameIdToTest)).thenReturn(players);
		when(mockedGame.listAllBombs(gameIdToTest)).thenReturn(bombs);
		when(mockedGame.startGame(gameNameToTest)).thenReturn(sg);
		when(mockedGame.joinGame(gameIdToTest, playerNameToTest)).thenReturn(player);

		new WebAPI(mockedGame);
	}

	@Test
	public void testCorsAllowed() {
		Response response = client.target(URL + "games").request(APPLICATION_JSON).get();
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Allow-Origin"));
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Allow-Headers"));
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Request-Method"));
	}

	@Test
	public void testResponseTypeIsJson() {
		Response response = client.target(URL + "games").request(APPLICATION_JSON).get();
		assertThat(response.getMediaType().getSubtype(), containsString("json"));
	}

	@Test
	public void testGetInformationAboutAllGames() {
		Response response = client.target(URL + "games").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		GamesCollection gi = gson.fromJson(actualBody, GamesCollection.class);
		assertEquals(gameIdToTest, gi.getGames().get(0).getGameId());
		assertEquals(gameNameToTest, gi.getGames().get(0).getName());
	}

	@Test
	public void testPostStartGame() {
		Entity<String> payload = Entity.json("{'name': '" + gameNameToTest + "'}");
		Response response = client.target(URL + "games").request(APPLICATION_JSON).post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
		String actualBody = response.readEntity(String.class);
		StartGameInformation sg = gson.fromJson(actualBody, StartGameInformation.class);
		assertEquals(gameIdToTest, sg.getGameId());
		assertEquals(gameNameToTest, sg.getName());
	}

	@Test
	public void testGetInformationSpecificGame() {
		Response responseToTest = client.target(URL + "games/" + gameIdToTest).request(APPLICATION_JSON).get();
		assertEquals(HttpURLConnection.HTTP_OK, responseToTest.getStatus());
		String actualBodyToTest = responseToTest.readEntity(String.class);
		SpecificGameInformation giToTest = gson.fromJson(actualBodyToTest, SpecificGameInformation.class);
		assertEquals(gameIdToTest, giToTest.getGameId());
	}

	@Test
	public void testPostJoinGame() {
		Entity<String> payload = Entity.json("{'name': " + playerNameToTest + "}");
		Response response = client.target(URL + "games/" + gameIdToTest + "/players").request(APPLICATION_JSON)
				.post(payload);
		String actualBodyToTest = response.readEntity(String.class);
		Player p = gson.fromJson(actualBodyToTest, Player.class);
		assertEquals(playerIdToTest, p.getId());
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testDeleteLeaveGame() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/" + playerIdToTest)
				.request(APPLICATION_JSON).delete();
		assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.getStatus());
	}

	@Test
	public void testPostDefuseBomb() {
		Entity<String> payload = Entity.json("{'playerid': " + playerIdToTest + "}");
		Response response = client.target(URL + "games/" + gameIdToTest + "/bombs").request(APPLICATION_JSON)
				.put(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testGetListAllBombs() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/bombs").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		BombsInGame di = gson.fromJson(actualBody, BombsInGame.class);
		// TODO: assert
	}

	@Test
	public void testGetAllPlayersInAGame() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/players").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		Type listType = new TypeToken<ArrayList<Player>>() {
		}.getType();
		List<Player> players = gson.fromJson(actualBody, listType);
		assertEquals(playerIdToTest, players.get(0).getId());
	}
}
