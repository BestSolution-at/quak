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

@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakResourceTest {

	@ConfigProperty( name = "quarkus.http.limits.max-body-size" )
	String confMaxUploadLimit;

	private static final int KB = 1024;
	private static final int MB = 1024 * 1024;

	@Test
	@Order( 4 )
	void testGetFolder() {
		given().when().get( "/at/bestsolution/blueprint/" ).then().statusCode( Status.OK.getStatusCode() );
	}

	@Test
	@Order( 3 )
	void testGetWrongPath() {
		given().when().get( "/at/wrong/path/" ).then().statusCode( Status.NOT_FOUND.getStatusCode() );
	}

	@Test
	@Order( 6 )
	void testGetFiles() {
		given().when().get( "/at/bestsolution/blueprint/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file_1KB.foo" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file_1MB.foo" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file.xml" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file.pom" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file.sha1" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file.md5" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file.sha256" ).then().statusCode( Status.OK.getStatusCode() );
		given().when().get( "/at/bestsolution/blueprint/dummy_file.sha512" ).then().statusCode( Status.OK.getStatusCode() );
	}

	@Test
	@Order( 2 )
	void testUpload() {
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( createStringDataOfSize( KB + 1 ) ).put( "/at/bestsolution/blueprint/dummy_file_1KB.foo" ).then()
				.statusCode( Status.OK.getStatusCode() );
		given().request().body( createStringDataOfSize( MB + 1 ) ).put( "/at/bestsolution/blueprint/dummy_file_1MB.foo" ).then()
				.statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.xml" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.pom" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.sha1" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.md5" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.sha256" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.sha512" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/subFolder/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/maven-metadata.xml" ).then().statusCode( Status.OK.getStatusCode() );

		// Re-deploy
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );
	}

	@Test
	@Order( 1 )
	void testUploadWrongPath() {
		given().request().body( "dummy file" ).put( "/at/wrong/path/dummy_file.foo" ).then().statusCode( Status.NOT_FOUND.getStatusCode() );
	}

	@Test
	@Order( 5 )
	void testUploadNoFilename() {
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/" ).then().statusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
	}

	@Test
	@Order( 7 )
	void testUploadLimit() {
		Long maxUploadLimit = new MemorySizeConverter().convert( confMaxUploadLimit ).asLongValue();
		given().request().body( createStringDataOfSize( maxUploadLimit.intValue() ) ).put( "/at/bestsolution/blueprint/at_limit.foo" ).then()
				.statusCode( Status.OK.getStatusCode() );
		assertThrows( SocketException.class, () -> given().request().body( createStringDataOfSize( maxUploadLimit.intValue() + 1 ) )
				.put( "/at/bestsolution/blueprint/above_limit.foo" ).andReturn() );
	}

	private String createStringDataOfSize( int size ) {
		StringBuilder sb = new StringBuilder( size );
		for ( int i = 0; i < size; i++ ) {
			sb.append( '.' );
		}
		return sb.toString();
	}
}
