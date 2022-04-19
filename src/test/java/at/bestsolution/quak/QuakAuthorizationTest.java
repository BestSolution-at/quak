/*
 * ----------------------------------------------------------------
 * Original File Name: QuakAuthorizationTest.java
 * Creation Date:      19.04.2022
 * Description: Test file of test cases for authorization in quak.     
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

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * JUnit test cases for authorization in quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfileAuthorization.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakAuthorizationTest {
	
	public static final String DUMMY_FILE_FOO_UNAUTHORIZED = QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED.concat( "/dummy_file.foo" );
	public static final String DUMMY_FILE_FOO_SUBPATH = QuakTestProfileAuthorization.BASE_URL_SUBPATH.concat( "/dummy_file.foo" );
	
	/**
	 * Asserts authorization is done correctly.
	 */
	@Test
	@Order( 1 )
	void testAuthorization() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
		.put( DUMMY_FILE_FOO_UNAUTHORIZED ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
	given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
		.put( DUMMY_FILE_FOO_SUBPATH ).then().statusCode( Status.OK.getStatusCode() );
		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED.concat( "/" ) )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_SUBPATH.concat( "/" ) )
		.then().statusCode( Status.OK.getStatusCode() );
	}
}
