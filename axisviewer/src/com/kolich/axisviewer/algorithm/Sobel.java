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

package com.kolich.axisviewer.algorithm;

public class Sobel {

	// The width and height of the output images.
	private int output_width;
	private int output_height;

	// Location of the destination pixels.
	private int [ ] destination;
	private int [ ] source;
	
	private int height;
	private int width;
	private double sobscale;
	private float offsetval;
	private int tolerance;
  
	/**
	 * Create a new Sobel object.
	 *
	 */
	public Sobel ( int [] source,
					int height,
					int width,
					double sobscale,
					float offsetval,
					int tolerance ) {
		
		this.source = source;
		this.height = height;
		this.width = width;
		this.sobscale = sobscale;
		this.offsetval = offsetval;
		this.tolerance = tolerance;
		
	}
	
	public Sobel ( ) {
		this.source = null;
		this.height = 0;
		this.width = 0;
		this.sobscale = 0;
		this.offsetval = 0;
		this.tolerance = 0;
	}
	
	/**
	 * Call the private static method sobel() to
	 * get the real work done.
	 * @return
	 */
	public int [] run ( ) {
		
		this.destination = this.sobel( this.source,
										this.width,
										this.height,
										this.sobscale,
										this.offsetval,
										this.tolerance
										);
		
		return this.destination;
		
	} // end run
	
	public int [] getDestination ( ) {
		return this.destination;
	}
  
	/**
	 * Applies the Sobel Edge Detection algorithm to the source pixel array.
	 * @param source
	 * @param width
	 * @param height
	 * @param sobscale
	 * @param offsetval
	 * @param tolerance
	 * @return
	 */
	private int [ ] sobel ( int [ ] source,
								int width,
								int height,
								double sobscale,
								float offsetval,
								int tolerance ) {
	    
	    output_width = width;
	    output_height = height;
	    this.destination = new int[ output_width * output_height ];
	    
	    // Run through the entire pixel array, top to bottom.
	    for ( int i = 0; i < source.length; i++ ) {
	    	
	    	try {
	    		
	    		/*
	    		 * The RGB data for pixel (i, j) where (i, j) is inside the
	    		 * rectangle (x, y, w, h) is stored in the array at
	    		 * source[(j - y) * scansize + (i - x) + off].
	    		 */			
				int a = source[ i ] & 0x000000ff;
				int b = source[ i + 1 ] & 0x000000ff;
				int c = source[ i + 2 ] & 0x000000ff;
				int d = source[ i + width ] & 0x000000ff;
				
				int e = source[ i + width + 2 ] & 0x000000ff;
				int f = source[ i + 2 * width ] & 0x000000ff;
				int g = source[ i + 2 * width + 1 ] & 0x000000ff;
				int h = source[ i + 2 * width + 2 ] & 0x000000ff;
				
				// Extract the horizontal and vertical planes from the
				// image data.
				int hor = Math.abs( ( a + d + f ) - ( c + e + h ) );
				int vert = Math.abs( ( a + b + c ) - ( f + g + h ) );
				
				// Compute the normalized pixel value based on the
				// HOR AND VER plane.
				short gc = (short)( sobscale * ( hor + vert ) );
				gc = (short)( gc + offsetval );
				
				/*
				 * If the resulting sum is greater than 255,
				 * we must ensure that it says only at 255.
				 */
				if ( gc > 255 ) {
					gc = 255;
				}
				
				// Adjust for tolerance...
				if ( gc > tolerance ) {
					gc = 255;
				}
				else {
					gc = 0;
				}
				
				// Mask off alpha and recombine the shorts into a recgonizable
				// 32-bit value.  Shift 16-bits for RED, shift 8-bits for GREEN,
				// and shift none for BLUE.
				this.destination[ i ] = 0xff000000 | gc << 16 | gc << 8 | gc;
				
				// Reached borders of image so goto next row...
				if ( ( ( i + 3 ) % width ) == 0 )  {
				    	this.destination[i] = 0;
				    	this.destination[i+1] = 0;
				    	this.destination[i+2] = 0;
				    	i += 3;
				} // end if
	        
	    	}
	    	catch ( ArrayIndexOutOfBoundsException e ) {
	    		
	    		// If reached row boudary of image, return.
	    		i = source.length;
			
	    	} // end catch
	      
	    } // end for
	    
	    return this.destination;
	    
	  } // end public int [ ] sobel


} // end public class Sobel

