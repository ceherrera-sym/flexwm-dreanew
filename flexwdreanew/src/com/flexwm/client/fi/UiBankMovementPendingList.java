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
import com.flexwm.shared.fi.BmoBankMovement;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiBankMovementPendingList extends UiList {
	BmoBankMovement bmoBankMovement;

	public UiBankMovementPendingList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoBankMovement());
		bmoBankMovement = (BmoBankMovement)getBmObject();	

		// Filtrar por cuentas por cobrar no autorizadas
		BmFilter filterRevision = new BmFilter();
		filterRevision.setValueFilter(bmoBankMovement.getKind(), 
				bmoBankMovement.getStatus(), "" + BmoBankMovement.STATUS_REVISION);

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(filterRevision);
	}

	@Override
	public void create() {
		UiBankMovement uiBankMovement = new UiBankMovement(getUiParams());
		getUiParams().setUiType(new BmoBankMovement().getProgramCode(), UiParams.MASTER);
		uiBankMovement.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoBankMovement = (BmoBankMovement)bmObject;
		getUiParams().setUiType(new BmoBankMovement().getProgramCode(), UiParams.MASTER);
		UiBankMovement uiBankMovementForm = new UiBankMovement(getUiParams());
		uiBankMovementForm.edit(bmoBankMovement);
	}

	//	@Override
	//	public void displayList() {
	//		int col = 0;
	//		
	//		listFlexTable.addListTitleCell(0, col++, "Abrir");
	//		listFlexTable.addListTitleCell(0, col++, "Clave");
	//		listFlexTable.addListTitleCell(0, col++, "Proveedor/Cliente");
	//		listFlexTable.addListTitleCell(0, col++, "Estatus");
	//		listFlexTable.addListTitleCell(0, col++, "Monto");
	//		
	//		int row = 1;
	//		while (iterator.hasNext()) {
	//
	//			BmoBankMovement cellBmObject = (BmoBankMovement)iterator.next(); 
	//		    
	//			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
	//			clickImage.setTitle("Abrir registro.");
	//			clickImage.addClickHandler(rowClickHandler);
	//			listFlexTable.setWidget(row, 0, clickImage);
	//			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
	//		    
	//		    col = 1;
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getCode());
	//		    if (cellBmObject.getCustomerId().toInteger() > 0) {
	//		    	listFlexTable.addListCell(row, col++, cellBmObject.getBmoCustomer().getDisplayName());
	//		    } else if (cellBmObject.getSupplierId().toInteger() > 0) {
	//		    		listFlexTable.addListCell(row, col++, cellBmObject.getBmoSupplier().getName());
	//    		} else {
	//    			listFlexTable.addListCell(row, col++, cellBmObject.getDescription().toString());
	//    		}
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getStatus());
	//		    listFlexTable.addListCell(row, col++, cellBmObject.getWithdraw());
	//			listFlexTable.formatRow(row);
	//			row++;
	//		}
	//	}
}
