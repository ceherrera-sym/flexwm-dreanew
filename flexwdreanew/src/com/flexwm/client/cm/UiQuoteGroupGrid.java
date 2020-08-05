package com.flexwm.client.cm;

import com.flexwm.client.cm.UiQuoteForm.QuoteUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoPayCondition;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.op.BmoWhStock;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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


public class UiQuoteGroupGrid extends Ui {
	private CellTable<BmObject> quoteItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	private BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
	private BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
	private TextArea descriptionTextArea = new TextArea();
	private int quoteGroupId;
	private BmFilter bmFilter;

	private FlowPanel quoteGroupPanel = new FlowPanel();
	private FlowPanel quoteItemPanel = new FlowPanel();
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private Button addQuoteItemButton = new Button("+ITEM");
	private Button deleteButton = new Button("ELIMINAR");

	DialogBox descriptionDialogBox;
	Button changeDescriptionButton = new Button("GUARDAR");
	Button closeDescriptionButton = new Button("CERRAR");

	// Cambio de Item
	UiSuggestBox changeItemSuggestBox;
	DialogBox changeItemDialogBox;
	Button changeItemButton = new Button("CAMBIAR");
	Button closeItemButton = new Button("CERRAR");

	// Mover grupo
	protected Image groupUpImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/up.png"));
	protected Image groupDownImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/down.png"));

	private Label quoteGroupTitle = new Label();
	private TextBox codeTextBox = new TextBox();
	private TextBox nameTextBox = new TextBox();
	private CheckBox showQuantityCheckBox = new CheckBox(); 
	private CheckBox showPriceCheckBox = new CheckBox();
	private CheckBox showAmountCheckBox = new CheckBox();
	private CheckBox showProductImageCheckBox = new CheckBox();
	private CheckBox showGroupImageCheckBox = new CheckBox();
	private CheckBox showItmesCheckBox = new CheckBox();
	private UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
	UiListBox payConditionUiListBox= new UiListBox(getUiParams(), new BmoPayCondition());
	private Label amountLabel = new Label("");
	private TextBox amountTextBox = new TextBox();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	protected Button saveButton = new Button("GUARDAR");

	protected DialogBox quoteItemDialogBox;

	private BmoQuote bmoQuote;
	protected BmoOpportunity bmoOpportunity;
	private QuoteUpdater quoteUpdater;
	private int bmoOrderGroupRpcAttempt = 0;
	private int saveItemRpcAttempt = 0;	

	public UiQuoteGroupGrid(UiParams uiParams, Panel defaultPanel, BmoQuote bmoQuote, BmoOpportunity bmoOpportunity, int quoteGroupId, QuoteUpdater quoteUpdater){
		super(uiParams, defaultPanel);
		this.quoteGroupId = quoteGroupId;
		this.bmoQuote = bmoQuote;
		this.bmoOpportunity = bmoOpportunity;
		bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(), quoteGroupId);
		data = new UiListDataProvider<BmObject>(bmoQuoteItem, bmFilter);
		this.quoteUpdater = quoteUpdater;

		// Asignar propiedades a los botones
		saveButton.setStyleName("formCloseButton");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveQuoteGroup();
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
//		filterByType.setValueOrFilter(bmoQuoteItem.getBmoProduct().getKind(), 
//				bmoQuoteItem.getBmoProduct().getType().getName(), "" + BmoProduct.TYPE_PRODUCT,  "" + BmoProduct.TYPE_SUBPRODUCT);
		filterByType.setValueOrOrFilter(bmoQuoteItem.getBmoProduct().getKind(), 
				bmoQuoteItem.getBmoProduct().getType().getName(), 
				"" + BmoProduct.TYPE_PRODUCT, 
				"" + BmoProduct.TYPE_SUBPRODUCT,
				"" + BmoProduct.TYPE_COMPLEMENTARY);
		changeItemSuggestBox = new UiSuggestBox(new BmoProduct(), filterByType);

		closeItemButton.setStyleName("formCloseButton");
		closeItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeItemDialogBox.hide();
			}
		});

		quoteGroupTitle.setText("Grupo Cotizacion");
		quoteGroupTitle.setStyleName("programSubtitle");

		// Inicializar paneles
		defaultPanel.clear();
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		quoteItemGrid.setWidth("100%", true);
		quoteGroupPanel.setWidth("100%");
		quoteItemPanel.setWidth("100%");
		setColumns();

		// Panel de botones
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

		buttonPanel.add(saveButton);

		// Dar de alta producto
		addQuoteItemButton.setStyleName("formSaveButton");
		addQuoteItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addProduct();
			}
		});
		buttonPanel.add(addQuoteItemButton);

		deleteButton.setStyleName("formDeleteButton");
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (Window.confirm("Está seguro que desea Eliminar este Grupo?")) delete();
			}
		});
		buttonPanel.add(deleteButton);

		// Handler de cambio de estatus del grid
		quoteItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 

		// Crear forma y campos
		formFlexTable.setWidth("100%");

		data.addDataDisplay(quoteItemGrid);

		// Acomodar paneles
		quoteGroupPanel.add(formFlexTable);
		quoteItemPanel.add(quoteItemGrid);
		quoteGroupPanel.addStyleName("separator");

		defaultPanel.add(quoteGroupPanel);
	}

	public void show() {
		get();
		data.list();
		statusEffect();
	}

	public void setBmObject(BmObject bmObject) {
		bmoQuoteGroup = (BmoQuoteGroup)bmObject;
		formFlexTable.clear();

		if (bmoQuoteGroup.getIsKit().toBoolean())
			formFlexTable.addSectionLabel(1, 0, "Kit de Items ", 4);
		else
			formFlexTable.addSectionLabel(1, 0, "Grupo de Items: " + bmoQuoteGroup.getName().toString(), 4);

		formFlexTable.addField(2, 0, nameTextBox, bmoQuoteGroup.getName());

		// Panel de Checkboxes
		FlowPanel checkBoxPanel = new FlowPanel();
		checkBoxPanel.setWidth("100%");
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showQuantityCheckBox, bmoQuoteGroup.getShowQuantity()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showAmountCheckBox, bmoQuoteGroup.getShowAmount()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showProductImageCheckBox, bmoQuoteGroup.getShowProductImage()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showPriceCheckBox, bmoQuoteGroup.getShowPrice()));
		checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showGroupImageCheckBox, bmoQuoteGroup.getShowGroupImage()));
		if (bmoQuoteGroup.getIsKit().toBoolean())
			checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showItmesCheckBox, bmoQuoteGroup.getShowItems()));
		formFlexTable.addLabelCell(3, 0, "Mostrar:");
		formFlexTable.addPanel(3, 1, checkBoxPanel, 1);

		formFlexTable.addField(4, 0, imageFileUpload, bmoQuoteGroup.getImage());
		if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
			formFlexTable.addField(5, 0, payConditionUiListBox, bmoQuoteGroup.getPayConditionId());
		
		// Si es kit y permite modificar monto
		if (bmoQuoteGroup.getIsKit().toBoolean()
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
			formFlexTable.addField(6, 0, amountTextBox, bmoQuoteGroup.getAmount());
		else
			formFlexTable.addLabelField(6, 0, amountLabel, bmoQuoteGroup.getAmount());
		formFlexTable.addPanel(7, 0, quoteItemPanel, 2);
		formFlexTable.addPanel(8, 0, buttonPanel, 2);

		numberFormat = NumberFormat.getCurrencyFormat();
		String formatted = numberFormat.format(bmoQuoteGroup.getAmount().toDouble());

		if (bmoQuoteGroup.getIsKit().toBoolean()
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
			amountTextBox.setText(formatted);
		else 
			amountLabel.setText(formatted);

		quoteGroupTitle.setText(bmoQuoteGroup.getName().toString());

		statusEffect();
	}

	public void statusEffect() {
		nameTextBox.setEnabled(false);
		showQuantityCheckBox.setEnabled(false);
		showPriceCheckBox.setEnabled(false);
		showAmountCheckBox.setEnabled(false);
		showItmesCheckBox.setEnabled(false);
		showProductImageCheckBox.setEnabled(false);
		showGroupImageCheckBox.setEnabled(false);
		addQuoteItemButton.setVisible(false);
		deleteButton.setVisible(false);
		saveButton.setVisible(false);
		payConditionUiListBox.setEnabled(false);
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			nameTextBox.setEnabled(true);
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);
			showAmountCheckBox.setEnabled(true);
			showItmesCheckBox.setEnabled(true);
			showProductImageCheckBox.setEnabled(true);
			showGroupImageCheckBox.setEnabled(true);
			addQuoteItemButton.setVisible(true);
			deleteButton.setVisible(true);
			saveButton.setVisible(true);
			payConditionUiListBox.setEnabled(true);
			if (bmoQuoteGroup.getIsKit().toBoolean()
					&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
				amountTextBox.setEnabled(true);
		}
	}

	public void changeHeight() {
		quoteItemGrid.setVisibleRange(0, data.getList().size());
	}

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
					changeItemIndex((BmoQuoteItem)bmObject, BmoFlexConfig.UP);
				}
			});
			quoteItemGrid.addColumn(upColumn, SafeHtmlUtils.fromSafeConstant(""));
			quoteItemGrid.setColumnWidth(upColumn, 30, Unit.PX);
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
					changeItemIndex((BmoQuoteItem)bmObject, BmoFlexConfig.DOWN);
				}
			});
			quoteItemGrid.addColumn(downColumn, SafeHtmlUtils.fromSafeConstant(""));
			quoteItemGrid.setColumnWidth(downColumn, 30, Unit.PX);
			downColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		}

		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteItem)bmObject).getQuantity().toString();
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
					return ((BmoQuoteItem)bmObject).getQuantity().toString();
				}
			};
		}
		quoteItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		quoteItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMNAME)) {

			nameColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						if (((BmoQuoteItem)bmObject).getProductId().toInteger() > 0)
							return ((BmoQuoteItem)bmObject).getBmoProduct().getName().toString();
						else
							return ((BmoQuoteItem)bmObject).getName().toString();
					} else
						return ((BmoQuoteItem)bmObject).getName().toString();
				}
			};
			nameColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeName(bmObject, value);
				}
			});
		} else {
			nameColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
						if (((BmoQuoteItem)bmObject).getProductId().toInteger() > 0)
							return ((BmoQuoteItem)bmObject).getBmoProduct().getName().toString();
						else
							return ((BmoQuoteItem)bmObject).getName().toString();
					} else
						return ((BmoQuoteItem)bmObject).getName().toString();
				}
			};
		}
		quoteItemGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		quoteItemGrid.setColumnWidth(nameColumn, 100, Unit.PX);
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Columna descripcion
			Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoQuoteItem)bmObject).getDescription().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			descriptionColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					descriptionDialog((BmoQuoteItem)bmObject);
				}
			});
			quoteItemGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Desc."));
			quoteItemGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);
			descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Tipo producto
			Column<BmObject, String> typeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					BmoQuoteItem bmoQuoteItem = (BmoQuoteItem)bmObject;
					if (bmoQuoteItem.getProductId().toInteger() > 0) {
						return bmoQuoteItem.getBmoProduct().getType().getSelectedOption().getLabel();	
					} else return "Extra";

				}
			};
			quoteItemGrid.addColumn(typeColumn, SafeHtmlUtils.fromSafeConstant("Tipo"));
			quoteItemGrid.setColumnWidth(typeColumn, 75, Unit.PX);

			// Columna de Modelo
			Column<BmObject, String> modelColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteItem)bmObject).getBmoProduct().getModel().toString();
				}
			};
			modelColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			quoteItemGrid.addColumn(modelColumn, SafeHtmlUtils.fromSafeConstant("Modelo"));
			quoteItemGrid.setColumnWidth(modelColumn, 100, Unit.PX);

			// Columna Unidad de Producto
			Column<BmObject, String> unitColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteItem)bmObject).getBmoProduct().getBmoUnit().getCode().toString();
				}
			};
			unitColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			quoteItemGrid.addColumn(unitColumn, SafeHtmlUtils.fromSafeConstant("Unidad"));
			quoteItemGrid.setColumnWidth(unitColumn, 50, Unit.PX);

			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				// Partida Presp.
				Column<BmObject, String> budgetItemColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {				
						return ((BmoQuoteItem)bmObject).getBmoBudgetItem().getBmoBudgetItemType().getName().toString();
					}
				};

				quoteItemGrid.addColumn(budgetItemColumn, SafeHtmlUtils.fromSafeConstant("Part. Presp."));
				budgetItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				quoteItemGrid.setColumnWidth(budgetItemColumn, 150, Unit.PX);

				// Departamento
				Column<BmObject, String> areaColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {				
						return ((BmoQuoteItem)bmObject).getBmoArea().getName().toString();
					}
				};
				quoteItemGrid.addColumn(areaColumn, SafeHtmlUtils.fromSafeConstant("Dpto."));
				areaColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				quoteItemGrid.setColumnWidth(areaColumn, 150, Unit.PX);
			}

			// Si es de tipo renta mostrar días
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				// Días
				Column<BmObject, String> daysColumn;
				if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
					daysColumn = new Column<BmObject, String>(new EditTextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoQuoteItem)bmObject).getDays().toString();
						}
					};
					daysColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
						@Override
						public void update(int index, BmObject bmObject, String value) {
							changeDays(bmObject, value);
						}
					});
				} else {
					daysColumn = new Column<BmObject, String>(new TextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoQuoteItem)bmObject).getDays().toString();
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
				quoteItemGrid.addColumn(daysColumn, daysHeader);
				quoteItemGrid.setColumnWidth(daysColumn, 50, Unit.PX);
				quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}

			if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission())) {
				// Columna de Comision
				Column<BmObject, String> commisionColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						if (((BmoQuoteItem)bmObject).getCommission().toBoolean())
							return "Si";
						else
							return "No";
					}
				};
				commisionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				quoteItemGrid.addColumn(commisionColumn, SafeHtmlUtils.fromSafeConstant("Comisión"));
				quoteItemGrid.setColumnWidth(commisionColumn, 50, Unit.PX);
			}
		}

		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoQuoteItem)bmObject).getPrice().toString();
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
					return ((BmoQuoteItem)bmObject).getPrice().toString();
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
		quoteItemGrid.addColumn(priceColumn, priceHeader);
		quoteItemGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Columna de Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoQuoteItem)bmObject).getAmount().toDouble());
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
		quoteItemGrid.addColumn(totalColumn, totalHeader);
		quoteItemGrid.setColumnWidth(totalColumn, 50, Unit.PX);
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
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
		quoteItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		quoteItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
	}

	public void addProduct(){
		addProduct(new BmoProduct());
	}

	public void addProduct(BmoProduct bmoProduct) {
		quoteItemDialogBox = new DialogBox(true);
		quoteItemDialogBox.setGlassEnabled(true);
		quoteItemDialogBox.setText("Item de Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		quoteItemDialogBox.setWidget(vp);

		UiQuoteItemForm quoteItemForm = new UiQuoteItemForm(getUiParams(), vp, quoteGroupId, bmoProduct);

		quoteItemForm.show();

		quoteItemDialogBox.center();
		quoteItemDialogBox.show();
	}

	public void saveQuoteGroup(){
		try {
			bmoQuoteGroup.setId(quoteGroupId);
			bmoQuoteGroup.getCode().setValue(codeTextBox.getText());
			bmoQuoteGroup.getName().setValue(nameTextBox.getText());
			bmoQuoteGroup.getShowQuantity().setValue(showQuantityCheckBox.getValue());
			bmoQuoteGroup.getShowPrice().setValue(showPriceCheckBox.getValue());
			bmoQuoteGroup.getShowAmount().setValue(showAmountCheckBox.getValue());
			bmoQuoteGroup.getShowProductImage().setValue(showProductImageCheckBox.getValue());
			bmoQuoteGroup.getShowGroupImage().setValue(showGroupImageCheckBox.getValue());	
			bmoQuoteGroup.getImage().setValue(imageFileUpload.getBlobKey());
			bmoQuoteGroup.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());

			if (bmoQuoteGroup.getIsKit().toBoolean()
					&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
				bmoQuoteGroup.getAmount().setValue(amountTextBox.getText());
			else 
				bmoQuoteGroup.getAmount().setValue(amountLabel.getText());

			if (bmoQuoteGroup.getIsKit().toBoolean())
				bmoQuoteGroup.getShowItems().setValue(showItmesCheckBox.getValue());
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
					getUiParams().getBmObjectServiceAsync().get(bmoQuoteGroup.getPmClass(), quoteGroupId, callback);
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
				getUiParams().getBmObjectServiceAsync().save(bmoQuoteGroup.getPmClass(), bmoQuoteGroup, callback);
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
			quoteGroupTitle.setText(bmoQuoteGroup.getName().toString());
			quoteUpdater.changeQuote();
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
				getUiParams().getBmObjectServiceAsync().delete(bmoQuoteGroup.getPmClass(), bmoQuoteGroup, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-delete() ERROR: " + e.toString());
		}
	}

	public void processDeleteResult(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processDeleteResult() ERROR: " + bmUpdateResult.errorsToString());
		else {
			quoteUpdater.changeQuote();
			quoteUpdater.updateQuoteGroups();
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
					quoteUpdater.updateQuoteGroups();
				}

			}
		};

		try {	
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoQuoteGroup.getPmClass(), bmoQuoteGroup, BmoQuoteGroup.ACTION_CHANGEINDEX, direction, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-changeGroupIndex() ERROR: " + e.toString());
		}
	} 

	public void changeName(BmObject bmObject, String name) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			bmoQuoteItem = (BmoQuoteItem)bmObject;
			try {
				if (name.length() > 0) {
					bmoQuoteItem.getName().setValue(name);
					saveItem();
				} else {
					// Eliminar registro
					deleteItem();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeName() ERROR: " + e.toString());
			}
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}
	
	// Cambiar orden del item
	public void changeItemIndex(BmoQuoteItem bmoQuoteItem, String direction) {

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
					// Actualiza lista de grupos de cotizaciones
					reset();
				}

			}
		};

		try {	
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoQuoteItem.getPmClass(), bmoQuoteItem, BmoQuoteItem.ACTION_CHANGEINDEX, direction, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-changeItemIndex() ERROR: " + e.toString());
		}
	} 

	public void changeItemDialog(BmoQuoteItem bmoQuoteItem) {
		this.bmoQuoteItem = bmoQuoteItem;

		if (bmoQuoteItem.getProductId().toInteger() > 0) {
			changeItemDialogBox = new DialogBox(true);
			changeItemDialogBox.setGlassEnabled(true);
			changeItemDialogBox.setText("Cambio de Item");
			changeItemDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			changeItemDialogBox.setWidget(vp);

			UiFormFlexTable formChangeItemTable = new UiFormFlexTable(getUiParams());
			BmoQuoteItem bmoChangeQuoteItem = new BmoQuoteItem();
			formChangeItemTable.addField(1, 0, changeItemSuggestBox, bmoChangeQuoteItem.getProductId());

			HorizontalPanel changeItemButtonPanel = new HorizontalPanel();
			changeItemButtonPanel.add(changeItemButton);
			changeItemButtonPanel.add(closeItemButton);

			vp.add(formChangeItemTable);
			vp.add(changeItemButtonPanel);

			changeItemDialogBox.center();
			changeItemDialogBox.show();
		}
	}

	public void descriptionDialog(BmoQuoteItem bmoQuoteItem) {
		this.bmoQuoteItem = bmoQuoteItem;
		descriptionDialogBox = new DialogBox(true);
		descriptionDialogBox.setGlassEnabled(true);
		descriptionDialogBox.setText("Descripción Item");
		descriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		descriptionDialogBox.setWidget(vp);

		descriptionTextArea = new TextArea();
		descriptionTextArea.setSize("320px", "190px");
		descriptionTextArea.setText(bmoQuoteItem.getDescription().toString());
		vp.add(descriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) 
			descriptionButtonPanel.add(changeDescriptionButton);
		descriptionButtonPanel.add(closeDescriptionButton);
		vp.add(descriptionButtonPanel);

		descriptionDialogBox.center();
		descriptionDialogBox.show();
	}

	public void changeDescription() {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			String description = descriptionTextArea.getText();
			try {
				bmoQuoteItem.getDescription().setValue(description);
				saveItem();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeDescription() ERROR: " + e.toString());
			}
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			bmoQuoteItem = (BmoQuoteItem)bmObject;
			try {
				double q = Double.parseDouble(quantity);
				if (q > 0) {
					bmoQuoteItem.getQuantity().setValue(quantity);
					saveItem();
				} else {
					showSystemMessage("La Cantidad no puede ser menor a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity() ERROR: " + e.toString());
			}
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}

	public void changeDays(BmObject bmObject, String days) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			bmoQuoteItem = (BmoQuoteItem)bmObject;
			try {
				double d = Double.parseDouble(days);
				if (d > 0) {
					bmoQuoteItem.getDays().setValue(days);
					saveItem();
				} else {
					showSystemMessage("Los Días no pueden ser menores a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeDays() ERROR: " + e.toString());
			}
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}

	public void changePrice(BmObject bmObject, String price) {
		if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
			bmoQuoteItem = (BmoQuoteItem)bmObject;
			try {
				Double p = Double.parseDouble(price);

				if (bmoQuoteItem.getProductId().toInteger() > 0) {
					// Es de tipo Producto
					if (p == 0) {
						bmoQuoteItem.getPrice().setValue(0);
						saveItem();
					} else if (p > 0) {

						if (p >= bmoQuoteItem.getBasePrice().toDouble()) {
							// Se esta subiendo precio, permitirlo
							bmoQuoteItem.getPrice().setValue(p);
							saveItem();
							reset();

						} else if (p < bmoQuoteItem.getBasePrice().toDouble()) {
							// El precio es menor al precio base, validar permisos
							if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
								bmoQuoteItem.getPrice().setValue(p);
								saveItem();
								reset();
							} else {
								numberFormat = NumberFormat.getCurrencyFormat();
								String formatted = numberFormat.format(bmoQuoteItem.getBasePrice().toDouble());
								showSystemMessage("No cuenta con Permisos para establecer Precio menor a: " + formatted);
								reset();
							}
						}

					} else {
						if  (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE)) {
							bmoQuoteItem.getPrice().setValue(p);
							saveItem();							
						} else {
							showSystemMessage("El Precio no puede ser menor a $0.00");							
						}	
						reset();
					}
				} else {
					// No es de tipo producto, permite todo
					bmoQuoteItem.getPrice().setValue(p);
					saveItem();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}

	public void fixProductClass(BmoQuoteItem bmoQuoteItem) {
		quoteItemDialogBox = new DialogBox(true);
		quoteItemDialogBox.setGlassEnabled(true);
		quoteItemDialogBox.setText("Cambiar Producto tipo Clase");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		quoteItemDialogBox.setWidget(vp);

		UiQuoteItemProductClassForm uiQuoteItemProductClassForm = new UiQuoteItemProductClassForm(getUiParams(), vp, quoteGroupId, bmoQuoteItem);
		uiQuoteItemProductClassForm.show();

		quoteItemDialogBox.center();
		quoteItemDialogBox.show();
	}

	public void processItemSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) {
			showErrorMessage(this.getClass().getName() + "-processItemChangeSave() ERROR: " + bmUpdateResult.errorsToString());
			this.reset();
		} else {
			quoteUpdater.changeQuote();
			this.reset();
		}
	}

	public void processItemDelete(BmUpdateResult result) {
		if (result.hasErrors()) showErrorMessage(this.getClass().getName() + "-processItemDelete() ERROR: " + result.errorsToString());
		else {
			quoteUpdater.changeQuote();
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteItem.getPmClass(), bmoQuoteItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange() ERROR: " + e.toString());
			}
		}
	}

	public void deleteItem(BmObject bmObject){
		bmoQuoteItem = (BmoQuoteItem)bmObject;
		deleteItem();
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
				getUiParams().getBmObjectServiceAsync().delete(bmoQuoteItem.getPmClass(), bmoQuoteItem, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem() ERROR: " + e.toString());
		}
	}

	public void reset(){
		get();
		data.list();
		quoteItemGrid.redraw();
	}

	// Agrega un item de un producto a la orden de compra
	private class UiQuoteItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox daysTextBox = new TextBox();
		private TextBox nameTextBox = new TextBox();
		private TextArea descriptionTextArea = new TextArea();
		private CheckBox commissionCheckBox = new CheckBox();
		private TextBox priceTextBox = new TextBox();
		private BmoQuoteItem bmoQuoteItem;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		private UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		int quoteGroupId;
		String productId = "";
		private int stockQuantityRpcAttempt = 0;
		private int lockedQuantityRpcAttempt = 0;
		private int priceRpcAttempt = 0;
		private int productCompanySpecificRpcAttempt = 0;

		public UiQuoteItemForm(UiParams uiParams, Panel defaultPanel, int quoteGroupId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoQuoteItem = new BmoQuoteItem();
			this.quoteGroupId = quoteGroupId;

			try {
				bmoQuoteItem.getQuoteGroupId().setValue(quoteGroupId);
				bmoQuoteItem.getProductId().setValue(bmoProduct.getId());
				bmoQuoteItem.getName().setValue("item");
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
			if (getSFParams().hasWrite(bmoQuote.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);


			//filtro para mostrar los productos que estan habilitados			
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoQuoteItem.getBmoProduct().getKind(), bmoQuoteItem.getBmoProduct().getEnabled(), "1");
			productSuggestBox.addFilter(filterByEnabled);

			BmFilter filterByNotSubProduct = new BmFilter();
			filterByNotSubProduct.setValueOperatorFilter(bmoQuoteItem.getBmoProduct().getKind(), bmoQuoteItem.getBmoProduct().getType(), BmFilter.NOTEQUALS , "" + BmoProduct.TYPE_SUBPRODUCT);
			productSuggestBox.addFilter(filterByNotSubProduct);
			// Filtros para pedido de Renta(Drea)
			if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				BmFilter productTypeProductFilter = new BmFilter();
				productTypeProductFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS, ""+BmoProduct.TYPE_COMPLEMENTARY);
				productSuggestBox.addFilter(productTypeProductFilter);

				BmFilter productConsumableFilter = new BmFilter();
				productConsumableFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getConsumable(), BmFilter.NOTEQUALS, 1);
				productSuggestBox.addFilter(productConsumableFilter);
			}

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				try {
					if (bmoQuote.getBudgetItemId().toInteger() > 0)
						bmoQuoteItem.getBudgetItemId().setValue(bmoQuote.getBudgetItemId().toInteger());
					if (bmoQuote.getAreaId().toInteger() > 0)
						bmoQuoteItem.getAreaId().setValue(bmoQuote.getAreaId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
				}
			}

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, productSuggestBox, bmoQuoteItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacén", stockQuantity);
			formTable.addLabelField(3, 0, "En Cotización", lockedQuantity);
			formTable.addField(4, 0, nameTextBox, bmoQuoteItem.getName());
			formTable.addField(5, 0, descriptionTextArea, bmoQuoteItem.getDescription());
			formTable.addField(6, 0, quantityTextBox, bmoQuoteItem.getQuantity());
			// Si es de renta, mostrar días
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) 
				formTable.addField(7, 0, daysTextBox, bmoQuoteItem.getDays());

			if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission()))
				formTable.addField(8, 0, commissionCheckBox, bmoQuoteItem.getCommission());
			formTable.addField(9, 0, priceTextBox, bmoQuoteItem.getPrice());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
				setBudgetItemsListBoxFilters(bmoQuote.getCompanyId().toInteger());
				formTable.addField(10, 0, budgetItemUiListBox, bmoQuoteItem.getBudgetItemId());
				formTable.addField(11, 0, areaUiListBox, bmoQuoteItem.getAreaId());
			}

			formTable.addButtonPanel(buttonPanel);

			//statusEffect();
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {

			if (uiSuggestBox == productSuggestBox) {
				if (productSuggestBox.getSelectedId() > 0) {
					if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission())) {
						//Si el producto maneja comisión marcar el check
						BmoProduct bmoProduct = (BmoProduct)productSuggestBox.getSelectedBmObject();
						if (bmoProduct.getCommision().toBoolean()) 
							commissionCheckBox.setValue(true);
						else 
							commissionCheckBox.setValue(false);
					}
				}
				statusEffect();
			}
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
				priceTextBox.setText("0");
				priceTextBox.setEnabled(true);
				stockQuantity.setText("");
				lockedQuantity.setText("");
				if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission())) 
					commissionCheckBox.setValue(false);
				BmoProductCompany bmoProductCompany = new BmoProductCompany();
				setBudgetItemAndArea(bmoProductCompany, true);
			}

			if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_NOPRODUCTITEM)) {
				nameTextBox.setText("");
				nameTextBox.setEnabled(false);
				priceTextBox.setText("");
				priceTextBox.setEnabled(false);
			}

			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE))
				priceTextBox.setEnabled(true);

			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMNAME)) 
				nameTextBox.setEnabled(true);
		}
		
//		public String getFormattedRoundDouble(double value, int decimalCount) {
//		    StringBuilder numberPattern = new StringBuilder(
//		            (decimalCount <= 0) ? "" : ".");
//		    for (int i = 0; i < decimalCount; i++) {
//		        numberPattern.append('0');
//		    }
//		    if (decimalCount == 0.0) 
//		    	return "0";
//		    else
//		    	return NumberFormat.getFormat(numberPattern.toString()).format(value);
//		}

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
						stockQuantity.setText("" + bmoWhStock.getQuantity().toDouble());
						getLockedQuantity();
					}
				};

				try {	
					if (!isLoading()) {
						stockQuantity.setText("");
						BmoWhStock bmoWhStock = new BmoWhStock();
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY,  productId + "|" + bmoQuote.getCompanyId().toInteger(), callback);
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

		//Obtener la cantidad en cotizacion
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
						BmoQuoteItem bmoQuoteItem = (BmoQuoteItem)result.getBmObject();
						lockedQuantity.setText("" + bmoQuoteItem.getQuantity().toDouble());		
						getPrice();
					}
				};

				try {	
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoQuote.getPmClass(), bmoQuote, BmoQuote.ACTION_LOCKEDQUANTITY,  productId, callback);
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

		// Obtener precio de venta/renta del producto
		public void getPrice(int priceRpcAttempt) {
			if (priceRpcAttempt < 5) {
				setPriceRpcAttempt(priceRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getPriceRpcAttempt() < 5)
							getPrice(getPriceRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getPrice() ERROR: " + caught.toString());
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
						bmoProductPrice.getStartDate().setValue(bmoQuote.getStartDate().toString());
						bmoProductPrice.getProductId().setValue(productSuggestBox.getSelectedId());
						bmoProductPrice.getCurrencyId().setValue(bmoQuote.getCurrencyId().toInteger());
						bmoProductPrice.getOrderTypeId().setValue(bmoQuote.getOrderTypeId().toInteger());
						bmoProductPrice.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toInteger());
						bmoProductPrice.getMarketId().setValue(bmoQuote.getMarketId().toInteger());
						bmoProductPrice.getCompanyId().setValue(bmoQuote.getCompanyId().toInteger());

						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoProductPrice.getPmClass(), bmoProductPrice, BmoProductPrice.ACTION_GETPRICE,  productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getPrice() ERROR: " + e.toString());
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
						bmoProductCompany.getCompanyId().setValue(bmoQuote.getCompanyId().toString());

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
		private void setBudgetItemAndArea(BmoProductCompany bmoProductCompany, boolean defaultQuote) {

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				// Colocar partida y dpto de la cotizacion
				if (defaultQuote) {
					if (bmoQuote.getBudgetItemId().toInteger() > 0)
						budgetItemUiListBox.setSelectedId("" + bmoQuote.getBudgetItemId().toInteger());
					if (bmoQuote.getAreaId().toInteger() > 0)
						areaUiListBox.setSelectedId("" + bmoQuote.getAreaId().toInteger());
				} else {
					// Colocar partida/dpto. de la empresa del producto
					if (bmoProductCompany.getId() > 0) {
						if (bmoProductCompany.getBudgetItemId().toInteger() > 0)
							budgetItemUiListBox.setSelectedId("" + bmoProductCompany.getBudgetItemId().toInteger());
						if (bmoProductCompany.getAreaId().toInteger() > 0)
							areaUiListBox.setSelectedId("" + bmoProductCompany.getAreaId().toInteger());
					} 
					// Colocar partida/dpto. del producto(no tiene empresas), si no tiene datos regresar por defecto al de la cotizacion
					else {
						BmoProduct bmoProduct = new BmoProduct();
						bmoProduct = ((BmoProduct)productSuggestBox.getSelectedBmObject());
						if (bmoProduct.getBudgetItemId().toInteger() > 0) 
							budgetItemUiListBox.setSelectedId("" + bmoProduct.getBudgetItemId().toInteger());
						else {
							if (bmoQuote.getBudgetItemId().toInteger() > 0) 
								budgetItemUiListBox.setSelectedId("" + bmoQuote.getBudgetItemId().toInteger());
						}
						if (bmoProduct.getAreaId().toInteger() > 0) 
							areaUiListBox.setSelectedId("" + bmoProduct.getAreaId().toInteger());
						else {
							if (bmoQuote.getAreaId().toInteger() > 0) 
								areaUiListBox.setSelectedId("" + bmoQuote.getAreaId().toInteger());
						}
					}
				}
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				quoteItemDialogBox.hide();
				quoteUpdater.changeQuote();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteItem = new BmoQuoteItem();
				bmoQuoteItem.getQuoteGroupId().setValue(quoteGroupId);
				bmoQuoteItem.getProductId().setValue(productSuggestBox.getSelectedId());
				bmoQuoteItem.getName().setValue(nameTextBox.getText());
				bmoQuoteItem.getDescription().setValue(descriptionTextArea.getText());
				bmoQuoteItem.getQuantity().setValue(quantityTextBox.getText());
				bmoQuoteItem.getDays().setValue(daysTextBox.getText());
				bmoQuoteItem.getPrice().setValue(priceTextBox.getText());
				bmoQuoteItem.getCommission().setValue(commissionCheckBox.getValue());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
					bmoQuoteItem.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
					bmoQuoteItem.getAreaId().setValue(areaUiListBox.getSelectedId());
				}
				// Si no tiene permisos para agregar items sin producto, no permite avanzar
				if (!(bmoQuoteItem.getProductId().toInteger() > 0)
						&& !getSFParams().hasSpecialAccess(BmoQuote.ACCESS_NOPRODUCTITEM))
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteItem.getPmClass(), bmoQuoteItem, callback);
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
	private class UiQuoteItemProductClassForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox daysTextBox = new TextBox();
		private BmoQuoteItem bmoQuoteItem;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiListBox productListBox;
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		String productId = "";
		int selectedQuoteGroupId;
		private int stockQuantityRpcAttempt = 0;
		private int lockedQuantityRpcAttempt = 0;

		public UiQuoteItemProductClassForm(UiParams uiParams, Panel defaultPanel, int selectedQuoteGroupId, BmoQuoteItem bmoQuoteItem) {
			super(uiParams, defaultPanel);
			this.bmoQuoteItem = bmoQuoteItem;
			this.productId = bmoQuoteItem.getProductId().toString();
			this.selectedQuoteGroupId = selectedQuoteGroupId;

			try {
				bmoQuoteItem.getQuoteGroupId().setValue(selectedQuoteGroupId);
				bmoQuoteItem.getProductId().setValue(productId);
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
			if (getSFParams().hasWrite(bmoQuote.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			// Filtros del combo de productos de clase
			BmFilter filterByProductGroup = new BmFilter();
			filterByProductGroup.setValueFilter(bmoQuoteItem.getBmoProduct().getKind(), 
					bmoQuoteItem.getBmoProduct().getProductGroupId(),
					bmoQuoteItem.getBmoProduct().getProductGroupId().toInteger());

			BmFilter filterByType = new BmFilter();
			filterByType.setValueFilter(bmoQuoteItem.getBmoProduct().getKind(), 
					bmoQuoteItem.getBmoProduct().getType(), "" + BmoProduct.TYPE_PRODUCT);

			productListBox = new UiListBox(getUiParams(), new BmoProduct());
			productListBox.addFilter(filterByProductGroup);
			productListBox.addFilter(filterByType);

			defaultPanel.add(formTable);
		}

		public void show(){
			formTable.addField(1, 0, productListBox, bmoQuoteItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacen", stockQuantity);
			formTable.addLabelField(3, 0, "En Cotización", lockedQuantity);
			// Si es de renta, mostrar días
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) 
				formTable.addField(4, 0, quantityTextBox, bmoQuoteItem.getQuantity());
			formTable.addField(5, 0, daysTextBox, bmoQuoteItem.getDays());

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
		
//		public String getFormattedRoundDouble(double value, int decimalCount) {
//		    StringBuilder numberPattern = new StringBuilder(
//		            (decimalCount <= 0) ? "" : ".");
//		    for (int i = 0; i < decimalCount; i++) {	
//		        numberPattern.append('0');
//		    }
//		    if (decimalCount == 0.0) 
//		    	return "0";
//		    else
//		    	return NumberFormat.getFormat(numberPattern.toString()).format(value);
//		}

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
						stockQuantity.setText("" + bmoWhStock.getQuantity().toDouble());
						getLockedQuantity();
					}
				};

				try {
					stockQuantity.setText("");
					BmoWhStock bmoWhStock = new BmoWhStock();
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY, productId + "|" + bmoQuote.getCompanyId().toInteger(), callback);
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
						BmoQuoteItem bmoQuoteItem = (BmoQuoteItem)result.getBmObject();
						lockedQuantity.setText("" + bmoQuoteItem.getQuantity().toDouble());		
					}
				};

				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoQuote.getPmClass(), bmoQuote, BmoQuote.ACTION_LOCKEDQUANTITY,  productId, callback);
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
				quoteItemDialogBox.hide();
				quoteUpdater.changeQuote();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteItem.getQuoteGroupId().setValue(selectedQuoteGroupId);
				bmoQuoteItem.getProductId().setValue(productListBox.getSelectedId());
				bmoQuoteItem.getQuantity().setValue(quantityTextBox.getText());
				bmoQuoteItem.getDays().setValue(daysTextBox.getText());
				bmoQuoteItem.getPrice().setValue(0);
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteItem.getPmClass(), bmoQuoteItem, callback);
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

	public int getSaveItemRpcAttempt() {
		return saveItemRpcAttempt;
	}

	public void setSaveItemRpcAttempt(int saveItemRpcAttempt) {
		this.saveItemRpcAttempt = saveItemRpcAttempt;
	}
}
