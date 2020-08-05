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

import com.flexwm.shared.co.BmoComplexUnitPrice;
import com.flexwm.shared.co.BmoUnitPrice;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


/**
 * @author smuniz
 *
 */

public class UiComplexUnitPriceForm extends UiForm{
	UiSuggestBox parentUnitPriceIdSuggestBox = new UiSuggestBox(new BmoUnitPrice());
	UiSuggestBox childUnitPriceIdSuggestBox = new UiSuggestBox(new BmoUnitPrice());
	TextBox quantityTextBox = new TextBox();
	
	BmoComplexUnitPrice bmoComplexUnitPrice;
	
	public UiComplexUnitPriceForm(UiParams uiParams, int id) {
		super(uiParams, new BmoComplexUnitPrice(), id); 
	}
	
	@Override
	public void populateFields(){
		bmoComplexUnitPrice = (BmoComplexUnitPrice)getBmObject();
		formFlexTable.addField(1, 0, parentUnitPriceIdSuggestBox, bmoComplexUnitPrice.getParentUnitPriceId());
		formFlexTable.addField(1, 2, childUnitPriceIdSuggestBox, bmoComplexUnitPrice.getChildUnitPriceId());
		formFlexTable.addField(2, 0, quantityTextBox, bmoComplexUnitPrice.getQuantity());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoComplexUnitPrice.setId(id);
		bmoComplexUnitPrice.getParentUnitPriceId().setValue(parentUnitPriceIdSuggestBox.getSelectedId());
		bmoComplexUnitPrice.getChildUnitPriceId().setValue(childUnitPriceIdSuggestBox.getSelectedId());
		bmoComplexUnitPrice.getQuantity().setValue(quantityTextBox.getText());

		return bmoComplexUnitPrice;
	}
	
	@Override
	public void close() {
		UiComplexUnitPriceList uiComplexUnitPriceList = new UiComplexUnitPriceList(getUiParams());
		uiComplexUnitPriceList.show();
	}
}


