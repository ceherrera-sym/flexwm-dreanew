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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoSessionSale;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
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
import com.symgae.client.ui.fields.UiTextBox;


public class UiSessionSale extends UiList {
	BmoSessionSale bmoSessionSale;

	public UiSessionSale(UiParams uiParams) {
		super(uiParams, new BmoSessionSale());
		bmoSessionSale = (BmoSessionSale)getBmObject();
	}

	@Override
	public void postShow() {

		if (isMaster()) {

			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();

			// Filtrar categorias de Flujos por Modulo Venta Inmuebles
			//			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			//			BmFilter filterWFlowCategory = new BmFilter();
			//			filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
			//			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoSessionSale.getBmoWFlowType().getBmoWFlowCategory());

			// Filtrar tipos de Flujos por Venta Inmuebles
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter filterWFlowType = new BmFilter();
			filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoSessionSale.getBmoWFlowType());

			// Filtrar fases por Venta Inmuebles
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmFilter filterWFlowPhase = new BmFilter();
			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
					bmoWFlowPhase.getWFlowCategoryId().getName(), 
					bmoWFlowCategory.getIdFieldName(),
					bmoWFlowCategory.getProgramId().getName(),
					"" + bmObjectProgramId);
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoSessionSale.getBmoWFlow().getBmoWFlowPhase());

		}
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
		addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoSessionSale.getSalesUserId());

		addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoSessionSale.getCustomerId());

		if (isMaster()) {
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoSessionSale.getStatus()), bmoSessionSale, bmoSessionSale.getStatus());		
		}
	}

	@Override
	public void create() {
		UiSessionSaleForm uiSessionSaleForm = new UiSessionSaleForm(getUiParams(), 0);
		uiSessionSaleForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoSessionSale = (BmoSessionSale)bmObject;
		UiSessionSaleDetail uiSessionSaleDetail = new UiSessionSaleDetail(getUiParams(), bmoSessionSale.getId());
		uiSessionSaleDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiSessionSaleForm uiSessionSaleForm = new UiSessionSaleForm(getUiParams(), bmObject.getId());
		uiSessionSaleForm.show();
	}

	public class UiSessionSaleForm extends UiFormDialog {	

		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox customerSuggestBox;
		UiSuggestBox userSuggestBox;
		UiTagBox tagBox = new UiTagBox(getUiParams());
		UiListBox wFlowTypeListBox;
		UiListBox orderTypeListBox;
		UiListBox sessionTypePackageListBox;
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiDateBox startDateBox = new UiDateBox();
		TextBox guestsTextBox = new TextBox();

		UiDateTimeBox sessionDateDemo = new UiDateTimeBox();	
		CheckBox sessionDemoCheckBox = new CheckBox();
		CheckBox signLetterCheckBox = new CheckBox();
		CheckBox takePhotoCheckBox = new CheckBox();
		UiTextBox maxSessionsTextBox = new UiTextBox();
		UiTextBox noSessionTextBox = new UiTextBox();

		UiDateBox inscriptionDateBox = new UiDateBox();
		
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();

		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();

		BmoSessionSale bmoSessionSale;
		BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();
		int programId;

		public UiSessionSaleForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSessionSale(), id);
			bmoSessionSale = (BmoSessionSale)getBmObject();
			initialize();
		}

		private void initialize() {

			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoSessionSale.getProgramCode());
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
			}
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por tipos de pedidos de 
			BmoOrderType bmoOrderType = new BmoOrderType();
			BmFilter sessionFilter = new BmFilter();
			sessionFilter.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_SESSION);
			orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType(), sessionFilter);

			// Filtrar por clientes existentes
			customerSuggestBox = new UiSuggestBox(new BmoCustomer());

			// Filtrar por vendedores
			userSuggestBox = new UiSuggestBox(new BmoUser());
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterSalesmen.setNotInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + salesGroupId);	
			/*filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
									bmoUser.getIdFieldName(),
									bmoProfileUser.getUserId().getName(),
									bmoProfileUser.getProfileId().getName(),
									"" + salesGroupId);*/	
			userSuggestBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);

			// Filtrar por paquetes disponibles
			sessionTypePackageListBox = new UiListBox(getUiParams(), new BmoSessionTypePackage());
			

			if (newRecord) {
				// Filtra por vigencia fin				
				BmFilter filterByPromoEnd = new BmFilter();
				filterByPromoEnd.setValueOperatorFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getEndDate(), BmFilter.MAJOREQUAL, GwtUtil.dateToString(new Date(), getUiParams().getSFParams().getDateFormat()));
				sessionTypePackageListBox.addFilter(filterByPromoEnd);

				// Filtra por vigencia inicio
				BmFilter filterByPromoStart = new BmFilter();								
				filterByPromoStart.setValueOperatorFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getStartDate(), BmFilter.MINOREQUAL, GwtUtil.dateToString(new Date(), getUiParams().getSFParams().getDateFormat()));
				sessionTypePackageListBox.addFilter(filterByPromoStart);

				if (!(bmoSessionSale.getSessionTypePackageId().toInteger() > 0)) {
					//Filtra por activo
					BmFilter filterEnabled = new BmFilter();
					filterEnabled.setValueFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getEnabled(), "1");
					sessionTypePackageListBox.addFilter(filterEnabled);
				}	
			}
			

			sessionDemoCheckBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					demoAppliesChange();
				}
			});
		}

		@Override
		public void populateFields() {
			bmoSessionSale = (BmoSessionSale)getBmObject();

			try {
				// Asigna fecha de inicio default
				if (newRecord)
					bmoSessionSale.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));

				// Si no esta asignado el tipo, buscar por el default	
				if (!(bmoSessionSale.getOrderTypeId().toInteger() > 0)) {		
					bmoSessionSale.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
				}
				
				// Si no esta asignada la moneda, buscar por la default
				if (!(bmoSessionSale.getCurrencyId().toInteger() > 0)) {
					bmoSessionSale.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
					getParityFromCurrency(bmoSessionSale.getCurrencyId().toString());
				}
				
				// Asigna empresa si es registro nuevo
				if (!(bmoSessionSale.getCompanyId().toInteger() > 0)) {	
					// Colocar empresa del usuario si no la empresa por defecto
					if (getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger() > 0)
						bmoSessionSale.getCompanyId().setValue(getSFParams().getLoginInfo().getBmoUser().getCompanyId().toInteger());
					else
						bmoSessionSale.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());
				}
				
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar tipo default : " + e.toString());
			}

			// Asgina por defecto el usuario que logea en el campo de vendedor
			try {
				if (newRecord) {
					bmoSessionSale.getSalesUserId().setValue(getSFParams().getLoginInfo().getUserId());
				}
			} catch (BmException e) {
				showSystemMessage("No se pudo asignar correctamente el vendedor, inténtelo manual: " + e.toString());
			}

			formFlexTable.addFieldReadOnly(1, 0, codeTextBox, bmoSessionSale.getCode());
			formFlexTable.addField(2, 0, nameTextBox, bmoSessionSale.getName());
			formFlexTable.addField(3, 0, orderTypeListBox, bmoSessionSale.getOrderTypeId());	
			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoSessionSale.getWFlowTypeId());
			formFlexTable.addField(5, 0, sessionTypePackageListBox, bmoSessionSale.getSessionTypePackageId());		
			formFlexTable.addField(6, 0, customerSuggestBox, bmoSessionSale.getCustomerId());
			formFlexTable.addField(7, 0, userSuggestBox, bmoSessionSale.getSalesUserId());
			formFlexTable.addField(8, 0, startDateBox, bmoSessionSale.getStartDate());
			formFlexTable.addField(9, 0, inscriptionDateBox, bmoSessionSale.getInscriptionDate());
			formFlexTable.addField(10, 0, descriptionTextArea, bmoSessionSale.getDescription());
			formFlexTable.addField(11, 0, signLetterCheckBox, bmoSessionSale.getSignLetter());
			formFlexTable.addField(12, 0, takePhotoCheckBox, bmoSessionSale.getTakePhoto());
			formFlexTable.addField(13, 0, sessionDemoCheckBox, bmoSessionSale.getSessionDemo());
			formFlexTable.addField(14, 0, sessionDateDemo, bmoSessionSale.getSessionDateDemo());
			formFlexTable.addFieldReadOnly(15, 0, maxSessionsTextBox, bmoSessionSale.getMaxSessions());
			formFlexTable.addField(16, 0, noSessionTextBox, bmoSessionSale.getNoSession());
			formFlexTable.addField(17, 0, tagBox, bmoSessionSale.getTags());			
			formFlexTable.addField(18, 0, currencyListBox, bmoSessionSale.getCurrencyId());
			formFlexTable.addField(19, 0, currencyParityTextBox, bmoSessionSale.getCurrencyParity());
			formFlexTable.addField(20, 0, companyListBox, bmoSessionSale.getCompanyId());
			formFlexTable.addField(21, 0, statusListBox, bmoSessionSale.getStatus());
			
			if (newRecord) 
				formFlexTable.hideField(bmoSessionSale.getSessionDateDemo());
				
			formFlexTable.hideField(bmoSessionSale.getWFlowTypeId());

			populateParityFromCurrency(currencyListBox.getSelectedId());
			statusEffect();
		}

		public void demoAppliesChange() {		
			if (sessionDemoCheckBox.getValue()) {
				formFlexTable.hideField(bmoSessionSale.getNoSession());
				formFlexTable.showField(bmoSessionSale.getSessionDateDemo());
			} else {
				formFlexTable.hideField(bmoSessionSale.getSessionDateDemo());
				formFlexTable.showField(bmoSessionSale.getNoSession());
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Estatus de la Venta de Sesión?");
			} else  if (event.getSource() == currencyListBox) {
				getParityFromCurrency(currencyListBox.getSelectedId());
			} else if (event.getSource() == sessionTypePackageListBox) {
				BmoSessionTypePackage bmoSessionTypePackage = (BmoSessionTypePackage)sessionTypePackageListBox.getSelectedBmObject();
				int currencyId = bmoSessionTypePackage.getBmoSessionType().getCurrencyId().toInteger();
				currencyListBox.populate("" + currencyId);
				//Al seleccionar el paquete calcular el numero de sessiones
				showSessionByPackage();
			} 
		}
		
		@Override
		public BmObject populateBObject() throws BmException {
			bmoSessionSale.setId(id);
			bmoSessionSale.getCode().setValue(codeTextBox.getText());
			bmoSessionSale.getName().setValue(nameTextBox.getText());
			bmoSessionSale.getDescription().setValue(descriptionTextArea.getText());
			bmoSessionSale.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoSessionSale.getSalesUserId().setValue(userSuggestBox.getSelectedId());
			bmoSessionSale.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoSessionSale.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoSessionSale.getSessionTypePackageId().setValue(sessionTypePackageListBox.getSelectedId());
			bmoSessionSale.getStatus().setValue(statusListBox.getSelectedCode());
			bmoSessionSale.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoSessionSale.getTags().setValue(tagBox.getTagList());
			bmoSessionSale.getSessionDemo().setValue(sessionDemoCheckBox.getValue());

			if (sessionDemoCheckBox.getValue())
				bmoSessionSale.getSessionDateDemo().setValue(sessionDateDemo.getDateTime());

			bmoSessionSale.getMaxSessions().setValue(maxSessionsTextBox.getText());
			bmoSessionSale.getNoSession().setValue(noSessionTextBox.getText());
			bmoSessionSale.getSignLetter().setValue(signLetterCheckBox.getValue());
			bmoSessionSale.getTakePhoto().setValue(takePhotoCheckBox.getValue());

			bmoSessionSale.getInscriptionDate().setValue(inscriptionDateBox.getTextBox().getText());
			
			bmoSessionSale.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoSessionSale.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoSessionSale.getCompanyId().setValue(companyListBox.getSelectedId());

			return bmoSessionSale;
		}
		
		@Override
		public void formValueChange(String event) {		
			statusEffect();
		}

		private void statusEffect() {
			customerSuggestBox.setEnabled(false);
			sessionTypePackageListBox.setEnabled(false);
			statusListBox.setEnabled(false);
			sessionDemoCheckBox.setEnabled(false);		
			wFlowTypeListBox.setEnabled(false);
			signLetterCheckBox.setEnabled(false);
			takePhotoCheckBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			startDateBox.setEnabled(false);
			inscriptionDateBox.setEnabled(false);
			noSessionTextBox.setEnabled(false);
			orderTypeListBox.setEnabled(false);
			

			if (newRecord) {				
				customerSuggestBox.setEnabled(true);
				sessionTypePackageListBox.setEnabled(true);			
				sessionDemoCheckBox.setEnabled(true);
				signLetterCheckBox.setEnabled(true);
				takePhotoCheckBox.setEnabled(true);
				userSuggestBox.setEnabled(true);
				startDateBox.setEnabled(true);	
				inscriptionDateBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				noSessionTextBox.setEnabled(true);
				wFlowTypeListBox.setEnabled(true);

				if (newRecord && getSFParams().hasSpecialAccess(BmoSessionSale.ACCESS_CREATEDEMO)) {
					sessionDemoCheckBox.setEnabled(true);
				}

			} else {

				startDateBox.setEnabled(true);
				inscriptionDateBox.setEnabled(true);
				signLetterCheckBox.setEnabled(true);
				takePhotoCheckBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);

				if (bmoSessionSale.getStatus().equals(BmoSessionSale.STATUS_AUTHORIZED)) {
					signLetterCheckBox.setEnabled(false);
					takePhotoCheckBox.setEnabled(false);
					descriptionTextArea.setEnabled(false);
				}

				if (getSFParams().hasSpecialAccess(BmoSessionSale.ACCESS_CHANGESTATUS)) 
					statusListBox.setEnabled(true);

				//Si existe un ordersession no se puede cambiar el tipo
				hasOrderSession();		
			}
		}

		@Override
		public void close() {
			UiSessionSale uiSessionSaleList = new UiSessionSale(getUiParams());
			uiSessionSaleList.show();
		}

		@Override
		public void saveNext() {
			// Si esta asignado el tipo de proyecto, envia al dashboard, en caso contrario, envia a la lista
			if (bmoSessionSale.getWFlowTypeId().toInteger() > 0) {
				UiSessionSaleDetail uiSessionSaleDetail = new UiSessionSaleDetail(getUiParams(), id);
				uiSessionSaleDetail.show();
			} else {
				UiSessionSale uiSessionSaleList = new UiSessionSale(getUiParams());
				uiSessionSaleList.show();
			}
		}
		
		//Obtener el número de sesiones
		public void showSessionByPackage() {
			BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {					
					showErrorMessage(this.getClass().getName() + "-showSessionByPackage() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();					
					maxSessionsTextBox.setText(result.getMsg());
					noSessionTextBox.setText(result.getMsg());
				}
			};
			try {
				if (!isLoading()) {					
					getUiParams().getBmObjectServiceAsync().action(bmoSessionTypePackage.getPmClass(), bmoSessionTypePackage, BmoSessionTypePackage.ACTION_NOSESSION, sessionTypePackageListBox.getSelectedId(), callback);
				}
			} catch (SFException e) {				
				showErrorMessage(this.getClass().getName() + "-showSessionByPackage() ERROR: " + e.toString());
			}
		}
		
		//Obtener la paridad de la moneda
		public void populateParityFromCurrency(String currencyId) {			
			getParityFromCurrency(currencyId);
		}
		
		public void getParityFromCurrency(String currencyId) {
			BmoCurrency bmoCurrency = new BmoCurrency();
			String startDate = startDateBox.getTextBox().getText();

			if (startDateBox.getTextBox().getText().equals(""))
				startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());

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
						showErrorMessage("Error al obtener El Tipo de Cambio");
					else {
						currencyParityTextBox.setValue(result.getMsg());
					}
				}
			};
			try {	
				if (!isLoading()) {					
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCurrency.getPmClass(), bmoCurrency, BmoCurrency.ACTION_GETCURRENCYPARITY, actionValues, callback);					
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getParityFromCurrency() ERROR: " + e.toString());
			}
		}

		public void hasOrderSession() {

			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(this.getClass().getName() + "-hasOrderSession() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					if (!result.hasErrors()) {
						if (Integer.parseInt(result.getMsg()) > 0) {
							sessionTypePackageListBox.setEnabled(false);
							startDateBox.setEnabled(false);
						} else {
							sessionTypePackageListBox.setEnabled(true);
							startDateBox.setEnabled(true);
						}
					} 					
				}
			};

			try {	
				if (!isLoading()) {					
					getUiParams().getBmObjectServiceAsync().action(bmoSessionSale.getPmClass(), bmoSessionSale, BmoSessionSale.ACTION_HASORSS, "" + bmoSessionSale.getOrderId().toInteger(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-hasOrderSession() ERROR: " + e.toString());
			}
		}
	}
	
	
}
