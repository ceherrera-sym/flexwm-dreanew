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



import com.flexwm.client.wf.UiWFlowStep;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.co.BmoWorkContractProperty;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.symgae.client.ui.UiDetail;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;


public class UiWorkContractDetail extends UiDetail {

	protected BmoWorkContract bmoWorkContract;	

	public UiWorkContractDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoWorkContract(), id);
		bmoWorkContract = (BmoWorkContract)getBmObject();
	}

	@Override
	public void populateLabels() {
		bmoWorkContract = (BmoWorkContract)getBmObject();
		addTitleLabel(bmoWorkContract.getName());
		addLabel(bmoWorkContract.getCode());
		addLabel(bmoWorkContract.getDescription());
		addLabel(bmoWorkContract.getBmoCompany().getName());
		addLabel(bmoWorkContract.getBmoSupplier().getName());
		addLabel(bmoWorkContract.getStartDate());
		addLabel(bmoWorkContract.getEndDate());
		addLabel(bmoWorkContract.getPaymentStatus());
		addLabel(bmoWorkContract.getStatus());
		
		addActionLabel("Inicio", bmoWorkContract.getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		// Editar oportunidad
		addActionLabel("Editar", "edit", new ClickHandler() {
			public void onClick(ClickEvent event) {
				showWorkContractForm();
			}
		});

		if (!bmoWorkContract.getStatus().equals(BmoWorkContract.STATUS_REVISION)) {
			// Contract Estimacion
			addActionLabel(new BmoContractEstimation(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					showContractEstimations();
				}
			});
		}

		// Viviendas
		addActionLabel(new BmoWorkContractProperty(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showWorkContractProperties();
			}
		});	
	
		addActionLabel(new BmoWFlowStep(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSteps();
			}
		});
		addActionLabel("Ir a Listado", "close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				close();
			}
		});

		//Panel default inicial
		showStart();
	}

	@Override
	public void close() {
		UiWorkContract uiWorkContractList = new UiWorkContract(getUiParams());
		setUiType(UiParams.MASTER);
		uiWorkContractList.show();
	}

	public void reset() {
		UiWorkContractDetail uiWorkContractDetail = new UiWorkContractDetail(getUiParams(), bmoWorkContract.getId());
		uiWorkContractDetail.show();
	}

	public void showStart() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWorkContractStart uiWorkContractStart = new UiWorkContractStart(getUiParams(), bmObjectProgramId, bmoWorkContract);
		uiWorkContractStart.show();
	}

	public void showWorkContractForm() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWorkContract uiWorkContractForm = new UiWorkContract(getUiParams());
		uiWorkContractForm.edit(bmoWorkContract);
	}

	public void showContractEstimations() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiContractEstimation uiContractEstimationList = new UiContractEstimation(getUiParams());
		BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getWorkContractId().getName(), bmoWorkContract.getId());
		getUiParams().getUiProgramParams(bmoContractEstimation.getProgramCode()).setForceFilter(bmFilter);
		setUiType(new BmoContractEstimation().getProgramCode(), UiParams.SLAVE);
		uiContractEstimationList.show();
	}

	//Viviendas
	public void showWorkContractProperties() {
		getUiParams().getUiTemplate().hideEastPanel();
		UiWorkContractShowProperty uiWorkContractShowProperty = new UiWorkContractShowProperty(getUiParams(), bmoWorkContract.getId());
		setUiType(UiParams.DETAILFORM);
		uiWorkContractShowProperty.show();

	}
	public void showSteps() {
		getUiParams().getUiTemplate().hideEastPanel();		
		

		UiWFlowStep uiWFlowStepList = new UiWFlowStep(getUiParams(), bmoWorkContract.getBmoWFlow());

		BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getWFlowId().getName(), 
				bmoWFlowStep.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWorkContract.getWFlowId().toInteger(), 
				bmoWorkContract.getBmoWFlow().getName().toString());
		getUiParams().getUiProgramParams(bmoWFlowStep.getProgramCode()).setForceFilter(bmFilter);
		setUiType(bmoWFlowStep.getProgramCode(), UiParams.SLAVE);
		uiWFlowStepList.show();
	}
	
}
