package com.flexwm.client.rpt;

import java.sql.Types;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;


public class UiWhTracksReport extends UiReport {

	BmoWhTrack bmoWhTrack;
	BmField dateMovStart;
	BmField dateMovEnd;

	UiListBox reportTypeListBox ;	
	UiDateBox dateMovStartDateBox = new UiDateBox();
	UiDateBox dateMovEndDateBox = new UiDateBox();
	UiSuggestBox productSuggestBox = new UiSuggestBox(new BmoProduct());

	String generalSection = "Filtros Generales";

	public UiWhTracksReport(UiParams uiParams) {
		super(uiParams, new BmoWhTrack(), "/rpt/op/whtr_general_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoWhTrack()));
		this.bmoWhTrack = (BmoWhTrack)getBmObject();

		dateMovStart = new BmField("datemovstart", "", "Fecha Mov. Inicio", 20, Types.DATE, true, BmFieldType.DATE, false);		
		dateMovEnd = new BmField("datemovend", "", "Fecha Mov. Fin", 20, Types.DATE, true, BmFieldType.DATE, false);		
	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte General Rastreo de Productos", "/rpt/op/whtr_general_report.jsp");

		formFlexTable.addSectionLabel(0,  0,  generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		
		if (getSFParams().isFieldEnabled(bmoWhTrack.getProductId()))
			formFlexTable.addField(2, 0, productSuggestBox, bmoWhTrack.getProductId());
		
		if (getSFParams().isFieldEnabled(bmoWhTrack.getDatemov())) {
			formFlexTable.addField(3, 0, dateMovStartDateBox, dateMovStart);
			formFlexTable.addField(4, 0, dateMovEndDateBox, dateMovEnd);
		}
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", "" + bmObjectProgramId);

		if (getSFParams().isFieldEnabled(bmoWhTrack.getDatemov())) {
			addFilter(dateMovStart, dateMovStartDateBox.getTextBox().getText());
			addFilter(dateMovEnd, dateMovEndDateBox.getTextBox().getText());
		}

		if (getSFParams().isFieldEnabled(bmoWhTrack.getProductId())) 
			addFilter(bmoWhTrack.getProductId(), productSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
