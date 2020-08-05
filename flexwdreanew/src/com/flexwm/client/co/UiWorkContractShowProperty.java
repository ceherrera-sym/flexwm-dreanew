/**
 * 
 */

package com.flexwm.client.co;

import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkContractProperty;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


/**
 * @author jhernandez
 *
 */

public class UiWorkContractShowProperty extends UiFormDialog {
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox costTextBox = new TextBox();
	TextBox priceTextBox = new TextBox();
	BmoWorkContract bmoWorkContract;

	public UiWorkContractShowProperty(UiParams uiParams, int id) {
		super(uiParams, new BmoWorkContract(), id);
	}

	@Override
	public void populateFields(){
		bmoWorkContract = (BmoWorkContract)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoWorkContract.getCode());
		formFlexTable.addField(2, 0, nameTextBox, bmoWorkContract.getName());
		formFlexTable.addField(3, 0, descriptionTextArea, bmoWorkContract.getDescription());		

		if (!newRecord) {
			
			BmoWorkContractProperty bmoWorkContractProperty = new BmoWorkContractProperty();
			FlowPanel productPriceFP = new FlowPanel();		
			BmFilter filterPropertys = new BmFilter();
			filterPropertys.setValueFilter(bmoWorkContractProperty.getKind(), bmoWorkContractProperty.getWorkContractId(), bmoWorkContract.getId());
			getUiParams().setForceFilter(bmoWorkContractProperty.getProgramCode(), filterPropertys);
			UiWorkContractPropertyList UiWorkContractProperty = new UiWorkContractPropertyList(getUiParams(), productPriceFP, bmoWorkContract);
			setUiType(bmoWorkContractProperty.getProgramCode(), UiParams.MINIMALIST);
			UiWorkContractProperty.show();
			formFlexTable.addPanel(4, 0, productPriceFP, 2);

			statusEffect();
		}
	}

	public void statusEffect() {
		codeTextBox.setEnabled(false);
		nameTextBox.setEnabled(false);
		descriptionTextArea.setEnabled(false);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoWorkContract.setId(id);
		bmoWorkContract.getCode().setValue(codeTextBox.getText());
		bmoWorkContract.getName().setValue(nameTextBox.getText());
		bmoWorkContract.getDescription().setValue(descriptionTextArea.getText());		
		return bmoWorkContract;
	}

	@Override
	public void close() {
		UiWorkContractDetail uiWorkContractDetail = new UiWorkContractDetail(getUiParams(), bmoWorkContract.getId());
		uiWorkContractDetail.show();	
	}

	@Override
	public void saveNext() {		
		UiWorkContractDetail uiWorkContractDetail = new UiWorkContractDetail(getUiParams(), bmoWorkContract.getId());
		uiWorkContractDetail.show();

	}
}
