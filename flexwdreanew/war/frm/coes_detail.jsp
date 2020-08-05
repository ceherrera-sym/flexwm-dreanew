<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoContractEstimation"%>
<%@page import="com.flexwm.shared.co.BmoEstimationItem"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
<%@page import="com.flexwm.shared.co.BmoContractConceptItem"%>
<%@page import="com.flexwm.server.co.PmContractEstimation"%>
<%@page import="com.flexwm.server.co.PmEstimationItem"%>
<%@page import="com.flexwm.server.co.PmWorkContract"%>
<%@page import="com.flexwm.server.co.PmContractConceptItem"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.Pair"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<html>
<head>
    <title>:::Detalle Estimacion.:::</title>
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
	int contractEstimationId = 0;	
	if (isExternal) contractEstimationId = decryptId;
	else contractEstimationId = Integer.parseInt(request.getParameter("foreignId"));
	
	PmContractEstimation pmContractEstimation = new PmContractEstimation(sFParams);	
	BmoContractEstimation bmoContractEstimation = (BmoContractEstimation)pmContractEstimation.get(contractEstimationId);
	
	PmWorkContract pmWorkContract = new PmWorkContract(sFParams);
	BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(bmoContractEstimation.getWorkContractId().toInteger());
	
 %>   
   
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
    <tr>
	    <td align="left" align="center" class="reportSubTitle">
		    					
		</td>
		<td align="center" class="documentTitle">
			<b>Hoja de Presupuesto</b><br>
	    <span class="reportSubTitle">Elaborada el</span>
		</td>
        <td align="right" rowspan="6" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
        </td>        
    </tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">	
	<tr>       
	   <td class="documentLabel">
	       &nbsp;No.Contrato 
	   </td>
	   <td class="documentText">&nbsp;
			<%= bmoContractEstimation.getBmoWorkContract().getCode().toString() %>
	   </td>
	   <td class="documentLabel">
	       &nbsp;Contratista: 
	   </td>
	   <td class="documentText">&nbsp;
	   		<%= bmoWorkContract.getBmoSupplier().getName().toString() %>
	   </td>
	</tr>
	<tr>       
	   <td class="documentLabel">
	       &nbsp;Empresa 
	   </td>
	   <td class="documentText">&nbsp;
			<%= bmoWorkContract.getBmoCompany().getLegalName().toString() %>
	   </td>
	   <td class="documentLabel">
	       &nbsp;Descripcion Obra 
	   </td>
	   <td class="documentText">&nbsp;
	   		<%= bmoWorkContract.getDescription().toString() %>
	   </td>
	</tr>
</table>	
<table border="1" cellspacing="0" width="100%" cellpadding="0" class="report">
		<TR class="">
			<td width="2%" align="left" class="reportHeaderCell">
				&nbsp;#
			</td>				
			<td class="reportHeaderCell">Clave</td>					
			<td class="reportHeaderCell">Nombre</td>
			<td class="reportHeaderCell">Total</td>
			<td class="reportHeaderCell">Cantidad</td>
			<td class="reportHeaderCell">Anterior</td>
			<td class="reportHeaderCell">Acumulado</td>
			<td class="reportHeaderCellRight">Precio</td>
			<td class="reportHeaderCellRight">Importe</td>
		</TR>
<%
		PmContractConceptItem pmContractConceptItem = new PmContractConceptItem(sFParams);
		BmoContractConceptItem bmoContractConceptItem;
		
		BmoEstimationItem bmoEstimationItem = new BmoEstimationItem();
		PmEstimationItem pmEstimationItem = new PmEstimationItem(sFParams);
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoEstimationItem.getKind(), bmoEstimationItem.getContractEstimationId().getName(), contractEstimationId);
		Iterator<BmObject> estimationItems = pmEstimationItem.list(bmFilter).iterator();
		int i = 1;
		while (estimationItems.hasNext()) {
			bmoEstimationItem = (BmoEstimationItem)estimationItems.next();
			bmoContractConceptItem = (BmoContractConceptItem)pmContractConceptItem.get(bmoEstimationItem.getContractConceptItemId().toInteger());
%>			
			<TR class="reportCellEven">				
				<%=HtmlUtil.formatReportCell(sFParams, "" + i++,BmFieldType.NUMBER)%>				
				<%=HtmlUtil.formatReportCell(sFParams, bmoEstimationItem.getBmoContractConceptItem().getCode().toString(),BmFieldType.CODE)%>
				<%=HtmlUtil.formatReportCell(sFParams, bmoEstimationItem.getBmoContractConceptItem().getName().toString(),BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getQuantityTotal().toDouble(),BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getQuantity().toDouble(),BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getQuantityLast().toDouble(),BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getQuantityReceipt().toDouble(),BmFieldType.NUMBER)%>				
				<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getPrice().toDouble(),BmFieldType.CURRENCY)%>				
				<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getSubTotal().toDouble(),BmFieldType.CURRENCY)%>		
        	</TR>
<%			
		}		
%>
	<tr class="" width="100%">
		<td align="left" colspan="7">&nbsp;</td>
	</tr>
	<tr class="" width="100%" class="reportCellEven">
		<td class="" align="left" colspan="6">
			Totales:
		</td>
		<%=HtmlUtil.formatReportCell(sFParams, "" + bmoContractEstimation.getTotal().toDouble(),BmFieldType.CURRENCY)%>						
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="report">
	<tr>
		<td class="detailText" style="font-size: 5pt;" align="center" colspan="7">
			&nbsp;
		</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="">
	<tr>
		<td align="center" class="documentTitleCaption">
			<b>Revis&oacute;</b>
		</td>
		<td align="center">
			&nbsp;
		</td>
		<td align="center" class="documentTitleCaption">
			<b>Valid&oacute;</b>
		</td>
	</tr>
	<tr>
		<td>&nbsp;<td>
	</tr>
	<tr>
		<td align="center"><br><br>
		    _______________________________________
		</td>
		<td align="center">
			&nbsp;
		</td>
		<td align="center"><br><br>
		    _______________________________________
		</td>
	</tr>
	<tr>
	<td align="center" class="documentTitleCaption">
		<b>
			<%//=bmoContractEstimation.getBmoUser().getFirstname().toString() %>
			&nbsp;
			<%//=bmoContractEstimation.getBmoUser().getFatherlastname().toString() %>
		</b>
	</td>
	<td align="center">
		&nbsp;
	</td>
	<td align="center" class="documentTitleCaption">
		<b>Luis Orozco</b>
	</td>
</tr>
</table>


<%  } catch (Exception e) { 
    String errorLabel = "Error en la Hoja de Presupuesto";
    String errorText = "La Hoja de Presupuesto no pudo ser desplegado exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    } finally {
    	pmConn2.close();
    	pmConn.close();
    }
%>
</body>
</html>
