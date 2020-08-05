///**
// * SYMGF
//
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author 
// * @version 2013-10
// */
//package com.flexwm.client.op; 
//
//import java.sql.Types;
//import java.util.Date;
//import com.flexwm.client.fi.UiBankMovement;
//import com.flexwm.client.fi.UiPaccountForm;
//import com.flexwm.shared.BmoFlexConfig;
//import com.flexwm.shared.fi.BmoBankMovConcept;
//import com.flexwm.shared.fi.BmoBudgetItem;
//import com.flexwm.shared.fi.BmoCurrency;
//import com.flexwm.shared.fi.BmoPaccount;
//import com.flexwm.shared.op.BmoProduct;
//import com.flexwm.shared.op.BmoRequisition;
//import com.flexwm.shared.op.BmoRequisitionItem;
//import com.flexwm.shared.op.BmoRequisitionReceipt;
//import com.flexwm.shared.op.BmoRequisitionReceiptItem;
//import com.flexwm.shared.op.BmoRequisitionType;
//import com.flexwm.shared.op.BmoSupplier;
//import com.flexwm.shared.op.BmoWarehouse;
//import com.flexwm.shared.op.BmoWhSection;
//import com.google.gwt.cell.client.ButtonCell;
//import com.google.gwt.cell.client.EditTextCell;
//import com.google.gwt.cell.client.FieldUpdater;
//import com.google.gwt.cell.client.TextCell;
//import com.google.gwt.core.client.Scheduler;
//import com.google.gwt.core.client.Scheduler.ScheduledCommand;
//import com.google.gwt.dom.client.Style.Unit;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
//import com.google.gwt.i18n.client.NumberFormat;
//import com.google.gwt.safehtml.shared.SafeHtmlUtils;
//import com.google.gwt.user.cellview.client.CellTable;
//import com.google.gwt.user.cellview.client.CellTree;
//import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
//import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HasHorizontalAlignment;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.Panel;
//import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.view.client.SelectionChangeEvent;
//import com.google.gwt.view.client.SingleSelectionModel;
//import com.symgae.client.ui.Ui;
//import com.symgae.client.ui.UiForm;
//import com.symgae.client.ui.UiFormFlexTable;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiListDataProvider;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.client.ui.UiSuggestBoxAction;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmField;
//import com.symgae.shared.BmFieldType;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.GwtUtil;
//import com.symgae.shared.SFException;
//import com.google.gwt.user.cellview.client.Column;
//
//
//public class UiRequisitionReceiptForm extends UiForm {
//	private BmoRequisitionReceiptItem bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
//	BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
//	BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
//
//	TextBox codeTextBox = new TextBox();
//	UiListBox typeListBox = new UiListBox(getUiParams());
//	UiListBox statusListBox = new UiListBox(getUiParams());
//	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
//	TextBox amountTextBox = new TextBox();	
//	TextBox discountTextBox = new TextBox();
//	TextBox taxTextBox = new TextBox();
//	TextBox totalTextBox = new TextBox();
//	UiListBox requisitionListBox = new UiListBox(getUiParams(), new BmoRequisition());
//	UiListBox whSectionListBox = new UiListBox(getUiParams(), new BmoWhSection());
//	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
//	UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
//	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
//	TextBox currencyParityTextBox = new TextBox();
//
//	//Calidad
//	UiListBox qualityListBox;
//	UiListBox punctualityListBox;
//	UiListBox serviceListBox;
//	UiListBox reliabilityListBox;
//	UiListBox handlingListBox;
//
//	private FlowPanel requisitionReceiptItemPanel = new FlowPanel();
//	private CellTable<BmObject> requisitionReceiptItemGrid = new CellTable<BmObject>();
//	private UiListDataProvider<BmObject> data;
//	protected DialogBox requisitionReceiptItemDialogBox;
//	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();	
//	private Button addItemButton = new Button("+ Item");
//
//	// Ciclo de vida orden de compra
//	public final SingleSelectionModel<BmObject> requisitionLifeCycleSelection = new SingleSelectionModel<BmObject>();
//	private UiRequisitionLifeCycleTree uiRequisitionLifeCycleTree;
//	private CellTree requisitionLifeCycleTreeCell;
//	private Label requisitionLifeCycleLabel = new Label("Seguimiento Órden de Compra:");
//	private Button docLifeCycleShowButton = new Button("Seguimiento");
//	private Button docLifeCycleCloseButton = new Button("Cerrar");
//
//	// Ingreso por codigo de barras
//	protected DialogBox barcodeDialogBox;
//	private Button barcodeDialogButton = new Button("Scanner");
//
//	int requisitionReceiptId;
//
//	public UiRequisitionReceiptForm(UiParams uiParams, int id) {
//		super(uiParams, new BmoRequisitionReceipt(), id);
//
//		requisitionReceiptId = id;
//
//		// Inicializar GRID si es registro existente
//		if (requisitionReceiptId > 0) { 
//
//			// Ciclo de vida de ordenes de compra
//			requisitionLifeCycleSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//				@Override
//				public void onSelectionChange(SelectionChangeEvent event) {
//					BmObject bmObject = (BmObject)requisitionLifeCycleSelection.getSelectedObject();
//					// Se esta tratando de abrir un registro
//					if (bmObject instanceof BmoRequisition) {
//						if (Window.confirm("Desea Abrir la Órden de Compra?"))
//							new UiRequisition(getUiParams()).edit(bmObject);
//					} else if (bmObject instanceof BmoRequisitionReceipt) {
//						if (Window.confirm("Desea Abrir el Recibo de Órden de Compra?"))
//							new UiRequisitionReceiptForm(getUiParams(), bmObject.getId()).show();
//					} else if (bmObject instanceof BmoPaccount) {
//						if (Window.confirm("Desea Abrir la Cuenta por Pagar?"))
//							new UiPaccountForm(getUiParams(), bmObject.getId()).show();
//					} else if (bmObject instanceof BmoBankMovConcept) {
//						if (Window.confirm("Desea Abrir el Movimiento Bancario?"))
//							new UiBankMovement(getUiParams()).edit(((BmoBankMovConcept)bmObject).getBmoBankMovement()); 
//					} 
//				}
//			});
//			docLifeCycleShowButton.setStyleName("formCloseButton");
//			docLifeCycleShowButton.addClickHandler(new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					showRequisitionLifeCycle();
//				}
//			});
//			docLifeCycleCloseButton.setStyleName("formCloseButton");
//			docLifeCycleCloseButton.addClickHandler(new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					hideRequisitionLifeCycle();
//				}
//			});
//
//			// Todas las operaciones con los items de la orden de compra
//			addItemButton.setStyleName("formSaveButton");
//			addItemButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					addItem();
//				}
//			});
//
//			// Pantalla de ingreso por codigo de barras
//			barcodeDialogButton.setStyleName("formSaveButton");
//			barcodeDialogButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					barcodeDialog();
//				}
//			});
//
//			//Filtar el contenido del recibo
//			BmFilter bmFilter = new BmFilter();
//			bmFilter.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId().getName(), requisitionReceiptId);
//			data = new UiListDataProvider<BmObject>(new BmoRequisitionReceiptItem(), bmFilter);
//
//			requisitionReceiptItemGrid.setWidth("100%");
//			requisitionReceiptItemPanel.setWidth("100%");
//
//			data.addDataDisplay(requisitionReceiptItemGrid);
//			requisitionReceiptItemPanel.add(requisitionReceiptItemGrid);
//
//			requisitionReceiptItemGrid.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {           
//				@Override
//				public void onLoadingStateChanged(LoadingStateChangeEvent event) {
//					if (event.getLoadingState() == LoadingState.LOADED) {
//						changeHeight();
//					}
//				}
//			}); 	
//
//		}
//	}
//
//	@Override
//	public void populateFields() {
//		bmoRequisitionReceipt = (BmoRequisitionReceipt)getBmObject();
//
//		// Ciclo de vida de ordenes de compra
//		uiRequisitionLifeCycleTree = new UiRequisitionLifeCycleTree(requisitionLifeCycleSelection, bmoRequisitionReceipt.getRequisitionId().toInteger());
//		requisitionLifeCycleTreeCell = new CellTree(uiRequisitionLifeCycleTree, requisitionLifeCycleSelection);
//		requisitionLifeCycleTreeCell.setAnimationEnabled(true);
//		requisitionLifeCycleTreeCell.addStyleName("quoteProductTree");
//		requisitionLifeCycleLabel.setStyleName("programSubtitle");
//
//		qualityListBox = new UiListBox(getUiParams());
//		punctualityListBox = new UiListBox(getUiParams());
//		serviceListBox = new UiListBox(getUiParams());
//		reliabilityListBox = new UiListBox(getUiParams());
//		handlingListBox = new UiListBox(getUiParams());
//
//
//		// Asignar valores por default
//		if (newRecord) {
//			try {
//				bmoRequisitionReceipt.getReceiptDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
//			}
//		}
//
//		formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoRequisitionReceipt.getCode());
//		formFlexTable.addField(1, 2, typeListBox, bmoRequisitionReceipt.getType());
//
//		formFlexTable.addField(2, 0, supplierSuggestBox, bmoRequisitionReceipt.getSupplierId());
//
//		formFlexTable.addLabelField(3, 0, bmoRequisitionReceipt.getReceiptDate());
//
//		// Llenado de Ordenes de Compra		
//		populateRequisitions(bmoRequisitionReceipt.getSupplierId().toInteger());
//
//		populateWhSections(bmoRequisitionReceipt.getBmoRequisition().getWarehouseId().toInteger());
//
//		// Si no esta asignada la moneda, buscar por la default
//		if (!(bmoRequisitionReceipt.getCurrencyId().toInteger() > 0)) {
//			try {
//				bmoRequisitionReceipt.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
//			} catch (BmException e) {
//				showSystemMessage("No se puede asignar moneda : " + e.toString());
//			}
//		}
//
//		if (!newRecord) {
//
//			formFlexTable.addButton(5, 0, addItemButton, "Agregar Item");
//			formFlexTable.addButton(5, 2, barcodeDialogButton, "Código de Barras");
//
//			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//				formFlexTable.addField(6, 0, budgetItemSuggestBox, bmoRequisitionReceipt.getBudgetItemId());
//			}	
//
//			formFlexTable.addSectionLabel(7, 0, "Items", 4);
//
//			formFlexTable.addPanel(8, 0, requisitionReceiptItemPanel, 4);
//
//			formFlexTable.addSectionLabel(9, 0, "Valoración del Proveedor", 4);
//			formFlexTable.addLabelField(10, 0, "(1=Pésimo .. 5=Excelente)");
//
//			formFlexTable.addField(11, 0, qualityListBox, bmoRequisitionReceipt.getQuality());
//			formFlexTable.addField(11, 2, punctualityListBox, bmoRequisitionReceipt.getPunctuality());
//			formFlexTable.addField(12, 0, serviceListBox, bmoRequisitionReceipt.getService());
//			formFlexTable.addField(13, 2, handlingListBox, bmoRequisitionReceipt.getHandling());
//			formFlexTable.addField(13, 0, reliabilityListBox, bmoRequisitionReceipt.getReliability());
//
//
//			formFlexTable.addSectionLabel(14, 0, "Totales", 4);
//
//			formFlexTable.addField(15, 0, currencyListBox, bmoRequisitionReceipt.getCurrencyId());
//			formFlexTable.addField(15, 2, currencyParityTextBox, bmoRequisitionReceipt.getCurrencyParity());
//
//			formFlexTable.addField(16, 0, statusListBox, bmoRequisitionReceipt.getStatus());			
//			formFlexTable.addField(16, 2, amountTextBox, bmoRequisitionReceipt.getAmount());
//			formFlexTable.addLabelField(17,0, bmoRequisitionReceipt.getPayment());						
//			formFlexTable.addField(17, 2, taxTextBox, bmoRequisitionReceipt.getTax());
//			formFlexTable.addField(18, 2, totalTextBox, bmoRequisitionReceipt.getTotal());
//
//			reset();
//		} else {
//			formFlexTable.addField(4, 0, currencyListBox, bmoRequisitionReceipt.getCurrencyId());
//			formFlexTable.addField(4, 2, currencyParityTextBox, bmoRequisitionReceipt.getCurrencyParity());
//			formFlexTable.addField(5, 0, statusListBox, bmoRequisitionReceipt.getStatus());
//			formFlexTable.addLabelField(5, 2, bmoRequisitionReceipt.getPayment());
//		}
//
//		populateValuationSupplier();
//
//
//		statusEffect();
//	}
//
//	@Override
//	public void postShow() {
//		if (id > 0)
//			buttonPanel.add(docLifeCycleShowButton);
//	}
//
//	public void populateValuationSupplier() {
//		// Calidad
//		qualityListBox.addItem("", "0");
//		qualityListBox.addItem("1", "1");
//		qualityListBox.addItem("2", "2");
//		qualityListBox.addItem("3", "3");
//		qualityListBox.addItem("4", "4");
//		qualityListBox.addItem("5", "5");
//
//		if (bmoRequisitionReceipt.getQuality().toInteger() == 0) qualityListBox.setSelectedIndex(0);
//		else if (bmoRequisitionReceipt.getQuality().toInteger() == 1) qualityListBox.setSelectedIndex(1);
//		else if (bmoRequisitionReceipt.getQuality().toInteger() == 2) qualityListBox.setSelectedIndex(2);
//		else if (bmoRequisitionReceipt.getQuality().toInteger() == 3) qualityListBox.setSelectedIndex(3);
//		else if (bmoRequisitionReceipt.getQuality().toInteger() == 4) qualityListBox.setSelectedIndex(4);
//		else if (bmoRequisitionReceipt.getQuality().toInteger() == 5) qualityListBox.setSelectedIndex(5);
//		else qualityListBox.setSelectedIndex(0);
//
//		// Puntualidad
//		punctualityListBox.addItem("", "0");
//		punctualityListBox.addItem("1", "1");
//		punctualityListBox.addItem("2", "2");
//		punctualityListBox.addItem("3", "3");
//		punctualityListBox.addItem("4", "4");
//		punctualityListBox.addItem("5", "5");
//
//		if (bmoRequisitionReceipt.getPunctuality().toInteger() == 0) punctualityListBox.setSelectedIndex(0);
//		else if (bmoRequisitionReceipt.getPunctuality().toInteger() == 1) punctualityListBox.setSelectedIndex(1);
//		else if (bmoRequisitionReceipt.getPunctuality().toInteger() == 2) punctualityListBox.setSelectedIndex(2);
//		else if (bmoRequisitionReceipt.getPunctuality().toInteger() == 3) punctualityListBox.setSelectedIndex(3);
//		else if (bmoRequisitionReceipt.getPunctuality().toInteger() == 4) punctualityListBox.setSelectedIndex(4);
//		else if (bmoRequisitionReceipt.getPunctuality().toInteger() == 5) punctualityListBox.setSelectedIndex(5);
//		else punctualityListBox.setSelectedIndex(0);
//
//		// Atencion
//		serviceListBox.addItem("", "0");
//		serviceListBox.addItem("1", "1");
//		serviceListBox.addItem("2", "2");
//		serviceListBox.addItem("3", "3");
//		serviceListBox.addItem("4", "4");
//		serviceListBox.addItem("5", "5");
//
//		if (bmoRequisitionReceipt.getService().toInteger() == 0) serviceListBox.setSelectedIndex(0);
//		else if (bmoRequisitionReceipt.getService().toInteger() == 1) serviceListBox.setSelectedIndex(1);
//		else if (bmoRequisitionReceipt.getService().toInteger() == 2) serviceListBox.setSelectedIndex(2);
//		else if (bmoRequisitionReceipt.getService().toInteger() == 3) serviceListBox.setSelectedIndex(3);
//		else if (bmoRequisitionReceipt.getService().toInteger() == 4) serviceListBox.setSelectedIndex(4);
//		else if (bmoRequisitionReceipt.getService().toInteger() == 5) serviceListBox.setSelectedIndex(5);
//		else serviceListBox.setSelectedIndex(0);
//
//		// Manejo y Descarga
//		handlingListBox.addItem("", "0");
//		handlingListBox.addItem("1", "1");
//		handlingListBox.addItem("2", "2");
//		handlingListBox.addItem("3", "3");
//		handlingListBox.addItem("4", "4");
//		handlingListBox.addItem("5", "5");
//
//		if (bmoRequisitionReceipt.getHandling().toInteger() == 0) handlingListBox.setSelectedIndex(0);
//		else if (bmoRequisitionReceipt.getHandling().toInteger() == 1) handlingListBox.setSelectedIndex(1);
//		else if (bmoRequisitionReceipt.getHandling().toInteger() == 2) handlingListBox.setSelectedIndex(2);
//		else if (bmoRequisitionReceipt.getHandling().toInteger() == 3) handlingListBox.setSelectedIndex(3);
//		else if (bmoRequisitionReceipt.getHandling().toInteger() == 4) handlingListBox.setSelectedIndex(4);
//		else if (bmoRequisitionReceipt.getHandling().toInteger() == 5) handlingListBox.setSelectedIndex(5);
//		else handlingListBox.setSelectedIndex(0);
//
//		// Veracidad de Documentos
//		reliabilityListBox.addItem("", "0");
//		reliabilityListBox.addItem("1", "1");
//		reliabilityListBox.addItem("2", "2");
//		reliabilityListBox.addItem("3", "3");
//		reliabilityListBox.addItem("4", "4");
//		reliabilityListBox.addItem("5", "5");
//
//		if (bmoRequisitionReceipt.getReliability().toInteger() == 0) reliabilityListBox.setSelectedIndex(0);
//		else if (bmoRequisitionReceipt.getReliability().toInteger() == 1) reliabilityListBox.setSelectedIndex(1);
//		else if (bmoRequisitionReceipt.getReliability().toInteger() == 2) reliabilityListBox.setSelectedIndex(2);
//		else if (bmoRequisitionReceipt.getReliability().toInteger() == 3) reliabilityListBox.setSelectedIndex(3);
//		else if (bmoRequisitionReceipt.getReliability().toInteger() == 4) reliabilityListBox.setSelectedIndex(4);
//		else if (bmoRequisitionReceipt.getReliability().toInteger() == 5) reliabilityListBox.setSelectedIndex(5);
//		else reliabilityListBox.setSelectedIndex(0);
//
//	}
//
//	public void reset() {
//		// Elimina columnas del grid
//		while (requisitionReceiptItemGrid.getColumnCount() > 0)
//			requisitionReceiptItemGrid.removeColumn(0);
//
//		// Crea las columnas
//		setColumns();
//
//		updateAmount(id);
//		data.list();
//		requisitionReceiptItemGrid.redraw();
//	}
//
//	public void changeHeight() {
//		requisitionReceiptItemGrid.setPageSize(data.getList().size());
//		requisitionReceiptItemGrid.setVisibleRange(0, data.getList().size());
//	}
//
//	public void setColumns() {
//		// Cantidad
//		Column<BmObject, String> quantityColumn;
//		if (bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_REVISION)) {
//			quantityColumn = new Column<BmObject, String>(new EditTextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoRequisitionReceiptItem)bmObject).getQuantity().toString();
//				}
//			};
//			quantityColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//				@Override
//				public void update(int index, BmObject bmObject, String value) {
//					changeQuantity(bmObject, value);
//				}
//			});
//		} else {
//			quantityColumn = new Column<BmObject, String>(new TextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoRequisitionReceiptItem)bmObject).getQuantity().toString();
//				}
//			};
//		}
//		requisitionReceiptItemGrid.addColumn(quantityColumn, SafeHtmlUtils.fromSafeConstant("Cant."));
//		requisitionReceiptItemGrid.setColumnWidth(quantityColumn, 50, Unit.PX);
//		quantityColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//
//		// Cantidad O. C.
//		Column<BmObject, String> requisitionItemQuantityColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoRequisitionReceiptItem)bmObject).getBmoRequisitionItem().getQuantity().toString();
//			}
//		};
//		requisitionReceiptItemGrid.setColumnWidth(requisitionItemQuantityColumn, 50, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(requisitionItemQuantityColumn, SafeHtmlUtils.fromSafeConstant("O. C."));		
//
//		// Balance la O. C.
//		Column<BmObject, String> quantityBalanceColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoRequisitionReceiptItem)bmObject).getQuantityBalance().toString();
//			}
//		};		
//		requisitionReceiptItemGrid.setColumnWidth(quantityBalanceColumn, 50, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(quantityBalanceColumn, SafeHtmlUtils.fromSafeConstant("Pend."));
//
//		// Devolucion
//		Column<BmObject, String> quantityReturnColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoRequisitionReceiptItem)bmObject).getQuantityReturned().toString();
//			}
//		};		
//		requisitionReceiptItemGrid.setColumnWidth(quantityReturnColumn, 50, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(quantityReturnColumn, SafeHtmlUtils.fromSafeConstant("Dev."));
//
//		// Clave
//		Column<BmObject, String> codeColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoRequisitionReceiptItem)bmObject).getBmoRequisitionItem().getBmoProduct().getCode().toString();
//			}
//		};
//		requisitionReceiptItemGrid.setColumnWidth(codeColumn, 70, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(codeColumn, SafeHtmlUtils.fromSafeConstant("Clave"));	
//
//		// Nombre
//		Column<BmObject, String> prodColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoRequisitionReceiptItem)bmObject).getName().toString(50);
//			}
//		};
//		requisitionReceiptItemGrid.setColumnWidth(prodColumn, 150, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(prodColumn, SafeHtmlUtils.fromSafeConstant("Nombre"));		
//
//		// Número de Serie
//		Column<BmObject, String> serialColumn;
//		if (bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_REVISION)) {
//			serialColumn = new Column<BmObject, String>(new EditTextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoRequisitionReceiptItem)bmObject).getSerial().toString();
//				}
//			};
//			serialColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//				@Override
//				public void update(int index, BmObject bmObject, String value) {
//					changeSerial(bmObject, value);
//				}
//			});
//		} else {
//			serialColumn = new Column<BmObject, String>(new TextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoRequisitionReceiptItem)bmObject).getSerial().toString();
//				}
//			};
//		}
//		requisitionReceiptItemGrid.addColumn(serialColumn, SafeHtmlUtils.fromSafeConstant("#Serie/Lote"));
//		requisitionReceiptItemGrid.setColumnWidth(serialColumn, 100, Unit.PX);
//		serialColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//
//		// Dias
//		Column<BmObject, String> daysColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				return ((BmoRequisitionReceiptItem)bmObject).getBmoRequisitionItem().getDays().toString();
//			}
//		};
//		requisitionReceiptItemGrid.setColumnWidth(daysColumn, 50, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(daysColumn, SafeHtmlUtils.fromSafeConstant("Dias"));
//
//		// Precio
//		Column<BmObject, String> priceColumn;
//		if (bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_REVISION)
//				&& getSFParams().hasSpecialAccess(BmoRequisitionReceipt.CHANGE_ITEMRECEIPTS)) {
//			priceColumn = new Column<BmObject, String>(new EditTextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return ((BmoRequisitionReceiptItem)bmObject).getPrice().toString();
//				}
//			};
//			priceColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//				@Override
//				public void update(int index, BmObject bmObject, String value) {
//					changePrice(bmObject, value);
//				}
//			});
//		} else {
//			priceColumn = new Column<BmObject, String>(new TextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					numberFormat = NumberFormat.getCurrencyFormat();
//					String formatted = numberFormat.format(((BmoRequisitionReceiptItem)bmObject).getPrice().toDouble());
//					return (formatted);
//				}
//			};
//		}
//		requisitionReceiptItemGrid.addColumn(priceColumn, SafeHtmlUtils.fromSafeConstant("Precio"));
//		requisitionReceiptItemGrid.setColumnWidth(priceColumn, 100, Unit.PX);
//		priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//
//		// Total
//		Column<BmObject, String> totalColumn = new Column<BmObject, String>(new TextCell()) {
//			@Override
//			public String getValue(BmObject bmObject) {
//				numberFormat = NumberFormat.getCurrencyFormat();
//				String formatted = numberFormat.format(((BmoRequisitionReceiptItem)bmObject).getAmount().toDouble());
//				return (formatted);
//			}
//		};
//		totalColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//		requisitionReceiptItemGrid.setColumnWidth(totalColumn, 120, Unit.PX);
//		requisitionReceiptItemGrid.addColumn(totalColumn, SafeHtmlUtils.fromSafeConstant("Total"));
//
//		// Eliminar
//		Column<BmObject, String> deleteColumn;
//		if (bmoRequisitionReceipt.getStatus().equals(BmoRequisitionReceipt.STATUS_REVISION)) {
//			deleteColumn = new Column<BmObject, String>(new ButtonCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return "-";
//				}
//			};
//			deleteColumn.setFieldUpdater(new FieldUpdater<BmObject, String>() {
//				@Override
//				public void update(int index, BmObject bmObject, String value) {
//					deleteItem(bmObject);
//				}
//			});
//		} else {
//			deleteColumn = new Column<BmObject, String>(new TextCell()) {
//				@Override
//				public String getValue(BmObject bmObject) {
//					return " ";
//				}
//			};
//		}
//		requisitionReceiptItemGrid.addColumn(deleteColumn, SafeHtmlUtils.fromSafeConstant("Eliminar"));
//		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		requisitionReceiptItemGrid.setColumnWidth(deleteColumn, 50, Unit.PX);
//	}
//
//	private void showRequisitionLifeCycle() {
//		getUiParams().getUiTemplate().showEastPanel();
//		getUiParams().getUiTemplate().getEastPanel().add(requisitionLifeCycleLabel);
//		getUiParams().getUiTemplate().getEastPanel().add(requisitionLifeCycleTreeCell);
//		getUiParams().getUiTemplate().getEastPanel().add(docLifeCycleCloseButton);
//	}
//
//	private void hideRequisitionLifeCycle() {
//		getUiParams().getUiTemplate().hideEastPanel();
//	}
//
//	@Override
//	public void formListChange(ChangeEvent event) {
//		if (event.getSource() == requisitionListBox) {
//
//			BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();
//			if (bmoRequisition != null) {
//				populateWhSections(bmoRequisition.getWarehouseId().toInteger());
//				populateCurrency(bmoRequisition.getCurrencyId().toInteger(), bmoRequisition.getCurrencyParity().toDouble());
//			} else {
//				populateWhSections(-1);
//			}
//			statusEffect();
//		} else if (event.getSource() == statusListBox)
//			update("Desea cambiar el Status de la Recepción de Órden de Compra?");
//	}
//
//	// Accion cambio SuggestBox
//	@Override
//	public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
//		if (uiSuggestBox == supplierSuggestBox) {
//			populateRequisitions(supplierSuggestBox.getSelectedId());	
//			statusEffect();
//		}
//	}
//
//	private void statusEffect() {
//		// Deshabilita campos
//		codeTextBox.setEnabled(false);
//		requisitionListBox.setEnabled(false);
//		amountTextBox.setEnabled(false);
//		taxTextBox.setEnabled(false);
//		discountTextBox.setEnabled(false);
//		totalTextBox.setEnabled(false);		
//		paymentStatusListBox.setEnabled(false);
//		supplierSuggestBox.setEnabled(false);		
//		addItemButton.setVisible(false);
//		barcodeDialogButton.setVisible(false);
//		whSectionListBox.setEnabled(false);
//		statusListBox.setEnabled(false);
//		typeListBox.setEnabled(false);
//		budgetItemSuggestBox.setEnabled(false);
//		currencyListBox.setEnabled(false);
//		currencyParityTextBox.setEnabled(false);
//
//		if (newRecord) {
//			supplierSuggestBox.setEnabled(true);			
//
//			if (supplierSuggestBox.getSelectedId() > 0)
//				requisitionListBox.setEnabled(true);
//
//			if (!requisitionListBox.getSelectedId().equals("0")) {
//				whSectionListBox.setEnabled(true);
//			}
//
//			typeListBox.setEnabled(true);
//
//		} else {
//			// De acuerdo al estatus habilitar campos
//			if (bmoRequisitionReceipt.getStatus().toChar() == BmoRequisitionReceipt.STATUS_REVISION) {
//				addItemButton.setVisible(true);
//				barcodeDialogButton.setVisible(true);	
//			} else {
//				qualityListBox.setEnabled(false);
//				punctualityListBox.setEnabled(false);
//				serviceListBox.setEnabled(false);
//				reliabilityListBox.setEnabled(false);
//				handlingListBox.setEnabled(false);
//			}
//
//			if (!newRecord && getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_CHANGEEVALUATEPV)) {
//				qualityListBox.setEnabled(true);
//				punctualityListBox.setEnabled(true);
//				serviceListBox.setEnabled(true);
//				reliabilityListBox.setEnabled(true);
//				handlingListBox.setEnabled(true);
//			}
//
//			// Validar permisos para cambiar estatus, si no esta provisionado ya el pago
//			if (bmoRequisitionReceipt.getPayment().equals(BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED)) {
//				if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_CHANGESTATUS)) {				 	 
//					statusListBox.setEnabled(true);
//				} else {
//
//					BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();				
//					if (bmoRequisition == null) {
//						bmoRequisition = (BmoRequisition)bmoRequisitionReceipt.getBmoRequisition();
//						bmoRequisitionType = (BmoRequisitionType)bmoRequisition.getBmoRequisitionType();
//					}	
//
//					/*
//					// Se quita, para poder recibir, pero no autorizar.
//					// Validar permisos distintos para autorizar estatus
//					if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_PURCHASE)) {
//						if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_PURCHASE))
//							statusListBox.setEnabled(true);
//					} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_RENTAL)) {
//						if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_RENTAL))
//							statusListBox.setEnabled(true);
//					} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_SERVICE)) {
//						if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_SERVICE))
//							statusListBox.setEnabled(true);
//					} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)) {
//						if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_TRAVELEXPENSE))
//							statusListBox.setEnabled(true);
//					} else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_COMMISION)) {
//						if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_COMMISION))
//							statusListBox.setEnabled(true);
//					}
//					 */
//				}
//			}
//
//			// Formato a los campos de totales
//			amountTextBox.setText(numberFormat.format(bmoRequisitionReceipt.getAmount().toDouble()));
//			taxTextBox.setText(numberFormat.format(bmoRequisitionReceipt.getTax().toDouble()));
//			totalTextBox.setText(numberFormat.format(bmoRequisitionReceipt.getTotal().toDouble()));
//		}
//	}
//
//	private void setAmount(BmoRequisitionReceipt bmoRequisitionReceipt) {
//		numberFormat = NumberFormat.getCurrencyFormat();
//		amountTextBox.setText(numberFormat.format(bmoRequisitionReceipt.getAmount().toDouble()));
//		taxTextBox.setText(numberFormat.format(bmoRequisitionReceipt.getTax().toDouble()));
//		totalTextBox.setText(numberFormat.format(bmoRequisitionReceipt.getTotal().toDouble()));		
//		setAmount("" + bmoRequisitionReceipt.getAmount().toDouble(), "" + bmoRequisitionReceipt.getTax().toDouble(), "" + bmoRequisitionReceipt.getTotal().toDouble());
//	}
//
//	private void setAmount(String amount, String tax, String total) {
//		double a = Double.parseDouble(amount);
//		amountTextBox.setText(numberFormat.format(a));
//
//		a = Double.parseDouble(tax);
//		taxTextBox.setText(numberFormat.format(a));
//
//		a = Double.parseDouble(total);
//		totalTextBox.setText(numberFormat.format(a));
//	}
//
//	@Override
//	public BmObject populateBObject() throws BmException {
//		bmoRequisitionReceipt.setId(id);
//		bmoRequisitionReceipt.getCode().setValue(codeTextBox.getText());
//		bmoRequisitionReceipt.getType().setValue(typeListBox.getSelectedCode());
//		bmoRequisitionReceipt.getStatus().setValue(statusListBox.getSelectedCode());		
//		bmoRequisitionReceipt.getAmount().setValue(amountTextBox.getText());
//		bmoRequisitionReceipt.getDiscount().setValue(discountTextBox.getText());
//		bmoRequisitionReceipt.getTax().setValue(taxTextBox.getText());
//		bmoRequisitionReceipt.getTotal().setValue(totalTextBox.getText());
//		bmoRequisitionReceipt.getRequisitionId().setValue(requisitionListBox.getSelectedId());		
//		bmoRequisitionReceipt.getWhSectionId().setValue(whSectionListBox.getSelectedId());	
//		bmoRequisitionReceipt.getSupplierId().setValue(supplierSuggestBox.getSelectedId());
//		bmoRequisitionReceipt.getQuality().setValue(qualityListBox.getSelectedCode());
//		bmoRequisitionReceipt.getService().setValue(serviceListBox.getSelectedCode());
//		bmoRequisitionReceipt.getPunctuality().setValue(punctualityListBox.getSelectedCode());
//		bmoRequisitionReceipt.getReliability().setValue(reliabilityListBox.getSelectedCode());
//		bmoRequisitionReceipt.getHandling().setValue(handlingListBox.getSelectedCode());
//		bmoRequisitionReceipt.getBudgetItemId().setValue(budgetItemSuggestBox.getSelectedId());
//		bmoRequisitionReceipt.getCurrencyId().setValue(currencyListBox.getSelectedId());
//		bmoRequisitionReceipt.getCurrencyParity().setValue(currencyParityTextBox.getText());
//
//		return bmoRequisitionReceipt;
//	}
//
//	@Override
//	public void close() {
//		UiRequisitionReceiptList uiRequisitionReceiptList = new UiRequisitionReceiptList(getUiParams());
//		uiRequisitionReceiptList.show();
//	}
//
//	@Override
//	public void saveNext() {
//		if (newRecord) { 
//			UiRequisitionReceiptForm uiRequisitionReceiptForm = new UiRequisitionReceiptForm(getUiParams(), getBmObject().getId());
//			uiRequisitionReceiptForm.show();
//		} else {
//			UiRequisitionReceiptList uiRequisitionReceiptList = new UiRequisitionReceiptList(getUiParams());
//			uiRequisitionReceiptList.show();
//		}		
//	}
//
//	public void updateAmount(int id){
//		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
//			}
//
//			public void onSuccess(BmObject result) {
//				stopLoading();
//				setAmount((BmoRequisitionReceipt)result);
//			}
//		};
//
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().get(bmoRequisitionReceipt.getPmClass(), id, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-updateAmount(): ERROR " + e.toString());
//		}
//	}
//
//	private void populateRequisitions(int supplierId) {
//		// Filtros de ordenes de compra
//		requisitionListBox = new UiListBox(getUiParams(), new BmoRequisition());
//		BmoRequisition bmoRequisition = new BmoRequisition();
//
//		// Ya esta elegido el proveedor
//		if (supplierId > 0) {
//
//			BmFilter bmFilterReqAutorized = new BmFilter();
//			BmFilter bmFilterReqDelivery = new BmFilter();
//			BmFilter bmFilterReqBySupplier = new BmFilter();
//
//			if (bmoRequisitionReceipt.getStatus().toChar() == BmoRequisitionReceipt.STATUS_REVISION) {
//				bmFilterReqAutorized.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getStatus(), 
//						"" + BmoRequisition.STATUS_AUTHORIZED);
//
//				if (typeListBox.getSelectedCode().toString().equals("" + BmoRequisitionReceipt.TYPE_RECEIPT))
//					bmFilterReqDelivery.setValueOrFilter(bmoRequisition.getKind(), 
//							bmoRequisition.getDeliveryStatus().getName(), "" + BmoRequisition.DELIVERYSTATUS_REVISION, "" + BmoRequisition.DELIVERYSTATUS_PARTIAL);
//				else 
//					bmFilterReqDelivery.setValueOrFilter(bmoRequisition.getKind(), 
//							bmoRequisition.getDeliveryStatus().getName(), "" + BmoRequisition.DELIVERYSTATUS_PARTIAL, "" + BmoRequisition.DELIVERYSTATUS_TOTAL);				
//
//				requisitionListBox.addBmFilter(bmFilterReqAutorized);
//				requisitionListBox.addBmFilter(bmFilterReqDelivery);
//
//			} else {
//				// No esta elegido el proveedor, filtrar
//				bmFilterReqAutorized.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getIdField(), bmoRequisitionReceipt.getRequisitionId().toInteger());
//				requisitionListBox.addBmFilter(bmFilterReqAutorized);
//			}
//
//			bmFilterReqBySupplier.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getSupplierId(), supplierId);
//			requisitionListBox.addBmFilter(bmFilterReqBySupplier);
//		} else {
//			BmFilter bmFilterLimitRecords = new BmFilter();
//			bmFilterLimitRecords.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getSupplierId(), "-1");
//			requisitionListBox.addBmFilter(bmFilterLimitRecords);
//		}
//
//		formFlexTable.addField(2, 2, requisitionListBox, bmoRequisitionReceipt.getRequisitionId());
//
//		// Deshabilita el tipo, ya no se puede cambiar
//		typeListBox.setEnabled(false);
//	}
//
//	private void populateWhSections(int warehouseId) {
//		// Filtros de secciones de almacen
//		whSectionListBox = new UiListBox(getUiParams(), new BmoWhSection());
//		whSectionListBox.clear();
//		whSectionListBox.clearFilters();
//		whSectionListBox.setEnabled(false);
//		BmoWhSection bmoWhSection = new BmoWhSection();
//		if (warehouseId > 0) {
//
//			BmFilter bmFilterByWarehouse = new BmFilter();
//			bmFilterByWarehouse.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getWarehouseId(), warehouseId);
//
//			BmFilter bmFilterByType = new BmFilter();	
//			bmFilterByType.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getBmoWarehouse().getType(), 
//					"" + BmoWarehouse.TYPE_NORMAL);
//
//			whSectionListBox.addBmFilter(bmFilterByWarehouse);
//			whSectionListBox.addBmFilter(bmFilterByType);
//			whSectionListBox.setEnabled(true);
//		} else {
//			BmFilter bmFilterLimitRecords = new BmFilter();
//			bmFilterLimitRecords.setValueFilter(bmoWhSection.getKind(), bmoWhSection.getOrderId(), "-1");
//			whSectionListBox.addBmFilter(bmFilterLimitRecords);
//		}
//
//		formFlexTable.addField(3, 2, whSectionListBox, bmoRequisitionReceipt.getWhSectionId());
//		typeListBox.setEnabled(false);
//	}
//
//	private void populateCurrency(int requisitionCurrency, double requisitionParity) {
//		// Coloca valores de la OC
//		currencyListBox.setSelectedId ("" + requisitionCurrency);
//		currencyParityTextBox.setText("" + requisitionParity);
//	}
//
//	public void saveAmountChange() {
//		// Establece eventos ante respuesta de servicio
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-saveDiscount(): ERROR " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();				
//				processRequisitionReceiptUpdateResult(result);
//			}
//		};
//
//		// Llamada al servicio RPC
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().save(bmoRequisitionReceipt.getPmClass(), bmoRequisitionReceipt, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-saveAmountChange(): ERROR " + e.toString());
//		}
//	}
//
//	public void processRequisitionReceiptUpdateResult(BmUpdateResult result) {
//		if (result.hasErrors()) showFormMsg("Errores al actualizar el recibo .", "Errores al actualizar el total del recibo: " + result.errorsToString());
//		else updateAmount(id);
//	}
//
//	public void changeQuantity(BmObject bmObject, String quantity) {
//		boolean changeAccess = false;
//		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;
//		try {			
//			double q = Double.parseDouble(quantity);
//
//			if (getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_CHANGESTATUS)) {	
//				changeAccess = true;
//			} else {
//
//				//Llenar el objeto de los tipos
//				BmoRequisition bmoRequisition = (BmoRequisition)requisitionListBox.getSelectedBmObject();				
//				if (bmoRequisition == null) {
//					bmoRequisition = (BmoRequisition)bmoRequisitionReceipt.getBmoRequisition();
//					bmoRequisitionType = (BmoRequisitionType)bmoRequisition.getBmoRequisitionType();
//				}	
//
//				//Validar los grupos
//				if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_PURCHASE)
//						&& getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_PURCHASE))
//					changeAccess = true;
//				else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_RENTAL)
//						&& getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_RENTAL))
//					changeAccess = true;
//				else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_SERVICE)
//						&& getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_PURCHASE))
//					changeAccess = true;
//				else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE) 
//						&& getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_TRAVELEXPENSE))
//					changeAccess = true;					
//				else if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_COMMISION)
//						&& getSFParams().hasSpecialAccess(BmoRequisitionReceipt.ACCESS_COMMISION)) 
//					changeAccess = true;
//			}			
//			if (changeAccess) {
//				// Si requiere serial, primero solicitarlo
//				// Identificar si es producto
//				if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getProductId().toInteger() > 0) {
//					if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL &&
//							(q > 1)) {
//						showErrorMessage("La Cantidad no puede ser mayor a 1.");
//						reset();
//					} else {
//						bmoRequisitionReceiptItem.getQuantity().setValue(q);
//						saveItemChange();
//					}
//				} else {
//					bmoRequisitionReceiptItem.getQuantity().setValue(q);
//					saveItemChange();
//				}
//			} else {
//				showErrorMessage("No tiene Autorización para recibir Órdenes de Compra.");
//				reset();				
//			}
//
//		} catch (BmException e) {
//			showErrorMessage(this.getClass().getName() + "-changeQuantity(): ERROR " + e.toString());
//			reset();
//		}
//	}
//
//	public void changeSerial(BmObject bmObject, String serial) {
//		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;
//		try {
//			if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
//				// Si esta asignado el # de serie, asignar cantidad a 1
//				if (serial.length() > 0)
//					bmoRequisitionReceiptItem.getQuantity().setValue(1);
//				bmoRequisitionReceiptItem.getSerial().setValue(serial);
//				saveItemChange();
//			} else if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_BATCH) {
//				bmoRequisitionReceiptItem.getSerial().setValue(serial);
//				saveItemChange();
//			} else {
//				showErrorMessage("No aplica cambio de #Serie/Lote para este Item.");
//				reset();
//			}
//		} catch (BmException e) {
//			showErrorMessage(this.getClass().getName() + "-changeSerial(): ERROR " + e.toString());
//		}
//	}
//
//	public void changePrice(BmObject bmObject, String price) {
//		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;
//		try {
//			double p = Double.parseDouble(price);
//			if (p > 0) {
//				bmoRequisitionReceiptItem.getPrice().setValue(price);
//				saveItemChange();
//			}
//		} catch (BmException e) {
//			showErrorMessage(this.getClass().getName() + "-changePrice(): ERROR " + e.toString());
//		}
//	}
//
//	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
//		if (bmUpdateResult.hasErrors()) 
//			showErrorMessage("Error al modificar el Item: " + bmUpdateResult.errorsToString());
//		this.reset();
//	}
//
//	public void processItemDelete(BmUpdateResult bmUpdateResult) {
//		if (bmUpdateResult.hasErrors()) 
//			showFormMsg("Error al modificar el Item.", "Error al modificar el Item: " + bmUpdateResult.errorsToString());
//		else {
//			this.reset();			
//		}		
//	}
//
//	public void saveItemChange() {
//		// Establece eventos ante respuesta de servicio
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();
//				processItemChangeSave(result);
//			}
//		};
//
//		// Llamada al servicio RPC
//		try {
//			if (!isLoading()){
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().save(bmoRequisitionReceiptItem.getPmClass(), bmoRequisitionReceiptItem, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-saveItemChange(): ERROR " + e.toString());
//		}
//	}
//
//	private void deleteItem(BmObject bmObject) {
//		bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmObject;
//		deleteItem();
//	}
//
//	public void deleteItem() {
//		// Establece eventos ante respuesta de servicio
//		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//			public void onFailure(Throwable caught) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + caught.toString());
//			}
//
//			public void onSuccess(BmUpdateResult result) {
//				stopLoading();
//				processItemDelete(result);
//			}
//		};
//
//		// Llamada al servicio RPC
//		try {
//			if (!isLoading()) {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().delete(bmoRequisitionReceiptItem.getPmClass(), bmoRequisitionReceiptItem, callback);
//			}
//		} catch (SFException e) {
//			stopLoading();
//			showErrorMessage(this.getClass().getName() + "-deleteItem(): ERROR " + e.toString());
//		}
//	}
//
//
//	public void addItem() {
//		requisitionReceiptItemDialogBox = new DialogBox(true);
//		requisitionReceiptItemDialogBox.setGlassEnabled(true);
//		requisitionReceiptItemDialogBox.setText("Item del Recibo de O.C.");
//
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("400px", "200px");
//
//		requisitionReceiptItemDialogBox.setWidget(vp);
//
//		UiRequisitionReceiptItemForm requisitionReceiptItemForm = new UiRequisitionReceiptItemForm(getUiParams(), vp, bmoRequisitionReceipt, new BmoRequisitionReceiptItem());
//
//		requisitionReceiptItemForm.show();
//
//		requisitionReceiptItemDialogBox.center();
//		requisitionReceiptItemDialogBox.show();
//	}
//
//
//	public void barcodeDialog() {
//		barcodeDialogBox = new DialogBox(true);
//		barcodeDialogBox.setGlassEnabled(true);
//		barcodeDialogBox.setText("Scanner Código de Barras");
//
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("250px", "200px");
//
//		barcodeDialogBox.setWidget(vp);
//
//		UiBarcodeForm uiBarcodeForm = new UiBarcodeForm(getUiParams(), vp, bmoRequisitionReceipt);
//		uiBarcodeForm.show();
//
//		barcodeDialogBox.center();
//		barcodeDialogBox.show();
//	}
//
//	// Agrega un item de un producto a la CXP
//	private class UiRequisitionReceiptItemForm extends Ui {
//		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
//		private TextBox nameTextBox = new TextBox();
//		private TextBox quantityTextBox = new TextBox();
//		private TextBox daysTextBox = new TextBox();
//		private TextBox serialTextBox = new TextBox();
//		private TextBox priceTextBox = new TextBox();
//		private BmoRequisitionReceiptItem bmoRequisitionReceiptItem;
//		private Button saveButton = new Button("Agregar");
//		private HorizontalPanel buttonPanel = new HorizontalPanel();
//		private UiSuggestBox requisitionItemSuggestBox = new UiSuggestBox(new BmoRequisitionItem());
//		BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
//
//		public UiRequisitionReceiptItemForm(UiParams uiParams, Panel defaultPanel, BmoRequisitionReceipt bmoRequisitionReceipt, BmoRequisitionReceiptItem bmoRequisitionReceiptItem) {
//			super(uiParams, defaultPanel);
//			this.bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
//
//			try {
//				bmoRequisitionReceiptItem.getRequisitionReceiptId().setValue(requisitionReceiptId);
//
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
//			}
//
//			saveButton.setStyleName("formSaveButton");
//			saveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					prepareSave();
//				}
//			});
//			saveButton.setEnabled(false);
//			if (getSFParams().hasWrite(bmoRequisitionReceipt.getProgramCode())) saveButton.setEnabled(true);
//			buttonPanel.add(saveButton);
//
//			// Manejo de acciones de suggest box
//			UiSuggestBoxAction uiSuggestBoxAction = new UiSuggestBoxAction() {
//				@Override
//				public void onSelect(UiSuggestBox uiSuggestBox) {
//					formSuggestionChange(uiSuggestBox);
//				}
//			};
//			formTable.setUiSuggestBoxAction(uiSuggestBoxAction);
//
//			// Filtro afecta inventario
//			BmFilter requisitionFilter = new BmFilter();
//			requisitionFilter.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisitionReceipt.getRequisitionId().toInteger());
//			requisitionItemSuggestBox = new UiSuggestBox(new BmoRequisitionItem(), requisitionFilter);
//
//			defaultPanel.add(formTable);
//		}
//
//		public void show(){
//			formTable.addField(1, 0, requisitionItemSuggestBox, bmoRequisitionReceiptItem.getRequisitionItemId());
//			formTable.addField(2, 0, nameTextBox, bmoRequisitionReceiptItem.getName());
//			formTable.addField(3, 0, serialTextBox, bmoRequisitionReceiptItem.getSerial());
//			formTable.addField(4, 0, quantityTextBox, bmoRequisitionReceiptItem.getQuantity());			
//			formTable.addField(5, 0, daysTextBox, bmoRequisitionReceiptItem.getDays());
//			formTable.addField(6, 0, priceTextBox, bmoRequisitionReceiptItem.getPrice());
//			formTable.addButtonPanel(buttonPanel);
//		}
//
//		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
//			if (uiSuggestBox == requisitionItemSuggestBox) {
//				if (requisitionItemSuggestBox.getSelectedId() > 0) {
//					bmoRequisitionItem = (BmoRequisitionItem)requisitionItemSuggestBox.getSelectedBmObject();
//					nameTextBox.setText(bmoRequisitionItem.getName().toString());
//					nameTextBox.setEnabled(false);
//					daysTextBox.setText(bmoRequisitionItem.getDays().toString());
//					daysTextBox.setEnabled(false);
//					priceTextBox.setText(bmoRequisitionItem.getPrice().toString());
//					priceTextBox.setEnabled(false);
//
//					// Hacer cambios por el producto
//					if (bmoRequisitionItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {
//						serialTextBox.setEnabled(true);
//						serialTextBox.setFocus(true);
//						quantityTextBox.setEnabled(false);
//						quantityTextBox.setText("1");
//					} else if (bmoRequisitionItem.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_BATCH) {
//						serialTextBox.setEnabled(true);
//						serialTextBox.setFocus(true);
//						quantityTextBox.setEnabled(true);
//						quantityTextBox.setText("");
//					} else {
//						serialTextBox.setText("");
//						serialTextBox.setEnabled(false);
//						quantityTextBox.setEnabled(true);
//						quantityTextBox.setFocus(true);
//					} 
//				}
//			}
//		}
//
//		public void prepareSave() {
//			try {
//				bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
//				bmoRequisitionReceiptItem.getName().setValue(nameTextBox.getText());
//				bmoRequisitionReceiptItem.getSerial().setValue(serialTextBox.getText());
//				bmoRequisitionReceiptItem.getQuantity().setValue(quantityTextBox.getText());
//				bmoRequisitionReceiptItem.getDays().setValue(daysTextBox.getText());
//				bmoRequisitionReceiptItem.getPrice().setValue(priceTextBox.getText());
//				bmoRequisitionReceiptItem.getRequisitionReceiptId().setValue(requisitionReceiptId);
//				bmoRequisitionReceiptItem.getRequisitionItemId().setValue(requisitionItemSuggestBox.getSelectedId());
//				bmoRequisitionReceiptItem.getQuantityBalance().setValue(0);
//				bmoRequisitionReceiptItem.getQuantityReturned().setValue(0);
//				save();
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
//			}
//		}
//
//		public void save() {
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					processUpdateResult(result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().save(bmoRequisitionReceiptItem.getPmClass(), bmoRequisitionReceiptItem, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
//			}
//		}
//
//		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) {
//				showSystemMessage("Error al crear Item: " + bmUpdateResult.errorsToString());
//			} else {
//				requisitionReceiptItemDialogBox.hide();
//				reset();
//			}
//		}
//	}
//
//	// Escanea codigo de barras y asigna cantidades a los items de la recepción
//	private class UiBarcodeForm extends Ui {
//		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());		
//		private Button saveButton = new Button("Guardar");
//		private Button closeButton = new Button("Cerrar");
//		private HorizontalPanel buttonPanel = new HorizontalPanel();
//
//		protected ValueChangeHandler<String> textChangeHandler;
//		private TextBox barcodeTextBox = new TextBox();
//		private TextBox serialTextBox = new TextBox();
//		private TextBox quantityTextBox = new TextBox();
//		private Label productLabel = new Label();
//		private Label requisitionQuantityLabel = new Label();
//		private Label requisitionBalanceLabel = new Label();
//
//		private BmoRequisitionReceipt bmoRequisitionReceipt;
//		private BmoRequisitionReceiptItem bmoRequisitionReceiptItem;
//
//		BmField barcodeTextBoxField;
//
//		public UiBarcodeForm(UiParams uiParams, Panel defaultPanel, BmoRequisitionReceipt bmoRequisitionReceipt) {
//			super(uiParams, defaultPanel);
//			this.bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
//			this.bmoRequisitionReceipt = bmoRequisitionReceipt;
//
//			barcodeTextBoxField = new BmField("barcodeTextBoxField", "", "Item Envío Ped.", 40, Types.VARCHAR, false, BmFieldType.STRING, false);
//
//			saveButton.setStyleName("formSaveButton");
//			saveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					prepareSave();
//				}
//			});
//			saveButton.setEnabled(false);
//			if (getSFParams().hasWrite(bmoRequisitionReceipt.getProgramCode())) saveButton.setEnabled(true);
//			buttonPanel.add(saveButton);
//
//			closeButton.setStyleName("formCloseButton");
//			closeButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					closeBarcodeDialog();
//				}
//			});
//			buttonPanel.add(closeButton);
//
//			// Dispara accion del codigo de barras
//			textChangeHandler = new ValueChangeHandler<String>() {
//				@Override
//				public void onValueChange(ValueChangeEvent<String> event) {
//					changeBarcode(event);
//				}
//			};
//			barcodeTextBox.addValueChangeHandler(textChangeHandler);
//
//			defaultPanel.add(formTable);
//		}
//
//		public void show(){
//			formTable.addField(1, 0, barcodeTextBox, barcodeTextBoxField);
//			formTable.addField(2, 0, serialTextBox, bmoRequisitionReceiptItem.getSerial());
//			formTable.addLabelField(3, 0, "Producto / Item:", productLabel);
//			formTable.addLabelField(4, 0, "Cantidad O.C.:", requisitionQuantityLabel);
//			formTable.addLabelField(5, 0, "Pendiente:", requisitionBalanceLabel);
//			formTable.addField(6, 0, quantityTextBox, bmoRequisitionReceiptItem.getQuantity());
//			formTable.addButtonPanel(buttonPanel);
//
//			serialTextBox.setEnabled(false);
//			setBarcodeFocus();
//		}
//
//		public void setBarcodeFocus(){
//			// Asigna foco al campo
//			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//				@Override
//				public void execute() {
//					barcodeTextBox.setFocus(true);                
//				}
//			});
//		}
//
//		public void changeBarcode(ValueChangeEvent<String> event) {
//			try {
//				bmoRequisitionReceiptItem.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
//				searchBarcode(barcodeTextBox.getText());
//			} catch (BmException e) {
//				showSystemMessage(this.getClass().getName() + "-changeBarcode(): " + e.toString());
//			}
//		}
//
//		//Obtener la cantidad en almacen
//		private void searchBarcode(String barcode){
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-searchBarcode() ERROR: " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					processBarcodeSearchResult((BmUpdateResult)result);
//				}
//			};
//
//			try {	
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().action(bmoRequisitionReceiptItem.getPmClass(), bmoRequisitionReceiptItem, BmoRequisitionReceiptItem.ACTION_SEARCHBARCODE, barcode, callback);
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-searchBarcode() ERROR: " + e.toString());
//			}
//		} 
//
//		public void processBarcodeSearchResult(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) {
//				bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
//				productLabel.setText("");
//				requisitionQuantityLabel.setText("");
//				requisitionBalanceLabel.setText("");
//				formTable.displayError(bmoRequisitionReceiptItem.getIdField(), "El Código de Barras no fue Encontrado.");
//				setBarcodeFocus();
//			} else {
//				bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)bmUpdateResult.getBmObject();
//				productLabel.setText(bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getName().toString());
//				requisitionQuantityLabel.setText(bmoRequisitionReceiptItem.getBmoRequisitionItem().getQuantity().toString());
//				requisitionBalanceLabel.setText(bmoRequisitionReceiptItem.getQuantityBalance().toString());
//				quantityTextBox.setText(bmoRequisitionReceiptItem.getQuantity().toString());
//
//				// Depende el tipo de rastreo
//				if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getTrack().equals(BmoProduct.TRACK_NONE)) {
//					serialTextBox.setText("");
//					serialTextBox.setEnabled(false);
//					quantityTextBox.setEnabled(true);
//					quantityTextBox.setFocus(true);
//				} else if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getTrack().equals(BmoProduct.TRACK_BATCH)) {
//					serialTextBox.setText(bmoRequisitionReceiptItem.getSerial().toString());
//					serialTextBox.setEnabled(true);
//					serialTextBox.setFocus(true);
//					quantityTextBox.setEnabled(true);
//				} else if (bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getTrack().equals(BmoProduct.TRACK_SERIAL)) {
//					if (!barcodeTextBox.getText().toLowerCase().toString().equals(bmoRequisitionReceiptItem.getBmoRequisitionItem().getBmoProduct().getCode().toString()))
//						serialTextBox.setText(barcodeTextBox.getText());
//					else
//						serialTextBox.setText("");
//
//					serialTextBox.setEnabled(true);
//					serialTextBox.setFocus(true);
//					quantityTextBox.setText("1");
//					quantityTextBox.setEnabled(false);
//				}
//			}
//		}
//
//		public void prepareSave() {
//			try {
//				bmoRequisitionReceiptItem.getSerial().setValue(serialTextBox.getText());
//				bmoRequisitionReceiptItem.getQuantity().setValue(quantityTextBox.getText());
//
//				save();
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-prepareSave(): ERROR " + e.toString());
//			}
//		}
//
//		public void save() {
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					processUpdateResult(result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().save(bmoRequisitionReceiptItem.getPmClass(), bmoRequisitionReceiptItem, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
//			}
//		}
//
//		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) 
//				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
//			else {
//				barcodeTextBox.setText("");
//				serialTextBox.setText("");
//				serialTextBox.setEnabled(false);
//				quantityTextBox.setText("");
//				barcodeTextBox.setFocus(true);
//
//				productLabel.setText("");
//				requisitionQuantityLabel.setText("");
//				requisitionBalanceLabel.setText("");
//
//				reset();
//			}
//		}
//
//		private void closeBarcodeDialog() {
//			barcodeDialogBox.setVisible(false);
//		}
//	}
//
//}
