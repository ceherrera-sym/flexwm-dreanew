/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import com.flexwm.client.op.UiProductTree;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoPayCondition;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.cm.BmoQuoteProperty;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductCompany;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoProductKitItem;
import com.flexwm.shared.op.BmoProductPrice;
import com.flexwm.shared.op.BmoWhStock;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
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


public class UiQuoteForm extends UiForm {
	BmoQuote bmoQuote = new BmoQuote();
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	TextBox codeTextBox = new TextBox();	
	UiListBox quoteTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
	TextBox nameTextBox = new TextBox();	
	TextBox amountTextBox = new TextBox();
	TextBox discountTextBox = new TextBox();
	TextArea descriptionTextArea = new TextArea();
	TextArea commentsTextArea = new TextArea();
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	TextBox currencyParityTextBox = new TextBox();
	CheckBox coveregeParityCheckBox = new CheckBox();
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox userSuggestBox;
	TextBox taxTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();
	UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
	UiDateTimeBox endDateTimeBox = new UiDateTimeBox();	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox customerContactIdListBox = new UiListBox(getUiParams(), new BmoCustomerContact());
	UiListBox payConditionUiListBox = new UiListBox(getUiParams(), new BmoPayCondition());

//	UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
//	UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
	CheckBox showEquipmentQuantityCheckBox = new CheckBox();
	CheckBox showEquipmentPriceCheckBox = new CheckBox();
	CheckBox showStaffQuantityCheckBox = new CheckBox();
	CheckBox showStaffPriceCheckBox = new CheckBox();

	protected FlowPanel formatPanel;

	// Botones
	private Button addQuoteItemButton = new Button("+ITEM");
	private Button addKitButton = new Button("+KIT");
	private FlowPanel quoteButtonPanel = new FlowPanel();
	private Button extrasButton = new Button("EXTRAS");

	// Quote Groups
	BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
	private HashMap<String, UiQuoteGroupGrid> quoteGroupUiMap = new HashMap<String, UiQuoteGroupGrid>();
	private FlowPanel quoteGroupsPanel = new FlowPanel();
	private QuoteUpdater quoteUpdater = new QuoteUpdater();
	private int selectedQuoteGroupId;
	private Button addQuoteGroupButton = new Button("+GRUPO");
	private BmFilter bmoFilterQuoteGroup;
	protected DialogBox quoteGroupDialogBox;
	private int quoteIdRpc = 0;
	private int quoteIdRpcAttempt = 0;
	private int quoteGroupsRpcAttempt = 0;
	private int parityFromCurrencyRpcAttempt = 0;
	private String currencyId;
	private int saveAmountChangeRpcAttempt = 0;
	private int stockQuantityRpcAttempt = 0;
	private int lockedQuantityRpcAttempt = 0;
	private int saveShowChangeRpcAttempt = 0;
	private int priceRpcAttempt = 0;
	private int productCompanySpecificRpcAttempt = 0;

	// QuoteItems
	protected DialogBox quoteItemDialogBox;

	// Browser de productos
	public final SingleSelectionModel<BmObject> productSelection = new SingleSelectionModel<BmObject>();
	private UiProductTree uiProductTree = new UiProductTree(productSelection);
	private CellBrowser productCellBrowser;
	private FlowPanel productCellPanel = new FlowPanel();

//	// Recursos
	private UiQuoteEquipmentGrid quoteEquipmentGrid;
	private FlowPanel quoteEquipmentPanel = new FlowPanel();

	// Personal
	private UiQuoteStaffGrid quoteStaffGrid;
	private FlowPanel quoteStaffPanel = new FlowPanel();

	// Inmuebles
	private UiQuotePropertyGrid quotePropertyGrid;
	private FlowPanel quotePropertyPanel = new FlowPanel();

	// Extras de inmuebles
	private UiQuotePropertyModelExtraGrid quotePropertyModelExtraGrid;
	private FlowPanel quotePropertyModelExtraPanel = new FlowPanel();

	// Otros
	private NumberFormat numberFormat = NumberFormat.getDecimalFormat();
	double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
	private CheckBox taxAppliesCheckBox = new CheckBox("");

	// Copiar cotizacion
	private Button copyQuoteButton = new Button("COPIAR");
	private Button copyQuoteDialogButton = new Button("COPIAR ITEMS");
	private Button copyQuoteCloseDialogButton = new Button("CERRAR");
	protected DialogBox copyQuoteDialogBox;
	UiSuggestBox copyQuoteSuggestBox = new UiSuggestBox(new BmoQuote());

	// SECCIONES
	private String generalSection = "Datos Generales";
	private String productTreeSection = "Navegador de Productos";
	private String itemSection = "Items";
	private String equipmentSection = "Recursos";
	private String staffSection = "Colaboradores";
	
	private String propertySection = "Inmueble";
	private String extrasSection = "Extras";
	private String paritySection = "Moneda y Tipo de Cambio";
	private String totalSection = "Totales";
	private String statusSection = "Estatus";


	public UiQuoteForm(UiParams uiParams, int id) {
		super(uiParams, new BmoQuote(), id);
		bmoQuote = (BmoQuote)getBmObject();
		initialize();
	}

	public UiQuoteForm(UiParams uiParams, int id, BmoOpportunity bmoOpportunity) {
		super(uiParams, new BmoQuote(), id);
		bmoQuote = (BmoQuote)getBmObject();
		this.bmoOpportunity = bmoOpportunity;
		initialize();
	}

	public void initialize() {
		// Cambiar etiqueta de acuerdo a configuracion de campo
		propertySection = getSFParams().getProgramTitle(new BmoQuoteProperty().getProgramCode());

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

		addQuoteGroupButton.setStyleName("formSaveButton");
		addQuoteGroupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addQuoteGroupDialog();
			}
		});

		// Todas las operaciones con los items de productos 
		addQuoteItemButton.setStyleName("formSaveButton");
		addQuoteItemButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addProduct();
			}
		});

		addKitButton.setStyleName("formSaveButton");
		addKitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addKit();
			}
		});

		// Copiar cotizacion
		copyQuoteDialogButton.setStyleName("formCloseButton");
		copyQuoteDialogButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				copyQuoteDialog();
			}
		});

		copyQuoteCloseDialogButton.setStyleName("formCloseButton");
		copyQuoteCloseDialogButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				copyQuoteDialogBox.hide();
			}
		});

		copyQuoteButton.setStyleName("formCloseButton");
		copyQuoteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				copyQuoteAction();
				copyQuoteDialogBox.hide();
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

		ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				discountChange();
			}
		};
		discountTextBox.addValueChangeHandler(textChangeHandler);

		if (!newRecord) {
			ValueChangeHandler<Boolean> coveregeParityCheckBoxChangeHandler = new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					coverageParity();
				}
			};
			coveregeParityCheckBox.addValueChangeHandler(coveregeParityCheckBoxChangeHandler);
		}
		// Boton de mostrar descuentos
		extrasButton.setStyleName("formCloseButton");
		extrasButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				toggleExtras();
			}
		});

		// Panel de staff
		quoteButtonPanel.setWidth("100%");
		quoteGroupsPanel.setWidth("100%");
//		quoteEquipmentPanel.setWidth("100%");
//		quoteStaffPanel.setWidth("100%");
		quotePropertyPanel.setWidth("100%");
		quotePropertyModelExtraPanel.setWidth("100%");
	}

//	public void getBmoOpportunity(int quoteIdRp) {
//		getBmoOpportunity(quoteIdRp, 0);
//	}
//	public void getBmoOpportunity(int quoteIdRp, int quoteIdRpcAttemp) {
//		if (quoteIdRpcAttemp < 5) {	
//			setQuoteIdRpcAttempt(quoteIdRpcAttemp + 1);
//			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//				public void onFailure(Throwable caught) {
//					getBmoOpportunity(quoteIdRp) ;
//					if(getQuoteIdRpcAttempt() < 5)
//						showErrorMessage(this.getClass().getName() + "-getBmoOpportunity() ERROR: " + caught.toString());	
//				}	
//				public void onSuccess(BmObject result) {
//					stopLoading();
//					setQuoteIdRpcAttempt(0);
//					bmoOpportunity = (BmoOpportunity)result;	
//					statusEffectRpc();
//
//				}
//			};
//			try {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().getBy(bmoOpportunity.getPmClass(), quoteIdRp, bmoOpportunity.getQuoteId().getName(), callback);	
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-getBmoOpportunity() ERROR: " + e.toString());
//			}
//		}	
//
//	}

	@Override
	public void populateFields(){
		bmoQuote = (BmoQuote)getBmObject();

		try {
			// Si no esta asignado el tipo, buscar por el default
			if (!(bmoQuote.getOrderTypeId().toString().length() > 0))
				bmoQuote.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());

			// Si no esta asignado el vendedor, buscar por el usuario loggeado
			if (!(bmoQuote.getUserId().toInteger() > 0))
				bmoQuote.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoQuote.getCurrencyId().toInteger() > 0))
				bmoQuote.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());

			if (newRecord) {
				populateParityFromCurrency(bmoQuote.getCurrencyId().toString());
				// Busca Empresa seleccionada por default
				if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
					bmoQuote.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
			}

			// Filtrar contactos del cliente
			BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
			BmFilter contactsByCustomer = new BmFilter();
			contactsByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoQuote.getCustomerId().toInteger());
			customerContactIdListBox.addFilter(contactsByCustomer);

			// Filtro de Grupos de Cotizacion
			bmoFilterQuoteGroup = new BmFilter();
			bmoFilterQuoteGroup.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId(), bmoQuote.getId());

		} catch (BmException e) {
			showSystemMessage("No se puede asignar tipo default : " + e.toString());
		}

		formFlexTable.addSectionLabel(1, 0, generalSection, 2);
		formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoQuote.getCode());
		formFlexTable.addField(3, 0, quoteTypeListBox, bmoQuote.getOrderTypeId());	
		formFlexTable.addField(4, 0, nameTextBox, bmoQuote.getName());
		formFlexTable.addField(5, 0, customerSuggestBox, bmoQuote.getCustomerId());
		formFlexTable.addField(6, 0, userSuggestBox, bmoQuote.getUserId());
		formFlexTable.addField(7, 0, companyListBox, bmoQuote.getCompanyId());
		formFlexTable.addField(8, 0, marketListBox, bmoQuote.getMarketId());
		if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
			formFlexTable.addField(9, 0, payConditionUiListBox, bmoQuote.getPayConditionId());
		formFlexTable.addField(10, 0, startDateTimeBox, bmoQuote.getStartDate());
		formFlexTable.addField(11, 0, endDateTimeBox, bmoQuote.getEndDate());
//		formFlexTable.addField(11, 0, customerContactIdListBox, bmoQuote.getCustomerContactId());
//		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
//			// Asigna filtros de control presupuestal
//			setBudgetItemsListBoxFilters(bmoQuote.getCompanyId().toInteger());
//			formFlexTable.addField(12, 0, budgetItemUiListBox, bmoQuote.getBudgetItemId());
//			formFlexTable.addField(13, 0, areaUiListBox, bmoQuote.getAreaId());
//		}

		if (!newRecord) {
			// Rentas, Ventas, Servicios
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) 
					|| bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
					|| bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {

				if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
					formFlexTable.addSectionLabel(12, 0, productTreeSection, 2);
					formFlexTable.addPanel(13, 0, productCellPanel, 2);
				}

				formFlexTable.addSectionLabel(14, 0, itemSection, 2);

				// Solo permitir agregar si no está autorizada
				if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_REVISION) {
					formFlexTable.addPanel(15, 0, quoteButtonPanel);
				}
				quoteButtonPanel.clear();
				quoteButtonPanel.add(addQuoteGroupButton);
				quoteButtonPanel.add(addQuoteItemButton);
				quoteButtonPanel.add(addKitButton);
				quoteButtonPanel.add(copyQuoteDialogButton);
				formFlexTable.addPanel(16, 0, quoteGroupsPanel, 2);
			}

//			// Recursos
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				formFlexTable.addSectionLabel(17, 0, equipmentSection, 2);
				// Panel de Checkboxes
				FlowPanel checkBoxPanel = new FlowPanel();
				checkBoxPanel.setWidth("100%");
				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showEquipmentQuantityCheckBox, bmoQuote.getShowEquipmentQuantity()));
				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showEquipmentPriceCheckBox, bmoQuote.getShowEquipmentPrice()));
				formFlexTable.addLabelCell(18, 0, "Mostrar:");
				formFlexTable.addPanel(18, 1, checkBoxPanel, 1);
				formFlexTable.addPanel(19, 0, quoteEquipmentPanel, 2);
				quoteEquipmentGrid = new UiQuoteEquipmentGrid(getUiParams(), quoteEquipmentPanel, bmoQuote, quoteUpdater);
				quoteEquipmentGrid.show();
			}
//
//			// Personal
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				formFlexTable.addSectionLabel(21, 0, staffSection, 2);
				// Panel de Checkboxes
				FlowPanel checkBoxPanel = new FlowPanel();
				checkBoxPanel.setWidth("100%");
				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showStaffQuantityCheckBox, bmoQuote.getShowStaffQuantity()));
				checkBoxPanel.add(formFlexTable.getCheckBoxPanel(showStaffPriceCheckBox, bmoQuote.getShowStaffPrice()));
				formFlexTable.addLabelCell(22, 0, "Mostrar:");
				formFlexTable.addPanel(22, 1, checkBoxPanel, 1);
				formFlexTable.addPanel(23, 0, quoteStaffPanel, 2);
				quoteStaffGrid = new UiQuoteStaffGrid(getUiParams(), quoteStaffPanel, bmoQuote, quoteUpdater);
				quoteStaffGrid.show();
			}

			// Inmuebles
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
				formFlexTable.addSectionLabel(24, 0, propertySection, 2);
				formFlexTable.addPanel(25, 0, quotePropertyPanel, 2);
				quotePropertyGrid = new UiQuotePropertyGrid(getUiParams(), quotePropertyPanel, bmoQuote, quoteUpdater);
				quotePropertyGrid.show();

				formFlexTable.addSectionLabel(26, 0, extrasSection, 2);
				formFlexTable.addPanel(27, 0, quotePropertyModelExtraPanel, 2);
				quotePropertyModelExtraGrid = new UiQuotePropertyModelExtraGrid(getUiParams(), quotePropertyModelExtraPanel, bmoQuote, quoteUpdater);
				quotePropertyModelExtraGrid.show();
			}

			formFlexTable.addSectionLabel(28, 0, paritySection, 2);
			formFlexTable.addField(29, 0, coveregeParityCheckBox, bmoQuote.getCoverageParity());
			formFlexTable.addField(30, 0, currencyListBox, bmoQuote.getCurrencyId());
			formFlexTable.addField(31, 0, currencyParityTextBox, bmoQuote.getCurrencyParity());

			formFlexTable.addSectionLabel(32, 0, totalSection, 2);
			formFlexTable.addFieldReadOnly(33, 0, amountTextBox, bmoQuote.getAmount());
			formFlexTable.addField(34, 0, discountTextBox, bmoQuote.getDiscount());
			formFlexTable.addButtonCell(34, 0, extrasButton);
			formFlexTable.addField(35, 0, taxAppliesCheckBox, bmoQuote.getTaxApplies());
			formFlexTable.addFieldReadOnly(36, 0, taxTextBox, bmoQuote.getTax());
			formFlexTable.addFieldReadOnly(37, 0, totalTextBox, bmoQuote.getTotal());
			formFlexTable.addField(38, 0, descriptionTextArea, bmoQuote.getDescription());
			formFlexTable.addField(39, 0, commentsTextArea, bmoQuote.getComments());

			formFlexTable.addSectionLabel(40, 0, statusSection, 2);
			formFlexTable.addLabelField(41, 0, bmoQuote.getAuthNum());			
			if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_AUTHORIZED) 
				formFlexTable.addLabelField(42, 0, bmoQuote.getAuthorizedDate());
			else if (bmoQuote.getStatus().toChar() == BmoQuote.STATUS_CANCELLED)
				formFlexTable.addLabelField(43, 0, bmoQuote.getCancelledDate());
			formFlexTable.addField(44, 0, statusListBox, bmoQuote.getStatus());
			
			listQuoteGroups();
			reset();

		} else {
			formFlexTable.addField(12, 0, currencyListBox, bmoQuote.getCurrencyId());
			formFlexTable.addField(13, 0, currencyParityTextBox, bmoQuote.getCurrencyParity());
			formFlexTable.addField(14, 0, statusListBox, bmoQuote.getStatus());
			statusListBox.setEnabled(false);
		}

		// Oculta secciones
		if (!newRecord)	{
			formFlexTable.hideSection(generalSection);
			formFlexTable.hideSection(productTreeSection);
			formFlexTable.hideSection(paritySection);
			formFlexTable.hideSection(statusSection);
			formFlexTable.hideSection(equipmentSection);
			formFlexTable.hideSection(staffSection);
		}

		statusEffect();
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoQuote.setId(id);

		bmoQuote.getCode().setValue(codeTextBox.getText());
		bmoQuote.getName().setValue(nameTextBox.getText());
		bmoQuote.getCustomerId().setValue(customerSuggestBox.getSelectedId());
		bmoQuote.getUserId().setValue(userSuggestBox.getSelectedId());
		bmoQuote.getCurrencyId().setValue(currencyListBox.getSelectedId());
		bmoQuote.getCurrencyParity().setValue(currencyParityTextBox.getText());
		bmoQuote.getOrderTypeId().setValue(quoteTypeListBox.getSelectedId());

		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) {
			if (discountTextBox.getText().equals("")) 
				bmoQuote.getDiscount().setValue(0);
			else 
				bmoQuote.getDiscount().setValue(discountTextBox.getText());
		}

		bmoQuote.getTax().setValue(0);
		bmoQuote.getTotal().setValue(0);
		bmoQuote.getDescription().setValue(descriptionTextArea.getText());
		bmoQuote.getComments().setValue(commentsTextArea.getText());
		bmoQuote.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
		bmoQuote.getStatus().setValue(statusListBox.getSelectedCode());
		bmoQuote.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoQuote.getMarketId().setValue(marketListBox.getSelectedId());
		bmoQuote.getStartDate().setValue(startDateTimeBox.getDateTime());
		bmoQuote.getEndDate().setValue(endDateTimeBox.getDateTime());
		bmoQuote.getCoverageParity().setValue(coveregeParityCheckBox.getValue());
		bmoQuote.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());
		return bmoQuote;
	}

	@Override
	public void saveNext() {
		if (newRecord || isSingleSlave()) {
			UiQuoteForm uiQuoteForm = new UiQuoteForm(getUiParams(), getBmObject().getId(), bmoOpportunity);
			uiQuoteForm.show();
		} else {
			UiQuoteList uiQuoteList = new UiQuoteList(getUiParams());
			uiQuoteList.show();
		}		
	}

	@Override
	public void close() {
		UiQuoteList uiQuoteList = new UiQuoteList(getUiParams());
		uiQuoteList.show();
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox) {
			update("Desea cambiar el Status de la Cotización?");
			statusEffect();
		} else if (event.getSource() == currencyListBox) {
			getParityFromCurrency(currencyListBox.getSelectedId());
			statusEffect();
		} 
		
	}

	@Override
	public void formDateChange(ValueChangeEvent<Date> event) {
		if (event.getSource() == startDateTimeBox) {
			if(bmoQuote.getCoverageParity().toInteger() != 1)
				populateParityFromCurrency(currencyListBox.getSelectedId());
		}
	}

	// Obtener la paridad de la moneda
	public void populateParityFromCurrency(String currencyId) {
		getParityFromCurrency(currencyId);
	}
	
	private void statusEffect() {
//		getBmoOpportunity(bmoQuote.getId());
		statusEffectRpc();
	}
	
	private void statusEffectRpc(){
		startDateTimeBox.setEnabled(false);
		endDateTimeBox.setEnabled(false);	
		discountTextBox.setVisible(false);
		statusListBox.setEnabled(false);
		extrasButton.setVisible(false);
		coveregeParityCheckBox.setEnabled(false);
		taxAppliesCheckBox.setEnabled(false);
		userSuggestBox.setEnabled(false);
		payConditionUiListBox.setEnabled(false);
		marketListBox.setEnabled(false);
		if (!newRecord) {
			nameTextBox.setEnabled(false);
			quoteTypeListBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			companyListBox.setEnabled(false);
		}

		// Obtener tipo de cotizacion
		BmoOrderType bmoOrderType = (BmoOrderType)quoteTypeListBox.getSelectedBmObject();
		if (bmoOrderType == null) bmoOrderType = bmoQuote.getBmoOrderType();
		if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
			startDateTimeBox.setEnabled(true);
			endDateTimeBox.setEnabled(true);
		}

		// Si tiene descuento, mostrarlo
		if (bmoQuote.getDiscount().toDouble() > 0)
			discountTextBox.setVisible(true);

		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED) 
				|| bmoQuote.getStatus().equals(BmoQuote.STATUS_CANCELLED)) {
			descriptionTextArea.setEnabled(false);
			commentsTextArea.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			startDateTimeBox.setEnabled(false);
			endDateTimeBox.setEnabled(false);
			discountTextBox.setEnabled(false);
			customerContactIdListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			coveregeParityCheckBox.setEnabled(false);
			taxAppliesCheckBox.setEnabled(false);

			showEquipmentQuantityCheckBox.setEnabled(false);
			showEquipmentPriceCheckBox.setEnabled(false);
			showStaffQuantityCheckBox.setEnabled(false);
			showStaffPriceCheckBox.setEnabled(false);
			
			if (bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED) 
					&& getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMANINQUOTAUT)
					&& (!bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON)))
				userSuggestBox.setEnabled(true);
		} else {
			payConditionUiListBox.setEnabled(true);
			marketListBox.setEnabled(true);

			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_LIMITEDDISCOUNT) ||
					getSFParams().hasSpecialAccess(BmoQuote.ACCESS_UNLIMITEDDISCOUNT)) {
				extrasButton.setVisible(true);
			}

			taxAppliesCheckBox.setEnabled(true);
			coveregeParityCheckBox.setEnabled(true);
			showEquipmentQuantityCheckBox.setEnabled(true);
			showEquipmentPriceCheckBox.setEnabled(true);
			showStaffQuantityCheckBox.setEnabled(true);
			showStaffPriceCheckBox.setEnabled(true);

			if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
				//				currencyListBox).setEnabled(true);
				currencyParityTextBox.setEnabled(true);
			} else {
				//				currencyListBox.setEnabled(true);
				currencyParityTextBox.setEnabled(false);
			}
			
			if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMANINQUOTAUT)
					&& (!bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON)))
				userSuggestBox.setEnabled(true);

			coverageParity();
		}

		// Si hay seleccion default de empresa, deshabilitar combo
		if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
			companyListBox.setEnabled(false);

		if (!newRecord) {
			// Si tiene permiso para modificar status, habilitar combo
			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGESTATUS)) 
				statusListBox.setEnabled(true);
		}
		//Si la cotización esta autorizada o en revision y si tiene el permiso especial puede cambiar fechas 
		if (bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED) 
				|| bmoQuote.getStatus().equals(BmoQuote.STATUS_REVISION)) {
			// Acceso especial cambiar vendedor
			if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEDATEINQUOTAUT)
					&& (!bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON))) {
				startDateTimeBox.setEnabled(true);
				endDateTimeBox.setEnabled(true);
			}
		}
	}
	// Actualiza combo de partidas presp. por empresa
//	private void populateBudgetItems(int companyId) {
//		budgetItemUiListBox .clear();
//		budgetItemUiListBox.clearFilters();
//		setBudgetItemsListBoxFilters(companyId);
//		budgetItemUiListBox.populate(bmoQuote.getBudgetItemId());
//	}
//
//	// Asigna filtros al listado de partidas presp. por empresa
//	private void setBudgetItemsListBoxFilters(int companyId) {
//		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
//
//		if (companyId > 0) {
//			BmFilter bmFilterByCompany = new BmFilter();
//			bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
//			budgetItemUiListBox.addBmFilter(bmFilterByCompany);
//			
//			// Filtro de ingresos(abono)
//			BmFilter filterByDeposit = new BmFilter();
//			filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
//			budgetItemUiListBox.addFilter(filterByDeposit);
//			
//		} else {
//			BmFilter bmFilter = new BmFilter();
//			bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
//			budgetItemUiListBox.addBmFilter(bmFilter);
//		}
//	}
	
	// Obtiene paridad de la moneda, primer intento
	public void getParityFromCurrency(String currencyId) {
		getParityFromCurrency(currencyId, 0);
	}

	public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
		if (parityFromCurrencyRpcAttempt < 5) {
			setCurrencyId(currencyId);
			setParityFromCurrencyRpcAttempt(parityFromCurrencyRpcAttempt + 1);
		
			BmoCurrency bmoCurrency = new BmoCurrency();
			String startDate = startDateTimeBox.getTextBox().getText();
			if (startDateTimeBox.getTextBox().getText().equals("")) {
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
					else
						currencyParityTextBox.setValue(result.getMsg());
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
	public void formBooleanChange(ValueChangeEvent<Boolean> event){
		try {
			if (event.getSource() == showEquipmentQuantityCheckBox
					|| event.getSource() == showEquipmentPriceCheckBox
					|| event.getSource() == showStaffQuantityCheckBox
					|| event.getSource() == showStaffPriceCheckBox) {
				bmoQuote.getShowEquipmentQuantity().setValue(showEquipmentQuantityCheckBox.getValue());
				bmoQuote.getShowEquipmentPrice().setValue(showEquipmentPriceCheckBox.getValue());
				bmoQuote.getShowStaffQuantity().setValue(showStaffQuantityCheckBox.getValue());
				bmoQuote.getShowStaffPrice().setValue(showStaffPriceCheckBox.getValue());
				saveShowChange();
			}
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-formBooleanChange() ERROR: " + e.toString());
		}
	}
	
	// Guardar datos al darle clic a mostrar cantidad/precio en la cotizacion
	// NOTA: formBooleanChange() no funciona, por eso se mete onClick
	public void setBooleanShowEquipmentAndStaff() {
		try {
			bmoQuote.getShowEquipmentQuantity().setValue(showEquipmentQuantityCheckBox.getValue());
			bmoQuote.getShowEquipmentPrice().setValue(showEquipmentPriceCheckBox.getValue());
			bmoQuote.getShowStaffQuantity().setValue(showStaffQuantityCheckBox.getValue());
			bmoQuote.getShowStaffPrice().setValue(showStaffPriceCheckBox.getValue());
			saveShowChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-setBooleanShowEquipmentStaff() ERROR: " + e.toString());
		}
	}

	private void toggleExtras() {
		if (discountTextBox.isVisible()) 
			discountTextBox.setVisible(false);
		else 
			discountTextBox.setVisible(true);
	}

	public void reset(){
		updateAmount(id);

		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)
				|| bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			quoteGroupListStatusEffect();
		}

		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
			quoteEquipmentGrid.show();
			quoteStaffGrid.show();
		}

		if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
			quotePropertyGrid.show();
			quotePropertyModelExtraGrid.show();
		}
	}

	public void updateQuoteGroup(String quoteGroupId) {
		this.selectedQuoteGroupId = Integer.parseInt(quoteGroupId);
		quoteGroupUiMap.get("" + quoteGroupId).show();
	}

	private void quoteGroupListStatusEffect() {
		ArrayList<UiQuoteGroupGrid> quoteGroupGridList = new ArrayList<UiQuoteGroupGrid>(quoteGroupUiMap.values());

		Iterator<UiQuoteGroupGrid> quoteGroupIterator = quoteGroupGridList.iterator();
		while (quoteGroupIterator.hasNext()) {
			((UiQuoteGroupGrid)quoteGroupIterator.next()).statusEffect();
		}
	}

	public void setAmount(BmoQuote bmoQuote) {
		this.bmoQuote = bmoQuote;
		numberFormat = NumberFormat.getCurrencyFormat();
		amountTextBox.setText(numberFormat.format(bmoQuote.getAmount().toDouble()));
		taxTextBox.setText(numberFormat.format(bmoQuote.getTax().toDouble()));
		totalTextBox.setText(numberFormat.format(bmoQuote.getTotal().toDouble()));	
	}
	
	public void setUpdateDataQuote(BmoQuote bmoQuote) {
		nameTextBox.setText(bmoQuote.getName().toString());
		companyListBox.setSelectedId(bmoQuote.getCompanyId().toString());
//		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//			budgetItemUiListBox.setSelectedId(bmoQuote.getBudgetItemId().toString());
//			populateBudgetItems(bmoQuote.getCompanyId().toInteger());
//		}
	}

	public void discountChange() {
		try {
			double discount = Double.parseDouble(discountTextBox.getText());
			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_UNLIMITEDDISCOUNT)) {
				// No revisa el limite del descuento
				bmoQuote.getDiscount().setValue(discount);
				saveAmountChange();
			} else if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_LIMITEDDISCOUNT)) {
				// Revisa que el descuento no sea mayor al % del total establecido
				double discountLimit = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDiscountLimit().toDouble();
				double maxDiscount = bmoQuote.getAmount().toDouble() * (discountLimit / 100);

				if (discount > maxDiscount) {
					showSystemMessage("El Descuento no puede ser mayor a: " + numberFormat.format(maxDiscount));
					discount = 0;
				}

				bmoQuote.getDiscount().setValue(discount);
				discountTextBox.setValue("" + discount);
				saveAmountChange();
			}  
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-discountChange() ERROR: " + e.toString());
		}
	}

	public void coverageParity() {
		// Si el campo de cobertura está habilitado, y es verdadero cobertura, habilitar paridad
		if (getSFParams().isFieldEnabled(bmoQuote.getCoverageParity())) {

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
			bmoQuote.getTaxApplies().setValue(taxAppliesCheckBox.getValue());
			saveAmountChange();
		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-taxAppliesChange() ERROR: " + e.toString());
		}
	}
	
	// Actualizar montos, 0 intentos
	public void updateAmount(int quoteIdRpc) {
		updateAmount(quoteIdRpc, 0);
	}

	public void updateAmount(int quoteIdRpc, int quoteIdRpcAttempt) {
		if (quoteIdRpcAttempt < 5) {
			setQuoteIdRpc(quoteIdRpc);
			setQuoteIdRpcAttempt(quoteIdRpcAttempt + 1);
			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getQuoteIdRpcAttempt() < 5) {
						updateAmount(getQuoteIdRpc(), getQuoteIdRpcAttempt());
					} else {
						showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
					}
				}
	
				public void onSuccess(BmObject result) {
					stopLoading();
					setQuoteIdRpcAttempt(0);
					BmoQuote bmoQuote = ((BmoQuote)result);
					setAmount(bmoQuote);
					setUpdateDataQuote(bmoQuote);
				}
			};
			try {
				startLoading();
				getUiParams().getBmObjectServiceAsync().get(bmoQuote.getPmClass(), id, callback);
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + e.toString());
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
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveAmountChangeRpcAttempt() < 5)
						saveAmountChange(getSaveAmountChangeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					setSaveAmountChangeRpcAttempt(0);
					processQuoteUpdateResult(result);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoQuote.getPmClass(), bmoQuote, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + e.toString());
			}
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
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getSaveShowChangeRpcAttempt() < 5)
						saveShowChange(getSaveShowChangeRpcAttempt());
					else
						showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + caught.toString());
				}
	
				public void onSuccess(BmUpdateResult result) {
					//showSystemMessage("saveShowChange");
					stopLoading();
					setSaveShowChangeRpcAttempt(0);
				}
			};
	
			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().saveSimple(bmoQuote.getPmClass(), bmoQuote, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + e.toString());
			}
		}
	}

	public void processQuoteUpdateResult(BmUpdateResult result) {
		if (result.hasErrors()) 
			showFormMsg("Error al actualizar descuento.", "Error al actualizar descuento: " + result.errorsToString());
		else 
			updateAmount(id);
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

	public void addQuoteGroup(int quoteGroupId) {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		quoteGroupsPanel.add(vp);

		UiQuoteGroupGrid quoteGroupGrid = new UiQuoteGroupGrid(getUiParams(), vp, bmoQuote, bmoOpportunity, quoteGroupId, this.quoteUpdater);
		quoteGroupGrid.show();

		quoteGroupUiMap.put("" + quoteGroupId, quoteGroupGrid);
	}

	//	public void listQuoteGroups(){
	//		AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
	//			public void onFailure(Throwable caught) {
	//				stopLoading();
	//				showErrorMessage(this.getClass().getName() + "-listQuoteGroups() ERROR: " + caught.toString());	
	//			}
	//			public void onSuccess(ArrayList<BmObject> result) {
	//				stopLoading();
	//				setQuoteGroupList(result);
	//			}
	//		};
	//		try {
	//			if (!isLoading()) {
	//				startLoading();
	//				getUiParams().getBmObjectServiceAsync().list(bmoQuoteGroup.getPmClass(), bmoFilterQuoteGroup, callback);
	//			} else {
	//				showSystemMessage("No se puede mostrar listado, esta cargado...");
	//			}
	//		} catch (SFException e) {
	//			stopLoading();
	//			showErrorMessage(this.getClass().getName() + "-listQuoteGroups() ERROR: " + e.toString());
	//		}
	//	}

	// Obtiene lista de grupos de pedidos, primer intento
	public void listQuoteGroups() {
		listQuoteGroups(0);
	}

	// Obtiene lista de grupos de pedidos
	public void listQuoteGroups(int quoteGroupsRpcAttempt) {
		if (quoteGroupsRpcAttempt < 5) {
			setQuoteGroupsRpcAttempt(quoteGroupsRpcAttempt + 1);

			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (getQuoteGroupsRpcAttempt() < 5) {
						listQuoteGroups(getQuoteGroupsRpcAttempt());
					} else {
						showErrorMessage(this.getClass().getName() + "-listQuoteGroups() ERROR: " + caught.toString());		
					}
				}
				@Override
				public void onSuccess(ArrayList<BmObject> result) {
					stopLoading();
					setQuoteGroupsRpcAttempt(0);
					setQuoteGroupList(result);
				}
			};
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().list(bmoQuoteGroup.getPmClass(), bmoFilterQuoteGroup, callback);
				} else {
					showSystemMessage("No se puede mostrar listado, esta cargando...");
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-listQuoteGroups() ERROR: " + e.toString());
			}
		}
	}

	public void setQuoteGroupList(ArrayList<BmObject> quoteGroupList) {
		quoteGroupsPanel.clear();
		quoteGroupUiMap = new HashMap<String, UiQuoteGroupGrid>();
		Iterator<BmObject> quoteGroupIterator = quoteGroupList.iterator();
		while (quoteGroupIterator.hasNext()) {
			BmoQuoteGroup bmoQuoteGroup = (BmoQuoteGroup)quoteGroupIterator.next();
			addQuoteGroup(bmoQuoteGroup.getId());
		}
	}

	public void addProduct(){
		addProduct(new BmoProduct());
	}

	public void addQuoteGroupDialog() {
		quoteGroupDialogBox = new DialogBox(true);
		quoteGroupDialogBox.setGlassEnabled(true);
		quoteGroupDialogBox.setText("Grupo de Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "150px");

		quoteGroupDialogBox.setWidget(vp);

		UiQuoteGroupForm quoteGroupForm = new UiQuoteGroupForm(getUiParams(), vp, bmoQuote.getId());
		quoteGroupForm.show();

		quoteGroupDialogBox.center();
		quoteGroupDialogBox.show();
	}

	public void addProduct(BmoProduct bmoProduct) {
		quoteItemDialogBox = new DialogBox(true);
		quoteItemDialogBox.setGlassEnabled(true);
		quoteItemDialogBox.setText("Item de Cotización");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "250px");

		quoteItemDialogBox.setWidget(vp);

		UiQuoteItemForm quoteItemForm = new UiQuoteItemForm(getUiParams(), vp, selectedQuoteGroupId, bmoProduct);

		quoteItemForm.show();

		quoteItemDialogBox.center();
		quoteItemDialogBox.show();
	}

	public void addKit() {
		addKit(new BmoProductKit());
	}

	public void addKit(BmoProductKit bmoProductKit){
		quoteItemDialogBox = new DialogBox(true);
		quoteItemDialogBox.setGlassEnabled(true);
		quoteItemDialogBox.setText("Item de Kit");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "150px");

		quoteItemDialogBox.setWidget(vp);

		UiQuoteKitForm quoteKitForm = new UiQuoteKitForm(getUiParams(), vp, bmoQuote.getId(), bmoProductKit);

		quoteItemDialogBox.center();
		quoteItemDialogBox.show();

		quoteKitForm.show();
	}

	public void addKitItem(BmoProductKitItem bmoProductKitItem) {
		quoteItemDialogBox = new DialogBox(true);
		quoteItemDialogBox.setGlassEnabled(true);
		quoteItemDialogBox.setText("Item de Kit");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "200px");

		quoteItemDialogBox.setWidget(vp);

		UiQuoteKitItemForm quoteKitItemForm = new UiQuoteKitItemForm(getUiParams(), vp, bmoQuote.getId(), bmoProductKitItem, selectedQuoteGroupId);

		quoteItemDialogBox.center();
		quoteItemDialogBox.show();

		quoteKitItemForm.show();
	}

	public void copyQuoteDialog() {
		copyQuoteDialogBox = new DialogBox(true);
		copyQuoteDialogBox.setGlassEnabled(true);
		copyQuoteDialogBox.setText("Copiar Cotización");
		copyQuoteDialogBox.setSize("400px", "100px");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("400px", "100px");
		copyQuoteDialogBox.setWidget(vp);

		UiFormFlexTable formCopyQuoteTable = new UiFormFlexTable(getUiParams());
		BmoQuote fromBmoQuote = new BmoQuote();
		formCopyQuoteTable.addField(1, 0, copyQuoteSuggestBox, fromBmoQuote.getIdField());

		HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
		changeStaffButtonPanel.add(copyQuoteButton);
		changeStaffButtonPanel.add(copyQuoteCloseDialogButton);

		vp.add(formCopyQuoteTable);
		vp.add(changeStaffButtonPanel);

		copyQuoteDialogBox.center();
		copyQuoteDialogBox.show();
	}

	//Obtener la cantidad en almacen
	public void copyQuoteAction(){
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-copyQuoteAction() ERROR: " + caught.toString());
			}

			public void onSuccess(BmUpdateResult result) {
				stopLoading();
				showSystemMessage("Copia de Items Exitosa.");
				show();
				//				reset();
			}
		};

		try {	
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().action(bmoQuote.getPmClass(), bmoQuote, BmoQuote.ACTION_COPYQUOTE, "" + copyQuoteSuggestBox.getSelectedId(), callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-copyQuoteAction() ERROR: " + e.toString());
		}
	} 
	
	// Variables para llamadas RPC
	public int getQuoteIdRpc() { // le puse rpc al final para que no haya confusion
		return quoteIdRpc;
	}

	public void setQuoteIdRpc(int quoteIdRpc) {
		this.quoteIdRpc = quoteIdRpc;
	}
	
	public int getQuoteIdRpcAttempt() {
		return quoteIdRpcAttempt;
	}

	public void setQuoteIdRpcAttempt(int quoteIdRpcAttempt) {
		this.quoteIdRpcAttempt = quoteIdRpcAttempt;
	}

	public int getQuoteGroupsRpcAttempt() {
		return quoteGroupsRpcAttempt;
	}

	public void setQuoteGroupsRpcAttempt(int quoteGroupsRpcAttempt) {
		this.quoteGroupsRpcAttempt = quoteGroupsRpcAttempt;
	}


	private class UiQuoteGroupForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox nameTextBox = new TextBox();
		private CheckBox showQuantityCheckBox = new CheckBox();
		private CheckBox showPriceCheckBox = new CheckBox();
		private CheckBox showAmountCheckBox = new CheckBox();
		private CheckBox showProductImageCheckBox = new CheckBox();
		private CheckBox showGroupImageCheckBox = new CheckBox();
		private UiFileUploadBox imageFileUpload = new UiFileUploadBox(getUiParams());
		UiListBox payConditionUiListBox= new UiListBox(getUiParams(), new BmoPayCondition());
		private BmoQuoteGroup bmoQuoteGroup;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private int quoteId;

		public UiQuoteGroupForm(UiParams uiParams, Panel defaultPanel, int quoteId) {
			super(uiParams, defaultPanel);
			bmoQuoteGroup = new BmoQuoteGroup();
			this.quoteId = quoteId;

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoQuoteGroup.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){

			// Por defecto mostrar Cantidad, Precio, Subtotal y Img. Prod
			try {
				bmoQuoteGroup.getShowQuantity().setValue(true);
				bmoQuoteGroup.getShowProductImage().setValue(true);
				bmoQuoteGroup.getShowAmount().setValue(true);
				bmoQuoteGroup.getShowPrice().setValue(true);
				
				if (!(bmoQuoteGroup.getId() > 0)) 
					bmoQuoteGroup.getPayConditionId().setValue(bmoQuote.getPayConditionId().toInteger());
				
			} catch (BmException e) {
				showSystemMessage("No se pudo validar, valídelo manual: " + e.toString());
			}	

			formTable.addField(1, 0, nameTextBox, bmoQuoteGroup.getName());
			formTable.addField(2, 0, showQuantityCheckBox, bmoQuoteGroup.getShowQuantity());
			formTable.addField(3, 0, showAmountCheckBox, bmoQuoteGroup.getShowAmount());
			formTable.addField(4, 0, showPriceCheckBox, bmoQuoteGroup.getShowPrice());
			formTable.addField(5, 0, showProductImageCheckBox, bmoQuoteGroup.getShowProductImage());
			formTable.addField(6, 0, imageFileUpload, bmoQuoteGroup.getImage());	
			formTable.addField(7, 0, showGroupImageCheckBox, bmoQuoteGroup.getShowGroupImage());
			if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
				formTable.addField(8, 0, payConditionUiListBox, bmoQuoteGroup.getPayConditionId());
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				quoteGroupDialogBox.hide();
				listQuoteGroups();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteGroup = new BmoQuoteGroup();
				bmoQuoteGroup.getCode().setValue("");
				bmoQuoteGroup.getName().setValue(nameTextBox.getText());
				bmoQuoteGroup.getShowQuantity().setValue(showQuantityCheckBox.getValue());
				bmoQuoteGroup.getShowPrice().setValue(showPriceCheckBox.getValue());
				bmoQuoteGroup.getShowProductImage().setValue(showProductImageCheckBox.getValue());
				bmoQuoteGroup.getShowGroupImage().setValue(showGroupImageCheckBox.getValue());
				bmoQuoteGroup.getImage().setValue(imageFileUpload.getBlobKey());
				bmoQuoteGroup.getShowAmount().setValue(showAmountCheckBox.getValue());
				bmoQuoteGroup.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());
				bmoQuoteGroup.getQuoteId().setValue(quoteId);
				bmoQuoteGroup.getAmount().setValue(0);
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteGroup.getPmClass(), bmoQuoteGroup, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}

	// Agrega un item de un producto a la orden de compra
	private class UiQuoteItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private TextBox quantityTextBox = new TextBox();
		private TextBox daysTextBox = new TextBox();
		private TextBox nameTextBox = new TextBox();		
		private TextArea descriptionTextArea = new TextArea();
		private TextBox priceTextBox = new TextBox();
		private CheckBox commissionCheckBox = new CheckBox();
		private BmoQuoteItem bmoQuoteItem;
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		private UiListBox quoteGroupListBox;
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		private UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		String productId = "";

		public UiQuoteItemForm(UiParams uiParams, Panel defaultPanel, int selectedQuoteGroupId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoQuoteItem = new BmoQuoteItem();

			try {
				if (selectedQuoteGroupId > 0) bmoQuoteItem.getQuoteGroupId().setValue(selectedQuoteGroupId);
				bmoQuoteItem.getProductId().setValue(bmoProduct.getId());
				bmoQuoteItem.getName().setValue("item");
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

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setVisible(false);
			if (getSFParams().hasWrite(bmoQuote.getProgramCode())) saveButton.setVisible(true);
			buttonPanel.add(saveButton);

			BmFilter filterQuoteGroups = new BmFilter();
			filterQuoteGroups.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId(), bmoQuote.getId());
			quoteGroupListBox = new UiListBox(getUiParams(), bmoQuoteGroup, filterQuoteGroups);

			//filtro para mostrar los equipos que Activos			
			BmFilter filterByEnabled = new BmFilter();
			filterByEnabled.setValueFilter(bmoQuoteItem.getBmoProduct().getKind(),bmoQuoteItem.getBmoProduct().getEnabled(), "1");
			productSuggestBox.addFilter(filterByEnabled);
			
			BmFilter filterByNotSubProduct = new BmFilter();
			filterByNotSubProduct.setValueOperatorFilter(bmoQuoteItem.getBmoProduct().getKind(), bmoQuoteItem.getBmoProduct().getType(), BmFilter.NOTEQUALS , "" + BmoProduct.TYPE_SUBPRODUCT);
			productSuggestBox.addFilter(filterByNotSubProduct);

			// Filtros para pedido de Renta(Drea)
			if (bmoOpportunity.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
				BmFilter productTypeProductFilter = new BmFilter();
				productTypeProductFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getType(), BmFilter.NOTEQUALS, ""+BmoProduct.TYPE_COMPLEMENTARY);
				productSuggestBox.addFilter(productTypeProductFilter);

				BmFilter productConsumableFilter = new BmFilter();
				productConsumableFilter.setValueOperatorFilter(bmoProduct.getKind(), bmoProduct.getConsumable(), BmFilter.NOTEQUALS, 1);
				productSuggestBox.addFilter(productConsumableFilter);
			}

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				try {
					if (bmoQuote.getBudgetItemId().toInteger() > 0)
						bmoQuoteItem.getBudgetItemId().setValue(bmoQuote.getBudgetItemId().toInteger());
					if (bmoQuote.getAreaId().toInteger() > 0)
						bmoQuoteItem.getAreaId().setValue(bmoQuote.getAreaId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
				}
			}

			defaultPanel.add(formTable);
		}

		public void show(){
			formTable.addField(1, 0, productSuggestBox, bmoQuoteItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacén", stockQuantity);
			formTable.addLabelField(3, 0, "En Cotización", lockedQuantity);
			formTable.addField(4, 0, nameTextBox, bmoQuoteItem.getName());
			formTable.addField(5, 0, descriptionTextArea, bmoQuoteItem.getDescription());
			formTable.addField(6, 0, quantityTextBox, bmoQuoteItem.getQuantity());
			// Si es de renta, mostrar días
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) 
				formTable.addField(7, 0, daysTextBox, bmoQuoteItem.getDays());
			if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission()))
				formTable.addField(8, 0, commissionCheckBox, bmoQuoteItem.getCommission());
			formTable.addField(9, 0, priceTextBox, bmoQuoteItem.getPrice());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				setBudgetItemsListBoxFilters(bmoQuote.getCompanyId().toInteger());
				formTable.addField(10, 0, budgetItemUiListBox, bmoQuoteItem.getBudgetItemId());
				formTable.addField(11, 0, areaUiListBox, bmoQuoteItem.getAreaId());
			}
			
			formTable.addField(12, 0, quoteGroupListBox, bmoQuoteItem.getQuoteGroupId());

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}
		
		// Actualiza combo de departamentos
//		private void populateArea() {
//			areaUiListBox.clear();
//			areaUiListBox.clearFilters();
//			areaUiListBox.populate(bmoQuoteItem.getAreaId());
//		}
//		
		// Actualiza combo de partidas presp. por empresa
//		private void populateBudgetItems(int companyId) {
//			budgetItemUiListBox.clear();
//			budgetItemUiListBox.clearFilters();
//			setBudgetItemsListBoxFilters(companyId);
//			budgetItemUiListBox.populate(bmoQuoteItem.getBudgetItemId());
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
					if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission())) {
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

		public void statusEffect() {	

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
				priceTextBox.setText("0");
				priceTextBox.setEnabled(true);
				stockQuantity.setText("");
				lockedQuantity.setText("");
				if (getSFParams().isFieldEnabled(bmoQuoteItem.getCommission())) 
					commissionCheckBox.setValue(false);
				BmoProductCompany bmoProductCompany = new BmoProductCompany();
				setBudgetItemAndArea(bmoProductCompany, true);
			}

			if (!getSFParams().hasSpecialAccess(BmoQuote.ACCESS_NOPRODUCTITEM)) {
				nameTextBox.setText("");
				nameTextBox.setEnabled(false);
				priceTextBox.setText("");
				priceTextBox.setEnabled(false);
			}

			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMPRICE))
				priceTextBox.setEnabled(true);

			if (getSFParams().hasSpecialAccess(BmoQuote.ACCESS_CHANGEITEMNAME)) 
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
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getStockQuantityRpcAttempt() < 5)
							getStockQuantity(getStockQuantityRpcAttempt());
						else
							showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						BmoWhStock bmoWhStock = (BmoWhStock)result.getBmObject();
						stockQuantity.setText(bmoWhStock.getQuantity().toString());
						getLockedQuantity();
					}
				};
	
				try {	
					stockQuantity.setText("");
					BmoWhStock bmoWhStock = new BmoWhStock();
					startLoading();
					setStockQuantityRpcAttempt(0);
					getUiParams().getBmObjectServiceAsync().action(bmoWhStock.getPmClass(), bmoWhStock, BmoWhStock.ACTION_STOCKQUANTITY,  productId + "|" + bmoQuote.getCompanyId().toInteger(), callback);
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
		//Obtener la cantidad en cotizacion
		public void getLockedQuantity(int lockedQuantityRpcAttempt) {
			if (lockedQuantityRpcAttempt < 5) {
				setLockedQuantityRpcAttempt(lockedQuantityRpcAttempt + 1);
				
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getLockedQuantityRpcAttempt() < 5)
							getLockedQuantity(getLockedQuantityRpcAttempt());
						else 
							showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						BmoQuoteItem bmoQuoteItem = (BmoQuoteItem)result.getBmObject();
						lockedQuantity.setText(bmoQuoteItem.getQuantity().toString());			
						getPrice();
					}
				};
	
				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoQuote.getPmClass(), bmoQuote, BmoQuote.ACTION_LOCKEDQUANTITY,  productId, callback);
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
							showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + caught.toString());
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
						bmoProductPrice.getStartDate().setValue(bmoQuote.getStartDate().toString());
						bmoProductPrice.getProductId().setValue(productSuggestBox.getSelectedId());
						bmoProductPrice.getCurrencyId().setValue(bmoQuote.getCurrencyId().toInteger());
						bmoProductPrice.getOrderTypeId().setValue(bmoQuote.getOrderTypeId().toInteger());
						bmoProductPrice.getWFlowTypeId().setValue(bmoOpportunity.getForeignWFlowTypeId().toInteger());
						bmoProductPrice.getMarketId().setValue(bmoQuote.getMarketId().toInteger());
						bmoProductPrice.getCompanyId().setValue(bmoQuote.getCompanyId().toInteger());
	
						startLoading();
						getUiParams().getBmObjectServiceAsync().action(bmoProductPrice.getPmClass(), bmoProductPrice, BmoProductPrice.ACTION_GETPRICE,  productId, callback);
					}
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + e.toString());
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
						bmoProductCompany.getCompanyId().setValue(bmoQuote.getCompanyId().toString());
	
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
		private void setBudgetItemAndArea(BmoProductCompany bmoProductCompany, boolean defaultQuote) {
			
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				// Colocar partida y dpto de la cotizacion
				if (defaultQuote) {
					if (bmoQuote.getBudgetItemId().toInteger() > 0)
						budgetItemUiListBox.setSelectedId("" + bmoQuote.getBudgetItemId().toInteger());
					if (bmoQuote.getAreaId().toInteger() > 0)
						areaUiListBox.setSelectedId("" + bmoQuote.getAreaId().toInteger());
				} else {
					// Colocar partida/dpto. de la empresa del producto
					if (bmoProductCompany.getId() > 0) {
						if (bmoProductCompany.getBudgetItemId().toInteger() > 0)
							budgetItemUiListBox.setSelectedId("" + bmoProductCompany.getBudgetItemId().toInteger());
						if (bmoProductCompany.getAreaId().toInteger() > 0)
							areaUiListBox.setSelectedId("" + bmoProductCompany.getAreaId().toInteger());
					} 
					// Colocar partida/dpto. del producto(no tiene empresas), si no tiene datos regresar por defecto al de la cotizacion
					else {
						BmoProduct bmoProduct = new BmoProduct();
						bmoProduct = ((BmoProduct)productSuggestBox.getSelectedBmObject());
						if (bmoProduct.getBudgetItemId().toInteger() > 0) 
							budgetItemUiListBox.setSelectedId("" + bmoProduct.getBudgetItemId().toInteger());
						else {
							if (bmoQuote.getBudgetItemId().toInteger() > 0) 
								budgetItemUiListBox.setSelectedId("" + bmoQuote.getBudgetItemId().toInteger());
						}
						if (bmoProduct.getAreaId().toInteger() > 0) 
							areaUiListBox.setSelectedId("" + bmoProduct.getAreaId().toInteger());
						else {
							if (bmoQuote.getAreaId().toInteger() > 0) 
								areaUiListBox.setSelectedId("" + bmoQuote.getAreaId().toInteger());
						}
					}
				}
			}
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) 
				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
			else {
				quoteItemDialogBox.hide();
				updateQuoteGroup(quoteGroupListBox.getSelectedId());
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteItem = new BmoQuoteItem();
				bmoQuoteItem.getQuoteGroupId().setValue(selectedQuoteGroupId);
				bmoQuoteItem.getProductId().setValue(productSuggestBox.getSelectedId());
				bmoQuoteItem.getName().setValue(nameTextBox.getText());
				bmoQuoteItem.getDescription().setValue(descriptionTextArea.getText());
				bmoQuoteItem.getQuantity().setValue(quantityTextBox.getText());
				bmoQuoteItem.getDays().setValue(daysTextBox.getText());
				bmoQuoteItem.getPrice().setValue(priceTextBox.getText());
				bmoQuoteItem.getCommission().setValue(commissionCheckBox.getValue());
				bmoQuoteItem.getQuoteGroupId().setValue(quoteGroupListBox.getSelectedId());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
					bmoQuoteItem.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
					bmoQuoteItem.getAreaId().setValue(areaUiListBox.getSelectedId());
				}
				
				if (!quoteGroupListBox.getSelectedId().equals(""))
					selectedQuoteGroupId = Integer.parseInt(quoteGroupListBox.getSelectedId());

				// Si no tiene permisos para agregar items sin producto, no permite avanzar
				if (!(bmoQuoteItem.getProductId().toInteger() > 0)
						&& !getSFParams().hasSpecialAccess(BmoQuote.ACCESS_NOPRODUCTITEM))
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
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-save(): ERROR " + caught.toString());
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteItem.getPmClass(), bmoQuoteItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
			}
		}
	}

	// Agrega un item de un kit a un grupo de la cotizacion
	private class UiQuoteKitItemForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private UiListBox quoteGroupListBox;
		private BmoQuoteItem bmoQuoteItem;
		private BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private BmoProductKitItem bmoProductKitItem;
		//		private int selectedQuoteGroupId;

		public UiQuoteKitItemForm(UiParams uiParams, Panel defaultPanel, int quoteId, BmoProductKitItem bmoProductKitItem, int selectedQuoteGroupId) {
			super(uiParams, defaultPanel);
			this.bmoQuoteItem = new BmoQuoteItem();
			this.bmoProductKitItem = bmoProductKitItem;

			try {
				bmoQuoteItem.getQuoteGroupId().setValue(selectedQuoteGroupId);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "() ERROR: " + e.toString());
			}

			saveButton.setStyleName("formSaveButton");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					prepareSave();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoQuoteGroup.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			BmFilter filterQuoteGroups = new BmFilter();
			filterQuoteGroups.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId(), quoteId);
			quoteGroupListBox = new UiListBox(getUiParams(), bmoQuoteGroup, filterQuoteGroups);

			defaultPanel.add(formTable);
		}

		public void show(){
			formTable.addLabelField(1, 0, bmoProductKitItem.getBmoProduct().getName());
			formTable.addLabelField(3, 0, bmoProductKitItem.getQuantity());
			// Si es de renta, mostrar días
			if (bmoQuote.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) 
				formTable.addLabelField(4, 0, bmoProductKitItem.getDays());
			formTable.addField(5, 0, quoteGroupListBox, bmoQuoteItem.getQuoteGroupId());
			formTable.addButtonPanel(buttonPanel);
		}

		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				quoteItemDialogBox.hide();
				updateQuoteGroup(quoteGroupListBox.getSelectedId());
				reset();
			}
		}

		public void prepareSave() {
			try {
				bmoQuoteItem = new BmoQuoteItem();
				bmoQuoteItem.getQuoteGroupId().setValue(quoteGroupListBox.getSelectedId());
				bmoQuoteItem.getProductId().setValue(bmoProductKitItem.getBmoProduct().getId());
				bmoQuoteItem.getQuantity().setValue(bmoProductKitItem.getQuantity().toDouble());
				bmoQuoteItem.getDays().setValue(bmoProductKitItem.getDays().toInteger());
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
					getUiParams().getBmObjectServiceAsync().save(bmoQuoteItem.getPmClass(), bmoQuoteItem, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
			}
		}
	}

	// Agrega un kit completo a un grupo de la cotizacion
	private class UiQuoteKitForm extends Ui {
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
		private BmoQuoteItem bmoQuoteItem;
		private BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		private Button saveButton = new Button("AGREGAR");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiListBox productKitListBox = new UiListBox(getUiParams(), new BmoProductKit());
		private BmoProductKit bmoProductKit;
		private TextArea descriptionTextArea = new TextArea();
		private TextBox amountTextBox  = new TextBox();
		private int quoteId;

		public UiQuoteKitForm(UiParams uiParams, Panel defaultPanel, int quoteId, BmoProductKit bmoProductKit) {
			super(uiParams, defaultPanel);
			this.bmoQuoteItem = new BmoQuoteItem();
			this.bmoProductKit = bmoProductKit;
			this.quoteId = quoteId;

			try {
				bmoQuoteItem.getQuoteGroupId().setValue(selectedQuoteGroupId);
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
					prepareAddKitAction();
				}
			});
			saveButton.setEnabled(false);
			if (getSFParams().hasWrite(bmoQuoteGroup.getProgramCode())) saveButton.setEnabled(true);
			buttonPanel.add(saveButton);

			defaultPanel.add(formTable);
		}

		public void show(){
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
				bmoQuoteGroup = new BmoQuoteGroup();
				bmoQuoteGroup.getName().setValue(bmoProductKit.getName().toString());
				bmoQuoteGroup.getDescription().setValue(bmoProductKit.getDescription().toString());
				bmoQuoteGroup.getAmount().setValue(bmoProductKit.getAmount().toDouble());
				bmoQuoteGroup.getImage().setValue(bmoProductKit.getImage().toString());
				bmoQuoteGroup.getShowProductImage().setValue(false);
				bmoQuoteGroup.getShowGroupImage().setValue(true);
				bmoQuoteGroup.getIsKit().setValue(true);
				bmoQuoteGroup.getShowQuantity().setValue(true);
				bmoQuoteGroup.getShowPrice().setValue(false);
				bmoQuoteGroup.getShowAmount().setValue(true);
				bmoQuoteGroup.getQuoteId().setValue(quoteId);
				addKitAction();
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareAddKitAction() ERROR: " + e.toString());
			}
		}

		public void addKitAction() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					processAddKitActionUpdateResult(result);
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoQuoteGroup.getPmClass(), bmoQuoteGroup, BmoQuoteGroup.ACTION_PRODUCTKIT, "" + bmoProductKit.getId(), callback);	
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-addKitAction() ERROR: " + e.toString());
			}
		}

		public void processAddKitActionUpdateResult(BmUpdateResult bmUpdateResult) {
			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processAddKitActionUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
			else {
				quoteItemDialogBox.hide();
				listQuoteGroups();
				reset();
			}
		}
	}
	
	// Variables para llamadas RPC
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
	
	public int getSaveShowChangeRpcAttempt() {
		return saveShowChangeRpcAttempt;
	}

	public void setSaveShowChangeRpcAttempt(int saveShowChangeRpcAttempt) {
		this.saveShowChangeRpcAttempt = saveShowChangeRpcAttempt;
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

	protected class QuoteUpdater {

		public void changeQuote() {
			stopLoading();
			reset();
		}

		public void updateQuoteGroups() {
			stopLoading();
			listQuoteGroups();
		}

		public void changeQuoteEquipment(){
			stopLoading();
			reset();
		}

		public void changeQuoteStaff(){
			stopLoading();
			reset();
		}

		public void changeQuoteProperty(){
			stopLoading();
			reset();
		}

		public void changeQuotePropertyModelExtra(){
			stopLoading();
			reset();
		}
	}
}
