package com.flexwm.client.rpt;

import java.sql.Types;
import java.util.ArrayList;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoUser;


public class UiOrderProjReport extends UiReport {
	BmoOrder bmoOrder;
	UiListBox reportTypeListBox ;	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox deliveryStatusListBox = new UiListBox(getUiParams());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());	
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
	UiListBox industryUiListBox = new UiListBox(getUiParams(), new BmoIndustry());
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox budgetUiListBox = new UiListBox(getUiParams(), new BmoBudget());
	UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());

	UiListBox areaListBox;
	UiListBox orderTypeListBox;

	UiDateBox lockStartBox = new UiDateBox();
	UiDateBox lockEndBox = new UiDateBox();
	BmoProduct bmoProduct = new BmoProduct();
	UiListBox productExtraUiListBox;
	BmField showProductExtra;
	UiListBox productFamilyListBox;
	UiListBox productGroupListBox;
	UiListBox currencyListBox;
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	String generalSection = "Filtros Generales";
	String productsSection = "Filtros de Productos";
	String statusSection = "Filtros de Estatus";

	public UiOrderProjReport(UiParams uiParams) {
		super(uiParams, new BmoOrder(), "/rpt/op/orde_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoOrder()));
		this.bmoOrder = (BmoOrder)getBmObject();

		//areaId = new BmField("area_areaid", "", "Departamento", 4, Types.INTEGER, true, BmFieldType.ID, false);
		showProductExtra = new BmField("showProductExtra", "", "Mostrar Items", 4, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public void populateFields() {
		populateStatusListBox();
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte de Pedidos General ", "/rpt/op/orde_general_report.jsp");
		//reportTypeListBox.addItem("Productos por Pedido  ", "/rpt/ordi_byproduct_report.jsp");
		reportTypeListBox.addItem("Reporte Disponibilidad de Productos", "/rpt/op/ordi_lock_report.jsp");
		//reportTypeListBox.addItem("Reporte Disponibilidad de Recursos", "/rpt/op/ordq_lock_report.jsp");
		//reportTypeListBox.addItem("Reporte Asignación de Staff / Grupos a Pedidos", "/rpt/op/ords_lock_report.jsp");
		reportTypeListBox.addItem("Reporte Disponibilidad de Colaboradores", "/rpt/wf/wflu_lock_report.jsp");
		reportTypeListBox.addItem("Reporte Productos en Pedidos", "/rpt/op/ordi_product_report.jsp");
		reportTypeListBox.addItem("Rpt. Productos en Pedidos Listado por Cliente", "/rpt/op/ordi_productbycustomerlist_report.jsp");
		reportTypeListBox.addItem("Rpt. Productos en Pedidos Agrupado por Cliente ", "/rpt/op/ordi_productbycustomer_report.jsp");
		reportTypeListBox.addItem("Reporte Items en Pedidos", "/rpt/op/ordi_pricecost_report.jsp");
		//reportTypeListBox.addItem("Reporte Rentabilidad Pedidos", "/rpt/op/ordi_ordercost_report.jsp");
		reportTypeListBox.addItem("Reporte Registro de Pedidos", "/rpt/op/orde_registry_report.jsp");
		reportTypeListBox.addItem("Reporte de Compras por Pedidos", "/rpt/op/orde_reqi_racc_report.jsp");	
		//reportTypeListBox.addItem("Reporte con Autorenovación", "/rpt/op/orde_reneworder_report.jsp");
		//reportTypeListBox.addItem("Reporte Matriz Renovación Pedidos", "/rpt/op/orde_renewordermatriz_report.jsp");
		reportTypeListBox.addItem("Reporte de Utilidad", "/rpt/op/orde_utilities_report.jsp");

		try {
			bmoOrder.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			// Filtro de ingresos(abono)
			BmFilter filterByDeposit = new BmFilter();
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
			filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
			budgetItemUiListBox.addFilter(filterByDeposit);
		}	
		orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		areaListBox = new UiListBox(getUiParams(), new BmoArea());
		productFamilyListBox = new UiListBox(getUiParams(), new BmoProductFamily());
		productGroupListBox = new UiListBox(getUiParams(), new BmoProductGroup());
		productExtraUiListBox = new UiListBox(getUiParams()); 
		currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());

		productExtraUiListBox.addItem("Todos", "2");
		productExtraUiListBox.addItem("Extras", "1");
		productExtraUiListBox.addItem("Productos", "0");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");

		formFlexTable.addField(2, 0, orderTypeListBox, bmoOrder.getOrderTypeId());
		formFlexTable.addField(3, 0, orderSuggestBox, bmoOrder.getIdField());
		formFlexTable.addField(4, 0, userSuggestBox, bmoOrder.getUserId());
		formFlexTable.addField(5, 0, customerSuggestBox, bmoOrder.getCustomerId());
		if (getSFParams().isFieldEnabled(bmoOrder.getBmoCustomer().getIndustryId()))
			formFlexTable.addField(6, 0, industryUiListBox, bmoOrder.getBmoCustomer().getIndustryId());
		formFlexTable.addField(7, 0, lockStartBox, bmoOrder.getLockStart());
		formFlexTable.addField(8, 0, lockEndBox, bmoOrder.getLockEnd());
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			formFlexTable.addField(9, 0, budgetUiListBox, bmoBudgetItem.getBudgetId());
			formFlexTable.addField(10, 0, budgetItemUiListBox, bmoOrder.getDefaultBudgetItemId());
			formFlexTable.addField(11, 0, areaListBox, bmoOrder.getDefaultAreaId());
		}
		formFlexTable.addField(12, 0, currencyListBox, bmoOrder.getCurrencyId());
		formFlexTable.addSectionLabel(13, 0, statusSection, 2);
		formFlexTable.addField(14, 0, deliveryStatusListBox, bmoOrder.getDeliveryStatus(), true);
		deliveryStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(15, 0, paymentStatusListBox, bmoOrder.getPaymentStatus(), true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(16, 0, statusListBox, bmoOrder.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

		formFlexTable.addSectionLabel(17, 0, productsSection, 2);
		formFlexTable.addField(18, 0, productExtraUiListBox, showProductExtra);	
		formFlexTable.addField(19, 0, productSuggestBox, bmoProduct.getIdField());
		formFlexTable.addField(20, 0, productFamilyListBox, bmoProduct.getProductFamilyId(), true);
		formFlexTable.addField(21, 0, productGroupListBox, bmoProduct.getProductGroupId(), true);

		formFlexTable.hideSection(statusSection);
		formFlexTable.hideSection(productsSection);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoOrder.getCustomerId(), customerSuggestBox);	
		addFilter(bmoOrder.getStatus(), statusListBox);
		addFilter(bmoOrder.getPaymentStatus(), paymentStatusListBox);
		addFilter(bmoOrder.getDeliveryStatus(), deliveryStatusListBox);
		addFilter(bmoOrder.getLockStart(), lockStartBox.getTextBox().getText());		
		addFilter(bmoOrder.getLockEnd(), lockEndBox.getTextBox().getText());

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			addFilter(bmoBudgetItem.getBudgetId(), budgetUiListBox);
			addFilter(bmoOrder.getDefaultBudgetItemId(), budgetItemUiListBox);
			addFilter(bmoOrder.getDefaultAreaId(), areaListBox);
		}
		addFilter(bmoOrder.getIdField(), orderSuggestBox);
		if (getSFParams().isFieldEnabled(bmoOrder.getBmoCustomer().getIndustryId()))
			addFilter(bmoOrder.getBmoCustomer().getIndustryId(), industryUiListBox);
		addFilter(bmoOrder.getUserId(), userSuggestBox);
		addFilter(bmoOrder.getCurrencyId(), currencyListBox);
		addFilter(bmoProduct.getIdField(), productSuggestBox);
		addFilter(showProductExtra, productExtraUiListBox);
		addFilter(bmoProduct.getProductFamilyId(), productFamilyListBox);
		addFilter(bmoProduct.getProductGroupId(), productGroupListBox);
		addFilter(bmoOrder.getOrderTypeId(), orderTypeListBox);
	}

	public void populateStatusListBox() {


		ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();

		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOrdeStatusRevision().toBoolean())) {
			status.add(new BmFieldOption(BmoOrder.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOrdeStatusAuthorized().toBoolean())) {
			status.add(new BmFieldOption(BmoOrder.STATUS_AUTHORIZED, "Autorizada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_authorized.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOrdeStatusFinished().toBoolean())) {
			status.add(new BmFieldOption(BmoOrder.STATUS_FINISHED, "Finalizada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_finished.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOrdeStatusCancelled().toBoolean())) {
			status.add(new BmFieldOption(BmoOrder.STATUS_CANCELLED, "Cancelada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
		}

		bmoOrder.getStatus().setOptionList(status);
	}
	
	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
