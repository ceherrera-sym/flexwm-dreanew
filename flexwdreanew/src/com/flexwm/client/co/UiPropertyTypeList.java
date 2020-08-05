/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoPropertyType;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */

public class UiPropertyTypeList extends UiList {
	BmoPropertyType bmoPropertyType;

	public UiPropertyTypeList(UiParams uiParams) {
		super(uiParams, new BmoPropertyType());
		bmoPropertyType = (BmoPropertyType)getBmObject();
	}

	@Override
	public void create() {
		UiPropertyTypeForm uiPropertyTypeForm = new UiPropertyTypeForm(getUiParams(), 0);
		uiPropertyTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPropertyType = (BmoPropertyType)bmObject;
		UiPropertyTypeForm uiPropertyTypeForm = new UiPropertyTypeForm(getUiParams(), bmObject.getId());
		uiPropertyTypeForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiPropertyTypeForm uiPropertyTypeForm = new UiPropertyTypeForm(getUiParams(), bmObject.getId());
		uiPropertyTypeForm.show();
	}

	public class UiPropertyTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		CheckBox copyTagsCheckBox = new CheckBox();
		BmoPropertyType bmoPropertyType;

		public UiPropertyTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPropertyType(), id); 
		}

		@Override
		public void populateFields(){
			bmoPropertyType = (BmoPropertyType)getBmObject();
			formFlexTable.addField(1, 0, codeTextBox, bmoPropertyType.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoPropertyType.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoPropertyType.getDescription());
			formFlexTable.addField(4, 0, orderTypeListBox, bmoPropertyType.getOrderTypeId());	
			formFlexTable.addField(5, 0, copyTagsCheckBox,bmoPropertyType.getCopyTag());
		}
		
		@Override
		public BmObject populateBObject() throws BmException {
			bmoPropertyType.setId(id);
			bmoPropertyType.getCode().setValue(codeTextBox.getText());
			bmoPropertyType.getName().setValue(nameTextBox.getText());
			bmoPropertyType.getDescription().setValue(descriptionTextArea.getText());
			bmoPropertyType.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoPropertyType.getCopyTag().setValue(copyTagsCheckBox.getValue());
			return bmoPropertyType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
