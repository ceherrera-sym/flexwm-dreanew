/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.op.BmoRequisition;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiRequisitionPendingList extends UiList {
	BmoRequisition bmoRequisition;
	protected BmFilter userFilter = new BmFilter();
	BmoUser bmoUser = new BmoUser();

	public UiRequisitionPendingList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();		
	}

	public UiRequisitionPendingList(UiParams uiParams, Panel defaultPanel, BmoUser bmoUser) {
		super(uiParams, defaultPanel, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();	
		this.bmoUser = bmoUser;		

		super.titleLabel.setHTML(getSFParams().getProgramListTitle(getBmObject()));

		// Elimina el filtro forzado, por ser llamado como SLAVE
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).removeForceFilter();
	}

	@Override
	public void postShow() {
		// Filtrar por ordenes de compra no autorizadas
		BmFilter filterRevision = new BmFilter();
		filterRevision.setValueFilter(bmoRequisition.getKind(), 
				bmoRequisition.getStatus(), "" + BmoRequisition.STATUS_REVISION);

		//getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setForceFilter(filterRevision);
		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(filterRevision);

		// Asignar usuario por defecto
		if (!(bmoUser.getId() > 0)) 
			bmoUser = getSFParams().getLoginInfo().getBmoUser();


		// Filtrar por el usuario que solicita la OC
		if (bmoUser.getId() > 0) {
			userFilter.setValueLabelFilter(bmoRequisition.getKind(), 
					bmoRequisition.getRequestedBy().getName(), 
					bmoRequisition.getRequestedBy().getLabel(), 
					BmFilter.EQUALS,
					bmoUser.getIdField().toString(),
					bmoUser.listBoxFieldsToString());
			getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(userFilter);
		}
	}
	
	@Override
	public void create() {
		UiRequisition uiRequisitionForm = new UiRequisition(getUiParams());
		uiRequisitionForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoRequisition = (BmoRequisition)bmObject;
		getUiParams().setUiType(new BmoRequisition().getProgramCode(), UiParams.MASTER);
		UiRequisition uiRequisitionForm = new UiRequisition(getUiParams());
		uiRequisitionForm.edit(bmoRequisition);
	}

	@Override
	public void displayList() {
		int col = 0;

		listFlexTable.addListTitleCell(0, col++, "Abrir");
		listFlexTable.addListTitleCell(0, col++, "Clave");
		listFlexTable.addListTitleCell(0, col++, "Nombre");
		listFlexTable.addListTitleCell(0, col++, "Proveedor");
		listFlexTable.addListTitleCell(0, col++, "Estatus");		
		listFlexTable.addListTitleCell(0, col++, "Total");

		int row = 1;
		while (iterator.hasNext()) {

			BmoRequisition cellBmObject = (BmoRequisition)iterator.next(); 

			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
			clickImage.setTitle("Abrir registro.");
			clickImage.addClickHandler(rowClickHandler);
			listFlexTable.setWidget(row, 0, clickImage);
			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");

			col = 1;
			listFlexTable.addListCell(row, col++, getBmObject(), cellBmObject.getCode());
			listFlexTable.addListCell(row, col++, getBmObject(), cellBmObject.getName());
			listFlexTable.addListCell(row, col++, getBmObject(), cellBmObject.getBmoSupplier().getName());
			listFlexTable.addListCell(row, col++, getBmObject(), cellBmObject.getStatus());
			listFlexTable.addListCell(row, col++, getBmObject(), cellBmObject.getTotal());
			listFlexTable.formatRow(row);
			row++;
		}
	}
}


