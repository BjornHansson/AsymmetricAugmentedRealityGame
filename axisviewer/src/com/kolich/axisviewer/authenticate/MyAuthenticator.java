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

package com.kolich.axisviewer.authenticate;

public class MyAuthenticator extends java.net.Authenticator {
	
	private String username; // Username needed for authentication.
	private String password; // Password needed for authentication.
	
	/**
	 * Create a new MyAuthenticator object.
	 * @param username
	 * @param password
	 */
	public MyAuthenticator ( String username, String password ) {
		
		this.username = username;
		this.password = password;
		
	} // end public MyAuthenticator ( ... )
	
	
    /**
     * This method is called when a password protected URL is accessed.
     * The username and password is then automatically provided using
     * the HTTP protocol.
     */
    protected java.net.PasswordAuthentication getPasswordAuthentication ( ) {
    	
        // Get information about the HTTP authentication request.
        //String promptString = getRequestingPrompt( );
        //String hostname = getRequestingHost( );
        //java.net.InetAddress ipaddr = getRequestingSite( );
        //int port = getRequestingPort( );

        // Return the information.
        return new java.net.PasswordAuthentication( this.username, this.password.toCharArray( ) );
        
    } // end protected java.net.PasswordAuthentication getPasswordAuthentication ( )
    
    
    /**
     * Set the username.
     * @param username
     */
    public void setUsername ( String username ) {
    	this.username = username;
    }
    
    
    /**
     * Set the password.
     * @param password
     */
    public void setPassword ( String password ) {
    	this.password = password;
    }   
    
    
} // end public class MyAuthenticator
