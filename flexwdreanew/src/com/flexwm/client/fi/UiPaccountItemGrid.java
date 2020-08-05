package com.flexwm.client.fi;


import com.flexwm.client.fi.UiPaccount.UiPaccountForm.PaccountUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountItem;
import com.flexwm.shared.fi.BmoPaccountType;
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
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;


public class UiPaccountItemGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// PaccountItems	
	private BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
	private FlowPanel paccountItemPanel = new FlowPanel();
	private CellTable<BmObject> paccountItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> paccountItemData;
	BmFilter paccountItemFilter;

	private Button addPaccountItemButton = new Button("+ ITEM");
	protected DialogBox paccountItemDialogBox;	

	// Cambio de Paccount
	//UiListBox changePaccountListBox;
	DialogBox changePaccountDialogBox;
	Button changePaccountButton = new Button("CAMBIAR");
	Button closePaccountButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoPaccount bmoPaccount;
	private BmoPaccountType bmoPaccountType;
	private PaccountUpdater paccountUpdater;


	public UiPaccountItemGrid(UiParams uiParams, Panel defaultPanel, BmoPaccount bmoPaccount, PaccountUpdater paccountUpdater){
		super(uiParams, defaultPanel);
		this.bmoPaccount = bmoPaccount;
		this.paccountUpdater = paccountUpdater;

		bmoPaccountType = (BmoPaccountType)bmoPaccount.getBmoPaccountType();

		closePaccountButton.setStyleName("formCloseButton");
		closePaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changePaccountDialogBox.hide();
			}
		});

		// Elementos del cambio de Item
		//changePaccountListBox = new UiListBox(getUiParams(), new BmoPaccount());

		// Inicializar grid de Personal
		paccountItemGrid = new CellTable<BmObject>();
		paccountItemGrid.setWidth("100%");
		paccountItemPanel.clear();
		paccountItemPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setPaccountItemColumns();
		paccountItemFilter = new BmFilter();
		paccountItemFilter.setValueFilter(bmoPaccountItem.getKind(), bmoPaccountItem.getPaccountId().getName(), bmoPaccount.getId());
		paccountItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		paccountItemData = new UiListDataProvider<BmObject>(new BmoPaccountItem(), paccountItemFilter);
		paccountItemData.addDataDisplay(paccountItemGrid);
		paccountItemPanel.add(paccountItemGrid);

		// Panel de botones
		addPaccountItemButton.setStyleName("formSaveButton");
		addPaccountItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addPaccountItem();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (//!bmoPaccount.getStatus().equals("" + BmoPaccount.STATUS_CANCELLED) 
				bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_OTHER) 
				|| bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)
				&& !bmoPaccount.getIsXml().toBoolean())		
			buttonPanel.add(addPaccountItemButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, paccountItemPanel);
		//Si es de tipo orden de compra no mostrar el boton para agregar items
		if (!bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION))
			formFlexTable.addButtonPanel(buttonPanel);

		defaultPanel.add(formFlexTable);
	}

	public void show(){
		paccountItemData.list();
		paccountItemGrid.redraw();

		statusEffect();
	}

	public void changeHeight() {
		paccountItemGrid.setPageSize(paccountItemData.getList().size());
		paccountItemGrid.setVisibleRange(0, paccountItemData.getList().size());
	}

	public void setPaccountItemColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION) && !bmoPaccount.getIsXml().toBoolean()) {
			if(bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {
				quantityColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoPaccountItem)bmObject).getQuantity().toString();
					}
				};
			} else {	
				quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoPaccountItem)bmObject).getQuantity().toString();
					}
				};
				quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeQuantity(bmObject, value);
					}
				});
			}

		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoPaccountItem)bmObject).getQuantity().toString();
				}
			};
		}
		paccountItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant"));
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		paccountItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);

		// Nombre
		Column<BmObject, String> nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoPaccountItem)bmObject).getName().toString();
			}
		};
		paccountItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		paccountItemGrid.setColumnWidth(nameColumn, 150, Unit.PX);	

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			// Partida Presp.
			Column<BmObject, String> budgetItemColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return ((BmoPaccountItem)bmObject).getBmoBudgetItem().getBmoBudgetItemType().getName().toString();
				}
			};
			paccountItemGrid.addColumn(budgetItemColumn, SafeHtmlUtils.fromSafeConstant("Part. Presp."));
			budgetItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			paccountItemGrid.setColumnWidth(budgetItemColumn, 150, Unit.PX);

			// Departamento
			Column<BmObject, String> areaColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {				
					return ((BmoPaccountItem)bmObject).getBmoArea().getName().toString();
				}
			};
			paccountItemGrid.addColumn(areaColumn, SafeHtmlUtils.fromSafeConstant("Dpto."));
			areaColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			paccountItemGrid.setColumnWidth(areaColumn, 150, Unit.PX);
		}
		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION) && !bmoPaccount.getIsXml().toBoolean()) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoPaccountItem)bmObject).getPrice().toString();
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
					String formatted = numberFormat.format(((BmoPaccountItem)bmObject).getPrice().toDouble());
					return (formatted);
				}
			};
		}
		paccountItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		paccountItemGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		
		// IVA si viene de XML
		if (bmoPaccount.getIsXml().toBoolean()) {
			// Descuento
			Column<BmObject, String> discountColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoPaccountItem)bmObject).getDiscount().toDouble());
					return (formatted);
				}
			};
			paccountItemGrid.addColumn(discountColumn, SafeHtmlUtils.fromSafeConstant("Descuento"));	
			discountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			paccountItemGrid.setColumnWidth(discountColumn, 50, Unit.PX);
		
			Column<BmObject, String> taxtColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoPaccountItem)bmObject).getTax().toDouble());
					return (formatted);
				}
			};
			paccountItemGrid.addColumn(taxtColumn, SafeHtmlUtils.fromSafeConstant("IVA"));	
			taxtColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			paccountItemGrid.setColumnWidth(taxtColumn, 50, Unit.PX);
			// retenciones
			Column<BmObject, String> retentionColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoPaccountItem)bmObject).getSumRetention().toDouble());
					return (formatted);
				}
			};
			paccountItemGrid.addColumn(retentionColumn, SafeHtmlUtils.fromSafeConstant("Retenciones"));	
			retentionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			paccountItemGrid.setColumnWidth(taxtColumn, 50, Unit.PX);
		}
		// Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoPaccountItem)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		paccountItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));	
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		paccountItemGrid.setColumnWidth(totalColumn, 50, Unit.PX);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (!bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION) &&
				bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION) && !bmoPaccount.getIsXml().toBoolean()) {
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
		paccountItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		paccountItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
	}

	public void statusEffect(){
		if (bmoPaccount.getStatus().toChar() == BmoPaccount.STATUS_AUTHORIZED) {
			addPaccountItemButton.setVisible(false);
		} else {
			if(!bmoPaccount.getIsXml().toBoolean())
				addPaccountItemButton.setVisible(true);
		}
	}

	public void reset(){
		paccountUpdater.changePaccount();
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoPaccountItem = (BmoPaccountItem)bmObject;
		try {

			if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
				showErrorMessage("No se puede modificar la cantidad, esta ligado a un recibo");
				reset();
			} else {
				double q = Double.parseDouble(quantity);
				if (q > 0) {
					bmoPaccountItem.getQuantity().setValue(quantity);
					saveItemChange();
				} else {
					// Eliminar registro
					showErrorMessage("La Cantidad Debe Ser Mayor a 0.");
					reset();
					//deleteItem();
				}
			}	
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		bmoPaccountItem = (BmoPaccountItem)bmObject;
		try {
			double p = Double.parseDouble(price);
			if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
				showErrorMessage("No se puede modificar el precio, esta ligado a un recibo");
				reset();

			} else {
				if (p > 0) {
					bmoPaccountItem.getPrice().setValue(price);
					saveItemChange();
				} else {
					showErrorMessage("El Precio Debe Ser Mayor a $ 0.00.");
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
				getUiParams().getBmObjectServiceAsync().save(bmoPaccountItem.getPmClass(), bmoPaccountItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
		}
	}

	private void deleteItem(BmObject bmObject) {
		bmoPaccountItem = (BmoPaccountItem)bmObject;
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
				getUiParams().getBmObjectServiceAsync().delete(bmoPaccountItem.getPmClass(), bmoPaccountItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}

	public void addPaccountItem() {
		paccountItemDialogBox = new DialogBox(true);
		paccountItemDialogBox.setGlassEnabled(true);
		paccountItemDialogBox.setText("Items Cuentas x Pagar");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		paccountItemDialogBox.setWidget(vp);

		UiPaccountItemForm	paccountExtraForm = new UiPaccountItemForm(getUiParams(), vp, bmoPaccount);
		paccountExtraForm.show();

		paccountItemDialogBox.center();
		paccountItemDialogBox.show();
	}

	public void changePaccountItemAmount(BmObject bmObject, String amount) {
		bmoPaccountItem = (BmoPaccountItem)bmObject;
		try {
			Double a = Double.parseDouble(amount);
			// Validaciones de cambio de precio si es de tipo producto
			bmoPaccountItem.getAmount().setValue(a);
			savePaccountChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeAmount(): ERROR " + e.toString());
		}
	}

	public void deletePaccountItem(BmObject bmObject) {
		bmoPaccountItem = (BmoPaccountItem)bmObject;
		if (bmoPaccount.getStatus().toChar() != BmoPaccount.STATUS_AUTHORIZED) {
			deletePaccount();
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
				getUiParams().getBmObjectServiceAsync().save(bmoPaccountItem.getPmClass(), bmoPaccountItem, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-savePaccountChange(): ERROR " + e.toString());
		}
	}

	public void deletePaccount() {
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
				getUiParams().getBmObjectServiceAsync().delete(bmoPaccountItem.getPmClass(), bmoPaccountItem, callback);	
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
	private class UiPaccountItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private TextBox nameTextBox = new TextBox();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private BmoPaccountItem bmoPaccountItem;
		private BmoPaccount bmoPaccount = new BmoPaccount();
		private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox budgetItemUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		
		private UiTextBox discountTextBox = new UiTextBox();
		public UiPaccountItemForm(UiParams uiParams, Panel defaultPanel, BmoPaccount bmoPaccount) {
			super(uiParams, defaultPanel);
			this.bmoPaccount = bmoPaccount;
			this.bmoPaccountItem = new BmoPaccountItem();

			try {
				bmoPaccountItem.getPaccountId().setValue(bmoPaccount.getId());

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
			if (getSFParams().hasWrite(bmoPaccount.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			// Filtro de empresa de presupuesto
			BmFilter filterByCompany = new BmFilter();
			filterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId().getName(), bmoPaccount.getCompanyId().toInteger());
			budgetItemUiSuggestBox.addFilter(filterByCompany);

			// Filtro de egresos(cargo)
			BmFilter filterByWithdraw = new BmFilter();
			filterByWithdraw.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_WITHDRAW);
			budgetItemUiSuggestBox.addFilter(filterByWithdraw);

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				try {
					if (bmoPaccount.getBudgetItemId().toInteger() > 0)
						bmoPaccountItem.getBudgetItemId().setValue(bmoPaccount.getBudgetItemId().toInteger());
					if (bmoPaccount.getAreaId().toInteger() > 0)
						bmoPaccountItem.getAreaId().setValue(bmoPaccount.getAreaId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
				}
			}

			defaultPanel.add(formTable);
		}

		public void show(){
			try {
				bmoPaccountItem.getName().setValue(bmoPaccount.getDescription().toString(10));
				bmoPaccountItem.getQuantity().setValue(1);
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-show() ERROR: " + e.toString());
			}

			formTable.addField(1, 0, quantityTextBox, bmoPaccountItem.getQuantity());
			formTable.addField(2, 0, nameTextBox, bmoPaccountItem.getName());
			formTable.addField(3, 0, priceTextBox, bmoPaccountItem.getPrice());
			if(bmoPaccount.getIsXml().toBoolean()) {
				formTable.addField(4, 0, discountTextBox,bmoPaccountItem.getDiscount());
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
				formTable.addField(5, 0, budgetItemUiSuggestBox, bmoPaccountItem.getBudgetItemId());
				formTable.addField(6, 0, areaUiListBox, bmoPaccountItem.getAreaId());
			}
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				paccountItemDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoPaccountItem = new BmoPaccountItem();
				bmoPaccountItem.getPaccountId().setValue(bmoPaccount.getId());
				bmoPaccountItem.getName().setValue(nameTextBox.getText());
				bmoPaccountItem.getQuantity().setValue(quantityTextBox.getText());
				bmoPaccountItem.getPrice().setValue(priceTextBox.getText());
				bmoPaccountItem.getDiscount().setValue(discountTextBox.getText());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
					bmoPaccountItem.getBudgetItemId().setValue(budgetItemUiSuggestBox.getSelectedId());
					bmoPaccountItem.getAreaId().setValue(areaUiListBox.getSelectedId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoPaccountItem.getPmClass(), bmoPaccountItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

}
