/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Paulina Padilla
 * @version 2018-08
 */

package com.flexwm.client.ar;

import java.util.Date;

import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomer.UiCustomerForm;
import com.flexwm.client.co.UiProperty;
import com.flexwm.client.co.UiProperty.UiPropertyForm;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.cm.BmoCustomerContact;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.co.BmoOrderPropertyTax;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiPropertyRental extends UiList {

	BmoPropertyRental bmoPropertyRental;
	BmoPropertyRental fromPropertyRental;

	public UiPropertyRental(UiParams uiParams) {
		super(uiParams, new BmoPropertyRental());
		bmoPropertyRental = (BmoPropertyRental)getBmObject();
	}

	public UiPropertyRental(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPropertyRental());
		bmoPropertyRental = (BmoPropertyRental)getBmObject();
	}
	
	@Override
	public void postShow() {
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();


		if (!isMobile()) {
			// Filtrar categorias de Flujos por Modulo Proyecto
			BmFilter filterWFlowCategory = new BmFilter();
			filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoPropertyRental.getBmoWFlowType().getBmoWFlowCategory());
		}

		// Filtrar tipos de Flujos por Categoria Proyecto
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoPropertyRental.getBmoWFlowType());

		if (!isMobile()) {
			// Filtrar fases por Categoría Proyecto
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmFilter filterWFlowPhase = new BmFilter();
			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
					bmoWFlowPhase.getWFlowCategoryId().getName(), 
					bmoWFlowCategory.getIdFieldName(),
					bmoWFlowCategory.getProgramId().getName(),
					"" + bmObjectProgramId);

			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoPropertyRental.getBmoWFlow().getBmoWFlowPhase());

		}

		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoPropertyRental.getUserId());

		if (isMaster()) {
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoPropertyRental.getStatus()), bmoPropertyRental, bmoPropertyRental.getStatus());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoPropertyRental.getEnabled()), bmoPropertyRental, bmoPropertyRental.getEnabled(), ""+BmoPropertyRental.ENABLED);	
		
			if (!isMobile()) 
				addTagFilterListBox(bmoPropertyRental.getTags());
		}
	}

	@Override
	public void create() {
		UiPropertyRentForm uiPropertyRentaForm = new UiPropertyRentForm(getUiParams(), 0);
		uiPropertyRentaForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPropertyRental = (BmoPropertyRental)bmObject;
		//Menu
		UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), bmoPropertyRental.getId());
		uiPropertyRentalDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiPropertyRentForm   uiPropertyRentForm = new UiPropertyRentForm(getUiParams(), bmObject.getId());
		uiPropertyRentForm.show();
	}

	public class UiPropertyRentForm extends UiFormDialog {

		UiListBox statusListBox = new UiListBox(getUiParams());
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiDateBox startDateBox = new UiDateBox();
		UiDateBox endDateBox = new UiDateBox();
		UiListBox orderTypeListBox;
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
		UiListBox currencyUiListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		UiTagBox tagBox = new UiTagBox(getUiParams());
		UiSuggestBox userSuggestBox;
		UiListBox wFlowTypeListBox;

		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiSuggestBox propertySuggestBox = new UiSuggestBox(new BmoProperty());
		TextBox contractTermField = new TextBox();	
		TextBox initialIconmeTextBox = new TextBox();
		TextBox currentIconmeTextBox = new TextBox();
		UiDateBox rentalScheduleDateDateBox = new UiDateBox();	
		TextBox ownerPropertyTextBox = new TextBox();
		UiListBox customerContactIdListBox = new UiListBox(getUiParams(), new BmoCustomerContact());
		UiDateBox rentIncreaseUiDateBox = new UiDateBox();
	
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		int programId;
		String generalSection = "Datos Generales";
		String dateTimeSection = "Fecha y Hora";
		
		Button copyPrrtButtonDialog = new Button("RENOVAR"); 		
		DialogBox copyContractDialogBox;
		private int dataOwnerPropertyRpcAttempt = 0;


		public UiPropertyRentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPropertyRental(), id);
			
			copyPrrtButtonDialog.setStyleName("formCloseButton");
			copyPrrtButtonDialog.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyContract();
					
				}
			});	
			
			
			initialize();

			// Propiertario del inmueble
			//ownerPropertyField = new BmField("ownerProperty", "", "Arrendador", 200, Types.VARCHAR, true, BmFieldType.STRING, false);
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoPropertyRental.getProgramCode());
				populateProperties();
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
			}

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

			// Filtrar por tipos de flujos ligados a proyectos
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por tipos de pedidos de tipo renta
			BmoOrderType bmoOrderType = new BmoOrderType();
			BmFilter rentalFilter = new BmFilter();
			rentalFilter.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_LEASE);

			// Filtrar clientes de categiria arrendatarios
			BmoCustomer bmoCustomer = new BmoCustomer(); 
			BmFilter filterByLessor = new BmFilter();
			filterByLessor.setValueOperatorFilter(bmoCustomer.getKind(), bmoCustomer.getCustomercategory(), BmFilter.EQUALS, ""+BmoCustomer.CATEGORY_LESSOR);
			customerSuggestBox.addFilter(filterByLessor);

			orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType(), rentalFilter);

			// Renta Vigente, copiar dato de Renta Inicial
			ValueChangeHandler<String> textChangeInitialIconmencomeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (newRecord)
						currentIconmeTextBox.setText(initialIconmeTextBox.getText());
				}
			};
			initialIconmeTextBox.addValueChangeHandler(textChangeInitialIconmencomeHandler);

			// Al cambiar Renta Vigente, re-calcular y guardar.
			ValueChangeHandler<String> textChangeCurrentIncomeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (!newRecord)
						currentIncomeChange();
				}
			};
			currentIconmeTextBox.addValueChangeHandler(textChangeCurrentIncomeHandler);

			// Al cambiar Renta Vigente, re-calcular y guardar.
			ValueChangeHandler<Date> rentalScheduleDateHandler = new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					if (!newRecord)
						currentIncomeChange();
				}
			};
			rentalScheduleDateDateBox.addValueChangeHandler(rentalScheduleDateHandler);

			// Al cambiar Renta Vigente, re-calcular y guardar.
			ValueChangeHandler<Date> rentIncreaseHandler = new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					if (!newRecord)
						currentIncomeChange();
				}
			};
			rentIncreaseUiDateBox.addValueChangeHandler(rentIncreaseHandler);
		}

		@Override
		public void populateFields() {
			bmoPropertyRental = (BmoPropertyRental)getBmObject();
			UiPropertyForm uiPropertyForm = new UiProperty(getUiParams()).getUiPropertyForm();
			UiCustomerForm uiCustomerForm = new UiCustomer(getUiParams()).getUiCustomerForm();

			// Asgina valores por defecto
			try {
				if (newRecord) {
					if (!(bmoPropertyRental.getOrderTypeId().toString().length() > 0))
						bmoPropertyRental.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());

					// Asigna empresa si es registro nuevo
					if (!(bmoPropertyRental.getCompanyId().toInteger() > 0))
						bmoPropertyRental.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());

					bmoPropertyRental.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoPropertyRental.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

					// Si no esta asignada la moneda, buscar por la default
					if (!(bmoPropertyRental.getCurrencyId().toInteger() > 0))
						bmoPropertyRental.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());

					bmoPropertyRental.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoPropertyRental.getEndDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));		

					getParityFromCurrency(bmoPropertyRental.getCurrencyId().toString());

				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 4);						
			formFlexTable.addFieldReadOnly(3, 0, codeTextBox, bmoPropertyRental.getCode());
			formFlexTable.addField(4, 0, orderTypeListBox, bmoPropertyRental.getOrderTypeId());	
			formFlexTable.addField(5, 0, wFlowTypeListBox, bmoPropertyRental.getWFlowTypeId());
			formFlexTable.addField(6, 0, propertySuggestBox, bmoPropertyRental.getPropertyId(), uiPropertyForm, new BmoProperty());
			formFlexTable.addField(7, 0, nameTextBox, bmoPropertyRental.getName());
			formFlexTable.addField(8, 0, descriptionTextArea, bmoPropertyRental.getDescription());
			formFlexTable.addField(9, 0, ownerPropertyTextBox, bmoPropertyRental.getOwnerProperty()); 
			formFlexTable.addField(10, 0, customerSuggestBox, bmoPropertyRental.getCustomerId(),uiCustomerForm, new BmoCustomer());
			formFlexTable.addField(11, 0, userSuggestBox, bmoPropertyRental.getUserId());
			formFlexTable.addField(12, 0, startDateBox, bmoPropertyRental.getStartDate());
			formFlexTable.addField(13, 0, endDateBox, bmoPropertyRental.getEndDate());
			formFlexTable.addField(14, 0, companyListBox, bmoPropertyRental.getCompanyId());
			formFlexTable.addField(15, 0, contractTermField, bmoPropertyRental.getContractTerm());
			formFlexTable.addField(16, 0, initialIconmeTextBox, bmoPropertyRental.getInitialIconme());
			formFlexTable.addField(17, 0, currentIconmeTextBox, bmoPropertyRental.getCurrentIncome());
			formFlexTable.addField(18, 0, rentalScheduleDateDateBox, bmoPropertyRental.getRentalScheduleDate());
			formFlexTable.addField(19, 0, rentIncreaseUiDateBox, bmoPropertyRental.getRentIncrease());
			formFlexTable.addField(20, 0, marketListBox, bmoPropertyRental.getMarketId());
			//formFlexTable.addField(20, 0, customerContactIdListBox, bmoPropertyRental.getCustomerContactId());
			formFlexTable.addField(21, 0, currencyUiListBox, bmoPropertyRental.getCurrencyId());
			formFlexTable.addField(22, 0, currencyParityTextBox, bmoPropertyRental.getCurrencyParity());
			formFlexTable.addField(23, 0, tagBox, bmoPropertyRental.getTags());
			formFlexTable.addField(24, 0, statusListBox, bmoPropertyRental.getStatus());
//			formFlexTable.addField(25, 0, enabledUiListBox,bmoPropertyRental.getEnabled());
			
		}

		@Override
		public void postShow() {
			statusEffect();
			buttonPanel.add(copyPrrtButtonDialog);
			if (bmoPropertyRental.getStatus().toString().equals(""+BmoPropertyRental.STATUS_FINISHED)) {							
					copyPrrtButtonDialog.setVisible(true);							
			}else {
				copyPrrtButtonDialog.setVisible(false);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPropertyRental.setId(id);
			bmoPropertyRental.getStatus().setValue(statusListBox.getSelectedCode());
			bmoPropertyRental.getCode().setValue(codeTextBox.getText());
			bmoPropertyRental.getName().setValue(nameTextBox.getText());
			bmoPropertyRental.getDescription().setValue(descriptionTextArea.getText());
			bmoPropertyRental.getStartDate().setValue(startDateBox.getTextBox().getText());
			bmoPropertyRental.getEndDate().setValue(endDateBox.getTextBox().getText());		
			bmoPropertyRental.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoPropertyRental.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoPropertyRental.getMarketId().setValue(marketListBox.getSelectedId());
			bmoPropertyRental.getCurrencyId().setValue(currencyUiListBox.getSelectedId());
			bmoPropertyRental.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoPropertyRental.getTags().setValue(tagBox.getTagList());
			bmoPropertyRental.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoPropertyRental.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoPropertyRental.getCustomerContactId().setValue(customerContactIdListBox.getSelectedId());
			bmoPropertyRental.getPropertyId().setValue(propertySuggestBox.getSelectedId());
			bmoPropertyRental.getContractTerm().setValue(contractTermField.getValue());
			bmoPropertyRental.getInitialIconme().setValue(initialIconmeTextBox.getText());
			bmoPropertyRental.getCurrentIncome().setValue(currentIconmeTextBox.getText());
			bmoPropertyRental.getRentalScheduleDate().setValue(rentalScheduleDateDateBox.getTextBox().getText());
			bmoPropertyRental.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoPropertyRental.getRentIncrease().setValue(rentIncreaseUiDateBox.getTextBox().getText());
			bmoPropertyRental.getOwnerProperty().setValue(ownerPropertyTextBox.getText());
//			bmoPropertyRental.getEnabled().setValue(enabledUiListBox.getSelectedCode());
			
			return bmoPropertyRental;
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status del Contrato?");
			} 
			else if (event.getSource() == currencyUiListBox) {
				getParityFromCurrency(currencyUiListBox.getSelectedId());
			}
			statusEffect();
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == startDateBox) {
				getParityFromCurrency(currencyUiListBox.getSelectedId());
				if(newRecord) {
					endDateBox.getTextBox().setValue(startDateBox.getTextBox().getValue());
				}
			}
			else if (event.getSource() == endDateBox) {
				getYearBetweenTwoDates();
			}
			else if (event.getSource() == rentalScheduleDateDateBox) {
				Date rentalScheduleDate = new Date();
				rentalScheduleDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(rentalScheduleDateDateBox.getTextBox().getText());
				
				Date endDate = new Date();
				Date startDate = new Date();
				endDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(endDateBox.getTextBox().getText());
				startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(startDateBox.getTextBox().getText());
				
				if(endDate.before(rentalScheduleDate)) {
					showSystemMessage("Error de captura: <b> La Fecha 1er. Renta no puede ser mayor a Fecha Fin.</b>");					
				}
				else if (startDate.after(rentalScheduleDate)){
					showSystemMessage("Error de captura: <b> La Fecha 1er. Renta no puede ser menor a Fecha Inicio.</b>");					
				}
				
			}
			else if (event.getSource() == rentIncreaseUiDateBox) {
				// Revisa fechas fecha incremento debe ser menor igual a la fecha de fin contrato
				Date rentIncreaseDate = new Date();
				
				rentIncreaseDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(rentIncreaseUiDateBox.getTextBox().getText());
								
				Date endDate = new Date();
				Date startDate = new Date();
				endDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(endDateBox.getTextBox().getText());
				startDate = DateTimeFormat.getFormat(getUiParams().getSFParams().getDateFormat())
						.parse(startDateBox.getTextBox().getText());
				
				if(endDate.before(rentIncreaseDate)) {
					showSystemMessage("Error de captura: <b> La Fecha Incremento no puede ser mayor a Fecha Fin.</b>");					
				}
				else if (startDate.after(rentIncreaseDate)){
					showSystemMessage("Error de captura: <b> La Fecha Incremento no puede ser menor a Fecha Inicio.</b>");					
				}
			}
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == propertySuggestBox) {
				if(propertySuggestBox.getSelectedId() > 0) {
					dataOwnerProperty(propertySuggestBox.getSelectedId());
				}
				else {
					nameTextBox.setText("");
					ownerPropertyTextBox.setText("");
				}
				
			}
			if(uiSuggestBox == customerSuggestBox) {
				//BmoCustomer bmoCustomer  = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
				populateCustomerContacts(customerSuggestBox.getSelectedId());
			}
			statusEffect();
		}
		
		private void populateProperties() {
			propertySuggestBox.clear();
			setPropertySuggestBoxFilters();
		}
		
		// Asignar filtros de arrendatarios en cliente 
		private void setPropertySuggestBoxFilters() {
			
			BmoProperty bmoProperty = new BmoProperty();
					
			BmFilter filterByAvailable = new BmFilter();
			filterByAvailable.setValueOperatorFilter(bmoProperty.getKind(), bmoProperty.getAvailable(), BmFilter.EQUALS, ""+1);
			propertySuggestBox.addFilter(filterByAvailable);
			
			BmFilter filterByOpen = new BmFilter();
			filterByOpen.setValueOperatorFilter(bmoProperty.getKind(), bmoProperty.getCansell(), BmFilter.EQUALS, ""+1);
			propertySuggestBox.addFilter(filterByOpen);
		}

		//Obtener la diferencia de dos fechas
		public void getYearBetweenTwoDates() {
			Date startDate = getDate(startDateBox.getTextBox().getText(), getUiParams().getSFParams().getDateFormat());
			Date endDate = getDate(endDateBox.getTextBox().getText(), getUiParams().getSFParams().getDateFormat());

			int diffYears = CalendarUtil.getDaysBetween(startDate, endDate);
			diffYears = diffYears / 365;
			contractTermField.setValue(String.valueOf(diffYears));
		}

		// Obtiene fecha
		private Date getDate(String dateString, String format) {
			Date result = null;
			try {
				DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(format);
				result = dateTimeFormat.parse(dateString);
			} catch (Exception e) {
				showSystemMessage(this.getClass().getName() + "-getDate() Error " + e.toString() + ", date: ->" + dateString + "<-, format: " + format);
			}
			return result;
		}

		// Asigna filtros al listado de direcciones del cliente
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
		
			orderTypeListBox.setEnabled(false);
			nameTextBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			startDateBox.setEnabled(false);
			endDateBox.setEnabled(false);
			statusListBox.setEnabled(false);
			currencyUiListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			marketListBox.setEnabled(false);
			customerContactIdListBox.setEnabled(false);
			propertySuggestBox.setEnabled(false);
			contractTermField.setEnabled(false);
			ownerPropertyTextBox.setEnabled(false);
			wFlowTypeListBox.setEnabled(false);
			descriptionTextArea.setEnabled(false);
			companyListBox.setEnabled(false);
			rentIncreaseUiDateBox.setEnabled(false);

			if (newRecord) { 
				orderTypeListBox.setEnabled(true);
				marketListBox.setEnabled(true);		
				customerSuggestBox.setEnabled(true);
				propertySuggestBox.setEnabled(true);
				wFlowTypeListBox.setEnabled(true);
				descriptionTextArea.setEnabled(true);
				companyListBox.setEnabled(true);
				rentIncreaseUiDateBox.setEnabled(true);
			} else {
				dataOwnerProperty(bmoPropertyRental.getPropertyId().toInteger());
				currentIconmeTextBox.setEnabled(false);
				initialIconmeTextBox.setEnabled(false);
				startDateBox.setEnabled(false);
				endDateBox.setEnabled(false);
				rentalScheduleDateDateBox.setEnabled(false);
				rentIncreaseUiDateBox.setEnabled(false);
			}

			if (bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_AUTHORIZED)
					|| bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_FINISHED)
					|| bmoPropertyRental.getStatus().equals(BmoPropertyRental.STATUS_CANCEL)) {
				
				initialIconmeTextBox.setEnabled(false);
				currentIconmeTextBox.setEnabled(false);
				rentalScheduleDateDateBox.setEnabled(false);
				descriptionTextArea.setEnabled(false);
				companyListBox.setEnabled(false);
				rentIncreaseUiDateBox.setEnabled(false);
			}

			if (bmoPropertyRental.getStatus().toChar() == BmoPropertyRental.STATUS_REVISION) {
				nameTextBox.setEnabled(true);
				userSuggestBox.setEnabled(true);
//				startDateBox.setEnabled(true);
//				endDateBox.setEnabled(true);
				startDateBox.setEnabled(true);
				endDateBox.setEnabled(true);
				currencyUiListBox.setEnabled(true);
				//rentIncreaseUiDateBox.setEnabled(true);	

				// Esta en revision
				if (currencyUiListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
					currencyParityTextBox.setEnabled(true);
				} else {
					currencyParityTextBox.setEnabled(false);
				}			
			}

			if (!newRecord && getSFParams().hasSpecialAccess(BmoPropertyRental.ACCESS_CHANGESTATUS)) 
				statusListBox.setEnabled(true);

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
			
		}

		@Override
		public void close() {
//			list();
//			// El eliminar un contrato regresar al listado de Contratos.
//			showList();
			if (deleted) new UiPropertyRental(getUiParams()).show();
		}

		public void showList() {
			UiPropertyRental uiPropertyRental = new UiPropertyRental(getUiParams());
			setUiType(UiParams.MASTER);
			uiPropertyRental.show();
		}
		@Override
		public void saveNext() {
			if (newRecord) {
				// Si esta asignado el tipo de proyecto, envia al dashboard, en caso contrario, envia a la lista
				if (bmoPropertyRental.getWFlowTypeId().toInteger() > 0) {
					UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), id);
					uiPropertyRentalDetail.show();
				} else {
					list();
				}
			} else {
				if (id > 0) {
					UiPropertyRentalDetail uiPropertyRentalDetail = new UiPropertyRentalDetail(getUiParams(), id);
					uiPropertyRentalDetail.show();
				} else {
					list();
				}
			}
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

		private void populateCustomerContacts(int customerId) {
			// Filtros de contactos del cliente
			customerContactIdListBox.clear();
			customerContactIdListBox.clearFilters();
			setCustomerContactsListBoxFilters(customerId);
			customerContactIdListBox.populate(bmoPropertyRental.getCustomerContactId());
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

		private void dataOwnerProperty(int propertyId) {
			dataOwnerProperty(0, propertyId);
		}

		private void dataOwnerProperty(int dataOwnerPropertyRpcAttemp, int propertyId) {
			if (dataOwnerPropertyRpcAttempt < 5) {
				setDataOwnerPropertyRpcAttempt(dataOwnerPropertyRpcAttempt + 1);

				// Establece eventos ante respuesta de servicio
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getDataOwnerPropertyRpcAttempt() < 5) dataOwnerProperty(getDataOwnerPropertyRpcAttempt());
						else showErrorMessage(this.getClass().getName() + "-dataOwnerProperty() ERROR: " + caught.toString());
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						setDataOwnerPropertyRpcAttempt(0);
						if (result.hasErrors()) 
							showSystemMessage("Error al traer datos del Arrendador: " + result.errorsToString());
						else {
							if(propertySuggestBox.getSelectedId() <= 0) {
								nameTextBox.setText("");
								ownerPropertyTextBox.setText("");
							}
							BmoProperty bmoProperty = (BmoProperty)result.getBmObject();
							ownerPropertyTextBox.setText(result.getMsg());
							if (nameTextBox.getText().equals(""))
								nameTextBox.setText(bmoProperty.getDescription().toString());
						}
					}
				};

				// Llamada al servicio RPC
				try {
					
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoPropertyRental.getPmClass(), bmoPropertyRental, BmoOrder.ACTION_GETDATAOWNERPROPERTY, "" + propertyId, callback);
					
				} catch (SFException e) {
					showErrorMessage(this.getClass().getName() + "-dataOwnerProperty() ERROR: " + e.toString());
				}
			}
		}

		public void currentIncomeChange() {
			try {
				double currentIncome = Double.parseDouble(currentIconmeTextBox.getText());
				bmoPropertyRental.getCurrentIncome().setValue(currentIncome);
				bmoPropertyRental.getRentalScheduleDate().setValue(rentalScheduleDateDateBox.getTextBox().getText());
				bmoPropertyRental.getRentIncrease().setValue(rentIncreaseUiDateBox.getTextBox().getText());

				String values = "" +bmoPropertyRental.getCurrentIncome().toDouble() 
						+"|" + bmoPropertyRental.getRentalScheduleDate().toString()
						+"|" + bmoPropertyRental.getRentIncrease().toString();
				saveCurrentIncomeChange(values);

			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-discountChange() ERROR: " + e.toString());
			}
		}

		public void saveCurrentIncomeChange(String values) {
			BmoOrderPropertyTax bmoOrderPropertyTax = new BmoOrderPropertyTax();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-saveCurrentIncomeChange() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) {
						showErrorMessage("Existen Errores " + result.errorsToString());
					}
					//reset();
					//formFlexTable.showSection(propertyTaxSection);
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoOrderPropertyTax.getPmClass(), bmoPropertyRental,
							BmoOrderPropertyTax.ACTION_CHANGEPRICE, "" + values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-updateOrderPayment() ERROR: " + e.toString());
			}
		}


		public int getDataOwnerPropertyRpcAttempt() {
			return dataOwnerPropertyRpcAttempt;
		}

		public void setDataOwnerPropertyRpcAttempt(int dataOwnerPropertyRpcAttempt) {
			this.dataOwnerPropertyRpcAttempt = dataOwnerPropertyRpcAttempt;
		}
		
		public void copyContract() {
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-copyContract() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					if(!result.hasErrors()) {										
						fromPropertyRental = (BmoPropertyRental)result.getBmObject();
						stopLoading();
						dialogClose();				

						UiPropertyRental uiPropertyRental = new UiPropertyRental(getUiParams());
						uiPropertyRental.open(fromPropertyRental);
						uiPropertyRental.edit(fromPropertyRental);
					}
					else {
						stopLoading();						
						showErrorMessage(result.getMsg());
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					
						getUiParams().getBmObjectServiceAsync().action(bmoPropertyRental.getPmClass(), bmoPropertyRental, BmoPropertyRental.ACTION_COPYCONTRATC, "", callback);
				}
			} catch (SFException e) {
				stopLoading();				
				showErrorMessage(this.getClass().getName() + "-action() ERROR: " + e.toString());
			}		
				
			
		}

	}
}
