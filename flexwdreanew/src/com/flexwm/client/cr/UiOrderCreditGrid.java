package com.flexwm.client.cr;

import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.cr.BmoOrderCredit;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiOrderCreditGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderCredit
	private BmoOrderCredit bmoOrderCredit = new BmoOrderCredit();
	private FlowPanel orderCreditPanel = new FlowPanel();
	private CellTable<BmObject> orderCreditGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderCreditData;
	BmFilter orderCreditFilter;

	protected DialogBox orderCreditDialogBox;	

	// Otros
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private OrderUpdater orderUpdater;

	BmoOrder bmoOrder = new BmoOrder();

	public UiOrderCreditGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderUpdater orderUpdater){
		super(uiParams, defaultPanel);
		this.orderUpdater = orderUpdater;
		this.bmoOrder = bmoOrder;

		// Inicializar grid de Inmueble
		orderCreditGrid = new CellTable<BmObject>();
		orderCreditGrid.setWidth("100%");
		orderCreditPanel.clear();
		orderCreditPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setOrderCreditColumns();
		orderCreditFilter = new BmFilter();
		orderCreditFilter.setValueFilter(bmoOrderCredit.getKind(), bmoOrderCredit.getOrderId().getName(), bmoOrder.getId());
		orderCreditGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderCreditData = new UiListDataProvider<BmObject>(new BmoOrderCredit(), orderCreditFilter);
		orderCreditData.addDataDisplay(orderCreditGrid);
		orderCreditPanel.add(orderCreditGrid);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, orderCreditPanel);
//		formFlexTable.addButtonPanel(buttonPanel);
		formFlexTable.addFieldEmpty(4, 0);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderCreditData.list();
		orderCreditGrid.redraw();
		
		statusEffect();
	}
	
	public void statusEffect(){
//		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED) {
//			addOrderCreditButton.setVisible(false);
//		} else {
//			addOrderCreditButton.setVisible(true);
//		}
	}

	public void reset(){
		orderUpdater.changeOrderCredit();
	}

	public void changeHeight() {
		orderCreditGrid.setPageSize(orderCreditData.getList().size());
		orderCreditGrid.setVisibleRange(0, orderCreditData.getList().size());
	}

	// Columnas grid de personal
	public void setOrderCreditColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		quantityColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderCredit)bmObject).getQuantity().toString();
			}
		};
		orderCreditGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		orderCreditGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderCredit)bmObject).getName().toString();
			}
		};
		orderCreditGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		orderCreditGrid.setColumnWidth(nameColumn, 200, Unit.PX);
		
		// Description
		Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderCredit)bmObject).getDescription().toString();
			}
		};
		orderCreditGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Descripción"));
		orderCreditGrid.setColumnWidth(descriptionColumn, 200, Unit.PX);

		// Precio
		Column<BmObject, String> priceColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderCredit)bmObject).getPrice().toDouble());
				return (formatted);
			}
		};
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderCreditGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		
		// Interes
		Column<BmObject, String> interestColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderCredit)bmObject).getInterest().toDouble());
				return (formatted);
			}
		};
		interestColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderCreditGrid.addColumn(interestColumn, SafeHtmlUtils.fromSafeConstant("Interes"));

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderCredit)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderCreditGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
	}

	public void addOrderCredit(){
		addOrderCredit(new BmoOrderCredit());
	}

	public void addOrderCredit(BmoOrderCredit bmoOrderCredit) {
		orderCreditDialogBox = new DialogBox(true);
		orderCreditDialogBox.setGlassEnabled(true);
		orderCreditDialogBox.setText("Extra de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		orderCreditDialogBox.setWidget(vp);

		UiOrderCreditForm OrderCreditForm = new UiOrderCreditForm(getUiParams(), vp, bmoOrder, bmoOrderCredit);

		OrderCreditForm.show();

		orderCreditDialogBox.center();
		orderCreditDialogBox.show();
	}

	// Agrega un paquete de sesions
	private class UiOrderCreditForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextArea commentsTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox orderCreditListBox = new UiListBox(getUiParams(), new BmoOrderCredit());

		private Button saveButton = new Button("Guardar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoOrder bmoOrder;
		private BmoOrderCredit bmoOrderCredit = new BmoOrderCredit();

		public UiOrderCreditForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoOrderCredit bmoOrderCredit) {
			super(uiParams, defaultPanel);
			this.bmoOrderCredit = new BmoOrderCredit();
			this.bmoOrder = bmoOrder;
			this.bmoOrderCredit = bmoOrderCredit;
			
			ChangeHandler listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChange(event);
				}
			};
			formTable.setListChangeHandler(listChangeHandler);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrderCredit.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
			
		}

		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoOrderCredit.getQuantity().setValue(1);
				bmoOrderCredit.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			formTable.addField(1, 0, orderCreditListBox, bmoOrderCredit.getIdField());
			formTable.addField(2, 0, quantityTextBox, bmoOrderCredit.getQuantity());
			formTable.addField(3, 0, priceTextBox, bmoOrderCredit.getPrice());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formListChange(ChangeEvent event) {
			
			statusEffect();
		}

		private void statusEffect() {
			if (!orderCreditListBox.getSelectedId().equals("") && !orderCreditListBox.getSelectedId().equals("0")) {
				bmoOrderCredit = (BmoOrderCredit)orderCreditListBox.getSelectedBmObject();
				quantityTextBox.setEnabled(false);
				quantityTextBox.setText("1");
				priceTextBox.setEnabled(false);	
			} else {
				priceTextBox.setText("");
				priceTextBox.setEnabled(false);
				quantityTextBox.setText("");
				quantityTextBox.setEnabled(false);
				commentsTextArea.setText("");
			}
		}

		public void prepareSave() {
			try {
				bmoOrderCredit = new BmoOrderCredit();				
				bmoOrderCredit.getPrice().setValue(priceTextBox.getText());
				bmoOrderCredit.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderCredit.getOrderId().setValue(bmoOrder.getId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderCredit.getPmClass(), bmoOrderCredit, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
		
		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Paquete: " + bmUpdateResult.errorsToString());
			else {
				orderCreditDialogBox.hide();
				reset();
			}
		}
	}
}
