/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import java.util.ArrayList;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import com.flexwm.client.op.UiOrderDetail;
import com.flexwm.client.op.UiOrderList;
import com.flexwm.client.op.UiOrderLock;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderGroup;
import com.flexwm.shared.op.BmoOrderItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductKit;
import com.flexwm.shared.op.BmoProductKitItem;
import com.flexwm.shared.op.BmoWhStock;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowType;


public class UiOrderFormSession extends UiForm {
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
	UiSuggestBox userSuggestBox;
	UiTagBox tagBox = new UiTagBox(getUiParams());
	TextBox taxTextBox = new TextBox();
	TextBox totalTextBox = new TextBox();
	UiDateTimeBox lockStartDateTimeBox = new UiDateTimeBox();
	UiDateTimeBox lockEndDateTimeBox = new UiDateTimeBox();	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());

	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	TextBox currencyParityTextBox = new TextBox();
	CheckBox coveregeParityCheckBox = new CheckBox();
	TextBox customerRequisitionTextBox = new TextBox();	

	protected FlowPanel formatPanel;

	// Botones
	private FlowPanel orderButtonPanel = new FlowPanel();
	private Button extrasButton = new Button("Extras");

	// Order Groups
	BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
	private HashMap<String, UiOrderSessionGroupGrid> orderGroupUiMap = new HashMap<String, UiOrderSessionGroupGrid>();
	private FlowPanel orderGroupsPanel = new FlowPanel();
	private OrderSessionUpdater orderSessionUpdater = new OrderSessionUpdater();
	private int selectedOrderGroupId;
	private BmFilter bmoFilterOrderGroup;
	protected DialogBox orderGroupDialogBox;

	// OrderItems
	protected DialogBox orderItemDialogBox;
	

	// Paquetes sesiones
	private UiOrderSessionTypePackageGrid orderSessionTypePackageGrid;
	private FlowPanel orderSessionTypePackagePanel = new FlowPanel();
	
	//Extras de la sesion
	private UiOrderSessionExtraGrid orderSessionExtraGrid;
	private FlowPanel orderSessionExtraPanel = new FlowPanel();
	
	//Pagos
	private UiOrderSessionFormPayment uiOrderSessionFormPayment;
	private FlowPanel orderSessionFormPaymentPanel = new FlowPanel();

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
	
	// Dialogo Pagar el Pedido
	DialogBox orderPaymentDialogBox;


	public UiOrderFormSession(UiParams uiParams, int id) {
		super(uiParams, new BmoOrder(), id);
		bmoOrder = (BmoOrder)getBmObject();
		initialize();
	}

	public void initialize() {		
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
			ValueChangeHandler<String> currencyParityTextBoxChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					coverageParity();
				}
			};
			currencyParityTextBox.addValueChangeHandler(currencyParityTextBoxChangeHandler);
		}
		
		// Boton de mostrar descuentos
		extrasButton.setStyleName("formCloseButton");
		extrasButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleExtras();
			}
		});

		// Panel
		orderButtonPanel.setWidth("100%");
		orderGroupsPanel.setWidth("100%");
		orderSessionTypePackagePanel.setWidth("100%");

		// Agregar filtros al tipo de flujo
		bmoWFlowType = new BmoWFlowType();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
		wFlowTypeListBox = new UiListBox(getUiParams(), bmoWFlowType, bmFilter);
	}

	@Override
	public void populateFields() {		
		bmoOrder = (BmoOrder)getBmObject();

		try {
			if (newRecord) {
				// Asigna empresa si es registro nuevo
				if (!(bmoOrder.getCompanyId().toInteger() > 0)) {	
					bmoOrder.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());
				}

				// Busca Empresa seleccionada por default
				if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
					bmoOrder.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
			}

			// Si no esta asignado el tipo, buscar por el default
			if (!(bmoOrder.getOrderTypeId().toString().length() > 0)) {
				bmoOrder.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
			}

			// Si no esta asignado el vendedor, buscar por el usuario loggeado
			if (!(bmoOrder.getUserId().toInteger() > 0)) {
				bmoOrder.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
			}

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoOrder.getCurrencyId().toInteger() > 0)) {
				bmoOrder.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger());					
				currencyListBox.setSelectedId(bmoOrder.getCurrencyId().toString());
			}

		} catch (BmException e) {
			showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
		}	

		formFlexTable.addSectionLabel(1, 0, generalSection, 2);
		formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoOrder.getCode());
		formFlexTable.addField(3, 0, orderTypeListBox, bmoOrder.getOrderTypeId());	
		formFlexTable.addField(4, 0, nameTextBox, bmoOrder.getName());
		formFlexTable.addField(5, 0, wFlowTypeListBox, bmoOrder.getWFlowTypeId());
		formFlexTable.addField(6, 0, customerSuggestBox, bmoOrder.getCustomerId());
		//populateCustomerContacts(bmoOrder.getCustomerId().toInteger());
		formFlexTable.addField(7, 0, userSuggestBox, bmoOrder.getUserId());
		formFlexTable.addField(8, 0, lockStartDateTimeBox, bmoOrder.getLockStart());
		formFlexTable.addField(9, 0, lockEndDateTimeBox, bmoOrder.getLockEnd());
		formFlexTable.addField(10, 0, companyListBox, bmoOrder.getCompanyId());
		formFlexTable.addField(11, 0, currencyListBox, bmoOrder.getCurrencyId());
		formFlexTable.addField(12, 0, currencyParityTextBox, bmoOrder.getCurrencyParity());

		//		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_PROPERTY)) {
		//			formFlexTable.addField(13, 0, commissionListBox, bmoOrder.getOrderCommissionId());
		//		}

		if (!newRecord) {
			formFlexTable.addField(16, 0, tagBox, bmoOrder.getTags());

			bmoFilterOrderGroup = new BmFilter();
			bmoFilterOrderGroup.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId(), bmoOrder.getId());

			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) 
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {

				
				formFlexTable.addSectionLabel(19, 0, itemsSection, 2);

				// Solo permitir agregar si no está autorizada
				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
					formFlexTable.addPanel(20, 0, orderButtonPanel);
				}

				orderButtonPanel.clear();				

				formFlexTable.addPanel(21, 0, orderGroupsPanel);
			} else {
				formFlexTable.addSectionLabel(17, 0, itemsSection, 2);
			}
			
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
				formFlexTable.addPanel(22, 0, orderSessionTypePackagePanel,2);				
				orderSessionTypePackageGrid = new UiOrderSessionTypePackageGrid(getUiParams(), orderSessionTypePackagePanel, bmoOrder, orderSessionUpdater);
				orderSessionTypePackageGrid.show();
				
				// Solo permitir agregar si no está autorizada
				if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) {
					formFlexTable.addPanel(23, 0, orderButtonPanel);
				}

				formFlexTable.addPanel(24, 0, orderGroupsPanel, 2);
				
				orderButtonPanel.clear();
			}
			
			formFlexTable.addPanel(25, 0, orderSessionExtraPanel, 2);
			orderSessionExtraGrid = new UiOrderSessionExtraGrid(getUiParams(), orderSessionExtraPanel, bmoOrder, orderSessionUpdater);
			orderSessionExtraGrid.show();
			
			
			//Totales
			formFlexTable.addFieldReadOnly(26, 0, amountTextBox, bmoOrder.getAmount());			
			formFlexTable.addField(27, 0, discountTextBox, bmoOrder.getDiscount());
			formFlexTable.addButtonCell(28, 0, extrasButton);
			if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT))
				formFlexTable.addField(29, 0, taxAppliesCheckBox, bmoOrder.getTaxApplies());
			formFlexTable.addFieldReadOnly(30, 0, taxTextBox, bmoOrder.getTax());
			formFlexTable.addFieldReadOnly(31, 0, totalTextBox, bmoOrder.getTotal());
			formFlexTable.addField(32, 0, descriptionTextArea, bmoOrder.getDescription());
			formFlexTable.addField(33, 0, commentsTextArea, bmoOrder.getComments());
			
			//Estatus
			formFlexTable.addLabelField(34, 0, bmoOrder.getLockStatus());
			formFlexTable.addLabelField(35, 0, bmoOrder.getPaymentStatus());
			formFlexTable.addLabelField(36, 0, bmoOrder.getDeliveryStatus());
			formFlexTable.addField(37, 0, statusListBox, bmoOrder.getStatus());
			
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
				formFlexTable.addPanel(38, 0, orderSessionFormPaymentPanel, 2);
				uiOrderSessionFormPayment = new UiOrderSessionFormPayment(getUiParams(), orderSessionFormPaymentPanel, bmoOrder, orderSessionUpdater);
				uiOrderSessionFormPayment.show();
			}	

			// Si es de tipo renta, y está habilitado el bloqueo, mostrar asignacion
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) &&
					((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableOrderLock().toBoolean()) {
				TabLayoutPanel tabPanel = new TabLayoutPanel(2.5, Unit.EM);
				tabPanel.setSize("100%", "300px");
				formFlexTable.addPanel(39, 0, tabPanel);

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
			formFlexTable.addField(11, 0, currencyListBox, bmoOrder.getCurrencyId());
			populateParityFromCurrency(bmoOrder.getCurrencyId().toString());
			formFlexTable.addField(12, 0, currencyParityTextBox, bmoOrder.getCurrencyParity());

			formFlexTable.addField(15, 0, statusListBox, bmoOrder.getStatus());
			formFlexTable.addField(16, 0, tagBox, bmoOrder.getTags());
		}

		// Oculta secciones
		if (!newRecord) {
			formFlexTable.hideSection(generalSection);
		}

		formFlexTable.hideSection(productsSection);

		statusEffect();
	}

	@Override
	public void postShow() {
			
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
		bmoOrder.getTags().setValue(tagBox.getTagList());
		bmoOrder.getComments().setValue(commentsTextArea.getText());
		bmoOrder.getCoverageParity().setValue(coveregeParityCheckBox.getValue());
		return bmoOrder;
	}
	
	@Override
	public void saveNext() {
		if (newRecord || isSingleSlave()) {
			UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), getBmObject().getId());
			uiOrderDetail.show();
		} else {
			if (!newRecord) {
				if (isSlave()) {
					UiOrderDetail uiOrderDetail = new UiOrderDetail(getUiParams(), getBmObject().getId());
					uiOrderDetail.show();
				} else {
					UiOrderFormSession uiOrderFormSession = new UiOrderFormSession(getUiParams(), getBmObject().getId());
					uiOrderFormSession.show();
				}
			} else {
				UiOrderList uiOrderList = new UiOrderList(getUiParams());
				uiOrderList.show();
			}	
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

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == statusListBox)
			update("Desea cambiar el Status del Pedido?");
		else if (event.getSource() == currencyListBox) {
			getParityFromCurrency(currencyListBox.getSelectedId());
		} 
		statusEffect();
	}	

	@Override
	public void formDateChange(ValueChangeEvent<Date> event) {
		if (event.getSource() == lockStartDateTimeBox) {
			populateParityFromCurrency(currencyListBox.getSelectedId());
		}
	}

	// Accion cambio SuggestBox
	@Override
	public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
		// Filtros de requisiones
		if(uiSuggestBox == customerSuggestBox) {
			BmoCustomer bmoCustomer = new BmoCustomer();
			bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
			currencyListBox.setSelectedId("" + bmoCustomer.getCurrencyId().toInteger());
			populateParityFromCurrency(bmoCustomer.getCurrencyId().toString());
			statusEffect();
		}
	}

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
		

		if (!newRecord) {
			orderTypeListBox.setEnabled(false);			
			userSuggestBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			//			commissionListBox.setEnabled(false);	
			companyListBox.setEnabled(false);

			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_REVISION)) {
				currencyParityTextBox.setEnabled(true);
				currencyListBox.setEnabled(true);
				coveregeParityCheckBox.setEnabled(true);
			} else if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {				
				if (!bmoOrder.getPaymentStatus().equals(BmoOrder.PAYMENTSTATUS_TOTAL)) {
					//paymentButton.setVisible(true);
				}
			} 

		} else {
			currencyListBox.setEnabled(true);
			userSuggestBox.setEnabled(true);
			wFlowTypeListBox.setEnabled(true);
			coveregeParityCheckBox.setEnabled(true);
		}

		// Si hay valor en IVA poner seleccionado el boton
		if (bmoOrder.getTaxApplies().toBoolean()) taxAppliesCheckBox.setValue(true);
		else taxAppliesCheckBox.setValue(false);
		if (bmoOrder.getStatus().toChar() == BmoOrder.STATUS_REVISION) taxAppliesCheckBox.setEnabled(true);
		else taxAppliesCheckBox.setEnabled(false);

		// Obtener tipo de pedido
		BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
		if (bmoOrderType == null) bmoOrderType = bmoOrder.getBmoOrderType();

		if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_RENTAL)
				|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_CONSULTANCY)
				|| bmoOrderType.getType().equals("" + BmoOrderType.TYPE_SALE)) {
			lockStartDateTimeBox.setEnabled(true);
			lockEndDateTimeBox.setEnabled(true);
		}

		// Si tiene descuento, mostrarlo
		if (bmoOrder.getDiscount().toDouble() > 0)
			discountTextBox.setVisible(true);

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
			customerRequisitionTextBox.setEnabled(false);
			statusListBox.setEnabled(false);

			showEquipmentQuantityCheckBox.setEnabled(false);
			showEquipmentPriceCheckBox.setEnabled(false);
			showStaffQuantityCheckBox.setEnabled(false);
			showStaffPriceCheckBox.setEnabled(false);
		} else {

			if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString())
				currencyParityTextBox.setEnabled(true);
			else	
				currencyParityTextBox.setEnabled(false);

			if (getSFParams().hasSpecialAccess(BmoOrder.ACCESS_LIMITEDDISCOUNT) ||
					getSFParams().hasSpecialAccess(BmoOrder.ACCESS_UNLIMITEDDISCOUNT)) {
				extrasButton.setVisible(true);
			}

			customerRequisitionTextBox.setEnabled(true);    
			showEquipmentQuantityCheckBox.setEnabled(true);
			showEquipmentPriceCheckBox.setEnabled(true);
			showStaffQuantityCheckBox.setEnabled(true);
			showStaffPriceCheckBox.setEnabled(true);
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

	}

	public void getParityFromCurrency(String currencyId) {
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
				showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {				
				stopLoading();				
				if (result.hasErrors())
					showErrorMessage("Error al obtener el Tipo de Cambio");
				else {
					currencyParityTextBox.setValue(result.getMsg());
					coverageParity();
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

	public void saveShowChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-listBirthdays() ERROR: " + caught.toString());
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
				getUiParams().getBmObjectServiceAsync().saveSimple(bmoOrder.getPmClass(), bmoOrder, callback);
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-saveShowChange() ERROR: " + e.toString());
		}
	}

	private void toggleExtras() {
		if (discountTextBox.isVisible()) 
			discountTextBox.setVisible(false);
		else 
			discountTextBox.setVisible(true);
	}

	public void reset() {		
		updateAmount(id);

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CONSULTANCY)
				|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			orderGroupListStatusEffect();
			
		}
				

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
			orderGroupListStatusEffect();
			orderSessionTypePackageGrid.show();
		}
		
		orderSessionExtraGrid.show();
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
			uiOrderSessionFormPayment.show();
		}	
	}

	public void updateOrderGroup(String orderGroupId) {
		this.selectedOrderGroupId = Integer.parseInt(orderGroupId);
		orderGroupUiMap.get("" + orderGroupId).show();
	}

	private void orderGroupListStatusEffect() {
		
		ArrayList<UiOrderSessionGroupGrid> orderSessionGroupGridList = new ArrayList<UiOrderSessionGroupGrid>(orderGroupUiMap.values());

		Iterator<UiOrderSessionGroupGrid> orderGroupIterator = orderSessionGroupGridList.iterator();
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
		formFlexTable.addLabelField(35, 0, bmoOrder.getPaymentStatus());
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
		// Si el campo de cobertura está habilitado, hacer operaciones de verdadero/falso
		if (getSFParams().isFieldEnabled(bmoOrder.getCoverageParity())) {
			double currencyParity = Double.parseDouble(currencyParityTextBox.getText());
			if (currencyParity != bmoOrder.getCurrencyParity().toDouble()) {
				coveregeParityCheckBox.setValue(true);
			} else {
				if (bmoOrder.getCoverageParity().toBoolean())
					coveregeParityCheckBox.setValue(true);
				else 
					coveregeParityCheckBox.setValue(false);
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

	public void updateAmount(int id) {		
		AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmObject result) {
				stopLoading();
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

	public void saveAmountChange() {
		// Establece eventos ante respuesta de servicio
		AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-saveAmountChange() ERROR: " + caught.toString());
			}

			@Override
			public void onSuccess(BmUpdateResult result) {
				stopLoading();
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

		UiOrderSessionGroupGrid orderSessionGroupGrid = new UiOrderSessionGroupGrid(getUiParams(), vp, bmoOrder, orderGroupId, orderSessionUpdater);
		orderSessionGroupGrid.show();

		orderGroupUiMap.put("" + orderGroupId, orderSessionGroupGrid);
	}

	public void listOrderGroups() {		
		AsyncCallback<ArrayList<BmObject>> callback = new AsyncCallback<ArrayList<BmObject>>() {
			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-listOrderGroups() ERROR: " + caught.toString());	
			}
			@Override
			public void onSuccess(ArrayList<BmObject> result) {
				stopLoading();
				setOrderGroupList(result);
			}
		};
		try {
			if (!isLoading()) {
				startLoading();
				getUiParams().getBmObjectServiceAsync().list(bmoOrderGroup.getPmClass(), bmoFilterOrderGroup, callback);
			} else {
				showSystemMessage("No se puede mostrar listado, esta cargado...");
			}
		} catch (SFException e) {
			stopLoading();
			showErrorMessage(this.getClass().getName() + "-listOrderGroups() ERROR: " + e.toString());
		}
	}

	public void setOrderGroupList(ArrayList<BmObject> orderGroupList) {
		orderGroupsPanel.clear();
		orderGroupUiMap = new HashMap<String, UiOrderSessionGroupGrid>();
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
	
	public void showMBPayment() {
		orderPaymentDialogBox = new DialogBox(true);
		orderPaymentDialogBox.setGlassEnabled(true);
		orderPaymentDialogBox.setText("Cobro Pedido");

		VerticalPanel vp = new VerticalPanel();
		vp.setSize("350px", "250px");

		orderPaymentDialogBox.setWidget(vp);

		UiOrderPaymentForm uiOrderPaymentForm = new UiOrderPaymentForm(getUiParams(), vp, bmoOrder.getId());
		uiOrderPaymentForm.show();

		orderPaymentDialogBox.center();
		orderPaymentDialogBox.show();
	}
	
	private class UiOrderPaymentForm extends Ui {		
		private UiFormFlexTable formTable = new UiFormFlexTable(getUiParams());

		
		BmoRaccount bmoRaccount = new BmoRaccount();
		
		UiListBox bankAccountListBox = new UiListBox(getUiParams(), new BmoBankAccount());
		UiListBox raccountlistBox = new UiListBox(getUiParams(), new BmoRaccount());
		TextBox amountPayText = new TextBox();
		
		private BmField bankAccountField;
		private BmField raccountListField;
		private BmField amountField;
		
		private Button payButton = new Button("Aplicar");
		private Button closeButton = new Button("Cerrar");
		private Button printButton = new Button("Imprimir");
		private HorizontalPanel buttonPanelPayment = new HorizontalPanel();

		private int orderId;

		public UiOrderPaymentForm(UiParams uiParams, Panel defaultPanel, int Id) {
			super(uiParams, defaultPanel);
			orderId = id;
			
			bankAccountField = new BmField("bankAccount", "", "Cta.Banco", 11, Types.INTEGER, false, BmFieldType.ID, false);
			raccountListField = new BmField("raccount", "", "CxC", 11, Types.INTEGER, false, BmFieldType.ID, false);
			amountField = new BmField("amount", "", "Monto", 20, Types.DOUBLE, false, BmFieldType.CURRENCY, false);
			
			//Filtar las CxC del pedido
			BmFilter byOrder = new BmFilter();
			byOrder.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId(), "" + orderId);
			
			BmFilter filterByPaymentStatus = new BmFilter();
			filterByPaymentStatus.setValueOperatorFilter(bmoRaccount.getKind(), bmoRaccount.getPaymentStatus(), BmFilter.NOTEQUALS, "" + BmoRaccount.PAYMENTSTATUS_TOTAL);
			
			BmFilter justWithdraw = new BmFilter();
			justWithdraw.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getBmoRaccountType().getType(), "" + BmoRaccountType.TYPE_WITHDRAW);
			
			
			raccountlistBox.addFilter(justWithdraw);
			raccountlistBox.addFilter(filterByPaymentStatus);			
			raccountlistBox.addFilter(byOrder);
			
			raccountlistBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {					
					showRaccAmount();					
				}
			});
			
			payButton.setStyleName("formSaveButton");
			payButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					payRaccOrder();					
				}
			});
			
			closeButton.setStyleName("formCloseButton");
			closeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					orderPaymentDialogBox.hide();
					saveNext();					
				}
			});
			
			printButton.setStyleName("formSaveButton");
			printButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					printReceipt();
					saveNext();
				}
			});
			
			buttonPanelPayment.add(payButton);
			buttonPanelPayment.add(closeButton);
			buttonPanelPayment.add(printButton);
			printButton.setVisible(false);
			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			
			formTable.addField(1, 0, bankAccountListBox, bankAccountField);
			formTable.addField(2, 0, raccountlistBox, raccountListField);
			formTable.addField(3, 0, amountPayText, amountField);
			getOrderBalance();
			
			
			
			showRaccAmount();
		}
		
		private void showRaccAmount() {		
			
			if (Integer.parseInt(raccountlistBox.getSelectedId()) > 0) {
				BmoRaccount bmoRaccount = (BmoRaccount)raccountlistBox.getSelectedBmObject();			
				amountPayText.setText(bmoRaccount.getBalance().toString());
			}	
		}
		
		private void getOrderBalance() {			
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getOrderBalance() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					
					formTable.addLabelField(4, 0, "Saldo:" , result.getMsg());
					formTable.addButtonPanel(buttonPanelPayment);
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_ORDERBALANCE, "" + orderId, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getOrderBalance() ERROR: " + e.toString());
			}
		}
		
		//Imprimir el recibo
		private void printReceipt() {
			//if (!isLoading()) {
				String pageUrl = "frm/flex_receiptorder.jsp?raccountid=" + raccountlistBox.getSelectedId() + 
						         "&payment=" + amountPayText.getText();
				Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), pageUrl), "_blank", "");
			//}
		}
		
		//Pagar el pedido
		private void payRaccOrder() {		
			payButton.setVisible(false);
			String values = "";
			values = orderId + "|" + bankAccountListBox.getSelectedId() + "|" + 
			         raccountlistBox.getSelectedId() + "|" +  amountPayText.getText();
			
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-payRaccOrder() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (!result.hasErrors()) {						
						showSystemMessage("Pago Realizado con Exito");						
						processOrderUpdateResult(result);
						printButton.setVisible(true);					
						
					} else {
						showErrorMessage("Error al realizar el pago del pedido " + result.errorsToString());
					}
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrder.getPmClass(), bmoOrder, BmoOrder.ACTION_PAYORDER, values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-payRaccOrder() ERROR: " + e.toString());
			}
		}
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
		private BmoOrderGroup bmoOrderGroup;
		private Button saveButton = new Button("Agregar");
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
			formTable.addField(8, 0, showCreateCxcCheckBox, bmoOrderGroup.getCreateRaccount());
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
		private BmoOrderItem bmoOrderItem;
		private BmoOrder bmoOrder = new BmoOrder();
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		private UiListBox orderGroupListBox;
		private Label stockQuantity = new Label();
		private Label lockedQuantity = new Label();
		String productId = "";


		public UiOrderItemForm(UiParams uiParams, Panel defaultPanel, BmoOrder bmoOrder, int selectedOrderGroupId, BmoProduct bmoProduct) {
			super(uiParams, defaultPanel);
			this.bmoOrderItem = new BmoOrderItem();
			this.bmoOrder = bmoOrder;

			try {
				if (selectedOrderGroupId > 0) bmoOrderItem.getOrderGroupId().setValue(selectedOrderGroupId);
				bmoOrderItem.getProductId().setValue(bmoProduct.getId());
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

			defaultPanel.add(formTable);
		}

		@Override
		public void show() {
			formTable.addField(1, 0, productSuggestBox, bmoOrderItem.getProductId());
			formTable.addLabelField(2, 0, "En Almacén", stockQuantity);
			formTable.addLabelField(3, 0, "En Pedidos", lockedQuantity);
			formTable.addField(4, 0, nameTextBox, bmoOrderItem.getName());
			formTable.addField(5, 0, descriptionTextArea, bmoOrderItem.getDescription());
			formTable.addField(6, 0, quantityTextBox, bmoOrderItem.getQuantity());

			// Mostrar los días si es de tipo renta
			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
				formTable.addField(7, 0, daysTextBox, bmoOrderItem.getDays());

			formTable.addField(8, 0, commissionCheckBox, bmoOrderItem.getCommission());

			formTable.addField(9, 0, priceTextBox, bmoOrderItem.getPrice());
			formTable.addField(10, 0, orderGroupListBox, bmoOrderItem.getOrderGroupId());

			formTable.addButtonPanel(buttonPanel);

			statusEffect();
		}

		public void formSuggestionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == productSuggestBox) {
				//El producto maneja comision
				commissionCheckBox.setValue(true);
			}
			statusEffect();
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
				nameTextBox.setText("");
				nameTextBox.setEnabled(true);
				priceTextBox.setText("");
				priceTextBox.setEnabled(true);
				stockQuantity.setText("0");
				lockedQuantity.setText("0");
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

		//Obtener la cantidad en almacen
		public void getStockQuantity() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getStockQuantity() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
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

		//Obtener la cantidad en pedidos
		public void getLockedQuantity() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLockedQuantity() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					BmoOrderItem bmoOrderItem = (BmoOrderItem)result.getBmObject();
					lockedQuantity.setText(bmoOrderItem.getQuantity().toString());
					getRentalSalePrice();
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

		//Obtener precio de venta/renta del producto
		public void getRentalSalePrice() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					BmoProduct bmoProduct = (BmoProduct)result.getBmObject();
					nameTextBox.setText("" + bmoProduct.getName().toString());
					//if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
					//	priceTextBox.setText("" + bmoProduct.getRentalPrice().toDouble());
					//else 
					//	priceTextBox.setText("" + bmoProduct.getSalePrice().toDouble());
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
				showErrorMessage(this.getClass().getName() + "-getRentalSalePrice() ERROR: " + e.toString());
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
				bmoOrderItem.getOrderGroupId().setValue(orderGroupListBox.getSelectedId());

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
		private Button saveButton = new Button("Agregar");
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
			//formTable.addLabelField(2, 0, bmoProductKitItem.getBmoProduct().getRentalPrice());
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
				//bmoOrderItem.getPrice().setValue(bmoProductKitItem.getBmoProduct().getRentalPrice().toDouble());
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
		private Button saveButton = new Button("Agregar");
		private HorizontalPanel buttonPanel = new HorizontalPanel();
		private UiListBox productKitListBox = new UiListBox(getUiParams(), new BmoProductKit());
		private BmoProductKit bmoProductKit;
		private Label descriptionLabel;
		private Label amountLabel;
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

			descriptionLabel = new Label(bmoProductKit.getDescription().toString());
			amountLabel = new Label(bmoProductKit.getAmount().toString());

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
			formTable.addLabelField(2, 0, bmoProductKit.getDescription().getLabel(), descriptionLabel);
			formTable.addLabelField(3, 0, bmoProductKit.getAmount().getLabel(), amountLabel);
			formTable.addButtonPanel(buttonPanel);
		}

		public void formListChange(ChangeEvent event) {
			if (event.getSource() == productKitListBox) {
				bmoProductKit = (BmoProductKit)productKitListBox.getSelectedBmObject();
				if (bmoProductKit != null) {
					descriptionLabel.setText(bmoProductKit.getDescription().toString());
					amountLabel.setText(bmoProductKit.getAmount().toString());
				} else {
					descriptionLabel.setText("");
					amountLabel.setText("");
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

public class OrderSessionUpdater {
		
		public void changeOrder() {
			stopLoading();
			reset();
		}

		public void updateOrderGroups() {			
			stopLoading();			
			listOrderGroups();
		}

		public void changeOrderEquipment(){
			stopLoading();
			reset();
		}

		public void changeOrderStaff(){
			stopLoading();
			reset();
		}

		public void changeOrderProperty(){
			stopLoading();
			reset();
		}

		public void changeOrderPropertyModelExtra(){
			stopLoading();
			reset();
		}
		
		public void changeOrderSessionTypePackage(){
			stopLoading();
			reset();
		}
		
		public void changeOrderCredit(){
			stopLoading();
			reset();
		}
		
		public void changeOrderSessionExtra(){
			stopLoading();
			reset();
		}
	}
}
