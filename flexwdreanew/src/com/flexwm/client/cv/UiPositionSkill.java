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

import com.flexwm.client.cv.UiPosition.UiPositionForm.PositionUpdater;
import com.flexwm.shared.cv.BmoPosition;
import com.flexwm.shared.cv.BmoPositionSkill;
import com.flexwm.shared.cv.BmoSkill;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;

/**
 * @author smuniz
 *
 */

public class UiPositionSkill extends UiList {
	int positionId;
	BmoPositionSkill bmoPositionSkill;
	BmoPosition bmoPosition;
	protected PositionUpdater positionUpdater;

	public UiPositionSkill(UiParams uiParams, Panel defaultPanel, BmoPosition bmoPosition, int positionId, PositionUpdater positionUpdater) {
		super(uiParams, defaultPanel, new BmoPositionSkill());
		bmoPositionSkill = (BmoPositionSkill)getBmObject();
		this.positionId = positionId;
		this.bmoPosition = bmoPosition;
		this.positionUpdater = positionUpdater;
	}

	@Override
	public void create() {
		UiPositionSkillForm uiPositionSkillForm = new UiPositionSkillForm(getUiParams(), 0);
		uiPositionSkillForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiPositionSkillForm uiPositionSkillForm = new UiPositionSkillForm(getUiParams(), bmObject.getId());
		uiPositionSkillForm.show();
	}

	private class UiPositionSkillForm extends UiFormDialog {
		UiSuggestBox skillIdUiSuggestBox = new UiSuggestBox(new BmoSkill());

		public UiPositionSkillForm(UiParams uiParams, int id) {
			super(uiParams, new BmoPositionSkill(), id);
		}

		@Override
		public void populateFields() {
			bmoPositionSkill = (BmoPositionSkill)getBmObject();
			formFlexTable.addField(1, 0, skillIdUiSuggestBox, bmoPositionSkill.getSkillId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoPositionSkill.setId(id);
			bmoPositionSkill.getSkillId().setValue(skillIdUiSuggestBox.getSelectedId());
			if(positionId > 0) bmoPositionSkill.getPositionId().setValue(positionId);

			return bmoPositionSkill;
		}
		
		@Override
		public void close() {
			list();

			if (positionUpdater != null)
				positionUpdater.update();
		}
	}
}