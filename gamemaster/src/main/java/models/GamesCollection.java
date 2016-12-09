package models;

import java.util.ArrayList;
import java.util.List;

import models.sub.AllActions;
import models.sub.GamesCollectionSub;

/**
 * Contains information when using GET on /games URL.
 * 
 * @see <a href=
 *      "http://docs.aarg.apiary.io/reference/0/games-collection/get-information-about-current-and-historical-games">Apiary
 *      link</a>
 */
public class GamesCollection {
	private List<GamesCollectionSub> games = new ArrayList<GamesCollectionSub>();
	private AllActions actions = new AllActions();

	public List<GamesCollectionSub> getGames() {
		return games;
	}

	public void setGames(List<GamesCollectionSub> games) {
		this.games = games;
	}

	public void addGame(GamesCollectionSub game) {
		games.add(game);
	}

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}
}
