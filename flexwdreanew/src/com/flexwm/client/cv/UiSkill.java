/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.cv.BmoSkill;
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

public class UiSkill extends UiList {
	BmoSkill bmoSkill;

	public UiSkill(UiParams uiParams) {
		super(uiParams, new BmoSkill());
		bmoSkill = (BmoSkill)getBmObject();
	}

	@Override
	public void postShow(){
		//
	}

	@Override
	public void create() {
		UiSkillForm uiSkillForm = new UiSkillForm(getUiParams(), 0);
		uiSkillForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSkillForm uiSkillForm = new UiSkillForm(getUiParams(), bmObject.getId());
		uiSkillForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiSkillForm uiSkillForm = new UiSkillForm(getUiParams(), bmObject.getId());
		uiSkillForm.show();
	}

	public class UiSkillForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		UiListBox typeUiListBox = new UiListBox(getUiParams());
		TextArea descriptionTextArea = new TextArea();

		BmoSkill bmoSkill;

		public UiSkillForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSkill(), id); 
		}

		@Override
		public void populateFields() {
			bmoSkill = (BmoSkill)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoSkill.getName());
			formFlexTable.addField(2, 0, typeUiListBox, bmoSkill.getType());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoSkill.getDescription());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSkill.setId(id);
			bmoSkill.getName().setValue(nameTextBox.getText());
			bmoSkill.getType().setValue(typeUiListBox.getSelectedCode());
			bmoSkill.getDescription().setValue(descriptionTextArea.getText());
			return bmoSkill;
		}

		@Override
		public void close() {
			list();
		}
	}
}
