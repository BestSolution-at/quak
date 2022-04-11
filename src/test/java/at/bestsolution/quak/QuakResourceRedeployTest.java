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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;

/**
 * JUnit test cases for redeploy functionality of quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfileRedeployNotAllowed.class )
class QuakResourceRedeployTest {

	/**
	 * For redeploy tests, the file must be created before.
	 */
	@BeforeEach
	void createFileIfNotExists() {
		Response response = given().when().auth().preemptive().basic( QuakTestProfile.USERNAME, QuakTestProfile.PASSWORD )
				.get( QuakResourceTest.DUMMY_FILE_FOO ).andReturn();
		if ( response.getStatusCode() == Status.NOT_FOUND.getStatusCode() ) {
			given().auth().preemptive().basic( QuakTestProfile.USERNAME, QuakTestProfile.PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
				.put( QuakResourceTest.DUMMY_FILE_FOO ).andReturn();
		}
	}

	/**
	 * Asserts that redeploy not allowed.
	 */
	@Test
	void testUploadRedeployNotAllowed() {
		given().auth().preemptive().basic( QuakTestProfile.USERNAME, QuakTestProfile.PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( QuakResourceTest.DUMMY_FILE_FOO ).then().statusCode( Status.METHOD_NOT_ALLOWED.getStatusCode() );
	}
}