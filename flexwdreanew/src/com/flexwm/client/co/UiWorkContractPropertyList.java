package com.flexwm.client.co;


import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkContractProperty;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiWorkContractPropertyList extends UiList {
	BmoWorkContractProperty bmoWorkContractProperty;
	BmoWorkContract  bmoWorkContract;
	int contractId;
	
	public UiWorkContractPropertyList(UiParams uiParams, Panel defaultPanel, BmoWorkContract bmoWorkContract) {
		super(uiParams,defaultPanel, new BmoWorkContractProperty());
		bmoWorkContractProperty = (BmoWorkContractProperty)getBmObject();
		this.bmoWorkContract = bmoWorkContract;
		contractId = bmoWorkContract.getId();
		
	}
	@Override
	public void create() {
		UiWorkContractPropertyForm uiProductPriceForm = new UiWorkContractPropertyForm(getUiParams(), 0);
		uiProductPriceForm.show();
	}	
	@Override
	public void open(BmObject bmObject) {
		UiWorkContractPropertyForm uiProductPriceForm = new UiWorkContractPropertyForm(getUiParams(), bmObject.getId());
		uiProductPriceForm.show(); 
	}	
	private class UiWorkContractPropertyForm extends UiFormDialog {
		UiSuggestBox properySuggest = new UiSuggestBox(new BmoProperty());
	
		public UiWorkContractPropertyForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWorkContractProperty(), id);
		}

		@Override
		public void populateFields(){
			bmoWorkContractProperty = (BmoWorkContractProperty)getBmObject();
			BmoProperty bmoProperty = new BmoProperty();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setNotInFilter(bmoWorkContractProperty.getKind(), 
					bmoProperty.getIdFieldName(),
					bmoWorkContractProperty.getPropertyId().getName(),
					"1","1"
					);
			properySuggest.addFilter(bmFilter);
		
			formFlexTable.addField(1, 0, properySuggest, bmoWorkContractProperty.getPropertyId());
		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoWorkContractProperty = new BmoWorkContractProperty();
			bmoWorkContractProperty.setId(id);
			bmoWorkContractProperty.getPropertyId().setValue(properySuggest.getSelectedId());
			bmoWorkContractProperty.getWorkContractId().setValue(contractId);
			
			return bmoWorkContractProperty;
		}
		@Override
		public void close() {
			list();
		}
		
	}
	

}
