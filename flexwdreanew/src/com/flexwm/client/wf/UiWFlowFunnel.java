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

import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowStepType;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiWFlowFunnel extends UiList {

	public UiWFlowFunnel(UiParams uiParams) {
		super(uiParams, new BmoWFlowFunnel());
	}

	@Override
	public void create() {
		UiWFlowFunnelForm uiWFlowFunnelForm = new UiWFlowFunnelForm(getUiParams(), 0);
		uiWFlowFunnelForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowFunnelForm uiWFlowFunnelForm = new UiWFlowFunnelForm(getUiParams(), bmObject.getId());
		uiWFlowFunnelForm.show();		
	}

	public class UiWFlowFunnelForm extends UiFormDialog {

		TextBox sequenceTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox wFlowStepTypeListBox = new UiListBox(getUiParams(), new BmoWFlowStepType());
		UiListBox wFlowStepListBox = new UiListBox(getUiParams(), new BmoWFlowStep());

		BmoWFlowFunnel bmoWFlowFunnel;

		public UiWFlowFunnelForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowFunnel(), id);
		}

		@Override
		public void populateFields() {
			bmoWFlowFunnel = (BmoWFlowFunnel)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoWFlowFunnel.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoWFlowFunnel.getDescription());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowFunnel.setId(id);
			bmoWFlowFunnel.getName().setValue(nameTextBox.getText());
			bmoWFlowFunnel.getDescription().setValue(descriptionTextArea.getText());
			return bmoWFlowFunnel;
		}

		@Override
		public void close() {
			list();
		}
	}
}