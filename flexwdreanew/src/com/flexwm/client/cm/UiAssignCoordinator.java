package com.flexwm.client.cm;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoAssignCoordinator;
import com.flexwm.shared.cm.BmoAssignSalesman;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoUser;


public class UiAssignCoordinator extends UiList {

	public UiAssignCoordinator(UiParams uiParams) {
		super(uiParams, new BmoAssignCoordinator());
	}

	@Override
	public void create() {
		UiAssignCoordinatorForm uiAssignCoordinatorForm = new UiAssignCoordinatorForm(getUiParams(), 0);
		uiAssignCoordinatorForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiAssignCoordinatorForm uiAssignCoordinatorForm = new UiAssignCoordinatorForm(getUiParams(), bmObject.getId());
		uiAssignCoordinatorForm.show();
	}

	public class UiAssignCoordinatorForm extends UiFormDialog {
		BmoAssignCoordinator bmoAssignCoordinator;

		UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
		UiSuggestBox coordinatorSuggestBox = new UiSuggestBox(new BmoUser());
		UiTextBox countTextBox = new UiTextBox();
		CheckBox assignedCheckBox = new CheckBox();
		CheckBox fullAssignmentCheckBox = new CheckBox();
		CheckBox queuedCoordinatorCheckBox = new CheckBox();

		AssignCoordinatorUpdater AssignCoordinatorUpdater = new AssignCoordinatorUpdater();

		private String companyId;
		private int profileSalesmanRpcAttempt = 0;

		public UiAssignCoordinatorForm(UiParams uiParams, int id) {
			super(uiParams, new BmoAssignCoordinator(), id);
		}

		@Override
		public void populateFields() {
			bmoAssignCoordinator = (BmoAssignCoordinator)getBmObject();
			
			try {
				// Busca Empresa seleccionada por default
				if (getUiParams().getSFParams().getSelectedCompanyId() > 0) {
					bmoAssignCoordinator.getCompanyId().setValue(getUiParams().getSFParams().getSelectedCompanyId());
					searchProfileCoordinatorByCompanyId("" + bmoAssignCoordinator.getCompanyId().toInteger(), 0);
				} else {
					// Filtrar por vendedores activos
					BmFilter filterSalesmenActive = new BmFilter();
					BmoUser bmoUser = new BmoUser();
					filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
					coordinatorSuggestBox.addFilter(filterSalesmenActive);
				}
			} catch (BmException e) {
				showSystemMessage("Error al asignar datos: " + e.toString());
			}
			formFlexTable.addField(1, 0, companyListBox, bmoAssignCoordinator.getCompanyId());
			formFlexTable.addField(2, 0, coordinatorSuggestBox, bmoAssignCoordinator.getUserId());
			if (!newRecord) {
				formFlexTable.addField(3, 0, assignedCheckBox, bmoAssignCoordinator.getAssigned());
				formFlexTable.addField(4, 0, countTextBox, bmoAssignCoordinator.getCountAssigned());
				formFlexTable.addField(5, 0, fullAssignmentCheckBox, bmoAssignCoordinator.getFullAssignment());
				//				formFlexTable.addField(6, 0, queuedCoordinatorCheckBox, bmoAssignCoordinator.getQueuedCoordinator());

				BmoAssignSalesman bmoAssignSalesman = new BmoAssignSalesman();
				FlowPanel assignSalesmanFP = new FlowPanel();
				BmFilter filterCustomerStatus = new BmFilter();
				filterCustomerStatus.setValueFilter(bmoAssignSalesman.getKind(), bmoAssignSalesman.getAssingCoordinatorId(), bmoAssignCoordinator.getId());
				getUiParams().setForceFilter(bmoAssignSalesman.getProgramCode(), filterCustomerStatus);
				UiAssignSalesman uiAssignSalesman = new UiAssignSalesman(getUiParams(), assignSalesmanFP, bmoAssignCoordinator, AssignCoordinatorUpdater);
				setUiType(bmoAssignSalesman.getProgramCode(), UiParams.MINIMALIST);
				uiAssignSalesman.show();
				formFlexTable.addPanel(7, 0, assignSalesmanFP, 2);
			} else {
				formFlexTable.addField(3, 0, queuedCoordinatorCheckBox, bmoAssignCoordinator.getQueuedCoordinator());
			}

			statusEffect();
		}

		public void statusEffect() {
			companyListBox.setEnabled(false);
			coordinatorSuggestBox.setEnabled(false);
			assignedCheckBox.setEnabled(false);
			countTextBox.setEnabled(false);
			fullAssignmentCheckBox.setEnabled(false);

			// Por defecto asignar verdadero
			if (newRecord) {
				companyListBox.setEnabled(true);
				coordinatorSuggestBox.setEnabled(true);
				queuedCoordinatorCheckBox.setValue(true);
			}
			
			// Si hay seleccion default de empresa, deshabilitar combo
			if (getUiParams().getSFParams().getSelectedCompanyId() > 0)
				companyListBox.setEnabled(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoAssignCoordinator.setId(id);
			bmoAssignCoordinator.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoAssignCoordinator.getUserId().setValue(coordinatorSuggestBox.getSelectedId());

			if (newRecord) {
				bmoAssignCoordinator.getCountAssigned().setValue("0");
				bmoAssignCoordinator.getAssigned().setValue("0");
				bmoAssignCoordinator.getFullAssignment().setValue("0");
				if (queuedCoordinatorCheckBox.getValue())
					bmoAssignCoordinator.getQueuedCoordinator().setValue(1);
				else 
					bmoAssignCoordinator.getQueuedCoordinator().setValue(0);
			} else {
				bmoAssignCoordinator.getCountAssigned().setValue(countTextBox.getText());
				bmoAssignCoordinator.getAssigned().setValue(assignedCheckBox.getValue());
				bmoAssignCoordinator.getFullAssignment().setValue(fullAssignmentCheckBox.getValue());
				bmoAssignCoordinator.getQueuedCoordinator().setValue(queuedCoordinatorCheckBox.getValue());
			}

			return bmoAssignCoordinator;
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == companyListBox) {
				BmoCompany bmoCompany = (BmoCompany)companyListBox.getSelectedBmObject();
				if (bmoCompany == null) {
					populateUsers(-1);
				} else {
					populateUsers(bmoCompany.getId());
				}
			}
		}

		// Filtrar vendedores por perfil/empresa
		private void populateUsers(int companyId) {
			if (newRecord) {
				coordinatorSuggestBox.clear();
				if (companyId > 0)
					searchProfileCoordinatorByCompanyId("" + companyId, 0);
			}
		}

		// Buscar perfil del vendedor POR empresa
		public void searchProfileCoordinatorByCompanyId(String companyId, int profileSalesmanRpcAttempt) {
			if (profileSalesmanRpcAttempt < 5) {
				setCompanyId(companyId);
				setProfileSalesmanRpcAttempt(profileSalesmanRpcAttempt + 1);
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					@Override
					public void onFailure(Throwable caught) {
						stopLoading();
						if (getProfileSalesmanRpcAttempt() < 5) {
							searchProfileCoordinatorByCompanyId(getCompanyId(), getProfileSalesmanRpcAttempt());
						} else {
							showErrorMessage(this.getClass().getName() + "-searchProfileCoordinatorByCompanyId() ERROR: " + caught.toString());
						}
					}

					@Override
					public void onSuccess(BmUpdateResult result) {
						stopLoading();	
						setProfileSalesmanRpcAttempt(0);
						if (result.hasErrors())
							showErrorMessage("Error al obtener el Perfil Coordinador de la Empresa.");
						else {
							// Aplicar filtro
							setUserListBoxFilters(result.getMsg());
						}
					}
				};

				try {	
					startLoading();
					BmoFlexConfig bmoFlexConfig = new BmoFlexConfig();
					getUiParams().getBmObjectServiceAsync().action(bmoFlexConfig.getPmClass(), bmoFlexConfig, BmoFlexConfig.ACTION_SEARCHPROFILECOORDINATOR, "" + companyId, callback);


				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-searchProfileCoordinatorByCompanyId() ERROR: " + e.toString());
				}
			}
		}

		// Filtrar usuarios por perfil de vendedores 
		private void setUserListBoxFilters(String profileCoordinatorId) {
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			
			// Filtro descartar al mismo usuario
			BmFilter filterAssigned = new BmFilter();
			filterAssigned.setNotInFilter(bmoAssignCoordinator.getKind(), 
					bmoUser.getIdFieldName(),
					bmoAssignCoordinator.getUserId().getName(),
					"1", "1");	
			coordinatorSuggestBox.addFilter(filterAssigned);
			
			// Filtro de perfil vendedor
			BmFilter filterSalesmen = new BmFilter();
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + profileCoordinatorId);	
			coordinatorSuggestBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			coordinatorSuggestBox.addFilter(filterSalesmenActive);
		}

		@Override
		public void saveNext() {
			// Si esta asignado el tipo de proyecto, envia al dashboard, en caso contrario, envia a la lista
			if (newRecord) {
				if (bmoAssignCoordinator.getUserId().toInteger() > 0) {
					UiAssignCoordinatorForm uiAssignCoordinator = new UiAssignCoordinatorForm(getUiParams(), id);
					uiAssignCoordinator.show();
				}
			} else {
				list();
			}
		}

		@Override
		public void close() {
			list();
		}

		// Actualizar la forma
		public class AssignCoordinatorUpdater {
			public void update() {
				stopLoading();
				get(id);
			}		
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
