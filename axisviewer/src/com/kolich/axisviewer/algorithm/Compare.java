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

public class Compare {
	
	private int [] differences;
	private int [] cache;
	private int [] cache2;
	
	private int count;
	private int tolerance;
	private boolean equal;

	public Compare ( int [] cache, int [] cache2, int tolerance ) {
		this.count = 0;
		this.cache = cache;
		this.cache2 = cache2;
		this.tolerance = tolerance;
		this.differences = new int [ this.cache.length ];
	}
	
	public void run ( ) {
		
		for ( int c = 0; c < this.cache.length; c++ ) {
			if ( this.cache[c] != this.cache2[c] ) {
				this.count += 1;
				this.differences[c] = this.cache2[c];
			}
		}
		
		if ( this.count >= this.tolerance ) {
			this.equal = true;
		}
		else {
			this.equal = false;
		}
		
	} // end compare
	
	public boolean motion ( ) {
		return this.equal;
	}
	
	public int getDiffCount ( ) {
		return this.count;
	}
	
	public int [] getDiffArray ( ) {
		return this.differences;
	}
	
}
