/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.cv.BmoPosition;
import com.flexwm.shared.cv.BmoPositionSkill;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


/**
 * @author smuniz
 *
 */

public class UiPosition extends UiList {
	BmoPosition bmoPosition;

	public UiPosition(UiParams uiParams) {
		super(uiParams, new BmoPosition());
		bmoPosition = (BmoPosition)getBmObject();
	}
	
	@Override
	public void postShow(){
		//
	}
	
	@Override
	public void create() {
		UiPositionForm uiPositionForm = new UiPositionForm(getUiParams(), 0);
		uiPositionForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		UiPositionForm uiPositionForm = new UiPositionForm(getUiParams(), bmObject.getId());
		uiPositionForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {
		UiPositionForm uiPositionForm = new UiPositionForm(getUiParams(), bmObject.getId());
		uiPositionForm.show();
	}
	
	public class UiPositionForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextArea objectiveTextArea = new TextArea();
		CheckBox isActivedCheckBox = new CheckBox();
		TextArea experienceTextArea = new TextArea();
		
		BmoPosition bmoPosition;
		String itemSection = "Items";
		PositionUpdater positionUpdater = new PositionUpdater();

		public UiPositionForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPosition(), id); 
		}
		
		@Override
		public void populateFields(){
			bmoPosition = (BmoPosition)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoPosition.getName());
			formFlexTable.addField(2, 0, isActivedCheckBox, bmoPosition.getIsActived());
			formFlexTable.addField(3, 0, objectiveTextArea, bmoPosition.getObjective());
			formFlexTable.addField(4, 0, descriptionTextArea, bmoPosition.getDescription());
			formFlexTable.addField(5, 0, experienceTextArea, bmoPosition.getExperience());
			

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(6, 0, itemSection, 2);
				BmoPositionSkill bmoPositionSkill = new BmoPositionSkill();
				FlowPanel positionSkillFP = new FlowPanel();
				BmFilter filterPositionSkill = new BmFilter();
				filterPositionSkill.setValueFilter(bmoPositionSkill.getKind(), bmoPositionSkill.getPositionId(), bmoPosition.getId());
				getUiParams().setForceFilter(bmoPositionSkill.getProgramCode(), filterPositionSkill);
				UiPositionSkill uiPositionSkill = new UiPositionSkill(getUiParams(), positionSkillFP, bmoPosition, bmoPosition.getId(), positionUpdater);
				setUiType(bmoPositionSkill.getProgramCode(), UiParams.MINIMALIST);
				uiPositionSkill.show();
				formFlexTable.addPanel(7, 0, positionSkillFP, 2);
			}
		}
		
		@Override
		public BmObject populateBObject() throws BmException {
			bmoPosition.setId(id);
			bmoPosition.getName().setValue(nameTextBox.getText());
			bmoPosition.getIsActived().setValue(isActivedCheckBox.getValue());
			bmoPosition.getObjective().setValue(objectiveTextArea.getText());
			bmoPosition.getDescription().setValue(descriptionTextArea.getText());
			bmoPosition.getExperience().setValue(experienceTextArea.getText());
			return bmoPosition;
		}
		
		@Override
		public void close() {
			list();
		}
		
		public class PositionUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}

}
