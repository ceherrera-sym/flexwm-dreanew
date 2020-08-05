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
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentService;
import com.flexwm.shared.op.BmoEquipmentType;
import com.flexwm.shared.op.BmoEquipmentUse;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


public class UiEquipment extends UiList {
	BmoEquipment bmoEquipment;

	public UiEquipment(UiParams uiParams) {
		super(uiParams, new BmoEquipment());
		bmoEquipment = (BmoEquipment)getBmObject();
	}

	public void postShow() {
		if (isMaster()) {
			addFilterListBox(new UiListBox(getUiParams(), new BmoEquipmentType()), new BmoEquipmentType());
			addFilterSuggestBox(new UiSuggestBox(new BmoUser()), new BmoUser(), bmoEquipment.getUserId());
			addStaticFilterListBox(new UiListBox(getUiParams(), bmoEquipment.getStatus()), bmoEquipment, bmoEquipment.getStatus());
		}
	}

	@Override
	public void create() {
		UiEquipmentForm uiEquipmentForm = new UiEquipmentForm(getUiParams(), 0);
		uiEquipmentForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoEquipment = (BmoEquipment)bmObject;
		UiEquipmentForm uiEquipmentForm = new UiEquipmentForm(getUiParams(), bmoEquipment.getId());
		uiEquipmentForm.show();
	}
	@Override
	public void edit(BmObject bmObject) {
		UiEquipmentForm uiEquipmentForm = new UiEquipmentForm(getUiParams(), bmObject.getId());
		uiEquipmentForm.show();
	}

	public class UiEquipmentForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox brandTextBox = new TextBox();
		TextBox modelTextBox = new TextBox();
		TextBox yearTextBox = new TextBox();
		TextBox colorTextBox = new TextBox();
		TextBox priceTextBox = new TextBox();
		TextBox costTextBox = new TextBox();
		UiListBox statusListBox = new UiListBox(getUiParams());
		UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
		UiListBox equipmentTypeListBox = new UiListBox(getUiParams(), new BmoEquipmentType());
		BmoEquipment bmoEquipment;

		String generalSection = "Datos Generales";
		String specificationsSection = "Especificaciones";
		String itemSection = "Items";

		EquipmentUpdater equipmentUpdater = new EquipmentUpdater();

		public UiEquipmentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoEquipment(), id);
		}

		@Override
		public void populateFields(){
			bmoEquipment = (BmoEquipment)getBmObject();

			//Mostrar usuarios Activos
			BmoUser bmoUser = new BmoUser();
			BmFilter bmFilterUsersActives = new BmFilter();
			bmFilterUsersActives.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(bmFilterUsersActives);

			formFlexTable.addSectionLabel(1, 0, generalSection, 2);
			formFlexTable.addField(2, 0, codeTextBox, bmoEquipment.getCode());
			formFlexTable.addField(3, 0, equipmentTypeListBox, bmoEquipment.getEquipmentTypeId());
			formFlexTable.addField(4, 0, nameTextBox, bmoEquipment.getName());
			formFlexTable.addField(5, 0, descriptionTextArea, bmoEquipment.getDescription());
			formFlexTable.addField(6, 0, priceTextBox, bmoEquipment.getPrice());
			formFlexTable.addField(7, 0, costTextBox, bmoEquipment.getCost());
			formFlexTable.addField(8, 0, userSuggestBox, bmoEquipment.getUserId());
			formFlexTable.addField(9, 0, statusListBox, bmoEquipment.getStatus());

			formFlexTable.addSectionLabel(10, 0, specificationsSection, 2);
			formFlexTable.addField(11, 0, brandTextBox, bmoEquipment.getBrand());
			formFlexTable.addField(12, 0, modelTextBox, bmoEquipment.getModel());
			formFlexTable.addField(12, 0, yearTextBox, bmoEquipment.getYear());
			formFlexTable.addField(14, 0, colorTextBox, bmoEquipment.getColor());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(15, 0, itemSection, 2);

				// Empresas
				formFlexTable.addField(16, 0, new UiEquipmentCompanyLabelList(getUiParams(), id));

				// Operacion
				BmoEquipmentUse bmoEquipmentUse = new BmoEquipmentUse();
				FlowPanel equipmentUseFP = new FlowPanel();
				BmFilter filterEquipmentUse = new BmFilter();
				filterEquipmentUse.setValueFilter(bmoEquipmentUse.getKind(), bmoEquipmentUse.getEquipmentId(), bmoEquipment.getId());
				getUiParams().setForceFilter(bmoEquipmentUse.getProgramCode(), filterEquipmentUse);
				UiEquipmentUse uiEquipmentUse = new UiEquipmentUse(getUiParams(), equipmentUseFP, bmoEquipment, bmoEquipment.getId(), equipmentUpdater);
				setUiType(bmoEquipmentUse.getProgramCode(), UiParams.MINIMALIST);
				uiEquipmentUse.show();
				formFlexTable.addPanel(17, 0, equipmentUseFP, 2);

				// Mantenimiento
				BmoEquipmentService bmoEquipmentService = new BmoEquipmentService();
				FlowPanel equipmentServiceFP = new FlowPanel();
				BmFilter filterEquipmentService  = new BmFilter();
				filterEquipmentService.setValueFilter(bmoEquipmentService.getKind(), bmoEquipmentService.getEquipmentId(), bmoEquipment.getId());
				getUiParams().setForceFilter(bmoEquipmentService.getProgramCode(), filterEquipmentService);
				UiEquipmentService uiEquipmentService = new UiEquipmentService(getUiParams(), equipmentServiceFP, bmoEquipment, bmoEquipment.getId(), equipmentUpdater);
				setUiType(bmoEquipmentService.getProgramCode(), UiParams.MINIMALIST);
				uiEquipmentService.show();
				formFlexTable.addPanel(18, 0, equipmentServiceFP, 2);
			}

			if (!newRecord)
				formFlexTable.hideSection(specificationsSection);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoEquipment.setId(id);
			bmoEquipment.getCode().setValue(codeTextBox.getText());
			bmoEquipment.getName().setValue(nameTextBox.getText());
			bmoEquipment.getDescription().setValue(descriptionTextArea.getText());
			bmoEquipment.getBrand().setValue(brandTextBox.getText());
			bmoEquipment.getModel().setValue(modelTextBox.getText());
			bmoEquipment.getYear().setValue(yearTextBox.getText());
			bmoEquipment.getColor().setValue(colorTextBox.getText());
			bmoEquipment.getPrice().setValue(priceTextBox.getText());
			bmoEquipment.getCost().setValue(costTextBox.getText());
			bmoEquipment.getStatus().setValue(statusListBox.getSelectedCode());

			bmoEquipment.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoEquipment.getEquipmentTypeId().setValue(equipmentTypeListBox.getSelectedId());
			return bmoEquipment;
		}

		@Override
		public void close() {
			list();
		}

		@Override
		public void saveNext() {
			if (newRecord) { 
				UiEquipmentForm uiEquipmentForm = new UiEquipmentForm(getUiParams(), getBmObject().getId());
				uiEquipmentForm.show();
			} else {
				UiEquipment uiEquipmentList = new UiEquipment(getUiParams());
				uiEquipmentList.show();
			}		
		}

		public class EquipmentUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}
