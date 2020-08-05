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

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.client.ui.fields.UiTextBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoProjectEquipment;




public class UiProjectEquipment extends UiList {	
	BmoProjectEquipment bmoProjectEquipment;
	BmoProject bmoProject;
	BmoEquipment bmoEquipment;


	public UiProjectEquipment(UiParams uiParams, Panel defaultPanel, BmoProject bmoProject) {
		super(uiParams, defaultPanel, new BmoProjectEquipment());
		bmoProjectEquipment = (BmoProjectEquipment)getBmObject();
		this.bmoProject = bmoProject;

	}

	@Override
	public void create() {
		UiProjectEquipmentForm uiProjectEquipmentForm = new UiProjectEquipmentForm(getUiParams(), 0);
		uiProjectEquipmentForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		bmoProjectEquipment = (BmoProjectEquipment)bmObject;
		UiProjectEquipmentForm uiProjectEquipmentForm = new UiProjectEquipmentForm(getUiParams(), bmoProjectEquipment.getId());
		uiProjectEquipmentForm.show();}

	@Override
	public void edit(BmObject bmObject) {
		UiProjectEquipmentForm uiProjectEquipmentForm = new UiProjectEquipmentForm(getUiParams(), bmObject.getId());
		uiProjectEquipmentForm.show();
	}
	private class UiProjectEquipmentForm extends UiFormDialog {
		UiTextBox codeTextBox = new UiTextBox();	
		UiTextBox nameTextBox = new UiTextBox();	
		TextArea descriptionTextArea = new TextArea();
		UiDateTimeBox endDateDateTimeBox = new UiDateTimeBox();
		UiDateTimeBox startDateDateTimeBox = new UiDateTimeBox();
		UiSuggestBox equipmentSuggestBox = new UiSuggestBox(new BmoEquipment());
		
		public UiProjectEquipmentForm(UiParams uiParams, int id) {
			super(uiParams, new BmoProjectEquipment(), id);
			bmoProjectEquipment = (BmoProjectEquipment)getBmObject();
			
			if(newRecord) {
			 try {
				 
				bmoProjectEquipment.getStartDate().setValue(bmoProject.getStartDate().toString());
				 bmoProjectEquipment.getStartDate().setValue(bmoProject.getEndDate().toString());		
			} catch (BmException e) {
				showErrorMessage("Error al asignar fechas");
				
			}				
			 }else{
				 equipmentSuggestBox.setEnabled(false);
				 codeTextBox.setEnabled(false);
				 nameTextBox.setEnabled(false);
			 }
		}
		
		@Override
		public void populateFields(){			
			bmoProjectEquipment = (BmoProjectEquipment)getBmObject();
			formFlexTable.addField(1, 0, equipmentSuggestBox, bmoProjectEquipment.getEquipmentId());
			formFlexTable.addField(2, 0, codeTextBox, bmoProjectEquipment.getCode());
			formFlexTable.addField(3, 0, nameTextBox, bmoProjectEquipment.getName());	
			formFlexTable.addField(4, 0, descriptionTextArea, bmoProjectEquipment.getDescription());
			formFlexTable.addField(5, 0, startDateDateTimeBox, bmoProjectEquipment.getStartDate());
			formFlexTable.addField(6, 0, endDateDateTimeBox, bmoProjectEquipment.getEndDate());
			if(newRecord) {				 
				formFlexTable.addField(5, 0, startDateDateTimeBox, bmoProject.getStartDate());
				formFlexTable.addField(6, 0, endDateDateTimeBox, bmoProject.getEndDate());

			}
		}	

		@Override
		//manda los datos reales al pm
		public BmObject populateBObject() throws BmException {	
			bmoProjectEquipment.setId(id);
			bmoProjectEquipment.getProjectId().setValue(bmoProject.getId());
			bmoProjectEquipment.getCode().setValue(codeTextBox.getText());
			bmoProjectEquipment.getDescription().setValue(descriptionTextArea.getText());
			bmoProjectEquipment.getName().setValue(nameTextBox.getText());
			bmoProjectEquipment.getEquipmentId().setValue(equipmentSuggestBox.getSelectedId());		
			bmoProjectEquipment.getStartDate().setValue(startDateDateTimeBox.getDateTime());
			bmoProjectEquipment.getEndDate().setValue(endDateDateTimeBox.getDateTime());					
			return bmoProjectEquipment;
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			

			if (uiSuggestBox == equipmentSuggestBox) {
				// Permitir elegir direccion del cliente
				if (equipmentSuggestBox.getSelectedId() > 0) {
					BmoEquipment bmoEquipment = new BmoEquipment();
					bmoEquipment = (BmoEquipment)equipmentSuggestBox.getSelectedBmObject();				
					if (bmoEquipment != null) {
						codeTextBox.setText(bmoEquipment.getCode().toString());
						nameTextBox.setText(bmoEquipment.getName().toString());
						descriptionTextArea.setText(bmoEquipment.getDescription().toString());
						nameTextBox.setEnabled(false);
						codeTextBox.setEnabled(false);
						descriptionTextArea.setEnabled(true);
					}else {
						nameTextBox.setText("");
						codeTextBox.setText("");
						descriptionTextArea.setText("");
						nameTextBox.setEnabled(false);
						codeTextBox.setEnabled(false);
						descriptionTextArea.setEnabled(true);
					}
				}else {
					nameTextBox.setText("");
					codeTextBox.setText("");
					descriptionTextArea.setText("");
				}
			}  				
		}
		
		@Override
		public void close() {
			list();
		}
	}
}