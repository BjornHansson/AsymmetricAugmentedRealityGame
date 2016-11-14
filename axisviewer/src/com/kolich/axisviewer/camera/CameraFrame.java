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

import com.kolich.axisviewer.constants.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class CameraFrame extends JFrame implements Runnable,
															ChangeListener,
															ActionListener,
															ItemListener {
   
	private static final long serialVersionUID = 3325123392308489256L;
	
	private Camera myCam; // The actual camera object.
    private boolean refreshThreadState = true; // Should the camera refresh itself?
    private boolean pausedThreadState = false; // Is the camera paused?
    
    // The following components are GUI-level objects used in the
    // actual application GUI.
    private JSlider refreshSlider = null;
    private JButton pauseButton = null;
       
    /**
     * Create a new CameraFrame object.
     * @param camera
     */
    public CameraFrame ( Camera camera ) {
    	
    	super( camera.getCameraName() );
        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.setSize( 680, 480 ); 
        
        // Assign the camera object to myCam.
        this.myCam = camera;
        
        // Create the pause button and add an action listener
        // to it.
        this.pauseButton = new JButton("Pause");
        this.pauseButton.setActionCommand("pause");
		this.pauseButton.addActionListener( this );
        
		// Create the refresh rate JSlider.
        this.refreshSlider = new JSlider ( 
        			JSlider.HORIZONTAL,
					(int)Constants.MIN_REFRESH_RATE,
					(int)Constants.MAX_REFRESH_RATE,
					(int)this.myCam.getRefreshRate( ) );
        
        // Adjust the ticks on the slider, and add an action
        // listner to the component.
        this.refreshSlider.addChangeListener( this );
        this.refreshSlider.setMajorTickSpacing( 1000 );
        this.refreshSlider.setMinorTickSpacing( 500 );
        this.refreshSlider.setPaintTicks( true );
        this.refreshSlider.setPaintLabels( true );
        
        // Create the edge-detectors combo box.
        //this.edgeDetectorsComboBox = new JComboBox( this.edgeDetectorIDs );
        //this.edgeDetectorsComboBox.addActionListener( this );
        
        // Create the enable shape detection check box.
        //this.showVectorsCheckBox = new JCheckBox( "Show Extracted Vectors" );
        //this.showVectorsCheckBox.setSelected( false );
        //this.showVectorsCheckBox.addItemListener( this );        
        
        /*
         * The following block of code builds and attaches all GUI components to
         * the CameraFrame.
         */     
        JPanel content = new JPanel( );
		content.setLayout( new BoxLayout( content, BoxLayout.PAGE_AXIS ) );
		content.add( this.myCam );
		content.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) );

		/*
		this.mouseCoordinates = new JTextField( "Mouse Coordinates:  (0,0)" );
		this.mouseCoordinates.setPreferredSize( new Dimension( 20, 20 ) );
		this.mouseCoordinates.setEditable( false );
		this.mouseCoordinates.setHorizontalAlignment( JTextField.RIGHT );
		this.mouseCoordinates.setFont( new Font( "Arial", Font.BOLD, 12 ) );
		this.mouseCoordinates.setBackground( new Color( 200, 200, 200 ) );
		*/
		
		JPanel controlPane = new JPanel( );
		controlPane.add( this.refreshSlider );
		controlPane.add( Box.createRigidArea( new Dimension( 50, 0 ) ) );
		controlPane.add( this.pauseButton );
		controlPane.add( Box.createRigidArea( new Dimension( 100, 0 ) ) );
		
		controlPane.setLayout( new BoxLayout( controlPane, BoxLayout.LINE_AXIS ) );
		controlPane.setBorder( BorderFactory.createEmptyBorder( 5, 0, 5, 0 ) );
		controlPane.add( Box.createHorizontalGlue( ) );
		
		//this.getContentPane( ).add( this.mouseCoordinates, BorderLayout.PAGE_START );
		this.getContentPane( ).add( content, BorderLayout.CENTER );
		this.getContentPane( ).add( controlPane, BorderLayout.PAGE_END );
		
		// Set the size of the window.
        this.setSize( 640, 565 );

        // Set the window's location.
        this.setLocation( 100, 100 );

        // For the (x,y) coordinates which are displayed in the Camera Window.
        // Add the action listener to the frame.
		//this.addMouseListener( this );
        //this.addMouseMotionListener( this );
        
    } // end public CameraFrame ( Camera camera )
    
    
    /**
     * Called when the CameraFrame object is asked to run itself as a thread.
     */
    public void run ( ) {
    	
    	// The camera is connected and refreshing, so update the
    	// is refreshing field.
    	this.refreshThreadState = true;
    	
    	// Keep looping as long as the refresh thread is true.
    	while ( this.refreshThreadState ) {    		
    		
            try {
            	// Sleep as long as needed...
                Thread.sleep( this.myCam.getRefreshRate( ) );
            }
            catch ( InterruptedException i ) {            	
            	// do nothing            	
            }
            
            // If the camera is not paused, then alert the camera object to refresh
            // itself.
            if ( !this.pausedThreadState ) {
                this.myCam.refresh( );            	
            } // end if
    
    	} // end while
       
    	// To safely end this thread, we must use return instead of
    	// thread.stop( ) which is deprecated.
    	return;
            
    } // end public void run ( )
    

    /**
     * Stops the current camera thread.
     *
     */
	public void stop ( ) {
		this.refreshThreadState = false;
	}
	
	
	/**
	 * Pauses the current camera thread.
	 *
	 */
	public void pause ( ) {
		this.pausedThreadState = true;
		this.myCam.setPausedState( true );
		this.myCam.repaint( );
	}
	
	
	/**
	 * Resumes the camera thread from pause.
	 *
	 */
	public void resume ( ) {
		this.pausedThreadState = false;
		this.myCam.setPausedState( false );
		this.myCam.repaint( );
	}
	
	
	/**
	 * On occasion, it's necessary to grab the camera object from
	 * the Camera frame.
	 * @return
	 */
	public Camera getCamera ( ) {
		return this.myCam;
	}

	
	/**
	 * Returns true of the camera frame is open.  False if it is closed.
	 * @return
	 */
	public boolean isOpen ( ) {
		return this.isVisible( );
	}

	
	/**
	 * Handles all state changes of the refresh-rate slider.
	 */
	public void stateChanged ( ChangeEvent e ) {
		
		// Get the event source, and convert it to a JSlider
		// object.
		JSlider source = (JSlider)e.getSource( );
		
		if ( !source.getValueIsAdjusting( ) ) {
			// Set the new refresh rate.
			this.myCam.setRefreshRate( (long)source.getValue( ) );
	    } // end if
		
	} // end public void stateChanged
	
	
	/**
	 * Handles all actions trigged by various buttons and other GUI components with
	 * the camera frame.
	 */
	public void actionPerformed ( ActionEvent e ) {		
		
		// If the pause button was clicked.
		if ( "pause".equals( e.getActionCommand ( ) ) ) {
			
			// If it's not already paused, pause it and disable the
			// refresh rate slider.
			if ( !this.pausedThreadState ) {
				this.pause( );
				this.refreshSlider.setEnabled( false );
				this.pauseButton.setText("  Play  ");
			}
			else {
				// Resume the camera stream.
				this.resume( );
				this.refreshSlider.setEnabled( true );
				this.pauseButton.setText("Pause");
			} // end else
						           
		} // end if pause
		else if ( "comboBoxChanged".equals( e.getActionCommand ( ) ) ) {
			
			//JComboBox cb = (JComboBox)e.getSource( );
	        //String detectorName = (String)cb.getSelectedItem( );
	        
	        // do nothing
			
		} // end if comboBoxChanged		
		
	} // end public void actionPerformed
	
	
	
	/**
	 * Handle the action event of the user clicking the 'enable shape
	 * detection' checkbox.
	 */
	public void itemStateChanged ( ItemEvent e ) {
		
		// Get the action source.
		//Object source = e.getItemSelectable( );
		
		/*
		// Check to see if the source was the actual shape
		// detection checkbox.
		if ( source == this.showVectorsCheckBox ) {
			
			if ( e.getStateChange( ) == ItemEvent.DESELECTED ) {
				// do nothing
			}
			else {
				// do nothing
			}
			
		} // end if
		 */
			
	} // end public void itemStateChanged

} // end class CameraFrame

