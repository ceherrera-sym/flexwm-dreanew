package com.flexwm.client.rpt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.co.BmoDevelopment;
import com.flexwm.shared.co.BmoDevelopmentBlock;
import com.flexwm.shared.co.BmoDevelopmentPhase;
import com.flexwm.shared.co.BmoPropertyModel;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertyType;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowType;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmFilter;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoTag;
import com.symgae.shared.sf.BmoUser;

public class UiPropertySalesReportG100 extends UiReport{
	
	BmoPropertySale bmoPropertySale;
	BmoPropertyType bmoPropertyType = new BmoPropertyType();

	UiListBox wFlowPhaseListBox;
	UiListBox wFlowTypeListBox;
	UiSuggestBox developmentIdUiSuggestBox = new UiSuggestBox(new BmoDevelopment());
	UiSuggestBox developmentPhaseUiSuggestBox = new UiSuggestBox(new BmoDevelopmentPhase());
	UiSuggestBox developmentBlockUiSuggestBox = new UiSuggestBox(new BmoDevelopmentBlock());
	UiSuggestBox propertyModelUiSuggestBox = new UiSuggestBox(new BmoPropertyModel());
	UiListBox propertyTypeListBox;
	UiListBox statusListBox = new UiListBox(getUiParams());
	UiListBox typeListBox = new UiListBox(getUiParams());
	UiListBox temporalDevelopmentBlockListBox;
	UiListBox habitabilityListBox;
	UiSuggestBox userIdUiSuggestBox = new UiSuggestBox(new BmoUser());
//	UiListBox profileListBox;
	UiListBox phaseListBox;
	UiDateBox saleDateStartDateBox = new UiDateBox();
	UiDateBox saleDateEndDateBox = new UiDateBox();
	BmField saleDateEnd;
	UiDateBox phaseStartDateBox = new UiDateBox();
	UiDateBox phaseEndDateBox = new UiDateBox();
	BmField phaseStartDate;
	BmField phaseEndDate;
//	BmField profileId;
	UiListBox reportTypeListBox;
	BmField byPhase;
	UiDateBox deliveryDateStartDateBox = new UiDateBox();
	UiDateBox deliveryDateEndDateBox = new UiDateBox();
	BmField deliveryDateEnd;

	UiDateBox cancellDateStartDateBox = new UiDateBox();
	UiDateBox cancellDateEndDateBox = new UiDateBox();
	BmField cancellDateEnd;
	UiListBox paymentStatusListBox = new UiListBox(getUiParams());
	BmField paymentStatus;
	UiListBox tagsListBox;
	public static final char PAYMENTSTATUS_REVISION = 'R';
	public static final char PAYMENTSTATUS_PARTIAL = 'P';
	public static final char PAYMENTSTATUS_TOTAL = 'T';

	String generalSection = "Filtros Generales";
	String datesSection = "Filtros Fechas";
	String propertySection = "Filtros de Inmueble";
	String statusSection = "Filtros de Estatus";
	
	
	public UiPropertySalesReportG100(UiParams uiParams) {
		super(uiParams, new BmoPropertySale(), "/rpt/co/prsa_propertysales_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoPropertySale()));
		this.bmoPropertySale = (BmoPropertySale)getBmObject();		

//		profileId = new BmField("profileid", "", "Perfil", 10, Types.INTEGER, true, BmFieldType.ID, false);
		saleDateEnd = new BmField("saledateend", "", "F. Venta Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		deliveryDateEnd = new BmField("deliverydateend", "", "F. Entrega Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);

		phaseStartDate = new BmField("phasestartdate", "", "F.Fase ", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		phaseEndDate = new BmField("phaseenddate", "", "F.Fase Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		//cancellDateStart = new BmField("cancelldatestart", "", "F.Cancelación ", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		cancellDateEnd = new BmField("cancelldateend", "", "F.Cancelación Fin", 10, Types.VARCHAR, true, BmFieldType.DATE, false);
		byPhase = new BmField("byphase", "", "Por Fase", 10, Types.VARCHAR, true, BmFieldType.STRING, false);
		paymentStatus = new BmField("paymentStatus", "", "Estatus Pago", 10, Types.CHAR, false, BmFieldType.OPTIONS, false);
		paymentStatus.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(PAYMENTSTATUS_REVISION, "Pendiente", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_paymentstatus_revision.png")),
				new BmFieldOption(PAYMENTSTATUS_PARTIAL, "Pago Parcial", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_paymentstatus_partialpayment.png")),
				new BmFieldOption(PAYMENTSTATUS_TOTAL, "Pago Total", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/racc_paymentstatus_totalpayment.png"))				
				)));
	}
	 @Override
	public void populateFields() {

			reportTypeListBox = new UiListBox(getUiParams());
			tagsListBox = new UiListBox(getUiParams(), new BmoTag());
			reportTypeListBox.addItem("Reporte de Ventas", "/rpt/co/prsa_propertysales_report.jsp");
			reportTypeListBox.addItem("Reporte de Gestoria", "/rpt/co/prsa_propertysales_gestoria_reportG.jsp");	
			reportTypeListBox.addItem("Reporte de ventas de oc", "/rpt/co/prsa_propertysales_oc_report.jsp");	
			reportTypeListBox.addItem("Reporte Cuenta Cliente", "/rpt/co/prsa_propertysales_custaccount_report.jsp");
			reportTypeListBox.addItem("Reporte Admin. Comercial", "/rpt/co/prsa_propertysales_admincom_reportG.jsp");
			reportTypeListBox.addItem("Reporte Fechas Fase Finalizada", "/rpt/co/prsa_phasedate_report.jsp");
			reportTypeListBox.addItem("Reporte de Ventas por Inmueble", "/rpt/co/prsa_propertySales_prty_report.jsp");
			reportTypeListBox.addItem("Reporte de Cobranza Ventas por Inmueble", "/rpt/co/prsa_raccounts_g100_report.jsp");

			propertyTypeListBox = new UiListBox(getUiParams(), new BmoPropertyType());
			temporalDevelopmentBlockListBox = new UiListBox(getUiParams());
			habitabilityListBox = new UiListBox(getUiParams());
			phaseListBox = new UiListBox(getUiParams()); 
//			profileListBox = new UiListBox(getUiParams(), new BmoProfile());

			temporalDevelopmentBlockListBox.addItem("Todos", "2");
			temporalDevelopmentBlockListBox.addItem("Si", "1");
			temporalDevelopmentBlockListBox.addItem("No", "0");

			habitabilityListBox.addItem("Todos", "2");
			habitabilityListBox.addItem("Si", "1");
			habitabilityListBox.addItem("No", "0");

			phaseListBox.addItem("Todas", "all");
			phaseListBox.addItem("Actual", "present");
			phaseListBox.addItem("Antes de", "before");
			phaseListBox.addItem("Finalizada", "finally");

			// Filtros en los listbox
			// Tipo de flujo
			BmoWFlowType bmoWFlowType = new BmoWFlowType();
			BmFilter filterWFlowType = new BmFilter();
			filterWFlowType.setValueFilter(bmoWFlowType.getKind(), bmoWFlowType.getBmoWFlowCategory().getProgramId(), bmObjectProgramId);		
			wFlowTypeListBox = new UiListBox(getUiParams(), new BmoWFlowType(), filterWFlowType);

			// Fase de flujo
			BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
			BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
			BmFilter filterWFlowPhase = new BmFilter();
			filterWFlowPhase.setInFilter(bmoWFlowCategory.getKind(), bmoWFlowPhase.getWFlowCategoryId().getName(), 	bmoWFlowCategory.getIdFieldName(),
					bmoWFlowCategory.getProgramId().getName(), "" + bmObjectProgramId);
			wFlowPhaseListBox = new UiListBox(getUiParams(), new BmoWFlowPhase(), filterWFlowPhase);
			// si hay empresa maestra seleccionada
			if(getUiParams().getSFParams().getSelectedCompanyId() > 0) {
				BmFilter developmentBlockByCompany = new BmFilter();
				BmoDevelopmentPhase bmoDevelopmentPhase = new  BmoDevelopmentPhase();
				developmentBlockByCompany.setValueFilter(bmoDevelopmentPhase.getKind(),
						bmoDevelopmentPhase.getCompanyId(), getUiParams().getSFParams().getSelectedCompanyId());
				
				developmentBlockUiSuggestBox.addFilter(developmentBlockByCompany);
			}
			formFlexTable.addSectionLabel(0, 0, generalSection, 2);
			formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
			formFlexTable.addField(2, 0, phaseListBox, byPhase);
			formFlexTable.addField(3, 0, wFlowPhaseListBox, bmoPropertySale.getBmoWFlow().getWFlowPhaseId());
			formFlexTable.addField(4, 0, wFlowTypeListBox, bmoPropertySale.getWFlowTypeId());
			formFlexTable.addField(5, 0, typeListBox, bmoPropertySale.getType());
			formFlexTable.addField(6, 0, userIdUiSuggestBox, bmoPropertySale.getSalesUserId());
//			formFlexTable.addField(7, 0, profileListBox, profileId);
			formFlexTable.addField(8, 0, tagsListBox,bmoPropertySale.getTags(),true);
			
			formFlexTable.addSectionLabel(9, 0, propertySection, 2);
			formFlexTable.addField(10, 0, developmentIdUiSuggestBox, bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId());
			formFlexTable.addField(11, 0, developmentPhaseUiSuggestBox, bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId());
			formFlexTable.addField(12, 0, developmentBlockUiSuggestBox, bmoPropertySale.getBmoProperty().getDevelopmentBlockId());
			formFlexTable.addField(13, 0, propertyModelUiSuggestBox, bmoPropertySale.getBmoProperty().getPropertyModelId());
			formFlexTable.addField(14, 0, propertyTypeListBox, bmoPropertyType.getIdField()); 
			formFlexTable.addField(15, 0, temporalDevelopmentBlockListBox, bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getIsTemporally());
			formFlexTable.addField(16, 0, habitabilityListBox, bmoPropertySale.getBmoProperty().getHabitability());	

			formFlexTable.addSectionLabel(17, 0, datesSection, 2);
			formFlexTable.addField(18, 0, saleDateStartDateBox, bmoPropertySale.getStartDate());
			formFlexTable.addField(19, 0, saleDateEndDateBox, saleDateEnd);
			formFlexTable.addField(20, 0, deliveryDateStartDateBox, bmoPropertySale.getEndDate());
			formFlexTable.addField(21, 0, deliveryDateEndDateBox, deliveryDateEnd);
			formFlexTable.addField(22, 0, phaseStartDateBox, phaseStartDate);
			formFlexTable.addField(23, 0, phaseEndDateBox, phaseEndDate);
			formFlexTable.addField(24, 0, cancellDateStartDateBox, bmoPropertySale.getCancellDate());
			formFlexTable.addField(25, 0, cancellDateEndDateBox, cancellDateEnd);
			
			formFlexTable.addSectionLabel(26, 0, statusSection, 2);
			formFlexTable.addField(27, 0, paymentStatusListBox, paymentStatus, true);
			paymentStatusListBox.setSelectedIndex(-1);
			formFlexTable.addField(28, 0,  statusListBox, bmoPropertySale.getStatus(), true);
			statusListBox.setSelectedIndex(-1);

			formFlexTable.hideSection(propertySection);
			formFlexTable.hideSection(datesSection);
			formFlexTable.hideSection(statusSection);

		
	}
	 @Override
		public void setFilters() {

			addUrlFilter("programId", bmObjectProgramId);
			addFilter(bmoPropertySale.getBmoWFlow().getWFlowPhaseId(), wFlowPhaseListBox);
			addFilter(bmoPropertySale.getWFlowTypeId(), wFlowTypeListBox);
			addFilter(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId(), developmentIdUiSuggestBox);	
			addFilter(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getDevelopmentPhaseId(), developmentPhaseUiSuggestBox);		
			addFilter(bmoPropertySale.getBmoProperty().getDevelopmentBlockId(), developmentBlockUiSuggestBox);		
			addFilter(bmoPropertySale.getBmoProperty().getPropertyModelId(), propertyModelUiSuggestBox);	
			addFilter(bmoPropertyType.getIdField(), propertyTypeListBox);	
			addFilter(bmoPropertySale.getStatus(), statusListBox);	
			addFilter(bmoPropertySale.getType(), typeListBox);	
			addFilter(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getIsTemporally(), temporalDevelopmentBlockListBox);
			addFilter(bmoPropertySale.getBmoProperty().getHabitability(), habitabilityListBox);		
			addFilter(bmoPropertySale.getStartDate(), saleDateStartDateBox.getTextBox().getText());
			addFilter(saleDateEnd, saleDateEndDateBox.getTextBox().getText());
			addFilter(bmoPropertySale.getEndDate(), deliveryDateStartDateBox.getTextBox().getText());
			addFilter(deliveryDateEnd, deliveryDateEndDateBox.getTextBox().getText());
			addFilter(phaseStartDate, phaseStartDateBox.getTextBox().getText());
			addFilter(phaseEndDate, phaseEndDateBox.getTextBox().getText());
//			addFilter(profileId, profileListBox);
			addFilter(bmoPropertySale.getSalesUserId(), userIdUiSuggestBox);
			addFilter(byPhase, phaseListBox);
			addFilter(bmoPropertySale.getCancellDate(), cancellDateStartDateBox.getTextBox().getText());
			addFilter(cancellDateEnd, cancellDateEndDateBox.getTextBox().getText());
			addFilter(paymentStatus, paymentStatusListBox);
			addFilter(bmoPropertySale.getTags(), tagsListBox);
		
		 
	 }
	 @Override
		public void formValueChange(String value) {
			setUrl(reportTypeListBox.getSelectedCode());
		}

}
