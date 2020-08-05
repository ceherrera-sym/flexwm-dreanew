/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;

import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.in.BmoPolicyRecipient;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;

public class UiPolicyRecipient extends UiFormList {
	UiListBox relationListBox = new UiListBox(getUiParams());
	TextBox percentageTextBox = new TextBox();
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	BmoPolicyRecipient bmoPolicyRecipient;
	int policyId;
	

	public UiPolicyRecipient(UiParams uiParams, Panel defaultPanel, int policyId) {
		super(uiParams, defaultPanel, new BmoPolicyRecipient());
		this.policyId = policyId;
		
		bmoPolicyRecipient = new BmoPolicyRecipient();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoPolicyRecipient.getKind(), 
				bmoPolicyRecipient.getPolicyId().getName(), 
				bmoPolicyRecipient.getPolicyId().getLabel(), 
				BmFilter.EQUALS, 
				policyId,
				bmoPolicyRecipient.getPolicyId().getName());
	}
	
	@Override
	public void populateFields(){
		bmoPolicyRecipient = (BmoPolicyRecipient)getBmObject();		
		formFlexTable.addField(1, 0, customerSuggestBox, bmoPolicyRecipient.getCustomerId());
		formFlexTable.addField(1, 2, relationListBox, bmoPolicyRecipient.getRelation());
		formFlexTable.addField(2, 0, percentageTextBox, bmoPolicyRecipient.getPercentage());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoPolicyRecipient = new BmoPolicyRecipient();
		bmoPolicyRecipient.setId(id);
		bmoPolicyRecipient.getRelation().setValue(relationListBox.getSelectedCode());
		bmoPolicyRecipient.getPercentage().setValue(percentageTextBox.getText());
		bmoPolicyRecipient.getCustomerId().setValue(customerSuggestBox.getSelectedId());
		bmoPolicyRecipient.getPolicyId().setValue(policyId);		
		return bmoPolicyRecipient;
	}
}
