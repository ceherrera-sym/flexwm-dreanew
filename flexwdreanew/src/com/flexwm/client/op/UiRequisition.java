/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import java.util.ArrayList;
import java.util.Date;

import com.flexwm.client.fi.UiBankMovement;
import com.flexwm.client.fi.UiPaccount;
import com.flexwm.client.wf.UiWFlowLog;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoLoan;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoRequisitionType;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.client.ui.UiTemplate;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class UiRequisition extends UiList {
	BmoRequisition bmoRequisition = new BmoRequisition();

	// Dialogo informacion de cuentas del pedido
	private Image orderBalanceButton = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_orderbalance.png"));
	private Button orderBalanceCloseDialogButton = new Button("CERRAR");
	private DialogBox orderBalanceDialogBox;

	BmoOrder bmoOrder = new BmoOrder();
	BmoProperty bmoProperty = new BmoProperty();
	BmoOpportunity bmoOpportunity = new BmoOpportunity();

	public UiRequisition(UiParams uiParams) {
		super(uiParams, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();

		initialize();
	}

	public UiRequisition(UiParams uiParams, BmoOrder bmoOrder) {
		super(uiParams, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();

		this.bmoOrder = bmoOrder;

		initialize();
	}
	public UiRequisition(UiParams uiParams,Panel defaultPanel, BmoProperty bmoProperty) {
		super(uiParams,defaultPanel, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();
		this.bmoProperty = bmoProperty;
		initialize();
	}

	public UiRequisition(UiParams uiParams, BmoOrder bmoOrder, BmoProperty bmoProperty) {
		super(uiParams, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();
		this.bmoOrder = bmoOrder;
		this.bmoProperty = bmoProperty;
		initialize();
	}

	public UiRequisition(UiParams uiParams,BmoOpportunity bmoOpportunity) {
		super(uiParams, new BmoRequisition());
		bmoRequisition = (BmoRequisition)getBmObject();
		this.bmoOpportunity = bmoOpportunity;
		initialize();
	}

	private void initialize() {

		// Saldo de Pêdido
		orderBalanceButton.setTitle("Saldo Pedido");
		orderBalanceButton.setStyleName("listSearchImage");
		orderBalanceButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {				
				openOrderBalanceDialog();				
			}
		});

		orderBalanceCloseDialogButton.setStyleName("formCloseButton");
		orderBalanceCloseDialogButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				orderBalanceDialogBox.hide();
			}
		});


	}

	@Override
	public void postShow() {
		if (isMaster()) {
			addFilterSuggestBox(new UiSuggestBox(new BmoSupplier()), new BmoSupplier(), bmoRequisition.getSupplierId());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoRequisition.getBmoRequisitionType().getType()), bmoRequisition, bmoRequisition.getBmoRequisitionType().getType());

			if (!isMobile()) {
				// Si está habilitado control presupuestal, mostrar filtro
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean())
					addFilterSuggestBox(new UiSuggestBox(new BmoBudgetItem()), new BmoBudgetItem(), bmoRequisition.getBudgetItemId());
			}

			addStaticFilterListBox(new UiListBox(getUiParams(), bmoRequisition.getStatus()), bmoRequisition, bmoRequisition.getStatus(), "" + BmoRequisition.STATUS_EDITION);

			if (!isMobile()) {
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRequisition.getDeliveryStatus()), bmoRequisition, bmoRequisition.getDeliveryStatus());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRequisition.getPaymentStatus()), bmoRequisition, bmoRequisition.getPaymentStatus());
				addDateRangeFilterListBox(bmoRequisition.getRequestDate());
			}
		} else {
			// Agrega boton de revisar detalle pedidos
			if (bmoOrder.getId() > 0)
				localButtonPanel.add(orderBalanceButton);
		}

		if (getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGESTATUS))
			addActionBatchListBox(new UiListBox(getUiParams(), new BmoRequisition().getStatus()), bmoRequisition);
	}

	@Override
	public void create() {
		UiRequisitionForm uiRequisitionForm = new UiRequisitionForm(getUiParams(), 0);
		uiRequisitionForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRequisitionForm uiRequisitionForm = new UiRequisitionForm(getUiParams(), bmObject.getId());
		uiRequisitionForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiRequisitionForm uiRequisitionForm = new UiRequisitionForm(getUiParams(), bmObject.getId());
		uiRequisitionForm.show();
	}

	public void edit(int requisitionId) {
		UiRequisitionForm uiRequisitionForm = new UiRequisitionForm(getUiParams(), requisitionId);
		uiRequisitionForm.show();
	}

	// Abre dialogo saldo pedidos
	public void openOrderBalanceDialog() {
		orderBalanceDialogBox = new DialogBox(true);
		orderBalanceDialogBox.setGlassEnabled(true);
		orderBalanceDialogBox.setText("Rentabilidad Pedido");
		orderBalanceDialogBox.setSize("250px", "150px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("250px", "150px");
		orderBalanceDialogBox.setWidget(vp);

		FlowPanel orderPanel = new FlowPanel();
		orderPanel.setWidth("250px");

		// Obten info del filtro forzado
		BmFilter forceFilter = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).getForceFilter();

		UiRequisitionOrderView uiRequisitionOrderView = new UiRequisitionOrderView(getUiParams(), orderPanel, forceFilter.getValue());
		uiRequisitionOrderView.show();					

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(orderBalanceCloseDialogButton);

		vp.add(orderPanel);
		vp.add(buttonPanel);

		orderBalanceDialogBox.center();
		orderBalanceDialogBox.show();
	}


	public class UiRequisitionForm extends UiFormDialog {	
		private BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
		BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
		BmoCurrency bmoCurrency = new BmoCurrency();
		BmoOrderType bmoOrderType = new BmoOrderType();

		UiSuggestBox requisitionSuggestBox = new UiSuggestBox(new BmoRequisition());	
		UiListBox requisitionTypeListBox = new UiListBox(getUiParams(), new BmoRequisitionType());	
		UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());	
		UiSuggestBox responsibleSuggestBox;
		TextBox codeTextBox = new TextBox();	
		TextBox nameTextBox = new TextBox();	
		TextArea descriptionTextArea = new TextArea();
		TextArea observationsTextArea = new TextArea();
		UiDateBox requestDateBox= new UiDateBox();
		UiDateBox paymentDateBox= new UiDateBox();
		UiDateBox deliveryDateBox= new UiDateBox();
		UiDateTimeBox deliveryTimeDateTimeBox = new UiDateTimeBox();
		UiSuggestBox requestedBySuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox warehouseListBox = new UiListBox(getUiParams(), new BmoWarehouse());
		UiListBox reqPayTypeListBox = new UiListBox(getUiParams(), new BmoReqPayType());
		UiListBox areaListBox = new UiListBox(getUiParams(), new BmoArea());	
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
		
		UiListBox contractEstimationListBox = new UiListBox(getUiParams(), new BmoContractEstimation());
		TextBox amountTextBox = new TextBox();
		TextBox discountTextBox = new TextBox();
		TextBox taxTextBox = new TextBox();
		TextBox holdBackTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		TextBox paymentsTextBox = new TextBox();
		TextBox balanceTextBox = new TextBox();
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
		UiListBox loanListBox = new UiListBox(getUiParams(), new BmoLoan());
		//campo de inadico
		UiSuggestBox propertySuggestBox = new UiSuggestBox(new BmoProperty());

		//Calidad
		UiListBox qualityListBox = new UiListBox(getUiParams());
		UiListBox punctualityListBox = new UiListBox(getUiParams());
		UiListBox attentionListBox = new UiListBox(getUiParams());
		
		CheckBox showOnOutFormatCheckBox = new CheckBox();

		private FlowPanel requisitionItemPanel = new FlowPanel();
		private HorizontalPanel requisitionButtonPanel = new HorizontalPanel();
		private CellTable<BmObject> requisitionItemGrid = new CellTable<BmObject>();
		private UiListDataProvider<BmObject> data;
		protected DialogBox requisitionItemDialogBox;
		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
		private Button addRequisitionItemButton = new Button("+ ITEM");

		// Browser de Productos
		public final SingleSelectionModel<BmObject> productSelection = new SingleSelectionModel<BmObject>();
		private UiProductTree uiProductTree = new UiProductTree(productSelection);
		private CellBrowser productCellBrowser;
		private FlowPanel productCellPanel = new FlowPanel();

		// Ciclo de vida documento
		public final SingleSelectionModel<BmObject> lifeCycleSelection = new SingleSelectionModel<BmObject>();
		private UiRequisitionLifeCycleViewModel uiLifeCycleViewModel;
		private CellTree lifeCycleCellTree;
		private DialogBox lifeCycleDialogBox = new DialogBox();
		private Button lifeCycleShowButton = new Button("SEGUIMIENTO");
		private Button lifeCycleCloseButton = new Button("CERRAR");

		// Comprobacion de viaticos
		private DialogBox verifyTravelExpenseDialogBox = new DialogBox();
		private Button verifyTravelExpenseShowButton = new Button("COMPROBACIÓN VIÁTICOS");		

		// IVA
		double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
		private CheckBox taxAppliesCheckBox = new CheckBox("");

		// Copiar OC
		private Button copyRequisitionButton = new Button("COPIAR");
		private Button copyRequisitionDialogButton = new Button("COPIAR "+getSFParams().getProgramTitle(bmoRequisition));
		private Button copyRequisitionCloseDialogButton = new Button("CERRAR");
		protected DialogBox copyRequisitionDialogBox;
		UiSuggestBox copyRequisitionSuggestBox = new UiSuggestBox(new BmoRequisition());

		String generalSection = "Datos Generales";
		String detailsSection = "Detalle y Fechas";
		String productSection = "Navegador de Productos";
		String itemSection = "Items";
		String totalsSection = "Totales";
		String statusSection = "Estatus";
		String wFlowLogSection = "Bitácora WFlow";

		private int propertyRpcAttempt = 0;
		private int parityFromCurrencyRpcAttempt = 0;
		private String currencyId = "";
		private int idRpcAttempt = 0;
		private int idReqiRpc = 0;
		private int idUserRpc = 0;
		private int userAutorizedRpcAttempt = 0;
		private int saveAmountChangeRpcAttempt = 0;
		private int defaultOrderTypeId = 0;
		private int bmoOrderTypeRpcAttempt = 0;
		private int saveItemChangeRpcAttempt = 0;

		public UiRequisitionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRequisition(), id);
			initialize();
		}

		private void initialize() {

			// Copiar cotizacion
			copyRequisitionDialogButton.setStyleName("formCloseButton");
			copyRequisitionDialogButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyRequisitionDialog();
				}
			});

			copyRequisitionCloseDialogButton.setStyleName("formCloseButton");
			copyRequisitionCloseDialogButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyRequisitionDialogBox.hide();
				}
			});

			copyRequisitionButton.setStyleName("formCloseButton");
			copyRequisitionButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyRequisitionAction();
					copyRequisitionDialogBox.hide();
				}
			});

			// Comprobacion de gastos-viaticos
			verifyTravelExpenseShowButton.setStyleName("formSaveButton");
			verifyTravelExpenseShowButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showVerifyTravelExpenseDialog();
				}
			});

			// Inicializar GRID si es registro existente
			if (id > 0) {									

				// Browser de seleccion de productos
				productSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						BmObject bmObject = productSelection.getSelectedObject();
						// Se esta agregando un producto directo
						if (bmObject instanceof BmoProduct) {
							addProduct((BmoProduct)bmObject);
						}
					}
				});
				productCellBrowser = new CellBrowser.Builder<SingleSelectionModel<BmObject>>(uiProductTree, productSelection).build();
				productCellBrowser.setSize("100%", "200px");
				productCellBrowser.setAnimationEnabled(true);
				productCellPanel.setSize("100%", "200px");
				productCellPanel.add(productCellBrowser);

				// Ciclo de vida de ordenes de compra
				lifeCycleSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						BmObject bmObject = lifeCycleSelection.getSelectedObject();
						// Se esta tratando de abrir un registro
						if (bmObject instanceof BmoRequisition) {
							if (Window.confirm("Desea Abrir la Órden de Compra?"))
								new UiRequisition(getUiParams()).edit(bmObject);;
						} else if (bmObject instanceof BmoRequisitionReceipt) {
							if (Window.confirm("Desea Abrir el Recibo de Órden de Compra?"))
								new UiRequisitionReceipt(getUiParams()).edit(bmObject);
						} else if (bmObject instanceof BmoPaccount) {
							if (Window.confirm("Desea Abrir la Cuenta por Pagar?"))
								new UiPaccount(getUiParams()).edit(bmObject);
						} else if (bmObject instanceof BmoBankMovConcept) {
							if (Window.confirm("Desea Abrir el Movimiento Bancario?"))
								new UiBankMovement(getUiParams()).edit(((BmoBankMovConcept)bmObject).getBmoBankMovement()); 
						} 
					}
				});
				uiLifeCycleViewModel = new UiRequisitionLifeCycleViewModel(lifeCycleSelection, id);
				lifeCycleCellTree = new CellTree(uiLifeCycleViewModel, lifeCycleSelection);
				lifeCycleCellTree.setSize("100%", "100%");
				lifeCycleCellTree.setAnimationEnabled(true);

				lifeCycleShowButton.setStyleName("formCloseButton");
				lifeCycleShowButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						showLifeCycleDialog();
					}
				});
				lifeCycleCloseButton.setStyleName("formCloseButton");
				lifeCycleCloseButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						lifeCycleDialogBox.hide();
					}
				});

				// Todas las operaciones con los items de la orden de compra
				addRequisitionItemButton.setStyleName("formSaveButton");
				addRequisitionItemButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						addProduct();
					}
				});

				requisitionButtonPanel.add(addRequisitionItemButton);

				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId().getName(), id);
				data = new UiListDataProvider<BmObject>(new BmoRequisitionItem(), bmFilter);

				requisitionItemGrid.setWidth("100%");
				requisitionItemPanel.setWidth("100%");

				data.addDataDisplay(requisitionItemGrid);
				requisitionItemPanel.add(requisitionItemGrid);

				requisitionItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
					@Override
					public void onLoadingStateChanged(LoadingStateChangeEvent event) {
						if (event.getLoadingState() == LoadingState.LOADED) {
							changeHeight();
						}
					}
				}); 

				ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						discountChange();
					}
				};
				discountTextBox.addValueChangeHandler(textChangeHandler);

				ValueChangeHandler<String> textChangeHandler1 = new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						holdBackChange();
					}
				};
				holdBackTextBox.addValueChangeHandler(textChangeHandler1);
			}

			taxAppliesCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					taxAppliesChange();
				}
			});
			//campo responsable Drea
			BmFilter userBmFilter = new BmFilter();
			BmoUser bmoUser = new BmoUser();
			userBmFilter.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			responsibleSuggestBox = new UiSuggestBox(new BmoUser(), userBmFilter);
		}

		public void copyRequisitionDialog() {
			copyRequisitionDialogBox = new DialogBox(true);
			copyRequisitionDialogBox.setGlassEnabled(true);
			copyRequisitionDialogBox.setText("Copiar " + getSFParams().getProgramTitle(bmoRequisition));
			copyRequisitionDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			copyRequisitionDialogBox.setWidget(vp);

			UiFormFlexTable formCopyRequisitionTable = new UiFormFlexTable(getUiParams());

			BmoRequisition fromBmoRequisition = new BmoRequisition();
			formCopyRequisitionTable.addField(1, 0, copyRequisitionSuggestBox, fromBmoRequisition.getIdField());

			HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
			changeStaffButtonPanel.add(copyRequisitionButton);
			changeStaffButtonPanel.add(copyRequisitionCloseDialogButton);

			vp.add(formCopyRequisitionTable);
			vp.add(changeStaffButtonPanel);

			copyRequisitionDialogBox.center();
			copyRequisitionDialogBox.show();
		}


		public void copyRequisitionAction() {

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else
						showErrorMessage(this.getClass().getName() + "-copyRequisitionAction() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					if (!result.hasErrors()) {
						stopLoading();
						dialogClose();
						BmoRequisition bmoRequisition = (BmoRequisition) result.getBmObject();
						UiRequisition uiRequisition = new UiRequisition(getUiParams());
						uiRequisition.edit(bmoRequisition.getId());
						showSystemMessage("Copia de OC Exitosa.");
					}
					else {

						dialogClose();
						stopLoading();
						UiRequisition uiRequisition = new UiRequisition(getUiParams());
						uiRequisition.create();
						showErrorMessage(this.getClass().getName() + "-createRequisition ERROR: "
								+ result.getBmErrorList().get(0).msg + result.getMsg());
					}
				}
			};

			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoRequisition.getPmClass(), bmoRequisition, BmoRequisition.ACTION_COPYREQUISITION, "" + copyRequisitionSuggestBox.getSelectedId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-copyRequisitionAction() ERROR: " + e.toString());
			}
		} 

		@Override
		public void populateFields() {
			bmoRequisition = (BmoRequisition)getBmObject();			

			try {
				// Asignar valores por default
				if (newRecord) {
					bmoRequisition.getRequestedBy().setValue(getSFParams().getLoginInfo().getUserId());
					bmoRequisition.getRequestDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoRequisition.getDeliveryDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					// Asignar por defecto los terminos de pago desde conf.
					bmoRequisition.getReqPayTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultReqPayTypeId().toInteger());

					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoRequisition.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
					

					if (isSlave()) {
						if (bmoOrder.getId() > 0) {
							// Es derivado de un pedido, no es necesario mostrarlo
							orderSuggestBox.setSelectedId(bmoOrder.getId());					
							bmoRequisition.getOrderId().setValue(bmoOrder.getId());
							bmoRequisition.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
							bmoRequisition.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
							bmoRequisition.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().getValue()); 

							if(bmoOrder.getBmoOrderType().getType().equals(""+BmoOrderType.TYPE_LEASE)
									|| bmoOrder.getBmoOrderType().getType().equals(""+BmoOrderType.TYPE_PROPERTY)) {
								if(bmoOrder.getBmoOrderType().getType().equals(""+BmoOrderType.TYPE_LEASE)) {
									//enviar el orderId y buscar en la tabla orderPropertiesTax este metodo debe regresar el bmoinmueble setearlo en el compo imueble
									getOrderPropertyTax(bmoOrder.getId());
								}
								else if(bmoOrder.getBmoOrderType().getType().equals(""+BmoOrderType.TYPE_PROPERTY)) {
									//enviar el orderId y buscar en la tabla orderProperties este metodo debe regresar el bmoinmueble setearlo en el compo imueble
									getOrderProperty(bmoOrder.getId());
								}

							}
						} else if (bmoOpportunity.getId() > 0) {
							bmoRequisition.getCompanyId().setValue(bmoOpportunity.getCompanyId().toInteger());
							bmoRequisition.getCurrencyId().setValue(bmoOpportunity.getCurrencyId().toInteger());
							bmoRequisition.getCurrencyParity().setValue(bmoOpportunity.getCurrencyParity().getValue()); 
						}

					}
					if(bmoProperty.getId() > 0) {
						bmoRequisition.getPropertyId().setValue(bmoProperty.getId());
						propertySuggestBox.setSelectedId(bmoProperty.getId());
					}
					if (bmoOpportunity.getId() > 0) {
						bmoRequisition.getOportunityId().setValue(bmoOpportunity.getId());
					}
				} else {
					// Modifica titulo
					formDialogBox.setText("Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoRequisition.getCode());
				}

				if (isSlave()) {
					orderSuggestBox.setEnabled(false);
				}

				// Si no esta asignado el area, buscar por el usuario loggeado
				if (!(bmoRequisition.getAreaId().toInteger() > 0)) 
					bmoRequisition.getAreaId().setValue(getSFParams().getLoginInfo().getBmoUser().getAreaId().toInteger());

				// Si no esta asignada la moneda, buscar por la default
				if (!(bmoRequisition.getCurrencyId().toInteger() > 0)) {
					bmoRequisition.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());				
					getParityFromCurrency(bmoRequisition.getCurrencyId().toString());
				}

				// Mostrar usuarios Activos
				BmoUser bmoUser = new BmoUser();
				BmFilter bmFilterUsersActives = new BmFilter();
				bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
				requestedBySuggestBox.addFilter(bmFilterUsersActives);

				taxAppliesCheckBox.setValue(bmoRequisition.getTaxApplies().toBoolean());

				// Deshabilita archivos si no esta en edicion
				if (bmoRequisition.getStatus().toChar()!=(BmoRequisition.STATUS_CANCELLED))
					setEnableFiles(true);
				else
					setEnableFiles(false);   

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
			}

			// Campo para cambiar de OC 
			if (!newRecord)
				formFlexTable.addField(1, 0, requisitionSuggestBox, bmoRequisition.getIdField());	

			formFlexTable.addSectionLabel(2, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(3, 0, codeTextBox, bmoRequisition.getCode());
			formFlexTable.addField(4, 0, requisitionTypeListBox, bmoRequisition.getRequisitionTypeId());	
			formFlexTable.addField(5, 0, nameTextBox, bmoRequisition.getName());
			formFlexTable.addField(6, 0, companyListBox, bmoRequisition.getCompanyId());
			formFlexTable.addField(7, 0, supplierSuggestBox, bmoRequisition.getSupplierId());

			setContractEstimationFilters(bmoRequisition.getSupplierId().toInteger(), bmoRequisition.getCompanyId().toInteger());
			formFlexTable.addField(9, 0, contractEstimationListBox, bmoRequisition.getContractEstimationId());

			setLoanFilters(bmoRequisition.getCompanyId().toInteger());				
			formFlexTable.addField(10, 0, loanListBox, bmoRequisition.getLoanId());

			if (!isSlave())
				setOrderSuggestBoxFilters(bmoRequisition.getCompanyId().toInteger());
			formFlexTable.addField(11, 0, orderSuggestBox, bmoRequisition.getOrderId());

			// Filtrar almacenes segun empresa
			setWarehouseListBoxFilters(bmoRequisition.getCompanyId().toInteger());
			formFlexTable.addField(12, 0, warehouseListBox, bmoRequisition.getWarehouseId());
			//Filtrar Inmueble segun empresa
			setFilterProperty(companyListBox.getSelectedId());
			formFlexTable.addField(13, 0, propertySuggestBox, bmoRequisition.getPropertyId());

			//Mostar los item del presupuesto de obra
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				setBudgetItemsSuggestBoxFilters();
				formFlexTable.addField(14, 0, budgetItemSuggestBox, bmoRequisition.getBudgetItemId());
			}

			formFlexTable.addSectionLabel(15, 0, detailsSection, 2);
			formFlexTable.addField(16, 0, areaListBox, bmoRequisition.getAreaId());
			formFlexTable.addField(17, 0, requestedBySuggestBox, bmoRequisition.getRequestedBy());
			formFlexTable.addField(18, 0, requestDateBox, bmoRequisition.getRequestDate());
			formFlexTable.addField(19, 0, deliveryDateBox, bmoRequisition.getDeliveryDate());
			formFlexTable.addField(20, 0, reqPayTypeListBox, bmoRequisition.getReqPayTypeId());
			formFlexTable.addField(21, 0, paymentDateBox, bmoRequisition.getPaymentDate());
			formFlexTable.addField(22, 0, descriptionTextArea, bmoRequisition.getDescription());
			formFlexTable.addField(23, 0, currencyListBox, bmoRequisition.getCurrencyId());
			formFlexTable.addField(24, 0, currencyParityTextBox, bmoRequisition.getCurrencyParity());
			//campos drea
			if (bmoRequisition.getBmoRequisitionType().getOutFormat().toBoolean()) {
				formFlexTable.addField(25, 0, showOnOutFormatCheckBox,bmoRequisition.getShowOnOutFormat());
			
				formFlexTable.addField(26, 0, deliveryTimeDateTimeBox, bmoRequisition.getDeliveryTime());
				formFlexTable.addField(27, 0, responsibleSuggestBox, bmoRequisition.getResponsibleId());
				formFlexTable.addField(28, 0, observationsTextArea, bmoRequisition.getObservations());
			}

			if (!newRecord) {
				showAutorizedData();


				// Solo permitir agregar si esta en edicion
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)) {
					formFlexTable.addSectionLabel(31, 0, productSection, 2);
					formFlexTable.addPanel(32, 0, productCellPanel, 2);
				}

				formFlexTable.addSectionLabel(33, 0, itemSection, 2);
				formFlexTable.addPanel(34, 0, requisitionItemPanel, 4);
				formFlexTable.addPanel(35, 0, requisitionButtonPanel, 2);

				formFlexTable.addSectionLabel(36, 0, totalsSection, 2);
				formFlexTable.addField(37, 0, amountTextBox, bmoRequisition.getAmount());
				formFlexTable.addField(38, 0, holdBackTextBox, bmoRequisition.getHoldBack());
				formFlexTable.addField(39, 0, discountTextBox, bmoRequisition.getDiscount());
				formFlexTable.addField(40, 0, taxAppliesCheckBox, bmoRequisition.getTaxApplies());
				formFlexTable.addField(41, 0, taxTextBox, bmoRequisition.getTax());
				formFlexTable.addField(42, 0, totalTextBox, bmoRequisition.getTotal());
				formFlexTable.addField(43, 0, paymentsTextBox, bmoRequisition.getPayments());
				formFlexTable.addField(44, 0, balanceTextBox, bmoRequisition.getBalance());

				formFlexTable.addSectionLabel(45, 0, statusSection, 4);
				formFlexTable.addLabelField(46, 0, bmoRequisition.getDeliveryStatus());
				formFlexTable.addLabelField(47, 0, bmoRequisition.getPaymentStatus());
				formFlexTable.addField(48, 0, statusListBox, bmoRequisition.getStatus());	

				reset();
			} else {
				formFlexTable.addField(31, 0, statusListBox, bmoRequisition.getStatus());	
			}
			if (!newRecord) {
				//formFlexTable.addSectionLabel(45, 0, wFlowLogSection, 4);
				// Bitacora
				BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
				FlowPanel WFlowLogFP = new FlowPanel();
				BmFilter filterWFlowLogs = new BmFilter();
				filterWFlowLogs.setValueFilter(bmoWFlowLog.getKind(), bmoWFlowLog.getWFlowId(), bmoRequisition.getWFlowId().toInteger());
				getUiParams().setForceFilter(bmoWFlowLog.getProgramCode(), filterWFlowLogs);
				UiWFlowLog uiWFlowLog = new UiWFlowLog(getUiParams(), WFlowLogFP, bmoRequisition.getWFlowId().toInteger(), -1);
				setUiType(bmoWFlowLog.getProgramCode(), UiParams.MINIMALIST);
				uiWFlowLog.show();
				formFlexTable.addPanel(49, 0, WFlowLogFP, 2);
			}

			if (newRecord)
				calculatePaymentDate();

			formFlexTable.hideSection(detailsSection);
			formFlexTable.hideSection(productSection);

			statusEffect();
		}	

		@Override
		public void postShow() {
			// Asigna el correo default
			if (bmoRequisition.getSupplierId().toInteger() > 0)
				setFormatEmailTo(bmoRequisition.getBmoSupplier().getEmail().toString(), bmoRequisition.getBmoSupplier().getName().toString());

			if (!newRecord)
				buttonPanel.add(lifeCycleShowButton);
			// Mostrar boton cuando NO tenga un recibo(en parcial/total) y tenga un pagos de anticipo
			if (enableCheekingCostTravelExpense(false))
				buttonPanel.add(verifyTravelExpenseShowButton);
			else {
				buttonPanel.add(copyRequisitionDialogButton);
			}

			getBmoOrderType(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toInteger());	
		}

		public void reset() {
			// Elimina columnas del grid
			while (requisitionItemGrid.getColumnCount() > 0)
				requisitionItemGrid.removeColumn(0);

			bmoRequisitionType = (BmoRequisitionType)requisitionTypeListBox.getSelectedBmObject();
			if (bmoRequisitionType == null)
				bmoRequisitionType = bmoRequisition.getBmoRequisitionType();

			// Crea las columnas
			setColumns();

			updateAmount(id);
			data.list();
			requisitionItemGrid.redraw();
		}

		// Forma de dialogo de Comprobacion de Viaticos
		public void showVerifyTravelExpenseDialog() {

			verifyTravelExpenseDialogBox = new DialogBox(true);
			verifyTravelExpenseDialogBox.setGlassEnabled(true);
			verifyTravelExpenseDialogBox.setText("Comprobación de Viáticos.");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			verifyTravelExpenseDialogBox.setWidget(vp);

			UiRequisitionCheekingCostsForm uiRequisitionCheekingCostsForm = new UiRequisitionCheekingCostsForm(getUiParams(), vp, bmoRequisition);
			uiRequisitionCheekingCostsForm.show();

			verifyTravelExpenseDialogBox.center();
			verifyTravelExpenseDialogBox.show();
		}

		// Agrega un concepto de movimiento bancario
		private class UiRequisitionCheekingCostsForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
			private HorizontalPanel buttonPanel = new HorizontalPanel();

			//			private TextBox amountTextBox = new TextBox();
			//			private TextBox parityTextBox = new TextBox();
			//			private TextBox amountConvertedTextBox = new TextBox();
			private BmoRequisition bmoRequisition;
			private BmoBankAccount bmoBankAccount = new BmoBankAccount();

			private Button saveButton = new Button("APLICAR");
			private Button closeButton = new Button("CERRAR");
			UiListBox bankAccountListBox = new UiListBox(getUiParams(), new BmoBankAccount());

			public UiRequisitionCheekingCostsForm(UiParams uiParams, Panel defaultPanel, BmoRequisition bmoRequisition) {
				super(uiParams, defaultPanel);
				this.bmoRequisition = bmoRequisition;

				BmFilter bmFilterByCurrency = new BmFilter();
				bmFilterByCurrency.setValueFilter(bmoBankAccount.getKind(), bmoBankAccount.getCurrencyId().getName(), this.bmoRequisition.getCurrencyId().toInteger());
				bankAccountListBox.addFilter(bmFilterByCurrency);

				saveButton.setStyleName("formSaveButton");
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						prepareSave();
					}
				});
				buttonPanel.add(saveButton);

				closeButton.setStyleName("formCloseButton");
				closeButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						verifyTravelExpenseDialogBox.setVisible(false);
					}
				});
				buttonPanel.add(closeButton);

				defaultPanel.add(formTable);
			}

			@Override
			public void show() {
				formTable.addField(1, 0, bankAccountListBox, bmoBankAccount.getIdField());

				formTable.addButtonPanel(buttonPanel);

				// TODO: Agregar multimoneda
			}

			public void prepareSave() {
				try {
					bmoBankAccount.getIdField().setValue(bankAccountListBox.getSelectedId());
					verifyTravelExpenseDialog();
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
				}
			}

			public void verifyTravelExpenseDialog() {

				String values = "";
				values += this.bmoRequisition.getId() + "|" + this.bmoBankAccount.getId();
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
						else
							showErrorMessage(this.getClass().getName() + "-verifyTravelExpenseDialog() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						if (!result.hasErrors()) {
							stopLoading();
							dialogClose();
							verifyTravelExpenseDialogBox.setVisible(false);
							UiRequisition uiRequisition = new UiRequisition(getUiParams());
							uiRequisition.edit(id);
							showSystemMessage("Comprobación Exitosa.");
						}
						else {
							stopLoading();
							showErrorMessage("<b>Error al Comprobar gastos</b>: " + this.getClass().getName() + "-verifyTravelExpenseDialog() ERROR: "
									+ result.getBmErrorList().get(0).msg + result.getMsg());
						}
					}
				};

				try {	
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoRequisition.getPmClass(), bmoRequisition, BmoRequisition.ACTION_CHEECKINGCOSTS, "" + values, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-copyRequisitionAction() ERROR: " + e.toString());
				}
			}
		}

		public void showLifeCycleDialog() {
			// Es de tipo forma de dialogo
			lifeCycleDialogBox = new DialogBox(true);
			lifeCycleDialogBox.setGlassEnabled(true);
			lifeCycleDialogBox.setText("Seguimiento del Documento");

			// Vertical Panel
			VerticalPanel vp = new VerticalPanel();
			vp.setSize("100%", "100%");
			vp.add(lifeCycleCellTree);
			vp.add(new HTML("&nbsp;"));
			vp.add(lifeCycleCloseButton);

			// Scroll Panel
			ScrollPanel scrollPanel = new ScrollPanel();
			if (getUiParams().isMobile())
				scrollPanel.setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
			else
				scrollPanel.setSize(Window.getClientWidth() * .4 + "px", Window.getClientHeight() * .3 + "px");
			scrollPanel.setWidget(vp);
			lifeCycleDialogBox.setWidget(scrollPanel);

			Double d = Window.getClientWidth() * .3;
			if (!getUiParams().isMobile()) 
				lifeCycleDialogBox.setPopupPosition(d.intValue(), UiTemplate.NORTHSIZE * 3);

			lifeCycleDialogBox.show();
		}

		public void setColumns() {
			// Cantidad		
			Column<BmObject, String> quantityColumn;
			if ((bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION) &&
					bmoRequisitionType.getType().toChar() != BmoRequisitionType.TYPE_CONTRACTESTIMATION)
					|| (enableCheekingCostTravelExpense(false))
					|| (enableItemTypeService(false)) ) {
				quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
					@Override
					public String getValue(BmObject bmObject) {
						return ((BmoRequisitionItem)bmObject).getQuantity().toString();
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
						return ((BmoRequisitionItem)bmObject).getQuantity().toString();
					}
				};
			}
			requisitionItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
			quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			requisitionItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);

			// Recibida
			Column<BmObject, String> quantityReceiptColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRequisitionItem)bmObject).getQuantityReceipt().toString();
				}
			};		
			requisitionItemGrid.addColumn(quantityReceiptColumn, SafeHtmlUtils.fromSafeConstant("Rec."));
			quantityReceiptColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			requisitionItemGrid.setColumnWidth(quantityReceiptColumn, 50, Unit.PX);

			// Clave
			Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRequisitionItem)bmObject).getBmoProduct().getCode().toString();
				}
			};
			requisitionItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
			codeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			requisitionItemGrid.setColumnWidth(codeColumn, 50, Unit.PX);

			// Nombre
			Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return ((BmoRequisitionItem)bmObject).getName().toString();
				}
			};
			requisitionItemGrid.addColumn(prodColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));
			prodColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			requisitionItemGrid.setColumnWidth(prodColumn, 150, Unit.PX);

			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				// Partida Presp.
				Column<BmObject, String> budgetItemColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {				
						return ((BmoRequisitionItem)bmObject).getBmoBudgetItem().getBmoBudgetItemType().getName().toString();
					}
				};
				requisitionItemGrid.addColumn(budgetItemColumn, SafeHtmlUtils.fromSafeConstant("Part. Presp."));
				budgetItemColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				requisitionItemGrid.setColumnWidth(budgetItemColumn, 150, Unit.PX);

				// Departamento
				Column<BmObject, String> areaColumn = new Column<BmObject, String>(new TextCell()) {
					@Override
					public String getValue(BmObject bmObject) {				
						return ((BmoRequisitionItem)bmObject).getBmoArea().getName().toString();
					}
				};
				requisitionItemGrid.addColumn(areaColumn, SafeHtmlUtils.fromSafeConstant("Dpto."));
				areaColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				requisitionItemGrid.setColumnWidth(areaColumn, 150, Unit.PX);
			}

			// Dias
			if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_RENTAL)
					|| bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_SERVICE)
					|| bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)) {
				Column<BmObject, String> daysColumn;
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION) 
						|| (enableCheekingCostTravelExpense(false))
						|| (enableItemTypeService(false)) ) {
					daysColumn = new Column<BmObject, String>(new EditTextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoRequisitionItem)bmObject).getDays().toString();
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
							return ((BmoRequisitionItem)bmObject).getDays().toString();
						}
					};
				}
				requisitionItemGrid.addColumn(daysColumn, SafeHtmlUtils.fromSafeConstant("Días"));
				daysColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				requisitionItemGrid.setColumnWidth(daysColumn, 50, Unit.PX);
			}

			// Precio
			if (!getUiParams().isMobile()) {
				Column<BmObject, String> priceColumn;
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)
						&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGEITEMPRICE)
						|| (enableCheekingCostTravelExpense(true)) 
						|| (enableItemTypeService(true)) ) {
					priceColumn = new Column<BmObject, String>(new EditTextCell()) {
						@Override
						public String getValue(BmObject bmObject) {
							return ((BmoRequisitionItem)bmObject).getPrice().toString();
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
							String formatted = numberFormat.format(((BmoRequisitionItem)bmObject).getPrice().toDouble());
							return (formatted);
						}
					};
				}
				requisitionItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
				priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				requisitionItemGrid.setColumnWidth(priceColumn, 50, Unit.PX);
			}

			// Total
			Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					numberFormat = NumberFormat.getCurrencyFormat();
					String formatted = numberFormat.format(((BmoRequisitionItem)bmObject).getAmount().toDouble());
					return (formatted);
				}
			};
			totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			requisitionItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
			totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			requisitionItemGrid.setColumnWidth(totalColumn, 50, Unit.PX);

			// Eliminar
			Column<BmObject, String> deleteColumn;
			if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)
					|| (enableCheekingCostTravelExpense(false))
					|| (enableItemTypeService(false)) ) {
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
			requisitionItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
			deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			requisitionItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
		}

		public void changeHeight() {
			requisitionItemGrid.setVisibleRange(0, data.getList().size());
		}

		public void setAmount(BmoRequisition bmoRequisition) {
			numberFormat = NumberFormat.getCurrencyFormat();
			amountTextBox.setText(numberFormat.format(bmoRequisition.getAmount().toDouble()));
			taxTextBox.setText(numberFormat.format(bmoRequisition.getTax().toDouble()));
			totalTextBox.setText(numberFormat.format(bmoRequisition.getTotal().toDouble()));
			paymentsTextBox.setText(numberFormat.format(bmoRequisition.getPayments().toDouble()));
			balanceTextBox.setText(numberFormat.format(bmoRequisition.getBalance().toDouble()));
			setAmount("" + bmoRequisition.getAmount().toDouble(), "" + bmoRequisition.getTax().toDouble(), "" + bmoRequisition.getTotal().toDouble());
		}

		private void setAmount(String amount, String tax, String total) {
			double a = Double.parseDouble(amount);
			amountTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(tax);
			taxTextBox.setText(numberFormat.format(a));

			a = Double.parseDouble(total);
			totalTextBox.setText(numberFormat.format(a));
		}

		// Habilitar Items si tienes permiso de Comprobacion de gastos(Viaticos)
		private boolean enableCheekingCostTravelExpense(Boolean changeItemPrice) {
			boolean enableCheekingCostTravelExpense = false;

			if (changeItemPrice) {
				if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)
						&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQTRAVEXP)
						&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGEITEMPRICE)
						&& bmoRequisition.getDeliveryStatus().equals(BmoRequisition.DELIVERYSTATUS_REVISION)
						&& bmoRequisition.getPayments().toDouble() > 0)
					enableCheekingCostTravelExpense = true;
			} else {
				if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)
						&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQTRAVEXP)
						&& bmoRequisition.getDeliveryStatus().equals(BmoRequisition.DELIVERYSTATUS_REVISION)
						&& bmoRequisition.getPayments().toDouble() > 0)
					enableCheekingCostTravelExpense = true;
			}

			return enableCheekingCostTravelExpense;
		}

		// Habilitar items de OC de tipo Servicio, con permiso especial y no tenga pagos 
		private boolean enableItemTypeService(Boolean changeItemPrice) {
			boolean enableItemTypeService = false;
			
			if (changeItemPrice) {
				if (getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQCHSERVICE) 
						&& bmoRequisitionType.getType().toChar() == BmoRequisitionType.TYPE_SERVICE
						&& getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGEITEMPRICE)
						&& bmoRequisition.getPaymentStatus().equals(BmoRequisition.PAYMENTSTATUS_PENDING)
						) 
					enableItemTypeService = true;
			} else {
				if (getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQCHSERVICE) 
						&& bmoRequisitionType.getType().toChar() == BmoRequisitionType.TYPE_SERVICE
						&& bmoRequisition.getPaymentStatus().equals(BmoRequisition.PAYMENTSTATUS_PENDING)
						) 
					enableItemTypeService = true;
			}
			return enableItemTypeService;
		}

		private void statusEffect() {
			requisitionTypeListBox.setEnabled(false);
			supplierSuggestBox.setEnabled(false);			
			descriptionTextArea.setEnabled(false);
			requestDateBox.setEnabled(false);
			paymentDateBox.setEnabled(false);
			deliveryDateBox.setEnabled(false);
			requestedBySuggestBox.setEnabled(false);
			reqPayTypeListBox.setEnabled(false);
			areaListBox.setEnabled(false);
			orderSuggestBox.setEnabled(false);		
			addRequisitionItemButton.setVisible(false);
			amountTextBox.setEnabled(false);
			taxAppliesCheckBox.setEnabled(false);
			companyListBox.setEnabled(false);
			warehouseListBox.setEnabled(false);
			discountTextBox.setEnabled(false);
			holdBackTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
			budgetItemSuggestBox.setEnabled(false);
			contractEstimationListBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);			
			loanListBox.setEnabled(false);
			
			showOnOutFormatCheckBox.setEnabled(false);
			deliveryTimeDateTimeBox.setEnabled(false);
			responsibleSuggestBox.setEnabled(false);
			observationsTextArea.setEnabled(false);
			if (getUiParams().getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_OUTDATA)) {
				showOnOutFormatCheckBox.setEnabled(true);
				if (showOnOutFormatCheckBox.getValue()) {
					deliveryTimeDateTimeBox.setEnabled(true);
					responsibleSuggestBox.setEnabled(true);
					observationsTextArea.setEnabled(true);
				} else {
					deliveryTimeDateTimeBox.setEnabled(false);
					responsibleSuggestBox.setEnabled(false);
					observationsTextArea.setEnabled(false);
					deliveryTimeDateTimeBox.setDateTime("");
					responsibleSuggestBox.setSelectedId(-1);
					responsibleSuggestBox.setText("");					
					observationsTextArea.setText("");
				}				
			} 
			
		
			// Ocultar
			formFlexTable.hideField(loanListBox);
			formFlexTable.hideField(warehouseListBox);
			formFlexTable.hideField(contractEstimationListBox);

			if(bmoOrderType.getType().equals(""+BmoOrderType.TYPE_LEASE) || bmoOrderType.getType().equals(""+BmoOrderType.TYPE_PROPERTY)) {
				formFlexTable.showField(propertySuggestBox);
			} else
				formFlexTable.hideField(propertySuggestBox);

			if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)) {
				supplierSuggestBox.setEnabled(true);
				nameTextBox.setEnabled(true);	
				descriptionTextArea.setEnabled(true);
				requestDateBox.setEnabled(true);
				paymentDateBox.setEnabled(true);
				deliveryDateBox.setEnabled(true);
				requestedBySuggestBox.setEnabled(true);						
				reqPayTypeListBox.setEnabled(true);
				areaListBox.setEnabled(true);

				if (!isSlave())
					orderSuggestBox.setEnabled(true);

				if (newRecord) 
					requisitionTypeListBox.setEnabled(true);
				taxAppliesCheckBox.setEnabled(true);
				requestDateBox.setEnabled(true);

				if (!isSlave())
					companyListBox.setEnabled(true);

				warehouseListBox.setEnabled(true);
				discountTextBox.setEnabled(true);
				holdBackTextBox.setEnabled(true);
				budgetItemSuggestBox.setEnabled(true);
				contractEstimationListBox.setEnabled(false);
				loanListBox.setEnabled(false);
				addRequisitionItemButton.setVisible(true);
				currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(true);
				formFlexTable.hideField(warehouseListBox);
				formFlexTable.hideField(loanListBox);
				formFlexTable.hideField(contractEstimationListBox);

				if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString())
					currencyParityTextBox.setEnabled(true);
				else	
					currencyParityTextBox.setEnabled(false);
			}
			// Si esta el tipo de cuenta
			BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)requisitionTypeListBox.getSelectedBmObject();
			if (bmoRequisitionType == null) 
				bmoRequisitionType = bmoRequisition.getBmoRequisitionType();			

			// Habilitar botones
			if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_COMMISION)) {				
				requestedBySuggestBox.setEnabled(false);
				amountTextBox.setEnabled(true);
				contractEstimationListBox.setEnabled(false);
				loanListBox.setEnabled(false);
			} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) {
				requestedBySuggestBox.setEnabled(true);
				addRequisitionItemButton.setVisible(false);
				amountTextBox.setEnabled(false);
				orderSuggestBox.setEnabled(false);
				contractEstimationListBox.setEnabled(true);
				loanListBox.setEnabled(false);
				formFlexTable.showField(contractEstimationListBox);
			} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CREDIT)) {
				requestedBySuggestBox.setEnabled(false);
				if(bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION))
					addRequisitionItemButton.setVisible(true);
				amountTextBox.setEnabled(false);
				contractEstimationListBox.setEnabled(true);				
				loanListBox.setEnabled(true);					
				formFlexTable.showField(loanListBox);
			} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_PURCHASE)) {
				formFlexTable.showField(warehouseListBox);
			}else {
				requestedBySuggestBox.setEnabled(true);
				if(bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION))
					addRequisitionItemButton.setVisible(true);
			}

			// Si no hay pagos y tiene permiso, se permite cambiar estatus
			if (!newRecord && getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGESTATUS) &&
					(bmoRequisition.getPayments().toDouble() == 0)) {
				statusListBox.setEnabled(true);
			}

			if (!newRecord && getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGEBUDGETITEM)) {
				budgetItemSuggestBox.setEnabled(true);
			}

			// Formatos
			amountTextBox.setEnabled(false);
			taxTextBox.setEnabled(false);
			totalTextBox.setEnabled(false);
			paymentsTextBox.setEnabled(false);
			balanceTextBox.setEnabled(false);
			setAmount(bmoRequisition);


			// Asignar correos default para enviar formatos
			if (supplierSuggestBox.getSelectedBmObject() != null) {
				BmoSupplier bmoSupplier = (BmoSupplier)supplierSuggestBox.getSelectedBmObject();
				setFormatEmailTo(bmoSupplier.getEmail().toString(), bmoSupplier.getName().toString());
			}

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
			else {
				if (bmoRequisition.getOrderId().toInteger() > 0) {
					companyListBox.setEnabled(false);
				}
			}

			if ((bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)
					|| bmoRequisition.getStatus().equals(BmoRequisition.STATUS_REVISION))) {
				currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(true);
			} else if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_CANCELLED)) {
				nameTextBox.setEnabled(false);
				requestedBySuggestBox.setEnabled(false);
				taxAppliesCheckBox.setEnabled(false);
				holdBackTextBox.setEnabled(false);
				discountTextBox.setEnabled(false);
			} else {
				currencyListBox.setEnabled(false);
				currencyParityTextBox.setEnabled(false);
				
				// Validacio permisos de comprobacion de viaticos
				if (enableCheekingCostTravelExpense(false)
					|| (enableItemTypeService(false)) ) {
					addRequisitionItemButton.setVisible(true);
					discountTextBox.setEnabled(true);
					holdBackTextBox.setEnabled(true);
					taxAppliesCheckBox.setEnabled(true);
				}
			}

			
		}

		@Override
		public void formValueChange(String event) {	
			statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {

			if (event.getSource() == reqPayTypeListBox) {			
				calculatePaymentDate();
			} else if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status de la Órden de Compra?");
			} else if (event.getSource() == currencyListBox) {
				getParityFromCurrency(currencyListBox.getSelectedId());

			} else if (event.getSource() == requisitionTypeListBox ) {
				BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)requisitionTypeListBox.getSelectedBmObject();

				if (bmoRequisitionType != null && bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) {
					formFlexTable.showField(contractEstimationListBox);
					populateContractEstimations(supplierSuggestBox.getSelectedId(), Integer.parseInt(companyListBox.getSelectedId()));	
					formFlexTable.hideField(warehouseListBox);
				} else if (bmoRequisitionType != null && bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CREDIT)) {	
					formFlexTable.showField(loanListBox);
					populateLoans(Integer.parseInt(companyListBox.getSelectedId()));
					formFlexTable.hideField(warehouseListBox);
					formFlexTable.hideField(contractEstimationListBox);
				} else if (bmoRequisitionType != null && bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_PURCHASE)) {
					formFlexTable.hideField(loanListBox);
					formFlexTable.hideField(contractEstimationListBox);
					formFlexTable.showField(warehouseListBox);					
					if (!isSlave())
						populateOrderSuggestBoxFilters(bmoRequisition.getCompanyId().toInteger());					
				} else {
					if (!isSlave()) {
						if (!(bmoRequisition.getOrderId().toInteger() > 0))
							populateOrderSuggestBoxFilters(bmoRequisition.getCompanyId().toInteger());
					}	

					formFlexTable.hideField(warehouseListBox);
				}

				if (bmoRequisitionType.getStock().toBoolean()) 							
					populateWarehouse(Integer.parseInt(companyListBox.getSelectedId()));

			} else if (event.getSource() == companyListBox) {			
				BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)requisitionTypeListBox.getSelectedBmObject();

				if (bmoRequisitionType.getStock().toBoolean()) 
					populateWarehouse(Integer.parseInt(companyListBox.getSelectedId()));


				if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CREDIT)) 
					populateLoans(Integer.parseInt(companyListBox.getSelectedId()));

				// De tipo estimacion, limpiar combo estimaciones si cambian la empresa para evitar 
				// que la oc y la estimacion(contrato) tengan diferente empresa
				if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) 
					populateContractEstimations(supplierSuggestBox.getSelectedId(), Integer.parseInt(companyListBox.getSelectedId()));

				if (!isSlave()) {
					populateOrderSuggestBoxFilters(Integer.parseInt(companyListBox.getSelectedId()));
				}
				populateproperty(companyListBox.getSelectedId());

			} 

			statusEffect();
		}

		// Accion cambio SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de requisiones
			if (uiSuggestBox == supplierSuggestBox) {
				BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)requisitionTypeListBox.getSelectedBmObject();
				if (bmoRequisitionType == null)
					bmoRequisitionType = bmoRequisition.getBmoRequisitionType();

				if (bmoRequisitionType != null && bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) {
					populateContractEstimations(supplierSuggestBox.getSelectedId(), Integer.parseInt(companyListBox.getSelectedId()));				
				} else if (bmoRequisitionType != null && bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CREDIT)) {					
					populateLoans(Integer.parseInt(companyListBox.getSelectedId()));
				} 
			} else if(uiSuggestBox == requestedBySuggestBox) {
				populateArea(requestedBySuggestBox.getSelectedId());
			} else if (uiSuggestBox == requisitionSuggestBox) {
				if (requisitionSuggestBox.getSelectedBmObject() != null)
					changeRequisition((BmoRequisition)requisitionSuggestBox.getSelectedBmObject());
			}




			statusEffect();
		}

		// Establece filtros del suggest box de pedidos
		private void populateOrderSuggestBoxFilters(int companyId) {
			orderSuggestBox.clear();
			setOrderSuggestBoxFilters(companyId);
		}

		// Establece filtros pedidos
		private void setOrderSuggestBoxFilters(int companyId) {
			if (companyId > 0) {
				if(getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_ENABLEORDERTOFINISHEDSALE)) {

					BmFilter filterByNotCancelled = new BmFilter();
					filterByNotCancelled.setValueOperatorFilter(bmoOrder.getKind(), bmoOrder.getStatus(), BmFilter.NOTEQUALS, "" + BmoOrder.STATUS_CANCELLED);
					orderSuggestBox.addFilter(filterByNotCancelled);									

					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoOrder.getKind(), bmoOrder.getCompanyId(), companyId);
					orderSuggestBox.addFilter(filterByCompany);

				}else {

					BmFilter filterByNotFinished = new BmFilter();
					filterByNotFinished.setValueOperatorFilter(bmoOrder.getKind(), bmoOrder.getStatus(), BmFilter.NOTEQUALS, "" + BmoOrder.STATUS_FINISHED);
					orderSuggestBox.addFilter(filterByNotFinished);

					BmFilter filterByNotCancelled = new BmFilter();
					filterByNotCancelled.setValueOperatorFilter(bmoOrder.getKind(), bmoOrder.getStatus(), BmFilter.NOTEQUALS, "" + BmoOrder.STATUS_CANCELLED);
					orderSuggestBox.addFilter(filterByNotCancelled);						

					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoOrder.getKind(), bmoOrder.getCompanyId(), companyId);
					orderSuggestBox.addFilter(filterByCompany);

				}

			} else {
				BmFilter filterByCompany = new BmFilter();
				filterByCompany.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdField(), "-1");
				orderSuggestBox.addFilter(filterByCompany);

			}
		}

		public void setFilterProperty(String companyId) {
			BmoProperty bmoProperty = new BmoProperty();
			BmFilter bmFilterByCompany = new BmFilter();			
			bmFilterByCompany.setValueFilter(bmoProperty.getKind(), bmoProperty.getCompanyId(), companyId);			
			propertySuggestBox.addFilter(bmFilterByCompany);
		}

		public void showAutorizedData() {
			if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
				if (bmoRequisition.getAuthorizedUser().toInteger() > 0)
					getUserAutorized(bmoRequisition.getAuthorizedUser().toInteger());
			}
		}

		// Actualiza combo de almacenes
		private void populateWarehouse(int companyId) {
			warehouseListBox.clear();
			warehouseListBox.clearFilters();
			setWarehouseListBoxFilters(companyId);
			warehouseListBox.populate(bmoRequisition.getWarehouseId());
		}

		private void populateproperty(String companyId) {
			propertySuggestBox.clear();
			setFilterProperty(companyId);
		}

		// Asigna filtros al listado de almacenes
		private void setWarehouseListBoxFilters(int companyId) {
			BmoWarehouse bmoWarehouse = new BmoWarehouse();
			BmFilter bmFilterByCompany = new BmFilter();
			bmFilterByCompany.setValueFilter(bmoWarehouse.getKind(), bmoWarehouse.getCompanyId(), companyId);
			warehouseListBox.addBmFilter(bmFilterByCompany);		
		}

		// Actualiza listado de estimaciones
		private void populateContractEstimations(int supplierId, int companyId) {
			contractEstimationListBox.clear();
			contractEstimationListBox.clearFilters();
			setContractEstimationFilters(supplierId, companyId);
			contractEstimationListBox.populate(bmoRequisition.getContractEstimationId());
		}

		// Asigna filtros de listado de estimaciones
		private void setContractEstimationFilters(int supplierId, int companyId) {
			BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();
			if (supplierId > 0) {
				BmFilter bmFilterCoesStatus = new BmFilter();
				BmFilter bmFilterCoesPaymentStatus = new BmFilter();
				BmFilter bmFilterCoesBySupplier = new BmFilter();

				// Se rehacen filtros
				bmFilterCoesStatus.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getStatus(), "" + BmoContractEstimation.STATUS_AUTHORIZED);
				bmFilterCoesPaymentStatus.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getPaymentStatus().getName(), "" + BmoContractEstimation.PAYMENTSTATUS_PENDING);
				bmFilterCoesBySupplier.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getBmoWorkContract().getSupplierId(), supplierId);

				contractEstimationListBox.addBmFilter(bmFilterCoesStatus);
				contractEstimationListBox.addBmFilter(bmFilterCoesPaymentStatus);
				contractEstimationListBox.addBmFilter(bmFilterCoesBySupplier);

				if (companyId > 0) {
					BmFilter bmFilterCoesCompany = new BmFilter();
					bmFilterCoesCompany.setValueFilter(bmoContractEstimation.getKind(), 
							bmoContractEstimation.getBmoWorkContract().getCompanyId(), companyId);
					contractEstimationListBox.addBmFilter(bmFilterCoesCompany);
				}

			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoContractEstimation.getKind(), bmoContractEstimation.getIdField(), bmoRequisition.getContractEstimationId().toInteger());
				contractEstimationListBox.addBmFilter(bmFilter);
			}
		}

		// Llena combo creditos
		private void populateLoans(int companyId) {
			loanListBox.clear();
			loanListBox.clearFilters();
			setLoanFilters(companyId);
			loanListBox.populate(bmoRequisition.getLoanId());
		}

		// Asigna filtros de los creditos
		private void setLoanFilters(int companyId) {
			BmoLoan bmoLoan = new BmoLoan();
			if (companyId > 0) {

				BmFilter bmFilterCompany = new BmFilter();
				bmFilterCompany.setValueFilter(bmoLoan.getKind(), bmoLoan.getCompanyId(), companyId);
				loanListBox.addBmFilter(bmFilterCompany);

			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoLoan.getKind(), bmoLoan.getIdField(), bmoRequisition.getLoanId().toInteger());
				loanListBox.addBmFilter(bmFilter);
			}
		}

		// Actualiza combo de partidas presp. (AUN NO: por empresa)

		//		private void populateBudgetItems() {
		//			budgetItemSuggestBox.clear();
		//			setBudgetItemsSuggestBoxFilters();
		//		}

		// Asigna filtros al listado de partidas presp. (AUN NO: por empresa)
		private void setBudgetItemsSuggestBoxFilters() {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

			//			if (companyId > 0) {
			//				BmFilter bmFilterByCompany = new BmFilter();
			//				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
			//				budgetItemSuggestBox.addFilter(bmFilterByCompany);
			//				
			//				// Filtro de egresos(cargo)
			//				BmFilter filterByWithDraw = new BmFilter();
			//				filterByWithDraw.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_WITHDRAW);
			//				budgetItemSuggestBox.addFilter(filterByWithDraw);
			//				
			//			} else {
			// Filtro de egresos(cargo)
			BmFilter filterByWithDraw = new BmFilter();
			filterByWithDraw.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_WITHDRAW);
			budgetItemSuggestBox.addFilter(filterByWithDraw);

			//				BmFilter bmFilter = new BmFilter();
			//				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
			//				budgetItemSuggestBox.addFilter(bmFilter);				
			//			}
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == deliveryDateBox)
				calculatePaymentDate();
			else if (event.getSource() == requestDateBox) {
				getParityFromCurrency(currencyListBox.getSelectedId());
			}
		}

		// Cambia la OC
		private void changeRequisition(BmoRequisition newBmoRequisition) {
			if (newBmoRequisition.getId() != bmoRequisition.getId()) {
				formDialogBox.hide();
				edit(newBmoRequisition);
			}
		}

		// Llena combo areas
		private void populateArea(int userId) {
			BmoUser bmoUser = new BmoUser();
			bmoUser = (BmoUser)requestedBySuggestBox.getSelectedBmObject();
			areaListBox.setSelectedId(bmoUser.getAreaId().toString());
		}

		private void calculatePaymentDate() {		
			// Calcular fecha de pago
			BmoReqPayType bmoReqPayType = (BmoReqPayType)reqPayTypeListBox.getSelectedBmObject();
			if (bmoReqPayType == null && bmoRequisition.getReqPayTypeId().toInteger() > 0) {
				bmoReqPayType = bmoRequisition.getBmoReqPayType();
			}
			if (bmoReqPayType != null) {
				if (!deliveryDateBox.getTextBox().getValue().equals("")) {
					Date dueDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).parse(deliveryDateBox.getTextBox().getValue());
					CalendarUtil.addDaysToDate(dueDate, bmoReqPayType.getDays().toInteger());
					paymentDateBox.getDatePicker().setValue(dueDate);
					paymentDateBox.getTextBox().setValue(GwtUtil.dateToString(dueDate, getSFParams().getDateFormat()));
				}
			} else reqPayTypeListBox.setSelectedId("-1");				
		}

		public void getParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId, 0);
		}

		public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
			if (parityFromCurrencyRpcAttempt < 5) {
				setCurrencyId(currencyId);
				setParityFromCurrencyRpcAttempt(parityFromCurrencyRpcAttempt + 1);

				BmoCurrency bmoCurrency = new BmoCurrency();
				String startDate = requestDateBox.getTextBox().getText();

				if (requestDateBox.getTextBox().getText().equals("")) {
					startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
				}
				String actionValues = currencyId + "|" + startDate;

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {

						stopLoading();
						if (getParityFromCurrencyRpcAttempt() < 5) 
							getParityFromCurrency(getCurrencyId(), getParityFromCurrencyRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {				
						stopLoading();	
						setParityFromCurrencyRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Tipo de Cambio.");
						else {
							currencyParityTextBox.setValue(result.getMsg());
						}
					}
				};

				try {	
					if (!isLoading()) {				
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoCurrency.getPmClass(), bmoCurrency, BmoCurrency.ACTION_GETCURRENCYPARITY, "" + actionValues, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + e.toString());
				}
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRequisition.setId(id);

			bmoRequisition.getName().setValue(nameTextBox.getText());	
			bmoRequisition.getAreaId().setValue(areaListBox.getSelectedId());
			bmoRequisition.getDescription().setValue(descriptionTextArea.getText());
			bmoRequisition.getDeliveryDate().setValue(deliveryDateBox.getTextBox().getText());
			bmoRequisition.getRequestDate().setValue(requestDateBox.getTextBox().getText());
			bmoRequisition.getPaymentDate().setValue(paymentDateBox.getTextBox().getText());
			bmoRequisition.getRequestedBy().setValue(requestedBySuggestBox.getSelectedId());		

			if (discountTextBox.getText().equals("")) bmoRequisition.getDiscount().setValue(0);
			else bmoRequisition.getDiscount().setValue(discountTextBox.getText());

			if (holdBackTextBox.getText().equals("")) bmoRequisition.getHoldBack().setValue(0);
			else bmoRequisition.getHoldBack().setValue(holdBackTextBox.getText());

			if (amountTextBox.getText().equals("") && 
					requisitionTypeListBox.getSelectedCode().toString().charAt(0) != BmoRequisitionType.TYPE_COMMISION) {

				bmoRequisition.getAmount().setValue(0);
				bmoRequisition.getTotal().setValue(0);
				bmoRequisition.getPayments().setValue(0);
				bmoRequisition.getBalance().setValue(0);
			} else {
				bmoRequisition.getAmount().setValue(amountTextBox.getText());
				bmoRequisition.getTotal().setValue(amountTextBox.getText());
				bmoRequisition.getPayments().setValue(paymentsTextBox.getText());
				bmoRequisition.getBalance().setValue(balanceTextBox.getText());
			}

			bmoRequisition.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
			bmoRequisition.getTax().setValue(0);

			bmoRequisition.getReqPayTypeId().setValue(reqPayTypeListBox.getSelectedId());
			bmoRequisition.getSupplierId().setValue(supplierSuggestBox.getSelectedId());
			bmoRequisition.getWarehouseId().setValue(warehouseListBox.getSelectedId());
			bmoRequisition.getOrderId().setValue(orderSuggestBox.getSelectedId());
			bmoRequisition.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoRequisition.getStatus().setValue(statusListBox.getSelectedCode());
			bmoRequisition.getContractEstimationId().setValue(contractEstimationListBox.getSelectedId());
			bmoRequisition.getRequisitionTypeId().setValue(requisitionTypeListBox.getSelectedId());
			bmoRequisition.getBudgetItemId().setValue(budgetItemSuggestBox.getSelectedId());
			bmoRequisition.getLoanId().setValue(loanListBox.getSelectedId());
			bmoRequisition.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoRequisition.getCurrencyParity().setValue(currencyParityTextBox.getText());		
			bmoRequisition.getPropertyId().setValue(propertySuggestBox.getSelectedId());
			//Campos drea
			bmoRequisition.getShowOnOutFormat().setValue(showOnOutFormatCheckBox.getValue());
			bmoRequisition.getDeliveryTime().setValue(deliveryTimeDateTimeBox.getDateTime());
			bmoRequisition.getResponsibleId().setValue(responsibleSuggestBox.getSelectedId());
			bmoRequisition.getObservations().setValue(observationsTextArea.getText());
			
			return bmoRequisition;
		}

		@Override
		public void close() {
			//showSystemMessage("tipo-close: "+ getUiParams().getUiType(getBmObject().getProgramCode()) + " |ob;"+getBmObject().getProgramCode());

			//			if (isSlave()) {
			//				//
			//			} else {
			//				if (getBmObject().getId() > 0) {
			////					UiRequisitionForm UiRequisitionForm = new UiRequisitionForm(getUiParams(), getBmObject().getId());
			////					UiRequisitionForm.show();
			//				} else {
			//					list();
			//				}
			//			}
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				UiRequisitionForm uiRequisitionForm = new UiRequisitionForm(getUiParams(), getBmObject().getId());
				uiRequisitionForm.show();
			} else {
				list();
			}		
		}

		public void discountChange() {
			try {
				bmoRequisition.getDiscount().setValue(discountTextBox.getText());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-discountChange() ERROR: " + e.toString());
			}
		}

		public void holdBackChange() {
			try {
				bmoRequisition.getHoldBack().setValue(holdBackTextBox.getText());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-holdBackChange() ERROR: " + e.toString());
			}
		}

		public void taxAppliesChange() {
			try {
				bmoRequisition.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
			}
		}

		public void updateAmount(int idReqiRpc) {
			updateAmount(idReqiRpc, 0);
		}

		public void updateAmount(int idReqiRpc, int idRpcAttempt) {
			if (idRpcAttempt < 5) {
				setIdReqiRpc(idReqiRpc);
				setIdRpcAttempt(idRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getIdRpcAttempt() < 5)
							updateAmount(getIdReqiRpc(), getIdRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setIdRpcAttempt(0);
						setAmount((BmoRequisition)result);
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoRequisition.getPmClass(), idReqiRpc, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
				}
			}
		}

		public void getUserAutorized(int idUserRpc) {
			getUserAutorized(idUserRpc, 0);
		}
		public void getUserAutorized(int idUserRpc, int userAutorizedRpcAttempt) {
			BmoUser bmoUser = new BmoUser();
			if (userAutorizedRpcAttempt < 5) {
				setIdUserRpc(idUserRpc);
				setUserAutorizedRpcAttempt(userAutorizedRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUserAutorizedRpcAttempt() < 5)
							getUserAutorized(getIdUserRpc(), getUserAutorizedRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getUserAutorized() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setUserAutorizedRpcAttempt(0);
						setBmObject((BmObject)result);
						BmoUser bmoUser = (BmoUser)getBmObject();
						formFlexTable.addLabelField(29, 0, "Autorizo:", bmoUser.getCode().toString());
						formFlexTable.addLabelField(30, 0, bmoRequisition.getAuthorizedDate());
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoUser.getPmClass(), idUserRpc, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getUserAutorized(): ERROR " + e.toString());
				}
			}
		}

		public void saveAmountChange() {
			saveAmountChange(0);
		}

		public void saveAmountChange(int saveAmountChangeRpcAttempt) {
			if (saveAmountChangeRpcAttempt < 5) {
				setSaveAmountChangeRpcAttempt(saveAmountChangeRpcAttempt + 1);

				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getSaveAmountChangeRpcAttempt() < 5)
							saveAmountChange(getSaveAmountChangeRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-saveAmountChange(): ERROR " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setSaveAmountChangeRpcAttempt(0);
						processRequisitionUpdateResult(result);
					}
				};

				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().save(bmoRequisition.getPmClass(), bmoRequisition, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveAmountChange(): ERROR " + e.toString());
				}
			}
		}

		public void processRequisitionUpdateResult(BmUpdateResult result) {
			if (result.hasErrors()) showFormMsg("Errores al actualizar montos.", "Errores al actualizar montos: " + result.errorsToString());
			else updateAmount(id);
		}

		private void changeQuantity(BmObject bmObject, String quantity) {
			bmoRequisitionItem = (BmoRequisitionItem)bmObject;
			try {
				double q = Double.parseDouble(quantity);			
				if (q > 0) {
					bmoRequisitionItem.getQuantity().setValue(quantity);
					saveItemChange();
				} else {
					// Eliminar registro
					deleteItem();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			}
		}

		private void deleteItem(BmObject bmObject) {
			bmoRequisitionItem = (BmoRequisitionItem)bmObject;
			deleteItem();
		}

		private void changeDays(BmObject bmObject, String quantity) {
			bmoRequisitionItem = (BmoRequisitionItem)bmObject;
			try {
				double q = Double.parseDouble(quantity);
				bmoRequisitionItem.getDays().setValue(q);
				saveItemChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
			}
		}

		public void changePrice(BmObject bmObject, String price) {
			bmoRequisitionItem = (BmoRequisitionItem)bmObject;
			try {
				double p = Double.parseDouble(price);
				if (p >= 0) {
					bmoRequisitionItem.getPrice().setValue(price);
					saveItemChange();
				} else {
					showSystemMessage("El Precio no puede ser menor a $0.00");
					reset();
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
			}
		}

		public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors())
				showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
			this.reset();
		}

		public void processItemDelete(BmUpdateResult result) {
			this.reset();
		}

		public void saveItemChange() {
			saveItemChange(0);
		}

		public void saveItemChange(int saveItemChangeRpcAttempt) {
			if (saveItemChangeRpcAttempt < 5) {
				setSaveItemChangeRpcAttempt(saveItemChangeRpcAttempt + 1);

				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getSaveItemChangeRpcAttempt() < 5)
							saveItemChange(getSaveItemChangeRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setSaveItemChangeRpcAttempt(0);
						processItemChangeSave(result);
					}
				};

				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().save(bmoRequisitionItem.getPmClass(), bmoRequisitionItem, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
				}
			}
		}

		public void deleteItem() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processItemDelete(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().delete(bmoRequisitionItem.getPmClass(), bmoRequisitionItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
			}
		}

		public void addProduct() {
			addProduct(new BmoProduct());
		}

		public void addProduct(BmoProduct bmoProduct) {
			requisitionItemDialogBox = new DialogBox(true);
			requisitionItemDialogBox.setGlassEnabled(true);
			requisitionItemDialogBox.setText("Item de Orden de Compra");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "200px");

			requisitionItemDialogBox.setWidget(vp);

			UiRequisitionItemForm requisitionItemForm = new UiRequisitionItemForm(getUiParams(), vp, bmoRequisition.getId(), bmoProduct);

			requisitionItemForm.show();

			requisitionItemDialogBox.center();
			requisitionItemDialogBox.show();
		}

		// Agrega un item de un producto a la orden de compra
		private class UiRequisitionItemForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
			private TextBox nameTextBox = new TextBox();
			private TextBox quantityTextBox = new TextBox();
			private TextBox daysTextBox = new TextBox();
			private TextBox priceTextBox = new TextBox();
			private BmoRequisitionItem bmoRequisitionItem;
			private Button saveButton = new Button("AGREGAR");
			private HorizontalPanel buttonPanel = new HorizontalPanel();
			private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
			private UiSuggestBox budgetItemUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
			private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
			private BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
			private int nameCostProductRpcAttempt = 0;

			//		private BmoProduct bmoProduct;
			String productId = "";
			int requisitionId;

			public UiRequisitionItemForm(UiParams uiParams, Panel defaultPanel, int requisitionId, BmoProduct bmoProduct) {
				super(uiParams, defaultPanel);
				this.bmoRequisitionItem = new BmoRequisitionItem();
				//			this.bmoProduct = bmoProduct;
				this.requisitionId = requisitionId;

				// Filtro de egresos(cargo)
				BmFilter filterByWithdraw = new BmFilter();
				filterByWithdraw.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_WITHDRAW);
				budgetItemUiSuggestBox.addFilter(filterByWithdraw);

				try {
					bmoRequisitionItem.getRequisitionId().setValue(requisitionId);
					bmoRequisitionItem.getProductId().setValue(bmoProduct.getId());
					bmoRequisitionItem.getQuantity().setValue(1);
					bmoRequisitionItem.getDays().setValue(1);
					if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
						if (bmoRequisition.getBudgetItemId().toInteger() > 0)
							bmoRequisitionItem.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());
						if (bmoRequisition.getAreaId().toInteger() > 0)
							bmoRequisitionItem.getAreaId().setValue(bmoRequisition.getAreaId().toInteger());
					}
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
				}

				saveButton.setStyleName("formSaveButton");
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						prepareSave();
					}
				});
				saveButton.setEnabled(false);
				if (getSFParams().hasWrite(bmoRequisition.getProgramCode())) saveButton.setEnabled(true);
				buttonPanel.add(saveButton);

				// Manejo de acciones de suggest box
				UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
					@Override
					public void onSelect(UiSuggestBox uiSuggestBox) {
						formSuggestionChange(uiSuggestBox);
					}
				};
				formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

				productSuggestBox = new UiSuggestBox(new BmoProduct());

				//filtro para mostrar los productos que estan habilitados			
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterByEnabled = new BmFilter();
				filterByEnabled.setValueFilter(bmoRequisitionItem.getBmoProduct().getKind(), bmoRequisitionItem.getBmoProduct().getEnabled(), "1");
				filterList.add(filterByEnabled);
				productSuggestBox.addFilter(filterByEnabled);
				// Filtro, si el tipo de oc NO afecta almacen, QUITAR productos que afectan
				if (!bmoRequisition.getBmoRequisitionType().getStock().toBoolean()) {
					BmFilter filterInfluenceStock = new BmFilter();
					filterInfluenceStock.setValueOperatorFilter(bmoRequisitionItem.getBmoProduct().getKind(), 
							bmoRequisitionItem.getBmoProduct().getInventory(), BmFilter.NOTEQUALS, "1");
					productSuggestBox.addFilter(filterInfluenceStock);
				} 

				defaultPanel.add(formTable);
			}

			@Override
			public void show() {		
				formTable.addField(1, 0, productSuggestBox, bmoRequisitionItem.getProductId());
				formTable.addField(2, 0, nameTextBox, bmoRequisitionItem.getName());
				formTable.addField(3, 0, quantityTextBox, bmoRequisitionItem.getQuantity());
				if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_RENTAL)
						|| bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_SERVICE)
						|| bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE))
					formTable.addField(4, 0, daysTextBox, bmoRequisitionItem.getDays());
				formTable.addField(5, 0, priceTextBox, bmoRequisitionItem.getPrice());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
					formTable.addField(6, 0, budgetItemUiSuggestBox, bmoRequisitionItem.getBudgetItemId());
					formTable.addField(7, 0, areaUiListBox, bmoRequisitionItem.getAreaId());
				}
				formTable.addButtonPanel(buttonPanel);

				statusEffect();
			}

			public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
				statusEffect();
			}

			private void statusEffect() {
				if (productSuggestBox.getSelectedId() > 0) {
					productId = "" + productSuggestBox.getSelectedId();
					nameTextBox.setText("");
					nameTextBox.setEnabled(false);
					priceTextBox.setText("");
					priceTextBox.setEnabled(false);

					getNameCostProduct();

					if (getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGEITEMPRICE))
						priceTextBox.setEnabled(true);

				} else {
					nameTextBox.setText("");
					nameTextBox.setEnabled(true);
					priceTextBox.setText("");
					priceTextBox.setEnabled(true);
				}
			}

			//Obtener Nombre y Costo del Producto
			public void getNameCostProduct() {
				getNameCostProduct(0);
			}

			//Obtener Nombre y Costo del Producto
			public void getNameCostProduct(int nameCostProductRpcAttempt) {
				if (nameCostProductRpcAttempt < 5) {
					setNameCostProductRpcAttempt(nameCostProductRpcAttempt + 1);

					AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
						@Override
						public void onFailure(Throwable caught) {
							stopLoading();
							if (getNameCostProductRpcAttempt() < 5)
								getNameCostProduct(getNameCostProductRpcAttempt());
							showErrorMessage(this.getClass().getName() + "-getNameCostProduct() ERROR: " + caught.toString());
						}

						@Override
						public void onSuccess(BmUpdateResult result) {
							stopLoading();
							setNameCostProductRpcAttempt(0);
							BmoProduct bmoProduct = (BmoProduct)result.getBmObject();
							nameTextBox.setText("" + bmoProduct.getName().toString());

							if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_RENTAL))
								priceTextBox.setText("" + bmoProduct.getRentalCost().toDouble());	
							else 
								priceTextBox.setText("" + bmoProduct.getCost().toDouble());	
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
						showErrorMessage(this.getClass().getName() + "-getNameCostProduct() ERROR: " + e.toString());
					}
				}
			}

			public void processUpdateResult(BmUpdateResult bmUpdateResult) {
				if (bmUpdateResult.hasErrors()) 
					showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
				else {
					requisitionItemDialogBox.hide();
					reset();
				}
			}

			public void prepareSave() {
				try {
					bmoRequisitionItem = new BmoRequisitionItem();
					bmoRequisitionItem.getRequisitionId().setValue(requisitionId);
					bmoRequisitionItem.getProductId().setValue(productSuggestBox.getSelectedId());
					bmoRequisitionItem.getName().setValue(nameTextBox.getText());
					bmoRequisitionItem.getQuantity().setValue(quantityTextBox.getText());
					bmoRequisitionItem.getDays().setValue(daysTextBox.getText());
					bmoRequisitionItem.getPrice().setValue(priceTextBox.getText());
					if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
						bmoRequisitionItem.getBudgetItemId().setValue(budgetItemUiSuggestBox.getSelectedId());
						bmoRequisitionItem.getAreaId().setValue(areaUiListBox.getSelectedId()); 
					}

					save();
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
				}
			}

			public void save() {
				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						processUpdateResult(result);
					}
				};

				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().save(bmoRequisitionItem.getPmClass(), bmoRequisitionItem, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
				}
			}

			// Variables para llamadas RPC
			public int getNameCostProductRpcAttempt() {
				return nameCostProductRpcAttempt;
			}

			public void setNameCostProductRpcAttempt(int nameCostProductRpcAttempt) {
				this.nameCostProductRpcAttempt = nameCostProductRpcAttempt;
			}

		}

		public void getBmoOrderType(int defaultOrderTypeId) {
			getBmoOrderType(defaultOrderTypeId, 0);
		}

		public void getBmoOrderType(int defaultOrderTypeId, int bmoOrderTypeRpcAttempt) {
			if (bmoOrderTypeRpcAttempt < 5) {
				setDefaultOrderTypeId(defaultOrderTypeId);
				setBmoOrderTypeRpcAttempt(bmoOrderTypeRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getBmoOrderTypeRpcAttempt() < 5)
							getBmoOrderType(getDefaultOrderTypeId(), getBmoOrderTypeRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getBmoOrderType() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setBmoOrderTypeRpcAttempt(0);
						bmoOrderType = ((BmoOrderType)result);
						if(bmoOrderType.getType().equals(""+BmoOrderType.TYPE_LEASE) || bmoOrderType.getType().equals(""+BmoOrderType.TYPE_PROPERTY)) {
							formFlexTable.showField(propertySuggestBox);
						}
					}
				};
				try {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoOrderType.getPmClass(), defaultOrderTypeId, callback);

				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getBmoOrderType() ERROR: " + e.toString());
				}
			}
		}

		private void getOrderProperty(int orderId) {
			getOrderProperty(0, orderId);
		}

		private void getOrderProperty(int getPropertyRpcAttempt, int orderId) {
			if (getPropertyRpcAttempt < 5) {
				setPropertyRpcAttempt(getPropertyRpcAttempt + 1);
				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getPropertyRpcAttempt() < 5) getOrderProperty(getPropertyRpcAttempt());
						else showErrorMessage(this.getClass().getName() + "-getOrderProperty() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setPropertyRpcAttempt(0);
						if (result.hasErrors()) showSystemMessage("Error al obtener el inmueble: " + result.errorsToString());
						else {

							BmoProperty bmoProperty = (BmoProperty) result.getBmObject();
							try {
								bmoRequisition.getPropertyId().setValue(bmoProperty.getId());
								formFlexTable.addField(13, 0, propertySuggestBox, bmoRequisition.getPropertyId());
							} catch (BmException e) {
								e.printStackTrace();
							}
						}
					}
				};

				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoRequisition.getPmClass(),bmoRequisition,
								BmoRequisition.ACTION_GETORDERPROPERTY,""+orderId,callback);
					} else { showSystemMessage("Está cargando..."); }
				} catch (SFException e) {
					showErrorMessage(this.getClass().getName() + "-getOrderProperty() ERROR: " + e.toString());
				}
			}
		}

		private void getOrderPropertyTax(int orderId) {
			getOrderPropertyTax(0, orderId);
		}

		private void getOrderPropertyTax(int getPropertyRpcAttempt, int orderId) {
			if (getPropertyRpcAttempt < 5) {
				setPropertyRpcAttempt(getPropertyRpcAttempt + 1);
				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getPropertyRpcAttempt() < 5) getOrderPropertyTax(getPropertyRpcAttempt());
						else showErrorMessage(this.getClass().getName() + "-getOrderPropertyTax() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setPropertyRpcAttempt(0);
						if (result.hasErrors()) showSystemMessage("Error al optener el inmueble: " + result.errorsToString());
						else {
							BmoProperty bmoProperty = (BmoProperty) result.getBmObject();
							try {
								bmoRequisition.getPropertyId().setValue(bmoProperty.getId());
								formFlexTable.addField(13, 0, propertySuggestBox, bmoRequisition.getPropertyId());
							} catch (BmException e) {
								showSystemMessage(this.getClass().getName() + "-getOrderPropertyTax() ERROR: El inmueble no se encontro :" + e.toString());
							}
						}
					}
				};

				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoRequisition.getPmClass(),bmoRequisition,
								BmoRequisition.ACTION_GETORDERPROPERTYTAX,""+orderId,callback);
					} else { showSystemMessage("esta cargando "); }
				} catch (SFException e) {
					showErrorMessage(this.getClass().getName() + "-hasRenewOrder() ERROR: " + e.toString());
				}
			}
		}

		// Variables para llamadas RPC
		public int getIdRpcAttempt() {
			return idRpcAttempt;
		}

		public void setIdRpcAttempt(int idRpcAttempt) {
			this.idRpcAttempt = idRpcAttempt;
		}

		public int getIdReqiRpc() {
			return idReqiRpc;
		}

		public void setIdReqiRpc(int idReqiRpc) {
			this.idReqiRpc = idReqiRpc;
		}

		public int getPropertyRpcAttempt() {
			return propertyRpcAttempt;
		}

		public void setPropertyRpcAttempt(int propertyRpcAttempt) {
			this.propertyRpcAttempt = propertyRpcAttempt;
		}

		public int getParityFromCurrencyRpcAttempt() {
			return parityFromCurrencyRpcAttempt;
		}

		public void setParityFromCurrencyRpcAttempt(int parityFromCurrencyRpcAttempt) {
			this.parityFromCurrencyRpcAttempt = parityFromCurrencyRpcAttempt;
		}

		public String getCurrencyId() {
			return currencyId;
		}

		public void setCurrencyId(String currencyId) {
			this.currencyId = currencyId;
		}

		public int getIdUserRpc() {
			return idUserRpc;
		}

		public void setIdUserRpc(int idUserRpc) {
			this.idUserRpc = idUserRpc;
		}

		public int getUserAutorizedRpcAttempt() {
			return userAutorizedRpcAttempt;
		}

		public void setUserAutorizedRpcAttempt(int userAutorizedRpcAttempt) {
			this.userAutorizedRpcAttempt = userAutorizedRpcAttempt;
		}

		public int getSaveAmountChangeRpcAttempt() {
			return saveAmountChangeRpcAttempt;
		}

		public void setSaveAmountChangeRpcAttempt(int saveAmountChangeRpcAttempt) {
			this.saveAmountChangeRpcAttempt = saveAmountChangeRpcAttempt;
		}

		public int getDefaultOrderTypeId() {
			return defaultOrderTypeId;
		}

		public void setDefaultOrderTypeId(int defaultOrderTypeId) {
			this.defaultOrderTypeId = defaultOrderTypeId;
		}

		public int getBmoOrderTypeRpcAttempt() {
			return bmoOrderTypeRpcAttempt;
		}

		public void setBmoOrderTypeRpcAttempt(int bmoOrderTypeRpcAttempt) {
			this.bmoOrderTypeRpcAttempt = bmoOrderTypeRpcAttempt;
		}

		public int getSaveItemChangeRpcAttempt() {
			return saveItemChangeRpcAttempt;
		}

		public void setSaveItemChangeRpcAttempt(int saveItemChangeRpcAttempt) {
			this.saveItemChangeRpcAttempt = saveItemChangeRpcAttempt;
		}
	}
}
