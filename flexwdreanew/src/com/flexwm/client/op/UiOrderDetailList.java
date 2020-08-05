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
import com.symgae.shared.BmObject;
import com.flexwm.shared.op.BmoOrderDetail;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiOrderDetailList extends UiList {
	BmoOrderDetail bmoOrderDetail;
	
	private String newTitle = "";

	public UiOrderDetailList(UiParams uiParams) {
		super(uiParams, new BmoOrderDetail());
	}
	
	public UiOrderDetailList(UiParams uiParams, Panel defaultPanel, String title) {
		super(uiParams, defaultPanel, new BmoOrderDetail());
		bmoOrderDetail = (BmoOrderDetail)getBmObject();
		newTitle = title;
	}
	
	@Override
	public void postShow(){ 
		titleLabel.setText(newTitle);
		newImage.setVisible(false);
		searchTextBox.setVisible(false);
		searchImage.setVisible(false);
		allImage.setVisible(false);
		securityImage.setVisible(false);
	}

	@Override
	public void create() {
		UiOrderDetailForm uiOrderDetailForm = new UiOrderDetailForm(getUiParams(), 0);
		uiOrderDetailForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiOrderDetailForm uiOrderDetailForm = new UiOrderDetailForm(getUiParams(), bmObject.getId());
		uiOrderDetailForm.show();
	}

	public class UiOrderDetailForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		BmoOrderDetail bmoOrderDetail;

		public UiOrderDetailForm(UiParams uiParams, int id) {
			super(uiParams, new BmoOrderDetail(), id);
		}

		@Override
		public void populateFields() {
			bmoOrderDetail = (BmoOrderDetail)getBmObject();
			formFlexTable.addLabelField(1, 0, bmoOrderDetail.getBmoOrder().getCode());
			formFlexTable.addLabelField(2, 0, bmoOrderDetail.getBmoOrder().getName());
			formFlexTable.addLabelField(3, 0, bmoOrderDetail.getBmoOrder().getDescription());
			formFlexTable.addLabelField(4, 0, bmoOrderDetail.getBmoOrder().getBmoCustomer().getDisplayName());
			formFlexTable.addLabelField(5, 0, bmoOrderDetail.getBmoOrder().getBmoWFlowType().getName());
			formFlexTable.addLabelField(6, 0, bmoOrderDetail.getBmoOrder().getTotal());
			formFlexTable.addLabelField(7, 0, bmoOrderDetail.getStatus());
		}
		
		@Override
		public void postShow() {
			saveButton.setVisible(false);
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoOrderDetail.setId(id);

			return bmoOrderDetail;
		}

		@Override
		public void close() {
			list();
		}
	}
}
