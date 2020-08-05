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
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoPropertySale;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiPropertySaleActiveList extends UiList {
	BmoPropertySale bmoPropertySale;
	UiSuggestBox userSuggestBox;
	BmoUser bmoUser = new BmoUser();

	public UiPropertySaleActiveList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPropertySale());
		bmoPropertySale = (BmoPropertySale)getBmObject();
	}

	@Override
	public void postShow() {
		// Filtro de Ventas En Revision
		BmFilter revisionFilter = new BmFilter();
		revisionFilter.setValueFilter(bmoPropertySale.getKind(), bmoPropertySale.getStatus(), "" + BmoPropertySale.STATUS_REVISION);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(revisionFilter);

		// Filtro por vendedor
		BmoUser bmoUser = new BmoUser();

		// Preparar filtro de ventas
		// Si tiene permiso de ver todas las ventas
		if (getUiParams().getSFParams().restrictData(bmoPropertySale.getProgramCode())) {
			// Si es version escritorio, colocar filtro de vendedor
			if (!isMobile()) {
				// Filtro de vendedores
				userSuggestBox = new UiSuggestBox(new BmoUser());
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterSalesmen = new BmFilter();
				int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + salesGroupId);	

				userSuggestBox.addFilter(filterSalesmen);

				// Filtrar por vendedores activos
				BmFilter filterSalesmenActive = new BmFilter();
				filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
				userSuggestBox.addFilter(filterSalesmenActive);

				addFilterSuggestBox(userSuggestBox, new BmoUser(), bmoPropertySale.getSalesUserId(), false);
			} else {
				// Es version movil, agregar filtro del usuario logeado
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueLabelFilter(bmoPropertySale.getKind(), 
						bmoUser.getIdFieldName(), 
						bmoUser.getProgramLabel(),
						"=",
						"" + getSFParams().getLoginInfo().getUserId(),
						getSFParams().getLoginInfo().getEmailAddress());
				getUiParams().getUiProgramParams(bmoPropertySale.getProgramCode()).addFilter(bmFilter);
			}
		} else {
			// Si no tiene permiso y es version movil, agregar filtro del usuario logeado
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueLabelFilter(bmoPropertySale.getKind(), 
					bmoUser.getIdFieldName(), 
					bmoUser.getProgramLabel(),
					"=",
					"" + getSFParams().getLoginInfo().getUserId(),
					getSFParams().getLoginInfo().getEmailAddress());
			getUiParams().getUiProgramParams(bmoPropertySale.getProgramCode()).addFilter(bmFilter);
		}
	}

	@Override
	public void create() {
		UiPropertySale uiPropertySaleForm = new UiPropertySale(getUiParams());
		getUiParams().setUiType(new BmoPropertySale().getProgramCode(), UiParams.MASTER);
		uiPropertySaleForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPropertySale = (BmoPropertySale)bmObject;
		getUiParams().setUiType(new BmoPropertySale().getProgramCode(), UiParams.MASTER);
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoPropertySale.getWFlowTypeId().toInteger() > 0) {
			UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), bmoPropertySale.getId());
			uiPropertySaleDetail.show();
		} else {
			UiPropertySale uiPropertySaleForm = new UiPropertySale(getUiParams());
			uiPropertySaleForm.edit(bmoPropertySale);
		}
	}

	//	@Override
	//	public void displayList() {
	//		int col = 0;
	//		
	//		listFlexTable.addListTitleCell(0, col++, "Abrir");
	//		listFlexTable.addListTitleCell(0, col++, "Clave");
	//		listFlexTable.addListTitleCell(0, col++, "Clave C.");
	//		listFlexTable.addListTitleCell(0, col++, "Cliente");
	//		listFlexTable.addListTitleCell(0, col++, "Inmueble");
	//		listFlexTable.addListTitleCell(0, col++, "Fase");
	//		listFlexTable.addListTitleCell(0, col++, "Avance");
	//		
	//		int row = 1;
	//		while (iterator.hasNext()) {
	//
	//		    BmoPropertySale cellBmObject = (BmoPropertySale)iterator.next(); 
	//		    
	//			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
	//			clickImage.setTitle("Abrir registro.");
	//			clickImage.addClickHandler(rowClickHandler);
	//			listFlexTable.setWidget(row, 0, clickImage);
	//			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
	//		    
	//		    col = 1;
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoCustomer().getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoCustomer().getDisplayName());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoProperty().getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoWFlow().getBmoWFlowPhase().getCode());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getBmoWFlow().getProgress());
	//			listFlexTable.formatRow(row);
	//			row++;
	//		}
	//	}
}


