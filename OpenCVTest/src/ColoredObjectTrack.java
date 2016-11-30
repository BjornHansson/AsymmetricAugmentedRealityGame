
/*
 * Just an example using the opencv to make a colored object tracking,
 * i adpted this code to bytedeco/javacv, i think this will help some people.
 *
 * Waldemar <waldemarnt@outlook.com>
 */

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class ColoredObjectTrack implements Runnable {

	public static void main(String[] args) {
		ColoredObjectTrack cot = new ColoredObjectTrack();
		Thread th = new Thread(cot);
		ColoredObjectTrackcontrolinterface();
		th.start();
	}

	// final int INTERVAL = 10;// 1sec
	// final int CAMERA_NUM = 0; // Default camera for this time

	/**
	 * Correct the color range- it depends upon the object, camera quality,
	 * environment.
	 */
	static int a = 0, b = 0, c = 0, d = 100, e = 255, f = 255;

	static CvScalar rgba_min = cvScalar(0, 0, 0, 0);
	static CvScalar rgba_max = cvScalar(100, 255, 255, 0);

	IplImage image;
	static CanvasFrame canvas = new CanvasFrame("Original");
	static CanvasFrame thresholdedCanvas = new CanvasFrame("Thresholded");
	static CanvasFrame canvas2 = new CanvasFrame("Controller");

	int ii = 0;

	public static void ColoredObjectTrackcontrolinterface() {
		canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		canvas2.getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(6, 1, 0, 0));

		JLabel lblRedMinimum = new JLabel("Red minimum");
		panel.add(lblRedMinimum);

		JPanel panel_1 = new JPanel();
		canvas2.getContentPane().add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new GridLayout(6, 1, 0, 0));

		JLabel lblRedMaximum = new JLabel("Red maximum");
		panel.add(lblRedMaximum);

		JLabel lblGreenMinimum = new JLabel("Green minimum");
		panel.add(lblGreenMinimum);

		JLabel lblGreenMaximum = new JLabel("Green maximum");
		panel.add(lblGreenMaximum);

		JLabel lblBlueMinimum = new JLabel("Blue minimum");
		panel.add(lblBlueMinimum);

		JLabel lblBlueMaximum = new JLabel("Blue maximum");
		panel.add(lblBlueMaximum);

		final JSlider slider = new JSlider();
		slider.setMaximum(255);
		lblRedMinimum.setLabelFor(slider);
		panel_1.add(slider);
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				a = slider.getValue();
			}
		});

		final JSlider slider_1 = new JSlider();
		slider_1.setMaximum(255);
		lblRedMaximum.setLabelFor(slider_1);
		panel_1.add(slider_1);
		slider_1.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				d = slider_1.getValue();
				updatergbvalues();
			}
		});

		final JSlider slider_2 = new JSlider();
		slider_2.setMaximum(255);
		lblGreenMinimum.setLabelFor(slider_2);
		panel_1.add(slider_2);
		slider_2.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				b = slider_2.getValue();
				updatergbvalues();
			}
		});

		final JSlider slider_3 = new JSlider();
		slider_3.setMaximum(255);
		lblGreenMaximum.setLabelFor(slider_3);
		panel_1.add(slider_3);
		slider_3.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				e = slider_3.getValue();
				updatergbvalues();
			}
		});

		final JSlider slider_4 = new JSlider();
		slider_4.setMaximum(255);
		lblBlueMinimum.setLabelFor(slider_4);
		panel_1.add(slider_4);
		slider_4.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				c = slider_4.getValue();
				updatergbvalues();
			}
		});

		final JSlider slider_5 = new JSlider();
		slider_5.setMaximum(255);
		slider_5.setBorder(new EmptyBorder(0, 4, 0, 0));
		lblBlueMaximum.setLabelFor(slider_5);
		panel_1.add(slider_5);
		slider_5.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				f = slider_5.getValue();
				updatergbvalues();
			}
		});

		thresholdedCanvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		canvas2.setVisible(true);
		canvas2.setPreferredSize(new Dimension(400, 300));
		canvas2.setMinimumSize(new Dimension(400, 300));
		canvas2.pack();
	}

	public static void updatergbvalues() {
		rgba_min = cvScalar(a, b, c, 0);
		rgba_max = cvScalar(d, e, f, 0);
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
			while (true) {
				img = converter.convert(grabber.grab());
				if (img != null) {
					// show image on window
					cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise
					canvas.showImage(converter.convert(img));
					IplImage detectThrs = getThresholdImage(img);
					thresholdedCanvas.showImage(converter.convert(detectThrs));

					// CvMoments moments = new CvMoments();
					// cvMoments(detectThrs, moments, 1);
					// double mom10 = cvGetSpatialMoment(moments, 1, 0);
					// double mom01 = cvGetSpatialMoment(moments, 0, 1);
					// double area = cvGetCentralMoment(moments, 0, 0);
					// posX = (int) (mom10 / area);
					// posY = (int) (mom01 / area);
					// // only if its a valid position
					// if (posX > 0 && posY > 0) {
					// paint(img, posX, posY);
					// }
				}
				// Thread.sleep(INTERVAL);
			}
		} catch (Exception e) {
		}
	}

	// private void paint(IplImage img, int posX, int posY) {
	// Graphics g = jp.getGraphics();
	// thresholdedCanvas.setSize(img.width(), img.height());
	// // g.clearRect(0, 0, img.width(), img.height());
	// g.setColor(Color.RED);
	// // g.fillOval(posX, posY, 20, 20);
	// g.drawOval(posX, posY, 20, 20);
	// System.out.println(posX + " , " + posY);
	//
	// }

	private IplImage getThresholdImage(IplImage orgImg) {
		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
		//
		cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red

		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
		// cvSaveImage(++ii + "dsmthreshold.jpg", imgThreshold);
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