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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.qute.Template;

@Path("/{path: .*}")
public class QuakResource {

	@ConfigProperty(name = "quak.storage.path", defaultValue = "/tmp/quak-repo")
	private java.nio.file.Path storagepath;
	
	@ConfigProperty(name = "quak.allowRedeploy", defaultValue = "false")
	private boolean allowRedeploy;

	@Context
	private UriInfo urlInfo;
	
	@Inject
    Template directory;
	
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

	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_HTML })
	public Response get() {
		String path = urlInfo.getPath();
		
		java.nio.file.Path file = storagepath.resolve(path.substring(1));
		
		if( Files.isDirectory(file) ) {
			List<DirectoryItem> items = new ArrayList<>();
			items.add(new DirectoryItem("drive_folder_upload","..",Paths.get(path).getParent().toString(),"-",getLastModified(file)));
			
			try {
				items.addAll(Files.list(file).sorted().map( p -> {
					if( Files.isDirectory(p) ) {
						return new DirectoryItem("folder",p.getFileName().toString(), path + "/" + p.getFileName(), "-", getLastModified(p));
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
									formatted = new DecimalFormat("#,##.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(size / (1024.0 * 1024.0 * 1024)) + " GB";
								} else {
									formatted = new DecimalFormat("#,##.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(size / (1024.0 * 1024.0)) + " MB";	
								}
								
							} else {
								formatted = new DecimalFormat("#,##.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(size / 1024.0) + " KB";
							}
						} else {
							formatted = size + "";
						}
						
						return new DirectoryItem("description", p.getFileName().toString(), path + "/" + p.getFileName(), formatted, getLastModified(p));
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
		
		if (path.endsWith(".xml")) {
			return Response.ok(file.toFile(), MediaType.APPLICATION_XML).build();
		} else if( path.endsWith(".sha1") || path.endsWith(".md5") ) {
			return Response.ok(file.toFile(), MediaType.TEXT_PLAIN).build();
		}
		
		return Response.ok(file.toFile(), MediaType.APPLICATION_OCTET_STREAM).build();
	}

	@PUT
	@POST
	public Response upload(InputStream messageBody) {
		java.nio.file.Path file = storagepath.resolve(urlInfo.getPath().substring(1));
		
		try {
			Files.createDirectories(file.getParent());
			if( Files.exists(file) ) {
				if( ! allowRedeploy && ! file.getFileName().toString().startsWith("maven-metadata.xml") ) {
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