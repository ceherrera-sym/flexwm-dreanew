/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.co;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;

import java.util.ArrayList;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoCustomerAddress;
import com.flexwm.shared.cm.BmoCustomerCompany;
import com.flexwm.shared.cm.BmoLoseMotive;
import com.flexwm.shared.cm.BmoPayCondition;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.flexwm.shared.wf.BmoWFlowTypeCompany;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;


public class UiPropertySale extends UiList {

	BmoPropertySale bmoPropertySale;

	public UiPropertySale(UiParams uiParams) {
		super(uiParams, new BmoPropertySale());
		bmoPropertySale = (BmoPropertySale)getBmObject();
	}

	public UiPropertySale(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPropertySale());
		bmoPropertySale = (BmoPropertySale)getBmObject();
	}

	@Override
	public void postShow() {
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();

		if (isMaster()) {
			if (!isMobile()) {
				// Filtrar categorias de Flujos por Modulo Venta Inmuebles
				BmFilter filterWFlowCategory = new BmFilter();
				filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
				//addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoPropertySale.getBmoWFlowType().getBmoWFlowCategory());
			}
		}

		// Filtrar tipos de Flujos por Venta Inmuebles
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		BmFilter filterWFlowType = new BmFilter();
		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoPropertySale.getBmoWFlowType());

		if (!isMobile()) {
			// Filtrar fases por Venta Inmuebles
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmFilter filterWFlowPhase = new BmFilter();
			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
					bmoWFlowPhase.getWFlowCategoryId().getName(), 
					bmoWFlowCategory.getIdFieldName(),
					bmoWFlowCategory.getProgramId().getName(),
					"" + bmObjectProgramId);
			addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoPropertySale.getBmoWFlow().getBmoWFlowPhase());

			addFilterListBox(new UiListBox(getUiParams(), new BmoDevelopmentPhase()), bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase());
		}

		// Filtrar por vendedores
		//		BmoUser bmoUser = new BmoUser();
		//		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		//		BmFilter filterSalesmen = new BmFilter();
		//		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
		//		filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
		//								bmoUser.getIdFieldName(),
		//								bmoProfileUser.getUserId().getName(),
		//								bmoProfileUser.getProfileId().getName(),
		//								"" + salesGroupId);	
		//		addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoPropertySale.getSalesUserId());
		addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoPropertySale.getSalesUserId());

		if (isMaster()) {
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoPropertySale.getStatus()), bmoPropertySale, bmoPropertySale.getStatus());
		}
		addTagFilterListBox(bmoPropertySale.getTags());
	}

	@Override
	public void create() {
		UiPropertySaleForm uiPropertySaleForm = new UiPropertySaleForm(getUiParams(), 0);
		uiPropertySaleForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPropertySale = (BmoPropertySale)bmObject;
		UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), bmoPropertySale.getId());
		uiPropertySaleDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiPropertySaleForm uiPropertySaleForm = new UiPropertySaleForm(getUiParams(), bmObject.getId());
		uiPropertySaleForm.show();
	}

	public class UiPropertySaleForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox customerSuggestBox;
		UiSuggestBox userSuggestBox;
		UiSuggestBox propertySuggestBox;
		UiTagBox tagBox = new UiTagBox(getUiParams());
		UiListBox wFlowTypeListBox;
		UiListBox orderTypeListBox;
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox statusListBox = new UiListBox(getUiParams());
		TextBox guestsTextBox = new TextBox();
		TextBox hookingTextBox = new TextBox();
		TextBox paymentDeadLineTextBox = new TextBox();
		BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox payConditionUiListBox= new UiListBox(getUiParams(), new BmoPayCondition());
		UiDateBox dateContractDateBox = new UiDateBox();
		UiDateBox dateKeepDateBox = new UiDateBox();
		
		// Reubicacion
		Button relocateButton = new Button("REUBICAR");
		UiSuggestBox newPropertySuggestBox;

		// Cambiar tipo de flujo
		Button changeWFlowTypeButton = new Button("CAMBIAR");
		UiListBox newWFlowTypeListBox;

		//Cancelacion
		TextArea loseCommentsTextArea = new TextArea();
		UiListBox loseMotiveListBox = new UiListBox(getUiParams(), new BmoLoseMotive());

		BmoPropertySale bmoPropertySale;
		int programId;
		boolean multiCompany = false;
		private String companyId;
		private int profileSalesmanRpcAttempt = 0;

		String generalSection = "Datos Generales";
		String cancelationSection = "Cancelación de la Venta";
		String changeLocationWFlowTypeSection = "Reubicación Inmueble / Cambio Medio Titulación";
		String statusSection = "Estatus";

		public UiPropertySaleForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPropertySale(), id);
			bmoPropertySale = (BmoPropertySale)getBmObject();
			initialize();
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoPropertySale.getProgramCode());
				if (newRecord) {
					bmoPropertySale.getType().setValue(BmoPropertySale.TYPE_NEW);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
			}
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			
					
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);
			

			// Cambios de tipos de flujo
			newWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por tipos de pedidos de venta de inmuebles
			BmoOrderType bmoOrderType = new BmoOrderType();
			BmFilter rentalFilter = new BmFilter();
			rentalFilter.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_PROPERTY);
			orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType(), rentalFilter);

			// Filtrar por Inmuebles disponibles
			BmoProperty bmoProperty = new BmoProperty();
			BmFilter filterByAvailableProperties = new BmFilter();
			filterByAvailableProperties.setValueFilter(bmoProperty.getKind(), bmoProperty.getAvailable(), 1);
			propertySuggestBox = new UiSuggestBox(new BmoProperty(), filterByAvailableProperties);

			customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	
		
			// Filtrar por Inmuebles disponibles, campo de reubicacion
			newPropertySuggestBox = new UiSuggestBox(new BmoProperty());
			newPropertySuggestBox.addFilter(filterByAvailableProperties);
		

			// Filtrar por vendedores
			userSuggestBox = new UiSuggestBox(new BmoUser());
//			BmoUser bmoUser = new BmoUser();
//			BmoProfileUser bmoProfileUser = new BmoProfileUser();
//			BmFilter filterSalesmen = new BmFilter();
//			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
//			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
//					bmoUser.getIdFieldName(),
//					bmoProfileUser.getUserId().getName(),
//					bmoProfileUser.getProfileId().getName(),
//					"" + salesGroupId);	
//			userSuggestBox.addFilter(filterSalesmen);
//
//			// Filtrar por vendedores activos
//			BmFilter filterSalesmenActive = new BmFilter();
//			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
//			userSuggestBox.addFilter(filterSalesmenActive);

			// Botón de reubicar
			relocateButton.setStyleName("formCloseButton");
			relocateButton.setVisible(true);
			relocateButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("¿Está seguro que desea reubicar la venta?")) 
						prepareRelocate();
				}
			});

			// Botón de cambiar tipo de flujo
			changeWFlowTypeButton.setStyleName("formCloseButton");
			changeWFlowTypeButton.setVisible(true);
			changeWFlowTypeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Window.confirm("¿Está seguro que desea cambiar el Tipo de Flujo?")) 
						changeAction();
				}
			});
		}

		@Override
		public void populateFields() {
			bmoPropertySale = (BmoPropertySale)getBmObject();

			// MultiEmpresa: g100
			multiCompany = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean();
			
			try {
				if (newRecord)
					bmoPropertySale.getType().setValue(BmoPropertySale.TYPE_NEW);
			} catch (BmException e) {
				showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
			}
			if(getSFParams().getSelectedCompanyId() > 0 && newRecord && multiCompany ) {
				try {
					bmoPropertySale.getCompanyId().setValue(getSFParams().getSelectedCompanyId());
//					addFilters(getSFParams().getSelectedCompanyId() );
				} catch (BmException e) {
					showSystemMessage("No se pudo asignar la empresa. Error: " + e.toString());
				}
			}
			// filtros extra por empresa para G100
			if(multiCompany) {
				BmoProperty bmoProperty = new BmoProperty();
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
				BmFilter filterByCompany = new BmFilter();
				filterByCompany.setValueFilter(bmoProperty.getKind(), bmoProperty.getCompanyId(), bmoPropertySale.getCompanyId().toInteger());	
				newPropertySuggestBox.addFilter(filterByCompany);
				ArrayList<BmFilter> filterListNewWflowType = new ArrayList<BmFilter>();
				filterByCompany = new BmFilter();
				filterByCompany.setInFilter(bmoWFlowTypeCompany.getKind() ,
						bmoWFlowType.getIdFieldName(), 
						bmoWFlowTypeCompany.getWflowTypeId().getName(),
						bmoWFlowTypeCompany.getCompanyId().getName(),
						""+bmoPropertySale.getCompanyId().toInteger());
				
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
				
				filterListNewWflowType.add(bmFilter);
				filterListNewWflowType.add(filterByCompany);
				
				newWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(),filterListNewWflowType);
			}
			
			// Busqueda normal y MultiEmpresa: g100
			setProfileSalesmanByCompanyIdFilters("" + bmoPropertySale.getCompanyId().toInteger(), multiCompany);
			
			formFlexTable.addSectionLabel(0, 0, generalSection, 4);
			if(multiCompany)
				formFlexTable.addField(1, 0, companyListBox, bmoPropertySale.getCompanyId());
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoPropertySale.getCode());
			formFlexTable.addField(3, 0, orderTypeListBox, bmoPropertySale.getOrderTypeId());	
			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoPropertySale.getWFlowTypeId());
			formFlexTable.addField(5, 0, customerSuggestBox, bmoPropertySale.getCustomerId());
			formFlexTable.addField(6, 0, propertySuggestBox, bmoPropertySale.getPropertyId());
			formFlexTable.addField(7, 0, userSuggestBox, bmoPropertySale.getSalesUserId());
				if (getSFParams().hasRead(new BmoPayCondition().getProgramCode()))
					formFlexTable.addField(8, 0, payConditionUiListBox, bmoPropertySale.getPayConditionId());
			formFlexTable.addField(9, 0, descriptionTextArea, bmoPropertySale.getDescription());
			if(!multiCompany)
				formFlexTable.addField(10, 0, companyListBox, bmoPropertySale.getCompanyId());
			formFlexTable.addField(11, 0, tagBox, bmoPropertySale.getTags());
			formFlexTable.addField(12, 0, hookingTextBox, bmoPropertySale.getHooking());
			formFlexTable.addField(13, 0, paymentDeadLineTextBox, bmoPropertySale.getDeadLinePayment());
			formFlexTable.addField(14, 0,dateContractDateBox, bmoPropertySale.getDateContract());
			formFlexTable.addField(15, 0,dateKeepDateBox, bmoPropertySale.getDateKeep());
			if (!newRecord) {
				formFlexTable.addLabelField(16, 0, bmoPropertySale.getStartDate());
				formFlexTable.addLabelField(17, 0, bmoPropertySale.getEndDate());
				
				if (getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_RELOCATE)
						|| getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_CHANGEWFLOWTYPE)) {

					formFlexTable.addSectionLabel(18, 0, changeLocationWFlowTypeSection, 4);
					if (getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_RELOCATE)) {
						formFlexTable.addField(19, 0, newPropertySuggestBox, bmoPropertySale.getPropertyId());
						formFlexTable.addButtonCell(20, 0, relocateButton);
					}
					if (getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_CHANGEWFLOWTYPE)) {
						formFlexTable.addField(21, 0, newWFlowTypeListBox, bmoPropertySale.getWFlowTypeId());
						formFlexTable.addButtonCell(22, 0, changeWFlowTypeButton);	
					}
				}

				formFlexTable.addSectionLabel(23, 0, cancelationSection, 4);
				formFlexTable.addLabelField(24, 0, bmoPropertySale.getCancellDate());			
				formFlexTable.addField(25, 0, loseMotiveListBox, bmoPropertySale.getLoseMotiveId());
				formFlexTable.addField(26, 0, loseCommentsTextArea, bmoPropertySale.getLoseComments());	
			}

			formFlexTable.addSectionLabel(27, 0, statusSection, 4);
			formFlexTable.addField(28, 0, typeListBox, bmoPropertySale.getType());
			formFlexTable.addField(29, 0, statusListBox, bmoPropertySale.getStatus());

			formFlexTable.hideSection(changeLocationWFlowTypeSection);
			formFlexTable.hideSection(cancelationSection);
		}

		@Override
		public void postShow() {
			statusEffect();
		
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el Status de la Venta de Inmueble?");
			} else if (event.getSource() == companyListBox) {
				BmoCompany bmoCompany = (BmoCompany)companyListBox.getSelectedBmObject();
				if(multiCompany) {
					if (bmoCompany != null) {
						addFilters(bmoCompany.getId());
					}else {
						addFilters(-1);
					}
				}
			}
		}
		private void addFilters(int companyId) {
			wflowTypeFilter(companyId);
			customerFilter(companyId);
			propertyFilter(companyId);
			// Filtrar usuarios
			if (newRecord)
				populateUsers(companyId, multiCompany);
		}
		private void propertyFilter(int companyId) {
			BmFilter filterByCompany = new BmFilter();
			BmoProperty bmoProperty = new BmoProperty();
			filterByCompany.setValueFilter(bmoProperty.getKind(), bmoProperty.getCompanyId(), companyId);
			
			BmFilter filterByAvailableProperties = new BmFilter();
			filterByAvailableProperties.setValueFilter(bmoProperty.getKind(), bmoProperty.getAvailable(), 1);
			if (newRecord) {
				propertySuggestBox.getFilterList().clear();
				propertySuggestBox.setText("");
				propertySuggestBox.setSelectedId(-1);
			}
			propertySuggestBox.addFilter(filterByCompany);
			propertySuggestBox.addFilter(filterByAvailableProperties);
		}
		// filtrar clientes por empresa G100
		private void customerFilter(int companyId) {
			BmFilter filterByCompany = new BmFilter();
			BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
			BmoCustomer bmoCustomer = new BmoCustomer();
			filterByCompany.setInFilter(bmoCustomerCompany.getKind(),
					bmoCustomer.getIdFieldName(), 
					bmoCustomerCompany.getCustomerId().getName(),
					bmoCustomerCompany.getCompanyId().getName(),
					""+companyId);
		
			if (newRecord) {
				customerSuggestBox.getFilterList().clear();
				customerSuggestBox.setText("");
				customerSuggestBox.setSelectedId(-1);
			}
			customerSuggestBox.addFilter(filterByCompany);

		}
		// filtrar tipos de flujo por empresa G100
		private void wflowTypeFilter(int companyId){
			//filtro tipod por empresa
			BmFilter filterByCompany = new BmFilter();
			BmoWFlowTypeCompany bmoWFlowTypeCompany = new BmoWFlowTypeCompany();
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			filterByCompany.setInFilter(bmoWFlowTypeCompany.getKind(),
					bmoWFlowType.getIdFieldName(), 
					bmoWFlowTypeCompany.getWflowTypeId().getName(), 
					bmoWFlowTypeCompany.getCompanyId().getName(),
					""+companyId);
			//filtro tipos por programa
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			
			wFlowTypeListBox.clear();
			wFlowTypeListBox.clearFilters();
			wFlowTypeListBox.addFilter(filterByCompany);
			wFlowTypeListBox.addFilter(bmFilter);
			wFlowTypeListBox.populate(bmoPropertySale.getWFlowTypeId());
		}
		@Override
		public BmObject populateBObject() throws BmException {
			bmoPropertySale.setId(id);
			bmoPropertySale.getCode().setValue(codeTextBox.getText());
			bmoPropertySale.getDescription().setValue(descriptionTextArea.getText());
			bmoPropertySale.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoPropertySale.getSalesUserId().setValue(userSuggestBox.getSelectedId());
			bmoPropertySale.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoPropertySale.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoPropertySale.getStatus().setValue(statusListBox.getSelectedCode());
			bmoPropertySale.getTags().setValue(tagBox.getTagList());
			bmoPropertySale.getPropertyId().setValue(propertySuggestBox.getSelectedId());
			bmoPropertySale.getLoseMotiveId().setValue(loseMotiveListBox.getSelectedId());
			bmoPropertySale.getLoseComments().setValue(loseCommentsTextArea.getText());
			bmoPropertySale.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoPropertySale.getType().setValue(typeListBox.getSelectedCode());
			bmoPropertySale.getHooking().setValue(hookingTextBox.getValue());
			bmoPropertySale.getDeadLinePayment().setValue(paymentDeadLineTextBox.getValue());
			bmoPropertySale.getPayConditionId().setValue(payConditionUiListBox.getSelectedId());
			bmoPropertySale.getDateContract().setValue(dateContractDateBox.getTextBox().getText());
			bmoPropertySale.getDateKeep().setValue(dateKeepDateBox.getTextBox().getText());
			return bmoPropertySale;
		}

		@Override
		public void formValueChange(String event) {
			statusEffect();
			
		}

		// Accion cambio SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de empresas
			if(uiSuggestBox == propertySuggestBox) {
				if(!multiCompany) {
					BmoProperty bmoProperty = (BmoProperty)propertySuggestBox.getSelectedBmObject();
					populateCompany(bmoProperty.getCompanyId().toInteger());
				}
			} else if (uiSuggestBox == customerSuggestBox) {
				BmoCustomer bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
				if (bmoCustomer != null) {
					// Si el cliente tiene un vendedor, asginarlo a la venta
					if (bmoCustomer.getSalesmanId().toInteger() > 0) {
						userSuggestBox.setSelectedId("" + bmoCustomer.getSalesmanId().toInteger());
						userSuggestBox.setSelectedBmObject(bmoCustomer.getBmoUser());
						userSuggestBox.setText(((BmoUser)userSuggestBox.getSelectedBmObject()).getFirstname().toString() + 
											" : " + ((BmoUser)userSuggestBox.getSelectedBmObject()).getFatherlastname().toString());
					}
				} else userSuggestBox.clear(); 
			}

		}
		
		private void statusEffect() {
			
			userSuggestBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);
			if (!newRecord && getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_MODIFYSALESMAN) ) {
				if ( bmoPropertySale.getStatus().equals("" + BmoPropertySale.STATUS_REVISION) 
						|| bmoPropertySale.getStatus().equals("" + + BmoPropertySale.STATUS_AUTHORIZED)) {
					userSuggestBox.setEnabled(true);
				}
			}
			statusListBox.setEnabled(false);
			propertySuggestBox.setEnabled(false);
			newPropertySuggestBox.setEnabled(false);
			relocateButton.setEnabled(false);
			newWFlowTypeListBox.setEnabled(false);
			changeWFlowTypeButton.setEnabled(false);
			if(!multiCompany || !newRecord)
				companyListBox.setEnabled(false);
			typeListBox.setEnabled(false);
			if (!newRecord)
				wFlowTypeListBox.setEnabled(false);

			if (!newRecord && getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_CHANGESTATUS)) 
				statusListBox.setEnabled(true);

			if (!newRecord && getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_RELOCATE)) {
				newPropertySuggestBox.setEnabled(true);
				relocateButton.setEnabled(true);
			}

			if (!newRecord && getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_CHANGEWFLOWTYPE)) {
				newWFlowTypeListBox.setEnabled(true);
				changeWFlowTypeButton.setEnabled(true);
			}

			// Si es un registro anterior y ya esta asignado el tipo de proyecto, no dejar cambiarlo
			if (!newRecord && bmoPropertySale.getBmoWFlowType().getId() > 0) {
				wFlowTypeListBox.setEnabled(false);
			}

			if (newRecord) {
				customerSuggestBox.setEnabled(true);
				propertySuggestBox.setEnabled(true);
			}

			if (newRecord && getSFParams().hasSpecialAccess(BmoPropertySale.ACCESS_NOSALEDIRECT)) {
				saveButton.setVisible(false);
			}

			if (!newRecord && bmoPropertySale.getStatus().equals(BmoPropertySale.STATUS_CANCELLED)) {
				loseMotiveListBox.setEnabled(false);
				loseCommentsTextArea.setEnabled(false);
			}
			
			if(getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
		}

		@Override
		public void close() {
			if(deleted) new UiPropertySale(getUiParams()).show();
//			if (isSlave()) {
//				//list();
//			} else {
//				if (id > 0) {
//					UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), id);
//					uiPropertySaleDetail.show();
//				} else list();
//			}
		}

		@Override
		public void saveNext() {
			// Si esta asignado el tipo de proyecto, envia al detalle, en caso contrario, envia a la lista
			if (bmoPropertySale.getWFlowTypeId().toInteger() > 0) {
				UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), id);
				uiPropertySaleDetail.show();
			} else {
				UiPropertySale uiPropertySaleList = new UiPropertySale(getUiParams());
				uiPropertySaleList.show();
			}
		}

		private void populateCompany(int companyId) {
			// Filtros de empresas de la venta
			companyListBox.clear();
			companyListBox.clearFilters();
			setCompanyListBoxFilters(companyId);
			companyListBox.populate("" + companyId);
			companyListBox.setEnabled(false);
		}

		private void setCompanyListBoxFilters(int companyId) {

			BmoCompany bmoCompany = new BmoCompany();
			BmFilter filterCompany = new BmFilter();

			if (companyId > 0) {
				filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyListBox.addFilter(filterCompany);
			} else {
				filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
				companyListBox.addFilter(filterCompany);	
			}
		}
		
		// Filtrar vendedores por perfil/empresa
		private void populateUsers(int companyId, boolean multiCompany) {
			userSuggestBox.clear();
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
			if (multiCompany)
				searchProfileSalesmanByCompanyId(companyId, multiCompany , 0);
			else {
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

		private void prepareRelocate(){
			relocateAction("" + newPropertySuggestBox.getSelectedId());
		}

		private void relocateAction(String newPropertyId) {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-relocateAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) showSystemMessage("Error al reubicar Venta: " + result.errorsToString());
					else {
						dialogClose();
						showSystemMessage("El inmueble fue reubicado");
						UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), id);
						uiPropertySaleDetail.show();
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoPropertySale.getPmClass(), bmoPropertySale, BmoPropertySale.ACTION_RELOCATE, newPropertyId, callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-relocateAction() ERROR: " + e.toString());
			}
		}

		private void changeAction(){
			changeAction("" + newWFlowTypeListBox.getSelectedId());
		}

		private void changeAction(String newWFlowTypeId) {
			// Establece eventos ante respuesta de servicio
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					if (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0) {}
					else showErrorMessage(this.getClass().getName() + "-batchUpdateAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) showSystemMessage("Error al reubicar Venta: " + result.errorsToString());
					else {
						dialogClose();
						showSystemMessage("El " +getSFParams().getFieldFormTitle(bmoPropertySale.getWFlowTypeId()) + " fue cambiado.");
						UiPropertySaleDetail uiPropertySaleDetail = new UiPropertySaleDetail(getUiParams(), id);
						uiPropertySaleDetail.show();
					}
				}
			};

			// Llamada al servicio RPC
			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoPropertySale.getPmClass(), bmoPropertySale, BmoPropertySale.ACTION_CHANGEWFLOWTYPE, newWFlowTypeId, callback);
				}
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-changeAction() ERROR: " + e.toString());
			}
		}
		
		// Variables para llamadas RPC
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
	}
}