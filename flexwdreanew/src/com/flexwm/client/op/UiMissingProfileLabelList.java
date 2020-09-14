package com.flexwm.client.op;

import com.flexwm.shared.op.BmoMissingProfile;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;

public class UiMissingProfileLabelList extends UiFormLabelList {
	BmoMissingProfile bmoMissingProfile;
	UiListBox profileListBox = new UiListBox(getUiParams(),new BmoProfile());
	int orderTypeId;
	BmoProfile bmoProfile;
	
	public UiMissingProfileLabelList(UiParams uiParams,int id) {
		super(uiParams, new BmoMissingProfile());		
		bmoMissingProfile = (BmoMissingProfile)getBmObject();
		this.orderTypeId = id;
		
		forceFilter = new BmFilter();		
		forceFilter.setValueLabelFilter(bmoMissingProfile.getKind(), 
				bmoMissingProfile.getOrderTypeId().getName(), 
				bmoMissingProfile.getOrderTypeId().getLabel(), 
				BmFilter.EQUALS, 
				orderTypeId,
				bmoMissingProfile.getOrderTypeId().getName());
	}
	@Override
	public void populateFields() {
		bmoMissingProfile = (BmoMissingProfile)getBmObject();
		setProfileListBoxFilters(id);
		
		formFlexTable.addField(1, 0, profileListBox,bmoMissingProfile.getProfileId());
		
		// DesHabilitar seleccion si es un registro, es decir; no se puede cambiar
		if (id > 0) profileListBox.setEnabled(false);
		else profileListBox.setEnabled(true);
	}
	private void setProfileListBoxFilters(int idExist) {

		// Si es nuevo, asignar filtros, si no limpiarlos
		if (!(idExist > 0)) {
			// Preparar filtro para mostrar solo categorias NO previamente asignados a este registro
			BmoProfile bmoProfile = new BmoProfile();
			BmFilter bmoProfileListFilter = new BmFilter();
			bmoProfileListFilter.setNotInFilter(bmoMissingProfile.getKind(), 
					bmoProfile.getIdFieldName(), 
					bmoMissingProfile.getProfileId().getName(),
					bmoMissingProfile.getOrderTypeId().getName(),
					"" + orderTypeId
					);
			profileListBox = new UiListBox(getUiParams(), bmoProfile, bmoProfileListFilter);
		} else {
			profileListBox.clear();
			profileListBox.clearFilters();
		}
	}
	@Override
	public BmObject populateBObject() throws BmException {
		bmoMissingProfile = new BmoMissingProfile();
		bmoMissingProfile.setId(id);
		bmoMissingProfile.getProfileId().setValue(profileListBox.getSelectedId());		
		bmoMissingProfile.getOrderTypeId().setValue(orderTypeId);

		return bmoMissingProfile;
	}

}
