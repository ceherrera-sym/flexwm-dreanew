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

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiDevelopmentPhaseActiveList extends UiList {
	BmoDevelopmentPhase bmoDevelopmentPhase;

	public UiDevelopmentPhaseActiveList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoDevelopmentPhase());
		bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();	

		// Filtrar por proyectos no terminadas al 100%
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoDevelopmentPhase.getKind(), 
				bmoDevelopmentPhase.getIsActive().getName(),
				"1");
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(bmFilter);
	}

	@Override
	public void create() {
		UiDevelopmentPhase uiDevelopmentPhaseForm = new UiDevelopmentPhase(getUiParams());
		getUiParams().setUiType(new BmoDevelopmentPhase().getProgramCode(), UiParams.MASTER);
		uiDevelopmentPhaseForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoDevelopmentPhase = (BmoDevelopmentPhase)bmObject;
		getUiParams().setUiType(new BmoDevelopmentPhase().getProgramCode(), UiParams.MASTER);
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoDevelopmentPhase.getWFlowTypeId().toInteger() > 0) {
			UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), bmoDevelopmentPhase.getId());
			uiDevelopmentPhaseDetail.show();
		} else {
			UiDevelopmentPhase uiDevelopmentPhaseForm = new UiDevelopmentPhase(getUiParams());
			uiDevelopmentPhaseForm.edit(bmoDevelopmentPhase);
		}
	}

	//	@Override
	//	public void displayList() {
	//		int col = 0;
	//		
	//		listFlexTable.addListTitleCell(0, col++, "Abrir");
	//		listFlexTable.addListTitleCell(0, col++, "Clave");
	//		listFlexTable.addListTitleCell(0, col++, "Nombre");
	//		listFlexTable.addListTitleCell(0, col++, "Descripción");
	//		
	//		int row = 1;
	//		while (iterator.hasNext()) {
	//
	//		    BmoDevelopmentPhase cellBmObject = (BmoDevelopmentPhase)iterator.next(); 
	//		    
	//			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
	//			clickImage.setTitle("Abrir registro.");
	//			clickImage.addClickHandler(rowClickHandler);
	//			listFlexTable.setWidget(row, 0, clickImage);
	//			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
	//		    
	//		    col = 1;
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getName());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getDescription());
	//			listFlexTable.formatRow(row);
	//			row++;
	//		}
	//	}
}
