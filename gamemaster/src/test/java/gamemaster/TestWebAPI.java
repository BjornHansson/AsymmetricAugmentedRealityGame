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
	private static final String URL = "http://localhost:8080/";
	private static final Client client = ClientBuilder.newClient();
	private static final Gson gson = new Gson();
	private static final int gameIdToTest = 1337;
	private static final String gameNameToTest = "Björn and his merry bomb squad";
	private static final int playerIdToTest = 42;
	private static final String playerNameToTest = "Hodor";
	private static final int bombIdToTest = 1;

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
		SpecificBombInformation bomb = new SpecificBombInformation();
		bomb.setName("Active bomb");
		bombs.addActive(bomb);

		SpecificDefuseInformation defuse = new SpecificDefuseInformation();
		DefusesInformation defuses = new DefusesInformation();
		defuses.addDefuses(defuse);

		GamesHolder mockedGame = mock(GamesHolder.class);
		when(mockedGame.getGamesCollection()).thenReturn(gc);
		when(mockedGame.getInformationSpecificGame(gameIdToTest)).thenReturn(isg);
		when(mockedGame.listPlayers(gameIdToTest)).thenReturn(players);
		when(mockedGame.listAllBombs(gameIdToTest)).thenReturn(bombs);
		when(mockedGame.getBombInformation(gameIdToTest, bombIdToTest)).thenReturn(bomb);
		when(mockedGame.defuseBomb(gameIdToTest, playerIdToTest)).thenReturn(defuse);
		when(mockedGame.startGame(gameNameToTest)).thenReturn(sg);
		when(mockedGame.joinGame(gameIdToTest, playerNameToTest)).thenReturn(player);
		when(mockedGame.getDefuses(gameIdToTest)).thenReturn(defuses);

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
		Response response = client.target(URL + "games/" + gameIdToTest + "/players/" + playerIdToTest)
				.request(APPLICATION_JSON).delete();
		assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.getStatus());
	}

	@Test
	public void testGetListAllBombs() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/bombs").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		BombsInGame bombs = gson.fromJson(actualBody, BombsInGame.class);
		assertEquals("Active bomb", bombs.getActive().get(0).getName());
	}

	@Test
	public void testGetBombInformation() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/bombs/1").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		SpecificBombInformation bomb = gson.fromJson(actualBody, SpecificBombInformation.class);
		assertEquals("Active bomb", bomb.getName());
	}

	@Test
	public void testPostDefuseBomb() {
		Entity<String> payload = Entity.json("{'id': '" + playerIdToTest + "'}");
		Response response = client.target(URL + "games/" + gameIdToTest + "/defuses").request(APPLICATION_JSON)
				.post(payload);
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
		SpecificBombInformation bomb = gson.fromJson(actualBody, SpecificBombInformation.class);
		assertEquals(false, bomb.isDefused());
	}

	@Test
	public void testGetDefuseInformation() {
		Response response = client.target(URL + "games/" + gameIdToTest + "/defuses").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		DefusesInformation defuses = gson.fromJson(actualBody, DefusesInformation.class);
		assertEquals(1, defuses.getAttempts().size());
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
