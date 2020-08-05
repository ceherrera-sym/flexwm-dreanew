/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.op;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoUserCompany;
import java.util.Date;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoCustomerServiceFollowup;
import com.flexwm.shared.op.BmoCustomerServiceType;
import com.flexwm.shared.op.BmoOrder;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFileUploadBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiCustomerService extends UiList {
	BmoCustomerService bmoOrderComplaint;

	public UiCustomerService(UiParams uiParams) {
		super(uiParams, new BmoCustomerService());
		bmoOrderComplaint = (BmoCustomerService)getBmObject();
	}

	@Override
	public void postShow() {
		if (isMaster()) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoCustomerServiceType()), new BmoCustomerServiceType());
			addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoOrderComplaint.getUserId());
			addStaticFilterListBox(new UiListBox(getUiParams(), new BmoCustomerService().getStatus()), bmoOrderComplaint, bmoOrderComplaint.getStatus());
		}
	}

	@Override
	public void create() {
		UiCustomerServiceForm uiOrderComplaintForm = new UiCustomerServiceForm(getUiParams(), 0);
		uiOrderComplaintForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCustomerServiceForm uiOrderComplaintForm = new UiCustomerServiceForm(getUiParams(), bmObject.getId());
		uiOrderComplaintForm.show();
	}

	public class UiCustomerServiceForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
		UiListBox customerServiceTypeListBox = new UiListBox(getUiParams(), new BmoCustomerServiceType());
		TextArea solutionTextArea = new TextArea();
		UiListBox statusUiListBox = new UiListBox(getUiParams());
		UiDateBox registrationDateDateBox = new UiDateBox();
		UiDateBox committalDateDateBox = new UiDateBox();
		UiDateBox solutionDateDateBox = new UiDateBox();
		UiFileUploadBox fileUploadBox = new UiFileUploadBox(getUiParams());
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox companyUiListBox = new UiListBox(getUiParams(), new BmoCompany());
		CustomerServiceUpdater customerServiceUpdater = new CustomerServiceUpdater();
		BmoCustomerService bmoCustomerService;

		String generalSection = "Datos Generales";
		String itemsSection = "Items";
		String statusSection = "Estatus";

		public UiCustomerServiceForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCustomerService(), id); 
		}

		@Override
		public void populateFields() {
			bmoCustomerService = (BmoCustomerService)getBmObject();

			if (isSlave()) {
				// Es derivado de un pedido, no es necesario mostrarlo
				int orderId = Integer.parseInt(getUiParams().getUiProgramParams(bmoCustomerService.getProgramCode()).getForceFilter().getValue());
				try {
					bmoCustomerService.getOrderId().setValue(orderId);
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}
			}

			if (newRecord) {
				try {
					bmoCustomerService.getRegistrationDate().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					bmoCustomerService.getUserId().setValue(getUiParams().getSFParams().getLoginInfo().getUserId());
					// Busca Empresa seleccionada por default
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0) {
						bmoCustomerService.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
					}
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}
			}

			//Mostrar usuarios Activos
			BmoUser bmoUser = new BmoUser();
			BmFilter bmFilterUsersActives = new BmFilter();
			bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(bmFilterUsersActives);

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);

			formFlexTable.addFieldReadOnly(2, 0, codeTextBox, bmoCustomerService.getCode());
			formFlexTable.addField(3, 0, customerServiceTypeListBox, bmoCustomerService.getCustomerServiceTypeId()); 
			formFlexTable.addField(4, 0, nameTextBox, bmoCustomerService.getName());		
			formFlexTable.addField(5, 0, orderSuggestBox, bmoCustomerService.getOrderId()); 
			formFlexTable.addField(6, 0, registrationDateDateBox, bmoCustomerService.getRegistrationDate());
			formFlexTable.addField(7, 0, descriptionTextArea, bmoCustomerService.getDescription());
			formFlexTable.addField(8, 0, committalDateDateBox, bmoCustomerService.getCommittalDate());
			formFlexTable.addField(9, 0, solutionDateDateBox, bmoCustomerService.getSolutionDate());
			formFlexTable.addField(10, 0, companyUiListBox, bmoCustomerService.getCompanyId());
			formFlexTable.addField(11, 0, solutionTextArea, bmoCustomerService.getSolution());
			populateUserByCompany(bmoCustomerService.getCompanyId().toInteger());
			formFlexTable.addField(12, 0, userSuggestBox, bmoCustomerService.getUserId()); 
			formFlexTable.addField(13, 0, fileUploadBox, bmoCustomerService.getFile());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(14, 0, itemsSection, 2);
				BmoCustomerServiceFollowup bmoCustomerServiceFollowup = new BmoCustomerServiceFollowup();
				FlowPanel customerServiceFollowupFP = new FlowPanel();
				BmFilter filterCustomerServiceFollowup = new BmFilter();
				filterCustomerServiceFollowup.setValueFilter(bmoCustomerServiceFollowup.getKind(), bmoCustomerServiceFollowup.getCustomerServiceId(), bmoCustomerService.getId());
				getUiParams().setForceFilter(bmoCustomerServiceFollowup.getProgramCode(), filterCustomerServiceFollowup);
				UiCustomerServiceFollowup uiCustomerServiceFollowup = new UiCustomerServiceFollowup(getUiParams(), customerServiceFollowupFP, bmoCustomerService, bmoCustomerService.getId(), customerServiceUpdater);
				setUiType(bmoCustomerServiceFollowup.getProgramCode(), UiParams.MINIMALIST);
				uiCustomerServiceFollowup.show();
				formFlexTable.addPanel(15, 0, customerServiceFollowupFP, 2);

			}

			formFlexTable.addSectionLabel(16, 0, statusSection, 2);
			formFlexTable.addField(17, 0, statusUiListBox, bmoCustomerService.getStatus()); 

			statusEffect();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCustomerService.setId(id);
			bmoCustomerService.getCode().setValue(codeTextBox.getText());
			bmoCustomerService.getName().setValue(nameTextBox.getText());
			bmoCustomerService.getDescription().setValue(descriptionTextArea.getText());
			bmoCustomerService.getCustomerServiceTypeId().setValue(customerServiceTypeListBox.getSelectedId()); 
			bmoCustomerService.getOrderId().setValue(orderSuggestBox.getSelectedId());
			bmoCustomerService.getSolution().setValue(solutionTextArea.getText());
			bmoCustomerService.getStatus().setValue(statusUiListBox.getSelectedCode()); 
			bmoCustomerService.getRegistrationDate().setValue(registrationDateDateBox.getTextBox().getText());
			bmoCustomerService.getCommittalDate().setValue(committalDateDateBox.getTextBox().getText());
			bmoCustomerService.getSolutionDate().setValue(solutionDateDateBox.getTextBox().getText());
			bmoCustomerService.getFile().setValue(fileUploadBox.getBlobKey());
			bmoCustomerService.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoCustomerService.getCompanyId().setValue(companyUiListBox.getSelectedId());
			return bmoCustomerService;
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == companyUiListBox) {
				BmoCompany bmoCompany = (BmoCompany)companyUiListBox.getSelectedBmObject();
				if (bmoCompany != null) {
					populateUserByCompany(bmoCompany.getId());
				} else { 
					populateUserByCompany(-1);
				}
			}
		}

		// Accion cambio SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			// Filtros de empresas
			// Toma empresa del pedido
			if	(uiSuggestBox == orderSuggestBox) {
				BmoOrder bmoOrder = (BmoOrder)orderSuggestBox.getSelectedBmObject();
				if	(bmoOrder != null)
					populateCompany(bmoOrder.getCompanyId().toInteger()); 
				else {
					if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
						populateCompany(getUiParams().getSFParams().getSelectedCompanyId());
					else 
						populateCompany(-1);
				}
			}
		}

		private void statusEffect() {
			if (isSlave())
				orderSuggestBox.setEnabled(false);

			if (!newRecord) {
				orderSuggestBox.setEnabled(false);
				customerServiceTypeListBox.setEnabled(false);
				registrationDateDateBox.setEnabled(false);

				if (orderSuggestBox.getSelectedId() > 0)
					companyUiListBox.setEnabled(false);
			}

			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyUiListBox.setEnabled(false);
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiCustomerServiceForm uiCustomerServiceForm = new UiCustomerServiceForm(getUiParams(), getBmObject().getId());
				uiCustomerServiceForm.show();
			} else {
				close();
			}		
		}

		private void populateCompany(int companyId) {
			companyUiListBox.clear();
			companyUiListBox.clearFilters();
			setCompanyListBoxFilters(companyId);
			companyUiListBox.populate("" + companyId);
		}

		private void setCompanyListBoxFilters(int companyId) {
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter filterCompany = new BmFilter();

			if (companyId > 0) {
				filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyUiListBox.addFilter(filterCompany);			
			} else {
//				filterCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
//				companyUiListBox.addFilter(filterCompany);				
			}

			populateUserByCompany(companyId);
		}

		private void populateUserByCompany(int companyId) {
			userSuggestBox.clear();
			setUserByCompanyFilter(companyId);
		}

		private void setUserByCompanyFilter(int companyId) {		
			BmoUser bmoUser = new BmoUser();
			BmoUserCompany bmoUserCompany = new BmoUserCompany();
			BmoCompany bmoCompany = new BmoCompany();

			BmFilter bmFilterUsersActives = new BmFilter();
			bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(bmFilterUsersActives);

			if (companyId > 0) {
				if (getSFParams().restrictData(bmoCompany.getProgramCode())) {

					// Filtro empresas del usuario
					BmFilter bmProductCompanyFilter = new BmFilter();
					bmProductCompanyFilter.setInFilter(bmoUserCompany.getKind(), 
							bmoUser.getIdFieldName(), 
							bmoUserCompany.getUserId().getName(), 
							bmoUserCompany.getCompanyId().getName(), 
							""+companyId);
					userSuggestBox.addFilter(bmProductCompanyFilter);
				}	
			} else {
				bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getCompanyId(), "-1");
				userSuggestBox.addFilter(bmFilterUsersActives);	
			}
		}

		public class CustomerServiceUpdater {
			public void update() {
				stopLoading();
			}		
		}

	}
}
