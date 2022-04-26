/*
 * ----------------------------------------------------------------
 * Original File Name: QuakAuthorizationRequest.java
 * Creation Date:      25.04.2022
 * Description:  Represents a quak authorization request.
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
 * Represents a quak authorization request.
 * 
 * @author kerim.yeniduenya@bestsolution.com
 */
public class QuakAuthorizationRequest {
	
	private String urlPath;
	private String username;
	private String password;
	private boolean isWrite;
	
	/**
	 * Constructs a quak authorization request instance.
	 * @param urlPath URL path of request.
	 * @param username username of requester.
	 * @param password password of requester.
	 * @param isWrite true if is a write request, false if read.
	 */
	public QuakAuthorizationRequest(String urlPath, String username, String password, boolean isWrite) {
		setUrlPath( urlPath );
		setUsername( username );
		setPassword( password );
		setWrite( isWrite );
	}
	
	/**
	 * @return URL path of request.
	 */
	public String getUrlPath() {
		return urlPath;
	}
	
	
	/**
	 * @param urlPath URL path of request.
	 */
	public void setUrlPath( String urlPath ) {
		this.urlPath = urlPath;
	}
	
	/**
	 * @return username of requester.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username username of requester.
	 */
	public void setUsername( String username ) {
		this.username = username;
	}
	
	/**
	 * @return password of requester.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password password of requester.
	 */
	public void setPassword( String password ) {
		this.password = password;
	}

	/**
	 * @return true if it is a write request, false if not.
	 */
	public boolean isWrite() {
		return isWrite;
	}

	/**
	 * @param isWrite true if it is a write request, false if not.
	 */
	public void setWrite( boolean isWrite ) {
		this.isWrite = isWrite;
	}
}