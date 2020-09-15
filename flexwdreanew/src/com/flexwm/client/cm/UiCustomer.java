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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoTitle;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoAssignCoordinator;
import com.flexwm.shared.cm.BmoAssignSalesman;
import com.flexwm.shared.cm.BmoConsultingService;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerStatus;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.cm.BmoMaritalStatus;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoNationality;
import com.flexwm.shared.cm.BmoPayCondition;
import com.flexwm.shared.cm.BmoRateType;
import com.flexwm.shared.cm.BmoReferral;
import com.flexwm.shared.cm.BmoRegion;
import com.flexwm.shared.cm.BmoTerritory;
import com.flexwm.shared.fi.BmoCFDI;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPayMethod;
import com.flexwm.shared.op.BmoReqPayType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;


public class UiCustomer extends UiList {
	private UiCustomerForm uiCustomerForm;
	BmoCustomer bmoCustomer;

	public UiCustomer(UiParams uiParams) {
		super(uiParams, new BmoCustomer());
		bmoCustomer = (BmoCustomer)getBmObject();
	}

	@Override
	public void postShow() {
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoCustomer.getCustomertype()), bmoCustomer, bmoCustomer.getCustomertype());
		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoCustomer.getSalesmanId());
		if (!isMobile()) {
			if (getSFParams().isFieldEnabled(bmoCustomer.getTerritoryId()))
				addFilterSuggestBox(new UiSuggestBox(new BmoTerritory()), new BmoTerritory(), bmoCustomer.getTerritoryId());
			if (getSFParams().isFieldEnabled(bmoCustomer.getIndustryId()))
				addFilterSuggestBox(new UiSuggestBox(new BmoIndustry()), new BmoIndustry(), bmoCustomer.getIndustryId());
			if (getSFParams().isFieldEnabled(bmoCustomer.getReqPayTypeId()))
				addFilterSuggestBox(new UiSuggestBox(new BmoReqPayType()), new BmoReqPayType(), bmoCustomer.getReqPayTypeId());
			if (getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory()))
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoCustomer.getCustomercategory()), bmoCustomer, bmoCustomer.getCustomercategory());		
		}
		//		addStaticFilterListBox(new UiListBox(bmoCustomer.getStatus()), bmoCustomer, bmoCustomer.getStatus());
		if (!isMobile())
			addTagFilterListBox(bmoCustomer.getTags());
	}

	@Override
	public void create() {
		uiCustomerForm = new UiCustomerForm(getUiParams(), 0);
		uiCustomerForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), bmObject.getId());
		uiCustomerDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		uiCustomerForm = new UiCustomerForm(getUiParams(), bmObject.getId());
		uiCustomerForm.show();
	}

	public UiCustomerForm getUiCustomerForm() {
		uiCustomerForm = new UiCustomerForm(getUiParams(), 0);
		return uiCustomerForm;
	}

	public class UiCustomerForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextArea legalnameTextArea = new TextArea();
		TextBox titleTextBox = new TextBox();
		TextBox displayNameTextBox = new TextBox();
		TextBox firstnameTextBox = new TextBox();
		TextBox fatherlastnameTextBox = new TextBox();
		TextBox motherlastnameTextBox = new TextBox();
		TextBox positionTextBox = new TextBox();
		UiDateBox birthDateBox = new UiDateBox();
		UiDateBox establishmentDateBox = new UiDateBox();
		TextBox rfcTextBox = new TextBox();
		TextBox wwwTextBox = new TextBox();

		TextBox phoneTextBox = new TextBox();
		TextBox extensionTextBox = new TextBox();
		TextBox mobileTextBox = new TextBox();
		TextBox emailTextBox = new TextBox();

		TextArea referralCommentsTextArea = new TextArea();
		UiListBox referralListBox = new UiListBox(getUiParams(), new BmoReferral());
		UiListBox titleListBox = new UiListBox(getUiParams(), new BmoTitle());
		UiSuggestBox accountOwnerSuggestBox;
		UiSuggestBox salesmanSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox customerTypeListBox = new UiListBox(getUiParams());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		UiFileUploadBox logoFileUpload = new UiFileUploadBox(getUiParams());
		UiTagBox tagBox = new UiTagBox(getUiParams());

		TextBox curpTextBox = new TextBox();
		TextBox nssTextBox = new TextBox();
		TextBox incomeTextBox = new TextBox();
		UiSuggestBox parentSuggestBox = new UiSuggestBox(new BmoCustomer());
//		UiListBox maritalStatusLisBox = new UiListBox(getUiParams());
		UiListBox maritalStatusCatListBox = new UiListBox(getUiParams(),new BmoMaritalStatus());
		UiSuggestBox recommendedBySuggestBox = new UiSuggestBox(new BmoCustomer());
		UiSuggestBox industrySuggestBox = new UiSuggestBox(new BmoIndustry());
		UiListBox territoryListBox = new UiListBox(getUiParams(), new BmoTerritory());
		UiSuggestBox consultingServiceSuggestBox = new UiSuggestBox(new BmoConsultingService());
		UiSuggestBox regionSuggestBox = new UiSuggestBox(new BmoRegion());
		UiListBox ratingListBox = new UiListBox(getUiParams());
		UiListBox paymentTypeUiListBox = new UiListBox(getUiParams());
		TextBox creditLimitTexBox = new TextBox();
		UiListBox reqPayTypeListBox = new UiListBox(getUiParams(), new BmoReqPayType());
		UiListBox CFDIListBox = new UiListBox(getUiParams(), new BmoCFDI());
		UiListBox payMethodListBox = new UiListBox(getUiParams(), new BmoPayMethod());
		//campo inadico
		UiListBox customerCategoryListBox = new UiListBox(getUiParams());
		TextBox userTextBox = new TextBox();
		PasswordTextBox passwTextBox = new PasswordTextBox();
		PasswordTextBox passwConfTextBox = new PasswordTextBox();

		UiListBox payConditionListBox = new UiListBox(getUiParams(), new BmoPayCondition());

		// Tipos de Tarifas / Tarifa
		BmoRateType bmoRateType = new BmoRateType();
		UiListBox rateTypeListBox = new UiListBox(getUiParams(), new BmoRateType());
		TextBox rateTexBox = new TextBox();
		TextBox developmentRateTexBox = new TextBox();
		UiSuggestBox lessorMasterSuggestBox;
		CheckBox leadCheckBox = new CheckBox();
		CheckBox remindPaymentRaccountCheckBox = new CheckBox();
		UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
		TextBox oficialIdentifyTexBox = new TextBox();
		UiListBox nationalityUiListBox = new UiListBox(getUiParams(), new BmoNationality());
		UiListBox locationIdListBox = new UiListBox(getUiParams());
		
		boolean multiCompany = false;
		private String companyId;
		private int profileSalesmanRpcAttempt = 0;
		
		private int companyUserLeadId;
		private int userLeadRpcAttempt = 0;
		
		BmoCustomer bmoCustomer;
		CustomerUpdater customerUpdater = new CustomerUpdater();

		String generalSection = "Datos Generales";
		String contactSection = "Datos de Contacto";
		String commercialSection = "Datos Comerciales";
		String additionalSection = "Datos Adicionales";
		String dataSection = "Datos Relacionados";
		String facturationSection = "Datos de Facturación";
		String customerStatusPF = "Estatus Cliente Por Empresa";

		public UiCustomerForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCustomer(), id);
			bmoCustomer = (BmoCustomer)getBmObject();
			initialize();
		}

		private void initialize() {

			// Filtrar por Representante Comercial
			BmoUser bmoUser = new BmoUser();

			accountOwnerSuggestBox = new UiSuggestBox(new BmoUser());
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + salesGroupId);	
			accountOwnerSuggestBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			accountOwnerSuggestBox.addFilter(filterSalesmenActive);
		}

		@Override
		public void populateFields(){
			bmoCustomer = (BmoCustomer)getBmObject();
			lessorMasterSuggestBox = new UiSuggestBox(new BmoCustomer());
			
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				locationIdListBox = new UiListBox(getUiParams(), new BmoLocation());
				try {
					if (newRecord) {
						bmoCustomer.getLocationId().setValue(getUiParams().getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger());
					}
					setUserListBoxFilters(bmoCustomer.getLocationId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}	
			}
			
			// Filtrar por vendedores
			// MultiEmpresa: g100
			multiCompany = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean();
			
			// MultiEmpresa: g100
			// Forzar filtro
			if (getSFParams().getSelectedCompanyId() > 0)
				setProfileSalesmanByCompanyIdFilters("" + getSFParams().getSelectedCompanyId(), multiCompany);
			else
				setProfileSalesmanByCompanyIdFilters("0", multiCompany);
						
			try {
				if (newRecord){
					bmoCustomer.getCustomertype().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultTypeCustomer().toChar());
					bmoCustomer.getSalesmanId().setValue(getSFParams().getLoginInfo().getUserId());

					if (!(bmoCustomer.getCurrencyId().toInteger() > 0))
						bmoCustomer.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				}
			} catch (BmException e) {
				showSystemMessage("No se puede asignar el Tipo de Cliente : " + e.toString());
			}
			int r = 1, c = 0; // Incrementar posicion de lineas

			formFlexTable.addSectionLabel(r, c, generalSection, 2);	r++;
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				formFlexTable.addField(r, c, locationIdListBox, bmoCustomer.getLocationId()); r++;
			}
			formFlexTable.addFieldReadOnly(r, c, codeTextBox, bmoCustomer.getCode());	r++;
			formFlexTable.addField(r, c, customerTypeListBox, bmoCustomer.getCustomertype());	r++;
			formFlexTable.addField(r, c, displayNameTextBox, bmoCustomer.getDisplayName());	r++;
			formFlexTable.addField(r, c, legalnameTextArea, bmoCustomer.getLegalname())		;r++;
			formFlexTable.addField(r, c, rfcTextBox, bmoCustomer.getRfc());	r++;
			formFlexTable.addField(r, c, establishmentDateBox, bmoCustomer.getEstablishmentDate());r++;
		
			formFlexTable.addSectionLabel(r, c, contactSection, 2);	r++;
			formFlexTable.addField(r, c, leadCheckBox, bmoCustomer.getLead());	r++;
			formFlexTable.addField(r, c, titleListBox, bmoCustomer.getTitleId());	r++;
			formFlexTable.addField(r, c, firstnameTextBox, bmoCustomer.getFirstname());	r++;
			formFlexTable.addField(r, c, fatherlastnameTextBox, bmoCustomer.getFatherlastname());	r++;
			formFlexTable.addField(r, c, motherlastnameTextBox, bmoCustomer.getMotherlastname());	r++;
			formFlexTable.addField(r, c, phoneTextBox, bmoCustomer.getPhone());	r++;
			formFlexTable.addField(r, c, extensionTextBox, bmoCustomer.getExtension());	r++;

			formFlexTable.addField(r, c, emailTextBox, bmoCustomer.getEmail());	r++;
			formFlexTable.addField(r, c, mobileTextBox, bmoCustomer.getMobile());	r++;
			formFlexTable.addField(r, c, positionTextBox, bmoCustomer.getPosition());	r++;

			formFlexTable.addSectionLabel(r, c, commercialSection, 2);	r++;
			formFlexTable.addField(r, c, accountOwnerSuggestBox, bmoCustomer.getAccountOwnerId());	r++;
			formFlexTable.addField(r, c, salesmanSuggestBox, bmoCustomer.getSalesmanId());	r++;
			formFlexTable.addField(r, c, currencyListBox, bmoCustomer.getCurrencyId());	r++;
			formFlexTable.addField(r, c, marketListBox, bmoCustomer.getMarketId());	r++;
			formFlexTable.addField(r, c, territoryListBox, bmoCustomer.getTerritoryId());	r++;
			formFlexTable.addField(r, c, regionSuggestBox, bmoCustomer.getRegionId());	r++;
			formFlexTable.addField(r, c, referralListBox, bmoCustomer.getReferralId());	r++;
			formFlexTable.addField(r, c, referralCommentsTextArea, bmoCustomer.getReferralComments());	r++;

			if (getSFParams().hasRead(bmoRateType.getProgramCode())) {
				formFlexTable.addField(r, c, rateTypeListBox, bmoCustomer.getRateTypeId());	r++;
				formFlexTable.addField(r, c, rateTexBox, bmoCustomer.getRate());	r++;
				formFlexTable.addField(r, c, developmentRateTexBox, bmoCustomer.getDevelopmentRate());	r++;
			}
			formFlexTable.addField(r, c, customerCategoryListBox, bmoCustomer.getCustomercategory());	r++;
			//campos Usuario y contraseña para Inadico
			formFlexTable.addField(r, c, userTextBox ,bmoCustomer.getUser());	r++;
			formFlexTable.addField(r, c, passwTextBox, bmoCustomer.getPassw());	r++;
			formFlexTable.addField(r, c, passwConfTextBox,bmoCustomer.getPasswconf());	r++;

			BmFilter filterLessor = new BmFilter();
			filterLessor.setValueFilter(bmoCustomer.getKind(), bmoCustomer.getCustomercategory(), ""+BmoCustomer.CATEGORY_LESSEE);
			BmFilter filterByParent = new BmFilter();
			filterByParent.setValueOperatorFilter(bmoCustomer.getKind(), bmoCustomer.getIdField(), BmFilter.NOTEQUALS, id);
			lessorMasterSuggestBox.addFilter(filterLessor);
			lessorMasterSuggestBox.addFilter(filterByParent);

			formFlexTable.addField(r, c, lessorMasterSuggestBox,bmoCustomer.getLessorMasterId());	r++;

			formFlexTable.addField(r, c, recommendedBySuggestBox, bmoCustomer.getRecommendedBy());	r++;
			formFlexTable.addField(r, c, industrySuggestBox, bmoCustomer.getIndustryId());	r++;
			formFlexTable.addField(r, c, tagBox, bmoCustomer.getTags());	r++;
			formFlexTable.addLabelField(r, c, bmoCustomer.getStatus());	r++;

			if (!newRecord) {
				BmoCustomerStatus bmoCustomerStatus = new BmoCustomerStatus();
				FlowPanel customerStatusFP = new FlowPanel();
				BmFilter filterCustomerStatus = new BmFilter();
				filterCustomerStatus.setValueFilter(bmoCustomerStatus.getKind(), bmoCustomerStatus.getCustomerId(), bmoCustomer.getId());
				getUiParams().setForceFilter(bmoCustomerStatus.getProgramCode(), filterCustomerStatus);
				UiCustomerStatus  uiCustomerStatus = new UiCustomerStatus(getUiParams(), customerStatusFP, bmoCustomer);
				setUiType(bmoCustomerStatus.getProgramCode(), UiParams.MINIMALIST);
				uiCustomerStatus.show();
				formFlexTable.addSectionLabel(r, c, customerStatusPF, 2);	r++;
				formFlexTable.addPanel(r, c, customerStatusFP, 2);	r++;
			}
			//datos de facturación
			formFlexTable.addSectionLabel(r, c, facturationSection, 2);	r++;
			formFlexTable.addField(r, c, reqPayTypeListBox, bmoCustomer.getReqPayTypeId());		r++;
			formFlexTable.addField(r, c, CFDIListBox,bmoCustomer.getCfdiid());	r++;
			formFlexTable.addField(r, c, payMethodListBox, bmoCustomer.getPaymethodid());	r++;
			if (getSFParams().hasRead(new BmoPayCondition().getProgramCode())) {
				formFlexTable.addField(r, c, payConditionListBox, bmoCustomer.getPayConditionId());	r++;
			}

			formFlexTable.addField(r, c, new UiCustomerPaymentTypeLabelList(getUiParams(), id));	r++;
			formFlexTable.addField(r, c, new UiCustomerBankAccountLabelList(getUiParams(), id));	r++;

			formFlexTable.addSectionLabel(r, c, additionalSection, 2);	r++;
			formFlexTable.addField(r, c, curpTextBox, bmoCustomer.getCurp());	r++;
			
			formFlexTable.addField(r, c, oficialIdentifyTexBox, bmoCustomer.getOficialIdentify());	r++;
			
			formFlexTable.addField(r, c, nationalityUiListBox, bmoCustomer.getNationalityId());	r++;		
			formFlexTable.addField(r, c, nssTextBox, bmoCustomer.getNss());	r++;
			formFlexTable.addField(r, c, birthDateBox, bmoCustomer.getBirthdate());	r++;
			formFlexTable.addField(r, c, maritalStatusCatListBox, bmoCustomer.getMaritalStatusId());		r++;
			formFlexTable.addField(r, c, parentSuggestBox, bmoCustomer.getParentId());	r++;
			formFlexTable.addField(r, c, incomeTextBox, bmoCustomer.getIncome());	r++;
			formFlexTable.addField(r, c, consultingServiceSuggestBox, bmoCustomer.getConsultingServiceId());	r++;
			formFlexTable.addField(r, c, ratingListBox, bmoCustomer.getRating());	r++;
			formFlexTable.addField(r, c, creditLimitTexBox, bmoCustomer.getCreditLimit());	r++;
			formFlexTable.addField(r, c, wwwTextBox, bmoCustomer.getWww());	r++;
			formFlexTable.addField(r, c, logoFileUpload, bmoCustomer.getLogo());	r++;

			if (!newRecord) {
				formFlexTable.addSectionLabel(r, c, dataSection, 2);	r++;
				formFlexTable.addField(r, c, new UiCustomerEmailLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerPhoneLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerDateLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerRelativeLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerAddressLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerCompanyLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerSocialLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerContactLabelList(getUiParams(), id, customerUpdater));	r++;
				formFlexTable.addField(r, c, new UiCustomerWebLabelList(getUiParams(), id));	r++;
				formFlexTable.addField(r, c, new UiCustomerNoteLabelList(getUiParams(), id));	r++;
			}

			formFlexTable.hideSection(commercialSection);
			formFlexTable.hideSection(additionalSection);
			formFlexTable.hideSection(dataSection);
			formFlexTable.hideSection(customerStatusPF);
			formFlexTable.hideSection(facturationSection);

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCustomer.setId(id);
			bmoCustomer.getCode().setValue(codeTextBox.getText());
			bmoCustomer.getLegalname().setValue(legalnameTextArea.getText());
			bmoCustomer.getDisplayName().setValue(displayNameTextBox.getText());
			bmoCustomer.getTitleId().setValue(titleListBox.getSelectedId());
			bmoCustomer.getFirstname().setValue(firstnameTextBox.getText());
			bmoCustomer.getFatherlastname().setValue(fatherlastnameTextBox.getText());
			bmoCustomer.getMotherlastname().setValue(motherlastnameTextBox.getText());
			bmoCustomer.getRfc().setValue(rfcTextBox.getText());
			bmoCustomer.getPosition().setValue(positionTextBox.getText());
			bmoCustomer.getWww().setValue(wwwTextBox.getText());
			bmoCustomer.getPhone().setValue(phoneTextBox.getText());
			bmoCustomer.getExtension().setValue(extensionTextBox.getText());
			bmoCustomer.getMobile().setValue(mobileTextBox.getText());
			bmoCustomer.getEmail().setValue(emailTextBox.getText());
			bmoCustomer.getBirthdate().setValue(birthDateBox.getTextBox().getText());
			bmoCustomer.getEstablishmentDate().setValue(establishmentDateBox.getTextBox().getText());
			bmoCustomer.getReferralComments().setValue(referralCommentsTextArea.getText());
			bmoCustomer.getCustomertype().setValue(customerTypeListBox.getSelectedCode());
			bmoCustomer.getSalesmanId().setValue(salesmanSuggestBox.getSelectedId());
			bmoCustomer.getReferralId().setValue(referralListBox.getSelectedId());
			bmoCustomer.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoCustomer.getTags().setValue(tagBox.getTagList());
			bmoCustomer.getLogo().setValue(logoFileUpload.getBlobKey());
			bmoCustomer.getCurp().setValue(curpTextBox.getText());
			bmoCustomer.getNss().setValue(nssTextBox.getText());
			bmoCustomer.getIncome().setValue(incomeTextBox.getText());
			bmoCustomer.getParentId().setValue(parentSuggestBox.getSelectedId());
			bmoCustomer.getMaritalStatusId().setValue(maritalStatusCatListBox.getSelectedId());
			bmoCustomer.getRecommendedBy().setValue(recommendedBySuggestBox.getSelectedId());
			bmoCustomer.getIndustryId().setValue(industrySuggestBox.getSelectedId());
			bmoCustomer.getTerritoryId().setValue(territoryListBox.getSelectedId());
			bmoCustomer.getConsultingServiceId().setValue(consultingServiceSuggestBox.getSelectedId());
			bmoCustomer.getRegionId().setValue(regionSuggestBox.getSelectedId());
			bmoCustomer.getRating().setValue(ratingListBox.getSelectedCode());
			bmoCustomer.getCreditLimit().setValue(creditLimitTexBox.getText());
			bmoCustomer.getReqPayTypeId().setValue(reqPayTypeListBox.getSelectedId());
			bmoCustomer.getRateTypeId().setValue(rateTypeListBox.getSelectedId());
			bmoCustomer.getRate().setValue(rateTexBox.getText());
			if(customerCategoryListBox.getSelectedCode().equals(""+BmoCustomer.CATEGORY_LESSEE) 
					&& getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory()))  {
				bmoCustomer.getUser().setValue(userTextBox.getText());
				bmoCustomer.getPassw().setValue(passwTextBox.getText());
				bmoCustomer.getPasswconf().setValue(passwConfTextBox.getText());
				bmoCustomer.getCustomercategory().setValue(customerCategoryListBox.getSelectedCode());
			} else if(customerCategoryListBox.getSelectedCode().equals(""+BmoCustomer.CATEGORY_LESSOR) 
					&& getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory()))  {
				bmoCustomer.getCustomercategory().setValue(customerCategoryListBox.getSelectedCode());
			}
			bmoCustomer.getLessorMasterId().setValue(lessorMasterSuggestBox.getSelectedId());
			bmoCustomer.getCfdiid().setValue(CFDIListBox.getSelectedId());
			bmoCustomer.getPaymethodid().setValue(payMethodListBox.getSelectedId());
			bmoCustomer.getPayConditionId().setValue(payConditionListBox.getSelectedId());
			bmoCustomer.getAccountOwnerId().setValue(accountOwnerSuggestBox.getSelectedId());
			bmoCustomer.getDevelopmentRate().setValue(developmentRateTexBox.getText());
			bmoCustomer.getLead().setValue(leadCheckBox.getValue());
			bmoCustomer.getOficialIdentify().setValue(oficialIdentifyTexBox.getValue());
			bmoCustomer.getNationalityId().setValue(nationalityUiListBox.getSelectedId());
			bmoCustomer.getMarketId().setValue(marketListBox.getSelectedId());
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() )
				bmoCustomer.getLocationId().setValue(locationIdListBox.getSelectedId());

			return bmoCustomer;
		}

		@Override
		public void close() {
			if (deleted) new UiCustomer(getUiParams()).show();
			//			showSystemMessage("tipo: "+ getUiParams().getUiType(getBmObject().getProgramCode()) + " |ob;"+getBmObject().getProgramCode());
			//			showSystemMessage("id: "+getBmObject().getId());

			//			if (isSlave()) {
			//				//
			//			} else {
			//				if (getBmObject().getId() > 0) {
			//					UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), getBmObject().getId());
			//					uiCustomerDetail.show();
			//					
			//				} else {
			//					list();
			//				}
			//			}
		}

		@Override
		public void saveNext() {
			if (isMaster()) {
				if (newRecord && getBmObject().getId() > 0) {
					// Recargar listado
					showList();
					// El cliente es recientemente creado, ir a la forma
					edit(bmoCustomer);
				} else {					
					// El cliente ya fue creado, ir a la forma de detalle
					UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), id);
					uiCustomerDetail.show();
				}
			} else if (isSlave()) {
				//				// Desde tablero...
				//				UiCustomerDetail uiCustomerDetail = new UiCustomerDetail(getUiParams(), getBmObject().getId());
				//				uiCustomerDetail.show();
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == customerTypeListBox) {
				statusEffect();
			}
			if (event.getSource() == customerCategoryListBox) {
				statusEffect();
			}
			if (event.getSource() == locationIdListBox) {
				if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
					BmoLocation bmoLocation = (BmoLocation)locationIdListBox.getSelectedBmObject();
					GWT.log("Ubicacion seleccionada:" + bmoLocation.getName().toString());
					populateUsers(bmoLocation.getId());
				}
			}
		}
		
		@Override
		public void formBooleanChange(ValueChangeEvent<Boolean> event) {
			// Notif. fecha fin de pedido
			if (event.getSource() == leadCheckBox) {
				if (leadCheckBox.getValue()) {
					if (!(getSFParams().getSelectedCompanyId() > 0)) {
						showSystemMessage("Debe seleccionar una Empresa maestra para poder asignar un Lead.");
						leadCheckBox.setValue(false);
					} else
						setUserLead(getSFParams().getSelectedCompanyId());
				}
			}
		}

		private void statusEffect() {
			rfcTextBox.setEnabled(false);
			displayNameTextBox.setEnabled(false);
			legalnameTextArea.setEnabled(false);
			establishmentDateBox.setEnabled(false);
			salesmanSuggestBox.setEnabled(false);
			leadCheckBox.setEnabled(false);

			if (customerTypeListBox.getSelectedCode().equals("" + BmoCustomer.TYPE_COMPANY)) {
				rfcTextBox.setEnabled(true);
				legalnameTextArea.setEnabled(true);
				displayNameTextBox.setEnabled(true);
				establishmentDateBox.setEnabled(true);
			} else {
				legalnameTextArea.setValue("");
				establishmentDateBox.getTextBox().setValue("");
			}

			if (newRecord || bmoCustomer.getStatus().equals(BmoCustomer.STATUS_INACTIVE) 
					|| getSFParams().hasSpecialAccess(BmoCustomer.SALESMANCHANGE) ) {
				salesmanSuggestBox.setEnabled(true);
				if (getSFParams().isFieldEnabled(bmoCustomer.getLead()) && leadCheckBox.getValue()) {
					salesmanSuggestBox.setEnabled(false);
				}
			}

			if (!newRecord && !getSFParams().hasSpecialAccess(BmoCustomer.TYPECHANGE))
				customerTypeListBox.setEnabled(false);
			else 
				customerTypeListBox.setEnabled(true);

			if (!newRecord && getSFParams().hasSpecialAccess(BmoCustomer.RFCCHANGE))
				rfcTextBox.setEnabled(true);
			// Ocultar campos de Usuario y contraseña 
			if (customerCategoryListBox.getSelectedCode().equals(""+BmoCustomer.CATEGORY_LESSEE) 
					&& getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory()))  {
				formFlexTable.showField(userTextBox);
				formFlexTable.showField(passwTextBox);
				formFlexTable.showField(passwConfTextBox);
				formFlexTable.showField(lessorMasterSuggestBox);
			} else {
				formFlexTable.hideField(userTextBox);
				formFlexTable.hideField(passwTextBox);
				formFlexTable.hideField(passwConfTextBox);
				formFlexTable.hideField(lessorMasterSuggestBox);
			}
			
			if (newRecord) {
				leadCheckBox.setEnabled(true);
			}
			
			// Validar si tiene permiso de propios, en la ubicacion
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean()) {
				if (getSFParams().restrictData(new BmoLocation().getProgramCode())) {
					locationIdListBox.setEnabled(false);
				} else {
					locationIdListBox.setEnabled(true);
				}
			}
		}
		
		// Buscar perfil del vendedor POR empresa
		public void setUserLead(int companyId) {
			if (companyId > 0) 
				setUserLead(companyId, 0);
		};

		// Buscar perfil del vendedor POR empresa
		public void setUserLead(int companyId, int userLeadRpcAttempt) {
			if (userLeadRpcAttempt < 5) {
				setCompanyUserLeadId(companyId);
				setUserLeadRpcAttempt(userLeadRpcAttempt + 1);
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getUserLeadRpcAttempt() < 5) {
							setUserLead(getCompanyUserLeadId(), getUserLeadRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-setUserLead() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult bmUpdateResult) {
						stopLoading();	
						setUserLeadRpcAttempt(0);
						if (bmUpdateResult.hasErrors())
							showErrorMessage("Error al obtener el Lead.");
						else {
							BmoAssignSalesman bmoAssignSalesman = new BmoAssignSalesman();
							// Revisa el tipo de objecto recibido
							if (bmUpdateResult.getBmObject() instanceof BmoAssignSalesman) {	
								// Se obtiene resultado correcto
								bmoAssignSalesman = (BmoAssignSalesman)bmUpdateResult.getBmObject();
								salesmanSuggestBox.setSelectedId(bmoAssignSalesman.getUserId().toInteger());
								salesmanSuggestBox.setText("" + bmoAssignSalesman.getBmoUser().listBoxFieldsToString());
								salesmanSuggestBox.setEnabled(false);
							}
						}
					}
				};

				try {	
					startLoading();
					BmoAssignCoordinator bmoAssignCoordinator = new BmoAssignCoordinator();
					getUiParams().getBmObjectServiceAsync().action(bmoAssignCoordinator.getPmClass(), bmoAssignCoordinator, BmoAssignCoordinator.ACTION_ASSIGNLEAD, "" + companyId, callback);


				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-setUserLead() ERROR: " + e.toString());
				}
			}
		}
		
		// Filtrar vendedores por perfil/empresa
//		private void populateUsers(int companyId, boolean multiCompany) {
//			salesmanSuggestBox.clear();
//			if (multiCompany && companyId > 0)
//				setProfileSalesmanByCompanyIdFilters("" + companyId, multiCompany);
//			else {
//				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
//				setUserListBoxFilters("" + salesProfileId);
//			}
//		}
		
		
		// Actualiza combo de usuarios por UBICACION (para daCredito)
		private void populateUsers(int locationId) {
			salesmanSuggestBox.clear();
			setUserListBoxFilters(locationId);
		}
		
		// Filtrar usuarios por UBICACION (para daCredito)
		private void setUserListBoxFilters(int locationId) {
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {

				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();

				BmoUser bmoUser = new BmoUser();
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				
				BmFilter filterSalesmen = new BmFilter();
				filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + salesProfileId);	
				salesmanSuggestBox.addFilter(filterSalesmen);
	
				// Filtrar por vendedores activos
				BmFilter filterSalesmenActive = new BmFilter();
				filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
				salesmanSuggestBox.addFilter(filterSalesmenActive);
				
				// Filtrar por vendedores de la ubicacion
				BmFilter filterByLocation= new BmFilter();
				filterByLocation.setValueFilter(bmoUser.getKind(), bmoUser.getLocationId(), "" + locationId);
				salesmanSuggestBox.addFilter(filterByLocation);
			}
		}

		// Filtrar usuarios por perfil de vendedores 
		private void setUserListBoxFilters(String salesProfileId, boolean multiCompany) {
			BmoUser bmoUser = new BmoUser();
			
			if (multiCompany && !(getSFParams().getSelectedCompanyId() > 0)) {
				// No filtrar por perfil cuando no hay empresa maestra
			} else {
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterSalesmen = new BmFilter();
				filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(),
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + salesProfileId);	
				salesmanSuggestBox.addFilter(filterSalesmen);
			}
			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			salesmanSuggestBox.addFilter(filterSalesmenActive);
		}
		
		// Buscar perfil del vendedor POR empresa
		public void setProfileSalesmanByCompanyIdFilters(String companyId, boolean multiCompany) {
			if (multiCompany && Integer.parseInt(companyId) > 0) {
				searchProfileSalesmanByCompanyId(companyId, multiCompany , 0);
			} else {
				int salesProfileId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
				setUserListBoxFilters("" + salesProfileId, multiCompany);
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
							showErrorMessage("Error al obtener el Perfil Vendedor de la Empresa.");
						else {
							// Aplicar filtro
							setUserListBoxFilters(result.getMsg(), multiCompany);
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

		// Actualizar la forma
		public class CustomerUpdater {
			public void update() {
				stopLoading();
				get(id);
			}		
		}
		
		// Multiempresa: g100
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

		public int getCompanyUserLeadId() {
			return companyUserLeadId;
		}

		public void setCompanyUserLeadId(int companyUserLeadId) {
			this.companyUserLeadId = companyUserLeadId;
		}

		public int getUserLeadRpcAttempt() {
			return userLeadRpcAttempt;
		}

		public void setUserLeadRpcAttempt(int userLeadRpcAttempt) {
			this.userLeadRpcAttempt = userLeadRpcAttempt;
		}
		
	}
}
