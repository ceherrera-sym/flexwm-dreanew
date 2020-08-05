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

import com.flexwm.shared.co.BmoContractProperty;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoWorkContract;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;

/**
 * @author smuniz
 *
 */

public class UiContractPropertyForm extends UiForm{
	UiSuggestBox workContractIdUiSuggestBox = new UiSuggestBox(new BmoWorkContract());
	UiSuggestBox propertyIdUiSuggestBox = new UiSuggestBox(new BmoProperty());

	BmoContractProperty bmoContractProperty;
	
	public UiContractPropertyForm(UiParams uiParams, int id) {
		super(uiParams, new BmoContractProperty(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoContractProperty = (BmoContractProperty)getBmObject();
		formFlexTable.addField(1, 0, workContractIdUiSuggestBox, bmoContractProperty.getWorkContractid());
		formFlexTable.addField(1, 2, propertyIdUiSuggestBox, bmoContractProperty.getPropertyId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoContractProperty.setId(id);
		bmoContractProperty.getWorkContractid().setValue(workContractIdUiSuggestBox.getSelectedId());
		bmoContractProperty.getPropertyId().setValue(propertyIdUiSuggestBox.getSelectedId());

		return bmoContractProperty;
	}
	
	@Override
	public void close() {
		UiContractPropertyList uiContractPropertyList = new UiContractPropertyList(getUiParams());
		uiContractPropertyList.show();
	}
}


