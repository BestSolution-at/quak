/*
 * ----------------------------------------------------------------
 * Original File Name: QuakConfigurationRelocateInterceptor.java
 * Creation Date:      01.07.2022
 * Description: Interceptor class for relocating Quarkus configuration
 * names to quak configuration names.
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

import java.util.function.UnaryOperator;

import io.smallrye.config.RelocateConfigSourceInterceptor;

/**
 * Interceptor class for relocating Quarkus configuration names to quak configuration names.
 * 
 * @author kerim.yeniduenya@bestsolution.at
 */
public class QuakConfigurationRelocateInterceptor extends RelocateConfigSourceInterceptor {
	
    /**
     * Transformation function to relocate required Quarkus configuration names to 
     * quak configuration names.  
     */
    private static final UnaryOperator<String> RELOCATE = name -> {
        if (name.startsWith( "quarkus.http.port" ) ) {
            return name.replaceFirst( "quarkus\\.http\\.port", "quak.http.port" );
        }
        if (name.startsWith( "quarkus.http.max-body-size" ) ) {
            return name.replaceFirst( "quarkus\\.http\\.max-body-size", "quak.http.max-body-size" );
        }
        return name;
    };

    public QuakConfigurationRelocateInterceptor() {
        super( RELOCATE );
    }
}