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

import com.flexwm.shared.cm.BmoCompetition;
import com.flexwm.shared.cm.BmoOpportunityCompetition;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiOpportunityCompetitionLabelList extends UiFormLabelList {
	BmoOpportunityCompetition bmoOpportunityCompetition;
	
	UiListBox nombreListBox = new UiListBox(getUiParams(), new BmoCompetition());

	int opportunityId;
	
	public UiOpportunityCompetitionLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoOpportunityCompetition());
		
		opportunityId = id;
		bmoOpportunityCompetition = new BmoOpportunityCompetition();
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoOpportunityCompetition.getKind(), 
				bmoOpportunityCompetition.getOpportunityId().getName(), 
				bmoOpportunityCompetition.getOpportunityId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoOpportunityCompetition.getOpportunityId().getName());
	}
	
	@Override
	public void populateFields() {
		bmoOpportunityCompetition = (BmoOpportunityCompetition)getBmObject();
		formFlexTable.addField(1, 0, nombreListBox, bmoOpportunityCompetition.getCompetitionId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoOpportunityCompetition = new BmoOpportunityCompetition();
		bmoOpportunityCompetition.setId(id);
		bmoOpportunityCompetition.getCompetitionId().setValue(nombreListBox.getSelectedId());	
		bmoOpportunityCompetition.getOpportunityId().setValue(opportunityId);
		
		return bmoOpportunityCompetition;
	}
}
