package com.flexwm.client.op;

import java.util.Iterator;
import com.flexwm.client.op.UiOrderForm.OrderUpdater;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderStaff;
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
import com.symgae.shared.sf.BmoProfile;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;


public class UiOrderStaffGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// OrderStaff
	private BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
	private FlowPanel orderStaffPanel = new FlowPanel();
	private CellTable<BmObject> orderStaffGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> orderStaffData;
	BmFilter orderStaffFilter;
	private TextArea orderStaffDescriptionTextArea = new TextArea();
	DialogBox orderStaffDescriptionDialogBox;
	Button changeStaffDescriptionButton = new Button("GUARDAR");
	Button closeStaffDescriptionButton = new Button("CERRAR");

	private Button addOrderStaffButton = new Button("+ PERSONAL");
	protected DialogBox orderStaffDialogBox;	

	// Cambio de Staff
	UiListBox changeStaffListBox;
	DialogBox changeStaffDialogBox;
	Button changeStaffButton = new Button("CAMBIAR");
	Button closeStaffButton = new Button("CERRAR");

	// Arbol
	public final MultiSelectionModel<BmObject> selectionOrderStaffModel = new MultiSelectionModel<BmObject>();
	private UiOrderStaffTreeModel uiOrderStaffTreeModel = new UiOrderStaffTreeModel(selectionOrderStaffModel);
	private Button treeShowButton = new Button("NAVEGADOR");
	private Button treeCloseButton = new Button("CERRAR");
	CellTree cellStaffTree;
	Label treeStaffLabel = new Label("Agregar Personal: ");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private CheckBox showQuantityCheckBox = new CheckBox();
	private CheckBox showPriceCheckBox = new CheckBox();
	//	private Label amountLabel = new Label();
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	private BmoOrder bmoOrder;
	private OrderUpdater orderUpdater;
	private int changeStaffActionRpcAttempt = 0;
	private int saveStaffChangeRpcAttempt = 0;

	public UiOrderStaffGrid(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, OrderUpdater orderUpdater){
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		this.orderUpdater = orderUpdater;

		// Arbol de seleccion
		selectionOrderStaffModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Iterator<BmObject> i = selectionOrderStaffModel.getSelectedSet().iterator();
				BmObject bmObject;
				if (i.hasNext()) {
					bmObject = (BmObject)i.next();

					// Se esta agregando un producto directo
					if (bmObject instanceof BmoProfile) {
						addStaff((BmoProfile)bmObject);
					}
				}
			}
		});
		cellStaffTree = new CellTree(uiOrderStaffTreeModel, selectionOrderStaffModel);
		cellStaffTree.setAnimationEnabled(true);
		cellStaffTree.addStyleName("quoteProductTree");
		treeStaffLabel.setStyleName("detailLabelTitle");

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
		changeStaffButton.setStyleName("formCloseButton");
		changeStaffButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeStaff();
				changeStaffDialogBox.hide();
			}
		});

		closeStaffButton.setStyleName("formCloseButton");
		closeStaffButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeStaffDialogBox.hide();
			}
		});

		// Boton de cerrar dialogo de personal
		changeStaffDescriptionButton.setStyleName("formCloseButton");
		changeStaffDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeOrderStaffDescription();
				orderStaffDescriptionDialogBox.hide();
			}
		});

		closeStaffDescriptionButton.setStyleName("formCloseButton");
		closeStaffDescriptionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				orderStaffDescriptionDialogBox.hide();
			}
		});

		// Elementos del cambio de staff
		changeStaffListBox = new UiListBox(getUiParams(), new BmoProfile());

		// Inicializar grid de Personal
		orderStaffGrid = new CellTable<BmObject>();
		orderStaffGrid.setWidth("100%");
		orderStaffPanel.clear();
		orderStaffPanel.setWidth("100%");
		setOrderStaffColumns();
		orderStaffFilter = new BmFilter();
		orderStaffFilter.setValueFilter(bmoOrderStaff.getKind(), bmoOrderStaff.getOrderId().getName(), bmoOrder.getId());
		orderStaffGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		orderStaffData = new UiListDataProvider<BmObject>(new BmoOrderStaff(), orderStaffFilter);
		orderStaffData.addDataDisplay(orderStaffGrid);
		orderStaffPanel.add(orderStaffGrid);

		// Panel de botones
		addOrderStaffButton.setStyleName("formSaveButton");
		addOrderStaffButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addStaff();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(addOrderStaffButton);
		buttonPanel.add(treeShowButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		//		formFlexTable.addSectionLabel(1, 0, "Personal", 4);
		//		formTable.addField(2, 0, showQuantityCheckBox, bmoOrder.get());
		//		formTable.addField(2, 0, showPriceCheckBox, bmoOrderStaff.getShowPrice());
		//		formFlexTable.addLabelField(2, 2, bmoOrderStaff.getAmount(), amountLabel);
		formFlexTable.addPanel(3, 0, orderStaffPanel);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		orderStaffData.list();
		orderStaffGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED ||
				bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED ||
				bmoOrder.getStatus().toChar() == BmoOrder.STATUS_CANCELLED) {
			showQuantityCheckBox.setEnabled(false);
			showPriceCheckBox.setEnabled(false);
			addOrderStaffButton.setVisible(false);
			treeShowButton.setVisible(false);
		} else {
			showQuantityCheckBox.setEnabled(true);
			showPriceCheckBox.setEnabled(true);	
			addOrderStaffButton.setVisible(true);
			treeShowButton.setVisible(true);
		}
	}

	private void showTree() {
		getUiParams().getUiTemplate().showEastPanel();
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(new HTML("<pre> </pre>"));
		getUiParams().getUiTemplate().getEastPanel().add(treeStaffLabel);
		getUiParams().getUiTemplate().getEastPanel().add(cellStaffTree);
		getUiParams().getUiTemplate().getEastPanel().add(treeCloseButton);
	}

	private void hideTree() {
		getUiParams().getUiTemplate().hideEastPanel();
	}

	public void reset(){
		orderUpdater.changeOrderStaff();
	}

	public void changeHeight() {
		orderStaffGrid.setVisibleRange(0, orderStaffData.getList().size());
	}

	public void addStaff(){
		addStaff(new BmoProfile());
	}

	public void addStaff(BmoProfile bmoProfile) {
		orderStaffDialogBox = new DialogBox(true);
		orderStaffDialogBox.setGlassEnabled(true);
		orderStaffDialogBox.setText("Personal de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		orderStaffDialogBox.setWidget(vp);

		UiOrderStaffForm orderStaffForm = new UiOrderStaffForm(getUiParams(), vp, bmoOrder, bmoProfile);

		orderStaffForm.show();

		orderStaffDialogBox.center();
		orderStaffDialogBox.show();
	}

	// Columnas
	public void setOrderStaffColumns() {
		// Cantidad
		Column<BmObject, String> quantityColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getQuantity().toString();
				}
			};
			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeOrderStaffQuantity(bmObject, value);
				}
			});
		} else {
			quantityColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getQuantity().toString();
				}
			};
		}
		orderStaffGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
		orderStaffGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// Nombre
		Column<BmObject, String> nameColumn;
		nameColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoOrderStaff)bmObject).getName().toString();
			}
		};
		orderStaffGrid.addColumn(nameColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
		orderStaffGrid.setColumnWidth(nameColumn, 100, Unit.PX);
		nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		if (!isMobile()) {
			// Si tiene acceso a cambiar recursos, mostrar boton
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION) && 
					getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEM)) {
				// Columna cambio de item
				Column<BmObject, String> changeStaffColumn = new Column<BmObject, String>(new ButtonCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						if (((BmoOrderStaff)bmObject).getProfileId().toInteger() > 0)
							return "<>";
						else return " ";
					}
				};
				changeStaffColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeStaffDialog((BmoOrderStaff)bmObject);
					}
				});
				orderStaffGrid.addColumn(changeStaffColumn, SafeHtmlUtils.fromSafeConstant("Cambiar"));
				orderStaffGrid.setColumnWidth(changeStaffColumn, 50, Unit.PX);
				changeStaffColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}

			// Columna descripcion
			Column<BmObject, String> descriptionColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoOrderStaff)bmObject).getDescription().toString().length() > 0)
						return "...";
					else return ".";
				}
			};
			descriptionColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					orderStaffDescriptionDialog((BmoOrderStaff)bmObject);
				}
			});
			orderStaffGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Desc."));
			orderStaffGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);
			descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Grupo
			Column<BmObject, String> groupColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getBmoProfile().getName().toString();
				}
			};
			groupColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			orderStaffGrid.addColumn(groupColumn, SafeHtmlUtils.fromSafeConstant("Grupo"));
			orderStaffGrid.setColumnWidth(groupColumn, 100, Unit.PX);

			// Días
			Column<BmObject, String> daysColumn;
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
				daysColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderStaff)bmObject).getDays().toString();
					}
				};
				daysColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
					@Override
					public void update(int index, BmObject bmObject, String value) {
						changeOrderStaffDays(bmObject, value);
					}
				});
			} else {
				daysColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoOrderStaff)bmObject).getDays().toString();
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
			orderStaffGrid.addColumn(daysColumn, daysHeader);
			orderStaffGrid.setColumnWidth(daysColumn, 50, Unit.PX);
			daysColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

			//Costo		
			Column<BmObject, String> costColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getBaseCost().toString();
				}
			};
			costColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			orderStaffGrid.addColumn(costColumn, SafeHtmlUtils.fromSafeConstant("Costo"));
			orderStaffGrid.setColumnWidth(costColumn, 50, Unit.PX);
		}

		// Precio
		Column<BmObject, String> priceColumn;
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getPrice().toString();
				}
			};
			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					changeOrderStaffPrice(bmObject, value);
				}
			});
		} else {
			priceColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getPrice().toString();
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
		orderStaffGrid.addColumn(priceColumn, priceHeader);
		orderStaffGrid.setColumnWidth(priceColumn, 50, Unit.PX);
		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		// Columna de Total
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				numberFormat = NumberFormat.getCurrencyFormat();
				String formatted = numberFormat.format(((BmoOrderStaff)bmObject).getAmount().toDouble());
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
		orderStaffGrid.addColumn(totalColumn, totalHeader);
		orderStaffGrid.setColumnWidth(totalColumn, 50, Unit.PX);
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
			// Etatus Apartado
			Column<BmObject, String> lockColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoOrderStaff)bmObject).getLockStatus().getSelectedOption().getLabel();
				}
			};
			orderStaffGrid.addColumn(lockColumn, SafeHtmlUtils.fromSafeConstant("E. Apartado"));
			orderStaffGrid.setColumnWidth(lockColumn, 50, Unit.PX);
			lockColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			// Estatus Apartado
			Column<BmObject, String> lockInfoColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					// No es de clase, ver estatus
					if (((BmoOrderStaff)bmObject).getLockStatus().toChar() == BmoOrderStaff.LOCKSTATUS_LOCKED) return "Info";
					else if (((BmoOrderStaff)bmObject).getLockStatus().toChar() == BmoOrderStaff.LOCKSTATUS_CONFLICT) return "Revisar";
					else return "";
				}
			};
			lockInfoColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					BmoOrderStaff bmoOrderStaff = (BmoOrderStaff)bmObject;
					if (bmoOrderStaff.getProfileId().toInteger() > 0)
						Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/rpt/orde_staff_availability.jsp?orderId=" + bmoOrderStaff.getOrderId().toString() + "&profileId=" + bmoOrderStaff.getProfileId().toString()), "_blank", "");
				}
			});
			orderStaffGrid.addColumn(lockInfoColumn, SafeHtmlUtils.fromSafeConstant("Info Apartado"));
			orderStaffGrid.setColumnWidth(lockInfoColumn, 50, Unit.PX);
			lockInfoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
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
					deleteStaff((BmoOrderStaff)bmObject);
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
		orderStaffGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		orderStaffGrid.setColumnWidth(deleteColumn, 50, Unit.PX);	
	}

	public void changeStaffDialog(BmoOrderStaff bmoOrderStaff) {
		this.bmoOrderStaff = bmoOrderStaff;

		if (bmoOrderStaff.getProfileId().toInteger() > 0) {
			changeStaffDialogBox = new DialogBox(true);
			changeStaffDialogBox.setGlassEnabled(true);
			changeStaffDialogBox.setText("Cambio de Personal");
			changeStaffDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			changeStaffDialogBox.setWidget(vp);

			UiFormFlexTable formChangeStaffTable = new UiFormFlexTable(getUiParams());
			BmoOrderStaff bmoChangeOrderStaff = new BmoOrderStaff();
			formChangeStaffTable.addField(1, 0, changeStaffListBox, bmoChangeOrderStaff.getProfileId());

			HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
			changeStaffButtonPanel.add(changeStaffButton);
			changeStaffButtonPanel.add(closeStaffButton);

			vp.add(formChangeStaffTable);
			vp.add(changeStaffButtonPanel);

			changeStaffDialogBox.center();
			changeStaffDialogBox.show();
		}
	}

	public void orderStaffDescriptionDialog(BmoOrderStaff bmoOrderStaff) {
		this.bmoOrderStaff = bmoOrderStaff;
		orderStaffDescriptionDialogBox = new DialogBox(true);
		orderStaffDescriptionDialogBox.setGlassEnabled(true);
		orderStaffDescriptionDialogBox.setText("Descripción Personal");
		orderStaffDescriptionDialogBox.setSize("400px", "200px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");
		orderStaffDescriptionDialogBox.setWidget(vp);

		orderStaffDescriptionTextArea = new TextArea();
		orderStaffDescriptionTextArea.setSize("320px", "190px");
		orderStaffDescriptionTextArea.setText(bmoOrderStaff.getDescription().toString());
		vp.add(orderStaffDescriptionTextArea);

		HorizontalPanel descriptionButtonPanel = new HorizontalPanel();
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION))
			descriptionButtonPanel.add(changeStaffDescriptionButton);
		descriptionButtonPanel.add(closeStaffDescriptionButton);
		vp.add(descriptionButtonPanel);

		orderStaffDescriptionDialogBox.center();
		orderStaffDescriptionDialogBox.show();
	}

	public void changeStaff() {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			if (!changeStaffListBox.getSelectedId().equals(""))
				changeStaffAction();
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
		}
	}

	//Cambiar el personal
	public void changeStaffAction() {
		changeStaffAction(0);
	}
	//Cambiar el personal
	public void changeStaffAction(int changeStaffActionRpcAttempt) {
		if (changeStaffActionRpcAttempt < 5) {
			setChangeStaffActionRpcAttempt(changeStaffActionRpcAttempt + 1);

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getChangeStaffActionRpcAttempt() < 5)
						changeStaffAction(getChangeStaffActionRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-changeStaffAction() ERROR: " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setChangeStaffActionRpcAttempt(0);
					orderUpdater.changeOrder();
					reset();
				}
			};
	
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrderStaff.getPmClass(), bmoOrderStaff, BmoOrderStaff.ACTION_CHANGEORDERSTAFF, changeStaffListBox.getSelectedId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-changeStaffAction() ERROR: " + e.toString());
			}
		}
	} 

	public void changeOrderStaffDescription() {
		String description = orderStaffDescriptionTextArea.getText();

		try {
			if (description.length() > 0) {
				bmoOrderStaff.getDescription().setValue(description);
				saveStaffChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeOrderStaffDescription() ERROR: " + e.toString());
		}
	}

	public void changeOrderStaffQuantity(BmObject bmObject, String quantity) {
		bmoOrderStaff = (BmoOrderStaff)bmObject;
		if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) {
			try {
				int q = Integer.parseInt(quantity);
				if (q > 0) {
					bmoOrderStaff.getQuantity().setValue(quantity);
					saveStaffChange();
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

	public void changeOrderStaffDays(BmObject bmObject, String days) {
		bmoOrderStaff = (BmoOrderStaff)bmObject;
		if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) {
			try {
				double d = Double.parseDouble(days);
				if (d > 0) {
					bmoOrderStaff.getDays().setValue(d);
					saveStaffChange();
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

	public void changeOrderStaffPrice(BmObject bmObject, String price) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			bmoOrderStaff = (BmoOrderStaff)bmObject;
			try {
				Double p = Double.parseDouble(price);

				if (bmoOrderStaff.getProfileId().toInteger() > 0) {
					// Es de tipo Producto
					if (p == 0) {
						bmoOrderStaff.getPrice().setValue(0);
						saveStaffChange();
					} else if (p > 0) {

						if (p >= bmoOrderStaff.getBasePrice().toDouble()) {
							// Se esta subiendo precio, permitirlo
							bmoOrderStaff.getPrice().setValue(p);
							saveStaffChange();
							reset();

						} else if (p < bmoOrderStaff.getBasePrice().toDouble()) {
							// El precio es menor al precio base, validar permisos
							if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE)) {
								bmoOrderStaff.getPrice().setValue(p);
								saveStaffChange();
								reset();
							} else {
								numberFormat = NumberFormat.getCurrencyFormat();
								String formatted = numberFormat.format(bmoOrderStaff.getBasePrice().toDouble());
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
					bmoOrderStaff.getPrice().setValue(p);
					saveStaffChange();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}		
		} else {
			showSystemMessage("No se puede editar la Cotización - Está Autorizada.");
			this.reset(); 
		}
	}
	// Primer intento
	public void saveStaffChange() {
		saveStaffChange(0);
	}

	public void saveStaffChange(int saveStaffChangeRpcAttempt) {
		if (saveStaffChangeRpcAttempt < 5) {
			setSaveStaffChangeRpcAttempt(saveStaffChangeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveStaffChangeRpcAttempt() < 5)
						saveStaffChange(getSaveStaffChangeRpcAttempt());
					else 
						showErrorMessage(this.getClass().getName() + "-saveStaffChange(): ERROR " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveStaffChangeRpcAttempt(0);
					processStaffChangeSave(result);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrderStaff.getPmClass(), bmoOrderStaff, callback);	
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveStaffChange(): ERROR " + e.toString());
			}
		}
	}

	public void deleteStaff(BmoOrderStaff bmoOrderStaff) {
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
			this.bmoOrderStaff = bmoOrderStaff;
			deleteStaff();
		} else {
			showSystemMessage("El Pedido no se puede modificar - está Autorizado");
		}
	}

	public void deleteStaff() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteStaff(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processStaffDelete(result);
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoOrderStaff.getPmClass(), bmoOrderStaff, callback);	
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteStaff(): ERROR " + e.toString());
		}
	}

	public void processStaffChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Personal: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processStaffDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}	

	// Agrega un item de un producto a un grupo de la cotizacion
	private class UiOrderStaffForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox nameTextBox = new TextBox();
		private TextArea descriptionTextArea = new TextArea();
		private TextBox daysTextBox = new TextBox();
		private TextBox quantityTextBox = new TextBox();
		private TextBox priceTextBox = new TextBox();
		private UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());

		private BmoOrderStaff bmoOrderStaff;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoOrder bmoOrder;
		private BmoProfile bmoProfile = new BmoProfile();
		private Label quoteStaffQuantity = new Label();
		private Label orderStaffQuantity = new Label();
		String profileId = "";
		private int quoteStaffQuantityRpcAttempt = 0;
		private int orderStaffQuantityRpcAttempt = 0;

		public UiOrderStaffForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, BmoProfile bmoProfile) {
			super(uiParams, defaultPanel);
			this.bmoOrderStaff = new BmoOrderStaff();
			this.bmoOrder = bmoOrder;
			this.bmoProfile = bmoProfile;

			try {
				bmoOrderStaff.getProfileId().setValue(bmoProfile.getId());
				profileId = String.valueOf(bmoProfile.getId());
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
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
			if (getSFParams().hasWrite(bmoOrderStaff.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			// Mostrar grupos que estan en tipos de pedidos
//			BmoOrderTypeGroup bmoOrderTypeGroup = new BmoOrderTypeGroup();
//			BmFilter filterGroups = new BmFilter();
//			filterGroups.setInFilter(bmoOrderTypeGroup.getKind(), 
//					bmoProfile.getIdFieldName(),
//					bmoOrderTypeGroup.getProfileId().getName(),
//					bmoOrderTypeGroup.getOrderTypeId().getName(),
//					"" + bmoOrder.getOrderTypeId().toInteger()
//					);
//			profileListBox.addFilter(filterGroups);

			defaultPanel.add(formTable);
		}

		public void show(){
			// Por defaul la cotizacion maneja 1 dia
			try {
				bmoOrderStaff.getDays().setValue(1);
				bmoOrderStaff.getQuantity().setValue(1);
				bmoOrderStaff.getLockStart().setValue(bmoOrder.getLockStart().toString());
				bmoOrderStaff.getLockEnd().setValue(bmoOrder.getLockEnd().toString());
				bmoProfile.getIdField().setNullable(true);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-show() ERROR: al asignar valor 1 a los dias del item de la cotizacion: " + e.toString());
			}

			formTable.addField(1, 0, profileListBox, bmoProfile.getIdField());
			formTable.addLabelField(2, 0, "En Cotización", quoteStaffQuantity);
			formTable.addLabelField(3, 0, "En Pedidos", orderStaffQuantity);
			formTable.addField(4, 0, nameTextBox, bmoOrderStaff.getName());
			formTable.addField(5, 0, descriptionTextArea, bmoOrderStaff.getDescription());
			formTable.addField(6, 0, quantityTextBox, bmoOrderStaff.getQuantity());
			formTable.addField(7, 0, daysTextBox, bmoOrderStaff.getDays());
			formTable.addField(8, 0, priceTextBox, bmoOrderStaff.getPrice());
			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == profileListBox) {
				profileId = profileListBox.getSelectedId();
				getQuoteStaffQuantity();
			} 

			statusEffect();
		}

		private void statusEffect() {
			if (!profileListBox.getSelectedId().equals("") && !profileListBox.getSelectedId().equals("0")) {
				getQuoteStaffQuantity();

				nameTextBox.setText("");
				nameTextBox.setEnabled(false);
				if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGEITEMPRICE))
					priceTextBox.setEnabled(true);
				else  
					priceTextBox.setEnabled(false);
			} else {
				nameTextBox.setText("");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Personal: " + bmUpdateResult.errorsToString());
			else {
				orderStaffDialogBox.hide();
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderStaff = new BmoOrderStaff();
				bmoOrderStaff.getProfileId().setValue(profileListBox.getSelectedId());
				bmoOrderStaff.getName().setValue(nameTextBox.getText());
				bmoOrderStaff.getDescription().setValue(descriptionTextArea.getText());
				bmoOrderStaff.getDays().setValue(daysTextBox.getText());
				bmoOrderStaff.getPrice().setValue(priceTextBox.getText());
				bmoOrderStaff.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderStaff.getOrderId().setValue(bmoOrder.getId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderStaff.getPmClass(), bmoOrderStaff, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}

		//Obtener la cantidad en COTIZACIONES, primer intento
		public void getQuoteStaffQuantity() {
			getQuoteStaffQuantity(0);
		}
		//Obtener la cantidad en almacen
		public void getQuoteStaffQuantity(int quoteStaffQuantityRpcAttempt) {
			if (quoteStaffQuantityRpcAttempt < 5) {
				setQuoteStaffQuantityRpcAttempt(quoteStaffQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getQuoteStaffQuantityRpcAttempt() < 5)
							getQuoteStaffQuantity(getQuoteStaffQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getQuoteStaffQuantity() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setQuoteStaffQuantityRpcAttempt(0);
						quoteStaffQuantity.setText(result.getMsg());
						getOrderStaffQuantity();
					}
				};
	
				try {	
					quoteStaffQuantity.setText("");
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_QUOTESTAFFQUANTITY, profileId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getQuoteStaffQuantity() ERROR: " + e.toString());
				}
			}
		} 

		//Obtener la cantidad en PEDIDOS, primer intento
		public void getOrderStaffQuantity() {
			getOrderStaffQuantity(0);
		}
		//Obtener la cantidad en almacen
		public void getOrderStaffQuantity(int orderStaffQuantityRpcAttempt) {
			if (orderStaffQuantityRpcAttempt < 5) {
				setOrderStaffQuantityRpcAttempt(orderStaffQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getOrderStaffQuantityRpcAttempt() < 5)
							getOrderStaffQuantity(getOrderStaffQuantityRpcAttempt());
						else;
						showErrorMessage(this.getClass().getName() + "-getOrderStaffQuantity() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setOrderStaffQuantityRpcAttempt(0);
						orderStaffQuantity.setText(result.getMsg());
						setDataGroup();
					}
				};
	
				try {	
					orderStaffQuantity.setText("");
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_ORDERSTAFFQUANTITY, profileId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getOrderStaffQuantity() ERROR: " + e.toString());
				}
			}
		}

		// Asigna datos personal
		private void setDataGroup() {
			nameTextBox.setText("" + ((BmoProfile)profileListBox.getSelectedBmObject()).getName().toString());
			descriptionTextArea.setText("" + ((BmoProfile)profileListBox.getSelectedBmObject()).getDescription().toString());
			priceTextBox.setText("" + ((BmoProfile)profileListBox.getSelectedBmObject()).getPrice().toString());
		}
		
		// Variables para llamadas RPC
		public int getQuoteStaffQuantityRpcAttempt() {
			return quoteStaffQuantityRpcAttempt;
		}

		public void setQuoteStaffQuantityRpcAttempt(int quoteStaffQuantityRpcAttempt) {
			this.quoteStaffQuantityRpcAttempt = quoteStaffQuantityRpcAttempt;
		}

		public int getOrderStaffQuantityRpcAttempt() {
			return orderStaffQuantityRpcAttempt;
		}

		public void setOrderStaffQuantityRpcAttempt(int orderStaffQuantityRpcAttempt) {
			this.orderStaffQuantityRpcAttempt = orderStaffQuantityRpcAttempt;
		}
	}
	// Variables para llamadas RPC
	public int getChangeStaffActionRpcAttempt() {
		return changeStaffActionRpcAttempt;
	}

	public void setChangeStaffActionRpcAttempt(int changeStaffActionRpcAttempt) {
		this.changeStaffActionRpcAttempt = changeStaffActionRpcAttempt;
	}
	
	public int getSaveStaffChangeRpcAttempt() {
		return saveStaffChangeRpcAttempt;
	}

	public void setSaveStaffChangeRpcAttempt(int saveStaffChangeRpcAttempt) {
		this.saveStaffChangeRpcAttempt = saveStaffChangeRpcAttempt;
	}
}
