package gamemaster;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.ws.rs.HttpMethod;

import org.junit.Before;
import org.junit.Test;

import comm.GamesHolder;
import models.DefuseInformation;
import models.GamesCollection;
import models.InformationSpecificGame;
import models.StartGame;

public class TestGamesHolder {
	private GamesHolder gamesHolderToTest;

	@Before
	public void beforeEach() {
		gamesHolderToTest = new GamesHolder();
	}

	@Test
	public void testGetInformationSpecificGame() {
		String nameOfTheGame = "Hodor";
		boolean status = false;
		int defuses = 0;
		StartGame sg = gamesHolderToTest.startGame(nameOfTheGame);
		gamesHolderToTest.joinGame(1, "Player name");

		InformationSpecificGame isg = gamesHolderToTest.getInformationSpecificGame(sg.getGameId());
		assertEquals(1, isg.getGameId());
		assertEquals(nameOfTheGame, isg.getName());
		assertEquals(status, isg.getStatus());
		assertEquals(defuses, isg.getDefuses());
		assertEquals("/games/1/defuse", isg.getActions().getDefuse().getUrl());
		assertEquals(HttpMethod.POST, isg.getActions().getDefuse().getMethod());
		assertEquals("number", isg.getActions().getDefuse().getParameters().get(0).getPlayerId());
		assertEquals("/games/1/1", isg.getActions().getLeaveGame().getUrl());
		assertEquals(HttpMethod.DELETE, isg.getActions().getLeaveGame().getMethod());
	}

	@Test
	public void testGetGamesCollection() {
		String firstNameOfTheGame = "Hodor";
		String secondNameOfTheGame = "Simon";
		StartGame createdGameFirst = gamesHolderToTest.startGame(firstNameOfTheGame);
		assertEquals(createdGameFirst.getGameId(), gamesHolderToTest.getCurrentGameId());
		StartGame createdGameSecond = gamesHolderToTest.startGame(secondNameOfTheGame);
		assertEquals(createdGameSecond.getGameId(), gamesHolderToTest.getCurrentGameId());
		assertThat(createdGameFirst, not(equalTo(createdGameSecond)));

		GamesCollection gc = gamesHolderToTest.getGamesCollection();
		assertEquals(2, gc.getGames().size());
		assertEquals(firstNameOfTheGame, gc.getGames().get(0).getName());
		assertEquals(secondNameOfTheGame, gc.getGames().get(1).getName());
		assertEquals("/games/2", gc.getActions().getCurrentgame().getUrl());
		assertEquals(HttpMethod.GET, gc.getActions().getCurrentgame().getMethod());
	}

	@Test
	public void testGetDefuseInfo() {
		StartGame createdGame = gamesHolderToTest.startGame("PieIsNice");
		DefuseInformation di = gamesHolderToTest.getDefuseInfo(createdGame.getGameId());
		// TODO: Implement
		// assertTrue(di.getAttempts().get(0));
	}

	@Test
	public void testJoinGame() {
		// TODO: implement
	}

	@Test
	public void testLeaveGame() {
		StartGame createdGame = gamesHolderToTest.startGame("PieIsNice");
		gamesHolderToTest.joinGame(createdGame.getGameId(), "Hodor");
	}

	@Test
	public void testListPlayers() {
		// TODO: implement
	}

	@Test
	public void testStartGame() {
		String nameOfTheGame = "PieIsNice";
		StartGame createdGame = gamesHolderToTest.startGame(nameOfTheGame);
		assertEquals(1, createdGame.getGameId());
		assertEquals(nameOfTheGame, createdGame.getName());
		assertEquals("/games/1", createdGame.getActions().getRegistration().getUrl());
		assertEquals(HttpMethod.POST, createdGame.getActions().getRegistration().getMethod());
		assertEquals("string", createdGame.getActions().getRegistration().getParameters().get(0).getName());
		assertEquals("/games/1", createdGame.getActions().getInformation().getUrl());
		assertEquals(HttpMethod.GET, createdGame.getActions().getInformation().getMethod());
	}
}
