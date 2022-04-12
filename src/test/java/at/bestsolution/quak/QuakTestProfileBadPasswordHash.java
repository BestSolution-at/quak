/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfileBadPasswordHash.java
 * Creation Date:      12.04.2022
 * Description: Test profile for user entry with bad password hash 
 * value.      
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
 * Test profile for user entry with bad password hash value.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfileBadPasswordHash extends QuakTestProfile {
	
    /**
     * @return Returns additional basic quak configuration with false redeploy field, 
     * to be applied to the test. 
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = super.getConfigOverrides();
    	testConfigurations.put( "quak.users[0].password", BAD_PASSWORD_HASH );
        return testConfigurations;
    }
}
