package com.flexwm.client.rpt;

import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiReport;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoProgramProfile;


public class UiProgramAccessReport extends UiReport {
	BmoProgramProfile bmoProgramProfile;

	UiSuggestBox profileSuggestBox = new UiSuggestBox(new BmoProfile());	
	UiListBox reportTypeListBox;
	String generalSection = "Filtros Generales";

	public UiProgramAccessReport(UiParams uiParams) {
		super(uiParams, new BmoProgramProfile(), "/rpt/sym/sfca_accessprofile_report.jsp", uiParams.getSFParams().getProgramTitle(new BmoProgramProfile()));
		this.bmoProgramProfile = (BmoProgramProfile)getBmObject();

	}

	@Override
	public void populateFields() {
		reportTypeListBox = new UiListBox(getUiParams());
		reportTypeListBox.addItem("Accesos por Grupo", "/rpt/sym/sfca_accessprofile_report.jsp");

		formFlexTable.addSectionLabel(0, 0, generalSection, 2);
		formFlexTable.addField(1, 0, reportTypeListBox, "Tipo de Reporte");
		formFlexTable.addField(2, 0, profileSuggestBox, bmoProgramProfile.getProfileId());
	}

	@Override
	public void setFilters() {
		addUrlFilter("programId", bmObjectProgramId);
		addFilter(bmoProgramProfile.getProfileId(), profileSuggestBox);
	}

	@Override
	public void formValueChange(String value) {
		setUrl(reportTypeListBox.getSelectedCode());
	}
}
