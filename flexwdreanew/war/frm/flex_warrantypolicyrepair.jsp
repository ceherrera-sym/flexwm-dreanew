<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.co.PmOrderProperty"%>
<%@page import="com.flexwm.shared.co.BmoOrderProperty"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.server.co.PmPropertyType"%>
<%@page import="com.flexwm.shared.co.BmoPropertyType"%>
<%@page import="com.flexwm.server.co.PmPropertyModel"%>
<%@page import="com.flexwm.shared.co.BmoPropertyModel"%>
<%@page import="com.flexwm.server.co.PmDevelopmentBlock"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.server.co.PmDevelopment"%>
<%@page import="com.flexwm.shared.co.BmoDevelopment"%>
<%@page import="com.symgae.server.sf.PmUser"%>
<%@page import="com.symgae.shared.sf.BmoUser"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "P&oacute;liza de Garant&iacute;a  Para la Reparaci&oacute;n de Fallas Tecnicas y/o Vicios Ocultos";
	
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
    // Obtener parametros
    int propertySaleId = 0;
    if (isExternal) propertySaleId = decryptId;
    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));    

	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
	bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
	
	BmoOrder bmoOrder = new BmoOrder();
	PmOrder pmOrder = new PmOrder(sFParams);
	bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());

	//Vivienda
	BmoProperty bmoProperty = new BmoProperty();
	PmProperty pmProperty = new PmProperty(sFParams);
	bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
	
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
	
	//Ciudad del Desarrollo
	BmoCity bmoCityDevelopment = new BmoCity();
	PmCity pmCityDevelopment = new PmCity(sFParams);
	bmoCityDevelopment = (BmoCity)pmCityDevelopment.get(bmoPropertyModel.getBmoDevelopment().getCityId().toInteger());			
	
	//Empresa
	BmoCompany bmoCompany = new BmoCompany();
	PmCompany  pmCompany  = new PmCompany (sFParams);
	if(bmoDevelopment.getCompanyId().toInteger() > 0)
		bmoCompany = (BmoCompany)pmCompany.get(bmoProperty.getCompanyId().toInteger());
	
	// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
	String logoURL ="";
	if (!bmoCompany.getLogo().toString().equals(""))
		logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
	else 
		logoURL = sFParams.getMainImageUrl();
	
	//Ciudad de la Empresa
	BmoCity bmoCityCompany = new BmoCity();
	PmCity pmCity = new PmCity(sFParams);
	bmoCityCompany = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
	//Cliente
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
	
	String customer = "";
	if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
		customer =  bmoCustomer.getFirstname().toHtml() + " " +
					bmoCustomer.getFatherlastname().toHtml() + " " +
					bmoCustomer.getMotherlastname().toHtml();
	} else {
		customer = bmoCustomer.getLegalname().toHtml();
	}
		
	//Vendedor
	String vendedor="";
	BmoUser bmoSalesUser = new BmoUser();
	PmUser pmSalesUser = new PmUser(sFParams);
	bmoSalesUser = (BmoUser)pmSalesUser.get(bmoPropertySale.getSalesUserId().toInteger());
	//vendedor = bmoSalesUser.getFirstname().toString() + " " + bmoSalesUser.getFatherlastname() + " " + bmoSalesUser.getMotherlastname();
	vendedor =  bmoSalesUser.getCode().toString();

	//Coordinador
	String coordinador="";
	BmoUser bmoCoordinadorUser = new BmoUser();
	PmUser pmCoordinadorUser = new PmUser(sFParams);
	if(bmoSalesUser.getParentId().toInteger() > 0){
		bmoCoordinadorUser = (BmoUser)pmCoordinadorUser.get(bmoSalesUser.getParentId().toInteger());
		//coordinador = bmoCoordinadorUser.getFirstname().toString() + " " + bmoCoordinadorUser.getFatherlastname().toString() + " " + bmoCoordinadorUser.getMotherlastname().toString();
		coordinador =  bmoCoordinadorUser.getCode().toString();
	}
	
	//Gerente
	String gerente="";
	BmoUser bmoGerenteUser = new BmoUser();
	PmUser pmGerenteUser = new PmUser(sFParams);
	if(bmoCoordinadorUser.getParentId().toInteger() > 0){
		bmoGerenteUser = (BmoUser)pmGerenteUser.get(bmoCoordinadorUser.getParentId().toInteger());
		//gerente = bmoGerenteUser.getFirstname().toString() + " " + bmoGerenteUser.getFatherlastname().toString() + " " + bmoGerenteUser.getMotherlastname().toString();
		gerente =  bmoGerenteUser.getCode().toString();
	}


	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOrder.getProgramCode());	

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

//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, men&uacute;(clic-derecho).
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
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td>
				&nbsp;<p>&nbsp;<p>
				&nbsp;<p>&nbsp;<p>
				&nbsp;<p>&nbsp;<p>
			</td>
		</tr>

    	<tr>
    		<!--
    		<td align="left" width="" rowspan="5" valign="top">
    			<img border="0" width="<%//= SFParams.LOGO_WIDTH %>" height="<%//= SFParams.LOGO_HEIGHT %>" src="<%//= logoURL %>" >
    		</td>
    		-->
    		<td class="reportTitleCell" colspan="3" style="text-align:center">
	    		P&oacute;liza de Garant&iacute;a<br>
				Para la Reparaci&oacute;n de Fallas T&eacute;cnicas y/o Vicios Ocultos
    		</td>
    		<td  class="reportTitleCell" style="text-align:right; font-size: 12px">
    			S020
    		</td>
      	</tr>
      	<tr>
	        <th align = "left" class="reportCellEven">Garant&iacute;a que otorga:</th>
	        <td class="reportCellEven">
	        	<%= bmoCompany.getLegalName().toString()%>               
	        </td>
	        <th align = "left" class="reportCellEven">En lo sucesivo  &quot;El Constructor&quot;, representada por:</th>
	        <td class="reportCellEven">
	        	<%= bmoCompany.getLegalRep().toString()%>                
	        </td>
        </tr>
        <tr>
	        <th align = "left" class="reportCellEven">Con Domicilio en:</th>
	        <td class="reportCellEven">
		        <%= bmoCompany.getStreet().toString()%> <%= bmoCompany.getNumber().toString()%>
				<%= bmoCompany.getNeighborhood().toString()%>,
	            <%= bmoCityCompany.getName().toString()%> <%= bmoCityCompany.getBmoState().getName().toString()%>,
	            C.P. <%= bmoCompany.getZip().toString()%>               
	        </td>
	        <th align = "left" class="reportCellEven">Tel&eacute;fono:</th>
	        <td class="reportCellEven">
	        	<%= bmoCompany.getPhone().toString()%>            
	        </td>
	    </tr> 
	    <tr>
	        <th align = "left" class="reportCellEven">Fax:</th>
	        <td class="reportCellEven">
	        	<%= bmoCompany.getPhone().toString()%> 
	        </td>
	        <th align = "left" class="reportCellEven">E-mail:</th>
	        <td class="reportCellEven">
	        	<%= bmoCompany.getEmail().toString()%>              
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven" colspan="4">A favor del (Trabajador/Derechohabiente)</th>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Apellido Paterno:</th>
	        <td class="reportCellEven">
	        	<%= bmoCustomer.getFatherlastname().toString()%>
	        </td>
	        <th align = "left" class="reportCellEven">Apellido Materno:</th>
	        <td class="reportCellEven">
	        	<%= bmoCustomer.getMotherlastname().toString()%>             
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Nombre:</th>
	        <td class="reportCellEven" colspan="3">
	        	<%= bmoCustomer.getFirstname().toString()%> 
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven" colspan="4">
	        	En lo sucesivo &quot;El Acreditado&quot;, respecto de la vivienda objeto del cr&eacute;dito:
        	</th>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Calle:</th>
	        <td class="reportCellEven">
	        	<%= bmoProperty.getStreet().toString()%>
	        </td>
	        <th align = "left" class="reportCellEven">No. Ext.:</th>
	        <td class="reportCellEven">
	        	<%= bmoProperty.getNumber().toString()%>             
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Int.:</th>
	        <td class="reportCellEven">
	        	&nbsp;
	        </td>
	        <th align = "left" class="reportCellEven">Lote:</th>
	        <td class="reportCellEven">
	        	<%= bmoProperty.getLot().toString()%>            
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Manzana:</th>
	        <td class="reportCellEven">
	        	<%= bmoDevelopmentBlock.getCode().toString()%>
	        </td>
	        <th align = "left" class="reportCellEven">Colonia/Fraccionamiento:</th>
	        <td class="reportCellEven">
	        	<%= bmoDevelopment.getName().toString()%>
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Entidad:</th>
	        <td class="reportCellEven">
	        	<%= bmoCityDevelopment.getBmoState().getName().toString()%>
	        </td>
	        <th align = "left" class="reportCellEven">Municipio:</th>
	        <td class="reportCellEven">
	        	<%= bmoCityDevelopment.getName().toString()%>
	        </td>
	    </tr>
	    <tr>
	        <th align = "left" class="reportCellEven">Localidad:</th>
	        <td class="reportCellEven">
	        	<%= bmoCityDevelopment.getName().toString()%>
	        </td>
	        <th align = "left" class="reportCellEven">C&oacute;digo Postal:</th>
	        <td class="reportCellEven">
	        	<%= bmoDevelopment.getZip().toString()%>
	        </td>
	    </tr>
    </table>
    <br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr >
			<td align="justify" colspan="2">
				La presente P&oacute;liza, se extiende en cumplimiento a los dispuesto en el instructivo de
				representaci&oacute;n, evaluaci&oacute;n y aprobaci&oacute;n de paquetes de vivienda en L&iacute;nea
				II, publicado en el Diario Oficial de la Federaci&oacute;n, el 13 de agosto de 1999, as&iacute; como
				en el procedimiento de paquetes L&iacute;nea II.

			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr >
			<td  align="center"  colspan="2">
				<b>O B L I G A C I O N E S</b>
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>

		<tr >
			<td  align="justify" colspan="2" >
				Primera.- "El Constructor", se obliga a responder por las fallas t&eacute;cnicas y/o vicios ocultos e impermeabilizaci&oacute;n, que 
				aparecieran en la vivienda objeto del cr&eacute;dito, durante un periodo de dos a&ntilde;os contados a partir de la fecha de entrega de la vivienda. 
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>

		<tr >
			<td  align="justify" colspan="2" >
				Segunda.- "El Constructor", se compromete frente a "El Acreditado" y/o sus beneficiarios, durante el t&eacute;rmino de dos a&ntilde;os a 
				proceder a la reparaci&oacute;n inmediata, por su cuenta y costo, de las fallas t&eacute;cnicas y/o vicios ocultos que se presenten en la
				vivienda objeto de la presente p&oacute;liza. Dichos trabajos los iniciara "El Constructor", dentro de un plazo no mayor de 10 d&iacute;as 
				naturales contados a partir de la fecha en que recibida la comunicaci&oacute;n respectiva por parte de "El Acreditado" debi&eacute;ndolos 
				concluir dentro de un plazo de 10 d&iacute;as h&aacute;biles. "El Constructor" no responder&aacute; por desperfectos derivados del mal uso, 
				falta de mantenimiento de la vivienda o por modificaciones realizadas por parte de "El Acreditado". 
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>

		<tr >
			<td  align="justify" colspan="2">
				Tercera.- "El Acreditado", se da por enterado, del estado que guardan los bienes e instalaciones de la vivienda, 
				de acuerdo a la descripci&oacute;n de especificaciones, mobiliario y equipo y revisi&oacute;n realizada. 
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>

		<tr >
			<td  align="justify" colspan="2">			
				Cuarta.- La garant&iacute;a consignada en la presente p&oacute;liza, se har&aacute; efectiva a favor de "El Acreditado" y/o sus beneficiarios, 
				cuando se le haga dentro de la vigencia de la misma. "El Constructor", releva a el infonavit de cualquier responsabilidad 
				derivada de las reclamaciones que, con fundamento en la presente puede efectuar "El Acreditado". 
			</td>
		</tr>
		<tr><td colspan="2"">&nbsp;</td></tr>

		<tr >
			<td  align="justify" colspan="2">	
				Quinta.- Es responsabilidad de "El Acreditado" conservar en su poder la presente p&oacute;liza de garant&iacute;a y al reportar cualquier 
				desperfecto, "El Constructor", tendr&aacute; la obligaci&oacute;n de atender satisfactoriamente el desperfecto reportado.  
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>

		<tr >
			<td  align="justify" colspan="2">	
				Sexta.- La presente p&oacute;liza estar&aacute; vigente a partir de la fecha de entrega de la vivienda a "El Acreditado" 
				y hasta por el t&eacute;rmino se&ntilde;alado en la obligaci&oacute;n primera. 
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>

		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
	        <th align = "left" class="reportCellEven" nowrap width="40%">Fecha de Entrega de la vivienda:</th>
	        <td class="reportCellEven" colspan="">
	        	&nbsp;
	        </td>
        </tr>
        <tr>
	        <th align = "left" class="reportCellEven" nowrap width="40%">Fecha de Expedici&oacute;n de la presente p&oacute;liza:</th>
	        <td class="reportCellEven" colspan="">
				<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
	        </td>
	    </tr>
		<tr><td colspan="2" height="40">&nbsp;</td></tr>
		</table>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
			<tr>
				<td >
					&nbsp;
				</td>
				<td >
					&nbsp;
				</td>
				<td align="center">
					&nbsp;<b><%= bmoCompany.getLegalName().toString()%></b>
				</td>
			</tr>
			<tr >
				<td width="">
					&nbsp;
				</td>
				<td width="">
					&nbsp;
				</td>
				<td width="" align="center" >
					Nombre o Raz&oacute;n social de la emprea
				</td>
			</tr>
			
			<tr>
				<td align="center" valign="bottom">
					<b><%= bmoCustomer.getDisplayName().toString()%></b>
				</td>
				<td>
					&nbsp;
				</td>
				<td align="center" >
					<br><br>
					<img src="../img/firma_lopez.jpg" width="100" height="50">
					<br><br>
					<b><%= bmoCompany.getLegalRep().toString()%></b>
				</td>
			</tr>
			
			<tr>
				<td align="center" >
					______________________________<br>
					Nombre y Firma del Acreditado
				</td>
				<td>
					&nbsp;
				</td>
				<td align="center" >
					______________________________<br>
					Nombre y Firma del Representante legal
				</td>
			</tr>
		</table>

		<% 
			
		} catch (Exception e) { 
				String errorLabel = "P&oacute;liza de Garant&iacute;a  Para la Reparaci&oacute;n de Fallas Tecnicas y/o Vicios Ocultos";
				String errorText = "El documento no puede ser desplegado.";
				String errorException = e.toString();
				//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		%>
			<%= errorException %>
		<%
			}
		%>

</body>
</html>
