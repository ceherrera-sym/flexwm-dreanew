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
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowUserSelect;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;


public class UiWFlowUserSelect extends UiList {
	BmoWFlowUserSelect bmoWFlowUserSelect;

	public UiWFlowUserSelect(UiParams uiParams) {
		super(uiParams, new BmoWFlowUserSelect());
		bmoWFlowUserSelect = (BmoWFlowUserSelect)getBmObject();
	}

	@Override
	public void create() {
		UiWFlowUserSelectForm uiWFlowUserSelectForm = new UiWFlowUserSelectForm(getUiParams(), 0);
		uiWFlowUserSelectForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWFlowUserSelectForm uiWFlowUserSelectForm = new UiWFlowUserSelectForm(getUiParams(), bmObject.getId());
		uiWFlowUserSelectForm.show();		
	}

	public class UiWFlowUserSelectForm extends UiFormDialog {
		UiListBox areaListBox = new UiListBox(getUiParams(), new BmoArea());
		UiListBox profileListBox = new UiListBox(getUiParams(), new BmoProfile());
		BmoWFlowUserSelect bmoWFlowUserSelect;


		public UiWFlowUserSelectForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWFlowUserSelect(), id);
		}

		@Override
		public void populateFields(){
			bmoWFlowUserSelect = (BmoWFlowUserSelect)getBmObject();

			formFlexTable.addField(1, 0, areaListBox, bmoWFlowUserSelect.getAreaId());
			formFlexTable.addField(2, 0, profileListBox, bmoWFlowUserSelect.getProfileId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWFlowUserSelect.setId(id);
			bmoWFlowUserSelect.getAreaId().setValue(areaListBox.getSelectedId());
			bmoWFlowUserSelect.getProfileId().setValue(profileListBox.getSelectedId());

			return bmoWFlowUserSelect;
		}

		@Override
		public void close() {
			list();
		}
	}
}