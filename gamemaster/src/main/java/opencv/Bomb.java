package opencv;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class Bomb {

	private int id;
	private float bearing;
	private DateTime timer;
	private double wobble = 0;
	private double wobbleLimit = 20;
	private double wobbleSpeedMin = 1;
	private double wobbleSpeed = 0.1;
	private double wobbleSpeedMax = 20;
	private boolean wobbleUp = true;
	
	private double wobbleDobble = 0;

	public float getBearing() {
		return bearing;
	}
	
	public double getWobble(){
		return wobble;
	}
	
	public int getSecondsRemaining(){
		return Seconds.secondsBetween(DateTime.now(), timer).getSeconds() + 1;
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
		Seconds diff = Seconds.secondsBetween(DateTime.now(), timer);
		
		double speedFactor = 1.0 - (double)Math.abs(diff.getSeconds())/30.0;
		if(speedFactor < 0) speedFactor = 0;
		wobbleSpeed = wobbleSpeedMin + (wobbleSpeedMax - wobbleSpeedMin) * speedFactor;
		
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
