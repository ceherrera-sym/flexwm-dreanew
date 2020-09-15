/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoLocation;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoCommission;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowCategoryProfile;
import com.flexwm.shared.wf.BmoWFlowUser;


public class UiWFlowUserLock extends UiList {	
	BmoWFlowUser bmoWFlowUser;
	BmoWFlow bmoWFlow;
	Image availabilityImage;
	private BmoProfile bmoProfile = new BmoProfile();

	public UiWFlowUserLock(UiParams uiParams, Panel defaultPanel, BmoWFlow bmoWFlow) {
		super(uiParams, defaultPanel, new BmoWFlowUser());
		bmoWFlowUser = (BmoWFlowUser)getBmObject();
		this.bmoWFlow = bmoWFlow;
		
		availabilityImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/wflu_availability.png"));
		availabilityImage.setTitle("Revisar disponibilidad...");
		availabilityImage.setStyleName("listSearchImage");
		availabilityImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isLoading()) {
					showAvailability();
				}
			}
		});
	}

	@Override
	public void create() {
		UiWFlowUserForm uiWFlowUserForm = new UiWFlowUserForm(getUiParams(), 0);
		uiWFlowUserForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoWFlowUser = (BmoWFlowUser)bmObject;
		UiWFlowUserForm uiWFlowUserForm = new UiWFlowUserForm(getUiParams(), bmoWFlowUser.getId());
		uiWFlowUserForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiWFlowUserForm uiWFlowUserForm = new UiWFlowUserForm(getUiParams(), bmObject.getId());
		uiWFlowUserForm.show();
	}
	
	@Override
	public void postShow() {
		localButtonPanel.add(availabilityImage);
	}
	
	public void showAvailability() {
		String url = "/rpt/wf/wflu_availability.jsp?wflu_wflowid=" + bmoWFlow.getId();
		Window.open(GwtUtil.getProperUrl(getUiParams().getSFParams(), url), "_blank", "");
	}
	
	private class UiWFlowUserForm extends UiFormDialog {
		UiListBox userListBox = new UiListBox(getUiParams(), new BmoUser());
		UiDateTimeBox lockStartDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox lockEndDateTimeBox = new UiDateTimeBox();
		CheckBox lockCheckBox = new CheckBox();
		CheckBox autoDateCheckBox = new CheckBox();
		CheckBox assignStepsCheckBox = new CheckBox();
		UiTextBox percetageCommisionTextBox = new UiTextBox();
		UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		CheckBox commissionCheckBox = new CheckBox();
		UiListBox locationIdListBox = new UiListBox(getUiParams());

		public UiWFlowUserForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowUser(), id);
			bmoWFlowUser = (BmoWFlowUser)getBmObject();
		}

		@Override
		public void populateFields() {
			bmoWFlowUser = (BmoWFlowUser)getBmObject();
			// Asignar valores del WFlow si es nuevo registro
			if (!(bmoWFlowUser.getId() > 0)) {
				try {
					bmoWFlowUser.getLockStart().setValue(bmoWFlow.getStartDate().toString());
					bmoWFlowUser.getLockEnd().setValue(bmoWFlow.getEndDate().toString());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
				}
			}
			
			BmoWFlowCategoryProfile bmoWFlowCategoryProfile = new BmoWFlowCategoryProfile();
			BmFilter filterGroups = new BmFilter();
			filterGroups.setInFilter(bmoWFlowCategoryProfile.getKind(), 
					bmoProfile.getIdFieldName(),
					bmoWFlowCategoryProfile.getProfileId().getName(),
					bmoWFlowCategoryProfile.getWFlowCategoryId().getName(), "" + bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getId()
					);
			profileListBox.addFilter(filterGroups);
			
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				try {
					bmoWFlowUser.getLocationId().setValue(getUiParams().getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields() ERROR: " + e.toString());
				}
				locationIdListBox = new UiListBox(getUiParams(), new BmoLocation());
				formFlexTable.addField(1, 0, locationIdListBox, bmoWFlowUser.getLocationId());
			}
			formFlexTable.addField(2, 0, profileListBox, bmoWFlowUser.getProfileId());
			formFlexTable.addField(4, 0, autoDateCheckBox, bmoWFlowUser.getAutoDate());	
			formFlexTable.addField(5, 0, lockStartDateTimeBox, bmoWFlowUser.getLockStart());
			formFlexTable.addField(6, 0, lockEndDateTimeBox, bmoWFlowUser.getLockEnd());
			
			formFlexTable.addField(7, 0, lockCheckBox, bmoWFlowUser.getLockStatus());	
			formFlexTable.addField(8, 0, assignStepsCheckBox, bmoWFlowUser.getAssignSteps());	
			formFlexTable.addField(9, 0, commissionCheckBox, bmoWFlowUser.getCommission());

			if (!newRecord && bmoWFlowUser.getLockStatus().toString().equals("" + BmoWFlowUser.LOCKSTATUS_LOCKED)) 
				lockCheckBox.setValue(true);

			statusEffect();
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				showUserList(bmoWFlowUser.getLocationId().toInteger());
			} else  showUserList(-1);
		}
		
//		@Override
		public void postShow() {
			if (!newRecord && bmoWFlowUser.getRequired().toBoolean())
				deleteButton.setVisible(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowUser.setId(id);
			bmoWFlowUser.getWFlowId().setValue(bmoWFlow.getId());
			bmoWFlowUser.getProfileId().setValue(profileListBox.getSelectedId());
			bmoWFlowUser.getUserId().setValue(userListBox.getSelectedId());
			bmoWFlowUser.getLockStart().setValue(lockStartDateTimeBox.getDateTime());
			bmoWFlowUser.getLockEnd().setValue(lockEndDateTimeBox.getDateTime());
			bmoWFlowUser.getAutoDate().setValue(autoDateCheckBox.getValue());
			bmoWFlowUser.getAssignSteps().setValue(assignStepsCheckBox.getValue());
			bmoWFlowUser.getCommission().setValue(commissionCheckBox.getValue());
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) 
				bmoWFlowUser.getLocationId().setValue(locationIdListBox.getSelectedId());

			if (lockCheckBox.getValue()) 
				bmoWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_LOCKED);
			else 
				bmoWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_OPEN);

			return bmoWFlowUser;
		}

		private void statusEffect() {
			profileListBox.setEnabled(false);		
			percetageCommisionTextBox.setEnabled(false);

			lockStartDateTimeBox.setEnabled(false);
			lockEndDateTimeBox.setEnabled(false);

			if (bmoWFlowUser.getRequired().toBoolean()) {
				deleteButton.setVisible(false);
			} else {
				profileListBox.setEnabled(true);
			}

			if (!autoDateCheckBox.getValue()) {
				lockStartDateTimeBox.setEnabled(true);
				lockEndDateTimeBox.setEnabled(true);
			}
			
			// Validar si tiene permiso de propios, en la ubicacion
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean()) {
				if (getSFParams().restrictData(new BmoLocation().getProgramCode())) {
					locationIdListBox.setEnabled(false);
				} else {
					locationIdListBox.setEnabled(true);
				}
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == profileListBox) {
				if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
					if (!locationIdListBox.getSelectedId().equalsIgnoreCase(""))
						showUserList(Integer.parseInt(locationIdListBox.getSelectedId()));
				} else {
					showUserList(-1);
				}
				//Habilitar la comision
				getCommisionGroup("" + profileListBox.getSelectedId());
				statusEffect();
			} else if (event.getSource() == locationIdListBox) {
				if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
					BmoLocation bmoLocation = (BmoLocation)locationIdListBox.getSelectedBmObject();
//					GWT.log("Ubicacion seleccionada:" + bmoLocation.getName().toString());
					showUserList(bmoLocation.getId());
				}
			}
		}

		private void showUserList(int locationId) {
			BmoUser bmoUser = new BmoUser();		
			if (!profileListBox.getSelectedId().equals("0")) {
				// Actualizar lista de usuarios del grupo

				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter userByGroup = new BmFilter();
				userByGroup.setInFilter(bmoProfileUser.getKind(), 
						bmoUser.getIdFieldName(), 
						bmoProfileUser.getUserId().getName(),
						bmoProfileUser.getProfileId().getName(),
						"" + profileListBox.getSelectedId()
						);

				BmFilter activeUser = new BmFilter();
				activeUser.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);

				ArrayList<BmFilter> filters = new ArrayList<BmFilter>();
				filters.add(userByGroup);
				filters.add(activeUser);
				
				
				if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
					if (locationId > 0) {
						BmFilter byLocation = new BmFilter();
						byLocation.setValueFilter(bmoUser.getKind(), bmoUser.getLocationId(), locationId);
						filters.add(byLocation);
					}
				}

				userListBox = new UiListBox(getUiParams(), bmoUser, filters);	

				formFlexTable.addField(3, 0, userListBox, bmoWFlowUser.getUserId());
			} else {
				formFlexTable.addFieldEmpty(3, 0);
			}
		}

		@Override
		public void formBooleanChange(ValueChangeEvent<Boolean> event) {
			if (commissionCheckBox.getValue()) {
				getCommisionGroup("" + profileListBox.getSelectedId());				
			} 
			statusEffect();
		}

		//Si el grupo maneja comision
		public void getCommisionGroup(String profileId) {
			BmoCommission bmoCommission = new BmoCommission();

			if (getSFParams().hasRead(bmoCommission.getProgramCode())) {
				AsyncCallback<BmUpdateResult> callback = new AsyncCallback<BmUpdateResult>() {
					public void onFailure(Throwable caught) {
						stopLoading();
						showErrorMessage(this.getClass().getName() + "-getCommisionGroup() ERROR: " + caught.toString());
					}
	
					public void onSuccess(BmUpdateResult result) {
						stopLoading();
						double percentage = Double.parseDouble(result.getMsg());				
						try {
							if (percentage > 0) {
								bmoWFlowUser.getCommission().setValue(true);						
							} else {
								bmoWFlowUser.getCommission().setValue(false);
							}
	
							formFlexTable.addField(9, 0, commissionCheckBox, bmoWFlowUser.getCommission());
	
						} catch (BmException e) {
							showErrorMessage("Error al asignar la comisión: " + e.toString());
						}	
					}
				};
	
				try {	
					startLoading();
					getUiParams().getBmObjectServiceAsync().action(bmoCommission.getPmClass(), bmoCommission, BmoCommission.ACTION_GETCOMMISSION,  profileId, callback);
				} catch (SFException e) {
					stopLoading();
					showErrorMessage(this.getClass().getName() + "-getCommisionGroup() ERROR: " + e.toString());
				}
			}
		} 

		@Override
		public void close() {
			list();
		}

	}
}