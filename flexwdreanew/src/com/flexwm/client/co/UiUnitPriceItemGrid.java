package com.flexwm.client.co;

import com.flexwm.client.co.UiUnitPrice.UiUnitPriceForm.UnitPriceUpdater;
import com.flexwm.shared.co.BmoUnitPrice;
import com.flexwm.shared.co.BmoUnitPriceItem;
import com.flexwm.shared.co.BmoWork;
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
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiUnitPriceItemGrid extends Ui {

	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// UnitPriceItems	
	private BmoUnitPriceItem bmoUnitPriceItem = new BmoUnitPriceItem();
	private FlowPanel unitPriceItemPanel = new FlowPanel();
	private CellTable<BmObject> unitPriceItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> unitPriceItemData;
	BmFilter unitPriceItemFilter;

	private Button addUnitPriceItemButton = new Button("+ P.U.");
	private Button addUnitPriceItemMasterWorkButton = new Button("+ P.U. Maestro");
	protected DialogBox unitPriceItemDialogBox;
	protected DialogBox unitPriceItemMasterWorkDialogBox;

	// Cambio de UnitPrice
	UiListBox changeUnitPriceListBox;
	DialogBox changeUnitPriceDialogBox;
	Button changeUnitPriceButton = new Button("Cambiar");
	Button closeUnitPriceButton = new Button("Cerrar");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoUnitPrice bmoUnitPrice;	
	private UnitPriceUpdater unitPriceUpdater;

	public UiUnitPriceItemGrid(UiParams uiParams, Panel defaultPanel, BmoUnitPrice bmoUnitPrice, UnitPriceUpdater unitPriceUpdater) {
		super(uiParams, defaultPanel);
		this.bmoUnitPrice = bmoUnitPrice;
		this.unitPriceUpdater = unitPriceUpdater;

		closeUnitPriceButton.setStyleName("formCloseButton");
		closeUnitPriceButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeUnitPriceDialogBox.hide();
			}
		});

		// Elementos del cambio de Item
		changeUnitPriceListBox = new UiListBox(getUiParams(), new BmoUnitPrice());

		// Inicializar grid de Personal
		unitPriceItemGrid = new CellTable<BmObject>();
		unitPriceItemGrid.setWidth("100%");
		unitPriceItemPanel.clear();
		unitPriceItemPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setUnitPriceItemColumns();
		unitPriceItemFilter = new BmFilter();		
		unitPriceItemFilter.setValueFilter(bmoUnitPriceItem.getKind(), bmoUnitPriceItem.getUnitPriceParentId().getName(), bmoUnitPrice.getId());
		unitPriceItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		unitPriceItemData = new UiListDataProvider<BmObject>(new BmoUnitPriceItem(), unitPriceItemFilter);
		unitPriceItemData.addDataDisplay(unitPriceItemGrid);
		unitPriceItemPanel.add(unitPriceItemGrid);

		// Precios unitarios
		addUnitPriceItemButton.setStyleName("formSaveButton");
		addUnitPriceItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addUnitPriceItem();
			}
		});

		// Precios unitarios catalago maestro
		addUnitPriceItemMasterWorkButton.setStyleName("formSaveButton");
		addUnitPriceItemMasterWorkButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addUnitPriceItemMasterWork();
			}
		});

		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);				
		buttonPanel.add(addUnitPriceItemButton);
		buttonPanel.add(addUnitPriceItemMasterWorkButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, unitPriceItemPanel, 4);
		//Si es de tipo orden de compra no mostrar el boton para agregar items		
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show() {
		get();
		unitPriceItemData.list();
		statusEffect();
	}

	public void reset() {
		get();
		unitPriceItemData.list();
		unitPriceItemGrid.redraw();
		unitPriceUpdater.changeUnitPrice();
	}

	public void changeHeight() {
		unitPriceItemGrid.setPageSize(unitPriceItemData.getList().size());
		unitPriceItemGrid.setVisibleRange(0, unitPriceItemData.getList().size());
	}

	public void setUnitPriceItemColumns() {

		// Clave
		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoUnitPriceItem)bmObject).getCode().toString();
			}
		};
		unitPriceItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unitPriceItemGrid.setColumnWidth(codeColumn, 100, Unit.PX);

		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoUnitPriceItem)bmObject).getBmoUnitPrice().getName().toString();
			}
		};
		unitPriceItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unitPriceItemGrid.setColumnWidth(nameColumn, 100, Unit.PX);

		Column<BmObject, String> unitColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoUnitPriceItem)bmObject).getBmoUnitPrice().getBmoUnit().getName().toString();
			}
		};
		unitPriceItemGrid.addColumn(unitColumn, SafeHtmlUtils.fromSafeConstant("Unidad"));
		unitColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		unitPriceItemGrid.setColumnWidth(unitColumn, 100, Unit.PX);	

		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoUnitPriceItem)bmObject).getQuantity().toString();
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
					return ((BmoUnitPriceItem)bmObject).getQuantity().toString();
				}
			};
		}
		unitPriceItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant"));
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		unitPriceItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);

		// Precio
		Column<BmObject, String>  priceColumn;
		if (bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoUnitPriceItem)bmObject).getAmount().toString();
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
					return ((BmoUnitPriceItem)bmObject).getAmount().toString();
				}
			};
		}
		unitPriceItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		unitPriceItemGrid.setColumnWidth(priceColumn, 50, Unit.PX);

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoUnitPriceItem)bmObject).getTotal().toDouble());
				return (formatted);
			}
		};
		unitPriceItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));	
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		unitPriceItemGrid.setColumnWidth(totalColumn, 50, Unit.PX);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if(bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_REVISION)) {
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
		unitPriceItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		unitPriceItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
	}

	public void statusEffect() {
		if(bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_REVISION)) {
			addUnitPriceItemButton.setVisible(true);
			addUnitPriceItemMasterWorkButton.setVisible(true);
		} else {
			addUnitPriceItemButton.setVisible(false);
			addUnitPriceItemMasterWorkButton.setVisible(false);
		}
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoUnitPriceItem = (BmoUnitPriceItem)bmObject;
		try {
			if(!bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_REVISION)) {
				showErrorMessage("No se puede modificar la cantidad, la obra esta autorizada");
				this.reset();
			} else {
				double q = Double.parseDouble(quantity);
				if (q > 0) {
					bmoUnitPriceItem.getQuantity().setValue(quantity);
					saveItemChange();
				} else {
					// Eliminar registro				
					deleteItem();
				}
			}	
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		bmoUnitPriceItem = (BmoUnitPriceItem)bmObject;
		try {
			double p = Double.parseDouble(price);
			if(!bmoUnitPrice.getBmoWork().getStatus().equals(BmoWork.STATUS_REVISION)) {
				showErrorMessage("No se puede modificar el precio, esta ligado a un recibo");
				this.reset();

			} else {
				if (p > 0) {
					bmoUnitPriceItem.getAmount().setValue(price);
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
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoUnitPriceItem.getPmClass(), bmoUnitPriceItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}

	private void deleteItem(BmObject bmObject) {
		bmoUnitPriceItem = (BmoUnitPriceItem)bmObject;
		deleteItem();
	}
	
	// Obtiene objeto del servicio
	public void get() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				setBmObject((BmObject)result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoUnitPriceItem.getPmClass(), bmoUnitPrice.getId(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
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
				getUiParams().getBmObjectServiceAsync().delete(bmoUnitPriceItem.getPmClass(), bmoUnitPriceItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addUnitPriceItem() {
		unitPriceItemDialogBox = new DialogBox(true);
		unitPriceItemDialogBox.setGlassEnabled(true);
		unitPriceItemDialogBox.setText("Items del Precio Unitario");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		unitPriceItemDialogBox.setWidget(vp);

		UiUnitPriceItemForm	unitPriceForm = new UiUnitPriceItemForm(getUiParams(), vp, bmoUnitPrice);
		unitPriceForm.show();

		unitPriceItemDialogBox.center();
		unitPriceItemDialogBox.show();
	}

	public void addUnitPriceItemMasterWork() {
		unitPriceItemDialogBox = new DialogBox(true);
		unitPriceItemDialogBox.setGlassEnabled(true);
		unitPriceItemDialogBox.setText("Items Precio Unitario Maestro");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		unitPriceItemDialogBox.setWidget(vp);

		UiUnitPriceItemMasterWorkForm	unitPriceMasterWorkForm = new UiUnitPriceItemMasterWorkForm(getUiParams(), vp, bmoUnitPrice);
		unitPriceMasterWorkForm.show();

		unitPriceItemDialogBox.center();
		unitPriceItemDialogBox.show();
	}	

	// Agrega un precio unitario
	private class UiUnitPriceItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private UiSuggestBox unitPriceSuggestBox = new UiSuggestBox(new BmoUnitPrice());
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceItemTextBox = new TextBox();		
		private BmoUnitPriceItem bmoUnitPriceItem;
		private BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();


		public UiUnitPriceItemForm(UiParams uiParams, Panel defaultPanel, BmoUnitPrice bmoUnitPrice) {
			super(uiParams, defaultPanel);
			this.bmoUnitPrice = bmoUnitPrice;
			this.bmoUnitPriceItem = new BmoUnitPriceItem();

			try {
				bmoUnitPriceItem.getUnitPriceParentId().setValue(bmoUnitPrice.getId());

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
			if (getSFParams().hasWrite(bmoUnitPrice.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show() {
			BmFilter myWork = new BmFilter();			
			myWork.setValueFilter(bmoUnitPrice.getKind(), bmoUnitPrice.getWorkId(), bmoUnitPrice.getWorkId().toInteger());
			unitPriceSuggestBox.addFilter(myWork);
			formTable.addField(1, 0, unitPriceSuggestBox, bmoUnitPriceItem.getUnitPriceId());
			formTable.addField(3, 0, quantityTextBox, bmoUnitPriceItem.getQuantity());			
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				unitPriceItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoUnitPriceItem = new BmoUnitPriceItem();
				bmoUnitPriceItem.getUnitPriceParentId().setValue(bmoUnitPrice.getId());
				bmoUnitPriceItem.getCode().setValue(bmoUnitPrice.getCode().toString());
				bmoUnitPriceItem.getUnitPriceId().setValue(unitPriceSuggestBox.getSelectedId());								
				bmoUnitPriceItem.getQuantity().setValue(quantityTextBox.getText());
				bmoUnitPriceItem.getAmount().setValue(priceItemTextBox.getText());
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
					getUiParams().getBmObjectServiceAsync().save(bmoUnitPriceItem.getPmClass(), bmoUnitPriceItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

	// Agrega un precio unitario del catalgo maestro
	private class UiUnitPriceItemMasterWorkForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private UiSuggestBox unitPriceSuggestBox = new UiSuggestBox(new BmoUnitPrice());
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceItemTextBox = new TextBox();
		private BmoUnitPriceItem bmoUnitPriceItem;
		private BmoUnitPrice bmoUnitPrice = new BmoUnitPrice();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();


		public UiUnitPriceItemMasterWorkForm(UiParams uiParams, Panel defaultPanel, BmoUnitPrice bmoUnitPrice) {
			super(uiParams, defaultPanel);
			this.bmoUnitPrice = bmoUnitPrice;
			this.bmoUnitPriceItem = new BmoUnitPriceItem();

			try {
				bmoUnitPriceItem.getUnitPriceParentId().setValue(bmoUnitPrice.getId());

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
			if (getSFParams().hasWrite(bmoUnitPrice.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show() {
			BmFilter masterWork = new BmFilter();			
			masterWork.setValueFilter(bmoUnitPrice.getKind(), bmoUnitPrice.getBmoWork().getIsMaster(), "1" );
			unitPriceSuggestBox.addFilter(masterWork);
			formTable.addField(1, 0, unitPriceSuggestBox, bmoUnitPriceItem.getUnitPriceId());			
			formTable.addField(3, 0, quantityTextBox, bmoUnitPriceItem.getQuantity());			
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				unitPriceItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoUnitPriceItem = new BmoUnitPriceItem();
				bmoUnitPriceItem.getUnitPriceParentId().setValue(bmoUnitPrice.getId());
				bmoUnitPriceItem.getCode().setValue(bmoUnitPrice.getCode().toString());
				bmoUnitPriceItem.getUnitPriceId().setValue(unitPriceSuggestBox.getSelectedId());								
				bmoUnitPriceItem.getQuantity().setValue(quantityTextBox.getText());
				bmoUnitPriceItem.getAmount().setValue(priceItemTextBox.getText());
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
					getUiParams().getBmObjectServiceAsync().save(bmoUnitPriceItem.getPmClass(), bmoUnitPriceItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}
}
