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
//
//import com.flexwm.shared.cm.BmoCustomer;
//import com.flexwm.shared.cm.BmoCustomerContact;
//import com.flexwm.shared.cm.BmoRFQU;
//import com.flexwm.shared.op.BmoOrderType;
//import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.ScrollPanel;
//import com.google.gwt.user.client.ui.TextArea;
//import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.VerticalPanel;
//import com.symgae.client.ui.UiDateBox;
//import com.symgae.client.ui.UiForm;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.client.ui.UiSuggestBox;
//import com.symgae.client.ui.UiTemplate;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.GwtUtil;
//import com.symgae.shared.SFException;
//import com.symgae.shared.sf.BmoCompany;
//import com.symgae.shared.sf.BmoUser;
//import com.flexwm.shared.wf.BmoWFlowCategory;
//import com.flexwm.shared.wf.BmoWFlowType;
//
//
//public class UiRFQUForm extends UiForm {
//	BmoWFlowType bmoWFlowType;
//	TextBox codeRFQUTextBox = new TextBox();
//	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());		
//	UiSuggestBox userSaleSuggestBox ;		
//	UiListBox contactListBox = new UiListBox(getUiParams(), new BmoCustomerContact());
//	TextBox affairTextBox = new TextBox();
//	UiDateBox dateTimeBox = new UiDateBox();
//	UiListBox orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
//	UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
//	UiListBox effectWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
//	TextArea objectiveTextArea = new TextArea();
//	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
//	UiListBox statusListBox = new UiListBox(getUiParams());
//	private DialogBox lifeCycleDialogBox = new DialogBox();
//	private Button lifeCycleCloseButton = new Button("CERRAR");
//	int bandera;
//	BmoRFQU bmoRFQU;
//	private int contactMainRpcAttempt  = 0;
//	private String customerId;
//	private int parityFromCurrencyRpcAttempt = 0;
//	private String currencyId = "";
//	
//	public UiRFQUForm(UiParams uiParams, int id) {
//		super(uiParams, new BmoRFQU(), id);
//		initialize();
//	}
//
//
//	private void initialize() {		
//		if(newRecord) {
//			statusListBox.setEnabled(false);	
//		}
//
//		bmoWFlowType = new BmoWFlowType();
//		wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
//
//		userSaleSuggestBox= new UiSuggestBox(new BmoUser());
////		BmoUser bmoUser = new BmoUser();
////		BmoProfileUser bmoProfileUser = new BmoProfileUser();
////		BmFilter filterSalesmen = new BmFilter();
////		int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
////		filterSalesmen.setInFilter(bmoUser.getKind(), 
////				bmoUser.getIdFieldName(),
////				bmoProfileUser.getUserId().getName(),
////				bmoProfileUser.getProfileId().getName(),
////				"" + salesGroupId);	
////		userSaleSuggestBox.addFilter(filterSalesmen);
////
//////		// Filtrar por vendedores activos
////		BmFilter filterSalesmenActive = new BmFilter();
////		filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
////		userSaleSuggestBox.addFilter(filterSalesmenActive);
//	
//
//		lifeCycleCloseButton.setStyleName("formCloseButton");
//		lifeCycleCloseButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				lifeCycleDialogBox.hide();
//				showLifeCycleDialog();
//			}
//		});
//
//	}
//	@Override
//	public void populateFields() {
//		bmoRFQU = (BmoRFQU)getBmObject();
//		if(bmoRFQU.getCustomerId().toInteger()>0) {
//			populateCustcomer(customerId);
//		}
//
//		if (newRecord) {
//			try {
//	
//				bmoRFQU.getUserSale().setValue(getSFParams().getLoginInfo().getUserId());
//			} catch (BmException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		// Filtro de tipos de flujo en categorias del modulo Oportunidad
//		bmoWFlowType = new BmoWFlowType();
//		BmFilter bmFilterWFlowTypeByProgram = new BmFilter();
//		bmFilterWFlowTypeByProgram.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
//		wFlowTypeListBox.addFilter(bmFilterWFlowTypeByProgram);
//
//		
//		// Filtrar contactos del cliente
//		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
//		BmFilter contactsByCustomer = new BmFilter();
//		contactsByCustomer.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), bmoRFQU.getCustomerId().toInteger());
//		contactListBox.addFilter(contactsByCustomer);
//		
//		
//		
//		try {
//			bmoRFQU.getDateRFQU().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
//		} catch (BmException e) {
//			showSystemMessage("Error al asignar fecha");
//		}
//
//		formFlexTable.addField(1, 0, codeRFQUTextBox, bmoRFQU.getCodeRFQU());
//		formFlexTable.addField(2, 0, customerSuggestBox, bmoRFQU.getCustomerId());
//		formFlexTable.addField(3, 0, userSaleSuggestBox, bmoRFQU.getUserSale());
//		formFlexTable.addField(4, 0, contactListBox, bmoRFQU.getCustomerContactId());
//		formFlexTable.addField(5, 0, affairTextBox, bmoRFQU.getAffair());
//		formFlexTable.addField(6, 0, dateTimeBox, bmoRFQU.getDateRFQU());
//		formFlexTable.addField(7, 0, orderTypeListBox, bmoRFQU.getOrderTypeId());
//		formFlexTable.addField(8, 0, wFlowTypeListBox, bmoRFQU.getwFlowTypeId());
//		formFlexTable.addField(9, 0, effectWFlowTypeListBox, bmoRFQU.getForeignWFlowTypeId());
//		formFlexTable.addField(10, 0, objectiveTextArea, bmoRFQU.getObjectiveRFQU());
//		formFlexTable.addField(11, 0, companyListBox, bmoRFQU.getCompanyId());
//		formFlexTable.addField(12, 0, statusListBox, bmoRFQU.getStatusRFQU());
//		statusEffect();
//		
//		}
//
//	
//	
//	private void statusEffect() {
//	
//		codeRFQUTextBox.setEnabled(false);
////		codeRFQUTextBox.setEnabled(false);
//		dateTimeBox.setEnabled(false);
////		String startDate;
////		if(!(dateTimeBox.getTextBox().getText().length()>0)) {
////			startDate = GwtUtil.dateToString(new Date(), getSFParams().getDateFormat());
////			dateTimeBox.setDate(startDate)startDate.toString();				
////		}
//		// Busca Empresa seleccionada por default
//		if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
//			try {
//				bmoRFQU.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
//			} catch (BmException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}					
//		if(!(customerSuggestBox.getSelectedId()>0)) {
////			customerSuggestBox.setValue(newValue);
//		}
//		if(bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_CANCELED)||bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_COMPLET)) {
//			customerSuggestBox.setEnabled(false);
//			userSaleSuggestBox.setEnabled(false);
//			contactListBox.setEnabled(false);
//			affairTextBox.setEnabled(false);
//			orderTypeListBox.setEnabled(false);
//			wFlowTypeListBox.setEnabled(false);
//			objectiveTextArea.setEnabled(false);
//			companyListBox.setEnabled(false);
//			effectWFlowTypeListBox.setEnabled(false);			
//		}
//		if(bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_NOTSTARTING)||(bmoRFQU.getStatusRFQU().equals(BmoRFQU.STATUS_PROCESSING))) {
//			customerSuggestBox.setEnabled(true);
//			userSaleSuggestBox.setEnabled(true);
//			contactListBox.setEnabled(true);
//			affairTextBox.setEnabled(true);
//			orderTypeListBox.setEnabled(true);
//			wFlowTypeListBox.setEnabled(true);
//			objectiveTextArea.setEnabled(true);
//			companyListBox.setEnabled(true);
//			effectWFlowTypeListBox.setEnabled(true);
//		}
//	}
//
//	@Override
//	public void saveNext() {
//		// 
//	
//		if (newRecord) {
//			
//			if (bmoRFQU.getwFlowTypeId().toInteger() > 0) {
//				UiRFQDetail uiRFQDetail = new UiRFQDetail(getUiParams(), id);
//				uiRFQDetail.show();
//			} 	
//		} else {
////			showErrorMessage(""+id);
//			UiRFQDetail uiRFQDetail = new UiRFQDetail(getUiParams(), id);
//		uiRFQDetail.show();}
//	}
//	
//	@Override
//	public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
//		// Filtros de requisiones
//		if (uiSuggestBox == customerSuggestBox) {
//			// Asignar promotor del cliente
//			try {
//				if(newRecord) {
////					populateCustomerContact(customerSuggestBox.getSelectedId());
//				bmoRFQU.getUserSale().setValue(bmoRFQU.getUserSale().toInteger());
//				
//				}
//					
//			} catch (BmException e) {
//				showErrorMessage(this.getClass().getName() + "-formSuggestionSelectionChange(): ERROR " + e.toString());
//			}
////			populateUser(bmoCustomer.getSalesmanId().toInteger(), bmoCustomer.getBmoUser().getCode().toString());
//			if(customerSuggestBox.getText().length()>0) {
//				populateCustomerContact(customerSuggestBox.getSelectedId());
//				BmoCustomer bmoCustomer = new BmoCustomer();
//				bmoCustomer = (BmoCustomer)customerSuggestBox.getSelectedBmObject();
//				userSaleSuggestBox.setValue(bmoCustomer.getBmoUser().listBoxFieldsToString());
//				userSaleSuggestBox.setSelectedId(bmoCustomer.getSalesmanId().toInteger());								
//			}else {
//				contactListBox.setSelectedId(""+0);
//				userSaleSuggestBox.setText("");
//			}
//
//		}
//}
//	//Cargar contactos del cliente
//	private void populateCustomerContact(int customerId) {
//		contactListBox.clear();
//		contactListBox.clearFilters();
//		if (customerId > 0)
//
//			setCustomerContactListBoxFilters(customerId);
//		contactListBox.populate("" + customerId);
//		callMainContasct(""+customerId);
//
//	}
//	
//	@Override
//	public void  updateNext() {
//		UiRFQDetail uiRFQDetail = new UiRFQDetail(getUiParams(), id);
//		uiRFQDetail.show();
//	}
//	
//	@Override
//	public void formListChange(ChangeEvent event) {
//		
//		//Deshabilitar campos cuando el estatus sea cancelado
//		if(!newRecord)
//		if (event.getSource() == statusListBox) {
//			update("Desea cambiar el Status del RFQ?");	
//			statusEffect();
//			
//		}		
//		
//		// Si el movimiento es en el lista de tipos de flujo del rfqu, modificar el de los efectos
//		if (event.getSource() == wFlowTypeListBox) {
//			resetEffectWFlowType();
//		} else if (event.getSource() == orderTypeListBox) {
//			BmoOrderType bmoOrderType = (BmoOrderType)orderTypeListBox.getSelectedBmObject();
//			if (bmoOrderType != null) {
//				changeOrderType(bmoOrderType);
//			}else {
//				wFlowTypeListBox.clear();
//				wFlowTypeListBox.clearFilters();
//				effectWFlowTypeListBox.clear();
//				effectWFlowTypeListBox.clearFilters();
//			}
//			}
//		
//		
//		statusEffect();
//	}
//	
//			
//	// Cambia el tipo de pedido y modifica combo de Tipos de Flujo
//	private void changeOrderType(BmoOrderType bmoOrderType) {
//		wFlowTypeListBox.clear();
//		wFlowTypeListBox.clearFilters();
//		// Limpia tambien el efecto
//		effectWFlowTypeListBox.clear();
//		effectWFlowTypeListBox.clearFilters();
//
//		// Agregar filtros al tipo de flujo
//		// Filtro de tipos de flujo en categorias del modulo Oportunidad
//		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
//		bmoWFlowType = new BmoWFlowType();
//		BmFilter bmFilter = new BmFilter();
//		bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);
//		filterList.add(bmFilter);
//
//		// Filtros de tipos de flujos activos
//		BmFilter bmFilterByStatus = new BmFilter();
//		bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
//		filterList.add(bmFilterByStatus);
//
//		// Filtro de flujos en categoria agregada al tipo de pedido
//		BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
//		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
//		BmFilter bmFilterByWFlowCategories = new BmFilter();
//		bmFilterByWFlowCategories.setInFilter(bmoOrderTypeWFlowCategory.getKind(), 
//				bmoWFlowCategory.getIdFieldName(), 
//				bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(), 
//				bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
//				"" + bmoOrderType.getId());
//
//		//wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
//		wFlowTypeListBox.addFilter(bmFilter); 
//
//		// Si el registro ya existia, es posible que se haya deshabilitado flujo, asegura que se muestre
//		if (newRecord) {
//			wFlowTypeListBox.addFilter(bmFilterByStatus);
//			wFlowTypeListBox.addFilter(bmFilterByWFlowCategories);
//		}
//
//		wFlowTypeListBox.populate(bmoRFQU.getwFlowTypeId().toString(), false);
//	}
//	
//	
//			// Agregar filtro al tipo de flujo del efecto de la oportunidad, si es que esta configurado 	
//			private void resetEffectWFlowType() {
//				effectWFlowTypeListBox.clear();
//				effectWFlowTypeListBox.clearFilters();
//
//				// Revisa el tipo de flujo seleccionado de la oportunidad
//				if (!wFlowTypeListBox.getSelectedId().equals("0")) {
//
//					bmoWFlowType = (BmoWFlowType)wFlowTypeListBox.getSelectedBmObject();
//					int effectCategoryId = bmoWFlowType.getBmoWFlowCategory().getEffectWFlowCategoryId().toInteger();
//					if (effectCategoryId > 0) {
//						BmFilter categoryFilter = new BmFilter();
//						categoryFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getIdField(), effectCategoryId);
//
//						BmFilter bmFilterByStatus = new BmFilter();
//						bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
//
//						effectWFlowTypeListBox.addFilter(categoryFilter);
//						effectWFlowTypeListBox.addFilter(bmFilterByStatus);
//					}
//					effectWFlowTypeListBox.populate("" + bmoRFQU.getForeignWFlowTypeId().toInteger(), false);
//					effectWFlowTypeListBox.setEnabled(true);
//					formFlexTable.showField(bmoRFQU.getForeignWFlowTypeId());
//				} else {
//					// No esta seleccionado
//					effectWFlowTypeListBox.setSelectedIndex(0);
//					effectWFlowTypeListBox.setEnabled(false);
//					formFlexTable.hideField(bmoRFQU.getForeignWFlowTypeId());
//				}
//			}
//			
//	private void setCustomerContactListBoxFilters(int customerId) {
//		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
//		BmFilter bmFilterByCustomerContact = new BmFilter();
//		bmFilterByCustomerContact.setValueFilter(bmoCustomerContact.getKind(), bmoCustomerContact.getCustomerId(), customerId);
//		contactListBox.addBmFilter(bmFilterByCustomerContact);		
//	}
//
//	
//	public void populateCustcomer(String customerId) {
//		callMainContasct(customerId);
//	}
//	@Override
//	public void postShow() {	
//		if(!(newRecord)) {
//			closeButton.setVisible(false);
//			deleteButton.setVisible(true);
//		}else {
//			deleteButton.setVisible(false);
//		}
//		
//
////			closeButton.setVisible(false);
//	}
//
//		
//	public void callMainContasct(String callMainContacts) {
//		callMainContasct(callMainContacts, 0);
//	};
//
//	//llamar el maincontact
//	public void callMainContasct(String customerId, int contactMainRpcAttempt) {
//		if (contactMainRpcAttempt < 5) {
//
//			setCustomerId(customerId);
//			setContactMainRpcAttempt(contactMainRpcAttempt + 1);
//			BmoCustomer bmoCustomer = new BmoCustomer();
//			AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					stopLoading();
//					if (getContactMainRpcAttempt() < 5) {
//					} else {
//						showErrorMessage(this.getClass().getName() + "-customerContact() ERROR: " + caught.toString());
//					}
//				}
//
//				@Override
//				public void onSuccess(BmUpdateResult result) {	
//					stopLoading();	
//					setContactMainRpcAttempt(0);
//					if (result.hasErrors())
//						showErrorMessage("Error al obtener el contacto principal.");
//					else {
//						contactListBox.setSelectedId(result.getMsg());
//					}
//				}
//			};
//
//			try {	
//
//				startLoading();
//
//				getUiParams().getBmObjectServiceAsync().action(bmoCustomer.getPmClass(), bmoCustomer, BmoCustomer.ACTION_GETCUSTOMERCONTACT, "" + customerId, callback);
//
//
//			} catch (SFException e) {
//				stopLoading();
//				showErrorMessage(this.getClass().getName() + "-getCustomerMainContact ERROR: " + e.toString());
//			}
//		}
//	}
//	
//
//	@Override
//	public void close() {
//		 new UiRFQU(getUiParams()).show();
//	}
//						
//	@Override
//	public BmObject populateBObject() throws BmException {
//		bmoRFQU.setId(id);
//		bmoRFQU.getCodeRFQU().setValue(codeRFQUTextBox.getText());
//		bmoRFQU.getCustomerId().setValue(customerSuggestBox.getSelectedId());
//		bmoRFQU.getUserSale().setValue(userSaleSuggestBox.getSelectedId());
//		bmoRFQU.getCustomerContactId().setValue(contactListBox.getSelectedId());
//		bmoRFQU.getAffair().setValue(affairTextBox.getText());
//		bmoRFQU.getDateRFQU().setValue(dateTimeBox.getTextBox().getText());
//		bmoRFQU.getOrderTypeId().setValue(orderTypeListBox.getSelectedId());
//		bmoRFQU.getwFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
//		bmoRFQU.getForeignWFlowTypeId().setValue(effectWFlowTypeListBox.getSelectedId());
//		bmoRFQU.getObjectiveRFQU().setValue(objectiveTextArea.getText());
//		bmoRFQU.getCompanyId().setValue(companyListBox.getSelectedId());
//		bmoRFQU.getStatusRFQU().setValue(statusListBox.getSelectedCode());
//		return bmoRFQU;
//	}
//	public void showLifeCycleDialog() {
//		// Vertical Panel
//		VerticalPanel vp = new VerticalPanel();
//		vp.setSize("100%", "100%");
//		vp.add(new HTML("&nbsp;"));
//		vp.add(lifeCycleCloseButton);
//
//		// Scroll Panel
//		ScrollPanel scrollPanel = new ScrollPanel();
//		if (getUiParams().isMobile())
//			scrollPanel.setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
//		else
//			scrollPanel.setSize(Window.getClientWidth() * .4 + "px", Window.getClientHeight() * .3 + "px");
//		scrollPanel.setWidget(vp);
//		lifeCycleDialogBox.setWidget(scrollPanel);
//
//		Double d = Window.getClientWidth() * .3;
//		if (!getUiParams().isMobile()) 
//			lifeCycleDialogBox.setPopupPosition(d.intValue(), UiTemplate.NORTHSIZE * 3);
//
//		lifeCycleDialogBox.show();
//	}
//	public String getCustomerId() {
//		return customerId;
//	}
//
//	public void setCustomerId(String customerId) {
//		this.customerId = customerId;
//	}
//
//	public int getContactMainRpcAttempt() {
//		return contactMainRpcAttempt;
//	}
//
//	public void setContactMainRpcAttempt(int contactMainRpcAttempt) {
//		this.contactMainRpcAttempt = contactMainRpcAttempt;
//	}
//
//	
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
//}