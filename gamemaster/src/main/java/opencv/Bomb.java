package opencv;

public class Bomb {

	private float bearing;
	private double timer;
	
	public float getBearing(){
		return bearing;
	}
	
	public Bomb(float bearing, float timer) {
		this.bearing = bearing;
		this.timer = timer;
	}
	
	//Update the timer here
	public void Update(double time){
		timer -= time;
	}
	
	public boolean hasExploded(){
		return timer < 0;		
	}
}
