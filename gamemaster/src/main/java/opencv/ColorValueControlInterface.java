package opencv;
import static org.bytedeco.javacpp.opencv_core.cvScalar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
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

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacv.CanvasFrame;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ColorValueControlInterface extends JFrame {
	int a = 0, b = 0, c = 0, d = 100, e = 255, f = 255;
	static CanvasFrame canvas2 = new CanvasFrame("Controller");
	CoreGame cot;
	CameraController aCameraControll;
	
	public ColorValueControlInterface( CoreGame cot ) {
		this.cot = cot;
	}
		
	/*
	 * Starts up the controller interface for changing color values that the camera identifies.
	 */
	public void initInterface() {
		//control camera even tho color control interface is in focus
		canvas2.addKeyListener(aCameraControll);

		//get color values from file
		readColorValuesFromFile();
		
		//setup panel
		canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		canvas2.setIconImage(new ImageIcon("bomb_small.png").getImage());
		
		//TWO PANELS, "panelWest" is the left side and "panelEast" is the right side
		JPanel panelWest = new JPanel();
		canvas2.getContentPane().add(panelWest, BorderLayout.WEST);
		JPanel panelEast = new JPanel();
		canvas2.getContentPane().add(panelEast, BorderLayout.EAST);
		panelWest.setBorder(new EmptyBorder(15, 15, 15, 15));
		panelEast.setBorder(new EmptyBorder(15, 15, 15, 15));
		canvas2.getContentPane().setBackground(Color.RED);
		panelWest.setBackground(Color.RED);
		panelEast.setBackground(Color.RED);
		
		// set amount of rows and columns in the layout
		panelEast.setLayout(new GridLayout(7, 1, 0, 0));
		panelWest.setLayout(new GridLayout(7, 1, 0, 0));
		
		// WEST SIDE
		//Labels
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
		
		//EAST SIDE
		//Sliders
		final JSlider slider = new JSlider();
		slider.setMaximum(255);
		lblRedMinimum.setLabelFor(slider);
		panelEast.add(slider);
		slider.setValue(a);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				a = slider.getValue();
				cot.updatergbvalues(a,b,c,d,e,f);
			}
		});
		slider.setBackground(Color.RED);
		slider.setForeground(Color.WHITE);

		final JSlider slider_1 = new JSlider();
		slider_1.setMaximum(255);
		lblRedMaximum.setLabelFor(slider_1);
		panelEast.add(slider_1);
		slider_1.setValue(d);
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				d = slider_1.getValue();
				cot.updatergbvalues(a,b,c,d,e,f);
			}
		});
		slider_1.setBackground(Color.RED);

		final JSlider slider_2 = new JSlider();
		slider_2.setMaximum(255);
		lblGreenMinimum.setLabelFor(slider_2);
		panelEast.add(slider_2);
		slider_2.setValue(b);
		slider_2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				b = slider_2.getValue();
				cot.updatergbvalues(a,b,c,d,e,f);
			}
		});
		slider_2.setBackground(Color.RED);
		
		final JSlider slider_3 = new JSlider();
		slider_3.setMaximum(255);
		lblGreenMaximum.setLabelFor(slider_3);
		panelEast.add(slider_3);
		slider_3.setValue(e);
		slider_3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				e = slider_3.getValue();
				cot.updatergbvalues(a,b,c,d,e,f);
			}
		});
		slider_3.setBackground(Color.RED);

		final JSlider slider_4 = new JSlider();
		slider_4.setMaximum(255);
		lblBlueMinimum.setLabelFor(slider_4);
		panelEast.add(slider_4);
		slider_4.setValue(c);
		slider_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				c = slider_4.getValue();
				cot.updatergbvalues(a,b,c,d,e,f);
			}
		});
		slider_4.setBackground(Color.RED);

		final JSlider slider_5 = new JSlider();
		slider_5.setMaximum(255);
		slider_5.setBorder(new EmptyBorder(0, 4, 0, 0));
		lblBlueMaximum.setLabelFor(slider_5);
		
		panelEast.add(slider_5);
		slider_5.setValue(f);
		slider_5.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				f = slider_5.getValue();
				cot.updatergbvalues(a,b,c,d,e,f);
			}
		});
		slider_5.setBackground(Color.RED);
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setToolTipText("Finalise calibration and play game");
		panelEast.add(btnPlay);
		btnPlay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				cot.play();
			}
		});
		btnPlay.setBackground(Color.WHITE);
			
		//setup panel
		canvas2.setVisible(true);
		canvas2.setPreferredSize(new Dimension(400, 300));
		canvas2.setMinimumSize(new Dimension(400, 300));
		canvas2.pack();
	}
	
	public void hide(){
		canvas2.setVisible(false);
	}
	
	public void show(){
		canvas2.setVisible(true);
	}
	
	/*
	 * Retrieve json data from file with previously saved slider state and set the values to the starting point of the sliders
	 */
	public void readColorValuesFromFile () {
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
	        System.out.println(b);
	        cot.updatergbvaluesFromFile(a, b, c, d, e, f);     
	        
			System.out.println("rbga successfully set");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("No file to read from");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
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
			};
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Problem with finding or creating file");
		}
		//write json object to file
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	}
}	


	
