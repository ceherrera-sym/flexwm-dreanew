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
import com.flexwm.shared.co.BmoConceptGroup;
import com.flexwm.shared.co.BmoConceptHeading;
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

public class UiConceptHeading extends UiList {
	BmoConceptHeading bmoConceptHeading;

	public UiConceptHeading(UiParams uiParams) {
		super(uiParams, new BmoConceptHeading());
		bmoConceptHeading = (BmoConceptHeading)getBmObject();
	}

	@Override
	public void postShow(){
		addFilterListBox(new UiListBox(getUiParams(), new BmoConceptGroup()), bmoConceptHeading.getBmoConceptGroup());
	}

	@Override
	public void create() {
		UiConceptHeadingForm uiConceptHeadingForm = new UiConceptHeadingForm(getUiParams(), 0);
		uiConceptHeadingForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiConceptHeadingForm uiConceptHeadingForm = new UiConceptHeadingForm(getUiParams(), bmObject.getId());
		uiConceptHeadingForm.show();
	}

	public class UiConceptHeadingForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox conceptGroupCategoryListBox = new UiListBox(getUiParams(), new BmoConceptGroup());

		BmoConceptHeading bmoConceptHeading;

		public UiConceptHeadingForm(UiParams uiParams, int id) {
			super(uiParams, new BmoConceptHeading(), id); 
		}

		@Override
		public void populateFields(){
			bmoConceptHeading = (BmoConceptHeading)getBmObject();
			formFlexTable.addField(1, 0, conceptGroupCategoryListBox, bmoConceptHeading.getConceptGroupId()); 
			formFlexTable.addField(2, 0, nameTextBox, bmoConceptHeading.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoConceptHeading.getDescription());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoConceptHeading.setId(id);
			bmoConceptHeading.getConceptGroupId().setValue(conceptGroupCategoryListBox.getSelectedId());
			bmoConceptHeading.getName().setValue(nameTextBox.getText());
			bmoConceptHeading.getDescription().setValue(descriptionTextArea.getText());
			return bmoConceptHeading;
		}

		@Override
		public void close() {
			list();
		}
	}
}
