package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;


public class UiBudgetReport extends UiReport {
	BmoBudgetItem bmoBudgetItem;

	UiSuggestBox budgetUiSuggestBox = new UiSuggestBox(new BmoBudget());
	UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	UiSuggestBox developmenPhaseSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());

	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiDateBox dueDateStartBox = new UiDateBox();
	UiDateBox dueDateEndBox = new UiDateBox();

	UiListBox reportTypeListBox;
	BmField developmentPhaseId;
	BmField supplierId;
	BmField dueDateStart;
	BmField dueDateEnd;

	String generalSection = "Filtros Generales";

	public UiBudgetReport(UiParams uiParams) {
		super(uiParams, new BmoBudgetItem(), "/rpt/fi/flex_budget_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoBudget()));
		this.bmoBudgetItem = (BmoBudgetItem)getBmObject();
	}

	@Override
	public void populateFields() {

		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte de Presupuestos", "/rpt/fi/flex_budget_report.jsp");
		reportTypeListBox.addItem("Presupuestos por Prov.", "/rpt/fi/flex_budgetbysupl_report.jsp");

		dueDateStart = new BmField("duedatestart", "", "Pago Inicio", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		dueDateEnd = new BmField("duedateend", "", "Pago Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		supplierId = new BmField("supplierid", "", "Proveedores", 20, Types.INTEGER, false, BmFieldType.ID, false);
		developmentPhaseId = new BmField("developmentphaseid", "", "Fase Desarrollo", 11, Types.INTEGER, false, BmFieldType.ID, false);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, budgetUiSuggestBox, bmoBudgetItem.getBudgetId());
		formFlexTable.addField(3, 0, budgetItemSuggestBox, bmoBudgetItem.getIdField());
		formFlexTable.addField(4, 0, supplierSuggestBox, supplierId);
		if (getSFParams().hasRead(new BmoDevelopmentPhase().getProgramCode()))
			formFlexTable.addField(5, 0, developmenPhaseSuggestBox, developmentPhaseId);
		formFlexTable.addField(6, 0, dueDateStartBox, dueDateStart);
		formFlexTable.addField(7, 0, dueDateEndBox, dueDateEnd);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoBudgetItem.getBudgetId(), budgetUiSuggestBox);
		addFilter(bmoBudgetItem.getIdField(), budgetItemSuggestBox);
		if (getSFParams().hasRead(new BmoDevelopmentPhase().getProgramCode()))
			addFilter(developmentPhaseId, developmenPhaseSuggestBox);
		addFilter(supplierId, supplierSuggestBox);
		addFilter(dueDateStart, dueDateStartBox.getTextBox().getText());
		addFilter(dueDateEnd, dueDateEndBox.getTextBox().getText());
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
