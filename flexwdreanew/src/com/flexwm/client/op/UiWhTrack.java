package com.flexwm.client.op;

import com.flexwm.shared.op.BmoEquipmentService;
import com.flexwm.shared.op.BmoEquipmentUse;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhTrack;
import com.google.gwt.user.client.ui.FlowPanel;
import com.symgae.client.ui.UiDateTimeBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiWhTrack extends UiList {
	BmoWhTrack bmoWhTrack;

	public UiWhTrack(UiParams uiParams) {
		super(uiParams, new BmoWhTrack());
		bmoWhTrack = (BmoWhTrack)getBmObject();
	}

	@Override
	public void create() {
		UiWhTrackForm uiWhTrackForm = new UiWhTrackForm(getUiParams(), 0);
		uiWhTrackForm.show();
	}

	@Override
	public void postShow() {
		newImage.setVisible(false);
		deleteImage.setVisible(false);
		addFilterSuggestBox(new UiSuggestBox(new BmoProduct()), new BmoProduct(), bmoWhTrack.getProductId());
	}

	@Override
	public void open(BmObject bmObject) {
		UiWhTrackForm uiWhTrackForm = new UiWhTrackForm(getUiParams(), bmObject.getId());
		uiWhTrackForm.show();
	}

	public class UiWhTrackForm extends UiFormDialog {
		UiDateTimeBox dateMovDateTimeBox = new UiDateTimeBox();
		UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
		BmoWhTrack bmoWhTrack;
		WhTrackUpdater whTrackUpdater = new WhTrackUpdater();

		String itemsSection = "Items";

		public UiWhTrackForm(UiParams uiParams, int id) {
			super(uiParams, new BmoWhTrack(), id);
		}

		@Override
		public void populateFields() {
			bmoWhTrack = (BmoWhTrack)getBmObject();
			formFlexTable.addField(1, 0, productSuggestBox, bmoWhTrack.getProductId());
			formFlexTable.addLabelField(2, 0, bmoWhTrack.getSerial());		
			formFlexTable.addLabelField(3, 0, bmoWhTrack.getInQuantity());
			formFlexTable.addLabelField(4, 0, bmoWhTrack.getDatemov());
			formFlexTable.addLabelField(5, 0, bmoWhTrack.getOutQuantity());

			if (!newRecord && bmoWhTrack.getBmoProduct().getTrack().toChar() == BmoProduct.TRACK_SERIAL) {

				formFlexTable.addSectionLabel(6, 0, itemsSection, 2);
				// Operacion
				BmoEquipmentUse bmoEquipmentUse = new BmoEquipmentUse();
				FlowPanel equipmentUseFP = new FlowPanel();
				BmFilter filterEquipmentUse = new BmFilter();
				filterEquipmentUse.setValueFilter(bmoEquipmentUse.getKind(), bmoEquipmentUse.getWhTrackId(), bmoWhTrack.getId());
				getUiParams().setForceFilter(bmoEquipmentUse.getProgramCode(), filterEquipmentUse);
				UiEquipmentUse uiEquipmentUse = new UiEquipmentUse(getUiParams(), equipmentUseFP, bmoWhTrack, bmoWhTrack.getId(), whTrackUpdater);
				setUiType(bmoEquipmentUse.getProgramCode(), UiParams.MINIMALIST);
				uiEquipmentUse.show();
				formFlexTable.addPanel(7, 0, equipmentUseFP, 2);


				// Mantenimiento
				BmoEquipmentService bmoEquipmentService = new BmoEquipmentService();
				FlowPanel equipmentServiceFP = new FlowPanel();
				BmFilter filterEquipmentService  = new BmFilter();
				filterEquipmentService.setValueFilter(bmoEquipmentService.getKind(), bmoEquipmentService.getWhTrackId(), bmoWhTrack.getId());
				getUiParams().setForceFilter(bmoEquipmentService.getProgramCode(), filterEquipmentService);
				UiEquipmentService uiEquipmentService = new UiEquipmentService(getUiParams(), equipmentServiceFP, bmoWhTrack, bmoWhTrack.getId(), whTrackUpdater);
				setUiType(bmoEquipmentService.getProgramCode(), UiParams.MINIMALIST);
				uiEquipmentService.show();
				formFlexTable.addPanel(8, 0, equipmentServiceFP, 2);
			}
		}

		@Override
		public void postShow() {
			saveButton.setVisible(false);
			deleteButton.setVisible(false);

			productSuggestBox.setEnabled(false);
			dateMovDateTimeBox.setEnabled(false);
		}

		@Override
		public void close() {
			UiWhTrack uiWhTrackList = new UiWhTrack(getUiParams());
			uiWhTrackList.show();
		}

		public class WhTrackUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}
