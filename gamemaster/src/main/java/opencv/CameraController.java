package opencv;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import org.joda.time.DateTime;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.util.Pair;

class CameraController implements KeyListener, Runnable {

	
	private ArrayList<Pair<Float,DateTime>> cachedPans = new ArrayList<Pair<Float,DateTime>>();
	
	int delayMS = 700; //HACK
	
	private static final String HTTP_AXIS_URL = "http://root:pass@192.168.20.254/axis-cgi/com/ptz.cgi";

	public static final float VIEW_ANGLE = 62.8f;


	private int deltaPan = 20;
	private int deltaPanStop = 0;
	private float pan = 0;

	private boolean isMoving = false;
	private boolean enabled;
	
	private CoreGame cot;

	public synchronized float getPan() {
		return pan;
	}

	public CameraController(CoreGame cot) {
		this(cot,true);
	}

	public CameraController(CoreGame cot, boolean enabled) {
		this.cot = cot;
		this.enabled = enabled;
		if (enabled) {
			try {
				Unirest.get(HTTP_AXIS_URL).queryString("autofocus", "on").asString();
				Unirest.get(HTTP_AXIS_URL).queryString("tilt", "1").asString();
				Unirest.get(HTTP_AXIS_URL).queryString("zoom", "1").asString();
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void getPanFromCamera() {
		try {
			String response = Unirest.get(HTTP_AXIS_URL).queryString("query", "position").asString().getBody();
			// Will return multiple results in text format
			int start = response.indexOf("pan=") + "pan=".length();
			int end = response.indexOf("pan=") + response.indexOf("tilt=");
			String resultString = response.substring(start, end);
			cachedPans.add(new Pair<Float,DateTime>(Float.parseFloat(resultString),DateTime.now()));
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void setPanDelayed(){
		for(int i = 0; i < cachedPans.size(); i++){
			if(DateTime.now().getMillis() - cachedPans.get(i).getValue().getMillis() >= delayMS){
				pan = cachedPans.get(i).getKey();
				cachedPans.remove(i--);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if (!enabled || isMoving)
			return;
		isMoving = true;
		int keyCode = e.getKeyCode();
		switch (keyCode) {
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
		}
	}

	public void keyReleased(KeyEvent e) {
		if (!enabled)
			return;
		isMoving = false;
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			cot.tryDefuseAll();
		case KeyEvent.VK_LEFT:
			try {
				pan(deltaPanStop);
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_RIGHT:
			try {
				pan(deltaPanStop);
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

	private void pan(int amount) throws UnirestException {
		Unirest.get(HTTP_AXIS_URL).queryString("continuouspantiltmove", amount + ",0").asString();
	}

	@Override
	public void run() {
		while (enabled) {
			getPanFromCamera();
			setPanDelayed();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
