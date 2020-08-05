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

import com.flexwm.shared.op.BmoOrderTypeWFlowCategory;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.flexwm.shared.wf.BmoWFlowCategory;


public class UiOrderTypeWFlowCategoryLabelList extends UiFormLabelList {
	BmoOrderTypeWFlowCategory bmoOrderTypeWFlowCategory;
	UiListBox wFlowCategoryListBox =  new UiListBox(getUiParams(), new BmoWFlowCategory());
	int orderTypeId;
	BmoWFlowCategory bmowFlowCategory;

	public UiOrderTypeWFlowCategoryLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoOrderTypeWFlowCategory());
		bmoOrderTypeWFlowCategory = (BmoOrderTypeWFlowCategory)getBmObject();
		this.orderTypeId = id;

		// Lista solo categorias de flujo del tipo de pedido seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoOrderTypeWFlowCategory.getKind(), 
				bmoOrderTypeWFlowCategory.getOrderTypeId().getName(), 
				bmoOrderTypeWFlowCategory.getOrderTypeId().getLabel(), 
				BmFilter.EQUALS, 
				orderTypeId,
				bmoOrderTypeWFlowCategory.getOrderTypeId().getName());
	}

	@Override
	public void populateFields() {
		bmoOrderTypeWFlowCategory = (BmoOrderTypeWFlowCategory)getBmObject();

		setWFlowCategoryListBoxFilters(id);
		formFlexTable.addField(1, 0, wFlowCategoryListBox, bmoOrderTypeWFlowCategory.getWFlowCategoryId());

		// DesHabilitar seleccion si es un registro, es decir; no se puede cambiar
		if (id > 0) wFlowCategoryListBox.setEnabled(false);
		else wFlowCategoryListBox.setEnabled(true);
	}

	// Asigna filtros al listado de empresa
	private void setWFlowCategoryListBoxFilters(int idExist) {

		// Si es nuevo, asignar filtros, si no limpiarlos
		if (!(idExist > 0)) {
			// Preparar filtro para mostrar solo categorias NO previamente asignados a este registro
			BmoWFlowCategory bmowFlowCategory = new BmoWFlowCategory();
			BmFilter bmwFlowCategoryListFilter = new BmFilter();
			bmwFlowCategoryListFilter.setNotInFilter(bmoOrderTypeWFlowCategory.getKind(), 
					bmowFlowCategory.getIdFieldName(), 
					bmoOrderTypeWFlowCategory.getWFlowCategoryId().getName(),
					bmoOrderTypeWFlowCategory.getOrderTypeId().getName(),
					"" + orderTypeId
					);
			wFlowCategoryListBox = new UiListBox(getUiParams(), bmowFlowCategory, bmwFlowCategoryListFilter);
		} else {
			wFlowCategoryListBox.clear();
			wFlowCategoryListBox.clearFilters();
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoOrderTypeWFlowCategory = new BmoOrderTypeWFlowCategory();
		bmoOrderTypeWFlowCategory.setId(id);
		bmoOrderTypeWFlowCategory.getWFlowCategoryId().setValue(wFlowCategoryListBox.getSelectedId());		
		bmoOrderTypeWFlowCategory.getOrderTypeId().setValue(orderTypeId);

		return bmoOrderTypeWFlowCategory;
	}
}
