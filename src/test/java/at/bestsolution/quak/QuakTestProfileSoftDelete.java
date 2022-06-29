/*
 * ----------------------------------------------------------------
 * Original File Name: QuakTestProfileSoftDelete.java
 * Creation Date:      29.06.2022
 * Description: Test profile class for clean up with soft delete.       
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
 * Test profile for clean up with soft delete.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
public class QuakTestProfileSoftDelete extends QuakTestProfile {
	
    /**
     * @return Returns additional basic quak configuration with soft deploy clean up.
     */
    @Override
    public Map<String, String> getConfigOverrides() {
    	Map<String, String> testConfigurations = super.getConfigOverrides();
    	testConfigurations.put( "quak.repositories[0].clean-up.hard-delete", SOFT_DELETE );
        return testConfigurations;
    }
}
