/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cr;

import java.util.Date;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.flexwm.client.cm.UiCustomer;
import com.flexwm.client.cm.UiCustomer.UiCustomerForm;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.cr.BmoCreditGuarantee;
import com.flexwm.shared.cr.BmoCreditType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoRegion;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiFormFlexTable;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.UiTagBox;


public class UiCredit extends UiList {
	BmoCredit bmoCredit;

	public UiCredit(UiParams uiParams) {
		super(uiParams, new BmoCredit());
		bmoCredit = (BmoCredit)getBmObject();
	}

	@Override
	public void postShow() {

		// Filtrar categorias de Flujos por Modulo Credito
		//		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		//		BmFilter filterWFlowCategory = new BmFilter();
		//		filterWFlowCategory.setValueFilter(bmoWFlowCategory.getKind(), bmoWFlowCategory.getProgramId(), bmObjectProgramId);		
		//		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowCategory(), filterWFlowCategory), bmoCredit.getBmoWFlowType().getBmoWFlowCategory());

		// Filtrar tipos de Flujos del Credito
		//		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		//		BmFilter filterWFlowType = new BmFilter();
		//		filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
		//		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType), bmoCredit.getBmoWFlowType());

		// Filtrar fases del Credito
		//		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		//		BmFilter filterWFlowPhase = new BmFilter();
		//		filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), 
		//				bmoWFlowPhase.getWFlowCategoryId().getName(), 
		//				bmoWFlowCategory.getIdFieldName(),
		//				bmoWFlowCategory.getProgramId().getName(),
		//				"" + bmObjectProgramId);
		//		addFilterListBox(new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase), bmoCredit.getBmoWFlow().getBmoWFlowPhase());

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
		addFilterSuggestBox(new UiSuggestBox(new BmoUser(), filterSalesmen), new BmoUser(), bmoCredit.getSalesUserId());

		addFilterSuggestBox(new UiSuggestBox(new BmoCustomer()), new BmoCustomer(), bmoCredit.getCustomerId());

		if (isMaster()) {
			if (!isMobile()) {
				addFilterSuggestBox(new UiSuggestBox(new BmoRegion()), new BmoRegion(), bmoCredit.getBmoCustomer().getRegionId());
				addDateRangeFilterListBox(bmoCredit.getStartDate());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoCredit.getStatus()), bmoCredit, bmoCredit.getStatus());
				addStaticFilterListBox(new UiListBox(getUiParams(), bmoCredit.getPaymentStatus()), bmoCredit, bmoCredit.getPaymentStatus());
			}
		}
	}

	@Override
	public void create() {
		UiCreditForm uiCreditForm = new UiCreditForm(getUiParams(), 0);
		uiCreditForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoCredit = (BmoCredit)bmObject;		
		UiCreditDetail uiCreditDetail = new UiCreditDetail(getUiParams(), bmoCredit.getId());
		uiCreditDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiCreditForm uiCreditForm = new UiCreditForm(getUiParams(), bmObject.getId());
		uiCreditForm.show();
	}

	public class UiCreditForm extends UiFormDialog {
		BmoCurrency bmoCurrency = new BmoCurrency();

		TextBox codeTextBox = new TextBox();
		TextArea commentsTextArea = new TextArea();
		UiSuggestBox customerSuggestBox;
		UiSuggestBox userSuggestBox;
		UiTagBox tagBox = new UiTagBox(getUiParams());
		UiListBox wFlowTypeListBox;
		UiListBox orderTypeListBox;	
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiListBox creditTypeListBox = new UiListBox(getUiParams(), new BmoCreditType());
		TextBox amountTextBox = new TextBox();
		UiDateBox startDateBox = new UiDateBox();	
		TextBox guestsTextBox = new TextBox();	
		TextBox bondTextBox = new TextBox();
		UiSuggestBox collectorUserSuggestBox = new UiSuggestBox(new BmoUser());
		UiSuggestBox guaranteeOneSuggestBox = new UiSuggestBox(new BmoCustomer());
		UiSuggestBox guaranteeTwoSuggestBox = new UiSuggestBox(new BmoCustomer());

		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
		TextBox currencyParityTextBox = new TextBox();

		//Renovacion
		private Button renewCreditButton = new Button("ACEPTAR");
		private Button renewCreditDialogButton = new Button("RENOVAR");
		private Button renewCreditCloseDialogButton = new Button("CERRAR");
		protected DialogBox renewCreditDialogBox;
		UiSuggestBox renewCreditListBox = new UiSuggestBox(new BmoCredit());

		BmoCredit bmoCredit;	
		int programId;

		CreditUpdater creditUpdater = new CreditUpdater();

		String generalSection = "Datos Generales";

		public UiCreditForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCredit(), id);
			bmoCredit = (BmoCredit)getBmObject();
			initialize();
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoCredit.getProgramCode());
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
			sessionFilter.setValueFilter(bmoOrderType.getKind(), bmoOrderType.getType(), "" + BmoOrderType.TYPE_CREDIT);
			orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType(), sessionFilter);

			// Filtrar por clientes existentes
			customerSuggestBox = new UiSuggestBox(new BmoCustomer());

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

			//Renovar el crédito
			renewCreditButton.setStyleName("formSaveButton");
			renewCreditButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {				
					renewCreditAction();
				}
			});	


			renewCreditCloseDialogButton.setStyleName("formCloseButton");
			renewCreditCloseDialogButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {				
					renewCreditDialogBox.hide();
				}
			});	

			renewCreditDialogButton.setStyleName("formSaveButton");		
			renewCreditDialogButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					renewCreditDialog();
				}
			});

			// Manejo de cambios de textbox
			ValueChangeHandler<String> textChangeHandler = new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					formTextChange(event);
				}
			};
			formFlexTable.setTextChangeHandler(textChangeHandler);

		}

		@Override
		public void populateFields() {
			bmoCredit = (BmoCredit)getBmObject();

			try {
				// Asigna fecha de inicio default
				// bmoCredit.getStartDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
				// Si no esta asignado el tipo, buscar por el default	
				if (!(bmoCredit.getOrderTypeId().toInteger() > 0)) {		
					bmoCredit.getOrderTypeId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getDefaultOrderTypeId().toString());
				}	
			} catch (BmException e) {
				showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar tipo default : " + e.toString());
			}

			// Asgina por defecto el usuario que logea en el campo de vendedor
			try {
				if (newRecord) {
					bmoCredit.getSalesUserId().setValue(getSFParams().getLoginInfo().getUserId());
					//Asigar la fecha al lunes de la semana en curso
					getLastMondayOfWeek();
				}
			} catch (BmException e) {
				showSystemMessage("No se pudo asignar correctamente el vendedor, inténtelo manual: " + e.toString());
			}

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoCredit.getCurrencyId().toInteger() > 0)) {				
				try {
					int defaultCurrencyId = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toInteger();
					bmoCredit.getCurrencyId().setValue(defaultCurrencyId);					
					currencyListBox.setSelectedId("" + defaultCurrencyId);

				} catch (BmException e) {
					showSystemMessage("No se puede asignar moneda : " + e.toString());
				}
			}

			if (newRecord) {
				try {
					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						bmoCredit.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());

				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}			
			}

			UiCustomerForm uiCustomerForm = new UiCustomer(getUiParams()).getUiCustomerForm();

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoCredit.getCode());
			formFlexTable.addField(3, 0, orderTypeListBox, bmoCredit.getOrderTypeId());
			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoCredit.getWFlowTypeId());
			formFlexTable.addField(5, 0, creditTypeListBox, bmoCredit.getCreditTypeId());
			formFlexTable.addField(6, 0, customerSuggestBox, bmoCredit.getCustomerId(), uiCustomerForm, new BmoCustomer());
			formFlexTable.addField(7, 0, userSuggestBox, bmoCredit.getSalesUserId());
			formFlexTable.addField(8, 0, startDateBox, bmoCredit.getStartDate());
			formFlexTable.addField(9, 0, commentsTextArea, bmoCredit.getComments());
			formFlexTable.addField(10, 0, bondTextBox, bmoCredit.getBond());
			formFlexTable.addField(11, 0, amountTextBox, bmoCredit.getAmount());
			formFlexTable.addLabelField(12, 0, bmoCredit.getBmoCreditType().getGuarantees());
			formFlexTable.addField(13, 0, guaranteeOneSuggestBox, bmoCredit.getGuaranteeOneId(), uiCustomerForm, new BmoCustomer());
			formFlexTable.addField(14, 0, guaranteeTwoSuggestBox, bmoCredit.getGuaranteeTwoId(), uiCustomerForm, new BmoCustomer());
			formFlexTable.addField(15, 0, collectorUserSuggestBox, bmoCredit.getCollectorUserId());
			formFlexTable.addField(16, 0, currencyListBox, bmoCredit.getCurrencyId());	
			formFlexTable.addField(17, 0, currencyParityTextBox, bmoCredit.getCurrencyParity());
			populateParityFromCurrency(currencyListBox.getSelectedId());
			formFlexTable.addField(18, 0, companyListBox, bmoCredit.getCompanyId());
			formFlexTable.addField(19, 0, tagBox, bmoCredit.getTags());
			formFlexTable.addField(20, 0, statusListBox, bmoCredit.getStatus());

			if (!newRecord) {

				if (bmoCredit.getStatus().equals(BmoCredit.STATUS_FINISHED))
					formFlexTable.addButtonCell(21, 0, renewCreditDialogButton);

				formFlexTable.hideField(guaranteeOneSuggestBox);
				formFlexTable.hideField(guaranteeTwoSuggestBox);


				if (!newRecord) {
					// Items
					BmoCreditGuarantee bmoCreditGuarantee = new BmoCreditGuarantee();
					FlowPanel creditGuaranteeFP = new FlowPanel();
					BmFilter filterSessionTypePackage = new BmFilter();
					filterSessionTypePackage.setValueFilter(bmoCreditGuarantee.getKind(), bmoCreditGuarantee.getCreditId(), bmoCredit.getId());
					getUiParams().setForceFilter(bmoCreditGuarantee.getProgramCode(), filterSessionTypePackage);
					UiCreditGuarantee uiCreditGuarantee = new UiCreditGuarantee(getUiParams(), creditGuaranteeFP, bmoCredit, bmoCredit.getId(), creditUpdater);
					setUiType(bmoCreditGuarantee.getProgramCode(), UiParams.MINIMALIST);
					uiCreditGuarantee.show();
					formFlexTable.addPanel(23, 0, creditGuaranteeFP, 2);
				}
			}

			if (newRecord)
				clearTypeCredit();

			statusEffect();
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == statusListBox) {
				update("Desea cambiar el estatus del crédito?");		
			} else if (event.getSource() == creditTypeListBox) {
				BmoCreditType bmoCreditType = (BmoCreditType)creditTypeListBox.getSelectedBmObject();
				if (!(bmoCredit.getCurrencyParity().toDouble() > 0))
					getParityFromCurrency(currencyListBox.getSelectedId());
				if (bmoCreditType != null) {

					formFlexTable.showField(bmoCredit.getBmoCreditType().getGuarantees());
					populateAmount(bmoCreditType);
				} else if (event.getSource() == currencyListBox) {
					getParityFromCurrency(currencyListBox.getSelectedId());
				} else {
					// Limpiar
					clearTypeCredit();
				}
			}
		}

		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de requisiones
			if (uiSuggestBox == customerSuggestBox) {
				// Asignar promotor del cliente
				BmoCustomer bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
				try {
					if(newRecord)
					bmoCredit.getSalesUserId().setValue(bmoCustomer.getSalesmanId().toInteger());
					else
						if(bmoCustomer.getId()>0) {
							changeCustomers();
						}
				
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-formSuggestionSelectionChange(): ERROR " + e.toString());
				}
				populateUser(bmoCustomer.getSalesmanId().toInteger(), bmoCustomer.getBmoUser().getCode().toString());
			}
			if (uiSuggestBox == userSuggestBox) {
				if (!newRecord) {
					if (getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGEINFO)) {
						changeUserSale();
					}
				}
			}
		}

//		 Actualiza combo de almacenes
		private void populateUser(int userId, String userCode) {
			userSuggestBox.clear();
			userSuggestBox.setSelectedId(userId);
			userSuggestBox.setText("" + userCode);
		}

//		private void populateCustomer (int customerId, String customerCode){
//			customerSuggestBox.clear();
//			customerSuggestBox.setSelectedId(customerId);
//			customerSuggestBox.setText("" + customerCode);
//		}
//		
		private void clearTypeCredit() {
			// Limpiar/ocultar monto, avales
			amountTextBox.setText("");
			guaranteeOneSuggestBox.clear();
			guaranteeTwoSuggestBox.clear();
			formFlexTable.hideField(bmoCredit.getBmoCreditType().getGuarantees());
			formFlexTable.hideField(bmoCredit.getGuaranteeOneId());
			formFlexTable.hideField(bmoCredit.getGuaranteeTwoId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCredit.setId(id);
			bmoCredit.getCode().setValue(codeTextBox.getText());
			bmoCredit.getComments().setValue(commentsTextArea.getText());
			bmoCredit.getCustomerId().setValue(customerSuggestBox.getSelectedId());
			bmoCredit.getSalesUserId().setValue(userSuggestBox.getSelectedId());
			bmoCredit.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
			bmoCredit.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());		
			bmoCredit.getStatus().setValue(statusListBox.getSelectedCode());
			bmoCredit.getStartDate().setValue(startDateBox.getTextBox().getText());		
			bmoCredit.getCreditTypeId().setValue(creditTypeListBox.getSelectedCode());
			bmoCredit.getAmount().setValue(amountTextBox.getText());
			bmoCredit.getBond().setValue(bondTextBox.getText());
			bmoCredit.getTags().setValue(tagBox.getTagList());
			bmoCredit.getCollectorUserId().setValue(collectorUserSuggestBox.getSelectedId());
			bmoCredit.getGuaranteeOneId().setValue(guaranteeOneSuggestBox.getSelectedId());
			bmoCredit.getGuaranteeTwoId().setValue(guaranteeTwoSuggestBox.getSelectedId());
			bmoCredit.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoCredit.getCurrencyId().setValue(currencyListBox.getSelectedId());
			bmoCredit.getCurrencyParity().setValue(currencyParityTextBox.getText());

			return bmoCredit;
		}

		public void populateAmount(BmoCreditType bmoCreditType) {

			// Llenar monto desde el tipo de credito
			amountTextBox.setText("" + bmoCreditType.getCreditLimit().toDouble());

			// mostrar numero de avales
			formFlexTable.addLabelField(12, 0, bmoCreditType.getGuarantees());

			// ocultar/mostrar avales
			if (newRecord) {
				if (bmoCreditType.getGuarantees().toInteger() > 0) {
					formFlexTable.showField(bmoCredit.getGuaranteeOneId());
					formFlexTable.hideField(bmoCredit.getGuaranteeTwoId());

					if (bmoCreditType.getGuarantees().toInteger() > 1) {
						formFlexTable.showField(bmoCredit.getGuaranteeTwoId());
					}
				} else {
					formFlexTable.hideField(bmoCredit.getGuaranteeOneId());
					formFlexTable.hideField(bmoCredit.getGuaranteeTwoId());
				}
			}
		}

		@Override
		public void formDateChange(ValueChangeEvent<Date> event) {

			if (newRecord) {
				if (event.getSource() == startDateBox) {
					getParityFromCurrency(currencyListBox.getSelectedId());
				}
			} else {
				if (bmoCredit.getStatus().equals(BmoCredit.STATUS_AUTHORIZED)) {
					//Modificar la fecha del pedido al cambiar la fecha del crédito
					if (event.getSource() == startDateBox) {
						changeDateCredit();
					}
				} 
			}
		}

		private void statusEffect() {	
			wFlowTypeListBox.setEnabled(false);
			statusListBox.setEnabled(false);
			userSuggestBox.setEnabled(false);
			creditTypeListBox.setEnabled(false);
			startDateBox.setEnabled(false);
			amountTextBox.setEnabled(false);
			customerSuggestBox.setEnabled(false);

			if (getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGECUSTOMER)) {
				customerSuggestBox.setEnabled(true);
			}
			creditTypeListBox.setEnabled(false);
			bondTextBox.setEnabled(false);
			currencyListBox.setEnabled(false);
			currencyParityTextBox.setEnabled(false);
			orderTypeListBox.setEnabled(false);
			companyListBox.setEnabled(false);
			if (newRecord) {
				orderTypeListBox.setEnabled(true);
				wFlowTypeListBox.setEnabled(true);
				customerSuggestBox.setEnabled(true);
				userSuggestBox.setEnabled(true);
				creditTypeListBox.setEnabled(true);			
				amountTextBox.setEnabled(true);				
				bondTextBox.setEnabled(true);
				startDateBox.setEnabled(true);
				currencyListBox.setEnabled(true);
				companyListBox.setEnabled(true);
			} else {
				if (bmoCredit.getStatus().equals(BmoCredit.STATUS_REVISION)) {
					creditTypeListBox.setEnabled(true);
					amountTextBox.setEnabled(true);
					bondTextBox.setEnabled(true);
					currencyListBox.setEnabled(true);
				}

				if (getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGEINFO)) {
					startDateBox.setEnabled(true);				
				} 

				if (getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGEUSER)) {
					userSuggestBox.setEnabled(true);
				}
				
				if (getSFParams().hasSpecialAccess(BmoCredit.ACCESS_CHANGESTATUS)) 
					statusListBox.setEnabled(true);
			}

			if (currencyListBox.getSelectedId() != ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getSystemCurrencyId().toString())
				currencyParityTextBox.setEnabled(true);
			else	
				currencyParityTextBox.setEnabled(false);
		}

		@Override
		public void close() {
			UiCredit uiCreditList = new UiCredit(getUiParams());
			uiCreditList.show();
		}

		@Override
		public void saveNext() {
			if (isSingleSlave()) {
				UiCreditForm uiCreditForm = new UiCreditForm(getUiParams(), getBmObject().getId());
				uiCreditForm.show();
			} else {
				UiCreditDetail uiCreditDetail = new UiCreditDetail(getUiParams(), bmoCredit.getId());
				uiCreditDetail.show();
			}
		}

		public void renewCreditDialog() {
			renewCreditDialogBox= new DialogBox(true);
			renewCreditDialogBox.setGlassEnabled(true);
			renewCreditDialogBox.setText("Renovación del Crédito");
			renewCreditDialogBox.setSize("200px", "100px");

			VerticalPanel vp = new VerticalPanel();
			vp.setSize("200px", "100px");
			renewCreditDialogBox.setWidget(vp);

			UiFormFlexTable formRenewCreditTable = new UiFormFlexTable(getUiParams());
			formRenewCreditTable.addLabelCell(1, 0, "Crédito:");
			formRenewCreditTable.addLabelCell(2, 0, bmoCredit.getCode().toString());
			formRenewCreditTable.addLabelCell(3, 0, "Cliente:");
			formRenewCreditTable.addLabelCell(4, 0, bmoCredit.getBmoCustomer().getDisplayName().toString());

			HorizontalPanel renewCreditButtonPanel = new HorizontalPanel();
			renewCreditButtonPanel.add(renewCreditButton);
			renewCreditButtonPanel.add(renewCreditCloseDialogButton);

			vp.add(formRenewCreditTable);
			vp.add(renewCreditButtonPanel);

			renewCreditDialogBox.center();
			renewCreditDialogBox.show();
		}

		//Obtener la cantidad en almacen
		public void getLastMondayOfWeek() {
			String startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getLastMondayOfWeek() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) {
						showSystemMessage("Existen Errores al buscar el lunes de la semana " + result.errorsToString());
					} else {
						try {
							bmoCredit.getStartDate().setValue(result.getMsg());
						} catch (BmException e) {						
							showSystemMessage("Existen Errores al buscar el lunes de la semana " + result.errorsToString());
						}			
						formFlexTable.addField(8, 0, startDateBox, bmoCredit.getStartDate());
					}
				}
			};

			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCredit.getPmClass(), bmoCredit, BmoCredit.ACTION_LASTMONDAYOFWEEK, startDate, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getLastMondayOfWeek() ERROR: " + e.toString());
			}
		}

		//Renovar el credito
		public void renewCreditAction(){
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-renewCreditAction() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors())
						showSystemMessage("Existen Errores " + result.errorsToString());
					else {
						//getRenewCredit();
						showSystemMessage("Renovación con exito, el nuevo crédito es CR-" + result.getId());
					}	
					renewCreditDialogBox.hide();
					dialogClose();
				}
			};

			try {
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCredit.getPmClass(), bmoCredit, BmoCredit.ACTION_RENEWCREDIT, "" + bmoCredit.getId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-renewCreditAction() ERROR: " + e.toString());
			}
		}

		public void getRenewCredit(){
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getRenewCredit() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();	
					String credCode = "";
					credCode = result.getMsg();
					showSystemMessage("Renovación con exito, el nuevo crédito es " + credCode);

					renewCreditDialogBox.hide();
				}
			};

			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCredit.getPmClass(), bmoCredit, BmoCredit.ACTION_GETRENEWCREDIT, "" + bmoCredit.getId(), callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-getRenewCredit() ERROR: " + e.toString());
			}
		}

		//Cambiar la fecha del crédito
		public void changeDateCredit() {
			String values = bmoCredit.getId() + "|" + startDateBox.getTextBox().getValue();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-changeDateCredit() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) {
						showSystemMessage("Existen Errores al cambiar la fecha del crédito " + result.errorsToString());
					} else {											
						showSystemMessage("Las fechas del crédito y el pedido se cambiaron " + result.errorsToString());
					}
				}
			};
			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCredit.getPmClass(), bmoCredit, BmoCredit.ACTION_CHANGECREDITDATE, values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-changeDateCredit() ERROR: " + e.toString());
			}
		}

		public void changeUserSale() {
			String values = bmoCredit.getId() + "|" + userSuggestBox.getSelectedId();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-changeDateCredit() ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) {
						showSystemMessage("Existen Errores al cambiar el usuario del crédito " + result.errorsToString());
					} else {											
						showSystemMessage("El usuario del crédito y el pedido se cambiaron " + result.errorsToString());
						initialize();
					}
				}
			};
			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCredit.getPmClass(), bmoCredit, BmoCredit.ACTION_CHANGEUSERSALE, values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-changeDateCredit() ERROR: " + e.toString());
			}
		}
		
		
		//lel customer change
		public void changeCustomers() {
			String values = bmoCredit.getId() + "|" + customerSuggestBox.getSelectedId();
			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
				@Override
				public void onFailure(Throwable caught) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-changeCustomer ERROR: " + caught.toString());
				}

				@Override
				public void onSuccess(BmUpdateResult result) {
					stopLoading();
					if (result.hasErrors()) {
						showSystemMessage("Existen Errores al cambiar el Cliente " + result.errorsToString());
					} else {											
						showSystemMessage("El cliente se cambio correctamten. " + result.errorsToString());
						initialize();
					}
				}
			};
			try {	
				if (!isLoading()) {
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCredit.getPmClass(), bmoCredit, BmoCredit.ACTION_CHANGECUSTOMERS, values, callback);
				}
			} catch (SFException e) {
				stopLoading();
				showErrorMessage(this.getClass().getName() + "-changeCustomers() ERROR: " + e.toString());
			}
		}
		
		
		//Obtener la paridad de la moneda
		public void populateParityFromCurrency(String currencyId) {			
			getParityFromCurrency(currencyId);
		}

		public void getParityFromCurrency(String currencyId) {

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
						showErrorMessage("Error al obtener el Tipo de Cambio");
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

		public class CreditUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}	

}