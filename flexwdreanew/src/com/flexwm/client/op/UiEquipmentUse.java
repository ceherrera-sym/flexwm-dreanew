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

import com.flexwm.client.op.UiEquipment.UiEquipmentForm.EquipmentUpdater;
import com.flexwm.client.op.UiWhTrack.UiWhTrackForm.WhTrackUpdater;
import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentUse;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoWhTrack;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiEquipmentUse extends UiList {
	BmoEquipmentUse bmoEquipmentUse;
	BmoEquipment bmoEquipment;
	BmoWhTrack bmoWhTrack;

	int equipmentId;
	int whTrackId;
	protected EquipmentUpdater equipmentUpdater;
	protected WhTrackUpdater whTrackUpdater;

	public UiEquipmentUse(UiParams uiParams, Panel defaultPanel, BmoEquipment bmoEquipment, int equipmentId, EquipmentUpdater equipmentUpdater) {
		super(uiParams, defaultPanel, new BmoEquipmentUse());
		bmoEquipmentUse = (BmoEquipmentUse)getBmObject();
		this.bmoEquipment = bmoEquipment;
		this.equipmentUpdater = equipmentUpdater;
		this.equipmentId = equipmentId;
	}

	public UiEquipmentUse(UiParams uiParams, Panel defaultPanel, BmoWhTrack bmoWhTrack, int whTrackId, WhTrackUpdater whTrackUpdater) {
		super(uiParams, defaultPanel, new BmoEquipmentUse());
		bmoEquipmentUse = (BmoEquipmentUse)getBmObject();
		this.bmoWhTrack = bmoWhTrack;
		this.whTrackUpdater = whTrackUpdater;
		this.whTrackId = whTrackId;
	}

	@Override
	public void create() {
		UiEquipmentUseForm uiEquipmentUseForm = new UiEquipmentUseForm(getUiParams(), 0);
		uiEquipmentUseForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiEquipmentUseForm uiEquipmentUseForm = new UiEquipmentUseForm(getUiParams(), bmObject.getId());
		uiEquipmentUseForm.show();
	}

	private class UiEquipmentUseForm extends UiFormDialog {

		private UiDateTimeBox dateTimeBox = new UiDateTimeBox();
		private UiDateTimeBox dateTimeInBox = new UiDateTimeBox();
		private TextBox meterTextBox = new TextBox();
		private TextBox meterInTextBox = new TextBox();
		private TextBox fuelTextBox = new TextBox();
		private TextBox fuelInTextBox = new TextBox();
		private TextBox fuelPriceTextBox = new TextBox();
		private TextArea commentsTextArea = new TextArea();
		private UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());

		public UiEquipmentUseForm(UiParams uiParams, int id) {
			super(uiParams, new BmoEquipmentUse(), id);
		}

		@Override
		public void populateFields() {
			bmoEquipmentUse = (BmoEquipmentUse)getBmObject();
			formFlexTable.addField(1, 0, dateTimeBox, bmoEquipmentUse.getDateTime());
			formFlexTable.addField(2, 0, dateTimeInBox, bmoEquipmentUse.getDateTimeIn());		
			formFlexTable.addField(3, 0, meterTextBox, bmoEquipmentUse.getMeter());
			formFlexTable.addField(4, 0, meterInTextBox, bmoEquipmentUse.getMeterIn());		
			formFlexTable.addField(5, 0, fuelTextBox, bmoEquipmentUse.getFuel());
			formFlexTable.addField(6, 0, fuelInTextBox, bmoEquipmentUse.getFuelIn());
			formFlexTable.addField(7, 0, commentsTextArea, bmoEquipmentUse.getComments());
			formFlexTable.addField(8, 0, fuelPriceTextBox, bmoEquipmentUse.getFuelPrice());
			formFlexTable.addField(9, 0, orderSuggestBox, bmoEquipmentUse.getOrderId());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoEquipmentUse.setId(id);
			bmoEquipmentUse.getDateTime().setValue(dateTimeBox.getDateTime());
			bmoEquipmentUse.getDateTimeIn().setValue(dateTimeInBox.getDateTime());
			bmoEquipmentUse.getMeter().setValue(meterTextBox.getText());
			bmoEquipmentUse.getMeterIn().setValue(meterInTextBox.getText());
			bmoEquipmentUse.getFuel().setValue(fuelTextBox.getText());
			bmoEquipmentUse.getFuelIn().setValue(fuelInTextBox.getText());
			bmoEquipmentUse.getFuelPrice().setValue(fuelPriceTextBox.getText());
			bmoEquipmentUse.getOrderId().setValue(orderSuggestBox.getSelectedId());
			bmoEquipmentUse.getComments().setValue(commentsTextArea.getText());
			if (equipmentId > 0) bmoEquipmentUse.getEquipmentId().setValue(equipmentId);		
			if (whTrackId > 0) bmoEquipmentUse.getWhTrackId().setValue(whTrackId);		
			return bmoEquipmentUse;
		}

		@Override
		public void close() {
			list();

			if (equipmentId > 0) {
				if (equipmentUpdater != null)
					equipmentUpdater.update();
			}
			if (whTrackId > 0) {
				if (whTrackUpdater != null)
					whTrackUpdater.update();
			}

		}
	}
}
