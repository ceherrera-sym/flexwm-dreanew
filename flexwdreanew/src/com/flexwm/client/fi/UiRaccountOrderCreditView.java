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
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDetailEastFlexTable;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiRaccountOrderCreditView extends Ui {
	NumberFormat numberFormat = NumberFormat.getCurrencyFormat();
	private int orderId;
	private BmoOrder bmoOrder;
	private double fail;
	private BmoRaccount bmoRaccount = new BmoRaccount();
	UiDetailEastFlexTable flexTable = new UiDetailEastFlexTable(getUiParams());

	public UiRaccountOrderCreditView(UiParams uiParams, Panel defaultPanel, String orderId) {
		super(uiParams, new BmoOrder(), defaultPanel);
		this.orderId = Integer.parseInt(orderId);
	}

	@Override
	public void show() {
		getOrder(orderId);
	}

	public void populateDetail(){
		bmoOrder = (BmoOrder)getBmObject();

		String title = "";
		title = "Resumen de CxC del Crédito";		
		flexTable.addTitleField(title);
		flexTable.addDetailField("Clave", bmoOrder.getCode().toString());		
		getOrderFailure();
		
		

		addToDP(flexTable);
	}

	public void populateRaccountData(String value) {
		flexTable.addDetailField("Provisionado", value, BmFieldType.CURRENCY);

		double provisioned = Math.round(Double.parseDouble(value));		
		double balance = Math.round(bmoOrder.getTotal().toDouble() - provisioned);

		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) 
			balance = 0;

		flexTable.addDetailField("Por Provisionar", "" + balance, BmFieldType.CURRENCY);

		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED))
			flexTable.addDetailField("Pedido", "Cancelado");
	}


	public void populateRaccountPayment(String value, double failure) {
		
	}

	public void populateRaccountFailure(String value) {
		double failure = 0;		
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) 
			failure = 0;
		else 
			failure = Double.parseDouble(value);	

		flexTable.addDetailField("Crédito", numberFormat.format(bmoOrder.getTotal().toDouble()));		
		flexTable.addDetailField("Penalización", numberFormat.format(failure));
		flexTable.addDetailField("", "--------");
		String total = numberFormat.format(bmoOrder.getTotal().toDouble() + failure);
		flexTable.addDetailField("Total", total);
		fail = failure;
		getOrderPayments();

	}

	public void populateOrderBalance(String value) {
		double balance = 0;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) 
			balance = 0;
		else 
			balance = Double.parseDouble(value);	

		flexTable.addDetailField("Saldo", "" +  numberFormat.format(balance));

	}

	public void populateOrderFailure(String value) {
		double failure = 0;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) 
			failure = 0;
		else 
			failure = Math.round(Double.parseDouble(value));	

		flexTable.addDetailField("Penalidad", "" + numberFormat.format(failure));

	}

	// Obtiene objeto del servicio
	public void getOrder(int id){

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getCustomer() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				setBmObject(result);
				populateDetail();
			}
		};

		// Llamada al servicio RPC
		try {
			getUiParams().getBmObjectServiceAsync().get(getBmObject().getPmClass(), id, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getCustomer() ERROR: " + e.toString());
		}
	}

	//Obtener el saldo del pedido
	public void getOrderProvision(){
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getOrdeBalance() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				populateRaccountData(result.getMsg());					
			}
		};

		try {
			if (!isLoading()) {
				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_ORDERPROVISION, "" + orderId, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getOrdeBalance() ERROR: " + e.toString());
		}
	} 

	//Obtener los pagos realizados
	public void getOrderPayments() {		
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getOrderPayments() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				double payments = 0;
				double balance = bmoOrder.getTotal().toDouble();
				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) 
					payments = 0;
				else 
					payments = Double.parseDouble(result.getMsg());	

				flexTable.addDetailField("Pagos", "" + numberFormat.format(payments));
				flexTable.addDetailField("Saldo", "" + numberFormat.format((balance + fail) - payments));
			}
		};

		try {
			if (!isLoading()) {
				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_ORDERPAYMENTS, "" + orderId, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getOrderPayments() ERROR: " + e.toString());
		}
	} 

	public void getOrderFailure(){
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getOrderFailure() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				populateRaccountFailure(result.getMsg());					
			}
		};

		try {
			if (!isLoading()) {
				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_ORDERFAILURE, "" + orderId, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getOrderFailure() ERROR: " + e.toString());
		}
	} 

}
