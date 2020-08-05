package com.flexwm.client.rpt;

import java.sql.Types;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ar.BmoPropertyRental;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.cm.BmoIndustry;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoBudget;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoBudgetItemType;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductFamily;
import com.flexwm.shared.op.BmoProductGroup;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.symgae.shared.sf.BmoUser;


public class UiContractReport extends UiReport {
	BmoOrder bmoOrder = new BmoOrder();
	BmoProperty bmoProperty = new BmoProperty();
	BmoRaccount bmoRaccount = new BmoRaccount();
	BmoPropertyRental bmoPropertyRental;
	UiListBox reportTypeListBox ;	
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	UiListBox deliveryStatusListBox = new UiListBox(getUiParams());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox customerLessorSuggestBox = new UiSuggestBox(new BmoCustomer());
	UiSuggestBox userSuggestBox = new UiSuggestBox(new BmoUser());	
	UiSuggestBox propertyRentalSuggestBox = new UiSuggestBox(new BmoPropertyRental());
	UiListBox industryUiListBox = new UiListBox(getUiParams(), new BmoIndustry());
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox budgetUiListBox = new UiListBox(getUiParams(), new BmoBudget());
	UiListBox budgetItemUiListBox = new UiListBox(getUiParams(), new BmoBudgetItem());

	UiListBox areaListBox;
//	UiListBox orderTypeListBox;

	UiDateBox lockStartBox = new UiDateBox();
	UiDateBox lockEndBox = new UiDateBox();
	UiDateBox rentIncreaseBox = new UiDateBox();
	UiDateBox rentEndIncreaseBox= new UiDateBox();
	UiDateBox rentalScheduleDateBox = new UiDateBox();
	UiDateBox rentalEndScheduleDateBox = new UiDateBox();
	UiDateBox dueDateBox = new UiDateBox();
	UiDateBox dueEndDateBox = new UiDateBox();
	
	BmoProduct bmoProduct = new BmoProduct();
	UiListBox productExtraUiListBox;
//	BmField showProductExtra;
	UiListBox productFamilyListBox;
	UiListBox productGroupListBox;
	UiListBox currencyListBox;
	BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();
	
	BmField rentEndIncrease;
	BmField rentalEndScheduleDate;
	BmField dueEndDate;
	
	String generalSection = "Filtros Generales";
	String datesSection = "Filtros de Fechas";
	String statusSection = "Filtros de Estatus";

	public UiContractReport(UiParams uiParams) {
		super(uiParams, new BmoPropertyRental(), "/rpt/pr/prrt_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoPropertyRental()));
		this.bmoPropertyRental = (BmoPropertyRental)getBmObject();
	
		//Field de fechas finales
		rentalEndScheduleDate = new BmField("rentalendscheduledate", "", "1er. Renta Fin", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		rentEndIncrease = new BmField("rentendincrease", "", "Incremento Renta Fin", 10, Types.VARCHAR, false, BmFieldType.DATE, false);
		dueEndDate = new BmField("dueenddate", "", "Programación Final", 10, Types.VARCHAR, false, BmFieldType.DATE, false);		
		
	}
	

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte con Autorenovación", "/rpt/pr/prrt_reneworder_report.jsp");
		reportTypeListBox.addItem("Reporte de Contratos General ", "/rpt/pr/prrt_general_report.jsp");
		
		BmoCustomer bmoCustomer = new BmoCustomer();
		BmFilter filterLessor = new BmFilter();
		filterLessor.setValueFilter(bmoCustomer.getKind(), bmoCustomer.getCustomercategory(), "" + BmoCustomer.CATEGORY_LESSEE);
		customerLessorSuggestBox.addFilter(filterLessor);
		
		
		
		BmFilter filterCustomer = new BmFilter();
		filterCustomer.setValueFilter(bmoCustomer.getKind(), bmoCustomer.getCustomercategory(), "" + BmoCustomer.CATEGORY_LESSOR);
		customerSuggestBox.addFilter(filterCustomer);
		
		try {
			bmoPropertyRental.getCurrencyId().setValue(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getCurrencyId().toInteger());
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
//		orderTypeListBox = new UiListBox(getUiParams(), new BmoOrderType());
		areaListBox = new UiListBox(getUiParams(), new BmoArea());
		productFamilyListBox = new UiListBox(getUiParams(), new BmoProductFamily());
		productGroupListBox = new UiListBox(getUiParams(), new BmoProductGroup());
		productExtraUiListBox = new UiListBox(getUiParams()); 
		currencyListBox = new UiListBox(getUiParams(), new BmoCurrency());

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
//		formFlexTable.addField(2, 0, orderTypeListBox, bmoPropertyRental.getOrderTypeId());
		formFlexTable.addField(3, 0, propertyRentalSuggestBox, bmoPropertyRental.getIdField());
		formFlexTable.addField(4, 0, userSuggestBox, bmoPropertyRental.getUserId());
		formFlexTable.addField(5, 0, customerSuggestBox, bmoPropertyRental.getCustomerId());
		formFlexTable.addField(6, 0, customerLessorSuggestBox, bmoProperty.getCustomerId());
		if (getSFParams().isFieldEnabled(bmoPropertyRental.getBmoCustomer().getIndustryId()))
			formFlexTable.addField(7, 0, industryUiListBox, bmoPropertyRental.getBmoCustomer().getIndustryId());
		formFlexTable.addField(8, 0, currencyListBox, bmoPropertyRental.getCurrencyId());
		formFlexTable.addSectionLabel(9, 0, datesSection, 2);
		formFlexTable.addField(10, 0, lockStartBox, bmoPropertyRental.getStartDate());
		formFlexTable.addField(11, 0, lockEndBox, bmoPropertyRental.getEndDate());
		
		formFlexTable.addField(12, 0, rentIncreaseBox, bmoPropertyRental.getRentIncrease());
		formFlexTable.addField(13, 0, rentEndIncreaseBox, rentEndIncrease );

		formFlexTable.addField(14, 0, rentalScheduleDateBox, bmoPropertyRental.getRentalScheduleDate());
		formFlexTable.addField(15, 0, rentalEndScheduleDateBox, rentalEndScheduleDate);

		formFlexTable.addField(16, 0, dueDateBox, bmoRaccount.getDueDate());		
		formFlexTable.addField(17, 0, dueEndDateBox, dueEndDate);
		
		formFlexTable.addSectionLabel(18, 0, statusSection, 2);
//		formFlexTable.addField(19, 0, paymentStatusListBox, bmoOrder.getPaymentStatus(), true);
		paymentStatusListBox.setSelectedIndex(-1);
		formFlexTable.addField(20, 0, statusListBox, bmoPropertyRental.getStatus(), true);
		statusListBox.setSelectedIndex(-1);
		formFlexTable.hideSection(statusSection);
		formFlexTable.hideField(dueEndDate);
		formFlexTable.hideField(dueDateBox);
		
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);
		addFilter(bmoPropertyRental.getCustomerId(), customerSuggestBox);	
		addFilter(bmoProperty.getCustomerId(), customerLessorSuggestBox);
		addFilter(bmoPropertyRental.getStatus(), statusListBox);
//		addFilter(bmoOrder.getPaymentStatus(), paymentStatusListBox);
		//Fechas
		addFilter(bmoPropertyRental.getStartDate(), lockStartBox.getTextBox().getText());		
		addFilter(bmoPropertyRental.getEndDate(), lockEndBox.getTextBox().getText());
		addFilter(bmoPropertyRental.getRentIncrease(), rentIncreaseBox.getTextBox().getText());
		addFilter(bmoPropertyRental.getRentIncrease(), rentIncreaseBox.getTextBox().getText());
		addFilter(rentEndIncrease, rentEndIncreaseBox.getTextBox().getText());
		addFilter(bmoPropertyRental.getRentalScheduleDate(), rentalScheduleDateBox.getTextBox().getText());		
		addFilter(rentalEndScheduleDate, rentalEndScheduleDateBox.getTextBox().getText());		
		addFilter(bmoRaccount.getDueDate(), dueDateBox.getTextBox().getText());
		addFilter(dueEndDate, dueEndDateBox.getTextBox().getText());
		
		addFilter(bmoPropertyRental.getIdField(), propertyRentalSuggestBox);
		if (getSFParams().isFieldEnabled(bmoPropertyRental.getBmoCustomer().getIndustryId()))
			addFilter( bmoPropertyRental.getBmoCustomer().getIndustryId(), industryUiListBox);
		addFilter(bmoPropertyRental.getUserId(), userSuggestBox);
		addFilter(bmoPropertyRental.getCurrencyId(), currencyListBox);
//		addFilter(bmoPropertyRental.getOrderTypeId(), orderTypeListBox);
		
		
		
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
	
	@Override
	public void formListChange(ChangeEvent event) {
		if (event.getSource() == reportTypeListBox) {
			if (reportTypeListBox.getSelectedCode().equals("/rpt/pr/prrt_racc_general_report.jsp")) {
				formFlexTable.showField(dueEndDate);
				formFlexTable.showField(dueDateBox);
			} else {
				formFlexTable.hideField(dueEndDate);
				formFlexTable.hideField(dueDateBox);
			}
			if (reportTypeListBox.getSelectedCode().equals("/rpt/pr/prrt_racc_general_report.jsp")) {
				formFlexTable.hideField(propertyRentalSuggestBox);
				formFlexTable.hideField(statusListBox);
				formFlexTable.hideField(lockStartBox);
				formFlexTable.hideField(lockEndBox);
				formFlexTable.hideField(rentIncreaseBox);
				formFlexTable.hideField(rentEndIncreaseBox);
				formFlexTable.hideField(rentalScheduleDateBox);
				formFlexTable.hideField(rentalEndScheduleDateBox);						
			}else {
				formFlexTable.showField(propertyRentalSuggestBox);
				formFlexTable.showField(statusListBox);
				formFlexTable.showField(lockStartBox);
				formFlexTable.showField(lockEndBox);
				formFlexTable.showField(rentIncreaseBox);
				formFlexTable.showField(rentEndIncreaseBox);
				formFlexTable.showField(rentalScheduleDateBox);
				formFlexTable.showField(rentalEndScheduleDateBox);
			}
		}

	//Se pierde la url, se manda a llamar de nuevo
	formValueChange("");
	}
}
