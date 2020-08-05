package com.flexwm.client.cm;

import com.flexwm.client.cm.UiAssignCoordinator.UiAssignCoordinatorForm.AssignCoordinatorUpdater;
import com.flexwm.shared.cm.BmoAssignCoordinator;
import com.flexwm.shared.cm.BmoAssignSalesman;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class UiAssignSalesman extends UiList {
	BmoAssignCoordinator bmoAssignCoordinator = new BmoAssignCoordinator();
	BmoAssignSalesman bmoAssignSalesman;
	protected AssignCoordinatorUpdater assignCoordinatorUpdater;

	public UiAssignSalesman(UiParams uiParams, Panel defaultPanel, BmoAssignCoordinator bmoAssignCoordinator, AssignCoordinatorUpdater assignCoordinatorUpdater) {
		super(uiParams, defaultPanel, new BmoAssignSalesman());
		bmoAssignSalesman = (BmoAssignSalesman)getBmObject();
		this.bmoAssignCoordinator = bmoAssignCoordinator;
		this.assignCoordinatorUpdater = assignCoordinatorUpdater;
	}

	@Override
	public void create() {
		UiCategoryForecastForm UiCategoryForecastForm = new UiCategoryForecastForm(getUiParams(), 0);
		UiCategoryForecastForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCategoryForecastForm UiCategoryForecastForm = new UiCategoryForecastForm(getUiParams(), bmObject.getId());
		UiCategoryForecastForm.show();
	}

	public class UiCategoryForecastForm extends UiFormDialog {
		BmoAssignSalesman bmoAssignSalesman;

		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiTextBox countTextBox = new UiTextBox();
		CheckBox assignedCheckBox = new CheckBox();
		CheckBox queuedUserCheckBox = new CheckBox();

		private String companyId;
		private int profileSalesmanRpcAttempt = 0;

		public UiCategoryForecastForm(UiParams uiParams, int id) {
			super(uiParams, new BmoAssignSalesman(), id);
			bmoAssignSalesman = (BmoAssignSalesman)getBmObject();

			// Filtro descartar al mismo usuario
			BmoUser bmoUser = new BmoUser();
			BmFilter filterAssigned = new BmFilter();
			filterAssigned.setNotInFilter(bmoAssignSalesman.getKind(), 
					bmoUser.getIdFieldName(),
					bmoAssignSalesman.getUserId().getName(),
					"1", "1");	
			userSuggestBox.addFilter(filterAssigned);

			// FIltro de jefe inmediaro
			BmFilter filterParent = new BmFilter();
			filterParent.setValueFilter(bmoUser.getKind(), bmoUser.getParentId(), bmoAssignCoordinator.getUserId().toInteger());
			userSuggestBox.addFilter(filterParent);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);
		}

		@Override
		public void populateFields() {
			bmoAssignSalesman = (BmoAssignSalesman)getBmObject();
			formFlexTable.addField(1, 0, userSuggestBox, bmoAssignSalesman.getUserId());
			if (!newRecord) {
				formFlexTable.addField(2, 0, assignedCheckBox, bmoAssignSalesman.getAssigned());
				formFlexTable.addField(3, 0, countTextBox, bmoAssignSalesman.getCountAssigned());
				formFlexTable.addField(4, 0, queuedUserCheckBox, bmoAssignSalesman.getQueuedUser());
			}

			statusEffect();
		}

		public void statusEffect() {
			userSuggestBox.setEnabled(false);
			assignedCheckBox.setEnabled(false);
			countTextBox.setEnabled(false);
			queuedUserCheckBox.setEnabled(false);

			if (newRecord) userSuggestBox.setEnabled(true);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoAssignSalesman.setId(id);
			bmoAssignSalesman.getAssingCoordinatorId().setValue(bmoAssignCoordinator.getId());
			bmoAssignSalesman.getUserId().setValue(userSuggestBox.getSelectedId());
			if (newRecord) {
				bmoAssignSalesman.getCountAssigned().setValue("0");
				bmoAssignSalesman.getAssigned().setValue("0");
				bmoAssignSalesman.getQueuedUser().setValue("0");
			} else {
				bmoAssignSalesman.getCountAssigned().setValue(countTextBox.getText());
				bmoAssignSalesman.getAssigned().setValue(assignedCheckBox.getValue());
				bmoAssignSalesman.getQueuedUser().setValue(queuedUserCheckBox.getValue());
			}
			return bmoAssignSalesman;
		}

		@Override
		public void saveNext() {
			//			if (newRecord) {
			updateassignCoordinatorForm();
			//			} else {
			//				list();
			//			}
		}

		// Actualizar datos del cliente
		public void updateassignCoordinatorForm() {
			if (assignCoordinatorUpdater != null)
				assignCoordinatorUpdater.update();
		}

		@Override
		public void close() {
			if (deleted) updateassignCoordinatorForm();
			else list();
		}

		// Variables para llamadas RPC
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
