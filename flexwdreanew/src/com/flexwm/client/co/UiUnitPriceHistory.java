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

import com.flexwm.shared.co.BmoUnitPriceHistory;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;

public class UiUnitPriceHistory extends UiFormList {
	BmoUnitPriceHistory bmoUnitPriceHistory;
	
	TextBox priceTextBox = new TextBox();
	DateBox dateTimeBox = new DateBox();
	int unitPriceId;
	TextArea commentsTextArea = new TextArea();
	
	public UiUnitPriceHistory(UiParams uiParams, Panel defaultPanel, int unitPriceId) {
		super(uiParams, defaultPanel, new BmoUnitPriceHistory());
		bmoUnitPriceHistory = (BmoUnitPriceHistory)getBmObject();

		this.unitPriceId = unitPriceId;
		forceFilter = new BmFilter();
		
		if (unitPriceId > 0) {
			forceFilter.setValueLabelFilter(bmoUnitPriceHistory.getKind(), 
					bmoUnitPriceHistory.getUnitPriceId().getName(), 
					bmoUnitPriceHistory.getUnitPriceId().getLabel(), 
					BmFilter.EQUALS, 
					unitPriceId,
					bmoUnitPriceHistory.getBmoUnitPrice().getName().getName());	
		}
	}
	
	@Override
	public void populateFields(){
		bmoUnitPriceHistory = (BmoUnitPriceHistory)getBmObject();
		formFlexTable.addField(1, 0, priceTextBox, bmoUnitPriceHistory.getPrice());
		formFlexTable.addField(1, 2, dateTimeBox, bmoUnitPriceHistory.getDate());
		formFlexTable.addField(2, 0, commentsTextArea, bmoUnitPriceHistory.getComments());
		
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoUnitPriceHistory = new BmoUnitPriceHistory();
		bmoUnitPriceHistory.setId(id);
		bmoUnitPriceHistory.getPrice().setValue(priceTextBox.getText()); 
		bmoUnitPriceHistory.getDate().setValue(dateTimeBox.getTextBox().getText());
		if (unitPriceId > 0) bmoUnitPriceHistory.getUnitPriceId().setValue(unitPriceId);	
		bmoUnitPriceHistory.getComments().setValue(commentsTextArea.getText());
		
		return bmoUnitPriceHistory;
	}
}
