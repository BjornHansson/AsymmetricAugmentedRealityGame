package models;

import java.util.ArrayList;
import java.util.List;

import models.sub.AllActions;

/**
 * Contains information when using GET on /games/:gameid/bombs URL.
 */
public class BombsInGame {
	private List<SpecificBombInformation> active = new ArrayList<SpecificBombInformation>();
	private List<SpecificBombInformation> defused = new ArrayList<SpecificBombInformation>();
	private AllActions actions = new AllActions();

	public List<SpecificBombInformation> getActive() {
		return active;
	}

	public void setActive(List<SpecificBombInformation> active) {
		this.active = active;
	}

	public List<SpecificBombInformation> getDefused() {
		return defused;
	}

	public void addDefused(SpecificBombInformation bomb) {
		defused.add(bomb);
	}

	public AllActions getActions() {
		return actions;
	}

	public void setActions(AllActions actions) {
		this.actions = actions;
	}

	public void addActive(SpecificBombInformation bomb) {
		active.add(bomb);
	}
}
