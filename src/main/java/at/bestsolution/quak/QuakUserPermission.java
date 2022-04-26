/*
 * ----------------------------------------------------------------
 * Original File Name: QuakUserPermission.java
 * Creation Date:      21.04.2022
 * Description:  Represents a read or write permissions given to a quak user 
 * for given paths in regular expressions.
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a read or write permissions given to a quak user for given paths in regular expressions.
 * 
 * @author kerim.yeniduenya@bestsolution.com
 */
public class QuakUserPermission {
	
	private String username;
	private boolean isWrite;
	private List<Pattern> urlPathPatterns;
	
	/**
	 * Constructs a user permission definition instance.
	 * @param username of user.
	 * @param isWrite true if write permission false if read permission.
	 * @param urlPathsInRegex is paths in regular expressions which defines authorized paths.
	 */
	public QuakUserPermission(String username, boolean isWrite, List<String> urlPathsInRegex ) {
		setUsername( username );
		setWrite( isWrite );
		urlPathPatterns = new ArrayList<>();
		urlPathsInRegex.stream().forEach( pa -> urlPathPatterns.add( Pattern.compile( pa, Pattern.CASE_INSENSITIVE ) ) );
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public boolean isWrite() {
		return isWrite;
	}

	public void setWrite( boolean isWrite ) {
		this.isWrite = isWrite;
	}

	public List<Pattern> getUrlPathPatterns() {
		return urlPathPatterns;
	}

	public void setUrlPathPatterns( List<Pattern> urlPathPatterns ) {
		this.urlPathPatterns = urlPathPatterns;
	}
}