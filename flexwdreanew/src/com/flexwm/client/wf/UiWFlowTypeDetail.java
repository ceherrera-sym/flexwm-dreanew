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
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.wf.BmoWFlowDocumentType;
import com.flexwm.shared.wf.BmoWFlowFormat;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiWFlowTypeDetail extends UiDetail {
	protected BmoWFlowType bmoWFlowType;

	public UiWFlowTypeDetail(UiParams uiParams, int id) {
		super(uiParams, new BmoWFlowType(), id);
		bmoWFlowType = (BmoWFlowType)getBmObject();
		getUiParams().getUiTemplate().hideEastPanel();
	}
	
	@Override
	public void populateLabels(){
		bmoWFlowType = (BmoWFlowType)getBmObject();
		addTitleLabel(bmoWFlowType.getName());
		addLabel(bmoWFlowType.getName());
		addLabel(bmoWFlowType.getDescription());
		
		// Forma de edicion de modulos
		addActionLabel("Editar", bmoWFlowType.getProgramCode(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiWFlowType uiWFlowType = new UiWFlowType(getUiParams());
				setUiType(UiParams.DETAILFORM);
				uiWFlowType.edit(bmoWFlowType);
			}
		});
		
		// Tipos de pasos
		addActionLabel(new BmoWFlowStepType(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showStepTypeList();
			}
		});


		// Tipos de documentos
		addActionLabel(new BmoWFlowDocumentType(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDocumentTypeList();
			}
		});

		// Formatos
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean())) {	
			addActionLabel(new BmoWFlowFormat(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					showFormat();
				}
			});
		}

		addActionLabel("Ir a Listado", "close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				close();
			}
		});

		showStepTypeList();
	}
	
	@Override
	public void close() {
		UiWFlowType uiWFlowType = new UiWFlowType(getUiParams());
		setUiType(UiParams.MASTER);
		uiWFlowType.show();
	}
	
	public void showFormat() {
		UiWFlowFormat uiWFlowFormat = new UiWFlowFormat(getUiParams(),bmoWFlowType);
		BmoWFlowFormat bmoWFlowFormat = new BmoWFlowFormat();
		
		
//		UiWFlowDocumentType uiWFlowTypeDocumentTypeList = new UiWFlowDocumentType(getUiParams());
//		BmoWFlowDocumentType bmoWFlowTypeDocumentType = new BmoWFlowDocumentType();
		
		//Force filter Tipo de Flujo
		UiProgramParams uiWFlowTypeParams = getUiParams().getUiProgramParams(bmoWFlowFormat.getProgramCode());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowFormat.getKind(), bmoWFlowFormat.getWflowTypeId(), bmoWFlowType.getId());
		uiWFlowTypeParams.setForceFilter(bmFilter);
	
		
		setUiType(bmoWFlowFormat.getProgramCode(), UiParams.SLAVE);
		uiWFlowFormat.show();
		
	}
	
	public void showStepTypeList(){
		UiWFlowStepType uiWFlowTypeStepType = new UiWFlowStepType(getUiParams());
		BmoWFlowStepType bmoWFlowTypeStepType = new BmoWFlowStepType();
		
		//Force filter Tipo de Flujo
		UiProgramParams uiWFlowTypeParams = getUiParams().getUiProgramParams(bmoWFlowTypeStepType.getProgramCode());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowTypeStepType.getKind(), 
				bmoWFlowTypeStepType.getBmoWFlowType().getIdFieldName(), 
				bmoWFlowTypeStepType.getBmoWFlowType().getProgramLabel(), 
				BmFilter.EQUALS, 
				bmoWFlowType.getId(), 
				bmoWFlowType.getName().toString());
		uiWFlowTypeParams.setForceFilter(bmFilter);
		
		//Force filter Fase de Flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		UiProgramParams uiWFlowPhaseParams = getUiParams().getUiProgramParams(bmoWFlowPhase.getProgramCode());
		BmFilter bmFilterPhase = new BmFilter();
		bmFilterPhase.setValueFilter(bmoWFlowPhase.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(),
				bmoWFlowType.getWFlowCategoryId().toInteger());
		uiWFlowPhaseParams.setForceFilter(bmFilterPhase);
		
		setUiType(bmoWFlowTypeStepType.getProgramCode(), UiParams.SLAVE);
		uiWFlowTypeStepType.show();
	}
	
	public void showDocumentTypeList(){
		UiWFlowDocumentType uiWFlowTypeDocumentTypeList = new UiWFlowDocumentType(getUiParams());
		BmoWFlowDocumentType bmoWFlowTypeDocumentType = new BmoWFlowDocumentType();
		
		//Force filter Tipo de Flujo
		UiProgramParams uiWFlowTypeParams = getUiParams().getUiProgramParams(bmoWFlowTypeDocumentType.getProgramCode());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowTypeDocumentType.getKind(), 
				bmoWFlowTypeDocumentType.getBmoWFlowType().getIdFieldName(), 
				bmoWFlowTypeDocumentType.getBmoWFlowType().getProgramLabel(), 
				BmFilter.EQUALS, 
				bmoWFlowType.getId(), 
				bmoWFlowType.getName().toString());
		uiWFlowTypeParams.setForceFilter(bmFilter);
		
		//Force filter Fase de Flujo
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		UiProgramParams uiWFlowPhaseParams = getUiParams().getUiProgramParams(bmoWFlowPhase.getProgramCode());
		BmFilter bmFilterPhase = new BmFilter();
		bmFilterPhase.setValueFilter(bmoWFlowPhase.getKind(), 
				bmoWFlowPhase.getWFlowCategoryId().getName(),
				bmoWFlowType.getWFlowCategoryId().toInteger());
		uiWFlowPhaseParams.setForceFilter(bmFilterPhase);
		
		setUiType(bmoWFlowTypeDocumentType.getProgramCode(), UiParams.SLAVE);
		uiWFlowTypeDocumentTypeList.show();
	}
}