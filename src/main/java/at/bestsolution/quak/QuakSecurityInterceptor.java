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
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Security Interceptor to verify the access permissions for a user.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@Provider
@PreMatching
public class QuakSecurityInterceptor implements ContainerRequestFilter {
	
	@Inject
	QuakSecurityValidator securityValidator;
	
	@Context
	private UriInfo urlInfo;

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
		LOG.debugf( "Request received at: %s", context.getUriInfo().getRequestUri().toString() );
		final List<String> authorization = context.getHeaders().get( AUTHORIZATION_PROPERTY );
		final boolean isWrite = context.getMethod().equals( HttpMethod.PUT.toString() ) || context.getMethod().equals( HttpMethod.POST.toString() );
		final QuakRepository repository = securityValidator.getQuakRepository( urlInfo.getPath() );
		final Response responseUnauthorized = Response.status( Response.Status.UNAUTHORIZED ).build();
		
		if ( repository != null && ( repository.isPrivate() || isWrite ) ) {
			if ( authorization == null || authorization.isEmpty() ) {
				LOG.debugf( "No credentials given for authentication." );
				responseUnauthorized.getHeaders().add( AUTHENTICATION_RESPONSE_HEADER, AUTHENTICATION_SCHEME );
				context.abortWith( responseUnauthorized );
			}
			else {
				final String encodedUserPassword = authorization.get( 0 ).replaceFirst( AUTHENTICATION_SCHEME + " ", "" );
				final String usernameAndPassword = new String( Base64.getDecoder().decode( encodedUserPassword ) );
				final StringTokenizer tokenizer = new StringTokenizer( usernameAndPassword, ":" );
				final String username = tokenizer.nextToken();
				final String password = tokenizer.nextToken();
				final QuakAuthorizationRequest request = new QuakAuthorizationRequest( urlInfo.getPath(), username, password, isWrite );
				try {
					LOG.debugf( "Validating request for user: %s", username );
					if ( !securityValidator.isUserAuthenticated( request ) || !securityValidator.isUserAuthorized( request ) ) {
						context.abortWith( responseUnauthorized );
					}
				} 
				catch ( Exception e ) {
					LOG.errorf( "Exception while checking password for: %s, %s", username, e );
					context.abortWith( responseUnauthorized );
				}
			}
		}
	}
}