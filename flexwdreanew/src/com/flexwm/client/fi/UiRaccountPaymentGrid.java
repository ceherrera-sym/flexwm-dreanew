package com.flexwm.client.fi;

import com.flexwm.client.fi.UiRaccount.UiRaccountForm.RaccountUpdater;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoRaccountType;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
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
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;


public class UiRaccountPaymentGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());

	// BankMovConcepts	
	private BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
	private FlowPanel BankMovConceptPanel = new FlowPanel();
	private CellTable<BmObject> bankMovConceptGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> bankMovConceptData;
	BmFilter bankMovConceptFilter;

	// Cambio de Raccount
	DialogBox changeRaccountDialogBox;
	Button changeRaccountButton = new Button("Cambiar");
	Button closeRaccountButton = new Button("Cerrar");

	private NumberFormat fmt = NumberFormat.getDecimalFormat();

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();	
	private BmoRaccountType bmoRaccountType;
	private RaccountUpdater RaccountUpdater;

	protected DialogBox showBankMovementDialogBox;


	public UiRaccountPaymentGrid(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, RaccountUpdater RaccountUpdater){
		super(uiParams, defaultPanel);
		this.RaccountUpdater = RaccountUpdater;

		bmoRaccountType = (BmoRaccountType)bmoRaccount.getBmoRaccountType();

		closeRaccountButton.setStyleName("formCloseButton");
		closeRaccountButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				changeRaccountDialogBox.hide();
			}
		});


		// Inicializar grid de Personal
		bankMovConceptGrid = new CellTable<BmObject>();
		bankMovConceptGrid.setWidth("100%");
		BankMovConceptPanel.clear();
		BankMovConceptPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setBankMovConceptColumns();
		bankMovConceptFilter = new BmFilter();
		bankMovConceptFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRaccountId().getName(), bmoRaccount.getId());
		bankMovConceptGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					changeHeight();
				}
			}
		}); 
		bankMovConceptData = new UiListDataProvider<BmObject>(new BmoBankMovConcept(), bankMovConceptFilter);
		bankMovConceptData.addDataDisplay(bankMovConceptGrid);
		BankMovConceptPanel.add(bankMovConceptGrid);

		// Panel de botones
		/*addBankMovConceptButton.setStyleName("formSaveButton");
		addBankMovConceptButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addBankMovConcept();
			}
		});*/
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, BankMovConceptPanel);
		//Si es de tipo orden de compra no mostrar el boton para agregar items
		if (!bmoRaccountType.getCategory().equals(BmoRaccountType.CATEGORY_ORDER))
			formFlexTable.addButtonPanel(buttonPanel);

		defaultPanel.add(formFlexTable);
	}

	public void show(){
		bankMovConceptData.list();
		bankMovConceptGrid.redraw();
	}

	public void changeHeight() {
		bankMovConceptGrid.setPageSize(bankMovConceptData.getList().size());
		bankMovConceptGrid.setVisibleRange(0, bankMovConceptData.getList().size());
	}

	public void setBankMovConceptColumns() {

		// Movimiento Banco
		Column<BmObject, String> bankCodeColumn;		
		bankCodeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoBankMovConcept)bmObject).getBmoBankMovement().getCode().toString();
			}
		};		
		bankMovConceptGrid.addColumn(bankCodeColumn, SafeHtmlUtils.fromSafeConstant("Mov.Banco"));
		bankCodeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		bankMovConceptGrid.setColumnWidth(bankCodeColumn, 50, Unit.PX);

		// Description
		Column<BmObject, String> descriptionColumn;		
		descriptionColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoBankMovConcept)bmObject).getBmoBankMovement().getDescription().toString();
			}
		};		
		bankMovConceptGrid.addColumn(descriptionColumn, SafeHtmlUtils.fromSafeConstant("Descripción"));
		descriptionColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		bankMovConceptGrid.setColumnWidth(descriptionColumn, 50, Unit.PX);

		// Boton
		Column<BmObject, String> showBankMovement;
		showBankMovement = new Column<BmObject, String>(new ButtonCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return "MB";
			}
		};
		showBankMovement.setFieldUpdater(new FieldUpdater<BmObject, String>() {
			@Override
			public void update(int index, BmObject bmObject, String value) {
				int bankMovementId = ((BmoBankMovConcept)bmObject).getBankMovementId().toInteger();
				showBankMovement(bankMovementId);
			}
		});

		bankMovConceptGrid.addColumn(showBankMovement, SafeHtmlUtils.fromSafeConstant("Ver"));
		showBankMovement.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		bankMovConceptGrid.setColumnWidth(showBankMovement, 50, Unit.PX);

		// Fecha
		Column<BmObject, String> dateCreateColumn;		
		dateCreateColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoBankMovConcept)bmObject).getBmoBankMovement().getDueDate().toString();
			}
		};		
		bankMovConceptGrid.addColumn(dateCreateColumn, SafeHtmlUtils.fromSafeConstant("Fecha"));
		dateCreateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		bankMovConceptGrid.setColumnWidth(dateCreateColumn, 50, Unit.PX);

		// Monto
		Column<BmObject, String> amountColumn;		
		amountColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				fmt = NumberFormat.getCurrencyFormat();
				String formatted = "";
				if (((BmoBankMovConcept)bmObject).getAmountCoverted().toDouble() > 0)
					formatted = fmt.format((((BmoBankMovConcept)bmObject).getAmountCoverted().toDouble()));
				else 
					formatted = fmt.format((((BmoBankMovConcept)bmObject).getAmount().toDouble()));

				// Devolucion cliente
//				if (((BmoBankMovConcept)bmObject).getBmoBankMovement().getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC))
//					formatted = "-" + formatted;

				return formatted;
			}

		};		
		bankMovConceptGrid.addColumn(amountColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
		amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		bankMovConceptGrid.setColumnWidth(amountColumn, 50, Unit.PX);
	}

	public void reset(){
		RaccountUpdater.changeRaccount();
	}

	public void showBankMovement(int bankMovementId) {		
		showBankMovementDialogBox = new DialogBox(true);
		showBankMovementDialogBox.setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		showBankMovementDialogBox.setWidget(vp);
		// Verifica si es Nota de credito o MB
		getBankMovement(bankMovementId);

	}

	//Obtener el concepto de Banco ligado a la CxC
	private void getBankMovement(int id) {
		BmoBankMovement bmoBankMovement = new BmoBankMovement();

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getBankMovement() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (!result.hasErrors()) {
					if (result.getBmObject() != null) {
						BmoBankMovement bmoBankMovement = (BmoBankMovement)result.getBmObject();					
						UiBankMovement uiBankMovement = new UiBankMovement(getUiParams());
						uiBankMovement.open(bmoBankMovement);
					} 
				}	
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoBankMovement.getPmClass(), bmoBankMovement, BmoBankMovement.ACTION_SHOWBANKMOVEMENT, "" + id, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBankMovement(): ERROR " + e.toString());
		}
	}

}
