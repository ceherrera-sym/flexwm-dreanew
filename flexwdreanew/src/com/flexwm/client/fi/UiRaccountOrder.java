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
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;


public class UiRaccountOrder extends UiFormList {
	BmoRaccount bmoRaccount;

	TextBox invoicenoTextBox = new TextBox();
	TextBox folioTextBox = new TextBox();
	DateBox receiveDateBox = new DateBox();
	DateBox dueDateBox = new DateBox();
	TextArea descriptionTextArea = new TextArea();
	TextBox withdrawTextBox = new TextBox();
	TextBox depositTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();
	TextArea observationsTextArea = new TextArea();
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox raccountTypeListBox = new UiListBox(getUiParams(), new BmoRaccountType());	

	Button printReceiptButton = new Button("Recibo");

	int OrderId;

	BmoOrder bmoOrder;

	public UiRaccountOrder(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder) {
		super(uiParams, defaultPanel, new BmoRaccount());
		bmoRaccount = (BmoRaccount)getBmObject();
		this.bmoOrder = bmoOrder;
		OrderId = bmoOrder.getId();

		// Establecer propiedades de los botones
		printReceiptButton.setStyleName("formCloseButton");
		printReceiptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				printInvoice();
			}
		});

		// Lista solo los conceptos ligados al movimiento bancario
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoRaccount.getKind(), 
				bmoRaccount.getOrderId().getName(), 
				bmoRaccount.getOrderId().getLabel(), 
				BmFilter.EQUALS, 
				bmoOrder.getId(),
				bmoOrder.getIdFieldName());

		// Preparar filtro para mostrar los tipos de CxC validos
		BmoRaccountType bmoRaccountType = new BmoRaccountType();
		BmFilter bmFilterRaccountType = new BmFilter();
		bmFilterRaccountType.setValueFilter(bmoRaccountType.getKind(), 
				bmoRaccountType.getCategory(), 
				"" + BmoRaccountType.CATEGORY_ORDER);

		BmFilter bmFRAccWithDraw = new BmFilter();
		bmFRAccWithDraw.setValueFilter(bmoRaccountType.getKind(), 
				bmoRaccountType.getType(), 
				"" + BmoRaccountType.TYPE_WITHDRAW);

		raccountTypeListBox.addBmFilter(bmFilterRaccountType);
		raccountTypeListBox.addBmFilter(bmFRAccWithDraw);
	}

	@Override
	public void populateFields(){
		bmoRaccount = (BmoRaccount)getBmObject();	

		if (bmoRaccount.getBmoRaccountType().getType().toChar() == BmoRaccountType.TYPE_DEPOSIT) {
			formFlexTable.addLabelField(1, 0, "Tipo CxC", bmoRaccount.getBmoRaccountType().getName().toString());
		} else {
			formFlexTable.addField(1, 0, raccountTypeListBox, bmoRaccount.getRaccountTypeId());	
		}

		formFlexTable.addField(2, 0, invoicenoTextBox, bmoRaccount.getInvoiceno());
		formFlexTable.addField(2, 2, folioTextBox, bmoRaccount.getReference());

		formFlexTable.addField(3, 0, receiveDateBox, bmoRaccount.getReceiveDate());
		formFlexTable.addField(3, 2, dueDateBox, bmoRaccount.getDueDate());

		formFlexTable.addField(4, 0, descriptionTextArea, bmoRaccount.getDescription());
		formFlexTable.addLabelField(4, 2, "Por Ingresar:", "Cargando...");

		formFlexTable.addField(5, 0, statusListBox, bmoRaccount.getStatus());
		formFlexTable.addField(5, 2, totalTextBox, bmoRaccount.getTotal());

		statusEffect();
		getOrderBalance();
		buttonPanel.add(printReceiptButton);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoRaccount.setId(id);
		bmoRaccount.getReference().setValue(folioTextBox.getText());
		bmoRaccount.getInvoiceno().setValue(invoicenoTextBox.getText());		
		bmoRaccount.getReceiveDate().setValue(receiveDateBox.getTextBox().getText());
		bmoRaccount.getDueDate().setValue(dueDateBox.getTextBox().getText());		
		bmoRaccount.getDescription().setValue(descriptionTextArea.getText());		
		bmoRaccount.getTotal().setValue(totalTextBox.getText());		
		bmoRaccount.getRaccountTypeId().setValue(raccountTypeListBox.getSelectedId());
		bmoRaccount.getOrderId().setValue(bmoOrder.getId());
		bmoRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
		bmoRaccount.getStatus().setValue(statusListBox.getSelectedCode());

		setCompany();
		return bmoRaccount;
	}

	private void statusEffect(){
		// Deshabilita botones
		saveButton.setVisible(false);
		deleteButton.setVisible(false);
		printReceiptButton.setVisible(false);

		// Deshabilita campos
		raccountTypeListBox.setEnabled(false);
		invoicenoTextBox.setEnabled(false);
		dueDateBox.setEnabled(false);
		receiveDateBox.setEnabled(false);
		descriptionTextArea.setEnabled(false);
		totalTextBox.setEnabled(false);
		withdrawTextBox.setEnabled(false);
		depositTextBox.setEnabled(false);
		statusListBox.setEnabled(false);

		// Solo mostrar opciones de modificacion si no esta pagada parcial o totalmente la CxC
		if (bmoRaccount.getPaymentStatus().toChar() != BmoRaccount.PAYMENTSTATUS_TOTAL && 
				bmoRaccount.getStatus().toChar() != BmoRaccount.STATUS_AUTHORIZED) {

			// Modificacion de botones
			saveButton.setVisible(true);
			deleteButton.setVisible(true);

			// Habilita campos
			raccountTypeListBox.setEnabled(true);
			invoicenoTextBox.setEnabled(true);
			folioTextBox.setEnabled(true);
			dueDateBox.setEnabled(true);
			receiveDateBox.setEnabled(true);
			descriptionTextArea.setEnabled(true);
			totalTextBox.setEnabled(true);

			if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_CHANGESTATUS)) 
				statusListBox.setEnabled(true);
		}

		if (bmoRaccount.getId() > 0 && bmoRaccount.getBmoRaccountType().getType().toChar() != BmoRaccountType.TYPE_WITHDRAW) {			
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
		}

		if (bmoRaccount.getId() > 0)
			printReceiptButton.setVisible(true);
	}

	private void setCompany(){
		if (!(bmoRaccount.getCompanyId().toInteger() > 0)) {
			int companyId = getUiParams().getSFParams().getBmoSFConfig().getDefaultCompanyId().toInteger();
			try {
				bmoRaccount.getCompanyId().setValue(companyId);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-setCompany() " + e.toString());
			}
		}
	}

	// Muestra el saldo por crear de cuentas x cobrar
	public void updateOrderBalance(String balance) {
		NumberFormat numberFormat = NumberFormat.getCurrencyFormat();
		String formatted = numberFormat.format(Double.parseDouble(balance));

		formFlexTable.addLabelField(4, 2, "Por Ingresar:", formatted);
	}

	//Obtener el saldo de la orden de compra
	public void getOrderBalance(){
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				showErrorMessage(this.getClass().getName() + "-getReqiBalance() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				updateOrderBalance(result.getMsg());
			}
		};

		try {	
			getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_ORDERBALANCE, "" + bmoOrder.getId(), callback);
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getReqiBalance() ERROR: " + e.toString());
		}
	}

	private void printInvoice(){
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/frm/flex_raccountreceipt.jsp?foreignId=" + bmoRaccount.getId()), "raccountInvoice", "");
	}
}
