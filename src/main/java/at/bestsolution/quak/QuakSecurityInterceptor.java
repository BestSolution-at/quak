/*
 * ----------------------------------------------------------------
 * Original File Name: QuakSecurityInterceptor.java
 * Creation Date:      05.04.2022
 * Description: Security Interceptor class to verify the access permissions 
 * for a user.       
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

import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.directory.api.ldap.model.password.BCrypt;
import org.jboss.logging.Logger;

/**
 * Security Interceptor to verify the access permissions for a user.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@Provider
public class QuakSecurityInterceptor implements ContainerRequestFilter {
	
	@Inject
	QuakConfiguration configuration;

	private static final Logger LOG = Logger.getLogger( QuakSecurityInterceptor.class );
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final String AUTHENTICATION_RESPONSE_HEADER = "WWW-Authenticate";

	/**
	 * Filters out the unauthorized access to quak service or path.
	 * @param context is current context of the request.
	 */
	@Override
	public void filter( ContainerRequestContext context ) {
		LOG.debugf( "Verifying the credentials for request: %s", context.getUriInfo().getRequestUri().toString() );

		MultivaluedMap<String, String> headers = context.getHeaders();
		final List<String> authorization = headers.get( AUTHORIZATION_PROPERTY );
		Response responseUnauthorized = Response.status( Response.Status.UNAUTHORIZED ).build();
		
		if ( authorization == null || authorization.isEmpty() ) {
			LOG.debugf( "No credentials given for authentication." );
			responseUnauthorized.getHeaders().add( AUTHENTICATION_RESPONSE_HEADER, AUTHENTICATION_SCHEME );
			context.abortWith( responseUnauthorized );
		} 
		else {
			final String encodedUserPassword = authorization.get( 0 ).replaceFirst( AUTHENTICATION_SCHEME + " ", "" );
			String usernameAndPassword = new String( Base64.getDecoder().decode( encodedUserPassword ) );
			final StringTokenizer tokenizer = new StringTokenizer( usernameAndPassword, ":" );
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();
			
			try {		    
				LOG.debugf( "Verifying the user: %s", username );
				if ( configuration.users().stream().noneMatch( ( t -> t.username().equals( username ) && BCrypt.checkPw( password, t.password() ) ) ) ) {
					context.abortWith( responseUnauthorized );
				}
			} 
			catch ( Exception e ) {
				LOG.error( "Exception while decrypting password!", e );
				context.abortWith( responseUnauthorized );
			}
		}
	}
}
