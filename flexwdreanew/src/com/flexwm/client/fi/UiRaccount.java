/**
 * 
 */
package com.flexwm.client.fi;

import java.sql.Types;
import java.util.Date;
import java.util.Iterator;

import com.flexwm.client.op.UiOrderLifeCycleViewModel;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionReceipt;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaymentType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.client.ui.UiTemplate;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiRaccount extends UiList {
	BmoRaccount bmoRaccount;
	BmoOrder bmoOrder = new BmoOrder();
	int customerId = 0;

	// Dialogo para creacion multiple de cxc
	private Image multipleRaccountButton = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_multiple.png"));
	private Button generateDialogButton = new Button("GENERAR");
	private Button multipleRaccountCloseDialogButton = new Button("CERRAR");
	private DialogBox multipleRaccountDialogBox;
	private UiDateBox startDateBox = new UiDateBox();
	private TextBox amountTextBox = new TextBox();
	private UiSuggestBox raccountSuggestBox = new UiSuggestBox(new BmoRaccount());
	private TextBox paymentsNumberTextBox = new TextBox();
	
	private int orderBalanceNoTaxRpcAttempt = 0;

	// Dialogo informacion de cuentas del pedido
	private Image orderBalanceButton = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_orderbalance.png"));
	private Button orderBalanceCloseDialogButton = new Button("CERRAR");
	private DialogBox orderBalanceDialogBox;

	protected Image paymentImage;
	protected Label labelTotalPayment;
	protected Label labelPartialPayment;
	DialogBox partialPaymentDialogBox;

	UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

	public UiRaccount(UiParams uiParams) {
		super(uiParams, new BmoRaccount());
		this.bmoRaccount = (BmoRaccount) getBmObject();
	}

	public UiRaccount(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoRaccount());
		this.bmoRaccount = (BmoRaccount) getBmObject();
	}
	
	public UiRaccount(UiParams uiParams, Panel defaultPanel, int customerId) {
		super(uiParams, defaultPanel, new BmoRaccount());
		this.bmoRaccount = (BmoRaccount) getBmObject();
		this.customerId = customerId;
	}

	public UiRaccount(UiParams uiParams, BmoOrder bmoOrder) {
		super(uiParams, new BmoRaccount());
		this.bmoOrder = bmoOrder;
		this.bmoRaccount = (BmoRaccount) getBmObject();

		// Crear multiples CxC
		generateDialogButton.setStyleName("formSaveButton");
		generateDialogButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				generateMultipleRaccount();
				multipleRaccountDialogBox.hide();
			}
		});

		multipleRaccountCloseDialogButton.setStyleName("formCloseButton");
		multipleRaccountCloseDialogButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				multipleRaccountDialogBox.hide();
			}
		});

		multipleRaccountButton.setTitle("Crear Múltiples CxC");
		multipleRaccountButton.setStyleName("listSearchImage");
		multipleRaccountButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				openMultipleRaccountDialog();
			}
		});

		// Saldo de Pedido
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
			if (isSlave()) {
				// Obten info del filtro forzado
				BmFilter forceFilter = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).getForceFilter();
	
				// Accion del pedido
				if (bmoOrder.getId() > 0) {
					// Agrega boton de crear CxC multiples
					localButtonPanel.add(multipleRaccountButton);
	
					// Agrega boton de revisar saldo pedidos
					localButtonPanel.add(orderBalanceButton);
					
					if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {	
						BmoRaccountType bmoRaccountType = new BmoRaccountType();
						addStaticFilterListBox(new UiListBox(getUiParams(), bmoRaccountType.getType()), bmoRaccountType, bmoRaccountType.getType(),""+BmoRaccountType.TYPE_WITHDRAW);

					}
				}
	
				if (!isMobile()) {
					if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_CHANGESTATUS)) {
						addActionBatchListBox(new UiListBox(getUiParams(), new BmoRaccount().getStatus()), bmoRaccount);
					}
	
					// Pedidos de tipo Credito
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
						FlowPanel orderPanel = new FlowPanel();
						orderPanel.setWidth((UiTemplate.EASTSIZE - 20) + "px");
	
						// Preparar filtro solo los cargos
						BmFilter typeFilter = new BmFilter();
						typeFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(),
								"" + BmoRaccountType.TYPE_WITHDRAW);
						getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(typeFilter);
	
						UiRaccountOrderCreditView uiRaccountOrderCreditView = new UiRaccountOrderCreditView(getUiParams(),
								orderPanel, forceFilter.getValue());
						uiRaccountOrderCreditView.show();
					} 
					// Mostrar boton para pagar con nota de credito para arrendamiento
					else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
						// Habilitar el cobro de las CxC
						if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getAutomaticPaymentCxC().toBoolean()) {
							// Pagar totalmente
	//						paymentImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/amount.png"));
	//						paymentImage.setTitle("Cobrar Registros.");
	//						paymentImage.setStyleName("listAllImage");
							labelTotalPayment = new Label("Cobro Total");
							labelTotalPayment.setTitle("Cobro Total.");
							labelTotalPayment.setStyleName("listAllImage");
							labelTotalPayment.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (!isLoading()) {
										if (Window.confirm("Esta seguro que desea cobrar los registros seleccionados?"))
											listPaymentAutomatic();
									}
								}
							});
		
							actionItems.add(labelTotalPayment);
							
							// Pagar parcialmente
							labelPartialPayment = new Label("Cobro Parcial");
							labelPartialPayment.setTitle("Cobro parcial.");
							labelPartialPayment.setStyleName("listAllImage");
							labelPartialPayment.addClickHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (!isLoading()) {
										listPaymentPartial();
									}
								}
							});
		
							actionItems.add(labelPartialPayment);
						}
					}
				} else {
					// Accion del pedido
					if (bmoOrder.getId() > 0) {
						FlowPanel orderPanel = new FlowPanel();
						orderPanel.setWidth((UiTemplate.EASTSIZE - 20) + "px");
	
						// Pedidos de tipo Credito
						if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
	
							// Preparar filtro solo los cargos
								BmFilter typeFilter = new BmFilter();
								typeFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(),
										"" + BmoRaccountType.TYPE_WITHDRAW);
							getUiParams().getUiProgramParams(getBmObject().getProgramCode()).addFilter(typeFilter);
	
							UiRaccountOrderCreditView uiRaccountOrderCreditView = new UiRaccountOrderCreditView(
									getUiParams(), orderPanel, forceFilter.getValue());
							uiRaccountOrderCreditView.show();
						}
					}
				}
	
			} else if (isMaster()) {
			if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_CHANGESTATUS)) {
				addActionBatchListBox(new UiListBox(getUiParams(), new BmoRaccount().getStatus()), bmoRaccount);
			}

			// Habilitar el cobro de las CxC (Creditos)
			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getAutomaticPaymentCxC().toBoolean()) {
//				paymentImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/amount.png"));
//				paymentImage.setTitle("Cobrar Registros.");
//				paymentImage.setStyleName("listAllImage");
				labelTotalPayment = new Label("Cobro Total");
				labelTotalPayment.setTitle("Cobro Total.");
				labelTotalPayment.setStyleName("listAllImage");
				labelTotalPayment.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (!isLoading()) {
							if (Window.confirm("Esta seguro que desea pagar los registros seleccionados?"))
								listPaymentAutomatic();
						}
					}
				});

				actionItems.add(labelTotalPayment);
			}

			addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoRaccount.getCustomerId());
			addFilterListBox(new UiListBox(getUiParams(), new BmoRaccountType()), bmoRaccount.getBmoRaccountType());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoRaccount.getStatus()), bmoRaccount, bmoRaccount.getStatus());

			if (!isMobile()) {
				addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoRaccount.getUserId());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRaccount.getPaymentStatus()), bmoRaccount,
						bmoRaccount.getPaymentStatus(), "" + BmoRaccount.PAYMENTSTATUS_PENDING);
				BmoRaccountType bmoRaccountType = new BmoRaccountType();
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRaccountType.getType()), bmoRaccountType,
						bmoRaccountType.getType());
				addDateRangeFilterListBox(bmoRaccount.getDueDate());
			}
		} else if (isMinimalist()) {
			if (!isMobile()) {
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRaccount.getStatus()), bmoRaccount, bmoRaccount.getStatus());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoRaccount.getPaymentStatus()), bmoRaccount,
						bmoRaccount.getPaymentStatus());
			}
		}
	}

	@Override
	public void create() {
		UiRaccountForm uiRaccountForm;

		if (bmoOrder.getId() > 0)
			uiRaccountForm = new UiRaccountForm(getUiParams(), 0, bmoOrder);
		else
			uiRaccountForm = new UiRaccountForm(getUiParams(), 0);

		uiRaccountForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiRaccountForm uiRaccountForm;
		if (bmoOrder.getId() > 0)
			uiRaccountForm = new UiRaccountForm(getUiParams(), bmObject.getId(), bmoOrder);
		else
			uiRaccountForm = new UiRaccountForm(getUiParams(), bmObject.getId());
		uiRaccountForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiRaccountForm uiRaccountForm = new UiRaccountForm(getUiParams(), bmObject.getId());
		uiRaccountForm.show();
	}

	public void edit(int id) {
		UiRaccountForm uiRaccountForm = new UiRaccountForm(getUiParams(), id);
		uiRaccountForm.show();
	}

	// Abre dialogo saldo pedidos
	public void openOrderBalanceDialog() {
		orderBalanceDialogBox = new DialogBox(true);
		orderBalanceDialogBox.setGlassEnabled(true);
		orderBalanceDialogBox.setText("Cobranza del Pedido");
		orderBalanceDialogBox.setSize("250px", "150px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("250px", "150px");
		orderBalanceDialogBox.setWidget(vp);

		FlowPanel orderPanel = new FlowPanel();
		orderPanel.setWidth("250px");

		// Obten info del filtro forzado
		BmFilter forceFilter = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).getForceFilter();

		if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			UiRaccountOrderView uiRaccountOrderView = new UiRaccountOrderView(getUiParams(), orderPanel,
					forceFilter.getValue());
			uiRaccountOrderView.show();
		} else {
			UiRaccountOrderCreditView uiRaccountOrderCreditView = new UiRaccountOrderCreditView(getUiParams(),
					orderPanel, forceFilter.getValue());
			uiRaccountOrderCreditView.show();
		}

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(orderBalanceCloseDialogButton);

		vp.add(orderPanel);
		vp.add(buttonPanel);

		orderBalanceDialogBox.center();
		orderBalanceDialogBox.show();
	}

	// Abre dialogo CxC multiples
	public void openMultipleRaccountDialog() {
		multipleRaccountDialogBox = new DialogBox(true);
		multipleRaccountDialogBox.setGlassEnabled(true);
		multipleRaccountDialogBox.setText("Crear Múltiples CxC");
		multipleRaccountDialogBox.setSize("350px", "150px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("350px", "150px");
		multipleRaccountDialogBox.setWidget(vp);

		// Creacion de BmField
		BmField paymentsNumberBmField = new BmField("paymentsnumber", "", "# Pagos", 20, Types.INTEGER, false,
				BmFieldType.NUMBER, false);
		BmField raccountIdField = new BmField("raccountidfield", "", "Dividir CxC", 11, Types.INTEGER, false,
				BmFieldType.ID, false);
		BmField amountField = new BmField("amountfield", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY,
				false);

		// Filtar CxC del pedido
		BmFilter raccOrderFilter = new BmFilter();
		raccOrderFilter.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), bmoOrder.getId());
		raccountSuggestBox.addFilter(raccOrderFilter);

		BmFilter raccAutorized = new BmFilter();
		raccAutorized.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_REVISION);
		raccountSuggestBox.addFilter(raccAutorized);

		BmFilter filterByType = new BmFilter();
		filterByType.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(),
				"" + BmoRaccountType.TYPE_WITHDRAW);
		raccountSuggestBox.addFilter(filterByType);

		BmFilter filterByPaymentStatus = new BmFilter();
		filterByPaymentStatus.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(),
				"" + BmoRaccount.PAYMENTSTATUS_PENDING);
		raccountSuggestBox.addFilter(filterByPaymentStatus);

		// Manejo de acciones de suggest box
		UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
			@Override
			public void onSelect(UiSuggestBox uiSuggestBox) {
				suggestAction(uiSuggestBox);
			}
		};
		formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

		try {
			bmoRaccount.getAmount().setValue(amountTextBox.getText());
			bmoRaccount.getDueDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
			paymentsNumberBmField.setValue(2);
		} catch (BmException e) {
			showSystemMessage("openMultipleRaccountDialog(): Error:" + e.toString());
		}

		formTable.addField(1, 0, raccountSuggestBox, raccountIdField);
		formTable.addField(2, 0, startDateBox, bmoRaccount.getDueDate());
		formTable.addField(3, 0, amountTextBox, amountField);
		formTable.addField(4, 0, paymentsNumberTextBox, paymentsNumberBmField);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(generateDialogButton);
		buttonPanel.add(multipleRaccountCloseDialogButton);

		vp.add(formTable);
		vp.add(buttonPanel);

		multipleRaccountDialogBox.center();
		multipleRaccountDialogBox.show();
	}

	// Generar multiples CxC
	public void generateMultipleRaccount() {
		String startDate = startDateBox.getTextBox().getText();

		if (startDateBox.getTextBox().getText().equals("")) {
			startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
		}

		String actionValues = bmoOrder.getId() + "|" + "|" + startDate + "|" + amountTextBox.getText() + "|"
				+ paymentsNumberTextBox.getText();

		if (raccountSuggestBox.getSelectedId() > 0) {
			actionValues += "|" + raccountSuggestBox.getSelectedId();
		} else {
			showSystemMessage("Debe seleccionar una CXC para dividir");
			actionValues += "|" + "0";
		}

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-generateMultipleRaccount() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.hasErrors()) {					
					if (raccountSuggestBox.getSelectedId() > 0) {
						showErrorMessage("Error al crear CxC Múltiples: " + result.errorsToString());
					}
				} else {
					showSystemMessage("Generación de CxC Múltiples Exitosa.");
				}
				showList();
			}
		};

		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount,
						BmoRaccount.ACTION_MULTIPLERACCOUNT, "" + actionValues, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-generateMultipleRaccount() ERROR: " + e.toString());
		}
	}
	
	// Generar multiples CxC
	public void getOrderBalanceNoTax() {
		getOrderBalanceNoTax(0);
	}

	// Generar multiples CxC
	public void getOrderBalanceNoTax(int orderBalanceNoTaxRpcAttempt) {
		if (orderBalanceNoTaxRpcAttempt < 5) {
			setOrderBalanceNoTaxRpcAttempt(orderBalanceNoTaxRpcAttempt + 1);
			
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getOrderBalanceNoTaxRpcAttempt() < 5)
						getOrderBalanceNoTax(getOrderBalanceNoTaxRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-getOrderBalanceNoTax() ERROR: " + caught.toString());
				}
	
				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setOrderBalanceNoTaxRpcAttempt(0);
					amountTextBox.setValue(result.getMsg());
					openMultipleRaccountDialog();
				}
			};
	
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount,
							BmoRaccount.ACTION_AMOUNTAUTOCREATE, "" + bmoOrder.getId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getOrderBalanceNoTax() ERROR: " + e.toString());
			}
		}
	}

	// Pagar las CxC Seleccionadas
	public void listPaymentAutomatic() {
		String values = "";
		Iterator<BmObject> listPayment = listFlexTable.getListCheckBoxSelectedBmObjectList().iterator();
		while (listPayment.hasNext()) {
			BmoRaccount bmoRaccount = (BmoRaccount) listPayment.next();
			values += bmoRaccount.getId() + "|";
		}

		paymentAutomatic(values);
	}
	
	// Pagar parcialmente la CxC Seleccionada
	public void listPaymentPartial() {
		if (listFlexTable.getListCheckBoxSelectedBmObjectList().size() > 1) {
			showSystemMessage ("<b>Solo puede seleccionar una CxC.</b>");
		} else {
			Iterator<BmObject> listPayment = listFlexTable.getListCheckBoxSelectedBmObjectList().iterator();
			if (listPayment.hasNext()) {
				BmoRaccount bmoRaccount = (BmoRaccount) listPayment.next();
				partialPaymentDialogBox(bmoRaccount);
			}
		}
	}

	// Action de pagar varios registros
	public void paymentAutomatic(String values) {

		// Llamada al servicio RPC
		try {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {
					} else
						showErrorMessage("Error al pagar los registros: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors())
						showErrorMessage(result.errorsToString());

					showList();
				}
			};

			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount,
						BmoRaccount.ACTION_MULTIPLEPAYMENT, values, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-paymentAutomatic() ERROR: " + e.toString());
		}
	}

	public void suggestAction(UiSuggestBox uiSuggestBox) {
		NumberFormat numberFormat = NumberFormat.getDecimalFormat();
		// Calcular el monto sin iva
		if (uiSuggestBox == raccountSuggestBox) {
			BmoRaccount bmoRaccSelect = new BmoRaccount();
			bmoRaccSelect = (BmoRaccount) uiSuggestBox.getSelectedBmObject();

			double amount = bmoRaccSelect.getBalance().toDouble();
			if (bmoRaccSelect.getTaxApplies().toBoolean()) {
				double taxRate = (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);
				amount = (amount / (taxRate + 1));
			}

			try {
				bmoRaccount.getAmount().setValue(numberFormat.format(amount));

			} catch (BmException e) {
				showErrorMessage("Error al obtener el saldo de la CxC");
			}
			formTable.addField(3, 0, amountTextBox, bmoRaccount.getAmount());
		}
	}
	
	public void partialPaymentDialogBox(BmoRaccount bmoRaccount) {
		partialPaymentDialogBox = new DialogBox(true);
		partialPaymentDialogBox.setGlassEnabled(true);
		partialPaymentDialogBox.setText("Pago Parcial");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		partialPaymentDialogBox.setWidget(vp);

		UiPartialPaymentForm uiPartialDueForm = new UiPartialPaymentForm(getUiParams(), vp, bmoRaccount);
		uiPartialDueForm.show();

		partialPaymentDialogBox.center();
		partialPaymentDialogBox.show();
	}
	
	private class UiPartialPaymentForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

		BmoRaccount bmoRaccountPartial = new BmoRaccount();
		private HorizontalPanel buttonPanelDialog = new HorizontalPanel();
		private Button saveDueButton = new Button("APLICAR");
		private Button closeDueButton = new Button("CERRAR");

		// Creacion de BmField
		BmField amountField;
		protected TextBox amountTextBox = new TextBox();

		public UiPartialPaymentForm(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount) {
			super(uiParams, defaultPanel);
			this.bmoRaccountPartial = bmoRaccount;
			amountField = new BmField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);

			saveDueButton.setStyleName("formSaveButton");
			saveDueButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					generatePartialPayment();
				}
			});

			closeDueButton.setStyleName("formCloseButton");
			closeDueButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					partialPaymentDialogBox.hide();
				}
			});

			buttonPanelDialog.add(saveDueButton);
			buttonPanelDialog.add(closeDueButton);

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			try {
				amountField.setValue(bmoRaccountPartial.getBalance().toDouble());
			} catch (BmException e) {
				e.printStackTrace();
			}

			formTable.addLabelField(1, 0, bmoRaccountPartial.getCode());
			formTable.addLabelField(2, 0, bmoRaccountPartial.getBmoCustomer().getDisplayName());
			formTable.addField(3, 0, amountTextBox, amountField);
			formTable.addButtonPanel(buttonPanelDialog);
		}

		// Generar el Pago Parcial
		public void generatePartialPayment() {

			if (bmoRaccountPartial != null) {
				String dueDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
				String actionValues = bmoRaccountPartial.getId() + "|" + dueDate + "|" + amountTextBox.getText() + "|";

				if (!getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_PAYMENTRACC))  {
					showSystemMessage("<b>No tiene Permiso de aplicar pagos a la CxC.</b>");
				} else {
					AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
						@Override
						public void onFailure(Throwable caught) {
							stopLoading();
							showErrorMessage(
									this.getClass().getName() + "-generatePartialPayment() ERROR: " + caught.toString());
						}
	
						@Override
						public void onSuccess(BmUpdateResult result) {
							stopLoading();
							if (result.hasErrors()) {
								showErrorMessage("Existen Errores " + result.errorsToString());
							} else {
								showSystemMessage("Generación de Pago Parcial Exitosa. (" + bmoRaccountPartial.getCode().toString() + ")");
							}
							partialPaymentDialogBox.hide();
							showList();
						}
					};
	
					try {
						if (!isLoading()) {
							startLoading();
							getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_PAYMENTAPP, "" + actionValues, callback);
						}
					} catch (SFException e) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-generatePartialPayment() ERROR: " + e.toString());
					}
				}
			}
		}
	}
	
	public int getOrderBalanceNoTaxRpcAttempt() {
		return orderBalanceNoTaxRpcAttempt;
	}

	public void setOrderBalanceNoTaxRpcAttempt(int orderBalanceNoTaxRpcAttempt) {
		this.orderBalanceNoTaxRpcAttempt = orderBalanceNoTaxRpcAttempt;
	}

	public class UiRaccountForm extends UiFormDialog {
		BmoRaccount bmoRaccount = new BmoRaccount();
		UiSuggestBox newRaccountSuggestBox = new UiSuggestBox(new BmoRaccount());
		TextBox codeTextBox = new TextBox();
		TextBox invoicenoTextBox = new TextBox();
		TextBox referenceTextBox = new TextBox();
		TextBox folioTextBox = new TextBox();
		TextBox serieTextBox = new TextBox();
		UiDateBox receiveDateBox = new UiDateBox();
		UiDateBox dueDateStartDateBox = new UiDateBox();
		UiDateBox dueDateBox = new UiDateBox();
		UiDateBox remindDateBox = new UiDateBox();
		TextArea descriptionTextArea = new TextArea();
		TextArea commentsTextArea = new TextArea();
		TextArea statusChargeTextArea = new TextArea();
		TextArea commentLogTextArea = new TextArea();
		TextBox amountTextBox = new TextBox();
		TextBox totalTextBox = new TextBox();
		TextBox taxTextBox = new TextBox();
		TextBox balanceTextBox = new TextBox();
		TextBox paymentsTextBox = new TextBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox paymentStatusListBox = new UiListBox(getUiParams());
		TextArea observationsTextArea = new TextArea();
		UiDateBox paymentDateBox = new UiDateBox();
		UiListBox orderListBox = new UiListBox(getUiParams(), new BmoOrder());
		UiListBox raccountTypeListBox = new UiListBox(getUiParams(), new BmoRaccountType());
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		UiSuggestBox userAuthorizedSuggestBox = new UiSuggestBox(new BmoUser());
		UiDateBox authorizeDateBox = new UiDateBox();
		UiListBox budgetItemListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		UiSuggestBox realtedRaccSuggestBox = new UiSuggestBox(new BmoRaccount());
		UiListBox paymentTypeListBox = new UiListBox(getUiParams(), new BmoPaymentType());
		UiSuggestBox userCollectorSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox reqPayTypeListBox = new UiListBox(getUiParams(), new BmoReqPayType());
		UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		// CxC Externa
		CheckBox linkedCheckBox = new CheckBox("");

		private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
		double taxRate = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
		private CheckBox taxAppliesCheckBox = new CheckBox("");
		protected FlowPanel formatPanel;

		// Aplicaciones de pagos
		private UiRaccountItemGrid uiRaccountItemGrid;
		private FlowPanel uiRaccountItemPanel = new FlowPanel();

		// Pagos (Conceptos de Banco)
		private UiRaccountPaymentGrid uiRaccountPaymentGrid;
		private FlowPanel uiRaccountPaymentsPanel = new FlowPanel();

		// Aplicaciones de pagos
		private UiRaccountAssignmentGrid uiRaccountAssignmentGrid;
		private FlowPanel uiRaccountAssignmentPanel = new FlowPanel();

		private UiRaccountLinkGrid uiRaccountLinkGrid;
		private UiRaccountLinkOriginGrid uiRaccountLinkOriginGrid;
		private FlowPanel uiRaccountLinkPanel = new FlowPanel();

		BmoOrder bmoOrder = new BmoOrder();
		RaccountUpdater raccountUpdater = new RaccountUpdater();
		
		private int raccIdRpc = 0;
		private int raccIdRpcAttempt = 0;
		private String currencyIdRpc = "";
		private int currencyIdRpcAttempt = 0;
		private int changePaymenteDateRpcAttempt = 0;
		private int orderIdRpc = 0;
		private int orderIdRpcAttempt = 0;
		private int saveAmountChangeRpcAttempt = 0;
		
		boolean multiCompany = false; //multiEmpresa: G100
		private String companyId; //multiEmpresa: G100
		private int profileCollectRpcAttempt = 0; //multiEmpresa: G100

		// Ciclo de vida documento
		public final SingleSelectionModel<BmObject> lifeCycleSelection = new SingleSelectionModel<BmObject>();
		private UiOrderLifeCycleViewModel uiLifeCycleViewModel;
		private CellTree lifeCycleCellTree;
		private DialogBox lifeCycleDialogBox = new DialogBox();
		private Button lifeCycleShowButton = new Button("SEGUIMIENTO");
		private Button lifeCycleCloseButton = new Button("CERRAR");

		// Pagos Directos
		DialogBox partialDueDialogBox;
		private Button paymentButton = new Button("PAGAR");

		private Button addPaymentButton = new Button("COBRAR");
		// Secciones
		String generalSection = "Datos Generales";
		String detailsSection = "Detalle y Fechas";
		String amountsSection = "Montos";
		String totalsSection = "Totales";
		String itemsSection = "Items";
		String paymentsSection = "Pagos Mov. Banco";
		String assignmentsSection = "Aplicaciones Notas de Crédito";
		String raccountLinkSection = "CxC Relacionadas";
		String statusSection = "Status";
		String logSection = "Bitácora";

		public UiRaccountForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRaccount(), id);
			initialize();
		}

		public UiRaccountForm(UiParams uiParams, int id, BmoOrder bmoOrder) {
			super(uiParams, new BmoRaccount(), id);
			this.bmoOrder = bmoOrder;

			initialize();
		}

		private void initialize() {
			getUiParams().getUiTemplate().hideEastPanel();
			uiRaccountItemPanel.setWidth("100%");
			uiRaccountAssignmentPanel.setWidth("100%");
			uiRaccountLinkPanel.setWidth("100%");

			// Ciclo de vida de ordenes de compra
			lifeCycleSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					BmObject bmObject = lifeCycleSelection.getSelectedObject();
					// Se esta tratando de abrir un registro
					if (bmObject instanceof BmoRequisition) {
						if (Window.confirm("Desea Abrir la Órden de Compra?"))
							new UiRequisition(getUiParams()).edit(bmObject);
						;
					} else if (bmObject instanceof BmoRequisitionReceipt) {
						if (Window.confirm("Desea Abrir el Recibo de Órden de Compra?"))
							new UiRequisitionReceipt(getUiParams()).edit(bmObject);
					} else if (bmObject instanceof BmoPaccount) {
						if (Window.confirm("Desea Abrir la Cuenta por Pagar?"))
							new UiPaccount(getUiParams()).edit(bmObject);
					} else if (bmObject instanceof BmoBankMovConcept) {
						if (Window.confirm("Desea Abrir el Movimiento Bancario?"))
							new UiBankMovement(getUiParams()).edit(((BmoBankMovConcept) bmObject).getBmoBankMovement());
					}
				}
			});

			// Actualiza seguimiento a documento
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

			// Aplicar IVA
			taxAppliesCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					taxAppliesChange();
				}
			});

			// Pagos Parciales
			paymentButton.setStyleName("formSaveButton");
			paymentButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					partialDueDialogBoxParity();
				}
			});
			
			// Pagos para Arrendamientos
			addPaymentButton.setStyleName("formSaveButton");
			addPaymentButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (Window.confirm("Desea Aplicar el Cobro de la CxC?"))
						payRacc();
				}
			});
		}

		@Override
		public void populateFields() {
			bmoRaccount = (BmoRaccount) getBmObject();

			try {
				// MultiEmpresa: g100
				multiCompany = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean();
				
				if (newRecord) {
					// Es nuevo registro asigna fechas
					bmoRaccount.getReceiveDate()
							.setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoRaccount.getDueDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));

					// Asigna valor de empresa, cliente y de pedido
					if (bmoOrder.getId() > 0) {
						bmoRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
						bmoRaccount.getOrderId().setValue(bmoOrder.getId());
						bmoRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
					} else {
						// Busca Empresa seleccionada por default
						if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
							bmoRaccount.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
					}

					// Filtros de Tipos de Cuentas x Cobrar
					raccountTypeListBox.clear();
					raccountTypeListBox.clearFilters();
					BmoRaccountType bmoRaccountType = new BmoRaccountType();
					BmFilter bmFilterVisible = new BmFilter();
					bmFilterVisible.setValueFilter(bmoRaccountType.getKind(), bmoRaccountType.getVisible(),
							"" + BmoRaccountType.VISIBLE_TRUE);
					raccountTypeListBox.addBmFilter(bmFilterVisible);

					// Asignar moneda default
					bmoRaccount.getCurrencyId()
							.setValue(((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());

					// Asigna IVA si viene del Pedido
					if (bmoOrder.getTaxApplies().toBoolean())
						bmoRaccount.getTaxApplies().setValue(true);
					
					if (isSlave()) {
						bmoRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
						bmoRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().getValue()); 
					}

				} else {
					setEnableFiles(true);

					// Modifica titulo
					formDialogBox.setText(
							"Editar " + getSFParams().getProgramTitle(getBmObject()) + ": " + bmoRaccount.getCode());
				}
				
				setProfileCollectByCompanyIdFilters("" + bmoRaccount.getCompanyId().toInteger(), multiCompany);
				setCustomerListBoxFilters(bmoRaccount.getCompanyId().toInteger(), multiCompany);

				// Filtros de Pedidos del cliente
				BmFilter ordersByCustomer = new BmFilter();
				ordersByCustomer.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(),
						bmoRaccount.getCustomerId().toInteger());
				orderListBox.addFilter(ordersByCustomer);

				// Filtros de Pedidos
				if ((bmoOrder.getId() > 0)) {
					BmFilter ordersByOrder = new BmFilter();
					ordersByOrder.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdField(),
							bmoRaccount.getOrderId().toInteger());
					orderListBox.addFilter(ordersByOrder);
				}

				// // Asigna filtros de control presupuestal
				setBudgetItemsListBoxFilters(bmoRaccount.getCompanyId().toInteger());

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
			}

			// Campo para cambiar de OC
			if (!newRecord)
				formFlexTable.addField(0, 0, newRaccountSuggestBox, bmoRaccount.getIdField());

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			if (multiCompany)
				formFlexTable.addField(2, 0, companyListBox, bmoRaccount.getCompanyId());

			formFlexTable.addFieldReadOnly(3, 0, codeTextBox, bmoRaccount.getCode());
			formFlexTable.addField(4, 0, raccountTypeListBox, bmoRaccount.getRaccountTypeId());
			formFlexTable.addField(5, 0, customerSuggestBox, bmoRaccount.getCustomerId());
			formFlexTable.addField(6, 0, orderListBox, bmoRaccount.getOrderId());
			if (!multiCompany)
				formFlexTable.addField(7, 0, companyListBox, bmoRaccount.getCompanyId());
			if ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				formFlexTable.addField(8, 0, budgetItemListBox, bmoRaccount.getBudgetItemId());
				formFlexTable.addField(9, 0, areaUiListBox, bmoRaccount.getAreaId());
			}

			formFlexTable.addSectionLabel(10, 0, detailsSection, 2);
			formFlexTable.addField(11, 0, referenceTextBox, bmoRaccount.getReference());
			formFlexTable.addField(12, 0, serieTextBox, bmoRaccount.getSerie());
			formFlexTable.addField(13, 0, folioTextBox, bmoRaccount.getFolio());
			formFlexTable.addField(14, 0, invoicenoTextBox, bmoRaccount.getInvoiceno());
			formFlexTable.addField(15, 0, descriptionTextArea, bmoRaccount.getDescription());

			formFlexTable.addField(16, 0, receiveDateBox, bmoRaccount.getReceiveDate());
			if (!newRecord)
				formFlexTable.addField(17, 0, dueDateStartDateBox, bmoRaccount.getDueDateStart());
			formFlexTable.addField(18, 0, dueDateBox, bmoRaccount.getDueDate());
			formFlexTable.addField(19, 0, reqPayTypeListBox, bmoRaccount.getReqPayTypeId());

			formFlexTable.addField(20, 0, paymentTypeListBox, bmoRaccount.getPaymentTypeId());
			formFlexTable.addField(21, 0, linkedCheckBox, bmoRaccount.getLinked());
			formFlexTable.addField(22, 0, userCollectorSuggestBox, bmoRaccount.getCollectorUserId());
			formFlexTable.addField(23, 0, remindDateBox, bmoRaccount.getRemindDate());
			formFlexTable.addField(24, 0, commentsTextArea, bmoRaccount.getComments());
			formFlexTable.addField(25, 0, statusChargeTextArea, bmoRaccount.getStatusCharge());

			if (!newRecord) {
				formFlexTable.addSectionLabel(26, 0, itemsSection, 2);
				formFlexTable.addPanel(27, 0, uiRaccountItemPanel, 4);
				uiRaccountItemGrid = new UiRaccountItemGrid(getUiParams(), uiRaccountItemPanel, bmoRaccount,
						raccountUpdater);
				uiRaccountItemGrid.show();

				formFlexTable.addSectionLabel(28, 0, paymentsSection, 2);
				formFlexTable.addPanel(29, 0, uiRaccountPaymentsPanel, 4);
				uiRaccountPaymentGrid = new UiRaccountPaymentGrid(getUiParams(), uiRaccountPaymentsPanel, bmoRaccount,
						raccountUpdater);
				uiRaccountPaymentGrid.show();

				formFlexTable.addSectionLabel(30, 0, assignmentsSection, 2);
				formFlexTable.addPanel(31, 0, uiRaccountAssignmentPanel, 4);
				uiRaccountAssignmentGrid = new UiRaccountAssignmentGrid(getUiParams(), uiRaccountAssignmentPanel,
						bmoRaccount, bmoRaccount.getOrderId().toInteger(), raccountUpdater);
				uiRaccountAssignmentGrid.show();

				formFlexTable.addSectionLabel(32, 0, totalsSection, 2);
				formFlexTable.addField(33, 0, currencyListBox, bmoRaccount.getCurrencyId());
				formFlexTable.addField(34, 0, currencyParityTextBox, bmoRaccount.getCurrencyParity());
				formFlexTable.addFieldReadOnly(35, 0, amountTextBox, bmoRaccount.getAmount());
				formFlexTable.addField(36, 0, taxAppliesCheckBox, bmoRaccount.getTaxApplies());
				formFlexTable.addFieldReadOnly(37, 0, taxTextBox, bmoRaccount.getTax());
				formFlexTable.addFieldReadOnly(38, 0, totalTextBox, bmoRaccount.getTotal());
				formFlexTable.addFieldReadOnly(39, 0, paymentsTextBox, bmoRaccount.getPayments());
				formFlexTable.addFieldReadOnly(40, 0, balanceTextBox, bmoRaccount.getBalance());

				// Si es de tipo cargo, y de categoria otros, y el pedido es de tipo otros
				// muestra cxc ligadas
				if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
					if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER)
							|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
						formFlexTable.addSectionLabel(41, 0, raccountLinkSection, 2);
						formFlexTable.addPanel(42, 0, uiRaccountLinkPanel, 4);
						uiRaccountLinkOriginGrid = new UiRaccountLinkOriginGrid(getUiParams(), uiRaccountLinkPanel,
								bmoRaccount, bmoOrder, raccountUpdater);
						uiRaccountLinkOriginGrid.show();

					} else {
						formFlexTable.addSectionLabel(43, 0, raccountLinkSection, 2);
						formFlexTable.addPanel(44, 0, uiRaccountLinkPanel, 4);
						uiRaccountLinkGrid = new UiRaccountLinkGrid(getUiParams(), uiRaccountLinkPanel, bmoRaccount,
								bmoOrder, raccountUpdater);
						uiRaccountLinkGrid.show();
					}
				}
				// Si la categoria de la CXC es de tipo Nota credito, pedido y Otros muestra las
				// cxc ligadas
				else if ((bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)
						|| bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_ORDER)
						|| bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER))) {
					formFlexTable.addSectionLabel(45, 0, raccountLinkSection, 2);
					formFlexTable.addPanel(46, 0, uiRaccountLinkPanel, 4);
					uiRaccountLinkGrid = new UiRaccountLinkGrid(getUiParams(), uiRaccountLinkPanel, bmoRaccount,
							bmoOrder, raccountUpdater);
					uiRaccountLinkGrid.show();
				}

				formFlexTable.addSectionLabel(47, 0, logSection, 2);
				formFlexTable.addField(48, 0, commentLogTextArea, bmoRaccount.getCommentLog());
				commentLogTextArea.setEnabled(false);
				commentLogTextArea.setHeight("250px");

				formFlexTable.addSectionLabel(49, 0, statusSection, 2);
				formFlexTable.addLabelField(50, 0, bmoRaccount.getPaymentStatus());
				formFlexTable.addField(51, 0, statusListBox, bmoRaccount.getStatus());

			} else {
				formFlexTable.addSectionLabel(26, 0, totalsSection, 2);
				formFlexTable.addField(27, 0, currencyListBox, bmoRaccount.getCurrencyId());

				formFlexTable.addSectionLabel(28, 0, statusSection, 2);
				formFlexTable.addField(29, 0, statusListBox, bmoRaccount.getStatus());
			}

			if (!newRecord) {
				formFlexTable.hideSection(detailsSection);
				formFlexTable.hideSection(paymentsSection);
				formFlexTable.hideSection(assignmentsSection);
				formFlexTable.hideSection(raccountLinkSection);
			}
			formFlexTable.hideSection(logSection);

			//showSystemMessage("tipo: "+ getUiParams().getUiType(getBmObject().getProgramCode()));
			statusEffect();
		}

		@Override
		public void postShow() {
			if (!newRecord) {
				buttonPanel.add(lifeCycleShowButton);
				// Al abrir desde el listado maestro buscar el tipo de pedido para mostrar boton de Pagar
				if (isSlave() && this.bmoOrder.getId() > 0) {
					showPaymentButton(this.bmoOrder);
				} else if (bmoRaccount.getOrderId().toInteger() > 0 && !(this.bmoOrder.getId() > 0)) {
					getOrder(bmoRaccount.getOrderId().toInteger());
				}
			}
		}
		
		public void showPaymentButton(BmoOrder bmoOrder) {
			// Mostrar el pago automatico solo para cxc ligadas al tipo de pedido crédito
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				if (!newRecord) {
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getAutomaticPaymentCxC().toBoolean()) {
						if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
							if (!bmoRaccount.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL)) {
								buttonPanel.setHeight("100%");
								buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
								buttonPanel.add(paymentButton);
							}
						}
					}
				}
			} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				// Mostrar boton si esta Autorizada, Pendiente de pago, con saldo y tienes permiso especial
				if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_PAYMENTRACC)
						&& bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED) 
						&& bmoRaccount.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_PENDING) 
						&& bmoRaccount.getBalance().toDouble() > 0)
				buttonPanel.add(addPaymentButton);
			}
		}
		
//		public void showPaymentButtonLease(BmoRaccount bmoRaccount) {
//			// Mostrar boton si esta Autorizada, Pendiente de pago, con saldo y tienes permiso especial
//			if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_PAYMENTRACC)
//					&& bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED) 
//					&& bmoRaccount.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_PENDING) 
//					&& bmoRaccount.getBalance().toDouble() > 0)
//			buttonPanel.add(addPaymentButton);
//		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoRaccount.setId(id);
			bmoRaccount.getCode().setValue(codeTextBox.getText());
			bmoRaccount.getInvoiceno().setValue(invoicenoTextBox.getText());
			bmoRaccount.getReceiveDate().setValue(receiveDateBox.getTextBox().getText());
			bmoRaccount.getDueDate().setValue(dueDateBox.getTextBox().getText());
			bmoRaccount.getPaymentDate().setValue(dueDateBox.getTextBox().getText());
			bmoRaccount.getDescription().setValue(descriptionTextArea.getText());
			bmoRaccount.getAmount().setValue(amountTextBox.getText());
			bmoRaccount.getTotal().setValue(totalTextBox.getText());
			bmoRaccount.getTax().setValue(taxTextBox.getText());
			bmoRaccount.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
			bmoRaccount.getRaccountTypeId().setValue(raccountTypeListBox.getSelectedId());
			bmoRaccount.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoRaccount.getOrderId().setValue(orderListBox.getSelectedId());
			bmoRaccount.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoRaccount.getStatus().setValue(statusListBox.getSelectedCode());
			bmoRaccount.getReference().setValue(referenceTextBox.getText());
			bmoRaccount.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoRaccount.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoRaccount.getBudgetItemId().setValue(budgetItemListBox.getSelectedId());
			bmoRaccount.getPaymentTypeId().setValue(paymentTypeListBox.getSelectedId());
			bmoRaccount.getLinked().setValue(linkedCheckBox.getValue());
			bmoRaccount.getSerie().setValue(serieTextBox.getText());
			bmoRaccount.getFolio().setValue(folioTextBox.getText());
			bmoRaccount.getComments().setValue(commentsTextArea.getText());
			bmoRaccount.getStatusCharge().setValue(statusChargeTextArea.getText());
			bmoRaccount.getCommentLog().setValue(commentLogTextArea.getText());
			bmoRaccount.getRemindDate().setValue(remindDateBox.getTextBox().getText());
			bmoRaccount.getCollectorUserId().setValue(userCollectorSuggestBox.getSelectedId());
			bmoRaccount.getReqPayTypeId().setValue(reqPayTypeListBox.getSelectedId());
			bmoRaccount.getDueDateStart().setValue(dueDateStartDateBox.getTextBox().getText());
			bmoRaccount.getAreaId().setValue(areaUiListBox.getSelectedId());
			
			return bmoRaccount;
		}

		public void reset() {
			updateAmount(id);
			uiRaccountItemGrid.show();
			uiRaccountPaymentGrid.show();
			uiRaccountAssignmentGrid.show();
			uiRaccountLinkOriginGrid.show();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			// Cambia el tipo de CxC
			if (event.getSource() == raccountTypeListBox) {
				BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
				if (bmoRaccountType == null)
					bmoRaccountType = bmoRaccount.getBmoRaccountType();

				// Se esta creando la cxc
				if (newRecord) {
					populateGeneralInfo(bmoRaccountType);
					if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER)) {
						if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem()
								.toInteger() > 0) {
							if (bmoOrder.getId() > 0) {
								getBudgetItemByOrder(bmoOrder.getDefaultBudgetItemId().toInteger());
								getAreaByOrder(bmoOrder.getDefaultAreaId().toInteger());
							}
						}
					} else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
						orderListBox.setEnabled(true);
					}

					// Viene de un pedido
					if (bmoOrder.getId() > 0) {
						// y no esta seleccionado un termino de pago
						if (!(Integer.parseInt(reqPayTypeListBox.getSelectedId()) > 0)) {
							BmoCustomer bmoCustomer = (BmoCustomer) customerSuggestBox.getSelectedBmObject();
							reqPayTypeListBox.setSelectedId(bmoCustomer.getReqPayTypeId().toString());
							calculatePaymentDate();
						}

						// Y no esta seleccionado la partida por defecto
						// if (!(Integer.parseInt(budgetItemListBox.getSelectedId()) > 0) ) {
						// BmoOrder bmoOrder = (BmoOrder)orderListBox.getSelectedBmObject();
						// showSystemMessage("pedidoo: "+bmoOrder.getCode().toString());
						// populateBudgetItems(bmoOrder.getCompanyId().toInteger());
						// getBudgetItemByOrder(bmoOrder.getDefaultBudgetItemId().toInteger());
						// getAreaByOrder(bmoOrder.getDefaultAreaId().toInteger());
						// }
					}
				}
			}
			// Cambia la empresa
			else if (event.getSource() == companyListBox) {
				BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
				if (bmoRaccountType == null)
					bmoRaccountType = bmoRaccount.getBmoRaccountType();

				BmoCompany bmoCompany = (BmoCompany) companyListBox.getSelectedBmObject();
				if (bmoCompany == null) {
					populateBudgetItems(-1);
					
					// MultiEmpresa: g100
					// Limpiar widget clientes
					populateCustomers(-1, multiCompany);
				} else {
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
						// Si es de tipo otros, la partida presp. se rige por la empresa
						if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_OTHER)) {
							populateBudgetItems(bmoCompany.getId());
						}
						// Si es nota de credito, y NO hay pedido seleccionado, se rige por la empresa
						else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
							if (!(Integer.parseInt(orderListBox.getSelectedId()) > 0))
								populateBudgetItems(bmoCompany.getId());
						}
					}
					
					// MultiEmpresa: g100
					// Filtrar usuarios
					populateUsersCollect(bmoCompany.getId(), multiCompany);
					// Filtrar clientes
					populateCustomers(bmoCompany.getId(), multiCompany);
				}
			}
			// Cambia la moneda
			else if (event.getSource() == currencyListBox) {
				populateParityFromCurrency(currencyListBox.getSelectedId());
				currencyListBox.setSelectedId(currencyListBox.getSelectedId());
			}
			// Cambia el estatus
			else if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status de la Cuenta por Cobrar?");
			}
			// Cambia el pedido
			else if (event.getSource() == orderListBox) {
				if (!multiCompany) {
					BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
					if (bmoRaccountType == null)
						bmoRaccountType = bmoRaccount.getBmoRaccountType();
	
					BmoOrder bmoOrder = (BmoOrder) orderListBox.getSelectedBmObject();
	
					// llenar/limpiar empresas
					if (bmoOrder == null) {
						// mostrar todas las empresas y quitar partidas
						populateCompany(-1, ' ');
						populateBudgetItems(-1);
					} else {
						populateCompany(bmoOrder.getCompanyId().toInteger(), ' ');
					}
	
					// Si es de tipo otros, la partida presp. se rige por la empresa
					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
						if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_OTHER)) {
							populateBudgetItems(bmoOrder.getCompanyId().toInteger());
						} else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER)) {
							populateBudgetItems(bmoOrder.getCompanyId().toInteger());
							getBudgetItemByOrder(bmoOrder.getDefaultBudgetItemId().toInteger());
							getAreaByOrder(bmoOrder.getDefaultAreaId().toInteger());
							// Ver "todo" del metodo
							// populateBudgetItemsByBudgetItem(bmoOrder.getBmoOrderType().getDefaultBudgetItemId().toInteger());
						} else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
							// Si hay pedido, traer la partida del pedido y desHabilitar partida si viene de
							// una Venta Inm.
							if (bmoOrder.getId() > 0) {
								populateBudgetItems(bmoOrder.getCompanyId().toInteger());
								getBudgetItemByOrder(bmoOrder.getDefaultBudgetItemId().toInteger());
								getAreaByOrder(bmoOrder.getDefaultAreaId().toInteger());
							}
						}
					}
				}
			}
			// Cambia
			else if (event.getSource() == reqPayTypeListBox) {
				calculatePaymentDate();
			}
			statusEffect();
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Cambia la empresa
			if (uiSuggestBox == customerSuggestBox) {
				BmoCustomer bmoCustomer = (BmoCustomer) customerSuggestBox.getSelectedBmObject();
				if (bmoCustomer != null) {
					BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
					if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER)) {
						orderListBox.setEnabled(true);
						populateOrder(customerSuggestBox.getSelectedId());
					} else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_OTHER)) {
						orderListBox.setEnabled(false);
					} else if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
						orderListBox.setEnabled(true);
						populateOrder(customerSuggestBox.getSelectedId());
					}

					reqPayTypeListBox.setSelectedId(bmoCustomer.getReqPayTypeId().toString());
					calculatePaymentDate();
				} else {
					populateOrder(-1);
					populateBudgetItems(-1);
				}

				//realtedRacc(bmoOrder);

			} else if (uiSuggestBox == newRaccountSuggestBox) {
				if (newRaccountSuggestBox.getSelectedBmObject() != null)
					changeRaccount((BmoRaccount) newRaccountSuggestBox.getSelectedBmObject());
			}
			statusEffect();
		}

		// Cambia la CxC
		private void changeRaccount(BmoRaccount newBmoRaccount) {
			if (newBmoRaccount.getId() != bmoRaccount.getId()) {
				formDialogBox.hide();
				edit(newBmoRaccount);
			}
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == receiveDateBox) {
				changePaymenteDate();
			}
		}
		
		// Cambiar la fecha Pago
		public void changePaymenteDate() {
			changePaymenteDate(0);
		}

		// Cambiar la fecha Pago
		public void changePaymenteDate(int changePaymenteDateRpcAttempt) {
			if (changePaymenteDateRpcAttempt < 5) {
				setChangePaymenteDateRpcAttempt(changePaymenteDateRpcAttempt + 1);
				
				String values = "" + bmoRaccount.getCustomerId().toInteger() + "|" + receiveDateBox.getTextBox().getValue()
						+ "|" + dueDateBox.getTextBox().getValue();
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getChangePaymenteDateRpcAttempt() < 5)
							changePaymenteDate(getChangePaymenteDateRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-changeDateCredit() ERROR: " + caught.toString());
					}
	
					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setChangePaymenteDateRpcAttempt(0);
						dueDateBox.getTextBox().setText(result.getMsg());
						calculatePaymentDate();
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount,
								BmoRaccount.ACTION_PAYDATE, values, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-changeDateCredit() ERROR: " + e.toString());
				}
			}
		}

		// No borrar, esto ayuda a que se seleccione por defecto el item y no se
		// pueda cambiar de partida, evita margen de error al seleccionar uno que no corresponde.
		//		
		// Actualiza combo de partidas presp. por partida
		// private void populateBudgetItemsByBudgetItem(int budgetItemId) {
		// budgetItemListBox.clear();
		// budgetItemListBox.clearFilters();
		// setBudgetItemsListBoxFiltersByBudgetItem(budgetItemId);
		// budgetItemListBox.populate("" + budgetItemId);
		// }

		// Asigna filtros al listado de partidas presp. por partid
		// private void setBudgetItemsListBoxFiltersByBudgetItem(int budgetItemId) {
		// BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
		//
		// if (budgetItemId > 0) {
		// BmFilter bmFilterByCompany = new BmFilter();
		// bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(),
		// bmoBudgetItem.getIdField(), budgetItemId);
		// budgetItemListBox.addBmFilter(bmFilterByCompany);
		//
		// // Filtro de ingresos(abono)
		// BmFilter filterByDeposit = new BmFilter();
		// filterByDeposit.setValueFilter(bmoBudgetItem.getKind(),
		// bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" +
		// BmoBudgetItemType.TYPE_DEPOSIT);
		// budgetItemListBox.addFilter(filterByDeposit);
		//
		// } else {
		// BmFilter bmFilter = new BmFilter();
		// bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(),
		// "-1");
		// budgetItemListBox.addBmFilter(bmFilter);
		// }
		// }

		// Actualiza combo de partidas presp. por empresa
		private void populateBudgetItems(int companyId) {
			budgetItemListBox.clear();
			budgetItemListBox.clearFilters();
			setBudgetItemsListBoxFilters(companyId);
			budgetItemListBox.populate(bmoRaccount.getBudgetItemId());
		}

		// Asigna filtros al listado de partidas presp. por empresa
		private void setBudgetItemsListBoxFilters(int companyId) {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(),
						companyId);
				budgetItemListBox.addBmFilter(bmFilterByCompany);

				// Filtro de ingresos(abono)
				BmFilter filterByDeposit = new BmFilter();
				filterByDeposit.setValueFilter(bmoBudgetItem.getKind(),
						bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
				budgetItemListBox.addFilter(filterByDeposit);

			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
				budgetItemListBox.addBmFilter(bmFilter);
			}
		}

		private void statusEffect() {
			// Deshabilita campos
			companyListBox.setEnabled(false);
			raccountTypeListBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			invoicenoTextBox.setEnabled(false);
			referenceTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			orderListBox.setEnabled(false);
			totalTextBox.setEnabled(false);
			balanceTextBox.setEnabled(false);
			paymentsTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
			orderListBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			budgetItemListBox.setEnabled(false);
			realtedRaccSuggestBox.setEnabled(false);
			taxAppliesCheckBox.setEnabled(false);
			paymentTypeListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			userCollectorSuggestBox.setEnabled(false);
			serieTextBox.setEnabled(false);
			folioTextBox.setEnabled(false);
			receiveDateBox.setEnabled(false);
			dueDateBox.setEnabled(false);
			dueDateStartDateBox.setEnabled(false);
			reqPayTypeListBox.setEnabled(false);
			// linkedCheckBox.setEnabled(false);
			areaUiListBox.setEnabled(false);

			// De acuerdo al estatus habilitar campos
			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {

				if (newRecord) {
					companyListBox.setEnabled(true);
					raccountTypeListBox.setEnabled(true);
					customerSuggestBox.setEnabled(true);
					currencyListBox.setEnabled(true);
					raccountTypeListBox.setVisible(true);
					realtedRaccSuggestBox.setEnabled(true);
				}

				descriptionTextArea.setEnabled(true);
				realtedRaccSuggestBox.setEnabled(true);
				paymentTypeListBox.setEnabled(true);
				referenceTextBox.setEnabled(true);
				serieTextBox.setEnabled(true);
				folioTextBox.setEnabled(true);
				taxAppliesCheckBox.setEnabled(true);
				receiveDateBox.setEnabled(true);
				dueDateBox.setEnabled(true);
				reqPayTypeListBox.setEnabled(true);

				if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_CHANGECOLLECTORUSER))
					userCollectorSuggestBox.setEnabled(true);

				// Habilitar texto de paridad?
				if (currencyListBox.getSelectedId() != ((BmoFlexConfig) getSFParams().getBmoAppConfig())
						.getSystemCurrencyId().toString())
					if (bmoOrder.getCoverageParity().toBoolean() && bmoOrder.getCurrencyId().toInteger() == Integer
							.parseInt(currencyListBox.getSelectedId())) {
						currencyParityTextBox.setEnabled(false);
					} else {
						currencyParityTextBox.setEnabled(true);
					}
				else
					currencyParityTextBox.setEnabled(false);

				// Control presupuestal
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					budgetItemListBox.setEnabled(true);
					areaUiListBox.setEnabled(true);
				}

				// Si esta el tipo de cuenta
				BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
				if (bmoRaccountType == null)
					bmoRaccountType = bmoRaccount.getBmoRaccountType();

				if (bmoRaccountType != null || newRecord) {
					if (bmoRaccountType.getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
						if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER))
							linkedCheckBox.setEnabled(true);
						else if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_LINKED))
							linkedCheckBox.setEnabled(true);
						else {
							linkedCheckBox.setEnabled(false);
						}

					} else {
						if (bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
							linkedCheckBox.setEnabled(true);
						} else {
							linkedCheckBox.setEnabled(false);
						}
					}

					if (customerSuggestBox.getSelectedId() > 0) {
						if (newRecord && bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER)) {
							orderListBox.setEnabled(true);
							if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem()
									.toBoolean()) {
								if (bmoRaccount.getOrderId().toInteger() > 0
										&& bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
									budgetItemListBox.setEnabled(false);
									areaUiListBox.setEnabled(true);
								} else {
									budgetItemListBox.setEnabled(true);
									areaUiListBox.setEnabled(true);
								}
							}
						} else if (newRecord && bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_OTHER)) {
							if (bmoRaccountType.getType().equals(BmoRaccountType.TYPE_DEPOSIT)) {
								if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem()
										.toBoolean()) {
									budgetItemListBox.setEnabled(false);
									areaUiListBox.setEnabled(false);
								} else {
									budgetItemListBox.setEnabled(true);
									areaUiListBox.setEnabled(true);
								}
							}
						} else if (newRecord
								&& bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
							orderListBox.setEnabled(true);
						} else {
							taxAppliesCheckBox.setEnabled(true);
						}
					}
				}
			} else if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_AUTHORIZED)) {
				// Mostrar el pago automatico solo para cxc ligadas al tipo de pedido crédito
//				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
//					if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getAutomaticPaymentCxC().toBoolean()) {
//						if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
//							if (!bmoRaccount.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL)) {
//								buttonPanel.setHeight("100%");
//								buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//								buttonPanel.add(paymentButton);
//							}
//						}
//					}
//				}

				if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
					if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_ORDER))
						linkedCheckBox.setEnabled(true);
					else if (getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_LINKED))
						linkedCheckBox.setEnabled(true);
					else
						linkedCheckBox.setEnabled(false);
				} else {
					if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
						linkedCheckBox.setEnabled(true);
					} else {
						linkedCheckBox.setEnabled(false);
					}
				}
				
				// Habilitar fecha de programacion pago si no esta pagada totalmente
				if (bmoRaccount.getPaymentStatus().toChar() == BmoRaccount.PAYMENTSTATUS_PENDING)
					dueDateBox.setEnabled(true);
			} else if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_CANCELLED)) {
				linkedCheckBox.setEnabled(false);
				remindDateBox.setEnabled(false);
				commentsTextArea.setEnabled(false);
				statusChargeTextArea.setEnabled(false);
			}

			// if (bmoRaccount.getTaxApplies().toBoolean())
			// taxAppliesCheckBox.setValue(true);
			// else taxAppliesCheckBox.setValue(false);

			BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
			if (bmoRaccountType == null)
				bmoRaccountType = bmoRaccount.getBmoRaccountType();

			if (!newRecord && getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_CHANGESTATUS)) {
				if (bmoRaccountType != null) {
					if (bmoRaccount.getBalance().toDouble() == bmoRaccount.getTotal().toDouble()) {
						statusListBox.setEnabled(true);
					} else if ((bmoRaccount.getBalance().toDouble() < bmoRaccount.getTotal().toDouble())
							&& bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
						statusListBox.setEnabled(true);
					}
				}
			}

			// Bloquear partida si esta seleccionado el pedido y es
			// if
			// (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean())
			// {
			// if
			// (bmoRaccountType.getCategory().toString().equals(""+BmoRaccountType.CATEGORY_CREDITNOTE)
			// ||
			// bmoRaccountType.getCategory().toString().equals(""+BmoRaccountType.CATEGORY_ORDER))
			// {
			// if (Integer.parseInt(orderListBox.getSelectedId()) > 0) {
			// budgetItemListBox.setEnabled(false);
			// }
			// }
			// }

			setAmount(bmoRaccount);

			// Si el pedido esta asignado bloquea empresa, cliente y pedido
			if (bmoRaccount.getOrderId().toInteger() > 0) {
				companyListBox.setEnabled(false);
				customerSuggestBox.setEnabled(false);
				orderListBox.setEnabled(false);
			}

			if (!bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
				companyListBox.setEnabled(false);
				raccountTypeListBox.setEnabled(false);
				customerSuggestBox.setEnabled(false);
				invoicenoTextBox.setEnabled(false);
				referenceTextBox.setEnabled(false);
				descriptionTextArea.setEnabled(false);
				orderListBox.setEnabled(false);
				totalTextBox.setEnabled(false);
				balanceTextBox.setEnabled(false);
				paymentsTextBox.setEnabled(false);
				orderListBox.setEnabled(false);
				currencyListBox.setEnabled(false);
				budgetItemListBox.setEnabled(false);
				areaUiListBox.setEnabled(false);
				realtedRaccSuggestBox.setEnabled(false);
				taxAppliesCheckBox.setEnabled(false);
			}

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);

			if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)) {
				currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(true);
			} else {
				currencyListBox.setEnabled(false);
				currencyParityTextBox.setEnabled(false);
			}
		}

		// Muestra la ventana de Seguimiento del Documento
		public void showLifeCycleDialog() {
			// Es de tipo forma de dialogo
			lifeCycleDialogBox = new DialogBox(true);
			lifeCycleDialogBox.setGlassEnabled(true);
			lifeCycleDialogBox.setText("Seguimiento del Documento");

			// Actualiza seguimiento a documento
			uiLifeCycleViewModel = new UiOrderLifeCycleViewModel(lifeCycleSelection,
					bmoRaccount.getOrderId().toInteger());
			lifeCycleCellTree = new CellTree(uiLifeCycleViewModel, lifeCycleSelection);
			lifeCycleCellTree.setSize("100%", "100%");
			lifeCycleCellTree.setAnimationEnabled(true);

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

		@Override
		public void close() {
			if (deleted) list();
//			showSystemMessage("tipo-close: "+ getUiParams().getUiType(getBmObject().getProgramCode()) + " |ob;"+getBmObject().getProgramCode());
			//list();

//			UiRaccount uiRaccountList;
//			if (bmoOrder.getId() > 0) {
//				uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
//				showSystemMessage("a");
//			}else {
//				UiCustomerDetail uiCustomerDetail;
//				
//				//if (isMinimalist()) showSystemMessage("mini: "+customerId);
//				if (customerId > 0) {
//						uiCustomerDetail = new UiCustomerDetail(getUiParams(), customerId);
//						uiCustomerDetail.show();
//				} else {
//					showSystemMessage("b");
//					uiRaccountList = new UiRaccount(getUiParams());
//					uiRaccountList.show();
//				}
//			}
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				UiRaccountForm uiRaccountForm;
				if (bmoOrder.getId() > 0)
					uiRaccountForm = new UiRaccountForm(getUiParams(), getBmObject().getId(), bmoOrder);
				else
					uiRaccountForm = new UiRaccountForm(getUiParams(), getBmObject().getId());
				uiRaccountForm.show();
			} else {
				UiRaccount uiRaccountList;
				if (bmoOrder.getId() > 0)
					uiRaccountList = new UiRaccount(getUiParams(), bmoOrder);
				else
					uiRaccountList = new UiRaccount(getUiParams());
				uiRaccountList.show();
			}
		}
		
		private void updateAmount(int raccId) {
			updateAmount(raccId, 0);
		}

		private void updateAmount(int raccId, int raccIdRpcAttempt) {
			if (raccIdRpcAttempt < 5) {
				setRaccIdRpc(raccId);
				setRaccIdRpcAttempt(raccIdRpcAttempt + 1);
				
				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getRaccIdRpcAttempt() < 5) {
							updateAmount(getRaccIdRpc(), getRaccIdRpcAttempt());
						}else {
							showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmObject result) {
						stopLoading();
						setRaccIdRpcAttempt(0);
						setAmount((BmoRaccount) result);
					}
				};
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().get(bmoRaccount.getPmClass(), id, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
				}
			}
		}

		private void setAmount(BmoRaccount bmoRaccount) {
			numberFormat = NumberFormat.getCurrencyFormat();
			taxTextBox.setText(numberFormat.format(bmoRaccount.getTax().toDouble()));
			paymentsTextBox.setText(numberFormat.format(bmoRaccount.getPayments().toDouble()));
			balanceTextBox.setText(numberFormat.format(bmoRaccount.getBalance().toDouble()));
			if (bmoRaccount.getAmount().toDouble() > 0)
				amountTextBox.setText("" + numberFormat.format(bmoRaccount.getAmount().toDouble()));
			else
				amountTextBox.setText("" + numberFormat.format(bmoRaccount.getTotal().toDouble()));
			totalTextBox.setText("" + numberFormat.format(bmoRaccount.getTotal().toDouble()));

		}

//		private void realtedRacc(BmoOrder bmoOrder) {
//			// Filtros de ordenes de compra
//			BmoRaccountType bmoRaccountType = (BmoRaccountType) raccountTypeListBox.getSelectedBmObject();
//			if (bmoRaccountType == null)
//				bmoRaccountType = bmoRaccount.getBmoRaccountType();
//
//			if (bmoOrder == null) {
//				bmoOrder = (BmoOrder) orderListBox.getSelectedBmObject();
//				getOrder(bmoOrder.getId());
//			} 
//		}

		// Rellenar Descripcion con el tipo de cxc
		private void populateGeneralInfo(BmoRaccountType bmoRaccountType) {

			if (bmoRaccountType == null)
				bmoRaccountType = bmoRaccount.getBmoRaccountType();

			if (newRecord)
				if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableAutoFill().toInteger() > 0)
					descriptionTextArea.setText(bmoRaccountType.getName().toString());

			//realtedRacc(bmoOrder);
		}

		// Actualiza combo de empresas
		private void populateCompany(int companyId, char typeCxC) {
			companyListBox.clear();
			companyListBox.clearFilters();
			setCompanyListBoxFilters(companyId, typeCxC);
			companyListBox.populate("" + companyId);
		}

		// Asigna filtros al listado de empresas
		private void setCompanyListBoxFilters(int companyId, char typeCxC) {
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter filterCompany = new BmFilter();

			if (companyId > 0) {
				filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyListBox.addFilter(filterCompany);
			} else {
				// Si es de tipo Pedido, aplica filtros de empresas,
				// sino no lo reinicia, esto para la seleccion manual de la empresa y llenar
				// partidas presp.
				if (typeCxC == BmoRaccountType.CATEGORY_ORDER) {
					filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
					companyListBox.addFilter(filterCompany);
				}
			}

		}

		// Actualiza combo de pedidos
		private void populateOrder(int customerId) {
			orderListBox.clear();
			orderListBox.clearFilters();
			setOrderListBoxFilters(customerId);
			orderListBox.populate("" + bmoRaccount.getOrderId(), false);
		}

		// Asigna filtros al listado de pedidos
		private void setOrderListBoxFilters(int customerId) {
			BmoOrder bmoOrder = new BmoOrder();

			if (customerId > 0) {
				BmFilter bmFilterOrder = new BmFilter();
				BmFilter bmFilterByCustomer = new BmFilter();
				BmFilter bmFilterByCompany = new BmFilter();

				// Se rehacen filtros
				if (bmoRaccount.getStatus().equals(BmoRaccount.STATUS_REVISION)
						&& !(bmoRaccount.getOrderId().toInteger() > 0)) {
					bmFilterOrder.setValueOperatorFilter(bmoOrder.getKind(), bmoOrder.getPaymentStatus(),
							BmFilter.NOTEQUALS, "" + BmoOrder.PAYMENTSTATUS_TOTAL);
				} else {
					bmFilterOrder.setValueFilter(bmoOrder.getKind(), bmoOrder.getIdField(),
							bmoRaccount.getOrderId().toInteger());
				}

				bmFilterByCustomer.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), customerId);

				if (multiCompany) {
					int companyId = 0;
					if (companyListBox.getSelectedId().equalsIgnoreCase("") || companyListBox.getSelectedId() == null)
						companyId = 0;
					else 
						companyId = Integer.parseInt(companyListBox.getSelectedId());
					if (companyId > 0)
						bmFilterByCompany.setValueFilter(bmoOrder.getKind(), bmoOrder.getCompanyId(), companyId);
				}
				
				orderListBox.addBmFilter(bmFilterByCustomer);
				orderListBox.addBmFilter(bmFilterOrder);
				if (multiCompany) 
					orderListBox.addBmFilter(bmFilterByCompany);

			} else {
				BmFilter bmFilterByCustomer = new BmFilter();
				bmFilterByCustomer.setValueFilter(bmoOrder.getKind(), bmoOrder.getCustomerId(), -1);
				orderListBox.addBmFilter(bmFilterByCustomer);
				if (!multiCompany)
					populateCompany(-1, ' ');
			}
		}

		// Obtener la paridad de la moneda
		private void populateParityFromCurrency(String currencyId) {
			if (bmoOrder.getId() > 0) {
				if (!bmoOrder.getCoverageParity().toBoolean()) {
					getParityFromCurrency(currencyId);
				} else {
					try {
						if (bmoOrder.getCoverageParity().toBoolean()
								&& bmoOrder.getCurrencyId().toInteger() == Integer.parseInt(currencyId)) {
							bmoRaccount.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
						} else {
							getParityFromCurrency(currencyId);
						}
					} catch (BmException e) {
						showErrorMessage("Error al aplicar el Tipo de Cambio desde el pedido");
					}
					formFlexTable.showField(bmoRaccount.getCurrencyParity());
				}
			} else {
				getParityFromCurrency(currencyId);
			}
		}

		// Calcular fecha de programacion pago
		private void calculatePaymentDate() {
			BmoReqPayType bmoReqPayType = (BmoReqPayType) reqPayTypeListBox.getSelectedBmObject();

			if (bmoReqPayType != null) {
				// Calcular sobre la fecha de registro de factura
				if (!dueDateBox.getTextBox().getValue().equals("")) {
					Date dueDate = new Date();
					dueDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
							.parse(receiveDateBox.getTextBox().getValue());
					CalendarUtil.addDaysToDate(dueDate, bmoReqPayType.getDays().toInteger());
					dueDateBox.getDatePicker().setValue(dueDate);
					dueDateBox.getTextBox().setValue(GwtUtil.dateToString(dueDate, getSFParams().getDateFormat()));
					dueDateStartDateBox.getTextBox()
							.setValue(GwtUtil.dateToString(dueDate, getSFParams().getDateFormat()));
				}
			} else {
				// NO hay termino de pago regresar a la fechas guardadas
				if (!newRecord)
					dueDateBox.getTextBox().setValue(bmoRaccount.getDueDate().toString());
				dueDateStartDateBox.getTextBox().setValue(bmoRaccount.getDueDateStart().toString());
			}
		}

		public void getBudgetItemByOrder(int orderBudgetItemId) {
			budgetItemListBox.setSelectedId("" + orderBudgetItemId);
		}

		public void getAreaByOrder(int orderAreaId) {
			areaUiListBox.setSelectedId("" + orderAreaId);
		}

		// Obtiene la paridad de acuerdo con la moneda elegida
		public void getParityFromCurrency(String currencyIdRpc) {
			getParityFromCurrency(currencyIdRpc, 0);	
		}
		
		// Obtiene la paridad de acuerdo con la moneda elegida
		public void getParityFromCurrency(String currencyIdRpc, int currencyIdRpcAttempt) {
			if (currencyIdRpcAttempt < 5) {
				setCurrencyIdRpc(currencyIdRpc);
				setCurrencyIdRpcAttempt(currencyIdRpcAttempt + 1);

				BmoCurrency bmoCurrency = new BmoCurrency();
				String startDate = "";
	
				startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
	
				String actionValues = currencyIdRpc + "|" + startDate;
	
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getCurrencyIdRpcAttempt() < 5) {
							getParityFromCurrency(getCurrencyIdRpc(), getCurrencyIdRpcAttempt());
						}else {
							showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
						}
					}
	
					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setCurrencyIdRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Tipo de Cambio");
						else
							currencyParityTextBox.setValue(result.getMsg());
					}
				};
	
				try {
					if (!isLoading()) {
						getUiParams().getBmObjectServiceAsync().action(bmoCurrency.getPmClass(), bmoCurrency,
								BmoCurrency.ACTION_GETCURRENCYPARITY, "" + actionValues, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + e.toString());
				}
			}
		}
		
		// Filtrar clientes por empresa
		private void populateCustomers(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				customerSuggestBox.clear();
				setCustomerListBoxFilters(companyId, multiCompany);
			}
		}

		// Filtrar clientes por empresa 
		private void setCustomerListBoxFilters(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
				BmFilter filterCustomer = new BmFilter();
				BmoCustomer bmocustomer = new BmoCustomer();
				filterCustomer.setInFilter(bmoCustomerCompany.getKind(), 
						bmocustomer.getIdFieldName(),
						bmoCustomerCompany.getCustomerId().getName(),
						bmoCustomerCompany.getCompanyId().getName(),
						"" + companyId);	
				customerSuggestBox.addFilter(filterCustomer);
			}
		}
		
		// Filtrar vendedores por perfil/empresa
		private void populateUsersCollect(int companyId, boolean multiCompany) {
			userCollectorSuggestBox.clear();
			if (multiCompany && companyId > 0) {
				setProfileCollectByCompanyIdFilters("" + companyId, multiCompany);
			} else {
				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
				setUserCollectorFilters("" + salesProfileId);
			}
		}

		// Filtrar usuarios por perfil de vendedores 
		private void setUserCollectorFilters(String profileCollectId) {
			// Filtrar por grupo de cobranza, desde conf.
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterProfile = new BmFilter();
//			int collectorGroupId
//			int collectorGroupId = ((BmoFlexConfig) getUiParams().getSFParams().getBmoAppConfig())
//					.getCollectProfileId().toInteger();
			filterProfile.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(), 
					"" + profileCollectId);
			userCollectorSuggestBox.addFilter(filterProfile);
			
			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userCollectorSuggestBox.addFilter(filterSalesmenActive);
		}
		
		// Buscar perfil del vendedor POR empresa
		public void setProfileCollectByCompanyIdFilters(String companyId, boolean multiCompany) {
			if (multiCompany)
				searchProfileCollectByCompanyId(companyId, multiCompany);
			else {
				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
				setUserCollectorFilters("" + salesProfileId);
			}
		};
		
		// Buscar perfil del vendedor POR empresa
		public void searchProfileCollectByCompanyId(String companyId, boolean multiCompany) {
			searchProfileCollectByCompanyId(companyId, multiCompany, 0);
		};

		// Buscar perfil del vendedor POR empresa
		public void searchProfileCollectByCompanyId(String companyId, boolean multiCompany, int profileCollectRpcAttempt) {
			if (profileCollectRpcAttempt < 5) {
				setCompanyId(companyId);
				setProfileCollectRpcAttempt(profileCollectRpcAttempt + 1);
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getProfileCollectRpcAttempt() < 5) {
							searchProfileCollectByCompanyId(getCompanyId(), multiCompany, getProfileCollectRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-searchProfileSalesmanByCompanyId() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();	
						setProfileCollectRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Perfil de la Empresa.");
						else {
							// Forzar la multempresa, aplicar filtros
							setUserCollectorFilters(result.getMsg());
						}
					}
				};

				try {	
					startLoading();
					BmoFlexConfig bmoFlexConfig = new BmoFlexConfig();
					getUiParams().getBmObjectServiceAsync().action(bmoFlexConfig.getPmClass(), bmoFlexConfig, BmoFlexConfig.ACTION_SEARCHCOLLECTPROFILE, "" + companyId, callback);


				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-searchProfileSalesmanByCompanyId() ERROR: " + e.toString());
				}
			}
		}

		public void taxAppliesChange() {
			try {
				bmoRaccount.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
				saveAmountChange();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
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
							showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + caught.toString());
					}
	
					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setSaveAmountChangeRpcAttempt(0);
						processRaccountUpdateResult(result);
					}
				};
	
				// Llamada al servicio RPC
				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().save(bmoRaccount.getPmClass(), bmoRaccount, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + e.toString());
				}
			}
		}

		public void partialDueDialogBoxParity() {
			partialDueDialogBox = new DialogBox(true);
			partialDueDialogBox.setGlassEnabled(true);
			partialDueDialogBox.setText("Pagos");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "200px");

			partialDueDialogBox.setWidget(vp);

			UiPartialDueForm uiPartialDueForm = new UiPartialDueForm(getUiParams(), vp, bmoRaccount);
			uiPartialDueForm.show();

			partialDueDialogBox.center();
			partialDueDialogBox.show();
		}

		public void processRaccountUpdateResult(BmUpdateResult result) {
			if (result.hasErrors())
				showFormMsg("Error al actualizar montos.", "Error al actualizar montos: " + result.errorsToString());
			else
				updateAmount(id);
		}

		protected class RaccountUpdater {
			public void changeRaccount() {
				stopLoading();
				reset();
			}
		}

		private class UiPartialDueForm extends Ui {
			private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

			BmoRaccount bmoRaccountDue = new BmoRaccount();
			private HorizontalPanel buttonPanelDialog = new HorizontalPanel();
			private Button saveDueButton = new Button("APLICAR");
			private Button closeDueButton = new Button("CERRAR");

			// Creacion de BmField
			BmField amountField;
			BmField dueDateField;
			protected UiDateBox dueDateBox = new UiDateBox();
			protected TextBox amountTextBox = new TextBox();

			public UiPartialDueForm(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount) {
				super(uiParams, defaultPanel);
				this.bmoRaccountDue = bmoRaccount;
				amountField = new BmField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
				dueDateField = new BmField("duedate", "", "F.Pago", 10, Types.DATE, false, BmFieldType.DATE, false);

				saveDueButton.setStyleName("formSaveButton");
				saveDueButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						generateDuePayment();
					}
				});

				closeDueButton.setStyleName("formCloseButton");
				closeDueButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						partialDueDialogBox.hide();
					}
				});

				buttonPanelDialog.add(saveDueButton);
				buttonPanelDialog.add(closeDueButton);

				defaultPanel.add(formTable);
			}

			@Override
			public void show() {
				try {
					dueDateField.setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					amountField.setValue(bmoRaccountDue.getBalance().toDouble());
				} catch (BmException e) {

					e.printStackTrace();
				}

				formTable.addLabelField(1, 0, bmoRaccountDue.getCode());
				formTable.addLabelField(2, 0, bmoRaccountDue.getBmoCustomer().getDisplayName());
				formTable.addField(3, 0, dueDateBox, dueDateField);
				formTable.addField(4, 0, amountTextBox, amountField);
				formTable.addButtonPanel(buttonPanelDialog);
			}

			// Generar el Pago
			public void generateDuePayment() {

				saveDueButton.setEnabled(false);
				if (bmoRaccountDue != null) {
					String dueDate = dueDateBox.getTextBox().getText();

					String actionValues = bmoRaccountDue.getId() + "|" + dueDate + "|" + amountTextBox.getText() + "|";

					AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
						@Override
						public void onFailure(Throwable caught) {
							stopLoading();
							showErrorMessage(
									this.getClass().getName() + "-generatePayment() ERROR: " + caught.toString());
						}

						@Override
						public void onSuccess(BmUpdateResult result) {
							stopLoading();
							if (result.hasErrors()) {
								showErrorMessage("Existen Errores " + result.errorsToString());
							} else {
								showSystemMessage("Generación de Pago Exitosa.");
							}
							saveDueButton.setEnabled(true);
							partialDueDialogBox.hide();
							dialogClose();
							showList();
						}
					};

					try {
						if (!isLoading()) {
							startLoading();
							getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount,
									BmoRaccount.ACTION_PAYMENTAPP, "" + actionValues, callback);
						}
					} catch (SFException e) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-generatePayment() ERROR: " + e.toString());
					}
				}
			}
		}

		// Obtiene objeto del servicio
		public void getOrder(int orderIdRpc) {
			getOrder(orderIdRpc, 0);
		}

		// Obtiene objeto del servicio
		public void getOrder(int orderIdRpc, int orderIdRpcAttempt) {
			if (orderIdRpcAttempt < 5) {
				setOrderIdRpc(orderIdRpc);
				setOrderIdRpcAttempt(orderIdRpcAttempt + 1);
				
				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getOrderIdRpcAttempt() < 5) 
							getOrder(getOrderIdRpc(), getOrderIdRpcAttempt());
						else	
							showErrorMessage(this.getClass().getName() + "-getOrder() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmObject result) {
						stopLoading();
						setOrderIdRpcAttempt(0);
						//setBmObject((BmObject) result);
						BmoOrder bmoOrder = (BmoOrder)result;
						showPaymentButton(bmoOrder);
					}
				};
	
				// Llamada al servicio RPC
				try {
					getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), orderIdRpc, callback);
				} catch (SFException e) {
					showErrorMessage(this.getClass().getName() + "-getOrder() ERROR: " + e.toString());
				}
			}
		}
		
		// Pagar la cxc. Para arrendamiento se paga con nota de credito
		private void payRacc() {		
			String values = "";
			values = bmoRaccount.getId() + "|" + bmoRaccount.getDueDate().toString() + "|" + bmoRaccount.getAmount().toDouble();

			if (!getSFParams().hasSpecialAccess(BmoRaccount.ACCESS_PAYMENTRACC))  {
				showSystemMessage("<b>No tiene Permiso de aplicar pagos a la(s) CxC.</b>");
			} else {
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					
					public void onFailure(Throwable caught) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-payRacc() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						if (!result.hasErrors()) {
							// Volver a cargar la forma
							formDialogBox.hide();
							showList();
							edit(bmoRaccount.getId());
							showSystemMessage("Generación de Cobro Exitosa. (" + bmoRaccount.getCode().toString() + ")");
						} else {
							showErrorMessage("Error al realizar el cobro de la CxC: " + result.errorsToString());
						}
					}
				};

				try {
					if (!isLoading()) {
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_PAYMENTAPP, values, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-payRacc() ERROR: " + e.toString());
				}
			}
		}
		
		// Variables para llamadas RPC
		public int getRaccIdRpc() { // le puse rpc al final para que no haya confusion
			return raccIdRpc;
		}

		public void setRaccIdRpc(int raccIdRpc) {
			this.raccIdRpc = raccIdRpc;
		}

		public int getRaccIdRpcAttempt() {
			return raccIdRpcAttempt;
		}

		public void setRaccIdRpcAttempt(int raccIdRpcAttempt) {
			this.raccIdRpcAttempt = raccIdRpcAttempt;
		}
		
		public String getCurrencyIdRpc() {
			return currencyIdRpc;
		}

		public void setCurrencyIdRpc(String currencyIdRpc) {
			this.currencyIdRpc = currencyIdRpc;
		}

		public int getCurrencyIdRpcAttempt() {
			return currencyIdRpcAttempt;
		}

		public void setCurrencyIdRpcAttempt(int currencyIdRpcAttempt) {
			this.currencyIdRpcAttempt = currencyIdRpcAttempt;
		}
		
		public int getChangePaymenteDateRpcAttempt() {
			return changePaymenteDateRpcAttempt;
		}

		public void setChangePaymenteDateRpcAttempt(int changePaymenteDateRpcAttempt) {
			this.changePaymenteDateRpcAttempt = changePaymenteDateRpcAttempt;
		}
		
		public int getOrderIdRpc() {
			return orderIdRpc;
		}

		public void setOrderIdRpc(int orderIdRpc) {
			this.orderIdRpc = orderIdRpc;
		}

		public int getOrderIdRpcAttempt() {
			return orderIdRpcAttempt;
		}

		public void setOrderIdRpcAttempt(int orderIdRpcAttempt) {
			this.orderIdRpcAttempt = orderIdRpcAttempt;
		}
		
		public int getSaveAmountChangeRpcAttempt() {
			return saveAmountChangeRpcAttempt;
		}

		public void setSaveAmountChangeRpcAttempt(int saveAmountChangeRpcAttempt) {
			this.saveAmountChangeRpcAttempt = saveAmountChangeRpcAttempt;
		}
		
		// MUltiempresa: g100
		public String getCompanyId() {
			return companyId;
		}

		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		
		public int getProfileCollectRpcAttempt() {
			return profileCollectRpcAttempt;
		}

		public void setProfileCollectRpcAttempt(int profileCollectRpcAttempt) {
			this.profileCollectRpcAttempt = profileCollectRpcAttempt;
		}
	}
}