package com.flexwm.client.op;


import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoPayCondition;
//import com.flexwm.shared.cm.BmoProjectStep;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.op.BmoWhStock;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
//import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.google.gwt.user.client.ui.CheckBox;


public class UiOrderGroupGrid extends Ui {
	private CellTable<BmObject> orderItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	private BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
	private BmoOrderItem bmoOrderItem = new BmoOrderItem();
	private TextArea descriptionTextArea = new TextArea();
	private int orderGroupId;
	private BmFilter bmFilter;
	protected TextBox daysTextBox = new TextBox();
	private Label totaLabel = new Label("");

	private FlowPanel orderGroupPanel = new FlowPanel();
	private FlowPanel orderItemPanel = new FlowPanel();
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private Button addOrderItemButton = new Button("+ ITEM");
	private Button deleteButton = new Button("ELIMINAR");

	DialogBox descriptionDialogBox;
	Button changeDescriptionButton = new Button("GUARDAR");
	Button closeDescriptionButton = new Button("CERRAR");

	// Cambio de Item
	UiSuggestBox changeItemSuggestBox;
	DialogBox changeItemDialogBox;
	Button changeItemButton = new Button("MODIFICAR");
	Button closeItemButton = new Button("CERRAR");

	// Mover grupo
	protected Image groupUpImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/up.png"));
	protected Image groupDownImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/down.png"));

	private Label orderGroupTitle = new Label();
	private TextBox nameTextBox = new TextBox();
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	private CheckBox showAmountCheckBox = new CheckBox();
	private CheckBox showProductImageCheckBox = new CheckBox();
	private CheckBox showGroupImageCheckBox = new CheckBox();
	private CheckBox createRaccountCheckBox = new CheckBox();
	private CheckBox showItmesCheckBox = new CheckBox();
	private UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
	UiListBox payConditionUiListBox= new UiListBox(getUiParams(), new BmoPayCondition());
	private Label amountLabel = new Label("");
	private TextBox amountTextBox = new TextBox();	
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	protected Button saveButton = new Button("GUARDAR");
	//DREa
	private TextBox discountRateTextBox = new TextBox();
	private CheckBox discountApliesCheckBox = new CheckBox();
	private TextBox feePrductionRateTextBox = new TextBox();
	private TextBox commisionRate = new TextBox();
	private CheckBox feeProductionApplyCheckBox = new CheckBox();
	private CheckBox commissionAplyCheckBox = new CheckBox();
	
	protected DialogBox orderItemDialogBox;

	private BmoOrder bmoOrder;
	private OrderUpdater orderUpdater;
	private int bmoOrderGroupRpcAttempt = 0;
	private int changeItemActionRpcAttempt = 0;
	private int saveItemRpcAttempt = 0;

	public UiOrderGroupGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, int orderGroupId, OrderUpdater orderUpdater) {
		super(uiParams, defaultPanel);
		this.orderGroupId = orderGroupId;
		this.bmoOrder = bmoOrder;
		bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId().getName(), orderGroupId);
		data = new UiListDataProvider<BmObject>(bmoOrderItem, bmFilter);
		this.orderUpdater = orderUpdater;

		// Boton de bajar grupo
		groupUpImage.setStyleName("listSearchImage");
		groupUpImage.setTitle("Subir Grupo");
		groupUpImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					changeGroupIndex(BmoFlexConfig.UP);
				}
			}
		});

		// Boton de subir grupo
		groupDownImage.setStyleName("listSearchImage");
		groupDownImage.setTitle("Bajar Grupo");
		groupDownImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					changeGroupIndex(BmoFlexConfig.DOWN);
				}
			}
		});

		buttonPanel.add(groupUpImage);
		buttonPanel.add(groupDownImage);

		// Boton de guardar
		saveButton.setStyleName("formCloseButton");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveOrderGroup();
			}
		});
		buttonPanel.add(saveButton);

		// Elementos del cambio de descripcion
		changeDescriptionButton.setStyleName("formCloseButton");
		changeDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeDescription();
				descriptionDialogBox.hide();
			}
		});

		closeDescriptionButton.setStyleName("formCloseButton");
		closeDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				descriptionDialogBox.hide();
			}
		});

		// Elementos del cambio de item
		BmFilter filterByType = new BmFilter();
//		filterByType.setValueOrFilter(bmoOrderItem.getBmoProduct().getKind(), 
//				bmoOrderItem.getBmoProduct().getType().getName(), "" + BmoProduct.TYPE_PRODUCT,  "" + BmoProduct.TYPE_SUBPRODUCT);
		filterByType.setValueOrOrFilter(bmoOrderItem.getBmoProduct().getKind(), 
				bmoOrderItem.getBmoProduct().getType().getName(), 
				"" + BmoProduct.TYPE_PRODUCT, 
				"" + BmoProduct.TYPE_SUBPRODUCT,
				"" + BmoProduct.TYPE_COMPLEMENTARY);
		changeItemSuggestBox = new UiSuggestBox(new BmoProduct(), filterByType);

		changeItemButton.setStyleName("formCloseButton");
		changeItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeItem();
				changeItemDialogBox.hide();
			}
		});

		closeItemButton.setStyleName("formCloseButton");
		closeItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeItemDialogBox.hide();
			}
		});

		orderGroupTitle.setText("Grupo Pedido");
		orderGroupTitle.setStyleName("programSubtitle");

		// Inicializar paneles
		defaultPanel.clear();
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		orderGroupPanel.setWidth("100%");
		orderItemGrid.setWidth("100%", true);
		orderItemPanel.setWidth("100%");
		setColumns();

		// Panel de botones
		// Dar de alta producto
		addOrderItemButton.setStyleName("formSaveButton");
		addOrderItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addProduct();
			}
		});
		buttonPanel.add(addOrderItemButton);

		deleteButton.setStyleName("formDeleteButton");
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (Window.confirm("Está seguro que desea Eliminar este Grupo?")) delete();
			}
		});
		buttonPanel.add(deleteButton);

		// Handler de cambio de estatus del grid
		orderItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addSectionLabel(1, 0, "Grupo de Items", 2);
		formFlexTable.addField(2, 0, nameTextBox, bmoOrderGroup.getName());
		formFlexTable.addField(3, 0, showQuantityCheckBox, bmoOrderGroup.getShowQuantity());
		formFlexTable.addField(4, 0, showPriceCheckBox, bmoOrderGroup.getShowPrice());
		formFlexTable.addField(5, 0, amountTextBox, bmoOrderGroup.getAmount());
		formFlexTable.addPanel(6, 0, orderItemPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		data.addDataDisplay(orderItemGrid);
		
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();	
//		if(getUiParams().getSFParams().hasRead(bmoProjectStep.getProgramCode())) {			
//				createProjectCheckBox.setVisible(true);
		//		}
		// Acomodar paneles
		orderGroupPanel.add(formFlexTable);
		orderItemPanel.add(orderItemGrid);
		orderGroupPanel.addStyleName("separator");
		defaultPanel.add(orderGroupPanel);

		//Cambio en check
		discountApliesCheckBox.addValueChangeHandler(new  ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				booleanChange();

			}
		});		
		commissionAplyCheckBox.addValueChangeHandler(new  ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				booleanChange();

			}
		});
		feeProductionApplyCheckBox.addValueChangeHandler(new  ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				booleanChange();

			}
		});
	
	}

	public void show() {
		get();
		data.list();
		statusEffect();
	}

	public void setBmObject(BmObject bmObject) {
		bmoOrderGroup = (BmoOrderGroup)bmObject;
		formFlexTable.clear();

		if (bmoOrderGroup.getIsKit().toBoolean())
			formFlexTable.addSectionLabel(1, 0, "Kit de Items: " + bmoOrderGroup.getName().toString(), 4);
		else
			formFlexTable.addSectionLabel(1, 0, "Grupo de Items: " + bmoOrderGroup.getName().toString(), 4);

		formFlexTable.addField(2, 0, nameTextBox, bmoOrderGroup.getName());

		// Panel de Checkboxes
		FlowPanel checkBoxPanel = new FlowPanel();
		checkBoxPanel.setWidth("100%");
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showQuantityCheckBox, bmoOrderGroup.getShowQuantity()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showAmountCheckBox, bmoOrderGroup.getShowAmount()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showProductImageCheckBox, bmoOrderGroup.getShowProductImage()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showPriceCheckBox, bmoOrderGroup.getShowPrice()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showGroupImageCheckBox, bmoOrderGroup.getShowGroupImage()));
		if (bmoOrderGroup.getIsKit().toBoolean())
			checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showItmesCheckBox, bmoOrderGroup.getShowItems()));
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();
//		if(getUiParams().getSFParams().hasRead(bmoProjectStep.getProgramCode())) {
//			if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean())
//				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(createProjectCheckBox, bmoOrderGroup.getCreateProject()));
//		}
		formFlexTable.addLabelCell(3, 0, "Mostrar:");
		formFlexTable.addPanel(3, 1, checkBoxPanel, 1);

		formFlexTable.addField(4, 0, imageFileUpload, bmoOrderGroup.getImage());
		if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
			formFlexTable.addField(5, 0, payConditionUiListBox, bmoOrderGroup.getPayConditionId());
		
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			if (!bmoOrderGroup.getIsKit().toBoolean()) {
				formFlexTable.addField(6, 0, discountApliesCheckBox, bmoOrderGroup.getDiscountApplies());
				formFlexTable.addField(7, 0, discountRateTextBox, bmoOrderGroup.getDiscountRate());
			}
			formFlexTable.addField(8, 0, commissionAplyCheckBox, bmoOrderGroup.getCommissionApply());
			formFlexTable.addField(9, 0, commisionRate, bmoOrderGroup.getCommissionRate());
			formFlexTable.addField(10, 0, feeProductionApplyCheckBox, bmoOrderGroup.getFeeProductionApply());
			formFlexTable.addField(11, 0, feePrductionRateTextBox, bmoOrderGroup.getFeeProductionRate());
		}
		
		// Si es kit y permite modificar monto
		if (bmoOrderGroup.getIsKit().toBoolean()
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
			formFlexTable.addField(11, 0, amountTextBox, bmoOrderGroup.getAmount());
		else
			formFlexTable.addLabelField(12, 0, amountLabel, bmoOrderGroup.getAmount());
		

		if (bmoOrderGroup.getIsKit().toBoolean()  && bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				formFlexTable.addField(13, 0, daysTextBox, bmoOrderGroup.getDays());
				formFlexTable.addLabelField(14, 0, totaLabel, bmoOrderGroup.getTotal());
		}	

		formFlexTable.addPanel(15, 0, orderItemPanel, 2);
		formFlexTable.addPanel(16, 0, buttonPanel, 2);

		numberFormat = NumberFormat.getCurrencyFormat();
		String formatted = numberFormat.format(bmoOrderGroup.getAmount().toDouble());

		if (bmoOrderGroup.getIsKit().toBoolean()
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
			amountTextBox.setText(formatted);
		else 
			amountLabel.setText(formatted);		

		formatted = numberFormat.format(bmoOrderGroup.getTotal().toDouble());
		
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) && bmoOrderGroup.getIsKit().toBoolean()) 
			totaLabel.setText(formatted);

		orderGroupTitle.setText(bmoOrderGroup.getName().toString());

		statusEffect();
	}
	private void booleanChange() {
		statusEffect();
	}

	public void statusEffect() {
		nameTextBox.setEnabled(false);
		showQuantityCheckBox.setEnabled(false);
		showPriceCheckBox.setEnabled(false);
		showAmountCheckBox.setEnabled(false);	
		showProductImageCheckBox.setEnabled(false);
		showGroupImageCheckBox.setEnabled(false);
		createRaccountCheckBox.setEnabled(false);
		addOrderItemButton.setVisible(false);
		deleteButton.setVisible(false);
		saveButton.setVisible(false);
		amountTextBox.setEnabled(false);
		payConditionUiListBox.setEnabled(false);
		showItmesCheckBox.setEnabled(false);
		daysTextBox.setEnabled(false);
		
		discountApliesCheckBox.setEnabled(false);
		commissionAplyCheckBox.setEnabled(false);
		feeProductionApplyCheckBox.setEnabled(false);
		discountRateTextBox.setEnabled(false);
		commisionRate.setEnabled(false);
		feePrductionRateTextBox.setEnabled(false);
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			daysTextBox.setEnabled(true);
			nameTextBox.setEnabled(true);
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);
			showAmountCheckBox.setEnabled(true);
			showProductImageCheckBox.setEnabled(true);
			showGroupImageCheckBox.setEnabled(true);
			createRaccountCheckBox.setEnabled(true);
			addOrderItemButton.setVisible(true);
			deleteButton.setVisible(true);
			saveButton.setVisible(true);
			payConditionUiListBox.setEnabled(true);
			showItmesCheckBox.setEnabled(true);

			if (bmoOrderGroup.getIsKit().toBoolean()
					&& getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEKITPRICE)) {
				amountTextBox.setEnabled(true);
			}
			
			//descuento Drea
			if (discountApliesCheckBox.getValue()) {
				formFlexTable.showField(discountRateTextBox);
			} else {
				formFlexTable.hideField(discountRateTextBox);
				discountRateTextBox.setText("0.0");
			}
			//comision Drea
			if (commissionAplyCheckBox.getValue()) {
				formFlexTable.showField(commisionRate);
			} else {
				formFlexTable.hideField(commisionRate);
				commisionRate.setText("0.0");
			}
			//Fee Prducción Drea
			if (feeProductionApplyCheckBox.getValue()) {
				formFlexTable.showField(feePrductionRateTextBox);
			} else {
				formFlexTable.hideField(feePrductionRateTextBox);
				feePrductionRateTextBox.setText("0.0");
			}
			
			discountApliesCheckBox.setEnabled(true);
			commissionAplyCheckBox.setEnabled(true);
			feeProductionApplyCheckBox.setEnabled(true);
			discountRateTextBox.setEnabled(true);
			commisionRate.setEnabled(true);
			feePrductionRateTextBox.setEnabled(true);
		}
	}

	public void changeHeight() {
		orderItemGrid.setPageSize(data.getList().size());
		orderItemGrid.setVisibleRange(0, data.getList().size());
	}

	// El Sub Producto viene de un Producto Compuesto
	public boolean isSubProductLinked(BmObject bmObject) {
		boolean isSubProduct = false;
		BmoOrderItem bmoOrderItem = ((BmoOrderItem)bmObject);
		if ((bmoOrderItem.getBmoProduct().getType().toChar() == BmoProduct.TYPE_SUBPRODUCT)
				&& bmoOrderItem.getOrderItemComposedId().toInteger() > 0)
			isSubProduct = true;
		return isSubProduct;
	}

	// Columnas
	public void setColumns() {

		// Subir o bajar de nivel
		if (!isMobile()) {
			// Columna mover arriba
			Column<BmObject, String> upColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "∧";
				}
			};
			upColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeItemIndex((BmoOrderItem)bmObject, BmoFlexConfig.UP);
				}
			});
			orderItemGrid.addColumn(upColumn, SafeHtmlUtils.fromSafeConstant(""));
			orderItemGrid.setColumnWidth(upColumn, 30, Unit.PX);
			upColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Columna mover abajo
			Column<BmObject, String> downColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "∨";
				}
			};
			downColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeItemIndex((BmoOrderItem)bmObject, BmoFlexConfig.DOWN);
				}
			});
			orderItemGrid.addColumn(downColumn, SafeHtmlUtils.fromSafeConstant(""));
			orderItemGrid.setColumnWidth(downColumn, 30, Unit.PX);
			downColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		}

		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					if (!isSubProductLinked(bmObject))
						changeQuantity(bmObject, value);
					else {
						// Mostrar mensaje y regresar la cantidad original
						showSystemMessage("NO se puede Modificar; el Sub Producto viene de un Producto Compuesto");
						changeQuantity(bmObject, ((BmoOrderItem)bmObject).getQuantity().toString());
					}
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getQuantity().toString();
				}
			};
		}
		orderItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		orderItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		if (!isMobile()) {
			// Columna de clave
			Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getBmoProduct().getCode().toString();
				}
			};
			codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			orderItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
			orderItemGrid.setColumnWidth(codeColumn, 50, Unit.PX);
		}

		// Nombre
		Column<BmObject, String> nameColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)
				&& getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMNAME)) {

			nameColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						if (((BmoOrderItem)bmObject).getProductId().toInteger() > 0)
							return ((BmoOrderItem)bmObject).getBmoProduct().getName().toString();
						else 
							return ((BmoOrderItem)bmObject).getName().toString();
					} else
						return ((BmoOrderItem)bmObject).getName().toString();
				}
			};
			nameColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					if (!isSubProductLinked(bmObject))
						changeName(bmObject, value);
					else {
						// Mostrar mensaje y regresar el nombre original
						showSystemMessage("NO se puede Modificar; el Sub Producto viene de un Producto Compuesto");
						changeName(bmObject, ((BmoOrderItem)bmObject).getName().toString());
					}
				}
			});
		} else {
			nameColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						if (((BmoOrderItem)bmObject).getProductId().toInteger() > 0)
							return ((BmoOrderItem)bmObject).getBmoProduct().getName().toString();
						else 
							return ((BmoOrderItem)bmObject).getName().toString();
					} else
						return ((BmoOrderItem)bmObject).getName().toString();
				}
			};
		}
		orderItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		orderItemGrid.setColumnWidth(nameColumn, 100, Unit.PX);
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Si tiene acceso a cambiar items, mostrar boton
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)
				&& getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEM)) {
			// Columna cambio de item
			Column<BmObject, String> changeItemColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoOrderItem)bmObject).getProductId().toInteger() > 0)
						return "<>";
					else return " ";
				}
			};
			changeItemColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					if (!isSubProductLinked(bmObject))
						if (((BmoOrderItem)bmObject).getBmoProduct().getType().toChar() == BmoProduct.TYPE_COMPOSED)
							// Mostrar mensaje y regresar el producto original
							showSystemMessage("NO se puede Cambiar; es un Producto Compuesto.");
						else
							changeItemDialog((BmoOrderItem)bmObject);
					else {
						// Mostrar mensaje y regresar el producto original
						showSystemMessage("NO se puede Modificar; el Sub Producto viene de un Producto Compuesto.");
						//changeName(bmObject, ((BmoOrderItem)bmObject));
					}
				}
			});
			changeItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			orderItemGrid.addColumn(changeItemColumn, SafeHtmlUtils.fromSafeConstant("Cambiar"));
			orderItemGrid.setColumnWidth(changeItemColumn, 50, Unit.PX);
		}

		if (!isMobile()) {
			// Columna descripcion
			Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoOrderItem)bmObject).getDescription().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			descriptionColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					descriptionDialog((BmoOrderItem)bmObject);
				}
			});
			orderItemGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Desc."));
			orderItemGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);
			descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Tipo producto
			Column<BmObject, String> typeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					BmoOrderItem bmoOrderItem = (BmoOrderItem)bmObject;
					if (bmoOrderItem.getProductId().toInteger() > 0) {
						return bmoOrderItem.getBmoProduct().getType().getSelectedOption().getLabel();	
					} else return "Extra";

				}
			};
			orderItemGrid.addColumn(typeColumn, SafeHtmlUtils.fromSafeConstant("Tipo"));
			orderItemGrid.setColumnWidth(typeColumn, 50, Unit.PX);

			// Columna de Modelo
			Column<BmObject, String> modelColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getBmoProduct().getModel().toString();
				}
			};
			modelColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			orderItemGrid.addColumn(modelColumn, SafeHtmlUtils.fromSafeConstant("Modelo"));
			orderItemGrid.setColumnWidth(modelColumn, 100, Unit.PX);

			// Columna Unidad de Producto
			Column<BmObject, String> unitColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getBmoProduct().getBmoUnit().getCode().toString();
				}
			};
			unitColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			orderItemGrid.addColumn(unitColumn, SafeHtmlUtils.fromSafeConstant("Unidad"));
			orderItemGrid.setColumnWidth(unitColumn, 50, Unit.PX);

			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				// Partida Presp.
				Column<BmObject, String> budgetItemColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {				
						return ((BmoOrderItem)bmObject).getBmoBudgetItem().getBmoBudgetItemType().getName().toString();
					}
				};

				orderItemGrid.addColumn(budgetItemColumn, SafeHtmlUtils.fromSafeConstant("Part. Presp."));
				budgetItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				orderItemGrid.setColumnWidth(budgetItemColumn, 150, Unit.PX);

				// Departamento
				Column<BmObject, String> areaColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {				
						return ((BmoOrderItem)bmObject).getBmoArea().getName().toString();
					}
				};
				orderItemGrid.addColumn(areaColumn, SafeHtmlUtils.fromSafeConstant("Dpto."));
				areaColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				orderItemGrid.setColumnWidth(areaColumn, 100, Unit.PX);
			}
			// Días, si es tipo renta
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				Column<BmObject, String> daysColumn;
				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
					daysColumn = new Column<BmObject, String>(new EditTextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoOrderItem)bmObject).getDays().toString();
						}
					};
					daysColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
						@Override
						public void update(int index, BmObject bmObject, String value) {
							if (!isSubProductLinked(bmObject))
								changeDays(bmObject, value);
							else {
								// Mostrar mensaje y regresar los dias original
								showSystemMessage("NO se puede Modificar; el Sub Producto viene de un Producto Compuesto.");
								changeDays(bmObject, ((BmoOrderItem)bmObject).getDays().toString());
							}
						}
					});
				} else {
					daysColumn = new Column<BmObject, String>(new TextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoOrderItem)bmObject).getDays().toString();
						}
					};
				}
				SafeHtmlHeader daysHeader = new SafeHtmlHeader(new SafeHtml() { 
					private static final long serialVersionUID = 1L;
					@Override 
					public String asString() { 
						return "<p style=\"text-center;\">D&iacute;as</p>"; 
					} 
				}); 
				orderItemGrid.addColumn(daysColumn, daysHeader);
				orderItemGrid.setColumnWidth(daysColumn, 50, Unit.PX);
				quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			} 

			if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission())) {
				// Columna de Comision
				Column<BmObject, String> commisionColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						if (((BmoOrderItem)bmObject).getCommission().toBoolean())
							return "Si";
						else
							return "No";
					}
				};
				commisionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				orderItemGrid.addColumn(commisionColumn, SafeHtmlUtils.fromSafeConstant("Comisión"));
				orderItemGrid.setColumnWidth(commisionColumn, 50, Unit.PX);
			}
		}
///////////////////////////////////////////////////Descuento
			if (bmoOrder.getStatus().equals(BmoQuote.STATUS_AUTHORIZED)) {
				Column<BmObject, String> discountAplyes = new Column<BmObject, String>(new  TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						if (((BmoOrderItem)bmObject).getDiscountApplies().toBoolean())
							return "Si";
						else
							return "No";
					}
				};

				orderItemGrid.addColumn(discountAplyes, SafeHtmlUtils.fromSafeConstant("Aplica Descuento?"));
				discountAplyes.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				orderItemGrid.setColumnWidth(discountAplyes, 150, Unit.PX);
			
			} else {
				Column<BmObject, Boolean> discountAplyes = new Column<BmObject, Boolean>(new CheckboxCell()){				
					@Override
					public Boolean getValue(BmObject bmObject) {
						return ((BmoOrderItem)bmObject).getDiscountApplies().toBoolean();
					}

				};

				orderItemGrid.addColumn(discountAplyes, SafeHtmlUtils.fromSafeConstant("Aplica Descuento?"));
				discountAplyes.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				orderItemGrid.setColumnWidth(discountAplyes, 150, Unit.PX);
				

				discountAplyes.setFieldUpdater(new FieldUpdater<BmObject, Boolean>() {			
					@Override
					public void update(int index, BmObject bmObject, Boolean value) {
						if (((BmoOrderItem)bmObject).getBmoOrderGroup().getDiscountApplies().toBoolean())
							updateDiscount(bmObject, value);
						else {
							showSystemMessage("El Grupo no aplica descuento");
							reset();
						}

					}
				});
			}

			Column<BmObject, String> discountItemColumn = new Column<BmObject, String>(new  TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoOrderItem)bmObject).getDiscount().toDouble());
					return (formatted);

				}
			};

			orderItemGrid.addColumn(discountItemColumn, SafeHtmlUtils.fromSafeConstant("Descuento"));
			discountItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			orderItemGrid.setColumnWidth(discountItemColumn, 150, Unit.PX);
		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getPrice().toString();
				}
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					if (!isSubProductLinked(bmObject))
						changePrice(bmObject, value);
					else {
						// Mostrar mensaje y regresar el precio original
						showSystemMessage("NO se puede Modificar; el Sub Producto viene de un Producto Compuesto.");
						changePrice(bmObject, ((BmoOrderItem)bmObject).getPrice().toString());
					}
				}
			});
		} else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderItem)bmObject).getPrice().toString();
				}
			};
		}
		SafeHtmlHeader priceHeader = new SafeHtmlHeader(new SafeHtml() { 
			private static final long serialVersionUID = 1L;
			@Override 
			public String asString() { 
				return "<p style=\"text-align:right;\">Precio</p>"; 
			} 
		}); 
		orderItemGrid.addColumn(priceColumn, priceHeader);
		orderItemGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Columna de Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderItem)bmObject).getAmount().toDouble());
				return (formatted);
			}
		};
		SafeHtmlHeader totalHeader = new SafeHtmlHeader(new SafeHtml() { 
			private static final long serialVersionUID = 1L;

			@Override 
			public String asString() { 
				return "<p style=\"text-align:right;\">Total</p>"; 
			} 
		}); 
		orderItemGrid.addColumn(totalColumn, totalHeader);
		orderItemGrid.setColumnWidth(totalColumn, 50, Unit.PX);
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		if (isMobile()) {
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
				// Etatus Apartado
				Column<BmObject, String> lockColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderItem)bmObject).getLockStatus().getSelectedOption().getLabel();
					}
				};
				orderItemGrid.addColumn(lockColumn, SafeHtmlUtils.fromSafeConstant("E. Apartado"));
				orderItemGrid.setColumnWidth(lockColumn, 50, Unit.PX);
				lockColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

				// Estatus Apartado
				Column<BmObject, String> lockInfoColumn = new Column<BmObject, String>(new ButtonCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						BmoOrderItem bmoOrderItem = (BmoOrderItem)bmObject;
						if (bmoOrderItem.getBmoProduct().getType().toChar() == BmoProduct.TYPE_CLASS) return "Cambiar";
						else if (bmoOrderItem.getBmoProduct().getType().toChar() == BmoProduct.TYPE_PRODUCT) {
							// No es de clase, ver estatus
							if (((BmoOrderItem)bmObject).getLockStatus().toChar() == BmoOrderItem.LOCKSTATUS_LOCKED) return "Info";
							else if (((BmoOrderItem)bmObject).getLockStatus().toChar() == BmoOrderItem.LOCKSTATUS_CONFLICT) return "Revisar";
							else return "";
						} else return "";
					}
				};
				lockInfoColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						BmoOrderItem bmoOrderItem = (BmoOrderItem)bmObject;
						if (bmoOrderItem.getProductId().toInteger() > 0)
							if (bmoOrderItem.getBmoProduct().getType().toChar() == BmoProduct.TYPE_PRODUCT)
								Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/rpt/orde_product_availability.jsp?orde_orderid=" + bmoOrderItem.getBmoOrderGroup().getOrderId().toString() + "&prod_productid=" + bmoOrderItem.getProductId().toString()), "_blank", "");
							else if (bmoOrderItem.getBmoProduct().getType().toChar() == BmoProduct.TYPE_CLASS) {
								fixProductClass(bmoOrderItem);
							}
					}
				});
				orderItemGrid.addColumn(lockInfoColumn, SafeHtmlUtils.fromSafeConstant("Info Apartado"));
				orderItemGrid.setColumnWidth(lockInfoColumn, 50, Unit.PX);
				lockInfoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
		}

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "-";
				}
			};
			deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					if (!isSubProductLinked(bmObject))
						deleteItem(bmObject);
					else {
						// Mostrar mensaje y no borrar
						showSystemMessage("NO se puede Modificar; el Sub Producto viene de un Producto Compuesto.");
						//deleteItem(bmObject);
					}
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
		orderItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	

		//crear proyecto
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();
//		if(getUiParams().getSFParams().hasRead(bmoProjectStep.getProgramCode())) {
//			if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean()) {
//				Column<BmObject, Boolean> addProject = new Column<BmObject, Boolean>(new CheckboxCell(true,false)) {
//					@Override
//					public Boolean getValue(BmObject bmObject) {				
//						return ((BmoOrderItem)bmObject).getCreateProject().toBoolean();
//					}
//				};
//				addProject.setFieldUpdater(new FieldUpdater<BmObject, Boolean>() {
//
//					@Override
//					public void update(int index, BmObject object, Boolean value) {		
//						bmoOrderItem = (BmoOrderItem)object;
//
//						try {
//
//							bmoOrderItem.getCreateProject().setValue(value);
//
//						} catch (BmException e) {	
//							showErrorMessage(e.getMessage());
//						}				
//						saveItem();						
//					}
//				});
//
//				orderItemGrid.addColumn(addProject,  SafeHtmlUtils.fromSafeConstant("Proy?"));
//				addProject.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//				orderItemGrid.setColumnWidth(addProject, 50, Unit.PX);	
//			}
//		}
	}
	
	public void updateDiscount(BmObject bmObject,boolean value) {
		BmoOrderItem nextBmoOrderItem = (BmoOrderItem)bmObject;
		try {
			nextBmoOrderItem.getDiscountApplies().setValue(value);
		} catch (BmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Window.confirm("Desea guardar el cambio?")) {
			saveOrderItem(nextBmoOrderItem);
		}
	}
	
	public void saveOrderItem(BmoOrderItem bmoOrderItem) {
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
				getUiParams().getBmObjectServiceAsync().save(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
		}
	}

	public void addProduct() {
		addProduct(new BmoProduct());
	}

	public void addProduct(BmoProduct bmoProduct) {
		orderItemDialogBox = new DialogBox(true);
		orderItemDialogBox.setGlassEnabled(true);
		orderItemDialogBox.setText("Item de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		orderItemDialogBox.setWidget(vp);

		UiOrderItemForm orderItemForm = new UiOrderItemForm(getUiParams(), vp, bmoOrder, orderGroupId, bmoProduct);

		orderItemForm.show();

		orderItemDialogBox.center();
		orderItemDialogBox.show();
	}

	public void saveOrderGroup() {
		try {
			bmoOrderGroup.setId(orderGroupId);
			bmoOrderGroup.getName().setValue(nameTextBox.getText());
			bmoOrderGroup.getShowQuantity().setValue(showQuantityCheckBox.getValue());
			bmoOrderGroup.getShowPrice().setValue(showPriceCheckBox.getValue());
			bmoOrderGroup.getShowAmount().setValue(showAmountCheckBox.getValue());
			bmoOrderGroup.getShowProductImage().setValue(showProductImageCheckBox.getValue());
			bmoOrderGroup.getShowGroupImage().setValue(showGroupImageCheckBox.getValue());	
			bmoOrderGroup.getCreateRaccount().setValue(createRaccountCheckBox.getValue());
			bmoOrderGroup.getImage().setValue(imageFileUpload.getBlobKey());
			bmoOrderGroup.getDays().setValue(daysTextBox.getValue());
			bmoOrderGroup.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());
			if (bmoOrderGroup.getIsKit().toBoolean()
					&& getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEKITPRICE))
				bmoOrderGroup.getAmount().setValue(amountTextBox.getText());
			else 
				bmoOrderGroup.getAmount().setValue(amountLabel.getText());
			
			if (bmoOrderGroup.getIsKit().toBoolean())
				bmoOrderGroup.getShowItems().setValue(showItmesCheckBox.getValue());
			
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				bmoOrderGroup.getDiscountApplies().setValue(discountApliesCheckBox.getValue());
				bmoOrderGroup.getDiscountRate().setValue(discountRateTextBox.getText());
				bmoOrderGroup.getCommissionApply().setValue(commissionAplyCheckBox.getValue());
				bmoOrderGroup.getCommissionRate().setValue(commisionRate.getText());
				bmoOrderGroup.getFeeProductionApply().setValue(feeProductionApplyCheckBox.getValue());
				bmoOrderGroup.getFeeProductionRate().setValue(feePrductionRateTextBox.getText());
			}
			
			save();

		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-formValueChange() ERROR: " + e.toString());
		}
	}

	// Obtiene objeto del servicio, primer intento
	public void get() {
		get(0);
	}

	// Obtiene objeto del servicio
	public void get(int bmoOrderGroupRpcAttempt) {
		if (bmoOrderGroupRpcAttempt < 5) {
			setBmoOrderGroupRpcAttempt(bmoOrderGroupRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getBmoOrderGroupRpcAttempt() < 5)
						get(getBmoOrderGroupRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-get() ERROR: " + caught.toString());
				}

				public void onSuccess(BmObject result) {
					stopLoading();
					setBmoOrderGroupRpcAttempt(0);
					setBmObject((BmObject)result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoOrderGroup.getPmClass(), orderGroupId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
			}
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
				getUiParams().getBmObjectServiceAsync().save(bmoOrderGroup.getPmClass(), bmoOrderGroup, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
		}
	}

	public void processUpdateResult(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
		else {
			orderGroupTitle.setText(bmoOrderGroup.getName().toString());
			orderUpdater.changeOrder();
			this.reset();
		}
	}

	public void delete() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processDeleteResult(result);
			}
		};

		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoOrderGroup.getPmClass(), bmoOrderGroup, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + e.toString());
		}
	}

	public void processDeleteResult(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processDeleteResult() ERROR: " + bmUpdateResult.errorsToString());
		else {
			orderUpdater.changeOrder();
			orderUpdater.updateOrderGroups();
		}
	}

	public void changeName(BmObject bmObject, String name) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderItem = (BmoOrderItem)bmObject;
			try {
				if (name.length() > 0) {
					bmoOrderItem.getName().setValue(name);
					saveItem();
				} else {
					// Eliminar registro
					deleteItem();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeName() ERROR: " + e.toString());
			}
		} else {
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				showSystemMessage("No se puede editar la Cotización - Está Finalizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				showSystemMessage("No se puede editar la Cotización - Está Cancelada.");

			this.reset();
		}
	}

	// Cambiar orden del grupo
	public void changeGroupIndex(String direction) {

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showSystemMessage(this.getClass().getName() + "-changeGroupIndex() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.hasErrors()) {
					showSystemMessage("Error al mover Grupo: " + result.errorsToString());	
				} else {
					// Actualiza lista de grupos de cotizaciones
					orderUpdater.updateOrderGroups();
				}
			}
		};

		try {	
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrderGroup.getPmClass(), bmoOrderGroup, BmoOrderGroup.ACTION_CHANGEINDEX, direction, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-changeGroupIndex() ERROR: " + e.toString());
		}
	} 
	
	// Cambiar orden del item
	public void changeItemIndex(BmoOrderItem bmoOrderItem, String direction) {

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showSystemMessage(this.getClass().getName() + "-changeItemIndex() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.hasErrors()) {
					showSystemMessage("Error al mover Item: " + result.errorsToString());	
				} else {
					// Actualiza lista de grupos de pedidos
					reset();
				}
			}
		};

		try {	
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrderItem.getPmClass(), bmoOrderItem, BmoOrderItem.ACTION_CHANGEINDEX, direction, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-changeItemIndex() ERROR: " + e.toString());
		}
	} 

	public void changeItemDialog(BmoOrderItem bmoOrderItem) {
		this.bmoOrderItem = bmoOrderItem;

		if (bmoOrderItem.getProductId().toInteger() > 0) {
			changeItemDialogBox = new DialogBox(true);
			changeItemDialogBox.setGlassEnabled(true);
			changeItemDialogBox.setText("Cambio de Item");
			changeItemDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			changeItemDialogBox.setWidget(vp);

			UiFormFlexTable formChangeItemTable = new UiFormFlexTable(getUiParams());
			BmoOrderItem bmoChangeOrderItem = new BmoOrderItem();
			formChangeItemTable.addField(1, 0, changeItemSuggestBox, bmoChangeOrderItem.getProductId());

			HorizontalPanel changeItemButtonPanel = new HorizontalPanel();
			changeItemButtonPanel.add(changeItemButton);
			changeItemButtonPanel.add(closeItemButton);

			vp.add(formChangeItemTable);
			vp.add(changeItemButtonPanel);

			changeItemDialogBox.center();
			changeItemDialogBox.show();
		}
	}

	public void changeItem() {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			if (changeItemSuggestBox.getSelectedId() > 0)
				changeItemAction();
		} else {
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				showSystemMessage("No se puede editar la Cotización - Está Finalizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				showSystemMessage("No se puede editar la Cotización - Está Cancelada.");
		}
	}

	// Cambiar el item del pedido
	public void changeItemAction() {
		changeItemAction(0);
	}
	// Cambiar el item del pedido
	public void changeItemAction(int changeItemActionRpcAttempt) {
		if (changeItemActionRpcAttempt < 5) {
			setChangeItemActionRpcAttempt(changeItemActionRpcAttempt + 1);
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getChangeItemActionRpcAttempt() < 5)
						changeItemAction(getChangeItemActionRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-changeItemAction() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setChangeItemActionRpcAttempt(0);
					orderUpdater.changeOrder();
					reset();
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrderItem.getPmClass(), bmoOrderItem, BmoOrderItem.ACTION_CHANGEORDERITEM, "" + changeItemSuggestBox.getSelectedId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-changeItemAction() ERROR: " + e.toString());
			}
		}
	} 

	public void descriptionDialog(BmoOrderItem bmoOrderItem) {
		this.bmoOrderItem = bmoOrderItem;
		descriptionDialogBox = new DialogBox(true);
		descriptionDialogBox.setGlassEnabled(true);
		descriptionDialogBox.setText("Descripción Item");
		descriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		descriptionDialogBox.setWidget(vp);

		descriptionTextArea = new TextArea();
		descriptionTextArea.setSize("320px", "190px");
		descriptionTextArea.setText(bmoOrderItem.getDescription().toString());
		vp.add(descriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		// No dejar editar descripcion si es un subproducto de un producto compuesto
		if (!isSubProductLinked(bmoOrderItem))
			descriptionButtonPanel.add(changeDescriptionButton);
		descriptionButtonPanel.add(closeDescriptionButton);
		vp.add(descriptionButtonPanel);

		descriptionDialogBox.center();
		descriptionDialogBox.show();
	}

	public void changeDescription() {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			String description = descriptionTextArea.getText();
			try {
				bmoOrderItem.getDescription().setValue(description);
				saveItem();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeDescription() ERROR: " + e.toString());
			}
		} else {
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				showSystemMessage("No se puede editar la Cotización - Está Finalizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				showSystemMessage("No se puede editar la Cotización - Está Cancelada.");
			;
			this.reset();
		}
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderItem = (BmoOrderItem)bmObject;
			try {
				double q = Double.parseDouble(quantity);
				if (q > 0) {
					bmoOrderItem.getQuantity().setValue(quantity);
					saveItem();
				} else {
					showSystemMessage("La Cantidad no puede ser menor a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity() ERROR: " + e.toString());
			}
		} else {
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				showSystemMessage("No se puede editar la Cotización - Está Finalizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				showSystemMessage("No se puede editar la Cotización - Está Cancelada.");

			this.reset();
		}
	}

	public void changeDays(BmObject bmObject, String days) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderItem = (BmoOrderItem)bmObject;
			try {
				double d = Double.parseDouble(days);
				if (d > 0) {
					bmoOrderItem.getDays().setValue(days);
					saveItem();
				} else {
					showSystemMessage("Los Días no pueden ser menores a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeDays() ERROR: " + e.toString());
			}
		} else {
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				showSystemMessage("No se puede editar la Cotización - Está Finalizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				showSystemMessage("No se puede editar la Cotización - Está Cancelada.");

			this.reset();
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderItem = (BmoOrderItem)bmObject;
			try {
				Double p = Double.parseDouble(price);

				if (bmoOrderItem.getProductId().toInteger() > 0) {
					// Es de tipo Producto
					if (p == 0) {
						bmoOrderItem.getPrice().setValue(0);
						saveItem();
					} else if (p > 0) {

						if (p >= bmoOrderItem.getBasePrice().toDouble()) {
							// Se esta subiendo precio, permitirlo
							bmoOrderItem.getPrice().setValue(p);
							saveItem();
							reset();

						} else if (p < bmoOrderItem.getBasePrice().toDouble()) {
							// El precio es menor al precio base, validar permisos
							if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
								bmoOrderItem.getPrice().setValue(p);
								saveItem();
								reset();
							} else {
								numberFormat = NumberFormat.getCurrencyFormat();
								String formatted = numberFormat.format(bmoOrderItem.getBasePrice().toDouble());
								showSystemMessage("No cuenta con Permisos para establecer Precio menor a: " + formatted);
								reset();
							}
						}

					} else {
						if  (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
							bmoOrderItem.getPrice().setValue(p);
							saveItem();							
						} else {
							showSystemMessage("El Precio no puede ser menor a $0.00");							
						}

					}
				} else {
					// No es de tipo producto, permite todo
					bmoOrderItem.getPrice().setValue(p);
					saveItem();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED)
				showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)
				showSystemMessage("No se puede editar la Cotización - Está Finalizada.");
			if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED)
				showSystemMessage("No se puede editar la Cotización - Está Cancelada.");

			this.reset();
		}
	}

	public void fixProductClass(BmoOrderItem bmoOrderItem) {
		orderItemDialogBox = new DialogBox(true);
		orderItemDialogBox.setGlassEnabled(true);
		orderItemDialogBox.setText("Cambiar Producto tipo Clase");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		orderItemDialogBox.setWidget(vp);

		UiOrderItemProductClassForm orderItemProductClassForm = new UiOrderItemProductClassForm(getUiParams(), vp, orderGroupId, bmoOrderItem, bmoOrder);
		orderItemProductClassForm.show();

		orderItemDialogBox.center();
		orderItemDialogBox.show();
	}

	public void processItemSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) {
			showErrorMessage(this.getClass().getName() + "-processItemChangeSave() ERROR: " + bmUpdateResult.errorsToString());
			this.reset();
		} else {
			orderUpdater.changeOrder();
			this.reset();
		}
	}

	public void processItemDelete(BmUpdateResult result) {
		if (result.hasErrors()) showErrorMessage(this.getClass().getName() + "-processItemDelete() ERROR: " + result.errorsToString());
		else {
			orderUpdater.changeOrder();
			this.reset();
		}
	}

	// Primer intento
	public void saveItem() {
		saveItem(0);
	}

	public void saveItem(int saveItemRpcAttempt) {
		if (saveItemRpcAttempt < 5) {
			setSaveItemRpcAttempt(saveItemRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveItemRpcAttempt() < 5)
						setSaveItemRpcAttempt(getSaveItemRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveItemChange() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveItemRpcAttempt(0);
					processItemSave(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange() ERROR: " + e.toString());
			}
		}
	}

	public void deleteItem(BmObject bmObject) {
		bmoOrderItem = (BmoOrderItem)bmObject;
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			if (!bmoOrderItem.getBmoOrderGroup().getIsKit().toBoolean())
				deleteItem();
			else 
				showErrorMessage("No se puede eliminar el ítem , es un Kit");
		} else {
			deleteItem();
		}
	}

	public void deleteItem() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItem() ERROR: " + caught.toString());
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
				getUiParams().getBmObjectServiceAsync().delete(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem() ERROR: " + e.toString());
		}
	}

	public void reset() {
		get();
		data.list();
		orderItemGrid.redraw();
	}

	// Agrega un item de un producto a la orden de compra
	private class UiOrderItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox daysTextBox = new TextBox();
		private TextBox nameTextBox = new TextBox();
		private TextArea descriptionTextArea = new TextArea();
		private TextBox priceTextBox = new TextBox();
		private CheckBox commissionCheckBox = new CheckBox();
		private CheckBox discountApliesItemCheckBox = new CheckBox();
		private BmoOrderItem bmoOrderItem;
		private BmoOrder bmoOrder = new BmoOrder();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		private UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		int orderGroupId;
		String productId = "";
		private int stockQuantityRpcAttempt = 0;
		private int lockedQuantityRpcAttempt = 0;
		private int priceRpcAttempt = 0;
		private int productCompanySpecificRpcAttempt = 0;

		public UiOrderItemForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, int orderGroupId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = new BmoOrderItem();
			this.bmoOrder = bmoOrder;
			this.orderGroupId = orderGroupId;

			try {
				bmoOrderItem.getOrderGroupId().setValue(orderGroupId);
				bmoOrderItem.getProductId().setValue(bmoProduct.getId());
				bmoOrderItem.getName().setValue("item");
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
			}

			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
				@Override
				public void onSelect(UiSuggestBox uiSuggestBox) {
					formSuggestionChange(uiSuggestBox);
				}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrder.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);


			//filtro para mostrar los productos que estan habilitados			
//			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoOrderItem.getBmoProduct().getKind(), bmoOrderItem.getBmoProduct().getEnabled(), "1");
//			filterList.add(filterByEnabled);
			
			productSuggestBox = new UiSuggestBox(new BmoProduct());
			productSuggestBox.addFilter(filterByEnabled);
			// Filtros para pedido de Renta(Drea)
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {			
				BmFilter productTypeSubProductFilter = new BmFilter();
				productTypeSubProductFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS, ""+BmoProduct.TYPE_SUBPRODUCT);
				productSuggestBox.addFilter(productTypeSubProductFilter);
				BmFilter productTypeCompProductFilter = new BmFilter();
				productTypeCompProductFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS, ""+BmoProduct.TYPE_COMPLEMENTARY);
				productSuggestBox.addFilter(productTypeCompProductFilter);
				BmFilter consumableFilter = new BmFilter();
				consumableFilter.setValueFilter(bmoProduct.getKind(), bmoProduct.getConsumable(), 0);
				productSuggestBox.addFilter(consumableFilter);
			}

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				try {
					if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0)
						bmoOrderItem.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
					if (bmoOrder.getDefaultAreaId().toInteger() > 0)
						bmoOrderItem.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
				}
			}

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, productSuggestBox, bmoOrderItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacén", stockQuantity);
			formTable.addLabelField(3, 0, "En Pedidos", lockedQuantity);
			if (bmoOrderGroup.getDiscountApplies().toBoolean() && bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				formTable.addField(4, 0, discountApliesItemCheckBox, bmoOrderItem.getDiscountApplies());
				discountApliesItemCheckBox.setValue(true);
			}
			
			formTable.addField(5, 0, nameTextBox, bmoOrderItem.getName());
			formTable.addField(6, 0, descriptionTextArea, bmoOrderItem.getDescription());
			formTable.addField(7, 0, quantityTextBox, bmoOrderItem.getQuantity());
			// Mostrar los días si es de tipo renta
			if (bmoOrder.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL))
				formTable.addField(8, 0, daysTextBox, bmoOrderItem.getDays());

			if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission()))
				formTable.addField(9, 0, commissionCheckBox, bmoOrderItem.getCommission());
			formTable.addField(10, 0, priceTextBox, bmoOrderItem.getPrice());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				setBudgetItemsListBoxFilters(bmoOrder.getCompanyId().toInteger());
				formTable.addField(11, 0, budgetItemUiListBox, bmoOrderItem.getBudgetItemId());
				formTable.addField(12, 0, areaUiListBox, bmoOrderItem.getAreaId());
			}
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		// Actualiza combo de departamentos
		//		private void populateArea() {
		//			areaUiListBox.clear();
		//			areaUiListBox.clearFilters();
		//			areaUiListBox.populate(bmoQuoteItem.getAreaId());
		//		}
		//		
		//		// Actualiza combo de partidas presp. por empresa
		//		private void populateBudgetItems(int companyId) {
		//			budgetItemUiListBox.clear();
		//			budgetItemUiListBox.clearFilters();
		//			setBudgetItemsListBoxFilters(companyId);
		//			budgetItemUiListBox.populate(bmoQuoteItem.getBudgetItemId());
		//		}

		// Asigna filtros al listado de partidas presp. por empresa
		private void setBudgetItemsListBoxFilters(int companyId) {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
				budgetItemUiListBox.addBmFilter(bmFilterByCompany);

				// Filtro de ingresos(abono)
				BmFilter filterByDeposit = new BmFilter();
				filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
				budgetItemUiListBox.addFilter(filterByDeposit);

			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
				budgetItemUiListBox.addBmFilter(bmFilter);
			}
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == productSuggestBox) {
				if (productSuggestBox.getSelectedId() > 0) {
					if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission())) {
						//Si el producto maneja comisión marcar el check
						BmoProduct bmoProduct = (BmoProduct)productSuggestBox.getSelectedBmObject();
						//					try {
						//						bmoOrderItem.getCommission().setValue(bmoProduct.getCommision().toBoolean());
						//					} catch (BmException e) {
						//						showSystemMessage("Error al marcar la comision " + e.toString());					
						//					}
						//					formTable.addField(8, 0, commissionCheckBox, bmoOrderItem.getCommission());
						if (bmoProduct.getCommision().toBoolean())
							commissionCheckBox.setValue(true);
						else 
							commissionCheckBox.setValue(false);
					}
				}
				statusEffect();
			}
		}

		private void statusEffect() {
			nameTextBox.setText("");
			nameTextBox.setEnabled(false);
			priceTextBox.setText("");
			priceTextBox.setEnabled(false);			

			if (productSuggestBox.getSelectedId() > 0) {
				productId = "" + productSuggestBox.getSelectedId();
				getStockQuantity();

			} else {
				nameTextBox.setText("item");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				stockQuantity.setText("");
				lockedQuantity.setText("");
				if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission())) 
					commissionCheckBox.setValue(false);
				BmoProductCompany bmoProductCompany = new BmoProductCompany();
				setBudgetItemAndArea(bmoProductCompany, true);
			}

			if (!getSFParams().hasSpecialAccess(BmoOrder.ACCESS_NOPRODUCTITEM)) {
				nameTextBox.setText("");
				nameTextBox.setEnabled(false);
				priceTextBox.setText("");
				priceTextBox.setEnabled(false);
			}

			if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) 
				priceTextBox.setEnabled(true);

			if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMNAME)) 
				nameTextBox.setEnabled(true);
		}

		//Obtener la cantidad en almacen, primer intento
		public void getStockQuantity() {
			getStockQuantity(0);
		}

		//Obtener la cantidad en almacen
		public void getStockQuantity(int stockQuantityRpcAttempt) {
			if (stockQuantityRpcAttempt < 5) {
				setStockQuantityRpcAttempt(stockQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getStockQuantityRpcAttempt() < 5)
							getStockQuantity(getStockQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setStockQuantityRpcAttempt(0);
						BmoWhStock bmoWhStock = (BmoWhStock)result.getBmObject();
						stockQuantity.setText(bmoWhStock.getQuantity().toString());
						getLockedQuantity();
					}
				};

				try {	
					if (!isLoading()) {
						stockQuantity.setText("");
						BmoWhStock bmoWhStock = new BmoWhStock();
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY, productId + "|" + this.bmoOrder.getCompanyId().toInteger(), callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + e.toString());
				}
			}
		} 

		//Obtener la cantidad en cotizacion, primer intento
		public void getLockedQuantity() {
			getLockedQuantity(0);
		}

		//Obtener la cantidad en pedidos
		public void getLockedQuantity(int lockedQuantityRpcAttempt) {
			if (lockedQuantityRpcAttempt < 5) {
				setLockedQuantityRpcAttempt(lockedQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getLockedQuantityRpcAttempt() < 5)
							getLockedQuantity(getLockedQuantityRpcAttempt());
						else 
							showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setLockedQuantityRpcAttempt(0);
						BmoOrderItem bmoOrderItem = (BmoOrderItem)result.getBmObject();
						lockedQuantity.setText(bmoOrderItem.getQuantity().toString());	
						getPrice();
					}
				};

				try {	
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_LOCKEDQUANTITY,  productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + e.toString());
				}
			}
		} 

		//Obtener precio de venta/renta del producto, primer intento
		public void getPrice() {
			getPrice(0);
		}

		//Obtener precio de venta/renta del producto
		public void getPrice(int priceRpcAttempt) {
			if (priceRpcAttempt < 5) {
				setPriceRpcAttempt(priceRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getPriceRpcAttempt() < 5)
							getPrice(getPriceRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setPriceRpcAttempt(0);
						setProductPrice((BmoProductPrice)result.getBmObject());
						getProductCompanySpecific();
					}
				};

				try {	
					if (!isLoading()) {
						priceTextBox.setText("");
						BmoProductPrice bmoProductPrice = new BmoProductPrice();
						bmoProductPrice.getStartDate().setValue(bmoOrder.getLockStart().toString());
						bmoProductPrice.getProductId().setValue(productSuggestBox.getSelectedId());
						bmoProductPrice.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
						bmoProductPrice.getOrderTypeId().setValue(bmoOrder.getOrderTypeId().toInteger());
						bmoProductPrice.getWFlowTypeId().setValue(bmoOrder.getWFlowTypeId().toInteger());
						bmoProductPrice.getMarketId().setValue(bmoOrder.getMarketId().toInteger());
						bmoProductPrice.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());

						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoProductPrice.getPmClass(), bmoProductPrice, BmoProductPrice.ACTION_GETPRICE,  productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + e.toString());
				}
			}
		} 

		// Asigna precio vigente
		private void setProductPrice(BmoProductPrice bmoProductPrice) {
			nameTextBox.setText("" + ((BmoProduct)productSuggestBox.getSelectedBmObject()).getName().toString());
			descriptionTextArea.setText("" + ((BmoProduct)productSuggestBox.getSelectedBmObject()).getDescription().toString());
			priceTextBox.setText("" + bmoProductPrice.getPrice().toDouble());
		}

		// Obtener empresa del producto, primer intento
		public void getProductCompanySpecific() {
			getProductCompanySpecific(0);
		}

		// Obtener empresa del producto
		public void getProductCompanySpecific(int productCompanySpecificRpcAttempt) {
			if (productCompanySpecificRpcAttempt < 5) {
				setProductCompanySpecificRpcAttempt(productCompanySpecificRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getProductCompanySpecificRpcAttempt() < 5)
							getProductCompanySpecific(getProductCompanySpecificRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getProductCompanySpecific() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setProductCompanySpecificRpcAttempt(0);
						setBudgetItemAndArea((BmoProductCompany)result.getBmObject(), false);
					}
				};

				try {	
					if (!isLoading()) {
						BmoProductCompany bmoProductCompany = new BmoProductCompany();
						bmoProductCompany.getCompanyId().setValue(bmoOrder.getCompanyId().toString());

						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoProductCompany.getPmClass(), bmoProductCompany, BmoProductCompany.ACTION_GETPRODUCTCOMPANY, productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getProductCompanySpecific() ERROR: " + e.toString());
				}
			}
		} 

		// Asigna partida presp./departamento
		private void setBudgetItemAndArea(BmoProductCompany bmoProductCompany, boolean defaultOrder) {

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				// Colocar partida y dpto de la cotizacion
				if (defaultOrder) {
					if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0)
						budgetItemUiListBox.setSelectedId("" + bmoOrder.getDefaultBudgetItemId().toInteger());
					if (bmoOrder.getDefaultAreaId().toInteger() > 0)
						areaUiListBox.setSelectedId("" + bmoOrder.getDefaultAreaId().toInteger());
				} else {
					// Colocar partida/dpto. de la empresa del producto
					if (bmoProductCompany.getId() > 0) {
						if (bmoProductCompany.getBudgetItemId().toInteger() > 0)
							budgetItemUiListBox.setSelectedId("" + bmoProductCompany.getBudgetItemId().toInteger());
						if (bmoProductCompany.getAreaId().toInteger() > 0)
							areaUiListBox.setSelectedId("" + bmoProductCompany.getAreaId().toInteger());
					} 
					// Colocar partida/dpto. del producto(no tiene empresas), si no tiene datos regresar por defecto a las del pedido
					else {
						BmoProduct bmoProduct = new BmoProduct();
						bmoProduct = ((BmoProduct)productSuggestBox.getSelectedBmObject());
						if (bmoProduct.getBudgetItemId().toInteger() > 0) 
							budgetItemUiListBox.setSelectedId("" + bmoProduct.getBudgetItemId().toInteger());
						else {
							if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0) 
								budgetItemUiListBox.setSelectedId("" + bmoOrder.getDefaultBudgetItemId().toInteger());
						}
						if (bmoProduct.getAreaId().toInteger() > 0) 
							areaUiListBox.setSelectedId("" + bmoProduct.getAreaId().toInteger());
						else {
							if (bmoOrder.getDefaultAreaId().toInteger() > 0) 
								areaUiListBox.setSelectedId("" + bmoOrder.getDefaultAreaId().toInteger());
						}
					}
				}
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				orderUpdater.changeOrder();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderItem = new BmoOrderItem();
				bmoOrderItem.getOrderGroupId().setValue(orderGroupId);
				bmoOrderItem.getProductId().setValue(productSuggestBox.getSelectedId());
				bmoOrderItem.getName().setValue(nameTextBox.getText());
				bmoOrderItem.getDescription().setValue(descriptionTextArea.getText());
				bmoOrderItem.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderItem.getDays().setValue(daysTextBox.getText());
				bmoOrderItem.getPrice().setValue(priceTextBox.getText());
				bmoOrderItem.getCommission().setValue(commissionCheckBox.getValue());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
					bmoOrderItem.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
					bmoOrderItem.getAreaId().setValue(areaUiListBox.getSelectedId());
				}
				bmoOrderItem.getDiscountApplies().setValue(discountApliesItemCheckBox.getValue());
				// Si no tiene permisos para agregar items sin producto, no permite avanzar
				if (!(bmoOrderItem.getProductId().toInteger() > 0)
						&& !getSFParams().hasSpecialAccess(BmoOrder.ACCESS_NOPRODUCTITEM))
					showSystemMessage("No cuenta con Permisos para agregar Items sin Producto Ligado: debe Seleccionar un Producto.");
				else
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}

		// Variables para llamadas RPC
		public int getStockQuantityRpcAttempt() {
			return stockQuantityRpcAttempt;
		}

		public void setStockQuantityRpcAttempt(int stockQuantityRpcAttempt) {
			this.stockQuantityRpcAttempt = stockQuantityRpcAttempt;
		}

		public int getLockedQuantityRpcAttempt() {
			return lockedQuantityRpcAttempt;
		}

		public void setLockedQuantityRpcAttempt(int lockedQuantityRpcAttempt) {
			this.lockedQuantityRpcAttempt = lockedQuantityRpcAttempt;
		}

		public int getPriceRpcAttempt() {
			return priceRpcAttempt;
		}

		public void setPriceRpcAttempt(int priceRpcAttempt) {
			this.priceRpcAttempt = priceRpcAttempt;
		}

		public int getProductCompanySpecificRpcAttempt() {
			return productCompanySpecificRpcAttempt;
		}

		public void setProductCompanySpecificRpcAttempt(int productCompanySpecificRpcAttempt) {
			this.productCompanySpecificRpcAttempt = productCompanySpecificRpcAttempt;
		}
	}

	// Agrega un item de un producto de clase especifica a la orden de compra
	private class UiOrderItemProductClassForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox daysTextBox = new TextBox();
		private BmoOrderItem bmoOrderItem;
		private BmoOrder bmoOrder = new BmoOrder();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiListBox productListBox;
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		String productId = "";
		int selectedOrderGroupId;
		private int stockQuantityRpcAttempt = 0;
		private int lockedQuantityRpcAttempt = 0;

		public UiOrderItemProductClassForm(UiParams uiParams, Panel defaultPanel, int selectedOrderGroupId, BmoOrderItem bmoOrderItem, BmoOrder bmoOrder) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = bmoOrderItem;
			this.productId = bmoOrderItem.getProductId().toString();
			this.selectedOrderGroupId = selectedOrderGroupId;
			this.bmoOrder = bmoOrder;

			try {
				bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
				bmoOrderItem.getProductId().setValue(productId);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
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
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrder.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			// Filtros del combo de productos de clase
			BmFilter filterByProductGroup = new BmFilter();
			filterByProductGroup.setValueFilter(bmoOrderItem.getBmoProduct().getKind(), 
					bmoOrderItem.getBmoProduct().getProductGroupId(),
					bmoOrderItem.getBmoProduct().getProductGroupId().toInteger());

			BmFilter filterByType = new BmFilter();
			filterByType.setValueFilter(bmoOrderItem.getBmoProduct().getKind(), 
					bmoOrderItem.getBmoProduct().getType(), "" + BmoProduct.TYPE_PRODUCT);

			productListBox = new UiListBox(getUiParams(), new BmoProduct());
			productListBox.addFilter(filterByProductGroup);
			productListBox.addFilter(filterByType);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, productListBox, bmoOrderItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacen", stockQuantity);
			formTable.addLabelField(3, 0, "En Pedidos", lockedQuantity);
			formTable.addField(4, 0, quantityTextBox, bmoOrderItem.getQuantity());
			formTable.addField(5, 0, daysTextBox, bmoOrderItem.getDays());

			formTable.addButtonPanel(buttonPanel);
		}

		// 
		public void formListChange(ChangeEvent event) {
			String selectedId = productListBox.getSelectedId();
			if (!selectedId.equals("")) {
				// Ya se selecciono un producto, buscar disponibilidad
				productId = "" + selectedId;
				getStockQuantity();
				getLockedQuantity();
			}
		}
		//Obtener la cantidad en almacen, primer intento
		public void getStockQuantity() {
			getStockQuantity(0);
		}
		//Obtener la cantidad en almacen
		public void getStockQuantity(int stockQuantityRpcAttempt) {
			if (stockQuantityRpcAttempt < 5) {
				setStockQuantityRpcAttempt(stockQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getStockQuantityRpcAttempt() < 5)
							getStockQuantity(getStockQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setStockQuantityRpcAttempt(0);
						BmoWhStock bmoWhStock = (BmoWhStock)result.getBmObject();
						stockQuantity.setText(bmoWhStock.getQuantity().toString());
						getLockedQuantity();
					}
				};

				try {
					stockQuantity.setText("");
					BmoWhStock bmoWhStock = new BmoWhStock();
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY,  productId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + e.toString());
				}
			}
		}

		//Obtener la cantidad en cotizacion, primer intento
		public void getLockedQuantity() {
			getLockedQuantity(0);
		}

		//Obtener la cantidad en almacen
		public void getLockedQuantity(int lockedQuantityRpcAttempt) {
			if (lockedQuantityRpcAttempt < 5) {
				setLockedQuantityRpcAttempt(lockedQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getLockedQuantityRpcAttempt() < 5)
							getLockedQuantity(getLockedQuantityRpcAttempt());
						else 
							showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setLockedQuantityRpcAttempt(0);
						BmoOrderItem bmoOrderItem = (BmoOrderItem)result.getBmObject();
						lockedQuantity.setText(bmoOrderItem.getQuantity().toString());				
					}
				};

				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_LOCKEDQUANTITY,  productId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + e.toString());
				}
			}
		} 

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				orderUpdater.changeOrder();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
				bmoOrderItem.getProductId().setValue(productListBox.getSelectedId());
				bmoOrderItem.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderItem.getDays().setValue(daysTextBox.getText());
				bmoOrderItem.getPrice().setValue(0);
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
		// Variables para llamadas RPC
		public int getStockQuantityRpcAttempt() {
			return stockQuantityRpcAttempt;
		}

		public void setStockQuantityRpcAttempt(int stockQuantityRpcAttempt) {
			this.stockQuantityRpcAttempt = stockQuantityRpcAttempt;
		}

		public int getLockedQuantityRpcAttempt() {
			return lockedQuantityRpcAttempt;
		}

		public void setLockedQuantityRpcAttempt(int lockedQuantityRpcAttempt) {
			this.lockedQuantityRpcAttempt = lockedQuantityRpcAttempt;
		}
	}
	// Variables para llamadas RPC
	public int getBmoOrderGroupRpcAttempt() {
		return bmoOrderGroupRpcAttempt;
	}

	public void setBmoOrderGroupRpcAttempt(int bmoOrderGroupRpcAttempt) {
		this.bmoOrderGroupRpcAttempt = bmoOrderGroupRpcAttempt;
	}

	public int getChangeItemActionRpcAttempt() {
		return changeItemActionRpcAttempt;
	}

	public void setChangeItemActionRpcAttempt(int changeItemActionRpcAttempt) {
		this.changeItemActionRpcAttempt = changeItemActionRpcAttempt;
	}

	public int getSaveItemRpcAttempt() {
		return saveItemRpcAttempt;
	}

	public void setSaveItemRpcAttempt(int saveItemRpcAttempt) {
		this.saveItemRpcAttempt = saveItemRpcAttempt;
	}
}
