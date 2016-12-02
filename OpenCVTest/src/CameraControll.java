import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class CameraControll implements KeyListener {

	public CameraControll() {
		try {
			Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("autofocus", "on")
					.asString();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			try {
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rtilt", 10).asString();
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_DOWN:
			try {
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rtilt", -10)
						.asString();
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_LEFT:
			try {
				System.out.println("LEFT");
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rpan", 10).asString();
			} catch (UnirestException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case KeyEvent.VK_RIGHT:
			try {
				System.out.println("RIGHT");
				Unirest.get("http://root:pass@192.168.20.253/axis-cgi/com/ptz.cgi").queryString("rpan", -10).asString();
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

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
}
