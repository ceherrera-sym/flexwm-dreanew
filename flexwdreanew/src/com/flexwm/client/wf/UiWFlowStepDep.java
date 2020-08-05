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
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowStepDep;


public class UiWFlowStepDep extends UiList {
	BmoWFlowStepDep bmoWFlowStepDep;
	BmoWFlowStep bmoWFlowStep;
	int wflowstepId, childId;

	public UiWFlowStepDep(UiParams uiParams, Panel defaultPanel, BmoWFlowStep bmoWFlowStep) {
		super(uiParams, defaultPanel, new BmoWFlowStepDep());
		bmoWFlowStepDep = (BmoWFlowStepDep)getBmObject();
		this.bmoWFlowStep = bmoWFlowStep;

		// Filtro de pasos
		BmFilter forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoWFlowStepDep.getKind(), 
				bmoWFlowStepDep.getChildStepId().getName(), 
				bmoWFlowStepDep.getChildStepId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWFlowStep.getId(),
				bmoWFlowStepDep.getChildStepId().getName());	
		getUiParams().setForceFilter(bmoWFlowStepDep.getProgramCode(), forceFilter);	
	}

	@Override
	public void create() {
		UiWFlowStepDepForm uiWFlowStepDepForm = new UiWFlowStepDepForm(getUiParams(), 0);
		uiWFlowStepDepForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowStepDepForm uiWFlowStepDepForm = new UiWFlowStepDepForm(getUiParams(), bmObject.getId());
		uiWFlowStepDepForm.show();
	}

	public class UiWFlowStepDepForm extends UiFormDialog {
		UiListBox stepListBox;

		public UiWFlowStepDepForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowStepDep(), id);
			
			// Preparar filtro para excluir pasos con secuencia de fase igual o superior al actual
			BmFilter filterPostSequence = new BmFilter();
			filterPostSequence.setValueLabelFilter(
					bmoWFlowStep.getKind(),
					bmoWFlowStep.getSequence().getName(),
					"",
					BmFilter.MINOREQUAL,
					bmoWFlowStep.getSequence().toInteger(),
					""
					);

			// Filtrar tipos de pasos que no sean iguales al actual
			BmFilter filterDifferentStepTypes = new BmFilter();
			filterDifferentStepTypes.setValueOperatorFilter(
					bmoWFlowStep.getKind(),
					bmoWFlowStep.getIdField(),
					BmFilter.NOTEQUALS,
					bmoWFlowStep.getId()
					);

			// Filtrar tipos de pasos anteriores, del mismo tipo de flujo
			BmFilter filterWFlowTypeSteps = new BmFilter();
			filterWFlowTypeSteps.setValueFilter(
					bmoWFlowStep.getKind(),
					bmoWFlowStep.getWFlowId().getName(),
					bmoWFlowStep.getWFlowId().toInteger()
					);

			stepListBox = new UiListBox(getUiParams(), new BmoWFlowStep());
			stepListBox.addBmFilter(filterPostSequence);
			stepListBox.addBmFilter(filterWFlowTypeSteps);
			stepListBox.addBmFilter(filterDifferentStepTypes);
		}

		@Override
		public void populateFields(){
			bmoWFlowStepDep = (BmoWFlowStepDep)getBmObject();
			formFlexTable.addField(1, 0, stepListBox, bmoWFlowStepDep.getWFlowStepId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowStepDep = new BmoWFlowStepDep();
			bmoWFlowStepDep.setId(id);
			bmoWFlowStepDep.getChildStepId().setValue(bmoWFlowStep.getId());
			bmoWFlowStepDep.getWFlowStepId().setValue(stepListBox.getSelectedId());	
			return bmoWFlowStepDep;
		}

		@Override
		public void close() {
			list();
		}
	}
}