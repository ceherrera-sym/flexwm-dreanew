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

import com.flexwm.client.op.UiWhBox.UiWhBoxForm.WhBoxUpdater;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhBoxTrack;
import com.flexwm.shared.op.BmoWhTrack;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiWhBoxTrack extends UiList {

	BmoWhBoxTrack bmoWhBoxTrack;
	BmoWhBox bmoWhBox;
	protected WhBoxUpdater whBoxUpdater;
	int whBoxId;

	public UiWhBoxTrack(UiParams uiParams, Panel defaultPanel, BmoWhBox bmoWhBox, int whBoxId, WhBoxUpdater whBoxUpdater) {
		super(uiParams, defaultPanel, new BmoWhBoxTrack());
		bmoWhBoxTrack = new BmoWhBoxTrack();
		this.whBoxId = whBoxId;
		this.whBoxUpdater = whBoxUpdater;
		this.bmoWhBox = bmoWhBox;
	}

	@Override
	public void create() {
		UiWhBoxTrackForm uiWhBoxTrackForm = new UiWhBoxTrackForm(getUiParams(), 0);
		uiWhBoxTrackForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiWhBoxTrackForm uiWhBoxTrackForm = new UiWhBoxTrackForm(getUiParams(), bmObject.getId());
		uiWhBoxTrackForm.show();
	}

	private class UiWhBoxTrackForm extends UiFormDialog {

		TextBox quantityTextBox = new TextBox();
		UiSuggestBox trackSuggestBox = new UiSuggestBox(new BmoWhTrack());

		public UiWhBoxTrackForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWhBoxTrack(), id);
		}

		@Override
		public void populateFields() {
			bmoWhBoxTrack = (BmoWhBoxTrack)getBmObject();
			formFlexTable.addField(1, 0, trackSuggestBox, bmoWhBoxTrack.getWhTrackId());
			formFlexTable.addField(2, 0, quantityTextBox, bmoWhBoxTrack.getQuantity());

			trackTypeEffect(bmoWhBoxTrack.getBmoWhTrack().getBmoProduct());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoWhBoxTrack = new BmoWhBoxTrack();
			bmoWhBoxTrack.setId(id);
			bmoWhBoxTrack.getQuantity().setValue(quantityTextBox.getText());
			bmoWhBoxTrack.getWhBoxId().setValue(whBoxId);	
			bmoWhBoxTrack.getWhTrackId().setValue(trackSuggestBox.getSelectedId());
			return bmoWhBoxTrack;
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			if (trackSuggestBox.getSelectedId() > 0) {
				BmoWhTrack bmoWhTrack = (BmoWhTrack)trackSuggestBox.getSelectedBmObject();	
				trackTypeEffect(bmoWhTrack.getBmoProduct());
			}
		}

		private void trackTypeEffect(BmoProduct bmoProduct) {
			if (bmoProduct.getTrack().equals(BmoProduct.TRACK_SERIAL)) {
				quantityTextBox.setText("1");
				quantityTextBox.setEnabled(false);
			} else {
				quantityTextBox.setEnabled(true);
			}
		}
		
		@Override
		public void close() {
			list();
			
			if (whBoxUpdater != null)
				whBoxUpdater.update();
		}
	}
}
