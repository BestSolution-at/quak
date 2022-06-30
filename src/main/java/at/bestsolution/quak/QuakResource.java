/*
 * ----------------------------------------------------------------
 * Original File Name: QuakResource.java
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
import java.nio.file.Paths;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.event.Observes;
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

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import at.bestsolution.quak.QuakConfiguration.Repository;
import io.quarkus.qute.Template;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;

/**
 * Represents a quak instance.
 */
@Path("/{path: .*}")
public class QuakResource {
	
	@Inject
	QuakSecurityValidator securityValidator;
	
	@Inject
	QuakConfigurationController configurationController;
	
	@Context
	UriInfo urlInfo;
	
	@Inject
    Template directory;
	
	@Inject
	ManagedExecutor executor;
	
	private static final Logger LOG = Logger.getLogger(QuakResource.class);
	private static final java.nio.file.Path REPOSITORIES_PATH = Paths.get( "repositories/" ); 
	private static final String FILE_SIZE_PATTERN = "#,###.0";
	
    /**
     * Startup event of quak for checking configurations.
     * @param event Startup event.
     */
    void onStart(@Observes StartupEvent event) {               
    	LOG.info("quak is starting...");
    	Optional<Boolean> oidcEnabled = ConfigProvider.getConfig().getOptionalValue("quarkus.oidc.enabled", Boolean.class);
    	Optional<Boolean> userInfoRequired = ConfigProvider.getConfig().getOptionalValue("quarkus.oidc.authentication.user-info-required", Boolean.class);
    	if ( oidcEnabled.orElse(true) && !userInfoRequired.orElse( false ) ) {
    		LOG.error("If OpenID Connect is used, 'quarkus.oidc.authentication.user-info-required' must be set to true in application.properties.");
    		Quarkus.asyncExit( 1 );
    	}
    	Optional<Repository> wrongPathRepo = configurationController.getRepositories().stream().filter( r -> r.storagePath().isAbsolute() ).findFirst();
    	if ( wrongPathRepo.isPresent() ) {
    		LOG.errorf( "Repository '%s' has an absolute storage path. This is not allowed. Repository directories will reside below the mandatory '$QUAK_SERVICE_USER/repositories' directory.", wrongPathRepo.get().name() );
    		Quarkus.asyncExit( 1 );
    	}
    	executor = ManagedExecutor.builder().maxAsync( configurationController.getConfiguration().maxConcurrentCleanUpTasks() ).build();
    }
	
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
		String relativeURL = url.substring( length );
		
		if ( relativeURL.length() > 0 && relativeURL.charAt( 0 ) == '/' ) {
			relativeURL = relativeURL.substring( 1 );
		}
		
		if ( relativeURL.isEmpty() ) {
			return REPOSITORIES_PATH.resolve( repository.getStoragePath() );
		}
		
		return REPOSITORIES_PATH.resolve( repository.getStoragePath() ).resolve( relativeURL ).normalize();
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
		
		// If maven-metadata-xml is uploaded, asynchronous CleanUp task will be started.
		if ( file.getFileName().toString().equals( "maven-metadata.xml" ) ) {
			QuakCleanUp cleanUpTask = new QuakCleanUp( file.getParent(), configurationController.getRepository( path ).cleanUp().hardDelete() );
			executor.submit( cleanUpTask );
		}
		
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