
/*
 * Just an example using the opencv to make a colored object tracking,
 * i adpted this code to bytedeco/javacv, i think this will help some people.
 *
 * Waldemar <waldemarnt@outlook.com>
 */

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetCentralMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Random;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;


public class ColoredObjectTrack implements Runnable {

	private ArrayList<Bomb> bombs = new ArrayList<Bomb>();
	private Random random = new Random();
	private CameraControll aCameraControll;

	public static void main(String[] args) {
		ColoredObjectTrack cot = new ColoredObjectTrack();
		Thread th = new Thread(cot);
		th.start();
		cot.setupInterface();

		ColorValueControlInterface colorinterface = new ColorValueControlInterface();
		colorinterface.initInterface();
		
	}

	// final int INTERVAL = 10;// 1sec
	// final int CAMERA_NUM = 0; // Default camera for this time



	/**
	 * Correct the color range- it depends upon the object, camera quality,
	 * environment.
	 */
	
	int a = 0, b = 0, c = 0, d = 100, e = 255, f = 255;

	CvScalar rgba_min = cvScalar(0, 0, 0, 0);
	CvScalar rgba_max = cvScalar(100, 255, 255, 0);

	IplImage image;

	// static TransparentPanel trackedPosition = new TransparentPanel();
	static CanvasFrame canvas = new CanvasFrame("Original");
	static CanvasFrame thresholdedCanvas = new CanvasFrame("Thresholded");
	
	public void SpawnBomb() {
		bombs.add(new Bomb(-180 + random.nextFloat() * 360, 20));
		// bombs.add(new Bomb(20.3f, 20));
		System.out.println("Bomb spawned at " + bombs.get(bombs.size() - 1).getBearing());
	}

	public ColoredObjectTrack() {

		SpawnBomb();
		SpawnBomb();
		SpawnBomb();
		SpawnBomb();
		SpawnBomb();

		// trackedPosition.setSize(500, 500);
		//
		//
		// trackedPosition.setBackground(new Color(255,255,255,0));
		// trackedPosition.setOpaque(false);
		// trackedPosition.setLayout( null ) ;

		// canvas.add(trackedPosition);

		// canvas.setGlassPane(trackedPosition);
		// canvas.image
		// canvas.setComponentZOrder(trackedPosition, 0);

	}

	int ii = 0;
	

	public void setupInterface() {
		aCameraControll = new CameraControll();
		canvas.addKeyListener(aCameraControll);
		thresholdedCanvas.addKeyListener(aCameraControll);
		thresholdedCanvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}
	
	public void updatergbvalues() {
		rgba_min = cvScalar(a, b, c, 0);
		rgba_max = cvScalar(d, e, f, 0);
	}
	
	public void updatergbvaluesFromFile(int a2, int b2, int c2, int d2, int e2, int f2) {
		this.a = a2;
		this.b = b2;
		this.c = c2;
		this.d = d2;
		this.e = e2;
		this.f = f2;
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
			FrameGrabber grabber = new FFmpegFrameGrabber(
					"http://root:pass@192.168.20.253/axis-cgi/mjpg/video.cgi?resolution=640x480&fps=25");
			// FrameGrabber grabber = FrameGrabber.createDefault(0);
			OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
			grabber.start();
			IplImage img;
			int posX = 0;
			int posY = 0;
			while (true) {
				img = converter.convert(grabber.grab());
				if (img != null) {
					// show image on window
					// cvFlip(img, img, 1);// l-r =
					// 90_degrees_steps_anti_clockwise

					// canvas.showImage(converter.convert(img));
					IplImage detectThrs = getThresholdImage(img);
					thresholdedCanvas.showImage(converter.convert(detectThrs));

					CvMoments moments = new CvMoments();
					cvMoments(detectThrs, moments, 1);
					double mom10 = cvGetSpatialMoment(moments, 1, 0);
					double mom01 = cvGetSpatialMoment(moments, 0, 1);
					double area = cvGetCentralMoment(moments, 0, 0);
					posX = (int) (mom10 / area);
					posY = (int) (mom01 / area);
					// only if its a valid position
					if (posX > 0 && posY > 0) {
						// paint(posX, posY);
						IplImage imgAnnotated = cvCreateImage(cvGetSize(img), 8, 1);
						imgAnnotated = img.clone();
						cvCircle(imgAnnotated, new int[] { posX, posY }, 5, new CvScalar(255, 0, 0, 0));

						System.out.println("Camera pan = " + aCameraControll.getPan());

						// DRAW SOME BOMBS!
						for (int i = 0; i < bombs.size(); i++) {
							// for each bomb:
							// if it is within the view angle,
							// calculate position in image and draw a dot
							if (Math.abs(angleDifference(bombs.get(i).getBearing(),
									aCameraControll.getPan())) < CameraControll.VIEW_ANGLE / 2.0f) {

								float diff = angleDifference(bombs.get(i).getBearing(), aCameraControll.getPan());
								// System.out.println("diff = " + diff);
								int w = imgAnnotated.width();
								// System.out.println("w = " + w);
								int xPos = w / 2 + (int) (diff / CameraControll.VIEW_ANGLE * w);
								// System.out.println("xPos = " + xPos);

								// int xPos = -(int)((aCameraControll.getPan() -
								// bombs.get(i).getBearing())/
								// (CameraControll.VIEW_ANGLE / 2.0f) *
								// imgAnnotated.width());
								cvCircle(imgAnnotated, new int[] { xPos, imgAnnotated.height() / 2 }, 10,
										new CvScalar(255, 255, 255, 0));

								// TODO: Base radius on position!
							}

						}

						canvas.showImage(converter.convert(imgAnnotated));
						imgAnnotated.release();
					}
					// img.release();
					detectThrs.release();
				}

				// Thread.sleep(INTERVAL);
			}
		} catch (Exception e) {
		}
	}

	private void paint(int posX, int posY) {

		// trackedPosition.x = posX;
		// trackedPosition.y = posY;
		// trackedPosition.repaint();

		// Graphics g = trackedPosition.getGraphics();
		//
		// g.clearRect(0, 0, 500, 500);
		// g.setColor(Color.RED);
		// // g.fillOval(posX, posY, 20, 20);
		// g.drawOval(posX, posY, 20, 20);

		// System.out.println(posX + " , " + posY);

	}

	private IplImage getThresholdImage(IplImage orgImg) {

		// IplImage imgThreshold = orgImg.clone();
		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

		IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 1);
		// imgHSV = cvCreateImage(orgImg);
		imgHSV = orgImg.clone();

		// COLOR_BGR2HSV

		cvCvtColor(orgImg, imgHSV, org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV);
		// org.bytedeco.javacpp.opencv_imgproc.cvtColor(orgImg.asCvMat(), dst,
		// code);

		// cvtColor(orgImg, imgHSV,);
		//
		cvInRangeS(imgHSV, rgba_min, rgba_max, imgThreshold);// red

		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
		// cvSaveImage(++ii + "dsmthreshold.jpg", imgThreshold);

		// org.bytedeco.javacpp.opencv_imgproc.cvcreatestr
		// org.bytedeco.javacpp.opencv_imgproc.cvErode(imgThreshold,
		// imgThreshold,
		// org.bytedeco.javacpp.opencv_imgproc.getStructuringElement(org.bytedeco.javacpp.opencv_imgproc.MORPH_ELLIPSE,
		// new org.bytedeco.javacpp.opencv_core.Size(5,5)));

		imgHSV.release();
		return imgThreshold;
	}

	public IplImage Equalize(BufferedImage bufferedimg) {
		Java2DFrameConverter converter1 = new Java2DFrameConverter();
		OpenCVFrameConverter.ToIplImage converter2 = new OpenCVFrameConverter.ToIplImage();
		IplImage iploriginal = converter2.convert(converter1.convert(bufferedimg));
		IplImage srcimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
		IplImage destimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
		cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
		cvEqualizeHist(srcimg, destimg);
		return destimg;
	}

	
}