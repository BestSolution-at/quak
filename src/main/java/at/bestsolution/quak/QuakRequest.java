/*
 * ----------------------------------------------------------------
 * Original File Name: QuakRequest.java
 * Creation Date:      25.04.2022
 * Description:  Represents a quak request.
 * ----------------------------------------------------------------

 * ----------------------------------------------------------------
 * Copyright (c) 2022 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved .
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software must not be used, redistributed or based from in
 * any other than the designated way without prior explicit written
 * permission by BestSolution.at.
 * -----------------------------------------------------------------
 */

package at.bestsolution.quak;

/**
 * Represents a quak request.
 * 
 * @author kerim.yeniduenya@bestsolution.com
 */
public class QuakRequest {
	
	private String path;
	private String username;
	private String password;
	private boolean isWrite;
	
	/**
	 * Constructs a quak request instance.
	 * @param path of request.
	 * @param username of requester.
	 * @param password of requester.
	 * @param isWrite true if is a write operation, false if read.
	 */
	public QuakRequest(String path, String username, String password, boolean isWrite) {
		setPath( path );
		setUsername( username );
		setPassword( password );
		setWrite( isWrite );
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath( String path ) {
		this.path = path;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername( String username ) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public boolean isWrite() {
		return isWrite;
	}

	public void setWrite( boolean isWrite ) {
		this.isWrite = isWrite;
	}
}