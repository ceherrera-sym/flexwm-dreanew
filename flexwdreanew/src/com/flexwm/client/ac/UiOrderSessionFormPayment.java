package com.flexwm.client.ac;

import java.sql.Types;

import java.util.ArrayList;

import com.flexwm.client.ac.UiOrderFormSession.OrderSessionUpdater;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;


public class UiOrderSessionFormPayment extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	
	// Raccount
	private BmoRaccount bmoRaccount = new BmoRaccount();
	private BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
	private FlowPanel orderSessionFormPanel = new FlowPanel();
	private CellTable<BmObject> orderSessionFormPaymentGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderSessionFormPaymentData;
	
	DialogBox RaccountDescriptionDialogBox;
	Button savePaymentButton = new Button("GUARDAR");
	Button closesessionTypeExtraDescriptionButton = new Button("CERRAR");
	
	ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

	private Button addRaccountButton = new Button("+PAGO");
	private Button addPrintButton = new Button("EDO.CUENTA");
	
	protected DialogBox RaccountDocumentDialogBox;

	// Cambio de sessionTypeExtra
	private HorizontalPanel buttonPanel = new HorizontalPanel();	
	
	private BmoOrder bmoOrder;
	private OrderSessionUpdater orderSessionUpdater;
		
	// Dialogo Pagar el Pedido
	DialogBox orderPaymentDialogBox;

	
	//private int paymentTypeId;
	//private String paymentTypeName;

	public UiOrderSessionFormPayment(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderSessionUpdater orderSessionUpdater){
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		this.orderSessionUpdater = orderSessionUpdater;

		// Boton de cerrar dialogo de personal
		savePaymentButton.setStyleName("formCloseButton");
		savePaymentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {				
				RaccountDescriptionDialogBox.hide();
			}
		});

		closesessionTypeExtraDescriptionButton.setStyleName("formCloseButton");
		closesessionTypeExtraDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				RaccountDescriptionDialogBox.hide();
			}
		});

		// Inicializar grid de Extra
		orderSessionFormPaymentGrid = new CellTable<BmObject>();
		orderSessionFormPaymentGrid.setWidth("100%");
		orderSessionFormPanel.clear();
		orderSessionFormPanel.setWidth("100%");
		//defaultPanel.setStyleName("detailStart");
		setRaccountColumns();
		
		//Filtar las CxC por Pedido
		BmFilter raccountOrderFilter = new BmFilter();
		raccountOrderFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getBmoRaccount().getOrderId().getName(), bmoOrder.getId());
		filterList.add(raccountOrderFilter);
		
		BmFilter raccountByType = new BmFilter();
		raccountByType.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType().getName(), "" + BmoRaccountType.TYPE_WITHDRAW);
		//filterList.add(raccountByType);
		
		
		
		orderSessionFormPaymentGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderSessionFormPaymentData = new UiListDataProvider<BmObject>(new BmoBankMovConcept(), filterList);
		orderSessionFormPaymentData.addDataDisplay(orderSessionFormPaymentGrid);
		orderSessionFormPanel.add(orderSessionFormPaymentGrid);

		// Panel de botones
		addPrintButton.setStyleName("formSaveButton");
		addPrintButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				printBalance();
			}
		});				
				
		addRaccountButton.setStyleName("formSaveButton");
		addRaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPayemtnMB();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addRaccountButton);
		buttonPanel.add(addPrintButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addSectionLabel(1, 0, "Pagos", 4);		
		formFlexTable.addPanel(3, 0, orderSessionFormPanel, 4);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderSessionFormPaymentData.list();
		orderSessionFormPaymentGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {			
			buttonPanel.setVisible(true);
		} 
	}

	public void reset(){
		orderSessionUpdater.changeOrder();
	}

	public void changeHeight() {
		orderSessionFormPaymentGrid.setPageSize(orderSessionFormPaymentData.getList().size());
		orderSessionFormPaymentGrid.setVisibleRange(0, orderSessionFormPaymentData.getList().size());
	}

	public void printBalance() {
		String pageUrl = "frm/flex_historyorderflotis.jsp?foreignid=" + bmoOrder.getId();
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), pageUrl), "_blank", "");
	}
	
	public void addPayemtnMB(){
		showMBPayment();
	}

	public void showMBPayment() {
		orderPaymentDialogBox = new DialogBox(true);
		orderPaymentDialogBox.setGlassEnabled(true);
		orderPaymentDialogBox.setText("Pagos");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("350px", "250px");

		orderPaymentDialogBox.setWidget(vp);

		UiOrderPaymentForm uiOrderPaymentForm = new UiOrderPaymentForm(getUiParams(), vp, bmoOrder.getId());
		uiOrderPaymentForm.show();

		orderPaymentDialogBox.center();
		orderPaymentDialogBox.show();
	}
	
	// Columnas grid de personal
	public void setRaccountColumns() {
		// Cuentas por Cobrar
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {				
				return ((BmoBankMovConcept)bmObject).getBmoBankMovement().getCode().toString();
			}
		};
		orderSessionFormPaymentGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("MB"));

		// Fecha
		Column<BmObject, String> dueDateColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoBankMovConcept)bmObject).getBmoBankMovement().getDueDate().toString();
			}
		};
		orderSessionFormPaymentGrid.addColumn(dueDateColumn, SafeHtmlUtils.fromSafeConstant("Fecha"));
		
		// Descripcion
		Column<BmObject, String> folioColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoBankMovConcept)bmObject).getBmoBankMovement().getDescription().toString();
			}
		};
		orderSessionFormPaymentGrid.addColumn(folioColumn, SafeHtmlUtils.fromSafeConstant("Descripción"));
		
		/*// Tipo de Pago
		Column<BmObject, String> paymentTypeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoRaccount)bmObject).getBmoPaymentType().getName().toString();
			}
		};
		orderSessionFormPaymentGrid.addColumn(paymentTypeColumn, SafeHtmlUtils.fromSafeConstant("Método Pago"));*/
		
		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format((((BmoBankMovConcept)bmObject).getAmount().toDouble()));
				return formatted;
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderSessionFormPaymentGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
		
		// Imprimir
		Column<BmObject, String> printColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "Imp.";
			}
		};
		printColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderSessionFormPaymentGrid.addColumn(printColumn, SafeHtmlUtils.fromSafeConstant("Imprimir"));
		printColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				int bankMovConceptId = ((BmoBankMovConcept)bmObject).getId();
				String amount = ((BmoBankMovConcept)bmObject).getAmount().toString();
				printReceipt(bankMovConceptId, amount);
			}
		});
		
		// Eliminar
		Column<BmObject, String> deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "-";
			}
		};
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderSessionFormPaymentGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				deletePayment((BmoBankMovConcept)bmObject);
			}
		});
	}
	

	public void deletePayment(BmoBankMovConcept bmoBankMovConcept) {		
		this.bmoBankMovConcept = bmoBankMovConcept;
		deletePayment();
	}

	public void deletePayment() {		
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deletesessionTypeExtra(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (!result.hasErrors())
					reset();
				else
					showErrorMessage("Error al Eliminar el Pago " + result.errorsToString());
			}
		}; 

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_DELETEPAYORDER, "" + bmoBankMovConcept.getId() ,callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deletesessionTypeExtra(): ERROR " + e.toString());
		}
	}

	public void processsessionTypeExtraChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Extra: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processsessionTypeExtraDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processsessionTypeExtraDelete() ERROR: " + result.errorsToString());
		this.reset();
	}	
	
	
	//Obtener la Cuenta de Banco
//	private void getBankAccount(String raccountId) {		
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-getBankAccount() ERROR: " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();
//				if (!result.hasErrors()) {					
//					//setColumns(result.getMsg());						
//				} else {
//					showErrorMessage("Error al obtener la cuenta de banco " + result.errorsToString());
//				}
//			}
//		};
//
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_GETBANKACCOUNT, raccountId, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-getBankAccount() ERROR: " + e.toString());
//		}
//	}
	
	//Imprimir el recibo
	private void printReceipt(int bankMovConceptId, String amount) {
		//if (!isLoading()) {			
			String pageUrl = "frm/flex_receiptorder.jsp?foreignid=" + bankMovConceptId + 
					         "&payment=" + amount;
			Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), pageUrl), "_blank", "");
		//}
	}

	private class UiOrderPaymentForm extends Ui {		
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

		NumberFormat numberFormat = NumberFormat.getCurrencyFormat();
		
		
		BmoRaccount bmoRaccount = new BmoRaccount();
		UiListBox bankAccountListBox = new UiListBox(getUiParams(), new BmoBankAccount());
		UiListBox raccountlistBox = new UiListBox(getUiParams(), new BmoRaccount());
		UiListBox paymentType = new UiListBox(getUiParams(), new BmoPaymentType());
		TextBox amountPayText = new TextBox();
		
		private BmField paymentTypeField;
		private BmField bankAccountField;
		private BmField raccountListField;
		private BmField amountField;
		
		private Button payButton = new Button("APLICAR");
		private Button closeButton = new Button("CERRAR");
		private Button printButton = new Button("IMPRIMIR");
		private HorizontalPanel buttonPanelPayment = new HorizontalPanel();

		private int orderId;

		public UiOrderPaymentForm(UiParams uiParams, Panel defaultPanel, int Id) {
			super(uiParams, defaultPanel);
			orderId = Id;
			
			paymentTypeField = new BmField("paymentType", "", "Tipo Pago", 11, Types.INTEGER, false, BmFieldType.ID, false);
			bankAccountField = new BmField("bankAccount", "", "Cta.Banco", 11, Types.INTEGER, false, BmFieldType.ID, false);
			raccountListField = new BmField("raccount", "", "CxC", 11, Types.INTEGER, false, BmFieldType.ID, false);
			amountField = new BmField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
			
			//Filtar las CxC del pedido
			BmFilter byOrder = new BmFilter();
			byOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), "" + orderId);
			
			BmFilter filterByPaymentStatus = new BmFilter();
			filterByPaymentStatus.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
			
			BmFilter justWithdraw = new BmFilter();
			justWithdraw.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), "" + BmoRaccountType.TYPE_WITHDRAW);
			
			
			raccountlistBox.addFilter(justWithdraw);
			raccountlistBox.addFilter(filterByPaymentStatus);			
			raccountlistBox.addFilter(byOrder);
			
			raccountlistBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {					
					showRaccAmount();					
				}
			});
			
			payButton.setStyleName("formSaveButton");
			payButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					payRaccOrder();					
				}
			});
			
			closeButton.setStyleName("formCloseButton");
			closeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					orderPaymentDialogBox.hide();										
				}
			});
			
			printButton.setStyleName("formSaveButton");
			printButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					printReceipt();					
				}
			});
			
			buttonPanelPayment.add(payButton);
			buttonPanelPayment.add(closeButton);
			buttonPanelPayment.add(printButton);
			printButton.setVisible(false);
			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			
			formTable.addField(1, 0, bankAccountListBox, bankAccountField);
			formTable.addField(2, 0, raccountlistBox, raccountListField);
			formTable.addField(3, 0, paymentType, paymentTypeField);
			formTable.addField(4, 0, amountPayText, amountField);
			getOrderBalance();
			
			showRaccAmount();
		}
		
		private void showRaccAmount() {
			if (Integer.parseInt(raccountlistBox.getSelectedId()) > 0) {
				BmoRaccount bmoRaccount = (BmoRaccount)raccountlistBox.getSelectedBmObject();			
				amountPayText.setText(bmoRaccount.getBalance().toString());
			}	
		}
		
		private void getOrderBalance() {			
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getOrderBalance() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					
					formTable.addLabelField(5, 0, "Saldo:" , numberFormat.format(Double.parseDouble(result.getMsg())));
					formTable.addButtonPanel(buttonPanelPayment);
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_ORDERBALANCE, "" + orderId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getOrderBalance() ERROR: " + e.toString());
			}
		}
		
		//Imprimir el recibo
		private void printReceipt() {
			//if (!isLoading()) {
				String pageUrl = "frm/flex_receiptorder.jsp?raccountid=" + raccountlistBox.getSelectedId() + 
						         "&payment=" + amountPayText.getText();
				Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), pageUrl), "_blank", "");
			//}
		}
		
		//Pagar el pedido
		private void payRaccOrder() {		
			payButton.setVisible(false);
			String values = "";
			values = orderId + "|" + bankAccountListBox.getSelectedId() + "|" + 
			         raccountlistBox.getSelectedId() + "|" + paymentType.getSelectedId() + "|" +  amountPayText.getText();
			
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-payOrder() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (!result.hasErrors()) {
						orderPaymentDialogBox.hide();
						reset();						
					} else {
						showErrorMessage("Error al realizar el pago del pedido " + result.errorsToString());
					}
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_PAYORDER, values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-payOrder() ERROR: " + e.toString());
			}
		}
		
	}	
	
}