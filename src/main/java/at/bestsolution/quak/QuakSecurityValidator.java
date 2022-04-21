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

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.directory.api.ldap.model.password.BCrypt;

/**
 * Validator class for credentials and permissions of quak users.
 * 
 * @author kerim.yeniduenya@bestsolution.at
 */
@ApplicationScoped
public class QuakSecurityValidator {
	
	@Inject
	QuakConfigurationController confController;
	
	/**
	 * Credential map containing username, password.
	 */
	private HashMap<String, String> mapCredentials;
	
	/**
	 * Initializes QuakSecurityValidator instance.
	 */
	@PostConstruct
	public void initialize() {
		mapCredentials = new HashMap<>();
		confController.getUsers().stream().forEach( u -> mapCredentials.put( u.username(), u.password() ) );
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
		return confController.getPermissionsOfUserForRepository( username, repositoryName ).stream().anyMatch( pe -> pe.paths().stream()
				.anyMatch( pa -> Pattern.compile( pa, Pattern.CASE_INSENSITIVE ).matcher( path ).matches() && ( !isWriteRequest || pe.isWrite() ) ) );
	}
	
	/**
	 * Searches through configuration for a matching username and password. For passwords, BCrypt hash algorithm is used.
	 * @param username of the user.
	 * @param password of the user, hashed with BCrypt.
	 * @return true if a valid username and password is given, false if not.
	 */
	public boolean isValidUsernamePassword(String username, String password) {
		 return ( mapCredentials.containsKey( username ) && BCrypt.checkPw( password, mapCredentials.get( username ) ) );
	}
}