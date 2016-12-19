package opencv;

import org.joda.time.DateTime;

public class Bomb {

	private int id;
	private float bearing;
	private DateTime timer;
	private double wobble = 0;
	private double wobbleLimit = 20;
	private double wobbleSpeed = 5;
	private boolean wobbleUp = true;
	
	private double wobbleDobble = 0;

	public float getBearing() {
		return bearing;
	}
	
	public double getWobble(){
		return wobble;
	}

	public Bomb(int id, float bearing, DateTime timer) {
		this.id = id;
		this.bearing = bearing;
		this.timer = timer;
		wobbleDobble = Math.random() * 2 * Math.PI;
	}
	
	public void Update(double time){
//		if(wobbleUp){
//			wobble += time * wobbleSpeed;
//			if(wobble >= wobbleLimit)
//				wobbleUp = false;
//		} else {
//			wobble -= time * wobbleSpeed;
//			if(wobble <= 0)
//				wobbleUp = true;
//		}
		
		wobbleDobble += time * wobbleSpeed;
		if(wobbleDobble > Math.PI * 2){
			wobbleDobble -= Math.PI * 2;
		}
		wobble = Math.sin(wobbleDobble)*wobbleLimit;
		
		
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
