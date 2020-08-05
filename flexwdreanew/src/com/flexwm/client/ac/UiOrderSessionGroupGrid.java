package com.flexwm.client.ac;


import java.util.ArrayList;

import com.flexwm.client.ac.UiOrderFormSession.OrderSessionUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
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
import com.google.gwt.user.client.ui.CheckBox;


public class UiOrderSessionGroupGrid extends Ui {
	private CellTable<BmObject> orderItemGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> data;
	private BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
	private BmoOrderItem bmoOrderItem = new BmoOrderItem();
	private TextArea descriptionTextArea = new TextArea();
	private int orderGroupId;
	private BmFilter bmFilter;

	private FlowPanel orderGroupPanel = new FlowPanel();
	private FlowPanel orderItemPanel = new FlowPanel();
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private Button addOrderItemButton = new Button("+ ITEM");

	DialogBox descriptionDialogBox;
	Button changeDescriptionButton = new Button("GUARDAR");
	Button closeDescriptionButton = new Button("CERRAR");

	// Cambio de Item
	UiSuggestBox changeItemSuggestBox;
	DialogBox changeItemDialogBox;
	Button changeItemButton = new Button("MODIFICAR");
	Button closeItemButton = new Button("CERRAR");

	private Label orderGroupTitle = new Label();
	private TextBox nameTextBox = new TextBox();
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	private CheckBox showAmountCheckBox = new CheckBox();
	private CheckBox showProductImageCheckBox = new CheckBox();
	private CheckBox showGroupImageCheckBox = new CheckBox();
	private CheckBox createRaccountCheckBox = new CheckBox();
	private UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
	private Label amountLabel = new Label("");
	private TextBox amountTextBox = new TextBox();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();

	protected DialogBox orderItemDialogBox;

	private BmoOrder bmoOrder;
	private OrderSessionUpdater orderSessionUpdater;

	public UiOrderSessionGroupGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, int orderGroupId, OrderSessionUpdater orderSessionUpdater) {
		super(uiParams, defaultPanel);
		this.orderGroupId = orderGroupId;
		this.bmoOrder = bmoOrder;
		bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId().getName(), orderGroupId);
		data = new UiListDataProvider<BmObject>(bmoOrderItem, bmFilter);
		this.orderSessionUpdater = orderSessionUpdater;

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
		filterByType.setValueFilter(bmoOrderItem.getBmoProduct().getKind(), 
				bmoOrderItem.getBmoProduct().getType(), "" + BmoProduct.TYPE_PRODUCT);
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
		orderItemGrid.setWidth("100%");
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

		// Acomodar paneles
		orderGroupPanel.add(formFlexTable);
		orderItemPanel.add(orderItemGrid);
		orderGroupPanel.addStyleName("separator");
		defaultPanel.add(orderGroupPanel);
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
			formFlexTable.addSectionLabel(1, 0, "Kit de Items ", 4);
		else
			formFlexTable.addSectionLabel(1, 0, "Grupo de Items: " + bmoOrderGroup.getName().toString(), 4);

		// Si es kit y permite modificar monto
		if (bmoOrderGroup.getIsKit().toBoolean()
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
			formFlexTable.addField(5, 0, amountTextBox, bmoOrderGroup.getAmount());
		else
			formFlexTable.addLabelField(5, 0, amountLabel, bmoOrderGroup.getAmount());
		formFlexTable.addPanel(6, 0, orderItemPanel, 2);
		formFlexTable.addPanel(7, 0, buttonPanel, 2);

		numberFormat = NumberFormat.getCurrencyFormat();
		String formatted = numberFormat.format(bmoOrderGroup.getAmount().toDouble());

		if (bmoOrderGroup.getIsKit().toBoolean()
				&& getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEKITPRICE))
			amountTextBox.setText(formatted);
		else 
			amountLabel.setText(formatted);

		orderGroupTitle.setText(bmoOrderGroup.getName().toString());

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
		amountTextBox.setEnabled(false);

		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			nameTextBox.setEnabled(true);
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);
			showAmountCheckBox.setEnabled(true);
			showProductImageCheckBox.setEnabled(true);
			showGroupImageCheckBox.setEnabled(true);
			createRaccountCheckBox.setEnabled(true);
			addOrderItemButton.setVisible(true);

			if (bmoOrderGroup.getIsKit().toBoolean()
					&& getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEKITPRICE)) {
				amountTextBox.setEnabled(true);
			}
		}
	}

	public void changeHeight() {
		orderItemGrid.setPageSize(data.getList().size());
		orderItemGrid.setVisibleRange(0, data.getList().size());
	}

	// Columnas
	public void setColumns() {
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
					changeQuantity(bmObject, value);
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
					changeName(bmObject, value);
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
					changeItemDialog((BmoOrderItem)bmObject);
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
							changeDays(bmObject, value);
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
				orderItemGrid.setColumnWidth(commisionColumn, 100, Unit.PX);
			}
		}

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
					changePrice(bmObject, value);
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
								Window.open(
										GwtUtil.getProperUrl(getUiParams().getSFParams(), "/rpt/orde_product_availability.jsp?orde_orderid=" + bmoOrderItem.getBmoOrderGroup().getOrderId().toString() + "&prod_productid=" + bmoOrderItem.getProductId().toString()), "_blank", "");
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
		orderItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
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

			if (bmoOrderGroup.getIsKit().toBoolean())
				bmoOrderGroup.getAmount().setValue(amountTextBox.getText());

			save();

		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-formValueChange() ERROR: " + e.toString());
		}
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
				getUiParams().getBmObjectServiceAsync().get(bmoOrderGroup.getPmClass(), orderGroupId, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-get() ERROR: " + e.toString());
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
			orderSessionUpdater.changeOrder();
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
			orderSessionUpdater.changeOrder();
			orderSessionUpdater.updateOrderGroups();
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
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				orderSessionUpdater.changeOrder();
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
		if (bmUpdateResult.hasErrors()) 
			showErrorMessage(this.getClass().getName() + "-processItemChangeSave() ERROR: " + bmUpdateResult.errorsToString());
		else {
			orderSessionUpdater.changeOrder();
			this.reset();
		}
	}

	public void processItemDelete(BmUpdateResult result) {
		if (result.hasErrors()) showErrorMessage(this.getClass().getName() + "-processItemDelete() ERROR: " + result.errorsToString());
		else {
			orderSessionUpdater.changeOrder();
			this.reset();
		}
	}

	public void saveItem() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
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

	public void deleteItem(BmObject bmObject) {
		bmoOrderItem = (BmoOrderItem)bmObject;
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
		private BmoOrderItem bmoOrderItem;
		private BmoOrder bmoOrder = new BmoOrder();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		int orderGroupId;
		String productId = "";


		public UiOrderItemForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, int orderGroupId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = new BmoOrderItem();
			this.bmoOrder = bmoOrder;
			this.orderGroupId = orderGroupId;

			try {
				bmoOrderItem.getOrderGroupId().setValue(orderGroupId);
				bmoOrderItem.getProductId().setValue(bmoProduct.getId());
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
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoOrderItem.getBmoProduct().getKind(), bmoOrderItem.getBmoProduct().getEnabled(), "1");
			filterList.add(filterByEnabled);

			productSuggestBox = new UiSuggestBox(new BmoProduct());
			productSuggestBox.addFilter(filterByEnabled);

			defaultPanel.add(formTable);
		}

		public void show() {
			formTable.addField(1, 0, productSuggestBox, bmoOrderItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacén", stockQuantity);
			formTable.addLabelField(3, 0, "En Pedidos", lockedQuantity);
			formTable.addField(4, 0, nameTextBox, bmoOrderItem.getName());
			formTable.addField(5, 0, descriptionTextArea, bmoOrderItem.getDescription());
			formTable.addField(6, 0, quantityTextBox, bmoOrderItem.getQuantity());
			// Mostrar los días si es de tipo renta
			if (bmoOrder.getBmoOrderType().getType().equals("" + BmoOrderType.TYPE_RENTAL))
				formTable.addField(7, 0, daysTextBox, bmoOrderItem.getDays());

			formTable.addField(8, 0, commissionCheckBox, bmoOrderItem.getCommission());
			formTable.addField(9, 0, priceTextBox, bmoOrderItem.getPrice());

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (productSuggestBox.getSelectedId() > 0) {
				//Si el producto maneja comisión marcar el check
				BmoProduct bmoProduct = (BmoProduct)productSuggestBox.getSelectedBmObject();
				try {
					bmoOrderItem.getCommission().setValue(bmoProduct.getCommision().toBoolean());
				} catch (BmException e) {
					showSystemMessage("Error al marcar la comision " + e.toString());					
				}
				formTable.addField(8, 0, commissionCheckBox, bmoOrderItem.getCommission());

			}
			statusEffect();
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
				nameTextBox.setText("");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				stockQuantity.setText("0");
				lockedQuantity.setText("0");
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

		//Obtener la cantidad en almacen
		public void getStockQuantity() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
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
					getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY,  productId + "|" + this.bmoOrder.getCompanyId().toInteger(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + e.toString());
			}
		} 

		//Obtener la cantidad en pedidos
		public void getLockedQuantity() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					BmoOrderItem bmoOrderItem = (BmoOrderItem)result.getBmObject();
					lockedQuantity.setText(bmoOrderItem.getQuantity().toString());	
					getRentalSalePrice();
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

		//Obtener precio de venta/renta del producto
		public void getRentalSalePrice() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					BmoProduct bmoProduct = (BmoProduct)result.getBmObject();
					nameTextBox.setText("" + bmoProduct.getName().toString());
					//if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
					//	priceTextBox.setText("" + bmoProduct.getRentalPrice().toDouble());
					//else 
					//	priceTextBox.setText("" + bmoProduct.getSalePrice().toDouble());
				}
			};

			try {	
				if (!isLoading()) {
					priceTextBox.setText("");
					BmoProduct bmoProduct = new BmoProduct();
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoProduct.getPmClass(), bmoProduct, BmoProduct.ACTION_GETPRODUCT,  productId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + e.toString());
			}
		} 

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				orderSessionUpdater.changeOrder();
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

		//Obtener la cantidad en almacen
		public void getStockQuantity() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					BmoWhStock bmoWhStock = (BmoWhStock)result.getBmObject();
					stockQuantity.setText(bmoWhStock.getQuantity().toString());
					getLockedQuantity();
				}
			};

			try {
				stockQuantity.setText("");
				BmoWhStock bmoWhStock = new BmoWhStock();
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY,  productId + "|" + this.bmoOrder.getCompanyId().toInteger(), callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + e.toString());
			}
		} 

		//Obtener la cantidad en almacen
		public void getLockedQuantity() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
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

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				orderSessionUpdater.changeOrder();
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
	}
}
