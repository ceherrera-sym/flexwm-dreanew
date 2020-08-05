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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoFileType;
import com.flexwm.shared.wf.BmoWFlowDocumentType;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiWFlowDocumentType extends UiList {

	public UiWFlowDocumentType(UiParams uiParams) {
		super(uiParams, new BmoWFlowDocumentType());
	}

	@Override
	public void create() {
		UiWFlowDocumentTypeForm uiWFlowDocumentTypeForm = new UiWFlowDocumentTypeForm(getUiParams(), 0);
		uiWFlowDocumentTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowDocumentTypeForm uiWFlowDocumentTypeForm = new UiWFlowDocumentTypeForm(getUiParams(), bmObject.getId());
		uiWFlowDocumentTypeForm.show();
	}

	public class UiWFlowDocumentTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		CheckBox requiredCheckBox = new CheckBox();
		UiListBox fileTypeListBox = new UiListBox(getUiParams(), new BmoFileType());
		BmoWFlowDocumentType bmoWFlowDocumentType;
		String wFlowTypeId;

		public UiWFlowDocumentTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowDocumentType(), id);
			bmoWFlowDocumentType = (BmoWFlowDocumentType)getBmObject();
		}

		@Override
		public void populateFields(){
			bmoWFlowDocumentType = (BmoWFlowDocumentType)getBmObject();

			// Obten las variables del filtro forzado
			BmFilter forceFilter = getUiParams().getUiProgramParams(bmoWFlowDocumentType.getProgramCode()).getForceFilter();
			wFlowTypeId = forceFilter.getValue();

			formFlexTable.addLabelField(1, 0, forceFilter.getFieldLabel(), forceFilter.getValueLabel());
			formFlexTable.addField(2, 0, codeTextBox, bmoWFlowDocumentType.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoWFlowDocumentType.getName());
			formFlexTable.addField(4, 0, fileTypeListBox, bmoWFlowDocumentType.getFileTypeId());
			formFlexTable.addField(5, 0, requiredCheckBox, bmoWFlowDocumentType.getRequired());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowDocumentType.setId(id);
			bmoWFlowDocumentType.getCode().setValue(codeTextBox.getText());
			bmoWFlowDocumentType.getName().setValue(nameTextBox.getText());
			bmoWFlowDocumentType.getRequired().setValue(requiredCheckBox.getValue());
			bmoWFlowDocumentType.getWFlowTypeId().setValue(wFlowTypeId);
			bmoWFlowDocumentType.getFileTypeId().setValue(fileTypeListBox.getSelectedId());

			return bmoWFlowDocumentType;
		}

		@Override
		public void close() {
			list();
		}
	}
}