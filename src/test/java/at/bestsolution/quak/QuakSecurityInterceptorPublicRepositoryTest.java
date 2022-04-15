/*
 * ----------------------------------------------------------------
 * Original File Name: QuakSecurityInterceptorPublicRepositoryTest.java
 * Creation Date:      14.04.2022
 * Description: JUnit test cases with public repository profile for 
 * security interceptor of quak.       
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
 * JUnit test cases with public repository profile for security interceptor of quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfilePublicRepository.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakSecurityInterceptorPublicRepositoryTest {
	
	private static final String WRONG_USERNAME = "wrongname";
	private static final String WRONG_PASSWORD = "wrongpass";
	
	/**
	 * Asserts authentication is done correctly.
	 */
	@Test
	@Order( 1 )
	void testAuthentication() {
		given().get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( WRONG_USERNAME, WRONG_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, WRONG_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( WRONG_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
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
}
