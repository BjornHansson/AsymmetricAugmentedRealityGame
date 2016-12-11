import static org.bytedeco.javacpp.opencv_core.cvScalar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	ColoredObjectTrack cot;
	CameraControll aCameraControll;
		
	public void initInterface() {
		//control camera even tho color control interface is in focus
		canvas2.addKeyListener(aCameraControll);

		//get color values from file
		readColorValuesFromFile();
		canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		canvas2.getContentPane().add(panel, BorderLayout.WEST);
		
		panel.setLayout(new GridLayout(7, 1, 0, 0));

		JLabel lblRedMinimum = new JLabel("Red minimum");
		panel.add(lblRedMinimum);

		JPanel panel_1 = new JPanel();
		canvas2.getContentPane().add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new GridLayout(7, 1, 0, 0));

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
		slider.setValue(a);
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				a = slider.getValue();
				cot.updatergbvalues();
			}
		});

		final JSlider slider_1 = new JSlider();
		slider_1.setMaximum(255);
		lblRedMaximum.setLabelFor(slider_1);
		panel_1.add(slider_1);
		slider_1.setValue(d);
		slider_1.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				d = slider_1.getValue();
				cot.updatergbvalues();
			}
		});

		final JSlider slider_2 = new JSlider();
		slider_2.setMaximum(255);
		lblGreenMinimum.setLabelFor(slider_2);
		panel_1.add(slider_2);
		slider_2.setValue(b);
		slider_2.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				b = slider_2.getValue();
				cot.updatergbvalues();
			}
		});

		final JSlider slider_3 = new JSlider();
		slider_3.setMaximum(255);
		lblGreenMaximum.setLabelFor(slider_3);
		panel_1.add(slider_3);
		slider_3.setValue(e);
		slider_3.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent event) {
				// TODO Auto-generated method stub
				e = slider_3.getValue();
				cot.updatergbvalues();
			}
		});

		final JSlider slider_4 = new JSlider();
		slider_4.setMaximum(255);
		lblBlueMinimum.setLabelFor(slider_4);
		panel_1.add(slider_4);
		slider_4.setValue(c);
		slider_4.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				c = slider_4.getValue();
				cot.updatergbvalues();
			}
		});

		final JSlider slider_5 = new JSlider();
		slider_5.setMaximum(255);
		slider_5.setBorder(new EmptyBorder(0, 4, 0, 0));
		lblBlueMaximum.setLabelFor(slider_5);
		
		panel_1.add(slider_5);
		slider_5.setValue(f);
		slider_5.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				f = slider_5.getValue();
				cot.updatergbvalues();
			}
		});
		
		JButton btnSaveState = new JButton("Save state");
		btnSaveState.setToolTipText("Save the current slider value state");
		panel.add(btnSaveState);
		btnSaveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				writeColorValuesToFile();
			}
		});
		canvas2.setVisible(true);
		canvas2.setPreferredSize(new Dimension(400, 300));
		canvas2.setMinimumSize(new Dimension(400, 300));
		canvas2.pack();
	}
		
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
	
	private void writeColorValuesToFile() {
		JSONObject obj = new JSONObject();
		obj.put("a", a);
		obj.put("d", d);
		obj.put("b", b);
		obj.put("e", e);
		obj.put("c", c);
		obj.put("f", f);

//		
//		JSONArray colorValues = new JSONArray();
//		colorValues.add(a);
//		colorValues.add(d);
//		colorValues.add(b);
//		colorValues.add(e);
//		colorValues.add(c);
//		colorValues.add(f);
//		obj.put("colorValues", colorValues);
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


	
