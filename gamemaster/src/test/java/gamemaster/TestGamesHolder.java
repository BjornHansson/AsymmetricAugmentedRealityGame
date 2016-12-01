
package gamemaster;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import comm.GamesHolder;
import models.AllGamesInfo;
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
	
	@Test
	public void testGetAllGamesInfo() {
		String nameOfTheGame = "Hodor";
		int idOfCreatedGame = gamesHolderToTest.startGame(nameOfTheGame);
//		String[] testArr = {nameOfTheGame, idOfCreatedGame};
		List<GameInfo> games = new ArrayList<GameInfo>();
		GameInfo gi = new GameInfo();
		gi.setGameId(idOfCreatedGame);
		gi.setName(nameOfTheGame);
		games.add(gi);
		
		AllGamesInfo agi = gamesHolderToTest.getGamesInfo();
		assertEquals(games, agi.getGames());

	}

}
