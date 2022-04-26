/*
 * ----------------------------------------------------------------
 * Original File Name: QuakRepository.java
 * Creation Date:      25.04.2022
 * Description:  Represents a quak repository.
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a quak repository.
 * 
 * @author kerim.yeniduenya@bestsolution.com
 */
public class QuakRepository {
	
	private String name;
	private String baseUrl;
	private boolean isPrivate;
	private boolean allowRedeploy;
	private Path storagePath;
	private List<QuakUserPermission> userPermissions;
	
	/**
	 * Constructs a quak repository instance. It has empty quak user permission list initialized. It must be filled with user permissions if
	 * authorized access to be given.
	 * @param name name of repository. It is a unique identifier.
	 * @param baseUrl base URL of repository.
	 * @param isPrivate true if it is a private repository, false if not. Private repositories are read and write protected. Public repositories are only write protected.
	 * @param allowRedeploy true if redeploys are allowed, false if not.
	 * @param storagePath storage path where the files are stored.
	 */
	public QuakRepository( String name, String baseUrl, boolean isPrivate, boolean allowRedeploy, Path storagePath ) {
		this.name = name;
		this.baseUrl = baseUrl;
		this.isPrivate = isPrivate;
		this.allowRedeploy = allowRedeploy;
		this.storagePath = storagePath;
		userPermissions = new ArrayList<>();
	}
	
	/**
	 * @return name of repository. It is a unique identifier.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name name of repository. It is a unique identifier.
	 */
	public void setName( String name ) {
		this.name = name;
	}
	
	/**
	 * @return base URL of repository.
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
	
	/**
	 * @param baseUrl base URL of repository.
	 */
	public void setBaseUrl( String baseUrl ) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * @return true if it is a private repository, false if not. Private repositories are read and write protected. Public repositories are only write protected.
	 */
	public boolean isPrivate() {
		return isPrivate;
	}
	
	/**
	 * @param isPrivate true if it is a private repository, false if not. Private repositories are read and write protected. Public repositories are only write protected.
	 */
	public void setPrivate( boolean isPrivate ) {
		this.isPrivate = isPrivate;
	}
	
	/**
	 * @return true if re-deploy is allowed, false if not.
	 */
	public boolean isAllowRedeploy() {
		return allowRedeploy;
	}
	
	/**
	 * @param allowRedeploy true if re-deploy is allowed, false if not.
	 */
	public void setAllowRedeploy( boolean allowRedeploy ) {
		this.allowRedeploy = allowRedeploy;
	}
	
	/**
	 * @return storage path of repository.
	 */
	public Path getStoragePath() {
		return storagePath;
	}
	
	/**
	 * @param storagePath storage path of repository.
	 */
	public void setStoragePath( Path storagePath ) {
		this.storagePath = storagePath;
	}
	
	/**
	 * @return list of quak user permissions which defines access to this repository.
	 */
	public List<QuakUserPermission> getUserPermissions() {
		return userPermissions;
	}
	
	/**
	 * @param userPermissions list of quak user permissions which defines access to this repository. 
	 */
	public void setUserPermissions( List<QuakUserPermission> userPermissions ) {
		this.userPermissions = userPermissions;
	}
}