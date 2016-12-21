package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href=
 *      "http://docs.aarg.apiary.io/#reference/0/bomb-defusal/get-a-list-of-defuse-attempts">Apiary</a>
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
