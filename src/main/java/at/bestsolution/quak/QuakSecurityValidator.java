/*
 * ----------------------------------------------------------------
 * Original File Name: QuakSecurityValidator.java
 * Creation Date:      19.04.2022
 * Description: Validator class for credentials and permissions of quak users.     
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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.directory.api.ldap.model.password.BCrypt;
import org.jboss.logging.Logger;

/**
 * Validator class for credentials and permissions of quak users.
 * 
 * @author kerim.yeniduenya@bestsolution.at
 */
@ApplicationScoped
public class QuakSecurityValidator {
	
	@Inject
	QuakConfigurationController confController;
	
	private static final Logger LOG = Logger.getLogger( QuakSecurityValidator.class );
	
	/**
	 * Credential map containing username, password.
	 */
	private HashMap<String, String> credentials;
	
	/**
	 * List of user authorizations given for repositories and paths
	 */
	private List<QuakUserAuthorization> userAuthorizations;
	
	/**
	 * Initializes QuakSecurityValidator instance.
	 */
	@PostConstruct
	public void initialize() {
		credentials = new HashMap<>();
		userAuthorizations = new ArrayList<>();
		
		confController.getUsers().stream().forEach( u -> credentials.put( u.username(), u.password() ) );
		confController.getUserPermissions().forEach( pe -> pe.paths().
				forEach( pa -> userAuthorizations.add( new QuakUserAuthorization( pe.username(), pe.repositoryName(), pe.isWrite(), Pattern.compile( pa, Pattern.CASE_INSENSITIVE ) ) ) ) );
		
		LOG.info( "QuakSecurityValidator is initialized." );
	}
	
	/**
	 * Searches through configuration for a matching repository permission for a given user.
	 * @param username of the authorization request.
	 * @param repositoryName of the repository which authorization requested for.
	 * @param path of the request which authorization requested for.
	 * @param isWriteRequest true if a write request, false if not. 
	 * @return true if authorized, false if not.
	 */
	public boolean isUserAuthorized( String username, String repositoryName, String path, boolean isWriteRequest ) {
		return userAuthorizations.stream().anyMatch( u -> u.getUsername().equals( username ) && u.getRepositoryName().equals( repositoryName ) 
				&& ( !isWriteRequest || u.isWrite() ) && u.getPathPattern().matcher( path ).matches() );
	}
	
	/**
	 * Searches through configuration for a matching username and password. For passwords, BCrypt hash algorithm is used.
	 * @param username of the user.
	 * @param password of the user, hashed with BCrypt.
	 * @return true if a valid username and password is given, false if not.
	 */
	public boolean isValidUsernamePassword(String username, String password) {
		 return ( credentials.containsKey( username ) && BCrypt.checkPw( password, credentials.get( username ) ) );
	}
}