package com.flexwm.client.op;

import com.flexwm.shared.BmoFlexConfig;
//import com.flexwm.shared.cm.BmoProjectStep;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.wf.BmoWFlowType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoProfile;


public class UiOrderType extends UiList {

	public UiOrderType(UiParams uiParams) {
		super(uiParams, new BmoOrderType());
	}

	@Override
	public void create() {
		UiOrderTypeForm uiOrderTypeForm = new UiOrderTypeForm(getUiParams(), 0);
		uiOrderTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiOrderTypeForm uiOrderTypeForm = new UiOrderTypeForm(getUiParams(), bmObject.getId());
		uiOrderTypeForm.show();
	}

	public class UiOrderTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox typeListBox = new UiListBox(getUiParams());
		CheckBox autoRenewCheckBox = new CheckBox();
		CheckBox emailRemindersOrderStartCheckBox = new CheckBox();
		CheckBox emailRemindersOrderEndCheckBox = new CheckBox();
		CheckBox emailChangeStatusOppoCheckBox = new CheckBox();
		TextBox remindDaysBeforeEndOrderTextBox = new TextBox();
		UiSuggestBox profileUiSuggestBox = new UiSuggestBox(new BmoProfile());
		UiSuggestBox dispersionProfileUiSuggestBox = new UiSuggestBox(new BmoProfile());
		CheckBox filterOnScrumCheckBox = new CheckBox();
		CheckBox endContractCheckBox = new CheckBox();

		CheckBox enableSendDeliveryCheckBox = new CheckBox();
		CheckBox enableReqiOrderFinishCheckBox = new CheckBox();
		CheckBox hasTaxes = new CheckBox();
		CheckBox requiredLoseComments = new CheckBox();

		UiSuggestBox defaultBudgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
		UiSuggestBox defaultAreaSuggestBox = new UiSuggestBox(new BmoArea());
		UiListBox wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType());
		CheckBox enableExtraOrderCheckBox = new CheckBox();
		BmoOrderType bmoOrderType;

		CheckBox dataFiscalCheckBox = new CheckBox();
		CheckBox atmCCRevisionCheckBox = new CheckBox();
		CheckBox createProyectCheckBox = new CheckBox();
		CheckBox requiredPropertyModelCheckBox = new CheckBox();
		UiListBox defaultWFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType()); // g100
		//Campos inadico recordar termino de contrato
		TextBox remindDaysBeforeEndOrderTwoTextBox = new TextBox();
		TextBox remindDaysBeforeRentIncreaseTextBox = new TextBox();
		TextBox remindDaysBeforeRentIncreaseTwoTextBox = new TextBox();
		TextBox remindDaysBeforeEndDateTextBox = new TextBox();
		TextBox remindDaysBeforeEndDateTwoTextBox = new TextBox();
		TextBox remindDaysBeforeEndDateThreeTextBox = new TextBox();

		UiListBox statusDefaultDetailUiList = new UiListBox(getUiParams());
		UiListBox areaDefaultDetailUiList = new UiListBox(getUiParams(),new BmoArea());

		String comercialSection = "Comercial";
		String financesSection = "Finanzas";
		String operationsSection = "Operaciones";

		public UiOrderTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoOrderType(), id);
		}

		@Override
		public void populateFields() {
			bmoOrderType = (BmoOrderType) getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoOrderType.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoOrderType.getDescription());
			formFlexTable.addField(3, 0, typeListBox, bmoOrderType.getType());

			formFlexTable.addSectionLabel(6, 0, comercialSection, 2);
			// todos
			formFlexTable.addField(7, 0, requiredPropertyModelCheckBox, bmoOrderType.getRequiredPropertyModel()); 
			formFlexTable.addField(8, 0, defaultWFlowTypeListBox, bmoOrderType.getDefaultWFlowTypeId()); 
			formFlexTable.addField(9, 0, requiredLoseComments, bmoOrderType.getRequiredLoseComments()); 
			formFlexTable.addField(10, 0, emailChangeStatusOppoCheckBox, bmoOrderType.getEmailChangeStatusOppo()); 
			formFlexTable.addField(11, 0, emailRemindersOrderStartCheckBox, bmoOrderType.getEmailRemindersOrderStart()); 
			formFlexTable.addField(12, 0, emailRemindersOrderEndCheckBox, bmoOrderType.getEmailRemindersOrderEnd());

			// todos
			formFlexTable.addField(13, 0, remindDaysBeforeEndOrderTextBox, bmoOrderType.getRemindDaysBeforeEndOrder()); 
			// arrendamiento
			formFlexTable.addField(14, 0, remindDaysBeforeEndOrderTwoTextBox, bmoOrderType.getRemindDaysBeforeEndOrderTwo()); 

			// todos y arrendamiento
			formFlexTable.addField(15, 0, profileUiSuggestBox, bmoOrderType.getProfileId()); 
			// arrendamiento
			formFlexTable.addField(16, 0, remindDaysBeforeRentIncreaseTextBox, bmoOrderType.getRemindDaysBeforeRentIncrease());
			formFlexTable.addField(17, 0, remindDaysBeforeRentIncreaseTwoTextBox, bmoOrderType.getRemindDaysBeforeRentIncreaseTwo());	
			formFlexTable.addField(18, 0, remindDaysBeforeEndDateThreeTextBox, bmoOrderType.getRemindDaysBeforeEndContractDateThree());
			formFlexTable.addField(19, 0, endContractCheckBox, bmoOrderType.getEmailReminderContractEnd()); 
			formFlexTable.addField(20, 0, remindDaysBeforeEndDateTextBox, bmoOrderType.getRemindDaysBeforeEndContractDate());
			formFlexTable.addField(21, 0, remindDaysBeforeEndDateTwoTextBox, bmoOrderType.getRemindDaysBeforeEndContractDateTwo());

			// Credito
			formFlexTable.addField(22, 0, dispersionProfileUiSuggestBox, bmoOrderType.getDispersionProfileId());

			formFlexTable.addField(23, 0, filterOnScrumCheckBox, bmoOrderType.getFilterOnScrum()); // todos
			formFlexTable.addField(24, 0, dataFiscalCheckBox, bmoOrderType.getDataFiscal()); // todos

			if (!newRecord) {	
				formFlexTable.addField(25, 0, new UiOrderTypeWFlowCategoryLabelList(getUiParams(), id));
			}

			// todos
			formFlexTable.addSectionLabel(26, 0, operationsSection, 2);
			formFlexTable.addField(27, 0, enableSendDeliveryCheckBox, bmoOrderType.getEnableDeliverySend());
//			if(getUiParams().getSFParams().hasRead(new BmoProjectStep().getProgramCode()))
//				formFlexTable.addField(28, 0, createProyectCheckBox,bmoOrderType.getCreateProject());
			formFlexTable.addField(29, 0, enableReqiOrderFinishCheckBox, bmoOrderType.getEnableReqiOrderFinish());
			if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRenewProducts().toBoolean())) {	
				formFlexTable.addField(30, 0, autoRenewCheckBox, bmoOrderType.getAutoRenew());
			}
			formFlexTable.addField(31, 0, atmCCRevisionCheckBox, bmoOrderType.getAtmCCRevision());

			// Renta
			formFlexTable.addField(32, 0, enableExtraOrderCheckBox, bmoOrderType.getEnableExtraOrder());
			formFlexTable.addField(33, 0, wFlowTypeListBox, bmoOrderType.getwFlowTypeId());
			formFlexTable.addField(34, 0, areaDefaultDetailUiList, bmoOrderType.getAreaDefaultDetail());
			formFlexTable.addField(35, 0, statusDefaultDetailUiList, bmoOrderType.getStatusDefaultDetail());
			// todos
			formFlexTable.addSectionLabel(36, 0, financesSection, 2);
			formFlexTable.addField(37, 0, hasTaxes, bmoOrderType.getHasTaxes());
			if ((((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0)) {
				formFlexTable.addField(38, 0, defaultBudgetItemSuggestBox, bmoOrderType.getDefaultBudgetItemId());
				formFlexTable.addField(39, 0, defaultAreaSuggestBox, bmoOrderType.getDefaultAreaId());
			}

			statusEffect();
		}

		@Override
		public void formBooleanChange(ValueChangeEvent<Boolean> event) {
			// Notif. fecha fin de pedido
			if (event.getSource() == emailRemindersOrderEndCheckBox) {
				statusEffect();
			}
			// Pedidos extras
			if (event.getSource() == enableExtraOrderCheckBox) {
				statusEffect();
			}
			// Contratos
			if (event.getSource() == endContractCheckBox) {
				statusEffect();
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == typeListBox) {
				if (typeListBox.getSelectedCode().equals("" + BmoOrderType.TYPE_PROPERTY)) {
					populateWFlowTypes();
				}
				statusEffect();
			}
		}

		private void statusEffect() {
			if (emailRemindersOrderEndCheckBox.getValue() == true) {
				remindDaysBeforeEndOrderTextBox.setEnabled(true);
				remindDaysBeforeEndOrderTwoTextBox.setEnabled(true);

				profileUiSuggestBox.setEnabled(true);
			} else {
				remindDaysBeforeEndOrderTextBox.setValue("");
				remindDaysBeforeEndOrderTextBox.setEnabled(false);

				remindDaysBeforeEndOrderTwoTextBox.setValue("");
				remindDaysBeforeEndOrderTwoTextBox.setEnabled(false);

				profileUiSuggestBox.reset();
				profileUiSuggestBox.setEnabled(false);
			}
			if (enableExtraOrderCheckBox.getValue() == true) {
				wFlowTypeListBox.setEnabled(true);
			} else {
				wFlowTypeListBox.clear();
				wFlowTypeListBox.clearFilters();
				wFlowTypeListBox.populate("");
				wFlowTypeListBox.setEnabled(false);
			}
			if (endContractCheckBox.getValue() == true) {
				remindDaysBeforeEndDateTextBox.setEnabled(true);
				remindDaysBeforeEndDateTwoTextBox.setEnabled(true);

			}else {
				remindDaysBeforeEndDateTextBox.setEnabled(false);
				remindDaysBeforeEndDateTextBox.setValue("");
				remindDaysBeforeEndDateTwoTextBox.setEnabled(false);
				remindDaysBeforeEndDateTwoTextBox.setValue("");
			}

			if (typeListBox.getSelectedCode().equals(""+BmoOrderType.TYPE_LEASE)) {
				formFlexTable.showField(remindDaysBeforeEndOrderTwoTextBox);
				formFlexTable.showField(remindDaysBeforeRentIncreaseTextBox);
				formFlexTable.showField(remindDaysBeforeRentIncreaseTwoTextBox);
				formFlexTable.showField(endContractCheckBox);
				formFlexTable.showField(remindDaysBeforeEndDateTextBox);
				formFlexTable.showField(remindDaysBeforeEndDateTwoTextBox);
				formFlexTable.showField(remindDaysBeforeEndDateThreeTextBox);

				formFlexTable.hideField(dispersionProfileUiSuggestBox);
				formFlexTable.hideField(enableExtraOrderCheckBox);
				formFlexTable.hideField(wFlowTypeListBox);
			} else if(typeListBox.getSelectedCode().equals("" + BmoOrderType.TYPE_CREDIT)) {
				formFlexTable.showField(dispersionProfileUiSuggestBox);

				formFlexTable.hideField(remindDaysBeforeEndOrderTwoTextBox);
				formFlexTable.hideField(remindDaysBeforeRentIncreaseTextBox);
				formFlexTable.hideField(remindDaysBeforeRentIncreaseTwoTextBox);
				formFlexTable.hideField(remindDaysBeforeEndDateThreeTextBox);
				formFlexTable.hideField(endContractCheckBox);
				formFlexTable.hideField(remindDaysBeforeEndDateTextBox);
				formFlexTable.hideField(remindDaysBeforeEndDateTwoTextBox);
				formFlexTable.hideField(enableExtraOrderCheckBox);
				formFlexTable.hideField(wFlowTypeListBox);
				formFlexTable.hideField(remindDaysBeforeEndDateThreeTextBox);
				formFlexTable.hideField(requiredPropertyModelCheckBox);
				formFlexTable.hideField(defaultWFlowTypeListBox);
			} 
			else if(typeListBox.getSelectedCode().equals("" + BmoOrderType.TYPE_RENTAL)) {
				formFlexTable.showField(enableExtraOrderCheckBox);
				formFlexTable.showField(wFlowTypeListBox);

				formFlexTable.hideField(remindDaysBeforeEndOrderTwoTextBox);
				formFlexTable.hideField(dispersionProfileUiSuggestBox);
				formFlexTable.hideField(remindDaysBeforeRentIncreaseTextBox);
				formFlexTable.hideField(remindDaysBeforeRentIncreaseTwoTextBox);
				formFlexTable.hideField(endContractCheckBox);
				formFlexTable.hideField(remindDaysBeforeEndDateTextBox);
				formFlexTable.hideField(remindDaysBeforeEndDateTwoTextBox);
				formFlexTable.hideField(remindDaysBeforeEndDateThreeTextBox);
				formFlexTable.hideField(requiredPropertyModelCheckBox);
				formFlexTable.hideField(defaultWFlowTypeListBox);

			} else if(typeListBox.getSelectedCode().equals("" + BmoOrderType.TYPE_PROPERTY)) {
				formFlexTable.showField(requiredPropertyModelCheckBox);
				formFlexTable.showField(defaultWFlowTypeListBox);

			} else {
				formFlexTable.hideField(remindDaysBeforeEndOrderTwoTextBox);
				formFlexTable.hideField(dispersionProfileUiSuggestBox);
				formFlexTable.hideField(remindDaysBeforeRentIncreaseTextBox);
				formFlexTable.hideField(remindDaysBeforeRentIncreaseTwoTextBox);
				formFlexTable.hideField(endContractCheckBox);
				formFlexTable.hideField(remindDaysBeforeEndDateTextBox);
				formFlexTable.hideField(remindDaysBeforeEndDateTwoTextBox);
				formFlexTable.hideField(remindDaysBeforeEndDateThreeTextBox);
				formFlexTable.hideField(enableExtraOrderCheckBox);
				formFlexTable.hideField(wFlowTypeListBox);

				formFlexTable.hideField(requiredPropertyModelCheckBox);
				formFlexTable.hideField(defaultWFlowTypeListBox);
			}
		}


		// Actualiza combo de direcciones del cliente
		private void populateWFlowTypes() {
			defaultWFlowTypeListBox.clear();
			defaultWFlowTypeListBox.clearFilters();
			setWFlowTypesListBoxFilters();
			defaultWFlowTypeListBox.populate(bmoOrderType.getDefaultWFlowTypeId().toString());
		}

		// Asigna filtros al listado de direcciones del cliente
		private void setWFlowTypesListBoxFilters() {
			BmoPropertySale bmoPropertySale = new BmoPropertySale();

			if (getSFParams().hasRead(new BmoPropertySale().getProgramCode())) {
				// Agregar filtros al tipo de flujo
				BmoWFlowType bmoWFlowType = new BmoWFlowType();
				BmFilter bmFilter = new BmFilter();
				try {
					bmFilter.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), getSFParams().getProgramId(bmoPropertySale.getProgramCode()));
				} catch (SFException e) {
					showSystemMessage(this.getClass().getName() + "- setWFlowTypesListBoxFilters() Error: " + e.toString());
				}
				defaultWFlowTypeListBox.addBmFilter(bmFilter); 

				// Filtro de estatus
				BmFilter bmFilterByStatus = new BmFilter();
				bmFilterByStatus.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getStatus(), "" + BmoWFlowType.STATUS_ACTIVE);
				defaultWFlowTypeListBox.addBmFilter(bmFilterByStatus); 
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderType.setId(id);
			bmoOrderType.getName().setValue(nameTextBox.getText());
			bmoOrderType.getDescription().setValue(descriptionTextArea.getText());
			bmoOrderType.getType().setValue(typeListBox.getSelectedCode());
			bmoOrderType.getAutoRenew().setValue(autoRenewCheckBox.getValue());
			bmoOrderType.getEmailRemindersOrderStart().setValue(emailRemindersOrderStartCheckBox.getValue());
			bmoOrderType.getEmailRemindersOrderEnd().setValue(emailRemindersOrderEndCheckBox.getValue());
			bmoOrderType.getRemindDaysBeforeEndOrder().setValue(remindDaysBeforeEndOrderTextBox.getText());
			bmoOrderType.getProfileId().setValue(profileUiSuggestBox.getSelectedId());
			bmoOrderType.getEmailChangeStatusOppo().setValue(emailChangeStatusOppoCheckBox.getValue());
			bmoOrderType.getDispersionProfileId().setValue(dispersionProfileUiSuggestBox.getSelectedId());
			bmoOrderType.getEnableDeliverySend().setValue(enableSendDeliveryCheckBox.getValue());
			bmoOrderType.getEnableReqiOrderFinish().setValue(enableReqiOrderFinishCheckBox.getValue());
			bmoOrderType.getHasTaxes().setValue(hasTaxes.getValue());
			bmoOrderType.getDefaultBudgetItemId().setValue(defaultBudgetItemSuggestBox.getSelectedId());
			bmoOrderType.getDefaultAreaId().setValue(defaultAreaSuggestBox.getSelectedId());
			bmoOrderType.getRequiredLoseComments().setValue(requiredLoseComments.getValue());
			bmoOrderType.getEnableExtraOrder().setValue(enableExtraOrderCheckBox.getValue());
			bmoOrderType.getwFlowTypeId().setValue(wFlowTypeListBox.getSelectedId());
			bmoOrderType.getRemindDaysBeforeEndOrderTwo().setValue(remindDaysBeforeEndOrderTwoTextBox.getText());
			bmoOrderType.getRemindDaysBeforeRentIncrease().setValue(remindDaysBeforeRentIncreaseTextBox.getText());
			bmoOrderType.getRemindDaysBeforeRentIncreaseTwo().setValue(remindDaysBeforeRentIncreaseTwoTextBox.getText());
			bmoOrderType.getDataFiscal().setValue(dataFiscalCheckBox.getValue());
			bmoOrderType.getFilterOnScrum().setValue(filterOnScrumCheckBox.getValue());
			bmoOrderType.getRemindDaysBeforeEndContractDate().setValue(remindDaysBeforeEndDateTextBox.getText());
			bmoOrderType.getRemindDaysBeforeEndContractDateTwo().setValue(remindDaysBeforeEndDateTwoTextBox.getText());
			bmoOrderType.getEmailReminderContractEnd().setValue(endContractCheckBox.getValue());
			bmoOrderType.getAtmCCRevision().setValue(atmCCRevisionCheckBox.getValue());
			bmoOrderType.getAreaDefaultDetail().setValue(areaDefaultDetailUiList.getSelectedId());
			bmoOrderType.getStatusDefaultDetail().setValue(statusDefaultDetailUiList.getSelectedCode());
			bmoOrderType.getRemindDaysBeforeEndContractDateThree().setValue(remindDaysBeforeEndDateThreeTextBox.getValue());
//			bmoOrderType.getCreateProject().setValue(createProyectCheckBox.getValue());
			bmoOrderType.getRequiredPropertyModel().setValue(requiredPropertyModelCheckBox.getValue());
			bmoOrderType.getDefaultWFlowTypeId().setValue(defaultWFlowTypeListBox.getSelectedId());
			return bmoOrderType;
		}

		@Override
		public void close() {
			list();
		}
	}
}
