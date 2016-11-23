package gamemaster;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.HttpURLConnection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import comm.WebAPI;
import models.GamesInformation;

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
		Response response = client.target(URL + "games").request(MediaType.APPLICATION_JSON).get();
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Allow-Origin"));
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Allow-Headers"));
		assertThat(response.getHeaders().toString(), containsString("Access-Control-Request-Method"));
	}

	@Test
	public void testResponseTypeIsJson() {
		Response response = client.target(URL + "games").request(MediaType.APPLICATION_JSON).get();
		assertThat(response.getMediaType().getSubtype(), containsString("json"));
	}

	@Test
	public void testGetInformationAboutGame() {
		Response response = client.target(URL + "games").request(MediaType.APPLICATION_JSON).get();
		String actualBody = response.readEntity(String.class);
		assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
		// Test the structure, e.g. response can be converted from JSON to Java
		GamesInformation gi = gson.fromJson(actualBody, GamesInformation.class);
	}
}
