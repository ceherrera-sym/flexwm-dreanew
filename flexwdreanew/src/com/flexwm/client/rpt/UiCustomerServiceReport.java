package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoCustomerService;
import com.flexwm.shared.op.BmoCustomerServiceType;
import com.flexwm.shared.op.BmoOrderType;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoUser;


public class UiCustomerServiceReport extends UiReport {
	BmoCustomerService bmoCustomerService;
	BmoOrderType bmoOrderType;

	UiListBox reportTypeListBox ;	
	UiListBox customerServiceTypeListBox;
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());	
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());	
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	

	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox deliveryStatusListBox = new UiListBox(getUiParams());
	UiDateBox lockStartBox = new UiDateBox();
	UiDateBox lockEndBox = new UiDateBox();

	UiDateBox registrationDateBox = new UiDateBox();
	UiDateBox committalDateBox = new UiDateBox();
	UiDateBox solutionDateBox = new UiDateBox();

	BmField registrationDateEnd;
	BmField committalDateEnd;
	BmField solutionDateEnd;
	UiDateBox registrationDateEndDateBox = new UiDateBox();
	UiDateBox committalDateEndDateBox = new UiDateBox();
	UiDateBox solutionDateEndDateBox = new UiDateBox();

	String generalSection = "Filtros Generales";
	String orderSection = "Filtros Pedidos";

	public UiCustomerServiceReport(UiParams uiParams) {
		super(uiParams, new BmoCustomerService(), "/rpt/op/cuse_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoCustomerService()));
		this.bmoCustomerService = (BmoCustomerService)getBmObject();

		registrationDateEnd = new BmField("registrationdateend", "", "F. Registro Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
		committalDateEnd = new BmField("committaldateend", "", "F. Compromiso Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
		solutionDateEnd = new BmField("solutiondateend", "", "F. Solución Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General de Atención Clientes", "/rpt/op/cuse_general_report.jsp");
		customerServiceTypeListBox = new UiListBox(getUiParams(), new BmoCustomerServiceType());

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, customerServiceTypeListBox, bmoCustomerService.getCustomerServiceTypeId());
		formFlexTable.addField(3, 0, userSuggestBox, bmoCustomerService.getUserId());
		formFlexTable.addField(4, 0, registrationDateBox, bmoCustomerService.getRegistrationDate());
		formFlexTable.addField(5, 0, registrationDateEndDateBox, registrationDateEnd);
		formFlexTable.addField(6, 0, committalDateBox, bmoCustomerService.getCommittalDate());
		formFlexTable.addField(7, 0, committalDateEndDateBox, committalDateEnd);
		formFlexTable.addField(8, 0, solutionDateBox, bmoCustomerService.getSolutionDate());
		formFlexTable.addField(9, 0, solutionDateEndDateBox, solutionDateEnd);
		formFlexTable.addField(10, 0, statusListBox, bmoCustomerService.getStatus(), true);
		statusListBox.setSelectedIndex(-1);

		formFlexTable.addSectionLabel(11, 0, orderSection, 2);
		formFlexTable.addField(12, 0, orderSuggestBox, bmoCustomerService.getOrderId());
		formFlexTable.addField(13, 0, customerSuggestBox, bmoCustomerService.getBmoOrder().getCustomerId());		
		formFlexTable.addField(14, 0, lockStartBox, bmoCustomerService.getBmoOrder().getLockStart());
		formFlexTable.addField(15, 0, lockEndBox, bmoCustomerService.getBmoOrder().getLockEnd());

		formFlexTable.hideSection(orderSection);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoCustomerService.getCustomerServiceTypeId(), customerServiceTypeListBox);
		addFilter(bmoCustomerService.getOrderId(), orderSuggestBox);
		addFilter(bmoCustomerService.getStatus(), statusListBox);
		addFilter(bmoCustomerService.getRegistrationDate(), registrationDateBox.getTextBox().getText());
		addFilter(registrationDateEnd, registrationDateEndDateBox.getTextBox().getText());
		addFilter(bmoCustomerService.getCommittalDate(), committalDateBox.getTextBox().getText());
		addFilter(committalDateEnd, committalDateEndDateBox.getTextBox().getText());
		addFilter(bmoCustomerService.getSolutionDate(), solutionDateBox.getTextBox().getText());
		addFilter(solutionDateEnd, solutionDateEndDateBox.getTextBox().getText());
		addFilter(bmoCustomerService.getBmoOrder().getCustomerId(), customerSuggestBox);
		addFilter(bmoCustomerService.getBmoOrder().getLockStart(), lockStartBox.getTextBox().getText());
		addFilter(bmoCustomerService.getBmoOrder().getLockEnd(), lockEndBox.getTextBox().getText());
		addFilter(bmoCustomerService.getUserId(), userSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
