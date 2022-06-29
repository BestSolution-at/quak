/*
 * ----------------------------------------------------------------
 * Original File Name: QuakCleanUpTest.java
 * Creation Date:      29.03.2022
 * Description: Test class file of JUnit test cases for clean-up 
 * task of quak.      
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

import java.io.StringWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Random;

import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * JUnit test cases for clean-up task of quak.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusTest
@TestProfile( QuakTestProfile.class )
class QuakCleanUpTest {

	public static final int MAX_TEST_BUILD_NUMBER = 100;
	public static final String DUMMY_FILE_CONTENT = "dummy file";
	public static final String DUMMY_VERSION = "1.0.0";
	public static final String DUMMY_FILE_NAME_PREVUOUS_DEPLOY = "dummy_file_previous_deploy.foo";
	public static final String DUMMY_FILE_PREVIOUS = QuakTestProfile.BASE_URL.concat( "/" ).concat( DUMMY_VERSION ).concat( "/" ).concat( DUMMY_FILE_NAME_PREVUOUS_DEPLOY );
	public static final String DUMMY_FILE_NAME_CONCURRENT_DEPLOY = "dummy_file_concurrent_deploy.foo";
	public static final String DUMMY_FILE_CONCURRENT = QuakTestProfile.BASE_URL.concat( "/" ).concat( DUMMY_VERSION ).concat( "/" ).concat( DUMMY_FILE_NAME_CONCURRENT_DEPLOY );
	public static final String MAVEN_METADATA_FILE = QuakTestProfile.BASE_URL.concat( "/" ).concat( DUMMY_VERSION ).concat( "/maven-metadata.xml" );

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
	 * Asserts that clean-up is done correctly.
	 */
	@Test
	void testCleanUp() {
		String deployTimestamp = new SimpleDateFormat( "yyyyMMdd.HHmmss" ).format( new java.util.Date() );

		Random random = new Random();
		String deployBuildNumber = String.valueOf( random.nextInt( MAX_TEST_BUILD_NUMBER ) );

		String dummyFileNameCurrentDeploy = DUMMY_VERSION.concat( "-" ).concat( deployTimestamp ).concat( "-" ).concat( deployBuildNumber );
		String dummyFileCurrentDeploy = QuakTestProfile.BASE_URL.concat( "/" ).concat( DUMMY_VERSION ).concat( "/" )
				.concat( DUMMY_VERSION.concat( "-" ).concat( deployTimestamp ).concat( "-" ).concat( deployBuildNumber ) );
		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( dummyFileCurrentDeploy ).then().statusCode( Status.OK.getStatusCode() );
		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( QuakResourceTest.DUMMY_FILE_CONTENT )
			.put( DUMMY_FILE_CONCURRENT ).then().statusCode( Status.OK.getStatusCode() );
		
		given().auth().preemptive().basic( QuakTestProfile.GOOD_USERNAME, QuakTestProfile.GOOD_PASSWORD ).request().body( getTestMavenMetadataXmlFileContent( deployTimestamp, deployBuildNumber ) )
			.put( MAVEN_METADATA_FILE ).then().statusCode( Status.OK.getStatusCode() );
		
		// File from previous deploy must be removed.
		Awaitility.await().until( () -> Files.notExists( QuakResource.REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( DUMMY_FILE_NAME_PREVUOUS_DEPLOY ) ) );
		// File from current deploy must stay available.
		Awaitility.await().until( () -> Files.exists( QuakResource.REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( dummyFileNameCurrentDeploy ) ) );
		// File which is not from current deploy but uploaded concurrently (within same seconds) must stay available.
		Awaitility.await().until( () -> Files.exists( QuakResource.REPOSITORIES_PATH.resolve( QuakTestProfile.STORAGE_PATH ).resolve( DUMMY_VERSION ).resolve( DUMMY_FILE_NAME_CONCURRENT_DEPLOY ) ) );
	}

	private String getTestMavenMetadataXmlFileContent(String deployTimestamp, String deployBuildNumber) {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} 
		catch ( ParserConfigurationException e ) {
			System.out.println( e );
		}
		Document document = builder.newDocument();
		Element root = document.createElement( "metadata" );
		document.appendChild( root );

		Element artifactId = document.createElement( "artifactId" );
		artifactId.setTextContent( QuakTestProfile.REPOSITORY_NAME );
		root.appendChild( artifactId );

		Element version = document.createElement( "version" );
		version.setTextContent( DUMMY_VERSION );
		root.appendChild( version );

		Element versioning = document.createElement( "versioning" );
		root.appendChild( versioning );

		Element snapshot = document.createElement( "snapshot" );
		versioning.appendChild( snapshot );

		Element timestamp = document.createElement( "timestamp" );
		timestamp.setTextContent( deployTimestamp );
		snapshot.appendChild( timestamp );

		Element buildNumber = document.createElement( "buildNumber" );
		buildNumber.setTextContent( deployBuildNumber );
		snapshot.appendChild( buildNumber );

		Transformer transformer;
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			StringWriter writer = new StringWriter();
			transformer.transform( new DOMSource( document ), new StreamResult( writer ) );
			return writer.getBuffer().toString();
		} 
		catch ( TransformerException e ) {
			System.out.println( e );
		}

		return null;
	}
}