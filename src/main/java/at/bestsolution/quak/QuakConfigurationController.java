/*
 * ----------------------------------------------------------------
 * Original File Name: QuakConfigurationController.java
 * Creation Date:      14.04.2022
 * Description: Quak Configuration controller for handling the configuration data.
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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import at.bestsolution.quak.QuakConfiguration.Repository;
import at.bestsolution.quak.QuakConfiguration.User;

/**
 * Quak Configuration controller for handling the configuration data.
 * 
 * @author kerim.yeniduenya@bestsolution.at
 */
@ApplicationScoped
public class QuakConfigurationController {
	
	@Inject
	QuakConfiguration configuration;
		
	/**
	 * Searches for a repository configuration which has a base URL matching with beginning of the path.
	 * @param path URL path of the upload request.
	 * @return Repository a repository configuration or null in case of no match.
	 */
	public Repository getRepository(String path) {
		return configuration.repositories().stream().filter( r -> path.startsWith( r.baseUrl() ) ).findFirst().orElse( null );
	}
	
	/**
	 * Get the list of users defined in configuration.
	 * @return list of users
	 */
	public List<User> getUsers() {
		return configuration.users();
	}
}
