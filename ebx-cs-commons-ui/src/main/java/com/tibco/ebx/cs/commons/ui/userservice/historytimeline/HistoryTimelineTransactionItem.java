/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.historytimeline;

import java.util.Date;

import com.tibco.ebx.cs.commons.ui.timeline.TimelineItem;

/**
 * The Class HistoryTimelineTransactionItem.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class HistoryTimelineTransactionItem extends TimelineItem {
	protected static class JsObjectName {
		private JsObjectName() {
			super();
		}

		protected static class EndVersion {
			private EndVersion() {
				super();
			}

			protected static final String JS_NAME = "endVersion";
		}

		protected static class IsVersion {
			private IsVersion() {
				super();
			}

			protected static final String JS_NAME = "isVersion";
		}

		protected static class IsVersionId {
			private IsVersionId() {
				super();
			}

			protected static final String JS_NAME = "isVersionId";
		}

		protected static class StartVersion {
			private StartVersion() {
				super();
			}

			protected static final String JS_NAME = "startVersion";
		}

		protected static class VersionId {
			private VersionId() {
				super();
			}

			protected static final String JS_NAME = "versionId";
		}
	}

	/** The Constant VERSION_EVEN_STYLE_VARNAME. */
	public static final String VERSION_EVEN_STYLE_VARNAME = "versionEvenStyle";
	/** The Constant VERSION_ODD_STYLE_VARNAME. */
	public static final String VERSION_ODD_STYLE_VARNAME = "versionOddStyle";
	private final Integer startVersion;
	private final Integer endVersion;
	private boolean isVersion;
	private boolean isVersionId;

	private String versionID;

	private String versionRecordLink;
	private static String versionEvenStyle = "background-color: #E4E4E4;border-color: #E4E4E4;";
	private static String versionOddStyle = "background-color: #F5F5F5;border-color: #F5F5F5;";

	/**
	 * Instantiates a new history timeline transaction item.
	 *
	 * @param group        the group
	 * @param start        the start
	 * @param end          the end
	 * @param startVersion the start version
	 * @param endVersion   the end version
	 * @param content      the content
	 * @since 1.0.0
	 */
	public HistoryTimelineTransactionItem(final String group, final Date start, final Date end, final Integer startVersion, final Integer endVersion, final String content) {
		super(group, start, end, content);
		this.startVersion = startVersion;
		this.endVersion = endVersion;
	}

	/**
	 * Gets the end version.
	 *
	 * @return the end version
	 * @since 1.0.0
	 */
	public Integer getEndVersion() {
		return this.endVersion;
	}

	/**
	 * Gets the start version.
	 *
	 * @return the start version
	 * @since 1.0.0
	 */
	public Integer getStartVersion() {
		return this.startVersion;
	}

	/**
	 * Gets the transaction even style.
	 *
	 * @return the transaction even style
	 * @since 1.0.0
	 */
	public String getTransactionEvenStyle() {
		return HistoryTimelineTransactionItem.versionEvenStyle;
	}

	/**
	 * Gets the transaction odd style.
	 *
	 * @return the transaction odd style
	 * @since 1.0.0
	 */
	public String getTransactionOddStyle() {
		return HistoryTimelineTransactionItem.versionOddStyle;
	}

	/**
	 * Gets the version id.
	 *
	 * @return the version id
	 * @since 1.0.0
	 */
	public String getVersionID() {
		if (this.versionID == null) {
			return "";
		} else {
			return this.versionID;
		}
	}

	/**
	 * Gets the version record link.
	 *
	 * @return the version record link
	 * @since 1.0.0
	 */
	public String getVersionRecordLink() {
		return this.versionRecordLink;
	}

	/**
	 * Checks if is version.
	 *
	 * @return true, if is version
	 * @since 1.0.0
	 */
	public boolean isVersion() {
		return this.isVersion;
	}

	/**
	 * Checks if is version id.
	 *
	 * @return true, if is version id
	 * @since 1.0.0
	 */
	public boolean isVersionId() {
		return this.isVersionId;
	}

	/**
	 * Sets the version.
	 *
	 * @param isVersion the new version
	 * @since 1.0.0
	 */
	public void setVersion(final boolean isVersion) {
		this.isVersion = isVersion;
	}

	/**
	 * Sets the version id.
	 *
	 * @param isVersionId the new version id
	 * @since 1.0.0
	 */
	public void setVersionId(final boolean isVersionId) {
		this.isVersionId = isVersionId;
	}

	/**
	 * Sets the version id.
	 *
	 * @param versionID the new version id
	 * @since 1.0.0
	 */
	public void setVersionID(final String versionID) {
		this.versionID = versionID;
	}

	/**
	 * Sets the version record link.
	 *
	 * @param versionRecordLink the new version record link
	 * @since 1.0.0
	 */
	public void setVersionRecordLink(final String versionRecordLink) {
		this.versionRecordLink = versionRecordLink;
	}

	/**
	 * Builds the js object content.
	 *
	 * @return the string
	 * @since 1.0.0
	 */
	@Override
	protected String buildJsObjectContent() {
		String jsObjectContent = super.buildJsObjectContent();

		jsObjectContent += ", ";
		jsObjectContent += JsObjectName.StartVersion.JS_NAME + ": " + this.getStartVersion() + ", ";
		jsObjectContent += JsObjectName.EndVersion.JS_NAME + ": " + this.getEndVersion() + ", ";
		jsObjectContent += JsObjectName.IsVersion.JS_NAME + ": " + this.isVersion() + ", ";
		jsObjectContent += JsObjectName.IsVersionId.JS_NAME + ": " + this.isVersionId() + ", ";
		jsObjectContent += JsObjectName.VersionId.JS_NAME + ": '" + this.getVersionID() + "'";

		return jsObjectContent;
	}

}
