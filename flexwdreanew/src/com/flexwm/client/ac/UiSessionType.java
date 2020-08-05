/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoSessionDiscipline;
import com.flexwm.shared.ac.BmoSessionType;
import com.flexwm.shared.ac.BmoSessionTypeExtra;
import com.flexwm.shared.ac.BmoSessionTypePackage;
import com.flexwm.shared.fi.BmoCurrency;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiSessionType extends UiList {

	public UiSessionType(UiParams uiParams) {
		super(uiParams, new BmoSessionType());
	}

	@Override
	public void create() {
		UiSessionTypeForm uiSessionTypeForm = new UiSessionTypeForm(getUiParams(), 0);
		uiSessionTypeForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiSessionTypeForm uiSessionTypeForm = new UiSessionTypeForm(getUiParams(), bmObject.getId());
		uiSessionTypeForm.show();
	}

	public class UiSessionTypeForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox durationTextBox = new TextBox();
		TextBox capacityTextBox = new TextBox();
		UiListBox sessionDisciplineListBox = new UiListBox(getUiParams(), new BmoSessionDiscipline());
		CheckBox gCalendarSyncCheckBox = new CheckBox();
		TextBox gCalendarIdTextBox = new TextBox();	
		UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());

		BmoSessionType bmoSessionType;
		SessionTypeUpdater sessionTypeUpdater = new SessionTypeUpdater();

		String generalSection = "Datos Generales";
		String itemSection = "Items";

		public UiSessionTypeForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSessionType(), id);
		}

		@Override
		public void populateFields() {
			bmoSessionType = (BmoSessionType)getBmObject();

			// Si no esta asignada la moneda, buscar por la default
			if (!(bmoSessionType.getCurrencyId().toInteger() > 0)) {
				try {
					bmoSessionType.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
				} catch (BmException e) {
					showSystemMessage("No se puede asignar moneda : " + e.toString());
				}
			}

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(2, 0, nameTextBox, bmoSessionType.getName());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoSessionType.getDescription());
			formFlexTable.addField(4, 0, sessionDisciplineListBox, bmoSessionType.getSessionDisciplineId());
			formFlexTable.addField(5, 0, durationTextBox, bmoSessionType.getDuration());
			formFlexTable.addField(6, 0, capacityTextBox, bmoSessionType.getCapacity());
			formFlexTable.addField(7, 0, gCalendarSyncCheckBox, bmoSessionType.getgCalendarSync());
			formFlexTable.addField(8, 0, gCalendarIdTextBox, bmoSessionType.getgCalendarId());
			formFlexTable.addField(9, 0, currencyListBox, bmoSessionType.getCurrencyId());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(10, 0, itemSection, 2);
				BmoSessionTypePackage bmoSessionTypePackage = new BmoSessionTypePackage();
				FlowPanel sessionTypePackageFP = new FlowPanel();
				BmFilter filterSessionTypePackage = new BmFilter();
				filterSessionTypePackage.setValueFilter(bmoSessionTypePackage.getKind(), bmoSessionTypePackage.getSessionTypeId(), bmoSessionType.getId());
				getUiParams().setForceFilter(bmoSessionTypePackage.getProgramCode(), filterSessionTypePackage);
				UiSessionTypePackage uiSessionTypePackage = new UiSessionTypePackage(getUiParams(), sessionTypePackageFP, bmoSessionType, bmoSessionType.getId(), sessionTypeUpdater);
				setUiType(bmoSessionTypePackage.getProgramCode(), UiParams.MINIMALIST);
				uiSessionTypePackage.show();
				formFlexTable.addPanel(11, 0, sessionTypePackageFP, 2);

				BmoSessionTypeExtra bmoSessionTypeExtra = new BmoSessionTypeExtra();
				FlowPanel sessionTypeExtraFP = new FlowPanel();
				BmFilter filterSessionTypeExtra = new BmFilter();
				filterSessionTypeExtra.setValueFilter(bmoSessionTypeExtra.getKind(), bmoSessionTypeExtra.getSessionTypeId(), bmoSessionType.getId());
				getUiParams().setForceFilter(bmoSessionTypeExtra.getProgramCode(), filterSessionTypeExtra);
				UiSessionTypeExtra uiSessionTypeExtra = new UiSessionTypeExtra(getUiParams(), sessionTypeExtraFP, bmoSessionType, bmoSessionType.getId(), sessionTypeUpdater);
				setUiType(bmoSessionTypeExtra.getProgramCode(), UiParams.MINIMALIST);
				uiSessionTypeExtra.show();
				formFlexTable.addPanel(12, 0, sessionTypeExtraFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSessionType.setId(id);
			bmoSessionType.getName().setValue(nameTextBox.getText());
			bmoSessionType.getDescription().setValue(descriptionTextArea.getText());
			bmoSessionType.getDuration().setValue(durationTextBox.getText());
			bmoSessionType.getCapacity().setValue(capacityTextBox.getText());
			bmoSessionType.getSessionDisciplineId().setValue(sessionDisciplineListBox.getSelectedId());
			bmoSessionType.getgCalendarSync().setValue(gCalendarSyncCheckBox.getValue());
			bmoSessionType.getgCalendarId().setValue(gCalendarIdTextBox.getText());
			bmoSessionType.getCurrencyId().setValue(currencyListBox.getSelectedId());

			return bmoSessionType;
		}

		@Override
		public void close() {
			UiSessionType uiSessionTypeList = new UiSessionType(getUiParams());
			uiSessionTypeList.show();
		}

		public class SessionTypeUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}