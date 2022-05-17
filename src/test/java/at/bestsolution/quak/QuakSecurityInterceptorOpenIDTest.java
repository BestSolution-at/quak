/*
 * ----------------------------------------------------------------
 * Original File Name: QuakSecurityInterceptorOpenIDTest.java
 * Creation Date:      17.05.2022
 * Description: Test class file of JUnit test cases for OpenID token 
 * authentication in QuakSecurityInterceptor.       
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.netty.handler.codec.http.HttpMethod;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.UserInfo;

/**
 * JUnit test cases for OpenID token authentication in QuakSecurityInterceptor.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakSecurityInterceptorOpenIDTest {
	
	@Inject
	QuakSecurityInterceptor securityInterceptor;
	
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
	private static final String USERINFO_KEY_SUB = "sub";
		
	/**
	 * Asserts QuakSecurityInterceptor does NOT abort a request with good OpenID Connect user info.
	 */
	@Test
	@TestSecurity( user = QuakTestProfile.GOOD_USERNAME )
    @OidcSecurity( userinfo = { @UserInfo( key = USERINFO_KEY_SUB, value = QuakTestProfile.GOOD_USERNAME) } )
	@Order( 1 )
	void testGoodSecurityIdentity() {
		final ContainerRequestContext contextMock = mock( ContainerRequestContext.class );
		final SecurityContext securityContextMock = mock( SecurityContext.class );
		final Principal userPrincipal = mock( Principal.class );
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( AUTHENTICATION_SCHEME_BEARER, QuakTestProfile.GOOD_USERNAME ) );
		when( contextMock.getUriInfo() ).thenReturn( new ResteasyUriInfo( QuakTestProfile.BASE_URL.concat( "/" ),  "/" ) );
		when( contextMock.getMethod() ).thenReturn( HttpMethod.PUT.toString() );
		when( contextMock.getHeaders() ).thenReturn( headers );
		when( contextMock.getSecurityContext() ).thenReturn( securityContextMock );
		when( securityContextMock.getAuthenticationScheme() ).thenReturn( AUTHENTICATION_SCHEME_BEARER );
		when( securityContextMock.getUserPrincipal() ).thenReturn( userPrincipal );
		securityInterceptor.filter( contextMock );
		verify( contextMock, times( 0 ) ).abortWith( any( Response.class ) );
	}
	
	/**
	 * Asserts QuakSecurityInterceptor aborts a request with wrong OpenID Connect user info.
	 */
	@Test
	@TestSecurity( user = QuakTestProfile.WRONG_USERNAME )
    @OidcSecurity( userinfo = { @UserInfo( key = USERINFO_KEY_SUB, value = QuakTestProfile.WRONG_USERNAME) } )
	@Order( 2 )
	void testWrongSecurityIdentity() {
		final ContainerRequestContext contextMock = mock( ContainerRequestContext.class );
		final SecurityContext securityContextMock = mock( SecurityContext.class );
		final Principal userPrincipal = mock( Principal.class );
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( AUTHENTICATION_SCHEME_BEARER, QuakTestProfile.GOOD_USERNAME ) );
		when( contextMock.getUriInfo() ).thenReturn( new ResteasyUriInfo( QuakTestProfile.BASE_URL.concat( "/" ),  "/" ) );
		when( contextMock.getMethod() ).thenReturn( HttpMethod.PUT.toString() );
		when( contextMock.getHeaders() ).thenReturn( headers );
		when( contextMock.getSecurityContext() ).thenReturn( securityContextMock );
		when( securityContextMock.getAuthenticationScheme() ).thenReturn( AUTHENTICATION_SCHEME_BEARER );
		when( securityContextMock.getUserPrincipal() ).thenReturn( userPrincipal );
		securityInterceptor.filter( contextMock );
		verify( contextMock, times( 1 ) ).abortWith( any( Response.class ) );
	}
}