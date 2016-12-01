package gamemaster;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import comm.GamesHolder;
import models.GameInfo;

public class TestGamesHolder {
	private GamesHolder gamesHolderToTest = new GamesHolder();
	// private static final int gameIdToTest = 1337;
	// private static final String gameNameToTest = "Hodor";
	// private static final boolean gameStatusToTest = true;
	// private static final int gameDefusesToTest = 2;

	@Test
	public void testGetGameInfo() {
		String nameOfTheGame = "Hodor";
		int idOfCreatedGame = gamesHolderToTest.startGame(nameOfTheGame);

		GameInfo gi = gamesHolderToTest.getGameInfo(idOfCreatedGame);
		assertEquals(1, gi.getGameId());
		assertEquals(nameOfTheGame, gi.getName());
		// assertEquals(gameNameToTest, sgi.getName());
		// assertEquals(gameStatusToTest, sgi.getStatus());
		// assertEquals(gameDefusesToTest, sgi.getDefuses());
	}

}
