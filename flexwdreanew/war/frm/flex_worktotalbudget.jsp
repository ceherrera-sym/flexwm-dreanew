<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.flexwm.shared.co.BmoWorkItem"%>
<%@page import="com.flexwm.shared.co.BmoUnitPrice"%>
<%@page import="com.flexwm.server.co.PmWork"%>
<%@page import="com.flexwm.server.co.PmWorkItem"%>
<%@page import="com.flexwm.server.co.PmUnitPrice"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.Pair"%>
<%@include file="../inc/login_opt.jsp" %>

<html>
<head>
    <title>:::Totales del Presupuesto.:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default">
<%
	
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
	PmConn pmConn = new PmConn(sFParams);
	PmConn pmConn2 = new PmConn(sFParams);
	
	pmConn.open();
	pmConn2.open();
	
	String sql = "";
try {
	int workId = 0;	
	if (isExternal) workId = decryptId;
	else workId = Integer.parseInt(request.getParameter("foreignId"));
	
	PmWork pmWork = new PmWork(sFParams);
	
	BmoWork bmoWork = (BmoWork)pmWork.get(workId);
	
	BmoCompany bmoCompany = new BmoCompany();
	PmCompany pmCompany = new PmCompany(sFParams);
	bmoCompany = (BmoCompany)pmCompany.get(bmoWork.getCompanyId().toInteger());
	
	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	String logoURL ="";
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();
   
 %>   
   
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
    <tr>
	    <td align="left" width="30%" align="center" class="reportSubTitle">
		    Obra: <%= bmoWork.getName().toString() %>, 
				Etapa: <%= bmoWork.getBmoDevelopmentPhase().getName().toString() %>,						    			
				Responsable: <%= bmoWork.getBmoUser().getCode().toString() %>,
				Empresa: <%= bmoWork.getBmoCompany().getLegalRep().toString() %>					
		</td>
		<td align="center" width="30%" class="reportSubTitle">
			Totales del Presupuesto<br>
	    <span class="reportSubTitle">Elaborada el <%= bmoWork.getDateCreate().toString() %></span>
		</td>
        <td align="right" width="" rowspan="6" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
        </td>        
    </tr>
</table>

<%
					
		sql =  " select * from workitems ";		
		sql += " left join works on (wkit_workid = work_workid) ";
		sql += " left join typecostcenters on (work_typecostcenterid = tycc_typecostcenterid) ";
		sql += " left join unitprices on (wkit_unitpriceid = unpr_unitpriceid) ";
		sql += " left join units on (unpr_unitid = unit_unitid) ";
		sql += " left join currencies on (unpr_currencyid = cure_currencyid) ";		
		sql += " where wkit_workid = " + bmoWork.getId();
		sql += " order by unpr_code";		 
		pmConn.doFetch(sql);
		double total = 0;
	
%>
<table border="0" cellspacing="0" width="100%" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
</table>

<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
		<TR class="">
			<td width="2%" align="left" class="reportHeaderCell">
				&nbsp;#
			</td>
			<td class="reportHeaderCell">C.C</td>
			<td class="reportHeaderCell">Clave</td>			
			<td class="reportHeaderCell">Descripci&oacute;n</td>
			<td class="reportHeaderCell">Unidad</td>
			<td class="reportHeaderCell">Cantidad</td>			
			<td class="reportHeaderCellRight">Precio</td>
			<td class="reportHeaderCellRight">Importe</td>
		</TR>
<%
		int i = 1;
		double sumTotal = 0;
		while (pmConn.next()) {
			sumTotal += pmConn.getDouble("wkit_amount");%>			
			<TR class="reportCellEven">  
				<%=HtmlUtil.formatReportCell(sFParams, "" + i,BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmConn.getString("typecostcenters", "tycc_name"),BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmConn.getString("unitprices", "unpr_code"),BmFieldType.CODE)%>								
				<%=HtmlUtil.formatReportCell(sFParams, pmConn.getString("unitprices", "unpr_description"),BmFieldType.CODE)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmConn.getString("units", "unit_code"),BmFieldType.CODE)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + pmConn.getDouble("wkit_quantity"),BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + pmConn.getDouble("wkit_price"),BmFieldType.CURRENCY)%>				
				<%=HtmlUtil.formatReportCell(sFParams, "" + pmConn.getDouble("wkit_amount"),BmFieldType.CURRENCY)%>			
			</TR>	
<%			
		i++;
		}
		
%>
	<tr class="" width="100%">
		<td align="left" colspan="7">&nbsp;</td>
	</tr>
	<tr class="" width="100%" class="reportCellEven">
		<td class="" align="left" colspan="7">
			Totales:
		</td>
		<%=HtmlUtil.formatReportCell(sFParams, "" + sumTotal,BmFieldType.CURRENCY)%>						
	</tr>	

<%  } catch (Exception e) { 
    String errorLabel = "Error en Los Totales del Presupuesto";
    String errorText = "Los Totales del Presupuesto no pudo ser desplegado exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    } finally {
    	pmConn2.close();
    	pmConn.close();
    }
%>
</body>
</html>
