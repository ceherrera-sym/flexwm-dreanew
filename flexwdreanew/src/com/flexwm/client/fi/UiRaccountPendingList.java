/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;

import com.flexwm.shared.fi.BmoRaccount;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiRaccountPendingList extends UiList {
	BmoRaccount bmoRaccount;

	public UiRaccountPendingList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoRaccount());
		bmoRaccount = (BmoRaccount)getBmObject();		

		// Filtrar por cuentas por cobrar no autorizadas
		BmFilter filterRevision = new BmFilter();
		filterRevision.setValueFilter(bmoRaccount.getKind(), 
				bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_REVISION);

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(filterRevision);
	}

	@Override
	public void create() {
		UiRaccount uiRaccount = new UiRaccount(getUiParams());
		getUiParams().setUiType(new BmoRaccount().getProgramCode(), UiParams.MASTER);
		uiRaccount.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoRaccount = (BmoRaccount)bmObject;
		getUiParams().setUiType(new BmoRaccount().getProgramCode(), UiParams.MASTER);
		UiRaccount uiRaccountForm = new UiRaccount(getUiParams());
		uiRaccountForm.edit(bmoRaccount);
	}

//	@Override
//	public void displayList() {
//		int col = 0;
//
//		listFlexTable.addListTitleCell(0, col++, "Abrir");
//		listFlexTable.addListTitleCell(0, col++, "Clave");
//		listFlexTable.addListTitleCell(0, col++, "Cliente");
//		listFlexTable.addListTitleCell(0, col++, "Estatus");
//		listFlexTable.addListTitleCell(0, col++, "Monto");
//
//		int row = 1;
//		while (iterator.hasNext()) {
//
//			BmoRaccount cellBmObject = (BmoRaccount)iterator.next(); 
//
//			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
//			clickImage.setTitle("Abrir registro.");
//			clickImage.addClickHandler(rowClickHandler);
//			listFlexTable.setWidget(row, 0, clickImage);
//			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
//
//			col = 1;
//			listFlexTable.addListCell(row, col++, cellBmObject.getCode());
//			listFlexTable.addListCell(row, col++, cellBmObject.getBmoCustomer().getDisplayName());
//			listFlexTable.addListCell(row, col++, cellBmObject.getStatus());
//			listFlexTable.addListCell(row, col++, cellBmObject.getTotal());
//			listFlexTable.formatRow(row);
//			row++;
//		}
//	}
}


