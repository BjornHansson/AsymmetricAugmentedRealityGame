package models.sub;

public class AllActions {
	private Action currentGame;
	private Action registration;
	private Action information;
	private Action defuse;
	private Action leaveGame;

	public Action getLeaveGame() {
		return leaveGame;
	}

	public void setLeaveGame(Action leaveGame) {
		this.leaveGame = leaveGame;
	}

	public Action getDefuse() {
		return defuse;
	}

	public void setDefuse(Action defuse) {
		this.defuse = defuse;
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
}
