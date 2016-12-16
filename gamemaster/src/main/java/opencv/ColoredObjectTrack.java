package opencv;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
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
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.joda.time.DateTime;

import comm.GamesHolder;
import comm.WebAPI;

public class ColoredObjectTrack implements Runnable {

	private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
	private Random random = new Random();
	private CameraController cameraController;
	private GamesHolder gamesHolder;
	private WebAPI webApi;
	private int bombIdCounter = 1;

	// int a = 0, b = 0, c = 0, d = 100, e = 255, f = 255;
	CvScalar rgba_min = cvScalar(0, 0, 0, 0);
	CvScalar rgba_max = cvScalar(100, 255, 255, 0);

	static CanvasFrame videoFrame = new CanvasFrame("Original");
	static CanvasFrame thresholdedVideoFrame = new CanvasFrame("Thresholded");

	public static void main(String[] args) {
		ColoredObjectTrack cot = new ColoredObjectTrack();
		Thread th = new Thread(cot);
		th.start();
		cot.setupInterface();
		ColorValueControlInterface colorinterface = new ColorValueControlInterface(cot);
		colorinterface.initInterface();

	}

	public void SpawnBomb() {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusSeconds(10 + random.nextInt(10));
		bombs.add(new Bomb(bombIdCounter, -180 + random.nextFloat() * 360, dateTime));
		gamesHolder.addBomb(bombIdCounter, dateTime);
		bombIdCounter++;
		// bombs.add(new Bomb(20.3f, 20));
		System.out.println("Bomb spawned at " + bombs.get(bombs.size() - 1).getBearing());
	}

	public boolean canDefuseBomb(int bombId) {
		return true;
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
		gamesHolder = new GamesHolder(this);
		webApi = new WebAPI(gamesHolder);
		SpawnBomb();
		SpawnBomb();
		SpawnBomb();
		SpawnBomb();
		SpawnBomb();
	}

	public void setupInterface() {
		cameraController = new CameraController(false);
		videoFrame.addKeyListener(cameraController);
		videoFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		thresholdedVideoFrame.addKeyListener(cameraController);
		thresholdedVideoFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}

	public void updatergbvalues(int a, int b, int c, int d, int e, int f) {
		rgba_min = cvScalar(a, b, c, 0);
		rgba_max = cvScalar(d, e, f, 0);
	}

	public void updatergbvaluesFromFile(int a, int b, int c, int d, int e, int f) {
		// this.a = a2;
		// this.b = b2;
		// this.c = c2;
		// this.d = d2;
		// this.e = e2;
		// this.f = f2;
		rgba_min = cvScalar(a, b, c, 0);
		rgba_max = cvScalar(d, e, f, 0);
	}

	private float angleDifference(float a, float b) {
		float diff = a - b;
		if (diff < -180)
			diff += 360;
		if (diff > 180)
			diff -= 360;
		return diff;
	}

	public void run() {
		try {
			// FrameGrabber grabber = new
			// FFmpegFrameGrabber("http://root:pass@192.168.20.253/axis-cgi/mjpg/video.cgi?resolution=640x480&fps=25");
			FrameGrabber grabber = FrameGrabber.createDefault(0);
			OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
			grabber.start();
			IplImage img;
			int posX = 0;
			int posY = 0;
			long lastLoopTime = System.nanoTime();
			final int TARGET_FPS = 60;
			final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
			boolean gameRunning = true;

			while (gameRunning) {
				long now = System.nanoTime();
				long updateLength = now - lastLoopTime;
				double updateLengthSeconds = (double) updateLength / 1000000000.0;
				lastLoopTime = now;

				// double delta = updateLength / ((double)OPTIMAL_TIME);

				img = converter.convert(grabber.grab());
				if (img != null) {

					IplImage detectThrs = getThresholdImage(img);
					thresholdedVideoFrame.showImage(converter.convert(detectThrs));

					CvMoments moments = new CvMoments();
					cvMoments(detectThrs, moments, 1);
					double mom10 = cvGetSpatialMoment(moments, 1, 0);
					double mom01 = cvGetSpatialMoment(moments, 0, 1);
					double area = cvGetCentralMoment(moments, 0, 0);
					posX = (int) (mom10 / area);
					posY = (int) (mom01 / area);
					// only if its a valid position
					if (posX > 0 && posY > 0) {
						IplImage imgAnnotated = cvCreateImage(cvGetSize(img), 8, 1);
						imgAnnotated = img.clone();
						cvCircle(imgAnnotated, new int[] { posX, posY }, 5, new CvScalar(255, 0, 0, 0));

						// System.out.println("Camera pan = " +
						// cameraController.getPan());

						// DRAW SOME BOMBS!
						drawBombs(imgAnnotated);

						// Show the annotated image
						videoFrame.showImage(converter.convert(imgAnnotated));
						imgAnnotated.release();
					}
					detectThrs.release();
				}

				Update(updateLengthSeconds);
				// Thread.sleep(INTERVAL);;
			}
		} catch (Exception e) {
		}
	}

	private void Update(double time) {
		for (int i = 0; i < bombs.size(); i++) {
			// bombs.get(i).Update(time);
			if (bombs.get(i).hasExploded()) {
				System.out.println("BOOM!");
				bombs.remove(i);
				i--;
			}
		}
	}

	private void drawBombs(IplImage imgAnnotated) {
		for (int i = 0; i < bombs.size(); i++) {
			// for each bomb:
			// if it is within the view angle,
			// calculate position in image and draw a dot
			if (Math.abs(
					angleDifference(bombs.get(i).getBearing(), cameraController.getPan())) < CameraController.VIEW_ANGLE
							/ 2.0f) {

				float diff = angleDifference(bombs.get(i).getBearing(), cameraController.getPan());
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

	private IplImage getThresholdImage(IplImage orgImg) {

		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
		IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 1);
		imgHSV = orgImg.clone();

		// Change image color format from BGR to HSV
		cvCvtColor(orgImg, imgHSV, org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV);
		// Threshold the image according to the calibrated values
		cvInRangeS(imgHSV, rgba_min, rgba_max, imgThreshold);
		// Smooth out noise
		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);

		imgHSV.release();
		return imgThreshold;
	}

	// Not sure what this was supposed to be for, but we aren't using it!
	// public IplImage Equalize(BufferedImage bufferedimg) {
	// Java2DFrameConverter converter1 = new Java2DFrameConverter();
	// OpenCVFrameConverter.ToIplImage converter2 = new
	// OpenCVFrameConverter.ToIplImage();
	// IplImage iploriginal =
	// converter2.convert(converter1.convert(bufferedimg));
	// IplImage srcimg = IplImage.create(iploriginal.width(),
	// iploriginal.height(), IPL_DEPTH_8U, 1);
	// IplImage destimg = IplImage.create(iploriginal.width(),
	// iploriginal.height(), IPL_DEPTH_8U, 1);
	// cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
	// cvEqualizeHist(srcimg, destimg);
	// return destimg;
	// }

}