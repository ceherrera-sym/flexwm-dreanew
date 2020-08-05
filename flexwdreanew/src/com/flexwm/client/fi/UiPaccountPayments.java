/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;

import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;


public class UiPaccountPayments extends UiList {
	BmoPaccount bmoPaccount;

	public UiPaccountPayments(UiParams uiParams) {
		super(uiParams, new BmoPaccount());
		this.bmoPaccount = (BmoPaccount)getBmObject();		
	}

	@Override
	public void postShow() {
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getBmoPaccountType().getType(), "" + BmoPaccountType.TYPE_DEPOSIT);
		getUiParams().getUiProgramParams(bmoPaccount.getProgramCode()).addFilter(bmFilter);
	}

	@Override
	public void create() {
		UiPaccount uiPaccountForm = new UiPaccount(getUiParams());
		uiPaccountForm.create();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoPaccount = (BmoPaccount)bmObject;
		UiPaccount uiPaccountForm = new UiPaccount(getUiParams());
		uiPaccountForm.open(bmoPaccount);
	}	
}
