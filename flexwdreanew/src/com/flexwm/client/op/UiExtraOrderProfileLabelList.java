package com.flexwm.client.op;

import com.flexwm.shared.op.BmoExtraOrderProfile;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoProfile;

public class UiExtraOrderProfileLabelList  extends UiFormLabelList  {
	BmoExtraOrderProfile bmoExtraOrderProfile;
	UiListBox profileListBox = new UiListBox(getUiParams(),new BmoProfile());
	int orderTypeId;
	BmoProfile bmoProfile;
	
	public UiExtraOrderProfileLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoExtraOrderProfile());
		bmoExtraOrderProfile = (BmoExtraOrderProfile)getBmObject();
		this.orderTypeId = id;
		
		// Lista solo categorias de flujo del tipo de pedido seleccionado
		forceFilter = new BmFilter();		
		forceFilter.setValueLabelFilter(bmoExtraOrderProfile.getKind(), 
				bmoExtraOrderProfile.getOrderTypeId().getName(), 
				bmoExtraOrderProfile.getOrderTypeId().getLabel(), 
				BmFilter.EQUALS, 
				orderTypeId,
				bmoExtraOrderProfile.getOrderTypeId().getName());
	}
	@Override
	public void populateFields() {
		bmoExtraOrderProfile = (BmoExtraOrderProfile)getBmObject();
		setProfileListBoxFilters(id);
		
		formFlexTable.addField(1, 0, profileListBox,bmoExtraOrderProfile.getProfileId());
		
		// DesHabilitar seleccion si es un registro, es decir; no se puede cambiar
				if (id > 0) profileListBox.setEnabled(false);
				else profileListBox.setEnabled(true);
	}

	// Asigna filtros al listado de empresa
	private void setProfileListBoxFilters(int idExist) {

		// Si es nuevo, asignar filtros, si no limpiarlos
		if (!(idExist > 0)) {
			// Preparar filtro para mostrar solo categorias NO previamente asignados a este registro
			BmoProfile bmoProfile = new BmoProfile();
			BmFilter bmoProfileListFilter = new BmFilter();
			bmoProfileListFilter.setNotInFilter(bmoExtraOrderProfile.getKind(), 
					bmoProfile.getIdFieldName(), 
					bmoExtraOrderProfile.getProfileId().getName(),
					bmoExtraOrderProfile.getOrderTypeId().getName(),
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
		bmoExtraOrderProfile = new BmoExtraOrderProfile();
		bmoExtraOrderProfile.setId(id);
		bmoExtraOrderProfile.getProfileId().setValue(profileListBox.getSelectedId());		
		bmoExtraOrderProfile.getOrderTypeId().setValue(orderTypeId);

		return bmoExtraOrderProfile;
	}

}
