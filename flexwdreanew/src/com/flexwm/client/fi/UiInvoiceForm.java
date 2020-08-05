/**
 * 
 */
package com.flexwm.client.fi;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoInvoice;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.shared.sf.BmoUser;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiDetailEastFlexTable;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;

/**
 * @author jhernandez
 *
 */
public class UiInvoiceForm extends UiForm {	
	TextBox codeTextBox = new TextBox();
	TextBox nameTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	
	DateBox dueDateBox = new DateBox();
	DateBox stampDatetimeDateBox = new DateBox();
	
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox orderListBox = new UiListBox(getUiParams(), new BmoOrder());
	
	TextBox amountTextBox = new TextBox();
	TextBox taxTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();
	
	TextBox folioTextBox = new TextBox();
	TextBox certStringTextBox = new TextBox();
	TextBox cfDisealTextBox = new TextBox();
	TextBox satSealTextBox = new TextBox();
	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
		
	BmoInvoice bmoInvoice;
	
	InvoiceUpdater invoiceUpdater = new InvoiceUpdater();
	
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();	
	protected FlowPanel formatPanel;

	protected UiDetailEastFlexTable eastTable = new UiDetailEastFlexTable(getUiParams());
	
	// Aplicaciones de pagos
	private UiInvoiceOrderDeliveryGrid uiInvoiceOrderDeliveryGrid;
	private FlowPanel uiInvoiceOrderDeliveryPanel = new FlowPanel();
	
	
	public UiInvoiceForm(UiParams uiParams, int id) {
		super(uiParams, new BmoInvoice(), id);
	}
	
	@Override
	public void populateFields() {
		bmoInvoice = (BmoInvoice)getBmObject();
		formFlexTable.addField(1, 0, codeTextBox, bmoInvoice.getCode());
		formFlexTable.addField(1, 2, nameTextBox, bmoInvoice.getName());		
		
		formFlexTable.addField(2, 0, descriptionTextArea, bmoInvoice.getDescription());
		formFlexTable.addField(2, 2, userSuggestBox, bmoInvoice.getUserId());

		formFlexTable.addField(3, 0, customerSuggestBox, bmoInvoice.getCustomerId());
		populateOrders(customerSuggestBox.getSelectedId());
		
		formFlexTable.addField(4, 0, dueDateBox, bmoInvoice.getDueDate());
		formFlexTable.addField(4, 2, stampDatetimeDateBox, bmoInvoice.getStampDatetime());
		
		// Si no esta asignada la moneda, buscar por la default
		if (!(bmoInvoice.getCurrencyId().toInteger() > 0)) {
			try {
				bmoInvoice.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
			} catch (BmException e) {
				showSystemMessage("No se puede asignar moneda : " + e.toString());
			}
		}
		formFlexTable.addField(5, 0, currencyListBox, bmoInvoice.getCurrencyId());
		formFlexTable.addField(5, 2, folioTextBox, bmoInvoice.getFolio());
		
		if (!newRecord) {
			formFlexTable.addPanel(6, 0, uiInvoiceOrderDeliveryPanel);
			uiInvoiceOrderDeliveryGrid = new UiInvoiceOrderDeliveryGrid(getUiParams(), uiInvoiceOrderDeliveryPanel, bmoInvoice, invoiceUpdater);
			uiInvoiceOrderDeliveryGrid.show();
			
			formFlexTable.addField(8, 0, paymentStatusListBox, bmoInvoice.getPaymentStatus());
			formFlexTable.addLabelField(8, 2, bmoInvoice.getAmount());
			
			formFlexTable.addField(9, 0, statusListBox, bmoInvoice.getStatus());
			formFlexTable.addLabelField(9, 2, bmoInvoice.getTax());
			
			formFlexTable.addLabelField(10, 2, bmoInvoice.getTotal());
		}
		
		statusEffect();
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoInvoice.setId(id);
		bmoInvoice.getCode().setValue(codeTextBox.getText());
		bmoInvoice.getName().setValue(nameTextBox.getText());		
		bmoInvoice.getDescription().setValue(descriptionTextArea.getText());
		bmoInvoice.getFolio().setValue(folioTextBox.getText());
		bmoInvoice.getDueDate().setValue(dueDateBox.getTextBox().getText());
		bmoInvoice.getStampDatetime().setValue(stampDatetimeDateBox.getTextBox().getText());
		bmoInvoice.getSatSeal().setValue(satSealTextBox.getText());
		bmoInvoice.getUserId().setValue(userSuggestBox.getSelectedId());
		bmoInvoice.getCustomerId().setValue(customerSuggestBox.getSelectedId());
		bmoInvoice.getCurrencyId().setValue(currencyListBox.getSelectedCode());
		bmoInvoice.getAmount().setValue(amountTextBox.getText());
		bmoInvoice.getTax().setValue(taxTextBox.getText());
		bmoInvoice.getTotal().setValue(totalTextBox.getText());
		bmoInvoice.getOrderId().setValue(orderListBox.getSelectedId());
		
		
		return bmoInvoice;
	}
	
	@Override
	public void close() {
		UiInvoiceList uiInvoicenoList = new UiInvoiceList(getUiParams());
		uiInvoicenoList.show();
	}
	
	public void statusEffect() {		
		amountTextBox.setEnabled(false);
		taxTextBox.setEnabled(false);
		totalTextBox.setEnabled(false);
		statusListBox.setEnabled(false);
		paymentStatusListBox.setEnabled(false);
		
		if (!newRecord && bmoInvoice.getStatus().equals(BmoInvoice.STATUS_REVISION)) {
			statusListBox.setEnabled(true);
		}
		
		
	}
	
	private void populateOrders(int customerId) {
		// Filtros de ordenes de compra
		// Se quitan elementos y filtros
		orderListBox.clear();
		orderListBox.clearFilters();
		
		BmoOrder bmoOrder = new BmoOrder();	
		
		
		if (newRecord) {						
			BmFilter bmFilterByCust = new BmFilter();
			bmFilterByCust.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), customerId );			
			orderListBox.addBmFilter(bmFilterByCust);
			formFlexTable.addField(3, 2, orderListBox, bmoInvoice.getOrderId());
			
		} else {			
			formFlexTable.addField(3, 2, orderListBox, bmoInvoice.getOrderId());
		}
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox) {			
			update("Desea cambiar el estatus de la factura?");
		}	
		statusEffect();
	}
	
	// Cambios en los SuggestBox
	@Override
	public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
		// Filtros de requisiones
		if (uiSuggestBox == customerSuggestBox) {								
			populateOrders(customerSuggestBox.getSelectedId());				
		}
		
		statusEffect();
	}
	
	private void updateAmount(int id){
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();				
				setAmount((BmoInvoice)result);
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoInvoice.getPmClass(), id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
		}
	}
	
	private void setAmount(BmoInvoice bmoInvoice) {
		numberFormat = NumberFormat.getCurrencyFormat();		
		amountTextBox.setText(numberFormat.format(bmoInvoice.getAmount().toDouble()));
		taxTextBox.setText(numberFormat.format(bmoInvoice.getTax().toDouble()));
		totalTextBox.setText(numberFormat.format(bmoInvoice.getTotal().toDouble()));
	}
	
	public void reset(){
		updateAmount(id);
		uiInvoiceOrderDeliveryGrid.show();		
	}
	
	protected class InvoiceUpdater {
		public void changeInvoice() {
			stopLoading();
			reset();
		}		
	}
} 

