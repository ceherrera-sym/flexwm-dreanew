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

import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDetailEastFlexTable;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiRequisitionOrderView extends Ui {
	private int orderId;
	private BmoOrder bmoOrder;
	UiDetailEastFlexTable flexTable = new UiDetailEastFlexTable(getUiParams());
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

	public UiRequisitionOrderView(UiParams uiParams, Panel defaultPanel, String orderId) {
		super(uiParams, new BmoOrder(), defaultPanel);
		this.orderId = Integer.parseInt(orderId);
	}

	public void show() {
		getOrder(orderId);
	}

	// Obtiene objeto del servicio
	public void getOrder(int id){

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				setBmObject((BmObject)result);
				populateDetail();
			}
		};

		// Llamada al servicio RPC
		try {
			getUiParams().getBmObjectServiceAsync().get(getBmObject().getPmClass(), id, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getOrder() ERROR: " + e.toString());
		}
		
		// Inicializa formateo
		numberFormat = NumberFormat.getCurrencyFormat();
	}

	public void populateDetail(){
		bmoOrder = (BmoOrder)getBmObject();

		flexTable.addDetailField(bmoOrder.getCode());		
		flexTable.addDetailField(bmoOrder.getName());
		flexTable.addDetailField(bmoOrder.getBmoCurrency().getCode());
		flexTable.addDetailField(bmoOrder.getAmount());
		if (bmoOrder.getDiscount().toDouble() > 0)
			flexTable.addDetailField(bmoOrder.getDiscount());
		if (bmoOrder.getTax().toDouble() > 0)
			flexTable.addDetailField(bmoOrder.getTax());
		flexTable.addDetailField(bmoOrder.getTotal());
		flexTable.addDetailField("", "-----");
		
		getRequisitionSum();
		
		addToDP(flexTable);
	}
	
	//Obtener Nombre y Costo del Producto
	public void getRequisitionSum() {
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getNameCostProduct() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				double totalRequisitions = Double.parseDouble(result.getMsg());
				flexTable.addDetailField("Total O.C.", numberFormat.format(totalRequisitions));
				flexTable.addDetailField("Rentabilidad", numberFormat.format(bmoOrder.getTotal().toDouble() - totalRequisitions));				
			}
		};

		try {	
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_TOTALREQUISITIONS, "" + orderId, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getNameCostProduct() ERROR: " + e.toString());
		}
	}
}
