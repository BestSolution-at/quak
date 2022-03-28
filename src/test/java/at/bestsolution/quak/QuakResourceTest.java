package at.bestsolution.quak;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.Response.Status;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(QuakTestProfile.class)
public class QuakResourceTest {

	@Test
	void testGet() {
		given().when().get().then().statusCode( Status.OK.getStatusCode() );
	}

	@Test
	void testUpload() {
		given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.txt" ).then().statusCode( Status.OK.getStatusCode() );
	}
}
