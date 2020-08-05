package com.flexwm.client.rpt;

import java.sql.Types;

import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoProperty;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.sf.BmoTag;


public class UiPropertyReport extends UiReport {

	BmoProperty bmoProperty;
	BmoDevelopment bmoDevelopment = new BmoDevelopment();
	UiSuggestBox developmentIdUiSuggestBox = new UiSuggestBox(new BmoDevelopment());
	UiSuggestBox developmentPhaseUiSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
	UiSuggestBox developmentBlockUiSuggestBox = new UiSuggestBox(new BmoDevelopmentBlock());
	UiListBox isActivateListBox = new UiListBox(getUiParams()); // Desarrollo activo
	UiListBox isOpenUiListBox = new UiListBox(getUiParams()); // Manzana abierta
	UiListBox availableUiListBox = new UiListBox(getUiParams()); //Viv. Disponible
	UiListBox habitabilityUiListBox = new UiListBox(getUiParams()); //Viv. Habitabilidad
	UiListBox canSellListBox = new UiListBox(getUiParams());	 //Viv. Abierta, se puede vender
	UiDateBox finishDateDateBox = new UiDateBox();
	UiDateBox finishDateEndDateBox = new UiDateBox();
	BmField finishDateEnd;
	UiListBox tagsListBox;

	UiListBox reportTypeListBox;
	String generalSection = "Filtros Generales";
	String propertySection = "Filtros Inmuebles";
	String propertyLabel = "Inmuebles";


	public UiPropertyReport(UiParams uiParams) {
		super(uiParams, new BmoProperty(), "/rpt/co/prty_property_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoProperty()));
		this.bmoProperty = (BmoProperty)getBmObject();		
		finishDateEnd = new BmField("finishdateend", "", "Fecha Term. Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		propertyLabel =  getSFParams().getProgramTitle(bmoProperty.getProgramCode());
		propertySection = "Filtros " + propertyLabel;

	}

	@Override
	public void populateFields() {
		tagsListBox = new UiListBox(getUiParams(), new BmoTag());
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Reporte " + propertyLabel, "/rpt/co/prty_property_report.jsp");	
		reportTypeListBox.addItem(propertyLabel + " por Etapa", "/rpt/co/prty_phase_report.jsp");

		availableUiListBox.addItem("Todos", "2");
		availableUiListBox.addItem("Si", "1"); 
		availableUiListBox.addItem("No", "0"); 

		isOpenUiListBox.addItem("Todos", "2");
		isOpenUiListBox.addItem("Si", "1"); 
		isOpenUiListBox.addItem("No", "0"); 

		isActivateListBox.addItem("Todos", "2");
		isActivateListBox.addItem("Activo", "1");
		isActivateListBox.addItem("Inactivo", "0");

		habitabilityUiListBox.addItem("Todos", "2");
		habitabilityUiListBox.addItem("Si", "1");
		habitabilityUiListBox.addItem("No", "0");

		canSellListBox.addItem("Todos", "2");
		canSellListBox.addItem("Si", "1"); 
		canSellListBox.addItem("No", "0");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, developmentIdUiSuggestBox, bmoProperty.getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId());
		formFlexTable.addField(3, 0, isActivateListBox, bmoDevelopment.getIsActivate());
		formFlexTable.addField(4, 0, developmentPhaseUiSuggestBox, bmoProperty.getBmoDevelopmentBlock().getDevelopmentPhaseId());
		formFlexTable.addField(5, 0, developmentBlockUiSuggestBox, bmoProperty.getDevelopmentBlockId());
		formFlexTable.addField(6, 0, isOpenUiListBox, bmoProperty.getBmoDevelopmentBlock().getIsOpen());
		formFlexTable.addField(7, 0, tagsListBox,bmoProperty.getTags(),true);
		formFlexTable.addSectionLabel(8, 0, propertySection, 2);
		formFlexTable.addField(9, 0, finishDateDateBox, bmoProperty.getFinishDate());
		formFlexTable.addField(10, 0, finishDateEndDateBox, finishDateEnd);
		formFlexTable.addField(11, 0, availableUiListBox, bmoProperty.getAvailable());		
		formFlexTable.addField(12, 0, habitabilityUiListBox, bmoProperty.getHabitability());
		formFlexTable.addField(13, 0, canSellListBox, bmoProperty.getCansell());

		formFlexTable.hideSection(propertySection);
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoProperty.getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId(), developmentIdUiSuggestBox);
		addFilter(bmoProperty.getDevelopmentBlockId(), developmentBlockUiSuggestBox);
		addFilter(bmoProperty.getBmoDevelopmentBlock().getDevelopmentPhaseId(), developmentPhaseUiSuggestBox);
		addFilter(bmoDevelopment.getIsActivate(), isActivateListBox);
		addFilter(bmoProperty.getFinishDate(), finishDateDateBox.getTextBox().getText());
		addFilter(finishDateEnd, finishDateEndDateBox.getTextBox().getText());
		addFilter(bmoProperty.getBmoDevelopmentBlock().getIsOpen(), isOpenUiListBox);
		addFilter(bmoProperty.getAvailable(), availableUiListBox);
		addFilter(bmoProperty.getCansell(), canSellListBox);
		addFilter(bmoProperty.getHabitability(), habitabilityUiListBox);
		addFilter(bmoProperty.getTags(), tagsListBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
