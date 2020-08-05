<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoContractEstimation"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.flexwm.server.co.PmContractEstimation"%>
<%@page import="com.flexwm.shared.op.BmoSupplierAddress"%>
<%@page import="com.flexwm.server.op.PmSupplierAddress"%>
<%@page import="com.flexwm.server.AmountByWord"%>
<%@page import="com.flexwm.server.AmountByWorden"%>
<%@page import="com.flexwm.server.AmountByWordes"%>
<%@page import="com.flexwm.server.IAmountByWord"%>
<%@page import="com.flexwm.server.NumberByWord"%>

<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.PmConn"%>
<%@include file="../inc/login_opt.jsp" %>
<%
	String title = "Recibo de Pago a Estimaci&oacute;n ";
	
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
	int ContractEstimationId = 0;	
	if (isExternal) ContractEstimationId = decryptId;
	else ContractEstimationId = Integer.parseInt(request.getParameter("foreignId"));
	
	PmContractEstimation pmContractEstimation = new PmContractEstimation(sFParams);
	
	bmoContractEstimation = (BmoContractEstimation)pmContractEstimation.get(ContractEstimationId);
	
    AmountByWord amountByWord = new AmountByWord();
	amountByWord.setLanguage("es");
	amountByWord.setCurrency("es");
    
	PmCity pmCity = new PmCity(sFParams);
	BmoCity bmoCityCompany = (BmoCity)pmCity.get(bmoContractEstimation.getBmoWorkContract().getBmoCompany().getCityId().toInteger());
	
	
	//Ciudad del Proveedor
    PmSupplierAddress pmSupplierAddress = new PmSupplierAddress(sFParams);
    BmoSupplierAddress bmoSupplierAddress = new BmoSupplierAddress();
    PmConn pmConnSupplierAddress = new PmConn(sFParams);
    pmConnSupplierAddress.open();

    String sqlSuplAddress = " SELECT * FROM supplieraddress WHERE suad_supplierid = " + bmoContractEstimation.getBmoWorkContract().getSupplierId().toInteger() + " ORDER BY suad_type DESC";
    pmConnSupplierAddress.doFetch(sqlSuplAddress);
	if(pmConnSupplierAddress.next()){
		bmoSupplierAddress = (BmoSupplierAddress)pmSupplierAddress.get(pmConnSupplierAddress.getInt("suad_supplieraddressid"));
	}
	
	pmConnSupplierAddress.close();
	
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
		
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoContractEstimation.getBmoWorkContract().getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
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
		<td colspan="4">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="4" class="reportHeaderCell">			
			<p><strong><font size="2" face="Verdana">&nbsp;A. Datos del Contratante:</font></strong></p>			
		</td>		
	</tr>	
	<tr>
		<td class="documentLabel" align ="left" colspan="">Compa&ntilde;ia Contratante:</td>
		<td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getLegalName().toString() %></td>
		<td class="documentLabel" align ="left" colspan="">Rfc :</td>
		<td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getRfc().toString() %></td>	
	</tr> 
	<tr>
		<td class="documentLabel" align ="left" colspan="">Domicilio :</td>
		<td class="documentText" align ="left" colspan="3">
		 	<%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getStreet().toString() %>&nbsp;
		 	<%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getNumber().toString() %>&nbsp;
		 	<%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getNeighborhood().toString() %>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4" class="reportHeaderCell">
			<p><strong><font size="2" face="Verdana">&nbsp;B. Datos del Contratista:</font></strong></p>
		</td>
	</tr>	
	<tr>		  
		<td class="documentLabel" align ="left" colspan="">Raz&oacute;n Social:</td>
		<td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getLegalName().toString() %></td>
		<td class="documentLabel" align ="left" colspan="">Representante Legal:</td>
		<td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getLegalRep().toString() %></td>
	</tr>
	<tr>
	    <td class="documentLabel" align ="left" colspan="">Domicilio Contratista:</td>
	    <td class="documentText" align ="left" colspan="3">
	    	<%= bmoSupplierAddress.getStreet().toString() %>
	    	<%= bmoSupplierAddress.getNumber().toString() %>
			#<%= bmoSupplierAddress.getNeighborhood().toString() %>
			<%= bmoSupplierAddress.getZip().toString() %>
		</td>
	</tr>
    <tr>
	    <td class="documentLabel" align ="left" colspan="">Rfc:</td>
	    <td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getRfc().toString() %></td>
	    <td class="documentLabel" align ="left" colspan="">Imss:</td>
	    <td class="documentText" align ="left" colspan=""><%= bmoContractEstimation.getBmoWorkContract().getBmoSupplier().getImss().toString() %></td>
    </tr>
    <tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4" class="reportHeaderCell">
			<p><strong><font size="2" face="Verdana">&nbsp;C. Datos del Contrato:</font></strong></p>
		</td>
	</tr>
	<tr>
	  <td class="documentLabel" align ="left" colspan="">No.Contrato:</td>
	  <td class="documentText" align ="left" colspan=""> <%= bmoContractEstimation.getBmoWorkContract().getName().toString() %></td>	
	  <td class="documentLabel" align ="left" colspan="">Importe:</td>
	  <td class="documentText" align ="left" colspan=""> <%= SFServerUtil.formatCurrency(bmoContractEstimation.getBmoWorkContract().getSubTotal().toDouble()) %></td>
	</tr>
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Descripci&oacute;n:</td>
	  <td class="documentText" align ="left" colspan="3"> <%= bmoContractEstimation.getBmoWorkContract().getDescription().toString() %></td>
	</tr> 
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Fecha de Inicio:</td>	
		<td class="documentText" align ="left" colspan="">&nbsp;
	  		  <%= bmoContractEstimation.getStartDate().toString() %>
	  	</td>
	</tr>
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Fecha de Terminiaci&oacute;n</td>
	  <td class="documentText" align ="left" colspan="">&nbsp;
			<%= bmoContractEstimation.getEndDate().toString() %>
	  </td>	  	
	</tr>   
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Ubicaci&oacute;n de la Obra:</td>
	  <td class="documentText" align ="left" colspan="3">
			<%= bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase().getName().toString() %>
			(<%= bmoContractEstimation.getBmoWorkContract().getBmoWork().getBmoDevelopmentPhase().getCode().toString() %>)
		</td>
	</tr> 
	<tr>
		<td class="documentLabel" align ="left" colspan="">Paquetes Contemplados:</td>
		<td class="documentText" align ="left" colspan="3">		
		 <%
			//Listar las viviendas
			sql = " SELECT * FROM workcontractproperties " +
				  " LEFT JOIN properties ON (wcpr_propertyid = prty_propertyid) " +		
			      " WHERE wcpr_workcontractid = " + bmoContractEstimation.getWorkContractId().toInteger();
			pmConn.doFetch(sql);
			while(pmConn.next()) {     
		%>
			    <B><%= pmConn.getString("properties", "prty_code") %></B>,
		<% } %>
		,<b>Total:<%= bmoContractEstimation.getBmoWorkContract().getQuantity().toDouble() %></b>
		</td>
    </tr>		
	<tr>
	  <td class="documentLabel" align ="left" colspan="">Clausulas Especiales u Observaciones:</td>
	  <td class="documentText" align ="left" colspan="3"> <%= bmoContractEstimation.getBmoWorkContract().getObservations().toString() %></td>
	</tr> 
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>	
	<tr>
		<td colspan="4" class="reportHeaderCell">			
			<p><strong><font size="2" face="Verdana">&nbsp;D. Datos Financieros:</font></strong></p>			
		</td>		
	</tr>	
	<tr>  	
		<td class="documentLabel" align ="left" colspan="">Cheque:</td>
		<td class="documentText" align ="right" colspan="">&nbsp;</td>
	    <td class="documentLabel" align ="left" colspan="">Banco:</td>
	    <td class="documentText" align ="right" colspan="">&nbsp;Bueno por <b><%= SFServerUtil.formatCurrency(bmoContractEstimation.getTotal().toDouble()) %></b> </td>
    </tr>
    <tr>
    	<td colspan="4">
		    <p align="justify" class="documentComments">
			    Recibimos de <b><%= bmoContractEstimation.getBmoWorkContract().getBmoCompany().getDescription().toString().toUpperCase() %></b>
			    la cantidad de <b><%= SFServerUtil.formatCurrency(bmoContractEstimation.getAmount().toDouble() + bmoContractEstimation.getTax().toDouble()) %></b>
			    ***<%= amountByWord.getMoneyAmountByWord(bmoContractEstimation.getAmount().toDouble() + bmoContractEstimation.getTax().toDouble()).toUpperCase() %>***
			    en pago de la estimaci&oacute;n cuyo n&uacute;mero se indica amparando trabajos diversos ejecutados en la obra cuyas 
			    referencias igualmente se indican, durante el periodo del <b><%= bmoContractEstimation.getStartDate().toString() %></b>
			    al <b><%= bmoContractEstimation.getEndDate().toString() %></b> de acuerdo a la liquidaci&oacute;n
			    descrita a continuaci&oacute;n.
		    </p>
		</td>	    
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>  	
	    <td class="reportHeaderCellCenter" colspan="2">Estimaci&oacuten:</td>
	    <td class="reportHeaderCellCenter" colspan="2">Retenciones</td>
	</tr>
	<tr>	  
	  <td class="documentLabel" align ="left" colspan="">Importe de la Estimaci&oacute;n:</td>
	  <td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getAmount().toDouble()) %></td>
	  <td class="documentLabel" align ="left" colspan=""> Amort.Anticipo %:</td>
	  <td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getAmount().toDouble() * (bmoContractEstimation.getBmoWorkContract().getPercentDownPayment().toDouble()/ 100)) %></td>
	</tr> 
	<tr>		
	  <td class="documentLabel" align ="left" colspan="">Traslado Iva:</td>
	  <td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getTax().toDouble()) %></td>
	  <td class="documentLabel" align ="left" colspan=""> Amort Iva Anticipo:</td>
	  <%
	  	//Calcular el iva
	  	if (bmoContractEstimation.getBmoWorkContract().getTax().toDouble() > 0) {
	  %>
	  	<td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getAmount().toDouble() * (bmoContractEstimation.getBmoWorkContract().getPercentDownPayment().toDouble()/ 100) * .16) %></td>
	  <% } else { %>
	  	<td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(0.0) %></td>
	  <% } %>
	</tr>
	<tr>		
	  <td class="documentLabel" align ="left" colspan="">SubTotal:</td>
	  <td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getSubTotal().toDouble()) %></td>
	  <td class="documentLabel" align ="left" colspan="">Fondo Garantia <%= bmoContractEstimation.getBmoWorkContract().getPercentGuaranteeFund().toDouble() %>%:</td>
	  <td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getWarrantyFund().toDouble()) %></td>
	</tr>
	<tr>		
	  <td class="documentLabel" align ="left" colspan="2">&nbsp;</td>	  
	  <td class="documentLabel" align ="left" colspan=""> Otros Cargos:</td>
	  <td class="documentText" align ="right" colspan=""><%= SFServerUtil.formatCurrency(bmoContractEstimation.getOthersExpenses().toDouble()) %></td>
	</tr>
	<tr>		
	  <td class="documentLabel" align ="left" colspan="">Total a Pagar</td>
	  <td class="documentText" align ="right" colspan=""><b><%= SFServerUtil.formatCurrency(bmoContractEstimation.getTotal().toDouble()) %></b></td>
	  <td class="documentLabel" align ="left" colspan=""> Total Retenciones:</td>
	  <td class="documentText" align ="right" colspan="">
	  	<%
	  		double retentions = 0;
	  		if (bmoContractEstimation.getBmoWorkContract().getTax().toDouble() > 0) {
		  		retentions = bmoContractEstimation.getAmount().toDouble() * (bmoContractEstimation.getBmoWorkContract().getPercentDownPayment().toDouble()/ 100) +
		  				     (bmoContractEstimation.getAmount().toDouble() * (bmoContractEstimation.getBmoWorkContract().getPercentDownPayment().toDouble()/ 100) * .16) +
		  				     bmoContractEstimation.getWarrantyFund().toDouble() + bmoContractEstimation.getOthersExpenses().toDouble();
	  		} else{
	  			retentions = bmoContractEstimation.getAmount().toDouble() * (bmoContractEstimation.getBmoWorkContract().getPercentDownPayment().toDouble()/ 100) +	  				     
	  				     bmoContractEstimation.getWarrantyFund().toDouble() + bmoContractEstimation.getOthersExpenses().toDouble();
	  		}
	  		
	  	%>
	  	<%= SFServerUtil.formatCurrency(retentions) %>
	  </td>
	</tr>
	<tr>  	
	  <td class="documentLabel" align ="left" colspan="">Cantidad con Letra:</td>
	  <td class="documentText" align ="left" colspan="3"> (<b><%= amountByWord.getMoneyAmountByWord(bmoContractEstimation.getTotal().toDouble()).toUpperCase() %></b>)</td>    		
	</tr>	
</table>
<p align="center" class="documentComments">	
	<b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>, 
	<b><%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>,  
	A <b><%= SFServerUtil.nowToString(sFParams, "dd") %> DE <%= nowMonth.toUpperCase() %>,  <%= SFServerUtil.nowToString(sFParams, "YYYY") %></b>
</p>



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
	    	Responsable Presupuesto
	    </td>	
	    <td align="center" class="documentComments">
	        Gerente General
	    </td>	
	    <td align="center" class="documentComments">
			Contratista
	    </td>
	</tr>
	<tr>
		<%
			bmoUser = (BmoUser)pmUser.get(bmoContractEstimation.getBmoWorkContract().getBmoWork().getUserId().toInteger());
			String responsable = bmoUser.getFirstname().toString().toUpperCase() + " " + bmoUser.getFatherlastname().toString().toUpperCase() + " " + bmoUser.getMotherlastname().toString().toUpperCase();
			
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
	    <%
			
	    %>
	    <td align="center" class="documentComments">
		    <b>
		    	<%= responsable %>		    	
		    </b>
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

<p>&nbsp;</p>


<%  } catch (Exception e) { 
    String errorLabel = "Error en la solicitud de pago a estimación";
    String errorText = "El recibo de pago a estimación no pudo ser desplegado exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    } finally {
    	pmConn2.close();
    	pmConn.close();
    }
%>
</body>
</html>
