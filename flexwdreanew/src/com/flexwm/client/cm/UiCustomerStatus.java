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

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerStatus;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;

public class UiCustomerStatus extends UiList {
	BmoCustomerStatus bmoCustomerStatus;
	BmoCustomer bmoCustomer = new BmoCustomer();
	
	
	public UiCustomerStatus(UiParams uiParams, Panel defaultPanel, BmoCustomer bmoCustomer) {
		super(uiParams, defaultPanel, new BmoCustomerStatus());
		bmoCustomerStatus = (BmoCustomerStatus)getBmObject();
		this.bmoCustomer = bmoCustomer;
	}


	@Override
	public void postShow() {
		newImage.setVisible(false);
//		addStaticFilterListBox(new UiListBox(bmoCustomer.getCustomertype()), bmoCustomer, bmoCustomer.getCustomertype());
//		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoCustomer.getSalesmanId());
//		if (!isMobile()) {
//			if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId()))
//				addFilterSuggestBox(new UiSuggestBox(new BmoTerritory()), new BmoTerritory(), bmoCustomer.getTerritoryId());
//			if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId()))
//				addFilterSuggestBox(new UiSuggestBox(new BmoIndustry()), new BmoIndustry(), bmoCustomer.getIndustryId());
//			if (getSFParams().isFieldEnabled(bmoCustomer.getReqPayTypeId()))
//				addFilterSuggestBox(new UiSuggestBox(new BmoReqPayType()), new BmoReqPayType(), bmoCustomer.getReqPayTypeId());
//			if (getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory()))
//				addStaticFilterListBox(new UiListBox(bmoCustomer.getCustomercategory()), bmoCustomer, bmoCustomer.getCustomercategory());		
//		}
//		addStaticFilterListBox(new UiListBox(bmoCustomer.getStatus()), bmoCustomer, bmoCustomer.getStatus());
//		if (!isMobile())
//			addTagFilterListBox(bmoCustomer.getTags());
	}

	@Override
	public void create() {
		UiCustomerStatusForm uiCustomerStatusForm = new UiCustomerStatusForm(getUiParams(), 0);
		uiCustomerStatusForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCustomerStatusForm uiCustomerStatusForm = new UiCustomerStatusForm(getUiParams(), bmObject.getId());
		uiCustomerStatusForm.show();
	}

	 @Override
	 public void edit(BmObject bmObject) {
		 UiCustomerStatusForm uiCustomerStatusForm = new UiCustomerStatusForm(getUiParams(),
		 bmObject.getId());
		 uiCustomerStatusForm.show();
	 }

	public class UiCustomerStatusForm extends UiFormDialog {
		
		//UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		BmoCustomerStatus bmoCustomerStatus;
		
		public UiCustomerStatusForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCustomerStatus(), id);
			
		}
		
		@Override
		public void postShow() {
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
		}

		@Override
		public void populateFields(){
			bmoCustomerStatus = (BmoCustomerStatus)getBmObject();

			//formFlexTable.addField(1, 0, customerSuggestBox, bmoCustomerStatus.getCustomerId());
			formFlexTable.addField(2, 0, companyListBox, bmoCustomerStatus.getCompanyId());
			formFlexTable.addLabelField(3, 0, bmoCustomerStatus.getStatus());
			companyListBox.setEnabled(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCustomerStatus.setId(id);
			bmoCustomerStatus.getCustomerId().setValue(bmoCustomer.getId());
			bmoCustomerStatus.getCompanyId().setValue(companyListBox.getSelectedId());
			//bmoCustomerStatus.getStatus().setValue(statusListBox.getSelectedCode());
			
			return bmoCustomerStatus;
		}

		@Override
		public void close() {
			list();
		}

//		@Override
//		public void saveNext() {
//			if (isMaster()) {
//				if (newRecord && getBmObject().getId() > 0) {
//					// Recargar listado
//					showList();
//					// El cliente es recientemente creado, ir a la forma
//					edit(bmoCustomerStatus);
//				} else {
//					// El cliente ya fue creado, ir a la forma de detalle
//					UiCustomerStatusForm UiCustomerStatusForm = new UiCustomerStatusForm(getUiParams(), id);
//					UiCustomerStatusForm.show();
//				}
//			}
//		}
		
	}
}


