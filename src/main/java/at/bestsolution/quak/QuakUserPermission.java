/*
 * ----------------------------------------------------------------
 * Original File Name: QuakUserPermission.java
 * Creation Date:      21.04.2022
 * Description:  Represents a read or write permissions given to a quak user 
 * for given URL paths in regular expressions.
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
 * Represents a read or write permissions given to a quak user for given URL paths in regular expressions.
 * 
 * @author kerim.yeniduenya@bestsolution.com
 */
public class QuakUserPermission {
	
	private String username;
	private boolean mayPublish;
	private List<Pattern> urlPathPatterns;
	
	/**
	 * Constructs a user permission definition instance. Strings in parameter list urlPathsInRegex are used in pattern compilation and
	 * added to urlPathPatterns list. 
	 * @param username username of user.
	 * @param mayPublish true if user has right to publish, false if has only read right.
	 * @param urlPathsInRegex list of strings in regular expressions each of them defining authorized URL paths for given username.
	 */
	public QuakUserPermission(String username, boolean mayPublish, List<String> urlPathsInRegex ) {
		setUsername( username );
		setMayPublish( mayPublish );
		urlPathPatterns = new ArrayList<>();
		urlPathsInRegex.stream().forEach( pa -> urlPathPatterns.add( Pattern.compile( pa, Pattern.CASE_INSENSITIVE ) ) );
	}

	/**
	 * @return username for whom permission is given.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username username for whom permission is given.
	 */
	public void setUsername( String username ) {
		this.username = username;
	}

	/**
	 * @return true if user has right to publish, false if has only read right.
	 */
	public boolean isMayPublish() {
		return mayPublish;
	}

	/**
	 * @param mayPublish true if user has right to publish, false if has only read right.
	 */
	public void setMayPublish( boolean mayPublish ) {
		this.mayPublish = mayPublish;
	}

	/**
	 * @return list of regular expression patterns compiled to define permitted URL paths for related user.
	 */
	public List<Pattern> getUrlPathPatterns() {
		return urlPathPatterns;
	}

	/**
	 * @param urlPathPatterns list of regular expression patterns compiled to define permitted URL paths for related user.
	 */
	public void setUrlPathPatterns( List<Pattern> urlPathPatterns ) {
		this.urlPathPatterns = urlPathPatterns;
	}
}