/*
 * ----------------------------------------------------------------
 * Original File Name: QuakResourceRedeployTest.java
 * Creation Date:      29.03.2022
 * Description: Test class file of quak instance.       
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

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;

@QuarkusTest
@TestProfile( QuakTestProfileRedeployNotAllowed.class )
class QuakResourceRedeployTest {

	@Test
	void testUploadRedeploy() {
		Response response = given().when().get( "/at/bestsolution/blueprint/" ).andReturn();
		if ( response.getStatusCode() == Status.OK.getStatusCode() ) {
			given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then()
					.statusCode( Status.METHOD_NOT_ALLOWED.getStatusCode() );
		} 
		else if ( response.getStatusCode() == Status.NOT_FOUND.getStatusCode() ) {
			given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );
			given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then()
					.statusCode( Status.METHOD_NOT_ALLOWED.getStatusCode() );
		}
	}
}