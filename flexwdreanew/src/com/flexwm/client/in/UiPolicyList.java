/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.in;


import com.flexwm.shared.in.BmoPolicy;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;

public class UiPolicyList extends UiList {
	BmoPolicy bmoPolicy;

	public UiPolicyList(UiParams uiParams) {
		super(uiParams, new BmoPolicy());
		bmoPolicy = (BmoPolicy)getBmObject();
	}
	
	public UiPolicyList(UiParams uiParams, Panel defaultPanel) {
		super(uiParams, defaultPanel, new BmoPolicy());
		bmoPolicy = (BmoPolicy)getBmObject();
	}
	
	@Override
	public void postShow() {
		addFilterListBox(new UiListBox(getUiParams(), new BmoUser()), bmoPolicy.getBmoUser());		
		addStaticFilterListBox(new UiListBox(getUiParams(), bmoPolicy.getStatus()), bmoPolicy, bmoPolicy.getStatus());
	}
	
	@Override
	public void create() {
		UiPolicyForm uiPolicyForm = new UiPolicyForm(getUiParams(), 0);
		uiPolicyForm.show();
	}
	
	@Override
	public void open(BmObject bmObject) {
		bmoPolicy = (BmoPolicy)bmObject;
		// Si esta asignado el tipo de proyecto, envia directo al dashboard
		if (bmoPolicy.getWFlowTypeId().toInteger() > 0) {
			UiPolicyDetail uiPolicyDetail = new UiPolicyDetail(getUiParams(), bmoPolicy.getId());
			uiPolicyDetail.show();
		} else {
			UiPolicyForm uiPolicyForm = new UiPolicyForm(getUiParams(), bmoPolicy.getId());
			uiPolicyForm.show();
		}
	}
}


