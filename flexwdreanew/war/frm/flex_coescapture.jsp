<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoContractEstimation"%>
<%@page import="com.flexwm.shared.co.BmoEstimationItem"%>
<%@page import="com.flexwm.shared.co.BmoEstimationItem"%>
<%@page import="com.flexwm.server.co.PmContractEstimation"%>
<%@page import="com.flexwm.server.co.PmEstimationItem"%>
<%@page import="com.flexwm.server.co.PmEstimationItem"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.Pair"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp" %>

<%
String title = "Hoja de Captura Estimaci&oacute;n.";

BmoContractEstimation bmoContractEstimation = new BmoContractEstimation();

BmoProgram bmoProgram = new BmoProgram();
PmProgram pmProgram  = new PmProgram(sFParams);
bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoContractEstimation.getProgramCode());	

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
	int contractEstimationId = 0;	
	if (isExternal) contractEstimationId = decryptId;
	else contractEstimationId = Integer.parseInt(request.getParameter("foreignId"));
	
	PmContractEstimation pmContractEstimation = new PmContractEstimation(sFParams);
	
	bmoContractEstimation = (BmoContractEstimation)pmContractEstimation.get(contractEstimationId);
	
    AmountByWord amountByWord = new AmountByWord();
	amountByWord.setLanguage("es");
	amountByWord.setCurrency("es");
    
	PmCity pmCity = new PmCity(sFParams);
	BmoCity bmoCityCompany = (BmoCity)pmCity.get(bmoContractEstimation.getBmoWorkContract().getBmoCompany().getCityId().toInteger());

	BmoCompany bmoCompany = new BmoCompany();
	PmCompany pmCompany = new PmCompany(sFParams);
	bmoCompany = (BmoCompany)pmCompany.get(bmoContractEstimation.getBmoWorkContract().getCompanyId().toInteger());
	
	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	String logoURL ="";
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();
	
	String nowMonth = SFServerUtil.nowToString(sFParams, "MM");
	
	switch (Integer.parseInt(nowMonth)) {
		        case 1:
		            nowMonth = "Enero";
		            break;
		        case 2:
		            nowMonth = "Febrero";
		            break;
		        case 3:
		            nowMonth = "Marzo";
		            break;
		        case 4:
		            nowMonth = "Abril";
		            break;
		        case 5:
		            nowMonth = "Mayo";
		            break;
		        case 6:
		            nowMonth = "Junio";
		            break;
		        case 7:
		            nowMonth = "Julio";
		            break;
		        case 8:
		            nowMonth = "Agosto";
		            break;
		        case 9:
		            nowMonth = "Septiembre";
		            break;
		        case 10:
		            nowMonth = "Octubre";
		            break;
		        case 11:
		            nowMonth = "Noviembre";
		            break;
		        case 12:
		            nowMonth = "Diciembre";
		            break;
		        default:
		            nowMonth = "n/d";
		  	 }
		
		//Obtener el Resposable de la Obra
		PmUser pmUser = new PmUser(sFParams);
		BmoUser bmoUser = null;
		
		
	   
 %> 
 
 <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td width="20%" align="left">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td width="60%" class="documentTitle" align="center"><%= title %> No.<%= bmoContractEstimation.getConsecutive().toInteger() %><br>Elaborada el:
			<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
		</td>
		<td width="20%" class="documentComments" align="left"><b>C&oacute;digo del Documento: FO-07.5.2.1-1 <br> (30-Sep-04)</b></td>
	</tr>			
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0">
	<tr>
		<td colspan="4" class="reportHeaderCell">			
			Informaci&oacute;n de la Estimaci&oacute;n:			
		</td>		
	</tr>	
	<tr>
	  <td class="documentLabel" align ="left" colspan="">No.Contrato:</td>
	  <td class="documentText" align ="left" colspan=""> <%= bmoContractEstimation.getBmoWorkContract().getName().toString() %></td>	
	  <td class="documentLabel" align ="left" colspan="">Contratista:</td>
	  <td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getLegalName().toString() %></td>
	</tr>
	<tr>		  
		<td class="documentLabel" align ="left" colspan="">Compa&ntilde;ia Contratante:</td>
		<td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getLegalName().toString() %></td>
		<td class="documentLabel" align ="left" colspan="">Descripci&oacute;n Obra:</td>
	    <td class="documentText" align ="left" colspan="3"> <%= bmoContractEstimation.getBmoWorkContract().getDescription().toString() %></td>
	</tr>
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Ubicaci&oacute;n de la Obra:</td>
	  <td class="documentText" align ="left" colspan="3">
			<%= bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase().getName().toString() %>
			(<%= bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase().getCode().toString() %>)
		</td>
	</tr>
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Fecha de Inicio:</td>	
		<td class="documentText" align ="left" colspan="">&nbsp;
	  		  <%= bmoContractEstimation.getStartDate().toString() %>
	  	</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" class="">
	<tr>
		<td colspan="4" class="reportHeaderCellCenter">			
			Conceptos de la Estimaci&oacute;n		
		</td>
		<td colspan="2" class="reportHeaderCellCenter">			
			Contrato		
		</td>
		<td colspan="2" class="reportHeaderCellCenter">			
			Acumulado		
		</td>	
	</tr>
	<TR class="">
		<td align="left" class="reportHeaderCell" width="2%">
			&nbsp;#
		</td>				
		<td class="reportHeaderCell" width="40%">Descripci&oacute;n</td>
		<td class="reportHeaderCellCenter" width="5%">Unidad</td>
		<td class="reportHeaderCellCenter" width="9%">Cantidad</td>
		<td class="reportHeaderCellRight" width="9%">Precio U.</td>
		<td class="reportHeaderCellCenter" width="5%">Anterior</td>
		<td class="reportHeaderCellCenter" width="15%">Esta</td>
		<td class="reportHeaderCellCenter" width="15%">Acum.</td>
	</TR>
<%
	
	BmoEstimationItem bmoEstimationItem = new BmoEstimationItem();
	PmEstimationItem pmEstimationItem = new PmEstimationItem(sFParams);
	BmFilter bmFilter = new BmFilter();
	bmFilter.setValueFilter(bmoEstimationItem.getKind(), bmoEstimationItem.getContractEstimationId().getName(), contractEstimationId);
	Iterator<BmObject> estimationItems = pmEstimationItem.list(bmFilter).iterator();
	int i = 1;
	while (estimationItems.hasNext()) {
		bmoEstimationItem = (BmoEstimationItem)estimationItems.next();
%>
		<TR class="reportCellEven">				
			<%=HtmlUtil.formatReportCell(sFParams, "" + i++,BmFieldType.NUMBER)%>				
			<%=HtmlUtil.formatReportCell(sFParams, bmoEstimationItem.getBmoContractConceptItem().getDescription().toString(),BmFieldType.STRING)%>
			<%=HtmlUtil.formatReportCell(sFParams, bmoEstimationItem.getBmoContractConceptItem().getBmoWorkItem().getBmoUnitPrice().getBmoUnit().getCode().toString(),BmFieldType.STRING)%>			
			<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getQuantityTotal().toDouble(),BmFieldType.NUMBER)%>				
			<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getPrice().toDouble(),BmFieldType.CURRENCY)%>				
			<%=HtmlUtil.formatReportCell(sFParams, "" + bmoEstimationItem.getQuantityLast().toDouble(),BmFieldType.NUMBER)%>
			<%=HtmlUtil.formatReportCell(sFParams, " ",BmFieldType.NUMBER)%>
			<%=HtmlUtil.formatReportCell(sFParams, " ",BmFieldType.NUMBER)%>
		</TR>
<% } %>
<p>&nbsp;</p>
<table width="100%" align="center" border="0">
	<tr>
		<td colspan="4">&nbsp;<br><br><br></td>
	</tr>
	<tr>	
		<td align="center" class="documentComments">
			_______________________
	    </td>
	    <td align="center" class="documentComments">
	    	_______________________
	    </td>
	    <td align="center" class="documentComments">
	    	_______________________
	    </td>	
	    <td align="center" class="documentComments">
	        _________________________
	    </td>			
	</tr>    
	<tr>
	    <td align="center" class="documentComments">	        
	    	Subdirector &Aacute;rea T&eacute;cnica
	    </td>
	    <td align="center" class="documentComments">
		    Supervisor
	    </td>	
	    <td align="center" class="documentComments">	    
			Residente
	    </td>	
	    <td align="center" class="documentComments">
			Contratista
	    </td>
	</tr>
	<tr>
		<%
			bmoUser = (BmoUser)pmUser.get(bmoContractEstimation.getBmoWorkContract().getBmoWork().getUserId().toInteger());
		
			BmoArea bmoArea = new BmoArea();
		    PmArea pmArea = new PmArea(sFParams);
		    if (bmoUser.getAreaId().toInteger() > 0)
			    bmoArea = (BmoArea)pmArea.get(bmoUser.getAreaId().toInteger()); 
		
			BmoUser bmoUserResponsibleArea = new BmoUser();
		    PmUser pmUserResponsibleArea = new PmUser(sFParams);
		    if (bmoArea.getUserId().toInteger() > 0)
		    	bmoUserResponsibleArea = (BmoUser)pmUserResponsibleArea.get(bmoArea.getUserId().toInteger());
		
	    %>
	    <td align="center" class="documentComments">
		    <b>
		    	<%= bmoUserResponsibleArea.getFirstname().toString().toUpperCase()%>
		    	<%= bmoUserResponsibleArea.getFatherlastname().toString().toUpperCase()%>
		    	<%= bmoUserResponsibleArea.getMotherlastname().toString().toUpperCase()%>  
		    </b>
	    </td>
	    <td align="center" class="documentComments">		    
		    &nbsp;
	    </td>
	    <td align="center" class="documentComments">		    
		    &nbsp;		    
	    </td>
	    <td align="center" class="documentComments">
		    <b>
		    	<%= bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getLegalRep().toString() %>
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
