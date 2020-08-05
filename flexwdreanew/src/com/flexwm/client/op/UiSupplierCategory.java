/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.flexwm.shared.op.BmoSupplierCategory;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */


public class UiSupplierCategory extends UiList {

	public UiSupplierCategory(UiParams uiParams) {
		super(uiParams, new BmoSupplierCategory());
	}

	@Override
	public void create() {
		UiSupplierCategoryForm uiSupplierCategoryForm = new UiSupplierCategoryForm(getUiParams(), 0);
		uiSupplierCategoryForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSupplierCategoryForm uiSupplierCategoryForm = new UiSupplierCategoryForm(getUiParams(), bmObject.getId());
		uiSupplierCategoryForm.show();
	}

	public class UiSupplierCategoryForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();	
		BmoSupplierCategory boSupplierCategory;

		public UiSupplierCategoryForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSupplierCategory(), id);
		}

		@Override
		public void populateFields() {
			boSupplierCategory = (BmoSupplierCategory)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, boSupplierCategory.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, boSupplierCategory.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			boSupplierCategory.setId(id);
			boSupplierCategory.getName().setValue(nameTextBox.getText());
			boSupplierCategory.getDescription().setValue(descriptionTextArea.getText());
			return boSupplierCategory;
		}

		@Override
		public void close() {
			list();
		}
	}
}
