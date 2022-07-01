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
 * Interceptor class for relocating Quarkus configuration names to quak
 * configuration names.
 * 
 * @author kerim.yeniduenya@bestsolution.at
 */
public class QuakConfigurationRelocateInterceptor extends RelocateConfigSourceInterceptor {

	/**
	 * Transformation function to relocate required Quarkus configuration names
	 * to quak configuration names.
	 */
	private static final UnaryOperator<String> RELOCATE = name -> {

		if ( name.startsWith( "quarkus.http.limits." ) ) {
			return name.replaceFirst( "quarkus\\.http\\.limits\\.", "quak.http." );
		} 
		else if ( name.startsWith( "quarkus.http." ) ) {
			return name.replaceFirst( "quarkus\\.http\\.", "quak.http." );
		} 
		else if ( name.startsWith( "quarkus.oauth2." ) ) {
			return name.replaceFirst( "quarkus\\.oauth2\\.", "quak.oauth2." );
		} 
		else if ( name.startsWith( "quarkus.oidc.authentication." ) ) {
			return name.replaceFirst( "quarkus\\.oidc\\.authentication.", "quak.oidc." );
		}
		else if ( name.startsWith( "quarkus.oidc.credentials.secret" ) ) {
			return name.replaceFirst( "quarkus\\.oidc\\.credentials\\.secret", "quak.oidc.shared-secret" );
		} 
		else if ( name.startsWith( "quarkus.oidc." ) ) {
			return name.replaceFirst( "quarkus\\.oidc\\.", "quak.oidc." );
		}
		else if ( name.startsWith( "mp.jwt.verify.publickey.location" ) ) {
			return name.replaceFirst( "mp\\.jwt\\.verify\\.publickey\\.location", "quak.jwt.issuer-public-key" );
		} 
		else if ( name.startsWith( "mp.jwt.verify.issuer" ) ) {
			return name.replaceFirst( "mp\\.jwt\\.verify\\.issuer", "quak.jwt.issuer-name" );
		}

		return name;
	};

	public QuakConfigurationRelocateInterceptor() {
		super( RELOCATE );
	}
}