
<%@page import="com.flexwm.shared.wf.BmoWFlowLog"%>
<%@page import="com.flexwm.server.wf.PmWFlowLog"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso para Insertar 1er bitacora del Cliente";
	String programDescription = programTitle;
	String populateData = "", action = "";
%>

<html>
	<head>
		<title>:::<%= programTitle %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%> %>/css/<%= defaultCss %>">
		<meta charset=utf-8>
	</head>
	<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top">
				<img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td colspan="" class="reportTitle">
			    &nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>
	
	<% 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		PmConn pmConn2 = new PmConn(sFParams);
		pmConn2.open();
		
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		
		PmWFlowLog pmWFlowLog = new PmWFlowLog(sFParams);
		BmoWFlow bmoWFlow = new BmoWFlow();
		PmWFlow pmWFlow = new PmWFlow(sFParams);
		
		String status = "";
		int idStart = 0, idEnd = 0;
		if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
		if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));
		
		try {		
			 String sql = "";
			 pmConn.disableAutoCommit();
			 pmConn2.disableAutoCommit();
			 
		     int i = 1;
		     
		     PmCustomer pmCustomer = new PmCustomer(sFParams);
		    BmoCustomer bmoCustomer =  new BmoCustomer();
		    
		     sql = "SELECT cust_customerid FROM customers " 
	    		 		+ " WHERE cust_customerid NOT IN "
		    			+ " (SELECT wflg_customerid FROM wflowlogs "
		    			+ " LEFT JOIN wflows ON (wflw_wflowid = wflg_wflowid) "	
		    			+ "	WHERE wflg_comments = 'Creación de Cliente.' "
		    			+ " AND wflw_callercode = 'CUST' ) ";

		     pmConn2.doFetch(sql);
		     
		     int projectId = 0;
		    	 
		     while (pmConn2.next()) {
		    	 bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn2.getInt("cust_customerid"));
		    	
		    	// Buscar el tipo de wflow, toma el primero
		 		int wFlowTypeId = new PmWFlowType(sFParams).getFirstWFlowTypeId(pmConn, bmoCustomer.getProgramCode());
		 		if (!(wFlowTypeId > 0)) {
		 			bmUpdateResult.addError(bmoCustomer.getCode().getName(), "Debe crearse Tipo de WFlow para Clientes.");
		 		}
		 		
				int wFlowId = ((BmoWFlow)pmWFlow.getBy(pmConn, bmoCustomer.getId(), bmoWFlow.getCustomerId().getName())).getId();
				if (!(wFlowId > 0)) bmUpdateResult.addError(bmoCustomer.getCode().getName(), "Debe crearse WFlow para el Cliente.");
				
		    	if (!bmUpdateResult.hasErrors()) {
		    		
		    		BmoWFlowLog bmoWFlowLog = new BmoWFlowLog();
		    		bmoWFlowLog.getWFlowId().setValue(wFlowId);
		    		bmoWFlowLog.getComments().setValue("Creación de Cliente.");
		    		bmoWFlowLog.getType().setValue("" + BmoWFlowLog.TYPE_WFLOW);
// 		    		if (bmoCustomer.getDateCreate().toString().equals(""))
// 			    		bmoWFlowLog.getLogdate().setValue(bmoCustomer.getDateModify().toString());
// 		    		else
		    			bmoWFlowLog.getLogdate().setValue(bmoCustomer.getDateCreate().toString());
		    		bmoWFlowLog.getUserId().setValue(bmoCustomer.getUserCreateId().toInteger());
		    		bmoWFlowLog.getUserCreateId().setValue(bmoCustomer.getUserCreateId().toInteger());

		    		// Validaciones de fechas
		    		if (bmoWFlowLog.getLogdate().toString().equals("")) {
		    			bmUpdateResult.addError(bmoCustomer.getCode().getName(), 
		    					"La bitácora no tiene una fecha. Cliente: "+bmoCustomer.getCode().toString());
		    		}

		    		pmWFlowLog.saveSimple(pmConn, bmoWFlowLog, bmUpdateResult);
		    	}
		     }
		     
		 	if (!bmUpdateResult.hasErrors()) {
		 		pmConn.commit();
			 	%>
			 		<h2><b><font color="green">&#10004; Proceso Terminado &#10004;</font></b></h2>
		 		<%	
		 	}
		%>
		<%= bmUpdateResult.errorsToString() %> 		
		<%
		} catch (Exception e) {		
			pmConn.rollback();
			throw new SFException(e.toString());
		
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		%>
	</body>
</html>