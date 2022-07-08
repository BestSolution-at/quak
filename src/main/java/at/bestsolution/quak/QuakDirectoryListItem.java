/*
 * ----------------------------------------------------------------
 * Original File Name: QuakDirectoryListItem.java
 * Creation Date:      07.07.2022
 * Description: Class file of quak directory list item.       
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

/**
 * Represents a quak directory list item.
 * 
 * @author kerim.yeniduenya@bestsolution.at
 * 
 * @param icon
 *            Icon to show for the list item.
 * @param name
 *            Name of the list item.
 * @param path
 *            Path of the list item.
 * @param fileSize
 *            Size of the file which list item points.
 * @param lastModified
 *            Last modification date of the list item.
 */
public record QuakDirectoryListItem( String icon, String name, String path, String fileSize, String lastModified ) {

}
