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

import com.flexwm.shared.op.BmoOrderDetail;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;


public class UiOrderDetailForm extends UiFormDialog {
	BmoOrderDetail bmoOrderDetail;
	BmoOrder bmoOrder;

	UiListBox statusListBox = new UiListBox(getUiParams());
	UiDateBox closeDateBox = new UiDateBox();
	UiDateBox orderDateBox = new UiDateBox();
	UiDateBox desiredDateBox = new UiDateBox();
	UiDateBox startDateBox = new UiDateBox();
	UiDateBox deliveryDateBox = new UiDateBox();
	UiListBox areaListBox = new UiListBox(getUiParams(), new BmoArea());
	UiSuggestBox leaderUserIdSuggestBox = new UiSuggestBox(new BmoUser());
	UiSuggestBox assignedUserIdSuggestBox = new UiSuggestBox(new BmoUser());

	public UiOrderDetailForm(UiParams uiParams, int id, BmoOrder bmoOrder) {
		super(uiParams, new BmoOrderDetail(), id);
		bmoOrderDetail = (BmoOrderDetail)getBmObject();
		super.foreignId = bmoOrder.getId();
		this.bmoOrder = bmoOrder;
		super.foreignField = bmoOrderDetail.getOrderId().getName();
		setUiType(UiParams.SINGLESLAVE);
		initialize();
	}
	
	public void initialize() {
		// Filtrar por usuarios activos
		BmoUser bmoUser = new BmoUser();

		// Responsable
		BmFilter filterLeaderUserActive = new BmFilter();
		filterLeaderUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		leaderUserIdSuggestBox.addFilter(filterLeaderUserActive);
		
		// Consultor
		BmFilter filterAssignedUserActive = new BmFilter();
		filterAssignedUserActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		assignedUserIdSuggestBox.addFilter(filterAssignedUserActive);
	}
	
	@Override
	public void populateFields() {
		bmoOrderDetail = (BmoOrderDetail)getBmObject();

		formFlexTable.addField(1, 0, areaListBox, bmoOrderDetail.getAreaId());
		formFlexTable.addField(2, 0, leaderUserIdSuggestBox, bmoOrderDetail.getLeaderUserId());
		formFlexTable.addField(3, 0, assignedUserIdSuggestBox, bmoOrderDetail.getAssignedUserId());
		formFlexTable.addField(4, 0, statusListBox, bmoOrderDetail.getStatus());
		formFlexTable.addField(5, 0, closeDateBox, bmoOrderDetail.getCloseDate());
		formFlexTable.addField(6, 0, orderDateBox, bmoOrderDetail.getOrderDate());
		formFlexTable.addField(7, 0, desiredDateBox, bmoOrderDetail.getDesireDate());
		formFlexTable.addField(8, 0, startDateBox, bmoOrderDetail.getStartDate());
		formFlexTable.addField(9, 0, deliveryDateBox, bmoOrderDetail.getDeliveryDate());

		statusEffect();
	}

	@Override
	public void postShow() {
		deleteButton.setVisible(false);
	}

	public void statusEffect() {
		areaListBox.setEnabled(false);
		leaderUserIdSuggestBox.setEnabled(false);
		assignedUserIdSuggestBox.setEnabled(false);
		statusListBox.setEnabled(false);
		// no tocar, asi se quedan
		closeDateBox.setEnabled(false);
		orderDateBox.setEnabled(false);

		desiredDateBox.setEnabled(false);
		startDateBox.setEnabled(false);
		deliveryDateBox.setEnabled(false);

		// Si no hay responsable habilitar campos
		if (!(bmoOrderDetail.getLeaderUserId().toInteger() > 0)) {
			areaListBox.setEnabled(true);
			leaderUserIdSuggestBox.setEnabled(true);
			assignedUserIdSuggestBox.setEnabled(true);
			statusListBox.setEnabled(true);
		} else {
			// Si es el Responsable o tienes permiso para cambiar datos se habilitan campos(dpto., responsable, consultor)
			if (bmoOrderDetail.getLeaderUserId().toInteger() == getSFParams().getLoginInfo().getUserId()
					|| getSFParams().hasSpecialAccess(BmoOrderDetail.ACCESS_CHANGEDATA) ) {
				areaListBox.setEnabled(true);
				leaderUserIdSuggestBox.setEnabled(true);
				assignedUserIdSuggestBox.setEnabled(true);
			}
		}

		// Permiso Cambiar Fecha Deseada
		if (getSFParams().hasSpecialAccess(BmoOrderDetail.ACCESS_CHANGEDESIREDATE)) 
			desiredDateBox.setEnabled(true);

		// Permiso Cambiar Fecha Inicio
		if (getSFParams().hasSpecialAccess(BmoOrderDetail.ACCESS_CHANGESTARTDATE)) 
			startDateBox.setEnabled(true);	

		// Permiso Cambiar Fecha Pactada
		if (getSFParams().hasSpecialAccess(BmoOrderDetail.ACCESS_CHANGEDELIVERYDATE)) 
			deliveryDateBox.setEnabled(true);

		// Permiso Cambiar Status
		if (getSFParams().hasSpecialAccess(BmoOrderDetail.ACCESS_CHANGESTATUS)) 
			statusListBox.setEnabled(true);
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrderDetail.setId(id);
		bmoOrderDetail.getOrderId().setValue(bmoOrder.getId());
		bmoOrderDetail.getAreaId().setValue(areaListBox.getSelectedId());
		bmoOrderDetail.getLeaderUserId().setValue(leaderUserIdSuggestBox.getSelectedId());
		bmoOrderDetail.getAssignedUserId().setValue(assignedUserIdSuggestBox.getSelectedId());
		bmoOrderDetail.getStatus().setValue(statusListBox.getSelectedCode());
		bmoOrderDetail.getCloseDate().setValue(closeDateBox.getTextBox().getText());
		bmoOrderDetail.getOrderDate().setValue(orderDateBox.getTextBox().getText());
		bmoOrderDetail.getDesireDate().setValue(desiredDateBox.getTextBox().getText());
		bmoOrderDetail.getStartDate().setValue(startDateBox.getTextBox().getText());
		bmoOrderDetail.getDeliveryDate().setValue(deliveryDateBox.getTextBox().getText());

		return bmoOrderDetail;
	}

	@Override
	public void close() {
		dialogClose();
	}
}
