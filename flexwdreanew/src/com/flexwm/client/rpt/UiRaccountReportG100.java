package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.google.gwt.event.dom.client.ChangeEvent;


public class UiRaccountReportG100 extends UiReport {
	
	BmoRaccount bmoRaccount;	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox;
	UiSuggestBox customersSuggestBox = new UiSuggestBox(new BmoCustomer());	
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());
	UiListBox companyListBox = new UiListBox(getUiParams(), new BmoCompany());
	UiListBox raccountTypeListBox = new UiListBox(getUiParams(), new BmoRaccountType());
	UiSuggestBox collectorUserSuggestBox = new UiSuggestBox(new BmoUser());	
	UiListBox wflowCategoryListBox;
	UiDateBox receiveDateBox = new UiDateBox();
	UiDateBox receiveEndDateBox = new UiDateBox();	
	UiDateBox dueDateBox = new UiDateBox();
	UiDateBox dueEndDateBox = new UiDateBox();	
	UiListBox currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());
	UiListBox failureUiListBox = new UiListBox(getUiParams());
	UiListBox linkedUiListBox = new UiListBox(getUiParams());
	UiListBox customerCategoryUiListBox = new UiListBox(getUiParams());
	UiListBox budgetUiListBox = new UiListBox(getUiParams(), new BmoBudget());
	UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());
	UiListBox areaListBox;

	UiListBox detailReportListBox;
	BmField detailReport;

	BmField receiveEndDate; 
	BmField dueEndDate;
	BmField wFlowCategoryId;
	BmField userId;
	BmoWFlowCategory bmoWFlowCategory;
	BmoOrder bmoOrder = new BmoOrder();
	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

	String generalSection = "Filtros Generales";
	String datesSection = "Filtros de Fechas";
	String statusSection = "Filtros de Estatus";
	String budgetsSection = "Filtros Presupuestales";

	public static final char PAYMENTSTATUS_REVISION = 'R';
	public static final char PAYMENTSTATUS_PARTIAL = 'P';
	public static final char PAYMENTSTATUS_PENDIENTANDPARTIAL = 'B';
	public static final char PAYMENTSTATUS_TOTAL = 'T';
	// CustomerCategory
	public static char CATEGORY_LESSOR = 'R';
	public static char CATEGORY_LESSEE = 'E';

	public UiRaccountReportG100(UiParams uiParams) {
		super(uiParams, new BmoRaccount(), "/rpt/fi/racc_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoRaccount()));
		this.bmoRaccount = (BmoRaccount)getBmObject();

		//Field de fechas finales
		receiveEndDate = new BmField("receiveenddate", "", "F. Registro Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		dueEndDate = new BmField("dueenddate", "", "Programación Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);		
		userId = new BmField("userid", "", "Usuario", 10, Types.INTEGER, false, BmFieldType.ID, false);
		// Categoria de flujos
		bmoWFlowCategory = new BmoWFlowCategory();
		wflowCategoryListBox = new UiListBox(getUiParams(), new BmoWFlowCategory());

		detailReport = new BmField("detailReport", "", "Modelo Reporte?", 10, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de CxC", "/rpt/fi/racc_general_report.jsp");
		reportTypeListBox.addItem("Reporte CxC Agrupadas por Pedido", "/rpt/fi/racc_byorder_report.jsp");
		//reportTypeListBox.addItem("Reporte Detallado de CxC", "/rpt/fi/racc_detail_report.jsp");
		reportTypeListBox.addItem("Reporte CxC Agrupadas por Cliente", "/rpt/fi/racc_bycustomer_report.jsp");
		reportTypeListBox.addItem("Reporte Agrupado por cliente a Resumen/Detalle", "/rpt/fi/rait_bycustomer_report.jsp");
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) 
			reportTypeListBox.addItem("Reporte CxC Agrupadas por Departamento", "/rpt/fi/rait_byarea_report.jsp");
//		reportTypeListBox.addItem("Estado de Cuenta del Cliente", "/rpt/fi/racc_statementofaccount_report.jsp");
		reportTypeListBox.addItem("Reporte de CxC Vinculadas", "/rpt/fi/racc_raccountlinks_report.jsp");
		reportTypeListBox.addItem("Reporte Antigüedad de Saldos de CxC", "/rpt/fi/racc_paymentscheduleg100_report.jsp");
		reportTypeListBox.addItem("Reporte de Flujo por Proyecto", "/rpt/fi/racc_detailedpaymentschedule_report.jsp"); 
		reportTypeListBox.addItem("Reporte de Seguimiento de Cobranza", "/rpt/fi/racc_collectiontracking_report.jsp"); 

		//		customerCategory = new BmField("customerCategory", "", "Categoría Cliente", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		//		customerCategory.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
		//				new BmFieldOption(CATEGORY_LESSEE, "Arrendador", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/cust_category_lessee.png")),
		//				new BmFieldOption(CATEGORY_LESSOR, "Arrendatario", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/cust_category_leesor.png"))
		//				)));

		if (getSFParams().isFieldEnabled(bmoRaccount.getFailure())) {
			failureUiListBox.addItem("Todos", "-1");
			failureUiListBox.addItem("Si", "1"); 
			failureUiListBox.addItem("No", "0"); 
		}

		if (getSFParams().isFieldEnabled(bmoRaccount.getLinked())) {
			linkedUiListBox.addItem("Todos", "-1");
			linkedUiListBox.addItem("Si", "1"); 
			linkedUiListBox.addItem("No", "0"); 
		}

		detailReportListBox = new UiListBox(getUiParams());
		detailReportListBox.addItem("Resumen", "1");
		detailReportListBox.addItem("Detalle", "2");

		// Si esta asignada la empresa maestra, la pone por defecto en los filtros
		try {
			if (getSFParams().getSelectedCompanyId() > 0)
				bmoRaccount.getCompanyId().setValue(getSFParams().getSelectedCompanyId());

		} catch (BmException e) {
			showSystemMessage(this.getClass().getName() + "-populateFields(): No se puede asignar Empresa: " + e.toString());
		}

		try {
			bmoRaccount.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
		} catch (BmException e) {
			showSystemMessage("No se puede asignar moneda : " + e.toString());
		}

		// Filtrar por grupo de cobranza
		BmoUser bmoUser = new BmoUser();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter filterCollector = new BmFilter();
		int collectorGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
		filterCollector.setInFilter(bmoProfileUser.getKind(), 
				bmoUser.getIdFieldName(),
				bmoProfileUser.getUserId().getName(),
				bmoProfileUser.getProfileId().getName(),
				"" + collectorGroupId);		

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			// Filtro de ingresos(abono)
			BmFilter filterByDeposit = new BmFilter();
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
			filterByDeposit.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBmoBudgetItemType().getType().getName(), "" + BmoBudgetItemType.TYPE_DEPOSIT);
			budgetItemUiListBox.addFilter(filterByDeposit);
		}

		collectorUserSuggestBox.addFilter(filterCollector);
		areaListBox = new UiListBox(getUiParams(), new BmoArea());

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, detailReportListBox, detailReport);

		formFlexTable.addField(3, 0, customersSuggestBox, bmoRaccount.getCustomerId());
		if (getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory())) 
			formFlexTable.addField(4, 0, customerCategoryUiListBox, bmoCustomer.getCustomercategory());
		formFlexTable.addField(5, 0, orderSuggestBox, bmoRaccount.getOrderId());
		formFlexTable.addField(6, 0, wflowCategoryListBox, bmoWFlowCategory.getIdField());
		formFlexTable.addField(7, 0, companyListBox, bmoRaccount.getCompanyId());
		formFlexTable.addField(8, 0, userSuggestBox, userId);
		formFlexTable.addField(9, 0, collectorUserSuggestBox, bmoRaccount.getCollectorUserId());

		if (getSFParams().isFieldEnabled(bmoRaccount.getFailure())) 
			formFlexTable.addField(10, 0, failureUiListBox, bmoRaccount.getFailure());

		if (getSFParams().isFieldEnabled(bmoRaccount.getLinked())) 
			formFlexTable.addField(11, 0, linkedUiListBox, bmoRaccount.getLinked());

		formFlexTable.addField(12, 0, currencyListBox, bmoRaccount.getCurrencyId());

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			formFlexTable.addSectionLabel(13, 0, budgetsSection, 2);
			formFlexTable.addField(14, 0, budgetUiListBox, bmoBudgetItem.getBudgetId());
			setBudgetItemListBoxFilters("-1");
			formFlexTable.addField(15, 0, budgetItemUiListBox, bmoRaccount.getBudgetItemId(), true);
			formFlexTable.addField(16, 0, areaListBox, bmoRaccount.getAreaId(), true);
		}

		formFlexTable.addSectionLabel(17, 0, datesSection, 2);
		formFlexTable.addField(18, 0, receiveDateBox, bmoRaccount.getReceiveDate());		
		formFlexTable.addField(19, 0, receiveEndDateBox, receiveEndDate);
		formFlexTable.addField(20, 0, dueDateBox, bmoRaccount.getDueDate());		
		formFlexTable.addField(21, 0, dueEndDateBox, dueEndDate);

		formFlexTable.addSectionLabel(22, 0, statusSection, 2);
		formFlexTable.addField(23, 0, paymentStatusListBox, bmoRaccount.getPaymentStatus(), true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(24, 0, statusListBox, bmoRaccount.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
		statusEffect();
	}	

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoRaccount.getCustomerId(), customersSuggestBox);		
		addFilter(bmoRaccount.getOrderId(), orderSuggestBox);
		addFilter(bmoRaccount.getCompanyId(), companyListBox);
		addFilter(bmoWFlowCategory.getIdField(), wflowCategoryListBox);
		addFilter(bmoRaccount.getStatus(), statusListBox);
		addFilter(bmoRaccount.getPaymentStatus(), paymentStatusListBox);
		addFilter(bmoRaccount.getReceiveDate(), receiveDateBox.getTextBox().getText());		
		addFilter(receiveEndDate, receiveEndDateBox.getTextBox().getText());
		addFilter(bmoRaccount.getCurrencyId(), currencyListBox);
		addFilter(bmoRaccount.getDueDate(), dueDateBox.getTextBox().getText());
		addFilter(dueEndDate, dueEndDateBox.getTextBox().getText());
		addFilter(bmoRaccount.getCollectorUserId(), collectorUserSuggestBox);
		addFilter(userId, userSuggestBox);
		if (getSFParams().isFieldEnabled(bmoRaccount.getFailure()))
			addFilter(bmoRaccount.getFailure(), failureUiListBox);
		if (getSFParams().isFieldEnabled(bmoRaccount.getLinked()))
			addFilter(bmoRaccount.getLinked(), linkedUiListBox);
		if (getSFParams().isFieldEnabled(bmoCustomer.getCustomercategory()))
			addFilter(bmoCustomer.getCustomercategory(), customerCategoryUiListBox);

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toInteger() > 0) {
			addFilter(bmoBudgetItem.getBudgetId(), budgetUiListBox);
			addFilter(bmoRaccount.getBudgetItemId(), budgetItemUiListBox);
			addFilter(bmoRaccount.getAreaId(), areaListBox);
		}

		addFilter(detailReport, detailReportListBox);
	}

	private void statusEffect() {
		if (getSFParams().getSelectedCompanyId() > 0)
			companyListBox.setEnabled(false);

		formFlexTable.hideField(detailReport);
		formFlexTable.hideSection(budgetsSection);
		formFlexTable.hideSection(datesSection);
		formFlexTable.hideSection(statusSection);
	}

	@Override
	public void formListChange(ChangeEvent event) {

		if (event.getSource() == companyListBox) {
			populateBudgets(companyListBox.getSelectedId());
			populateBudgetItem("0");
		} else if (event.getSource() == budgetUiListBox) {
			populateBudgetItem(budgetUiListBox.getSelectedId());
		}
		else if (event.getSource() == reportTypeListBox) {
			if (reportTypeListBox.getSelectedCode() == "/rpt/fi/rait_bycustomer_report.jsp") 
				formFlexTable.showField(detailReport);
			else 
				formFlexTable.hideField(detailReport);
		} 
		formValueChange("");
	}

	// Actualiza combo de almacenes
	private void populateBudgets(String companyId) {
		budgetUiListBox.clear();
		budgetUiListBox.clearFilters();
		setBudgetsListBoxFilters(companyId);
		budgetUiListBox.populate("" + companyId);
	}

	// Asigna filtros al listado de almacenes
	private void setBudgetsListBoxFilters(String companyId) {

		if (Integer.parseInt(companyId) > 0) {
			BmoBudget bmoBudget = new BmoBudget();
			BmFilter bmFilterByCompany = new BmFilter();
			bmFilterByCompany.setValueFilter(bmoBudget.getKind(), bmoBudget.getCompanyId(), companyId);
			budgetUiListBox.addBmFilter(bmFilterByCompany);
		} else {
			BmoBudget bmoBudget = new BmoBudget();
			BmFilter bmFilterNull = new BmFilter();
			bmFilterNull.setValueFilter(bmoBudget.getKind(), bmoBudget.getIdField(), -1);
			budgetUiListBox.addBmFilter(bmFilterNull);
		}
	}

	// Actualiza combo de partidas presupuestales
	private void populateBudgetItem(String budgetId) {
		budgetItemUiListBox.clear();
		budgetItemUiListBox.clearFilters();
		setBudgetItemListBoxFilters(budgetId);
		budgetItemUiListBox.populate("" + budgetId);
	}

	// Asigna filtros al listado de partidas presupuestales
	private void setBudgetItemListBoxFilters(String budgetId) {

		if (Integer.parseInt(budgetId) > 0) {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
			BmFilter bmFilterByBudget = new BmFilter();
			bmFilterByBudget.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId(), budgetId);
			budgetItemUiListBox.addBmFilter(bmFilterByBudget);
		} else {
			BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
			BmFilter bmFilterNull = new BmFilter();
			bmFilterNull.setValueFilter(bmoBudgetItem.getKind(), bmoBudgetItem.getBudgetId(), -1);
			budgetItemUiListBox.addBmFilter(bmFilterNull);
		}
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

