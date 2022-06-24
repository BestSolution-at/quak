/*
 * ----------------------------------------------------------------
 * Original File Name: QuakCleanUp.java
 * Creation Date:      24.06.2022
 * Description: Class file of task for cleaning up storage path of 
 * the repository which has new maven-metadadata.xml uploaded.       
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Runnable class for cleaning up storage path of the repository which has new maven-metadadata.xml uploaded.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakCleanUp extends Thread {
	
	private static final Logger LOG = Logger.getLogger(QuakCleanUp.class);
	private QuakRepository repository;
	private File metadataXml;

	/**
	 * Constructs a task for cleaning up storage path of the repository which has new maven-metadadata.xml uploaded.
	 * @param repository to be cleaned up.
	 * @param metadataXml maven-metadata.xml file which is newly uploaded.
	 */
	public QuakCleanUp( QuakRepository repository, File metadataXml ) {
		this.repository = repository;
		this.metadataXml = metadataXml;
	}

	/**
	 * Task for cleaning up storage path of the repository which has new maven-metadadata.xml uploaded.
	 */
	@Override
	public void run() {
		try {
			// Read metadata.xml file
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			// Process XML securely, avoid attacks like XML External Entities
			builderFactory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
		    Document document = builder.parse( metadataXml.getAbsolutePath() );
		    document.getDocumentElement().normalize();
		    XPath xPath = XPathFactory.newInstance().newXPath();
		    
		    // Extract metadata variables for repository
		    String version = xPath.compile( "/metadata/version" ).evaluate( document );
		    String buildNumber = xPath.compile( "/metadata/versioning/snapshot/buildNumber" ).evaluate( document );
		    String filenamePrefix = repository.getName().concat( "-" ).concat( version.split( "-" )[0] );

		    // Remove or move all deploys which are done previously, and not of the current build.
			Path repositoryStoragePath = QuakResource.REPOSITORIES_PATH.resolve( repository.getStoragePath() ).resolve( version );
			Stream<java.nio.file.Path> filePaths = Files.list( repositoryStoragePath );
			filePaths.filter( p -> p.getFileName().toString().startsWith( filenamePrefix ) && !p.getFileName().toString().contains( "-".concat( buildNumber ).concat( "." ) ) ).forEach( p -> moveFile( p.toFile() ) );
			filePaths.close();
			
		} 
		catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e ) {
			LOG.error( "Exception while CleanUp!", e );
		} 
	}
	
	private void moveFile( File file ) {
		LOG.infof( "Artifact (%s) is moved. Repository: %s", file.getName(), repository.getName() );
	}
	
	private void removeFile( File file ) {
		LOG.infof( "Artifact (%s) is removed. Repository: %s", file.getName(), repository.getName() );
	}
}
