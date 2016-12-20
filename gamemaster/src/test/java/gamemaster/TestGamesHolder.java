package gamemaster;

import static comm.WebAPI.SERVER_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.ws.rs.HttpMethod;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import logic.GamesHolder;
import models.BombsInGame;
import models.GamesCollection;
import models.SpecificBombInformation;
import models.SpecificDefuseInformation;
import models.SpecificGameInformation;
import models.StartGameInformation;
import models.sub.Player;
import opencv.ColoredObjectTrack;

public class TestGamesHolder {
	private GamesHolder gamesHolderToTest;
	private static ColoredObjectTrack track;
	private static final int bombIdCanDefuseToTest = 42;

	@BeforeClass
	public static void onlyOnce() {
		new ColoredObjectTrack();
		track = mock(ColoredObjectTrack.class);
		doNothing().when(track).SpawnBomb();
		when(track.canDefuseBomb(bombIdCanDefuseToTest)).thenReturn(true);
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

		// informations
		assertEquals(SERVER_URL + "/games/1", isg.getActions().getInformation().getUrl());
		assertEquals(HttpMethod.GET, isg.getActions().getInformation().getMethod());

		// Defuse
		assertEquals(SERVER_URL + "/games/1/defuses", isg.getActions().getDefuse().getUrl());
		assertEquals(HttpMethod.POST, isg.getActions().getDefuse().getMethod());
		assertEquals("number", isg.getActions().getDefuse().getParameters().get(0).getPlayerId());

		// defuses
		assertEquals(SERVER_URL + "/games/1/defuses", isg.getActions().getDefuses().getUrl());
		assertEquals(HttpMethod.GET, isg.getActions().getDefuses().getMethod());

		// join game
		assertEquals(SERVER_URL + "/games/1/players", isg.getActions().getJoinGame().getUrl());
		assertEquals(HttpMethod.POST, isg.getActions().getJoinGame().getMethod());
		assertEquals("string", isg.getActions().getJoinGame().getParameters().get(0).getName());
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
		assertEquals(SERVER_URL + "/games/2", gc.getActions().getCurrentgame().getUrl());
		assertEquals(HttpMethod.GET, gc.getActions().getCurrentgame().getMethod());
	}

	@Test
	public void testListAllBombs() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		Player player = gamesHolderToTest.joinGame(createdGame.getGameId(), "Hodor");
		gamesHolderToTest.addBomb(bombIdCanDefuseToTest, DateTime.now());
		gamesHolderToTest.addBomb(313, DateTime.now());
		gamesHolderToTest.defuseBomb(createdGame.getGameId(), player.getId());
		BombsInGame bombs = gamesHolderToTest.listAllBombs(createdGame.getGameId());
		assertEquals(1, bombs.getActive().size());
		assertEquals(1, bombs.getDefused().size());
	}

	@Test
	public void testGetBombInformation() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		int bombId = 1;
		DateTime dateTime = DateTime.now();
		gamesHolderToTest.addBomb(bombId, dateTime);
		SpecificBombInformation bomb = gamesHolderToTest.getBombInformation(createdGame.getGameId(), bombId);
		assertEquals(bombId, bomb.getId());
		assertEquals(dateTime, bomb.getExplosionAt());
	}

	@Test
	public void testDefuseBomb() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		Player player = gamesHolderToTest.joinGame(createdGame.getGameId(), "Hodor");
		gamesHolderToTest.addBomb(1, DateTime.now());
		SpecificDefuseInformation defuse = gamesHolderToTest.defuseBomb(createdGame.getGameId(), player.getId());
		assertEquals(1, defuse.getId());
		assertEquals(player.getId(), defuse.getPlayer());
		assertEquals(false, defuse.isDefused());
		assertNotNull(defuse.getWhen());
		assertEquals(HttpMethod.GET, defuse.getActions().getInformation().getMethod());
		assertEquals(SERVER_URL + "/games/1/bombs/1", defuse.getActions().getInformation().getUrl());
	}

	@Test
	public void testJoinGame() {
		StartGameInformation createdGame = gamesHolderToTest.startGame("PieIsNice");
		int createdGameId = createdGame.getGameId();
		Player joinedPlayer = gamesHolderToTest.joinGame(createdGameId, "Hodor");

		assertEquals(1, joinedPlayer.getId());
		assertEquals(SERVER_URL + "/games/1/defuse", joinedPlayer.getActions().getDefuse().getUrl());
		assertEquals(HttpMethod.POST, joinedPlayer.getActions().getDefuse().getMethod());
		assertEquals("number", joinedPlayer.getActions().getDefuse().getParameters().get(0).getPlayerId());
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
		assertEquals(SERVER_URL + "/games/1", createdGame.getActions().getRegistration().getUrl());
		assertEquals(HttpMethod.POST, createdGame.getActions().getRegistration().getMethod());
		assertEquals("string", createdGame.getActions().getRegistration().getParameters().get(0).getName());
		assertEquals(SERVER_URL + "/games/1", createdGame.getActions().getInformation().getUrl());
		assertEquals(HttpMethod.GET, createdGame.getActions().getInformation().getMethod());
	}
}
