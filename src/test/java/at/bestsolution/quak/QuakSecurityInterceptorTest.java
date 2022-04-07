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
	
	/**
	 * Asserts authentication is done correctly.
	 */
	@Test
	@Order( 1 )
	void testAuthentication() {
		given().get( "/at/bestsolution/blueprint/" ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "wrongname", "wrongpass" ).get( "/at/bestsolution/blueprint/" ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );		
		given().auth().preemptive().basic( "user1", "wrongpass" ).get( "/at/bestsolution/blueprint/" ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "wrongname", "pass123" ).get( "/at/bestsolution/blueprint/" ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "user1", "pass123" ).get( "/at/bestsolution/blueprint/" ).then().statusCode( Status.OK.getStatusCode() );
		
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "wrongname", "wrongpass" ).request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "user1", "wrongpass" ).request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "wrongname", "pass123" ).request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
		given().auth().preemptive().basic( "user1", "pass123" ).request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" )
			.then().statusCode( Status.OK.getStatusCode() );
	}
}
