package opencv;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetCentralMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.util.ArrayList;
import java.util.Random;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.joda.time.DateTime;

import comm.WebAPI;
import logic.GamesHolder;

public class ColoredObjectTrack implements Runnable {

	enum GameState {
		Menu, Calibration, Playing, GameOver
	}

	private GameState gameState = GameState.Calibration;

	private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
	private Random random = new Random();
	public CameraController cameraController;
	private GamesHolder gamesHolder;
	private WebAPI webApi;
	private int bombIdCounter = 1;
	private FrameGrabber grabber;
	private OpenCVFrameConverter.ToIplImage converter;
	private IplImage grabbedImage;
	private IplImage annotatedImage;
	private IplImage thresholdedImage;
	private IplImage bombImage;

	// int a = 0, b = 0, c = 0, d = 100, e = 255, f = 255;
	CvScalar rgba_min = cvScalar(0, 0, 0, 0);
	CvScalar rgba_max = cvScalar(100, 255, 255, 0);

	private OverlayCanvasFrame videoFrame;
	private CanvasFrame thresholdedVideoFrame;
	private ColorValueControlInterface colorinterface;
	
	private double spawnIntervalMin = 0.0;
	private double spawnIntervalMax = 1.0;
	private double spawnTimer = 0;
	private double nextSpawn = 0;

	public static void main(String[] args) {
		ColoredObjectTrack cot = new ColoredObjectTrack();
		Thread th = new Thread(cot);
		th.start();
	}

	public void SpawnBomb() {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusSeconds(20 + random.nextInt(10));
		bombs.add(new Bomb(bombIdCounter, -180 + random.nextFloat() * 360, dateTime));
		// gamesHolder.addBomb(bombIdCounter, dateTime);
		bombIdCounter++;
		// bombs.add(new Bomb(20.3f, 20));
		System.out.println("Bomb spawned at " + bombs.get(bombs.size() - 1).getBearing());
	}

	public boolean canDefuseBomb(int bombId) {
		return true;
		//TODO: Fill this in!
	}

	public void defuseBomb(int bombId) {
		for (int i = 0; i < bombs.size(); i++) {
			if (bombs.get(i).getId() == bombId) {
				bombs.remove(i);
				break;
			}
		}
	}

	public ColoredObjectTrack() {
		//gamesHolder = new GamesHolder(this);
		//webApi = new WebAPI(gamesHolder);

	}

	public void updatergbvalues(int a, int b, int c, int d, int e, int f) {
		rgba_min = cvScalar(a, b, c, 0);
		rgba_max = cvScalar(d, e, f, 0);
	}

	public void updatergbvaluesFromFile(int a, int b, int c, int d, int e, int f) {

		rgba_min = cvScalar(a, b, c, 0);
		rgba_max = cvScalar(d, e, f, 0);
	}

	public void run() {
		System.out.println("Game loop");
		GameLoop();
	}

	private void GameLoop() {

		setupCamera("192.168.20.253");
		// setupCamera(null);
		setupWindows();
		Thread thPan = new Thread(cameraController);
		thPan.start();
		loadBombImage();

		long lastLoopTime = System.nanoTime();
		boolean gameRunning = true;
		while (gameRunning) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			double updateLengthSeconds = (double) updateLength / 1000000000.0;
			lastLoopTime = now;

			GrabFrame();

			switch (gameState) {
			case Calibration:
				thresholdedVideoFrame.showImage(converter.convert(thresholdedImage));
				break;
			case Playing:
				Update(updateLengthSeconds);
				trackAndAnnotate();
				videoFrame.showImage(converter.convert(annotatedImage));
				break;
			case GameOver:
				break;
			default:
			}
		}
	}

	public void play() {
		videoFrame.setVisible(true);
		thresholdedVideoFrame.setVisible(false);
		colorinterface.hide();
		gameState = GameState.Playing;

	}

	private void loadBombImage() {
		bombImage = cvLoadImage(System.getProperty("user.dir") + "\\bomb_small.png", -1);
		if (bombImage == null)
			System.out.println("Failed to load bomb image");
	}

	// Connect a frame grabber to a camera and set things up
	private void setupCamera(String ip) {
		if (ip == null) {
			try {
				grabber = FrameGrabber.createDefault(0);
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				e.printStackTrace();
			}
		} else {
			grabber = new FFmpegFrameGrabber("http://root:pass@" + ip + "/axis-cgi/mjpg/video.cgi");
		}
		converter = new OpenCVFrameConverter.ToIplImage();
		try {
			grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}
		cameraController = new CameraController(ip == null ? false : true);
	}

	private void setupWindows() {
		videoFrame = new OverlayCanvasFrame("Original", cameraController, this);
		videoFrame.addKeyListener(cameraController);
		videoFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		videoFrame.setVisible(false);

		thresholdedVideoFrame = new CanvasFrame("Thresholded");
		thresholdedVideoFrame.addKeyListener(cameraController);
		thresholdedVideoFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		colorinterface = new ColorValueControlInterface(this);
		colorinterface.initInterface();
	}

	// Grab a frame
	private void GrabFrame() {
		try {
			grabbedImage = converter.convert(grabber.grab());
			if (grabbedImage != null) {
				if (annotatedImage == null)
					annotatedImage = cvCreateImage(cvGetSize(grabbedImage), 8, 1);
				else
					annotatedImage.release();
				annotatedImage = grabbedImage.clone();
				getThresholdImage(grabbedImage);
			}
		} catch (Exception e) {
			System.out.println("Failed to grab frame.");
		}
	}

	private void trackAndAnnotate() {
		CvMoments moments = new CvMoments();
		cvMoments(thresholdedImage, moments, 1);
		double mom10 = cvGetSpatialMoment(moments, 1, 0);
		double mom01 = cvGetSpatialMoment(moments, 0, 1);
		double area = cvGetCentralMoment(moments, 0, 0);
		int trackedPosX = (int) (mom10 / area);
		int trackedPosY = (int) (mom01 / area);
		// only if its a valid position
		if (trackedPosX > 0 && trackedPosY > 0) {
			cvCircle(annotatedImage, new int[] { trackedPosX, trackedPosY }, 5, new CvScalar(255, 0, 0, 0));
		}
		drawBombs(annotatedImage);
	}

	private void Update(double time) {
		spawnTimer += time;
		if(spawnTimer >= nextSpawn){
			SpawnBomb();
			spawnTimer = 0;
			nextSpawn = spawnIntervalMin + Math.random()*(spawnIntervalMax - spawnIntervalMin);
		}
		
		for (int i = 0; i < bombs.size(); i++) {
			bombs.get(i).Update(time);
			if (bombs.get(i).hasExploded()) {
				System.out.println("BOOM!");
				//TODO: Tell the API that the game is over
				//gameState = GameState.GameOver;
				bombs.remove(i);
				i--;
			}
		}
	}

	private void drawBombs(IplImage imgAnnotated) {

		// This overlays a bomb, but is horrendously slow - find a better way
		// for(int i = 0; i < bombImage.width(); i++)
		// for(int j = 0; j < bombImage.height(); j++){
		// CvScalar c1 = org.bytedeco.javacpp.opencv_core.cvGet2D(imgAnnotated,
		// j, i);
		// CvScalar c2 = org.bytedeco.javacpp.opencv_core.cvGet2D(bombImage, j,
		// i);
		//
		// CvScalar m = new CvScalar();
		// for(int k = 0; k < 4; k++)
		// {
		// if(c2.getVal(k) == 0)
		// m.setVal(k, c1.getVal(k));
		// else
		// m.setVal(k, 0*c1.getVal(k) + 1*c2.getVal(k));
		//
		// }
		//
		//
		// org.bytedeco.javacpp.opencv_core.cvSet2D(imgAnnotated, j, i, m);
		// }

		for (int i = 0; i < bombs.size(); i++) {
			// for each bomb:
			// if it is within the view angle,
			// calculate position in image and draw a dot
			if (Math.abs(Utility.angleDifference(bombs.get(i).getBearing(),
					cameraController.getPan())) < CameraController.VIEW_ANGLE / 2.0f) {

				float diff = Utility.angleDifference(bombs.get(i).getBearing(), cameraController.getPan());
				// System.out.println("diff = " + diff);
				int w = imgAnnotated.width();
				// System.out.println("w = " + w);
				int xPos = w / 2 + (int) (diff / CameraController.VIEW_ANGLE * w);
				// System.out.println("xPos = " + xPos);

				cvCircle(imgAnnotated, new int[] { xPos, imgAnnotated.height() / 2 }, 10,
						new CvScalar(255, 255, 255, 0));

			}

		}
	}

	public ArrayList<Bomb> getBombs() {
		return bombs;
	}

	private void getThresholdImage(IplImage orgImg) {

		if (thresholdedImage == null)
			thresholdedImage = cvCreateImage(cvGetSize(orgImg), 8, 1);
		else
			thresholdedImage.release();
		IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 1);
		imgHSV = orgImg.clone();

		// Change image color format from BGR to HSV
		cvCvtColor(orgImg, imgHSV, org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV);
		// Threshold the image according to the calibrated values
		cvInRangeS(imgHSV, rgba_min, rgba_max, thresholdedImage);
		// Smooth out noise
		cvSmooth(thresholdedImage, thresholdedImage, CV_MEDIAN, 15, 0, 0, 0);

		imgHSV.release();
		// return thresholdedImage;
	}

}