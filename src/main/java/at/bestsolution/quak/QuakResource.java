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

import at.bestsolution.quak.QuakConfiguration.Repository;
import io.quarkus.qute.Template;

/**
 * Represents a quak instance.
 */
@Path("/{path: .*}")
public class QuakResource {

	@Inject
	QuakConfiguration configuration;
	
	@Context
	private UriInfo urlInfo;
	
	@Inject
    Template directory;
	
	/**
	 * @param p path of the file to be checked.
	 * @return String text displaying last modify date.
	 */
	private static String getLastModified(java.nio.file.Path p) {
		try {
			FileTime time = Files.getLastModifiedTime(p);
			return DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH).format(LocalDateTime.ofInstant(time.toInstant(),ZoneId.systemDefault()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Unknown";
		}
	}
	
	/**
	 * Searches for a repository configuration which has a base URL matching with beginning of the path.
	 * @param path URL path of the upload request.
	 * @return Repository a repository configuration or null in case of no match.
	 */
	private Repository findRepository(String path) {
		return configuration.repositories().stream().filter( r -> path.startsWith(r.baseUrl())).findFirst().orElse(null);
	}
	
	/**
	 * Extracts the path for the artifact file.
	 * @param repository repository configuration of the artifact.
	 * @param url current URL path.
	 * @return path for file or null if no match.
	 */
	private java.nio.file.Path resolveFileSystemPath(Repository repository, String url) {
		int length = repository.baseUrl().length();
		String relative = url.substring(length);
		
		if( relative.length() > 0 && relative.charAt(0) == '/' ) {
			relative = relative.substring(1);
		}
		
		if( relative.isEmpty() ) {
			return repository.storagePath();
		}
		
		java.nio.file.Path path = repository.storagePath().resolve(relative).normalize();

		if( path.startsWith(repository.storagePath()) ) {
			return path;
		}
		
		return null;
	}
	
	/**
	 * A directory item listing GET method.
	 * @return Response containing files related to the repository of the URL.
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_HTML })
	public Response get() {
		String path = urlInfo.getPath();
		
		Repository repository = findRepository(path);
		
		if( repository == null ) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		java.nio.file.Path file = resolveFileSystemPath(repository, path);
		
		if( file == null ) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if( Files.isDirectory(file) ) {
			List<DirectoryItem> items = new ArrayList<>();
			if( ! repository.storagePath().equals(file) ) {
				items.add(new DirectoryItem("drive_folder_upload","..","..","-",getLastModified(file)));		
			}
			
			try {
				items.addAll(Files.list(file).sorted().map( p -> {
					if( Files.isDirectory(p) ) {
						return new DirectoryItem("folder",p.getFileName().toString(), p.getFileName().toString()+"/", "-", getLastModified(p));
					} else {
						long size;
						try {
							size = Files.size(p);
						} catch (IOException e) {
							size = -1;
						}
						
						String formatted = "-";
						if( size > 1024 ) {
							if( size > 1024 * 1024 ) {
								if( size > 1024 * 1024 * 1024 ) {
									formatted = new DecimalFormat("#,###.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(size / (1024.0 * 1024.0 * 1024)) + " GB";
								} else {
									formatted = new DecimalFormat("#,###.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(size / (1024.0 * 1024.0)) + " MB";	
								}
								
							} else {
								formatted = new DecimalFormat("#,###.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(size / 1024.0) + " KB";
							}
						} else {
							formatted = size + "";
						}
						
						return new DirectoryItem("description", p.getFileName().toString(), p.getFileName().toString(), formatted, getLastModified(p));
					}
				}).collect(Collectors.toList()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return Response.ok(directory.data("items", items).render(),MediaType.TEXT_HTML).build();
		}
		
		if (!Files.exists(file) && !Files.isRegularFile(file)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		if (path.endsWith(".xml") || path.endsWith(".pom")) {
			return Response.ok(file.toFile(), MediaType.APPLICATION_XML).build();
		} else if( path.endsWith(".sha1") || path.endsWith(".md5") || path.endsWith("sha256") || path.endsWith("sha512") ) {
			return Response.ok(file.toFile(), MediaType.TEXT_PLAIN).build();
		}
		
		return Response.ok(file.toFile(), MediaType.APPLICATION_OCTET_STREAM).build();
	}

	/** 
	 * Uploads an artifact for a defined repository to it's pre-defined path.
	 * @param messageBody an artifact file to be uploaded.
	 * @return Response representing status of the upload.
	 */
	@PUT
	@POST
	public Response upload(InputStream messageBody) {
		String path = urlInfo.getPath();
		Repository repository = findRepository(path);
		
		if( repository == null ) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		java.nio.file.Path file = resolveFileSystemPath(repository, path);
		
		if( file == null ) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} 
		else if (urlInfo.getPath().endsWith( "/" )) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		try {
			Files.createDirectories(file.getParent());
			if( Files.exists(file) ) {
				if( ! repository.allowRedeploy() && ! file.getFileName().toString().startsWith("maven-metadata.xml") ) {
					return Response.status(Status.METHOD_NOT_ALLOWED).entity("Redeployment not allowed").build();
				}
				Files.copy(messageBody, file, StandardCopyOption.REPLACE_EXISTING);	
			} else {
				Files.copy(messageBody, file);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().build();
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
	
	/*

	private static Artifact fromPath(String path) {
		String[] parts = path.split("/");

		String file = parts[parts.length - 1];

		String versionOrArtifact = parts[parts.length - 2];

		Artifact artifact = new Artifact();

		if (Character.isDigit(versionOrArtifact.charAt(0))) {
			artifact.version = versionOrArtifact;
			artifact.artifactId = parts[parts.length - 3];
			artifact.group = getGroup(parts, 3);
		} else {
			artifact.artifactId = parts[parts.length - 2];
			artifact.group = getGroup(parts, 2);
		}

		return artifact;
	}

	private static String getGroup(String[] parts, int cutOff) {
		StringBuilder b = new StringBuilder();

		for (int i = 0; i < parts.length - cutOff; i++) {
			if (b.length() > 0) {
				b.append(".");
			}
			b.append(parts[i]);
		}

		return b.toString();
	}

	private static class Artifact {
		private String group;
		private String artifactId;
		private String version;
		private String classifier;

		@Override
		public String toString() {
			return "Artifact [group=" + group + ", artifactId=" + artifactId + ", version=" + version + ", classifier="
					+ classifier + "]";
		}

	}*/

}