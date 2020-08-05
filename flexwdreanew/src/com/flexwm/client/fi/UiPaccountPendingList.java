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

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.fi.BmoPaccount;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiPaccountPendingList extends UiList {
	BmoPaccount bmoPaccount;

	public UiPaccountPendingList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPaccount());
		bmoPaccount = (BmoPaccount)getBmObject();	

		// Filtrar por cuentas por cobrar no autorizadas
		BmFilter filterRevision = new BmFilter();
		filterRevision.setValueFilter(bmoPaccount.getKind(), 
				bmoPaccount.getStatus(), "" + BmoPaccount.STATUS_REVISION);

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(filterRevision);
	}

	@Override
	public void create() {
		UiPaccount uiPaccount = new UiPaccount(getUiParams());
		getUiParams().setUiType(new BmoPaccount().getProgramCode(), UiParams.MASTER);
		uiPaccount.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPaccount = (BmoPaccount)bmObject;
		getUiParams().setUiType(new BmoPaccount().getProgramCode(), UiParams.MASTER);
		UiPaccount uiPaccountForm = new UiPaccount(getUiParams());
		uiPaccountForm.edit(bmoPaccount);
	}

//	@Override
//	public void displayList() {
//		int col = 0;
//
//		listFlexTable.addListTitleCell(0, col++, "Abrir");
//		listFlexTable.addListTitleCell(0, col++, "Clave");
//		listFlexTable.addListTitleCell(0, col++, "Proveedor");
//		listFlexTable.addListTitleCell(0, col++, "Estatus");
//		listFlexTable.addListTitleCell(0, col++, "Monto");
//
//		int row = 1;
//		while (iterator.hasNext()) {
//
//			BmoPaccount cellBmObject = (BmoPaccount)iterator.next(); 
//
//			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
//			clickImage.setTitle("Abrir registro.");
//			clickImage.addClickHandler(rowClickHandler);
//			listFlexTable.setWidget(row, 0, clickImage);
//			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
//
//			col = 1;
//			listFlexTable.addListCell(row, col++, cellBmObject.getCode());
//			listFlexTable.addListCell(row, col++, cellBmObject.getBmoSupplier().getName());
//			listFlexTable.addListCell(row, col++, cellBmObject.getStatus());
//			listFlexTable.addListCell(row, col++, cellBmObject.getAmount());
//			listFlexTable.formatRow(row);
//			row++;
//		}
//	}
}