package com.flexwm.client.rpt;

import java.sql.Types;
import java.util.ArrayList;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.op.BmoSupplier;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoArea;


public class UiBankMovementReport extends UiReport {

	BmoBankMovement bmoBankMovement;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox ;
	UiDateBox dueDateBox = new UiDateBox();
	UiDateBox dueEndDateBox = new UiDateBox();	
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiListBox bankAccountListBox = new UiListBox(getUiParams(), new BmoBankAccount());
	BmoArea bmoArea = new BmoArea();
	UiListBox bankMovTypeListBox;
	BmField dueEndDate;
	BmField budgetId, budgetItemId;
	UiSuggestBox budgetSuggestBox = new UiSuggestBox(new BmoBudget());
	UiSuggestBox budgetItemSuggestBox = new UiSuggestBox(new BmoBudgetItem());
	UiListBox commentLogListbox;
	
	String generalSection = "Filtros Generales";

	public UiBankMovementReport(UiParams uiParams) {
		super(uiParams, new BmoBankMovement(), "/rpt/fi/bkmv_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoBankMovement()));		
		this.bmoBankMovement = (BmoBankMovement)getBmObject();		
	}

	@Override
	public void populateFields() {
		bankMovTypeListBox = new UiListBox(getUiParams(), new BmoBankMovType());
		reportTypeListBox = new UiListBox(getUiParams());
		commentLogListbox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de Mov. Bancarios", "/rpt/fi/bkmv_general_report.jsp");
		reportTypeListBox.addItem("Reporte Simple de Mov. Bancarios", "/rpt/fi/bkmv_general_simple_report.jsp");
		reportTypeListBox.addItem("Reporte Detalle de Mov. Bancarios", "/rpt/fi/bkmc_general_report.jsp");
		reportTypeListBox.addItem("Reporte Mov. de Banco Sin Detalle Aplicado", "/rpt/fi/bkmc_withoutdetail_report.jsp");
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean())
			reportTypeListBox.addItem("Reporte Pagos por Presupuesto", "/rpt/fi/bkmv_paymentsbudgetweek_report.jsp");
		
		commentLogListbox.addItem("No");
		commentLogListbox.addItem("Si");
		
		dueEndDate = new BmField("dueenddate", "", "Pago Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		budgetId = new BmField("budgetId", "", "Presupuesto", 8, Types.INTEGER, true, BmFieldType.ID, false);
		budgetItemId = new BmField("budgetItemId", "", "Part. Presup.", 8, Types.INTEGER, true, BmFieldType.ID, false);

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, bankAccountListBox, bmoBankMovement.getBankAccountId());
		formFlexTable.addField(3, 0, bankMovTypeListBox, bmoBankMovement.getBankMovTypeId(), true);
		formFlexTable.addField(4, 0, supplierSuggestBox, bmoBankMovement.getSupplierId());
		formFlexTable.addField(5, 0, customerSuggestBox, bmoBankMovement.getCustomerId());
		formFlexTable.addField(6, 0, commentLogListbox,bmoBankMovement.getCommentLog());
		formFlexTable.addField(7, 0, dueDateBox, bmoBankMovement.getDueDate());
		formFlexTable.addField(8, 0, dueEndDateBox, dueEndDate);
		// Si esta activo el manejo de presupuestos, mostrarlo
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			formFlexTable.addField(9, 0, budgetSuggestBox, budgetId);
			formFlexTable.addField(10, 0, budgetItemSuggestBox, budgetItemId);
		}
		populateStatusListBox();
		formFlexTable.addField(11, 0, statusListBox, bmoBankMovement.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
		
		formFlexTable.hideField(budgetId);
		formFlexTable.hideField(budgetItemId);		
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoBankMovement.getSupplierId(), supplierSuggestBox);		
		addFilter(bmoBankMovement.getCustomerId(), customerSuggestBox);
		addFilter(bmoBankMovement.getBankAccountId(), bankAccountListBox);
		addFilter(bmoBankMovement.getBankMovTypeId(), bankMovTypeListBox);		
		addFilter(bmoBankMovement.getDueDate(), dueDateBox.getTextBox().getText());		
		addFilter(dueEndDate, dueEndDateBox.getTextBox().getText());		
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			addFilter(budgetId, budgetSuggestBox);
			addFilter(budgetItemId, budgetItemSuggestBox);
		}
		addFilter(bmoBankMovement.getStatus(), statusListBox);
		addFilter(bmoBankMovement.getCommentLog(),commentLogListbox);
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == reportTypeListBox) {
			if (reportTypeListBox.getSelectedCode().equals("/rpt/fi/bkmv_paymentsbudgetweek_report.jsp")
					|| reportTypeListBox.getSelectedCode().equals("/rpt/fi/bkmc_general_report.jsp")
					|| reportTypeListBox.getSelectedCode().equals("/rpt/fi/bkmc_withoutdetail_report.jsp")) {
				formFlexTable.showField(budgetId);
				formFlexTable.showField(budgetItemId);
			} else {
				formFlexTable.hideField(budgetId);
				formFlexTable.hideField(budgetItemId);
			}
			if (reportTypeListBox.getSelectedCode().equals("/rpt/fi/bkmv_general_report.jsp") || reportTypeListBox.getSelectedCode().equals("/rpt/fi/bkmv_general_simple_report.jsp")) {
				formFlexTable.showField(commentLogListbox);
			}
			else {
				formFlexTable.hideField(commentLogListbox);
			}
		}
		
		//Se pierde la url, se manda a llamar de nuevo
		formValueChange("");
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
	public void populateStatusListBox() {
		
		
		ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();
		
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusRevision().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusAuthorized().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_AUTHORIZED, "Autorizado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusReconciled().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_RECONCILED, "Conciliado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_reconciled.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getStatusCancelled().toBoolean())) {
			status.add(new BmFieldOption(BmoBankMovement.STATUS_CANCELLED, "Cancelado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
		}
		
		bmoBankMovement.getStatus().setOptionList(status);

	}
}
