/*
 * ----------------------------------------------------------------
 * Original File Name: QuakBasicAuthenticationTest.java
 * Creation Date:      17.05.2022
 * Description: Test class file of JUnit test cases for Basic 
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

import static io.restassured.RestAssured.given;

import java.util.Arrays;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * JUnit test cases for Basic authentication in QuakSecurityInterceptor.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakBasicAuthenticationTest {
	
	public static final String AUTHORIZATION_PROPERTY = "Authorization";
	public static final String UNDEFINED_AUTHORIZATION_SCHEME = "UndefinedScheme";
	
	/**
	 * Asserts authentication is done correctly.
	 */
	@Test
	@Order( 1 )
	void testBasicAuthentication() {
		given().request().body( "dummy file" ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.WRONG_USERNAME, QuakTestProfile.WRONG_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.WRONG_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.WRONG_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO )
			.then().statusCode( Status.OK.getStatusCode() );
		
		given().get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.WRONG_USERNAME, QuakTestProfile.WRONG_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.WRONG_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.WRONG_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
	}
		
	/**
	 * Asserts a request with null authorization header has unauthorized response.
	 */
	@Test
	@Order( 2 )
	void testNullAuthorizationHeader() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, null );
		given().headers( headers ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
	}
	
	/**
	 * Asserts a request with empty authorization header has unauthorized response.
	 */
	@Test
	@Order( 3 )
	void testEmptyAuthorizationHeader() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( ) );
		given().headers( headers ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
	}
	
	/**
	 * Asserts a request with undefined authorization scheme has unauthorized response.
	 */
	@Test
	@Order( 4 )
	void testUndefinedAuthentication() {
		MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();
		headers.put( AUTHORIZATION_PROPERTY, Arrays.asList( UNDEFINED_AUTHORIZATION_SCHEME, QuakTestProfile.GOOD_USERNAME ) );
		given().headers( headers ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
	}
}