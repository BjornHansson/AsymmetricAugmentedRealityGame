package models;

import java.util.ArrayList;
import java.util.List;

import models.sub.AllActions;

/**
 * Contains information when using GET on /games/:gameid/bombs URL.
 */
public class BombsInGame {
	private List<BombInformation> active = new ArrayList<BombInformation>();
	private List<BombInformation> defused = new ArrayList<BombInformation>();
	private AllActions actions = new AllActions();

	public List<BombInformation> getActive() {
		return active;
	}

	public void setActive(List<BombInformation> active) {
		this.active = active;
	}

	public List<BombInformation> getDefused() {
		return defused;
	}

	public void setDefused(List<BombInformation> defused) {
		this.defused = defused;
	}

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}
}
