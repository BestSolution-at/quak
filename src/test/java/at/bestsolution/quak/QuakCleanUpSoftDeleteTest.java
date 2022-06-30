/*
 * ----------------------------------------------------------------
 * Original File Name: QuakCleanUpSoftDeleteTest.java
 * Creation Date:      29.06.2022
 * Description: Test class file of JUnit test cases for clean-up 
 * task of quak with soft delete test profile.      
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

import java.nio.file.Files;

import javax.ws.rs.core.Response.Status;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * JUnit test cases for clean-up task of quak with soft delete test profile.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfileSoftDelete.class )
class QuakCleanUpSoftDeleteTest extends QuakCleanUpTest {
	
	private static final String TRASH_DIRECTORY_NAME = ".filesToBeDeleted"; 

	/**
	 * Maven deploy is imitated before each test.
	 */
	@BeforeEach
	void previousDeploy() {
		// Previous deploy
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_PREVIOUS ).then().statusCode( Status.OK.getStatusCode() );

		Awaitility.await().pollDelay( Durations.ONE_SECOND ).until( () -> true );
	}

	/**
	 * Asserts that clean-up is done correctly with soft delete.
	 */
	@Test
	void testSoftDeleteCleanUp() {
		// Do a dummy Maven deploy.
		String dummyFileNameCurrentDeploy = doDummyMavenDeploy();

		// File from previous deploy must be moved.
		Awaitility.await().until( () -> Files.notExists( REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( DUMMY_FILE_NAME_PREVUOUS_DEPLOY ) ) );
		Awaitility.await().until( () -> Files.exists( REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( TRASH_DIRECTORY_NAME ).resolve( DUMMY_FILE_NAME_PREVUOUS_DEPLOY ) ) );
		
		// File from current deploy must stay undeleted.
		Awaitility.await().until( () -> Files.exists( REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( dummyFileNameCurrentDeploy ) ) );
		
		// Do another maven deploy simultaneously and see files stay undeleted.
		String dummyFileNameSimultaneousDeploy = doDummyMavenDeploy();
		Awaitility.await().until( () -> Files.exists( REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( dummyFileNameSimultaneousDeploy ) ) );
	}
}