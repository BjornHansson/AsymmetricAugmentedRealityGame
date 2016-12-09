package models;

import java.util.ArrayList;
import java.util.List;

import models.sub.AllActions;

/**
 * Contains information when using GET on /games/:gameid/defuses URL.
 */
public class BombsInGame {
	private List<BombInformation> active = new ArrayList<BombInformation>();
	private List<BombInformation> defused = new ArrayList<BombInformation>();
	private AllActions actions = new AllActions();
}
