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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Security Interceptor to verify the repository access for a user.
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
	
	@Inject
    JsonWebToken jwt;

	private static final Logger LOG = Logger.getLogger( QuakSecurityInterceptor.class );
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME_BASIC = "Basic";
	private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
	private static final String AUTHENTICATION_RESPONSE_HEADER = "WWW-Authenticate";

	/**
	 * Filters out the unauthorized access to quak service. Valid authentication must be provided if repository of requested URL is a private one or request is a
	 * write request sent by a PUT or POST. If requirements are not met than request is filtered out by this method.
	 * @param context is context of request. It must include authorization headers if required.
	 */
	@Override
	public void filter( ContainerRequestContext context ) {
		LOG.debugf( "Request received at: %s", context.getUriInfo().getRequestUri().toString() );
		final SecurityContext securityContext = context.getSecurityContext();
		final List<String> authorizationHeader = context.getHeaders().get( AUTHORIZATION_PROPERTY );
		final QuakRepository repository = securityValidator.getQuakRepository( urlInfo.getPath() );
		QuakAuthorizationRequest request = new QuakAuthorizationRequest( urlInfo.getPath(), null, null, context.getMethod().equals( HttpMethod.PUT.toString() ) || context.getMethod().equals( HttpMethod.POST.toString() ) );
		if ( repository == null ) {
			LOG.errorf( "No repository found for path: %s", urlInfo.getPath() );
			context.abortWith( Response.status( Status.NOT_FOUND ).build() );
		}
		else if ( isAuthorizationRequired( request, repository ) ) {
			LOG.debugf( "Validating authentication for user: %s", request.getUsername() );
			// Check if any authentication is provided.
			if ( !isAuthenticationProvided( authorizationHeader ) ) {
				LOG.debugf( "No credentials given for authentication." );
				final Response responseUnauthorized = Response.status( Response.Status.UNAUTHORIZED ).build();
				responseUnauthorized.getHeaders().add( AUTHENTICATION_RESPONSE_HEADER, AUTHENTICATION_SCHEME_BASIC );
				context.abortWith( responseUnauthorized );
			} 
			else if ( securityContext.getAuthenticationScheme().equals( AUTHENTICATION_SCHEME_BEARER ) ) {
				// Check if Bearer/Token Authentication is provided and authorization is valid.
				request.setUsername( securityContext.getUserPrincipal().getName() );
				if ( !isUserAuthorizedByBearerAuth( securityContext, request ) ) {
					context.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
				}
			} 
			else if ( securityContext.getAuthenticationScheme().equals( AUTHENTICATION_SCHEME_BASIC ) ) { 
				// Check if Basic Authentication with username and password is provided and authorization is valid.
				final String encodedUserPassword = authorizationHeader.get( 0 ).replaceFirst( AUTHENTICATION_SCHEME_BASIC + " ", "" );
				final String usernameAndPassword = new String( Base64.getDecoder().decode( encodedUserPassword ) );
				final StringTokenizer tokenizer = new StringTokenizer( usernameAndPassword, ":" );
				request.setUsername( tokenizer.nextToken() );
				request.setPassword( tokenizer.nextToken() );
				if ( !isUserAuthorizedByBasicAuth( request ) ) {
					context.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
				}
			} 
			else {
				// If what is provided does not match anything, abort with unauthorized.
				context.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
			}
		}
	}
	
	/**
	 * Checks if user is authorized by bearer token authentication.
	 * @param securityContext security context of request. 
	 * @param request quak authorization request.
	 * @return true if authenticated, false if not.
	 */
	private boolean isUserAuthorizedByBearerAuth( SecurityContext securityContext, QuakAuthorizationRequest request ) {
		return securityContext.getUserPrincipal() != null && securityContext.getUserPrincipal().getName().equals( jwt.getName() )
				&& securityValidator.isUserAuthorized( request );
	}
	
	/**
	 * Checks if user is authorized by basic username and password authentication.
	 * @param request quak authorization request with credentials.
	 * @return true if authorized, false if not. 
	 */
	private boolean isUserAuthorizedByBasicAuth( QuakAuthorizationRequest request ) {
		try {
			if ( !securityValidator.isUserAuthenticated( request ) || !securityValidator.isUserAuthorized( request ) ) {
				return false;
			}
		} 
		catch ( Exception e ) {
			LOG.errorf( "Exception while checking password for: %s, %s", request.getUsername(), e );
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if authorization header is filled or not.
	 * @param authorizationHeader authorization header of request.
	 * @return true if authentication provided, false if not.
	 */
	private boolean isAuthenticationProvided( List<String> authorizationHeader ) {
		return authorizationHeader != null && !authorizationHeader.isEmpty();
	}
		
	/**
	 * Checks if authorization is required or not. Authorization is required if request is a write or repository is
	 * a private one.
	 * @param request quak authorization request.
	 * @param repository quak repository to be accessed.
	 * @return true if authorization must be provided, false if no need for authorization.
	 */
	private boolean isAuthorizationRequired( QuakAuthorizationRequest request, QuakRepository repository ) {
		return repository.isPrivate() || request.isWrite();
	}
}