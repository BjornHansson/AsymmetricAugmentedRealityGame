package models.sub;

import com.google.gson.annotations.SerializedName;

public class Parameter {
	private String name;
	@SerializedName("playerid")
	private String playerId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerId() {
		return playerId;
	}
}
