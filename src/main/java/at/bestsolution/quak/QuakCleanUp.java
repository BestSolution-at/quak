/*
 * ----------------------------------------------------------------
 * Original File Name: QuakCleanUp.java
 * Creation Date:      24.06.2022
 * Description: Class file of task for cleaning up storage path of 
 * the repository which has maven-metadadata.xml uploaded.       
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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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
 * Runnable class for cleaning up storage path of the repository which has
 * maven-metadadata.xml uploaded.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakCleanUp implements Runnable {

	private static final Logger LOG = Logger.getLogger( QuakCleanUp.class );
	
	/**
	 * Maven metadata XML file name.
	 */
	private static final String MAVEN_METADATA_XML_FILE_NAME = "maven-metadata.xml";
	
	/**
	 * Path to be cleaned up.
	 */
	private Path path;
	
	/**
	 * True if clean up with hard delete, false if soft delete.
	 */
	private boolean hardDelete = false;
	
	/**
	 * Directory name for keeping files to be deleted.
	 */
	private static final String TRASH_DIRECTORY_NAME = ".filesToBeDeleted"; 

	/**
	 * Constructs a task for cleaning up a storage path of the repository which
	 * has maven-metadadata.xml uploaded.
	 * 
	 * @param path
	 *            path to be cleaned up.
	 * @param hardDelete
	 * 			  true if clean up with hard delete, false if soft delete.
	 */
	public QuakCleanUp( Path path, boolean hardDelete ) {
		this.path = path;
		this.hardDelete = hardDelete;
	}

	/**
	 * Task for cleaning up storage path of the repository which has
	 * maven-metadadata.xml uploaded.
	 */
	public void run() {
		try {
			Path metadataXml = path.resolve( MAVEN_METADATA_XML_FILE_NAME );
			// Read metadata.xml file
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			// Disable DOCTYPE declaration for vulnerabilities
			builderFactory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
			// Process XML securely, avoid attacks like XML External Entities
			builderFactory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse( metadataXml.toString() );
			document.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();

			// Extract metadata variables
			String artifactId = xPath.compile( "/metadata/artifactId" ).evaluate( document );
			String version = xPath.compile( "/metadata/version" ).evaluate( document );
			String versionNo = version.split( "-" )[0];
			String timestamp = xPath.compile( "/metadata/versioning/snapshot/timestamp" ).evaluate( document );
			String buildNumber = xPath.compile( "/metadata/versioning/snapshot/buildNumber" ).evaluate( document );
			Long metadataTimestamp = metadataXml.toFile().lastModified();

			// If version is empty it is root metadata-xml, no clean-up required.
			if ( !version.isEmpty() && !timestamp.isEmpty() ) {
				String buildFilesPrefix = String.format( "%s-%s-%s-%s", artifactId, versionNo, timestamp, buildNumber );
				Stream<Path> filePaths = Files.list( path );
				try {
					filePaths.filter( fp -> !fp.toFile().getName().startsWith( MAVEN_METADATA_XML_FILE_NAME )
											&& !fp.toFile().getName().startsWith( buildFilesPrefix )
											&& fp.toFile().lastModified() < metadataTimestamp )
							.forEach( fp -> {
								if ( hardDelete ) {
									deleteFile( fp.toFile() );
								} 
								else {
									moveFile( fp.toFile() );
								}
							} );
				} 
				finally {
					filePaths.close();
				}
			}
		} 
		catch ( ParserConfigurationException | SAXException | IOException | XPathExpressionException e ) {
			LOG.error( "Exception while CleanUp!", e );
		}
	}

	private void moveFile( File file ) {
		try {
			 if ( file.isFile() ) {
				 Files.createDirectories( file.toPath().getParent().resolve( TRASH_DIRECTORY_NAME ) );
				 Files.move( file.toPath(), file.toPath().getParent().resolve( TRASH_DIRECTORY_NAME ).resolve( file.getName() ), REPLACE_EXISTING );
				 LOG.infof( "Artifact (%s) is moved.", file.getName() );
             }
		} 
		catch ( IOException e ) {
			LOG.error( "Exception while moving the file!", e );
		}
	}

	private void deleteFile( File file ) {
		try {
			if ( file.isFile() ) {
				Files.deleteIfExists( file.toPath() );
				LOG.infof( "Artifact (%s) is deleted.", file.getName() );
			}
		} 
		catch ( IOException e ) {
			LOG.error( "Exception while deleting the file!", e );
		}
	}
}
