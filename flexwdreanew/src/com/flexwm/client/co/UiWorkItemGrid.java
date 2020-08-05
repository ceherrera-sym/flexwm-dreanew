package com.flexwm.client.co;

import com.flexwm.client.co.UiWorkShowItem.WorkUpdater;
import com.flexwm.shared.co.BmoWork;
import com.flexwm.shared.co.BmoWorkItem;
import com.flexwm.shared.co.BmoUnitPrice;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiWorkItemGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// WorkItems	
	private BmoWorkItem bmoWorkItem = new BmoWorkItem();
	private FlowPanel workItemPanel = new FlowPanel();
	private CellTable<BmObject> workItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> workItemData;
	BmFilter workItemFilter;

	private Button addWorkItemButton = new Button("+ Maestro P.U.");
	private Button addItemButton = new Button("+ item");
	protected DialogBox workItemDialogBox;	

	// Cambio de Work
	
	DialogBox changeWorkDialogBox;
	Button changeWorkButton = new Button("Cambiar");
	Button closeWorkButton = new Button("Cerrar");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoWork bmoWork;	
	private WorkUpdater workUpdater;

	public UiWorkItemGrid(UiParams uiParams, Panel defaultPanel, BmoWork bmoWork, WorkUpdater workUpdater2){
		super(uiParams, defaultPanel);
		this.bmoWork = bmoWork;
		this.workUpdater = workUpdater2;

		closeWorkButton.setStyleName("formCloseButton");
		closeWorkButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeWorkDialogBox.hide();
			}
		});

		// Inicializar grid de Personal
		workItemGrid = new CellTable<BmObject>();
		workItemGrid.setWidth("100%");
		workItemPanel.clear();
		workItemPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setWorkItemColumns();
		workItemFilter = new BmFilter();		
		workItemFilter.setValueFilter(bmoWorkItem.getKind(), bmoWorkItem.getWorkId().getName(), bmoWork.getId());
		workItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		workItemData = new UiListDataProvider<BmObject>(new BmoWorkItem(), workItemFilter);
		workItemData.addDataDisplay(workItemGrid);
		workItemPanel.add(workItemGrid);

		// Panel de botones
		addWorkItemButton.setStyleName("formSaveButton");
		addWorkItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addWorkItem();
			}
		});

		addItemButton.setStyleName("formSaveButton");
		addItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addItem();
			}
		});

		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);				
		buttonPanel.add(addWorkItemButton);
		buttonPanel.add(addItemButton);

		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, workItemPanel, 4);
		if (bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) 
			formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		workItemData.list();
		workItemGrid.redraw();
		statusEffect();
	}

	public void changeHeight() {
		workItemGrid.setPageSize(workItemData.getList().size());
		workItemGrid.setVisibleRange(0, workItemData.getList().size());
	}

	public void setWorkItemColumns() {
		// Clave
		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWorkItem)bmObject).getBmoUnitPrice().getCode().toString();
			}
		};
		workItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		workItemGrid.setColumnWidth(codeColumn, 100, Unit.PX);

		//Descripcion
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWorkItem)bmObject).getBmoUnitPrice().getName().toString();
			}
		};
		workItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		workItemGrid.setColumnWidth(nameColumn, 100, Unit.PX);

		// Unidad
		Column<BmObject, String> unitColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoWorkItem)bmObject).getBmoUnitPrice().getBmoUnit().getName().toString();
			}
		};
		workItemGrid.addColumn(unitColumn, SafeHtmlUtils.fromSafeConstant("Unidad"));
		unitColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		workItemGrid.setColumnWidth(unitColumn, 100, Unit.PX);

		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWorkItem)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeQuantity(bmObject, value);
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoWorkItem)bmObject).getQuantity().toString();
				}
			};
		}
		workItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant"));
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		workItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);

		// Precio
		Column<BmObject, String>  priceColumn;
		
		if (bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoWorkItem)bmObject).getPrice().toDouble());
					return (formatted);
				}
				
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {

				@Override
				public void update(int index, BmObject bmObject, String value) {
					changePrice(bmObject, value);					
				}
			});
			
		}else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoWorkItem)bmObject).getPrice().toDouble());
					return (formatted);
				}
			};
		}

		workItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		workItemGrid.setColumnWidth(priceColumn, 100, Unit.PX);

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoWorkItem)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		workItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("SubTotal"));	
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		workItemGrid.setColumnWidth(totalColumn, 100, Unit.PX);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if(bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
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
		workItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		workItemGrid.setColumnWidth(deleteColumn, 70, Unit.PX);
	}

	public void statusEffect(){
		if(bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
			addWorkItemButton.setVisible(true);
		} else {
			addWorkItemButton.setVisible(false);
		}
	}

	public void reset(){
		workUpdater.changeWork();
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoWorkItem = (BmoWorkItem)bmObject;
		try {
			if(!bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
				showErrorMessage("No se puede modificar la cantidad, la obra esta autorizada");
				reset();
			} else {
				bmoWorkItem.getQuantity().setValue(quantity);
				saveItemChange();				
			}	
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		bmoWorkItem = (BmoWorkItem)bmObject;
		try {
			double p = Double.parseDouble(price);
			if(!bmoWork.getStatus().equals(BmoWork.STATUS_REVISION)) {
				showErrorMessage("No se puede modificar el precio, esta ligado a un recibo");
				reset();

			} else {
				if (p > 0) {
					bmoWorkItem.getPrice().setValue(price);
					saveItemChange();
				}								
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
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
				getUiParams().getBmObjectServiceAsync().save(bmoWorkItem.getPmClass(), bmoWorkItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}

	private void deleteItem(BmObject bmObject) {
		bmoWorkItem = (BmoWorkItem)bmObject;
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
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoWorkItem.getPmClass(), bmoWorkItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addWorkItem() {
		workItemDialogBox = new DialogBox(true);
		workItemDialogBox.setGlassEnabled(true);
		workItemDialogBox.setText("Items P.U. Maestro");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		workItemDialogBox.setWidget(vp);

		UiWorkItemForm	uiWorkItemForm = new UiWorkItemForm(getUiParams(), vp, bmoWork);
		uiWorkItemForm.show();

		workItemDialogBox.center();
		workItemDialogBox.show();
	}

	public void addItem() {
		workItemDialogBox = new DialogBox(true);
		workItemDialogBox.setGlassEnabled(true);
		workItemDialogBox.setText("Items P.U.");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		workItemDialogBox.setWidget(vp);

		UiItemForm	uiItemForm = new UiItemForm(getUiParams(), vp, bmoWork);
		uiItemForm.show();

		workItemDialogBox.center();
		workItemDialogBox.show();
	}

	// Agrega un items del catalago maestro
	private class UiWorkItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private UiSuggestBox unitPriceSuggestBox = new UiSuggestBox(new BmoUnitPrice());					
		private TextBox quantityTextBox = new TextBox();		
		private BmoWorkItem bmoWorkItem;		
		private BmoWork bmoWork = new BmoWork();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();


		public UiWorkItemForm(UiParams uiParams, Panel defaultPanel, BmoWork bmoWork) {
			super(uiParams, defaultPanel);
			this.bmoWork = bmoWork;
			this.bmoWorkItem = new BmoWorkItem();

			try {
				bmoWorkItem.getWorkId().setValue(bmoWork.getId());

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
			if (getSFParams().hasWrite(bmoWork.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
			//BmFilter myUnitPrices = new BmFilter();			
			//myUnitPrices.setValueFilter(bmoUnitPrice.getKind(), bmoUnitPrice.getWorkId(), "2");
			//unitPriceSuggestBox.addFilter(myUnitPrices);
			formTable.addField(1, 0, unitPriceSuggestBox, bmoWorkItem.getUnitPriceId());			
			formTable.addField(3, 0, quantityTextBox, bmoWorkItem.getQuantity());				
			formTable.addButtonPanel(buttonPanel);


		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				workItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoWorkItem = new BmoWorkItem();
				bmoWorkItem.getWorkId().setValue(bmoWork.getId());
				bmoWorkItem.getUnitPriceId().setValue(unitPriceSuggestBox.getSelectedId());								
				bmoWorkItem.getQuantity().setValue(quantityTextBox.getText());
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
					getUiParams().getBmObjectServiceAsync().save(bmoWorkItem.getPmClass(), bmoWorkItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

	// Agrega un items de PU
	private class UiItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private UiSuggestBox unitPriceSuggestBox = new UiSuggestBox(new BmoUnitPrice());					
		private TextBox quantityTextBox = new TextBox();		
		private BmoWorkItem bmoWorkItem;		
		private BmoWork bmoWork = new BmoWork();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();


		public UiItemForm(UiParams uiParams, Panel defaultPanel, BmoWork bmoWork) {
			super(uiParams, defaultPanel);
			this.bmoWork = bmoWork;
			this.bmoWorkItem = new BmoWorkItem();

			try {
				bmoWorkItem.getWorkId().setValue(bmoWork.getId());

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
			if (getSFParams().hasWrite(bmoWork.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
			BmFilter myUnitPrices = new BmFilter();			
			BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();				
			myUnitPrices.setValueFilter(bmoUnitPrice.getKind(), bmoUnitPrice.getWorkId(), bmoWork.getId());
			unitPriceSuggestBox.addFilter(myUnitPrices);
			formTable.addField(1, 0, unitPriceSuggestBox, bmoWorkItem.getUnitPriceId());			
			formTable.addField(3, 0, quantityTextBox, bmoWorkItem.getQuantity());				
			formTable.addButtonPanel(buttonPanel);


		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				workItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoWorkItem = new BmoWorkItem();
				bmoWorkItem.getWorkId().setValue(bmoWork.getId());
				bmoWorkItem.getUnitPriceId().setValue(unitPriceSuggestBox.getSelectedId());								
				bmoWorkItem.getQuantity().setValue(quantityTextBox.getText());
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
					getUiParams().getBmObjectServiceAsync().save(bmoWorkItem.getPmClass(), bmoWorkItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

}
