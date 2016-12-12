package models;

import org.joda.time.DateTime;

import com.google.gson.annotations.SerializedName;

import models.sub.AllActions;

public class BombInformation {
	private int id;
	private String name;
	private boolean defused;
	@SerializedName("explosion_at")
	private DateTime explosionAt;
	private AllActions actions = new AllActions();
}
