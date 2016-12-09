
package gamemaster;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
	public void testGetSpecificGameInfo() {
		String nameOfTheGame = "Hodor";
		boolean status = false;
		int defuses = 0;
		StartGame sg = gamesHolderToTest.startGame(nameOfTheGame);

		InformationSpecificGame isg = gamesHolderToTest.getInformationSpecificGame(sg.getGameId());
		assertEquals(1, isg.getGameId());
		assertEquals(nameOfTheGame, isg.getName());
		assertEquals(status, isg.getStatus());
		assertEquals(defuses, isg.getDefuses());
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

		GamesCollection returned = gamesHolderToTest.getGamesCollection();
		assertEquals(2, returned.getGames().size());
		assertEquals(firstNameOfTheGame, returned.getGames().get(0).getName());
		assertEquals(secondNameOfTheGame, returned.getGames().get(1).getName());

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
		StartGame createdGame = gamesHolderToTest.startGame("PieIsNice");
		gamesHolderToTest.joinGame(createdGame.getGameId(), "Hodor");
	}
}
