/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfile.java
 * Creation Date:      29.03.2022
 * Description: Profile class file for quak tests.       
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

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Test profile class for basic tests.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfile implements QuarkusTestProfile {
	
	public static final String PUBLIC_KEY_FILE_LOCATION = "/tests/publicKey.pem";
	public static final String PUBLIC_KEY_FILE = "publicKey.pem";
	public static final String JWT_ISSUER = "https://www.bestsolution.at/quak/test";
	public static final String GOOD_USERNAME = "user1";
	public static final String GOOD_PASSWORD = "pass123";
	public static final String GOOD_PASSWORD_HASH = "$2a$10$Nfw5dwvR8ly0HPzhVE92TuqqUORzw1WBs9f4hmPBdkaqctmJQVtNu";
	public static final String BAD_PASSWORD_HASH = "BAD_PASSWORD_HASH_VALUE";
	public static final String REPOSITORY_NAME = "blueprint";
	public static final String STORAGE_PATH = "repos/blueprint";
	public static final String BASE_URL = "/at/bestsolution/blueprint";
	public static final String ALLOW_REDEPLOY = "true";
	public static final String DO_NOT_ALLOW_REDEPLOY = "false";
	public static final String MAX_BODY_SIZE = "10M";
	public static final String PRIVATE_REPOSITORY = "true";
	public static final String PUBLIC_REPOSITORY = "false";
	public static final String WRITE_PERMISSION = "true";
	public static final String READ_PERMISSION = "false";
	public static final String PATH_REGEX = "/at/bestsolution.*";
	public static final String WRONG_USERNAME = "wrongname";
	public static final String WRONG_PASSWORD = "wrongpass";
	public static final String VALID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy5i"
											+ "ZXN0c29sdXRpb24uYXQvcXVhay90ZXN0IiwidXBuIjoidXNlcjEiLCJncm91cHM"
											+ "iOlsiVXNlciIsIkFkbWluIl0sImV4cCI6MTY4MjU3NTA5NSwiaWF0IjoxNjUxMD"
											+ "M5MDk1LCJqdGkiOiJjYTU0YmUzZi0zZDYyLTRkZDgtYjgyMi1hNTZmNzVmZDViM"
											+ "GIifQ.HwsbuQUxr5uk-lxs4gf6vo1j90LLigZRmAfCLmVf1U8GUWkHCsor3U5Qx"
											+ "kgdN8RyexnlQBjOSnGr7BInhCcqWjn5EAPymB7ExGFRJSJ1NmewmhyAP6EtOG3N"
											+ "r6Sp9Awm8-3O--TABGzI-ss8iXKadagUoO_ZS6VAbS2JikhU1TBn0yt49ti3aqD"
											+ "Fyyr-7_cCeQT2NfX42VHnNIXAHZUsWKVNyWoyzqxpq_I0zH1c19KSgiQ3WRl9s6"
											+ "Z-rnHXtUNNwLOD2YMW0bs5XO2k_hlA78KLMbfqJRe9xCqMFN6NRJ_w1JA7uwQFl"
											+ "weM_8gH40BSg1Ui3Kwq5exxit-KNAEeYw";
	public static final String VALID_TOKEN_WITH_WRONGUSERNAME = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy"
											+ "5iZXN0c29sdXRpb24uYXQvcXVhay90ZXN0IiwidXBuIjoidXNlcldyb25nIiwiZ"
											+ "3JvdXBzIjpbIlVzZXIiLCJBZG1pbiJdLCJleHAiOjE2ODI1OTQ1OTYsImlhdCI6"
											+ "MTY1MTA1ODU5NiwianRpIjoiMzA1MWFiZGMtYWViZC00MjlmLTgyZmMtNGFmMTl"
											+ "jMTFkMmZlIn0.dAJxTa10a8nPbu2ueQ67rRd4uPZarS470kp0lpF9cYTbGTjwTG"
											+ "dW4mTvnqFByZdc6WtUtbKg2qluFDacFALYkOvbCaKJ9rPwvnMqgSAj3Iv-MfY3U"
											+ "lXT-9awuYe_p5bjHR_sEJgAYwlw1G6MHvX6Ib1ypXHRvqQWNFyZ3a9V_L6xqp0q"
											+ "4IiKUpvpOOe0140Z7qJOvh-s_r1Kwk9dhSiTE3M61nbNuqFQrv1qFoeovGkLTcF"
											+ "MOXmexy0rebvRtd8Z66R_fJTQObRXtyiG5f6F5yNF6SwLry6to2wcBsEFDEZjoi"
											+ "pkNnXmB9EwlfnWBfTjz7dJFPo6l9APipUl8tYHYw";
	public static final String INVALID_TOKEN = "WRONG_TOKEN";


    /**
     * @return Returns additional basic quak configuration to be applied to the test. 
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = new HashMap<String, String>();
    	testConfigurations.put( "mp.jwt.verify.publickey.location", PUBLIC_KEY_FILE_LOCATION );
    	testConfigurations.put( "mp.jwt.verify.issuer", JWT_ISSUER );
    	testConfigurations.put( "quarkus.native.resources.includes", PUBLIC_KEY_FILE );
    	testConfigurations.put( "quak.repositories[0].name", REPOSITORY_NAME );
    	testConfigurations.put( "quak.repositories[0].storage-path", STORAGE_PATH );
    	testConfigurations.put( "quak.repositories[0].base-url", BASE_URL );
    	testConfigurations.put( "quak.repositories[0].allow-redeploy", ALLOW_REDEPLOY );
    	testConfigurations.put( "quak.repositories[0].is-private", PRIVATE_REPOSITORY );
    	testConfigurations.put( "quak.users[0].username", GOOD_USERNAME );
    	testConfigurations.put( "quak.users[0].password", GOOD_PASSWORD_HASH );
    	testConfigurations.put( "quarkus.http.limits.max-body-size", MAX_BODY_SIZE );
    	testConfigurations.put( "quak.user-permissions[0].username", GOOD_USERNAME );
    	testConfigurations.put( "quak.user-permissions[0].repository-name", REPOSITORY_NAME );
    	testConfigurations.put( "quak.user-permissions[0].url-paths[0]", PATH_REGEX );
    	testConfigurations.put( "quak.user-permissions[0].may-publish", WRITE_PERMISSION );
        return testConfigurations;
    }
}
