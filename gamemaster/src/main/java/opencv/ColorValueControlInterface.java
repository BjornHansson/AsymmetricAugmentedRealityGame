package opencv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacv.CanvasFrame;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ColorValueControlInterface extends JFrame {
	private static final long serialVersionUID = -5460223539537059051L;
	private int a = 0, b = 0, c = 0, d = 100, e = 255, f = 255;
	private static CanvasFrame canvasFrame = new CanvasFrame("Controller");
	private ColoredObjectTrack coloredObjectTrack;
	private CameraController cameraController;

	public ColorValueControlInterface(ColoredObjectTrack cot) {
		this.coloredObjectTrack = cot;
	}

	/**
	 * Starts up the controller interface for changing color values that the
	 * camera identifies.
	 */
	public void initInterface() {
		// control camera even if color control interface is in focus
		canvasFrame.addKeyListener(cameraController);

		// get color values from file
		readColorValuesFromFile();

		// setup panel
		canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		canvasFrame.setIconImage(new ImageIcon("bomb_small.png").getImage());

		// TWO PANELS, "panelWest" is the left side and "panelEast" is the right
		// side
		JPanel panelWest = new JPanel();
		canvasFrame.getContentPane().add(panelWest, BorderLayout.WEST);
		JPanel panelEast = new JPanel();
		canvasFrame.getContentPane().add(panelEast, BorderLayout.EAST);
		panelWest.setBorder(new EmptyBorder(15, 15, 15, 15));
		panelEast.setBorder(new EmptyBorder(15, 15, 15, 15));
		canvasFrame.getContentPane().setBackground(Color.RED);
		panelWest.setBackground(Color.RED);
		panelEast.setBackground(Color.RED);

		// set amount of rows and columns in the layout
		panelEast.setLayout(new GridLayout(7, 1, 0, 0));
		panelWest.setLayout(new GridLayout(7, 1, 0, 0));

		// WEST SIDE
		// Labels
		JLabel lblRedMinimum = new JLabel("Hue minimum");
		panelWest.add(lblRedMinimum);
		JLabel lblRedMaximum = new JLabel("Hue maximum");
		panelWest.add(lblRedMaximum);
		JLabel lblGreenMinimum = new JLabel("Sat minimum");
		panelWest.add(lblGreenMinimum);
		JLabel lblGreenMaximum = new JLabel("Sat maximum");
		panelWest.add(lblGreenMaximum);
		JLabel lblBlueMinimum = new JLabel("Val minimum");
		panelWest.add(lblBlueMinimum);
		JLabel lblBlueMaximum = new JLabel("Val maximum");
		panelWest.add(lblBlueMaximum);

		lblBlueMaximum.setForeground(Color.WHITE);
		lblBlueMinimum.setForeground(Color.WHITE);
		lblGreenMaximum.setForeground(Color.WHITE);
		lblGreenMinimum.setForeground(Color.WHITE);
		lblRedMaximum.setForeground(Color.WHITE);
		lblRedMinimum.setForeground(Color.WHITE);

		JButton btnSaveState = new JButton("Save state");
		btnSaveState.setToolTipText("Save the current slider value state");
		panelWest.add(btnSaveState);
		btnSaveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				writeColorValuesToFile();
			}
		});
		btnSaveState.setBackground(Color.WHITE);

		// EAST SIDE
		// Sliders
		final JSlider slider = new JSlider();
		slider.setMaximum(255);
		lblRedMinimum.setLabelFor(slider);
		panelEast.add(slider);
		slider.setValue(a);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				a = slider.getValue();
				coloredObjectTrack.updatergbvalues(a, b, c, d, e, f);
			}
		});
		slider.setBackground(Color.RED);
		slider.setForeground(Color.WHITE);

		final JSlider slider1 = new JSlider();
		slider1.setMaximum(255);
		lblRedMaximum.setLabelFor(slider1);
		panelEast.add(slider1);
		slider1.setValue(d);
		slider1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				d = slider1.getValue();
				coloredObjectTrack.updatergbvalues(a, b, c, d, e, f);
			}
		});
		slider1.setBackground(Color.RED);

		final JSlider slider2 = new JSlider();
		slider2.setMaximum(255);
		lblGreenMinimum.setLabelFor(slider2);
		panelEast.add(slider2);
		slider2.setValue(b);
		slider2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				b = slider2.getValue();
				coloredObjectTrack.updatergbvalues(a, b, c, d, e, f);
			}
		});
		slider2.setBackground(Color.RED);

		final JSlider slider3 = new JSlider();
		slider3.setMaximum(255);
		lblGreenMaximum.setLabelFor(slider3);
		panelEast.add(slider3);
		slider3.setValue(e);
		slider3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				e = slider3.getValue();
				coloredObjectTrack.updatergbvalues(a, b, c, d, e, f);
			}
		});
		slider3.setBackground(Color.RED);

		final JSlider slider4 = new JSlider();
		slider4.setMaximum(255);
		lblBlueMinimum.setLabelFor(slider4);
		panelEast.add(slider4);
		slider4.setValue(c);
		slider4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				c = slider4.getValue();
				coloredObjectTrack.updatergbvalues(a, b, c, d, e, f);
			}
		});
		slider4.setBackground(Color.RED);

		final JSlider slider5 = new JSlider();
		slider5.setMaximum(255);
		slider5.setBorder(new EmptyBorder(0, 4, 0, 0));
		lblBlueMaximum.setLabelFor(slider5);

		panelEast.add(slider5);
		slider5.setValue(f);
		slider5.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				f = slider5.getValue();
				coloredObjectTrack.updatergbvalues(a, b, c, d, e, f);
			}
		});
		slider5.setBackground(Color.RED);

		JButton btnPlay = new JButton("Play");
		btnPlay.setToolTipText("Finalise calibration and play game");
		panelEast.add(btnPlay);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				coloredObjectTrack.play();
			}
		});
		btnPlay.setBackground(Color.WHITE);

		// setup panel
		canvasFrame.setVisible(true);
		canvasFrame.setPreferredSize(new Dimension(400, 300));
		canvasFrame.setMinimumSize(new Dimension(400, 300));
		canvasFrame.pack();
	}

	public void hide() {
		canvasFrame.setVisible(false);
	}

	/**
	 * Retrieve json data from file with previously saved slider state and set
	 * the values to the starting point of the sliders
	 */
	public void readColorValuesFromFile() {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("savedColorValueStates.json"));

			JSONObject jsonObject = (JSONObject) obj;

			a = Integer.parseInt(jsonObject.get("a").toString());
			b = Integer.parseInt(jsonObject.get("b").toString());
			c = Integer.parseInt(jsonObject.get("c").toString());
			d = Integer.parseInt(jsonObject.get("d").toString());
			e = Integer.parseInt(jsonObject.get("e").toString());
			f = Integer.parseInt(jsonObject.get("f").toString());
			coloredObjectTrack.updatergbvaluesFromFile(a, b, c, d, e, f);

			System.out.println("rbga successfully set");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("No file to read from");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert slider values into json and save state to file
	 */
	private void writeColorValuesToFile() {
		JSONObject obj = new JSONObject();
		obj.put("a", a);
		obj.put("d", d);
		obj.put("b", b);
		obj.put("e", e);
		obj.put("c", c);
		obj.put("f", f);
		// set file to use or create a new one if no file exists
		try {
			File file = new File("savedColorValueStates.json");
			if (file.createNewFile()) {
				System.out.println("File named: " + file + " has been created");
			}
			;
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Problem with finding or creating file");
		}
		// write json object to file
		FileWriter writeFile = null;
		try {
			writeFile = new FileWriter("savedColorValueStates.json");
			writeFile.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			try {
				writeFile.flush();
				writeFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
