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

import com.flexwm.client.ev.UiVenue;
import com.flexwm.client.ev.UiVenue.UiVenueForm;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.cm.BmoMarket;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.ev.BmoVenue;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.flexwm.shared.op.BmoWarehouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
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


public class UiProject extends UiList {

	BmoProject bmoProject;

	public UiProject(UiParams uiParams) {
		super(uiParams, new BmoProject());
		bmoProject = (BmoProject)getBmObject();
	}

	public UiProject(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoProject());
		bmoProject = (BmoProject)getBmObject();
	}

	@Override
	public void postShow() {
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();


		if (!isMobile()) {
			// Filtrar categorias de Flujos por Modulo Proyecto
			BmFilter filterWFlowCategory = new BmFilter();
			filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoProject.getBmoWFlowType().getBmoWFlowCategory());
		}

		// Filtrar tipos de Flujos por Categoria Proyecto
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoProject.getBmoWFlowType());

		if (!isMobile()) {
			// Filtrar fases por Categoría Proyecto
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmFilter filterWFlowPhase = new BmFilter();
			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
					bmoWFlowPhase.getWFlowCategoryId().getName(), 
					bmoWFlowCategory.getIdFieldName(),
					bmoWFlowCategory.getProgramId().getName(),
					"" + bmObjectProgramId);

//			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoProject.getBmoWFlow().getBmoWFlowPhase());

			//Filtar por luagres
			addFilterSuggestBox(new UiSuggestBox(new BmoVenue()),  new BmoVenue(), bmoProject.getVenueId());

		}
		/*
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
				addFilterListBox(new UiListBox(getUiParams(), new BmoUser(), filterSalesmen), bmoProject.getBmoUser());
		 */

		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoProject.getUserId());

		if (isMaster()) {
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoProject.getStatus()), bmoProject, bmoProject.getStatus());
			if (!isMobile()) 
				addTagFilterListBox(bmoProject.getTags());

		}
		newImage.setVisible(false);
		if (getUiParams().getSFParams().hasSpecialAccess(BmoProject.ACCESS_CREATEPROJECT)) {
			newImage.setVisible(true);
		}
	}

	@Override
	public void create() {
		UiProjectForm uiProjectForm = new UiProjectForm(getUiParams(), 0);
		uiProjectForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoProject = (BmoProject)bmObject;
		UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), bmoProject.getId());
		uiProjectDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiProjectForm uiProjectForm = new UiProjectForm(getUiParams(), bmObject.getId());
		uiProjectForm.show();
	}

	public class UiProjectForm extends UiFormDialog {

		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextArea commentsTextArea = new TextArea();
		UiDateTimeBox startDateBox = new UiDateTimeBox();
		UiDateTimeBox endDateBox = new UiDateTimeBox();
		UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiSuggestBox userSuggestBox;
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox marketListBox = new UiListBox(getUiParams(), new BmoMarket());
		UiListBox orderTypeListBox;
		TextBox totalTextBox = new TextBox();
		UiTagBox tagBox = new UiTagBox(getUiParams());
		UiListBox wFlowTypeListBox;
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiSuggestBox venueSuggestBox = new UiSuggestBox(new BmoVenue());
		//UiListBox customerAddressListBox = new UiListBox(getUiParams(), new BmoCustomerAddress());
		UiDateBox dateContractDateBox = new UiDateBox();

		//copiar proyecto
		Button copyProjectButton = new Button("COPIAR");
		Button copyProjectDialogButton = new Button("COPIAR PROYECTOS");
		Button copyProjectCloseDialogButton = new Button("CERRAR");
		DialogBox copyProjectDialogBox;
		UiSuggestBox copyProjectSuggestBox = new UiSuggestBox(new BmoProject());

		TextBox guestsTextBox = new TextBox();
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		TextBox homeAddressTextBox = new TextBox();
		Button sendPoll = new Button("ENVIAR ENCUESTA");
		UiListBox currencyUiListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();
		int programId;
		String generalSection = "Datos Generales";
		String dateTimeSection = "Fechas";
		String locationSection = "Lugar / Ubicación";
		UiSuggestBox warehouseManagerIdSuggestBox = new UiSuggestBox(new BmoUser());

		BmoWFlowType bmoWFlowType;


		public UiProjectForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProject(), id);
			initialize();

			copyProjectDialogButton.setStyleName("formCloseButton");
			copyProjectDialogButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyProjectDialog();
				}
			});

			copyProjectButton.setStyleName("formCloseButton");
			copyProjectButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {				
					copyProjectsAction();
					copyProjectDialogBox.hide();
				}
			});


			copyProjectCloseDialogButton.setStyleName("formCloseButton");
			copyProjectCloseDialogButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					copyProjectDialogBox.hide();
				}
			});
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoProject.getProgramCode());
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

			// Filtrar Responsables de almacen activos
			warehouseManagerIdSuggestBox.addFilter(filterSalesmenActive);

			// Filtrar solo usuarios que estan en almacenes
			BmoWarehouse bmoWarehouse = new BmoWarehouse();
			BmFilter filterUserWarehouse = new BmFilter();
			filterUserWarehouse.setInFilter(bmoWarehouse.getKind(), 
					bmoUser.getIdFieldName(), 
					bmoWarehouse.getUserId().getName(), "1" , "1");
			warehouseManagerIdSuggestBox.addFilter(filterUserWarehouse);

			// Filtrar por tipos de flujos ligados a proyectos
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por tipos de pedidos de tipo renta
			BmoOrderType bmoOrderType = new BmoOrderType();
			BmFilter rentalFilter = new BmFilter();
			rentalFilter.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_RENTAL);
			orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType(), rentalFilter);

			// Botón de Enviar encuesta al cliente
			sendPoll.setStyleName("formCloseButton");
			if(newRecord) sendPoll.setVisible(false);
			else sendPoll.setVisible(true);
			sendPoll.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("¿Está seguro que desea enviar la Encuesta al Cliente?")) sendPoll();
				}
			});

		}

		@Override
		public void populateFields() {
			bmoProject = (BmoProject)getBmObject();

			// Asgina valores por defecto
			try {
				if (newRecord) {
					//					if (!(bmoProject.getOrderTypeId().toString().length() > 0))
					//						bmoProject.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());

					// Asigna empresa si es registro nuevo
					if (!(bmoProject.getCompanyId().toInteger() > 0))
						bmoProject.getCompanyId().setValue(getSFParams().getBmoSFConfig().getDefaultCompanyId().toString());

					bmoProject.getUserId().setValue(getSFParams().getLoginInfo().getUserId());

					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoProject.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

					// Si no esta asignada la moneda, buscar por la default
					if (!(bmoProject.getCurrencyId().toInteger() > 0))
						bmoProject.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());

					getParityFromCurrency(bmoProject.getCurrencyId().toString());
				}
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
			}

			UiVenueForm uiVenueForm = new UiVenue(getUiParams()).getUiVenueForm();

			formFlexTable.addSectionLabel(1, 0, generalSection, 4);
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoProject.getCode());
			formFlexTable.addField(3, 0, orderTypeListBox, bmoProject.getOrderTypeId());	
			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoProject.getWFlowTypeId());
			formFlexTable.addField(5, 0, nameTextBox, bmoProject.getName());
			formFlexTable.addField(6, 0, descriptionTextArea, bmoProject.getDescription());
			formFlexTable.addField(7, 0, commentsTextArea, bmoProject.getComments());
			formFlexTable.addField(8, 0, customerSuggestBox, bmoProject.getCustomerId());
			formFlexTable.addField(9, 0, userSuggestBox, bmoProject.getUserId());
			formFlexTable.addField(10, 0, companyListBox, bmoProject.getCompanyId());
			formFlexTable.addField(11, 0, marketListBox, bmoProject.getMarketId());
			formFlexTable.addField(12, 0, guestsTextBox, bmoProject.getGuests());
			formFlexTable.addField(13, 0, warehouseManagerIdSuggestBox, bmoProject.getWarehouseManagerId());
			formFlexTable.addField(14, 0, currencyUiListBox, bmoProject.getCurrencyId());
			formFlexTable.addField(15, 0, currencyParityTextBox, bmoProject.getCurrencyParity());
			formFlexTable.addField(16, 0, tagBox, bmoProject.getTags());
			formFlexTable.addFieldReadOnly(17, 0, totalTextBox,bmoProject.getTotal());
			formFlexTable.addField(18, 0, statusListBox, bmoProject.getStatus());
			formFlexTable.addSectionLabel(19, 0, dateTimeSection, 4);
			formFlexTable.addField(20, 0, startDateBox, bmoProject.getStartDate());
			formFlexTable.addField(21, 0, endDateBox, bmoProject.getEndDate());
			formFlexTable.addField(22, 0, dateContractDateBox, bmoProject.getDateContract());
			formFlexTable.addSectionLabel(23, 0, locationSection, 4);
			formFlexTable.addField(24, 0, venueSuggestBox, bmoProject.getVenueId(), uiVenueForm, new BmoVenue());
			formFlexTable.addField(25, 0, homeAddressTextBox, bmoProject.getHomeAddress());
			//			setCustomerAddressListBoxFilters(customerSuggestBox.getSelectedId());
			//			formFlexTable.addField(23, 0, customerAddressListBox, bmoProject.getCustomerAddressId());
			formFlexTable.hideField(bmoProject.getHomeAddress());
			if (newRecord) {
				//formFlexTable.hideField(bmoProject.getCustomerAddressId());
				formFlexTable.hideField(bmoProject.getWarehouseManagerId());
			}
		}

		@Override
		public void postShow() {
			statusEffect();
			if (!newRecord) {
				buttonPanel.add(sendPoll);
			}else {
				buttonPanel.add(copyProjectDialogButton);
				if (getSFParams().hasSpecialAccess(BmoProject.ACCESS_COPYPROJECT)) 
					copyProjectDialogButton.setVisible(true);
				else
					copyProjectDialogButton.setVisible(false);
			}

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoProject.setId(id);
			bmoProject.getCode().setValue(codeTextBox.getText());
			bmoProject.getName().setValue(nameTextBox.getText());
			bmoProject.getDescription().setValue(descriptionTextArea.getText());
			bmoProject.getComments().setValue(commentsTextArea.getText());
			bmoProject.getStartDate().setValue(startDateBox.getDateTime());
			bmoProject.getEndDate().setValue(endDateBox.getDateTime());		
			bmoProject.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoProject.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoProject.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoProject.getVenueId().setValue(venueSuggestBox.getSelectedId());
			bmoProject.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoProject.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoProject.getMarketId().setValue(marketListBox.getSelectedId());
			bmoProject.getStatus().setValue(statusListBox.getSelectedCode());
			bmoProject.getGuests().setValue(guestsTextBox.getText());
			bmoProject.getTags().setValue(tagBox.getTagList());
			//bmoProject.getCustomerAddressId().setValue(customerAddressListBox.getSelectedId());
			bmoProject.getHomeAddress().setValue(homeAddressTextBox.getText());
			bmoProject.getCurrencyId().setValue(currencyUiListBox.getSelectedId());
			bmoProject.getCurrencyParity().setValue(currencyParityTextBox.getText());
			bmoProject.getWarehouseManagerId().setValue(warehouseManagerIdSuggestBox.getSelectedId());
			bmoProject.getDateContract().setValue(dateContractDateBox.getTextBox().getText());
			bmoProject.getTotal().setValue(totalTextBox.getText());
			return bmoProject;
		}


		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status del Proyecto?");
			} 
			//			else if (event.getSource() == customerAddressListBox) {
			//				// Desactivar Salones si hay una Direccion del cliente seleccionada
			//					if (Integer.parseInt(customerAddressListBox.getSelectedId()) > 0) {
			//						venueSuggestBox.clear();
			//						venueSuggestBox.setEnabled(false);
			//				} else {
			//					venueSuggestBox.setEnabled(true);
			//				}
			//			} 
			else if (event.getSource() == currencyUiListBox) {
				getParityFromCurrency(currencyUiListBox.getSelectedId());

			} else if (event.getSource() == orderTypeListBox) {
				BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
				if (bmoOrderType != null) {

					// Modificar la lista de Tipos de Flujo
					changeOrderType(bmoOrderType);
				}
			} 
			statusEffect();
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {
			if (event.getSource() == startDateBox) {
				getParityFromCurrency(currencyUiListBox.getSelectedId());
				if(newRecord) {
					endDateBox.setDate(startDateBox.getTextBox().getValue());
				}
			}
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == customerSuggestBox) {
				// Llenar moneda del cliente
				BmoCustomer bmoCustomer = new BmoCustomer();
				bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
				if (bmoCustomer != null) {
					// Permitir elegir direccion del cliente
					//populateCustomerAddress(customerSuggestBox.getSelectedId());
					formFlexTable.showField(bmoProject.getCustomerAddressId());
					//customerAddressListBox.setEnabled(true);
					
					if (bmoCustomer.getMarketId().toInteger() > 0) {
						marketListBox.setSelectedId("" + bmoCustomer.getMarketId().toInteger());
					} else marketListBox.setSelectedId("0");
				} else 
					marketListBox.setSelectedId("0");
				
			} else if (uiSuggestBox == venueSuggestBox) {
				// Activar campo si esta marcado el Salon como casa particular
				BmoVenue bmoVenue = (BmoVenue)venueSuggestBox.getSelectedBmObject();
				if (bmoVenue != null) {
					if (bmoVenue.getHomeAddress().toBoolean() && bmoVenue.getId() > 0) {
						formFlexTable.showField(bmoProject.getHomeAddress());
						homeAddressTextBox.setText("");
					}
				} else  {
					homeAddressTextBox.setText("");
					formFlexTable.hideField(bmoProject.getHomeAddress());
				}
			}

			statusEffect();
		}

		//		// Actualiza combo de direcciones del cliente
		//		private void populateCustomerAddress(int customerId) {
		//			customerAddressListBox.clear();
		//			customerAddressListBox.clearFilters();
		//			setCustomerAddressListBoxFilters(customerId);
		//			if (customerId > 0)
		//				customerAddressListBox.populate(bmoProject.getCustomerAddressId().toString());
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

		// Cambia el tipo de pedido y modifica combo de Tipos de Flujo
		private void changeOrderType(BmoOrderType bmoOrderType) {
			wFlowTypeListBox.clear();
			wFlowTypeListBox.clearFilters();

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

			//wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
			wFlowTypeListBox.addFilter(bmFilter); 

			// Si el registro ya existia, es posible que se haya deshabilitado flujo, asegura que se muestre
			if (newRecord) {
				wFlowTypeListBox.addFilter(bmFilterByStatus);
				wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
			}

			wFlowTypeListBox.populate("");
		}

		private void statusEffect() {
			orderTypeListBox.setEnabled(false);
			nameTextBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			if(getSFParams().hasSpecialAccess(BmoProject.ACCESS_CHANGECUSTOMER)) {
				customerSuggestBox.setEnabled(true);
			}
			userSuggestBox.setEnabled(false);
			startDateBox.setEnabled(false);
			endDateBox.setEnabled(false);
			dateContractDateBox.setEnabled(false);
			venueSuggestBox.setEnabled(false);
			guestsTextBox.setEnabled(false);
			statusListBox.setEnabled(false);
			currencyUiListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			marketListBox.setEnabled(false);

			if (newRecord) { 
				orderTypeListBox.setEnabled(true);
				marketListBox.setEnabled(true);		
				customerSuggestBox.setEnabled(true);
			}

			if (bmoProject.getStatus().toChar() == BmoProject.STATUS_REVISION) {
				nameTextBox.setEnabled(true);
				userSuggestBox.setEnabled(true);
				startDateBox.setEnabled(true);
				endDateBox.setEnabled(true);
				dateContractDateBox.setEnabled(true);
				venueSuggestBox.setEnabled(true);
				guestsTextBox.setEnabled(true);
				currencyUiListBox.setEnabled(true);
				marketListBox.setEnabled(true);	
				// Esta en revision
				if (currencyUiListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString()) {
					currencyParityTextBox.setEnabled(true);
				} else {
					currencyParityTextBox.setEnabled(false);
				}

				if (!homeAddressTextBox.getText().equals(""))
					formFlexTable.showField(bmoProject.getHomeAddress());
				//				if (!(venueSuggestBox.getSelectedId() > 0) && !(Integer.parseInt(customerAddressListBox.getSelectedId()) > 0)) {
				//					venueSuggestBox.setEnabled(true);
				//					customerAddressListBox.setEnabled(true);
				//				} else {
				//					if (venueSuggestBox.getSelectedId() > 0) {
				//						venueSuggestBox.setEnabled(true);
				//						customerAddressListBox.setEnabled(false);
				//					}
				//
				//					if (Integer.parseInt(customerAddressListBox.getSelectedId()) > 0) {
				//						customerAddressListBox.setEnabled(true);
				//						venueSuggestBox.setEnabled(false);
				//					}
				//				}
			}

			if (!newRecord && getSFParams().hasSpecialAccess(BmoProject.ACCESS_CHANGESTATUS)) 
				statusListBox.setEnabled(true);

			// Si es un registro anterior y ya esta asignado el tipo de proyecto, no dejar cambiarlo
			if (!newRecord && bmoProject.getBmoWFlowType().getId() > 0) {
				wFlowTypeListBox.setEnabled(false);
			}

			// Si esta asignado el lugar, eliminar registro de direccion del cliente
			//			if (venueSuggestBox.getSelectedId() > 0) {
			//				customerAddressListBox.setEnabled(false);
			//			}

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
		}

		@Override
		public void close() {
			if(deleted) new UiProject(getUiParams()).show();
//			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) {
				// Si esta asignado el tipo de proyecto, envia al dashboard, en caso contrario, envia a la lista
				if (bmoProject.getWFlowTypeId().toInteger() > 0) {
					UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), id);
					uiProjectDetail.show();
				} else {
					list();
				}
			} else {
				if (id > 0) {
					UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(), id);
					uiProjectDetail.show();
				} else list();
			}
		}

		private void sendPoll() {
			sendPollAction();
		}

		private void sendPollAction() {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-sendPollAction() ERROR: " + caught.toString());
				}

				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors())
						showSystemMessage("Error al Enviar Encuesta: " + result.errorsToString());
					else {
						showSystemMessage("Encuesta Enviada. "+
								"<a href=\"" + GWT.getHostPageBaseURL() + 
								"/frm/flex_customerpoll.jsp?h=" + new Date().getTime() + "proj&w=EXT&z=" + 
								GwtUtil.encryptId(bmoProject.getId()) + "&r=interv" + (new Date().getTime() * 456) +"\" target='_blank'><br>Ver Encuesta</a>"
								);
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoProject.getPmClass(), bmoProject, BmoProject.ACTION_SENDPOLL, "" + bmoProject.getId(), callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-sendPollAction() ERROR: " + e.toString());
			}
		}
		//copiar projecto


		public void copyProjectDialog() {
			copyProjectDialogBox = new DialogBox(true);
			copyProjectDialogBox.setGlassEnabled(true);
			copyProjectDialogBox.setText("Copiar " + getSFParams().getProgramTitle(bmoProject));
			copyProjectDialogBox.setSize("400px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("400px", "100px");
			copyProjectDialogBox.setWidget(vp);

			UiFormFlexTable formCopyProjectTable = new UiFormFlexTable(getUiParams());

			BmoProject fromBmoproject = new BmoProject();
			formCopyProjectTable.addField(1, 0, copyProjectSuggestBox, fromBmoproject.getIdField());
			BmoCustomer fromBmoCustomer = new BmoCustomer();
			formCopyProjectTable.addField(2, 0, customerSuggestBox, fromBmoCustomer.getIdField());


			HorizontalPanel changeStaffButtonPanel = new HorizontalPanel();
			changeStaffButtonPanel.add(copyProjectButton);
			changeStaffButtonPanel.add(copyProjectCloseDialogButton);

			vp.add(formCopyProjectTable);
			vp.add(changeStaffButtonPanel);

			copyProjectDialogBox.center();
			copyProjectDialogBox.show();			

		}

		public void copyProjectsAction(){
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {

					}
					else {
						showErrorMessage(this.getClass().getName() + "-copyProject() ERROR: " + caught.toString());
						copyProjectDialog();
					}

				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					if(!result.hasErrors()) {
						stopLoading();
						dialogClose();
						BmoProject bmoProject = (BmoProject) result.getBmObject();
						UiProjectDetail uiProjectDetail = new UiProjectDetail(getUiParams(),
								bmoProject.getId());
						uiProjectDetail.show();

						getUiParams().getUiTemplate().hideEastPanel();
						UiProject uiProject = new UiProject(getUiParams());
						uiProject.edit(bmoProject);
						showSystemMessage("Copia Exitosa.");
					}
					else {
						stopLoading();
						copyProjectDialog();
						showSystemMessage(result.getBmErrorList().get(0).getMsg());
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					if(copyProjectSuggestBox.getSelectedId() > 0) {
						bmoProject.setId(copyProjectSuggestBox.getSelectedId());
						getUiParams().getBmObjectServiceAsync().action(bmoProject.getPmClass(), bmoProject, BmoProject.ACCESS_COPYPROJECT, "" + customerSuggestBox.getSelectedId(), callback);
					}
					else {
						stopLoading();
						showErrorMessage(this.getClass().getName() + " ERROR: " + "Debe seleccionar Projecto");
					}

				}
			} catch (SFException e) {
				stopLoading();				
				showErrorMessage(this.getClass().getName() + "-action() ERROR: " + e.toString());
			}
		}
		//fin copiar projecto
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
	}
}
