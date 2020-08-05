
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.co.PmPropertySaleDetail"%>
<%@page import="com.flexwm.shared.co.BmoPropertySaleDetail"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String programTitle = "Proceso Crear Detalle de Venta de Inmuebles";
	String programDescription = programTitle;
	String populateData = "", action = "";
	
%>

<html>
	<head>
		<title>:::<%= programTitle %>:::</title>
		<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
		<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
		<meta charset=utf-8>
	</head>
	<body class="default">
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top">
			    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
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
		     
		     PmPropertySaleDetail pmPropertySaleDetail = new PmPropertySaleDetail(sFParams);
		     
		     sql = "SELECT prsa_propertysaleid, prsa_code FROM propertysales " +
		    		 	" WHERE prsa_propertysaleid >= " + idStart +
		    		 	" AND prsa_propertysaleid <= " + idEnd;
		     pmConn2.doFetch(sql);
		     
		     int propertySaleId = 0;
		    	 
		     while (pmConn2.next()) {
		    	propertySaleId = pmConn2.getInt("prsa_propertysaleid");
		    	
		    	if (!pmPropertySaleDetail.propertySaleDetailExists(pmConn, propertySaleId)) {
		    		//System.out.println(pmConn2.getString("prsa_code"));
					BmoPropertySaleDetail bmoPropertySaleDetail = new BmoPropertySaleDetail();
					
			    	bmoPropertySaleDetail.getPropertySaleId().setValue(propertySaleId);
					pmPropertySaleDetail.saveSimple(pmConn, bmoPropertySaleDetail, bmUpdateResult);
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


