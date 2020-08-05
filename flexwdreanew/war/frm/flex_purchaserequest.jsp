<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.flexwm.server.cm.PmQuote"%>
<%@page import="com.flexwm.shared.cm.BmoQuote"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.shared.sf.BmoUser"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.cm.PmQuoteProperty"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteProperty"%>
<%@page import="com.flexwm.server.cm.PmQuotePropertyModelExtra"%>
<%@page import="com.flexwm.shared.cm.BmoQuotePropertyModelExtra"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModel"%>
<%@page import="com.flexwm.server.co.PmDevelopment"%>
<%@page import="com.flexwm.shared.co.BmoDevelopment"%>
<%@page import="com.flexwm.server.co.PmDevelopmentBlock"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	try {
		Locale locale = new Locale("es", "MX");
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
		
		
		boolean opde = false;
		
	    // Obtener parametros
	    int opportunityId = 0;
	    if (isExternal) opportunityId = decryptId;
	    else opportunityId = Integer.parseInt(request.getParameter("foreignId"));    
	
	   	    
	    //Oportunidad
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
		
		//Cotizacion
		BmoQuote bmoQuote = new BmoQuote(); 
		PmQuote pmQuote = new PmQuote(sFParams);
		bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
		
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) throw new Exception("La Cotizacion no esta autorizada - no se puede desplegar.");
		
		if (!(bmoOpportunity.getWFlowTypeId().toInteger() > 0)) throw new Exception("La oportunidad no cuenta con el Tipo de efecto - no se puede desplegar.");

		
		//Cotizacion-Inmueble		
		BmoQuoteProperty bmoQuoteProperty = new BmoQuoteProperty();
		PmQuoteProperty pmQuoteProperty = new PmQuoteProperty(sFParams);
		bmoQuoteProperty = (BmoQuoteProperty)pmQuoteProperty.getBy(bmoQuote.getId(), bmoQuoteProperty.getQuoteId().getName());
		
		//Vivienda
		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		bmoProperty = (BmoProperty)pmProperty.get(bmoQuoteProperty.getPropertyId().toInteger());
		
		//Modelo
		BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
		PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
		bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoProperty.getPropertyModelId().toInteger());
		
		//Manzana
		BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
		PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
		bmoDevelopmentBlock = (BmoDevelopmentBlock)pmDevelopmentBlock.get(bmoProperty.getDevelopmentBlockId().toInteger());
				
		//Desarrollo
		BmoDevelopment bmoDevelopment = new BmoDevelopment();
		PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
		bmoDevelopment = (BmoDevelopment)pmDevelopment.get(bmoPropertyModel.getDevelopmentId().toInteger());
		
		//Empresa Desarrollo
		BmoCompany bmoCompanyDevelopment = new BmoCompany();
		PmCompany pmCompanyDevelopment = new PmCompany(sFParams);
		bmoCompanyDevelopment = (BmoCompany)pmCompanyDevelopment.get(bmoDevelopment.getCompanyId().toInteger());
				
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompanyDevelopment.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompanyDevelopment.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
		//Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoOpportunity.getCustomerId().toInteger());
		
		String customer = "";
		if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
			customer =  bmoOpportunity.getBmoCustomer().getFirstname().toHtml() + " " +
						bmoOpportunity.getBmoCustomer().getFatherlastname().toHtml() + " " +
						bmoOpportunity.getBmoCustomer().getMotherlastname().toHtml();
		} else {
			customer = bmoOpportunity.getBmoCustomer().getLegalname().toHtml();
		}
		
		//Tipo de Efecto
		BmoWFlowType bmoWFlowTypeEffect = new BmoWFlowType();
		PmWFlowType pmWFlowTypeEffect = new PmWFlowType(sFParams);
		bmoWFlowTypeEffect = (BmoWFlowType)pmWFlowTypeEffect.get(bmoOpportunity.getForeignWFlowTypeId().toInteger());
		
		//Vendedor
		BmoUser bmoUserProm = new BmoUser();
		PmUser pmUserProm = new PmUser(sFParams);
		bmoUserProm = (BmoUser)pmUserProm.get(bmoOpportunity.getUserId().toInteger());
		
		//Coordinador
		BmoUser bmoUserCoord = new BmoUser();
		PmUser pmUserCoord = new PmUser(sFParams);
		if(bmoUserProm.getParentId().toInteger() > 0)
			bmoUserCoord = (BmoUser)pmUserCoord.get(bmoUserProm.getParentId().toInteger());
		
		//Gerencia
		BmoUser bmoUserGerencia = new BmoUser();
		PmUser pmUserGerencia = new PmUser(sFParams);
		if(bmoUserCoord.getParentId().toInteger() > 0)
			bmoUserGerencia = (BmoUser)pmUserGerencia.get(bmoUserCoord.getParentId().toInteger());
		

		%>

<html>
<head>
    <title>:::Solicitud Oficial de Compra:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
    <link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
    <meta charset=utf-8>
</head>
<body class="default">
<table border="1" cellspacing="0" width="100%" cellpadding="0">
	<tr>
		<td width="20%" align="left">
			<img border="1" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		</td>
		<td width="60%" class="documentTitle" align="center">Solicitud Oficial de Compra</td>
		<td width="20%" class="documentComments" align="right"><b>Secci&oacute;n 07.2</b></td>
	</tr>
</table>
<br>

<table border="1" cellspacing="0" width="100%" cellpadding="0">
	<tr>   
		<td class="documentSubTitle" colspan="3">
		&nbsp;Datos Generales
		</td>
	</tr>
	<tr>       
		<td class="documentLabel" colspan="3">
			&nbsp;Por medio de la presente expreso mi aceptaci&oacute;n unilateral de compra de una vivienda, ubicada en el fraccionamiento: 
			<span class="documentText"><%= bmoDevelopment.getName().toString()%> </span>, 
			tipo de vivienda: <span class="documentText"><%= bmoPropertyModel.getCode().toString()%>(<%= bmoPropertyModel.getName().toString()%>) </span>
			conforme a las condiciones mencionadas en este documento, comprometi&eacute;ndome en este momento a firmar el contrato de compra-venta 
			correspondiente en el momento en que se me indique.
		</td>
	</tr>
	<tr>   
		<td class="documentLabel">
			&nbsp;Nombre del cliente: <span class="documentText"><%= customer%> </span>
		</td>
		<td class="documentLabel">
			&nbsp;No. Cliente: <span class="documentText"><%= bmoCustomer.getCode().toString()%> </span>
		</td>
		<td class="documentLabel">
			&nbsp;Fecha de elaboraci&oacute;n: <span class="documentText"><%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd")) %> </span>
		</td>
	</tr>
</table>
<br> 
<table border="1" cellspacing="0" width="100%" cellpadding="0">
	<tr>   
		<td class="documentSubTitle" align="left" width="50%">
			&nbsp;Ubicaci&oacute;n de la Vivienda
		</td>
		<td class="documentSubTitle"  align="right" width="50%">
			Valor de la Operaci&oacute;n y Diferencias a pagar&nbsp;
		</td>
	</tr>
</table> 
<table border="0" cellspacing="0" width="100%" cellpadding="0" >
	<tr class="documentLabel"> 
		<td width="5%">
			&nbsp;
		</td>
		<td width="15%">
			&nbsp;Tipo de Cr&eacute;dito:
		</td>
		<td width="5%" class="documentText">
			&nbsp;<%= bmoWFlowTypeEffect.getName().toString()%>
		</td>
		<td width="10%">
			&nbsp;Individual:
		</td>
		<td width="5%">
			&nbsp;<input type="checkbox">
		</td>
		<td width="15%">
			&nbsp;Conyugal:
		</td>
		<td width="5%">
			&nbsp;<input type="checkbox">
		</td>
		<td width="15%">
			&nbsp;Precio de la vivienda:
		</td>
		<td width="5%" class="documentText" style="white-space: nowrap;">
			&nbsp;<%= formatCurrency.format(bmoQuoteProperty.getPrice().toDouble())%>
		</td>
	</tr>
	<tr class="documentLabel"> 
		<td width="5%">
			&nbsp;
		</td>
		<td width="15%">
			&nbsp;Instituci&oacute;n Crediticia:
		</td>
		<td width="5%" class="documentText">
			&nbsp;<%= bmoWFlowTypeEffect.getName().toString()%>
		</td>
		<td width="10%">
			&nbsp;Con Subsidio:
		</td>
		<td width="5%">
			&nbsp;<input type="checkbox">
		</td>
		<td width="15%">
			&nbsp;Sin Subsidio:
		</td>
		<td width="5%">
			&nbsp;<input type="checkbox">
		</td>
		<td width="15%">
			&nbsp;Precio de terreno adic.:
		</td>
		<td width="5%" class="documentText" style="white-space: nowrap;">
			&nbsp;<%= formatCurrency.format((bmoQuoteProperty.getExtraLand().toDouble() 
			+ bmoQuoteProperty.getExtraConstruction().toDouble() 
			+ bmoQuoteProperty.getExtraOther().toDouble()))%>
			&nbsp;
			<sup>C/A</<sup>
		</td>
	</tr>
	<tr class="documentLabel"> 
		<td width="5%">
			&nbsp;
		</td>
		<td width="15%">
			&nbsp;
		</td>
		<td width="5%">
			&nbsp;
		</td>
		<td width="10%">
			&nbsp;
		</td>
		<td width="5%">
			&nbsp;
		</td>
		<td width="15%">
			&nbsp;Monto de Subsidio:
		</td>
		<td width="5%" class="documentText">
			<input type="text" class="documentLabel" style="width: 90%;" value="<%= formatCurrency.format(0) %>">
		</td>
		<td width="15%">
			&nbsp;Monto de Cr&eacute;dito:
		</td>
		<td width="5%" class="documentText">
			<input type="text" class="documentLabel" style="width: 90%;" value="<%= formatCurrency.format(0) %>">
		</td>
	</tr>
	<tr class="documentLabel"> 
		<td width="5%">
			&nbsp;
		</td>
		<td width="15%">
			&nbsp;
		</td>
		<td width="5%" class="documentText">
			&nbsp;
		</td>
		<td width="10%">
			&nbsp;
		</td>
		<td width="5%" class="documentText">
			&nbsp;
		</td>
		<td width="15%">
			&nbsp;Mts<sup>2</sup> Terreno Adicional:
		</td>
		<td width="5%" class="documentText">
			<input type="text" class="documentLabel" style="width: 90%;" value="">
		</td>
		<td width="15%">
			&nbsp;Anticipo:
		</td>
		<td width="5%" class="documentText">
			<input type="text" class="documentLabel" style="width: 90%;" value="<%= formatCurrency.format(0) %>">
		</td>
	</tr>
	<tr class="documentLabel"> 
		<td width="10%">
			&nbsp;Manzana: <span class="documentText">&nbsp;<%= bmoDevelopmentBlock.getCode().toString()%>&nbsp;</span>
		</td>

		<td width="5%">
			&nbsp;Lote:
		</td>
		<td width="10%" class="documentText">
			&nbsp;<%= bmoProperty.getLot().toString()%>
		</td>
		<td width="5%">
			&nbsp;No. Oficial:
		</td>
		<td width="10%" class="documentText">
			&nbsp;<%= bmoProperty.getNumber().toString()%>
		</td>
		<td width="15%">
			&nbsp;Precio Mt<sup>2</sup> Terreno Adicional:
		</td>
		<td width="5%" class="documentText">
			<input type="text" class="documentLabel" style="width: 90%;" value="<%= formatCurrency.format(0) %>">
		</td>
		<td width="15%">
			&nbsp;Diferencia a documentar:
		</td>
		<td width="5%" class="documentText">
			<input type="text" class="documentLabel" style="width: 90%;" value="<%= formatCurrency.format(0) %>">
		</td>
	</tr>
	<tr class="documentLabel"> 
		<td width="5%">
			&nbsp;
		</td>
		<td width="5%">
			&nbsp;
		</td>
		<td width="10%" class="">
			&nbsp;
		</td>
		<td width="5%">
			&nbsp;
		</td>
		<td width="15%" class="">
			&nbsp;
		</td>
		<td width="" coslpan"4">
			&nbsp;
		</td>
	</tr>
</table>
<table border="1" cellspacing="0" width="100%" cellpadding="0" align="">
	<tr>   
		<td class="documentSubTitle" width="50%" colspan="2">
			&nbsp;Expediente Completo
		</td>
	</tr>
	<tr class="documentLabel">
		<td align="center">
			Si&nbsp;<input type="checkbox" <%= ((bmoOpportunity.getBmoWFlow().getHasDocuments().toBoolean()) ? "checked" : "")%>>
		</td>
		<td align="center">
			No&nbsp;<input type="checkbox" <%= ((bmoOpportunity.getBmoWFlow().getHasDocuments().toBoolean()) ? "" : "checked")%>>
		</td>
	</tr>
	<tr>   
		<td class="documentSubTitle" colspan="2">
			&nbsp;Documentos Faltantes
		</td>
	</tr>
	<tr>   
		<td class="documentText" colspan="2">
			&nbsp;<input type="text" class="documentLabel" style="width: 99%;" value="">
		</td>
	</tr>
	<tr>   
		<td class="documentText" colspan="2">
			&nbsp;<input type="text" class="documentLabel" style="width: 99%;" value="">
		</td>
	</tr>
	<tr>   
		<td class="documentText" colspan="2">
			&nbsp;<input type="text" class="documentLabel" style="width: 99%;" value="">
		</td>
	</tr>
	<tr>   
		<td class="documentText" colspan="2">
			&nbsp;<input type="text" class="documentLabel" style="width: 99%;" value="">
		</td>
	</tr>
	<tr>   
		<td class="documentText" colspan="2">
			&nbsp;<input type="text" class="documentLabel" style="width: 99%;" value="">
		</td>
	</tr>
	<tr class="documentLabel">
		<td align="center">
			&nbsp;Firma y Fecha de Autorizaci&oacute;n Gestor&iacute;a:
				<br><br><br><br><br><br>
		<td align="center">
			&nbsp;Firma y Fecha de Autorizaci&oacute;n Cr&eacute;dito y Cobranza:
				<br><br><br><br><br><br>
		</td>
	</tr>
	<tr>
		<td class="documentSubTitle" colspan="2">
			&nbsp;Paquete / Promoci&oacute;n:
		</td>
	</tr>
	<tr>
		<td colspan="2" class="documentLabel">
			<br>
				<%
				 BmoQuotePropertyModelExtra bmoQuotePropertyModelExtra = new BmoQuotePropertyModelExtra();
	             PmQuotePropertyModelExtra pmQuotePropertyModelExtra = new PmQuotePropertyModelExtra(sFParams);
	             BmFilter bmFilter = new BmFilter();
	             bmFilter.setValueFilter(bmoQuotePropertyModelExtra.getKind(), bmoQuotePropertyModelExtra.getQuoteId().getName(), bmoQuote.getId());
	             Iterator<BmObject> quotePrmx = pmQuotePropertyModelExtra.list(bmFilter).iterator();
	             int i = 1;
	             while (quotePrmx.hasNext()) {
	            	 bmoQuotePropertyModelExtra = (BmoQuotePropertyModelExtra)quotePrmx.next();
	            	 %>
	            	 <li>
	            	 	<%= bmoQuotePropertyModelExtra.getBmoPropertyModelExtra().getName().toString()%>
	            	 	<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	            	 	<%= bmoQuotePropertyModelExtra.getComments().toString()%>
	            	 </li>
	            	 <%
	             }
				%>
			<br>
		</td>
	</tr>
</table> 

<!--
<table border="1" cellspacing="0" width="50%" cellpadding="0" align="right" >
	<tr>   
		<td class="documentSubTitle" colspan="5">
			&nbsp;Solo en Cr&eacute;ditos (SFH, Bancario y Cofinanciamiento)
		</td>
	</tr>
	<tr class="documentLabel">   
		<td colspan="4" align="center">
			&nbsp;Tipo de econom&iacute;a
		</td>
		<td colspan="">
			&nbsp;Ingresos requeridos
		</td>
	</tr>
	<tr class="documentLabel">   
		<td>
			&nbsp;Formal
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;Informal
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;<input type="text" class="documentLabel" style="width: 30%;" value="<%= formatCurrency.format(0) %>">
		</td>
	</tr>
	<tr class="documentLabel">   
		<td>
			&nbsp;Pesos
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;Udis
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;Ingresos Reales
		</td>
	</tr>
	<tr class="documentLabel">   
		<td colspan="4" align="center">
			&nbsp;Plazo
		</td>
		<td>
			&nbsp;<input type="text" class="documentLabel" style="width: 30%;" value="<%= formatCurrency.format(0) %>">
		</td>
	</tr>
	<tr class="documentLabel">   
		<td>
			&nbsp;05 a&ntilde;os
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;15 a&ntilde;os
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;Mensualdad Aprox.
		</td>
	</tr>
	<tr class="documentLabel">   
		<td>
			&nbsp;10 a&ntilde;os
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;25 a&ntilde;os
		</td>
		<td>
			&nbsp;<input type="checkbox">
		</td>
		<td>
			&nbsp;<input type="text" class="documentLabel" style="width: 30%;" value="<%= formatCurrency.format(0) %>">
		</td>
	</tr>
	<tr class="documentLabel">   
		<td colspan="">
			&nbsp;20 a&ntilde;os
		</td>
		<td colspan="4">
			&nbsp;<input type="checkbox">
		</td>
	</tr>
	<tr class="documentLabel">   
		<td colspan="5">
			&nbsp;Veces Salarios M&iacute;nimos:<br><br><br>
		</td>
	</tr>
	<tr >   
		<td class="documentSubTitle" colspan="5">
			&nbsp;Observaciones
		</td>
	</tr>
	<tr class="documentLabel">   
		<td colspan="5">
			&nbsp;<br><br><br><br><br>
		</td>
	</tr>
</table> 
-->
<table border="1" cellspacing="0" width="100%" cellpadding="0" >
	<tr>   
		<td class="documentSubTitle">
			&nbsp;Aviso de privacidad:
		</td>
	</tr>
	<tr>   
		<td class="documentLabel" align="justify">
			&nbsp;
			<%= bmoCompanyDevelopment.getLegalName().toString() %>
			<!--Corporativo Madama, S.A.P.I. de C.V.-->
			, ratifica que todos los datos personales que han proporcionado y que en el futuro proporcionen a esta empresa y/o a sus subsidiarias los utilizar&aacute; para identificarlos como clientes, mantener
			un expediente f&iacute;sico y procesar electr&oacute;nicamente sus datos en los diferentes sistemas internos y en su caso, ofrecerles promociones y/o informaci&oacute;n de nuestros productos. La empresa por su parte les informa que dar&aacute;
			cumplimiento a lo que dispone la Ley Federal de Protecci&oacute;n de Datos Personales en posesi&oacute;n de los particulares, como responsable del tratamiento de Datos Personales, seg&uacute;n se definen en la Ley citada. Respecto al
			procedimiento para solicitar sus Derechos de Acceso, Rectificaci&oacute;n, Cancelaci&oacute;n u oposici&oacute;n (ARCO), en relaci&oacute;n a cualesquier modificaci&oacute;n al Aviso de Privacidad y Transferencia de Datos Personales, podr&aacute;n hacerlo por medio
			de nuestra p&aacute;gina de Internet http://www.mdm.mx, en el apartado de Privacidad de Datos personales
		</td>
	</tr>
	<tr>   
		<td class="documentSubTitle">
			&nbsp;Firmas:
		</td>
	</tr>
</table>
<br> 
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >
    <tr align="center" class="documentLabel">
        <td align="center" width="25%"><br><br>
        	<b>_<u>Acepto la Compra definitiva del Inmueble</u>_</b>
        </td>
        <td align="center" width="25%"><br><br>
            ____________________________________
        </td>
        <td align="center" width="25%"><br><br>
            ____________________________________
        </td>
        <td align="center" width="25%"><br><br>
            ____________________________________
        </td>
    </tr>
    <tr class="documentLabel">
	    <td align="center" width="25%">
	    	Comprador:<br>
			<%= bmoOpportunity.getBmoCustomer().getDisplayName().toHtml() %>
		</td>
		<td align="center" width="25%">
	    	Coordinador Inmobiliario:<br>
    		<input type="text" class="documentLabel" style="width: 60%; text-align: center;" value="<%= bmoUserCoord.getFirstname().toHtml()%> <%= bmoUserCoord.getFatherlastname().toHtml()%> <%= bmoUserCoord.getMotherlastname().toHtml()%>">
	    </td>
        <td align="center" width="25%">
        	Promotor Inmobiliario:<br>
        	<input type="text" class="documentLabel" style="width: 60%; text-align: center;" value="<%= bmoUserProm.getFirstname().toHtml()%> <%= bmoUserProm.getFatherlastname().toHtml()%> <%= bmoUserProm.getMotherlastname().toHtml()%>">
        </td>
        <td align="center" width="25%">
	    	Asistente Comercial:<br>
        	<input type="text" class="documentLabel" style="width: 60%; text-align: center;" value="<%= bmoUserCoord.getFirstname().toHtml()%> <%= bmoUserCoord.getFatherlastname().toHtml()%> <%= bmoUserCoord.getMotherlastname().toHtml()%>">
	    </td>
    </tr> 
</table>

<% 	} catch (Exception e) { 
		String errorLabel = "Error de Cotizacion";
		String errorText = "La Cotizacion no puede ser desplegada.";
		String errorException = e.toString();
		
		//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

	<%= errorException %>

<%
	}
%>

</body>
</html>