package com.flexwm.client.fi;

import com.flexwm.client.fi.UiRaccount.UiRaccountForm.RaccountUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountItem;
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
import com.symgae.shared.sf.BmoArea;


public class UiRaccountItemGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// RaccountItems
	UiSuggestBox changeRaccountSuggestBox;
	private BmoRaccountItem bmoRaccountItem = new BmoRaccountItem();
	private FlowPanel raccountItemPanel = new FlowPanel();
	private CellTable<BmObject> raccountItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> raccountItemData;
	BmFilter raccountItemFilter;

	private Button addRaccountItemButton = new Button("+ ITEM");
	protected DialogBox raccountItemDialogBox;	

	// Cambio de Paccount
	DialogBox changeRaccountDialogBox;
	Button changeRaccountButton = new Button("CAMBIAR");
	Button closeRaccountButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoRaccount bmoRaccount;

	private RaccountUpdater raccountUpdater;


	public UiRaccountItemGrid(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, RaccountUpdater raccountUpdater){
		super(uiParams, defaultPanel);
		this.bmoRaccount = bmoRaccount;
		this.raccountUpdater = raccountUpdater;

		closeRaccountButton.setStyleName("formCloseButton");
		closeRaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeRaccountDialogBox.hide();
			}
		});

		// Inicializar grid de Personal
		raccountItemGrid = new CellTable<BmObject>();
		raccountItemGrid.setWidth("100%");
		raccountItemPanel.clear();
		raccountItemPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setRaccountItemColumns();
		raccountItemFilter = new BmFilter();
		raccountItemFilter.setValueFilter(bmoRaccountItem.getKind(), bmoRaccountItem.getRaccountId().getName(), bmoRaccount.getId());
		raccountItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		raccountItemData = new UiListDataProvider<BmObject>(new BmoRaccountItem(), raccountItemFilter);
		raccountItemData.addDataDisplay(raccountItemGrid);
		raccountItemPanel.add(raccountItemGrid);

		// Panel de botones
		addRaccountItemButton.setStyleName("formSaveButton");
		addRaccountItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addRaccountItem();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION) && 
				bmoRaccount.getAutoCreate().equals("0"))		
			buttonPanel.add(addRaccountItemButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, raccountItemPanel, 4);
		//Si es de tipo orden de compra no mostrar el boton para agregar items
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION))
			formFlexTable.addButtonPanel(buttonPanel);

		defaultPanel.add(formFlexTable);
	}

	public void show(){
		raccountItemData.list();
		raccountItemGrid.redraw();

		statusEffect();
	}

	public void setRaccountItemColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION) && !bmoRaccount.getAutoCreate().toBoolean()) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRaccountItem)bmObject).getQuantity().toString();
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
					return ((BmoRaccountItem)bmObject).getQuantity().toString();
				}
			};
		}
		raccountItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant"));
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		raccountItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);

		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new EditTextCell()) {
			@Override
			public String getValue(BmObject bmObject) {				
				return ((BmoRaccountItem)bmObject).getName().toString();
			}
		};
		raccountItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		raccountItemGrid.setColumnWidth(nameColumn, 150, Unit.PX);

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			// Partida Presp.
			Column<BmObject, String> budgetItemColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return ((BmoRaccountItem)bmObject).getBmoBudgetItem().getBmoBudgetItemType().getName().toString();
				}
			};
		
			raccountItemGrid.addColumn(budgetItemColumn, SafeHtmlUtils.fromSafeConstant("Part. Presp."));
			budgetItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountItemGrid.setColumnWidth(budgetItemColumn, 150, Unit.PX);

			// Departamento
			Column<BmObject, String> areaColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return ((BmoRaccountItem)bmObject).getBmoArea().getName().toString();
				}
			};
			raccountItemGrid.addColumn(areaColumn, SafeHtmlUtils.fromSafeConstant("Dpto."));
			areaColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			raccountItemGrid.setColumnWidth(areaColumn, 150, Unit.PX);
		}

		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION) && !bmoRaccount.getAutoCreate().toBoolean()) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRaccountItem)bmObject).getPrice().toString();
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
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoRaccountItem)bmObject).getPrice().toDouble());
					return (formatted);
				}
			};
		}
		raccountItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		raccountItemGrid.setColumnWidth(priceColumn, 50, Unit.PX);

		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoRaccountItem)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		raccountItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));	
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		raccountItemGrid.setColumnWidth(totalColumn, 50, Unit.PX);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION) && !bmoRaccount.getAutoCreate().toBoolean()) {
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
		raccountItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		raccountItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
	}

	public void statusEffect(){
		if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
			addRaccountItemButton.setVisible(false);
		} else {
			addRaccountItemButton.setVisible(true);
		}
	}

	public void reset(){
		raccountUpdater.changeRaccount();
	}

	public void changeHeight() {
		raccountItemGrid.setVisibleRange(0, raccountItemData.getList().size());
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoRaccountItem = (BmoRaccountItem)bmObject;
		try {

			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
				showErrorMessage("No se puede modificar la cantidad, la cxc esta autorizada.");
				reset();
			} else {
				double q = Double.parseDouble(quantity);
				if (q > 0) {
					bmoRaccountItem.getQuantity().setValue(quantity);
					saveItemChange();
				} else {
					showErrorMessage("La Cantidad Debe Ser Mayor a 0.");
					reset();
					// Eliminar registro
					//deleteItem();
				}
			}	
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		bmoRaccountItem = (BmoRaccountItem)bmObject;
		try {
			double p = Double.parseDouble(price);
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
				showErrorMessage("No se Puede Modificar El Precio , La CxC Esta Autorizada.");
				reset();
			} else {
				if (p > 0) {					
					bmoRaccountItem.getPrice().setValue(price);
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
				getUiParams().getBmObjectServiceAsync().save(bmoRaccountItem.getPmClass(), bmoRaccountItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}

	private void deleteItem(BmObject bmObject) {
		bmoRaccountItem = (BmoRaccountItem)bmObject;
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
				getUiParams().getBmObjectServiceAsync().delete(bmoRaccountItem.getPmClass(), bmoRaccountItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addRaccountItem() {
		raccountItemDialogBox = new DialogBox(true);
		raccountItemDialogBox.setGlassEnabled(true);
		raccountItemDialogBox.setText("Items Cuentas x Cobrar");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		raccountItemDialogBox.setWidget(vp);

		UiRaccountItemForm	raccountExtraForm = new UiRaccountItemForm(getUiParams(), vp, bmoRaccount);
		raccountExtraForm.show();

		raccountItemDialogBox.center();
		raccountItemDialogBox.show();
	}

	public void changeRaccountItemAmount(BmObject bmObject, String amount) {
		bmoRaccountItem = (BmoRaccountItem)bmObject;
		try {
			Double a = Double.parseDouble(amount);
			// Validaciones de cambio de precio si es de tipo producto
			bmoRaccountItem.getAmount().setValue(a);
			savePaccountChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeAmount(): ERROR " + e.toString());
		}
	}

	public void deleteRaccountItem(BmObject bmObject) {
		bmoRaccountItem = (BmoRaccountItem)bmObject;
		if (bmoRaccount.getStatus().toChar() != BmoRaccount.STATUS_AUTHORIZED) {
			deleteRaccount();
		} else {
			showSystemMessage("El Pedido no se puede modificar - está Autorizado.");
		}
	}

	public void savePaccountChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-savePaccountChange(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processPaccountChangeSave(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().save(bmoRaccountItem.getPmClass(), bmoRaccountItem, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-savePaccountChange(): ERROR " + e.toString());
		}
	}

	public void deleteRaccount() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deletePaccount(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processPaccountDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoRaccountItem.getPmClass(), bmoRaccountItem, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deletePaccount(): ERROR " + e.toString());
		}
	}

	public void processPaccountChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Recurso: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processPaccountDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}

	// Agrega un item de un producto a la CXP
	private class UiRaccountItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private TextBox nameTextBox = new TextBox();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private BmoRaccountItem bmoRaccountItem;
		private BmoRaccount bmoRaccount = new BmoRaccount();
		private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox budgetItemUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());

		public UiRaccountItemForm(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount) {
			super(uiParams, defaultPanel);
			this.bmoRaccount = bmoRaccount;
			this.bmoRaccountItem = new BmoRaccountItem();

			try {
				bmoRaccountItem.getRaccountId().setValue(bmoRaccount.getId());

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
			if (getSFParams().hasWrite(bmoRaccount.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			// Filtro de empresa de presupuesto
			BmFilter filterByCompany = new BmFilter();
			filterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId().getName(), bmoRaccount.getCompanyId().toInteger());
			budgetItemUiSuggestBox.addFilter(filterByCompany);

			// Filtro de ingresos(abono)
			BmFilter filterByDeposit = new BmFilter();
			filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
			budgetItemUiSuggestBox.addFilter(filterByDeposit);

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {

				try {
					if (bmoRaccount.getBudgetItemId().toInteger() > 0)
						bmoRaccountItem.getBudgetItemId().setValue(bmoRaccount.getBudgetItemId().toInteger());
					if (bmoRaccount.getAreaId().toInteger() > 0)
						bmoRaccountItem.getAreaId().setValue(bmoRaccount.getAreaId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
				}
			}

			defaultPanel.add(formTable);
		}

		public void show(){
			try {
				bmoRaccountItem.getName().setValue(bmoRaccount.getDescription().toString(10));
				bmoRaccountItem.getQuantity().setValue(1);
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-show() ERROR: " + e.toString());
			}

			formTable.addField(1, 0, quantityTextBox, bmoRaccountItem.getQuantity());
			formTable.addField(2, 0, nameTextBox, bmoRaccountItem.getName());
			formTable.addField(3, 0, priceTextBox, bmoRaccountItem.getPrice());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
				formTable.addField(4, 0, budgetItemUiSuggestBox, bmoRaccountItem.getBudgetItemId());
				formTable.addField(5, 0, areaUiListBox, bmoRaccountItem.getAreaId());
			}
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				raccountItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoRaccountItem = new BmoRaccountItem();
				bmoRaccountItem.getRaccountId().setValue(bmoRaccount.getId());
				bmoRaccountItem.getName().setValue(nameTextBox.getText());
				bmoRaccountItem.getQuantity().setValue(quantityTextBox.getText());
				bmoRaccountItem.getPrice().setValue(priceTextBox.getText());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
					bmoRaccountItem.getBudgetItemId().setValue(budgetItemUiSuggestBox.getSelectedId());
					bmoRaccountItem.getAreaId().setValue(areaUiListBox.getSelectedId());
				}
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
					getUiParams().getBmObjectServiceAsync().save(bmoRaccountItem.getPmClass(), bmoRaccountItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

}
