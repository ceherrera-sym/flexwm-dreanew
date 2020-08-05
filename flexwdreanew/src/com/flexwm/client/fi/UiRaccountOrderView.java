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

import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDetailEastFlexTable;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiRaccountOrderView extends Ui {
	private int orderId;
	private BmoOrder bmoOrder;
	UiDetailEastFlexTable flexTable = new UiDetailEastFlexTable(getUiParams());
	private NumberFormat numberFormat = NumberFormat.getCurrencyFormat();
	private double sumTaxRaccs = 0, sumTaxRaccsPayments = 0, sumTaxRaccsPending = 0;


	public UiRaccountOrderView(UiParams uiParams, Panel defaultPanel, String orderId) {
		super(uiParams, new BmoOrder(), defaultPanel);
		this.orderId = Integer.parseInt(orderId);
	}

	public void show() {
		getOrder(orderId);		
	}

	// Obtiene el total de iva de cxc y total pagos
	public void getTaxRaccs(int orderId) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getTaxRaccs() ERROR: " + caught.toString());
			}
			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();

				if (!result.getMsg().equals(""))
					sumTaxRaccs = Double.parseDouble(result.getMsg());

				if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) 
					getTaxRaccsPayments(orderId);
				else
					populateDetail();
			}
		};

		// Llamada al servicio RPC
		try {
			startLoading();
			getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_GETTAXRACCS, ""+orderId, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getTaxRaccs() ERROR: " + e.toString());
		}
	}

	// Obtiene el total de iva de cxc abonadas
	public void getTaxRaccsPayments(int orderId) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getTaxRaccsPayments() ERROR: " + caught.toString());
			}
			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				//				double sumTaxRaccsPayments = 0;
				if (!result.getMsg().equals(""))
					sumTaxRaccsPayments = Double.parseDouble(result.getMsg());
				getTaxRaccsPending(orderId);
			}
		};

		// Llamada al servicio RPC
		try {
			startLoading();
			getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_GETTAXRACCSPAYMENTS, ""+orderId, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getTaxRaccsPayments() ERROR: " + e.toString());
		}
	}

	// Obtiene el total de iva de cxc sin pagos
	public void getTaxRaccsPending(int orderId) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getTaxRaccsPayments() ERROR: " + caught.toString());
			}
			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				//					double sumTaxRaccsPayments = 0;
				if (!result.getMsg().equals(""))
					sumTaxRaccsPending = Double.parseDouble(result.getMsg());

				populateDetail();
			}
		};

		// Llamada al servicio RPC
		try {
			startLoading();
			getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_GETTAXRACCSPENDING, ""+orderId, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getTaxRaccsPayments() ERROR: " + e.toString());
		}
	}

	// Obtiene objeto del servicio
	public void getOrder(int id) {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				setBmObject((BmObject)result);
				bmoOrder = (BmoOrder)getBmObject();
				getTaxRaccs(bmoOrder.getId());
			}
		};

		// Llamada al servicio RPC
		try {
			getUiParams().getBmObjectServiceAsync().get(getBmObject().getPmClass(), id, callback);
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-getOrder() ERROR: " + e.toString());
		}
	}

	public void populateDetail() {
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
		flexTable.addDetailField(bmoOrder.getTotalRaccounts());
		flexTable.addDetailField(bmoOrder.getTotalCreditNotes());
		if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			flexTable.addDetailField("IVA Cobrado", "" + numberFormat.format(sumTaxRaccsPayments));
			flexTable.addDetailField("IVA Pendiente", "" + numberFormat.format(sumTaxRaccsPending));
//			flexTable.addDetailField("IVA Total", "" + numberFormat.format(sumTaxRaccs));
			if (bmoOrder.getTaxApplies().toInteger() > 0)
				flexTable.addDetailField("Pagos CXC", "" + numberFormat.format(bmoOrder.getPayments().toDouble()));
			else 
				flexTable.addDetailField("Pagos CXC","" + numberFormat.format((bmoOrder.getPayments().toDouble() - sumTaxRaccsPayments)));
		} else {
			flexTable.addDetailField("IVA CXC", "" + numberFormat.format(sumTaxRaccs));
			
			if (bmoOrder.getTaxApplies().toInteger() > 0)
				flexTable.addDetailField("Pagos CXC", "" + numberFormat.format(bmoOrder.getPayments().toDouble()));
			else 
				flexTable.addDetailField("Pagos CXC","" + numberFormat.format((bmoOrder.getPayments().toDouble() - sumTaxRaccs)));
		}
		
//		if (bmoOrder.getTaxApplies().toInteger() > 0)
//			flexTable.addDetailField("Pagos CXC", "" + numberFormat.format(bmoOrder.getPayments().toDouble()));
//		else 
//			flexTable.addDetailField("Pagos CXC","" + numberFormat.format((bmoOrder.getPayments().toDouble() - sumTaxRaccs)));

		if(bmoOrder.getBmoOrderType().getType().toChar() != BmoOrderType.TYPE_RENTAL) {
			flexTable.addDetailField("Total Facturable", "" + numberFormat.format(bmoOrder.getTotalRaccounts().toDouble() - bmoOrder.getTotalCreditNotes().toDouble()));
			flexTable.addDetailField("Descto. Cobranza ", "" + numberFormat.format((bmoOrder.getAmount().toDouble() - bmoOrder.getDiscount().toDouble()) - bmoOrder.getTotalRaccounts().toDouble() - bmoOrder.getTotalCreditNotes().toDouble()));
		}
		flexTable.addDetailField("", "-----");
		if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			if (bmoOrder.getTaxApplies().toInteger() > 0)
				flexTable.addDetailField("Saldo",""+numberFormat.format(bmoOrder.getTotal().toDouble()-(bmoOrder.getPayments().toDouble())));
			else {
				flexTable.addDetailField("Saldo",""+numberFormat.format(bmoOrder.getTotal().toDouble()-(bmoOrder.getPayments().toDouble() - sumTaxRaccsPayments)));
			}

		}else {
			flexTable.addDetailField(bmoOrder.getBalance());
		}
		addToDP(flexTable);
	}
}
