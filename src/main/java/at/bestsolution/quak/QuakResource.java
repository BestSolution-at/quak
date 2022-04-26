/*
 * ----------------------------------------------------------------
 * Original File Name: Faculty.java
 * Creation Date:      26.01.2022
 * Description: Class file of quak instance.       
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import io.quarkus.qute.Template;

/**
 * Represents a quak instance.
 */
@Path("/{path: .*}")
public class QuakResource {
	
	@Inject
	QuakSecurityValidator securityValidator;
	
	@Context
	private UriInfo urlInfo;
	
	@Inject
    Template directory;
	
	private static final Logger LOG = Logger.getLogger(QuakResource.class);
	private static final String FILE_SIZE_PATTERN = "#,###.0";
	
	/**
	 * @param p path of the file to be checked.
	 * @return String text displaying last modify date.
	 */
	private static String getLastModified(java.nio.file.Path p) {
		try {
			FileTime time = Files.getLastModifiedTime( p );
			return DateTimeFormatter.ofPattern( "dd-MMM-yyyy HH:mm", Locale.ENGLISH ).format( LocalDateTime.ofInstant( time.toInstant(), ZoneId.systemDefault() ) );
		} 
		catch ( IOException e ) {
			LOG.error( "Exception while reading file modification time!", e );
			return "Unknown";
		}
	}
	
	/**
	 * Extracts the path for the artifact file.
	 * @param repository repository configuration of the artifact.
	 * @param url current URL path.
	 * @return path for file or null if no match.
	 */
	private java.nio.file.Path resolveFileSystemPath(QuakRepository repository, String url) {
		int length = repository.getBaseUrl().length();
		String relative = url.substring( length );
		
		if ( relative.length() > 0 && relative.charAt( 0 ) == '/' ) {
			relative = relative.substring( 1 );
		}
		
		if ( relative.isEmpty() ) {
			return repository.getStoragePath();
		}
		
		return repository.getStoragePath().resolve( relative ).normalize();
	}
	
	private String getFormattedFileSize(long size) {
		String formatted;
		if ( size > 1024 ) {
			if ( size > 1024 * 1024 ) {
				if ( size > 1024 * 1024 * 1024 ) {
					formatted = new DecimalFormat(FILE_SIZE_PATTERN, DecimalFormatSymbols.getInstance( Locale.ENGLISH ) )
							.format( size / ( 1024.0 * 1024.0 * 1024 ) ) + " GB";
				} 
				else {
					formatted = new DecimalFormat(FILE_SIZE_PATTERN, DecimalFormatSymbols.getInstance( Locale.ENGLISH ) )
							.format( size / ( 1024.0 * 1024.0 ) ) + " MB";	
				}
			} 
			else {
				formatted = new DecimalFormat(FILE_SIZE_PATTERN, DecimalFormatSymbols.getInstance( Locale.ENGLISH ) )
							.format( size / 1024.0 ) + " KB";
			}
		} 
		else {
			formatted = size + "";
		}
		
		return formatted;
	}
	
	private Response getResponseForFileType(java.nio.file.Path filePath) {
		if ( !Files.exists( filePath ) && !Files.isRegularFile( filePath ) ) {
			LOG.errorf( "File (%s) does not exist or could not be read.", filePath.toAbsolutePath() );
			return Response.status( Response.Status.NOT_FOUND ).build();
		}
		else if ( filePath.toString().endsWith( ".xml" ) || filePath.toString().endsWith( ".pom" ) ) {
			return Response.ok( filePath.toFile(), MediaType.APPLICATION_XML ).build();
		} 
		else if ( filePath.toString().endsWith( ".sha1" ) || filePath.toString().endsWith( ".md5" ) 
				|| filePath.toString().endsWith( "sha256" ) || filePath.toString().endsWith( "sha512" ) ) {
			return Response.ok( filePath.toFile(), MediaType.TEXT_PLAIN ).build();
		} 
		else {
			return Response.ok( filePath.toFile(), MediaType.APPLICATION_OCTET_STREAM ).build();
		}
	}
	
	/**
	 * A directory item listing GET method.
	 * @return Response containing files related to the repository of the URL.
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_HTML })
	public Response get() {
		LOG.debugf( "Get request received with: %s", urlInfo.getRequestUri() );
		String path = urlInfo.getPath();
		
		QuakRepository repository = securityValidator.getQuakRepository( path );
		if ( repository == null ) {
			LOG.errorf( "No repository found for path: %s", path );
			return Response.status( Status.NOT_FOUND ).build();
		}
		
		java.nio.file.Path file = resolveFileSystemPath( repository, path );
		
		if ( file == null ) {
			LOG.errorf( "Can not resolve path: %s", path );
			return Response.status( Response.Status.NOT_FOUND ).build();
		}
		
		if ( Files.isDirectory( file ) ) {
			List<DirectoryItem> items = new ArrayList<>();
			if ( !repository.getStoragePath().equals( file ) ) {
				items.add( new DirectoryItem( "drive_folder_upload", "..", "..", "-", getLastModified( file ) ) );		
			}
			try {
				Stream<java.nio.file.Path> paths = Files.list( file );
				try {
					items.addAll( paths.sorted().map( p -> {
						if ( Files.isDirectory( p ) ) {
							return new DirectoryItem( "folder", p.getFileName().toString(), p.getFileName().toString() + "/", "-", getLastModified( p ) );
						} 
						else {
							long size;
							try {
								size = Files.size( p );
							} 
							catch ( IOException e ) {
								size = -1;
							}
							return new DirectoryItem("description", p.getFileName().toString(), p.getFileName().toString(), getFormattedFileSize( size ), getLastModified( p ));
						}
					} ).collect( Collectors.toList() ) );
				}
				finally {
					paths.close();
				}
			} 
			catch ( IOException e ) {
				LOG.error( "Exception while reading directories!", e );
			}
			
			return Response.ok( directory.data( "items", items ).render(), MediaType.TEXT_HTML ).build();
		}
			
		return getResponseForFileType( file );
	}

	/** 
	 * Uploads an artifact for a defined repository to it's pre-defined path.
	 * @param messageBody an artifact file to be uploaded.
	 * @return Response representing status of the upload.
	 */
	@PUT
	@POST
	public Response upload(InputStream messageBody) {
		LOG.infof( "Upload request received with: %s", urlInfo.getRequestUri() );
		String path = urlInfo.getPath();
		
		QuakRepository repository = securityValidator.getQuakRepository( path );
		if ( repository == null ) {
			LOG.errorf( "No repository found for path: %s", path );
			return Response.status( Status.NOT_FOUND ).build();
		}
		
		java.nio.file.Path file = resolveFileSystemPath( repository, path );
		
		if ( file == null ) {
			LOG.errorf( "Can not resolve path: %s", path );
			return Response.status( Response.Status.NOT_FOUND ).build();
		} 
		else if (urlInfo.getPath().endsWith( "/" )) {
			return Response.status( Response.Status.BAD_REQUEST ).build();
		}
		
		try {
			Files.createDirectories( file.getParent() );
			if ( Files.exists( file ) ) {
				if ( !repository.isAllowRedeploy() && !file.getFileName().toString().startsWith( "maven-metadata.xml" ) ) {
					LOG.infof( "Redeploy for %s is not allowed. Artifact (%s) is NOT uploaded.", repository.getName(), file.getFileName() );
					return Response.status( Status.METHOD_NOT_ALLOWED ).entity( "Redeployment not allowed" ).build();
				}
				Files.copy( messageBody, file, StandardCopyOption.REPLACE_EXISTING );	
			} 
			else {
				Files.copy( messageBody, file );
			}
			
		} 
		catch (IOException e) {
			LOG.error( "Exception while creating directories!", e );
			return Response.serverError().build();
		}
		
		LOG.infof( "Artifact (%s) is successfully uploaded for repository: %s", file.getFileName(), repository.getName() );
		return Response.ok().build();
	}
	
	/**
	 * Represents a directory item to be listed.
	 */
	public static class DirectoryItem {
		public String name;
		public String path;
		public String lastModified;
		public String fileSize;
		public String icon;
		
		public DirectoryItem(String icon, String name, String path, String fileSize, String lastModified) {
			this.name = name;
			this.path = path;
			this.fileSize = fileSize;
			this.lastModified = lastModified;
			this.icon = icon;
		}
	}
}