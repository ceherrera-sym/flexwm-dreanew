package com.flexwm.client.fi;


import com.flexwm.client.fi.UiRaccount.UiRaccountForm.RaccountUpdater;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountLink;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiRaccountLinkOriginGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// RaccountlINK
	UiSuggestBox changeRaccountSuggestBox;
	private BmoRaccountLink bmoRaccountLink = new BmoRaccountLink();
	private FlowPanel raccountLinkPanel = new FlowPanel();
	private CellTable<BmObject> raccountLinkGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> raccountLinkData;
	BmFilter raccountLinkFilter;

	private Button addRaccountLinkButton = new Button("+ NUEVO");
	
	protected DialogBox raccountLinkDialogBox;

	protected DialogBox showRaccountDialogBox;

	// Cambio de Raccount
	DialogBox changeRaccountDialogBox;
	Button changeRaccountButton = new Button("CAMBIAR");
	Button closeRaccountButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();

	private BmoRaccount bmoRaccount;
	private RaccountUpdater raccountUpdater;
	private BmoOrder bmoOrder;

	// Constructor original
	public UiRaccountLinkOriginGrid(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, RaccountUpdater raccountUpdater) {
		super(uiParams, defaultPanel);

		initialize(uiParams, defaultPanel, bmoRaccount, raccountUpdater);
	}

	// Constructor con parametro de Pedidos
	public UiRaccountLinkOriginGrid(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, BmoOrder bmoOrder, RaccountUpdater raccountUpdater) {
		super(uiParams, defaultPanel);
		this.bmoOrder = bmoOrder;
		initialize(uiParams, defaultPanel, bmoRaccount, raccountUpdater);
	}

	// Inicializa el objeto
	public void initialize(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, RaccountUpdater raccountUpdater) {

		this.bmoRaccount = bmoRaccount;
		this.raccountUpdater = raccountUpdater;

		closeRaccountButton.setStyleName("formCloseButton");
		closeRaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeRaccountDialogBox.hide();
			}
		});
		
		// Inicializar grid de Personal
		raccountLinkGrid = new CellTable<BmObject>();
		raccountLinkGrid.setWidth("100%");
		raccountLinkPanel.clear();
		raccountLinkPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setRaccountLinkColumns();
		raccountLinkFilter = new BmFilter();
		 
		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
			if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER) ||
					bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				raccountLinkFilter.setValueFilter(bmoRaccountLink.getKind(), bmoRaccountLink.getRaccountId().getName(), bmoRaccount.getId());
			}
		}
		else if ((bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)
				|| bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_ORDER)
				|| bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER))) {
			raccountLinkFilter.setValueFilter(bmoRaccountLink.getKind(), bmoRaccountLink.getRaccountId().getName(), bmoRaccount.getId());			
		}
				
		raccountLinkGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		//Consulta de datos
		raccountLinkData = new UiListDataProvider<BmObject>(new BmoRaccountLink(), raccountLinkFilter);
		raccountLinkData.addDataDisplay(raccountLinkGrid);
		raccountLinkPanel.add(raccountLinkGrid);

		// Panel de botones
		//
		addRaccountLinkButton.setStyleName("formSaveButton");
		addRaccountLinkButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addRaccountLink();
			}
		});
		
		
		
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		if (bmoRaccount.getBmoRaccountType().getType().equals(BmoRaccountType.TYPE_WITHDRAW)) {
			if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_OTHER) ||
					bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				buttonPanel.add(addRaccountLinkButton);
			}
			
		}

		// Crear forma y campos
		formFlexTable.setWidth("100%");		
		formFlexTable.addPanel(3, 0, raccountLinkPanel, 4);
		formFlexTable.addButtonPanel(buttonPanel);
		defaultPanel.add(formFlexTable);
	}

	public void show(){
		raccountLinkData.list();
		raccountLinkGrid.redraw();

		statusEffect();
	}

	public void statusEffect(){
	}

	

	public void changeHeight() {
		raccountLinkGrid.setVisibleRange(0, raccountLinkData.getList().size());
	}

	public void setRaccountLinkColumns() {
		
		Column<BmObject, String> originColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {				
				return "" + bmoRaccount.getCode().toString();
			}
		};

		raccountLinkGrid.addColumn(originColumn, SafeHtmlUtils.fromSafeConstant("CxC Origen"));
		originColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		raccountLinkGrid.setColumnWidth(originColumn, 50, Unit.PX);
		
		// Destino CxC
		Column<BmObject, String> showRaccount;
		showRaccount = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "" + ((BmoRaccountLink)bmObject).getBmoForeign().getCode().toString();
			}
		};
		showRaccount.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				int raccountId = ((BmoRaccountLink)bmObject).getForeignId().toInteger();
				showRaccount(raccountId);
			}
		});
		
		raccountLinkGrid.addColumn(showRaccount, SafeHtmlUtils.fromSafeConstant("CxC Destino"));
		showRaccount.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		raccountLinkGrid.setColumnWidth(showRaccount, 50, Unit.PX);
		
		// Pedido
		Column<BmObject, String> orderColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoRaccountLink)bmObject).getBmoOrder().getCode().toString();
			}
		};
		raccountLinkGrid.addColumn(orderColumn, SafeHtmlUtils.fromSafeConstant("Pedido Destino"));
		orderColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		raccountLinkGrid.setColumnWidth(orderColumn, 150, Unit.PX);
		
		// Factura
		Column<BmObject, String> invoiceColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoRaccountLink)bmObject).getBmoForeign().getInvoiceno().toString();
			}
		};
		raccountLinkGrid.addColumn(invoiceColumn, SafeHtmlUtils.fromSafeConstant("Factura Destino"));
		invoiceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		raccountLinkGrid.setColumnWidth(invoiceColumn, 150, Unit.PX);
		
		// Programacion
		Column<BmObject, String> dueDateColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoRaccountLink)bmObject).getBmoForeign().getDueDate().toString();
			}
		};
		raccountLinkGrid.addColumn(dueDateColumn, SafeHtmlUtils.fromSafeConstant("F. Programación Destino"));
		dueDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		raccountLinkGrid.setColumnWidth(dueDateColumn, 150, Unit.PX);
		
		// Programacion
		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoRaccountLink)bmObject).getBmoForeign().getTotal().toString();
			}
		};
		raccountLinkGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total CxC Destino"));
		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		raccountLinkGrid.setColumnWidth(totalColumn, 150, Unit.PX);
		
		// Eliminar
		Column<BmObject, String> deleteColumn;
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
				 
		raccountLinkGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		raccountLinkGrid.setColumnWidth(deleteColumn, 50, Unit.PX);		

	}
	
	private void deleteItem(BmObject bmObject) {
		bmoRaccountLink = (BmoRaccountLink)bmObject;
		deleteItem();
	}
	
	public void reset1(){
		raccountUpdater.changeRaccount();
	}
	
	public void deleteItem() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteRaccountLink(): ERROR " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.hasErrors()) {
					stopLoading();
					showSystemMessage("Error al Eliminar CxC Relacionada: " + result.errorsToString());
				}
				else {
					stopLoading();
					reset1();			
				}	
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoRaccountLink.getPmClass(), bmoRaccountLink, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteRaccountLink(): ERROR " + e.toString());
		}
	}
	
	
	public void addRaccountLink() {	
		FlowPanel raccountLinkFP = new FlowPanel();
		UiRaccountLinks uiRaccountLinks = new UiRaccountLinks(getUiParams(), raccountLinkFP, bmoRaccount, bmoRaccount.getId(), raccountUpdater);
		uiRaccountLinks.create();
		startLoading();
		reset1();
	}
	
	public void showRaccount(int raccountId) {		
		showRaccountDialogBox = new DialogBox(true);
		showRaccountDialogBox.setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		showRaccountDialogBox.setWidget(vp);
		getRaccount(raccountId);
	}
	
	private void getRaccount(int id) {

		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getRaccount() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				stopLoading();				
				BmoRaccount bmoRacc = (BmoRaccount)result;
				UiRaccount uiRaccount = new UiRaccount(getUiParams());
				uiRaccount.edit(bmoRacc);
				
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoRaccount.getPmClass(), id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getRaccount(): ERROR " + e.toString());
		}
	}
	
	
}
