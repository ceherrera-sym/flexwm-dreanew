package com.flexwm.client.fi;


import java.util.ArrayList;
import com.flexwm.client.fi.UiInvoiceForm.InvoiceUpdater;
import com.flexwm.shared.fi.BmoInvoiceOrderDelivery;
import com.flexwm.shared.fi.BmoInvoice;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiInvoiceOrderDeliveryGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	
	// InvoiceOrderDeliverys
	private BmoInvoiceOrderDelivery bmoInvoiceOrderDelivery = new BmoInvoiceOrderDelivery();
	private FlowPanel InvoiceOrderDeliveryPanel = new FlowPanel();
	private CellTable<BmObject> invoiceOrderDeliveryGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> invoiceOrderDeliveryData;
	BmFilter InvoiceOrderDeliveryFilter;

	private Button addInvoiceOrderDeliveryButton = new Button("+ item");
	protected DialogBox invoiceOrderDeliveryDialogBox;	

	
	
	DialogBox changeinvoiceOrderDeliveryDialogBox;
	Button changeInvoiceOrderDeliveryButton = new Button("Cambiar");
	Button closeInvoiceOrderDeliveryButton = new Button("Cerrar");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();	
	private BmoInvoice bmoInvoice;	
	private InvoiceUpdater invoiceUpdater;
	
	BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
	BmoOrder bmoOrder = new BmoOrder();


	public UiInvoiceOrderDeliveryGrid(UiParams uiParams, Panel defaultPanel, BmoInvoice bmoInvoice, InvoiceUpdater invoiceUpdater){
		super(uiParams, defaultPanel);
		this.bmoInvoice = bmoInvoice;
		this.invoiceUpdater = invoiceUpdater;		

		closeInvoiceOrderDeliveryButton.setStyleName("formCloseButton");
		closeInvoiceOrderDeliveryButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeinvoiceOrderDeliveryDialogBox.hide();
			}
		});


		// Inicializar grid de Personal
		invoiceOrderDeliveryGrid = new CellTable<BmObject>();
		invoiceOrderDeliveryGrid.setWidth("100%");
		InvoiceOrderDeliveryPanel.clear();
		InvoiceOrderDeliveryPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setInvoiceOrderDeliveryColumns();
		InvoiceOrderDeliveryFilter = new BmFilter();
		InvoiceOrderDeliveryFilter.setValueFilter(bmoInvoiceOrderDelivery.getKind(), bmoInvoiceOrderDelivery.getInvoiceId().getName(), bmoInvoice.getId());
		invoiceOrderDeliveryGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		invoiceOrderDeliveryData = new UiListDataProvider<BmObject>(new BmoInvoiceOrderDelivery(), InvoiceOrderDeliveryFilter);
		invoiceOrderDeliveryData.addDataDisplay(invoiceOrderDeliveryGrid);
		InvoiceOrderDeliveryPanel.add(invoiceOrderDeliveryGrid);

		// Panel de botones
		addInvoiceOrderDeliveryButton.setStyleName("formSaveButton");
		addInvoiceOrderDeliveryButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addInvoiceOrderDeliveryButton();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);					
		buttonPanel.add(addInvoiceOrderDeliveryButton);
		
		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, InvoiceOrderDeliveryPanel);		
		formFlexTable.addButtonPanel(buttonPanel);
				
		
		defaultPanel.add(formFlexTable);
	}
	
	public void show(){
		invoiceOrderDeliveryData.list();
		invoiceOrderDeliveryGrid.redraw();

		statusEffect();
	}
	
	public void addInvoiceOrderDeliveryButton() {
		invoiceOrderDeliveryDialogBox = new DialogBox(true);
		invoiceOrderDeliveryDialogBox.setGlassEnabled(true);
		invoiceOrderDeliveryDialogBox.setText("Items");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		invoiceOrderDeliveryDialogBox.setWidget(vp);

		UiInvoiceOrderDeliveryForm InvoiceOrderDeliveryForm = new UiInvoiceOrderDeliveryForm(getUiParams(), vp, bmoInvoice);
		InvoiceOrderDeliveryForm.show();

		invoiceOrderDeliveryDialogBox.center();
		invoiceOrderDeliveryDialogBox.show();
	}

	public void setInvoiceOrderDeliveryColumns() {
		// Cantidad
		Column<BmObject, String> paccountCodeColumn;		
		paccountCodeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoInvoiceOrderDelivery)bmObject).getCode().toString();
			}
		};		
		invoiceOrderDeliveryGrid.addColumn(paccountCodeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		paccountCodeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		invoiceOrderDeliveryGrid.setColumnWidth(paccountCodeColumn, 50, Unit.PX);

		// Cantidad
		Column<BmObject, String> amountColumn;		
		amountColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format((((BmoInvoiceOrderDelivery)bmObject).getAmount().toDouble()));
				return formatted;
			}
			
		};		
		invoiceOrderDeliveryGrid.addColumn(amountColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
		amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		invoiceOrderDeliveryGrid.setColumnWidth(amountColumn, 50, Unit.PX);		

		
		// Eliminar	
		Column<BmObject, String> deleteColumn;
		if (bmoInvoice.getStatus().equals(BmoInvoice.STATUS_REVISION)) {
			deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "-";
				}
			};
			deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					deleteItem(bmObject);
				}
			});
		} else {
			deleteColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return " ";
				}
			};
		}
		
		
		invoiceOrderDeliveryGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		invoiceOrderDeliveryGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
	}

	public void statusEffect(){
		
		addInvoiceOrderDeliveryButton.setVisible(true);
			
	}

	public void reset(){
		invoiceUpdater.changeInvoice();
	}

	public void changeHeight() {
		invoiceOrderDeliveryGrid.setVisibleRange(0, invoiceOrderDeliveryData.getList().size());
	}
	
	public void processItemDelete(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();			
		}		
	}

	
	private void deleteItem(BmObject bmObject) {
		bmoInvoiceOrderDelivery = (BmoInvoiceOrderDelivery)bmObject;
		deleteItem();		
	}

	public void deleteItem() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemDelete(result);
				reset();
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoInvoiceOrderDelivery.getPmClass(), bmoInvoiceOrderDelivery, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addInvoiceOrderDelivery() {
		invoiceOrderDeliveryDialogBox = new DialogBox(true);
		invoiceOrderDeliveryDialogBox.setGlassEnabled(true);
		invoiceOrderDeliveryDialogBox.setText("Items");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "430px");

		invoiceOrderDeliveryDialogBox.setWidget(vp);

		UiInvoiceOrderDeliveryForm InvoiceOrderDeliveryForm = new UiInvoiceOrderDeliveryForm(getUiParams(), vp, bmoInvoice);
		InvoiceOrderDeliveryForm.show();

		invoiceOrderDeliveryDialogBox.center();
		invoiceOrderDeliveryDialogBox.show();
	}

	// Agrega un item de un producto a la CXP
	private class UiInvoiceOrderDeliveryForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private UiSuggestBox orderDeliverySuggestBox = new UiSuggestBox(new BmoOrderDelivery());
		private BmoInvoiceOrderDelivery bmoInvoiceOrderDelivery;		
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoInvoice bmoInvoice;
		

		public UiInvoiceOrderDeliveryForm(UiParams uiParams, Panel defaultPanel, BmoInvoice bmoInvoice) {
			super(uiParams, defaultPanel);
			this.bmoInvoice = bmoInvoice;
			this.bmoInvoiceOrderDelivery = new BmoInvoiceOrderDelivery();
							

			try {
				bmoInvoiceOrderDelivery.getInvoiceId().setValue(bmoInvoice.getId());

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
			}

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(true);
			buttonPanel.add(saveButton);
			
			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
					@Override
					public void onSelect(UiSuggestBox uiSuggestBox) {
						formSuggestionChange(uiSuggestBox);
					}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);		
			
			
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByOrder = new BmFilter();
			filterByOrder.setValueFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getOrderId(), bmoInvoice.getOrderId().toInteger());
			filterList.add(filterByOrder);
			
			BmFilter filterByPaymentStatus = new BmFilter();
			filterByPaymentStatus.setValueOperatorFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getBmoOrder().getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoOrder.PAYMENTSTATUS_TOTAL);
			filterList.add(filterByPaymentStatus);
			
			orderDeliverySuggestBox = new UiSuggestBox(new BmoOrderDelivery());
			orderDeliverySuggestBox.addFilter(filterByOrder);
			orderDeliverySuggestBox.addFilter(filterByPaymentStatus);
			

			defaultPanel.add(formTable);
		}
		
		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
				
			bmoOrderDelivery = (BmoOrderDelivery)orderDeliverySuggestBox.getSelectedBmObject();
			
			formTable.addButtonPanel(buttonPanel);
		}
		
		
		public void show() {			
			//Validar que la CxP y el MB tengan el mismo tipo de monenda
			formTable.addField(1, 0, orderDeliverySuggestBox, bmoInvoiceOrderDelivery.getOrderDeliveryId());
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				invoiceOrderDeliveryDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoInvoiceOrderDelivery = new BmoInvoiceOrderDelivery();
				bmoInvoiceOrderDelivery.getInvoiceId().setValue(bmoInvoice.getId());				
				bmoInvoiceOrderDelivery.getOrderDeliveryId().setValue(orderDeliverySuggestBox.getSelectedId());			
								
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoInvoiceOrderDelivery.getPmClass(), bmoInvoiceOrderDelivery, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}
	
	

}
