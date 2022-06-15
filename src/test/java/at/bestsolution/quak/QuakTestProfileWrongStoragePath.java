/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfileWrongStoragePath.java
 * Creation Date:      15.06.2022
 * Description: Test profile for testing startup with wrong 
 * storage-path configuration.       
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
 * Test profile for testing startup with wrong storage-path configuration.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfileWrongStoragePath extends QuakTestProfile {
	
	private static final String WRONG_STORAGE_PATH = "/absolute/storage/path";
	
    /**
     * @return Returns additional quak configuration for a repository with wrong storage-path. 
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = super.getConfigOverrides();
    	testConfigurations.put( "quak.repositories[0].storage-path", WRONG_STORAGE_PATH );
        return testConfigurations;
    }
}
