package opencv;

import java.awt.Rectangle;

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
	
	private Rectangle srcRect;
	private int frames = 2;
	private int frame = 0;
	private double frameTimer = 0;
	private double frameLength = 0.04;
	
	private boolean exploding = false;
	

	public float getBearing() {
		return bearing;
	}
	
	public double getWobble(){
		return wobble;
	}
	
	public Rectangle getSrcRect(){
		return srcRect;
	}
	
	public int getSecondsRemaining(){
		return Seconds.secondsBetween(DateTime.now(), timer).getSeconds() + 1;
	}
	
	private int getAnimation(){
		int s = getSecondsRemaining();
		if(s < 4){
			return 7;
		}
		if(s < 8){
			return 6;
		}
		if(s < 12){
			return 5;
		}
		if(s < 16){
			return 4;
		}
		if(s < 20){
			return 3;
		}
		if(s < 24){
			return 2;
		}
		if(s < 28){
			return 1;
		}
		//if(s < 32){
			return 0;
		//}
	}

	public Bomb(int id, float bearing, DateTime timer) {
		this.id = id;
		this.bearing = bearing;
		this.timer = timer;
		wobbleDobble = Math.random() * 2 * Math.PI;
		srcRect = new Rectangle();
		srcRect.setBounds(0, 0, 200, 238);
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
		if(!exploding){
			Seconds diff = Seconds.secondsBetween(DateTime.now(), timer);
			
			double speedFactor = 1.0 - (double)Math.abs(diff.getSeconds())/30.0;
			if(speedFactor < 0) speedFactor = 0;
			wobbleSpeed = wobbleSpeedMin + (wobbleSpeedMax - wobbleSpeedMin) * speedFactor;
			
			wobbleDobble += time * wobbleSpeed;
			if(wobbleDobble > Math.PI * 2){
				wobbleDobble -= Math.PI * 2;
			}
			wobble = Math.sin(wobbleDobble)*wobbleLimit*speedFactor;
		}
		
		
		//Animate
		frameTimer += time;
		if(frameTimer >= frameLength){
			frameTimer -= frameLength;
			frame = (frame + 1) % frames;
			if(exploding){
				srcRect.setBounds(frame*329, 0, 329, 345);
				//System.out.println("exploding!");
			}
			else
				srcRect.setBounds((2 * (getAnimation() % 2) + frame) * srcRect.width, getAnimation()/2 * srcRect.height, srcRect.width, srcRect.height);
		}
		
	}

	public void explode(){
		exploding = true;
		frame = 0;
		frames = 8;
		frameLength = 0.01;
	}
	
	public boolean finishedExploding(){
		return exploding && frame >= 7;
	}
	
	public boolean hasStartedToExplode(){
		return exploding;
	}
	
	public boolean hasExploded() {
		return timer.isBeforeNow() || exploding;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
