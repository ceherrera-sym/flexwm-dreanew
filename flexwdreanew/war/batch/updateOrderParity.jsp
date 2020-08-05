
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>

<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Pedidos";
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

PmConn pmConn3 = new PmConn(sFParams);
pmConn3.open();

BmUpdateResult bmUpdateResult = new BmUpdateResult();
NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

PmCurrency pmCurrency = new PmCurrency(sFParams);

String status = "";
int idStart = 0, idEnd = 0;
if (request.getParameter("status") != null) status = request.getParameter("status");
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));



try {		
	String	sql = "";
	
	//" WHERE not orde_currencyid is null " +
    //" AND (orde_currencyparity = 0 OR orde_currencyparity is null)";
	sql = " SELECT * FROM opportunities " +	      
	      " ORDER BY oppo_opportunityid";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		
		double currencyParity = pmConn.getDouble("oppo_currencyparity"); 
		if (!(currencyParity > 0)) {
			
			String startDate = pmConn.getString("oppo_startdate");
			
			if (startDate.equals("")) {
				startDate = pmConn.getString("oppo_datecreate");
			}	
			
			String value = pmConn.getInt("oppo_currencyid") + "|" + startDate;			
			currencyParity = Double.parseDouble(pmCurrency.getParityFromCurrency(value, bmUpdateResult));			
			sql = " UPDATE opportunities SET oppo_currencyparity = " + currencyParity + ", oppo_usermodifyid = 4 " + 
				  " WHERE oppo_opportunityid = " + pmConn.getInt("oppo_opportunityid");
			pmConn2.doUpdate(sql);
			
			//Actualizar la cotizacion
			sql = " UPDATE quotes SET quot_currencyparity = " + currencyParity + ", " +
			      " quot_currencyid = " + pmConn.getInt("oppo_currencyid") + " WHERE " +
			      " quot_quoteid = " + pmConn.getInt("oppo_quoteid");
			pmConn2.doUpdate(sql);
		}
		
		//Actualizar el pedido
		sql = " SELECT * FROM orders " +
		      " WHERE orde_opportunityid = " + pmConn.getInt("oppo_opportunityid");		      
		pmConn3.doFetch(sql);
		if (pmConn3.next()) {			
			if (!(pmConn3.getDouble("orde_currencyparity") > 0 )) {
				//Actualizar la paridad de la oportunidad
				sql = " UPDATE orders SET orde_currencyparity = " + currencyParity + ", orde_usermodifyid = 4 " +			       
				      " WHERE orde_orderid = " + pmConn3.getInt("orde_orderid");
				pmConn2.doUpdate(sql);
			}	
		}		
	}
	
	
	//Actualizar el pedido
	sql = " SELECT * FROM orders " +
	      " WHERE (orde_opportunityid is null OR orde_opportunityid = 0)";	      		      
	pmConn3.doFetch(sql);
	while (pmConn3.next()) {
		double currencyParity = pmConn3.getDouble("orde_currencyparity");
		
		if (!(currencyParity > 0 )) {
			//Actualizar la paridad de la oportunidad
			String value = pmConn3.getInt("orde_currencyid") + "|" + pmConn3.getString("orde_lockstart");			
			currencyParity = Double.parseDouble(pmCurrency.getParityFromCurrency(value, bmUpdateResult));
			System.out.println("currencyParity " + currencyParity);
			sql = " UPDATE orders SET orde_currencyparity = " + currencyParity + ", orde_usermodifyid = 4 " +			       
			      " WHERE orde_orderid = " + pmConn3.getInt("orde_orderid");
			pmConn2.doUpdate(sql);
		}	
	}	
	
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {
	throw new SFException(e.toString());

} finally {
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
	
	
}
	


%>

</table>
</body>
</html>
