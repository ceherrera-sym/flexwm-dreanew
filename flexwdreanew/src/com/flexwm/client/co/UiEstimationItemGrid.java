package com.flexwm.client.co;

import com.flexwm.shared.co.BmoEstimationItem;
import com.flexwm.client.co.UiContractEstimation.UiContractEstimationForm.ContractEstimationUpdater;
import com.flexwm.shared.co.BmoContractEstimation;
import com.google.gwt.cell.client.EditTextCell;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiEstimationItemGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// EstimationItems
	UiSuggestBox changeContractEstimationSuggestBox;
	private BmoEstimationItem bmoEstimationItem = new BmoEstimationItem();
	private FlowPanel estimationItemPanel = new FlowPanel();
	private CellTable<BmObject> estimationItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> estimationItemData;
	BmFilter estimationItemFilter;

	//private Button addEstimationItemButton = new Button("+ item");
	protected DialogBox estimationItemDialogBox;	

	// Cambio de ContractEstimation
	UiListBox changeContractEstimationListBox;
	DialogBox changeContractEstimationDialogBox;
	Button changeContractEstimationButton = new Button("Cambiar");
	Button closeContractEstimationButton = new Button("Cerrar");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoContractEstimation bmoContractEstimation;
	private ContractEstimationUpdater contractEstimationUpdater;


	public UiEstimationItemGrid(UiParams uiParams, Panel defaultPanel, BmoContractEstimation bmoContractEstimation, ContractEstimationUpdater contractEstimationUpdater){
		super(uiParams, defaultPanel);
		this.bmoContractEstimation = bmoContractEstimation;
		this.contractEstimationUpdater = contractEstimationUpdater;

		closeContractEstimationButton.setStyleName("formCloseButton");
		closeContractEstimationButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeContractEstimationDialogBox.hide();
			}
		});

		// Elementos del cambio de Item
		changeContractEstimationListBox = new UiListBox(getUiParams(), new BmoContractEstimation());

		// Inicializar grid de Personal
		estimationItemGrid = new CellTable<BmObject>();
		estimationItemGrid.setWidth("100%");
		estimationItemPanel.clear();
		estimationItemPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setEstimationItemColumns();
		estimationItemFilter = new BmFilter();
		estimationItemFilter.setValueFilter(bmoEstimationItem.getKind(), bmoEstimationItem.getContractEstimationId().getName(), bmoContractEstimation.getId());
		estimationItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		estimationItemData = new UiListDataProvider<BmObject>(new BmoEstimationItem(), estimationItemFilter);
		estimationItemData.addDataDisplay(estimationItemGrid);
		estimationItemPanel.add(estimationItemGrid);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, estimationItemPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		estimationItemData.list();
		estimationItemGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){

	}

	public void reset(){
		contractEstimationUpdater.changeContractEstimation();
	}

	public void changeHeight() {
		estimationItemGrid.setPageSize(estimationItemData.getList().size());
		estimationItemGrid.setVisibleRange(0, estimationItemData.getList().size());
	}

	public void changeQuantity(BmObject bmObject, String amount) {
		bmoEstimationItem = (BmoEstimationItem)bmObject;
		try {			
			double q = Double.parseDouble(amount);				
			if ((q + bmoEstimationItem.getQuantityLast().toDouble()) > bmoEstimationItem.getQuantityTotal().toDouble()) {
				bmoEstimationItem.getQuantity().setValue(0);
				showErrorMessage("La Cantidad No debe ser Mayor al Total.");
			} else {				
				bmoEstimationItem.getQuantity().setValue(q);
			}	
			saveItemChange();				
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}



	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();
		}
	}

	public void processItemDelete(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();			
		}		
	}

	public void saveItemChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()){
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoEstimationItem.getPmClass(), bmoEstimationItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
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
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoEstimationItem.getPmClass(), bmoEstimationItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addEstimationItem() {
		estimationItemDialogBox = new DialogBox(true);
		estimationItemDialogBox.setGlassEnabled(true);
		estimationItemDialogBox.setText("Items Cuentas x Pagar");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		estimationItemDialogBox.setWidget(vp);

		UiEstimationItemForm	ContractEstimationExtraForm = new UiEstimationItemForm(getUiParams(), vp, bmoContractEstimation);
		ContractEstimationExtraForm.show();

		estimationItemDialogBox.center();
		estimationItemDialogBox.show();
	}


	public void setEstimationItemColumns() {

		//Obtener el contractestimation
		//BmoContractEstimation bmoContractEstimation = (BmoContractEstimation)bmoEstimationItem.getBmoContractEstimation();

		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoEstimationItem)bmObject).getBmoContractConceptItem().getCode().toString();
			}
		};
		estimationItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));	

		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoEstimationItem)bmObject).getBmoContractConceptItem().getName().toString();
			}
		};
		estimationItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));

		Column<BmObject, String> unitColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoEstimationItem)bmObject).getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice().getBmoUnit().getName().toString();
			}
		};
		estimationItemGrid.addColumn(unitColumn, SafeHtmlUtils.fromSafeConstant("Unidad"));

		Column<BmObject, String> quantityTotalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoEstimationItem)bmObject).getQuantityTotal().toString();
			}
		};
		estimationItemGrid.addColumn(quantityTotalColumn, SafeHtmlUtils.fromSafeConstant("Total"));

		Column<BmObject, String> quantityColumn;
		if (bmoContractEstimation.getStatus().equals(BmoContractEstimation.STATUS_PENDING)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoEstimationItem)bmObject).getQuantity().toString();
				}
			};
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoEstimationItem)bmObject).getQuantity().toString();
				}
			};
		}
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		estimationItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cantidad"));
		quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {

			@Override
			public void update(int index, BmObject bmObject, String value) {
				// Called when the user changes the value.
				changeQuantity(bmObject, value);
			}
		});

		Column<BmObject, String> quantityLastColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getDecimalFormat();
				String formatted = numberFormat.format(((BmoEstimationItem)bmObject).getQuantityLast().toDouble());
				return (formatted);				
			}
		};
		estimationItemGrid.addColumn(quantityLastColumn, SafeHtmlUtils.fromSafeConstant("Anterior"));

		Column<BmObject, String> quantityReceiptColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getDecimalFormat();
				String formatted = numberFormat.format(((BmoEstimationItem)bmObject).getQuantityReceipt().toDouble());
				return (formatted);
			}
		};
		estimationItemGrid.addColumn(quantityReceiptColumn, SafeHtmlUtils.fromSafeConstant("Acumulado"));


		Column<BmObject, String> priceColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoEstimationItem)bmObject).getPrice().toDouble());
				return (formatted);
			}
		};

		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		estimationItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));

		Column<BmObject, String> subTotalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoEstimationItem)bmObject).getSubTotal().toDouble());
				return (formatted);
			}
		};

		subTotalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		estimationItemGrid.addColumn(subTotalColumn, SafeHtmlUtils.fromSafeConstant("Importe"));

	}


	public void changeEstimationItemAmount(BmObject bmObject, String amount) {
		bmoEstimationItem = (BmoEstimationItem)bmObject;
		try {
			Double a = Double.parseDouble(amount);
			// Validaciones de cambio de precio si es de tipo producto
			bmoEstimationItem.getQuantity().setValue(a);
			saveContractEstimationChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeAmount(): ERROR " + e.toString());
		}
	}

	public void deleteEstimationItem(BmObject bmObject) {
		bmoEstimationItem = (BmoEstimationItem)bmObject;
		if (bmoContractEstimation.getStatus().toChar() != BmoContractEstimation.STATUS_AUTHORIZED) {
			deleteContractEstimation();
		} else {
			showSystemMessage("El Contrato no se puede modificar - está Autorizado.");
		}
	}

	public void saveContractEstimationChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveContractEstimationChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processContractEstimationChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoEstimationItem.getPmClass(), bmoEstimationItem, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveContractEstimationChange(): ERROR " + e.toString());
		}
	}

	public void deleteContractEstimation() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteContractEstimation(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processContractEstimationDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoEstimationItem.getPmClass(), bmoEstimationItem, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteContractEstimation(): ERROR " + e.toString());
		}
	}

	public void processContractEstimationChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Recurso: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processContractEstimationDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}

	// Agrega un item de un producto a la CXP
	private class UiEstimationItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private TextBox amountTextBox = new TextBox();
		private TextBox consecutiveTextBox = new TextBox();
		private BmoEstimationItem bmoEstimationItem;
		private BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();


		public UiEstimationItemForm(UiParams uiParams, Panel defaultPanel, BmoContractEstimation bmoContractEstimation) {
			super(uiParams, defaultPanel);
			this.bmoContractEstimation = bmoContractEstimation;
			this.bmoEstimationItem = new BmoEstimationItem();

			try {
				bmoEstimationItem.getContractEstimationId().setValue(bmoContractEstimation.getId());

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
			}

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoContractEstimation.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				estimationItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoEstimationItem = new BmoEstimationItem();
				bmoEstimationItem.getContractEstimationId().setValue(bmoContractEstimation.getId());
				bmoEstimationItem.getContractConceptItemId().setValue(bmoEstimationItem.getContractConceptItemId().toInteger());				
				bmoEstimationItem.getQuantity().setValue(amountTextBox.getText());
				bmoEstimationItem.getConsecutive().setValue(consecutiveTextBox.getText());
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
					getUiParams().getBmObjectServiceAsync().save(bmoEstimationItem.getPmClass(), bmoEstimationItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

}
