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
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.op.BmoOrderCommission;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
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
 * @author jhernandez
 *
 */

public class UiDevelopmentPhase extends UiList {
	BmoDevelopmentPhase bmoDevelopmentPhase;

	public UiDevelopmentPhase(UiParams uiParams) {
		super(uiParams, new BmoDevelopmentPhase());
		bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();
	}

	@Override
	public void postShow() {
		addFilterSuggestBox(new UiSuggestBox(new BmoDevelopment()), new BmoDevelopment(), bmoDevelopmentPhase.getDevelopmentId());
	}

	@Override
	public void create() {
		UiDevelopmentPhaseForm uiDevelopmentPhaseForm = new UiDevelopmentPhaseForm(getUiParams(), 0);
		uiDevelopmentPhaseForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoDevelopmentPhase = (BmoDevelopmentPhase)bmObject;
		UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), bmoDevelopmentPhase.getId());
		uiDevelopmentPhaseDetail.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiDevelopmentPhaseForm uiDevelopmentPhaseForm = new UiDevelopmentPhaseForm(getUiParams(), bmObject.getId());
		uiDevelopmentPhaseForm.show();
	}

	public class UiDevelopmentPhaseForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		CheckBox isActiveCheckBox = new CheckBox();
		UiDateBox startDateDateBox = new UiDateBox();
		UiDateBox endDateDateBox = new UiDateBox();
		UiListBox developmentListBox = new UiListBox(getUiParams(), new BmoDevelopment());
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
		UiFileUploadBox blueprintFileUpload = new UiFileUploadBox(getUiParams());
		UiListBox orderCommissionListBox = new UiListBox(getUiParams(), new BmoOrderCommission());
		UiListBox wFlowTypeListBox;
		TextBox electronicFolioTextBox = new TextBox();
		TextBox fideicomisoNumberTextBox = new TextBox();
		UiSuggestBox budgetSuggestBox = new UiSuggestBox(new BmoBudget());
		TextBox numberPropertiesTextBox = new TextBox();
		TextBox maintenanceCostTextBox = new TextBox();
		UiDateBox feeDueDateDateBox =  new UiDateBox();
		UiListBox companyUiListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiListBox bankAccountUiListBox = new UiListBox(getUiParams(), new BmoBankAccount());

		BmoDevelopmentPhase bmoDevelopmentPhase;
		int programId;

		public UiDevelopmentPhaseForm(UiParams uiParams, int id) {
			super(uiParams, new BmoDevelopmentPhase(), id);
			bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();
			initialize();
		}

		private void initialize() {
			// Agregar filtros al tipo de flujo
			try {
				programId = getSFParams().getProgramId(bmoDevelopmentPhase.getProgramCode());
			} catch (SFException e) {
				showErrorMessage(this.getClass().getName() + "-initialize() ERROR: " + e.toString());
			}
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), programId);
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), bmFilter);

			// Filtrar por usuarios activos
			BmoUser bmoUser = new BmoUser();
			BmFilter filterUserActive = new BmFilter();
			filterUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterUserActive);
		}

		@Override
		public void populateFields() {
			bmoDevelopmentPhase = (BmoDevelopmentPhase)getBmObject();

			formFlexTable.addField(1, 0, codeTextBox, bmoDevelopmentPhase.getCode());
			formFlexTable.addField(2, 0, wFlowTypeListBox, bmoDevelopmentPhase.getWFlowTypeId());
			formFlexTable.addField(3, 0, nameTextBox, bmoDevelopmentPhase.getName());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoDevelopmentPhase.getDescription());
			formFlexTable.addField(5, 0, developmentListBox, bmoDevelopmentPhase.getDevelopmentId());
			formFlexTable.addField(6, 0, isActiveCheckBox, bmoDevelopmentPhase.getIsActive());
			formFlexTable.addField(7, 0, startDateDateBox, bmoDevelopmentPhase.getStartDate());
			formFlexTable.addField(8, 0, endDateDateBox, bmoDevelopmentPhase.getEndDate());
			formFlexTable.addField(9, 0, userSuggestBox, bmoDevelopmentPhase.getUserId());
			if (!newRecord)
				formFlexTable.addField(10, 0, blueprintFileUpload, bmoDevelopmentPhase.getBlueprint());
			formFlexTable.addField(11, 0, orderCommissionListBox, bmoDevelopmentPhase.getOrderCommissionId());				
			formFlexTable.addFieldReadOnly(12, 0, numberPropertiesTextBox, bmoDevelopmentPhase.getNumberProperties());
			if (!newRecord)
				formFlexTable.addField(13, 0, budgetSuggestBox, bmoDevelopmentPhase.getBudgetId());
			formFlexTable.addField(14, 0, maintenanceCostTextBox, bmoDevelopmentPhase.getMaintenanceCost());
			formFlexTable.addField(15, 0, feeDueDateDateBox, bmoDevelopmentPhase.getFeeDueDate());
			formFlexTable.addField(16, 0, electronicFolioTextBox, bmoDevelopmentPhase.getElectronicFolio());
			formFlexTable.addField(17, 0, fideicomisoNumberTextBox, bmoDevelopmentPhase.getFideicomisoNumber());
			setCompanyListBoxFilters(bmoDevelopmentPhase.getCompanyId().toInteger());
			formFlexTable.addField(18, 0, companyUiListBox, bmoDevelopmentPhase.getCompanyId());
			setBankAccountListBoxFilters(bmoDevelopmentPhase.getCompanyId().toInteger());
			formFlexTable.addField(19, 0, bankAccountUiListBox, bmoDevelopmentPhase.getBankAccountId());
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == developmentListBox) {
				BmoDevelopment bmoDevelopment = (BmoDevelopment)developmentListBox.getSelectedBmObject();
				if (bmoDevelopment != null) {
					populateCompany(bmoDevelopment.getCompanyId().toInteger());
					populateBankAccount(bmoDevelopment.getCompanyId().toInteger());
				} else {
					populateCompany(-1);
					populateBankAccount(-1);
				}
			}
		}

		// Actualiza combo de Empresas
		private void populateCompany(int companyId) {
			companyUiListBox.clear();
			companyUiListBox.clearFilters();
			setCompanyListBoxFilters(companyId);
			companyUiListBox.populate("" + companyId);
		}

		// Asigna filtros al listado de Empresas
		private void setCompanyListBoxFilters(int companyId) {
			BmoCompany bmoCompany = new BmoCompany();
			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), companyId);
				companyUiListBox.addBmFilter(bmFilterByCompany);
				companyUiListBox.setEnabled(false);
			} else {
				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoCompany.getKind(), bmoCompany.getIdField(), "-1");
				companyUiListBox.addBmFilter(bmFilterLimitRecords);
				companyUiListBox.setEnabled(true);
			}		
		}

		// Actualiza combo de Cuentas de Banco
		private void populateBankAccount(int companyId) {
			bankAccountUiListBox.clear();
			bankAccountUiListBox.clearFilters();
			setBankAccountListBoxFilters(companyId);
			bankAccountUiListBox.populate(bmoDevelopmentPhase.getBankAccountId());
		}

		// Asigna filtros al listado de Cuentas de Banco
		private void setBankAccountListBoxFilters(int companyId) {
			BmoBankAccount bmoBankAccount = new BmoBankAccount();
			if (companyId > 0) {
				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setValueFilter(bmoBankAccount.getKind(), bmoBankAccount.getCompanyId(), companyId);
				bankAccountUiListBox.addBmFilter(bmFilterByCompany);
			} else {
				BmFilter bmFilterLimitRecords = new BmFilter();
				bmFilterLimitRecords.setValueFilter(bmoBankAccount.getKind(), bmoBankAccount.getIdField(), "-1");
				bankAccountUiListBox.addBmFilter(bmFilterLimitRecords);
			}		
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoDevelopmentPhase.setId(id);
			bmoDevelopmentPhase.getCode().setValue(codeTextBox.getText());
			bmoDevelopmentPhase.getName().setValue(nameTextBox.getText());
			bmoDevelopmentPhase.getDescription().setValue(descriptionTextArea.getText());
			bmoDevelopmentPhase.getIsActive().setValue(isActiveCheckBox.getValue());
			bmoDevelopmentPhase.getStartDate().setValue(startDateDateBox.getTextBox().getText());
			bmoDevelopmentPhase.getEndDate().setValue(endDateDateBox.getTextBox().getText());
			bmoDevelopmentPhase.getDevelopmentId().setValue(developmentListBox.getSelectedId());
			bmoDevelopmentPhase.getOrderCommissionId().setValue(orderCommissionListBox.getSelectedId());
			bmoDevelopmentPhase.getBlueprint().setValue(blueprintFileUpload.getBlobKey());
			bmoDevelopmentPhase.getWFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoDevelopmentPhase.getUserId().setValue(userSuggestBox.getSelectedId());		
			bmoDevelopmentPhase.getElectronicFolio().setValue(electronicFolioTextBox.getText());
			bmoDevelopmentPhase.getFideicomisoNumber().setValue(fideicomisoNumberTextBox.getText());
			bmoDevelopmentPhase.getNumberProperties().setValue(numberPropertiesTextBox.getText());
			bmoDevelopmentPhase.getBudgetId().setValue(budgetSuggestBox.getSelectedId());
			bmoDevelopmentPhase.getMaintenanceCost().setValue(maintenanceCostTextBox.getText());
			bmoDevelopmentPhase.getFeeDueDate().setValue(feeDueDateDateBox.getTextBox().getText());
			bmoDevelopmentPhase.getCompanyId().setValue(companyUiListBox.getSelectedId());
			bmoDevelopmentPhase.getBankAccountId().setValue(bankAccountUiListBox.getSelectedId());

			return bmoDevelopmentPhase;
		}

		@Override
		public void close() {
//			if (isSlave()) {
//				//
//			} else {
//				if (getBmObject().getId() > 0) {
//					UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), getBmObject().getId());
//					uiDevelopmentPhaseDetail.show();
//				} else {
//					list();
//				}
//			}
		}

		@Override
		public void saveNext() {
			if (bmoDevelopmentPhase.getWFlowTypeId().toInteger() > 0) {
				UiDevelopmentPhaseDetail uiDevelopmentPhaseDetail = new UiDevelopmentPhaseDetail(getUiParams(), id);
				uiDevelopmentPhaseDetail.show();
			} else {
				UiDevelopmentPhase uiDevelopmentPhaseList = new UiDevelopmentPhase(getUiParams());
				uiDevelopmentPhaseList.show();
			}
		}
	}
}
