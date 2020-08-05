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

import java.sql.Types;

import com.flexwm.client.cm.UiOpportunityDetail;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;

public class UiInsuranceStepPendingList extends UiList {
	BmoWFlowStep bmoWFlowStep;

	public UiInsuranceStepPendingList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoWFlowStep());
		bmoWFlowStep = (BmoWFlowStep)getBmObject();

		// Filtrar por tareas no terminadas al 100%
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoWFlowStep.getKind(), 
				bmoWFlowStep.getProgress().getName(),
				bmoWFlowStep.getProgress().getName(),
				BmFilter.MINOR,
				100,
				"< 100%"
				);

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(bmFilter);
	}

	@Override
	public void postShow(){
		// Filtrar por tareas de la categoría ligada al módulo de Oportunidaes			
		BmFilter activeFilter = new BmFilter();
		activeFilter.setValueFilter(bmoWFlowStep.getKind(),
				bmoWFlowStep.getEnabled(),
				"1");
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(activeFilter);
	}

	@Override
	public void open(BmObject bmObject) {		
		BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)bmObject;
		int opportunityProgramId = 0;
		try {
			opportunityProgramId = getSFParams().getProgramId(new BmoOpportunity().getProgramCode());
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-rowSelected() ERROR: " + e.toString());
		}

		// Es de tipo oportunidad
		if ((bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getProgramId().toInteger() == opportunityProgramId)) {
			UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoWFlowStep.getBmoWFlow().getCallerId().toInteger());
			uiOpportunityDetail.show();
		}
	}

	@Override
	public void displayList() {
		int col = 0;

		listFlexTable.addListTitleCell(0, col++, "Abrir");
		listFlexTable.addListTitleCell(0, col++, "Proy / Opor");
		listFlexTable.addListTitleCell(0, col++, "Tarea");
		listFlexTable.addListTitleCell(0, col++, "Tipo");
		listFlexTable.addListTitleCell(0, col++, "Nombre");
		listFlexTable.addListTitleCell(0, col++, "Grupo");
		listFlexTable.addListTitleCell(0, col++, "Inicio");
		listFlexTable.addListTitleCell(0, col++, "Avance");
		listFlexTable.addListTitleCell(0, col++, "Status");

		int row = 1;
		while (iterator.hasNext()) {
			BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)iterator.next(); 

			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
			clickImage.addClickHandler(rowClickHandler);
			listFlexTable.setWidget(row, 0, clickImage);
			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");

			BmField displayCode = new BmField("displaycode", "", "Clave Tarea", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
			try {
				displayCode.setValue(bmoWFlowStep.getBmoWFlowPhase().getSequence() + "." + bmoWFlowStep.getSequence() + " - " + bmoWFlowStep.getName());
			} catch (BmException e) {
				System.out.println(this.getClass().getName() + "-getDisplayFieldList(): " + e.toString());
			}

			col = 1;
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoWFlow().getCode());
			listFlexTable.addListCell(row, col++, getBmObject(), displayCode);
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getType());				
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getName());		    
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getBmoProfile().getName());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getStartdate());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getProgress());
			listFlexTable.addListCell(row, col++, getBmObject(), bmoWFlowStep.getEnabled());

			listFlexTable.formatRow(row);
			row++;
		}
	}

}


