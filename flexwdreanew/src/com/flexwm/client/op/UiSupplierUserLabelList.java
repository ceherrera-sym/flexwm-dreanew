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

import com.flexwm.shared.op.BmoSupplierUser;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoUser;


public class UiSupplierUserLabelList extends UiFormLabelList {

	BmoSupplierUser bmoSupplierUser;
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	int supplierId;

	public UiSupplierUserLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoSupplierUser());
		this.supplierId = id;
		bmoSupplierUser = new BmoSupplierUser();
		forceFilter = new BmFilter();

		forceFilter.setValueLabelFilter(bmoSupplierUser.getKind(), 
				bmoSupplierUser.getSupplierId().getName(), 
				bmoSupplierUser.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				id,
				bmoSupplierUser.getSupplierId().getName()
				);

	}

	@Override
	public void populateFields() {
		bmoSupplierUser = (BmoSupplierUser)getBmObject();
		// Filtrar por usuarios activos
		BmoUser bmoUser = new BmoUser();
		BmFilter filterUsersActive = new BmFilter();
		filterUsersActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		userSuggestBox.addFilter(filterUsersActive);

		formFlexTable.addField(1, 0, userSuggestBox, bmoSupplierUser.getUserId());
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierUser = new BmoSupplierUser();
		bmoSupplierUser.setId(id);
		bmoSupplierUser.getUserId().setValue(userSuggestBox.getSelectedId());
		bmoSupplierUser.getSupplierId().setValue(supplierId);

		return bmoSupplierUser;
	}
}
