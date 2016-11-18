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

package com.kolich.axisviewer;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
//import org.slf4j.Logger;

import com.kolich.axisviewer.camera.Camera;
import com.kolich.axisviewer.camera.CameraFrame;
import com.kolich.axisviewer.constants.Constants;

//import static org.slf4j.LoggerFactory.getLogger;

public class AxisViewer {

	// private static final Logger log = getLogger(AxisViewer.class);

	@Option(name = "--url", usage = "URL to camera stream.", required = true)
	private String _url = null;

	public static void main(String[] args) throws Exception {
		new AxisViewer().doMain(args);
	}

	private void doMain(final String[] args) throws Exception {
		final ParserProperties properties = ParserProperties.defaults().withUsageWidth(80);
		final CmdLineParser parser = new CmdLineParser(this, properties);
		try {
			parser.parseArgument(args);
			if (_url == null) {
				throw new CmdLineException(parser, "URL to camera stream required.", null);
			}
			// http://1.0.0.6:8085/axis-cgi/jpg/image.cgi?resolution=640x480
			// http://http://127.0.0.1/:8080/axis-cgi/jpg/image.cgi?resolution=640x480
			Camera axis = new Camera("Axis 2100 Network Camera", _url, "root", "pass", Constants.REFRESH_RATE);

			CameraFrame frame = new CameraFrame(axis);
			frame.setVisible(true);
			frame.run();
		} catch (Exception e) {
			System.err.println("Usage:");
			parser.printUsage(System.err);
		}
	}

}
