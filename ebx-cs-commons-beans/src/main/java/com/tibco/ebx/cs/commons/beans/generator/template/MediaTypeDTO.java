/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.template;

/**
 * DTO for MediaType<br>
 *
 * @author Fabien Bontemps
 * @since 2.1.0
 *
 */
public class MediaTypeDTO {
	private String attachment;

	/**
	 * Constructor
	 */
	public MediaTypeDTO() {
		super();
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(final String attachment) {
		this.attachment = attachment;
	}

}
