/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfileAuthorization.java
 * Creation Date:      19.04.2022
 * Description: Test profile for authorization tests.      
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

import java.util.Map;

/**
 * Test profile for authorization tests.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfileAuthorization extends QuakTestProfile {
	
	public static final String USERNAME_ADMIN = "admin";
	public static final String PATH_REGEX_MATCH_ALL = "/at/.*";
	public static final String REPOSITORY_NAME_UNAUTHORIZED = "blueprintUnauthorized";
	public static final String REPOSITORY_NAME_READ_ONLY = "blueprintReadOnly";
	public static final String BASE_URL_UNAUTHORIZED = "/unauthorized/at/bestsolution/";
	public static final String BASE_URL_READ_ONLY = "/at/readOnly/bestsolution/";
	public static final String BASE_URL_SUBPATH = "/at/bestsolution/blueprint/sub/path";
	public static final String BASE_URL_UNAUTHORIZED_PATH = "/unauthorized/at/bestsolution/blueprint/sub/path";
	public static final String BASE_URL_UNAUTHORIZED_READ_ONLY_PATH = "/unauthorized/at/readOnly/bestsolution/";

    /**
     * @return Returns additional basic quak configuration for authorization tests.
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = super.getConfigOverrides();
    	// Insert Admin User
    	testConfigurations.put( "quak.users[1].username", USERNAME_ADMIN );
    	testConfigurations.put( "quak.users[1].password", GOOD_PASSWORD_HASH );
    	// Insert Permissions
    	testConfigurations.put( "quak.user-permissions[1].username", GOOD_USERNAME );
    	testConfigurations.put( "quak.user-permissions[1].repository-name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.user-permissions[1].url-paths[0]", PATH_REGEX_MATCH_ALL );
    	testConfigurations.put( "quak.user-permissions[1].may-publish", READ_PERMISSION );
    	testConfigurations.put( "quak.user-permissions[2].username", USERNAME_ADMIN );
    	testConfigurations.put( "quak.user-permissions[2].repository-name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.user-permissions[2].url-paths[0]", PATH_REGEX_MATCH_ALL );
    	testConfigurations.put( "quak.user-permissions[2].may-publish", WRITE_PERMISSION );
    	// Insert Repositories
    	testConfigurations.put( "quak.repositories[1].name", REPOSITORY_NAME_UNAUTHORIZED );
    	testConfigurations.put( "quak.repositories[1].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[1].base-url", BASE_URL_UNAUTHORIZED );
    	testConfigurations.put( "quak.repositories[1].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.repositories[2].name", REPOSITORY_NAME );
    	testConfigurations.put( "quak.repositories[2].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[2].base-url", BASE_URL_SUBPATH );
    	testConfigurations.put( "quak.repositories[2].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.repositories[3].name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.repositories[3].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[3].base-url", BASE_URL_READ_ONLY );
    	testConfigurations.put( "quak.repositories[3].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.repositories[4].name", REPOSITORY_NAME );
    	testConfigurations.put( "quak.repositories[4].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[4].base-url", BASE_URL_UNAUTHORIZED_PATH );
    	testConfigurations.put( "quak.repositories[4].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.repositories[5].name", REPOSITORY_NAME_READ_ONLY );
    	testConfigurations.put( "quak.repositories[5].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[5].base-url", BASE_URL_UNAUTHORIZED_READ_ONLY_PATH );
    	testConfigurations.put( "quak.repositories[5].is-private", PRIVATE_REPOSITORY );
        return testConfigurations;
    }
}
