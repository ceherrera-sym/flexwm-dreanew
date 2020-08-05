package com.flexwm.client.op;


import java.util.ArrayList;
import java.util.Iterator;
import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderEquipment;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;


public class UiOrderEquipmentGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderEquipments
	private BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
	private FlowPanel orderEquipmentPanel = new FlowPanel();
	private CellTable<BmObject> orderEquipmentGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderEquipmentData;
	BmFilter orderEquipmentFilter;
	private TextArea orderEquipmentDescriptionTextArea = new TextArea();
	DialogBox orderEquipmentDescriptionDialogBox;
	Button changeEquipmentDescriptionButton = new Button("GUARDAR");
	Button closeEquipmentDescriptionButton = new Button("CERRAR");

	private Button addOrderEquipmentButton = new Button("+ RECURSO");
	protected DialogBox orderEquipmentDialogBox;	

	// Cambio de Equipment
	UiListBox changeEquipmentListBox;
	DialogBox changeEquipmentDialogBox;
	Button changeEquipmentButton = new Button("CAMBIAR");
	Button closeEquipmentButton = new Button("CERRAR");

	// Arbol
	public final MultiSelectionModel<BmObject> selectionOrderEquipmentModel = new MultiSelectionModel<BmObject>();
	private UiOrderEquipmentTreeModel uiOrderEquipmentTreeModel = new UiOrderEquipmentTreeModel(selectionOrderEquipmentModel);
	private Button treeShowButton = new Button("NAVEGADOR");
	private Button treeCloseButton = new Button("CERRAR");
	CellTree cellEquipmentTree;
	Label treeEquipmentLabel = new Label("Agregar Recurso: ");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoOrder bmoOrder;
	private OrderUpdater orderUpdater;
	private int saveEquipmentChangeRpcAttempt = 0;
	private int changeEquipmentActionRpcAttempt = 0;

	public UiOrderEquipmentGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderUpdater orderUpdater){
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		this.orderUpdater = orderUpdater;

		// Arbol de seleccion
		selectionOrderEquipmentModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Iterator<BmObject> i = selectionOrderEquipmentModel.getSelectedSet().iterator();
				BmObject bmObject;
				if (i.hasNext()) {
					bmObject = (BmObject)i.next();

					// Se esta agregando un producto directo
					if (bmObject instanceof BmoEquipment) {
						addEquipment((BmoEquipment)bmObject);
					}
				}
			}
		});
		cellEquipmentTree = new CellTree(uiOrderEquipmentTreeModel, selectionOrderEquipmentModel);
		cellEquipmentTree.setAnimationEnabled(true);
		cellEquipmentTree.addStyleName("orderProductTree");
		treeEquipmentLabel.setStyleName("detailLabelTitle");

		treeShowButton.setStyleName("formCloseButton");
		treeShowButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showTree();
			}
		});
		treeCloseButton.setStyleName("formCloseButton");
		treeCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hideTree();
			}
		});

		// Cambios en grid
		changeEquipmentButton.setStyleName("formCloseButton");
		changeEquipmentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeEquipment();
				changeEquipmentDialogBox.hide();
			}
		});

		closeEquipmentButton.setStyleName("formCloseButton");
		closeEquipmentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeEquipmentDialogBox.hide();
			}
		});

		// Boton de cerrar dialogo de personal
		changeEquipmentDescriptionButton.setStyleName("formCloseButton");
		changeEquipmentDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeOrderEquipmentDescription();
				orderEquipmentDescriptionDialogBox.hide();
			}
		});

		closeEquipmentDescriptionButton.setStyleName("formCloseButton");
		closeEquipmentDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				orderEquipmentDescriptionDialogBox.hide();
			}
		});

		// Elementos del cambio de staff
		changeEquipmentListBox = new UiListBox(getUiParams(), new BmoEquipment());

		// Inicializar grid de Personal
		orderEquipmentGrid = new CellTable<BmObject>();
		orderEquipmentGrid.setWidth("100%");
		orderEquipmentPanel.clear();
		orderEquipmentPanel.setWidth("100%");
		setOrderEquipmentColumns();
		orderEquipmentFilter = new BmFilter();
		orderEquipmentFilter.setValueFilter(bmoOrderEquipment.getKind(), bmoOrderEquipment.getOrderId().getName(), bmoOrder.getId());
		orderEquipmentGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderEquipmentData = new UiListDataProvider<BmObject>(new BmoOrderEquipment(), orderEquipmentFilter);
		orderEquipmentData.addDataDisplay(orderEquipmentGrid);
		orderEquipmentPanel.add(orderEquipmentGrid);

		// Panel de botones
		addOrderEquipmentButton.setStyleName("formSaveButton");
		addOrderEquipmentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addEquipment();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addOrderEquipmentButton);
		buttonPanel.add(treeShowButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(3, 0, orderEquipmentPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderEquipmentData.list();
		orderEquipmentGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED ||
				bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED ||
				bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED) {
			showQuantityCheckBox.setEnabled(false);
			showPriceCheckBox.setEnabled(false);
			addOrderEquipmentButton.setVisible(false);
			treeShowButton.setVisible(false);
		} else {
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);	
			addOrderEquipmentButton.setVisible(true);
			treeShowButton.setVisible(true);
		}
	}

	public void reset(){
		orderUpdater.changeOrderEquipment();
	}

	private void showTree() {
		getUiParams().getUiTemplate().showEastPanel();
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(treeEquipmentLabel);
		getUiParams().getUiTemplate().getEastPanel().add(cellEquipmentTree);
		getUiParams().getUiTemplate().getEastPanel().add(treeCloseButton);
	}

	private void hideTree() {
		getUiParams().getUiTemplate().hideEastPanel();
	}

	public void changeHeight() {
		orderEquipmentGrid.setVisibleRange(0, orderEquipmentData.getList().size());
	}

	public void addEquipment(){
		addEquipment(new BmoEquipment());
	}

	// Columnas
	public void setOrderEquipmentColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderEquipment)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeOrderEquipmentQuantity(bmObject, value);
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderEquipment)bmObject).getQuantity().toString();
				}
			};
		}
		orderEquipmentGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		orderEquipmentGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn;
		nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderEquipment)bmObject).getName().toString();
			}
		};
		orderEquipmentGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		orderEquipmentGrid.setColumnWidth(nameColumn, 100, Unit.PX);
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Si tiene acceso a cambiar recursos, mostrar boton
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION) &&
					getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEM)) {
				// Columna cambio de item
				Column<BmObject, String> changeEquipmentColumn = new Column<BmObject, String>(new ButtonCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						if (((BmoOrderEquipment)bmObject).getEquipmentId().toInteger() > 0)
							return "<>";
						else 
							return " ";
					}
				};
				changeEquipmentColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeEquipmentDialog((BmoOrderEquipment)bmObject);
					}
				});
				orderEquipmentGrid.addColumn(changeEquipmentColumn, SafeHtmlUtils.fromSafeConstant("Cambiar"));
				orderEquipmentGrid.setColumnWidth(changeEquipmentColumn, 50, Unit.PX);
				changeEquipmentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}

			// Columna descripcion
			Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoOrderEquipment)bmObject).getDescription().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			descriptionColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					orderEquipmentDescriptionDialog((BmoOrderEquipment)bmObject);
				}
			});
			orderEquipmentGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Desc."));
			orderEquipmentGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);
			descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Grupo
			Column<BmObject, String> equipmentColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderEquipment)bmObject).getBmoEquipment().getName().toString();
				}
			};
			equipmentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			orderEquipmentGrid.addColumn(equipmentColumn, SafeHtmlUtils.fromSafeConstant("Recurso"));
			orderEquipmentGrid.setColumnWidth(equipmentColumn, 100, Unit.PX);

			// Días
			Column<BmObject, String> daysColumn;
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
				daysColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderEquipment)bmObject).getDays().toString();
					}
				};
				daysColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeOrderEquipmentDays(bmObject, value);
					}
				});
			} else {
				daysColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderEquipment)bmObject).getDays().toString();
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
			orderEquipmentGrid.addColumn(daysColumn, daysHeader);
			orderEquipmentGrid.setColumnWidth(daysColumn, 50, Unit.PX);
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		}

		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderEquipment)bmObject).getPrice().toString();
				}
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeOrderEquipmentPrice(bmObject, value);
				}
			});
		} else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderEquipment)bmObject).getPrice().toString();
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
		orderEquipmentGrid.addColumn(priceColumn, priceHeader);
		orderEquipmentGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Columna de Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderEquipment)bmObject).getAmount().toDouble());
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
		orderEquipmentGrid.addColumn(totalColumn, totalHeader);
		orderEquipmentGrid.setColumnWidth(totalColumn, 50, Unit.PX);
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		if (!isMobile()) {
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
				// Etatus Apartado
				Column<BmObject, String> lockColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderEquipment)bmObject).getLockStatus().getSelectedOption().getLabel();
					}
				};
				orderEquipmentGrid.addColumn(lockColumn, SafeHtmlUtils.fromSafeConstant("E. Apartado"));
				orderEquipmentGrid.setColumnWidth(lockColumn, 50, Unit.PX);
				lockColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

				// Estatus Apartado
				Column<BmObject, String> lockInfoColumn = new Column<BmObject, String>(new ButtonCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						// No es de clase, ver estatus
						if (((BmoOrderEquipment)bmObject).getLockStatus().toChar() == BmoOrderEquipment.LOCKSTATUS_LOCKED) return "Info";
						else if (((BmoOrderEquipment)bmObject).getLockStatus().toChar() == BmoOrderEquipment.LOCKSTATUS_CONFLICT) return "Revisar";
						else return "";
					}
				};
				lockInfoColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						BmoOrderEquipment bmoOrderEquipment = (BmoOrderEquipment)bmObject;
						if (bmoOrderEquipment.getEquipmentId().toInteger() > 0)
							Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/rpt/orde_equipment_availability.jsp?orderId=" + bmoOrderEquipment.getOrderId().toString() + "&equipmentId=" + bmoOrderEquipment.getEquipmentId().toString()), "_blank", "");
					}
				});
				orderEquipmentGrid.addColumn(lockInfoColumn, SafeHtmlUtils.fromSafeConstant("Info Apartado"));
				orderEquipmentGrid.setColumnWidth(lockInfoColumn, 50, Unit.PX);
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
					deleteEquipment((BmoOrderEquipment)bmObject);
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
		orderEquipmentGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderEquipmentGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
	}	

	public void changeEquipment() {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			if (!changeEquipmentListBox.getSelectedId().equals(""))
				changeEquipmentAction();
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
		}
	}

	public void addEquipment(BmoEquipment bmoEquipment) {
		orderEquipmentDialogBox = new DialogBox(true);
		orderEquipmentDialogBox.setGlassEnabled(true);
		orderEquipmentDialogBox.setText("Recurso de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		orderEquipmentDialogBox.setWidget(vp);

		UiOrderEquipmentForm orderEquipmentForm = new UiOrderEquipmentForm(getUiParams(), vp, bmoOrder, bmoEquipment);

		orderEquipmentForm.show();

		orderEquipmentDialogBox.center();
		orderEquipmentDialogBox.show();
	}

	//Cambiar el recurso, primer intento
	public void changeEquipmentAction() {
		changeEquipmentAction(0);
	}
	//Cambiar el recurso
	public void changeEquipmentAction(int changeEquipmentActionRpcAttemp) {
		if (changeEquipmentActionRpcAttemp < 5) {
			setChangeEquipmentActionRpcAttempt(changeEquipmentActionRpcAttemp + 1);

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getChangeEquipmentActionRpcAttempt() < 5)
						changeEquipmentAction(getChangeEquipmentActionRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-changeEquipmentAction() ERROR: " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setChangeEquipmentActionRpcAttempt(0);
					orderUpdater.changeOrder();
					reset();
				}
			};
	
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrderEquipment.getPmClass(), bmoOrderEquipment, BmoOrderEquipment.ACTION_CHANGEORDEREQUIPMENT, "" + changeEquipmentListBox.getSelectedId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-changeEquipmentAction() ERROR: " + e.toString());
			}
		}
	} 

	public void changeEquipmentDialog(BmoOrderEquipment bmoOrderEquipment) {
		this.bmoOrderEquipment = bmoOrderEquipment;

		if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) {
			changeEquipmentDialogBox = new DialogBox(true);
			changeEquipmentDialogBox.setGlassEnabled(true);
			changeEquipmentDialogBox.setText("Cambio de Recurso");
			changeEquipmentDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			changeEquipmentDialogBox.setWidget(vp);

			UiFormFlexTable formChangeEquipmentTable = new UiFormFlexTable(getUiParams());
			BmoOrderEquipment bmoChangeOrderEquipment = new BmoOrderEquipment();
			formChangeEquipmentTable.addField(1, 0, changeEquipmentListBox, bmoChangeOrderEquipment.getEquipmentId());

			HorizontalPanel changeEquipmentButtonPanel = new HorizontalPanel();
			changeEquipmentButtonPanel.add(changeEquipmentButton);
			changeEquipmentButtonPanel.add(closeEquipmentButton);

			vp.add(formChangeEquipmentTable);
			vp.add(changeEquipmentButtonPanel);

			changeEquipmentDialogBox.center();
			changeEquipmentDialogBox.show();
		}
	}

	public void orderEquipmentDescriptionDialog(BmoOrderEquipment bmoOrderEquipment) {
		this.bmoOrderEquipment = bmoOrderEquipment;
		orderEquipmentDescriptionDialogBox = new DialogBox(true);
		orderEquipmentDescriptionDialogBox.setGlassEnabled(true);
		orderEquipmentDescriptionDialogBox.setText("Descripción Recurso");
		orderEquipmentDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		orderEquipmentDescriptionDialogBox.setWidget(vp);

		orderEquipmentDescriptionTextArea = new TextArea();
		orderEquipmentDescriptionTextArea.setSize("320px", "190px");
		orderEquipmentDescriptionTextArea.setText(bmoOrderEquipment.getDescription().toString());
		vp.add(orderEquipmentDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION))
			descriptionButtonPanel.add(changeEquipmentDescriptionButton);
		descriptionButtonPanel.add(closeEquipmentDescriptionButton);
		vp.add(descriptionButtonPanel);

		orderEquipmentDescriptionDialogBox.center();
		orderEquipmentDescriptionDialogBox.show();
	}

	public void changeOrderEquipmentDescription() {
		String description = orderEquipmentDescriptionTextArea.getText();

		try {
			if (description.length() > 0) {
				bmoOrderEquipment.getDescription().setValue(description);
				saveEquipmentChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeOrderEquipmentDescription() ERROR: " + e.toString());
		}
	}

	public void changeOrderEquipmentDays(BmObject bmObject, String days) {
		bmoOrderEquipment = (BmoOrderEquipment)bmObject;
		if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) {
			try {
				double d = Double.parseDouble(days);
				if (d > 0) {
					bmoOrderEquipment.getDays().setValue(d);
					saveEquipmentChange();
				} else {
					showSystemMessage("Los Días no pueden ser menores a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeDays(): ERROR " + e.toString());
			}
		} else {
			showSystemMessage("El Pedido no se puede modificar - está Autorizado.");
		}
	}

	public void changeOrderEquipmentPrice(BmObject bmObject, String price) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderEquipment = (BmoOrderEquipment)bmObject;
			try {
				Double p = Double.parseDouble(price);

				if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) {
					// Es de tipo Producto
					if (p == 0) {
						bmoOrderEquipment.getPrice().setValue(0);
						saveEquipmentChange();
					} else if (p > 0) {

						if (p >= bmoOrderEquipment.getBasePrice().toDouble()) {
							// Se esta subiendo precio, permitirlo
							bmoOrderEquipment.getPrice().setValue(p);
							saveEquipmentChange();
							reset();

						} else if (p < bmoOrderEquipment.getBasePrice().toDouble()) {
							// El precio es menor al precio base, validar permisos
							if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
								bmoOrderEquipment.getPrice().setValue(p);
								saveEquipmentChange();
								reset();
							} else {
								numberFormat = NumberFormat.getCurrencyFormat();
								String formatted = numberFormat.format(bmoOrderEquipment.getBasePrice().toDouble());
								showSystemMessage("No cuenta con Permisos para establecer Precio menor a: " + formatted);
								reset();
							}
						}

					} else {
						showSystemMessage("El Precio no puede ser menor a $0.00");
						reset();
					}
				} else {
					// No es de tipo producto, permite todo
					bmoOrderEquipment.getPrice().setValue(p);
					saveEquipmentChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset();
		}
	}

	public void changeOrderEquipmentQuantity(BmObject bmObject, String quantity) {
		bmoOrderEquipment = (BmoOrderEquipment)bmObject;
		if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					if (bmoOrderEquipment.getEquipmentId().toInteger() > 0) 
						showSystemMessage("No se puede modificar la cantidad de un Recurso Asignado.");
					else bmoOrderEquipment.getQuantity().setValue(quantity);
					saveEquipmentChange();
				} else {
					showSystemMessage("La Cantidad no puede ser menor a 0.");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			}
		} else {
			showSystemMessage("El Pedido no se puede modificar - está Autorizado.");
		}
	}
	
	// Primer intento
	public void saveEquipmentChange() {
		saveEquipmentChange(0);
	}

	public void saveEquipmentChange(int saveEquipmentChangeRpcAttempt) {
		if (saveEquipmentChangeRpcAttempt < 5) {
			setSaveEquipmentChangeRpcAttempt(saveEquipmentChangeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveEquipmentChangeRpcAttempt() < 5)
						saveEquipmentChange(getSaveEquipmentChangeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveEquipmentChange(): ERROR " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveEquipmentChangeRpcAttempt(0);
					processEquipmentChangeSave(result);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrderEquipment.getPmClass(), bmoOrderEquipment, callback);	
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveEquipmentChange(): ERROR " + e.toString());
			}
		}
	}

	public void deleteEquipment(BmObject bmObject){
		bmoOrderEquipment = (BmoOrderEquipment)bmObject;
		deleteEquipment();
	}

	public void deleteEquipment() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteEquipment(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processEquipmentDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoOrderEquipment.getPmClass(), bmoOrderEquipment, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteEquipment(): ERROR " + e.toString());
		}
	}

	public void processEquipmentChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Recurso: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processEquipmentDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}



	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiOrderEquipmentForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox daysTextBox = new TextBox();
		private TextBox nameTextBox = new TextBox();
		private TextArea descriptionTextArea = new TextArea();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		UiDateTimeBox lockStartDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox lockEndDateTimeBox = new UiDateTimeBox();
		private BmoOrderEquipment bmoOrderEquipment;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox equipmentSuggestBox = new UiSuggestBox(new BmoEquipment());
		private BmoOrder bmoOrder;
		private BmoEquipment bmoEquipment = new BmoEquipment();
		private int bmoEquipmentRpcAttempt;

		public UiOrderEquipmentForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoEquipment bmoEquipment) {
			super(uiParams, defaultPanel);
			this.bmoOrderEquipment = new BmoOrderEquipment();
			this.bmoOrder = bmoOrder;

			try {
				bmoOrderEquipment.getEquipmentId().setValue(bmoEquipment.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
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
			if (getSFParams().hasWrite(bmoOrderEquipment.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			//filtro para mostrar los equipos que Activos			
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoOrderEquipment.getBmoEquipment().getKind(), bmoOrderEquipment.getBmoEquipment().getStatus(), "" + BmoEquipment.STATUS_ACTIVE);
			filterList.add(filterByEnabled);

			equipmentSuggestBox = new UiSuggestBox(new BmoEquipment());
			equipmentSuggestBox.addFilter(filterByEnabled);

			defaultPanel.add(formTable);
		}

		public void show(){

			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoOrderEquipment.getDays().setValue(1);
				bmoOrderEquipment.getQuantity().setValue(1);
				bmoOrderEquipment.getLockStart().setValue(bmoOrder.getLockStart().toString());
				bmoOrderEquipment.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			formTable.addField(1, 0, equipmentSuggestBox, bmoOrderEquipment.getEquipmentId());
			formTable.addField(2, 0, nameTextBox, bmoOrderEquipment.getName());
			formTable.addField(3, 0, descriptionTextArea, bmoOrderEquipment.getDescription());
			formTable.addField(4, 0, quantityTextBox, bmoOrderEquipment.getQuantity());
			formTable.addField(5, 0, daysTextBox, bmoOrderEquipment.getDays());
			formTable.addField(6, 0, priceTextBox, bmoOrderEquipment.getPrice());
			formTable.addField(7, 0, lockStartDateTimeBox, bmoOrderEquipment.getLockStart());
			formTable.addField(8, 0, lockEndDateTimeBox, bmoOrderEquipment.getLockEnd());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		// Actualizar cantidades
		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == equipmentSuggestBox) 
				statusEffect();
		}

		private void statusEffect() {
			if (equipmentSuggestBox.getSelectedId() > 0) {
				nameTextBox.setText("");
				nameTextBox.setEnabled(false);
				priceTextBox.setText("");
				if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE))
					priceTextBox.setEnabled(true);
				else  
					priceTextBox.setEnabled(false);
				quantityTextBox.setText("1");
				quantityTextBox.setEnabled(false);
				// Colocar datos del recurso, 
				// NOTA: lo hice con asincrono porque suggestBox no te manda todos los datos de la tabla, en cambio el listBox SI?
				getBmoEquipment();
			} else {
				nameTextBox.setText("");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				quantityTextBox.setText("1");
				quantityTextBox.setEnabled(true);
			}
		}
		
		// Asigna datos personal
		private void setDataEquipment(BmoEquipment bmoEquipment) {
			nameTextBox.setText("" + bmoEquipment.getName().toString());
			descriptionTextArea.setText("" + bmoEquipment.getDescription().toString());
			priceTextBox.setText("" + bmoEquipment.getPrice().toString());
		}
		
		//Obtener la cantidad en los grupos
		public void getBmoEquipment() {
			getBmoEquipment(0);
		}

		//Obtener la cantidad en los grupos
		public void getBmoEquipment(int bmoEquipmentRpcAttempt) {
			if (bmoEquipmentRpcAttempt < 5) {
				setBmoEquipmentRpcAttempt(bmoEquipmentRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getBmoEquipmentRpcAttempt() < 5)
							getBmoEquipment(getBmoEquipmentRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getBmoEquipment() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmObject result) {
						stopLoading();
						setBmoEquipmentRpcAttempt(0);
						BmoEquipment bmoEquipment = ((BmoEquipment)result);
						setDataEquipment(bmoEquipment);
					}
				};
	
				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoEquipment .getPmClass(), equipmentSuggestBox.getSelectedId(), callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getBmoEquipment() ERROR: " + e.toString());
				}
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Recurso: " + bmUpdateResult.errorsToString());
			else {
				orderEquipmentDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderEquipment = new BmoOrderEquipment();
				bmoOrderEquipment.getEquipmentId().setValue(equipmentSuggestBox.getSelectedId());
				bmoOrderEquipment.getName().setValue(nameTextBox.getText());
				bmoOrderEquipment.getDescription().setValue(descriptionTextArea.getText());
				bmoOrderEquipment.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderEquipment.getDays().setValue(daysTextBox.getText());
				bmoOrderEquipment.getPrice().setValue(priceTextBox.getText());
				bmoOrderEquipment.getLockStart().setValue(lockStartDateTimeBox.getDateTime());
				bmoOrderEquipment.getLockEnd().setValue(lockEndDateTimeBox.getDateTime());
				bmoOrderEquipment.getOrderId().setValue(bmoOrder.getId());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderEquipment.getPmClass(), bmoOrderEquipment, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}

		// Variables para llamadas RPC
		public int getBmoEquipmentRpcAttempt() {
			return bmoEquipmentRpcAttempt;
		}

		public void setBmoEquipmentRpcAttempt(int bmoEquipmentRpcAttempt) {
			this.bmoEquipmentRpcAttempt = bmoEquipmentRpcAttempt;
		}
	}

	// Variables para llamadas RPC
	public int getSaveEquipmentChangeRpcAttempt() {
		return saveEquipmentChangeRpcAttempt;
	}

	public void setSaveEquipmentChangeRpcAttempt(int saveEquipmentChangeRpcAttempt) {
		this.saveEquipmentChangeRpcAttempt = saveEquipmentChangeRpcAttempt;
	}
	
	public int getChangeEquipmentActionRpcAttempt() {
		return changeEquipmentActionRpcAttempt;
	}

	public void setChangeEquipmentActionRpcAttempt(int changeEquipmentActionRpcAttempt) {
		this.changeEquipmentActionRpcAttempt = changeEquipmentActionRpcAttempt;
	}
}
