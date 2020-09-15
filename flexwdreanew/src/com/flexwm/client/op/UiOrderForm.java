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
import java.util.HashMap;
import java.util.Iterator;
import com.flexwm.client.ac.UiOrderSessionTypePackageGrid;
import com.flexwm.client.ar.UiPropertyRentalDetail;
import com.flexwm.client.co.UiOrderPropertyGrid;
import com.flexwm.client.co.UiOrderPropertyModelExtraGrid;
import com.flexwm.client.co.UiOrderPropertyTaxGrid;
import com.flexwm.client.cr.UiOrderCreditGrid;
import com.flexwm.client.fi.UiBankMovement;
import com.flexwm.client.fi.UiPaccount;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoPayCondition;
//import com.flexwm.shared.cm.BmoProjectActivities;
import com.flexwm.shared.co.BmoOrderProperty;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoProductKitItem;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoWhStock;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.symgae.client.ui.Ui;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiForm;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiSuggestBoxAction;
import com.symgae.client.ui.UiTagBox;
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
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiOrderForm extends UiForm {
	BmoOrder bmoOrder = new BmoOrder();
	BmoWFlowType bmoWFlowType;
	TextBox codeTextBox = new TextBox();	
	UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
	TextBox nameTextBox = new TextBox();	
	TextBox amountTextBox = new TextBox();
	TextBox discountTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextArea commentsTextArea = new TextArea();	
	UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiListBox customerContactIdListBox = new UiListBox(getUiParams(), new BmoCustomerContact());

	UiSuggestBox userSuggestBox;
	UiTagBox tagBox = new UiTagBox(getUiParams());
	TextBox taxTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();
	UiDateTimeBox lockStartDateTimeBox = new UiDateTimeBox();
	UiDateTimeBox lockEndDateTimeBox = new UiDateTimeBox();	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
	UiSuggestBox renewOrderSuggestBox = new UiSuggestBox(new BmoOrder());
	CheckBox willRenewCheckBox = new CheckBox();
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	TextBox currencyParityTextBox = new TextBox();
	CheckBox coveregeParityCheckBox = new CheckBox();
	TextBox customerRequisitionTextBox = new TextBox();
	UiListBox defaultBudgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
	UiListBox defaultAreaUiListBox = new UiListBox(getUiParams(), new BmoArea());
	UiListBox payConditionUiListBox = new UiListBox(getUiParams(), new BmoPayCondition());

	protected FlowPanel formatPanel;

	// Botones
	private Button addOrderItemButton = new Button("+ITEM");
	private Button addKitButton = new Button("+KIT");
	private FlowPanel orderButtonPanel = new FlowPanel();
	private Button extrasButton = new Button("EXTRAS");
	private Button renewOrderButton = new Button("RENOVAR");
//	private Button createProyectButton = new Button("CREAR PROYECTO");
	private Button consultancyDeleteButton = new Button("ELIMINAR");

	// Order Groups
	BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
	private HashMap<String, UiOrderGroupGrid> orderGroupUiMap = new HashMap<String, UiOrderGroupGrid>();
	private FlowPanel orderGroupsPanel = new FlowPanel();
	private OrderUpdater orderUpdater = new OrderUpdater();
	private int selectedOrderGroupId;
	private Button addOrderGroupButton = new Button("+GRUPO");
	private BmFilter bmoFilterOrderGroup;
	protected DialogBox orderGroupDialogBox;
	private int orderIdRpcAttempt = 0;
	private int orderIdRpc = 0;
	private int orderGroupsRpcAttempt = 0;
	private int hasRenewOrderRpcAttempt = 0;
	private int parityFromCurrencyRpcAttempt = 0;
	private String currencyId;
	private int saveShowChangeRpcAttempt = 0;
	private int saveAmountChangeRpcAttempt = 0;
	private int stockQuantityRpcAttempt = 0;
	private int lockedQuantityRpcAttempt = 0;
	private int priceRpcAttempt = 0;
	private int productCompanySpecificRpcAttempt = 0;

	// OrderItems
	protected DialogBox orderItemDialogBox;

	// Browser de productos
	public final SingleSelectionModel<BmObject> productSelection = new SingleSelectionModel<BmObject>();
	private UiProductTree uiProductTree = new UiProductTree(productSelection);
	private CellBrowser productCellBrowser;
	private FlowPanel productCellPanel = new FlowPanel();

	// Ciclo de vida documento
	public final SingleSelectionModel<BmObject> lifeCycleSelection = new SingleSelectionModel<BmObject>();
	private UiOrderLifeCycleViewModel uiLifeCycleViewModel;
	private CellTree lifeCycleCellTree;
	private DialogBox lifeCycleDialogBox = new DialogBox();
	private Button lifeCycleShowButton = new Button("SEGUIMIENTO");
	private Button lifeCycleCloseButton = new Button("CERRAR");
	//private Button lifeCycleOrderButton = new Button("PEDIDO EXTRA");

	//	// Recursos
//		private UiOrderEquipmentGrid orderEquipmentGrid;
//		private FlowPanel orderEquipmentPanel = new FlowPanel();
	//
	//	// Personal
//		private UiOrderStaffGrid orderStaffGrid;
//		private FlowPanel orderStaffPanel = new FlowPanel();

	// Inmuebles
	private UiOrderPropertyGrid orderPropertyGrid;
	private FlowPanel orderPropertyPanel = new FlowPanel();

	// Arrendamiento
	private UiOrderPropertyTaxGrid orderPropertyTaxGrid;
	private FlowPanel orderPropertyTaxPanel = new FlowPanel();

	// Extras de inmuebles
	private UiOrderPropertyModelExtraGrid orderPropertyModelExtraGrid;
	private FlowPanel orderPropertyModelExtraPanel = new FlowPanel();

	// Paquetes sesiones
	private UiOrderSessionTypePackageGrid orderSessionTypePackageGrid;
	private FlowPanel orderSessionTypePackagePanel = new FlowPanel();

	// Paquetes sesiones
	private UiOrderCreditGrid orderCreditGrid;
	private FlowPanel orderCreditPanel = new FlowPanel();

	// Otros
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
	private CheckBox taxAppliesCheckBox = new CheckBox("");

	CheckBox showEquipmentQuantityCheckBox = new CheckBox();
	CheckBox showEquipmentPriceCheckBox = new CheckBox();
	CheckBox showStaffQuantityCheckBox = new CheckBox();
	CheckBox showStaffPriceCheckBox = new CheckBox();

	private String generalSection = "Datos Generales";
	private String productsSection = "Navegador Productos";
	private String itemsSection = "Items";
	//	private String equipmentsSection = "Recursos";
	//	private String staffSection = "Personal";
	private String propertySection = "Inmueble";
	private String propertyTaxSection = "Arrendamiento";
	private String creditSection = "Línea de Crédito";
	private String extrasSection = "Extras";
	private String currencySection = "Tipo de Cambio";
	private String totalSection = "Totales";
	private String statusSection = "Estatus";


	public UiOrderForm(UiParams uiParams, int id) {
		super(uiParams, new BmoOrder(), id);
		bmoOrder = (BmoOrder)getBmObject();
		initialize();
	}

	public void initialize() {
		// Cambiar etiqueta de acuerdo a configuracion de campo
		propertySection = getSFParams().getProgramTitle(new BmoOrderProperty().getProgramCode());

		// Filtrar por vendedores
		userSuggestBox = new UiSuggestBox(new BmoUser());
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterSalesmen = new BmFilter();
		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + salesGroupId);	
		userSuggestBox.addFilter(filterSalesmen);

		// Filtrar por vendedores activos
		BmFilter filterSalesmenActive = new BmFilter();
		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		userSuggestBox.addFilter(filterSalesmenActive);

		// Arbol de seleccion de productos
		productSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				BmObject bmObject = (BmObject)productSelection.getSelectedObject();

				// Se esta agregando un producto directo
				if (bmObject instanceof BmoProduct) {
					addProduct((BmoProduct)bmObject);
				}

				// Se esta agregando un kit, que incluye un grupo de productos
				if (bmObject instanceof BmoProductKit) {
					addKit((BmoProductKit)bmObject);
				}
			}
		});
		productCellBrowser = new CellBrowser.Builder<SingleSelectionModel<BmObject>>(uiProductTree, productSelection).build();
		productCellBrowser.setSize("100%", "200px");
		productCellBrowser.setAnimationEnabled(true);
		productCellPanel.setSize("100%", "200px");
		productCellPanel.add(productCellBrowser);

		addOrderGroupButton.setStyleName("formSaveButton");
		addOrderGroupButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {				
				addOrderGroupDialog();					
			}
		});

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

		// Pedido Extra
		//		lifeCycleOrderButton.setStyleName("formCloseButton");
		//		lifeCycleOrderButton.addClickHandler(new ClickHandler() {
		//			@Override
		//			public void onClick(ClickEvent event) {
		//				if (Window.confirm("Desea Crear un Pedido Extra?"))
		//					addExtraOrder();
		//			}
		//		});

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

		// Todas las operaciones con los items de productos 
		addOrderItemButton.setStyleName("formSaveButton");
		addOrderItemButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addProduct();
			}
		});

		addKitButton.setStyleName("formSaveButton");
		addKitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addKit();
			}
		});

		// Guardar datos al darle clic a mostrar cantidad/precio en la cotizacion
		showEquipmentQuantityCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setBooleanShowEquipmentAndStaff();
			}
		});

		showEquipmentPriceCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setBooleanShowEquipmentAndStaff();
			}
		});

		showStaffQuantityCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setBooleanShowEquipmentAndStaff();
			}
		});

		showStaffPriceCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setBooleanShowEquipmentAndStaff();
			}
		});

		taxAppliesCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				taxAppliesChange();
			}
		});

		taxAppliesCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				taxAppliesChange();
			}
		});

		ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				discountChange();
			}
		};
		discountTextBox.addValueChangeHandler(textChangeHandler);

		if (!newRecord) {
			ValueChangeHandler<Boolean> currencyParityTextBoxChangeHandler = new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					coverageParity();
				}
			};
			coveregeParityCheckBox.addValueChangeHandler(currencyParityTextBoxChangeHandler);
		}

		// Boton de mostrar descuentos
		extrasButton.setStyleName("formCloseButton");
		extrasButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleExtras();
			}
		});

		// Botón de Renovar
		renewOrderButton.setStyleName("formSaveButton");
		renewOrderButton.setVisible(true);
		renewOrderButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("¿Está seguro que desea Renovar el Pedido?"))
					prepareRenewOrder();
			}
		});
		
		// Botón de Eliminar venta de consultoria
		consultancyDeleteButton.setStyleName("formSaveButton");
		consultancyDeleteButton.setVisible(true);
		consultancyDeleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm("¿Está seguro que desea Eliminar la Venta?"))
					prepareDeleteConsultancy();
			}
		});

		// Panel
		orderButtonPanel.setWidth("100%");
		orderGroupsPanel.setWidth("100%");
//				orderEquipmentPanel.setWidth("100%");
//				orderStaffPanel.setWidth("100%");
		orderPropertyPanel.setWidth("100%");
		orderPropertyModelExtraPanel.setWidth("100%");		
		orderSessionTypePackagePanel.setWidth("100%");
	}

	@Override
	public void populateFields() {
		bmoOrder = (BmoOrder)getBmObject();
		bmoWFlowType = new BmoWFlowType();
		wFlowTypeListBox = new UiListBox(getUiParams(), bmoWFlowType);

		try {
			if (newRecord) {
				// Asigna empresa si es registro nuevo
				if (!(bmoOrder.getCompanyId().toInteger() > 0)) {	
					bmoOrder.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());
				}

				// Busca Empresa seleccionada por default
				if (newRecord && getUiParams().getSFParams().getSelectedCompanyId() > 0)
					bmoOrder.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

				// Agregar filtros al tipo de flujo
				bmoWFlowType = new BmoWFlowType();
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
				wFlowTypeListBox.addFilter(bmFilter); 
				
				// Filtro de estatus
				BmFilter bmFilterByStatus = new BmFilter();
				bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
				wFlowTypeListBox.addFilter(bmFilterByStatus); 
			}

			// Si esta desHabilitado el control presupuestal y si no esta asignado el tipo, buscar por el default
			//			if (!(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {	
			//				if (!(bmoOrder.getOrderTypeId().toString().length() > 0)) 
			//					bmoOrder.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
			//			}

			// Si no esta asignado el vendedor, buscar por el usuario loggeado
			if (!(bmoOrder.getUserId().toInteger() > 0)) {
				bmoOrder.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
			}

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoOrder.getCurrencyId().toInteger() > 0)) {
				bmoOrder.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				getParityFromCurrency(bmoOrder.getCurrencyId().toString());
			}

			if (newRecord) {
				bmoOrder.getLockStart().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
				bmoOrder.getLockEnd().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateTimeFormat()));
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
		}	

		formFlexTable.addSectionLabel(1, 0, generalSection, 2);
		formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoOrder.getCode());
		formFlexTable.addField(3, 0, orderTypeListBox, bmoOrder.getOrderTypeId());	
		
		formFlexTable.addField(4, 0, wFlowTypeListBox, bmoOrder.getWFlowTypeId());
		formFlexTable.addField(5, 0, nameTextBox, bmoOrder.getName());
		formFlexTable.addField(6, 0, customerSuggestBox, bmoOrder.getCustomerId());
		setCustomerContactsListBoxFilters(bmoOrder.getCustomerId().toInteger());
		formFlexTable.addField(7, 0, userSuggestBox, bmoOrder.getUserId());
		formFlexTable.addField(8, 0, lockStartDateTimeBox, bmoOrder.getLockStart());
		formFlexTable.addField(9, 0, lockEndDateTimeBox, bmoOrder.getLockEnd());
		formFlexTable.addField(10, 0, companyListBox, bmoOrder.getCompanyId());

		formFlexTable.addField(11, 0, marketListBox, bmoOrder.getMarketId());
		
		if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
			formFlexTable.addField(12, 0, payConditionUiListBox, bmoOrder.getPayConditionId());
		formFlexTable.addField(13, 0, customerRequisitionTextBox, bmoOrder.getCustomerRequisition());
		
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
			// Asigna filtros de control presupuestal
			setBudgetItemsListBoxFilters(bmoOrder.getCompanyId().toInteger());
			formFlexTable.addField(14, 0, defaultBudgetItemUiListBox, bmoOrder.getDefaultBudgetItemId());
			formFlexTable.addField(15, 0, defaultAreaUiListBox, bmoOrder.getDefaultAreaId());
		}

		if (!newRecord) {
			formFlexTable.addField(16, 0, customerContactIdListBox, bmoOrder.getCustomerContactId());
			formFlexTable.addField(17, 0, renewOrderSuggestBox, bmoOrder.getRenewOrderId());
			formFlexTable.addField(18, 0, willRenewCheckBox, bmoOrder.getWillRenew());
//			if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY))
				formFlexTable.addField(19, 0, tagBox, bmoOrder.getTags());

			bmoFilterOrderGroup = new BmFilter();
			bmoFilterOrderGroup.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId(), bmoOrder.getId());

			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) 
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {

				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
					formFlexTable.addSectionLabel(20, 0, productsSection, 2);
					formFlexTable.addPanel(21, 0, productCellPanel, 2);
				}

				formFlexTable.addSectionLabel(22, 0, itemsSection, 2);

				// Solo permitir agregar si no está autorizada
				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
					formFlexTable.addPanel(23, 0, orderButtonPanel);
				}

				orderButtonPanel.clear();				
				orderButtonPanel.add(addOrderGroupButton);
				orderButtonPanel.add(addOrderItemButton);
				orderButtonPanel.add(addKitButton);

				formFlexTable.addPanel(24, 0, orderGroupsPanel);
			} 

			//			// Recursos
//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//				formFlexTable.addSectionLabel(25, 0, equipmentsSection, 2);
//				// Panel de Checkboxes
//				FlowPanel checkBoxPanel = new FlowPanel();
//				checkBoxPanel.setWidth("100%");
//				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showEquipmentQuantityCheckBox, bmoOrder.getShowEquipmentQuantity()));
//				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showEquipmentPriceCheckBox, bmoOrder.getShowEquipmentPrice()));
//				formFlexTable.addLabelCell(26, 0, "Mostrar:");
//				formFlexTable.addPanel(26, 1, checkBoxPanel, 1);
//				formFlexTable.addPanel(27, 0, orderEquipmentPanel, 2);
//				orderEquipmentGrid = new UiOrderEquipmentGrid(getUiParams(), orderEquipmentPanel, bmoOrder, orderUpdater);
//				orderEquipmentGrid.show();
//			}
//
//			// Personal
//			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//				formFlexTable.addSectionLabel(28, 0, staffSection, 2);
//				// Panel de Checkboxes
//				FlowPanel checkBoxPanel = new FlowPanel();
//				checkBoxPanel.setWidth("100%");
//				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showStaffQuantityCheckBox, bmoOrder.getShowStaffQuantity()));
//				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showStaffPriceCheckBox, bmoOrder.getShowStaffPrice()));
//				formFlexTable.addLabelCell(29, 0, "Mostrar:");
//				formFlexTable.addPanel(29, 1, checkBoxPanel, 1);
//				formFlexTable.addPanel(30, 0, orderStaffPanel, 2);
//				orderStaffGrid = new UiOrderStaffGrid(getUiParams(), orderStaffPanel, bmoOrder, orderUpdater);
//				orderStaffGrid.show();
//			}

			// Inmuebles
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
				formFlexTable.addSectionLabel(30, 0, propertySection, 2);
				formFlexTable.addPanel(31, 0, orderPropertyPanel, 2);
				orderPropertyGrid = new UiOrderPropertyGrid(getUiParams(), orderPropertyPanel, bmoOrder, orderUpdater);
				orderPropertyGrid.show();

				formFlexTable.addSectionLabel(32, 0, extrasSection, 2);
				formFlexTable.addPanel(23, 0, orderPropertyModelExtraPanel, 2);
				orderPropertyModelExtraGrid = new UiOrderPropertyModelExtraGrid(getUiParams(), orderPropertyModelExtraPanel, bmoOrder, orderUpdater);
				orderPropertyModelExtraGrid.show();
			}

			// Arrendamiento
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				formFlexTable.addSectionLabel(34, 0, propertyTaxSection, 2);
				formFlexTable.addPanel(35, 0, orderPropertyTaxPanel, 2);
				orderPropertyTaxGrid = new UiOrderPropertyTaxGrid (getUiParams(), orderPropertyTaxPanel, bmoOrder, orderUpdater);
				orderPropertyTaxGrid.show();

				formFlexTable.addSectionLabel(36, 0, extrasSection, 2);
				formFlexTable.addPanel(37, 0, orderPropertyModelExtraPanel, 2);
				orderPropertyModelExtraGrid = new UiOrderPropertyModelExtraGrid(getUiParams(), orderPropertyModelExtraPanel, bmoOrder, orderUpdater);
				orderPropertyModelExtraGrid.show();
			}

			// Sesiones
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
				formFlexTable.addPanel(38, 0, orderSessionTypePackagePanel);

				// Solo permitir agregar si no está autorizada
				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
					formFlexTable.addPanel(39, 0, orderButtonPanel);
				}
			}

			// Creditos
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
				formFlexTable.addSectionLabel(40, 0, creditSection, 2);
				formFlexTable.addPanel(41, 0, orderCreditPanel);
				orderCreditGrid = new UiOrderCreditGrid(getUiParams(), orderCreditPanel, bmoOrder, orderUpdater);
				orderCreditGrid.show();

				orderButtonPanel.clear();
				orderButtonPanel.add(addOrderGroupButton);
				orderButtonPanel.add(addOrderItemButton);
			}

			formFlexTable.addSectionLabel(42, 0, currencySection, 2);
			formFlexTable.addField(43, 0, coveregeParityCheckBox, bmoOrder.getCoverageParity());
			formFlexTable.addField(44, 0, currencyListBox, bmoOrder.getCurrencyId());
			formFlexTable.addField(45, 0, currencyParityTextBox, bmoOrder.getCurrencyParity());

			formFlexTable.addSectionLabel(46, 0, totalSection, 2);
			formFlexTable.addFieldReadOnly(47, 0, amountTextBox, bmoOrder.getAmount());
			formFlexTable.addButtonCell(48, 0, extrasButton);
			formFlexTable.addField(49, 0, discountTextBox, bmoOrder.getDiscount());
			if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT))
				formFlexTable.addField(50, 0, taxAppliesCheckBox, bmoOrder.getTaxApplies());
			formFlexTable.addFieldReadOnly(52, 0, taxTextBox, bmoOrder.getTax());
			formFlexTable.addFieldReadOnly(52, 0, totalTextBox, bmoOrder.getTotal());
			formFlexTable.addField(53, 0, descriptionTextArea, bmoOrder.getDescription());
			formFlexTable.addField(54, 0, commentsTextArea, bmoOrder.getComments());

			formFlexTable.addSectionLabel(55, 0, statusSection, 2);
			formFlexTable.addLabelField(56, 0, bmoOrder.getLockStatus());
			formFlexTable.addLabelField(57, 0, bmoOrder.getPaymentStatus());
			formFlexTable.addLabelField(58, 0, bmoOrder.getDeliveryStatus());
			formFlexTable.addField(59, 0, statusListBox, bmoOrder.getStatus());

			// Si es de tipo renta, y está habilitado el bloqueo, mostrar asignacion
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) &&
					((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
				TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
				tabPanel.setSize("100%", "300px");
				formFlexTable.addPanel(60, 0, tabPanel);

				FlowPanel orderLockPanel = new FlowPanel();
				orderLockPanel.setSize("100%", "100%");
				ScrollPanel orderLockScrollPanel = new ScrollPanel();
				orderLockScrollPanel.setSize("100%", "255px");
				orderLockScrollPanel.add(orderLockPanel);
				UiOrderLock uiOrderLock = new UiOrderLock(getUiParams(), orderLockPanel, id);
				uiOrderLock.show();
				tabPanel.add(orderLockScrollPanel, "Bloqueo Items");
			}

			listOrderGroups();
			reset();
		} else {
			formFlexTable.addField(16, 0, currencyListBox, bmoOrder.getCurrencyId());
			populateParityFromCurrency(currencyListBox.getSelectedId());
			formFlexTable.addField(17, 0, currencyParityTextBox, bmoOrder.getCurrencyParity());
			formFlexTable.addField(18, 0, statusListBox, bmoOrder.getStatus());
//			if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY))
				formFlexTable.addField(19, 0, tagBox, bmoOrder.getTags());
		}

		// Oculta secciones
		if (!newRecord) {
			formFlexTable.hideSection(generalSection);
			formFlexTable.hideSection(productsSection);
			formFlexTable.hideSection(currencySection);
			//			formFlexTable.hideSection(equipmentsSection);
			//			formFlexTable.hideSection(staffSection);
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
				formFlexTable.hideSection(propertyTaxSection);
				formFlexTable.hideSection(extrasSection);
			} 
			//			else 
			//				formFlexTable.hideSection(statusSection);
		}

		statusEffect();
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox) {
			update("Desea cambiar el Estatus del Pedido?");
		}
		else if (event.getSource() == currencyListBox) {
			getParityFromCurrency(currencyListBox.getSelectedId());
		} else if (event.getSource() == orderTypeListBox) {
			BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
			if (bmoOrderType != null) {

				// Modificar la lista de Tipos de Flujo
				changeOrderType(bmoOrderType);

				// Traer dpto. y partida por defecto del pedido
				if (bmoOrderType.getDefaultBudgetItemId().toInteger() > 0)
					defaultBudgetItemUiListBox.setSelectedId("" + bmoOrderType.getDefaultBudgetItemId().toInteger());
				else
					defaultBudgetItemUiListBox.setSelectedIndex(0);
				if (bmoOrderType.getDefaultAreaId().toInteger() > 0)
					defaultAreaUiListBox.setSelectedId("" + bmoOrderType.getDefaultAreaId().toInteger());
				else
					defaultAreaUiListBox.setSelectedIndex(0);
			} else {
				defaultBudgetItemUiListBox.setSelectedIndex(0);
				defaultAreaUiListBox.setSelectedIndex(0);
				customerSuggestBox.clear();
			}
		} else if (event.getSource() == companyListBox) {
			BmoCompany bmoCompany = (BmoCompany)companyListBox.getSelectedBmObject();
			if (bmoCompany == null) {
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)
					populateBudgetItems(-1);
			} else {
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) 
					populateBudgetItems(bmoCompany.getId());
			}
		}
		statusEffect();
	}

	public void formDateChange(ValueChangeEvent<Date> event) {
		if (event.getSource() == lockEndDateTimeBox || event.getSource() == lockStartDateTimeBox) {
			try {
				if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
					bmoOrder.getLockStart().setValue(lockStartDateTimeBox.getDateTime());
					bmoOrder.getLockEnd().setValue(lockEndDateTimeBox.getDateTime());
					getMonths();
					//Validar que la f.inicio y f.f sea menor a la f.fin del pedido del que viene renovado siempre y cuando sea renovado
					if(bmoOrder.getRenewOrderId().toInteger() > 0) {
						getBmoOrderRenew();
					}
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-formDateChange() ERROR: " + e.toString());
			}
		}
	}

	//	@Override
	//	public void formDateChange(ValueChangeEvent<Date> event) {
	//		if (event.getSource() == lockStartDateTimeBox) {
	//			populateParityFromCurrency(currencyListBox.getSelectedId());
	//		}
	//	}

	// Accion cambio SuggestBox
	@Override
	public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
		// Filtros de requisiones
		if(uiSuggestBox == customerSuggestBox) {
			BmoCustomer bmoCustomer = new BmoCustomer();
			bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
			if (bmoCustomer.getCurrencyId().toInteger() > 0) {
				currencyListBox.setSelectedId(bmoCustomer.getCurrencyId().toString());
				populateParityFromCurrency(bmoCustomer.getCurrencyId().toString());
				payConditionUiListBox.setSelectedId("" + bmoCustomer.getPayConditionId().toInteger());
			}
			
			if (bmoCustomer.getMarketId().toInteger() > 0) {
				marketListBox.setSelectedId("" + bmoCustomer.getMarketId().toInteger());
			} else marketListBox.setSelectedId("0");
			
			populateCustomerContacts(customerSuggestBox.getSelectedId());

			statusEffect();
		}
	}

	@Override
	public void formBooleanChange(ValueChangeEvent<Boolean> event) {
		try {
			if (event.getSource() == showEquipmentQuantityCheckBox
					|| event.getSource() == showEquipmentPriceCheckBox
					|| event.getSource() == showStaffQuantityCheckBox
					|| event.getSource() == showStaffPriceCheckBox)  {
				bmoOrder.getShowEquipmentQuantity().setValue(showEquipmentQuantityCheckBox.getValue());
				bmoOrder.getShowEquipmentPrice().setValue(showEquipmentPriceCheckBox.getValue());
				bmoOrder.getShowStaffQuantity().setValue(showStaffQuantityCheckBox.getValue());
				bmoOrder.getShowStaffPrice().setValue(showStaffPriceCheckBox.getValue());
				saveShowChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-formBooleanChange() ERROR: " + e.toString());
		}
	}

	// Cambia el tipo de pedido y modifica combo de Tipos de Flujo
	private void changeOrderType(BmoOrderType bmoOrderType) {
		
		wFlowTypeListBox.clear();
		wFlowTypeListBox.clearFilters();

		// Agregar filtros al tipo de flujo
		// Filtro de tipos de flujo en categorias del modulo Oportunidad
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
		bmoWFlowType = new BmoWFlowType();
		
		// Si el registro ya existia, es posible que se haya deshabilitado flujo, asegura que se muestre
		if (newRecord) {
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
			filterList.add(bmFilter);
			wFlowTypeListBox.addFilter(bmFilter); 

			// Filtros de tipos de flujos activos
			BmFilter bmFilterByStatus = new BmFilter();
			bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
			filterList.add(bmFilterByStatus);
			wFlowTypeListBox.addFilter(bmFilterByStatus);

			// Filtro de flujos en categoria agregada al tipo de pedido
			BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			BmFilter bmFilterByWFlowCategories = new BmFilter();
			bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
					bmoWFlowCategory.getIdFieldName(), 
					bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
					bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
					"" + bmoOrderType.getId());
			wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
			wFlowTypeListBox.populate("", false);
		} else {
			wFlowTypeListBox.populate("" + bmoOrder.getWFlowTypeId().toInteger());
		}
	}

	@Override
	public void postShow() {		
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();
		
		//		if(bmoOrder.getStatus().getValue().charAt(0) == BmoOrder.STATUS_AUTHORIZED || 
		//				bmoOrder.getStatus().getValue().charAt(0) == BmoOrder.STATUS_FINISHED) {
		//			buttonPanel.add(lifeCycleOrderButton);
		//		}
		if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
			buttonPanel.add(consultancyDeleteButton);
		}
		buttonPanel.add(renewOrderButton);

		showRenewOrderButton();

		if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_LEASE)
			closeButton.setVisible(false);
		if (bmoOrder.getBmoOrderType().getType().toChar() == BmoOrderType.TYPE_CONSULTANCY) {
			deleteButton.setVisible(false);
			closeButton.setVisible(false);
			consultancyDeleteButton.setVisible(true);
		}
		if (!newRecord)
			buttonPanel.add(lifeCycleShowButton);

	}

	//	@Override
	//	public void formDateChange(ValueChangeEvent<Date> event) {
	//		if (event.getSource() == lockStartDateTimeBox) {
	//			populateParityFromCurrency(currencyListBox.getSelectedId());
	//		}
	//	}
	
	private void statusEffect() {
		lockStartDateTimeBox.setEnabled(false);
		lockEndDateTimeBox.setEnabled(false);	
		discountTextBox.setVisible(false);
		statusListBox.setEnabled(false);
		extrasButton.setVisible(false);
		wFlowTypeListBox.setEnabled(false);
		currencyListBox.setEnabled(false);
		currencyParityTextBox.setEnabled(false);
		coveregeParityCheckBox.setEnabled(false);
		customerRequisitionTextBox.setEnabled(false);
		renewOrderSuggestBox.setEnabled(false);
		marketListBox.setEnabled(false);
		taxTextBox.setEnabled(false);
		taxAppliesCheckBox.setEnabled(false);
		renewOrderButton.setVisible(false);
		payConditionUiListBox.setEnabled(false);
	
		if (!newRecord) {
			orderTypeListBox.setEnabled(false);			
			userSuggestBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			companyListBox.setEnabled(false);
	
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
				coveregeParityCheckBox.setEnabled(true);
				marketListBox.setEnabled(true);
				taxAppliesCheckBox.setEnabled(true);
				payConditionUiListBox.setEnabled(true);
			}
			renewOrderSuggestBox.setEnabled(false);
	
		} else {
			currencyListBox.setEnabled(true);
			userSuggestBox.setEnabled(true);
			wFlowTypeListBox.setEnabled(true);
			coveregeParityCheckBox.setEnabled(true);
			marketListBox.setEnabled(true);
			payConditionUiListBox.setEnabled(true);
		}
	
		// Obtener tipo de pedido
		BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
		if (bmoOrderType == null) bmoOrderType = bmoOrder.getBmoOrderType();
	
		if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)
				|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_CONSULTANCY)
				|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SALE)
				|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_LEASE)) {
			lockStartDateTimeBox.setEnabled(true);
			lockEndDateTimeBox.setEnabled(true);
		}
	
		// Si tiene descuento, mostrarlo
		if (bmoOrder.getDiscount().toDouble() > 0) {
			discountTextBox.setVisible(true);
			formFlexTable.showField(discountTextBox);
		} else {
			formFlexTable.hideField(discountTextBox);
		}
	
		// Si tiene permiso, deja cambiar vendedor del pedido
		if (!newRecord && getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGESALESMAN))
			userSuggestBox.setEnabled(true);
	
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED) ||
				bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED) ||
				bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) {
			nameTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			commentsTextArea.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			lockStartDateTimeBox.setEnabled(false);
			lockEndDateTimeBox.setEnabled(false);
			discountTextBox.setEnabled(false);
			extrasButton.setVisible(false);
			companyListBox.setEnabled(false);
			customerContactIdListBox.setEnabled(false);
			customerRequisitionTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
			taxTextBox.setEnabled(false);
			taxAppliesCheckBox.setEnabled(false);
	
			showEquipmentQuantityCheckBox.setEnabled(false);
			showEquipmentPriceCheckBox.setEnabled(false);
			showStaffQuantityCheckBox.setEnabled(false);
			showStaffPriceCheckBox.setEnabled(false);
			if(!newRecord && !(bmoOrder.getStatus().toString().equals(""+BmoOrder.STATUS_REVISION)))
				willRenewCheckBox.setEnabled(false);
		} else {
			// Esta en revision
			if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
				currencyParityTextBox.setEnabled(true);
			} else {
				currencyParityTextBox.setEnabled(false);
			}
			coverageParity();
	
			if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_LIMITEDDISCOUNT) ||
					getSFParams().hasSpecialAccess(BmoOrder.ACCESS_UNLIMITEDDISCOUNT)) {
				extrasButton.setVisible(true);
			}
	
			currencyListBox.setEnabled(true);
			if(bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)){
				renewOrderSuggestBox.setEnabled(false);
			}
			renewOrderSuggestBox.setEnabled(true);
			customerRequisitionTextBox.setEnabled(true);    
			showEquipmentQuantityCheckBox.setEnabled(true);
			showEquipmentPriceCheckBox.setEnabled(true);
			showStaffQuantityCheckBox.setEnabled(true);
			showStaffPriceCheckBox.setEnabled(true);
			payConditionUiListBox.setEnabled(true);
		}
	
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) {
			if (!newRecord && getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUSUNFINISHED)) 
				statusListBox.setEnabled(true);
		} else {
			// Si no tiene permiso para modificar status, deshabilitar combo
			if (!newRecord && getSFParams().hasSpecialAccess(BmoOrder.ACCESS_CHANGESTATUS))
				statusListBox.setEnabled(true);
		}
	
		// Si hay seleccion default de empresa, deshabilitar combo
		if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
			companyListBox.setEnabled(false);
	
		// Fecha de validacion para boton de renovacion
	
	
		// Validar boton de renovacion
		if ((bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)
				|| bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED))
				&& bmoOrder.getWillRenew().toInteger() == 1
				&& bmoOrder.getOriginRenewOrderId() == null
				) {
	
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrder.setId(id);
		bmoOrder.getCode().setValue(codeTextBox.getText());
		bmoOrder.getName().setValue(nameTextBox.getText());
		bmoOrder.getCustomerId().setValue(customerSuggestBox.getSelectedId());
		bmoOrder.getUserId().setValue(userSuggestBox.getSelectedId());
		bmoOrder.getCurrencyId().setValue(currencyListBox.getSelectedId());
		bmoOrder.getCurrencyParity().setValue(currencyParityTextBox.getText());
		bmoOrder.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
		if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) {
			if (discountTextBox.getText().equals("")) bmoOrder.getDiscount().setValue(0);
			else bmoOrder.getDiscount().setValue(discountTextBox.getText());
		}
		bmoOrder.getRenewOrderId().setValue(renewOrderSuggestBox.getSelectedId());
		bmoOrder.getWillRenew().setValue(willRenewCheckBox.getValue());
		bmoOrder.getTax().setValue(0);
		bmoOrder.getTotal().setValue(0);
		bmoOrder.getDescription().setValue(descriptionTextArea.getText());
		bmoOrder.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
		bmoOrder.getLockStart().setValue(lockStartDateTimeBox.getDateTime());
		bmoOrder.getLockEnd().setValue(lockEndDateTimeBox.getDateTime());
		bmoOrder.getStatus().setValue(statusListBox.getSelectedCode());
		//		bmoOrder.getOrderCommissionId().setValue(commissionListBox.getSelectedId());
		bmoOrder.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoOrder.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
//		if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY))
			bmoOrder.getTags().setValue(tagBox.getTagList());
		bmoOrder.getComments().setValue(commentsTextArea.getText());
		bmoOrder.getCustomerContactId().setValue(customerContactIdListBox.getSelectedId());
		bmoOrder.getCustomerRequisition().setValue(customerRequisitionTextBox.getText());
		bmoOrder.getCoverageParity().setValue(coveregeParityCheckBox.getValue());
		bmoOrder.getMarketId().setValue(marketListBox.getSelectedId());
		bmoOrder.getDefaultBudgetItemId().setValue(defaultBudgetItemUiListBox.getSelectedId());
		bmoOrder.getDefaultAreaId().setValue(defaultAreaUiListBox.getSelectedId());
		bmoOrder.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());
		return bmoOrder;
	}

	public void addExtraOrder() {

		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-creatExtraOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();

				if (!result.hasErrors()) {	
					UiOrderDetail uiOrderForm = new UiOrderDetail(getUiParams(),((BmoOrder)result.getBmObject()).getId());
					uiOrderForm.show();
				} 
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(),bmoOrder,
						BmoOrder.ACTION_EXTRAORDER,"",callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-createSessionSale() ERROR: " + e.toString());
		}
	}

	// Muestra la ventana de Seguimiento del Documento
	public void showLifeCycleDialog() {
		// Es de tipo forma de dialogo
		lifeCycleDialogBox = new DialogBox(true);
		lifeCycleDialogBox.setGlassEnabled(true);
		lifeCycleDialogBox.setText("Seguimiento del Documento");

		// Actualiza seguimiento a documento
		uiLifeCycleViewModel = new UiOrderLifeCycleViewModel(lifeCycleSelection, bmoOrder.getId());
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
	public void saveNext() {
		if (newRecord) {
			UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), getBmObject().getId());
			uiOrderDetail.show();
		} else {
			UiOrderForm uiOrderForm = new UiOrderForm(getUiParams(), getBmObject().getId());
			uiOrderForm.show();
		}
	}

	@Override
	public void close() {
		UiOrderList uiOrderList = new UiOrderList(getUiParams());
		uiOrderList.show();
	}

	//Obtener la paridad de la moneda
	public void populateParityFromCurrency(String currencyId) {
		getParityFromCurrency(currencyId);
	}



	//	@Override
	//	public void formDateChange(ValueChangeEvent<Date> event) {
	//		if (event.getSource() == lockStartDateTimeBox) {
	//			populateParityFromCurrency(currencyListBox.getSelectedId());
	//		}
	//	}

	// Actualiza combo de partidas presp. por empresa
	private void populateBudgetItems(int companyId) {
		defaultBudgetItemUiListBox .clear();
		defaultBudgetItemUiListBox.clearFilters();
		setBudgetItemsListBoxFilters(companyId);
		defaultBudgetItemUiListBox.populate(bmoOrder.getDefaultBudgetItemId());
	}

	// Asigna filtros al listado de partidas presp. por empresa
	private void setBudgetItemsListBoxFilters(int companyId) {
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

		if (companyId > 0) {
			BmFilter bmFilterByCompany = new BmFilter();
			bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
			defaultBudgetItemUiListBox.addBmFilter(bmFilterByCompany);

			// Filtro de ingresos(abono)
			BmFilter filterByDeposit = new BmFilter();
			filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
			defaultBudgetItemUiListBox.addFilter(filterByDeposit);

		} else {
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
			defaultBudgetItemUiListBox.addBmFilter(bmFilter);
		}
	}

	// Obtiene paridad de la moneda, primer intento
	public void getParityFromCurrency(String currencyId) {
		getParityFromCurrency(currencyId, 0);
	}

	public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
		if (parityFromCurrencyRpcAttempt < 5) {
			setCurrencyId(currencyId);
			setParityFromCurrencyRpcAttempt(parityFromCurrencyRpcAttempt + 1);

			BmoCurrency bmoCurrency = new BmoCurrency();
			String startDate = lockStartDateTimeBox.getTextBox().getText();

			if (lockStartDateTimeBox.getTextBox().getText().equals("")) {
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
						showErrorMessage("Error al obtener el Tipo de Cambio");
					else {
						currencyParityTextBox.setValue(result.getMsg());
						if (!newRecord) coverageParity();
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
	
	private void populateCustomerContacts(int customerId) {
		// Filtros de contactos del cliente
		customerContactIdListBox.clear();
		customerContactIdListBox.clearFilters();
		setCustomerContactsListBoxFilters(customerId);
		customerContactIdListBox.populate(bmoOrder.getCustomerContactId());
	}

	private void setCustomerContactsListBoxFilters(int customerId) {

		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		BmFilter filterCustomerContacts = new BmFilter();

		if (customerId > 0) {
			filterCustomerContacts.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), customerId);
			customerContactIdListBox.addFilter(filterCustomerContacts);
		} else {
			filterCustomerContacts.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), "-1");
			customerContactIdListBox.addFilter(filterCustomerContacts);	
		}
	}

	// Guardar datos al darle clic a mostrar cantidad/precio en la cotizacion
	// NOTA: formBooleanChange() no funciona, por eso se mete onClick
	public void setBooleanShowEquipmentAndStaff() {
		try {
			bmoOrder.getShowEquipmentQuantity().setValue(showEquipmentQuantityCheckBox.getValue());
			bmoOrder.getShowEquipmentPrice().setValue(showEquipmentPriceCheckBox.getValue());
			bmoOrder.getShowStaffQuantity().setValue(showStaffQuantityCheckBox.getValue());
			bmoOrder.getShowStaffPrice().setValue(showStaffPriceCheckBox.getValue());
			saveShowChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-setBooleanShowEquipmentStaff() ERROR: " + e.toString());
		}
	}

	// guardar datos, primer intento
	public void saveShowChange() {
		saveShowChange(0);
	}

	public void saveShowChange(int saveShowChangeRpcAttempt) {
		if (saveShowChangeRpcAttempt < 5) {
			setSaveShowChangeRpcAttempt(saveShowChangeRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveShowChangeRpcAttempt() < 5)
						saveShowChange(getSaveShowChangeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					setSaveShowChangeRpcAttempt(0);
					getUiParams().getBmObjectServiceAsync().saveSimple(bmoOrder.getPmClass(), bmoOrder, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + e.toString());
			}
		}
	}

	// Si no esta renovado el pedido y esta marcado el check de "renovar?", 
	// (ya no: y es un renovado) y esta autorizado o terminado MOSTRAR el boton.
	private void showRenewOrderButton() {
		if (!newRecord 
				&& bmoOrder.getWillRenew().toBoolean() 
				//&& bmoOrder.getRenewOrderId().toInteger() > 0
				&& (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_AUTHORIZED 
				|| bmoOrder.getStatus().toChar() == BmoOrder.STATUS_FINISHED)) {
			hasRenewOrder();
		}
	}
	// Si no esta renovado el pedido mostrar el boton
	private void hasRenewOrder() {
		hasRenewOrder(0);
	}

	// Si no esta renovado el pedido mostrar el boton
	private void hasRenewOrder(int hasRenewOrderRpcAttempt) {
		if (hasRenewOrderRpcAttempt < 5) {
			setHasRenewOrderRpcAttempt(hasRenewOrderRpcAttempt + 1);

			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getHasRenewOrderRpcAttempt() < 5) hasRenewOrder(getHasRenewOrderRpcAttempt());
					else showErrorMessage(this.getClass().getName() + "-hasRenewOrder() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setHasRenewOrderRpcAttempt(0);
					if (result.hasErrors()) showSystemMessage("Error al mostrar Botón de Renovación: " + result.errorsToString());
					else {
						if (result.getMsg().equals("0")) {
							renewOrderButton.setVisible(true);				
						}
						if (result.getMsg().equals("1")) {
							renewOrderButton.setVisible(false);				
						}
					}
				}
			};

			// Llamada al servicio RPC
			try {
				//if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_SHOWBUTTONRENEWORDER, ""+bmoOrder.getId(), callback);
				//} else { showSystemMessage("esta cargando "); }
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-hasRenewOrder() ERROR: " + e.toString());
			}
		}
	}

	private void prepareRenewOrder() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
			
					showErrorMessage(this.getClass().getName() + "-renewOrderAction() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
			
				if (result.hasErrors()) showSystemMessage("Error al renovar Pedido: " + result.errorsToString());
				else {
					// Verificar de que Programa vienes
					if (result.getBmObject() instanceof BmoOrder) {
						BmoOrder bmoOrderResult = (BmoOrder)result.getBmObject();
						if (bmoOrderResult.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
							showSystemMessage("El Contrato fue Renovado.");
							UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), Integer.parseInt(result.getMsg()));
							uiPropertyRentalDetail.show();
						}
					} else if (result.getBmObject() instanceof BmoConsultancy) {
						BmoConsultancy bmoConsultancy = (BmoConsultancy)result.getBmObject();
						 if (bmoConsultancy.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
							showSystemMessage("La Venta fue Renovada.");
							UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), bmoConsultancy.getId());
							uiConsultancyDetail.show();
						}
					}
				}
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_CREATERENEWORDER, "" + bmoOrder.getId(), callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + "-renewOrderAction() ERROR: " + e.toString());
		}
	}
	
	private void prepareDeleteConsultancy() {

		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + " - prepareDeleteConsultancy() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (result.hasErrors()) showSystemMessage("Error al eliminar la Venta: " + result.errorsToString());
				else {
					UiConsultancy uiConsultancy = new UiConsultancy(getUiParams());
					uiConsultancy.show();
				}
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_DELETECONSULTANCYFROMORDER, "" + bmoOrder.getId(), callback);
			}
		} catch (SFException e) {
			showErrorMessage(this.getClass().getName() + " - prepareDeleteConsultancy() ERROR: " + e.toString());
		}
	}

	private void toggleExtras() {
		if (discountTextBox.isVisible()) {
			discountTextBox.setVisible(false);
			formFlexTable.hideField(discountTextBox);
		} else  {
			discountTextBox.setVisible(true);
			formFlexTable.showField(discountTextBox);
		}
	}

	public void reset() {
		updateAmount(id);

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			orderGroupListStatusEffect();
		}

				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//					orderEquipmentGrid.show();
//					orderStaffGrid.show();
				}

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			orderPropertyGrid.show();

			orderPropertyModelExtraGrid.show();
		}

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_LEASE)) {
			orderPropertyTaxGrid.show();
			orderPropertyModelExtraGrid.show();
		}

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
			orderGroupListStatusEffect();
			orderSessionTypePackageGrid.show();
		}

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			orderGroupListStatusEffect();
			orderCreditGrid.show();
		}
	}

	public void updateOrderGroup(String orderGroupId) {
		this.selectedOrderGroupId = Integer.parseInt(orderGroupId);
		orderGroupUiMap.get("" + orderGroupId).show();
	}

	private void orderGroupListStatusEffect() {
		ArrayList<UiOrderGroupGrid> orderGroupGridList = new ArrayList<UiOrderGroupGrid>(orderGroupUiMap.values());

		Iterator<UiOrderGroupGrid> orderGroupIterator = orderGroupGridList.iterator();
		while (orderGroupIterator.hasNext()) {
			orderGroupIterator.next().statusEffect();
		}
	}

	public void setAmount(BmoOrder bmoOrder) {
		this.bmoOrder = bmoOrder;
		numberFormat = NumberFormat.getCurrencyFormat();
		amountTextBox.setText(numberFormat.format(bmoOrder.getAmount().toDouble()));
		taxTextBox.setText(numberFormat.format(bmoOrder.getTax().toDouble()));
		totalTextBox.setText(numberFormat.format(bmoOrder.getTotal().toDouble()));
	}

	public void discountChange() {
		try {
			double discount = Double.parseDouble(discountTextBox.getText());
			if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_UNLIMITEDDISCOUNT)) {
				// No revisa el limite del descuento
				bmoOrder.getDiscount().setValue(discount);
				saveAmountChange();
			} else	if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_LIMITEDDISCOUNT)) {
				// Revisa que el descuento no sea mayor al % del total establecido
				double discountLimit = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDiscountLimit().toDouble();
				double maxDiscount = bmoOrder.getAmount().toDouble() * (discountLimit / 100);

				if (discount > maxDiscount) {
					showSystemMessage("El Descuento no puede ser mayor a: " + numberFormat.format(maxDiscount));
					discount = 0;
				}

				bmoOrder.getDiscount().setValue(discount);
				discountTextBox.setValue("" + discount);
				saveAmountChange();
			}  
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-discountChange() ERROR: " + e.toString());
		}
	}

	public void coverageParity() {
		// Si el campo de cobertura está habilitado, y es verdadero cobertura, habilitar paridad
		if (getSFParams().isFieldEnabled(bmoOrder.getCoverageParity())) {

			if (coveregeParityCheckBox.getValue()) {
				// Validar de nuevo la moneda
				if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) 
					currencyParityTextBox.setEnabled(true);
				else 
					currencyParityTextBox.setEnabled(false);

			} else {
				currencyParityTextBox.setEnabled(false);
			}
		}
	}

	public void taxAppliesChange() {
		try {
			bmoOrder.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
			saveAmountChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
		}
	}

	// Actualizar montos, 0 intentos
	public void updateAmount(int orderIdRpc) {
		updateAmount(orderIdRpc, 0);
	}

	public void updateAmount(int orderIdRpc, int orderIdRpcAttempt) {
		if (orderIdRpcAttempt < 5) {
			setOrderIdRpc(orderIdRpc);
			setOrderIdRpcAttempt(orderIdRpcAttempt + 1);

			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getOrderIdRpcAttempt() < 5) {
						updateAmount(getOrderIdRpc(), getOrderIdRpcAttempt());
					} else {
						showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
					}
				}

				@Override
				public void onSuccess(BmObject result) {
					stopLoading();
					setOrderIdRpcAttempt(0);
					setAmount((BmoOrder)result);
				}
			};
			try {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoOrder.getPmClass(), id, callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + e.toString());
			}
		}
	}

	// guardar datos, primero intento
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
					processOrderUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOrder.getPmClass(), bmoOrder, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + e.toString());
			}
		}
	}

	public void processOrderUpdateResult(BmUpdateResult result) {
		if (result.hasErrors()) showFormMsg("Error al actualizar descuento.", "Error al actualizar descuento: " + result.errorsToString());
		else updateAmount(id);
	}

	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
		if (bmUpdateResult.hasErrors()) 
			showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
		this.reset();
	}

	public void processItemDelete(BmUpdateResult result) {
		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
		this.reset();
	}

	public void addOrderGroup(int orderGroupId) {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		orderGroupsPanel.add(vp);

		UiOrderGroupGrid orderGroupGrid = new UiOrderGroupGrid(getUiParams(), vp, bmoOrder, orderGroupId, this.orderUpdater);
		orderGroupGrid.show();

		orderGroupUiMap.put("" + orderGroupId, orderGroupGrid);
	}


	// Obtiene lista de grupos de pedidos, primer intento
	public void listOrderGroups() {
		listOrderGroups(0);
	}

	// Obtiene lista de grupos de pedidos
	public void listOrderGroups(int orderGroupsRpcAttempt) {
		if (orderGroupsRpcAttempt < 5) {
			setOrderGroupsRpcAttempt(orderGroupsRpcAttempt + 1);

			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getOrderGroupsRpcAttempt() < 5) {
						listOrderGroups(getOrderGroupsRpcAttempt());
					} else {
						showErrorMessage(this.getClass().getName() + "-listOrderGroups() ERROR: " + caught.toString());		
					}
				}
				@Override
				public void onSuccess(ArrayList<BmObject> result) {
					stopLoading();
					setOrderGroupsRpcAttempt(0);
					setOrderGroupList(result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().list(bmoOrderGroup.getPmClass(), bmoFilterOrderGroup, callback);
				} else {
					showSystemMessage("No se puede mostrar listado, esta cargando...");
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-listOrderGroups() ERROR: " + e.toString());
			}
		}
	}

	public void setOrderGroupList(ArrayList<BmObject> orderGroupList) {
		orderGroupsPanel.clear();
		orderGroupUiMap = new HashMap<String, UiOrderGroupGrid>();
		Iterator<BmObject> orderGroupIterator = orderGroupList.iterator();
		while (orderGroupIterator.hasNext()) {
			BmoOrderGroup bmoOrderGroup = (BmoOrderGroup)orderGroupIterator.next();
			addOrderGroup(bmoOrderGroup.getId());
		}
	}

	public void addProduct() {
		addProduct(new BmoProduct());
	}

	public void addOrderGroupDialog() {
		orderGroupDialogBox = new DialogBox(true);
		orderGroupDialogBox.setGlassEnabled(true);
		orderGroupDialogBox.setText("Grupo de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "150px");

		orderGroupDialogBox.setWidget(vp);

		UiOrderGroupForm orderGroupForm = new UiOrderGroupForm(getUiParams(), vp, bmoOrder.getId());
		orderGroupForm.show();

		orderGroupDialogBox.center();
		orderGroupDialogBox.show();
	}

	public void addProduct(BmoProduct bmoProduct) {
		orderItemDialogBox = new DialogBox(true);
		orderItemDialogBox.setGlassEnabled(true);
		orderItemDialogBox.setText("Item de Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		orderItemDialogBox.setWidget(vp);

		UiOrderItemForm orderItemForm = new UiOrderItemForm(getUiParams(), vp, bmoOrder, selectedOrderGroupId, bmoProduct);

		orderItemForm.show();

		orderItemDialogBox.center();
		orderItemDialogBox.show();
	}

	public void addKit() {
		addKit(new BmoProductKit());
	}

	public void addKit(BmoProductKit bmoProductKit) {
		orderItemDialogBox = new DialogBox(true);
		orderItemDialogBox.setGlassEnabled(true);
		orderItemDialogBox.setText("Item de Kit");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "150px");

		orderItemDialogBox.setWidget(vp);

		UiOrderKitForm orderKitForm = new UiOrderKitForm(getUiParams(), vp, bmoOrder.getId(), bmoProductKit);

		orderItemDialogBox.center();
		orderItemDialogBox.show();

		orderKitForm.show();
	}

	public void addKitItem(BmoProductKitItem bmoProductKitItem) {
		orderItemDialogBox = new DialogBox(true);
		orderItemDialogBox.setGlassEnabled(true);
		orderItemDialogBox.setText("Item de Kit");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		orderItemDialogBox.setWidget(vp);

		UiOrderKitItemForm orderKitItemForm = new UiOrderKitItemForm(getUiParams(), vp, bmoOrder.getId(), bmoProductKitItem, selectedOrderGroupId);

		orderItemDialogBox.center();
		orderItemDialogBox.show();

		orderKitItemForm.show();
	}


	private class UiOrderGroupForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox nameTextBox = new TextBox();
		private CheckBox showQuantityCheckBox = new CheckBox();
		private CheckBox showPriceCheckBox = new CheckBox();
		private CheckBox showAmountCheckBox = new CheckBox();
		private CheckBox showProductImageCheckBox = new CheckBox();
		private CheckBox showGroupImageCheckBox = new CheckBox();
		private CheckBox showCreateCxcCheckBox = new CheckBox();		
		private UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
		UiListBox payConditionUiListBox= new UiListBox(getUiParams(), new BmoPayCondition());

		private BmoOrderGroup bmoOrderGroup;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private int orderId;

		public UiOrderGroupForm(UiParams uiParams, Panel defaultPanel, int orderId) {
			super(uiParams, defaultPanel);
			bmoOrderGroup = new BmoOrderGroup();
			this.orderId = orderId;

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrderGroup.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {

			// Por defecto mostrar Cantidad, Precio, Subtotal y Img. Prod
			try {
				bmoOrderGroup.getShowQuantity().setValue(true);
				bmoOrderGroup.getShowProductImage().setValue(true);
				bmoOrderGroup.getShowAmount().setValue(true);
				bmoOrderGroup.getShowPrice().setValue(true);
				//bmoOrderGroup.getCreateRaccount().setValue(true);
				
				if (!(bmoOrderGroup.getId() > 0)) 
					bmoOrderGroup.getPayConditionId().setValue(bmoOrder.getPayConditionId().toInteger());
			} catch (BmException e) {
				showSystemMessage("No se pudo validar, valídelo manual: " + e.toString());
			}
			formTable.addField(1, 0, nameTextBox, bmoOrderGroup.getName());
			formTable.addField(2, 0, showQuantityCheckBox, bmoOrderGroup.getShowQuantity());
			formTable.addField(3, 0, showAmountCheckBox, bmoOrderGroup.getShowAmount());
			formTable.addField(4, 0, showPriceCheckBox, bmoOrderGroup.getShowPrice());
			formTable.addField(5, 0, showProductImageCheckBox, bmoOrderGroup.getShowProductImage());
			formTable.addField(6, 0, imageFileUpload, bmoOrderGroup.getImage());	
			formTable.addField(7, 0, showGroupImageCheckBox, bmoOrderGroup.getShowGroupImage());
			if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
				formTable.addField(8, 0, payConditionUiListBox, bmoOrderGroup.getPayConditionId());
			formTable.addField(9, 0, showCreateCxcCheckBox, bmoOrderGroup.getCreateRaccount());
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				orderGroupDialogBox.hide();
				listOrderGroups();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderGroup = new BmoOrderGroup();
				bmoOrderGroup.getName().setValue(nameTextBox.getText());
				bmoOrderGroup.getShowQuantity().setValue(showQuantityCheckBox.getValue());
				bmoOrderGroup.getShowPrice().setValue(showPriceCheckBox.getValue());
				bmoOrderGroup.getShowProductImage().setValue(showProductImageCheckBox.getValue());
				bmoOrderGroup.getShowGroupImage().setValue(showGroupImageCheckBox.getValue());
				bmoOrderGroup.getImage().setValue(imageFileUpload.getBlobKey());
				bmoOrderGroup.getShowAmount().setValue(showAmountCheckBox.getValue());
				bmoOrderGroup.getCreateRaccount().setValue(showCreateCxcCheckBox.getValue());
				bmoOrderGroup.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());

				bmoOrderGroup.getOrderId().setValue(orderId);
				bmoOrderGroup.getAmount().setValue(0);
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderGroup.getPmClass(), bmoOrderGroup, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
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
		private CheckBox discountAply = new CheckBox();
		private BmoOrderItem bmoOrderItem;
		private BmoOrder bmoOrder = new BmoOrder();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		private UiListBox orderGroupListBox;
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		private UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		String productId = "";


		public UiOrderItemForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, int selectedOrderGroupId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = new BmoOrderItem();
			this.bmoOrder = bmoOrder;

			try {
				if (selectedOrderGroupId > 0) bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
				bmoOrderItem.getProductId().setValue(bmoProduct.getId());
				bmoOrderItem.getName().setValue("item");
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

			listChangeHandler = new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					formListChangeItem(event);
				}
			};

			formTable.setListChangeHandler(listChangeHandler);
			
			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoOrder.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			BmFilter filterOrderGroups = new BmFilter();
			filterOrderGroups.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId(), bmoOrder.getId());
			orderGroupListBox = new UiListBox(getUiParams(), bmoOrderGroup, filterOrderGroups);

			//filtro para mostrar los equipos que Activos			
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoOrderItem.getBmoProduct().getKind(),bmoOrderItem.getBmoProduct().getEnabled(), "1");
			filterList.add(filterByEnabled);

			productSuggestBox = new UiSuggestBox(new BmoProduct());
			productSuggestBox.addFilter(filterByEnabled);
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {		
				BmFilter productTypeSubProductFilter = new BmFilter();
				productTypeSubProductFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS, ""+BmoProduct.TYPE_SUBPRODUCT);
				productSuggestBox.addFilter(productTypeSubProductFilter);
				BmFilter productTypeCompProductFilter = new BmFilter();
				productTypeCompProductFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS, ""+BmoProduct.TYPE_COMPLEMENTARY);
				productSuggestBox.addFilter(productTypeCompProductFilter);
				BmFilter consumableFilter = new BmFilter();
				consumableFilter.setValueFilter(bmoProduct.getKind(), bmoProduct.getConsumable(), 0);
				productSuggestBox.addFilter(consumableFilter);
			}

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				try {
					if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0)
						bmoOrderItem.getBudgetItemId().setValue(bmoOrder.getDefaultBudgetItemId().toInteger());
					if (bmoOrder.getDefaultAreaId().toInteger() > 0)
						bmoOrderItem.getAreaId().setValue(bmoOrder.getDefaultAreaId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
				}
			}

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			formTable.addField(1, 0, productSuggestBox, bmoOrderItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacén", stockQuantity);
			formTable.addLabelField(3, 0, "En Pedidos", lockedQuantity);
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
				formTable.addField(4, 0, discountAply,bmoOrderItem.getDiscountApplies());
			formTable.addField(5, 0, nameTextBox, bmoOrderItem.getName());
			formTable.addField(6, 0, descriptionTextArea, bmoOrderItem.getDescription());
			formTable.addField(7, 0, quantityTextBox, bmoOrderItem.getQuantity());

			// Mostrar los días si es de tipo renta
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
				formTable.addField(8, 0, daysTextBox, bmoOrderItem.getDays());

			if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission()))
				formTable.addField(9, 0, commissionCheckBox, bmoOrderItem.getCommission());

			formTable.addField(10, 0, priceTextBox, bmoOrderItem.getPrice());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				setBudgetItemsListBoxFilters(bmoOrder.getCompanyId().toInteger());
				formTable.addField(11, 0, budgetItemUiListBox, bmoOrderItem.getBudgetItemId());
				formTable.addField(12, 0, areaUiListBox, bmoOrderItem.getAreaId());
			}
			formTable.addField(1, 0, orderGroupListBox, bmoOrderItem.getOrderGroupId());
			formTable.addButtonPanel(buttonPanel);

			discountAply.setEnabled(false);
			statusEffect();
		}

		// Actualiza combo de departamentos
		//		private void populateArea() {
		//			areaUiListBox.clear();
		//			areaUiListBox.clearFilters();
		//			areaUiListBox.populate(bmoOrderItem.getAreaId());
		//		}
		//		
		//		// Actualiza combo de partidas presp. por empresa
		//		private void populateBudgetItems(int companyId) {
		//			budgetItemUiListBox.clear();
		//			budgetItemUiListBox.clearFilters();
		//			setBudgetItemsListBoxFilters(companyId);
		//			budgetItemUiListBox.populate(bmoOrderItem.getBudgetItemId());
		//		}

		// Asigna filtros al listado de partidas presp. por empresa
		private void setBudgetItemsListBoxFilters(int companyId) {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
				budgetItemUiListBox.addBmFilter(bmFilterByCompany);

				// Filtro de ingresos(abono)
				BmFilter filterByDeposit = new BmFilter();
				filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
				budgetItemUiListBox.addFilter(filterByDeposit);

			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
				budgetItemUiListBox.addBmFilter(bmFilter);
			}
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == productSuggestBox) {
				if (productSuggestBox.getSelectedId() > 0) {
					if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission())) {
						//Si el producto maneja comisión marcar el check
						BmoProduct bmoProduct = (BmoProduct)productSuggestBox.getSelectedBmObject();
						if (bmoProduct.getCommision().toBoolean())
							commissionCheckBox.setValue(true);
						else 
							commissionCheckBox.setValue(false);
					}
				}
				statusEffect();
			}
		}
		
		public void formListChangeItem(ChangeEvent event) {			
			if (event.getSource() == orderGroupListBox) {
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					bmoOrderGroup = (BmoOrderGroup)orderGroupListBox.getSelectedBmObject();
					if (bmoOrderGroup.getDiscountApplies().toBoolean()) {
						discountAply.setEnabled(true);
						discountAply.setValue(true);
					} else {
						discountAply.setEnabled(false);
						discountAply.setValue(false);
					}
				} 
			}
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
				nameTextBox.setText("item");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				stockQuantity.setText("0");
				lockedQuantity.setText("0");
				if (getSFParams().isFieldEnabled(bmoOrderItem.getCommission())) 
					commissionCheckBox.setValue(false);
				BmoProductCompany bmoProductCompany = new BmoProductCompany();
				setBudgetItemAndArea(bmoProductCompany, true);
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

		//Obtener la cantidad en almacen, primer intento
		public void getStockQuantity() {
			getStockQuantity(0);
		}

		//Obtener la cantidad en almacen
		public void getStockQuantity(int stockQuantityRpcAttempt) {
			if (stockQuantityRpcAttempt < 5) {
				setStockQuantityRpcAttempt(stockQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getStockQuantityRpcAttempt() < 5)
							getStockQuantity(getStockQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setStockQuantityRpcAttempt(0);
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
		} 

		//Obtener la cantidad en cotizacion, primer intento
		public void getLockedQuantity() {
			getLockedQuantity(0);
		}

		//Obtener la cantidad en pedidos
		public void getLockedQuantity(int lockedQuantityRpcAttempt) {
			if (lockedQuantityRpcAttempt < 5) {
				setLockedQuantityRpcAttempt(lockedQuantityRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getLockedQuantityRpcAttempt() < 5)
							getLockedQuantity(getLockedQuantityRpcAttempt());
						else 
							showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setLockedQuantityRpcAttempt(0);
						BmoOrderItem bmoOrderItem = (BmoOrderItem)result.getBmObject();
						lockedQuantity.setText(bmoOrderItem.getQuantity().toString());
						getPrice();
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
		} 

		//Obtener precio de venta/renta del producto, primer intento
		public void getPrice() {
			getPrice(0);
		}

		//Obtener precio de venta/renta del producto
		public void getPrice(int priceRpcAttempt) {
			if (priceRpcAttempt < 5) {
				setPriceRpcAttempt(priceRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getPriceRpcAttempt() < 5)
							getPrice(getPriceRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getPrice() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setPriceRpcAttempt(0);
						setProductPrice((BmoProductPrice)result.getBmObject());
						getProductCompanySpecific();
					}
				};

				try {	
					if (!isLoading()) {
						priceTextBox.setText("");
						BmoProductPrice bmoProductPrice = new BmoProductPrice();
						bmoProductPrice.getStartDate().setValue(bmoOrder.getLockStart().toString());
						bmoProductPrice.getProductId().setValue(productSuggestBox.getSelectedId());
						bmoProductPrice.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
						bmoProductPrice.getOrderTypeId().setValue(bmoOrder.getOrderTypeId().toInteger());
						bmoProductPrice.getWFlowTypeId().setValue(bmoOrder.getWFlowTypeId().toInteger());
						bmoProductPrice.getMarketId().setValue(bmoOrder.getMarketId().toInteger());
						bmoProductPrice.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());

						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoProductPrice.getPmClass(), bmoProductPrice, BmoProductPrice.ACTION_GETPRICE,  productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getPrice() ERROR: " + e.toString());
				}
			}
		} 

		// Asigna precio vigente
		private void setProductPrice(BmoProductPrice bmoProductPrice) {
			nameTextBox.setText("" + ((BmoProduct)productSuggestBox.getSelectedBmObject()).getName().toString());
			descriptionTextArea.setText("" + ((BmoProduct)productSuggestBox.getSelectedBmObject()).getDescription().toString());
			priceTextBox.setText("" + bmoProductPrice.getPrice().toDouble());
		}

		// Obtener empresa del producto, primer intento
		public void getProductCompanySpecific() {
			getProductCompanySpecific(0);
		}

		// Obtener empresa del producto
		public void getProductCompanySpecific(int productCompanySpecificRpcAttempt) {
			if (productCompanySpecificRpcAttempt < 5) {
				setProductCompanySpecificRpcAttempt(productCompanySpecificRpcAttempt + 1);

				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getProductCompanySpecificRpcAttempt() < 5)
							getProductCompanySpecific(getProductCompanySpecificRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getProductCompanySpecific() ERROR: " + caught.toString());
					}

					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setProductCompanySpecificRpcAttempt(0);
						setBudgetItemAndArea((BmoProductCompany)result.getBmObject(), false);
					}
				};

				try {	
					if (!isLoading()) {
						BmoProductCompany bmoProductCompany = new BmoProductCompany();
						bmoProductCompany.getCompanyId().setValue(bmoOrder.getCompanyId().toString());

						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoProductCompany.getPmClass(), bmoProductCompany, BmoProductCompany.ACTION_GETPRODUCTCOMPANY, productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getProductCompanySpecific() ERROR: " + e.toString());
				}
			}
		} 

		// Asigna partida presp./departamento
		private void setBudgetItemAndArea(BmoProductCompany bmoProductCompany, boolean defaultOrder) {

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				// Colocar partida y dpto de la cotizacion
				if (defaultOrder) {
					if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0)
						budgetItemUiListBox.setSelectedId("" + bmoOrder.getDefaultBudgetItemId().toInteger());
					if (bmoOrder.getDefaultAreaId().toInteger() > 0)
						areaUiListBox.setSelectedId("" + bmoOrder.getDefaultAreaId().toInteger());
				} else {
					// Colocar partida/dpto. de la empresa del producto
					if (bmoProductCompany.getId() > 0) {
						if (bmoProductCompany.getBudgetItemId().toInteger() > 0)
							budgetItemUiListBox.setSelectedId("" + bmoProductCompany.getBudgetItemId().toInteger());
						if (bmoProductCompany.getAreaId().toInteger() > 0)
							areaUiListBox.setSelectedId("" + bmoProductCompany.getAreaId().toInteger());
					} 
					// Colocar partida/dpto. del producto(no tiene empresas), si no tiene datos regresar por defecto a las del pedido
					else {
						BmoProduct bmoProduct = new BmoProduct();
						bmoProduct = ((BmoProduct)productSuggestBox.getSelectedBmObject());
						if (bmoProduct.getBudgetItemId().toInteger() > 0) 
							budgetItemUiListBox.setSelectedId("" + bmoProduct.getBudgetItemId().toInteger());
						else {
							if (bmoOrder.getDefaultBudgetItemId().toInteger() > 0) 
								budgetItemUiListBox.setSelectedId("" + bmoOrder.getDefaultBudgetItemId().toInteger());
						}
						if (bmoProduct.getAreaId().toInteger() > 0) 
							areaUiListBox.setSelectedId("" + bmoProduct.getAreaId().toInteger());
						else {
							if (bmoOrder.getDefaultAreaId().toInteger() > 0) 
								areaUiListBox.setSelectedId("" + bmoOrder.getDefaultAreaId().toInteger());
						}
					}
				}
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				updateOrderGroup(orderGroupListBox.getSelectedId());
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderItem = new BmoOrderItem();
				bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
				bmoOrderItem.getProductId().setValue(productSuggestBox.getSelectedId());
				bmoOrderItem.getName().setValue(nameTextBox.getText());
				bmoOrderItem.getDescription().setValue(descriptionTextArea.getText());
				bmoOrderItem.getQuantity().setValue(quantityTextBox.getText());
				bmoOrderItem.getDays().setValue(daysTextBox.getText());
				bmoOrderItem.getPrice().setValue(priceTextBox.getText());
				bmoOrderItem.getCommission().setValue(commissionCheckBox.getValue());
				bmoOrderItem.getOrderGroupId().setValue(orderGroupListBox.getSelectedId());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
					bmoOrderItem.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
					bmoOrderItem.getAreaId().setValue(areaUiListBox.getSelectedId());
				}
				if (!orderGroupListBox.getSelectedId().equals(""))
					selectedOrderGroupId = Integer.parseInt(orderGroupListBox.getSelectedId());

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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

	// Agrega un item de un kit a un grupo de la cotizacion
	private class UiOrderKitItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private UiListBox orderGroupListBox;
		private BmoOrderItem bmoOrderItem;
		private BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoProductKitItem bmoProductKitItem;
		//		private int selectedOrderGroupId;

		public UiOrderKitItemForm(UiParams uiParams, Panel defaultPanel, int orderId, BmoProductKitItem bmoProductKitItem, int selectedOrderGroupId) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = new BmoOrderItem();
			this.bmoProductKitItem = bmoProductKitItem;

			try {
				bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
			}

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoOrderGroup.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			BmFilter filterOrderGroups = new BmFilter();
			filterOrderGroups.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId(), orderId);
			orderGroupListBox = new UiListBox(getUiParams(), bmoOrderGroup, filterOrderGroups);

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			formTable.addLabelField(1, 0, bmoProductKitItem.getBmoProduct().getName());
			formTable.addLabelField(3, 0, bmoProductKitItem.getQuantity());
			formTable.addLabelField(4, 0, bmoProductKitItem.getDays());
			formTable.addField(5, 0, orderGroupListBox, bmoOrderItem.getOrderGroupId());
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				updateOrderGroup(orderGroupListBox.getSelectedId());
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoOrderItem = new BmoOrderItem();
				bmoOrderItem.getOrderGroupId().setValue(orderGroupListBox.getSelectedId());
				bmoOrderItem.getProductId().setValue(bmoProductKitItem.getBmoProduct().getId());
				bmoOrderItem.getQuantity().setValue(bmoProductKitItem.getQuantity().toDouble());
				bmoOrderItem.getDays().setValue(bmoProductKitItem.getDays().toInteger());
				save();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
			}
		}

		public void save() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
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
					getUiParams().getBmObjectServiceAsync().save(bmoOrderItem.getPmClass(), bmoOrderItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}

	// Agrega un kit completo a un grupo de la cotizacion
	private class UiOrderKitForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private BmoOrderItem bmoOrderItem;
		private BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiListBox productKitListBox = new UiListBox(getUiParams(), new BmoProductKit());
		private BmoProductKit bmoProductKit;
		private TextArea descriptionTextArea = new TextArea();
		private TextBox amountTextBox  = new TextBox();
		private int orderId;

		public UiOrderKitForm(UiParams uiParams, Panel defaultPanel, int orderId, BmoProductKit bmoProductKit) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = new BmoOrderItem();
			this.bmoProductKit = bmoProductKit;
			this.orderId = orderId;

			try {
				bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
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
				@Override
				public void onClick(ClickEvent event) {
					prepareAddKitAction();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoOrderGroup.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			formTable.addField(1, 0, productKitListBox, bmoProductKit.getIdField());
			formTable.addField(2, 0, descriptionTextArea, bmoProductKit.getDescription());
			formTable.addField(3, 0, amountTextBox, bmoProductKit.getAmount());
			descriptionTextArea.setEnabled(false);
			amountTextBox.setEnabled(false);

			formTable.addButtonPanel(buttonPanel);
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == productKitListBox) {
				bmoProductKit = (BmoProductKit)productKitListBox.getSelectedBmObject();
				if (bmoProductKit != null) {
					descriptionTextArea.setText(bmoProductKit.getDescription().toString());
					amountTextBox.setText(bmoProductKit.getAmount().toString());
				} else {
					descriptionTextArea.setText("");
					amountTextBox.setText("");
				}
			} 

			statusEffect();
		}

		public void prepareAddKitAction() {
			try {
				bmoOrderGroup = new BmoOrderGroup();
				bmoOrderGroup.getName().setValue(bmoProductKit.getName().toString());
				bmoOrderGroup.getDescription().setValue(bmoProductKit.getDescription().toString());
				bmoOrderGroup.getAmount().setValue(bmoProductKit.getAmount().toDouble());
				bmoOrderGroup.getIsKit().setValue(true);
				bmoOrderGroup.getShowQuantity().setValue(true);
				bmoOrderGroup.getShowPrice().setValue(false);
				bmoOrderGroup.getImage().setValue(bmoProductKit.getImage().toString());
				bmoOrderGroup.getShowProductImage().setValue(false);
				bmoOrderGroup.getShowGroupImage().setValue(true);
				bmoOrderGroup.getShowAmount().setValue(true);
				bmoOrderGroup.getOrderId().setValue(orderId);
				addKitAction();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareAddKitAction() ERROR: " + e.toString());
			}
		}

		public void addKitAction() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processAddKitActionUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrderGroup.getPmClass(), bmoOrderGroup, BmoOrderGroup.ACTION_PRODUCTKIT, "" + bmoProductKit.getId(), callback);	
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + e.toString());
			}
		}

		public void processAddKitActionUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processAddKitActionUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				orderItemDialogBox.hide();
				listOrderGroups();
				reset();
			}
		}
	}

	//Pagar el pedido
	public void payOrder() {				
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-payOrder() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				if (!result.hasErrors()) {						
					showSystemMessage("Pago Realizado con Exito");
					show();
				} else {
					showErrorMessage("Error al realizar el pago del pedido " + result.errorsToString());
				}
			}
		};

		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_PAYORDER, "" + bmoOrder.getId(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-payOrder() ERROR: " + e.toString());
		}
	}

	//Obtener diferencia de meses de f.inicio y f.fin
	public void getMonths() {		
		
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getMonths() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {				
				stopLoading();
				if (!result.hasErrors()) {						
					showSystemMessage("Las fechas se actualizaron correctamente!");
					show();
				} else {
					showErrorMessage("Error  " + result.errorsToString());
				}
			}
		};

		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_GETMONTHS, ""+ bmoOrder.getId(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getMonths() ERROR: " + e.toString());
		}
	}

	public void getBmoOrderRenew() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
				else showErrorMessage(this.getClass().getName() + "-getBmoOrderRenew() ERROR: " + caught.toString());
			}

			public void onSuccess(BmObject result) {
				stopLoading();
				BmoOrder bmoOrderRenew = ((BmoOrder)result);

				Date lockStart = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(lockStartDateTimeBox.getTextBox().getText());
				Date lockEnd = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(lockEndDateTimeBox.getTextBox().getText());

				Date lockEndRenew = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateTimeFormat())
						.parse(bmoOrderRenew.getLockEnd().toString());
				if(lockStart.before(lockEndRenew) || lockEnd.before(lockEndRenew)) {
					showErrorMessage("Error de captura: <b> La fecha inicio o fecha fin no puede ser menor a la fecha fin del pedido anterior</b>");					
				}
			}
		};

		// Llamada al servicio RPC
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().getBy(bmoOrder.getPmClass(), bmoOrder.getRenewOrderId().toInteger(), bmoOrder.getIdFieldName(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-getBmoOrderRenew() ERROR: " + e.toString());
		}
	}
	@Override
	public void updateNext() {
//		if(bmoOrder.getBmoOrderType().getCreateProject().toBoolean() && getUiParams().getSFParams().hasRead(new BmoProjectActivities().getProgramCode())) {
//			UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(),id);
//			uiOrderDetail.show();
//		}else {
			get(id);
//		}
	}
	//crear proyecto
		public void createProject() {

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-createProject() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();

					if (!result.hasErrors()) {	
						UiOrderDetail uiOrderForm = new UiOrderDetail(getUiParams(),((BmoOrder)result.getBmObject()).getId());
						uiOrderForm.show();
					}else {
						showSystemMessage(result.errorsToString());
					}
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(),bmoOrder,
							BmoOrder.ACTION_CREATEPROJ,"",callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-createProject() ERROR: " + e.toString());
			}
		}

	// Variables para llamadas RPC
	public int getOrderIdRpc() { // le puse rpc al final para que no haya confusion
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

	public int getOrderGroupsRpcAttempt() {
		return orderGroupsRpcAttempt;
	}

	public void setOrderGroupsRpcAttempt(int orderGroupsRpcAttempt) {
		this.orderGroupsRpcAttempt = orderGroupsRpcAttempt;
	}

	public int getHasRenewOrderRpcAttempt() {
		return hasRenewOrderRpcAttempt;
	}

	public void setHasRenewOrderRpcAttempt(int hasRenewOrderRpcAttempt) {
		this.hasRenewOrderRpcAttempt = hasRenewOrderRpcAttempt;
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

	public int getSaveShowChangeRpcAttempt() {
		return saveShowChangeRpcAttempt;
	}

	public void setSaveShowChangeRpcAttempt(int saveShowChangeRpcAttempt) {
		this.saveShowChangeRpcAttempt = saveShowChangeRpcAttempt;
	}

	public int getSaveAmountChangeRpcAttempt() {
		return saveAmountChangeRpcAttempt;
	}

	public void setSaveAmountChangeRpcAttempt(int saveAmountChangeRpcAttempt) {
		this.saveAmountChangeRpcAttempt = saveAmountChangeRpcAttempt;
	}

	public int getStockQuantityRpcAttempt() {
		return stockQuantityRpcAttempt;
	}

	public void setStockQuantityRpcAttempt(int stockQuantityRpcAttempt) {
		this.stockQuantityRpcAttempt = stockQuantityRpcAttempt;
	}

	public int getLockedQuantityRpcAttempt() {
		return lockedQuantityRpcAttempt;
	}

	public void setLockedQuantityRpcAttempt(int lockedQuantityRpcAttempt) {
		this.lockedQuantityRpcAttempt = lockedQuantityRpcAttempt;
	}

	public int getPriceRpcAttempt() {
		return priceRpcAttempt;
	}

	public void setPriceRpcAttempt(int priceRpcAttempt) {
		this.priceRpcAttempt = priceRpcAttempt;
	}

	public int getProductCompanySpecificRpcAttempt() {
		return productCompanySpecificRpcAttempt;
	}

	public void setProductCompanySpecificRpcAttempt(int productCompanySpecificRpcAttempt) {
		this.productCompanySpecificRpcAttempt = productCompanySpecificRpcAttempt;
	}

	public class OrderUpdater {

		public void changeOrder() {
			stopLoading();
			reset();
		}

		public void updateOrderGroups() {
			stopLoading();
			listOrderGroups();
		}

		public void changeOrderEquipment() {
			stopLoading();
			reset();
		}

		public void changeOrderStaff() {
			stopLoading();
			reset();
		}

		public void changeOrderProperty() {
			stopLoading();
			reset();
		}

		public void changeOrderPropertyTax() {
			stopLoading();
			reset();
		}

		public void changeOrderPropertyModelExtra() {
			stopLoading();
			reset();
		}

		public void changeOrderSessionTypePackage() {
			stopLoading();
			reset();
		}

		public void changeOrderCredit() {
			stopLoading();
			reset();
		}
	}
}
