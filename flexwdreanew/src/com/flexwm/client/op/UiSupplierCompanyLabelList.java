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

import com.flexwm.shared.op.BmoSupplierCompany;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;


public class UiSupplierCompanyLabelList extends UiFormLabelList {

	BmoSupplierCompany bmoSupplierCompany;
	UiListBox companyListBox;
	int supplierId;
	BmoCompany bmoCompany;

	public UiSupplierCompanyLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoSupplierCompany());
		bmoSupplierCompany = new BmoSupplierCompany();
		this.supplierId = id;

		// Lista solo grupos del usuario seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoSupplierCompany.getKind(), 
				bmoSupplierCompany.getSupplierId().getName(), 
				bmoSupplierCompany.getSupplierId().getLabel(), 
				BmFilter.EQUALS, 
				supplierId,
				bmoSupplierCompany.getSupplierId().getName());

		// Preparar filtro para mostrar solo grupos NO previamente asignados a este proveedor
		BmoCompany bmoCompany = new BmoCompany();
		BmFilter bmCompanyListFilter = new BmFilter();
		bmCompanyListFilter.setNotInFilter(bmoSupplierCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoSupplierCompany.getCompanyId().getName(),
				bmoSupplierCompany.getSupplierId().getName(),
				"" + supplierId
				);
		companyListBox = new UiListBox(getUiParams(), bmoCompany, bmCompanyListFilter);
	}

	@Override
	public void populateFields() {
		bmoSupplierCompany = (BmoSupplierCompany)getBmObject();
		if (id > 0) {
			formFlexTable.addLabelCell(1, 0, bmoSupplierCompany.getBmoCompany().getName().toString());
		} else {
			formFlexTable.addField(1, 0, companyListBox, bmoSupplierCompany.getCompanyId());	
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoSupplierCompany = new BmoSupplierCompany();
		bmoSupplierCompany.setId(id);
		bmoSupplierCompany.getCompanyId().setValue(companyListBox.getSelectedId());
		bmoSupplierCompany.getSupplierId().setValue(supplierId);		
		return bmoSupplierCompany;
	}
}
