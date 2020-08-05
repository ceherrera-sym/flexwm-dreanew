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
import com.flexwm.shared.op.BmoEquipmentService;
import com.flexwm.shared.op.BmoWhTrack;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmObject;


public class UiEquipmentService extends UiList {
	BmoEquipmentService bmoEquipmentService;
	BmoEquipment bmoEquipment;
	BmoWhTrack bmoWhTrack;
	protected EquipmentUpdater equipmentUpdater;
	protected WhTrackUpdater whTrackUpdater;
	int equipmentId;
	int whTrackId;

	public UiEquipmentService(UiParams uiParams, Panel defaultPanel, BmoEquipment bmoEquipment, int equipmentId, EquipmentUpdater equipmentUpdater) {
		super(uiParams, defaultPanel, new BmoEquipmentService());
		bmoEquipmentService = (BmoEquipmentService)getBmObject();
		this.bmoEquipment = bmoEquipment;
		this.equipmentUpdater = equipmentUpdater;
		this.equipmentId = equipmentId;
	}

	public UiEquipmentService(UiParams uiParams, Panel defaultPanel, BmoWhTrack bmoWhTrack, int whTrackId, WhTrackUpdater whTrackUpdater) {
		super(uiParams, defaultPanel, new BmoEquipmentService());
		bmoEquipmentService = (BmoEquipmentService)getBmObject();
		this.bmoWhTrack = bmoWhTrack;
		this.whTrackUpdater = whTrackUpdater;
		this.whTrackId = whTrackId;
	}

	@Override
	public void create() {
		UiEquipmentServiceForm uiEquipmentServiceForm = new UiEquipmentServiceForm(getUiParams(), 0);
		uiEquipmentServiceForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiEquipmentServiceForm uiEquipmentServiceForm = new UiEquipmentServiceForm(getUiParams(), bmObject.getId());
		uiEquipmentServiceForm.show();
	}

	private class UiEquipmentServiceForm extends UiFormDialog {
		UiDateBox dateBox = new UiDateBox();
		UiDateBox nextDateBox = new UiDateBox();
		UiDateBox liberateDateBox = new UiDateBox();
		TextBox nextMeterTextBox = new TextBox();
		UiListBox statusUiListBox = new UiListBox(getUiParams());
		TextBox costTextBox = new TextBox();
		TextArea commentsTextArea = new TextArea();

		public UiEquipmentServiceForm(UiParams uiParams, int id) {
			super(uiParams, new BmoEquipmentService(), id);
		}

		@Override
		public void populateFields() {
			bmoEquipmentService = (BmoEquipmentService)getBmObject();
			formFlexTable.addField(1, 0, dateBox, bmoEquipmentService.getDate());
			formFlexTable.addField(2, 0, nextDateBox, bmoEquipmentService.getNextDate());
			formFlexTable.addField(3, 0, liberateDateBox,bmoEquipmentService.getLiberateDate());
			//formFlexTable.addField(4, 0, nextMeterTextBox, bmoEquipmentService.getNextMeter());
			formFlexTable.addField(5, 0, costTextBox, bmoEquipmentService.getCost());
			formFlexTable.addField(6, 0, commentsTextArea, bmoEquipmentService.getComments());
			formFlexTable.addField(7, 0, statusUiListBox, bmoEquipmentService.getStatus());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoEquipmentService = new BmoEquipmentService();
			bmoEquipmentService.setId(id);
			bmoEquipmentService.getDate().setValue(dateBox.getTextBox().getText());
			bmoEquipmentService.getNextDate().setValue(nextDateBox.getTextBox().getText());
			bmoEquipmentService.getNextMeter().setValue(nextMeterTextBox.getText());
			bmoEquipmentService.getStatus().setValue(statusUiListBox.getSelectedCode());
			bmoEquipmentService.getComments().setValue(commentsTextArea.getText());
			bmoEquipmentService.getCost().setValue(costTextBox.getText());
			bmoEquipmentService.getLiberateDate().setValue(liberateDateBox.getTextBox().getText());
			if (equipmentId > 0) bmoEquipmentService.getEquipmentId().setValue(equipmentId);
			if (whTrackId > 0) bmoEquipmentService.getWhTrackId().setValue(whTrackId);	
			return bmoEquipmentService;
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
