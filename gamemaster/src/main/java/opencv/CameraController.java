package opencv;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class CameraController implements KeyListener {

	private float pan = 0;
	private float tilt = 0;
	public static final float VIEW_ANGLE = 62.8f;

	private float deltaPan = 15;
	private float deltaTilt = 5;
	
	private boolean enabled;

	public float getPan() {
		return pan;
	}

	public CameraController(){
		this(true);
	}
	
	public CameraController(boolean enabled) {
		this.enabled = enabled;
		if(enabled){
			try {
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("autofocus", "on")
						.asString();
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if(!enabled) 
			return;
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			try {
				tilt(deltaTilt);
			} catch (UnirestException e1) {
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_DOWN:
			try {
				tilt(-deltaTilt);
			} catch (UnirestException e1) {
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_LEFT:
			try {
				System.out.println("LEFT");
				pan(-deltaPan);
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_RIGHT:
			try {
				System.out.println("RIGHT");
				pan(deltaPan);
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_PAGE_UP:
			try {
				System.out.println("PAGE_UP");
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rzoom", 200)
						.asString();
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_PAGE_DOWN:
			try {
				System.out.println("PAGE_DOWN");
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rzoom", -200)
						.asString();
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		}
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	private void pan(float amount) throws UnirestException {
		Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rpan", amount).asString();
		pan += amount;
		if (pan > 180) {
			pan -= 360;
		}
		if (pan < -180) {
			pan += 360;
		}
	}
	
	private void tilt(float amount) throws UnirestException {
		Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rtilt", amount).asString();
		tilt += amount;
		if (tilt > 180) {
			tilt -= 360;
		}
		if (tilt < -180) {
			tilt += 360;
		}
	}
}
