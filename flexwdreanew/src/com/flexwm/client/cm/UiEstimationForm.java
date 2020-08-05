///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.client.cm;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import com.flexwm.client.op.UiProductTree;
//import com.flexwm.shared.BmoFlexConfig;
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoCustomerContact;
//import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.fi.BmoBudgetItem;
//import com.flexwm.shared.fi.BmoBudgetItemType;
//import com.flexwm.shared.fi.BmoCurrency;
//import com.flexwm.shared.cm.BmoEstimation;
//import com.flexwm.shared.cm.BmoEstimationGroup;
//import com.flexwm.shared.cm.BmoEstimationRFQItem;
//import com.flexwm.shared.op.BmoOrderType;
//import com.flexwm.shared.op.BmoProduct;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.cellview.client.CellBrowser;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.CheckBox;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Panel;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.google.gwt.view.client.SelectionChangeEvent;
//import com.google.gwt.view.client.SingleSelectionModel;
//import com.symgae.client.ui.Ui;
//import com.symgae.client.ui.UiDateBox;
//import com.symgae.client.ui.UiDateTimeBox;
//import com.symgae.client.ui.UiForm;
//import com.symgae.client.ui.UiFormFlexTable;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.client.ui.UiSuggestBoxAction;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.GwtUtil;
//import com.symgae.shared.SFException;
//import com.symgae.shared.sf.BmoArea;
//import com.symgae.shared.sf.BmoCompany;
//import com.symgae.shared.sf.BmoUser;
//import com.symgae.shared.sf.BmoProfileUser;
//
//
//public class UiEstimationForm extends UiForm {
//	BmoEstimation bmoEstimation = new BmoEstimation();
//	BmoRFQU bmoRFQU = new BmoRFQU();
//	TextBox codeTextBox = new TextBox();	
//	UiListBox estimationTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
//	TextBox nameTextBox = new TextBox();	
//	TextBox amountTextBox = new TextBox();
//	TextArea descriptionTextArea = new TextArea();
//	TextArea commentsTextArea = new TextArea();
//	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
//	TextBox currencyParityTextBox = new TextBox();
//	CheckBox coveregeParityCheckBox = new CheckBox();
//	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
////	UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
//	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
//	UiSuggestBox userSuggestBox;
//	TextBox taxTextBox = new TextBox();
//
//	TextBox totalTextBox = new TextBox();
//	UiDateBox startDateBox = new UiDateBox();
//	UiDateTimeBox endDateTimeBox = new UiDateTimeBox();	
//	UiListBox statusListBox = new UiListBox(getUiParams());
//	UiListBox customerContactIdListBox = new UiListBox(getUiParams(), new BmoCustomerContact());
//	//	UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
//	//	UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
//
//	protected FlowPanel formatPanel;
//
//	// Botones
//	private Button addEstimationItemButton = new Button("+ITEM");
//	private FlowPanel estimationButtonPanel = new FlowPanel();
//	// Estimation Groups
//	BmoEstimationGroup bmoEstimationGroup = new BmoEstimationGroup();
//	private HashMap<String, UiEstimationGroupGrid> estimationGroupUiMap = new HashMap<String, UiEstimationGroupGrid>();
//	private FlowPanel estimationGroupsPanel = new FlowPanel();
//	private EstimationUpdater estimationUpdater = new EstimationUpdater();
//	private int selectedEstimationGroupId;
//	private Button addEstimationGroupButton = new Button("+GRUPO");
//	private BmFilter bmoFilterEstimationGroup;
//	protected DialogBox estimationGroupDialogBox;
//	private int estimationIdRpc = 0;
//	private int estimationIdRpcAttempt = 0;
//	private int estimationGroupsRpcAttempt = 0;
//	private int parityFromCurrencyRpcAttempt = 0;
//	private String currencyId;
//	private int saveAmountChangeRpcAttempt = 0;
//	private int stockQuantityRpcAttempt = 0;
//	private int lockedQuantityRpcAttempt = 0;
//	private int saveShowChangeRpcAttempt = 0;
//	private int priceRpcAttempt = 0;
//	private int productCompanySpecificRpcAttempt = 0;
//
//	// EstimationItems
//	protected DialogBox estimationItemDialogBox;
//
//	// Browser de productos
//	public final SingleSelectionModel<BmObject> productSelection = new SingleSelectionModel<BmObject>();
//	private UiProductTree uiProductTree = new UiProductTree(productSelection);
//	private CellBrowser productCellBrowser;
//	private FlowPanel productCellPanel = new FlowPanel();
//
//	// Copiar cotizacion
//	private Button copyEstimationButton = new Button("COPIAR");
//	//	private Button copyEstimationDialogButton = new Button("COPIAR ITEMS");
//	private Button copyEstimationCloseDialogButton = new Button("CERRAR");
//	protected DialogBox copyEstimationDialogBox;
//	UiSuggestBox copyEstimationSuggestBox = new UiSuggestBox(new BmoEstimation());
//
//	// SECCIONES
//	private String generalSection = "Datos Generales";
//	//	private String productTreeSection = "Navegador de Productos";
//	private String itemSection = "Items";
//	//	private String equipmentSection = "Recursos";
//	//	private String staffSection = "Colaboradores";
//	//	private String paritySection = "Moneda y Paridad";
//	private String statusSection = "Estatus";
//
//
//	public UiEstimationForm(UiParams uiParams, int id) {
//		super(uiParams, new BmoEstimation(), id);
//		bmoEstimation = (BmoEstimation)getBmObject();
//		initialize();
//	}
//
//	public void initialize() {
//
//		// Filtrar por vendedores
//		userSuggestBox = new UiSuggestBox(new BmoUser());
//		BmoUser bmoUser = new BmoUser();
//		BmoProfileUser bmoProfileUser = new BmoProfileUser();
//		BmFilter filterSalesmen = new BmFilter();
//		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
//		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
//				bmoUser.getIdFieldName(),
//				bmoProfileUser.getUserId().getName(),
//				bmoProfileUser.getProfileId().getName(),
//				"" + salesGroupId);	
//		userSuggestBox.addFilter(filterSalesmen);
//
//		// Filtrar por vendedores activos
//		BmFilter filterSalesmenActive = new BmFilter();
//		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
//		userSuggestBox.addFilter(filterSalesmenActive);
//
//		// Arbol de seleccion de productos
//		productSelection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//			@Override
//			public void onSelectionChange(SelectionChangeEvent event) {
//				BmObject bmObject = (BmObject)productSelection.getSelectedObject();
//
//				// Se esta agregando un producto directo
//				if (bmObject instanceof BmoProduct) {
//					addProduct((BmoProduct)bmObject);
//				}
//
//			}
//		});
//		productCellBrowser = new CellBrowser.Builder<SingleSelectionModel<BmObject>>(uiProductTree, productSelection).build();
//		productCellBrowser.setSize("100%", "200px");
//		productCellBrowser.setAnimationEnabled(true);
//		productCellPanel.setSize("100%", "200px");
//		productCellPanel.add(productCellBrowser);
//		addEstimationGroupButton.setStyleName("formSaveButton");
//		addEstimationGroupButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				addEstimationGroupDialog();
//			}
//		});
//
//		// Todas las operaciones con los items de productos 
//		addEstimationItemButton.setStyleName("formSaveButton");
//		addEstimationItemButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				addProduct();
//			}
//		});
//
//
//
//		// Copiar cotizacion
//		//		copyEstimationDialogButton.setStyleName("formCloseButton");
//		//		copyEstimationDialogButton.addClickHandler(new ClickHandler() {
//		//			public void onClick(ClickEvent event) {
//		//				copyEstimationDialog();
//		//			}
//		//		});
//
//		copyEstimationCloseDialogButton.setStyleName("formCloseButton");
//		copyEstimationCloseDialogButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				copyEstimationDialogBox.hide();
//			}
//		});
//
//
//		// Panel de staff
//		estimationButtonPanel.setWidth("100%");
//		estimationGroupsPanel.setWidth("100%");
//		//		estimationEquipmentPanel.setWidth("100%");
//		//		estimationStaffPanel.setWidth("100%");
//	}
//
//	public void getRFQUStatus(int estimationIdRp) {
//		getRFQUStatus(estimationIdRp, 0);
//	}
//	public void getRFQUStatus(int estimationIdRp, int estimationIdRpcAttemp) {
//		if (estimationIdRpcAttemp < 5) {	
//			setEstimationIdRpcAttempt(estimationIdRpcAttemp + 1);
//			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//				public void onFailure(Throwable caught) {
//					getRFQUStatus(estimationIdRp) ;
//					showErrorMessage(this.getClass().getName() + "-getRFQUStatus() ERROR: " + caught.toString());	
//				}	
//				public void onSuccess(BmObject result) {
//					stopLoading();
//					bmoRFQU = (BmoRFQU)result;	
//					statusEffectRpc();
//
//				}
//			};
//			try {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().getBy(bmoRFQU.getPmClass(), estimationIdRp, bmoRFQU.getEstimationId().getName(), callback);	
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-getRFQUStatus() ERROR: " + e.toString());
//			}
//		}	
//
//	}
//
//	@Override
//	public void populateFields(){
//		bmoEstimation = (BmoEstimation)getBmObject();
//
//		try {
//
//			// Si no esta asignada la moneda, buscar por la default
//			if (!(bmoEstimation.getCurrencyId().toInteger() > 0))
//				bmoEstimation.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
//
//			if (newRecord) {
//				populateParityFromCurrency(bmoEstimation.getCurrencyId().toString());
//				// Busca Empresa seleccionada por default
//				if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
//					bmoEstimation.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
//			}
//
//			// Filtrar contactos del cliente
//			BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
//			BmFilter contactsByCustomer = new BmFilter();
//			contactsByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoEstimation.getCustomerId().toInteger());
//			customerContactIdListBox.addFilter(contactsByCustomer);
//
//			// Filtro de Grupos de Cotizacion
//			bmoFilterEstimationGroup = new BmFilter();
//			bmoFilterEstimationGroup.setValueFilter(bmoEstimationGroup.getKind(), bmoEstimationGroup.getEstimationId(), bmoEstimation.getId());
//
//		} catch (BmException e) {
//			showSystemMessage("No se puede asignar tipo default : " + e.toString());
//		}
//
//		formFlexTable.addSectionLabel(1, 0, generalSection, 2);
//		formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoEstimation.getCode());
//		formFlexTable.addField(3, 0, estimationTypeListBox, bmoEstimation.getOrderTypeId());	
//		formFlexTable.addField(4, 0, nameTextBox, bmoEstimation.getName());
//		formFlexTable.addField(5, 0, customerSuggestBox, bmoEstimation.getCustomerId());
//		formFlexTable.addField(6, 0, userSuggestBox, bmoEstimation.getUserId());
//		formFlexTable.addField(7, 0, startDateBox, bmoEstimation.getStartDate());
//
//		if (!newRecord) {
//			// Rentas, Ventas, Servicios
//			if (bmoEstimation.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) 
//					|| bmoEstimation.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
//					|| bmoEstimation.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
//				formFlexTable.addSectionLabel(8, 0, itemSection, 2);
//
//				// Solo permitir agregar si no está autorizada
//				if (bmoEstimation.getStatus().toChar() == BmoEstimation.STATUS_PROCESSING) {
//					formFlexTable.addPanel(9, 0, estimationButtonPanel);
//				}
//				estimationButtonPanel.clear();
//				estimationButtonPanel.add(addEstimationGroupButton);
//				estimationButtonPanel.add(addEstimationItemButton);
//				//				estimationButtonPanel.add(copyEstimationDialogButton);
//				formFlexTable.addPanel(10, 0, estimationGroupsPanel, 2);
//			}
//
//			listEstimationGroups();
//			reset();
//			formFlexTable.addSectionLabel(11, 0, statusSection, 2);
//			formFlexTable.addField(12, 0, statusListBox, bmoEstimation.getStatus());
//			
//
//		}
//		statusEffect();
//	}
//
//	
//
//	@Override
//	public BmObject populateBObject() throws BmException {
//		bmoEstimation.setId(id);
//		
//		bmoEstimation.getCode().setValue(codeTextBox.getText());
//		bmoEstimation.getName().setValue(nameTextBox.getText());
//		bmoEstimation.getCustomerId().setValue(customerSuggestBox.getSelectedId());
//
//		bmoEstimation.getUserId().setValue(userSuggestBox.getSelectedId());
//		bmoEstimation.getCurrencyId().setValue(1);
//		bmoEstimation.getCurrencyParity().setValue(currencyParityTextBox.getText());
//		bmoEstimation.getOrderTypeId().setValue(estimationTypeListBox.getSelectedId());
//		bmoEstimation.getDescription().setValue(descriptionTextArea.getText());
//		bmoEstimation.getComments().setValue(commentsTextArea.getText());
//		bmoEstimation.getStatus().setValue(statusListBox.getSelectedCode());
//		bmoEstimation.getCompanyId().setValue(companyListBox.getSelectedId());
////		bmoEstimation.getMarketId().setValue(marketListBox.getSelectedId());
//		//		bmoEstimation.getCustomerContactId().setValue(customerContactIdListBox.getSelectedId());
//		bmoEstimation.getStartDate().setValue(startDateBox.getTextBox().getText());
//		return bmoEstimation;
//	}
//
//	@Override
//	public void saveNext() {
//		if (newRecord || isSingleSlave()) {
//			UiEstimationForm uiEstimationForm = new UiEstimationForm(getUiParams(), getBmObject().getId());
//			uiEstimationForm.show();
//		} else {
//			UiEstimationList uiEstimationList = new UiEstimationList(getUiParams());
//			uiEstimationList.show();
//			
//		}		
//	}
//
//	@Override
//	public void close() {
//		UiEstimationForm uiEstimationForm = new UiEstimationForm(getUiParams(),id);
//		uiEstimationForm.show();
//	}
//
//	@Override
//	public void formListChange(ChangeEvent event) {
//		if (event.getSource() == statusListBox) {			
//			update("Desea cambiar el Status de la Estimación?");
//		} 
//		statusEffect();
//	}
//
//	// Obtener la paridad de la moneda
//	public void populateParityFromCurrency(String currencyId) {
//		getParityFromCurrency(currencyId);
//	}
//
//	private void statusEffect() {
//		getRFQUStatus(bmoEstimation.getId());	
//	}
//
//	private void statusEffectRpc(){
//		startDateBox.setEnabled(false);
//		endDateTimeBox.setEnabled(false);	
//		userSuggestBox.setEnabled(false);
//		if (!newRecord) {
//			nameTextBox.setEnabled(false);
//			estimationTypeListBox.setEnabled(false);
//			currencyListBox.setEnabled(false);
//			currencyParityTextBox.setEnabled(false);
//			userSuggestBox.setEnabled(false);
//			customerSuggestBox.setEnabled(false);
//			companyListBox.setEnabled(false);
////			marketListBox.setEnabled(false);
//			if(bmoEstimation.getStatus().equals(BmoEstimation.STATUS_AUTHORIZED)) {
//				addEstimationGroupButton.setVisible(false);
//				addEstimationItemButton.setVisible(false);
//				
//			}
//		}
//
//		// Obtener tipo de cotizacion
//		BmoOrderType bmoOrderType = (BmoOrderType)estimationTypeListBox.getSelectedBmObject();
//		if (bmoOrderType == null) bmoOrderType = bmoEstimation.getBmoOrderType();
//		if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)) {
//			startDateBox.setEnabled(true);
//			endDateTimeBox.setEnabled(true);
//		}
//		if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
//			//				currencyListBox.setEnabled(true);
//			currencyParityTextBox.setEnabled(true);
//		} else {
//			//				currencyListBox.setEnabled(true);
//			currencyParityTextBox.setEnabled(false);
//		}
//
//
//
//
//		// Si hay seleccion default de empresa, deshabilitar combo
//		if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
//			companyListBox.setEnabled(false);
//
//	}
//	// Obtiene paridad de la moneda, primer intento
//	public void getParityFromCurrency(String currencyId) {
//		getParityFromCurrency(currencyId, 0);
//	}
//
//	public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
//		if (parityFromCurrencyRpcAttempt < 5) {
//			setCurrencyId(currencyId);
//			setParityFromCurrencyRpcAttempt(parityFromCurrencyRpcAttempt + 1);
//
//			BmoCurrency bmoCurrency = new BmoCurrency();
//			String startDate = startDateBox.getTextBox().getText();
//			if (startDateBox.getTextBox().getText().equals("")) {
//				startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
//			}
//
//			String actionValues = currencyId + "|" + startDate;
//
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				@Override
//				public void onFailure(Throwable caught) {
//
//					stopLoading();
//					if (getParityFromCurrencyRpcAttempt() < 5) 
//						getParityFromCurrency(getCurrencyId(), getParityFromCurrencyRpcAttempt());
//					else 
//						showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
//				}
//
//				@Override
//				public void onSuccess(BmUpdateResult result) {				
//					stopLoading();
//					setParityFromCurrencyRpcAttempt(0);
//					if (result.hasErrors())
//						showErrorMessage("Error al obtener el Tipo de Cambio");
//					else
//						currencyParityTextBox.setValue(result.getMsg());
//				}
//			};
//
//			try {	
//				if (!isLoading()) {				
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().action(bmoCurrency.getPmClass(), bmoCurrency, BmoCurrency.ACTION_GETCURRENCYPARITY, "" + actionValues, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + e.toString());
//			}
//		}
//	}
//
//
//	public void reset(){
//		updateAmount(id);
//
//		if (bmoEstimation.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
//			estimationGroupListStatusEffect();
//		}
//
//		//		if (bmoEstimation.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
//		//			estimationEquipmentGrid.show();
//		//			estimationStaffGrid.show();
//		//		}
//	}
//
//	public void updateEstimationGroup(String estimationGroupId) {
//		this.selectedEstimationGroupId = Integer.parseInt(estimationGroupId);
//		estimationGroupUiMap.get("" + estimationGroupId).show();
//	}
//
//	private void estimationGroupListStatusEffect() {
//		ArrayList<UiEstimationGroupGrid> estimationGroupGridList = new ArrayList<UiEstimationGroupGrid>(estimationGroupUiMap.values());
//
//		Iterator<UiEstimationGroupGrid> estimationGroupIterator = estimationGroupGridList.iterator();
//		while (estimationGroupIterator.hasNext()) {
//			((UiEstimationGroupGrid)estimationGroupIterator.next()).statusEffect();
//		}
//	}
//
//	public void setUpdateDataEstimation(BmoEstimation bmoEstimation) {
//		nameTextBox.setText(bmoEstimation.getName().toString());
//		companyListBox.setSelectedId(bmoEstimation.getCompanyId().toString());
//		//		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
//		//			budgetItemUiListBox.setSelectedId(bmoEstimation.getBudgetItemId().toString());
//		//			populateBudgetItems(bmoEstimation.getCompanyId().toInteger());
//		//		}
//	}
//	@Override
//	public void postShow() {
//		saveButton.setVisible(false);
//		deleteButton.setVisible(false);
//		closeButton.setVisible(false);
//	}
//
//	// Actualizar montos, 0 intentos
//	public void updateAmount(int estimationIdRpc) {
//		updateAmount(estimationIdRpc, 0);
//	}
//
//	public void updateAmount(int estimationIdRpc, int estimationIdRpcAttempt) {
//		if (estimationIdRpcAttempt < 5) {
//			setEstimationIdRpc(estimationIdRpc);
//			setEstimationIdRpcAttempt(estimationIdRpcAttempt + 1);
//			AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getEstimationIdRpcAttempt() < 5) {
//						updateAmount(getEstimationIdRpc(), getEstimationIdRpcAttempt());
//					} else {
//						showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
//					}
//				}
//
//				public void onSuccess(BmObject result) {
//					stopLoading();
//					setEstimationIdRpcAttempt(0);
//					BmoEstimation bmoEstimation = ((BmoEstimation)result);
//					setUpdateDataEstimation(bmoEstimation);
//				}
//			};
//			try {
//				startLoading();
//				getUiParams().getBmObjectServiceAsync().get(bmoEstimation.getPmClass(), id, callback);
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + e.toString());
//			}
//		}
//	}
//
//	public void saveAmountChange() {
//		saveAmountChange(0);
//	}
//
//	public void saveAmountChange(int saveAmountChangeRpcAttempt) {
//		if (saveAmountChangeRpcAttempt < 5) {
//			setSaveAmountChangeRpcAttempt(saveAmountChangeRpcAttempt + 1);
//
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getSaveAmountChangeRpcAttempt() < 5)
//						saveAmountChange(getSaveAmountChangeRpcAttempt());
//					else
//						showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					stopLoading();
//					setSaveAmountChangeRpcAttempt(0);
//					processEstimationUpdateResult(result);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().save(bmoEstimation.getPmClass(), bmoEstimation, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + e.toString());
//			}
//		}
//	}
//
//	// guardar datos, primer intento
//	public void saveShowChange() {
//		saveShowChange(0);
//	}
//	public void saveShowChange(int saveShowChangeRpcAttempt) {
//		if (saveShowChangeRpcAttempt < 5) {
//			setSaveShowChangeRpcAttempt(saveShowChangeRpcAttempt + 1);
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getSaveShowChangeRpcAttempt() < 5)
//						saveShowChange(getSaveShowChangeRpcAttempt());
//					else
//						showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + caught.toString());
//				}
//
//				public void onSuccess(BmUpdateResult result) {
//					//showSystemMessage("saveShowChange");
//					stopLoading();
//					setSaveShowChangeRpcAttempt(0);
//				}
//			};
//
//			// Llamada al servicio RPC
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().saveSimple(bmoEstimation.getPmClass(), bmoEstimation, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + e.toString());
//			}
//		}
//	}
//
//	public void processEstimationUpdateResult(BmUpdateResult result) {
//		if (result.hasErrors()) 
//			showFormMsg("Error al actualizar descuento.", "Error al actualizar descuento: " + result.errorsToString());
//		else 
//			updateAmount(id);
//	}
//
//	public void processItemChangeSave(BmUpdateResult bmUpdateResult) {
//		if (bmUpdateResult.hasErrors()) 
//			showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
//		this.reset();
//	}
//
//	public void processItemDelete(BmUpdateResult result) {
//		if (result.hasErrors()) showSystemMessage("processStaffDelete() ERROR: " + result.errorsToString());
//		this.reset();
//	}
//
//	public void addEstimationGroup(int estimationGroupId) {
//		VerticalPanel vp = new VerticalPanel();
//		vp.setWidth("100%");
//		estimationGroupsPanel.add(vp);
//
//		UiEstimationGroupGrid estimationGroupGrid = new UiEstimationGroupGrid(getUiParams(), vp, bmoEstimation, estimationGroupId, this.estimationUpdater);
//		estimationGroupGrid.show();
//
//		estimationGroupUiMap.put("" + estimationGroupId, estimationGroupGrid);
//	}
//
//	// Obtiene lista de grupos de pedidos, primer intento
//	public void listEstimationGroups() {
//		listEstimationGroups(0);
//	}
//
//	// Obtiene lista de grupos de rfqu
//	public void listEstimationGroups(int estimationGroupsRpcAttempt) {
//		if (estimationGroupsRpcAttempt < 5) {
//			setEstimationGroupsRpcAttempt(estimationGroupsRpcAttempt + 1);
//
//			AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getEstimationGroupsRpcAttempt() < 5) {
//						listEstimationGroups(getEstimationGroupsRpcAttempt());
//					} else {
//						showErrorMessage(this.getClass().getName() + "-listEstimationGroups() ERROR: " + caught.toString());		
//					}
//				}
//				@Override
//				public void onSuccess(ArrayList<BmObject> result) {
//					stopLoading();
//					setEstimationGroupsRpcAttempt(0);
//					setEstimationGroupList(result);
//				}
//			};
//			try {
//				if (!isLoading()) {
//					startLoading();
//					getUiParams().getBmObjectServiceAsync().list(bmoEstimationGroup.getPmClass(), bmoFilterEstimationGroup, callback);
//				} else {
//					showSystemMessage("No se puede mostrar listado, esta cargando...");
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-listEstimationGroups() ERROR: " + e.toString());
//			}
//		}
//	}
//
//	public void setEstimationGroupList(ArrayList<BmObject> estimationGroupList) {
//		estimationGroupsPanel.clear();
//		estimationGroupUiMap = new HashMap<String, UiEstimationGroupGrid>();
//		Iterator<BmObject> estimationGroupIterator = estimationGroupList.iterator();
//		while (estimationGroupIterator.hasNext()) {
//			BmoEstimationGroup bmoEstimationGroup = (BmoEstimationGroup)estimationGroupIterator.next();
//			addEstimationGroup(bmoEstimationGroup.getId());
//		}
//	}
//
//	
//	public void addProduct(){
//		addProduct(new BmoProduct());
//	}
//
//	public void addEstimationGroupDialog() {
//
//		estimationGroupDialogBox = new DialogBox(true);
//		estimationGroupDialogBox.setGlassEnabled(true);
//		estimationGroupDialogBox.setText("Grupo de Estimación");
//
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("400px", "150px");
//
//		estimationGroupDialogBox.setWidget(vp);
//
//		UiEstimationGroupForm estimationGroupForm = new UiEstimationGroupForm(getUiParams(), vp, bmoEstimation.getId());
//		estimationGroupForm.show();
//
//		estimationGroupDialogBox.center();
//		estimationGroupDialogBox.show();
//	}
//	public void addProduct(BmoProduct bmoProduct) {
//
//		estimationItemDialogBox = new DialogBox(true);
//		estimationItemDialogBox.setGlassEnabled(true);
//		estimationItemDialogBox.setText("Item de Estimación");
//
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("400px", "250px");
//
//		estimationItemDialogBox.setWidget(vp);
//
//		UiEstimationItemForm estimationItemForm = new UiEstimationItemForm(getUiParams(), vp, selectedEstimationGroupId, bmoProduct);
//
//		estimationItemForm.show();
//
//		estimationItemDialogBox.center();
//		estimationItemDialogBox.show();
//	}
//
//
//	public void copyEstimationDialog() {
//		copyEstimationDialogBox = new DialogBox(true);
//		copyEstimationDialogBox.setGlassEnabled(true);
//		copyEstimationDialogBox.setText("Copiar Cotización");
//		copyEstimationDialogBox.setSize("400px", "100px");
//
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("400px", "100px");
//		copyEstimationDialogBox.setWidget(vp);
//
//		UiFormFlexTable formCopyEstimationTable = new UiFormFlexTable(getUiParams());
//		BmoEstimation fromBmoEstimation = new BmoEstimation();
//		formCopyEstimationTable.addField(1, 0, copyEstimationSuggestBox, fromBmoEstimation.getIdField());
//
//		HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
//		changeStaffButtonPanel.add(copyEstimationButton);
//		changeStaffButtonPanel.add(copyEstimationCloseDialogButton);
//
//		vp.add(formCopyEstimationTable);
//		vp.add(changeStaffButtonPanel);
//
//		copyEstimationDialogBox.center();
//		copyEstimationDialogBox.show();
//	}
//
//
//
//	// Variables para llamadas RPC
//	public int getEstimationIdRpc() { // le puse rpc al final para que no haya confusion
//		return estimationIdRpc;
//	}
//
//	public void setEstimationIdRpc(int estimationIdRpc) {
//		this.estimationIdRpc = estimationIdRpc;
//	}
//
//	public int getEstimationIdRpcAttempt() {
//		return estimationIdRpcAttempt;
//	}
//
//	public void setEstimationIdRpcAttempt(int estimationIdRpcAttempt) {
//		this.estimationIdRpcAttempt = estimationIdRpcAttempt;
//	}
//
//	public int getEstimationGroupsRpcAttempt() {
//		return estimationGroupsRpcAttempt;
//	}
//
//	public void setEstimationGroupsRpcAttempt(int estimationGroupsRpcAttempt) {
//		this.estimationGroupsRpcAttempt = estimationGroupsRpcAttempt;
//	}
//
//
//	private class UiEstimationGroupForm extends Ui {
//		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
//		private TextBox nameTextBox = new TextBox();
//		private BmoEstimationGroup bmoEstimationGroup;
//		private Button saveButton = new Button("AGREGAR");
//		private Button closeButton = new Button("CERRAR");
//		private HorizontalPanel buttonPanel = new HorizontalPanel();
//		private int estimationId;
//
//		public UiEstimationGroupForm(UiParams uiParams, Panel defaultPanel, int estimationId) {
//			super(uiParams, defaultPanel);
//			bmoEstimationGroup = new BmoEstimationGroup();
//			this.estimationId = estimationId;
//
//			
//			
//			saveButton.setStyleName("formSaveButton");
//			saveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					prepareSave();
//				}
//			});
//			saveButton.setVisible(false);
//			if (getSFParams().hasWrite(bmoEstimationGroup.getProgramCode())) saveButton.setVisible(true);
//			buttonPanel.add(saveButton);
//
//			closeButton.setStyleName("formCloseButton");
//			closeButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					
//					close();
//				}
//			});
////			buttonPanel.add(closeButton);
//			
//			defaultPanel.add(formTable);
//		}
//
//		public void show(){
//			
//			formTable.addField(1, 0, nameTextBox, bmoEstimationGroup.getName());
//			formTable.addButtonPanel(buttonPanel);
//		}
//
//		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) showErrorMessage(this.getClass().getName() + "-processUpdateResult() ERROR: " + bmUpdateResult.errorsToString());
//			else {
//				estimationGroupDialogBox.hide();
//				listEstimationGroups();
//			}
//		}
//
//		public void prepareSave() {
//			try {
//				bmoEstimationGroup = new BmoEstimationGroup();
//				bmoEstimationGroup.getCode().setValue("");
//				bmoEstimationGroup.getName().setValue(nameTextBox.getText());
//				bmoEstimationGroup.getEstimationId().setValue(estimationId);
//				save();
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-prepareSave() ERROR: " + e.toString());
//			}
//		}
//		
//	
//
//
//		public void save() {
//			// Establece eventos ante respuesta de servicio
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					showErrorMessage(this.getClass().getName() + "-save() ERROR: " + caught.toString());
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
//					getUiParams().getBmObjectServiceAsync().save(bmoEstimationGroup.getPmClass(), bmoEstimationGroup, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-save() ERROR: " + e.toString());
//			}
//		}
//}
//
//	// Agrega un item de un producto a la orden de compra
//	private class UiEstimationItemForm extends Ui {
//		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());
//		private TextBox quantityTextBox = new TextBox();
//		private TextBox nameTextBox = new TextBox();		
//		private BmoEstimationRFQItem bmoEstimationRFQUItem;
//		private Button saveButton = new Button("AGREGAR");
//		private HorizontalPanel buttonPanel = new HorizontalPanel();
//		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
//		private UiListBox estimationGroupListBox;
//		private UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
//		private UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
//		//		String productId = "";
//		//		int estimationGroupId;
//		public UiEstimationItemForm(UiParams uiParams, Panel defaultPanel, int selectedEstimationGroupId, BmoProduct bmoProduct) {
//			super(uiParams, defaultPanel);
//			this.bmoEstimationRFQUItem = new BmoEstimationRFQItem();
//			//			this.estimationGroupId = selectedEstimationGroupId;
//			try {
//				if (selectedEstimationGroupId > 0) bmoEstimationRFQUItem.getEstimationGroupId().setValue(selectedEstimationGroupId);
//				bmoEstimationRFQUItem.getProductId().setValue(bmoProduct.getId());
//				bmoEstimationRFQUItem.getName().setValue("item");
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "(): ERROR " + e.toString());
//			}
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
//			saveButton.setStyleName("formSaveButton");
//			saveButton.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					prepareSave();
//				}
//			});
//			saveButton.setVisible(false);
//			if (getSFParams().hasWrite(bmoEstimation.getProgramCode())) saveButton.setVisible(true);
//			buttonPanel.add(saveButton);
//
//			BmFilter filterEstimationGroups = new BmFilter();
//			filterEstimationGroups.setValueFilter(bmoEstimationGroup.getKind(), bmoEstimationGroup.getEstimationId(), bmoEstimation.getId());
//			estimationGroupListBox = new UiListBox(getUiParams(), bmoEstimationGroup, filterEstimationGroups);
//
//			//filtro para mostrar los equipos que Activos			
//			BmFilter filterByEnabled = new BmFilter();
//			filterByEnabled.setValueFilter(bmoEstimationRFQUItem.getBmoProduct().getKind(),bmoEstimationRFQUItem.getBmoProduct().getEnabled(), "1");
//			productSuggestBox.addFilter(filterByEnabled);
//
//			BmFilter filterByNotSubProduct = new BmFilter();
//			filterByNotSubProduct.setValueOperatorFilter(bmoEstimationRFQUItem.getBmoProduct().getKind(), bmoEstimationRFQUItem.getBmoProduct().getType(), BmFilter.NOTEQUALS , "" + BmoProduct.TYPE_SUBPRODUCT);
//			productSuggestBox.addFilter(filterByNotSubProduct);
//
//
//			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
//				try {
//					if (bmoEstimation.getBudgetItemId().toInteger() > 0)
//						bmoEstimationRFQUItem.getBudgetItemId().setValue(bmoEstimation.getBudgetItemId().toInteger());
//					if (bmoEstimation.getAreaId().toInteger() > 0)
//						bmoEstimationRFQUItem.getAreaId().setValue(bmoEstimation.getAreaId().toInteger());
//				} catch (BmException e) {
//					showErrorMessage(this.getClass().getName() + "(): Error al asginar Partida Presp./Dpto. " + e.toString());
//				}
//			}
//			defaultPanel.add(formTable);
//		}
//
//		public void show(){
//			formTable.addField(1, 0, productSuggestBox, bmoEstimationRFQUItem.getProductId());
//			formTable.addField(2, 0, nameTextBox, bmoEstimationRFQUItem.getName());
//			formTable.addField(3, 0, quantityTextBox, bmoEstimationRFQUItem.getQuantity());		
//			formTable.addField(4, 0, estimationGroupListBox, bmoEstimationRFQUItem.getEstimationGroupId());	
//			setBudgetItemsListBoxFilters(bmoEstimation.getCompanyId().toInteger());
//			formTable.addField(5, 0, budgetItemUiListBox, bmoEstimationRFQUItem.getBudgetItemId());
//			formTable.addField(6, 0, areaUiListBox, bmoEstimationRFQUItem.getAreaId());
//			formTable.addButtonPanel(buttonPanel);
//			statusEffect();
//		}
//
//		// Asigna filtros al listado de partidas presp. por empresa
//		private void setBudgetItemsListBoxFilters(int companyId) {
//			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
//
//			if (companyId > 0) {
//				BmFilter bmFilterByCompany = new BmFilter();
//				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
//				budgetItemUiListBox.addBmFilter(bmFilterByCompany);
//				
//				// Filtro de ingresos(abono)
//				BmFilter filterByDeposit = new BmFilter();
//				filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
//				budgetItemUiListBox.addFilter(filterByDeposit);
//				
//			} else {
//				BmFilter bmFilter = new BmFilter();
//				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
//				budgetItemUiListBox.addBmFilter(bmFilter);
//			}
//		}
//		
//
//		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
//			if (uiSuggestBox == productSuggestBox) {
//				if (productSuggestBox.getSelectedId() > 0) {
//				}
//				statusEffect();
//			}
//		}
//
//		public void statusEffect() {
//			
//			nameTextBox.setText("");
//			nameTextBox.setEnabled(false);
//			if (productSuggestBox.getSelectedId() > 0) {
//				nameTextBox.setText("" + ((BmoProduct)productSuggestBox.getSelectedBmObject()).getName().toString());
//
//				//				productId = "" + productSuggestBox.getSelectedId();
//			} else {
//				nameTextBox.setText("item");
//				nameTextBox.setEnabled(true);
//			}
//			nameTextBox.setFocus(true);
//		}
//		public void processUpdateResult(BmUpdateResult bmUpdateResult) {
//			if (bmUpdateResult.hasErrors()) 
//				showSystemMessage("Error al modificar Item: " + bmUpdateResult.errorsToString());
//			else {
//				estimationItemDialogBox.hide();
//				updateEstimationGroup(estimationGroupListBox.getSelectedId());
//				reset();
//			}
//		}
//
//		public void prepareSave() {		
//
//			try {
//				bmoEstimationRFQUItem.getEstimationGroupId().setValue(selectedEstimationGroupId);
//				bmoEstimationRFQUItem = new BmoEstimationRFQItem();
//				bmoEstimationRFQUItem.getProductId().setValue(productSuggestBox.getSelectedId());
//				bmoEstimationRFQUItem.getName().setValue(nameTextBox.getText());
//				bmoEstimationRFQUItem.getQuantity().setValue(quantityTextBox.getText());
//				bmoEstimationRFQUItem.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
//				bmoEstimationRFQUItem.getAreaId().setValue(areaUiListBox.getSelectedId());
//				bmoEstimationRFQUItem.getEstimationGroupId().setValue(estimationGroupListBox.getSelectedId());
//
//				if (!estimationGroupListBox.getSelectedId().equals(""))
//					selectedEstimationGroupId= Integer.parseInt(estimationGroupListBox.getSelectedId());
//					save();
//
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
//					getUiParams().getBmObjectServiceAsync().save(bmoEstimationRFQUItem.getPmClass(), bmoEstimationRFQUItem, callback);
//				}
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-save(): ERROR " + e.toString());
//			}
//		}
//	}
//
//	// Variables para llamadas RPC
//	public int getParityFromCurrencyRpcAttempt() {
//		return parityFromCurrencyRpcAttempt;
//	}
//
//	public void setParityFromCurrencyRpcAttempt(int parityFromCurrencyRpcAttempt) {
//		this.parityFromCurrencyRpcAttempt = parityFromCurrencyRpcAttempt;
//	}
//
//	public String getCurrencyId() {
//		return currencyId;
//	}
//
//	public void setCurrencyId(String currencyId) {
//		this.currencyId = currencyId;
//	}
//
//	public int getSaveAmountChangeRpcAttempt() {
//		return saveAmountChangeRpcAttempt;
//	}
//
//	public void setSaveAmountChangeRpcAttempt(int saveAmountChangeRpcAttempt) {
//		this.saveAmountChangeRpcAttempt = saveAmountChangeRpcAttempt;
//	}
//
//	public int getStockQuantityRpcAttempt() {
//		return stockQuantityRpcAttempt;
//	}
//
//	public void setStockQuantityRpcAttempt(int stockQuantityRpcAttempt) {
//		this.stockQuantityRpcAttempt = stockQuantityRpcAttempt;
//	}
//
//	public int getLockedQuantityRpcAttempt() {
//		return lockedQuantityRpcAttempt;
//	}
//
//	public void setLockedQuantityRpcAttempt(int lockedQuantityRpcAttempt) {
//		this.lockedQuantityRpcAttempt = lockedQuantityRpcAttempt;
//	}
//
//	public int getSaveShowChangeRpcAttempt() {
//		return saveShowChangeRpcAttempt;
//	}
//
//	public void setSaveShowChangeRpcAttempt(int saveShowChangeRpcAttempt) {
//		this.saveShowChangeRpcAttempt = saveShowChangeRpcAttempt;
//	}
//
//	public int getPriceRpcAttempt() {
//		return priceRpcAttempt;
//	}
//
//	public void setPriceRpcAttempt(int priceRpcAttempt) {
//		this.priceRpcAttempt = priceRpcAttempt;
//	}
//
//	public int getProductCompanySpecificRpcAttempt() {
//		return productCompanySpecificRpcAttempt;
//	}
//
//	public void setProductCompanySpecificRpcAttempt(int productCompanySpecificRpcAttempt) {
//		this.productCompanySpecificRpcAttempt = productCompanySpecificRpcAttempt;
//	}
//
//	protected class EstimationUpdater {
//
//		public void changeEstimation() {
//			stopLoading();
//			reset();
//		}
//
//		public void updateEstimationGroups() {
//			stopLoading();
//			listEstimationGroups();
//		}
//
//		public void changeEstimationEquipment(){
//			stopLoading();
//			reset();
//		}
//
//		public void changeEstimationStaff(){
//			stopLoading();
//			reset();
//		}
//
//		public void changeEstimationProperty(){
//			stopLoading();
//			reset();
//		}
//
//		public void changeEstimationPropertyModelExtra(){
//			stopLoading();
//			reset();
//		}
//	}
//}