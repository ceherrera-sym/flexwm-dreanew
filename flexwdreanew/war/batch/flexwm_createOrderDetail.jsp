
<%@page import="com.flexwm.shared.op.BmoOrderDetail"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.server.op.PmOrderDetail"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.co.PmPropertySaleDetail"%>
<%@page import="com.flexwm.shared.co.BmoPropertySaleDetail"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String programTitle = "Proceso Crear Detalle de Pedido";
	String programDescription = programTitle;
	String populateData = "", action = "";
	
%>

<html>
	<head>
		<title>:::<%= programTitle %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%> %>css/<%= defaultCss %>">
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
		     
		     BmoOrder bmoOrder = new BmoOrder();
		     PmOrder pmOrder = new PmOrder(sFParams);
		     PmOrderDetail pmOrderDetail = new PmOrderDetail(sFParams);
		     
		     sql = "SELECT orde_orderid, orde_code FROM orders WHERE orde_orderid > 0";
// 		    		 	" WHERE orde_orderid >= " + idStart +
// 		    		 	" AND orde_orderid <= " + idEnd;
		     pmConn2.doFetch(sql);
		     
		     int orderId = 0;
		    	 
		     while (pmConn2.next()) {
		    	orderId = pmConn2.getInt("orde_orderid");
	    		bmoOrder  = (BmoOrder)pmOrder.get(orderId);
    			if (!pmOrderDetail.orderDetailExists(pmConn, bmoOrder.getId())) {
		 			BmoOrderDetail bmoOrderDetail = new BmoOrderDetail();
		 			bmoOrderDetail.getCloseDate().setValue(bmoOrder.getDateCreate().toString());
		 			bmoOrderDetail.getOrderDate().setValue(bmoOrder.getLockStart().toString());
		 			bmoOrderDetail.getOrderId().setValue(bmoOrder.getId());
		 			pmOrderDetail.save(pmConn, bmoOrderDetail, bmUpdateResult);	
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


