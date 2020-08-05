package com.flexwm.client.op;

import java.util.ArrayList;
import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoConsultancy;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoUser;


public class UiConsultancy extends UiList {

	BmoConsultancy bmoConsultancy;

	public UiConsultancy(UiParams uiParams) {
		super(uiParams, new BmoConsultancy());
		bmoConsultancy = (BmoConsultancy)getBmObject();
	}

	public UiConsultancy(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoConsultancy());
		bmoConsultancy = (BmoConsultancy)getBmObject();
	}

	@Override
	public void postShow() {

		if (isMaster() || isSlave()) {

			addFilterListBox(new UiListBox(getUiParams(), new BmoOrderType()), new BmoOrderType());
			addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoConsultancy.getCustomerId());

			if (!isMobile()) {
				// Filtrar por vendedores
				BmoUser bmoUser = new BmoUser();
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterSalesmen = new BmFilter();
				int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + salesGroupId);		
				addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoConsultancy.getUserId());

				addDateRangeFilterListBox(bmoConsultancy.getStartDate());
			}
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoConsultancy.getStatus()), bmoConsultancy, bmoConsultancy.getStatus());

			addStaticFilterListBox(new UiListBox(getUiParams(), bmoConsultancy.getStatusScrum()), bmoConsultancy, bmoConsultancy.getStatusScrum());

			if (!isMobile()) 
				addTagFilterListBox(bmoConsultancy.getTags());

			if (!isMobile()) {
				if (getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGESTATUS)) {
					addActionBatchListBox(new UiListBox(getUiParams(), new BmoConsultancy().getStatus()), bmoConsultancy);
				}
			}
		}
	}

	@Override
	public void create() {
		UiConsultancyForm uiConsultancyForm = new UiConsultancyForm(getUiParams(), 0);
		uiConsultancyForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoConsultancy = (BmoConsultancy)bmObject;

		UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), bmoConsultancy.getId());
		uiConsultancyDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiConsultancyForm uiConsultancyForm = new UiConsultancyForm(getUiParams(), bmObject.getId());
		uiConsultancyForm.show();
	}


	public class UiConsultancyForm extends UiFormDialog {
		BmoConsultancy bmoConsultancy;
		TextBox codeTextBox = new TextBox();	
		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiListBox customerContactIdListBox = new UiListBox(getUiParams(), new BmoCustomerContact());
		UiSuggestBox userSuggestBox;
		UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox endDateTimeBox = new UiDateTimeBox();	
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		UiListBox budgetItemIdUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		UiListBox areaIdUiListBox = new UiListBox(getUiParams(), new BmoArea());
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiTagBox tagBox = new UiTagBox(getUiParams());
		TextBox customerRequisitionTextBox = new TextBox();

		// SCRUM
		UiListBox statusScrumListBox = new UiListBox(getUiParams());
		UiDateBox closeDateBox = new UiDateBox();
		UiDateBox orderDateBox = new UiDateBox();
		UiDateBox desiredDateBox = new UiDateBox();
		UiDateBox startDateScrumDateBox = new UiDateBox();
		UiDateBox deliveryDateBox = new UiDateBox();
		UiListBox areaIdScrumListBox = new UiListBox(getUiParams(), new BmoArea());
		UiSuggestBox leaderUserIdSuggestBox = new UiSuggestBox(new BmoUser());
		UiSuggestBox assignedUserIdSuggestBox = new UiSuggestBox(new BmoUser());

		// Secciones
		private String generalSection = "Datos Generales";
		private String scrumSection = "Datos SCRUM";

		// Variables RPC
		private int parityFromCurrencyRpcAttempt = 0;
		private String currencyIdRpcAttempt;
		private String customerIdRpcAttempt;
		private int contactMainRpcAttempt  = 0;

		// Otros
		BmoWFlowType bmoWFlowType;

		public UiConsultancyForm(UiParams uiParams, int id) {
			super(uiParams, new BmoConsultancy(), id);
			bmoConsultancy = (BmoConsultancy)getBmObject();
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

			// Responsable
			BmFilter filterLeaderUserActive = new BmFilter();
			filterLeaderUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			leaderUserIdSuggestBox.addFilter(filterLeaderUserActive);

			// Consultor
			BmFilter filterAssignedUserActive = new BmFilter();
			filterAssignedUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			assignedUserIdSuggestBox.addFilter(filterAssignedUserActive);
			
		}

		@Override
		public void populateFields() {
			bmoConsultancy = (BmoConsultancy)getBmObject();

			if (!newRecord)
				formDialogBox.setText("Editar Detalle");

			bmoWFlowType = new BmoWFlowType();
			wFlowTypeListBox = new UiListBox(getUiParams(), bmoWFlowType);

			try {
				if (newRecord) {

					// Busca Empresa seleccionada por default
					if (newRecord && getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoConsultancy.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

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

				// Si no esta asignado el vendedor, buscar por el usuario loggeado
				if (!(bmoConsultancy.getUserId().toInteger() > 0)) {
					bmoConsultancy.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
				}

				// Si no esta asignada la moneda, buscar por la default
				if (!(bmoConsultancy.getCurrencyId().toInteger() > 0)) {
					bmoConsultancy.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
					getParityFromCurrency(bmoConsultancy.getCurrencyId().toString());
				}

				// Asignar fechas por defecto
				if (newRecord) {
					bmoConsultancy.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoConsultancy.getEndDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));

					bmoConsultancy.getCloseDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoConsultancy.getOrderDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));

				} else {
					// Forzar a colocar valores origen
					bmoConsultancy.getCode().setValue(bmoConsultancy.getCode().toString());
					bmoConsultancy.getOrderTypeId().setValue(bmoConsultancy.getOrderTypeId().toInteger());
					bmoConsultancy.getWFlowTypeId().setValue(bmoConsultancy.getWFlowTypeId().toInteger());
					bmoConsultancy.getName().setValue(bmoConsultancy.getName().toString());
					bmoConsultancy.getCustomerId().setValue(bmoConsultancy.getCustomerId().toInteger());
					bmoConsultancy.getUserId().setValue(bmoConsultancy.getUserId().toInteger());
					bmoConsultancy.getStartDate().setValue(bmoConsultancy.getStartDate().toString());
					bmoConsultancy.getEndDate().setValue(bmoConsultancy.getEndDate().toString());
					bmoConsultancy.getCompanyId().setValue(bmoConsultancy.getCompanyId().toInteger());
					bmoConsultancy.getMarketId().setValue(bmoConsultancy.getMarketId().toInteger());
					bmoConsultancy.getCustomerRequisition().setValue(bmoConsultancy.getCustomerRequisition().toString());
					bmoConsultancy.getCustomerContactId().setValue(bmoConsultancy.getCustomerContactId().toInteger());
					bmoConsultancy.getCurrencyId().setValue(bmoConsultancy.getCurrencyId().toInteger());
					bmoConsultancy.getCurrencyParity().setValue(bmoConsultancy.getCurrencyParity().toDouble());
					if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
						bmoConsultancy.getBudgetItemId().setValue(bmoConsultancy.getBudgetItemId().toInteger());
						bmoConsultancy.getAreaId().setValue(bmoConsultancy.getAreaId().toInteger());
					}
					bmoConsultancy.getStatus().setValue(bmoConsultancy.getStatus().toString());
					bmoConsultancy.getTags().setValue(bmoConsultancy.getTags().toString());				
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
			}

			int row = 0, col = 0;
			if (newRecord) {
				// Se quita porque se ven en los mismos datos en dos lados (venta y pedidos)
				formFlexTable.addSectionLabel(row++, col, generalSection, 2);
				formFlexTable.addFieldReadOnly(row++, col, codeTextBox, bmoConsultancy.getCode());
				formFlexTable.addField(row++, 0, orderTypeListBox, bmoConsultancy.getOrderTypeId());	
	
				formFlexTable.addField(row++, col, wFlowTypeListBox, bmoConsultancy.getWFlowTypeId());
				formFlexTable.addField(row++, col, nameTextBox, bmoConsultancy.getName());
				formFlexTable.addField(row++, col, customerSuggestBox, bmoConsultancy.getCustomerId());
				setCustomerContactsListBoxFilters(bmoConsultancy.getCustomerId().toInteger());
				formFlexTable.addField(row++, col, userSuggestBox, bmoConsultancy.getUserId());
	
				formFlexTable.addField(row++, col, startDateTimeBox, bmoConsultancy.getStartDate());
				formFlexTable.addField(row++, col, endDateTimeBox, bmoConsultancy.getEndDate());
				formFlexTable.addField(row++, col, companyListBox, bmoConsultancy.getCompanyId());
				formFlexTable.addField(row++, col, marketListBox, bmoConsultancy.getMarketId());
				formFlexTable.addField(row++, 0, customerRequisitionTextBox, bmoConsultancy.getCustomerRequisition());
				formFlexTable.addField(row++, 0, customerContactIdListBox, bmoConsultancy.getCustomerContactId());
	
				formFlexTable.addField(row++, col, currencyListBox, bmoConsultancy.getCurrencyId());
				//			populateParityFromCurrency(currencyListBox.getSelectedId());
				formFlexTable.addField(row++, col, currencyParityTextBox, bmoConsultancy.getCurrencyParity());
	
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
					// Asigna filtros de control presupuestal
					setBudgetItemsListBoxFilters(bmoConsultancy.getCompanyId().toInteger());
					formFlexTable.addField(row++, col, budgetItemIdUiListBox, bmoConsultancy.getBudgetItemId());
					formFlexTable.addField(row++, col, areaIdUiListBox, bmoConsultancy.getAreaId());
				}
	
				formFlexTable.addField(row++, col, statusListBox, bmoConsultancy.getStatus());
				formFlexTable.addField(row++, col, tagBox, bmoConsultancy.getTags());
			}
			formFlexTable.addSectionLabel(row++, col, scrumSection, 2); //10
			formFlexTable.addField(row++, col, areaIdScrumListBox, bmoConsultancy.getAreaIdScrum());
			formFlexTable.addField(row++, col, leaderUserIdSuggestBox, bmoConsultancy.getLeaderUserId());
			formFlexTable.addField(row++, col, assignedUserIdSuggestBox, bmoConsultancy.getAssignedUserId());
			formFlexTable.addField(row++, col, statusScrumListBox, bmoConsultancy.getStatusScrum());
			formFlexTable.addField(row++, col, closeDateBox, bmoConsultancy.getCloseDate());
			formFlexTable.addField(row++, col, orderDateBox, bmoConsultancy.getOrderDate());
			formFlexTable.addField(row++, col, desiredDateBox, bmoConsultancy.getDesireDate());
			formFlexTable.addField(row++, col, startDateScrumDateBox, bmoConsultancy.getStartDateScrum());
			formFlexTable.addField(row++, col, deliveryDateBox, bmoConsultancy.getDeliveryDate());

			
			statusEffect();
		}

		private void statusEffect() {
			// ****** Datos generales ******
			startDateTimeBox.setEnabled(false);
			endDateTimeBox.setEnabled(false);	
			statusListBox.setEnabled(false);
			wFlowTypeListBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			marketListBox.setEnabled(false);
			if (!newRecord) {
				orderTypeListBox.setEnabled(false);			
				userSuggestBox.setEnabled(false);
				customerSuggestBox.setEnabled(false);
				companyListBox.setEnabled(false);

				if (bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_REVISION)) {
					marketListBox.setEnabled(true);
				}

			} else {
				currencyListBox.setEnabled(true);
				userSuggestBox.setEnabled(true);
				wFlowTypeListBox.setEnabled(true);
				marketListBox.setEnabled(true);
			}
			
			startDateTimeBox.setEnabled(true);
			endDateTimeBox.setEnabled(true);
			

			// Si tiene permiso, deja cambiar vendedor del pedido
			if (!newRecord && getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGESALESMAN))
				userSuggestBox.setEnabled(true);

			if (bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_AUTHORIZED) ||
					bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_FINISHED) ||
					bmoConsultancy.getStatus().equals(BmoConsultancy.STATUS_CANCELLED)) {
				nameTextBox.setEnabled(false);
				descriptionTextArea.setEnabled(false);
				customerSuggestBox.setEnabled(false);
				userSuggestBox.setEnabled(false);
				startDateTimeBox.setEnabled(false);
				endDateTimeBox.setEnabled(false);
				companyListBox.setEnabled(false);
				statusListBox.setEnabled(false);
				customerContactIdListBox.setEnabled(false);
			} else {
				// Esta en revision
				if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
					currencyParityTextBox.setEnabled(true);
				} else {
					currencyParityTextBox.setEnabled(false);
				}

				currencyListBox.setEnabled(true);
			}

			// Si no tiene permiso para modificar status, deshabilitar combo
			if (!newRecord && getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGESTATUS))
				statusListBox.setEnabled(true);

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);			


			// ****** Datos para scrum ******
			areaIdScrumListBox.setEnabled(false);
			leaderUserIdSuggestBox.setEnabled(false);
			assignedUserIdSuggestBox.setEnabled(false);
			statusScrumListBox.setEnabled(false);
			// no tocar, asi se quedan
			closeDateBox.setEnabled(false);
			orderDateBox.setEnabled(false);

			desiredDateBox.setEnabled(false);
			startDateScrumDateBox.setEnabled(false);
			deliveryDateBox.setEnabled(false);

			// Si no hay responsable habilitar campos
			if (!(bmoConsultancy.getLeaderUserId().toInteger() > 0)) {
				areaIdScrumListBox.setEnabled(true);
				leaderUserIdSuggestBox.setEnabled(true);
				assignedUserIdSuggestBox.setEnabled(true);
				statusScrumListBox.setEnabled(true);
			} else {
				// Si es el Responsable o tienes permiso para cambiar datos se habilitan campos(dpto., responsable, consultor)
				if (bmoConsultancy.getLeaderUserId().toInteger() == getSFParams().getLoginInfo().getUserId()
						|| getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGEDATA) ) {
					areaIdScrumListBox.setEnabled(true);
					leaderUserIdSuggestBox.setEnabled(true);
					assignedUserIdSuggestBox.setEnabled(true);
				}
			}

			// Permiso Cambiar Fecha Deseada
			if (getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGEDESIREDATE)) 
				desiredDateBox.setEnabled(true);

			// Permiso Cambiar Fecha Inicio
			if (getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGESTARTDATE)) 
				startDateScrumDateBox.setEnabled(true);	

			// Permiso Cambiar Fecha Pactada
			if (getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGEDELIVERYDATE)) 
				deliveryDateBox.setEnabled(true);

			// Permiso Cambiar Status
			if (getSFParams().hasSpecialAccess(BmoConsultancy.ACCESS_CHANGESTATUSSCRUM)) 
				statusScrumListBox.setEnabled(true);
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
						budgetItemIdUiListBox.setSelectedId("" + bmoOrderType.getDefaultBudgetItemId().toInteger());
					else
						budgetItemIdUiListBox.setSelectedIndex(0);
					if (bmoOrderType.getDefaultAreaId().toInteger() > 0)
						areaIdUiListBox.setSelectedId("" + bmoOrderType.getDefaultAreaId().toInteger());
					else
						areaIdUiListBox.setSelectedIndex(0);


					// Traer dpto. y estatus de tipo de pedido
					if (bmoOrderType.getAreaDefaultDetail().toInteger() > 0)
						areaIdScrumListBox.setSelectedId("" + bmoOrderType.getAreaDefaultDetail().toInteger());

					if(!bmoOrderType.getStatusDefaultDetail().equals("")) 
						statusScrumListBox.setSelectedCode("" + bmoOrderType.getStatusDefaultDetail().toString());

				} else {
					budgetItemIdUiListBox.setSelectedIndex(0);
					areaIdUiListBox.setSelectedIndex(0);
					customerSuggestBox.clear();
				}

				// Asigna dato si solo existe 1 registro
				marketListBox.populate("", false);
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
//					payConditionUiListBox.setSelectedId("" + bmoCustomer.getPayConditionId().toInteger());
				}
				
				if (bmoCustomer.getMarketId().toInteger() > 0) {
					marketListBox.setSelectedId("" + bmoCustomer.getMarketId().toInteger());
				} else marketListBox.setSelectedId("0");
				
				populateCustomerContacts(customerSuggestBox.getSelectedId());
				statusEffect();
			}
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			// Datos scrum
			// Asignar fecha de inicio dle pedido a fecha de pedido de scrum
			if (event.getSource() == startDateTimeBox) {
				Date startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat()).parse(startDateTimeBox.getTextBox().getText());
				orderDateBox.setValue(startDate);
				populateParityFromCurrency(currencyListBox.getSelectedId());
			}
		}

		//Obtener la paridad de la moneda
		public void populateParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId);
		}

		// Obtiene paridad de la moneda, primer intento
		public void getParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId, 0);
		}

		public void getParityFromCurrency(String currencyId, int parityFromCurrencyRpcAttempt) {
			if (parityFromCurrencyRpcAttempt < 5) {
				setCurrencyIdRpcAttempt(currencyId);
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
							getParityFromCurrency(getCurrencyIdRpcAttempt(), getParityFromCurrencyRpcAttempt());
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
			customerContactIdListBox.populate(bmoConsultancy.getCustomerContactId());
			callMainContasct("" + customerId);
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
		public void callMainContasct(String callMainContacts) {
			callMainContasct(callMainContacts, 0);
		};

		//llamar el maincontact
		public void callMainContasct(String customerId, int contactMainRpcAttempt) {
			if (contactMainRpcAttempt < 5) {

				setCustomerIdRpcAttempt(customerId);
				setContactMainRpcAttempt(contactMainRpcAttempt + 1);
				BmoCustomer bmoCustomer = new BmoCustomer();
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getContactMainRpcAttempt() < 5) {
						} else {
							showErrorMessage(this.getClass().getName() + "-customerContact() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult result) {	
						stopLoading();	
						setContactMainRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el contacto principal.");
						else {
							customerContactIdListBox.setSelectedId(result.getMsg());
						}
					}
				};

				try {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCustomer.getPmClass(), bmoCustomer, BmoCustomer.ACTION_GETCUSTOMERCONTACT, "" + customerId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getCustomerMainContact ERROR: " + e.toString());
				}
			}
		}

		// Actualiza combo de partidas presp. por empresa
		private void populateBudgetItems(int companyId) {
			budgetItemIdUiListBox .clear();
			budgetItemIdUiListBox.clearFilters();
			setBudgetItemsListBoxFilters(companyId);
			budgetItemIdUiListBox.populate(bmoConsultancy.getBudgetItemId());
		}

		// Asigna filtros al listado de partidas presp. por empresa
		private void setBudgetItemsListBoxFilters(int companyId) {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
				budgetItemIdUiListBox.addBmFilter(bmFilterByCompany);

				// Filtro de ingresos(abono)
				BmFilter filterByDeposit = new BmFilter();
				filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
				budgetItemIdUiListBox.addFilter(filterByDeposit);

			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
				budgetItemIdUiListBox.addBmFilter(bmFilter);
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
				wFlowTypeListBox.populate("" + bmoConsultancy.getWFlowTypeId().toInteger());
			}
		}
		
		@Override
		public void postShow() {
			deleteButton.setVisible(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoConsultancy.setId(id);
			
			if (newRecord) {
				// Se quita porque se ven en los mismos datos en dos lados (venta y pedidos)
				bmoConsultancy.getCode().setValue(codeTextBox.getText());
				bmoConsultancy.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
				bmoConsultancy.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
				bmoConsultancy.getName().setValue(nameTextBox.getText());
				bmoConsultancy.getCustomerId().setValue(customerSuggestBox.getSelectedId());
				bmoConsultancy.getUserId().setValue(userSuggestBox.getSelectedId());
	
				bmoConsultancy.getStartDate().setValue(startDateTimeBox.getDateTime());
				bmoConsultancy.getEndDate().setValue(endDateTimeBox.getDateTime());
				bmoConsultancy.getCompanyId().setValue(companyListBox.getSelectedId());
				bmoConsultancy.getMarketId().setValue(marketListBox.getSelectedId());
				bmoConsultancy.getCurrencyId().setValue(currencyListBox.getSelectedId());
				bmoConsultancy.getCurrencyParity().setValue(currencyParityTextBox.getText());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
					bmoConsultancy.getBudgetItemId().setValue(budgetItemIdUiListBox.getSelectedId());
					bmoConsultancy.getAreaId().setValue(areaIdUiListBox.getSelectedId());
				}
				bmoConsultancy.getStatus() .setValue(statusListBox.getSelectedCode());
				bmoConsultancy.getTags().setValue(tagBox.getTagList());
			} else {
				// Mandar los mismos valores
				bmoConsultancy.getCode().setValue(bmoConsultancy.getCode().toString());
				bmoConsultancy.getOrderTypeId().setValue(bmoConsultancy.getOrderTypeId().toInteger());
				bmoConsultancy.getWFlowTypeId().setValue(bmoConsultancy.getWFlowTypeId().toInteger());
				bmoConsultancy.getName().setValue(bmoConsultancy.getName().toString());
				bmoConsultancy.getCustomerId().setValue(bmoConsultancy.getCustomerId().toInteger());
				bmoConsultancy.getUserId().setValue(bmoConsultancy.getUserId().toInteger());
				bmoConsultancy.getStartDate().setValue(bmoConsultancy.getStartDate().toString());
				bmoConsultancy.getEndDate().setValue(bmoConsultancy.getEndDate().toString());
				bmoConsultancy.getCompanyId().setValue(bmoConsultancy.getCompanyId().toInteger());
				bmoConsultancy.getMarketId().setValue(bmoConsultancy.getMarketId().toInteger());
				bmoConsultancy.getCustomerRequisition().setValue(bmoConsultancy.getCustomerRequisition().toString());
				bmoConsultancy.getCustomerContactId().setValue(bmoConsultancy.getCustomerContactId().toInteger());
				bmoConsultancy.getCurrencyId().setValue(bmoConsultancy.getCurrencyId().toInteger());
				bmoConsultancy.getCurrencyParity().setValue(bmoConsultancy.getCurrencyParity().toDouble());
				if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
					bmoConsultancy.getBudgetItemId().setValue(bmoConsultancy.getBudgetItemId().toInteger());
					bmoConsultancy.getAreaId().setValue(bmoConsultancy.getAreaId().toInteger());
				}
				bmoConsultancy.getStatus().setValue(bmoConsultancy.getStatus().toString());
				bmoConsultancy.getTags().setValue(bmoConsultancy.getTags().toString());
			}
			// Scrum
			bmoConsultancy.getAreaIdScrum().setValue(areaIdScrumListBox.getSelectedId());
			bmoConsultancy.getLeaderUserId().setValue(leaderUserIdSuggestBox.getSelectedId());
			bmoConsultancy.getAssignedUserId().setValue(assignedUserIdSuggestBox.getSelectedId());
			bmoConsultancy.getStatusScrum().setValue(statusScrumListBox.getSelectedCode());
			bmoConsultancy.getCloseDate().setValue(closeDateBox.getTextBox().getText());
			bmoConsultancy.getOrderDate().setValue(orderDateBox.getTextBox().getText());
			bmoConsultancy.getDesireDate().setValue(desiredDateBox.getTextBox().getText());
			bmoConsultancy.getStartDateScrum().setValue(startDateScrumDateBox.getTextBox().getText());
			bmoConsultancy.getDeliveryDate().setValue(deliveryDateBox.getTextBox().getText());
			bmoConsultancy.getCustomerRequisition().setValue(customerRequisitionTextBox.getText());
			bmoConsultancy.getCustomerContactId().setValue(customerContactIdListBox.getSelectedId());

			return bmoConsultancy;
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				// Si esta asignado el tipo de proyecto, envia al dashboard, en caso contrario, envia a la lista
				if (bmoConsultancy.getWFlowTypeId().toInteger() > 0) {
					UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), id);
					uiConsultancyDetail.show();
				} else 
					list();
			} else {
				if (id > 0) {
					UiConsultancyDetail uiConsultancyDetail = new UiConsultancyDetail(getUiParams(), id);
					uiConsultancyDetail.show();
				} else 
					list();
			}
		}

		@Override
		public void close() {
			if (deleted) new UiConsultancy(getUiParams()).show();
		}

		// Variables para llamadas RPC
		public int getParityFromCurrencyRpcAttempt() {
			return parityFromCurrencyRpcAttempt;
		}

		public void setParityFromCurrencyRpcAttempt(int parityFromCurrencyRpcAttempt) {
			this.parityFromCurrencyRpcAttempt = parityFromCurrencyRpcAttempt;
		}

		public String getCurrencyIdRpcAttempt() {
			return currencyIdRpcAttempt;
		}

		public void setCurrencyIdRpcAttempt(String currencyIdRpcAttempt) {
			this.currencyIdRpcAttempt = currencyIdRpcAttempt;
		}

		public String getCustomerIdRpcAttempt() {
			return customerIdRpcAttempt;
		}
		
		public void setCustomerIdRpcAttempt(String customerIdRpcAttempt) {
			this.customerIdRpcAttempt = customerIdRpcAttempt;
		}

		public int getContactMainRpcAttempt() {
			return contactMainRpcAttempt;
		}

		public void setContactMainRpcAttempt(int contactMainRpcAttempt) {
			this.contactMainRpcAttempt = contactMainRpcAttempt;
		}
	}

}
