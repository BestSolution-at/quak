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

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * JUnit test cases for security interceptor of quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakSecurityInterceptorTest {
	
	private static final String WRONG_USERNAME = "wrongname";
	private static final String WRONG_PASSWORD = "wrongpass";
	private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy5i"
										+ "ZXN0c29sdXRpb24uYXQvcXVhay90ZXN0IiwidXBuIjoidXNlcjEiLCJncm91cHM"
										+ "iOlsiVXNlciIsIkFkbWluIl0sImV4cCI6MTY4MjU3NTA5NSwiaWF0IjoxNjUxMD"
										+ "M5MDk1LCJqdGkiOiJjYTU0YmUzZi0zZDYyLTRkZDgtYjgyMi1hNTZmNzVmZDViM"
										+ "GIifQ.HwsbuQUxr5uk-lxs4gf6vo1j90LLigZRmAfCLmVf1U8GUWkHCsor3U5Qx"
										+ "kgdN8RyexnlQBjOSnGr7BInhCcqWjn5EAPymB7ExGFRJSJ1NmewmhyAP6EtOG3N"
										+ "r6Sp9Awm8-3O--TABGzI-ss8iXKadagUoO_ZS6VAbS2JikhU1TBn0yt49ti3aqD"
										+ "Fyyr-7_cCeQT2NfX42VHnNIXAHZUsWKVNyWoyzqxpq_I0zH1c19KSgiQ3WRl9s6"
										+ "Z-rnHXtUNNwLOD2YMW0bs5XO2k_hlA78KLMbfqJRe9xCqMFN6NRJ_w1JA7uwQFl"
										+ "weM_8gH40BSg1Ui3Kwq5exxit-KNAEeYw\n";
	private static final String WRONG_TOKEN = "WRONG_TOKEN";
	
	
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
	 * Asserts authorization with tokens are done correctly.
	 */
	@Test
	@Order( 2 )
	void testTokenAuthorization() {
		given().auth().oauth2( TOKEN ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO ).then().statusCode( Status.OK.getStatusCode() );
		given().auth().oauth2( WRONG_TOKEN ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT ).put( QuakResourceTest.DUMMY_FILE_FOO ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		
		given().auth().oauth2( TOKEN ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
		given().auth().oauth2( WRONG_TOKEN ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
	}
}
