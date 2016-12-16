package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information when using GET on /games/:gameid/defuses URL.
 */
public class DefusesInformation {
	private List<SpecificDefuseInformation> attempts = new ArrayList<SpecificDefuseInformation>();

	public void addDefuses(SpecificDefuseInformation defuse) {
		attempts.add(defuse);
	}

	public List<SpecificDefuseInformation> getAttempts() {
		return attempts;
	}

	public void setAttempts(List<SpecificDefuseInformation> attempts) {
		this.attempts = attempts;
	}
}
