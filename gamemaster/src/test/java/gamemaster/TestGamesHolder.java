
package gamemaster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.*;


import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.AssertTrue;

import org.junit.Before;
import org.junit.Test;

import comm.GamesHolder;
import models.AllGamesInfo;
import models.DefuseInformation;
import models.GameInfo;

public class TestGamesHolder {
	private GamesHolder gamesHolderToTest;
	// private static final int gameIdToTest = 1337;
	// private static final String gameNameToTest = "Hodor";
	// private static final boolean gameStatusToTest = true;
	// private static final int gameDefusesToTest = 2;
	
	@Before
	public void beforeEach() {
		gamesHolderToTest = new GamesHolder();
	}

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
		String firstNameOfTheGame = "Hodor";
		String secondNameOfTheGame = "Simon";
		int idOfFirstCreatedGame = gamesHolderToTest.startGame(firstNameOfTheGame);
		assertEquals(idOfFirstCreatedGame, gamesHolderToTest.getCurrentGameId());
		int idOfSecondCreatedGame = gamesHolderToTest.startGame(secondNameOfTheGame);
		assertEquals(idOfSecondCreatedGame, gamesHolderToTest.getCurrentGameId());
		assertThat(idOfFirstCreatedGame, not(equalTo(idOfSecondCreatedGame)));
		
		AllGamesInfo returnedAgi = gamesHolderToTest.getGamesInfo();
		assertEquals(2, returnedAgi.getGames().size());
		assertEquals(firstNameOfTheGame, returnedAgi.getGames().get(0).getName());
		assertEquals(secondNameOfTheGame, returnedAgi.getGames().get(1).getName());

	}
	
	@Test
	public void testgetDefuseInfo() {
		int gameIdtoTest = gamesHolderToTest.startGame("PieIsNice");
		DefuseInformation di = gamesHolderToTest.getDefuseInfo(gameIdtoTest);
		
		assertTrue(di.getAttempts().get(0));
	}

}
