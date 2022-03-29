package at.bestsolution.quak;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile( QuakTestProfile.class )
@TestMethodOrder( OrderAnnotation.class )
public class QuakResourceTest {

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
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.xml" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.pom" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.sha1" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.md5" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.sha256" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.sha512" ).then().statusCode( Status.OK.getStatusCode() );
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/subFolder/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );

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
}
