package models;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;

/**
 * Contains information when using GET on /games/:gameid/defuses URL.
 */
public class DefuseInformation {
	private List<Boolean> attempts = new ArrayList<Boolean>();
	private Actions actions = new Actions();

	public DefuseInformation(int currentGameId) {
		Action information = new Action();
		information.setUrl("/games/" + currentGameId);
		information.setMethod(HttpMethod.GET);
		actions.setInformation(information);
	}

	public List<Boolean> getAttempts() {
		return attempts;
	}

	public void setAttempts(List<Boolean> attempts) {
		this.attempts = attempts;
	}

	public void addAttempt(boolean attempt) {
		this.attempts.add(attempt);
	}
}
