
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.co.PmWorkContract"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Actualizar estatus de Wflow";
	String programDescription = programTitle;
	String populateData = "", action = "",sql = "";
	PmConn pmConn = new PmConn(sFParams);
	PmWorkContract pmWorkContract = new PmWorkContract(sFParams);
	BmoWorkContract bmoWorkContract = new BmoWorkContract();
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmWFlow pmWFlow = new PmWFlow(sFParams);
	BmUpdateResult bmUpdateResult = new BmUpdateResult();
	  sql = "SELECT woco_workcontractid FROM workcontracts " 
		 		+ " WHERE woco_workcontractid > 0 "
		 		+ " AND woco_wflowid IS NOT NULL AND (woco_status = '"+BmoWorkContract.STATUS_CANCEL+"' OR woco_status = '" +BmoWorkContract.STATUS_CLOSED +"')";
	pmConn.open();
	int wFlowTypeId = new PmWFlowType(sFParams).getFirstWFlowTypeId(pmConn, bmoWorkContract.getProgramCode());
	pmConn.doFetch(sql);
	
	
	while(pmConn.next()){
		 bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn.getInt("woco_workcontractid"));
		 bmoWFlow = (BmoWFlow)pmWFlow.get(bmoWorkContract.getWFlowId().toInteger());
		 bmoWFlow.getStatus().setValue(BmoWFlow.STATUS_INACTIVE);
		 pmWFlow.save(bmoWFlow, bmUpdateResult);
		 System.out.println(bmUpdateResult.getMsg());
		
	
	}
	pmConn.close();

%>
