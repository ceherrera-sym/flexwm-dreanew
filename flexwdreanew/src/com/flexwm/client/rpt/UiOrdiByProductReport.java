package com.flexwm.client.rpt;


import java.sql.Types;
import com.flexwm.shared.cm.BmoCustomer;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoSupplierCategory;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.chart.UiChart;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;


public class UiOrdiByProductReport extends UiReport {
	BmoOrder bmoOrder;
	BmoProduct bmoProduct = new BmoProduct();
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox reportTypeListBox ;	
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());
	UiListBox supplierCategoryBox = new UiListBox(getUiParams(), new BmoSupplierCategory());
	UiSuggestBox customerSuggestBox = new UiSuggestBox(new BmoCustomer());	
	UiSuggestBox orderSuggestBox = new UiSuggestBox(new BmoOrder());
	DateBox lockStartBox = new DateBox();
	DateBox lockEndBox = new DateBox();
	
	UiChart uiChart;
	
	BmField lockStart;
	BmField lockEnd;
	

	public UiOrdiByProductReport(UiParams uiParams) {
		super(uiParams, new BmoOrder(), "/rpt/op/ordi_byproduct_report.jsp", "Productos por Pedido");
		this.bmoOrder = (BmoOrder)getBmObject();
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Productos", "/rpt/op/ordi_byproduct_report.jsp");
		
		lockStart = new BmField("lockstart", "", "Apartado Inicio", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		lockEnd = new BmField("lockEnd", "", "Apartado Fin", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
				
		formFlexTable.addField(1, 0, supplierSuggestBox, bmoProduct.getSupplierId());
		formFlexTable.addField(1, 2, orderSuggestBox, bmoOrder.getIdField());
		formFlexTable.addField(2, 0, lockStartBox, lockStart);
		formFlexTable.addField(2, 2, lockEndBox, lockEnd);
		formFlexTable.addField(3, 0, statusListBox, bmoOrder.getStatus());		
		formFlexTable.addField(3, 2, reportTypeListBox, "Tipo de Reporte");
	}

	@Override
	public void setFilters(){
		addFilter(bmoProduct.getSupplierId(), supplierSuggestBox);		
		addFilter(bmoOrder.getIdField(), orderSuggestBox);		
		addFilter(bmoOrder.getStatus(), statusListBox);
		addFilter(lockStart, lockStartBox.getTextBox().getText());		
		addFilter(lockEnd, lockEndBox.getTextBox().getText());
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

