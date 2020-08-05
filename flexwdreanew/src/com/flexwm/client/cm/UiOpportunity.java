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

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.flexwm.shared.cm.BmoCategoryForecast;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoOpportunity;
import com.flexwm.shared.cm.BmoOpportunityCompetition;
import com.flexwm.shared.cm.BmoPayCondition;
import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiClickHandler;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiListCheckBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;
import com.flexwm.client.cm.UiCustomer.UiCustomerForm;
import com.flexwm.client.ev.UiVenue;
import com.flexwm.client.ev.UiVenue.UiVenueForm;
import com.flexwm.shared.BmoFlexConfig;


public class UiOpportunity extends UiList {
	BmoOpportunity bmoOpportunity;

	public UiOpportunity(UiParams uiParams) {
		super(uiParams, new BmoOpportunity());
		bmoOpportunity = (BmoOpportunity)getBmObject();
	}

	public UiOpportunity(UiParams uiParams, Panel panel) {
		super(uiParams, panel, new BmoOpportunity());
		bmoOpportunity = (BmoOpportunity)getBmObject();
	}

	@Override
	public void postShow() {
		populateStatusListBox();
		
		if (!isMobile())
			addFilterListBox(new UiListBox(getUiParams(), new BmoOrderType()), new BmoOrderType());

		// Filtrar tipos de Flujos por Categoria Oportunidad
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		//		BmFilter filterWFlowType = new BmFilter();
		//		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		//		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoOpportunity.getBmoWFlowType());
		BmFilter filterWFlowTypeIn = new BmFilter();
		filterWFlowTypeIn.setInFilter(bmoOpportunity.getKind(), bmoWFlowType.getIdFieldName(), bmoOpportunity.getWFlowTypeId().getName(), "1" , "1");
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowTypeIn), bmoOpportunity.getBmoWFlowType());

//		if (!isMobile()) {
//			// Filtrar fases por Categoría Oportunidad
//			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
//			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
//			BmFilter filterWFlowPhase = new BmFilter();
//			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
//					bmoWFlowPhase.getWFlowCategoryId().getName(), 
//					bmoWFlowCategory.getIdFieldName(),
//					bmoWFlowCategory.getProgramId().getName(),
//					"" + bmObjectProgramId);
//			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoOpportunity.getBmoWFlow().getBmoWFlowPhase());
//		}

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
		addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoOpportunity.getUserId());

		addStaticFilterListBox(new UiListBox(getUiParams(), bmoOpportunity.getStatus()), bmoOpportunity, bmoOpportunity.getStatus(), "" + BmoOpportunity.STATUS_REVISION);
		if (!isMobile()) 
			addTagFilterListBox(bmoOpportunity.getTags());
		if (getSFParams().isFieldEnabled(bmoOpportunity.getVenueId())) {
			addFilterSuggestBox(new UiSuggestBox(new BmoVenue()), new BmoVenue(), bmoOpportunity.getVenueId());
		}
	}

	@Override
	public void create() {
		UiOpportunityForm uiOpportunityForm = new UiOpportunityForm(getUiParams(), 0);
		uiOpportunityForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoOpportunity = (BmoOpportunity)bmObject;
		UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), bmoOpportunity.getId());
		uiOpportunityDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiOpportunityForm uiOpportunityForm = new UiOpportunityForm(getUiParams(), bmObject.getId());
		uiOpportunityForm.show();
	}

	public class UiOpportunityForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox startDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox endDateTimeBox = new UiDateTimeBox();
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox categoryForestcastListBox = new UiListBox(getUiParams());
		UiListBox categoryForestcastIdListBox = new UiListBox(getUiParams(), new BmoCategoryForecast());
		UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
		UiSuggestBox venueSuggestBox = new UiSuggestBox(new BmoVenue());
		//UiListBox customerAddressListBox = new UiListBox(getUiParams(), new BmoCustomerAddress());
		UiDateBox leadDateBox = new UiDateBox();
		TextArea compvsposTextArea = new TextArea();
		TextArea commercialTermsTextArea = new TextArea();

		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox wFlowTypeListBox;
		UiListBox effectWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		Button copyDates = new Button("Copiar");
		UiListBox saleProbabilityListBox = new UiListBox(getUiParams());
		UiDateBox saleDateBox = new UiDateBox();
		UiDateBox expireDateBox = new UiDateBox();
		UiTagBox tagBox = new UiTagBox(getUiParams());
		TextBox amountTextBox = new TextBox();
		TextBox amountServiceTextBox = new TextBox();
		TextBox amountLicenseTextBox = new TextBox();
		TextBox customField1TextBox = new TextBox();
		TextBox customField2TextBox = new TextBox();	
		TextBox customField3TextBox = new TextBox();
		TextBox customField4TextBox = new TextBox();

		TextArea loseCommentsTextArea = new TextArea();
		UiListBox loseMotiveListBox = new UiListBox(getUiParams(), new BmoLoseMotive());
		TextBox customerRequisitionTextBox = new TextBox();
		UiListBox customerContactIdListBox = new UiListBox(getUiParams(), new BmoCustomerContact());
		UiSuggestBox propertyModelSuggestBox = new UiSuggestBox(new BmoPropertyModel());
		UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
		UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
		TextBox fiscalYearTextBox = new TextBox();
		TextBox fiscalPeriodTextBox = new TextBox();
		UiListBox payConditionUiListBox= new UiListBox(getUiParams(), new BmoPayCondition());

		BmoOpportunity bmoOpportunity;
		BmoWFlowType bmoWFlowType;
		BmoQuote bmoQuote = new BmoQuote();

		private int parityFromCurrencyRpcAttempt = 0;
		private String currencyId;
		private String customerId;
		private String companyId;
		private int profileSalesmanRpcAttempt = 0;

		private int bmoQuoteRpcAttempt = 0;
		private int contactMainRpcAttempt  = 0;
		String generalSection = "Datos Generales";
		String valueSection = "Valores";
		String extraSection = "Datos Adicionales";
		String loseSection = "Perder Oportunidad";
		String statusSection = "Estatus";

		Button lostButton = new Button("PERDER");

		//Copia de Oportunidad
		Button copyOpportunityButton = new Button("COPIAR");
		Button copyOpportunityDialogButton = new Button("COPIAR OPORTUNIDAD");
		Button copyOpportunitynCloseDialogButton = new Button("CERRAR");
		DialogBox copyOpportunityDialogBox;
		UiSuggestBox copyOpportunitySuggestBox = new UiSuggestBox(new BmoOpportunity());
		//UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());

		// Cambiar tipo de flujo
		String changeWFlowTypeSection = "Cambiar Workflow";
		Button changeWFlowTypeButton = new Button("CAMBIAR WORKFLOW");
		UiListBox newWFlowTypeListBox;
		UiListBox newEffectWFlowTypeListBox;
		BmField newWFlowTypeFieldId;
		BmField newEffectWFlowTypeFieldId;
		boolean multiCompany = false;

		public UiOpportunityForm(UiParams uiParams, int id) {
			super(uiParams, new BmoOpportunity(), id);

			newWFlowTypeFieldId = new BmField("newWFlowTypeFieldId", "", "Tipo Seguimiento", 20, Types.INTEGER, false, BmFieldType.ID, false);
			newEffectWFlowTypeFieldId = new BmField("newEffectWFlowTypeFieldId", "", "Tipo Efecto", 20, Types.INTEGER, false, BmFieldType.ID, false);

			initialize();
		}

		private void initialize() {

			bmoOpportunity = (BmoOpportunity)getBmObject();


			// Genera listado WFlow
			bmoWFlowType = new BmoWFlowType();
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());

			// Boton de copiar fechas
			copyDates.setStyleName("formCloseButton");
			copyDates.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					copyDates();
				}
			});

			// Botón de Perder
			lostButton.setStyleName("formCloseButton");
			lostButton.setVisible(true);
			lostButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("¿Está seguro que desea Perder la Oportunidad?")) 
						prepareLost();
				}
			});

			copyOpportunityDialogButton.setStyleName("formCloseButton");
			copyOpportunityDialogButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyOpportunityDialog();
				}
			});

			copyOpportunityButton.setStyleName("formCloseButton");
			copyOpportunityButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {				
					copyOpportunityAction();
					copyOpportunityDialogBox.hide();
				}
			});

			copyOpportunitynCloseDialogButton.setStyleName("formCloseButton");
			copyOpportunitynCloseDialogButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyOpportunityDialogBox.hide();
				}
			});

			// Botón de cambiar tipo de flujo
			changeWFlowTypeButton.setStyleName("formCloseButton");
			changeWFlowTypeButton.setVisible(true);
			changeWFlowTypeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("¿Está seguro que desea cambiar el WorkFlow?")) 
						changeWFlowAction();
				}
			});
		}

		@Override
		public void populateFields() {				
			bmoOpportunity = (BmoOpportunity)getBmObject();
			populateStatusListBox();
			ValueChangeHandler<String> sumAmount = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {	

					amountTextBox.setText(String.valueOf(Double.parseDouble(amountLicenseTextBox.getText()) + Double.parseDouble(amountServiceTextBox.getText())));

				}
			};

			amountLicenseTextBox.addValueChangeHandler(sumAmount);
			amountServiceTextBox.addValueChangeHandler(sumAmount);
			
			// MultiEmpresa: g100
			multiCompany = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean();

			// Asignacion inicial de datos
			try {
				// Si no esta asignado el tipo, buscar por el default
				//				if (!(bmoOpportunity.getOrderTypeId().toInteger() > 0)) {			
				//					bmoOpportunity.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
				//				}	

				// Asigna empresa si es registro nuevo
				if (!(bmoOpportunity.getCompanyId().toInteger() > 0)) {	
					bmoOpportunity.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());
				}

				if (newRecord) {
					bmoOpportunity.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

					// Si esta desHabilitado el control presupuestal y si no esta asignado el tipo, buscar por el default
					//					if (!(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
					//						if (!(bmoOpportunity.getOrderTypeId().toString().length() > 0)) 
					//							bmoOpportunity.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
					//					}

					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoOpportunity.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());					
				} else {
					// Asignar datos del flujo a los datos del flujo a cambiar
					if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEWFLOWTYPE)) {
						newWFlowTypeFieldId.setValue(bmoOpportunity.getWFlowTypeId().toInteger());
						newEffectWFlowTypeFieldId.setValue(bmoOpportunity.getForeignWFlowTypeId().toInteger());
					}
				}
				
				// Busqueda normal y MultiEmpresa: g100
				setProfileSalesmanByCompanyIdFilters("" + bmoOpportunity.getCompanyId().toInteger(), multiCompany);
				setCustomerListBoxFilters(bmoOpportunity.getCompanyId().toInteger(), multiCompany);

				if(bmoOpportunity.getCustomerId().toInteger()>0) {
					populateCustcomer(customerId);
				}

				// Si no esta asignada la moneda, buscar por la default
				if (!(bmoOpportunity.getCurrencyId().toInteger() > 0)) {
					bmoOpportunity.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
					populateParityFromCurrency(bmoOpportunity.getCurrencyId().toString());
				}

				//					}else {
				// Filtrar contactos del cliente
				BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
				BmFilter contactsByCustomer = new BmFilter();
				contactsByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoOpportunity.getCustomerId().toInteger());
				customerContactIdListBox.addFilter(contactsByCustomer);
				//					}
				// Filtro de tipos de flujo en categorias del modulo Oportunidad
				bmoWFlowType = new BmoWFlowType();
				BmFilter bmFilterWFlowTypeByProgram = new BmFilter();
				bmFilterWFlowTypeByProgram.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
				wFlowTypeListBox.addFilter(bmFilterWFlowTypeByProgram);

				newWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilterWFlowTypeByProgram);

				/*
					// Filtro de flujos en categoria agregada al tipo de pedido
					BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
					BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
					BmFilter bmFilterByWFlowCategories = new BmFilter();
					bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
							bmoWFlowCategory.getIdFieldName(), 
							bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
							bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
							"" + bmoOpportunity.getOrderTypeId());
					wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
				 */


				// Filtro inicial Efectos flujo
				BmFilter categoryFilter = new BmFilter();
				categoryFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getIdField(), bmoOpportunity.getBmoWFlowType().getBmoWFlowCategory().getEffectWFlowCategoryId().toInteger());
				effectWFlowTypeListBox.addFilter(categoryFilter);

				// Filtro inicial Copiar Efecto Flujo
				newEffectWFlowTypeListBox= new UiListBox(getUiParams(), new BmoWFlowType(), categoryFilter);
				
				if (!newRecord) {
					if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
						payConditionUiListBox.setSelectedId("" + bmoOpportunity.getPayConditionId().toInteger());
				}

			} catch (BmException e) {
				showSystemMessage("Error al asignar datos: " + e.toString());
			}

			UiCustomerForm uiCustomerForm = new UiCustomer(getUiParams()).getUiCustomerForm();
			UiVenueForm uiVenueForm = new UiVenue(getUiParams()).getUiVenueForm();
			// Campos de la forma
			formFlexTable.addSectionLabel(0, 0, generalSection, 2);
			
			if (multiCompany)
				formFlexTable.addField(1, 0, companyListBox, bmoOpportunity.getCompanyId());
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoOpportunity.getCode());
			formFlexTable.addField(3, 0, orderTypeListBox, bmoOpportunity.getOrderTypeId());	

			// MultiEmpresa: g100
			setWFlowTypeListBoxFilters(bmoOpportunity.getCompanyId().toInteger(), multiCompany);
			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoOpportunity.getWFlowTypeId());
			formFlexTable.addField(5, 0, effectWFlowTypeListBox, bmoOpportunity.getForeignWFlowTypeId());
			formFlexTable.addField(6, 0, nameTextBox, bmoOpportunity.getName());
			formFlexTable.addField(7, 0, descriptionTextArea, bmoOpportunity.getDescription());
			formFlexTable.addField(8, 0, customerSuggestBox, bmoOpportunity.getCustomerId(), uiCustomerForm, new BmoCustomer());
			formFlexTable.addField(9, 0, customerContactIdListBox, bmoOpportunity.getCustomerContactId());  
			// MultiEmpresa: g100
			if (!multiCompany)
				formFlexTable.addField(10, 0, companyListBox, bmoOpportunity.getCompanyId());
			formFlexTable.addField(11, 0, marketListBox, bmoOpportunity.getMarketId());
			formFlexTable.addField(12, 0, userSuggestBox, bmoOpportunity.getUserId());
			formFlexTable.addField(13, 0, propertyModelSuggestBox, bmoOpportunity.getPropertyModelId());
			formFlexTable.addField(14, 0, venueSuggestBox, bmoOpportunity.getVenueId(), uiVenueForm, new BmoVenue());	
			formFlexTable.addField(15, 0, customField4TextBox, bmoOpportunity.getCustomField4()); // se tomo para drea
			//			setCustomerAddressListBoxFilters(customerSuggestBox.getSelectedId());
			//			formFlexTable.addField(14, 0, customerAddressListBox, bmoOpportunity.getCustomerAddressId());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				// Asigna filtros de control presupuestal
				setBudgetItemsListBoxFilters(bmoOpportunity.getCompanyId().toInteger());
				formFlexTable.addField(16, 0, budgetItemUiListBox, bmoOpportunity.getBudgetItemId());
				formFlexTable.addField(17, 0, areaUiListBox, bmoOpportunity.getAreaId());
			}
			formFlexTable.addField(18, 0, startDateTimeBox, bmoOpportunity.getStartDate());
			formFlexTable.addField(19, 0, endDateTimeBox, bmoOpportunity.getEndDate());

			formFlexTable.addSectionLabel(20, 0, valueSection, 2);
			formFlexTable.addField(21, 0, saleProbabilityListBox, bmoOpportunity.getSaleProbability());

			formFlexTable.addField(24, 0, saleDateBox, bmoOpportunity.getSaleDate());
			formFlexTable.addField(25, 0, fiscalPeriodTextBox, bmoOpportunity.getFiscalPeriod());
			formFlexTable.addField(26, 0, fiscalYearTextBox, bmoOpportunity.getFiscalYear());
			formFlexTable.addField(27, 0, amountLicenseTextBox,bmoOpportunity.getAmountLicense());
			formFlexTable.addField(28, 0, amountServiceTextBox,bmoOpportunity.getAmountService());
			formFlexTable.addField(29, 0, amountTextBox, bmoOpportunity.getAmount());
			formFlexTable.addField(30, 0, expireDateBox, bmoOpportunity.getExpireDate());
			formFlexTable.addField(31, 0, currencyListBox, bmoOpportunity.getCurrencyId());
			formFlexTable.addField(32, 0, currencyParityTextBox, bmoOpportunity.getCurrencyParity());

			formFlexTable.addSectionLabel(33, 0, extraSection, 2);
			formFlexTable.addField(35, 0, categoryForestcastIdListBox, bmoOpportunity.getCategoryForecastId());

			formFlexTable.addField(36, 0, leadDateBox, bmoOpportunity.getLeadDate());
			if(!(newRecord)) {
				if (getSFParams().hasRead(new BmoOpportunityCompetition().getProgramCode()))
					formFlexTable.addField(37, 0, new UiOpportunityCompetitionLabelList(getUiParams(), id));			
			} 
			formFlexTable.addField(38, 0, compvsposTextArea, bmoOpportunity.getCompvspos());
			formFlexTable.addField(39, 0, commercialTermsTextArea, bmoOpportunity.getCommercialTerms());
			if (newRecord) {
				if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
					formFlexTable.addField(40, 0, payConditionUiListBox, bmoOpportunity.getPayConditionId());
			}
			formFlexTable.addField(41, 0, customerRequisitionTextBox, bmoOpportunity.getCustomerRequisition());
			formFlexTable.addField(42, 0, customField1TextBox, bmoOpportunity.getCustomField1());
			formFlexTable.addField(43, 0, customField2TextBox, bmoOpportunity.getCustomField2());
			formFlexTable.addField(44, 0, customField3TextBox, bmoOpportunity.getCustomField3());
			formFlexTable.addField(45, 0, tagBox, bmoOpportunity.getTags());

			populateSaleProbabilityListBox();

			if (!newRecord) {
				formFlexTable.addSectionLabel(46, 0, loseSection, 2);
				formFlexTable.addField(47, 0, loseMotiveListBox, bmoOpportunity.getLoseMotiveId());
				formFlexTable.addField(48, 0, loseCommentsTextArea, bmoOpportunity.getLoseComments());
				if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_REVISION) || bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_HOLD) || bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_EXPIRED) ) 
					formFlexTable.addButtonCell(49, 1, lostButton);

				if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_REVISION)
						|| bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_HOLD)
						&& getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEWFLOWTYPE)) {
					formFlexTable.addSectionLabel(50, 0, changeWFlowTypeSection, 2);
					changeOrderTypeOnChangeWFlowType(bmoOpportunity.getBmoOrderType());
					formFlexTable.addField(51, 0, newWFlowTypeListBox, newWFlowTypeFieldId);
					formFlexTable.addField(52, 0, newEffectWFlowTypeListBox, newEffectWFlowTypeFieldId);
					formFlexTable.addButtonCell(53, 1, changeWFlowTypeButton);	
				}
			}

			//estatus de la oportundiad opo
			formFlexTable.addSectionLabel(54, 0, statusSection, 2);
			formFlexTable.addField(55, 0, statusListBox, bmoOpportunity.getStatus());

			if (newRecord) {
				formFlexTable.hideField(bmoOpportunity.getPropertyModelId());
				formFlexTable.hideField(bmoOpportunity.getVenueId());
			}

			formFlexTable.hideSection(extraSection);
			formFlexTable.hideSection(loseSection);
			if(bmoOpportunity.getQuoteId().toInteger() > 0) {
				getQuote(true);
			}

			statusEffect();
		}
		

		@Override
		public void postShow() {
			if (newRecord) {
				buttonPanel.add(copyOpportunityDialogButton);
				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_COPYOPPORTUNITY)) 
					copyOpportunityDialogButton.setVisible(true);
				else
					copyOpportunityDialogButton.setVisible(false);
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			// Si el movimiento es en el lista de tipos de flujo de la oportunidad, modificar el de los efectos
			if (event.getSource() == wFlowTypeListBox) {
				resetEffectWFlowType();
			} else if (event.getSource() == orderTypeListBox) {
				BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
				if (bmoOrderType != null) {
					// Modificar la lista de Tipos de Flujo
					changeOrderType(bmoOrderType);

					// Traer dpto. y partida por defecto del pedido
					if (bmoOrderType.getDefaultBudgetItemId().toInteger() > 0)
						budgetItemUiListBox.setSelectedId("" + bmoOrderType.getDefaultBudgetItemId().toInteger());
					else
						budgetItemUiListBox.setSelectedIndex(0);
					if (bmoOrderType.getDefaultAreaId().toInteger() > 0)
						areaUiListBox.setSelectedId("" + bmoOrderType.getDefaultAreaId().toInteger());
					else
						areaUiListBox.setSelectedIndex(0);
					
					// Filtrar modelos por empresa
					if (bmoOrderType.getType().toChar() == BmoOrderType.TYPE_PROPERTY)
						if (Integer.parseInt(companyListBox.getSelectedId()) > 0)
							populatePropertyModelByCompany(Integer.parseInt(companyListBox.getSelectedId()), multiCompany);
				} else {
					wFlowTypeListBox.clear();
					wFlowTypeListBox.clearFilters();
					effectWFlowTypeListBox.clear();
					effectWFlowTypeListBox.clearFilters();
					budgetItemUiListBox.setSelectedIndex(0);
					areaUiListBox.setSelectedIndex(0);
				}
			} else if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status de la Oportunidad?");
			} else if (event.getSource() == currencyListBox) {
				getParityFromCurrency(currencyListBox.getSelectedId());
			} 
			//			else if (event.getSource() == customerAddressListBox) {
			//				// Desactivar Salones si hay una Direccion del cliente seleccionada
			//				if (Integer.parseInt(customerAddressListBox.getSelectedId()) > 0) {
			//						venueSuggestBox.clear();
			//						venueSuggestBox.setEnabled(false);
			//				} else {
			//					venueSuggestBox.setEnabled(true);
			//				}
			//				customerAddressListBox.setEnabled(true);
			//			} 
			else if (event.getSource() == companyListBox) {
				BmoCompany bmoCompany = (BmoCompany)companyListBox.getSelectedBmObject();
				if (bmoCompany == null) {
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)
						populateBudgetItems(-1);
					
					// MultiEmpresa: g100
					// Limpiar widget clientes
					populateCustomers(-1, multiCompany);

				} else {
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) 
						populateBudgetItems(bmoCompany.getId());
					
					// MultiEmpresa: g100
					// Filtrar usuarios
					populateUsers(bmoCompany.getId(), multiCompany);
					// Filtrar clientes
					if (newRecord)
						populateCustomers(bmoCompany.getId(), multiCompany);
					
					// Filtrar modelos por empresa
					if (Integer.parseInt(orderTypeListBox.getSelectedId()) > 0) {
						BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
						if (bmoOrderType != null) {
							if (bmoOrderType.getType().toChar() == BmoOrderType.TYPE_PROPERTY)
								populatePropertyModelByCompany(bmoCompany.getId(), multiCompany);
							
							// Si esta lleno el tipo de pedido, ejecutar como si hubieran cambiado el tipo
							if (multiCompany)
								changeOrderType(bmoOrderType);
						}
					}
					
				}
				
			} else if (event.getSource() == newWFlowTypeListBox) {
				resetEffectWFlowTypeOnChangeWFlowType();
			}
			statusEffect();
		}

		// Cambia el tipo de pedido y modifica combo de Tipos de Flujo
		private void changeOrderType(BmoOrderType bmoOrderType) {
			wFlowTypeListBox.clear();
			wFlowTypeListBox.clearFilters();
			if (!multiCompany)
				wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());

			// Limpia tambien el efecto
			effectWFlowTypeListBox.clear();
			effectWFlowTypeListBox.clearFilters();

			// Agregar filtros al tipo de flujo
			// Filtro de tipos de flujo en categorias del modulo Oportunidad
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
			filterList.add(bmFilter);

			// Filtros de tipos de flujos activos
			BmFilter bmFilterByStatus = new BmFilter();
			bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
			filterList.add(bmFilterByStatus);

			// Filtro de flujos en categoria agregada al tipo de pedido
			BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			BmFilter bmFilterByWFlowCategories = new BmFilter();
			bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
					bmoWFlowCategory.getIdFieldName(), 
					bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
					bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
					"" + bmoOrderType.getId());

			wFlowTypeListBox.addFilter(bmFilter); 
			
			// MultiEmpresa: g100
			// Filtrar tipos de flujo por empresa
			if (multiCompany && (Integer.parseInt(companyListBox.getSelectedId()) > 0)) {
				BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
				BmFilter bmFilterByWFlowTypeCompany = new BmFilter();
				bmFilterByWFlowTypeCompany.setInFilter(bmoWFlowTypeCompany.getKind(), 
						bmoWFlowType.getIdFieldName(), 
						bmoWFlowTypeCompany.getWflowTypeId().getName(), 
						bmoWFlowTypeCompany.getCompanyId().getName(), 
						"" + companyListBox.getSelectedId());
				wFlowTypeListBox.addFilter(bmFilterByWFlowTypeCompany);
			}

			// Si el registro ya existia, es posible que se haya deshabilitado flujo, asegura que se muestre
			if (newRecord) {
				wFlowTypeListBox.addFilter(bmFilterByStatus);
				wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
			}

			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoOpportunity.getWFlowTypeId());
//			wFlowTypeListBox.populate(bmoOpportunity.getWFlowTypeId().toString(), false); // Se duplican registros
		}

		// Con Permiso Cambiar Tipo de Flujo: Cambia el tipo de pedido y modifica combo de Tipos de Flujo
		private void changeOrderTypeOnChangeWFlowType(BmoOrderType bmoOrderType) {

			// Agregar filtros al tipo de flujo
			// Filtro de tipos de flujo en categorias del modulo Oportunidad
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
			filterList.add(bmFilter);

			// Filtros de tipos de flujos activos
			BmFilter bmFilterByStatus = new BmFilter();
			bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
			filterList.add(bmFilterByStatus);

			// Filtro de flujos en categoria agregada al tipo de pedido
			BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			BmFilter bmFilterByWFlowCategories = new BmFilter();
			bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
					bmoWFlowCategory.getIdFieldName(), 
					bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
					bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
					"" + bmoOrderType.getId());

			newWFlowTypeListBox.addFilter(bmFilter); 
			newWFlowTypeListBox.addFilter(bmFilterByStatus);
			newWFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
			
			// MultiEmpresa: g100
			// Filtrar tipos de flujo por empresa
			if (multiCompany && (Integer.parseInt(companyListBox.getSelectedId()) > 0)) {
				BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
				BmFilter bmFilterByWFlowTypeCompany = new BmFilter();
				bmFilterByWFlowTypeCompany.setInFilter(bmoWFlowTypeCompany.getKind(), 
						bmoWFlowType.getIdFieldName(), 
						bmoWFlowTypeCompany.getWflowTypeId().getName(), 
						bmoWFlowTypeCompany.getCompanyId().getName(), 
						"" + companyListBox.getSelectedId());
				newWFlowTypeListBox.addFilter(bmFilterByWFlowTypeCompany);
			}
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == startDateTimeBox) {
				if (!newRecord) {
					getQuote(false);

				}
			}
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {

			if (uiSuggestBox == propertyModelSuggestBox) {
				if (!multiCompany) {
					BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
					bmoPropertyModel = (BmoPropertyModel)propertyModelSuggestBox.getSelectedBmObject();
					if (bmoPropertyModel != null) {
						populateCompany(bmoPropertyModel.getBmoDevelopment().getCompanyId().toInteger());
						populateBudgetItems(bmoPropertyModel.getBmoDevelopment().getCompanyId().toInteger());
					} else 
						populateCompany(-1);
	
					statusEffect();
				}
			} else if (uiSuggestBox == customerSuggestBox) {
				// Llenar moneda del cliente
				BmoCustomer bmoCustomer = new BmoCustomer();
				bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
				if (bmoCustomer != null) {
					if (bmoCustomer.getCurrencyId().toInteger() > 0) {
						currencyListBox.setSelectedId("" + bmoCustomer.getCurrencyId().toInteger());
						populateParityFromCurrency(bmoCustomer.getCurrencyId().toString());
					}
					if (bmoCustomer.getSalesmanId().toInteger() > 0) {
						userSuggestBox.setSelectedId("" + bmoCustomer.getSalesmanId().toInteger());
						userSuggestBox.setSelectedBmObject(bmoCustomer.getBmoUser());
						userSuggestBox.setText(((BmoUser)userSuggestBox.getSelectedBmObject()).getFirstname().toString() + 
											" : " + ((BmoUser)userSuggestBox.getSelectedBmObject()).getFatherlastname().toString());
					}
					if (bmoCustomer.getMarketId().toInteger() > 0) {
						marketListBox.setSelectedId("" + bmoCustomer.getMarketId().toInteger());
					} else marketListBox.setSelectedId("0");
					
					populateCustomerContact(bmoCustomer.getId());
					payConditionUiListBox.setSelectedId("" + bmoCustomer.getPayConditionId().toInteger());
				} else {
					userSuggestBox.clear();
					marketListBox.setSelectedId("0");
				}
				
				statusEffect();
			} else if (uiSuggestBox == venueSuggestBox) {
				// Activar campo si esta marcado el Salon como casa particular
				BmoVenue bmoVenue = (BmoVenue)venueSuggestBox.getSelectedBmObject();
				
				if (bmoVenue != null) {
					if (bmoVenue.getHomeAddress().toBoolean() && bmoVenue.getId() > 0) {
						formFlexTable.showField(bmoOpportunity.getCustomField4());
						//customField4TextBox.setText("");
					}
				} else  {
					//customField4TextBox.setText("");
					formFlexTable.hideField(bmoOpportunity.getCustomField4());
				}
			}
		}
		
		// Filtrar vendedores por perfil/empresa
//		private void populateWFlowType(int companyId, boolean multiCompany) {
//			if (multiCompany && companyId > 0)
//				setWFlowTypeListBoxFilters(companyId, multiCompany);
//		}
		
		// Filtrar tipos de flujo por empresa
		private void setWFlowTypeListBoxFilters(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				// Filtro de flujos en categoria agregada al tipo de pedido
				BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
				BmFilter bmFilterByWFlowTypeCompany = new BmFilter();
				bmFilterByWFlowTypeCompany.setInFilter(bmoWFlowTypeCompany.getKind(), 
						bmoWFlowType.getIdFieldName(), 
						bmoWFlowTypeCompany.getWflowTypeId().getName(), 
						bmoWFlowTypeCompany.getCompanyId().getName(), 
						"" + companyId);
				wFlowTypeListBox.addFilter(bmFilterByWFlowTypeCompany);
			}
		}
		
		// Filtrar vendedores por perfil/empresa
		private void populateUsers(int companyId, boolean multiCompany) {
			if (newRecord) userSuggestBox.clear();
			if (multiCompany && companyId > 0)
				setProfileSalesmanByCompanyIdFilters("" + companyId, multiCompany);
			else {
				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				setUserListBoxFilters("" + salesProfileId);
			}
		}

		// Filtrar usuarios por perfil de vendedores 
		private void setUserListBoxFilters(String salesProfileId) {
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + salesProfileId);	
			userSuggestBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);
		}
		
		// Buscar perfil del vendedor POR empresa
		public void setProfileSalesmanByCompanyIdFilters(String companyId, boolean multiCompany) {
			if (multiCompany) {
				searchProfileSalesmanByCompanyId(companyId, multiCompany , 0);
			} else {
				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				setUserListBoxFilters("" + salesProfileId);
			}
		};

		// Buscar perfil del vendedor POR empresa
		public void searchProfileSalesmanByCompanyId(String companyId,  boolean multiCompany, int profileSalesmanRpcAttempt) {
			if (profileSalesmanRpcAttempt < 5) {
				setCompanyId(companyId);
				setProfileSalesmanRpcAttempt(profileSalesmanRpcAttempt + 1);
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getProfileSalesmanRpcAttempt() < 5) {
							searchProfileSalesmanByCompanyId(getCompanyId(), multiCompany, getProfileSalesmanRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-searchProfileSalesmanByCompanyId() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();	
						setProfileSalesmanRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Perfil de la Empresa.");
						else {
							// Aplicar filtro
							setUserListBoxFilters(result.getMsg());
						}
					}
				};

				try {	
					startLoading();
					BmoFlexConfig bmoFlexConfig = new BmoFlexConfig();
					getUiParams().getBmObjectServiceAsync().action(bmoFlexConfig.getPmClass(), bmoFlexConfig, BmoFlexConfig.ACTION_SEARCHPROFILESALESMAN, "" + companyId, callback);


				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-searchProfileSalesmanByCompanyId() ERROR: " + e.toString());
				}
			}
		}
		
		// Filtrar clientes por empresa
		private void populateCustomers(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				customerSuggestBox.clear();
				setCustomerListBoxFilters(companyId, multiCompany);
			}
		}

		// Filtrar clientes por empresa 
		private void setCustomerListBoxFilters(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
				BmFilter filterCustomer = new BmFilter();
				BmoCustomer bmocustomer = new BmoCustomer();
				filterCustomer.setInFilter(bmoCustomerCompany.getKind(), 
						bmocustomer.getIdFieldName(),
						bmoCustomerCompany.getCustomerId().getName(),
						bmoCustomerCompany.getCompanyId().getName(),
						"" + companyId);	
				customerSuggestBox.addFilter(filterCustomer);
			}
		}
		
		private void populatePropertyModelByCompany(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				propertyModelSuggestBox.clear();
				setPropertyModelListBoxFilters(companyId, multiCompany);
			}
		}

		private void setPropertyModelListBoxFilters(int companyId, boolean multiCompany) {
			if (multiCompany && companyId > 0) {
				BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
				BmFilter bmFilterByCompany = new BmFilter();
				if (companyId > 0) {
					bmFilterByCompany.setValueFilter(bmoPropertyModel.getKind(), bmoPropertyModel.getBmoDevelopment().getCompanyId(), companyId);
					propertyModelSuggestBox.addFilter(bmFilterByCompany);		
				} else {
					bmFilterByCompany.setValueFilter(bmoPropertyModel.getKind(), bmoPropertyModel.getBmoDevelopment().getCompanyId(), -1);
					propertyModelSuggestBox.addFilter(bmFilterByCompany);		
	
				}
			}
		}

		private void populateCompany(int companyId) {
			companyListBox.clear();
			companyListBox.clearFilters();
			if (companyId > 0)
				setCompanyListBoxFilters(companyId);
			companyListBox.populate("" + companyId);
		}

		private void setCompanyListBoxFilters(int companyId) {
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter bmFilterByCompany = new BmFilter();
			bmFilterByCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
			companyListBox.addBmFilter(bmFilterByCompany);		
		}

		//Cargar contactos del cliente
		private void populateCustomerContact(int customerId) {
			customerContactIdListBox.clear();
			customerContactIdListBox.clearFilters();
			if (customerId > 0)
				setCustomerContactListBoxFilters(customerId);
			customerContactIdListBox.populate("" + customerId);
			callMainContasct(""+customerId);
		}

		private void setCustomerContactListBoxFilters(int customerId) {
			BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
			BmFilter bmFilterByCustomerContact = new BmFilter();
			bmFilterByCustomerContact.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), customerId);
			customerContactIdListBox.addBmFilter(bmFilterByCustomerContact);		
		}

		// Obtener la paridad de la moneda
		public void populateParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId);
		}
		public void populateCustcomer(String customerId) {
			callMainContasct(customerId);
		}
		//		// Actualiza combo de direcciones del cliente
		//		private void populateCustomerAddress(int customerId) {
		//			customerAddressListBox.clear();
		//			customerAddressListBox.clearFilters();
		//			setCustomerAddressListBoxFilters(customerId);
		//			if (customerId > 0)
		//				customerAddressListBox.populate(bmoOpportunity.getCustomerAddressId().toString());
		//		}
		//
		//		// Asigna filtros al listado de direcciones del cliente
		//		private void setCustomerAddressListBoxFilters(int customerId) {
		//			BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		//			BmFilter bmFilterByCustomer = new BmFilter();
		//
		//			if (customerId > 0) {
		//				bmFilterByCustomer.setValueFilter(bmoCustomerAddress.getKind(), bmoCustomerAddress.getCustomerId(), customerId);
		//				customerAddressListBox.addBmFilter(bmFilterByCustomer);
		//			} else {
		//				bmFilterByCustomer.setValueFilter(bmoCustomerAddress.getKind(), bmoCustomerAddress.getIdField(), -1);
		//				customerAddressListBox.addBmFilter(bmFilterByCustomer);
		//			}
		//		}

		private void statusEffect() {
			statusListBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			expireDateBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			orderTypeListBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			startDateTimeBox.setEnabled(false);
			endDateTimeBox.setEnabled(false);	
			nameTextBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			saleProbabilityListBox.setEnabled(false);
			saleDateBox.setEnabled(false);
			amountTextBox.setEnabled(false);
			effectWFlowTypeListBox.setEnabled(false);
			copyDates.setEnabled(false);
			companyListBox.setEnabled(false);
			marketListBox.setEnabled(false);
			customerRequisitionTextBox.setEnabled(false);
			fiscalPeriodTextBox.setEnabled(false);
			fiscalYearTextBox.setEnabled(false);
			
			if(bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_WON)) {
				budgetItemUiListBox.setEnabled(false);
				areaUiListBox.setEnabled(false);
			}

			//lostButton.setVisible(false);

			formFlexTable.hideField(bmoOpportunity.getFiscalPeriod());
			formFlexTable.hideField(bmoOpportunity.getFiscalYear());
			formFlexTable.hideField(bmoOpportunity.getCustomField4());

			// Obtener tipo de pedido
			BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
			if (bmoOrderType == null) {
				bmoOrderType = bmoOpportunity.getBmoOrderType();
			}

			if (newRecord) {
				customerSuggestBox.setEnabled(true);
				orderTypeListBox.setEnabled(true);
				userSuggestBox.setEnabled(true);
				companyListBox.setEnabled(true);
				marketListBox.setEnabled(true);

				if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
					currencyListBox.setEnabled(true);
					currencyParityTextBox.setEnabled(true);
				} else {
					currencyListBox.setEnabled(true);
					currencyParityTextBox.setEnabled(false);
				}
			} else {
				if (bmoOpportunity.getBmoWFlowType().getId() > 0) {
					// Si es un registro anterior y ya esta asignado el tipo de oportunidad, no dejar cambiarlo
					wFlowTypeListBox.setEnabled(false);
					// Si tiene permiso para modificar status, habilitar combo
					if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESTATUS)) 
						statusListBox.setEnabled(true);
				}

				// Mostrar campos si esta habilitado en el tipo de pedido
				if (bmoOrderType.getDataFiscal().toBoolean()) {
					formFlexTable.showField(bmoOpportunity.getFiscalPeriod());
					formFlexTable.showField(bmoOpportunity.getFiscalYear());
				}

				if (bmoOrderType.getType().equals(BmoOrderType.TYPE_PROPERTY)) {
					propertyModelSuggestBox.setEnabled(false);
					formFlexTable.showField(bmoOpportunity.getPropertyModelId());
				} else 
					formFlexTable.hideField(bmoOpportunity.getPropertyModelId());
				
			}

			if (bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_REVISION) || bmoOpportunity.getStatus().equals(BmoOpportunity.STATUS_HOLD)) {
				nameTextBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				saleProbabilityListBox.setEnabled(true);
				saleDateBox.setEnabled(true);
				amountTextBox.setEnabled(true);
				effectWFlowTypeListBox.setEnabled(true);
				copyDates.setEnabled(true);
				customerRequisitionTextBox.setEnabled(true);
				marketListBox.setEnabled(true);
				if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
					currencyListBox.setEnabled(true);
					currencyParityTextBox.setEnabled(true);
				} else {
					currencyListBox.setEnabled(true);
					currencyParityTextBox.setEnabled(false);
				}

				//				// Acceso especial cambiar estatus, habilita boton de perder
				//				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESTATUS)) 
				//					lostButton.setVisible(true);

				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_LOSEOPPORTUNITY)) 
					lostButton.setVisible(true);

				// Acceso especial cambiar vendedor
				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMAN))
					userSuggestBox.setEnabled(true);

				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGESALESMANINQUOTAUT))
					userSuggestBox.setEnabled(true);

				// Acceso especial cambiar expiracion
				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGEEXPIREDATE))
					expireDateBox.setEnabled(true);

				// Acceso especial para cambiar cliente 
				if (getSFParams().hasSpecialAccess(BmoOpportunity.ACCESS_CHANGECUSTOMER)) 
					customerSuggestBox.setEnabled(true);		

				// Tipo de Pedido Renta
				if (bmoOrderType.getType().equals(BmoOrderType.TYPE_RENTAL)) {
					// Ocultar campos
					formFlexTable.hideField(bmoOpportunity.getPropertyModelId());

					// Mostrar campos
					formFlexTable.showField(bmoOpportunity.getStartDate());
					formFlexTable.showField(bmoOpportunity.getEndDate());
					formFlexTable.showField(bmoOpportunity.getVenueId());
					//formFlexTable.showField(bmoOpportunity.getCustomerAddressId());

					startDateTimeBox.setEnabled(true);
					endDateTimeBox.setEnabled(true);
					nameTextBox.setEnabled(true);
					companyListBox.setEnabled(true);

					if (!customField4TextBox.getText().equals(""))
						formFlexTable.showField(bmoOpportunity.getCustomField4());

					// Activar campos si no hay seleccion por parte de los dos
					//					if (!(venueSuggestBox.getSelectedId() > 0) && !(Integer.parseInt(customerAddressListBox.getSelectedId()) > 0)) {
					//						venueSuggestBox.setEnabled(true);
					//						customerAddressListBox.setEnabled(true);
					//					} else {
					//						// Habilitar campo de salon si existe, deshabilitar dir. cliente
					//						if (venueSuggestBox.getSelectedId() > 0) {
					//							venueSuggestBox.setEnabled(true);
					//							customerAddressListBox.setEnabled(false);
					//						}
					//
					//						// Habilitar campo de dir. cliente si existe, deshabilitar salon
					//						if (Integer.parseInt(customerAddressListBox.getSelectedId()) > 0) {
					//							customerAddressListBox.setEnabled(true);
					//							venueSuggestBox.setEnabled(false);
					//						}
					//					}
				} 
				// Tipo de Pedido Venta
				else if (bmoOrderType.getType().equals(BmoOrderType.TYPE_SALE)) {

					startDateTimeBox.setEnabled(true);
					endDateTimeBox.setEnabled(true);
					nameTextBox.setEnabled(true);
					companyListBox.setEnabled(true);

					// Mostrar campos
					formFlexTable.showField(bmoOpportunity.getStartDate());
					formFlexTable.showField(bmoOpportunity.getEndDate());

					// Ocultar campos
					formFlexTable.hideField(bmoOpportunity.getPropertyModelId());
					formFlexTable.hideField(bmoOpportunity.getVenueId());
					//formFlexTable.hideField(bmoOpportunity.getCustomerAddressId());
				}
				// Tipo de Pedido Servicio
				else if (bmoOrderType.getType().equals(BmoOrderType.TYPE_CONSULTANCY)) {
					// Mostrar campos
					formFlexTable.showField(bmoOpportunity.getStartDate());
					formFlexTable.showField(bmoOpportunity.getEndDate());
					// Ocultar campos
					formFlexTable.hideField(bmoOpportunity.getPropertyModelId());
					formFlexTable.hideField(bmoOpportunity.getVenueId());
					//formFlexTable.hideField(bmoOpportunity.getCustomerAddressId());

					startDateTimeBox.setEnabled(true);
					endDateTimeBox.setEnabled(true);
					nameTextBox.setEnabled(true);
					companyListBox.setEnabled(true);
				}
				// Tipo de pedido inmueble
				else if (bmoOrderType.getType().equals(BmoOrderType.TYPE_PROPERTY)) {
					// Mostrar campos
					if (!(propertyModelSuggestBox.getSelectedId() > 0)) {
						formFlexTable.showField(bmoOpportunity.getPropertyModelId());
					}
					// Ocultar campos
					formFlexTable.hideField(bmoOpportunity.getStartDate());
					formFlexTable.hideField(bmoOpportunity.getEndDate());
					formFlexTable.hideField(bmoOpportunity.getVenueId());
					//formFlexTable.hideField(bmoOpportunity.getCustomerAddressId());

					if (!multiCompany)
						companyListBox.setEnabled(false);
					nameTextBox.setEnabled(false);
					
					// Habilitar nombre, ya que no es requerido el modelo
					if (newRecord && !bmoOrderType.getRequiredPropertyModel().toBoolean()) {
						nameTextBox.setEnabled(true);
					}
					
					// Existe un efecto en Tipo de Pedido, colocarlo
					if (newRecord && bmoOrderType.getDefaultWFlowTypeId().toInteger() > 0) {
						effectWFlowTypeListBox.setSelectedId("" + bmoOrderType.getDefaultWFlowTypeId().toInteger());
					}
				} else {
					formFlexTable.hideField(bmoOpportunity.getPropertyModelId());
					formFlexTable.hideField(bmoOpportunity.getVenueId());
					//formFlexTable.hideField(bmoOpportunity.getCustomerAddressId());
					formFlexTable.hideField(bmoOpportunity.getCustomField4());
				}
			}

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);		
			if(bmoQuote.getStatus().equals(BmoQuote.STATUS_AUTHORIZED)) {
				amountLicenseTextBox.setEnabled(false);
				amountServiceTextBox.setEnabled(false);
			}
		}



		public void populateStatusListBox() {


			ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();

			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusRevision().toBoolean())) {
				status.add(new BmFieldOption(BmoOpportunity.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusGanada().toBoolean())) {
				status.add(new BmFieldOption(BmoOpportunity.STATUS_WON, "Ganada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_authorized.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusPerdida().toBoolean())) {
				status.add(new BmFieldOption(BmoOpportunity.STATUS_LOST, "Perdida", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusExpirada().toBoolean())) {
				status.add(new BmFieldOption(BmoOpportunity.STATUS_EXPIRED, "Expirado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_expired.png")));
			}
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusHold().toBoolean())) {
				status.add(new BmFieldOption(BmoOpportunity.STATUS_HOLD, "Detenido", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_on_hold.png")));
			}

			bmoOpportunity.getStatus().setOptionList(status);
		}
		private void copyDates() {
			endDateTimeBox.setDateTime(startDateTimeBox.getDateTime());
		}

		public void copyOpportunityDialog() {
			copyOpportunityDialogBox = new DialogBox(true);
			copyOpportunityDialogBox.setGlassEnabled(true);
			copyOpportunityDialogBox.setText("Copiar " + getSFParams().getProgramTitle(bmoOpportunity));
			copyOpportunityDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			copyOpportunityDialogBox.setWidget(vp);

			UiFormFlexTable formCopyOpportunityTable = new UiFormFlexTable(getUiParams());

			BmoOpportunity fromBmoOpportunity = new BmoOpportunity();
			formCopyOpportunityTable.addField(1, 0, copyOpportunitySuggestBox, fromBmoOpportunity.getIdField());
			BmoCustomer fromBmoCustomer = new BmoCustomer();
			formCopyOpportunityTable.addField(2, 0, customerSuggestBox, fromBmoCustomer.getIdField());


			HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
			changeStaffButtonPanel.add(copyOpportunityButton);
			changeStaffButtonPanel.add(copyOpportunitynCloseDialogButton);

			vp.add(formCopyOpportunityTable);
			vp.add(changeStaffButtonPanel);

			copyOpportunityDialogBox.center();
			copyOpportunityDialogBox.show();			

		}

		public void copyOpportunityAction(){
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {

					}
					else {
						showErrorMessage(this.getClass().getName() + "-copyOpportunity() ERROR: " + caught.toString());
						copyOpportunityDialog();
					}

				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					if(!result.hasErrors()) {
						stopLoading();
						dialogClose();
						BmoOpportunity bmoOpportunity = (BmoOpportunity) result.getBmObject();
						UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(),
								bmoOpportunity.getId());
						uiOpportunityDetail.show();

						getUiParams().getUiTemplate().hideEastPanel();
						UiOpportunity uiOpportunity = new UiOpportunity(getUiParams());
						uiOpportunity.edit(bmoOpportunity);
						showSystemMessage("Copia Exitosa.");
					}
					else {
						stopLoading();
						copyOpportunityDialog();
						showSystemMessage(result.getBmErrorList().get(0).getMsg());
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					if(copyOpportunitySuggestBox.getSelectedId() > 0) {
						bmoOpportunity.setId(copyOpportunitySuggestBox.getSelectedId());
						getUiParams().getBmObjectServiceAsync().action(bmoOpportunity.getPmClass(), bmoOpportunity, BmoOpportunity.ACTION_COPYOPPORTUNITY, "" + customerSuggestBox.getSelectedId(), callback);
					}
					else {
						stopLoading();
						showErrorMessage(this.getClass().getName() + " ERROR: " + "Debe seleccionar una oportunidad");
					}

				}
			} catch (SFException e) {
				stopLoading();				
				showErrorMessage(this.getClass().getName() + "-action() ERROR: " + e.toString());
			}
		}

		public void populateSaleProbabilityListBox() {
			// Probabilidad de Cierre
			//saleProbabilityListBox.addItem("0%", "0");
			saleProbabilityListBox.addItem("25%", "25");
			saleProbabilityListBox.addItem("50%", "50");
			saleProbabilityListBox.addItem("75%", "75");
			saleProbabilityListBox.addItem("100%", "100");

			//if (bmoOpportunity.getSaleProbability().toInteger() == 0) saleProbabilityListBox.setSelectedIndex(0);
			if (bmoOpportunity.getSaleProbability().toInteger() == 25) saleProbabilityListBox.setSelectedIndex(0);
			else if (bmoOpportunity.getSaleProbability().toInteger() == 50) saleProbabilityListBox.setSelectedIndex(1);
			else if (bmoOpportunity.getSaleProbability().toInteger() == 75) saleProbabilityListBox.setSelectedIndex(2);
			else if (bmoOpportunity.getSaleProbability().toInteger() == 100) saleProbabilityListBox.setSelectedIndex(3);
			else saleProbabilityListBox.setSelectedIndex(0);
		}

		// Agregar filtro al tipo de flujo del efecto de la oportunidad, si es que esta configurado 	
		private void resetEffectWFlowType() {

			effectWFlowTypeListBox.clear();
			effectWFlowTypeListBox.clearFilters();
			if (multiCompany)
				effectWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());

			BmFilter categoryFilter = new BmFilter();
			BmFilter bmFilterByStatus = new BmFilter();
			// Revisa el tipo de flujo seleccionado de la oportunidad
			if (!wFlowTypeListBox.getSelectedId().equals("0")) {

				bmoWFlowType = (BmoWFlowType)wFlowTypeListBox.getSelectedBmObject();
				int effectCategoryId = bmoWFlowType.getBmoWFlowCategory().getEffectWFlowCategoryId().toInteger();
				if (effectCategoryId > 0) {
					categoryFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getIdField(), effectCategoryId);
					
					bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);

					effectWFlowTypeListBox.addFilter(categoryFilter);
					effectWFlowTypeListBox.addFilter(bmFilterByStatus);
				}
				// Aplicar si es multiempresa, ya que si no duplica registros
				if (!multiCompany)
					effectWFlowTypeListBox.populate("" + bmoOpportunity.getForeignWFlowTypeId().toInteger(), false);
				else
					formFlexTable.addField(5, 0, effectWFlowTypeListBox, bmoOpportunity.getForeignWFlowTypeId());

				effectWFlowTypeListBox.setEnabled(true);
				formFlexTable.showField(bmoOpportunity.getForeignWFlowTypeId());
				
			} else {
				// No esta seleccionado
				effectWFlowTypeListBox.setSelectedIndex(0);
				effectWFlowTypeListBox.setEnabled(false);
				formFlexTable.hideField(bmoOpportunity.getForeignWFlowTypeId());
			}
		}

		// Con Permiso Cambiar Tipo de Flujo: Agregar filtro al tipo de flujo del efecto de la oportunidad, si es que esta configurado 	
		private void resetEffectWFlowTypeOnChangeWFlowType() {
			newEffectWFlowTypeListBox.clear();
			newEffectWFlowTypeListBox.clearFilters();
			// Aplicar si es multiempresa, ya que si no duplica registros
			if (multiCompany)
				newEffectWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());

			try {
				// Quitar el que carga por defecto
				newEffectWFlowTypeFieldId.setValue(null);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-resetEffectWFlowTypeOnChangeWFlowType() ERROR: " + e.toString());
			}
			// Revisa el tipo de flujo seleccionado de la oportunidad
			if (!newWFlowTypeListBox.getSelectedId().equals("0")) {

				bmoWFlowType = (BmoWFlowType)newWFlowTypeListBox.getSelectedBmObject();
				int effectCategoryId = bmoWFlowType.getBmoWFlowCategory().getEffectWFlowCategoryId().toInteger();
				if (effectCategoryId > 0) {
					BmFilter categoryFilter = new BmFilter();
					categoryFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getIdField(), effectCategoryId);

					BmFilter bmFilterByStatus = new BmFilter();
					bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);

					newEffectWFlowTypeListBox.addFilter(categoryFilter);
					newEffectWFlowTypeListBox.addFilter(bmFilterByStatus);
				}
				
				// Aplicar si es multiempresa, ya que si no duplica registros
				if (multiCompany)
					formFlexTable.addField(52, 0, newEffectWFlowTypeListBox, newEffectWFlowTypeFieldId);
				else
					newEffectWFlowTypeListBox.populate("" + newEffectWFlowTypeFieldId, false);
				newEffectWFlowTypeListBox.setEnabled(true);
				formFlexTable.showField(newEffectWFlowTypeFieldId);
			} else {
				// No esta seleccionado
				newEffectWFlowTypeListBox.setSelectedIndex(0);
				newEffectWFlowTypeListBox.setEnabled(false);
				formFlexTable.hideField(newEffectWFlowTypeFieldId);
			}
		}					

		// Obtiene paridad de la moneda, primer intento
		public void getParityFromCurrency(String currencyId) {
			getParityFromCurrency(currencyId, 0);
		}

		public void callMainContasct(String callMainContacts) {
			callMainContasct(callMainContacts, 0);
		};

		//llamar el maincontact
		public void callMainContasct(String customerId, int contactMainRpcAttempt) {
			if (contactMainRpcAttempt < 5) {

				setCustomerId(customerId);
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
		// Obtiene paridad de la moneda
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
						if (getParityFromCurrencyRpcAttempt() < 5) {
							getParityFromCurrency(getCurrencyId(), getParityFromCurrencyRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult result) {				
						stopLoading();	
						setParityFromCurrencyRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Tipo de Cambio Cambiaria.");
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

		// Actualiza combo de partidas presp. por empresa
		private void populateBudgetItems(int companyId) {
			budgetItemUiListBox .clear();
			budgetItemUiListBox.clearFilters();
			setBudgetItemsListBoxFilters(companyId);
			budgetItemUiListBox.populate(bmoOpportunity.getBudgetItemId());
		}

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

		@Override
		public void saveNext() {
			// Si esta asignado el tipo de proyecto, envia al dashboard, en caso contrario, envia a la lista
			if (newRecord) {
				if (bmoOpportunity.getWFlowTypeId().toInteger() > 0) {
					UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), id);
					uiOpportunityDetail.show();
				} else {
					list();
				}			
			} else {
				UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), id);
				uiOpportunityDetail.show();
			}
		}

		private void prepareLost() {
			try {
				//	bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_LOST);
				bmoOpportunity.getLoseMotiveId().setValue(loseMotiveListBox.getSelectedId());
				//bmoOpportunity.getLoseComments().setValue(loseCommentsTextArea.getText());
				if (bmoOpportunity.getLoseMotiveId().toInteger() > 0) {
					bmoOpportunity.getStatus().setValue(BmoOpportunity.STATUS_LOST);
					bmoOpportunity.getLoseComments().setValue(loseCommentsTextArea.getText());
					changeStatusButtonAction();
				}else {
					showErrorMessage(this.getClass().getName() + "-prepareLost() ERROR: Debe asignarse el Motivo de Perder la Oportunidad." );
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-prepareLost() ERROR: " + e.toString());
			}
			//changeStatusButtonAction();
		}

		private void changeStatusButtonAction() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-changeStatusButtonAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) showSystemMessage("Error al Perder Oportunidad: " + result.errorsToString());
					else {
						dialogClose();
						UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), id);
						uiOpportunityDetail.show();
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().save(bmoOpportunity.getPmClass(), bmoOpportunity, callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-changeStatusButtonAction() ERROR: " + e.toString());
			}
		}

		private void getQuote(boolean amountAplies) {
			getQuote(0,amountAplies);
		}

		private void getQuote(int bmoQuoteRpcAttempt,boolean amountAplies) {
			if (bmoQuoteRpcAttempt < 5) {
				setBmoQuoteRpcAttempt(bmoQuoteRpcAttempt + 1);

				AsyncCallback<BmObject> callback = new AsyncCallback<BmObject>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getBmoQuoteRpcAttempt() < 5)
							getQuote(getBmoQuoteRpcAttempt(),amountAplies);
						else
							showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + caught.toString());
					}

					public void onSuccess(BmObject result) {
						stopLoading();
						setBmoQuoteRpcAttempt(0);
						bmoQuote = ((BmoQuote)result);
						if(amountAplies) {
							statusEffect();
						}else {
							if (bmoQuote.getCoverageParity().toInteger() == 1) {
								populateParityFromCurrency(currencyListBox.getSelectedId());
								if (newRecord) {
									endDateTimeBox.setDate(startDateTimeBox.getTextBox().getValue());
								}
							}
						}
					}
				};
				try {
					startLoading();
					getUiParams().getBmObjectServiceAsync().get(bmoQuote.getPmClass(), bmoOpportunity.getQuoteId().toInteger(), callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-updateAmount() ERROR: " + e.toString());
				}
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOpportunity.setId(id);
			bmoOpportunity.getCode().setValue(codeTextBox.getText());
			bmoOpportunity.getName().setValue(nameTextBox.getText());
			bmoOpportunity.getDescription().setValue(descriptionTextArea.getText());
			bmoOpportunity.getStartDate().setValue(startDateTimeBox.getDateTime());
			bmoOpportunity.getEndDate().setValue(endDateTimeBox.getDateTime());		
			bmoOpportunity.getSaleDate().setValue(saleDateBox.getTextBox().getText());	
			bmoOpportunity.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoOpportunity.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoOpportunity.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoOpportunity.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoOpportunity.getMarketId().setValue(marketListBox.getSelectedId());
			bmoOpportunity.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoOpportunity.getSaleProbability().setValue(saleProbabilityListBox.getSelectedCode());	
			bmoOpportunity.getAmount().setValue(amountTextBox.getText());
			bmoOpportunity.getTags().setValue(tagBox.getTagList());
			bmoOpportunity.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoOpportunity.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoOpportunity.getCustomField1().setValue(customField1TextBox.getText());
			bmoOpportunity.getCustomField2().setValue(customField2TextBox.getText());
			bmoOpportunity.getCustomField3().setValue(customField3TextBox.getText());
			bmoOpportunity.getCustomField4().setValue(customField4TextBox.getText());	
			bmoOpportunity.getLoseMotiveId().setValue(loseMotiveListBox.getSelectedId());
			bmoOpportunity.getLoseComments().setValue(loseCommentsTextArea.getText());	
			bmoOpportunity.getStatus().setValue(statusListBox.getSelectedCode());
			bmoOpportunity.getCategoryForecastId().setValue(categoryForestcastIdListBox.getSelectedId());
			bmoOpportunity.getForeignWFlowTypeId().setValue(effectWFlowTypeListBox.getSelectedId());
			bmoOpportunity.getPropertyModelId().setValue(propertyModelSuggestBox.getSelectedId());
			bmoOpportunity.getExpireDate().setValue(expireDateBox.getTextBox().getText());
			bmoOpportunity.getCustomerRequisition().setValue(customerRequisitionTextBox.getText());
			bmoOpportunity.getVenueId().setValue(venueSuggestBox.getSelectedId());
			//bmoOpportunity.getCustomerAddressId().setValue(customerAddressListBox.getSelectedId());
			bmoOpportunity.getBudgetItemId().setValue(budgetItemUiListBox.getSelectedId());
			bmoOpportunity.getAreaId().setValue(areaUiListBox.getSelectedId());
			bmoOpportunity.getFiscalPeriod().setValue(fiscalPeriodTextBox.getText());
			bmoOpportunity.getFiscalYear().setValue(fiscalYearTextBox.getText());
			bmoOpportunity.getCustomerContactId().setValue(customerContactIdListBox.getSelectedId());
			bmoOpportunity.getAmountLicense().setValue(amountLicenseTextBox.getText());
			bmoOpportunity.getAmountService().setValue(amountServiceTextBox.getText());
			bmoOpportunity.getLeadDate().setValue(leadDateBox.getTextBox().getText());
			bmoOpportunity.getCompvspos().setValue(compvsposTextArea.getText());
			bmoOpportunity.getCommercialTerms().setValue(commercialTermsTextArea.getText());
			bmoOpportunity.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());

			return bmoOpportunity;
		}

		@Override
		public void close() {
			if (deleted) new UiOpportunity(getUiParams()).show();
			//				showSystemMessage("tipo-close: "+ getUiParams().getUiType(getBmObject().getProgramCode()) + " |ob;"+getBmObject().getProgramCode());
			//
			//				if (isSlave() || isMinimalist()) {
			//					//
			//				}
			//				
			//				else {
			//					if (getBmObject().getId() > 0) {
			//						UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), getBmObject().getId());
			//						uiOpportunityDetail.show();
			//					} else 
			//						list();
			//				}
		}

		// Variables para llamadas RPC
		public int getParityFromCurrencyRpcAttempt() {
			return parityFromCurrencyRpcAttempt;
		}

		public void setParityFromCurrencyRpcAttempt(int parityFromCurrencyRpcAttempt) {
			this.parityFromCurrencyRpcAttempt = parityFromCurrencyRpcAttempt;
		}

		public int getcontactMainRpcAttempt() {
			return contactMainRpcAttempt;
		}

		public void setcontactMainRpcAttempt(int contactMainRpcAttempt) {
			this.contactMainRpcAttempt = contactMainRpcAttempt;
		}

		public String getCurrencyId() {
			return currencyId;
		}

		public void setCurrencyId(String currencyId) {
			this.currencyId = currencyId;
		}

		public int getBmoQuoteRpcAttempt() {
			return bmoQuoteRpcAttempt;
		}

		public void setBmoQuoteRpcAttempt(int bmoQuoteRpcAttempt) {
			this.bmoQuoteRpcAttempt = bmoQuoteRpcAttempt;
		}

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public int getContactMainRpcAttempt() {
			return contactMainRpcAttempt;
		}

		public void setContactMainRpcAttempt(int contactMainRpcAttempt) {
			this.contactMainRpcAttempt = contactMainRpcAttempt;
		}
		
		// MUltiempresa: g100
		public String getCompanyId() {
			return companyId;
		}

		public void setCompanyId(String companyId) {
			this.companyId = companyId;
		}
		
		public int getProfileSalesmanRpcAttempt() {
			return profileSalesmanRpcAttempt;
		}

		public void setProfileSalesmanRpcAttempt(int profileSalesmanRpcAttempt) {
			this.profileSalesmanRpcAttempt = profileSalesmanRpcAttempt;
		}

		private void changeWFlowAction() {
			// Validar que exista un Seguimiento/Efecto
			if (!(Integer.parseInt(newEffectWFlowTypeListBox.getSelectedId()) > 0)
					|| !(Integer.parseInt(newWFlowTypeListBox.getSelectedId()) > 0))
				showSystemMessage("<b>Debe seleccionar un " 
						+ "" + getSFParams().getFieldFormTitle(bmoOpportunity.getWFlowTypeId()) + ""
						+ "/" + getSFParams().getFieldFormTitle(bmoOpportunity.getForeignWFlowTypeId()) + ""
						+ " para cambiar el Workflow.</b>");

			// Si existen aplicar cambios
			if (Integer.parseInt(newWFlowTypeListBox.getSelectedId()) > 0 && Integer.parseInt(newEffectWFlowTypeListBox.getSelectedId()) > 0 )
				changeWFlowAction("" + newWFlowTypeListBox.getSelectedId(), newEffectWFlowTypeListBox.getSelectedId() );
		}

		private void changeWFlowAction(String newWFlowTypeId, String newEffectWFlowTypeId) {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + " - changeWFlowAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) showSystemMessage("Error al cambiar Workflow: " + result.errorsToString());
					else {
						dialogClose();
						UiOpportunityDetail uiOpportunityDetail = new UiOpportunityDetail(getUiParams(), id);
						uiOpportunityDetail.show();
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOpportunity.getPmClass(), bmoOpportunity, BmoOpportunity.ACTION_CHANGEWFLOWTYPE, newWFlowTypeId + "|" +newEffectWFlowTypeId, callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + " - changeWFlowAction() ERROR: " + e.toString());
			}
		}
	}
	
	@Override
	public void displayColumnList() {
		int row = 1, col = 0, firstCol = 0;
		listActionCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				listFlexTable.listCheckBoxTooggle(event.getValue());
				toggleActionPanelOppo();
			}
		});
		listActionCheckBox.setValue(false);
		listFlexTable.resetListCheckBoxList();

		prepareColumnHeaderOppo();
		while (iterator.hasNext()) {
			BmoOpportunity nextBmoOpportunity = (BmoOpportunity)iterator.next(); 
			ArrayList<BmField> fields = new ArrayList<BmField>();
			if (isMobile()) 
				fields = nextBmoOpportunity.getMobileFieldList();
			else {
				fields.add(nextBmoOpportunity.getCode());
				fields.add(nextBmoOpportunity.getName());
				fields.add(nextBmoOpportunity.getBmoCustomer().getLogo());
				fields.add(nextBmoOpportunity.getBmoCustomer().getCode());
				fields.add(nextBmoOpportunity.getBmoCustomer().getDisplayName());
				if (getUiParams().getSFParams().isFieldEnabled(nextBmoOpportunity.getVenueId()))
					fields.add(nextBmoOpportunity.getBmoVenue().getName());
				fields.add(nextBmoOpportunity.getBmoUser().getCode());
				fields.add(nextBmoOpportunity.getBmoWFlow().getBmoWFlowPhase().getName());
				fields.add(nextBmoOpportunity.getBmoWFlow().getProgress());
				fields.add(nextBmoOpportunity.getExpireDate());
				fields.add(nextBmoOpportunity.getStartDate());
				fields.add(nextBmoOpportunity.getStatus());
				fields.add(nextBmoOpportunity.getBmoCurrency().getCode());
				fields.add(nextBmoOpportunity.getAmount());
				fields.add(nextBmoOpportunity.getTags());

			}
			col = 0;
			if (enableActionBar) {
				CheckBox checkBox = new CheckBox();
				checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						toggleActionPanelOppo();
					}
				});
				UiListCheckBox uiListCheckBox = new UiListCheckBox(nextBmoOpportunity, checkBox);
				listFlexTable.addListCheckBox(uiListCheckBox);
				listFlexTable.setWidget(row, 0, uiListCheckBox.getCheckBox());
				listFlexTable.setWidget(row, 0, uiListCheckBox.getCheckBox());
				listFlexTable.getCellFormatter().addStyleName(row, 0, "listFirstColumn");
				col++;
			}
			firstCol = col;

			Iterator<BmField> itf = fields.iterator();
			while (itf.hasNext()) {
				BmField cellBmField = (BmField)itf.next();

				if (!cellBmField.isInternal()) {

					if (col == firstCol) {
						String linkValue = cellBmField.getValue();
						if (linkValue == null || linkValue.equalsIgnoreCase(""))
							linkValue = "Editar";
						Label linkLabel = new Label(linkValue);
						linkLabel.addClickHandler(rowClickHandler);
						linkLabel.setStyleName("listCellLink");
						listFlexTable.setWidget(row, col++, linkLabel);
					} else {
						listFlexTable.addListCell(row, col++, nextBmoOpportunity, cellBmField);	
					}

					if (totalCols < col) totalCols = col;
				}
			}
			listFlexTable.formatRow(row);
			row++;
		}
	}
	private void prepareColumnHeaderOppo() {
		ArrayList<BmField> fields = new ArrayList<BmField>();

		fields.add(bmoOpportunity.getCode());
		fields.add(bmoOpportunity.getName());
		fields.add(bmoOpportunity.getBmoCustomer().getLogo());
		fields.add(bmoOpportunity.getBmoCustomer().getCode());
		fields.add(bmoOpportunity.getBmoCustomer().getDisplayName());
		if (getUiParams().getSFParams().isFieldEnabled(bmoOpportunity.getVenueId()))
			fields.add(bmoOpportunity.getBmoVenue().getName());
		fields.add(bmoOpportunity.getBmoUser().getCode());
		fields.add(bmoOpportunity.getBmoWFlow().getBmoWFlowPhase().getName());
		fields.add(bmoOpportunity.getBmoWFlow().getProgress());
		fields.add(bmoOpportunity.getExpireDate());
		fields.add(bmoOpportunity.getStartDate());
		fields.add(bmoOpportunity.getStatus());
		fields.add(bmoOpportunity.getBmoCurrency().getCode());
		fields.add(bmoOpportunity.getAmount());
		fields.add(bmoOpportunity.getTags());
		
		Iterator<BmField> it = fields.iterator();

		int col = 0;
		if (enableActionBar) {
			listFlexTable.setWidget(0, 0, listActionCheckBox);
			listFlexTable.getCellFormatter().addStyleName(0, 0, "listHeaderFirstColumn");
			listFlexTable.getCellFormatter().addStyleName(0, col++, "listHeaderFirstColumn");
		}

		while (it.hasNext()) {
			BmField titleBmField = (BmField)it.next();
			if (!titleBmField.isInternal()) {
				boolean existingOrder = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).hasOrder(getBmObject().getKind(), titleBmField.getName());
				String suffix = "";

				if (existingOrder) {
					BmOrder currentOrder = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).searchOrder(getBmObject().getKind(), titleBmField.getName());
					if (currentOrder.getOrder().equalsIgnoreCase(BmOrder.ASC)) {
						suffix = " >";
					} else {
						suffix = " <";
					}
				}

				Label titleLabel = new Label();
				listFlexTable.addListTitleCell(0, col++, getBmObject().getProgramCode(), titleBmField, titleLabel, existingOrder);

				titleLabel.setText(titleLabel.getText() + suffix);

				if (!(titleBmField.getFieldType() == BmFieldType.IMAGE) && !(titleBmField.getFieldType() == BmFieldType.FILEUPLOAD)) {
					titleLabel.addClickHandler(new UiClickHandler(titleBmField) {
						@Override
						public void onClick(ClickEvent event) {
							addOrderField(bmField);
						}
					});
				}		
			}
		}
	}
	// Asigna campos de ordenamiento
	private void addOrderField(BmField bmField) {
		ArrayList<BmOrder> orderList = new ArrayList<BmOrder>();

		// Revisa si ya esta listado por ese, y cambia el ordenamiento
		BmOrder currentOrder = getUiParams().getUiProgramParams(getBmObject().getProgramCode()).searchOrder(getBmObject().getKind(), bmField.getName());

		if (currentOrder == null || currentOrder.getKind() == null) {
			BmOrder bmOrder = new BmOrder(getBmObject().getKind(), bmField.getName(), bmField.getLabel(), BmOrder.ASC);
			orderList.add(bmOrder);
		} else {
			if (currentOrder.getOrder().equalsIgnoreCase(BmOrder.ASC)) {
				currentOrder.setOrder(BmOrder.DESC);
			} else {
				currentOrder.setOrder(BmOrder.ASC);
			}
			orderList.add(currentOrder);
		}

		getUiParams().getUiProgramParams(getBmObject().getProgramCode()).setOrderList(orderList);
		list();
	}

	// Muestra los botones de accion 
	public void toggleActionPanelOppo() {
		// Lista de tipo columnas
		if (listFlexTable.hasSelectedListCheckBox()) {
			listFlexTable.removeRow(0);
			listFlexTable.insertRow(0);
			prepareRowHeaderOppo();
			actionPanel.setVisible(true);
		} else {
			listFlexTable.removeRow(0);
			listFlexTable.insertRow(0);
			prepareColumnHeaderOppo();
		}
	}
	private void prepareRowHeaderOppo() {
		listFlexTable.setWidget(0, 0, listActionCheckBox);
		listFlexTable.getCellFormatter().addStyleName(0, 0, "listHeaderFirstColumn");

		listFlexTable.addListTitleCell(0, 1, getSFParams().getProgramTitle(getBmObject()));

		listFlexTable.addListTitleCell(0, 2, " ");
		listFlexTable.getCellFormatter().addStyleName(0, 2, "listLastColumn");
		listFlexTable.getFlexCellFormatter().setColSpan(0, 2, totalCols);
		listFlexTable.setWidget(0, 2, actionPanel);
	}
	public void populateStatusListBox() {

		ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();

		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusRevision().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusGanada().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_WON, "Ganada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_authorized.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusPerdida().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_LOST, "Perdida", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusExpirada().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_EXPIRED, "Expirado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_expired.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusHold().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_HOLD, "Detenido", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_on_hold.png")));
		}

		bmoOpportunity.getStatus().setOptionList(status);
	}
}
