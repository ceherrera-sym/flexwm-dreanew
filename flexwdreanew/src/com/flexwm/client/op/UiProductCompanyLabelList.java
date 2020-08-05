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

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoProductCompany;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiFormLabelList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;


public class UiProductCompanyLabelList extends UiFormLabelList {
	BmoProductCompany bmoProductCompany;
	UiListBox companyListBox =  new UiListBox(getUiParams(), new BmoCompany());
	int productId;
	BmoCompany bmoCompany;
	UiSuggestBox budgetItemUiSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	UiListBox areaUiListBox = new UiListBox(getUiParams(), new BmoArea());
	String budgetItemSection = "Datos Control Prespuestal";

	public UiProductCompanyLabelList(UiParams uiParams, int id) {
		super(uiParams, new BmoProductCompany());
		bmoProductCompany = (BmoProductCompany)getBmObject();
		this.productId = id;

		// Lista solo grupos del usuario seleccionado
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoProductCompany.getKind(), 
				bmoProductCompany.getProductId().getName(), 
				bmoProductCompany.getProductId().getLabel(), 
				BmFilter.EQUALS, 
				productId,
				bmoProductCompany.getProductId().getName());
	}

	@Override
	public void populateFields() {
		bmoProductCompany = (BmoProductCompany)getBmObject();

		setCompanyListBoxFilters(id);
		formFlexTable.addField(1, 0, companyListBox, bmoProductCompany.getCompanyId());
		// Llenar partidas con la empresa seleccionada
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0))  {
			populateBudgetItems(Integer.parseInt(companyListBox.getSelectedId()));
			formFlexTable.addField(2, 0, budgetItemUiSuggestBox, bmoProductCompany.getBudgetItemId());
			formFlexTable.addField(3, 0, areaUiListBox, bmoProductCompany.getAreaId());
		}

		// DesHabilitar empresa si es un registro, es decir; no se puede cambiar la empresa
		if (id > 0) companyListBox.setEnabled(false);
		else companyListBox.setEnabled(true);
	}

	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == companyListBox) {
			// Llenar partidas por la empresa
			populateBudgetItems(Integer.parseInt(companyListBox.getSelectedId()));
		}
	}
	// Actualiza combo de partidas presp. por empresa
	private void populateBudgetItems(int companyId) {
		budgetItemUiSuggestBox.clear();
		setBudgetItemsListBoxFilters(companyId);
	}

	// Asigna filtros al listado de partidas presp. por empresa
	private void setBudgetItemsListBoxFilters(int companyId) {
		BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

		if (companyId > 0) {
			BmFilter bmFilterByCompany = new BmFilter();
			bmFilterByCompany.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudget().getCompanyId(), companyId);
			budgetItemUiSuggestBox.addFilter(bmFilterByCompany);

		} else {
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getIdField(), "-1");
			budgetItemUiSuggestBox.addFilter(bmFilter);
		}
	}

	// Asigna filtros al listado de empresa
	private void setCompanyListBoxFilters(int idExist) {

		// Si es nuevo, asignar filtros, si no limpiarlos
		if (!(idExist > 0)) {
			// Preparar filtro para mostrar solo empresas NO previamente asignados a este producto
			BmoCompany bmoCompany = new BmoCompany();
			BmFilter bmCompanyListFilter = new BmFilter();
			bmCompanyListFilter.setNotInFilter(bmoProductCompany.getKind(), 
					bmoCompany.getIdFieldName(), 
					bmoProductCompany.getCompanyId().getName(),
					bmoProductCompany.getProductId().getName(),
					"" + productId
					);
			companyListBox = new UiListBox(getUiParams(), bmoCompany, bmCompanyListFilter);
		} else {
			companyListBox.clear();
			companyListBox.clearFilters();
		}
	}

	@Override
	public BmObject populateBObject() throws BmException {
		bmoProductCompany = new BmoProductCompany();
		bmoProductCompany.setId(id);
		bmoProductCompany.getCompanyId().setValue(companyListBox.getSelectedId());		
		bmoProductCompany.getProductId().setValue(productId);
		bmoProductCompany.getBudgetItemId().setValue(budgetItemUiSuggestBox.getSelectedId());
		bmoProductCompany.getAreaId().setValue(areaUiListBox.getSelectedId());

		return bmoProductCompany;
	}
}
