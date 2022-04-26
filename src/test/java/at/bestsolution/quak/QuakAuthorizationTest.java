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
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
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
	
	@Inject
	QuakConfigurationController confController;
	
	public static final String DUMMY_FILE_FOO_UNAUTHORIZED = QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED.concat( "/dummy_file.foo" );
	public static final String DUMMY_FILE_FOO_SUBPATH = QuakTestProfileAuthorization.BASE_URL_SUBPATH.concat( "/dummy_file.foo" );
	public static final String DUMMY_FILE_FOO_READ_ONLY = QuakTestProfileAuthorization.BASE_URL_READ_ONLY.concat( "/dummy_file.foo" );
	public static final String DUMMY_FILE_FOO_UNAUTHORIZED_PATH = QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED_PATH.concat( "/dummy_file.foo" );
	public static final String DUMMY_FILE_FOO_UNAUTHORIZED__READ_ONLY_PATH = QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED_READ_ONLY_PATH.concat( "/dummy_file.foo" );
	
	/**
	 * Asserts permissions are written and read correctly.
	 */
	@Test
	@Order( 1 )
	void testReadPermissions() {
		assertEquals( QuakTestProfile.GOOD_USERNAME, confController.getUserPermissions().get( 0 ).username() );
		assertEquals( QuakTestProfile.REPOSITORY_NAME, confController.getUserPermissions().get( 0 ).repositoryName() );
		assertEquals( QuakTestProfile.PATH_REGEX, confController.getUserPermissions().get( 0 ).urlPaths().get( 0 ) );
		assertEquals( true, confController.getUserPermissions().get( 0 ).isWrite() );
		assertEquals( QuakTestProfile.GOOD_USERNAME, confController.getUserPermissions().get( 1 ).username() );
		assertEquals( QuakTestProfileAuthorization.REPOSITORY_NAME_READ_ONLY, confController.getUserPermissions().get( 1 ).repositoryName() );
		assertEquals( QuakTestProfileAuthorization.PATH_REGEX_MATCH_ALL, confController.getUserPermissions().get( 1 ).urlPaths().get( 0 ) );
		assertEquals( false, confController.getUserPermissions().get( 1 ).isWrite() );
		assertEquals( QuakTestProfileAuthorization.USERNAME_ADMIN, confController.getUserPermissions().get( 2 ).username() );
		assertEquals( QuakTestProfileAuthorization.REPOSITORY_NAME_READ_ONLY, confController.getUserPermissions().get( 2 ).repositoryName() );
		assertEquals( QuakTestProfileAuthorization.PATH_REGEX_MATCH_ALL, confController.getUserPermissions().get( 2 ).urlPaths().get( 0 ) );
		assertEquals( true, confController.getUserPermissions().get( 2 ).isWrite() );
	}
	
	/**
	 * Asserts user can not write to unauthorized repository.
	 */
	@Test
	@Order( 2 )
	void testWriteToUnauthorizedRepository() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_FOO_UNAUTHORIZED ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can write to path regex matched sub path of permitted repository.
	 */
	@Test
	@Order( 3 )
	void testWriteToSubPathOfPermittedRepository() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_FOO_SUBPATH ).then().statusCode( Status.OK.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can not write to read permitted repository.
	 */
	@Test
	@Order( 4 )
	void testWriteToReadOnlyPermittedRepository() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_FOO_READ_ONLY ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
	}
	
	/**
	 * Asserts another user with write permission can write to given repository.
	 */
	@Test
	@Order( 5 )
	void testWriteWithPermittedUser() {
		given().auth().preemptive().basic( QuakTestProfileAuthorization.USERNAME_ADMIN, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_FOO_READ_ONLY ).then().statusCode( Status.OK.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can not read from unauthorized repository.
	 */
	@Test
	@Order( 6 )
	void testReadFromUnauthorizedRepository() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED.concat( "/" ) )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can read from path regex matched sub path of permitted repository.
	 */
	@Test
	@Order( 7 )
	void testReadFromSubPathOfPermittedRepository() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_SUBPATH.concat( "/" ) )
			.then().statusCode( Status.OK.getStatusCode() );
	}
	
	/**
	 * Asserts user can read from read only permitted repository.
	 */
	@Test
	@Order( 8 )
	void testReadFromReadOnlyPermittedRepository() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_READ_ONLY.concat( "/" ) )
			.then().statusCode( Status.OK.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can read from write permitted repository.
	 */
	@Test
	@Order( 9 )
	void testReadFromWritePermittedRepository() {
		given().auth().preemptive().basic( QuakTestProfileAuthorization.USERNAME_ADMIN, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_READ_ONLY.concat( "/" ) )
			.then().statusCode( Status.OK.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can not read from unauthorized path.
	 */
	@Test
	@Order( 10 )
	void testReadFromUnauthorizedPath() {
		given().auth().preemptive().basic( QuakTestProfileAuthorization.USERNAME_ADMIN, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED_PATH.concat( "/" ) )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() ); 
	}
	
	/**
	 * Asserts user can not write to unauthorized path.
	 */
	@Test
	@Order( 11 )
	void testWriteToUnauthorizedPath() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_FOO_UNAUTHORIZED_PATH ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
	}
	
	/**
	 * Asserts user can not write to unauthorized and read-only path.
	 */
	@Test
	@Order( 12 )
	void testWriteToUnauthorizedReadOnlyPath() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_FOO_UNAUTHORIZED__READ_ONLY_PATH ).then().statusCode( Status.UNAUTHORIZED.getStatusCode() );
	}
	
	/**
	 * Asserts user can not read from unauthorized and read-only path.
	 */
	@Test
	@Order( 13 )
	void testReadFromUnauthorizedReadOnlyPath() {
		given().auth().preemptive().basic( QuakTestProfileAuthorization.USERNAME_ADMIN, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfileAuthorization.BASE_URL_UNAUTHORIZED_READ_ONLY_PATH.concat( "/" ) )
			.then().statusCode( Status.UNAUTHORIZED.getStatusCode() ); 
	}
}
