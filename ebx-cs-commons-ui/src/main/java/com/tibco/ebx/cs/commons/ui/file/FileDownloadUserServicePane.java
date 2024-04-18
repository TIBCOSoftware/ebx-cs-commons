/*
 * Copyright Orchestra Networks 2000-2019. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Objects;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ui.UICSSClasses;
import com.orchestranetworks.userservice.UserServiceGetContext;
import com.orchestranetworks.userservice.UserServiceGetResponse;
import com.orchestranetworks.userservice.UserServicePane;
import com.orchestranetworks.userservice.UserServicePaneContext;
import com.orchestranetworks.userservice.UserServicePaneWriter;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;

/**
 * Generic UserServicePane to download a file.
 *
 * @author Aurélien Ticot
 * @since TODO since
 */
public class FileDownloadUserServicePane implements UserServicePane {
	private final File fileToDownload;
	private final UserMessage downloadDoesNotStartMessage;

	/**
	 * Constructor for the pane.
	 *
	 * @param pFile the file to download.
	 * @since TODO since
	 */
	public FileDownloadUserServicePane(final File pFile) {
		Objects.requireNonNull(pFile, "The file argument shall not be null");

		this.fileToDownload = pFile;
		this.downloadDoesNotStartMessage = Messages.getInfo(this.getClass(), "IfDownloadOfFileDoesNotStartAutomatically");
	}

	/**
	 * Constructor for the pane.
	 *
	 * @param pFile                        the file to download.
	 * @param pDownloadDoesNotStartMessage optionaly a customized message to display on the download page. Default is "If the download of the file does not start automatically".
	 * @since TODO since
	 */
	public FileDownloadUserServicePane(final File pFile, final UserMessage pDownloadDoesNotStartMessage) {
		this.fileToDownload = pFile;
		if (pDownloadDoesNotStartMessage != null) {
			this.downloadDoesNotStartMessage = pDownloadDoesNotStartMessage;
		} else {
			this.downloadDoesNotStartMessage = Messages.getInfo(this.getClass(), "IfDownloadOfFileDoesNotStartAutomatically");
		}
	}

	@Override
	public void writePane(final UserServicePaneContext pPaneContext, final UserServicePaneWriter pWriter) {
		Locale locale = pWriter.getLocale();
		String downloadURL = pWriter.getURLForGetRequest(this::processDownloadRequest);

		pWriter.add("<div ");
		pWriter.addSafeAttribute("class", UICSSClasses.CONTAINER_WITH_TEXT_PADDING);
		pWriter.add(">");

		pWriter.add(this.downloadDoesNotStartMessage);
		pWriter.add(", ");

		pWriter.add("<a ");
		pWriter.addSafeAttribute("href", downloadURL);
		pWriter.add(">");

		pWriter.add(Messages.get(this.getClass(), locale, "ClickToDownloadItManually"));

		pWriter.add("</a>");
		pWriter.add("</div>");

		pWriter.addJS_cr();
		pWriter.addJS_cr("function downloadFile() {");
		pWriter.addJS_cr("    var downloadFileURL = '" + downloadURL + "';");
		pWriter.addJS_cr("    window.open(downloadFileURL, '_blank');");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr();
		pWriter.addJS_cr("window.setTimeout(function() {");
		pWriter.addJS_cr("    downloadFile();");
		pWriter.addJS_cr("}, 500);");
		pWriter.addJS_cr();
	}

	private void defineResponse(final UserServiceGetResponse pResponse) {
		String filename = this.fileToDownload.getName();

		pResponse.setContentType("application/octet-stream");
		pResponse.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	}

	private void processDownloadRequest(final UserServiceGetContext pContext, final UserServiceGetResponse pResponse) {
		if (this.fileToDownload == null || !this.fileToDownload.exists()) {
			pResponse.setContentType("text/plain");
			OutputStream outStream = pResponse.getOutputStream();

			PrintWriter out;
			try {
				out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"));
				out.println("Something went wrong with the file to download");
				outStream.close();

			} catch (IOException e) {
				CommonsLogger.getLogger().error(e.getMessage(), e);
			}
		} else {
			try {
				this.defineResponse(pResponse);
				this.streamFile(pResponse);
			} catch (Exception exception) {
				CommonsLogger.getLogger().error(exception.getMessage(), exception);
			}
		}
	}

	private void streamFile(final UserServiceGetResponse pResponse) throws IOException {
		try (FileInputStream inStream = new FileInputStream(this.fileToDownload)) {
			OutputStream outStream = pResponse.getOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.close();
		}
	}
}
