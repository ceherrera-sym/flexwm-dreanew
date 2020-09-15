/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.client.cr;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cr.BmoCreditType;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoLocation;


/**
 * @author jhernandez
 *
 */

public class UiCreditTypeList extends UiList {
	BmoCreditType bmoCreditType;

	public UiCreditTypeList(UiParams uiParams) {
		super(uiParams, new BmoCreditType());
		bmoCreditType = (BmoCreditType)getBmObject();
	}

	@Override
	public void postShow( ){
		//addFilterListBox(new UiListBox(getUiParams(), new bmoCreditTypeGroup()), bmoCreditType.getbmoCreditTypeGroup());
	}

	@Override
	public void create() {
		UiCreditTypeForm uiTermForm = new UiCreditTypeForm(getUiParams(), 0);
		uiTermForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCreditTypeForm uiTermForm = new UiCreditTypeForm(getUiParams(), bmObject.getId());
		uiTermForm.show();
	}

	public class UiCreditTypeForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox deadLineTextBox = new TextBox();
		TextBox interestTextBox = new TextBox();
		TextBox creditLimitTextBox = new TextBox();
		TextBox guaranteesTextBox = new TextBox();
		TextBox failureTextBox = new TextBox();
		UiListBox typeListBox = new UiListBox(getUiParams());
		UiListBox locationIdListBox = new UiListBox(getUiParams());
		TextBox amountFailureTextBox = new TextBox();
		
		// Esta funcionalidad esta desarrollada para SOLO TIPO DIARIO (para daCredito), ya que cobi no usa creditos diarios
		// deberia estar en todos los tipos, pero por urgencia solo es tipo diario
		// Panel de Checkboxes
		FlowPanel paymentDayPanel = new FlowPanel();
		CheckBox paymentDayMondayCheckBox = new CheckBox();
		CheckBox paymentDayTuesdayCheckBox = new CheckBox();
		CheckBox paymentDayWednesdayCheckBox = new CheckBox();
		CheckBox paymentDayThursdayCheckBox = new CheckBox();
		CheckBox paymentDayFridayCheckBox = new CheckBox();
		CheckBox paymentDaySaturdayCheckBox = new CheckBox();
		CheckBox paymentDaySundayCheckBox = new CheckBox();

		BmoCreditType bmoCreditType;

		public UiCreditTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCreditType(), id);
		}

		@Override
		public void populateFields() {
			bmoCreditType = (BmoCreditType)getBmObject();
			
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() )
				locationIdListBox = new UiListBox(getUiParams(), new BmoLocation());
			
			if (newRecord) {
				try {
					if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
						bmoCreditType.getLocationId().setValue(getUiParams().getSFParams().getLoginInfo().getBmoUser().getLocationId().toInteger());
					}
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}			
			}
			
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() )
				formFlexTable.addField(1, 0, locationIdListBox, bmoCreditType.getLocationId());
			formFlexTable.addField(2, 0, nameTextBox, bmoCreditType.getName());
			formFlexTable.addField(3, 0, typeListBox, bmoCreditType.getType());
			formFlexTable.addField(4, 0, deadLineTextBox, bmoCreditType.getDeadLine());
			formFlexTable.addField(5, 0, descriptionTextArea, bmoCreditType.getDescription());
			formFlexTable.addField(6, 0, creditLimitTextBox, bmoCreditType.getCreditLimit());
			formFlexTable.addField(7, 0, interestTextBox, bmoCreditType.getInterest());
			formFlexTable.addField(8, 0, guaranteesTextBox, bmoCreditType.getGuarantees());
			formFlexTable.addField(9, 0, failureTextBox, bmoCreditType.getFailure());
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() )
				formFlexTable.addField(10, 0, amountFailureTextBox, bmoCreditType.getAmountFailure());
				
			paymentDayPanel.setWidth("100%");
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDayMondayCheckBox, bmoCreditType.getPaymentDayMonday()));
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDayTuesdayCheckBox, bmoCreditType.getPaymentDayTuesday()));
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDayWednesdayCheckBox, bmoCreditType.getPaymentDayWednesday()));
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDayThursdayCheckBox, bmoCreditType.getPaymentDayThursday()));
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDayFridayCheckBox, bmoCreditType.getPaymentDayFriday()));
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDaySaturdayCheckBox, bmoCreditType.getPaymentDaySaturday()));
			paymentDayPanel.add(formFlexTable.getCheckBoxPanel(paymentDaySundayCheckBox, bmoCreditType.getPaymentDaySunday()));
			formFlexTable.addLabelCell(11, 0, "Días de cobro:");//
			formFlexTable.addPanel(11, 1, paymentDayPanel, 1);	
				
			statusEffect();
		}
		
		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == typeListBox) {
				showPaymentDays();
			}
		}
		
		public void statusEffect() {
			showPaymentDays();
			
			// Validar si tiene permiso de propios, en la ubicacion
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean()) {
				if (getSFParams().restrictData(new BmoLocation().getProgramCode())) {
					locationIdListBox.setEnabled(false);
				} else {
					locationIdListBox.setEnabled(true);
				}
				
				if (getSFParams().hasSpecialAccess(BmoCreditType.ACCESS_CHANGEAMOUNTFAILURE)) {
					amountFailureTextBox.setEnabled(true);
				} else {
					amountFailureTextBox.setEnabled(false);
				}
			}
		}
		
		public void showPaymentDays() {
			if (typeListBox.getSelectedCode().equalsIgnoreCase("" + BmoCreditType.TYPE_DAILY)) {
				formFlexTable.getCellFormatter().setVisible(11, 0, true);
				formFlexTable.getCellFormatter().setVisible(11, 1, true);
			} else {
				formFlexTable.getCellFormatter().setVisible(11, 0, false);
				formFlexTable.getCellFormatter().setVisible(11, 1, false);
			}
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCreditType.setId(id);
			bmoCreditType.getName().setValue(nameTextBox.getText());
			bmoCreditType.getDescription().setValue(descriptionTextArea.getText());
			bmoCreditType.getDeadLine().setValue(deadLineTextBox.getText());		
			bmoCreditType.getInterest().setValue(interestTextBox.getText());		
			bmoCreditType.getType().setValue(typeListBox.getSelectedCode());
			bmoCreditType.getCreditLimit().setValue(creditLimitTextBox.getText());
			bmoCreditType.getGuarantees().setValue(guaranteesTextBox.getText());
			bmoCreditType.getFailure().setValue(failureTextBox.getText());
			if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCreditByLocation().toBoolean() ) {
				bmoCreditType.getLocationId().setValue(locationIdListBox.getSelectedId());
				bmoCreditType.getAmountFailure().setValue(amountFailureTextBox.getText());
			}
			bmoCreditType.getPaymentDayMonday().setValue(paymentDayMondayCheckBox.getValue());
			bmoCreditType.getPaymentDayTuesday().setValue(paymentDayTuesdayCheckBox.getValue());
			bmoCreditType.getPaymentDayWednesday().setValue(paymentDayWednesdayCheckBox.getValue());
			bmoCreditType.getPaymentDayThursday().setValue(paymentDayThursdayCheckBox.getValue());
			bmoCreditType.getPaymentDayFriday().setValue(paymentDayFridayCheckBox.getValue());
			bmoCreditType.getPaymentDaySaturday().setValue(paymentDaySaturdayCheckBox.getValue());
			bmoCreditType.getPaymentDaySunday().setValue(paymentDaySundayCheckBox.getValue());
			
			return bmoCreditType;
		}
	}
}
