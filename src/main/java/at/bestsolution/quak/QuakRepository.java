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
	 * Constructs a quak repository instance.
	 * @param name of repository.
	 * @param baseUrl of repository.
	 * @param isPrivate true if it is a private repository, false if not.
	 * @param allowRedeploy true if redeploys are allowed, false if not.
	 * @param storagePath path where the files are stored.
	 */
	public QuakRepository( String name, String baseUrl, boolean isPrivate, boolean allowRedeploy, Path storagePath ) {
		this.name = name;
		this.baseUrl = baseUrl;
		this.isPrivate = isPrivate;
		this.allowRedeploy = allowRedeploy;
		this.storagePath = storagePath;
		userPermissions = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl( String baseUrl ) {
		this.baseUrl = baseUrl;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate( boolean isPrivate ) {
		this.isPrivate = isPrivate;
	}
	
	public boolean isAllowRedeploy() {
		return allowRedeploy;
	}
	
	public void setAllowRedeploy( boolean allowRedeploy ) {
		this.allowRedeploy = allowRedeploy;
	}
	
	public Path getStoragePath() {
		return storagePath;
	}
	
	public void setStoragePath( Path storagePath ) {
		this.storagePath = storagePath;
	}
	
	public List<QuakUserPermission> getUserPermissions() {
		return userPermissions;
	}
	
	public void setUserPermissions( List<QuakUserPermission> userPermissions ) {
		this.userPermissions = userPermissions;
	}
}