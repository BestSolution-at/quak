/*
 * ----------------------------------------------------------------
 * Original File Name: QuakResourceTest.java
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.SocketException;

import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.runtime.configuration.MemorySizeConverter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * JUnit test cases for basic functionalities of quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakResourceTest {

	@ConfigProperty( name = "quarkus.http.limits.max-body-size" )
	String confMaxUploadLimit;

	public static final int KB = 1024;
	public static final int MB = 1024 * 1024;
	public static final String DUMMY_FILE_CONTENT = "dummy file";
	public static final String DUMMY_FILE_FOO = QuakTestProfile.BASE_URL.concat( "/dummy_file.foo" );
	public static final String SUB_FOLDER_DUMMY_FILE_FOO = QuakTestProfile.BASE_URL.concat( "/subFolder/dummy_file.foo" );
	public static final String DUMMY_FILE_FOO_1KB = QuakTestProfile.BASE_URL.concat( "/dummy_file_1KB.foo" );
	public static final String DUMMY_FILE_FOO_1MB = QuakTestProfile.BASE_URL.concat( "/dummy_file_1MB.foo" );
	public static final String DUMMY_FILE_XML = QuakTestProfile.BASE_URL.concat( "/dummy_file.xml" );
	public static final String DUMMY_FILE_POM = QuakTestProfile.BASE_URL.concat( "/dummy_file.pom" );
	public static final String DUMMY_FILE_SHA1 = QuakTestProfile.BASE_URL.concat( "/dummy_file.sha1" );
	public static final String DUMMY_FILE_MD5 = QuakTestProfile.BASE_URL.concat( "/dummy_file.md5" );
	public static final String DUMMY_FILE_SHA256 = QuakTestProfile.BASE_URL.concat( "/dummy_file.sha256" );
	public static final String DUMMY_FILE_SHA512 = QuakTestProfile.BASE_URL.concat( "/dummy_file.sha512" );
	public static final String DUMMY_FILE_AT_LIMIT = QuakTestProfile.BASE_URL.concat( "/at_limit.foo" );
	public static final String DUMMY_FILE_ABOVE_LIMIT = QuakTestProfile.BASE_URL.concat( "/above_limit.foo" );
	public static final String NON_EXISTING_FILE = QuakTestProfile.BASE_URL.concat( "/doesnt_exist.foo" );
	public static final String WRONG_PATH = "/at/wrong/path";
	
	/**
	 * Asserts that get on folder returns OK.
	 */
	@Test
	@Order( 4 )
	void testGetFolder() {
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( QuakTestProfile.BASE_URL.concat( "/" ) ).then().statusCode( Status.OK.getStatusCode() );
	}

	/**
	 * Asserts that get for wrong path returns NOT FOUND.
	 */
	@Test
	@Order( 3 )
	void testGetWrongPath() {
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( WRONG_PATH.concat( "/" ) ).then().statusCode( Status.NOT_FOUND.getStatusCode() );
	}

	/**
	 * Asserts that get returns OK for various file types. 
	 */
	@Test
	@Order( 6 )
	void testGetFiles() {
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_FOO ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_FOO_1KB ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_FOO_1MB ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_XML ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_POM ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_SHA1 ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_MD5 ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_SHA256 ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( DUMMY_FILE_SHA512 ).then().statusCode( Status.OK.getStatusCode() );
		given().when().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).get( NON_EXISTING_FILE ).then().statusCode( Status.NOT_FOUND.getStatusCode() );
	}

	/**
	 * Asserts that upload returns OK for various files.
	 */
	@Test
	@Order( 2 )
	void testUpload() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_FOO )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( createStringDataOfSize( KB + 1 ) ).put( DUMMY_FILE_FOO_1KB )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( createStringDataOfSize( MB + 1 ) ).put( DUMMY_FILE_FOO_1MB )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_XML )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_POM )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_SHA1 )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_MD5 )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_SHA256 )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_SHA512 )
			.then().statusCode( Status.OK.getStatusCode() );
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( SUB_FOLDER_DUMMY_FILE_FOO )
			.then().statusCode( Status.OK.getStatusCode() );
		
		// Re-deploy
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( DUMMY_FILE_FOO )
			.then().statusCode( Status.OK.getStatusCode() );
		
		// Post
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).post( DUMMY_FILE_FOO )
			.then().statusCode( Status.OK.getStatusCode() );
	}

	/**
	 * Asserts that upload to wrong path returns NOT FOUND.
	 */
	@Test
	@Order( 1 )
	void testUploadWrongPath() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( WRONG_PATH.concat( DUMMY_FILE_FOO ) )
			.then().statusCode( Status.NOT_FOUND.getStatusCode() );
	}

	/**
	 * Asserts that upload with no filename returns BAD_REQUEST.
	 */
	@Test
	@Order( 5 )
	void testUploadNoFilename() {
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( DUMMY_FILE_CONTENT ).put( QuakTestProfile.BASE_URL.concat( "/" ) )
			.then().statusCode( Status.BAD_REQUEST.getStatusCode() );
	}

	/**
	 * Asserts that the upload size limit is enforced.
	 */
	@Test
	@Order( 7 )
	void testUploadLimit() {
		Long maxUploadLimit = new MemorySizeConverter().convert( confMaxUploadLimit ).asLongValue();
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( createStringDataOfSize( maxUploadLimit.intValue() ) ).put( DUMMY_FILE_AT_LIMIT )
			.then().statusCode( Status.OK.getStatusCode() );
		assertThrows( SocketException.class, () -> given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( createStringDataOfSize( maxUploadLimit.intValue() + 1 ) )
			.put( DUMMY_FILE_ABOVE_LIMIT ).andReturn() );
	}

	private String createStringDataOfSize( int size ) {
		StringBuilder sb = new StringBuilder( size );
		for ( int i = 0; i < size; i++ ) {
			sb.append( '.' );
		}
		return sb.toString();
	}
}
