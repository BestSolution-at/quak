/*
 * ----------------------------------------------------------------
 * Original File Name: QuakUserAuthorization.java
 * Creation Date:      21.04.2022
 * Description: Represents a read or write authorization given to a user 
 * on a repository.     
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

import java.util.regex.Pattern;

/**
 * Represents a read or write authorization given to a user on a repository. Paths are to be restricted by set pattern.
 * 
 * @author kerim.yeniduenya@bestsolution.com
 */
public class QuakUserAuthorization {
	
	private String username;
	private String repositoryName;
	private boolean isWrite;
	private Pattern pathPattern;
	
	public QuakUserAuthorization(String username, String repositoryName, boolean isWrite, Pattern pathPattern) {
		setUsername( username );
		setRepositoryName( repositoryName );
		setWrite( isWrite );
		setPathPattern( pathPattern );
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName( String repositoryName ) {
		this.repositoryName = repositoryName;
	}

	public boolean isWrite() {
		return isWrite;
	}

	public void setWrite( boolean isWrite ) {
		this.isWrite = isWrite;
	}

	public Pattern getPathPattern() {
		return pathPattern;
	}

	public void setPathPattern( Pattern pathPattern ) {
		this.pathPattern = pathPattern;
	}
}