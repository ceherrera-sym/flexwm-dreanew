
<%@page import="com.flexwm.server.co.PmWorkContract"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso Crear WFlow del Contrato de Obra";
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
		
		String status = "";
		int idStart = 0, idEnd = 0;
		if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
		if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));
		
		try {		
			 String sql = "";
			 pmConn.disableAutoCommit();
			 pmConn2.disableAutoCommit();
			 
		     int i = 1;
		     
		   
		     PmWorkContract pmWorkContract = new PmWorkContract(sFParams);
		     BmoWorkContract bmoWorkContract =  new BmoWorkContract();
		    
		     sql = "SELECT woco_workcontractid FROM workcontracts " 
	    		 		+ " WHERE woco_workcontractid > 0 "
	    		 		+ " AND woco_wflowid IS NULL ";
		     pmConn2.doFetch(sql);
		     		    	 
		     while (pmConn2.next()) {
		    	 bmoWorkContract = (BmoWorkContract)pmWorkContract.get(pmConn2.getInt("woco_workcontractid"));
		    	 System.out.println("ID DEL CONTRATO " + bmoWorkContract.getId()) ;
		    	// Buscar el tipo de wflow, toma el primero
		 		int wFlowTypeId = new PmWFlowType(sFParams).getFirstWFlowTypeId(pmConn, bmoWorkContract.getProgramCode());
		 		if (!(wFlowTypeId > 0)) {
		 			bmUpdateResult.addError(bmoWorkContract.getCode().getName(), "Debe crearse Tipo de WFlow para Contratos de Obra.");
		 		}
		 		
		    	if (!bmUpdateResult.hasErrors()) {
		    		// Crea el WFlow y asigna el ID recien creado, siempre va estar activo este flujo
		    		BmoWFlow bmoWFlow = new BmoWFlow();
		    		PmWFlow pmWFlow = new PmWFlow(sFParams);
		    		int wFlowId = 0;
		    		
		    		// Obtener flujo y validar que el cliente tenga flujo
// 	    			wFlowId = ((BmoWFlow)pmWFlow.getBy(pmConn, bmoCustomer.getId(), bmoWFlow.getCustomerId().getName())).getId();
// 	    			if (!(wFlowId > 0)) bmUpdateResult.addError(bmoCustomer.getCode().getName(), "Debe crearse WFlow para el Cliente.");
		    		
	    			if (!(wFlowId > 0)) {
	    				/*
	    				PmConn pmConn, int wFlowTypeId, int wFlowId, String programCode, int foreignId, int userId, int companyId, int customerId, String code, String name, 
	    				String description, String startDate, String endDate, char wFlowStatus, BmUpdateResult bmUpdateResult
	    				*/
			    		wFlowId = pmWFlow.updateWFlow(pmConn, wFlowTypeId, wFlowId, 
			    				bmoWorkContract.getProgramCode(), bmoWorkContract.getId(), bmoWorkContract.getUserCreateId().toInteger(), bmoWorkContract.getCompanyId().toInteger(), 
			    				-1, bmoWorkContract.getCode().toString(), bmoWorkContract.getName().toString(), bmoWorkContract.getDescription().toString(), 
				 				SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()), 
				 				SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()), BmoWFlow.STATUS_ACTIVE, bmUpdateResult).getId();
			    		
			    		bmoWorkContract.getWFlowId().setValue(wFlowId);
			    		bmoWorkContract.getWFlowTypeId().setValue(wFlowTypeId);
				 		
			    		pmWorkContract.saveSimple(pmConn, bmoWorkContract, bmUpdateResult);
		    		}
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