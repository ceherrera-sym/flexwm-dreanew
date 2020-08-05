//package com.flexwm.client.cm;
//
//import com.flexwm.client.cm.UiEstimationForm.EstimationUpdater;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoEstimationGroup;
//import com.flexwm.shared.cm.BmoEstimationRFQItem;
//import com.flexwm.shared.fi.BmoBudgetItem;
//import com.flexwm.shared.fi.BmoBudgetItemType;
//import com.flexwm.shared.op.BmoProduct;
//import com.google.gwt.cell.client.ButtonCell;
//import com.google.gwt.cell.client.EditTextCell;
//import com.google.gwt.cell.client.FieldUpdater;
//import com.google.gwt.cell.client.TextCell;
//import com.google.gwt.dom.client.Style.Unit;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.safehtml.shared.SafeHtmlUtils;
//import com.google.gwt.user.cellview.client.CellTable;
//import com.google.gwt.user.cellview.client.Column;
//import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
//import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HasHorizontalAlignment;
//import com.google.gwt.user.client.ui.HasVerticalAlignment;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.Panel;
////import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.symgae.client.ui.Ui;
//import com.symgae.client.ui.UiFormFlexTable;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiListDataProvider;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.client.ui.UiSuggestBoxAction;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.sf.BmoArea;
//
//
//
//public class UiEstimationGroupGrid extends Ui {
//	private CellTable<BmObject> estimationItemGrid = new CellTable<BmObject>();
//	private UiListDataProvider<BmObject> data;
//	private BmoEstimationGroup bmoEstimationGroup = new BmoEstimationGroup();
//	private BmoEstimationRFQItem bmoEstimationRFQItem = new BmoEstimationRFQItem();
////	private TextArea descriptionTextArea = new TextArea();
//	private int EstimationGroupId;
//	private BmFilter bmFilter;
//
//	private FlowPanel estimationGroupPanel = new FlowPanel();
//	private FlowPanel estimationItemPanel = new FlowPanel();
//	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
//	private HorizontalPanel buttonPanel = new HorizontalPanel();
//	private Button addEstimatioItemButton = new Button("+ITEM");
//	private Button deleteButton = new Button("ELIMINAR");
//
//	DialogBox descriptionDialogBox;
//	Button changeDescriptionButton = new Button("GUARDAR");
//	Button closeDescriptionButton = new Button("CERRAR");
//
//	// Cambio de Item
//	UiSuggestBox changeItemSuggestBox;
//	DialogBox changeItemDialogBox;
//	Button changeItemButton = new Button("CAMBIAR");
//	Button closeItemButton = new Button("CERRAR");
//
//	// Mover grupo
//	Label quantityLabel = new Label();
//	private Label estimationGroupTitle = new Label();
//	private TextBox codeTextBox = new TextBox();
//	private TextBox nameTextBox = new TextBox();
//	protected Button saveButton = new Button("GUARDAR");
//
//	protected DialogBox estimationItemDialogBox;
//
//	private BmoEstimation bmoEstimation;
//	private EstimationUpdater estimationUpdater;
//	private int bmoOrderGroupRpcAttempt = 0;
//	private int saveItemRpcAttempt = 0;	
//
//	public UiEstimationGroupGrid(UiParams uiParams, Panel defaultPanel, BmoEstimation bmoEstimation, int EstimationGroupId, EstimationUpdater estimationUpdater){
//		super(uiParams, defaultPanel);
//		this.EstimationGroupId = EstimationGroupId;
//		this.bmoEstimation = bmoEstimation;
//		bmFilter = new BmFilter();
//		bmFilter.setValueFilter(bmoEstimationRFQItem.getKind(), bmoEstimationRFQItem.getEstimationGroupId().getName(), EstimationGroupId);
//		data = new UiListDataProvider<BmObject>(bmoEstimationRFQItem, bmFilter);
//		this.estimationUpdater = estimationUpdater;
//
//
//		//		subtotalTextBox.setStyleName("programSubtitle");
//		//		subtotalTextBox.setEnabled(false);
//		//		subtotalTextBox.setText("Cantidad:");
//		//		buttonPanel.add(subtotalTextBox);
//
//
//		// Asignar propiedades a los botones
//		saveButton.setStyleName("formCloseButton");
//		saveButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				saveEstimationGroup();
//			}
//		});
//		buttonPanel.add(saveButton);
//
//		// Elementos del cambio de descripcion
//		changeDescriptionButton.setStyleName("formCloseButton");
//		changeDescriptionButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				descriptionDialogBox.hide();
//			}
//		});
//
//		closeDescriptionButton.setStyleName("formCloseButton");
//		closeDescriptionButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				descriptionDialogBox.hide();
//			}
//		});
//
//		// Elementos del cambio de item
//		BmFilter filterByType = new BmFilter();
//				filterByType.setValueOrOrFilter(bmoEstimationRFQItem.getBmoProduct().getKind(), 
//				bmoEstimationRFQItem.getBmoProduct().getType().getName(), 
//				"" + BmoProduct.TYPE_PRODUCT, 
//				"" + BmoProduct.TYPE_SUBPRODUCT,
//				"" + BmoProduct.TYPE_COMPLEMENTARY);
//		changeItemSuggestBox = new UiSuggestBox(new BmoProduct(), filterByType);
//
//		closeItemButton.setStyleName("formCloseButton");
//		closeItemButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				changeItemDialogBox.hide();
//			}
//		});
//		estimationGroupTitle.setText("Grupo Cotizacion");
//		estimationGroupTitle.setStyleName("programSubtitle");
//
//		// Inicializar paneles
//		defaultPanel.clear();
//		buttonPanel.setHeight("100%");
//		buttonPanel.setStyleName("formButtonPanel");
//		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		estimationItemGrid.setWidth("100%", true);
//		estimationGroupPanel.setWidth("100%");
//		estimationItemPanel.setWidth("100%");
//		setColumns();
//
//
//		// Panel de botones
//
//		buttonPanel.add(saveButton);
//
//		// Dar de alta producto
//		addEstimatioItemButton.setStyleName("formSaveButton");
//		addEstimatioItemButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				addProduct();
//			}
//		});
//		buttonPanel.add(addEstimatioItemButton);
//
//		deleteButton.setStyleName("formDeleteButton");
//		deleteButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				if (Window.confirm("Está seguro que desea Eliminar este Grupo?")) delete();
//			}
//		});
//		buttonPanel.add(deleteButton);
//
//		// Handler de cambio de estatus del grid
//		estimationItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
//			@Override
//			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
//				if (event.getLoadingState() == LoadingState.LOADED) {
//					changeHeight();
//				}
//			}
//		}); 
//
//		// Crear forma y campos
//		formFlexTable.setWidth("100%");
//
//		data.addDataDisplay(estimationItemGrid);
//
//		// Acomodar paneles
//		estimationGroupPanel.add(formFlexTable);
//		estimationItemPanel.add(estimationItemGrid);
//		estimationGroupPanel.addStyleName("separator");
//
//		defaultPanel.add(estimationGroupPanel);
//	}
//	//
//	//	public UiEstimationGroupGrid(UiParams uiParams) {
//	//		super(uiParams, new BmoEstimationGroup());
//	//	
//	//	}
//
//	public void show() {
//		get();
//		data.list();
//		statusEffect();
//	}
//
//	public void setBmObject(BmObject bmObject) {
//		bmoEstimationGroup = (BmoEstimationGroup)bmObject;
//		formFlexTable.clear();
//
//		formFlexTable.addSectionLabel(1, 0, "Grupo de Items: " + bmoEstimationGroup.getName().toString(), 4);
//
//		formFlexTable.addField(2, 0, nameTextBox, bmoEstimationGroup.getName());
//
//		// Panel de Checkboxes
//		FlowPanel quantityPanel = new FlowPanel();
//		quantityLabel.setText(bmoEstimationGroup.getTotalQuantity().toString());
//		quantityPanel.add(quantityLabel);
//		quantityPanel.setWidth("100%");
//		//		formFlexTable.addPanel(3, 1, quantityPanel, 1);
//		formFlexTable.addPanel(4, 0, estimationItemPanel, 2);
//		formFlexTable.addPanel(5, 0, buttonPanel, 2);
//		formFlexTable.addLabelCell(3, 0, "Total:");
//		formFlexTable.addPanel(3, 1, quantityPanel, 1);
//
//		estimationGroupTitle.setText(bmoEstimationGroup.getName().toString());
//		statusEffect();
//	}
//
//	public void statusEffect() {
//		nameTextBox.setEnabled(false);
//		addEstimatioItemButton.setVisible(false);
//		deleteButton.setVisible(false);
//		saveButton.setVisible(false);
//
//		if (bmoEstimation.getStatus().equals(BmoEstimation.STATUS_PROCESSING)) {
//			nameTextBox.setEnabled(true);
//			addEstimatioItemButton.setVisible(true);
//			deleteButton.setVisible(true);
//			saveButton.setVisible(true);
//
//
//		}
//	}
//
//	public void changeHeight() {
//		estimationItemGrid.setVisibleRange(0, data.getList().size());
//	}
//
//	public void setColumns() {
//
//		// Cantidad
//		Column<BmObject, String> quantityColumn;
//		if (bmoEstimation.getStatus().equals(BmoEstimation.STATUS_PROCESSING)) {
//			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoEstimationRFQItem)bmObject).getQuantity().toString();
//				}
//			};
//			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//				@Override
//				public void update(int index, BmObject bmObject, String value) {
//					changeQuantity(bmObject, value);
//				}
//			});
//		} else {
//			quantityColumn = new Column<BmObject, String>(new TextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoEstimationRFQItem)bmObject).getQuantity().toString();
//				}
//			};
//		}
//		//nombre
//		Column<BmObject, String> nameColumn;
//
//		nameColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoEstimationRFQItem)bmObject).getName().toString();
//			}
//		};
//
//		//p. presupuestal
//		Column<BmObject, String> budgetItemColumn;
//
//		budgetItemColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoEstimationRFQItem)bmObject).getBmoBudgetItem().getBmoBudgetItemType().getName().toString();
//			}
//		};
//
//		//area
//		Column<BmObject, String> areaColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoEstimationRFQItem)bmObject).getBmoArea().getName().toString();
//			}
//		};
//		
//
//
//
//		estimationItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Concepto"));
//		estimationItemGrid.setColumnWidth(nameColumn, 50, Unit.PX);
//		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//
//		estimationItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cantidad(Horas)"));
//		estimationItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
//		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
//
//		estimationItemGrid.addColumn(budgetItemColumn, SafeHtmlUtils.fromSafeConstant("P. Presupuestal"));
//		estimationItemGrid.setColumnWidth(budgetItemColumn, 50, Unit.PX);
//		budgetItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
//
//		estimationItemGrid.addColumn(areaColumn, SafeHtmlUtils.fromSafeConstant("Departamento"));
//		estimationItemGrid.setColumnWidth(areaColumn, 50, Unit.PX);
//		areaColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);	
//
//		// Eliminar
//		Column<BmObject, String> deleteColumn;
//		if (bmoEstimation.getStatus().equals(BmoEstimation.STATUS_PROCESSING)) {
//			deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return "-";
//				}
//			};
//			deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//				@Override
//				public void update(int index, BmObject bmObject, String value) {
//					deleteItem(bmObject);
//				}
//			});
//		} else {
//			deleteColumn = new Column<BmObject, String>(new TextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return " ";
//				}
//			};
//		}
//		estimationItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
//		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		estimationItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
//
//	}
//
//	public void addProduct(){
//		addProduct(new BmoProduct());
//	}
//
//	public void addProduct(BmoProduct bmoProduct) {
//		estimationItemDialogBox = new DialogBox(true);
//		estimationItemDialogBox.setGlassEnabled(true);
//		estimationItemDialogBox.setText("Item de Estimación");
//
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("400px", "250px");
//
//		estimationItemDialogBox.setWidget(vp);
//
//		UiEstimationItemForm estimationItemForm = new UiEstimationItemForm(getUiParams(), vp, EstimationGroupId, bmoProduct);
//
//		estimationItemForm.show();
//
//		estimationItemDialogBox.center();
//		estimationItemDialogBox.show();
//	}
//
//	public void saveEstimationGroup(){
//		try {
//			bmoEstimationGroup.setId(EstimationGroupId);
//			bmoEstimationGroup.getCode().setValue(codeTextBox.getText());
//			bmoEstimationGroup.getName().setValue(nameTextBox.getText());
//			save();
//
//		} catch (BmException e) {
//			showErrorMessage(this.getClass().getName() + "-formValueChange() ERROR: " + e.toString());
//		}
//	}
//
//	// Obtiene objeto del servicio, primer intento
//	public void get() {
//		get(0);
//	}
//
//	// Obtiene objeto del servicio
//	public void get(int bmoOrderGroupRpcAttempt) {
//		if (bmoOrderGroupRpcAttempt < 5) {
//			setBmoOrderGroupRpcAttempt(bmoOrderGroupRpcAttempt + 1);
//
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getBmoOrderGroupRpcAttempt() < 5)
//						get(getBmoOrderGroupRpcAttempt());
//					else
//						showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
//				}
//
//				public void onSuccess(BmObject result) {
//					stopLoading();
//					setBmoOrderGroupRpcAttempt(0);
//					setBmObject((BmObject)result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().get(bmoEstimationGroup.getPmClass(), EstimationGroupId, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
//			}
//		}
//	}
//
//	public void save() {
//		// Establece eventos ante respuesta de servicio
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();
//				processUpdateResult(result);
//			}
//		};
//
//		// Llamada al servicio RPC
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().save(bmoEstimationGroup.getPmClass(), bmoEstimationGroup, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
//		}
//	}
//
//	public void processUpdateResult(BmUpdateResult bmUpdateResult) {
//		if (bmUpdateResult.hasErrors()) 
//			showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
//		else {
//			estimationItemDialogBox.hide();
//			//estimationGroupTitle.setText(bmoEstimationGroup.getName().toString());
//			estimationUpdater.changeEstimation();
//			this.reset();
//		}
//	}
//
//	public void delete() {
//		// Establece eventos ante respuesta de servicio
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();
//				processDeleteResult(result);
//			}
//		};
//
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().delete(bmoEstimationGroup.getPmClass(), bmoEstimationGroup, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + e.toString());
//		}
//	}
//
//	public void processDeleteResult(BmUpdateResult bmUpdateResult) {
//		if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processDeleteResult() ERROR: " + bmUpdateResult.errorsToString());
//		else {
//			estimationUpdater.changeEstimation();
//			estimationUpdater.updateEstimationGroups();
//		}
//	}
//
//	public void changeName(BmObject bmObject, String name) {
//		if (bmoEstimation.getStatus().toChar() == BmoEstimation.STATUS_PROCESSING) {
//			bmoEstimationRFQItem = (BmoEstimationRFQItem)bmObject;
//			try {
//				if (name.length() > 0) {
//					bmoEstimationRFQItem.getName().setValue(name);
//					saveItem();
//				} else {
//					// Eliminar registro
//					deleteItem();
//				}
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-changeName() ERROR: " + e.toString());
//			}
//		} else {
//			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
//			this.reset();
//		}
//	}
//
//
//	public void changeItemDialog(BmoEstimationRFQItem bmoEstimationRFQItem) {
//		this.bmoEstimationRFQItem = bmoEstimationRFQItem;
//
//		if (bmoEstimationRFQItem.getProductId().toInteger() > 0) {
//			changeItemDialogBox = new DialogBox(true);
//			changeItemDialogBox.setGlassEnabled(true);
//			changeItemDialogBox.setText("Cambio de Item");
//			changeItemDialogBox.setSize("400px", "100px");
//
//			VerticalPanel vp = new VerticalPanel();
//			vp.setSize("400px", "100px");
//			changeItemDialogBox.setWidget(vp);
//
//			UiFormFlexTable formChangeItemTable = new UiFormFlexTable(getUiParams());
//			BmoEstimationRFQItem bmoChangeEstimationItem = new BmoEstimationRFQItem();
//			formChangeItemTable.addField(1, 0, changeItemSuggestBox, bmoChangeEstimationItem.getProductId());
//
//			HorizontalPanel changeItemButtonPanel = new HorizontalPanel();
//			changeItemButtonPanel.add(changeItemButton);
//			changeItemButtonPanel.add(closeItemButton);
//
//			vp.add(formChangeItemTable);
//			vp.add(changeItemButtonPanel);
//
//			changeItemDialogBox.center();
//			changeItemDialogBox.show();
//		}
//	}
//
//	public void changeQuantity(BmObject bmObject, String quantity) {
//		if (bmoEstimation.getStatus().toChar() == BmoEstimation.STATUS_PROCESSING) {
//			bmoEstimationRFQItem = (BmoEstimationRFQItem)bmObject;
//			try {
//
//				int q = Integer.parseInt(quantity);
//				if (q > 0) {
//					bmoEstimationRFQItem.getQuantity().setValue(quantity);
//					saveItem();
//				} else {
//					showSystemMessage("La Cantidad no puede ser menor a 0.");
//					reset();
//				}
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-changeQuantity() ERROR: " + e.toString());
//			}
//		} else {
//			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
//			this.reset();
//		}
//	}
//
//	public void processItemSave(BmUpdateResult bmUpdateResult) {
//		if (bmUpdateResult.hasErrors()) {
//			showErrorMessage(this.getClass().getName() + "-processItemChangeSave() ERROR: " + bmUpdateResult.errorsToString());
//			this.reset();
//		}else {
//			estimationUpdater.changeEstimation();
//			this.reset();
//		}
//	}
//
//	public void processItemDelete(BmUpdateResult result) {
//		if (result.hasErrors()) showErrorMessage(this.getClass().getName() + "-processItemDelete() ERROR: " + result.errorsToString());
//		else {
//			estimationUpdater.changeEstimation();
//			this.reset();
//		}
//	}
//
//	// Primer intento
//	public void saveItem() {
//		saveItem(0);
//	}
//
//	public void saveItem(int saveItemRpcAttempt) {
//		if (saveItemRpcAttempt < 5) {
//			setSaveItemRpcAttempt(saveItemRpcAttempt + 1);
//
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getSaveItemRpcAttempt() < 5)
//						setSaveItemRpcAttempt(getSaveItemRpcAttempt());
//					else
//						showErrorMessage(this.getClass().getName() + "-saveItemChange() ERROR: " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					setSaveItemRpcAttempt(0);
//					processItemSave(result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().save(bmoEstimationRFQItem.getPmClass(), bmoEstimationRFQItem, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-saveItemChange() ERROR: " + e.toString());
//			}
//		}
//	}
//
//	public void deleteItem(BmObject bmObject){
//		bmoEstimationRFQItem = (BmoEstimationRFQItem)bmObject;
//		deleteItem();
//	}
//
//	public void deleteItem() {
//		// Establece eventos ante respuesta de servicio
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-deleteItem() ERROR: " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();
//				processItemDelete(result);
//			}
//		};
//
//		// Llamada al servicio RPC
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().delete(bmoEstimationRFQItem.getPmClass(), bmoEstimationRFQItem, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-deleteItem() ERROR: " + e.toString());
//		}
//	}
//
//	public void reset(){
//		get();
//		data.list();
//		estimationItemGrid.redraw();
//	}
//
//
//
//	// Agrega un item de un producto a la orden de compra
//	private class UiEstimationItemForm extends Ui {
//		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
//		private TextBox quantityTextBox = new TextBox();
//		private TextBox nameTextBox = new TextBox();		
//		private BmoEstimationRFQItem bmoEstimationRFQUItem;
//		private Button saveButton = new Button("AGREGAR");
//		private HorizontalPanel buttonPanel = new HorizontalPanel();
//		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
//		private UiListBox estimationGroupListBox;
//		private UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
//		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
//
//		//		String productId = "";
//		//		int estimationGroupId;
//		public UiEstimationItemForm(UiParams uiParams, Panel defaultPanel, int selectedEstimationGroupId, BmoProduct bmoProduct) {
//			super(uiParams, defaultPanel);
//			this.bmoEstimationRFQUItem = new BmoEstimationRFQItem();
//			//			this.estimationGroupId = selectedEstimationGroupId;
//			try {
//				if (selectedEstimationGroupId > 0) bmoEstimationRFQUItem.getEstimationGroupId().setValue(selectedEstimationGroupId);
//				bmoEstimationRFQUItem.getProductId().setValue(bmoProduct.getId());
//				bmoEstimationRFQUItem.getName().setValue("item");
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
//			}
//
//			// Manejo de acciones de suggest box
//			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
//				@Override
//				public void onSelect(UiSuggestBox uiSuggestBox) {
//					formSuggestionChange(uiSuggestBox);
//				}
//			};
//			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);
//
//			saveButton.setStyleName("formSaveButton");
//			saveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					prepareSave();
//				}
//			});
//			saveButton.setVisible(false);
//			if (getSFParams().hasWrite(bmoEstimation.getProgramCode())) saveButton.setVisible(true);
//			buttonPanel.add(saveButton);
//
//			BmFilter filterEstimationGroups = new BmFilter();
//			filterEstimationGroups.setValueFilter(bmoEstimationGroup.getKind(), bmoEstimationGroup.getEstimationId(), bmoEstimation.getId());
//			estimationGroupListBox = new UiListBox(getUiParams(), bmoEstimationGroup, filterEstimationGroups);
//
//			//filtro para mostrar los equipos que Activos			
//			BmFilter filterByEnabled = new BmFilter();
//			filterByEnabled.setValueFilter(bmoEstimationRFQUItem.getBmoProduct().getKind(),bmoEstimationRFQUItem.getBmoProduct().getEnabled(), "1");
//			productSuggestBox.addFilter(filterByEnabled);
//
//			BmFilter filterByNotSubProduct = new BmFilter();
//			filterByNotSubProduct.setValueOperatorFilter(bmoEstimationRFQUItem.getBmoProduct().getKind(), bmoEstimationRFQUItem.getBmoProduct().getType(), BmFilter.NOTEQUALS , "" + BmoProduct.TYPE_SUBPRODUCT);
//			productSuggestBox.addFilter(filterByNotSubProduct);
//
//
//			defaultPanel.add(formTable);
//		}
//
//		public void show(){
//			formTable.addField(1, 0, productSuggestBox, bmoEstimationRFQUItem.getProductId());
//			formTable.addField(2, 0, nameTextBox, bmoEstimationRFQUItem.getName());
//			formTable.addField(3, 0, quantityTextBox, bmoEstimationRFQUItem.getQuantity());		
//			formTable.addField(4, 0, estimationGroupListBox, bmoEstimationRFQUItem.getEstimationGroupId());		
//			setBudgetItemsListBoxFilters(bmoEstimation.getCompanyId().toInteger());
//			formTable.addField(5, 0, budgetItemUiListBox, bmoEstimationRFQUItem.getBudgetItemId());
//			formTable.addField(6, 0, areaUiListBox, bmoEstimationRFQUItem.getAreaId());
//			formTable.addButtonPanel(buttonPanel);
//			formTable.removeRow(4);
//			statusEffect();
//		}
//
//		// Asigna filtros al listado de partidas presp. por empresa
//		private void setBudgetItemsListBoxFilters(int companyId) {
//			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
//
//			if (companyId > 0) {
//				BmFilter bmFilterByCompany = new BmFilter();
//				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
//				budgetItemUiListBox.addBmFilter(bmFilterByCompany);
//				
//				// Filtro de ingresos(abono)
//				BmFilter filterByDeposit = new BmFilter();
//				filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
//				budgetItemUiListBox.addFilter(filterByDeposit);
//				
//			} else {
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
//				budgetItemUiListBox.addBmFilter(bmFilter);
//			}
//		}
//
//
//		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
//			if (uiSuggestBox == productSuggestBox) {
//				if (productSuggestBox.getSelectedId() > 0) {
//				}
//				statusEffect();
//			}
//		}
//
//		public void statusEffect() {
//			estimationGroupListBox.setEnabled(false);
//			estimationGroupListBox.setVisible(false);
//			nameTextBox.setText("");
//
//			nameTextBox.setEnabled(false);
//
//			if (productSuggestBox.getSelectedId() > 0) {
//				nameTextBox.setText("" + ((BmoProduct)productSuggestBox.getSelectedBmObject()).getName().toString());
//			} else {
//				nameTextBox.setText("item");
//				nameTextBox.setEnabled(true);
//
//			}
//		}
//
//		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) 
//				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
//			else {
//				estimationItemDialogBox.hide();
//				reset();
//			}
//		}
//
//		public void prepareSave() {		
//
//			try {
//				bmoEstimationRFQItem.getEstimationGroupId().setValue(EstimationGroupId);
//				bmoEstimationRFQUItem = new BmoEstimationRFQItem();
//				bmoEstimationRFQUItem.getProductId().setValue(productSuggestBox.getSelectedId());
//				bmoEstimationRFQUItem.getName().setValue(nameTextBox.getText());
//				bmoEstimationRFQUItem.getQuantity().setValue(quantityTextBox.getText());
//				bmoEstimationRFQUItem.getEstimationGroupId().setValue(estimationGroupListBox.getSelectedId());
//				bmoEstimationRFQUItem.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
//				bmoEstimationRFQUItem.getAreaId().setValue(areaUiListBox.getSelectedId());
//
//				if (!estimationGroupListBox.getSelectedId().equals(""))
//					save();
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
//			}
//		}
//
//		public void save() {
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					processUpdateResult(result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().save(bmoEstimationRFQUItem.getPmClass(), bmoEstimationRFQUItem, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
//			}
//		}
//	}
//
//	// Variables para llamadas RPC
//	public int getBmoOrderGroupRpcAttempt() {
//		return bmoOrderGroupRpcAttempt;
//	}
//
//	public void setBmoOrderGroupRpcAttempt(int bmoOrderGroupRpcAttempt) {
//		this.bmoOrderGroupRpcAttempt = bmoOrderGroupRpcAttempt;
//	}
//
//	public int getSaveItemRpcAttempt() {
//		return saveItemRpcAttempt;
//	}
//
//	public void setSaveItemRpcAttempt(int saveItemRpcAttempt) {
//		this.saveItemRpcAttempt = saveItemRpcAttempt;
//	}
//
//}
