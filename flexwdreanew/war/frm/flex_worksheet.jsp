<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.flexwm.shared.co.BmoWorkItem"%>
<%@page import="com.flexwm.shared.co.BmoUnitPrice"%>
<%@page import="com.flexwm.server.co.PmWork"%>
<%@page import="com.flexwm.server.co.PmWorkItem"%>
<%@page import="com.flexwm.server.co.PmUnitPrice"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.Pair"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "Hoja de Presupuesto";
	
	BmoWork bmoWork = new BmoWork();
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoWork.getProgramCode());	
%>
<html>
<% 
// Imprimir
String print = "0", permissionPrint = "";
if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");

// Exportar a Excel
String exportExcel = "0";
if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
	response.setContentType("application/vnd.ms-excel");
	response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
}

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
//En caso de que mande a imprimir, deshabilita contenido
if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
<style> 
	body{
		user-select: none;
		-moz-user-select: none; 
		-o-user-select: none; 
		-webkit-user-select: none; 
		-ie-user-select: none; 
		-khtml-user-select:none; 
		-ms-user-select:none; 
		-webkit-touch-callout:none
	}
</style>
<style type="text/css" media="print">
    * { display: none; }
</style>
<%
permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
	//Mensaje 
	if(print.equals("1") || exportExcel.equals("1")) { %>
		<script>
			alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
		</script>
	<% }
}
 
%>
<head>
    <title>:::<%= title %>:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %>>
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
	bmoWork = (BmoWork)pmWork.get(workId);
	
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
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="5%" rowspan="5" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="4" class="reportTitleCell">
			Hoja de Presupuesto
		</td>
	</tr>
	<tr>
		<th align = "left" class="reportCellEven">Elaborada el:</th>
		<td class="reportCellEven">
			<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>                
		</td>
		<th align = "left" class="reportCellEven">C&oacute;digo del Documento:</th>
		<td class="reportCellEven">
			FO-07.5.2.1-1 (30-Sep-04)
		</td>
	</tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td colspan="4" class="reportHeaderCell">			
			Informaci&oacute;n de la Obra
		</td>
	</tr>
	<tr>
	      <th align = "left" class="reportCellEven">No. Obra:</th>
	      <td class="reportCellEven">
	           <%= bmoWork.getCode().toString()%>                 
	      </td>
	      <th align = "left" class="reportCellEven">Descripci&oacute;n Obra:</th>
	      <td class="reportCellEven">
	           <%= bmoWork.getDescription().toString()%>                 
	      </td>
	</tr>
	<tr>
		<th class="reportCellEven" align ="left" colspan="">Etapa:</th>
		<td class="reportCellEven" align ="left" colspan=""><%= bmoWork.getBmoDevelopmentPhase().getName().toString() %></td>
	    <th align = "left" class="reportCellEven">&nbsp;</th>
	    <td class="reportCellEven">
	         &nbsp;                
	    </td>	   
	</tr>
	<tr>
	    <th align = "left" class="reportCellEven">Empresa:</th>
	    <td class="reportCellEven">
	         <%= bmoWork.getBmoCompany().getLegalName().toString()%>                 
	    </td>
	    <th align = "left" class="reportCellEven">Responsable:</th>
	    <td class="reportCellEven">
	    	<%= bmoWork.getBmoUser().getFirstname().toString() + 
	    	" " + bmoWork.getBmoUser().getFatherlastname().toString()+ 
	    	" " + bmoWork.getBmoUser().getMotherlastname().toString() %> 
    	</td>
	</tr>
	<tr>
	    <th align = "left" class="reportCellEven">Fecha Obra:</th>
	    <td class="reportCellEven">
	    	<%= bmoWork.getStartDate().toString() %>               
	    </td>
	    <th align = "left" class="reportCellEven">Estatus:</th>
	    <td class="reportCellEven">
	    	<%= bmoWork.getStatus().getSelectedOption().getLabel() %>          
	    </td>
	</tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 12px">
		<TR class="reportCellEven">
			<th width="1%" align="" class="reportHeaderCell">#</th>				
			<th class="reportHeaderCell">Clave</th>					
			<th class="reportHeaderCell">Descripci&oacute;n</th>			
			<th class="reportHeaderCell">Unidad</th>
			<th class="reportHeaderCellRight">Cantidad&nbsp;&nbsp;&nbsp;</th>
			<th class="reportHeaderCellRight">Precio&nbsp;&nbsp;&nbsp;</th>
			<th class="reportHeaderCellRight">Importe&nbsp;&nbsp;&nbsp;<th>
		</TR>
<%
		PmUnitPrice pmUnitPrice = new PmUnitPrice(sFParams);
		BmoUnitPrice bmoUnitPrice;
		
		BmoWorkItem bmoWorkItem = new BmoWorkItem();
		PmWorkItem pmWorkItem = new PmWorkItem(sFParams);
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoWorkItem.getKind(), bmoWorkItem.getWorkId().getName(), workId);
		Iterator<BmObject> conceptItems = pmWorkItem.list(bmFilter).iterator();
		int i = 1;
		while (conceptItems.hasNext()) {
			bmoWorkItem =  (BmoWorkItem)conceptItems.next();
			bmoUnitPrice = (BmoUnitPrice)pmUnitPrice.get(bmoWorkItem.getUnitPriceId().toInteger());            			
		
%>			
			<TR class="reportCellEven">
				<td class="reportCellEven">
	            	<%= i %>
	            </td>
				<td class="reportCellEven">
	            	<b><%= bmoWorkItem.getBmoUnitPrice().getCode().toString()%></b>
	            </td>
				<td class="reportCellEven">
	            	<%= bmoWorkItem.getBmoUnitPrice().getDescription().toString()%>
	            </td>
				<td class="reportCellEven">
	            	<%= bmoWorkItem.getBmoUnitPrice().getBmoUnit().getCode().toString()%>
	            </td>
				<td class="reportCellEven" align="right">
		        	<%= bmoWorkItem.getQuantity().toDouble()%>
		        </td>
				<td class="reportCellEven" align="right">
	            	<%=  SFServerUtil.formatCurrency(bmoWorkItem.getPrice().toDouble())%>
	            </td>
				<td class="reportCellEven" align="right">
	            	<%= SFServerUtil.formatCurrency(bmoWorkItem.getAmount().toDouble())%>
	            </td>
        	</TR>
<%
		}
%>

	<tr class="reportCellEven">
		<td class="" colspan="5">
			&nbsp;
		</td>
		<th class=""align="right">
			<b>Totales:&nbsp;</b>
		</th>
		<td class="reportCellEven" align ="right" colspan="">
			<b><%= SFServerUtil.formatCurrency(bmoWork.getTotal().toDouble()) %></b>
		</td>
								
	</tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="">
	<tr>
		<td>&nbsp;<td>
	</tr>
	<tr>
		<td align="center"><br><br>
			___________________
		</td>
		<td align="center">
			&nbsp;
		</td>
		<td align="center"><br><br>
			___________________
		</td>
	</tr>
	<tr>
	<td align="center" class="documentTitleCaption">
		<b>
			Revis&oacute;<br>
			<%=bmoWork.getBmoUser().getFirstname().toString() %>
			<%=bmoWork.getBmoUser().getFatherlastname().toString() %>
			<%=bmoWork.getBmoUser().getMotherlastname().toString() %>
		</b>
	</td>
	<td align="center">
		&nbsp;
	</td>
	<%
		BmoArea bmoArea = new BmoArea();
	    PmArea pmArea = new PmArea(sFParams);
	    if (bmoWork.getBmoUser().getAreaId().toInteger() > 0)
		    bmoArea = (BmoArea)pmArea.get(bmoWork.getBmoUser().getAreaId().toInteger());    
	    
	    BmoUser bmoUserResponsibleArea = new BmoUser();
	    PmUser pmUserResponsibleArea = new PmUser(sFParams);
	    if (bmoArea.getUserId().toInteger() > 0)
	    	bmoUserResponsibleArea = (BmoUser)pmUserResponsibleArea.get(bmoArea.getUserId().toInteger()); 
	%>
	<td align="center" class="documentTitleCaption">
		<b>
			Valid&oacute;<br>
			<%= bmoUserResponsibleArea.getFirstname().toString() %>
			<%= bmoUserResponsibleArea.getFatherlastname().toString() %>
			<%= bmoUserResponsibleArea.getMotherlastname().toString() %>
		</b>
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
