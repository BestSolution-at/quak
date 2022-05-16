/*
 * ----------------------------------------------------------------
 * Original File Name: QuakSecurityInterceptorTest.java
 * Creation Date:      29.03.2022
 * Description: Test class file of quak security interceptor.       
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

import static io.restassured.RestAssured.given;
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
import javax.ws.rs.core.Response.Status;
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
 * JUnit test cases for security interceptor of quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakSecurityInterceptorTest {
	
	@Inject
	QuakSecurityInterceptor securityInterceptor;
	
	private static final String WRONG_USERNAME = "wrongname";
	private static final String WRONG_PASSWORD = "wrongpass";
	private static final String VALID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy5i"
											+ "ZXN0c29sdXRpb24uYXQvcXVhay90ZXN0IiwidXBuIjoidXNlcjEiLCJncm91cHM"
											+ "iOlsiVXNlciIsIkFkbWluIl0sImV4cCI6MTY4MjU3NTA5NSwiaWF0IjoxNjUxMD"
											+ "M5MDk1LCJqdGkiOiJjYTU0YmUzZi0zZDYyLTRkZDgtYjgyMi1hNTZmNzVmZDViM"
											+ "GIifQ.HwsbuQUxr5uk-lxs4gf6vo1j90LLigZRmAfCLmVf1U8GUWkHCsor3U5Qx"
											+ "kgdN8RyexnlQBjOSnGr7BInhCcqWjn5EAPymB7ExGFRJSJ1NmewmhyAP6EtOG3N"
											+ "r6Sp9Awm8-3O--TABGzI-ss8iXKadagUoO_ZS6VAbS2JikhU1TBn0yt49ti3aqD"
											+ "Fyyr-7_cCeQT2NfX42VHnNIXAHZUsWKVNyWoyzqxpq_I0zH1c19KSgiQ3WRl9s6"
											+ "Z-rnHXtUNNwLOD2YMW0bs5XO2k_hlA78KLMbfqJRe9xCqMFN6NRJ_w1JA7uwQFl"
											+ "weM_8gH40BSg1Ui3Kwq5exxit-KNAEeYw";
	private static final String INVALID_TOKEN = "WRONG_TOKEN";
	private static final String VALID_TOKEN_WITH_WRONGUSERNAME = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy"
											+ "5iZXN0c29sdXRpb24uYXQvcXVhay90ZXN0IiwidXBuIjoidXNlcldyb25nIiwiZ"
											+ "3JvdXBzIjpbIlVzZXIiLCJBZG1pbiJdLCJleHAiOjE2ODI1OTQ1OTYsImlhdCI6"
											+ "MTY1MTA1ODU5NiwianRpIjoiMzA1MWFiZGMtYWViZC00MjlmLTgyZmMtNGFmMTl"
											+ "jMTFkMmZlIn0.dAJxTa10a8nPbu2ueQ67rRd4uPZarS470kp0lpF9cYTbGTjwTG"
											+ "dW4mTvnqFByZdc6WtUtbKg2qluFDacFALYkOvbCaKJ9rPwvnMqgSAj3Iv-MfY3U"
											+ "lXT-9awuYe_p5bjHR_sEJgAYwlw1G6MHvX6Ib1ypXHRvqQWNFyZ3a9V_L6xqp0q"
											+ "4IiKUpvpOOe0140Z7qJOvh-s_r1Kwk9dhSiTE3M61nbNuqFQrv1qFoeovGkLTcF"
											+ "MOXmexy0rebvRtd8Z66R_fJTQObRXtyiG5f6F5yNF6SwLry6to2wcBsEFDEZjoi"
											+ "pkNnXmB9EwlfnWBfTjz7dJFPo6l9APipUl8tYHYw";
	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";
	private static final String UNDEFINED_AUTHORIZATION_SCHEME = "UndefinedScheme";
	private static final String USERINFO_KEY_SUB = "sub";
	
	/**
	 * Asserts authentication is done correctly.
	 */
	@Test
	@Order( 1 )
	void testAuthentication() {
		given().get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( WRONG_USERNAME, WRONG_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, WRONG_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( WRONG_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
		
		given().request().body( "dummy file" ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( WRONG_USERNAME, WRONG_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, WRONG_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( WRONG_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.OK.getStatusCode() );
	}
	
	/**
	 * Asserts authentication with tokens are done correctly.
	 */
	@Test
	@Order( 2 )
	void testTokenAuthentication() {
		given().auth().oauth2( VALID_TOKEN ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO ).then().statusCode( Status.OK.getStatusCode() );
		given().auth().oauth2( INVALID_TOKEN ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().oauth2( VALID_TOKEN_WITH_WRONGUSERNAME ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		
		given().auth().oauth2( VALID_TOKEN ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
		given().auth().oauth2( INVALID_TOKEN ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().oauth2( VALID_TOKEN_WITH_WRONGUSERNAME ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
	}
	
	/**
	 * Asserts a request with null authorization header has unauthorized response.
	 */
	@Test
	@Order( 3 )
	void testNullAuthorizationHeader() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, null );
		given().headers( headers ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
	}
	
	/**
	 * Asserts a request with empty authorization header has unauthorized response.
	 */
	@Test
	@Order( 4 )
	void testEmptyAuthorizationHeader() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( ) );
		given().headers( headers ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
	}
	
	/**
	 * Asserts a request with undefined authorization scheme has unauthorized response.
	 */
	@Test
	@Order( 5 )
	void testUndefinedAuthentication() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( UNDEFINED_AUTHORIZATION_SCHEME, QuakTestProfile.GOOD_USERNAME ) );
		given().headers( headers ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
	}
	
	/**
	 * Asserts QuakSecurityInterceptor aborts a request with null user principal.
	 */
	@Test
	@Order( 6 )
	void testNullUserPrincipal() {
		final ContainerRequestContext contextMock = mock( ContainerRequestContext.class );
		final SecurityContext securityContextMock = mock( SecurityContext.class );
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( AUTHENTICATION_SCHEME_BEARER, QuakTestProfile.GOOD_USERNAME ) );
		when( contextMock.getUriInfo() ).thenReturn( new ResteasyUriInfo( QuakTestProfile.BASE_URL.concat( "/" ),  "/" ) );
		when( contextMock.getMethod() ).thenReturn( HttpMethod.PUT.toString() );
		when( contextMock.getHeaders() ).thenReturn( headers );
		when( contextMock.getSecurityContext() ).thenReturn( securityContextMock );
		when( securityContextMock.getAuthenticationScheme() ).thenReturn( AUTHENTICATION_SCHEME_BEARER );
		securityInterceptor.filter( contextMock );
		verify( contextMock, times( 1 ) ).abortWith( any( Response.class ) );
	}
	
	/**
	 * Asserts QuakSecurityInterceptor aborts a request with null user principal name.
	 */
	@Test
	@Order( 7 )
	void testNullUserPrincipalName() {
		final ContainerRequestContext contextMock = mock( ContainerRequestContext.class );
		final SecurityContext securityContextMock = mock( SecurityContext.class );
		final Principal userPrincipal = mock( Principal.class );
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( AUTHENTICATION_SCHEME_BEARER, QuakTestProfile.GOOD_USERNAME ) );
		when( contextMock.getUriInfo() ).thenReturn( new ResteasyUriInfo( QuakTestProfile.BASE_URL.concat( "/" ),  "/" ) );
		when( contextMock.getMethod() ).thenReturn( HttpMethod.PUT.toString() );
		when( contextMock.getHeaders() ).thenReturn( headers );
		when( contextMock.getSecurityContext() ).thenReturn( securityContextMock );
		when( userPrincipal.getName() ).thenReturn( null );
		when( securityContextMock.getAuthenticationScheme() ).thenReturn( AUTHENTICATION_SCHEME_BEARER );
		when( securityContextMock.getUserPrincipal() ).thenReturn( userPrincipal );
		securityInterceptor.filter( contextMock );
		verify( contextMock, times( 1 ) ).abortWith( any( Response.class ) );
	}
	
	/**
	 * Asserts QuakSecurityInterceptor aborts a request with null security context.
	 */
	@Test
	@Order( 8 )
	void testNullSecurityContext() {
		final ContainerRequestContext contextMock = mock( ContainerRequestContext.class );
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( AUTHENTICATION_SCHEME_BEARER, QuakTestProfile.GOOD_USERNAME ) );
		when( contextMock.getUriInfo() ).thenReturn( new ResteasyUriInfo( QuakTestProfile.BASE_URL.concat( "/" ),  "/" ) );
		when( contextMock.getMethod() ).thenReturn( HttpMethod.PUT.toString() );
		when( contextMock.getHeaders() ).thenReturn( headers );
		when( contextMock.getSecurityContext() ).thenReturn( null );
		securityInterceptor.filter( contextMock );
		verify( contextMock, times( 1 ) ).abortWith( any( Response.class ) );
	}
	
	/**
	 * Asserts QuakSecurityInterceptor does NOT abort a request with good OpenID Connect user info.
	 */
	@Test
	@TestSecurity( user = QuakTestProfile.GOOD_USERNAME )
    @OidcSecurity( userinfo = { @UserInfo( key = USERINFO_KEY_SUB, value = QuakTestProfile.GOOD_USERNAME) } )
	@Order( 9 )
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
	@TestSecurity( user = WRONG_USERNAME )
    @OidcSecurity( userinfo = { @UserInfo( key = USERINFO_KEY_SUB, value = WRONG_USERNAME) } )
	@Order( 9 )
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