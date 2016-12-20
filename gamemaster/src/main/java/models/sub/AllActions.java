package models.sub;

import com.google.gson.annotations.SerializedName;

public class AllActions {
	@SerializedName("currentgame")
	private Action currentGame;
	private Action registration;
	private Action information;
	private Action defuse;
	private Action defuses;
	private Action join;

	public Action getDefuse() {
		return defuse;
	}

	public void setDefuse(Action defuse) {
		this.defuse = defuse;
	}

	public Action getDefuses() {
		return defuses;
	}

	public void setDefuses(Action defuses) {
		this.defuses = defuses;
	}

	public Action getCurrentgame() {
		return currentGame;
	}

	public void setCurrentGame(Action currentGame) {
		this.currentGame = currentGame;
	}

	public Action getRegistration() {
		return registration;
	}

	public void setRegistration(Action registration) {
		this.registration = registration;
	}

	public Action getInformation() {
		return information;
	}

	public void setInformation(Action information) {
		this.information = information;
	}

	public Action getJoinGame() {
		return join;
	}

	public void setJoinGame(Action join) {
		this.join = join;
		// TODO Auto-generated method stub

	}
}
