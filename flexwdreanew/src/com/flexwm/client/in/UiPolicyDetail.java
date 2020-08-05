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

import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.in.BmoPolicy;
import com.flexwm.shared.in.BmoPolicyPayment;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;


public class UiPolicyDetail extends UiDetail {
	protected BmoPolicy bmoPolicy;
	protected Label codeLabel = new Label();
	protected Label nameLabel = new Label();
	protected Label descriptionLabel = new Label();

	
	public UiPolicyDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoPolicy(), id);
		bmoPolicy = (BmoPolicy)getBmObject();
		
		getUiParams().getUiTemplate().hideEastPanel();
	}
	
	@Override
	public void populateLabels(){
		bmoPolicy = (BmoPolicy)getBmObject();
		addTitleLabel(bmoPolicy.getNumber());
		addLabel(bmoPolicy.getCode());
		addLabel(bmoPolicy.getStartDate());
		addLabel(bmoPolicy.getEndDate());	
		
		westTable.addActionLabel("Inicio", "start", new ClickHandler() {
			public void onClick(ClickEvent event) {
				showStart();
			}
		});

		// Forma de edicion de modulos
		westTable.addActionLabel("Editar", "poli", new ClickHandler() {
			public void onClick(ClickEvent event) {
				showEditForm();
			}
		});
		
		// Pagos
		westTable.addActionLabel(new BmoPolicyPayment(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showPolicyPayments();
			}
		});
	
		
		// Tareas
		westTable.addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});
		
		// Panel default inicial
		showStart();
	}
	
	@Override
	public void close() {
		// Se regresa a estatus de registro maestro
		getUiParams().setUiType(bmoPolicy.getProgramCode(), UiParams.MASTER);
		UiPolicyList uiPolicyList = new UiPolicyList(getUiParams());
		uiPolicyList.show();
	}
	
	public void showStart(){
		getUiParams().getUiTemplate().hideEastPanel();
		UiPolicyStart uiPolicyStart = new UiPolicyStart(getUiParams(), bmObjectProgramId, bmoPolicy);
		uiPolicyStart.show();
	}
	
	public void showEditForm() {
		setUiType(bmoPolicy.getProgramCode(), UiParams.DETAILFORM);
		UiPolicyForm uiPolicyForm = new UiPolicyForm(getUiParams(), id);
		uiPolicyForm.show();
	}
	
	public void showPolicyPayments(){
		BmoPolicyPayment bmoPolicyPayment = new BmoPolicyPayment();
		
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoPolicyPayment.getKind(), bmoPolicyPayment.getPolicyId(), bmoPolicy.getId()); 
		getUiParams().setForceFilter(bmoPolicyPayment.getProgramCode(), bmFilter);
		
		setUiType(bmoPolicyPayment.getProgramCode(), UiParams.SLAVE);
		
		UiPolicyPaymentList uiPolicyPaymentList = new UiPolicyPaymentList(getUiParams());		
		uiPolicyPaymentList.show();
	}
	
	public void showSteps() {
		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoPolicy.getBmoWFlow());
		
		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoPolicy.getWFlowId().toInteger(), 
				bmoPolicy.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}

}
