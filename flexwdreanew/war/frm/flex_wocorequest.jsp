<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.co.BmoWorkContract"%>
<%@page import="com.flexwm.shared.co.BmoWork"%>
<%@page import="com.flexwm.server.co.PmWorkContract"%>
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

<html>
<head>
    <title>:::Solicitud del Contrato.:::</title>
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
	int workContractId = 0;	
	if (isExternal) workContractId = decryptId;
	else workContractId = Integer.parseInt(request.getParameter("foreignId"));
	
	PmWorkContract pmWorkContract = new PmWorkContract(sFParams);
	
	BmoWorkContract bmoWorkContract = (BmoWorkContract)pmWorkContract.get(workContractId);
	
	//Ciudad del Proveedor
    PmSupplierAddress pmSupplierAddress = new PmSupplierAddress(sFParams);
    BmoSupplierAddress bmoSupplierAddress = new BmoSupplierAddress();
    PmConn pmConnSupplierAddress = new PmConn(sFParams);
    pmConnSupplierAddress.open();

    String sqlSuplAddress = " SELECT * FROM supplieraddress WHERE suad_supplierid = " + bmoWorkContract.getSupplierId().toInteger() + " ORDER BY suad_type DESC";
    pmConnSupplierAddress.doFetch(sqlSuplAddress);
	if(pmConnSupplierAddress.next()){
		bmoSupplierAddress = (BmoSupplierAddress)pmSupplierAddress.get(pmConnSupplierAddress.getInt("suad_supplieraddressid"));
	}

	pmConnSupplierAddress.close();
    
    //Ciudad del Company
	PmCity pmCity = new PmCity(sFParams);
    BmoCity bmoCityCompany = (BmoCity)pmCity.get(bmoWorkContract.getBmoCompany().getCityId().toInteger());

    AmountByWord amountByWord = new AmountByWord();
	amountByWord.setLanguage("es");
	amountByWord.setCurrency("es");
    
	BmoCompany bmoCompany = new BmoCompany();
	PmCompany pmCompany = new PmCompany(sFParams);
	bmoCompany = (BmoCompany)pmCompany.get(bmoWorkContract.getCompanyId().toInteger());
	
	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	String logoURL ="";
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();
	
	PmUser pmUser = new PmUser(sFParams);
	BmoUser bmoUser = null;
 %>   


<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="5%" rowspan="8" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td colspan="4" class="reportTitleCell">
			Solicitud del Contrato
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
		 	A. Informaci&oacute;n General:
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Clave Contrato:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getName().toString() %>
		 </td>
		 <th align = "left" class="reportCellEven">Compa&ntilde;&iacute;a Solicitante:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoCompany().getLegalName().toString() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Nombre Contratista:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoSupplier().getName().toString() %>
		 </td>
		 <th align = "left" class="reportCellEven">Raz&oacute;n Social:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoSupplier().getLegalName().toString() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Representante Legal:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoSupplier().getLegalRep().toString() %>
		 </td>
		 <th align = "left" class="reportCellEven">Tipo Contrato:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoSupplier().getFiscalType().getSelectedOption().getLabel() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Domicilio Contratista:</th>
		 <td class="reportCellEven" colspan="3">
		 	<%= bmoSupplierAddress.getStreet().toString() %>
		 	<%= bmoSupplierAddress.getNumber().toString() %>
			<%= bmoSupplierAddress.getNeighborhood().toString() %>
			<%= bmoSupplierAddress.getZip().toString() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Rfc:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoSupplier().getRfc().toString() %>
		 </td>
		 <th align = "left" class="reportCellEven">Imss:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getBmoSupplier().getImss().toString() %>
		 </td>
	 </tr>
	 <tr>
		 <td colspan="4">			
		 	&nbsp;
		 </td>
	 </tr>
	 <tr>
		 <td colspan="4" class="reportHeaderCell">			
		 	B. Trabajo a Realizar:
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Descripci&oacute;n:</th>
		 <td class="reportCellEven" colspan="3">
		 	<%= bmoWorkContract.getDescription().toString() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Fecha de Inicio:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getStartDate().toString() %>
		 </td>
		 <th align = "left" class="reportCellEven">Fecha de Terminaci&oacute;n:</th>
		 <td class="reportCellEven">
		 	<%= bmoWorkContract.getEndDate().toString() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Ubicaci&oacute;n de la Obra:</th>
		 <td class="reportCellEven" colspan="3">
		 	<%= bmoWorkContract.getBmoWork().getBmoDevelopmentPhase().getName().toString() %>
			(<%= bmoWorkContract.getBmoWork().getBmoDevelopmentPhase().getCode().toString() %>)
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Viviendas Contempladas:</th>
		 <td class="reportCellEven" colspan="3">
		 <%
			//Listar las viviendas
			sql = " SELECT * FROM workcontractproperties " +
				  " LEFT JOIN properties ON (wcpr_propertyid = prty_propertyid) " +		
			      " WHERE wcpr_workcontractid = " + bmoWorkContract.getId();
			pmConn.doFetch(sql);
			while(pmConn.next()) {   
		 %>
			    <b><%= pmConn.getString("properties", "prty_code") %></b>,
		<% } %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Total de Unidades:</th>
		 <td class="reportCellEven" colspan="3">
		 	<%= bmoWorkContract.getQuantity().toDouble() %>
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven">Cl&aacute;usulas Especiales u Observaciones:</th>
		 <td class="reportCellEven" colspan="3">
		 	<%= bmoWorkContract.getObservations().toString() %>
		 </td>
	 </tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	 <tr>
		 <td colspan="4" class="reportHeaderCell">			
		 	C. Datos Financieros:
		 </td>
	 </tr>
	 <tr>
		 <th align = "left" class="reportCellEven" width="15%">Fecha Anticipo:</th>
		 <td class="reportCellEven"  width="55%">
		 	<%= bmoWorkContract.getPaymentDate().toString() %>
		 </td>
		 <th align = "right" >% Anticipo:</th>
		 <td class="reportCellEven" align = "right">
		 	<%= bmoWorkContract.getPercentDownPayment().toDouble() %>%
		 </td>
	 </tr>
	 <tr><td colspan="4">&nbsp;</td></tr>
	 <tr>
		 <td colspan="2" rowspan="5"  valign="top" align="left" class="detailStart">
		 <p class="documentComments"><b>Comentarios:</b><br>
		 	<%= bmoWorkContract.getComments().toString() %>
		 </p>
		 </td> 
		 <th align="right">$ Anticipo:</th>
		 <td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(bmoWorkContract.getDownPayment().toDouble()) %></td>
	 </tr>
	 <tr>
	     <th align="right">Sanci&oacute;n Diaria:</th>
	     <td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(bmoWorkContract.getDailySanction().toDouble()) %></td>
	 </tr>
	 <tr>
	     <th align="right">Importe:</th>
	     <td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(bmoWorkContract.getSubTotal().toDouble()) %></td>
	 </tr>
	 <tr>
	     <th align="right">IVA:</th>
	     <td class="reportCellEven" align="right"><%= SFServerUtil.formatCurrency(bmoWorkContract.getTax().toDouble()) %></td>
	 </tr>
	 <tr>
	     <th align="right">Total:</th>
	     <td class="reportCellEven" align="right"> 
	     	<b><%= SFServerUtil.formatCurrency(bmoWorkContract.getTotal().toDouble()) %></b>
	     </td>
	 </tr>
	 <tr>
	     <th class="reportCellEven" align="left">Cantidad con letra:</th>
	     <td class="reportCellEven" align="right" colspan="3"> 
	     	<b>(<b><%= amountByWord.getMoneyAmountByWord(bmoWorkContract.getTotal().toDouble()).toUpperCase() %></b>)</b>
	     </td>
	 </tr>
</table>
<p align="center" class="documentComments">
	&nbsp;<b><%= bmoCityCompany.getName().toString().toUpperCase() %></b>, <b><%= bmoCityCompany.getBmoState().getName().toString().toUpperCase() %></b>,  
	A <b><%= bmoWorkContract.getDateContract().toString() %></b>
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
	        _________________________
	    </td>			
	</tr>    
	<tr>
	    <td align="center" class="documentComments">	        
	    	Subdirector &Aacute;rea T&eacute;cnica
	    </td>	   
	    <td align="center" class="documentComments">
			Contratista
	    </td>
	</tr>
	<tr>
		<%
			bmoUser = (BmoUser)pmUser.get(bmoWorkContract.getBmoWork().getUserId().toInteger());
		
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
		    	<%= bmoUserResponsibleArea.getFirstname().toString()%>
		    	<%= bmoUserResponsibleArea.getFatherlastname().toString()%>
		    	<%= bmoUserResponsibleArea.getMotherlastname().toString()%>
		    </b>
	    </td>	    
	    <td align="center" class="documentComments">
		    <b>
		    	<%= bmoWorkContract.getBmoSupplier().getLegalRep().toString() %>
		    </b>
	    </td>
	</tr>
</table>
<p>&nbsp;</p>
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
