package com.flexwm.client.rpt;


import java.sql.Types;
import com.flexwm.shared.cm.BmoProject;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoWarehouse;
import com.google.gwt.user.datepicker.client.DateBox;
import com.symgae.client.chart.UiChart;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;


public class UiReqiByProductReport extends UiReport {
	
	BmoRequisitionItem bmoRequisitionItem;
	
	UiListBox statusListBox;
	UiListBox reportTypeListBox ;
	DateBox requestDateBox = new DateBox();
	DateBox deliveryStartBox = new DateBox();
	DateBox deliveryEndBox = new DateBox();	
	UiSuggestBox supplierSuggestBox = new UiSuggestBox(new BmoSupplier());	
	UiSuggestBox requisitionSuggestBox = new UiSuggestBox(new BmoRequisition());
	UiSuggestBox projectSuggestBox = new UiSuggestBox(new BmoProject());
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());
	UiListBox warehouseListBox = new UiListBox(getUiParams(), new BmoWarehouse());
	
	
	UiChart uiChart;
	
	BmField inputEndDate;
	BmField dueEndDate;
	BmField projectList;
	BmField productList;
	BmField warehouseList;
	BmField reqiStatus;
	BmField deliveryStart;
	BmField deliveryEnd;
	
	public UiReqiByProductReport(UiParams uiParams) {
		super(uiParams, new BmoRequisitionItem(), "/rpt/op/reqi_byproduct_report.jsp", "Productos por Pedido");
		this.bmoRequisitionItem = (BmoRequisitionItem)getBmObject();
		
		
	}

	@Override
	public void populateFields() {
		
		projectList = new BmField("projectid", "", "Projecto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		productList = new BmField("productid", "", "Producto", 8, Types.INTEGER, false, BmFieldType.ID, false);
		warehouseList = new BmField("warehouseid", "", "Almacén", 8, Types.INTEGER, false, BmFieldType.ID, false);
		
		
		deliveryStart = new BmField("deliverystart", "", "Entrega Inicio", 20, 0, true, BmFieldType.DATE, false);
		deliveryEnd = new BmField("deliveryend", "", "Entrega Fin", 20, 0, true, BmFieldType.DATE, false);
		
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Productos", "/rpt/op/reqi_byproduct_report.jsp");		
						
		formFlexTable.addField(1, 0, supplierSuggestBox, bmoRequisitionItem.getBmoProduct().getSupplierId());		
		formFlexTable.addField(1, 2, requisitionSuggestBox, bmoRequisitionItem.getRequisitionId());
		formFlexTable.addField(2, 0, projectSuggestBox, projectList);
		formFlexTable.addField(2, 2, productSuggestBox, productList);
		formFlexTable.addField(3, 0, deliveryStartBox, deliveryStart);
		formFlexTable.addField(3, 2, deliveryEndBox, deliveryEnd);		
		formFlexTable.addField(4, 2, warehouseListBox, warehouseList);
		
		
		
		formFlexTable.addField(5, 2, reportTypeListBox, "Tipo de Reporte");
	}

	@Override
	public void setFilters(){
		addFilter(bmoRequisitionItem.getBmoProduct().getSupplierId(), supplierSuggestBox);		
		addFilter(bmoRequisitionItem.getRequisitionId(), requisitionSuggestBox);		
		addFilter(projectList, projectSuggestBox);
		addFilter(productList, productSuggestBox);
		addFilter(warehouseList, warehouseListBox);
		addFilter(deliveryStart, deliveryStartBox.getTextBox().getText());		
		addFilter(deliveryEnd, deliveryEndBox.getTextBox().getText());
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}

