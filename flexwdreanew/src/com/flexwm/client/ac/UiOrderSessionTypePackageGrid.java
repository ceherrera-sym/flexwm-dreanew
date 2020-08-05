package com.flexwm.client.ac;

import com.flexwm.client.ac.UiOrderFormSession.OrderSessionUpdater;
import com.flexwm.shared.ac.BmoOrderSessionTypePackage;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
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


public class UiOrderSessionTypePackageGrid extends Ui {	
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderSessionTypePackage
	private BmoOrderSessionTypePackage bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
	private FlowPanel orderSessionTypePackagePanel = new FlowPanel();
	private CellTable<BmObject> orderSessionTypePackageGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderSessionTypePackageData;
	BmFilter orderSessionTypePackageFilter;

	// Cambiar tipo de paquete
//	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//private Button addOrderSessionTypePackageButton = new Button("Paquete");
	protected DialogBox orderSessionTypePackageDialogBox;	

	// Otros
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private OrderSessionUpdater orderUpdater;

	BmoOrder bmoOrder = new BmoOrder();

	public UiOrderSessionTypePackageGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderSessionUpdater orderUpdater){
		super(uiParams, defaultPanel);		
		this.orderUpdater = orderUpdater;
		//this.bmoOrder = bmoOrder;

		// Inicializar grid de Inmueble
		orderSessionTypePackageGrid = new CellTable<BmObject>();
		orderSessionTypePackageGrid.setWidth("100%");
		orderSessionTypePackagePanel.clear();
		orderSessionTypePackagePanel.setWidth("100%");
		setOrderSessionTypePackageColumns();
		orderSessionTypePackageFilter = new BmFilter();
		orderSessionTypePackageFilter.setValueFilter(bmoOrderSessionTypePackage.getKind(), bmoOrderSessionTypePackage.getOrderId().getName(), bmoOrder.getId());
		orderSessionTypePackageGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderSessionTypePackageData = new UiListDataProvider<BmObject>(new BmoOrderSessionTypePackage(), orderSessionTypePackageFilter);
		orderSessionTypePackageData.addDataDisplay(orderSessionTypePackageGrid);
		orderSessionTypePackagePanel.add(orderSessionTypePackageGrid);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addSectionLabel(1, 0, "Paquete Sesiones", 2);
		formFlexTable.addPanel(2, 0, orderSessionTypePackagePanel);
		formFlexTable.addFieldEmpty(3, 0);
		defaultPanel.add(formFlexTable);
		
	}

	@Override
	public void show(){
		orderSessionTypePackageData.list();
		orderSessionTypePackageGrid.redraw();
	}
	
	public void reset(){
		orderUpdater.changeOrderSessionTypePackage();
	}

	public void changeHeight() {
		orderSessionTypePackageGrid.setPageSize(orderSessionTypePackageData.getList().size());
		orderSessionTypePackageGrid.setVisibleRange(0, orderSessionTypePackageData.getList().size());
	}

	// Columnas grid de personal
	public void setOrderSessionTypePackageColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		quantityColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderSessionTypePackage)bmObject).getQuantity().toString();
			}
		};
		orderSessionTypePackageGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		orderSessionTypePackageGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderSessionTypePackage)bmObject).getName().toString();
			}
		};
		orderSessionTypePackageGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		orderSessionTypePackageGrid.setColumnWidth(nameColumn, 200, Unit.PX);
		
		// Description
		Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderSessionTypePackage)bmObject).getDescription().toString();
			}
		};
		orderSessionTypePackageGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Descripción"));
		orderSessionTypePackageGrid.setColumnWidth(descriptionColumn, 200, Unit.PX);

		// Precio
		/*Column<BmObject, String> priceColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderSessionTypePackage)bmObject).getPrice().toDouble());
				return (formatted);
			}
		};
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderSessionTypePackageGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));*/
		
		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderSessionTypePackage)bmObject).getPrice().toString();
				}
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changePrice(bmObject, value);
				}
			});
		} else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderSessionTypePackage)bmObject).getPrice().toString();
				}
			};
		}
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderSessionTypePackageGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderSessionTypePackage)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		orderSessionTypePackageGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
	}

	public void addSessionTypePackage(){
		addSessionTypePackage(new BmoSessionTypePackage());
	}

	public void addSessionTypePackage(BmoSessionTypePackage bmoSessionTypePackage) {
		orderSessionTypePackageDialogBox = new DialogBox(true);
		orderSessionTypePackageDialogBox.setGlassEnabled(true);
		orderSessionTypePackageDialogBox.setText("Extra de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		orderSessionTypePackageDialogBox.setWidget(vp);

		UiOrderSessionTypePackageForm orderSessionTypePackageForm = new UiOrderSessionTypePackageForm(getUiParams(), vp, bmoOrder, bmoSessionTypePackage);

		orderSessionTypePackageForm.show();

		orderSessionTypePackageDialogBox.center();
		orderSessionTypePackageDialogBox.show();
	}
	
	public void changePrice(BmObject bmObject, String price) {
		bmoOrderSessionTypePackage = (BmoOrderSessionTypePackage)bmObject;
		try {
			double p = Double.parseDouble(price);
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
				showErrorMessage("No se puede modificar el precio , El pedido esta autorizado.");
				reset();
			} else {
				if (p > 0) {					
					bmoOrderSessionTypePackage.getPrice().setValue(price);
					saveItemChange();
				} else {
					showErrorMessage("El Precio Debe Ser Mayor a $ 0.00");
					reset();
				}
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
		}
	}
	
	public void saveItemChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()){
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoOrderSessionTypePackage.getPmClass(), bmoOrderSessionTypePackage, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}
	
	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();
		}
	}
	
	// Agrega un paquete de sesions
	private class UiOrderSessionTypePackageForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextArea commentsTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox sessionTypePackageListBox = new UiListBox(getUiParams(), new BmoSessionTypePackage());

		private BmoOrderSessionTypePackage bmoOrderSessionTypePackage;
		private Button saveButton = new Button("Guardar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoOrder bmoOrder;
		private BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();
		String sessionTypePackageId = "";


		public UiOrderSessionTypePackageForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoSessionTypePackage bmoSessionTypePackage) {
			super(uiParams, defaultPanel);
			this.bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
			this.bmoOrder = bmoOrder;
			this.bmoSessionTypePackage = bmoSessionTypePackage;

			try {
				bmoOrderSessionTypePackage.getSessionTypePackageId().setValue(bmoSessionTypePackage.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
			}

			ChangeHandler listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChange(event);
				}
			};
			formTable.setListChangeHandler(listChangeHandler);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrderSessionTypePackage.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
			
			// Filtra por vigencia fin				
			BmFilter filterByPromoEnd = new BmFilter();
			filterByPromoEnd.setValueOperatorFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getEndDate(), BmFilter.MAJOREQUAL, bmoOrder.getLockStart().toString().substring(0,10));
			sessionTypePackageListBox.addFilter(filterByPromoEnd);

			// Filtra por vigencia inicio
			BmFilter filterByPromoStart = new BmFilter();								
			filterByPromoStart.setValueOperatorFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getStartDate(), BmFilter.MINOREQUAL, bmoOrder.getLockStart().toString().substring(0,10));
			sessionTypePackageListBox.addFilter(filterByPromoStart);
		}

		@Override
		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoOrderSessionTypePackage.getQuantity().setValue(1);
				bmoSessionTypePackage.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			formTable.addField(1, 0, sessionTypePackageListBox, bmoSessionTypePackage.getIdField());
			formTable.addField(2, 0, quantityTextBox, bmoOrderSessionTypePackage.getQuantity());
			formTable.addField(3, 0, priceTextBox, bmoOrderSessionTypePackage.getPrice());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == sessionTypePackageListBox) {			
				BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)sessionTypePackageListBox.getSelectedBmObject();
				sessionTypePackageId = sessionTypePackageListBox.getSelectedId();
				priceTextBox.setText(bmoSessionTypePackage.getSalePrice().toString());
			} 

			statusEffect();
		}

		private void statusEffect() {
			if (!sessionTypePackageListBox.getSelectedId().equals("") && !sessionTypePackageListBox.getSelectedId().equals("0")) {
				bmoSessionTypePackage = (BmoSessionTypePackage)sessionTypePackageListBox.getSelectedBmObject();
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
				bmoOrderSessionTypePackage = new BmoOrderSessionTypePackage();
				bmoOrderSessionTypePackage.getSessionTypePackageId().setValue(sessionTypePackageId);
				bmoOrderSessionTypePackage.getPrice().setValue(priceTextBox.getText());
				bmoOrderSessionTypePackage.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderSessionTypePackage.getOrderId().setValue(bmoOrder.getId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrderSessionTypePackage.getPmClass(), bmoOrderSessionTypePackage, callback);					
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
				orderSessionTypePackageDialogBox.hide();
				reset();
			}
		}
	}
}
