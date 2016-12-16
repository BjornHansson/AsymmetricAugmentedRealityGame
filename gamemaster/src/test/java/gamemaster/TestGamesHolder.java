package gamemaster;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.ws.rs.HttpMethod;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import comm.GamesHolder;
import models.BombsInGame;
import models.GamesCollection;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.Player;
import opencv.ColoredObjectTrack;

public class TestGamesHolder {
	private GamesHolder gamesHolderToTest;
	private static ColoredObjectTrack track;

	@BeforeClass
	public static void onlyOnce() {
		new ColoredObjectTrack();
		track = mock(ColoredObjectTrack.class);
		doNothing().when(track).SpawnBomb();
	}

	@Before
	public void beforeEach() {
		gamesHolderToTest = new GamesHolder(track);
	}

	@Test
	public void testGetInformationSpecificGame() {
		String nameOfTheGame = "Hodor";
		boolean status = false;
		int defuses = 0;
		StartGameInformation sg = gamesHolderToTest.startGame(nameOfTheGame);
		gamesHolderToTest.joinGame(1, "Player name");

		SpecificGameInformation isg = gamesHolderToTest.getInformationSpecificGame(sg.getGameId());
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
		StartGameInformation createdGameFirst = gamesHolderToTest.startGame(firstNameOfTheGame);
		assertEquals(createdGameFirst.getGameId(), gamesHolderToTest.getCurrentGameId());
		StartGameInformation createdGameSecond = gamesHolderToTest.startGame(secondNameOfTheGame);
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
	public void testListAllBombs() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		BombsInGame bombs = gamesHolderToTest.listAllBombs(createdGame.getGameId());
		assertEquals(0, bombs.getActive().size());
		assertEquals(0, bombs.getDefused().size());
		// TODO: How to add bombs?
	}

	@Test
	public void testGetBombInformation() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		// BombInformation bomb = gamesHolderToTest.getBombInformation(gameId,
		// bombId);
		// TODO: Implement. Logic to add bombs
	}

	@Test
	public void testDefuseBomb() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		// BombInformation bomb = gamesHolderToTest.defuseBomb(gameId, bombId,
		// playerId);
		// TODO: Implement. Logic to add bombs
	}

	@Test
	public void testJoinGame() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		int createdGameId = createdGame.getGameId();
		Player joinedPlayer = gamesHolderToTest.joinGame(createdGameId, "Hodor");

		assertEquals(1, joinedPlayer.getId());
		assertEquals("/games/1/defuse", joinedPlayer.getActions().getDefuse().getUrl());
		assertEquals(HttpMethod.POST, joinedPlayer.getActions().getDefuse().getMethod());
		assertEquals("number", joinedPlayer.getActions().getDefuse().getParameters().get(0).getPlayerId());
		assertEquals("/games/1/1", joinedPlayer.getActions().getLeaveGame().getUrl());
		assertEquals(HttpMethod.DELETE, joinedPlayer.getActions().getLeaveGame().getMethod());
	}

	@Test
	public void testLeaveGame() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		int createdGameId = createdGame.getGameId();
		gamesHolderToTest.joinGame(createdGameId, "Hodor");

		assertEquals(1, gamesHolderToTest.getInformationSpecificGame(createdGameId).getAllPlayers().size());
		gamesHolderToTest.leaveGame(createdGameId, 1);
		assertEquals(0, gamesHolderToTest.getInformationSpecificGame(createdGameId).getAllPlayers().size());
	}

	@Test
	public void testListPlayers() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		int createdGameId = createdGame.getGameId();
		gamesHolderToTest.joinGame(createdGameId, "Hodor");
		gamesHolderToTest.joinGame(createdGameId, "Jon Snow");
		gamesHolderToTest.joinGame(createdGameId, "Tyrion Lannister");
		List<Player> players = gamesHolderToTest.listPlayers(createdGameId);
		assertEquals(3, players.size());
		assertEquals(players.get(2).getId(), 3);
		assertEquals(players.get(2).getName(), "Tyrion Lannister");
	}

	@Test
	public void testStartGame() {
		String nameOfTheGame = "PieIsNice";
		StartGameInformation createdGame = gamesHolderToTest.startGame(nameOfTheGame);

		assertEquals(1, createdGame.getGameId());
		assertEquals(nameOfTheGame, createdGame.getName());
		assertEquals("/games/1", createdGame.getActions().getRegistration().getUrl());
		assertEquals(HttpMethod.POST, createdGame.getActions().getRegistration().getMethod());
		assertEquals("string", createdGame.getActions().getRegistration().getParameters().get(0).getName());
		assertEquals("/games/1", createdGame.getActions().getInformation().getUrl());
		assertEquals(HttpMethod.GET, createdGame.getActions().getInformation().getMethod());
	}
}
