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
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowStepTypeDep;


public class UiWFlowStepTypeDep extends UiList {
	BmoWFlowStepTypeDep bmoWFlowStepTypeDep;
	BmoWFlowStepType bmoWFlowStepType;
	int wflowsteptypeId, childId;

	public UiWFlowStepTypeDep(UiParams uiParams, Panel defaultPanel, BmoWFlowStepType bmoWFlowStepType) {
		super(uiParams, defaultPanel, new BmoWFlowStepTypeDep());
		bmoWFlowStepTypeDep = (BmoWFlowStepTypeDep)getBmObject();
		this.bmoWFlowStepType = bmoWFlowStepType;

		// Filtro de pasos
		BmFilter forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoWFlowStepTypeDep.getKind(), 
				bmoWFlowStepTypeDep.getChildStepTypeId().getName(), 
				bmoWFlowStepTypeDep.getChildStepTypeId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWFlowStepType.getId(),
				bmoWFlowStepTypeDep.getChildStepTypeId().getName());	
		getUiParams().setForceFilter(bmoWFlowStepTypeDep.getProgramCode(), forceFilter);	
	}

	@Override
	public void create() {
		UiWFlowStepTypeDepForm uiWFlowStepTypeDepForm = new UiWFlowStepTypeDepForm(getUiParams(), 0);
		uiWFlowStepTypeDepForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowStepTypeDepForm uiWFlowStepTypeDepForm = new UiWFlowStepTypeDepForm(getUiParams(), bmObject.getId());
		uiWFlowStepTypeDepForm.show();
	}

	public class UiWFlowStepTypeDepForm extends UiFormDialog {
		UiListBox stepTypeListBox;

		public UiWFlowStepTypeDepForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowStepTypeDep(), id);
			
			// Preparar filtro para excluir pasos con secuencia de fase igual o superior al actual
			BmFilter filterPostSequence = new BmFilter();
			filterPostSequence.setValueLabelFilter(
					bmoWFlowStepType.getKind(),
					bmoWFlowStepType.getBmoWFlowPhase().getSequence().getName(),
					"",
					BmFilter.MINOREQUAL,
					bmoWFlowStepType.getBmoWFlowPhase().getSequence().toInteger(),
					""
					);

			// Filtrar tipos de pasos que no sean iguales al actual
			BmFilter filterDifferentStepTypes = new BmFilter();
			filterDifferentStepTypes.setValueOperatorFilter(
					bmoWFlowStepType.getKind(),
					bmoWFlowStepType.getIdField(),
					BmFilter.NOTEQUALS,
					bmoWFlowStepType.getId()
					);

			// Filtrar tipos de pasos anteriores, del mismo tipo de flujo
			BmFilter filterWFlowTypeSteps = new BmFilter();
			filterWFlowTypeSteps.setValueFilter(
					bmoWFlowStepType.getKind(),
					bmoWFlowStepType.getWFlowTypeId().getName(),
					bmoWFlowStepType.getWFlowTypeId().toInteger()
					);

			stepTypeListBox = new UiListBox(getUiParams(), new BmoWFlowStepType());
			stepTypeListBox.addBmFilter(filterPostSequence);
			stepTypeListBox.addBmFilter(filterWFlowTypeSteps);
			stepTypeListBox.addBmFilter(filterDifferentStepTypes);
		}

		@Override
		public void populateFields(){
			bmoWFlowStepTypeDep = (BmoWFlowStepTypeDep)getBmObject();
			formFlexTable.addField(1, 0, stepTypeListBox, bmoWFlowStepTypeDep.getWFlowStepTypeId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowStepTypeDep = new BmoWFlowStepTypeDep();
			bmoWFlowStepTypeDep.setId(id);
			bmoWFlowStepTypeDep.getChildStepTypeId().setValue(bmoWFlowStepType.getId());
			bmoWFlowStepTypeDep.getWFlowStepTypeId().setValue(stepTypeListBox.getSelectedId());	
			return bmoWFlowStepTypeDep;
		}

		@Override
		public void close() {
			list();
		}
	}
}