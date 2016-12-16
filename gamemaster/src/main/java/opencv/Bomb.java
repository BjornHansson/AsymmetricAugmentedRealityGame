package opencv;

import org.joda.time.DateTime;

public class Bomb {

	private int id;
	private float bearing;
	private DateTime timer;

	public float getBearing() {
		return bearing;
	}

	public Bomb(int id, float bearing, DateTime timer) {
		this.id = id;
		this.bearing = bearing;
		this.timer = timer;
	}

	public boolean hasExploded() {
		return timer.isBeforeNow();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
