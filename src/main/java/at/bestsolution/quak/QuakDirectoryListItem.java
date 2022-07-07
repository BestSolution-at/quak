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
 */
public class QuakDirectoryListItem {

	/**
	 * Name of the list item.
	 */
	private String name;
	
	/**
	 * Path of the list item. 
	 */
	private String path;
	
	/**
	 * Last modification date of the list item.
	 */
	private String lastModified;
	
	/**
	 * Size of the file which list item points.
	 */
	private String fileSize;
	
	/**
	 * Icon to show for the list item. 
	 */
	private String icon;
	

	/**
	 * Constructs a quak directory list item.
	 * 
	 * @param icon 
	 * 		Icon to show for the list item. 
	 * @param name 
	 * 		Name of the list item.
	 * @param path
	 * 		Path of the list item. 
	 * @param fileSize 
	 * 		Size of the file which list item points.
	 * @param lastModified 
	 * 		Last modification date of the list item.
	 */
	public QuakDirectoryListItem( String icon, String name, String path, String fileSize, String lastModified ) {
		this.name = name;
		this.path = path;
		this.fileSize = fileSize;
		this.lastModified = lastModified;
		this.icon = icon;
	}

	/**
	 * @return Name of the list item.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * 		Name of the list item.
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * @return Path of the list item.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 * 		Path of the list item.
	 */
	public void setPath( String path ) {
		this.path = path;
	}

	/**
	 * @return Last modification date of the list item.
	 */
	public String getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 * 		Last modification date of the list item.
	 */
	public void setLastModified( String lastModified ) {
		this.lastModified = lastModified;
	}

	/**
	 * @return Size of the file which list item points.
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 * 		Size of the file which list item points.
	 */
	public void setFileSize( String fileSize ) {
		this.fileSize = fileSize;
	}

	/**
	 * @return Icon to show for the list item.
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 * 		Icon to show for the list item.
	 */
	public void setIcon( String icon ) {
		this.icon = icon;
	}
}
