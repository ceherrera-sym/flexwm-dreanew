/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiBmFieldClickHandler;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoFileType;
import com.flexwm.shared.wf.BmoWFEmail;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiWFlowDocument extends UiList {
	BmoWFlowDocument bmoWFlowDocument;
	int wFlowId = 0;

	public UiWFlowDocument(UiParams uiParams) {
		super(uiParams, new BmoWFlowDocument());
		bmoWFlowDocument = (BmoWFlowDocument)getBmObject();
	}

	public UiWFlowDocument(UiParams uiParams, Panel defaultPanel, int wFlowId) {
		super(uiParams, defaultPanel, new BmoWFlowDocument());
		bmoWFlowDocument = (BmoWFlowDocument)getBmObject();
		this.wFlowId = wFlowId;
	}

	@Override
	public void postShow() {
		// Muestra filtros si es modulo independiente
		if (getUiType() == UiParams.MASTER) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory()), bmoWFlowDocument.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory());
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType()), bmoWFlowDocument.getBmoWFlow().getBmoWFlowType());
			addFilterListBox(new UiListBox(getUiParams(), new BmoFileType()), bmoWFlowDocument.getBmoFileType());
		}
	}

	@Override
	public void displayList() {	
		int col = 0;

		listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getCode());
		if (!isMobile()) {
			listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getName());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getBmoFileType().getName());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getRequired());
			listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getIsUp());
		}
		listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getFile());
		listFlexTable.addListTitleCell(0, col++, bmoWFlowDocument.getProgramCode(), bmoWFlowDocument.getFileLink());
		listFlexTable.addListTitleCell(0, col++, "Enviar");

		int row = 1;
		while (iterator.hasNext()) {
			BmoWFlowDocument nextBmoWFlowDocument = (BmoWFlowDocument)iterator.next(); 
			col = 0;

			Label linkLabel = new Label(nextBmoWFlowDocument.getCode().toString());
			linkLabel.addClickHandler(rowClickHandler);
			linkLabel.setStyleName("listCellLink");
			listFlexTable.setWidget(row, col++, linkLabel);
			if (!isMobile()) {
				listFlexTable.addListCell(row, col++, getBmObject(), nextBmoWFlowDocument.getName());
				listFlexTable.addListCell(row, col++, getBmObject(), nextBmoWFlowDocument.getBmoFileType().getName());
				listFlexTable.addListCell(row, col++, getBmObject(), nextBmoWFlowDocument.getRequired());
				listFlexTable.addListCell(row, col++, getBmObject(), nextBmoWFlowDocument.getIsUp());
			}
			listFlexTable.addListCell(row, col++, getBmObject(), nextBmoWFlowDocument.getFile());
			listFlexTable.addListCell(row, col++, getBmObject(), nextBmoWFlowDocument.getFileLink());

			Image emailImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/send.png"));
			emailImage.setTitle("Enviar Documento por Email.");
			emailImage.addClickHandler(new UiBmFieldClickHandler(nextBmoWFlowDocument, nextBmoWFlowDocument.getFile()) {
				@Override
				public void onClick(ClickEvent event) {
					defaultSendEmail((BmoWFlowDocument)bmObject);
				}
			});
			listFlexTable.setWidget(row, col, emailImage);
			listFlexTable.getCellFormatter().setWidth(row,  col,  "50px");
			listFlexTable.getCellFormatter().addStyleName(row, col++, "listCellLink");

			listFlexTable.formatRow(row);
			row++;
		}
	}

	// Enviar correo
	public void defaultSendEmail(BmoWFlowDocument nextBmoWFlowDocument) {
		BmoWFEmail bmoWFEmail = new BmoWFEmail();

		// Liga documento
		String link = GwtUtil.parseImageLink(getUiParams().getSFParams(), nextBmoWFlowDocument.getFile());

		try {
			bmoWFEmail.getTo().setValue("");
			bmoWFEmail.getToName().setValue("");
			bmoWFEmail.getFromName().setValue(getUiParams().getSFParams().getMainAppTitle());
			bmoWFEmail.getSubject().setValue(getUiParams().getSFParams().getMainAppTitle() 
					+ ": #" + "-Documento " 
					+ nextBmoWFlowDocument.getName().toString() 
					+ " de " + nextBmoWFlowDocument.getBmoWFlow().getCode().toString() 
					+ " " + nextBmoWFlowDocument.getBmoWFlow().getName().toString());
			bmoWFEmail.getCp().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoWFEmail.getReplyTo().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoWFEmail.getFrom().setValue(getUiParams().getSFParams().getLoginInfo().getEmailAddress());
			bmoWFEmail.getBody().setValue(""
					+ "Se envía el Documento [" + nextBmoWFlowDocument.getName().toString() + "] "
					+ "Tipo [" + nextBmoWFlowDocument.getBmoFileType().getName().toString() + "] "
					+ " de " + nextBmoWFlowDocument.getBmoWFlow().getCode().toString() + ": " + nextBmoWFlowDocument.getBmoWFlow().getName().toString()
					);
			bmoWFEmail.getFixedBody().setValue("<br><br>Para revisar el Documento, haz click "
					+ "<a href=\"" 
					+ link
					+ "\" target=\"_blank\">Aqui</a>");
			bmoWFEmail.getWFlowId().setValue(nextBmoWFlowDocument.getWFlowId().toInteger());
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-sendEmail() ERROR: " + e.toString());
		}

		UiWFEmailForm uiWFlowDocumentEmailForm = new UiWFEmailForm(getUiParams(), 0, bmoWFEmail);
		getUiParams().setUiType(new BmoWFEmail().getProgramCode(), UiParams.LOOKUPDIALOG);
		uiWFlowDocumentEmailForm.show();
	}

	@Override
	public void create() {
		UiWFlowDocumentForm uiWFlowDocumentForm = new UiWFlowDocumentForm(getUiParams(), 0);
		uiWFlowDocumentForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWFlowDocument = (BmoWFlowDocument)bmObject;
		UiWFlowDocumentForm uiWFlowDocumentForm = new UiWFlowDocumentForm(getUiParams(), bmoWFlowDocument.getId());
		uiWFlowDocumentForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWFlowDocumentForm uiWFlowDocumentForm = new UiWFlowDocumentForm(getUiParams(), bmObject.getId());
		uiWFlowDocumentForm.show();
	}


	private class UiWFlowDocumentForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		UiFileUploadBox fileUploadBox = new UiFileUploadBox(getUiParams());
		UiListBox fileTypeListBox = new UiListBox(getUiParams(), new BmoFileType());
		TextBox fileLinkTextBox = new TextBox();

		public UiWFlowDocumentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowDocument(), id);
			bmoWFlowDocument = (BmoWFlowDocument)getBmObject();
		}

		@Override
		public void populateFields(){
			bmoWFlowDocument = (BmoWFlowDocument)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoWFlowDocument.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlowDocument.getName());
			formFlexTable.addField(3, 0, fileTypeListBox, bmoWFlowDocument.getFileTypeId());
			formFlexTable.addField(4, 0, fileUploadBox, bmoWFlowDocument.getFile());
			formFlexTable.addField(5, 0, fileLinkTextBox, bmoWFlowDocument.getFileLink());
			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowDocument.setId(id);
			bmoWFlowDocument.getWFlowId().setValue(wFlowId);
			bmoWFlowDocument.getCode().setValue(codeTextBox.getText());
			bmoWFlowDocument.getName().setValue(nameTextBox.getText());
			bmoWFlowDocument.getFile().setValue(fileUploadBox.getBlobKey());
			bmoWFlowDocument.getFileTypeId().setValue(fileTypeListBox.getSelectedId());
			bmoWFlowDocument.getFileLink().setValue(fileLinkTextBox.getText());

			return bmoWFlowDocument;
		}

		private void statusEffect() {
			if (bmoWFlowDocument.getRequired().toBoolean()) {
				codeTextBox.setEnabled(false);
				nameTextBox.setEnabled(false);
			}
		}

		@Override
		public void close() {
			list();
		}
	}
}


