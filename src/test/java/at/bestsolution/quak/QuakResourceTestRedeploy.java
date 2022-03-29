package at.bestsolution.quak;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;

@QuarkusTest
@TestProfile( QuakTestProfileRedeployNotAllowed.class )
public class QuakResourceTestRedeploy {

	@Test
	void testUploadRedeploy() {
		Response response = given().when().get( "/at/bestsolution/blueprint/" ).andReturn();
		if( response.getStatusCode() == Status.OK.getStatusCode() ) {
			given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then()
					.statusCode( Status.METHOD_NOT_ALLOWED.getStatusCode() );
		} else if ( response.getStatusCode() == Status.NOT_FOUND.getStatusCode() ) {
			given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then().statusCode( Status.OK.getStatusCode() );
			given().request().body( "dummy file" ).put( "/at/bestsolution/blueprint/dummy_file.foo" ).then()
					.statusCode( Status.METHOD_NOT_ALLOWED.getStatusCode() );
		}
	}
}