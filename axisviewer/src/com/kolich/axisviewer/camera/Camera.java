/**
 * Copyright (c) 2016 Mark S. Kolich
 * http://mark.koli.ch
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.kolich.axisviewer.camera;

//import static java.awt.Container.log;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.kolich.axisviewer.authenticate.MyAuthenticator;
import com.kolich.axisviewer.constants.Constants;

public class Camera extends JComponent {

	// private static final Logger log = getLogger(Camera.class);

	private static final long serialVersionUID = 7093285565252932115L;

	// IMAGE RELATED OBJECTS
	private Image cameraStream; // Actual Image which holds live binary
								// JPEG-image data.
	private PixelGrabber grabber; // Used to grab pixel information from the
									// real-time JPEG-stream.
	private int pixels[]; // 1D which holds binary image data.
	private BufferedImage bufferedCamera; // Buffered camera-image.

	// CAMERA AUTHENTICATION/CONNECTIVITY RELATED OBJECTS
	// Use to authenticate against a HTTP protected camera-stream.
	private MyAuthenticator authenticator;

	// Camera URL, name, authentication username and password.
	private URL cameraURL;
	private String cameraURLString;
	private String username;
	private String password;
	private String cameraName;

	// CAMERA CONTROL FIELDS
	private boolean error = false; // True if we have a camera configuration
									// error.
	private boolean isPaused = false; // Is the camera stream paused?

	// Default refresh rate as defined in the constants file.
	private long refreshRate = Constants.REFRESH_RATE;

	/**
	 * Create a new Camera object.
	 * 
	 * @param name
	 * @param url
	 * @param username
	 * @param password
	 * @param refreshRate
	 */
	public Camera(String name, String url, String username, String password, long refreshRate) {

		this.cameraName = name; // Set the camera name.
		this.cameraURLString = url; // Set the camera URL.
		this.username = username; // Set the camera username.
		this.password = password; // Set the camera password.
		this.refreshRate = refreshRate; // Set the desired refresh rate.

		try {
			// Create a new URL object from the URL-string of our camera.
			cameraURL = new URL(this.cameraURLString);
		} catch (MalformedURLException m) {
			this.error = true;
		}

		// Check if this camera requires authentication.
		// If so, then create and set the authenticator object.
		if (!username.equals("") && !password.equals("")) {
			this.authenticator = new MyAuthenticator(this.username, this.password);
			Authenticator.setDefault(this.authenticator);
		}

	} // end public Camera ( ... )

	/**
	 * Paint the camera stream to the JInternalFrame.
	 */
	@Override
	public void paint(Graphics g) {

		// Print loading the window canvas....
		if (this.cameraStream == null) {

			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, 640, 480);

			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Arial", Font.BOLD, 50));
			g.drawString("BUFFERING", 175, 250);

			return;

		} // end if camerastream is null

		// Create a new BufferedImage, the Silhouette buffered camera.
		this.bufferedCamera = new BufferedImage(this.cameraStream.getWidth(null), this.cameraStream.getHeight(null),
				BufferedImage.TYPE_INT_RGB);

		// Create the graphics object.
		Graphics2D g2 = this.bufferedCamera.createGraphics();
		g2.drawImage(this.cameraStream, null, null);

		// Grab the pixels from the camera stream.
		this.grabPixels();

		g.drawImage(this.bufferedCamera, 0, 0, null);

		// If paused, show the red paused strip at the bottom of the image
		// window.
		if (this.isPaused) {

			g.setColor(new Color(255, 0, 0, 90));
			g.fillRect(0, 460, 640, 20);

			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Arial", Font.BOLD, 15));
			g.drawString("PAUSED", 3, 475);

		} // end if

		// If we have an error, show the red error strip.
		if (this.error) {

			g.setColor(new Color(255, 0, 0, 90));
			g.fillRect(0, 456, 640, 20);

			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Arial", Font.BOLD, 15));
			g.drawString("I/O ERROR:  Check network connection.", 3, 471);

		} // end if error

	} // end public void paint ( Graphics g )

	/**
	 * Refresh the camera image.
	 *
	 */
	public void refresh() {

		// If the camera object is null, return.
		if (this.cameraURL == null) {
			return;
		}

		// Try to read the data from the camera URL.
		try {

			// Read the image data from the camera URL.
			this.cameraStream = ImageIO.read(cameraURL);

			// No errors were found.
			this.error = false;

		} catch (IOException e) {

			// Caught an exception, so set the error flag to true.
			this.error = true;

		} // end catch

		// Repaint the camera image.
		this.repaint();

	} // end public void refresh ( )

	/**
	 * Use a PixelGrabber to grab the pixel data from the camera stream.
	 *
	 */
	public void grabPixels() {

		// Get the width and height of the camera image.
		int width = this.cameraStream.getWidth(this);
		int height = this.cameraStream.getHeight(this);

		// Create a new pixels array with a given width and height.
		this.pixels = new int[width * height];

		// Create a new PixelGrabber using the camera stream and place the
		// grabbed
		// pixel data into this.pixels.
		this.grabber = new PixelGrabber(this.cameraStream, 0, 0, width, height, this.pixels, 0, width);

		try {

			// Grab the pixels.
			this.grabber.grabPixels();

		} catch (Exception e) {
			// log.error("Failed to grab pixels.", e);
		} // end catch

	} // end public void grabPixels ( )

	/**
	 * Gets the name of the camera.
	 * 
	 * @return
	 */
	public String getCameraName() {
		return this.cameraName;
	}

	/**
	 * Sets the name of the camera.
	 * 
	 * @param name
	 */
	public void setCameraName(String name) {
		this.cameraName = name;
	}

	/**
	 * Gets the URL of the camera.
	 * 
	 * @return
	 */
	public String getCameraURL() {
		return this.cameraURLString;
	}

	/**
	 * Sets the URL of the camera.
	 * 
	 * @param url
	 */
	public void setCameraURL(String url) {
		this.cameraURLString = url;
	}

	/**
	 * Gets the username used in the camera authentication process.
	 * 
	 * @return
	 */
	public String getCameraUsername() {
		return this.username;
	}

	/**
	 * Sets the username used in the camera authentication process.
	 * 
	 * @param username
	 */
	public void setCameraUsername(String username) {
		this.username = username;
		this.authenticator.setUsername(username);
	}

	/**
	 * Gets the password used in the camera authentication process.
	 * 
	 * @return
	 */
	public String getCameraPassword() {
		return this.password;
	}

	/**
	 * Returns true or false if the camera requires authentication.
	 * 
	 * @return
	 */
	public boolean requiresAutentication() {
		return !this.password.equals("") && !this.username.equals("");
	} // end public boolean requiresAutentication ( )

	/**
	 * Sets the password used in the camera authentication process.
	 * 
	 * @param password
	 */
	public void setCameraPassword(String password) {
		this.password = password;
		this.authenticator.setPassword(password);
	}

	/**
	 * Get the camera's refresh rate.
	 * 
	 * @return
	 */
	public long getRefreshRate() {
		return this.refreshRate;
	}

	/**
	 * Set the camera's refresh rate.
	 * 
	 * @param rate
	 */
	public void setRefreshRate(long rate) {
		this.refreshRate = rate;
	}

	/**
	 * Get the camera's error status flag.
	 * 
	 * @return
	 */
	public boolean getErrorStatus() {
		return this.error;
	}

	/**
	 * Used to pause or unpause the camera. True = paused. False = playing.
	 * 
	 * @param state
	 */
	public void setPausedState(boolean state) {
		this.isPaused = state;
	}

	/**
	 * Get camera pause state. True = paused. False = playing.
	 * 
	 * @return
	 */
	public boolean getPausedState() {
		return this.isPaused;
	}

	/**
	 * Converts a standard Java Image to a BufferedImage.
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {

		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded.
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels.
		boolean hasAlpha = false;

		// Create a buffered image with a format that's compatible with the
		// screen.
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		try {

			// Determine the type of transparency of the new buffered image.
			int transparency = Transparency.OPAQUE;

			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image...
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();

			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);

		} catch (HeadlessException e) {
		}

		if (bimage == null) {

			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);

		} // end if bimage == null

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image and dispose of the graphics.
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;

	} // end public static BufferedImage toBufferedImage

	/**
	 * Java image libraries typically represent binary image data using a
	 * standard 1D 32-bit integer array. However, using a 1D array for image
	 * maniuplation operations can be confusing. It's more intuitive to use a 2D
	 * array. Therefore, this method converts a standard Java 1D graphics array
	 * into a 2D array.
	 */
	public int[][] imageArrayConvert(int[] source, int height, int width) {

		int destination[][] = new int[width][height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				destination[x][y] = source[y * width + x] & 0xff;
			} // end inner for
		} // end outer for

		return destination;

	} // end public int [ ] [ ] imageArrayConvert

	/**
	 * Returns a string representation of this camera to be used in a properties
	 * file.
	 * 
	 * @return
	 */
	public String getStringRepresentation() {

		return this.cameraName + "," + this.cameraURLString + "," + this.username + "," + this.password + ","
				+ this.refreshRate;

	} // end public String getStringRepresentation ( )

} // end public class Camera
