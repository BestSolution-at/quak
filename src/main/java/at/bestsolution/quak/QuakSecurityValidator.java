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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkus.elytron.security.common.BcryptUtil;

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
	 * List of quak repositories
	 */
	private List<QuakRepository> repositories;
	
	/**
	 * Initializes QuakSecurityValidator instance.
	 */
	@PostConstruct
	public void initialize() {
		credentials = new HashMap<>();
		repositories = new ArrayList<>();
		
		confController.getBasicUsers().stream().forEach( u -> credentials.put( u.username(), u.password() ) );
		confController.getRepositories().forEach( re -> repositories.add( new QuakRepository( re.name(), re.baseUrl(), re.isPrivate(), re.allowRedeploy(), re.storagePath() ) ) );
		confController.getUserPermissions().forEach( pe ->  repositories.stream().filter( re -> pe.repositoryName().equals( re.getName() ) ).findFirst().get().
				getUserPermissions().add( new QuakUserPermission( pe.username(), pe.mayPublish(), pe.urlPaths() ) ) );
		
		LOG.info( "QuakSecurityValidator is initialized." );
	}
	
	/**
	 * Searches through configuration for a matching repository permission for the user in given request.
	 * @param request to be validated if it's user is authorized to given path. 
	 * @return true if authorized, false if not.
	 */
	public boolean isUserAuthorized( QuakAuthorizationRequest request ) {
		return getQuakRepository( request.getUrlPath() ).getUserPermissions().stream().anyMatch( p -> p.getUsername().equals( request.getUsername() ) 
				&& ( !request.isWrite() || p.isMayPublish() ) && p.getUrlPathPatterns().stream().anyMatch( pa -> pa.matcher( request.getUrlPath() ).matches() ) );
	}
	
	/**
	 * Searches through configuration for a matching username and password for a given request. For passwords, BCrypt hash algorithm is used.
	 * @param request to be checked if valid user credentials are given.
	 * @return true if username and password is valid, false if not.
	 */
	public boolean isUserAuthenticated( QuakAuthorizationRequest request ) {
		 return ( credentials.containsKey( request.getUsername() ) && BcryptUtil.matches( request.getPassword(), credentials.get( request.getUsername() ) ) );
	}

	
	/**
	 * Searches for a quak repository which has a base URL matching with beginning of the path.
	 * @param urlPath URL path of the upload request.
	 * @return Repository a quak repository or null in case of no match.
	 */
	public QuakRepository getQuakRepository( String urlPath ) {
		return repositories.stream().filter( r -> urlPath.startsWith( r.getBaseUrl() ) ).findFirst().orElse( null );
	}
}