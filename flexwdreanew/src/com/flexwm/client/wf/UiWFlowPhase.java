/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;


public class UiWFlowPhase extends UiList {
	BmoWFlowPhase bmoWFlowPhase;
	BmoWFlowCategory bmoWFlowCategory;
	
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox sequenceTextBox = new TextBox();


	public UiWFlowPhase(UiParams uiParams, Panel defaultPanel, BmoWFlowCategory bmoWFlowCategory) {
		super(uiParams, defaultPanel, new BmoWFlowPhase());
		this.bmoWFlowCategory = bmoWFlowCategory;
		bmoWFlowPhase = new BmoWFlowPhase();

		BmFilter filterByCategory = new BmFilter();
		filterByCategory.setValueLabelFilter(bmoWFlowPhase.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(), 
				bmoWFlowPhase.getWFlowCategoryId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWFlowCategory.getId(),
				bmoWFlowPhase.getWFlowCategoryId().getName());
		getUiParams().setForceFilter(bmoWFlowPhase.getProgramCode(), filterByCategory);
	}

	@Override
	public void create() {
		UiWFlowPhaseForm uiWFlowPhaseForm = new UiWFlowPhaseForm(getUiParams(), 0);
		uiWFlowPhaseForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowPhaseForm uiWFlowPhaseForm = new UiWFlowPhaseForm(getUiParams(), bmObject.getId());
		uiWFlowPhaseForm.show();	
	}


	public class UiWFlowPhaseForm extends UiFormDialog {
		BmoWFlowPhase bmoWFlowPhase;
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox sequenceTextBox = new TextBox();

		public UiWFlowPhaseForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowPhase(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowPhase = (BmoWFlowPhase)getBmObject();
			formFlexTable.addField(1, 0, sequenceTextBox, bmoWFlowPhase.getSequence());	
			formFlexTable.addField(2, 0, nameTextBox, bmoWFlowPhase.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoWFlowPhase.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowPhase = new BmoWFlowPhase();
			bmoWFlowPhase.setId(id);
			bmoWFlowPhase.getName().setValue(nameTextBox.getText());
			bmoWFlowPhase.getDescription().setValue(descriptionTextArea.getText());
			bmoWFlowPhase.getSequence().setValue(sequenceTextBox.getText());	
			bmoWFlowPhase.getWFlowCategoryId().setValue(bmoWFlowCategory.getId());
			return bmoWFlowPhase;
		}

		@Override
		public void close() {
			list();
		}
	}
}