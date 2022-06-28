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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
 * Runnable class for cleaning up storage path of the repository which has new
 * maven-metadadata.xml uploaded.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakCleanUp extends Thread {

	private static final Logger LOG = Logger.getLogger( QuakCleanUp.class );
	
	/**
	 * Repository to be cleaned up.
	 */
	private QuakRepository repository;
	
	/**
	 * Maven metadata XML file which has newly uploaded.
	 */
	private File metadataXml;
	
	/**
	 * True if clean up with hard delete, false if soft delete.
	 */
	private boolean hardDelete = false;
	
	/**
	 * Directory name for keeping files to be deleted.
	 */
	private static final String TRASH_DIRECTORY_NAME = ".filesToBeDeleted"; 

	/**
	 * Constructs a task for cleaning up storage path of the repository which
	 * has new maven-metadadata.xml uploaded.
	 * 
	 * @param repository
	 *            to be cleaned up.
	 * @param metadataXml
	 *            maven-metadata.xml file which is newly uploaded.
	 * @param hardDelete
	 * 			  true if clean up with hard delete, false if soft delete.
	 */
	public QuakCleanUp( QuakRepository repository, File metadataXml, boolean hardDelete ) {
		this.repository = repository;
		this.metadataXml = metadataXml;
		this.hardDelete = hardDelete;
	}

	/**
	 * Task for cleaning up storage path of the repository which has new
	 * maven-metadadata.xml uploaded.
	 */
	@Override
	public void run() {
		try {
			// Read metadata.xml file
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			// Disable DOCTYPE declaration for vulnerabilities
			builderFactory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
			// Process XML securely, avoid attacks like XML External Entities
			builderFactory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse( metadataXml.getAbsolutePath() );
			document.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();

			// Extract metadata variables
			String version = xPath.compile( "/metadata/version" ).evaluate( document );
			String versionNo = version.split( "-" )[0];
			String timestamp = xPath.compile( "/metadata/versioning/snapshot/timestamp" ).evaluate( document );
			String buildNumber = xPath.compile( "/metadata/versioning/snapshot/buildNumber" ).evaluate( document );
			String previousArtifactsPattern = "(^((?!(".concat( versionNo ).concat( "-" ).concat( timestamp ).concat( "-" ).concat( buildNumber ).concat( ")|(maven-metadata.xml)).)*$)" );

			// If version is empty it is root metadata-xml, no clean-up required.
			if ( !version.isEmpty() && !timestamp.isEmpty()) {
				Long timestampValue = new SimpleDateFormat("yyyyMMdd.HHmmss").parse( timestamp ).getTime();
				Path repositoryStoragePath = QuakResource.REPOSITORIES_PATH.resolve( repository.getStoragePath() ).resolve( version );
				// list all files matching the previousArtifactsPattern or metadata
				Stream<Path> filePaths = Files.find( repositoryStoragePath, 1, (path, basicFileAttributes) -> path.toFile().getName().matches( previousArtifactsPattern ) && path.toFile().isFile() );
				try {
					// Delete or move all deploy files which are done previously, and not of the current build.
					if ( hardDelete ) {
						filePaths.filter( p -> p.toFile().lastModified() > timestampValue ).forEach( p -> deleteFile( p.toFile() ) );
					} 
					else {
						filePaths.filter( p -> p.toFile().lastModified() > timestampValue ).forEach( p -> moveFile( p.toFile() ) );
					}
				} 
				finally {
					filePaths.close();
				}
			}
		}
		catch ( ParserConfigurationException | SAXException | IOException | XPathExpressionException | ParseException e ) {
			LOG.error( "Exception while CleanUp!", e );
		}
	}

	private void moveFile( File file ) {
		try {
			 if ( file.isFile() ) {
				 Files.createDirectories( file.toPath().getParent().resolve( TRASH_DIRECTORY_NAME ) );
				 Files.move( file.toPath(), file.toPath().getParent().resolve( TRASH_DIRECTORY_NAME ).resolve( file.getName() ), REPLACE_EXISTING );
				 LOG.infof( "Artifact (%s) is moved. Repository: %s", file.getName(), repository.getName() );
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
				LOG.infof( "Artifact (%s) is deleted. Repository: %s", file.getName(), repository.getName() );
			}
		} 
		catch ( IOException e ) {
			LOG.error( "Exception while deleting the file!", e );
		}
	}
}
