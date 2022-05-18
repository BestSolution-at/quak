/*
 * ----------------------------------------------------------------
 * Original File Name: QuakUserInfoRequiredFalseTest.java
 * Creation Date:      18.05.2022
 * Description: Test class file of quak with wrong user-info-required 
 * setup for OpenID Connect.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;

/**
 * JUnit test cases of quak with wrong user-info-required setup for OpenID Connect.
 * 
 * @author: kerim.yeniduenya@bestsolution.at
 */
@QuarkusMainTest
@TestProfile( QuakTestProfileUserInfoRequiredFalse.class )
@TestMethodOrder( OrderAnnotation.class )
class QuakUserInfoRequiredFalseTest {
	
	/**
	 * Asserts quak exits with code 1 for wrong user-info-required setup for OpenID Connect.
	 * @param result launch result of  quak instance.
	 */
	@Test
	@Order( 1 )
	@Launch(value = {}, exitCode = 1)
	void testUserInfoRequiredFalse( LaunchResult result ) {
		assertEquals( 1, result.exitCode() );
	}
}
