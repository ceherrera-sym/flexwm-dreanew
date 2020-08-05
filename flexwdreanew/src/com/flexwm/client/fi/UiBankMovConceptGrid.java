package com.flexwm.client.fi;

import java.sql.Types;

import com.google.gwt.i18n.client.NumberFormat;
import java.util.ArrayList;
import com.flexwm.client.fi.UiBankMovement.UiBankMovementForm.BankMovementUpdater;
import com.flexwm.client.op.UiOrderLifeCycleViewModel;
import com.flexwm.client.op.UiRequisition;
import com.flexwm.client.op.UiRequisitionLifeCycleViewModel;
import com.flexwm.client.op.UiRequisitionReceipt;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListDataProvider;
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
import com.symgae.shared.SFException;


public class UiBankMovConceptGrid extends Ui {
	private UiFormFlexTable formFlexTable = new UiFormFlexTable(getUiParams());
	private NumberFormat fmt = NumberFormat.getDecimalFormat();

	// BankMovConcepts
	private BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
	private FlowPanel bankMovConceptPanel = new FlowPanel();
	private CellTable<BmObject> bankMovConceptGrid = new CellTable<BmObject>();
	private UiListDataProvider<BmObject> bankMovConceptData;
	BmFilter bankMovConceptFilter;

	private Button addBankMovConceptButton = new Button("+ ITEM");
	protected DialogBox bankMovConceptDialogBox;

	protected DialogBox paccountConceptDialogBox;
	protected DialogBox raccountConceptDialogBox;
	protected DialogBox requisitionConceptDialogBox;

	// Cambio de Paccount
	DialogBox changeBankMovConceptDialogBox;
	Button changeBankMovConceptButton = new Button("CAMBIAR");
	Button closeBankMovConceptButton = new Button("CERRAR");

	// Otros
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private BmoBankMovement bmoBankMovement;	
	private BankMovementUpdater bankMovementUpdater;
	BmoPaccount bmoPaccount = new BmoPaccount();
	BmoRaccount bmoRaccount = new BmoRaccount();
	BmoRequisition bmoRequisition = new BmoRequisition();
	BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();

	// Ciclo de vida orden de compra
	public final SingleSelectionModel<BmObject> lifeCycleSelection = new SingleSelectionModel<BmObject>();
	private UiRequisitionLifeCycleViewModel uiLifeCycleViewModel;
	private CellTree lifeCycleCellTree;
	private DialogBox lifeCycleDialogBox = new DialogBox();	
	private Button lifeCycleCloseButton = new Button("CERRAR");

	// Ciclo de vida pedido	
	public final SingleSelectionModel<BmObject> lifeCycleOrderSelection = new SingleSelectionModel<BmObject>();
	private UiOrderLifeCycleViewModel uiLifeCycleOrderViewModel;
	private CellTree lifeCycleOrderCellTree;
	private DialogBox lifeCycleOrderDialogBox = new DialogBox();	
	private Button lifeCycleOrderCloseButton = new Button("CERRAR");
	private int saveItemChangeRpcAttempt = 0;
	private int findOrderRpcAttempt = 0;
	private int findRequisitionRpcAttempt = 0;
	private int raccountIdRPC = 0;
	private int paccountIdRPC = 0;

	public UiBankMovConceptGrid(UiParams uiParams, Panel defaultPanel, BmoBankMovement bmoBankMovement, BankMovementUpdater bankMovementUpdater){
		super(uiParams, defaultPanel);
		this.bmoBankMovement = bmoBankMovement;
		this.bankMovementUpdater = bankMovementUpdater;		

		closeBankMovConceptButton.setStyleName("formCloseButton");
		closeBankMovConceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				changeBankMovConceptDialogBox.hide();
			}
		});



		// Inicializar grid de Personal
		bankMovConceptGrid = new CellTable<BmObject>();
		bankMovConceptGrid.setWidth("100%");
		bankMovConceptPanel.clear();
		bankMovConceptPanel.setWidth("100%");
		defaultPanel.setStyleName("detailStart");
		setBankMovConceptColumns();
		bankMovConceptFilter = new BmFilter();
		bankMovConceptFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getBankMovementId().getName(), bmoBankMovement.getId());
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
		bankMovConceptPanel.add(bankMovConceptGrid);

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

		// Ciclo de vida de ordenes de compra
		lifeCycleOrderSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				BmObject bmObject = lifeCycleOrderSelection.getSelectedObject();
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


		/*lifeCycleShowButton.setStyleName("formCloseButton");
		lifeCycleShowButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showLifeCycleDialog();
			}
		});*/

		//Cerrar Seguimiento de Orden de Compra
		lifeCycleCloseButton.setStyleName("formCloseButton");
		lifeCycleCloseButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lifeCycleDialogBox.hide();
			}
		});

		//Cerrar Seguimiento del Pedido
		lifeCycleOrderCloseButton.setStyleName("formCloseButton");
		lifeCycleOrderCloseButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lifeCycleOrderDialogBox.hide();
			}
		});



		// Panel de botones
		addBankMovConceptButton.setStyleName("formSaveButton");
		addBankMovConceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addBankMovConcept();
			}
		});
		buttonPanel.setHeight("100%");
		buttonPanel.setStyleName("formButtonPanel");
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER) 
				&& !bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL))			
			buttonPanel.add(addBankMovConceptButton);

		// Crear forma y campos
		formFlexTable.setWidth("100%");
		formFlexTable.addPanel(1, 0, bankMovConceptPanel);		
		formFlexTable.addButtonPanel(buttonPanel);

		defaultPanel.add(formFlexTable);
	}

	@Override
	public void show(){
		bankMovConceptData.list();
		bankMovConceptGrid.redraw();

		statusEffect();
	}

	public void addBankMovConcept() {
		bankMovConceptDialogBox = new DialogBox(true);
		bankMovConceptDialogBox.setGlassEnabled(true);
		bankMovConceptDialogBox.setText("Items");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		bankMovConceptDialogBox.setWidget(vp);

		UiBankMovConceptForm bankMovConceptForm = new UiBankMovConceptForm(getUiParams(), vp, bmoBankMovement);
		bankMovConceptForm.show();

		bankMovConceptDialogBox.center();
		bankMovConceptDialogBox.show();
	}

	public void setBankMovConceptColumns() {		
		// Clave
		Column<BmObject, String> paccountCodeColumn;		
		paccountCodeColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				return ((BmoBankMovConcept)bmObject).getCode().toString();
			}
		};		
		bankMovConceptGrid.addColumn(paccountCodeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));
		paccountCodeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		bankMovConceptGrid.setColumnWidth(paccountCodeColumn, 50, Unit.PX);

		// Monto
		Column<BmObject, String> amountColumn;		
		amountColumn = new Column<BmObject, String>(new TextCell()) {
			@Override
			public String getValue(BmObject bmObject) {
				fmt = NumberFormat.getCurrencyFormat();
				String formatted = fmt.format((((BmoBankMovConcept)bmObject).getAmount().toDouble()));
				return formatted;
			}

		};		
		bankMovConceptGrid.addColumn(amountColumn, SafeHtmlUtils.fromSafeConstant("Monto"));
		amountColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		bankMovConceptGrid.setColumnWidth(amountColumn, 50, Unit.PX);

		//Mostrar el boton para ir a la CxC o CxP
		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP) ||
				bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC) ||
				bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			// Seguimiento
			Column<BmObject, String> showPaccount;
			showPaccount = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					String type = "";
					if (((BmoBankMovConcept)bmObject).getRaccountId().toInteger() > 0)					
						type = "CC";
					else if (((BmoBankMovConcept)bmObject).getPaccountId().toInteger() > 0)
						type = "CP"; 
					// Si es de tipo anticipo prov.
					else if (((BmoBankMovConcept)bmObject).getRequisitionId().toInteger() > 0
							&& ((BmoBankMovConcept)bmObject).getBmoBankMovement().getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW))
						type = "O.C."; 
					return type;
				}
			};
			showPaccount.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept)bmObject;	
					if (bmoBankMovConcept.getPaccountId().toInteger() > 0)
						showPaccount(bmoBankMovConcept.getPaccountId().toInteger());
					else if (bmoBankMovConcept.getRaccountId().toInteger() > 0)
						showRaccount(bmoBankMovConcept.getRaccountId().toInteger());
					else if (((BmoBankMovConcept)bmObject).getRequisitionId().toInteger() > 0
							&& ((BmoBankMovConcept)bmObject).getBmoBankMovement().getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW))
						showRequisition(bmoBankMovConcept.getRequisitionId().toInteger());
				}
			});

			bankMovConceptGrid.addColumn(showPaccount, SafeHtmlUtils.fromSafeConstant("Ver"));
			showPaccount.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bankMovConceptGrid.setColumnWidth(showPaccount, 50, Unit.PX);
		}	

		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP) ||
				bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC) ||
				bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			// Seguimiento
			Column<BmObject, String> lifeCycleColumn;
			lifeCycleColumn = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return "Ver";
				}
			};
			lifeCycleColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept)bmObject;
					if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)
							|| bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
						findOrder(bmoBankMovConcept.getRaccountId().toInteger());
					}else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
						findRequisition(bmoBankMovConcept.getPaccountId().toInteger());
					}else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
						// Si es devolucion mandar a cxp, sino a oc
						if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT))
							findRequisition(bmoBankMovConcept.getPaccountId().toInteger());
						else 
							findRequisition(bmoBankMovConcept.getRequisitionId().toInteger());
					}
					
//					if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT))
//						findOrder(bmoBankMovConcept.getRaccountId().toInteger());
//					else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW))
//						findRequisition(bmoBankMovConcept.getPaccountId().toInteger());
				}
			});

			bankMovConceptGrid.addColumn(lifeCycleColumn, SafeHtmlUtils.fromSafeConstant("Seguimiento"));
			lifeCycleColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bankMovConceptGrid.setColumnWidth(lifeCycleColumn, 50, Unit.PX);
		}
		// Eliminar CP
		Column<BmObject, String> deleteColumnCxP;
		
		if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)
				&& bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)
				&& bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {
			
			deleteColumnCxP = new Column<BmObject, String>(new ButtonCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					if (((BmoBankMovConcept)bmObject).getPaccountId().toInteger() > 0)
						return "-";
					else 
						return " ";
				}
			};
			deleteColumnCxP.setFieldUpdater(new FieldUpdater<BmObject, String>() {
				@Override
				public void update(int index, BmObject bmObject, String value) {
					if (((BmoBankMovConcept)bmObject).getPaccountId().toInteger() > 0)
						deleteItemCP(bmObject);
				}
			});
			
			bankMovConceptGrid.addColumn(deleteColumnCxP, SafeHtmlUtils.fromSafeConstant("Desvincular CxP."));
			deleteColumnCxP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bankMovConceptGrid.setColumnWidth(deleteColumnCxP, 50, Unit.PX);
		}

		// Eliminar
		Column<BmObject, String> deleteColumn;
		if (!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER) 
				&& bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_REVISION)) {

			if (!(bmoBankMovement.getBkmvCancelId().toInteger() > 0) && 
					!(bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL)) &&
					!(bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) &&
					!(bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE))) {				

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
		} else {
			deleteColumn = new Column<BmObject, String>(new TextCell()) {
				@Override
				public String getValue(BmObject bmObject) {
					return " ";
				}
			};
		}
		bankMovConceptGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		bankMovConceptGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
		
	}

	public void statusEffect(){
		if (bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_RECONCILED) ||
				bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_CANCELLED)) {
			addBankMovConceptButton.setVisible(false);
		} else {
			if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEPOSITFREE) ||
					bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE) ||
					bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_TRANSFER)) {
				addBankMovConceptButton.setVisible(false);			
			} else {
				addBankMovConceptButton.setVisible(true);
			}
		}	
	}

	public void showPaccount(int paccountId) {		
		paccountConceptDialogBox = new DialogBox(true);
		paccountConceptDialogBox.setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		paccountConceptDialogBox.setWidget(vp);

		UiPaccount paccountForm = new UiPaccount(getUiParams());		
		paccountForm.edit(paccountId);
	}

	public void showRaccount(int raccountId) {		
		raccountConceptDialogBox = new DialogBox(true);
		raccountConceptDialogBox.setGlassEnabled(true);
		//paccountConceptDialogBox.setText("Items");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "130px");

		raccountConceptDialogBox.setWidget(vp);

		UiRaccount raccountForm = new UiRaccount(getUiParams());		
		raccountForm.edit(raccountId);

		//paccountConceptDialogBox.center();
		//paccountConceptDialogBox.show();
	}
	
	public void showRequisition(int requisitionId) {		
//		requisitionConceptDialogBox = new DialogBox(true);
//		requisitionConceptDialogBox.setGlassEnabled(true);
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("400px", "130px");
//		requisitionConceptDialogBox.setWidget(vp);

		UiRequisition uiRequisition = new UiRequisition(getUiParams());		
		uiRequisition.edit(requisitionId);

	}

	public void showLifeCycleDialog(int requisitionId) {
		// Es de tipo forma de dialogo
		lifeCycleDialogBox = new DialogBox(true);
		lifeCycleDialogBox.setGlassEnabled(true);
		lifeCycleDialogBox.setText("Seguimiento del Documento");

		uiLifeCycleViewModel = new UiRequisitionLifeCycleViewModel(lifeCycleSelection, requisitionId);
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

	private void findRequisition(int paccountIdRPC) {
		findRequisition(paccountIdRPC, 0);
	}
	private void findRequisition(int paccountIdRPC, int findRequisitionRpcAttempt) {
		if (findRequisitionRpcAttempt < 5) {
			setPaccountIdRPC(paccountIdRPC);
			setFindRequisitionRpcAttempt(findRequisitionRpcAttempt + 1);
			
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getFindRequisitionRpcAttempt() < 5)
						findRequisition(getPaccountIdRPC(), getFindRequisitionRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-findRequisition() ERROR: " + caught.toString());
				}
	
				@Override
				public void onSuccess(BmObject result) {
					stopLoading();	
					setFindRequisitionRpcAttempt(0);
					if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) 
						showLifeCycleDialog(((BmoPaccount)result).getRequisitionId().toInteger());
					else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
						// Si es devolucion mandar a cxp, sino a oc
						if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT))
							showLifeCycleDialog(((BmoPaccount)result).getRequisitionId().toInteger());
						else
							showLifeCycleDialog(((BmoRequisition)result).getId());
					}
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) 
						getUiParams().getBmObjectServiceAsync().get(bmoPaccount.getPmClass(), paccountIdRPC, callback);
					else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
						// Si es devolucion mandar a cxp, sino a oc
						if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT))
							getUiParams().getBmObjectServiceAsync().get(bmoPaccount.getPmClass(), paccountIdRPC, callback);
						else
							getUiParams().getBmObjectServiceAsync().get(bmoRequisition.getPmClass(), paccountIdRPC, callback);
					}
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-findRequisition(): ERROR " + e.toString());
			}
		}
	}

	//Seguimiento del pedido
	public void showLifeCycleOrderDialog(int orderId) {
		// Es de tipo forma de dialogo
		lifeCycleOrderDialogBox = new DialogBox(true);
		lifeCycleOrderDialogBox.setGlassEnabled(true);
		lifeCycleOrderDialogBox.setText("Seguimiento del Documento");

		// Actualiza seguimiento a documento
		uiLifeCycleOrderViewModel = new UiOrderLifeCycleViewModel(lifeCycleOrderSelection, orderId);
		lifeCycleOrderCellTree = new CellTree(uiLifeCycleOrderViewModel, lifeCycleOrderSelection);
		lifeCycleOrderCellTree.setSize("100%", "100%");
		lifeCycleOrderCellTree.setAnimationEnabled(true);

		// Vertical Panel
		VerticalPanel vp = new VerticalPanel();
		vp.setSize("100%", "100%");
		vp.add(lifeCycleOrderCellTree);
		vp.add(new HTML("&nbsp;"));
		vp.add(lifeCycleOrderCloseButton);

		// Scroll Panel
		ScrollPanel scrollPanel = new ScrollPanel();
		if (getUiParams().isMobile())
			scrollPanel.setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
		else
			scrollPanel.setSize(Window.getClientWidth() * .4 + "px", Window.getClientHeight() * .3 + "px");
		scrollPanel.setWidget(vp);
		lifeCycleOrderDialogBox.setWidget(scrollPanel);

		Double d = Window.getClientWidth() * .3;
		if (!getUiParams().isMobile()) 
			lifeCycleOrderDialogBox.setPopupPosition(d.intValue(), UiTemplate.NORTHSIZE * 3);

		lifeCycleOrderDialogBox.show();
	}

	private void findOrder(int raccountIdRPC){
		findOrder(raccountIdRPC, 0);
	}
	private void findOrder(int raccountIdRPC, int findOrderRpcAttempt) {
		if (findOrderRpcAttempt < 5) {
			setRaccountIdRPC(raccountIdRPC);
			setFindOrderRpcAttempt(findOrderRpcAttempt + 1);
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getFindOrderRpcAttempt() < 5)
						findOrder(getRaccountIdRPC(), getFindOrderRpcAttempt());
					showErrorMessage(this.getClass().getName() + "-findOrder() ERROR: " + caught.toString());
				}
	
				@Override
				public void onSuccess(BmObject result) {
					stopLoading();	
					setFindOrderRpcAttempt(0);
					showLifeCycleOrderDialog(((BmoRaccount)result).getOrderId().toInteger());
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoRaccount.getPmClass(), raccountIdRPC, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-findOrder(): ERROR " + e.toString());
			}
		}
	}

	public void reset(){
		bankMovementUpdater.changeBankMovement();
	}

	public void changeHeight() {
		bankMovConceptGrid.setVisibleRange(0, bankMovConceptData.getList().size());
	}

	public void changeQuantity(BmObject bmObject, String quantity) {
		bmoBankMovConcept = (BmoBankMovConcept)bmObject;
		try {
			int q = Integer.parseInt(quantity);
			if (q > 0) {
				bmoBankMovConcept.getAmount().setValue(quantity);
				saveItemChange();
			} else {
				// Eliminar registro				
				deleteItem();
			}	
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
		}
	}

	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();
		}
	}

	public void processItemDelete(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
		else {
			this.reset();			
		}		
	}
	// primer intento
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
				if (!isLoading()){
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
			}
		}
	}

	private void deleteItem(BmObject bmObject) {
		bmoBankMovConcept = (BmoBankMovConcept)bmObject;
		deleteItem();		
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
				reset();
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().delete(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
		}
	}
	
	private void deleteItemCP(BmObject bmObject) {
		bmoBankMovConcept = (BmoBankMovConcept)bmObject;
		deleteItemCP();		
	}
	
	public void deleteItemCP() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-deleteItemCP(): ERROR " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				processItemDelete(result);
				reset();
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, BmoBankMovConcept.ACTION_DELETEPACCOUNTID, "" + bmoBankMovConcept.getId(), callback);
				//getUiParams().getBmObjectServiceAsync().delete(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-deleteItemCP(): ERROR " + e.toString());
		}
	}

	public void addbankMovConcept() {
		bankMovConceptDialogBox = new DialogBox(true);
		bankMovConceptDialogBox.setGlassEnabled(true);
		bankMovConceptDialogBox.setText("Items");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "430px");

		bankMovConceptDialogBox.setWidget(vp);

		UiBankMovConceptForm bankMovConceptForm = new UiBankMovConceptForm(getUiParams(), vp, bmoBankMovement);
		bankMovConceptForm.show();

		bankMovConceptDialogBox.center();
		bankMovConceptDialogBox.show();
	}

	// Agrega un concepto de movimiento bancario
	private class UiBankMovConceptForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
		private TextBox codeTextBox = new TextBox();
		private TextBox amountTextBox = new TextBox();
		private TextBox parityTextBox = new TextBox();
		private TextBox parityDestinyTextBox = new TextBox();
		private TextBox amountConvertedTextBox = new TextBox();
		private UiSuggestBox paccountSuggestBox = new UiSuggestBox(new BmoPaccount());		
		private UiSuggestBox raccountSuggestBox = new UiSuggestBox(new BmoRaccount());
		private UiSuggestBox requisitionSuggestBox = new UiSuggestBox(new BmoRequisition());		
		private UiSuggestBox orderDeliverySuggestBox = new UiSuggestBox(new BmoOrderDelivery());
		public CheckBox calculateCheckBox = new CheckBox();
		private BmoBankMovConcept bmoBankMovConcept;		
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoBankMovement bmoBankMovement;

		BmField parityTextBoxField;
		BmField amountConvertedTextBoxField;
		BmField calculateField;


		public UiBankMovConceptForm(UiParams uiParams, Panel defaultPanel, BmoBankMovement bmoBankMovement) {
			super(uiParams, defaultPanel);
			this.bmoBankMovement = bmoBankMovement;
			this.bmoBankMovConcept = new BmoBankMovConcept();

			parityTextBoxField = new BmField("parityTextBoxField", "", "Tipo de Cambio.", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
			calculateField = new BmField("calculateField", "", "Auto Calcular.", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

			if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
				amountConvertedTextBoxField = new BmField("amountConvertedTextBoxField", "", "Aplicar CxP.", 10, Types.VARCHAR, false, BmFieldType.STRING, false);
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) { 
				amountConvertedTextBoxField = new BmField("amountConvertedTextBoxField", "", "Aplicar O.C.", 10, Types.VARCHAR, false, BmFieldType.STRING, false);
			} else if(bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)
					|| bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
				amountConvertedTextBoxField = new BmField("amountConvertedTextBoxField", "", "Aplicar CxC.", 10, Types.VARCHAR, false, BmFieldType.STRING, false);
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) { 
				amountConvertedTextBoxField = new BmField("amountConvertedTextBoxField", "", "Aplicar Envio Ped.", 10, Types.VARCHAR, false, BmFieldType.STRING, false);
			}

			try {
				bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());

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
			saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			// Manejo de acciones de suggest box
			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
				@Override
				public void onSelect(UiSuggestBox uiSuggestBox) {
					formSuggestionChange(uiSuggestBox);
				}
			};
			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);

			// Manejo de cambios de textbox
			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					formTextChange(event);
				}
			};
			formTable.setTextChangeHandler(textChangeHandler);

			// Tipo Retiro pagos CxP
			if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW) 
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
				
				bmoPaccount = new BmoPaccount();
				paccountSuggestBox = new UiSuggestBox(new BmoPaccount());

				// Filtros CxP destino
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterBySupplier = new BmFilter();
				filterBySupplier.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getSupplierId(), bmoBankMovement.getSupplierId().toInteger());
				filterList.add(filterBySupplier);

				BmFilter filterByPaymentStatus = new BmFilter();
				filterByPaymentStatus.setValueOperatorFilter(bmoPaccount.getKind(), bmoPaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoPaccount.PAYMENTSTATUS_TOTAL);
				filterList.add(filterByPaymentStatus);

				BmFilter filterByStatus = new BmFilter();
				filterByStatus.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getStatus(), "" + BmoPaccount.STATUS_AUTHORIZED);
				filterList.add(filterByStatus);

				BmFilter filterByType = new BmFilter();
				filterByType.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getBmoPaccountType().getType(), "" + BmoPaccountType.TYPE_WITHDRAW);
				filterList.add(filterByType);

				// Filtro por empresa en bancos por permiso
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_ADDMBCALLCOMPANY)) {
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getCompanyId(), bmoBankMovement.getBmoBankAccount().getCompanyId().toString());
					filterList.add(filterByCompany);
					paccountSuggestBox.addFilter(filterByCompany);
				}

				paccountSuggestBox.addFilter(filterBySupplier);
				paccountSuggestBox.addFilter(filterByPaymentStatus);
				paccountSuggestBox.addFilter(filterByStatus);
				paccountSuggestBox.addFilter(filterByType);
			} 
			// Tipo retiro, anticipo de O.C.
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW) 
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				
				bmoRequisition = new BmoRequisition();

				// Filtro OC Autorizadas
				BmFilter filterByStatus = new BmFilter();
				filterByStatus.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getStatus(), "" + BmoRequisition.STATUS_AUTHORIZED);
				requisitionSuggestBox.addFilter(filterByStatus);
				
				// Filtro OC con Pago Pendiente
				BmFilter filterByPaymentStatus = new BmFilter();
				filterByPaymentStatus.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getPaymentStatus(), "" + BmoRequisition.PAYMENTSTATUS_PENDING);
				requisitionSuggestBox.addFilter(filterByPaymentStatus);
				
				// Filtro de Proveedor 
				BmFilter filterBySupplier = new BmFilter();
				filterBySupplier.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getSupplierId(), this.bmoBankMovement.getSupplierId().toInteger());
				requisitionSuggestBox.addFilter(filterBySupplier);
				
				// Filtro de pagos, solo se puede generar UN concepto a UNA oc
				BmFilter filterByPayment = new BmFilter();
				filterByPayment.setValueOperatorFilter(bmoRequisition.getKind(), bmoRequisition.getPayments(), BmFilter.EQUALS, 0);
				requisitionSuggestBox.addFilter(filterByPayment);
				
				// Filtro por empresa en bancos
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_ADDMBCALLCOMPANY)) {
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getCompanyId(), this.bmoBankMovement.getBmoBankAccount().getCompanyId().toString());
					requisitionSuggestBox.addFilter(filterByCompany);
				}
				/*
				// Filtros CxP destino
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterBySupplier = new BmFilter();
				filterBySupplier.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getSupplierId(), bmoBankMovement.getSupplierId().toInteger());
				filterList.add(filterBySupplier);

				BmFilter filterByPaymentStatus = new BmFilter();
				filterByPaymentStatus.setValueOperatorFilter(bmoPaccount.getKind(), bmoPaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoPaccount.PAYMENTSTATUS_TOTAL);
				filterList.add(filterByPaymentStatus);

				BmFilter filterByStatus = new BmFilter();
				filterByStatus.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getStatus(), "" + BmoPaccount.STATUS_AUTHORIZED);
				filterList.add(filterByStatus);

				BmFilter filterByType = new BmFilter();
				filterByType.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getBmoPaccountType().getType(), "" + BmoPaccountType.TYPE_WITHDRAW);
				filterList.add(filterByType);

				//Filtro por empresa en bancos
				BmFilter filterByCompany = new BmFilter();
				filterByCompany.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getCompanyId(), bmoBankMovement.getBmoBankAccount().getCompanyId().toString());
				filterList.add(filterByCompany);

				//Filtro por orden de compra
//				BmFilter filterByRequisition = new BmFilter();
//				filterByRequisition.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionId(), bmoBankMovement.getRequisitionId().toInteger());
//				filterList.add(filterByRequisition);

				paccountSuggestBox = new UiSuggestBox(new BmoPaccount());
				paccountSuggestBox.addFilter(filterBySupplier);
				paccountSuggestBox.addFilter(filterByPaymentStatus);
				paccountSuggestBox.addFilter(filterByStatus);
				paccountSuggestBox.addFilter(filterByType);	
				paccountSuggestBox.addFilter(filterByCompany);
				//paccountSuggestBox.addFilter(filterByRequisition);
				*/
			} 
			// Tipo Abono, y NO es anticipo O.C.
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT) 
					&& !bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				bmoRaccount = new BmoRaccount();
				
				// Filtros CxC destino
				if (!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_MULTIPLECXC)) {
					BmFilter filterByCustomer = new BmFilter();
					filterByCustomer.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCustomerId(), bmoBankMovement.getCustomerId().toInteger());
					raccountSuggestBox.addFilter(filterByCustomer);
				}	

				BmFilter filterByPaymentStatus = new BmFilter();
				filterByPaymentStatus.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
				raccountSuggestBox.addFilter(filterByPaymentStatus);

				BmFilter filterByStatus = new BmFilter();
				filterByStatus.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_AUTHORIZED);
				raccountSuggestBox.addFilter(filterByStatus);

				BmFilter filterByType = new BmFilter();									
				filterByType.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), "" + BmoRaccountType.TYPE_WITHDRAW);
				raccountSuggestBox.addFilter(filterByType);

				//Filtro por empresa en bancos
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_ADDMBCALLCOMPANY)) {
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCompanyId(), bmoBankMovement.getBmoBankAccount().getCompanyId().toString());
					raccountSuggestBox.addFilter(filterByCompany);
				}
			} 
			// Tipo Abono, y SI anticipo O.C.
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT) 
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE) ) {
				bmoPaccount = new BmoPaccount();
				paccountSuggestBox = new UiSuggestBox(new BmoPaccount());

				//showSystemMessage("dev prov");
				// Filtros CxP destino
				BmFilter filterBySupplier = new BmFilter();
				filterBySupplier.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getSupplierId(), bmoBankMovement.getSupplierId().toInteger());
				paccountSuggestBox.addFilter(filterBySupplier);

				BmFilter filterByPaymentStatus = new BmFilter();
				filterByPaymentStatus.setValueOrFilter(bmoPaccount.getKind(), bmoPaccount.getPaymentStatus().getName(), "" + BmoPaccount.PAYMENTSTATUS_PENDING, "" + BmoPaccount.PAYMENTSTATUS_TOTAL);
				//filterByPaymentStatus.setValueOperatorFilter(bmoPaccount.getKind(), bmoPaccount.getPaymentStatus(), BmFilter.EQUALS, "" + BmoPaccount.PAYMENTSTATUS_TOTAL);
				paccountSuggestBox.addFilter(filterByPaymentStatus);

				BmFilter filterByStatus = new BmFilter();
				filterByStatus.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getStatus(), "" + BmoPaccount.STATUS_AUTHORIZED);
				paccountSuggestBox.addFilter(filterByStatus);

				//Filtro por empresa en bancos
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_ADDMBCALLCOMPANY)) {
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getCompanyId(), bmoBankMovement.getBmoBankAccount().getCompanyId().toString());
					paccountSuggestBox.addFilter(filterByCompany);
				}
			} 
			// Tipo Retiro, devolucion CxC
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW) 
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {

				bmoRaccount = new BmoRaccount();
				// Filtros CxC cliente
				BmFilter filterByCustomer = new BmFilter();
				filterByCustomer.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCustomerId(), bmoBankMovement.getCustomerId().toInteger());
				raccountSuggestBox.addFilter(filterByCustomer);	

				// Filtro de estatus Autorizado
				BmFilter filterByStatus = new BmFilter();
				filterByStatus.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getStatus(), "" + BmoRaccount.STATUS_AUTHORIZED);
				raccountSuggestBox.addFilter(filterByStatus);

				//Filtro por empresa en bancos
				if (!getSFParams().hasSpecialAccess(BmoBankMovement.ACCESS_ADDMBCALLCOMPANY)) {
					BmFilter filterByCompany = new BmFilter();
					filterByCompany.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getCompanyId(), bmoBankMovement.getBmoBankAccount().getCompanyId().toString());
					raccountSuggestBox.addFilter(filterByCompany);
				}
			}
			// Tipo Retiro, Disposicion de Credito
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW) 
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) {
				
				// Filtros Envio Pedido
				bmoOrderDelivery = new BmoOrderDelivery();
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmFilter filterByCustomer = new BmFilter();
				filterByCustomer.setValueFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getBmoOrder().getCustomerId(), bmoBankMovement.getCustomerId().toInteger());
				filterList.add(filterByCustomer);

				//Filtro de Entrega
				BmFilter filterDelivery = new BmFilter();
				filterDelivery.setValueOperatorFilter(bmoOrderDelivery.getKind(), bmoOrderDelivery.getBmoOrder().getDeliveryStatus(), BmFilter.EQUALS, "" + BmoOrder.DELIVERYSTATUS_TOTAL);
				filterList.add(filterDelivery);

				orderDeliverySuggestBox.addFilter(filterByCustomer);
				orderDeliverySuggestBox.addFilter(filterDelivery);
			} else {
				//showSystemMessage("Sin filtros");
			}

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {

			// Tipo Retiro, Pago CxP
			if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
				formTable.addField(1, 0, paccountSuggestBox, bmoBankMovConcept.getPaccountId());
			} 
			// Tipo Retiro, Anticipo O.C.
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				formTable.addField(2, 0, requisitionSuggestBox, bmoBankMovConcept.getRequisitionId());
			} 
			// Tipo Abono, NO anticipo O.C.
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT) 
					&& !bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE) ) {
				formTable.addField(3, 0, raccountSuggestBox, bmoBankMovConcept.getRaccountId());	
			} 
			// Tipo Abono, SI anticipo O.C.
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT) 
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE) ) {
				formTable.addField(1, 0, paccountSuggestBox, bmoBankMovConcept.getPaccountId());				
			} 
			// Tipo Retiro, Devolucion CxC
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
				formTable.addField(3, 0, raccountSuggestBox, bmoBankMovConcept.getRaccountId());
			} 
			// Tipo Retiro, Multiples CxC
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_MULTIPLECXC)) {
				formTable.addField(3, 0, raccountSuggestBox, bmoBankMovConcept.getRaccountId());
			} 
			// Tipo Retiro, Disposicion Creditos
			else if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)
					&& bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) {
				formTable.addField(4, 0, orderDeliverySuggestBox, bmoBankMovConcept.getOrderDeliveryId());
			}

			formTable.addField(5, 0, amountTextBox, bmoBankMovConcept.getAmount());
			amountTextBox.setText("0");
		}
		
		// Conversion de una moneda a otra
		public double currencyExchange(double amount, int currencyIdOrigin, double currencyParityOrigin, int currencyIdDestiny, double currencyParityDestiny) {
			double currencyExchange = 0;
			// Si es la misma moneda regresar el mismo monto, en caso contrario se hace la conversion
			if (currencyIdOrigin == currencyIdDestiny) {
				currencyExchange = amount;
				//				currencyExchange = ((amount * currencyParityOrigin) / currencyParityOrigin);
			} else {
				currencyExchange = ((amount * currencyParityOrigin) / currencyParityDestiny);
			}
			return currencyExchange;
		}

		// Cambio en campo de texto
		public void formTextChange(ValueChangeEvent<String> event) {
			NumberFormat nFmt = NumberFormat.getFormat("######.##");
			int currencyId = 0, currencyIdDestiny = 0;
			double parityOrigen = 0, parityDestiny = 0;
			// Se cambia el monto del concepto de banco
			if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)) {
				parityDestinyTextBox.setText("" + bmoPaccount.getBmoCurrency().getParity().toDouble()); // este no tiene sentido ya que es la paridad mas reciente
				currencyId = bmoPaccount.getCurrencyId().toInteger();
				parityOrigen = bmoPaccount.getCurrencyParity().toDouble();
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				parityDestinyTextBox.setText("" + bmoRequisition.getBmoCurrency().getParity().toDouble());
				currencyId = bmoRequisition.getCurrencyId().toInteger();
				parityOrigen = bmoRequisition.getCurrencyParity().toDouble();
			}else {
				parityDestinyTextBox.setText("" + bmoRaccount.getBmoCurrency().getParity().toDouble());
				currencyId = bmoRaccount.getCurrencyId().toInteger();
				parityOrigen = bmoRaccount.getCurrencyParity().toDouble();
			}
			//Obtener la paridad
			BmoBankAccount bmoBankAccount = bmoBankMovement.getBmoBankAccount();
			currencyIdDestiny = bmoBankAccount.getCurrencyId().toInteger();
			parityDestiny = bmoBankAccount.getBmoCurrency().getParity().toDouble();
			if (calculateCheckBox.getValue()) {

				double parity = Double.parseDouble(parityTextBox.getText());

				// Si la paridad origen es mayor al destino
				if (currencyId != bmoBankAccount.getCurrencyId().toInteger()) {
				//if (parityOrigen > parityDestiny) { 
					if (event.getSource() == amountTextBox) {
						// ******************* codigo de jv *******************
						//amountConvertedTextBox.setText("" + nFmt.format(Double.parseDouble(amountTextBox.getText()) * parity));
						//parity = parity / bmoBankAccount.getBmoCurrency().getParity().toDouble();
						//amountTextBox.setText("" + nFmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * factor));	
						// ******************* hasta aqui codigo javier *******************
						
						// Si mi doc es en moneda sistema, cambiar paridades ej:
						// Cuenta banco en usd, paridad de moneda 20
						// CP 1000 mxn, paridad 1
						// Ventana del flex al añadir concepto(con diferente moneda a la del doc/cuentaBanco): 
						//			Monto: 50
						// 			Paridad: 20 
						//			Aplicar CP/CC: 1000
						// AJUSTE: Esto es porque en el metodo(currencyExchange) toma la paridad origen del textBox(editable), 
						// y como vas a meterlo a otra moneda(usd) se debe dividir a la paridad destino.	
						// el calculo es: 1000(aplicar doc) * 1 (paridad origen) / 20(paridad destino) = 50
						if (parityOrigen == 1  && currencyId == 1) {
							//showSystemMessage("monedaSis");
							parityDestiny = parity;
							parity = parityOrigen;
						}
						
						// Se intercambia moneda/paridad ORIGEN por DESTINO, es el monto a convertir a la moneda de la cuenta de banco
						amountConvertedTextBox.setText("" + nFmt.format(currencyExchange(
								Double.parseDouble(amountTextBox.getText()), 
								currencyIdDestiny, 
								parityDestiny,
								currencyId, 
								parity)));
						/*
						showSystemMessage(
								" monto: " +amountTextBox.getText()
								+" |mOrigen: " +currencyId
								+" |pOrigen: " +parity
								+" |mDest: " +currencyIdDestiny
								+" |pDest: " +parityDestiny
								+" | montoConFin: "+ Double.parseDouble(amountConvertedTextBox.getText()));
						*/
					} 
					// Se cambia la paridad
					else if (event.getSource() == parityTextBox) {
						// ******************* codigo de jv *******************
						//amountTextBox.setText("" + nFmt.format(Double.parseDouble(amountConvertedTextBox.getText()) / parity ));
						//double factor = parity / bmoBankAccount.getBmoCurrency().getParity().toDouble();
						//amountTextBox.setText("" + nFmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * factor));	
						// ******************* hasta aqui codigo javier *******************
						
						// Si mi doc es en moneda sistema, cambiar paridades ej:
						// Cuenta banco en usd, paridad de moneda 20
						// CP 1000 mxn, paridad 1
						// Ventana del flex al añadir concepto(con diferente moneda a la del doc/cuentaBanco): 
						//			Monto: 50
						// 			Paridad: 20 
						//			Aplicar CP/CC: 1000

						// AJUSTE: Esto es porque en el metodo(currencyExchange) toma la paridad origen del textBox(editable), 
						// y como vas a meterlo a otra moneda(usd) se debe dividir a la paridad destino.
						// el calculo es: 1000(aplicar doc) * 1 (paridad origen) / 20(paridad destino) = 50
						if (parityOrigen == 1  && currencyId == 1) {
							//showSystemMessage("monedaSis");
							parityDestiny = parity;
							parity = parityOrigen;
						}
						amountTextBox.setText("" + nFmt.format(currencyExchange(
								Double.parseDouble(amountConvertedTextBox.getText()), 
								currencyId, 
								parity, 
								currencyIdDestiny, 
								parityDestiny)));
						/*
						showSystemMessage(
								" montoCon: " +amountConvertedTextBox.getText()
								+" |mOrigen: " +currencyId
								+" |pOrigen: " +parity
								+" |mDest: " +currencyIdDestiny
								+" |pDest: " +parityDestiny
								+" |montoFin: "+ Double.parseDouble(amountTextBox.getText()));
						*/
					}
					// Se cambia el monto a aplicar en cxp o cxc
					else if (event.getSource() == amountConvertedTextBox) {
						// ******************* codigo de jv *******************
//						if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP)
//							|| bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) 
//							parity = bmoPaccount.getBmoCurrency().getParity().toDouble();
//						else 
//							parity = bmoRaccount.getBmoCurrency().getParity().toDouble();
//						double factor = parity / bmoBankAccount.getBmoCurrency().getParity().toDouble();
//						amountTextBox.setText("" + nFmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * factor));	
						// ******************* hasta aqui codigo javier *******************
						
						// Si mi doc es en moneda sistema, cambiar paridades ej:
						// Cuenta banco en usd, paridad de moneda 20
						// CP 1000 mxn, paridad 1
						// Ventana del flex al añadir concepto(con diferente moneda a la del doc/cuentaBanco): 
						//			Monto: 50
						// 			Paridad: 20 
						//			Aplicar CP/CC: 1000
						// AJUSTE: Esto es porque en el metodo(currencyExchange) toma la paridad origen del textBox(editable), 
						// y como vas a meterlo a otra moneda(usd) se debe dividir a la paridad destino.
						// el calculo es: 1000(aplicar doc) * 1 (paridad origen) / 20(paridad destino) = 50
						if (parityOrigen == 1  && currencyId == 1) {
							//showSystemMessage("monedaSis");
							parityDestiny = parity;
							parity = parityOrigen;
						}
							
						amountTextBox.setText("" + nFmt.format(currencyExchange(
								Double.parseDouble(amountConvertedTextBox.getText()), 
								currencyId, 
								parity, 
								currencyIdDestiny, 
								parityDestiny)));
						/*
						showSystemMessage(
								" montoCon: " +amountConvertedTextBox.getText()
								+" |mOrigen: " +currencyId
								+" |pOrigen: " +parity
								+" |mDest: " +currencyIdDestiny
								+" |pDest: " +parityDestiny
								+" | montoFin: "+ Double.parseDouble(amountTextBox.getText()));
						*/
					}
				} else {
					if (event.getSource() == amountTextBox) {
						amountConvertedTextBox.setText("" + nFmt.format(Double.parseDouble(amountTextBox.getText()) / parity));
					} 
					// Se cambia la paridad
					else if (event.getSource() == parityTextBox) {
						amountTextBox.setText("" + nFmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * parity ));
					}
					// Se cambia el monto a aplicar en cxp o cxc
					else if (event.getSource() == amountConvertedTextBox) {
						amountTextBox.setText("" + nFmt.format(Double.parseDouble(amountConvertedTextBox.getText()) * parity ));					
					}
				}
			}	
		}

		// Cambio del Suggest
		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			NumberFormat numberFormat = NumberFormat.getFormat("###,###.####");
			NumberFormat nFmt = NumberFormat.getFormat("###,###.##");
			int currencyId = 0, currencyIdDestiny = 0;
			double parityOrigen = 0, parityDestiny = 0;
			// Reiniciar campos
			if (uiSuggestBox == requisitionSuggestBox) {
				BmoRequisition bmoRequisition = (BmoRequisition)requisitionSuggestBox.getSelectedBmObject();
				if (bmoRequisition == null) {
					formTable.removeAllRows();
					show();
				}
			}
			
			if (uiSuggestBox == paccountSuggestBox) {
				BmoPaccount bmoPaccount = (BmoPaccount)paccountSuggestBox.getSelectedBmObject();
				if (bmoPaccount == null) {
					formTable.removeAllRows();
					show();
				}
			}
			
			if (uiSuggestBox == raccountSuggestBox) {
				BmoRaccount bmoRaccount = (BmoRaccount)raccountSuggestBox.getSelectedBmObject();
				if (bmoRaccount == null) {
					formTable.removeAllRows();
					show();
				}
			}
			
			// Si esta asignada la CxP
			if (bmoBankMovement.getBmoBankMovType().getPaccountTypeId().toInteger() > 0) {
				//if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP))
					bmoPaccount = (BmoPaccount)paccountSuggestBox.getSelectedBmObject();

				//Obtener la paridad
				BmoBankAccount bmoBankAccount = bmoBankMovement.getBmoBankAccount();
				currencyIdDestiny = bmoBankAccount.getCurrencyId().toInteger();
				parityDestiny = bmoBankAccount.getBmoCurrency().getParity().toDouble();
				
				// Es anticipo proveedor
				if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE) 
						&& bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
					
					// Obtener datos de la OC
					bmoRequisition = (BmoRequisition)requisitionSuggestBox.getSelectedBmObject();
					currencyId = bmoRequisition.getCurrencyId().toInteger();
					parityOrigen = bmoRequisition.getCurrencyParity().toDouble();
					
					//Si el tipo de cambio no es el mismo, manejar el pago con paridad
					if (currencyId != bmoBankAccount.getCurrencyId().toInteger()) {

						// Mostrar campos(paridad, conversion, autocalcular) por si estan deshabilitados
						visibleWidgetCurrency(true);
						
						//formTable.addField(4, 0, amountTextBox, bmoBankMovConcept.getAmount());
						formTable.addField(6, 0, parityTextBox, parityTextBoxField);
						formTable.addField(7, 0, amountConvertedTextBox, amountConvertedTextBoxField);
						formTable.addField(8, 0, calculateCheckBox, calculateField);

						// Asignar valores
						// SI la cta Banco es moneda sistema, traer paridad reciente de la moneda del doc
						if (bmoBankAccount.getCurrencyId().toInteger() == ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger() )
							parityTextBox.setText("" + bmoRequisition.getBmoCurrency().getParity().toDouble());
						else
							parityTextBox.setText("" + bmoBankAccount.getBmoCurrency().getParity().toDouble());
						amountConvertedTextBox.setText("" + bmoRequisition.getBalance().toDouble());
						
						double parity = Double.parseDouble(parityTextBox.getText());
						
						// Ver comentario de funcionamiento en formTextChange() -> "AJUSTE"
						if (parityOrigen == 1  && currencyId == 1) {
							parityDestiny = parity;
							parity = parityOrigen;
						}
							
						amountTextBox.setText("" + nFmt.format(currencyExchange(
								Double.parseDouble(amountConvertedTextBox.getText()), 
								currencyId, 
								parity, 
								currencyIdDestiny, 
								parityDestiny)));
						
						/*
						showSystemMessage(
								" montoCon: " +amountConvertedTextBox.getText()
								+" |mOrigen: " +currencyId
								+" |pOrigen: " +parity
								+" |mDest: " +currencyIdDestiny
								+" |pDest: " +parityDestiny
								+" | montoFin: "+ Double.parseDouble(amountTextBox.getText()));
						*/

						calculateCheckBox.setValue(true);
					} else {
						amountTextBox.setText("" + bmoRequisition.getBalance().toDouble());
						amountTextBox.setEnabled(true);

						visibleWidgetCurrency(false);
					}
				} else {
					//Si el tipo de cambio no es el mismo, manejar el pago con paridad
					if (bmoPaccount.getCurrencyId().toInteger() != bmoBankAccount.getCurrencyId().toInteger()) {
						// Mostrar campos
						visibleWidgetCurrency(true);
						
						//Obtener la paridad del la CxC
						double parity = bmoPaccount.getCurrencyParity().toDouble();

						// Obtener la paridad del la CxC
						parity = bmoPaccount.getBmoCurrency().getParity().toDouble();
						//showSystemMessage("parityPACC: "+parity + " / parityBKCA: "+bmoBankAccount.getBmoCurrency().getParity().toDouble());
						// Obtener la paridad de la moneda
						double factor = parity / bmoBankAccount.getBmoCurrency().getParity().toDouble();
						amountTextBox.setText("" + nFmt.format(bmoPaccount.getBalance().toDouble() * factor));
						//showSystemMessage("monto: "+bmoPaccount.getBalance().toDouble() * factor);
						factor = parity * bmoBankAccount.getBmoCurrency().getParity().toDouble();

						try {
							parityTextBoxField.setValue("" + numberFormat.format(factor));
							bmoBankMovConcept.getAmount().setValue(amountTextBox.getText());
							// Si el monto es negativo, mandarlo positivo
							if (!(bmoBankMovConcept.getAmount().toDouble() > 0))
								bmoBankMovConcept.getAmount().setValue((bmoBankMovConcept.getAmount().toDouble() * -1));
							
							// Si el monto es negativo, mandarlo positivo
							if (!(bmoPaccount.getBalance().toDouble() > 0))
								bmoPaccount.getBalance().setValue((bmoPaccount.getBalance().toDouble() * -1));
							
							amountConvertedTextBoxField.setValue("" + bmoPaccount.getBalance().toDouble());
						} catch (BmException e) {
							showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
						}

						//formTable.addField(4, 0, amountTextBox, bmoBankMovConcept.getAmount());
						formTable.addField(6, 0, parityTextBox, parityTextBoxField);
						formTable.addField(7, 0, amountConvertedTextBox, amountConvertedTextBoxField);
						formTable.addField(8, 0, calculateCheckBox, calculateField);
						calculateCheckBox.setValue(true);
					} else {
						//showSystemMessage("ccc");

						try {
							// Si el monto es negativo, mandarlo positivo
							if (!(bmoPaccount.getBalance().toDouble() > 0))
								bmoPaccount.getBalance().setValue((bmoPaccount.getBalance().toDouble() * -1));
						} catch (BmException e) {
							showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
						}
						
						amountTextBox.setText(bmoPaccount.getBalance().toString());
						
						visibleWidgetCurrency(false);
					}
				}

				//Validar la paridad de la Cta Banco es mayor a 1
				if (!bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {

					//Si el tipo de cambio no es el mismo, manejar el pago con paridad
					if (bmoPaccount.getCurrencyId().toInteger() != bmoBankAccount.getCurrencyId().toInteger()) {
						// Mostrar campos
						visibleWidgetCurrency(true);

						//Obtener la paridad del la CxC
						double parity = bmoPaccount.getCurrencyParity().toDouble();

						// Obtener la paridad del la CxC
						parity = bmoPaccount.getBmoCurrency().getParity().toDouble();

						// Obtener la paridad de la moneda
						double factor = parity / bmoBankAccount.getBmoCurrency().getParity().toDouble();
						amountTextBox.setText("" + nFmt.format(bmoPaccount.getBalance().toDouble() * factor));
						factor = parity * bmoBankAccount.getBmoCurrency().getParity().toDouble();

						try {
							parityTextBoxField.setValue("" + numberFormat.format(factor));
							bmoBankMovConcept.getAmount().setValue(amountTextBox.getText());
							amountConvertedTextBoxField.setValue("" + bmoPaccount.getBalance().toDouble());
						} catch (BmException e) {
							showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
						}

						//formTable.addField(4, 0, amountTextBox, bmoBankMovConcept.getAmount());
						formTable.addField(6, 0, parityTextBox, parityTextBoxField);
						formTable.addField(7, 0, amountConvertedTextBox, amountConvertedTextBoxField);
						formTable.addField(8, 0, calculateCheckBox, calculateField);
						calculateCheckBox.setValue(true);
					} else {					
						amountTextBox.setText(bmoPaccount.getBalance().toString());
						
						visibleWidgetCurrency(false);
					}
				}	
			} 
			// Si esta asignada la CxC
			else if (bmoBankMovement.getBmoBankMovType().getRaccountTypeId().toInteger() > 0) {
				bmoRaccount = (BmoRaccount)raccountSuggestBox.getSelectedBmObject();

				// Obtener la paridad
				BmoBankAccount bmoBankAccount = bmoBankMovement.getBmoBankAccount();

				// Si el tipo de cambio no es el mismo, manejar el pago con paridad
				if (bmoRaccount.getCurrencyId().toInteger() != bmoBankAccount.getCurrencyId().toInteger()) {
					// Mostrar campos
					visibleWidgetCurrency(true);
					
					double parity = bmoRaccount.getCurrencyParity().toDouble();
					// Obtener la paridad de la moneda
					parity = bmoRaccount.getBmoCurrency().getParity().toDouble();
					//showSystemMessage("parityPACC: "+parity + " / parityBKCA: "+bmoBankAccount.getBmoCurrency().getParity().toDouble());

					// Obtener la paridad de la moneda
					double factor = parity / bmoBankAccount.getBmoCurrency().getParity().toDouble();
					amountTextBox.setText("" + nFmt.format(bmoRaccount.getBalance().toDouble() * factor));
					//showSystemMessage("monto: "+bmoRaccount.getBalance().toDouble() * factor);
					factor = parity * bmoBankAccount.getBmoCurrency().getParity().toDouble();


					try {						
						parityTextBoxField.setValue("" + numberFormat.format(factor));
						bmoBankMovConcept.getAmount().setValue(amountTextBox.getText());
						amountConvertedTextBoxField.setValue("" + bmoRaccount.getBalance().toDouble());
					} catch (BmException e) {
						showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
					}

					//formTable.addField(4, 0, amountTextBox, bmoBankMovConcept.getAmount());
					formTable.addField(6, 0, parityTextBox, parityTextBoxField);
					formTable.addField(7, 0, amountConvertedTextBox, amountConvertedTextBoxField);
					formTable.addField(8, 0, calculateCheckBox, calculateField);
					calculateCheckBox.setValue(true);
				} else {			
					if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
						//Quitar el negativo
						if (bmoRaccount.getBalance().toDouble() > 0)
							amountTextBox.setText(bmoRaccount.getBalance().toString());
						else {
							double balance = 0;
							balance = bmoRaccount.getBalance().toDouble() * -1;
							amountTextBox.setText("" + balance);
						}	
					} else {	
						amountTextBox.setText(bmoRaccount.getBalance().toString());
					}	
					visibleWidgetCurrency(false);
				}

			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) { 
				bmoOrderDelivery = (BmoOrderDelivery)orderDeliverySuggestBox.getSelectedBmObject();

				//Obtener la paridad
				BmoBankAccount bmoBankAccount = bmoBankMovement.getBmoBankAccount();

				//Validar la paridad de la Cta Banco es mayor a 1				
				if (!bmoBankAccount.getBmoCurrency().getParity().toString().equals("1") &&  
						!bmoBankAccount.getBmoCurrency().getParity().toString().equals("")) {
					// Mostrar campos
					visibleWidgetCurrency(true);
					try {
						parityTextBoxField.setValue(bmoBankAccount.getBmoCurrency().getParity().toDouble());
						//Hacer la conversion
						amountTextBox.setText("" + bmoOrderDelivery.getTotal().toDouble() / bmoBankAccount.getBmoCurrency().getParity().toDouble());
						amountConvertedTextBoxField.setValue("" + bmoOrderDelivery.getTotal().toDouble());
						amountConvertedTextBox.setText("" + bmoOrderDelivery.getTotal().toDouble());
					} catch (BmException e) {
						showSystemMessage(this.getClass().getName() + " ERROR: " + e.toString());
					}

					formTable.addField(6, 0, parityTextBox, parityTextBoxField);
					formTable.addField(7, 0, amountConvertedTextBox, amountConvertedTextBoxField);
					formTable.addField(8, 0, calculateCheckBox, calculateField);
					calculateCheckBox.setValue(true);
				} else {
					amountTextBox.setText(bmoOrderDelivery.getTotal().toString());
					visibleWidgetCurrency(false);
				}
			}

			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				bankMovConceptDialogBox.hide();
				reset();
			}
		}
		
		public void visibleWidgetCurrency(boolean a) {
			amountConvertedTextBox.setText("");
			amountConvertedTextBox.setVisible(a);
			
			parityTextBox.setText("");
			parityTextBox.setVisible(a);
			
			calculateCheckBox.setValue(a);
			calculateCheckBox.setVisible(a);
		}

		public void prepareSave() {
			try {
				bmoBankMovConcept = new BmoBankMovConcept();
				bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());
				bmoBankMovConcept.getPaccountId().setValue(paccountSuggestBox.getSelectedId());
				bmoBankMovConcept.getRaccountId().setValue(raccountSuggestBox.getSelectedId());
				bmoBankMovConcept.getOrderDeliveryId().setValue(orderDeliverySuggestBox.getSelectedId());
				bmoBankMovConcept.getCode().setValue(codeTextBox.getText());				
				bmoBankMovConcept.getCurrencyParity().setValue(parityTextBox.getText());
				bmoBankMovConcept.getAmount().setValue(amountTextBox.getText());
				bmoBankMovConcept.getAmountCoverted().setValue(amountConvertedTextBox.getText());
				bmoBankMovConcept.getRequisitionId().setValue(requisitionSuggestBox.getSelectedId());
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
					getUiParams().getBmObjectServiceAsync().save(bmoBankMovConcept.getPmClass(), bmoBankMovConcept, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

	// Variables para llamadas RPC
	public int getSaveItemChangeRpcAttempt() {
		return saveItemChangeRpcAttempt;
	}

	public void setSaveItemChangeRpcAttempt(int saveItemChangeRpcAttempt) {
		this.saveItemChangeRpcAttempt = saveItemChangeRpcAttempt;
	}

	public int getFindOrderRpcAttempt() {
		return findOrderRpcAttempt;
	}

	public void setFindOrderRpcAttempt(int findOrderRpcAttempt) {
		this.findOrderRpcAttempt = findOrderRpcAttempt;
	}

	public int getFindRequisitionRpcAttempt() {
		return findRequisitionRpcAttempt;
	}

	public void setFindRequisitionRpcAttempt(int findRequisitionRpcAttempt) {
		this.findRequisitionRpcAttempt = findRequisitionRpcAttempt;
	}

	public int getRaccountIdRPC() {
		return raccountIdRPC;
	}

	public void setRaccountIdRPC(int raccountIdRPC) {
		this.raccountIdRPC = raccountIdRPC;
	}

	public int getPaccountIdRPC() {
		return paccountIdRPC;
	}

	public void setPaccountIdRPC(int paccountIdRPC) {
		this.paccountIdRPC = paccountIdRPC;
	}

}
