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

import com.flexwm.shared.op.BmoWarehouse;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCity;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


/**
 * @author jhernandez
 *
 */

public class UiWarehouse extends UiList {

	BmoWarehouse bmoWarehouse;

	public UiWarehouse(UiParams uiParams) {
		super(uiParams, new BmoWarehouse());
		bmoWarehouse = (BmoWarehouse)getBmObject();
	}

	@Override
	public void postShow() {
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoWarehouse.getType()), bmoWarehouse, bmoWarehouse.getType());
		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoWarehouse.getUserId());
	}

	@Override
	public void create() {
		UiWarehouseForm uiWareHouseForm = new UiWarehouseForm(getUiParams(), 0);
		uiWareHouseForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWarehouse = (BmoWarehouse)bmObject;
		UiWarehouseForm uiWareHouseForm = new UiWarehouseForm(getUiParams(), bmoWarehouse.getId());
		uiWareHouseForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWarehouseForm uiWareHouseForm = new UiWarehouseForm(getUiParams(), bmObject.getId());
		uiWareHouseForm.show();
	}

	public class UiWarehouseForm extends UiFormDialog {
		UiListBox typeListBox = new UiListBox(getUiParams());
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();	
		TextArea descriptionTextArea = new TextArea();
		TextBox streetTextBox = new TextBox();
		TextBox numberTextBox = new TextBox();
		TextBox neighborhoodTextBox = new TextBox();
		TextBox officePhoneTextBox = new TextBox();
		UiSuggestBox citySuggestBox = new UiSuggestBox(new BmoCity());
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());

		BmoWarehouse bmoWarehouse;

		public UiWarehouseForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWarehouse(), id);
		}

		@Override
		public void populateFields() {
			bmoWarehouse = (BmoWarehouse)getBmObject();

			if (newRecord) {
				try {
					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoWarehouse.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}			
			}

			//Mostrar usuarios Activos
			BmoUser bmoUser = new BmoUser();
			BmFilter bmFilterUsersActives = new BmFilter();
			bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(bmFilterUsersActives);

			formFlexTable.addField(1, 0, nameTextBox, bmoWarehouse.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoWarehouse.getDescription());	
			formFlexTable.addField(3, 0, typeListBox, bmoWarehouse.getType());		
			formFlexTable.addField(4, 0, streetTextBox, bmoWarehouse.getStreet());
			formFlexTable.addField(5, 0, numberTextBox, bmoWarehouse.getNumber());
			formFlexTable.addField(6, 0, neighborhoodTextBox, bmoWarehouse.getNeighborhood());
			formFlexTable.addField(7, 0, officePhoneTextBox, bmoWarehouse.getOfficePhone());
			formFlexTable.addField(8, 0, citySuggestBox, bmoWarehouse.getCityId());
			formFlexTable.addField(9, 0, userSuggestBox, bmoWarehouse.getUserId());
			formFlexTable.addField(10, 0, companyListBox, bmoWarehouse.getCompanyId());	

			statusEffect();
		}

		public void statusEffect() {
			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWarehouse.setId(id);

			bmoWarehouse.getType().setValue(typeListBox.getSelectedCode());
			bmoWarehouse.getName().setValue(nameTextBox.getText());		
			bmoWarehouse.getDescription().setValue(descriptionTextArea.getText());
			bmoWarehouse.getStreet().setValue(streetTextBox.getText());
			bmoWarehouse.getNumber().setValue(numberTextBox.getText());
			bmoWarehouse.getNeighborhood().setValue(neighborhoodTextBox.getText());
			bmoWarehouse.getOfficePhone().setValue(officePhoneTextBox.getText());		
			bmoWarehouse.getCityId().setValue(citySuggestBox.getSelectedId());
			bmoWarehouse.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoWarehouse.getCompanyId().setValue(companyListBox.getSelectedId());

			return bmoWarehouse;
		}

		@Override
		public void close() {
			list();
		}
	}
}
