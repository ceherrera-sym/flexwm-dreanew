package com.flexwm.client.rpt;

import com.flexwm.shared.cm.BmoOpportunity;
import com.symgae.client.chart.UiChart;
import com.symgae.client.chart.UiChartBar;
import com.symgae.client.ui.UiGraphFilter;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.sf.BmoUser;


public class UiOpportunityGraph extends UiGraphFilter {
	BmoOpportunity bmoOpportunity;
	UiListBox userListBox = new UiListBox(getUiParams(), new BmoUser());
	UiChart uiChart;

	public UiOpportunityGraph(UiParams uiParams) {
		super(uiParams, new BmoOpportunity(), "Gráfica de Oportunidades");
		bmoOpportunity = (BmoOpportunity)getBmObject();
	}

	@Override
	public void populateFields() {
		formFlexTable.addField(1, 0, userListBox, bmoOpportunity.getUserId());
		formFlexTable.addFieldEmpty(1, 2);
	}
	
	@Override
	public void generateGraph() {
		String filterUser = "";
		if (!userListBox.getSelectedId().equals("")) filterUser = " AND oppo_userid = " + userListBox.getSelectedId();
		
		String sql = "SELECT user_email as Vendedor, COUNT(oppo_opportunityid) as Oportunidades FROM opportunities "
				+ " LEFT JOIN wflows ON (oppo_wflowid = wflw_wflowid)"
				+ " LEFT JOIN wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid)"
				+ " LEFT JOIN users ON (oppo_userid = user_userid)"
				+ " WHERE oppo_opportunityid > 0 "
				+ filterUser
				+ " GROUP BY user_email";
		
		uiChart = new UiChartBar(getUiParams(), graphPanel, titleLabel.getText(), sql);
		uiChart.show();
	}
}
