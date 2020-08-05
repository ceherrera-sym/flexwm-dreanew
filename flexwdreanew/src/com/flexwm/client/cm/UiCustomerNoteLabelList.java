/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import com.flexwm.shared.cm.BmoCustomerNote;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiCustomerNoteLabelList extends UiFormLabelList {
	BmoCustomerNote bmoCustomerNote;
	
	UiListBox typeListBox = new UiListBox(getUiParams());
	TextArea notesTextArea = new TextArea();
	UiFileUploadBox fileFileUpload = new UiFileUploadBox(getUiParams());

	int customerId;

	public UiCustomerNoteLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoCustomerNote());
		customerId = id;
		bmoCustomerNote = new BmoCustomerNote();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoCustomerNote.getKind(), 
				bmoCustomerNote.getCustomerId().getName(), 
				bmoCustomerNote.getCustomerId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoCustomerNote.getCustomerId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoCustomerNote = (BmoCustomerNote)getBmObject();
		formFlexTable.addField(1, 0, typeListBox, bmoCustomerNote.getType());
		formFlexTable.addField(2, 0, notesTextArea, bmoCustomerNote.getNotes());
		formFlexTable.addField(3, 0, fileFileUpload, bmoCustomerNote.getFile());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoCustomerNote = new BmoCustomerNote();
		bmoCustomerNote.setId(id);
		bmoCustomerNote.getType().setValue(typeListBox.getSelectedCode());
		bmoCustomerNote.getNotes().setValue(notesTextArea.getText());
		bmoCustomerNote.getFile().setValue(fileFileUpload.getBlobKey());
		bmoCustomerNote.getCustomerId().setValue(customerId);		
		return bmoCustomerNote;
	}
}
