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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.UiProgramParams;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowStep;


public class UiWFlowDetail extends UiDetail {
	protected BmoWFlow bmoWFlow;

	public UiWFlowDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoWFlow(), id);
		bmoWFlow = (BmoWFlow)getBmObject();
	}
	
	@Override
	public void populateLabels(){
		bmoWFlow = (BmoWFlow)getBmObject();
		addLabel(bmoWFlow.getName());
		addLabel(bmoWFlow.getCode());
		addLabel(bmoWFlow.getName());
		addLabel(bmoWFlow.getDescription());
				
		// Editar
		addActionLabel("Editar", bmoWFlow.getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiWFlow uiWFlow = new UiWFlow(getUiParams());
				setUiType(UiParams.SLAVE);
				uiWFlow.edit(bmoWFlow);
			}
		});
		
		// Detalle Módulos
		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoWFlow);
				BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
				UiProgramParams uiWFlowParams = getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode());
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
						bmoWFlowStep.getBmoWFlow().getIdFieldName(), 
						bmoWFlowStep.getBmoWFlow().getProgramLabel(), 
						BmFilter.EQUALS, 
						bmoWFlow.getId(), 
						bmoWFlow.getName().toString());
				uiWFlowParams.setForceFilter(bmFilter);
				uiWFlowStepList.show();
			}
		});
	}
	
	@Override
	public void close() {
		UiWFlow uiWFlowList = new UiWFlow(getUiParams());
		uiWFlowList.show();
	}
}