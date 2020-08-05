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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoProductKitItem;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiProductKit extends UiList {
	BmoProductKit bmoProductKit;

	public UiProductKit(UiParams uiParams) {
		super(uiParams, new BmoProductKit());
	}

	@Override
	public void create() {
		UiProductKitForm uiProductKitForm = new UiProductKitForm(getUiParams(), 0);
		uiProductKitForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoProductKit = (BmoProductKit)bmObject;
		UiProductKitForm uiProductKitForm = new UiProductKitForm(getUiParams(), bmoProductKit.getId());
		uiProductKitForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiProductKitForm uiProductKitForm = new UiProductKitForm(getUiParams(), bmObject.getId());
		uiProductKitForm.show();
	}

	public class UiProductKitForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox amountTextBox = new TextBox();
		UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());

		ProductKitUpdater productKitUpdater = new ProductKitUpdater();
		BmoProductKit bmoProductKit;

		String itemSection = "Items";
		public UiProductKitForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProductKit(), id);
		}

		@Override
		public void populateFields(){
			bmoProductKit = (BmoProductKit)getBmObject();
			formFlexTable.addField(2, 0, nameTextBox, bmoProductKit.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoProductKit.getDescription());
			formFlexTable.addField(4, 0, imageFileUpload, bmoProductKit.getImage());	
			formFlexTable.addField(5, 0, amountTextBox, bmoProductKit.getAmount());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(6, 0, itemSection, 2);
				BmoProductKitItem bmoProductKitItem = new BmoProductKitItem();
				FlowPanel productKitItemFP = new FlowPanel();
				BmFilter filterProductKitItem = new BmFilter();
				filterProductKitItem.setValueFilter(bmoProductKitItem.getKind(), bmoProductKitItem.getProductKitId(), bmoProductKit.getId());
				getUiParams().setForceFilter(bmoProductKitItem.getProgramCode(), filterProductKitItem);
				UiProductKitItem uiProductKitItem = new UiProductKitItem(getUiParams(), productKitItemFP, bmoProductKit, bmoProductKit.getId(), productKitUpdater);
				setUiType(bmoProductKitItem.getProgramCode(), UiParams.MINIMALIST);
				uiProductKitItem.show();
				formFlexTable.addPanel(7, 0, productKitItemFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProductKit.setId(id);
			bmoProductKit.getName().setValue(nameTextBox.getText());
			bmoProductKit.getDescription().setValue(descriptionTextArea.getText());
			bmoProductKit.getImage().setValue(imageFileUpload.getBlobKey());
			bmoProductKit.getAmount().setValue(amountTextBox.getText());
			return bmoProductKit;
		}

		@Override
		public boolean validate(BmObject bObject) throws BmException {
			//throw new BoException("error de objeto de negocio.");
			// Validaciones especiales, en caso de error enviar una excepcion de objecto de negocio
			return true;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				UiProductKitForm uiProductKitForm = new UiProductKitForm(getUiParams(), getBmObject().getId());
				uiProductKitForm.show();
			} else {
				UiProductKit uiProductKitList = new UiProductKit(getUiParams());
				uiProductKitList.show();
			}		
		}

		public class ProductKitUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}