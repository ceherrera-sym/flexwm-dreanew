/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.dash;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.flexwm.client.op.UiConsultancyDetail;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoConsultancy;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiDashboard;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDetailLabelFlexTable;
import com.symgae.client.ui.UiDialogLookupClickHandler;
import com.symgae.client.ui.UiFilterChangeHandler;
import com.symgae.client.ui.UiFilterDateRangeChangeHandler;
import com.symgae.client.ui.UiFilterDateRangeTextChangeHandler;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListFlexTable;
import com.symgae.client.ui.UiLookup;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;


public class UiConsultancyDash extends UiDashboard {
	private FlexTable dashFlexTable = new FlexTable();
	UiDetailLabelFlexTable totalsLabelTable = new UiDetailLabelFlexTable(getUiParams());
	BmoConsultancy bmoConsultancy = new BmoConsultancy();

	int initialRpcAttempt = 0;
	VerticalPanel initialVP = new VerticalPanel();

	Label currencyLabel = new Label("Moneda");
	Label currencyCodeText = new Label("");
	Label currencyParityLabel = new Label("Tipo de Cambio");
	Label currencyParityText = new Label("");

	Label initialLabel = new Label("");
	ArrayList<BmObject> initialList = new ArrayList<BmObject>();
	DialogBox initialDialogBox = new DialogBox();
	Label initialTotalLabel = new Label("$ Por Iniciar");
	Label initialTotalText = new Label("$0.00");

	int doingRpcAttempt = 0;
	VerticalPanel doingVP = new VerticalPanel();
	Label doingLabel = new Label("");
	ArrayList<BmObject> doingList = new ArrayList<BmObject>();
	DialogBox doingDialogBox = new DialogBox();
	Label doingTotalLabel = new Label("$ En Proceso");
	Label doingTotalText = new Label("$0.00");

	int doneRpcAttempt = 0;
	VerticalPanel doneVP = new VerticalPanel();
	Label doneLabel = new Label("");
	ArrayList<BmObject> doneList = new ArrayList<BmObject>();
	DialogBox doneDialogBox = new DialogBox();
	Label doneTotalLabel = new Label("$ Terminados");
	Label doneTotalText = new Label("$0.00");

	int holdRpcAttempt = 0;
	VerticalPanel holdVP = new VerticalPanel();
	Label holdLabel = new Label("");
	ArrayList<BmObject> holdList = new ArrayList<BmObject>();
	DialogBox holdDialogBox = new DialogBox();
	Label holdTotalLabel = new Label("$ Retenidos");
	Label holdTotalText = new Label("$0.00");

	private int sumCCRpcAttempt = 0;
	private String statusRpcAttempt = "";
	Label labelTotalText = new Label("$0.00");
	String startDate = "";
	String endDate = "";

	// Filtros
	UiSuggestBox customerSuggestBox;
	UiListBox leaderUserListBox;
	UiListBox assignedUserListBox;
	UiListBox currencyListBox;
	UiListBox areaListBox;

	// Campo para Total de Moneda
	BmField amount = new BmField("amount", "", "Sub Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	BmField total = new BmField("total", "", "Total", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	BmField payments = new BmField("payments", "", "Pagos", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);
	BmField balance = new BmField("balance", "", "Saldo", 20, Types.DOUBLE, true, BmFieldType.CURRENCY, false);

	private NumberFormat numberFormat = NumberFormat.getCurrencyFormat();

	public UiConsultancyDash(UiParams uiParams) {
		super(uiParams, "Tablero Ventas SCRUM", GwtUtil.getProperUrl(uiParams.getSFParams(), "/icons/scrm.png"));
		setBmObject(bmoConsultancy);

		setShowUserInfo(true);
		dashFlexTable.setStyleName("dashboard");
		getUiParams().getUiTemplate().getWestPanel().add(new HTML("&nbsp;"));
		getUiParams().getUiTemplate().getWestPanel().add(totalsLabelTable);

		initialTotalLabel.setStyleName("detailLabel");
		currencyLabel.setStyleName("detailLabel");
		currencyCodeText.setStyleName("detailText");
		currencyParityLabel.setStyleName("detailLabel");
		currencyParityText.setStyleName("detailText");

		initialTotalLabel.setStyleName("detailLabel");
		initialTotalText.setStyleName("detailText");
		initialLabel.setStyleName("programMinimalistSubtitle");
		initialLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showInitialDialog();
			}
		});

		doingTotalLabel.setStyleName("detailLabel");
		doingTotalText.setStyleName("detailText");
		doingLabel.setStyleName("programMinimalistSubtitle");
		doingLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDoingDialog();
			}
		});

		doneTotalLabel.setStyleName("detailLabel");
		doneTotalText.setStyleName("detailText");
		doneLabel.setStyleName("programMinimalistSubtitle");
		doneLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDoneDialog();
			}
		});

		holdTotalLabel.setStyleName("detailLabel");
		holdTotalText.setStyleName("detailText");
		holdLabel.setStyleName("programMinimalistSubtitle");
		holdLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showHoldDialog();
			}
		});
	}

	@Override
	public void populate() {
		clearDP();
		addToDP(dashFlexTable);
		setWest();
	}

	// Agregar iconos
	private void setWest() {
		// Inicio Tablero
		addActionLabel("Inicio", "crmo", new ClickHandler() {
			public void onClick(ClickEvent event) {
				UiConsultancyDash uiConsultancyDash = new UiConsultancyDash(getUiParams());
				uiConsultancyDash.show();
			}
		});
	}

	// Agregar etiqueta de totales
	private void addTotalsLabel() {
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, currencyLabel);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, currencyCodeText);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);

		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, currencyParityLabel);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, currencyParityText);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);

		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, initialTotalLabel);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, initialTotalText);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);

		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, doingTotalLabel);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, doingTotalText);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);

		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, doneTotalLabel);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, doneTotalText);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);

		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, holdTotalLabel);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
		totalsLabelTable.setWidget(totalsLabelTable.getDetailRow(), 0, holdTotalText);
		totalsLabelTable.setDetailRow(totalsLabelTable.getDetailRow() + 1);
	}

	@Override
	public void setDashboard(BmoUser bmoUser) {
		totalsLabelTable.removeAllRows();
		totalsLabelTable.setDetailRow(0);

		// Se desactiva el filtro
		userListBox.setVisible(false);

		addTotalsLabel();

		// Filtros
		HorizontalPanel filterPanel = new HorizontalPanel();

		// Filtrar por vendedores activos
		BmFilter filterUserActive = new BmFilter();
		filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);

		// Filtro de Clientes
		BmoCustomer bmoCustomer = new BmoCustomer();
		customerSuggestBox = new UiSuggestBox(bmoCustomer);
		addFilterSuggestBox(filterPanel, customerSuggestBox, bmoCustomer.getDisplayName());

		// Filtro de lider
		leaderUserListBox = new UiListBox(getUiParams(), bmoUser);
		leaderUserListBox.addFilter(filterUserActive);

		// Filtro usuarios Responsable en detalle de pedido, como no me interesa colocar el where hago que sea 1=1 para que me regrese verdadero
		// ej:  AND user_userid IN (SELECT ordt_leaderuserid FROM orderdetails WHERE 1 = 1)
		BmFilter filterUserLeader = new BmFilter();
		filterUserLeader.setInFilter(bmoConsultancy.getKind(), bmoUser.getIdFieldName(), bmoConsultancy.getLeaderUserId().getName(), "1" , "1");
		leaderUserListBox.addFilter(filterUserLeader);

		addFilterListBox(filterPanel, leaderUserListBox, bmoConsultancy.getLeaderUserId());

		// Filtro de usuario asignado
		assignedUserListBox = new UiListBox(getUiParams(), bmoUser);
		assignedUserListBox.addFilter(filterUserActive);

		// Filtro usuarios Consultor en detalle de pedido, como no me interesa colocar el where hago que sea 1=1 para que me regrese verdadero
		// ej:  AND user_userid IN (SELECT ordt_assigneduserid FROM orderdetails WHERE 1 = 1)
		BmFilter filterAssignedUser = new BmFilter();
		filterAssignedUser.setInFilter(bmoConsultancy.getKind(), bmoUser.getIdFieldName(), bmoConsultancy.getAssignedUserId().getName(), "1" , "1");
		assignedUserListBox.addFilter(filterAssignedUser);
		addFilterListBox(filterPanel, assignedUserListBox, bmoConsultancy.getAssignedUserId());

		// Filtro de Departamento
		BmoArea bmoArea = new BmoArea();
		areaListBox = new UiListBox(getUiParams(), bmoArea);
		addFilterListBox(filterPanel, areaListBox, bmoConsultancy.getAreaId());

		// Filtro de Moneda
		BmoCurrency bmoCurrency = new BmoCurrency();
		currencyListBox = new UiListBox(getUiParams(), bmoCurrency);
		addFilterListBox(filterPanel, currencyListBox, bmoConsultancy.getCurrencyId(), "");

		addDateRangeFilterListBox(filterPanel, bmoConsultancy.getStartDate(), "", "");

		dashFlexTable.setWidget(0, 0, filterPanel);
		dashFlexTable.getFlexCellFormatter().setColSpan(0, 0, 2);

		// Estatus Por Iniciar
		DecoratorPanel initialDP = new DecoratorPanel();
		initialDP.setSize("100%", "300px");
		ScrollPanel initialSP = new ScrollPanel();
		initialSP.setSize("100%", "300px");
		initialVP.setSize("100%", "100%");
		initialDP.setWidget(initialSP);
		initialSP.setWidget(initialVP);
		dashFlexTable.setWidget(1, 0, initialLabel);
		dashFlexTable.setWidget(2, 0, initialDP);

		// Estatus Por En Proceso
		DecoratorPanel doingDP = new DecoratorPanel();
		doingDP.setSize("100%", "300px");
		ScrollPanel doingSP = new ScrollPanel();
		doingSP.setSize("100%", "300px");
		doingVP.setSize("100%", "100%");
		doingDP.setWidget(doingSP);
		doingSP.setWidget(doingVP);
		dashFlexTable.setWidget(1, 1, doingLabel);
		dashFlexTable.setWidget(2, 1, doingDP);

		// Estatus Terminado
		DecoratorPanel doneDP = new DecoratorPanel();
		doneDP.setSize("100%", "300px");
		ScrollPanel doneSP = new ScrollPanel();
		doneSP.setSize("100%", "300px");
		doneVP.setSize("100%", "100%");
		doneDP.setWidget(doneSP);
		doneSP.setWidget(doneVP);
		dashFlexTable.setWidget(3, 0, doneLabel);
		dashFlexTable.setWidget(4, 0, doneDP);

		// Estatus Retenido
		DecoratorPanel holdDP = new DecoratorPanel();
		holdDP.setSize("100%", "300px");
		ScrollPanel holdSP = new ScrollPanel();
		holdSP.setSize("100%", "300px");
		holdVP.setSize("100%", "100%");
		holdDP.setWidget(holdSP);
		holdSP.setWidget(holdVP);
		dashFlexTable.setWidget(3, 1, holdLabel);
		dashFlexTable.setWidget(4, 1, holdDP);

		addToDP(dashFlexTable);

		reset();
	}

	// Resetea tablas de Pedidos
	private void reset() {
		initialVP.clear();
		doingVP.clear();
		doneVP.clear();
		holdVP.clear();

		getInitialList(0);

	}

	// Obtener listado de Detalles de Pedido en estatus por iniciar
	public void getInitialList(int initialRpcAttempt) {
		if (initialRpcAttempt < 5) {
			setInitialRpcAttempt(initialRpcAttempt + 1);

			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					if (getInitialRpcAttempt() < 5) {
						getInitialList(getInitialRpcAttempt());
					} else {
						handleRPCFailure(this.getClass().getName() + "-list()", caught);
					}
				}
				public void onSuccess(ArrayList<BmObject> result) {
					setInitialRpcAttempt(0);
					initialList = result;
					setListData(initialVP, result.iterator(), initialTotalText);

					initialLabel.setText("Por Iniciar " + result.size());

					//  Traer sumatioria de cxc
					getSumCCPendingWithoutLinked(initialTotalText, "" + BmoConsultancy.STATUSSCRUM_INITIAL);

				}
			};
			try {
				if (!isLoading()) {
					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					BmFilter filterByStatus = new BmFilter();
					filterByStatus.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getStatusScrum(), "" + BmoConsultancy.STATUSSCRUM_INITIAL);
					filterList.add(filterByStatus);

					// Filtrar por cliente
					if (customerSuggestBox.getSelectedId() > 0) {
						BmFilter filterByCustomer = new BmFilter();
						filterByCustomer.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getCustomerId(), customerSuggestBox.getSelectedId());
						filterList.add(filterByCustomer);
					}

					// Filtrar por lider
					if (!leaderUserListBox.getSelectedId().equals("") && !leaderUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByLeader = new BmFilter();
						filterByLeader.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getLeaderUserId(), leaderUserListBox.getSelectedId());
						filterList.add(filterByLeader);
					}

					// Filtrar por usuario asignado
					if (!assignedUserListBox.getSelectedId().equals("") && !assignedUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByAssigned = new BmFilter();
						filterByAssigned.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAssignedUserId(), assignedUserListBox.getSelectedId());
						filterList.add(filterByAssigned);
					}

					// Filtrar por departamento
					if (!areaListBox.getSelectedId().equals("") && !areaListBox.getSelectedId().equals("0")) {
						BmFilter filterByArea = new BmFilter();
						filterByArea.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAreaId(), areaListBox.getSelectedId());
						filterList.add(filterByArea);
					}

					// Filtrar solo los pedidos donde este marcado en "filtrar en scrum" en el catalogo de tipos de pedido
					BmoOrderType bmoOrderType = new BmoOrderType();
					BmFilter filterByOrderType = new BmFilter();
					filterByOrderType.setInFilter(bmoOrderType.getKind(), 
							bmoConsultancy.getOrderTypeId().getName(), 
							bmoOrderType.getIdFieldName(), 
							bmoOrderType.getFilterOnScrum().getName(), "1");
					filterList.add(filterByOrderType);

					// Filtro de fechas Inicio
					BmFilter startDateRangeFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmoConsultancy.getStartDate().getName());
					if (!startDateRangeFilter.getKind().equals(""))
						filterList.add(startDateRangeFilter);

					getUiParams().getBmObjectServiceAsync().list(bmoConsultancy.getPmClass(), filterList, callback);
				}

			} catch (SFException e) {
				stopLoading();
				handleRPCException(this.getClass().getName() + "-list()", e);
			}
		} 
	}

	// Muestra el listado en dialogo de Terminadas
	private void showInitialDialog() {
		initialDialogBox = new DialogBox(true);
		initialDialogBox.setGlassEnabled(true);
		initialDialogBox.setText("Pedidos Por Iniciar");

		ScrollPanel sp = new ScrollPanel();
		sp.setSize("667px", "400px");
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("650px", "400px");
		sp.setWidget(vp);
		initialDialogBox.setWidget(sp);

		UiListFlexTable listFlexTable = new UiListFlexTable(getUiParams());

		displayList(listFlexTable, initialList.iterator());

		vp.add(listFlexTable);

		initialDialogBox.center();
		initialDialogBox.show();
	}

	// Obtener listado de Detalles de Pedido en estatus en proceso
	public void getDoingList(int doingRpcAttempt) {
		if (doingRpcAttempt < 5) {
			setDoingRpcAttempt(doingRpcAttempt + 1);

			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					if (getDoingRpcAttempt() < 5) {
						getDoingList(getDoingRpcAttempt());
					} else {
						handleRPCFailure(this.getClass().getName() + "-list()", caught);
					}
				}
				public void onSuccess(ArrayList<BmObject> result) {
					setDoingRpcAttempt(0);
					doingList = result;
					setListData(doingVP, result.iterator(), doingTotalText);

					doingLabel.setText("En Proceso " + result.size());

					getSumCCPendingWithoutLinked(doingTotalText, "" + BmoConsultancy.STATUSSCRUM_DOING);

				}
			};
			try {
				if (!isLoading()) {
					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					BmFilter filterByStatus = new BmFilter();
					filterByStatus.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getStatusScrum(), "" + BmoConsultancy.STATUSSCRUM_DOING);
					filterList.add(filterByStatus);

					// Filtrar por cliente
					if (customerSuggestBox.getSelectedId() > 0) {
						BmFilter filterByCustomer = new BmFilter();
						filterByCustomer.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getCustomerId(), customerSuggestBox.getSelectedId());
						filterList.add(filterByCustomer);
					}

					// Filtrar por lider
					if (!leaderUserListBox.getSelectedId().equals("") && !leaderUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByLeader = new BmFilter();
						filterByLeader.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getLeaderUserId(), leaderUserListBox.getSelectedId());
						filterList.add(filterByLeader);
					}

					// Filtrar por usuario asignado
					if (!assignedUserListBox.getSelectedId().equals("") && !assignedUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByAssigned = new BmFilter();
						filterByAssigned.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAssignedUserId(), assignedUserListBox.getSelectedId());
						filterList.add(filterByAssigned);
					}

					// Filtrar por departamento
					if (!areaListBox.getSelectedId().equals("") && !areaListBox.getSelectedId().equals("0")) {
						BmFilter filterByArea = new BmFilter();
						filterByArea.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAreaId(), areaListBox.getSelectedId());
						filterList.add(filterByArea);
					}

					// Filtrar solo los pedidos donde este marcado en "filtrar en scrum" en el catalogo de tipos de pedido
					BmoOrderType bmoOrderType = new BmoOrderType();
					BmFilter filterByOrderType = new BmFilter();
					filterByOrderType.setInFilter(bmoOrderType.getKind(), 
							bmoConsultancy.getOrderTypeId().getName(), 
							bmoOrderType.getIdFieldName(), 
							bmoOrderType.getFilterOnScrum().getName(), "1");
					filterList.add(filterByOrderType);

					// Filtro de fechas Inicio
					BmFilter startDateRangeFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmoConsultancy.getStartDate().getName());
					if (!startDateRangeFilter.getKind().equals(""))
						filterList.add(startDateRangeFilter);

					getUiParams().getBmObjectServiceAsync().list(bmoConsultancy.getPmClass(), filterList, callback);
				}

			} catch (SFException e) {
				stopLoading();
				handleRPCException(this.getClass().getName() + "-list()", e);
			}
		} 
	}

	// Muestra el listado en dialogo de En Proceso
	private void showDoingDialog() {
		doingDialogBox = new DialogBox(true);
		doingDialogBox.setGlassEnabled(true);
		doingDialogBox.setText("Pedidos En Proceso");

		ScrollPanel sp = new ScrollPanel();
		sp.setSize("667px", "400px");
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("650px", "400px");
		sp.setWidget(vp);
		doingDialogBox.setWidget(sp);

		UiListFlexTable listFlexTable = new UiListFlexTable(getUiParams());

		displayList(listFlexTable, doingList.iterator());

		vp.add(listFlexTable);

		doingDialogBox.center();
		doingDialogBox.show();
	}

	// Obtener listado de Detalles de Pedido en estatus terminado
	public void getDoneList(int doneRpcAttempt) {
		if (doneRpcAttempt < 5) {
			setDoneRpcAttempt(doneRpcAttempt + 1);

			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					if (getDoneRpcAttempt() < 5) {
						getDoneList(getDoneRpcAttempt());
					} else {
						handleRPCFailure(this.getClass().getName() + "-list()", caught);
					}
				}
				public void onSuccess(ArrayList<BmObject> result) {
					setDoneRpcAttempt(0);
					doneList = result;
					setListData(doneVP, result.iterator(), doneTotalText);

					doneLabel.setText("Terminadas " + result.size());

					getSumCCPendingWithoutLinked(doneTotalText, "" + BmoConsultancy.STATUSSCRUM_DONE);

				}
			};
			try {
				if (!isLoading()) {
					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					BmFilter filterByStatus = new BmFilter();
					filterByStatus.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getStatusScrum(), "" + BmoConsultancy.STATUSSCRUM_DONE);
					filterList.add(filterByStatus);

					// Filtrar por cliente
					if (customerSuggestBox.getSelectedId() > 0) {
						BmFilter filterByCustomer = new BmFilter();
						filterByCustomer.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getCustomerId(), customerSuggestBox.getSelectedId());
						filterList.add(filterByCustomer);
					}

					// Filtrar por lider
					if (!leaderUserListBox.getSelectedId().equals("") && !leaderUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByLeader = new BmFilter();
						filterByLeader.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getLeaderUserId(), leaderUserListBox.getSelectedId());
						filterList.add(filterByLeader);
					}

					// Filtrar por usuario asignado
					if (!assignedUserListBox.getSelectedId().equals("") && !assignedUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByAssigned = new BmFilter();
						filterByAssigned.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAssignedUserId(), assignedUserListBox.getSelectedId());
						filterList.add(filterByAssigned);
					}

					// Filtrar por departamento
					if (!areaListBox.getSelectedId().equals("") && !areaListBox.getSelectedId().equals("0")) {
						BmFilter filterByArea = new BmFilter();
						filterByArea.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAreaId(), areaListBox.getSelectedId());
						filterList.add(filterByArea);
					}

					// Filtrar solo los pedidos donde este marcado en "filtrar en scrum" en el catalogo de tipos de pedido
					BmoOrderType bmoOrderType = new BmoOrderType();
					BmFilter filterByOrderType = new BmFilter();
					filterByOrderType.setInFilter(bmoOrderType.getKind(), 
							bmoConsultancy.getOrderTypeId().getName(), 
							bmoOrderType.getIdFieldName(), 
							bmoOrderType.getFilterOnScrum().getName(), "1");
					filterList.add(filterByOrderType);

					// Filtro de fechas Inicio
					BmFilter startDateRangeFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmoConsultancy.getStartDate().getName());
					if (!startDateRangeFilter.getKind().equals(""))
						filterList.add(startDateRangeFilter);

					getUiParams().getBmObjectServiceAsync().list(bmoConsultancy.getPmClass(), filterList, callback);
				}

			} catch (SFException e) {
				stopLoading();
				handleRPCException(this.getClass().getName() + "-list()", e);
			}
		} 
	}

	// Muestra el listado en dialogo de Terminadas
	private void showDoneDialog() {
		doneDialogBox = new DialogBox(true);
		doneDialogBox.setGlassEnabled(true);
		doneDialogBox.setText("Pedidos Terminados");

		ScrollPanel sp = new ScrollPanel();
		sp.setSize("667px", "400px");
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("650px", "400px");
		sp.setWidget(vp);
		doneDialogBox.setWidget(sp);

		UiListFlexTable listFlexTable = new UiListFlexTable(getUiParams());

		displayList(listFlexTable, doneList.iterator());

		vp.add(listFlexTable);

		doneDialogBox.center();
		doneDialogBox.show();
	}

	// Obtener listado de Detalles de Pedido en estatus terminado
	public void getHoldList(int holdRpcAttempt) {
		if (holdRpcAttempt < 5) {
			setHoldRpcAttempt(holdRpcAttempt + 1);

			// Set up the callback object.
			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				public void onFailure(Throwable caught) {
					if (getHoldRpcAttempt() < 5) {
						getHoldList(getHoldRpcAttempt());
					} else {
						handleRPCFailure(this.getClass().getName() + "-list()", caught);
					}
				}
				public void onSuccess(ArrayList<BmObject> result) {
					setHoldRpcAttempt(0);
					holdList = result;
					setListData(holdVP, result.iterator(), holdTotalText);

					holdLabel.setText("Retenidas " + result.size());
					getSumCCPendingWithoutLinked(holdTotalText, "" + BmoConsultancy.STATUSSCRUM_HOLD);
				}
			};
			try {
				if (!isLoading()) {
					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
					BmFilter filterByStatus = new BmFilter();
					filterByStatus.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getStatusScrum(), "" + BmoConsultancy.STATUSSCRUM_HOLD);
					filterList.add(filterByStatus);

					// Filtrar por cliente
					if (customerSuggestBox.getSelectedId() > 0) {
						BmFilter filterByCustomer = new BmFilter();
						filterByCustomer.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getCustomerId(), customerSuggestBox.getSelectedId());
						filterList.add(filterByCustomer);
					}

					// Filtrar por lider
					if (!leaderUserListBox.getSelectedId().equals("") && !leaderUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByLeader = new BmFilter();
						filterByLeader.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getLeaderUserId(), leaderUserListBox.getSelectedId());
						filterList.add(filterByLeader);
					}

					// Filtrar por usuario asignado
					if (!assignedUserListBox.getSelectedId().equals("") && !assignedUserListBox.getSelectedId().equals("0")) {
						BmFilter filterByAssigned = new BmFilter();
						filterByAssigned.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAssignedUserId(), assignedUserListBox.getSelectedId());
						filterList.add(filterByAssigned);
					}

					// Filtrar por departamento
					if (!areaListBox.getSelectedId().equals("") && !areaListBox.getSelectedId().equals("0")) {
						BmFilter filterByArea = new BmFilter();
						filterByArea.setValueFilter(bmoConsultancy.getKind(), bmoConsultancy.getAreaId(), areaListBox.getSelectedId());
						filterList.add(filterByArea);
					}

					// Filtrar solo los pedidos donde este marcado en "filtrar en scrum" en el catalogo de tipos de pedido
					BmoOrderType bmoOrderType = new BmoOrderType();
					BmFilter filterByOrderType = new BmFilter();
					filterByOrderType.setInFilter(bmoOrderType.getKind(), 
							bmoConsultancy.getOrderTypeId().getName(), 
							bmoOrderType.getIdFieldName(), 
							bmoOrderType.getFilterOnScrum().getName(), "1");
					filterList.add(filterByOrderType);

					// Filtro de fechas Inicio
					BmFilter startDateRangeFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmoConsultancy.getStartDate().getName());
					if (!startDateRangeFilter.getKind().equals(""))
						filterList.add(startDateRangeFilter);

					getUiParams().getBmObjectServiceAsync().list(bmoConsultancy.getPmClass(), filterList, callback);
				}

			} catch (SFException e) {
				stopLoading();
				handleRPCException(this.getClass().getName() + "-list()", e);
			}
		} 
	}

	// Muestra el listado en dialogo de Terminadas
	private void showHoldDialog() {
		holdDialogBox = new DialogBox(true);
		holdDialogBox.setGlassEnabled(true);
		holdDialogBox.setText("Pedidos Retenidos");

		ScrollPanel sp = new ScrollPanel();
		sp.setSize("667px", "400px");
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("650px", "400px");
		sp.setWidget(vp);
		holdDialogBox.setWidget(sp);

		UiListFlexTable listFlexTable = new UiListFlexTable(getUiParams());

		displayList(listFlexTable, holdList.iterator());

		vp.add(listFlexTable);

		holdDialogBox.center();
		holdDialogBox.show();
	}

	// Muestra lista de pedidos
	private void setListData(Panel panel, Iterator<BmObject> serviceIterator, Label totalText) {
		//double total = 0; 
		int count = 0;

		while (serviceIterator.hasNext()) {
			BmoConsultancy nextService = (BmoConsultancy)serviceIterator.next();

			Label codeLabel = new Label(nextService.getCode().toString());
			codeLabel.setStyleName("listCellLink");
			codeLabel.addClickHandler(new UiClickHandler(nextService) {
				@Override
				public void onClick(ClickEvent arg0) {
					BmoConsultancy bmoConsultancy = (BmoConsultancy)bmObject;
					UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), bmoConsultancy.getId());
					uiConsultancyDetail.show();
				}
			});

			Label nameLabel = new Label(nextService.getName().toString());

			Label customerLabel = new Label(nextService.getBmoCustomer().getCode()
					+ " " + nextService.getBmoCustomer().getDisplayName());
			customerLabel.setStyleName("detailLabel");

			HorizontalPanel fp = new HorizontalPanel();
			fp.add(codeLabel);
			fp.add(new HTML("&nbsp;"));
			fp.add(nameLabel);
			fp.add(new HTML("&nbsp;"));
			fp.add(customerLabel);

			panel.add(fp);

			//total += nextOrderDetail.getBmoOrder().getAmount().toDouble();
			count++;

			//			// Hacer conversión de acuerdo a la moneda seleccionada
			//			if (Integer.parseInt(currencyListBox.getSelectedId()) == nextOrderDetail.getBmoOrder().getCurrencyId().toInteger()) {
			//				total += nextOrderDetail.getBmoOrder().getAmount().toDouble();
			//			} else {
			//				total += (nextOrderDetail.getBmoOrder().getAmount().toDouble() * nextOrderDetail.getBmoOrder().getCurrencyParity().toDouble()
			//						/ ((BmoCurrency)currencyListBox.getSelectedBmObject()).getParity().toDouble());
			//			}
		}

		//totalText.setText(numberFormat.format(total));

		if (count == 0) {
			HorizontalPanel fp = new HorizontalPanel();
			fp.add(new HTML("&nbsp;"));
			fp.add(new HTML("Sin Registros..."));
			fp.add(new HTML("&nbsp;"));

			panel.add(fp);
		}

		currencyCodeText.setText(((BmoCurrency)currencyListBox.getSelectedBmObject()).getCode().toString());
		currencyParityText.setText("" + ((BmoCurrency)currencyListBox.getSelectedBmObject()).getParity().toDouble());
	}

	// Despliega lista de Pedidos
	private void displayList(UiListFlexTable listFlexTable, Iterator<BmObject> iterator) {
		int col = 0;

		double amountExchange = 0, totalExchange = 0, paymentsExchange = 0, balanceExchange = 0;

		listFlexTable.addListTitleCell(0, col++, "Clave");
		listFlexTable.addListTitleCell(0, col++, "Nombre");
		listFlexTable.addListTitleCell(0, col++, "Cliente");
		listFlexTable.addListTitleCell(0, col++, "Fecha Inicio");
		listFlexTable.addListTitleCell(0, col++, "Moneda");
		listFlexTable.addListTitleCell(0, col++, "SubTotal");
		listFlexTable.addListTitleCell(0, col++, "Total");
		listFlexTable.addListTitleCell(0, col++, "Pagos");
		listFlexTable.addListTitleCell(0, col++, "Saldo");

		BmoConsultancy nextService = new BmoConsultancy();
		int row = 1;
		while (iterator.hasNext()) {
			col = 0;
			nextService = (BmoConsultancy)iterator.next();

			listFlexTable.addListCell(row, col++, getBmObject(), nextService.getCode());
			listFlexTable.addListCell(row, col++, getBmObject(), nextService.getName());
			listFlexTable.addListCell(row, col++, getBmObject(), nextService.getBmoCustomer().getDisplayName());
			listFlexTable.addListCell(row, col++, getBmObject(), nextService.getStartDate());
			listFlexTable.addListCell(row, col++, getBmObject(), nextService.getBmoCurrency().getCode());

			// Hacer conversión de acuerdo a la moneda seleccionada
			if (Integer.parseInt(currencyListBox.getSelectedId()) == nextService.getCurrencyId().toInteger()) {
				amountExchange = nextService.getAmount().toDouble();
				totalExchange = nextService.getTotal().toDouble();
				paymentsExchange = nextService.getPayments().toDouble();
				balanceExchange = nextService.getBalance().toDouble();
			} else {
				amountExchange = (nextService.getAmount().toDouble() * nextService.getCurrencyParity().toDouble()
						/ ((BmoCurrency)currencyListBox.getSelectedBmObject()).getParity().toDouble());
				totalExchange = (nextService.getTotal().toDouble() * nextService.getCurrencyParity().toDouble()
						/ ((BmoCurrency)currencyListBox.getSelectedBmObject()).getParity().toDouble());
				paymentsExchange = (nextService.getPayments().toDouble() * nextService.getCurrencyParity().toDouble()
						/ ((BmoCurrency)currencyListBox.getSelectedBmObject()).getParity().toDouble());
				balanceExchange = (nextService.getBalance().toDouble() * nextService.getCurrencyParity().toDouble()
						/ ((BmoCurrency)currencyListBox.getSelectedBmObject()).getParity().toDouble());
			}
			try {
				amount.setValue(amountExchange);
				total.setValue(totalExchange);
				payments.setValue(paymentsExchange);
				balance.setValue(balanceExchange);
			} catch (BmException e) {
				showErrorMessage("Error al asignar Valores: " +e.toString());				
			}

			listFlexTable.addListCell(row, col++, getBmObject(), amount);
			listFlexTable.addListCell(row, col++, getBmObject(), total);
			listFlexTable.addListCell(row, col++, getBmObject(), payments);
			listFlexTable.addListCell(row, col++, getBmObject(), balance);

			listFlexTable.formatRow(row);
			row++;
		}
	}

	// Obtener suma de cxc pendientes de cobro y NO externas
	public void getSumCCPendingWithoutLinked(Label totalLabel, String status) {
		getSumCCPendingWithoutLinked(totalLabel, status, 0);
	}

	// Obtener suma de cxc pendientes de cobro y NO externas
	public void getSumCCPendingWithoutLinked(Label totalLabel, String status, int sumCCRpcAttempt) {
		if (sumCCRpcAttempt < 5) {
			setStatusRpcAttempt(status);
			setLabelTotalTextRpcAttempt(totalLabel);

			setSumCCRpcAttempt(sumCCRpcAttempt + 1);
			BmoRaccount bmoRaccount = new BmoRaccount();

			if (startDate.equals("")) startDate = "0";
			if (endDate.equals("")) endDate = "0";
			String values = status + "|" + 
					customerSuggestBox.getSelectedId()+ "|" + 
					leaderUserListBox.getSelectedId()+ "|" + 
					assignedUserListBox.getSelectedId() + "|" +
					areaListBox.getSelectedId() + "|" +
					currencyListBox.getSelectedId() + "|"+ 
					startDate + "|" + endDate;

			// Set up the callback object.
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {

				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSumCCRpcAttempt() < 5) {
						getSumCCPendingWithoutLinked(getLabelTotalTextRpcAttempt(), getStatusRpcAttempt(), getSumCCRpcAttempt());
					} else {
						handleRPCFailure(this.getClass().getName() + "-getSumCCPendingWithoutLinked()", caught);
					}
				}
				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSumCCRpcAttempt(0);
					getLabelTotalTextRpcAttempt().setText(numberFormat.format(Double.parseDouble(result.getMsg())));

					if (getStatusRpcAttempt().equals("" + BmoConsultancy.STATUSSCRUM_INITIAL)) {
						getDoingList(0);
//						initialTotalText.setText(result.getMsg());
					} if (getStatusRpcAttempt().equals("" + BmoConsultancy.STATUSSCRUM_DOING)) {
//						doingTotalText.setText(result.getMsg());
						getDoneList(0);
					} if (getStatusRpcAttempt().equals("" + BmoConsultancy.STATUSSCRUM_DONE)) {
//						doneTotalText.setText(result.getMsg());
						getHoldList(0);
					} if (getStatusRpcAttempt().equals("" + BmoConsultancy.STATUSSCRUM_HOLD)) {
//						holdTotalText.setText(result.getMsg());
					}
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoRaccount.getPmClass(), bmoRaccount, BmoRaccount.ACTION_SUMCCPENDINGNOLINKED, values, callback);
				}

			} catch (SFException e) {
				stopLoading();
				handleRPCException(this.getClass().getName() + "-list()", e);
			}
		} 
	}

	public int getInitialRpcAttempt() {
		return initialRpcAttempt;
	}

	public void setInitialRpcAttempt(int initialRpcAttempt) {
		this.initialRpcAttempt = initialRpcAttempt;
	}

	public int getDoingRpcAttempt() {
		return doingRpcAttempt;
	}

	public void setDoingRpcAttempt(int doingRpcAttempt) {
		this.doingRpcAttempt = doingRpcAttempt;
	}

	public int getDoneRpcAttempt() {
		return doneRpcAttempt;
	}

	public void setDoneRpcAttempt(int doneRpcAttempt) {
		this.doneRpcAttempt = doneRpcAttempt;
	}

	public int getHoldRpcAttempt() {
		return holdRpcAttempt;
	}

	public void setHoldRpcAttempt(int holdRpcAttempt) {
		this.holdRpcAttempt = holdRpcAttempt;
	}

	public int getSumCCRpcAttempt() {
		return sumCCRpcAttempt;
	}

	public void setSumCCRpcAttempt(int sumCCRpcAttempt) {
		this.sumCCRpcAttempt = sumCCRpcAttempt;
	}

	public String getStatusRpcAttempt() {
		return statusRpcAttempt;
	}

	public void setStatusRpcAttempt(String statusRpcAttempt) {
		this.statusRpcAttempt = statusRpcAttempt;
	}

	public Label getLabelTotalTextRpcAttempt() {
		return labelTotalText;
	}

	public void setLabelTotalTextRpcAttempt(Label labelTotalText) {
		this.labelTotalText = labelTotalText;
	}

	//	// Agrega filtros dinámicos ListBox
	//	public void addFilterListBox(Panel p, UiListBox uiListBox, BmObject foreign, BmField bmField) {
	//		uiListBox.setStyleName("listFilterListBox");
	//		uiListBox.populate(bmField);
	//		uiListBox.addChangeHandler(getFilterChangeHandler(uiListBox, foreign, foreign));
	//		uiListBox.setWidth("100px");
	//		
	//		String label = getUiParams().getSFParams().getFieldListTitle(foreign.getProgramCode(), bmField);
	//		Label l = new Label(label);
	//		l.setStyleName("listFilterLabel");
	//		
	//		HorizontalPanel hp = new HorizontalPanel();
	//		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	//		hp.add(l);
	//		hp.add(uiListBox);
	//		hp.add(new HTML("&nbsp;"));
	//
	//		p.add(hp);
	//	}
	//
	//	// Genera un disparador de lista desde el listbox de filtros
	//	protected ChangeHandler getFilterChangeHandler(UiListBox uiListBox, BmObject baseBmObject, BmObject targetBmObject) {
	//		UiFilterChangeHandler c = new UiFilterChangeHandler(uiListBox, baseBmObject, targetBmObject) {
	//			public void onChange(ChangeEvent event) {
	//				reset();
	//			}
	//		};
	//		return c;
	//	}

	// Agrega filtros dinámicos ListBox, solo combo moneda; asigna por defecto la moneda predeterminada del sistema
	public void addFilterListBox(Panel p, UiListBox uiListBox, BmField bmField, String defaultValue) {
		String filterValue = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).checkFilter(bmoConsultancy.getKind(), bmField.getName());

		uiListBox.setStyleName("listFilterListBox");
		uiListBox.populate(filterValue);

		// Colocar moneda por defecto del sistema
		uiListBox.setSelectedId("" + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());

		uiListBox.addChangeHandler(getFilterChangeHandler(uiListBox, bmField));
		uiListBox.setWidth("100px");

		// Si es primera vez que se carga, y no tiene filtro asignado, bloquea carga
		//		if (filterValue.equals("0"))
		//			setListBoxFilter(uiListBox, getBmObject(), foreign);
		//	
		//		// Si tiene valor default
		//		if ((filterValue.equals("0")) || !defaultValue.equals(""))
		//			setListBoxFilter(uiListBox, getBmObject(), foreign);

		String label = getUiParams().getSFParams().getFieldListTitle(bmoConsultancy.getProgramCode(), bmField);
		Label l = new Label(label);
		l.setStyleName("listFilterLabel");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hp.add(l);
		hp.add(uiListBox);
		hp.add(new HTML("&nbsp;"));

		p.add(hp);
	}

	// Agrega filtros dinámicos ListBox
	public void addFilterListBox(Panel p, UiListBox uiListBox, BmField bmField) {
		String filterValue = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).checkFilter(bmoConsultancy.getKind(), bmField.getName());

		uiListBox.setStyleName("listFilterListBox");
		uiListBox.populate(filterValue, true);
		uiListBox.addChangeHandler(getFilterChangeHandler(uiListBox, bmField));
		uiListBox.setWidth("100px");

		// Si es primera vez que se carga, y no tiene filtro asignado, bloquea carga
		//		if (!nullable && filterValue.equals("0"))
		//			setListBoxFilter(uiListBox, getBmObject(), foreign);
		//
		//		// Si tiene valor default
		//		if ((!nullable && filterValue.equals("0")) || !defaultValue.equals(""))
		//			setListBoxFilter(uiListBox, getBmObject(), foreign);

		String label = getUiParams().getSFParams().getFieldListTitle(bmoConsultancy.getProgramCode(), bmField);
		Label l = new Label(label);
		l.setStyleName("listFilterLabel");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hp.add(l);
		hp.add(uiListBox);
		hp.add(new HTML("&nbsp;"));

		p.add(hp);
	}

	// Genera un disparador de lista desde el listbox de filtros
	protected ChangeHandler getFilterChangeHandler(UiListBox uiListBox, BmField bmField) {
		UiFilterChangeHandler c = new UiFilterChangeHandler(uiListBox, bmoConsultancy, bmField) {
			public void onChange(ChangeEvent event) {
				if (event.getSource() == currencyListBox) {
					if (currencyListBox.getSelectedId().equals("") || currencyListBox.getSelectedId().equals("0") ) {
						showSystemMessage("<b>Debe seleccionar una moneda. Se regresará a la moneda predeterminada del sistema.</b>");
						// Colocar moneda por defecto
						uiListBox.setSelectedId("" + ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
					}
				}
				setListBoxFilter(uiListBox, baseBmField);
				reset();
			}
		};
		return c;
	}

	// Asigna los filtros de listado
	private void setListBoxFilter(UiListBox uiListBox, BmField bmField) {
		int index  = uiListBox.getSelectedIndex();

		// Si esta seleccionado un item, o se selecciona el primer registro y no acepta nulos
		if (index > 0 || !uiListBox.isNullableBmField()) {
			int selectedId = Integer.parseInt(uiListBox.getValue(index));
			String valueLabel = uiListBox.getItemText(uiListBox.getSelectedIndex());
			if (selectedId > 0 || !uiListBox.isNullableBmField()) {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
						bmField.getName(), 
						bmoConsultancy.getProgramLabel(),
						"=",
						selectedId,
						valueLabel);
				getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).addFilter(bmFilter);
			}  
		} else {
			// Se elimina el filtro, se selecciono el primer registro del filtro
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
					bmField.getName(), 
					bmoConsultancy.getProgramLabel(),
					"=",
					0,
					"");
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).removeFilter(bmFilter);
		}
	}

	// Agrega filtros dinámicos UiSuggestBox
	public void addFilterSuggestBox(Panel p, UiSuggestBox uiSuggestBox, BmField bmField) {
		uiSuggestBox.setStyleName("listSuggestBoxField");
		uiSuggestBox.setUiSuggestBoxAction(getUiSuggestBoxAction(uiSuggestBox, bmField));

		BmFilter existingFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmField.getName());
		if (existingFilter.getValue().toString().length() > 0) {
			uiSuggestBox.getValueBox().setValue(existingFilter.getValueLabel());
			uiSuggestBox.setSelectedId(existingFilter.getValue());
		}
		uiSuggestBox.setWidth("75px");

		UiLookup uiLookup = new UiLookup(getUiParams(), uiSuggestBox);

		Button clearButton = new Button("X");
		clearButton.setStyleName("listClearFilterButton");
		clearButton.setTitle("Eliminar Selección.");
		clearButton.addClickHandler(new UiDialogLookupClickHandler(uiLookup, uiSuggestBox, bmField) {
			public void onClick(ClickEvent event) {
				if (uiSuggestBox.isEnabled()) {
					uiSuggestBox.getValueBox().setText("");
					uiSuggestBox.setSelectedId(0);
					BmFilter existingFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmField.getName());
					getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).removeFilter(existingFilter);
					reset();
				}
			}
		});

		String label = getUiParams().getSFParams().getFieldListTitle(getBmObject().getProgramCode(), bmField) + ": ";
		Label l = new Label(label);
		l.setStyleName("listFilterLabel");
		l.setTitle("Búsqueda de Registros.");
		l.addClickHandler(new UiDialogLookupClickHandler(uiLookup, uiSuggestBox, bmField) {
			public void onClick(ClickEvent event) {
				if (uiSuggestBox.isEnabled()) {
					uiLookup.setCallerSuggestBox(uiSuggestBox);
					uiLookup.show();
				}
			}
		});

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hp.add(l);
		hp.add(uiSuggestBox);
		hp.add(clearButton);
		hp.add(new HTML("&nbsp;"));

		p.add(hp);
	}

	// Genera una clase de accion de los filtros SuggestBox
	protected UiSuggestBoxAction getUiSuggestBoxAction(UiSuggestBox uiSuggestBox, BmField baseBmField) {
		UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction(uiSuggestBox, bmoConsultancy, baseBmField) {
			@Override
			public void onSelect(UiSuggestBox uiSuggestBox) {
				setSuggestBoxFilter(uiSuggestBox, baseBmField);
				reset();
			}
		};

		return uiSuggestBoxAction;
	}

	// Ejecuta accion de un SuggestBox
	public void setSuggestBoxFilter(UiSuggestBox uiSuggestBox, BmField baseBmField) {
		int selectedId  = uiSuggestBox.getSelectedId();
		if (selectedId > 0) {
			String valueLabel = uiSuggestBox.getValueBox().getValue();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
					baseBmField.getName(), 
					baseBmField.getLabel(),
					"=",
					selectedId,
					valueLabel);
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).addFilter(bmFilter);
		} else {
			// Se elimina el filtro, se selecciono el primer registro del filtro
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
					baseBmField.getName(), 
					baseBmField.getLabel(),
					"=",
					0,
					"");
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).removeFilter(bmFilter);
		}
	}

	// Displara primer registro
	public void fireSuggestBoxHandler(UiSuggestBox uiSuggestBox, BmField baseBmField){
		int selectedId  = uiSuggestBox.getSelectedId();
		String valueLabel = uiSuggestBox.getValueBox().getValue();

		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
				baseBmField.getName(), 
				baseBmField.getLabel(),
				"=",
				selectedId,
				valueLabel);
		getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).addFilter(bmFilter);
		reset();
	}

	// Agrega filtros de fechas
	public void addDateRangeFilterListBox(Panel p, BmField bmField, String defaultStartDate, String defaultEndDate) {
		DateTimeFormat format = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat());

		BmFilter dateRangeFilter = getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).searchFilter(bmoConsultancy.getKind(), bmField.getName());
		String startDateFilter = dateRangeFilter.getMinValue();
		if (startDateFilter.equals("")  && dateRangeFilter.getOperator() == BmFilter.MAJOREQUAL)
			startDateFilter = dateRangeFilter.getValue();
		if (startDateFilter.equals(""))
			startDateFilter = defaultStartDate;

		String endDateFilter = dateRangeFilter.getMaxValue();
		if (endDateFilter.equals("") && dateRangeFilter.getOperator() == BmFilter.MINOREQUAL)
			endDateFilter = dateRangeFilter.getValue();
		if (endDateFilter.equals(""))
			endDateFilter = defaultEndDate;

		UiDateBox startDateBox = new UiDateBox();
		startDateBox.setStyleName("listRangeField");
		startDateBox.setFormat(new UiDateBox.DefaultFormat(format));
		startDateBox.getTextBox().setText(startDateFilter);

		UiDateBox endDateBox = new UiDateBox();
		endDateBox.setStyleName("listRangeField");	
		endDateBox.setFormat(new UiDateBox.DefaultFormat(format));
		endDateBox.getTextBox().setText(endDateFilter);

		// Manejar cambios de texto
		startDateBox.getTextBox().addValueChangeHandler(getDateRangeFilterTextChangeHandler(startDateBox, endDateBox, bmField));
		endDateBox.getTextBox().addValueChangeHandler(getDateRangeFilterTextChangeHandler(startDateBox, endDateBox, bmField));

		// Manejar cambios de fecha
		startDateBox.addValueChangeHandler(getDateRangeFilterChangeHandler(startDateBox, endDateBox, bmField));
		endDateBox.addValueChangeHandler(getDateRangeFilterChangeHandler(startDateBox, endDateBox, bmField));

		Label l = new Label("Rango " + bmField.getLabel() + ": ");
		l.setStyleName("listRangeFilterLabel");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hp.add(l);
		hp.add(startDateBox);
		hp.add(endDateBox);
		hp.add(new HTML("&nbsp;"));

		p.add(hp);

		// Genera accion si hay valor default a filtrar
		//		if (!defaultStartDate.equals("") || !defaultEndDate.equals("")) {
		//			setDateRangeFilter(getBmObject(), bmField, startDateBox, endDateBox);
		//		}
	}

	// Genera un disparador de lista desde el listbox de filtros
	protected ValueChangeHandler<String> getDateRangeFilterTextChangeHandler(UiDateBox startDateBox, UiDateBox endDateBox, BmField bmField) {
		UiFilterDateRangeTextChangeHandler c = new UiFilterDateRangeTextChangeHandler(startDateBox, endDateBox, bmoConsultancy, bmField) {
			public void onValueChange(ValueChangeEvent<String> event) {
				setDateRangeFilter(baseBmField, startDateBox, endDateBox);
				reset();
			}
		};
		return c;
	}

	// Genera un disparador de lista desde el listbox de filtros
	protected ValueChangeHandler<Date> getDateRangeFilterChangeHandler(UiDateBox startDateBox, UiDateBox endDateBox, BmField bmField) {
		UiFilterDateRangeChangeHandler c = new UiFilterDateRangeChangeHandler(startDateBox, endDateBox, bmoConsultancy, bmField) {
			public void onValueChange(ValueChangeEvent<Date> event) {
				setDateRangeFilter(baseBmField, startDateBox, endDateBox);
				reset();
			}
		};
		return c;
	}

	// Ejecuta accion filtro cambio fechas
	private void setDateRangeFilter(BmField baseBmField, UiDateBox startDateBox, UiDateBox endDateBox) {

		startDate = startDateBox.getTextBox().getText() ;
		endDate = endDateBox.getTextBox().getText() ;

		// Determinar tipo de filtro, datos a usar
		if (startDateBox.getTextBox().getText().length() > 0
				&& endDateBox.getTextBox().getText().length() > 0) {
			// Es un rango entre 2 fechas
			BmFilter bmFilter = new BmFilter();
			bmFilter.setRangeFilter(bmoConsultancy.getKind(),
					baseBmField.getName(), 
					baseBmField.getLabel(), 
					startDateBox.getTextBox().getText() + " 00:00:00", 
					endDateBox.getTextBox().getText() + " 23:59:59");
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).addFilter(bmFilter);
		} else if (startDateBox.getTextBox().getText().length() > 0) {
			// Es mayor que la fecha inicio
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueOperatorFilter(bmoConsultancy.getKind(),
					baseBmField,
					BmFilter.MAJOREQUAL,
					startDateBox.getTextBox().getText());
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).addFilter(bmFilter);
		} else if (endDateBox.getTextBox().getText().length() > 0) {
			// Es menor que la fecha fin
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueOperatorFilter(bmoConsultancy.getKind(),
					baseBmField,
					BmFilter.MINOREQUAL,
					endDateBox.getTextBox().getText() + " 23:59:59");
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).addFilter(bmFilter);
		} else {
			// Se elimina el filtro, se selecciono el primer registro del filtro
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueLabelFilter(bmoConsultancy.getKind(), 
					baseBmField.getName(), 
					bmoConsultancy.getProgramLabel(),
					"=",
					0,
					"");
			getUiParams().getUiProgramParams(bmoConsultancy.getProgramCode()).removeFilter(bmFilter);
		}
	}

}
