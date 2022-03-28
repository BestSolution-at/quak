package at.bestsolution.quak;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuakResourceTest {

	@Test
	void testGet() {
		given().when().get().then().statusCode( 200 );

	}

	@Test
	void testUpload() {
		given().put().then().statusCode( 200 );
	}
}
