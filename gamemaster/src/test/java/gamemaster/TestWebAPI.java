package gamemaster;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.net.HttpURLConnection;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import comm.WebAPI;
import models.GamesInformation;
import models.SpecificGameInformation;

public class TestWebAPI {
	private static final String URL = "http://localhost:8080/";
	private static final Client client = ClientBuilder.newClient();
	private static final Gson gson = new Gson();

	@BeforeClass
	public static void onlyOnce() {
		new WebAPI();
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
		// Test the structure, e.g. response can be converted from JSON to Java
		gson.fromJson(actualBody, GamesInformation.class);
	}

	@Test
	public void testPostGame() {
		Entity<String> payload = Entity.json("{'name': 'Björn and his merry bomb squad'}");
		Response response = client.target(URL + "games").request(APPLICATION_JSON).post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testGetInformationSpecificGame() {
		int idToTest = 1337;
		WebAPI mockedApi = mock(WebAPI.class);
		// Now the actually thing to test
		Response responseToTest = client.target(URL + "games/" + idToTest).request(APPLICATION_JSON).get();
		assertEquals(HttpURLConnection.HTTP_OK, responseToTest.getStatus());
		String actualBodyToTest = responseToTest.readEntity(String.class);
		SpecificGameInformation giToTest = gson.fromJson(actualBodyToTest, SpecificGameInformation.class);
		assertEquals(idToTest, giToTest.getGameId());
	}

	@Test
	public void testPostJoinGame() {
		int idToTest = postDummyTestGameData();
		Entity<String> payload = Entity.json("{'name': 'Björn and his merry bomb squad'}");
		Response response = client.target(URL + "games/" + idToTest + "/players").request(APPLICATION_JSON).post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testDeleteLeaveGame() {
		int gameIdToTest = postDummyTestGameData();
		// TODO: Need the player id?
		int playerIdToTest = 42;
		Response response = client.target(URL + "games/" + gameIdToTest + "/" + playerIdToTest)
				.request(APPLICATION_JSON).delete();
		assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.getStatus());
	}

	@Test
	public void testPostDefuseBomb() {
		int gameIdToTest = postDummyTestGameData();
		// TODO: Need the player id?
		Entity<String> payload = Entity.json("{'playerid': 42}");
		Response response = client.target(URL + "games/" + gameIdToTest + "/defuses").request(APPLICATION_JSON)
				.post(payload);
		assertEquals(HttpURLConnection.HTTP_CREATED, response.getStatus());
	}

	@Test
	public void testGetDefusalAttempts() {
		int gameIdToTest = postDummyTestGameData();
		Response response = client.target(URL + "games/" + gameIdToTest + "/defuses").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		// Test the structure, e.g. response can be converted from JSON to Java
		GamesInformation gi = gson.fromJson(actualBody, GamesInformation.class);
	}

	private int postDummyTestGameData() {
		String randomName = UUID.randomUUID().toString();
		Entity<String> payload = Entity.json("{'name': '" + randomName + "'}");
		client.target(URL + "games").request(APPLICATION_JSON).post(payload);
		// Get all games and use the first ID
		Response response = client.target(URL + "games").request(APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		GamesInformation gi = gson.fromJson(actualBody, GamesInformation.class);

		int nr = 0;
		for (int i = 0; i < gi.getGames().size(); i++) {
			if (gi.getGames().get(0).getName().equals(randomName)) {
				nr = i;
				System.out.println("Test: found " + randomName);
			}
		}
		return gi.getGames().get(nr).getId();
	}
}
