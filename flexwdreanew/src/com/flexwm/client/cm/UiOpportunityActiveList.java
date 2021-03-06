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

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.cm.BmoOpportunity;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiOpportunityActiveList extends UiList {
	BmoOpportunity bmoOpportunity;
	UiSuggestBox userSuggestBox;
	protected BmFilter userFilter = new BmFilter();
	
	BmoUser bmoUser = new BmoUser();
	
	public UiOpportunityActiveList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoOpportunity());
		bmoOpportunity = (BmoOpportunity)getBmObject();	
		
		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activas");
	}
	
	public UiOpportunityActiveList(UiParams uiParams, Panel defaultPanel, BmoUser bmoUser) {
		super(uiParams, defaultPanel, new BmoOpportunity());
		bmoOpportunity = (BmoOpportunity)getBmObject();	
		
		this.bmoUser = bmoUser;	
		
		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()) + " Activas");
		
		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();
	}
	
	@Override
	public void postShow() {
		// Filtro de Ventas En Revision
		BmFilter revisionFilter = new BmFilter();
		revisionFilter.setValueFilter(bmoOpportunity.getKind(), 
				bmoOpportunity.getStatus(), 
				"" + BmoOpportunity.STATUS_REVISION);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(revisionFilter);
		
		// Filtro de vendedores
//		BmoUser bmoUser = new BmoUser();
//		BmFilter bmFilter = new BmFilter();
//		userSuggestBox = new UiSuggestBox(new BmoUser());
//
//		if (getUiParams().getSFParams().restrictData(bmoOpportunity.getProgramCode()) == BmoSFComponentAccess.DISCLOSURE_ALL) {
//			bmFilter.setValueLabelFilter(bmoOpportunity.getKind(), 
//					bmoOpportunity.getUserId().getName(), 
//					bmoOpportunity.getUserId().getLabel(),
//					"=",
//					"" + getSFParams().getLoginInfo().getUserId(),
//					getSFParams().getLoginInfo().getEmailAddress());
//			getUiParams().getUiProgramParams(bmoOpportunity.getProgramCode()).addFilter(bmFilter);
//			
//			// Filtrar por vendedores
//			BmoProfileUser bmoProfileUser = new BmoProfileUser();
//			BmFilter filterSalesmen = new BmFilter();
//			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
//			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
//									bmoUser.getIdFieldName(),
//									bmoProfileUser.getUserId().getName(),
//									bmoProfileUser.getProfileId().getName(),
//									"" + salesGroupId);	
//			
//			userSuggestBox.addFilter(filterSalesmen);
//
//			// Filtrar por vendedores activos
//			BmFilter filterSalesmenActive = new BmFilter();
//			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
//			userSuggestBox.addFilter(filterSalesmenActive);
//			
//			addFilterSuggestBox(userSuggestBox, new BmoUser(), bmoOpportunity.getUserId());
//		} else {
//			// Preparar filtro default de usuario loggeado
//			bmFilter.setValueLabelFilter(bmoOpportunity.getKind(), 
//					bmoOpportunity.getUserId().getName(), 
//					bmoOpportunity.getUserId().getLabel(),
//					"=",
//					"" + getSFParams().getLoginInfo().getUserId(),
//					getSFParams().getLoginInfo().getEmailAddress());
//			getUiParams().getUiProgramParams(bmoOpportunity.getProgramCode()).addFilter(bmFilter);
//		}
		
		if (bmoUser.getId() > 0) {
			userFilter.setValueLabelFilter(bmoOpportunity.getKind(), 
					bmoOpportunity.getUserId().getName(), 
					bmoOpportunity.getUserId().getLabel(), 
					BmFilter.EQUALS,
					bmoUser.getIdField().toString(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		}
	}
	
	@Override
	public void create() {
		UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
		getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
		uiOpportunity.create();
	}
	
	@Override
	public void open(BmObject bmObject) {
		bmoOpportunity = (BmoOpportunity)bmObject;
		getUiParams().setUiType(new BmoOpportunity().getProgramCode(), UiParams.MASTER);
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
			UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoOpportunity.getId());
			uiOpportunityDetail.show();
		} else {
			UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
			uiOpportunity.edit(bmoOpportunity);
		}
	}
		
//	@Override
//	public void displayList() {
//		int col = 0;
//		
//		listFlexTable.addListTitleCell(0, col++, "Abrir");
//		listFlexTable.addListTitleCell(0, col++, "Clave");
//		listFlexTable.addListTitleCell(0, col++, "Nombre");
//		listFlexTable.addListTitleCell(0, col++, "Cliente");
//		listFlexTable.addListTitleCell(0, col++, "Fase");
//		listFlexTable.addListTitleCell(0, col++, "Avance");
//		
//		int row = 1;
//		while (iterator.hasNext()) {
//
//		    BmoOpportunity cellBmObject = (BmoOpportunity)iterator.next(); 
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
//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoCustomer().getDisplayName());
//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoWFlow().getBmoWFlowPhase().getCode());
//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoWFlow().getProgress());
//			listFlexTable.formatRow(row);
//			row++;
//		}
//	}
}


