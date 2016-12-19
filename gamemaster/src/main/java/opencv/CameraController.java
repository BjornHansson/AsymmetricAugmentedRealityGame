package opencv;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class CameraController implements KeyListener, Runnable {

	private static final String HTTP_AXIS_URL = "http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi";

	public static final float VIEW_ANGLE = 62.8f;

	private float deltaPan = 20;
	private float deltaPanStop = 0;
	private float pan = 0;

	private boolean isMoving = false;
	private boolean enabled;

	public synchronized float getPan() {
		return pan;
	}

	public CameraController() {
		this(true);
	}

	public CameraController(boolean enabled) {
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
			pan = Float.parseFloat(resultString);
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		case KeyEvent.VK_LEFT:
			try {
				System.out.println("LEFT");
				pan(-deltaPanStop);
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_RIGHT:
			try {
				System.out.println("RIGHT");
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

	private void pan(float amount) throws UnirestException {
		Unirest.get(HTTP_AXIS_URL).queryString("continuouspantiltmove", amount + ",0").asString();
	}

	@Override
	public void run() {
		while (true) {
			getPanFromCamera();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
