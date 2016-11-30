package gamemaster;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import comm.Game;
import comm.WebAPI;
import models.DefuseInformation;
import models.GameInfo;
import models.GamesInformation;
import models.Player;
import models.SpecificGameInformation;

public class TestWebAPI {
	private static final String URL = "http://localhost:8080/";
	private static final Client client = ClientBuilder.newClient();
	private static final Gson gson = new Gson();
	private static final int gameIdToTest = 1337;
	private static final String gameNameToTest = "Hodor";
	private static final int playerIdToTest = 42;

	@BeforeClass
	public static void onlyOnce() {
		// Setting up mock data
		GamesInformation gi = new GamesInformation(gameIdToTest);
		GameInfo g = new GameInfo();
		g.setId(gameIdToTest);
		g.setName(gameNameToTest);
		gi.addGame(g);

		SpecificGameInformation sgi = new SpecificGameInformation();
		sgi.setGameId(gameIdToTest);

		Player player = new Player();
		player.setId(playerIdToTest);
		List<Player> players = new ArrayList<Player>();
		players.add(player);

		DefuseInformation di = new DefuseInformation(gameIdToTest);
		di.addAttempt(true);

		Game mockedGame = mock(Game.class);
		when(mockedGame.getGamesInfo()).thenReturn(gi);
		when(mockedGame.getGameInfo(gameIdToTest)).thenReturn(sgi);
		when(mockedGame.getGameInfo(gameIdToTest)).thenReturn(sgi);
		when(mockedGame.listPlayers(gameIdToTest)).thenReturn(players);
		when(mockedGame.getDefuseInfo(gameIdToTest)).thenReturn(di);

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
	public void testGetInformationAboutGame() {
		Response response = client.target(URL + "games").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		// Test the structure, e.g. response can be converted from JSON to Java,
		// and values
		GamesInformation gi = gson.fromJson(actualBody, GamesInformation.class);
		assertEquals(gameIdToTest, gi.getGames().get(0).getId());
		assertEquals(gameNameToTest, gi.getGames().get(0).getName());
		assertEquals(HttpMethod.GET, gi.getActions().getCurrentgame().getMethod());
	}

	@Test
	public void testPostGame() {
		Entity<String> payload = Entity.json("{'name': 'Björn and his merry bomb squad'}");
		Response response = client.target(URL + "games").request(APPLICATION_JSON).post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
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
		Entity<String> payload = Entity.json("{'name': 'Björn and his merry bomb squad'}");
		Response response = client.target(URL + "games/" + gameIdToTest + "/players").request(APPLICATION_JSON)
				.post(payload);
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
		Response response = client.target(URL + "games/" + gameIdToTest + "/defuses").request(APPLICATION_JSON)
				.post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testGetDefusalAttempts() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/defuses").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		DefuseInformation di = gson.fromJson(actualBody, DefuseInformation.class);
		assertEquals(1, di.getAttempts().size());
		assertTrue(di.getAttempts().get(0));
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
