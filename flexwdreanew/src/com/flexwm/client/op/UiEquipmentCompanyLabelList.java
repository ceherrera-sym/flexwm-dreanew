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

import com.flexwm.shared.op.BmoEquipment;
import com.flexwm.shared.op.BmoEquipmentCompany;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;


public class UiEquipmentCompanyLabelList extends UiFormLabelList {
	BmoEquipmentCompany bmoEquipmentCompany;
	BmoEquipment bmoEquipment = new BmoEquipment();
	UiListBox equipmentCompanyListBox = new UiListBox(getUiParams(), new BmoCompany());
	int equipmentId;
	BmoCompany bmoCompany;

	public UiEquipmentCompanyLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoEquipmentCompany());
		bmoEquipmentCompany = (BmoEquipmentCompany)getBmObject();
		this.equipmentId = id;

		// Lista solo empresas del recurso seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoEquipmentCompany.getKind(), 
				bmoEquipmentCompany.getEquipmentId().getName(), 
				bmoEquipmentCompany.getEquipmentId().getLabel(), 
				BmFilter.EQUALS, 
				equipmentId,
				bmoEquipmentCompany.getEquipmentId().getName());

		// Preparar filtro para mostrar solo empresas NO previamente asignados a este recurso
		BmoCompany bmoCompany = new BmoCompany();

		BmFilter bmCompanyListFilter = new BmFilter();
		bmCompanyListFilter.setNotInFilter(bmoEquipmentCompany.getKind(), 
				bmoCompany.getIdFieldName(), 
				bmoEquipmentCompany.getCompanyId().getName(),
				bmoEquipmentCompany.getEquipmentId().getName(),
				"" + equipmentId
				);

		equipmentCompanyListBox.addFilter(bmCompanyListFilter);	 		
	}

	@Override
	public void populateFields(){
		bmoEquipmentCompany = (BmoEquipmentCompany)getBmObject();
		if (id > 0) {
			formFlexTable.addLabelCell(1, 0, bmoEquipmentCompany.getBmoCompany().getName().toString());
		} else {
			formFlexTable.addField(1, 0, equipmentCompanyListBox, bmoEquipmentCompany.getCompanyId());	
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoEquipmentCompany = new BmoEquipmentCompany();
		bmoEquipmentCompany.setId(id);
		bmoEquipmentCompany.getCompanyId().setValue(equipmentCompanyListBox.getSelectedId());
		bmoEquipmentCompany.getEquipmentId().setValue(equipmentId);		
		return bmoEquipmentCompany;
	}
}
