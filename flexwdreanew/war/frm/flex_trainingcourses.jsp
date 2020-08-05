<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmQuoteGroup"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteGroup"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteItem"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteEquipment"%>
<%@page import="com.flexwm.shared.cm.BmoQuoteStaff"%>
<%@page import="com.flexwm.server.cm.PmQuoteItem"%>
<%@page import="com.flexwm.server.cm.PmQuoteEquipment"%>
<%@page import="com.flexwm.server.cm.PmQuoteStaff"%>
<%@page import="com.flexwm.server.cm.PmQuote"%>
<%@page import="com.flexwm.shared.cm.BmoQuote"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.server.cm.PmCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="com.flexwm.server.cm.PmCustomerContact"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowLog"%>
<%@page import="com.flexwm.server.wf.PmWFlowLog"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.flexwm.shared.fi.BmoCurrency"%>
<%@page import="com.flexwm.server.fi.PmCurrency"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "Cursos de Capacitaci&oacute;n VISUAL 8.0";
	
	BmoOpportunity bmoOpportunity = new BmoOpportunity();
	PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoOpportunity.getProgramCode());
	
	// Conexiones Base de datos
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
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
		<link rel="stylesheet" type="text/css" href="../../css/<%= defaultCss %>"> 
		<link rel="stylesheet" type="text/css" href="../../css/flexwm.css"> 
	</head>
	<body class="default" <%= permissionPrint %>>
<%
	if (sFParams.hasRead(bmoProgram.getCode().toString())) {
		
	    // Obtener parametros
	    int opportunityId = 0;
	    if (isExternal) opportunityId = decryptId;
	    else opportunityId = Integer.parseInt(request.getParameter("foreignId"));  
	    
	    // Si es llamada externa, utilizar llave desencriptada, en caso contrario utilizar llave de la orden
		bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
		
		// Cotizacion
		BmoQuote bmoQuote = new BmoQuote();
		PmQuote pmQuote = new PmQuote(sFParams);
		bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());

		// Vendedor
		BmoUser bmoUser = new BmoUser();
		PmUser pmUser = new PmUser(sFParams);
		bmoUser = (BmoUser)pmUser.get(bmoOpportunity.getUserId().toInteger());

		// Cliente
		BmoCustomer bmoCustomer = new BmoCustomer();
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		bmoCustomer = (BmoCustomer)pmCustomer.get(bmoOpportunity.getCustomerId().toInteger());

		// Moneda de la cotizacion
		BmoCurrency bmoCurrency = new BmoCurrency();
		PmCurrency pmCurrency = new PmCurrency(sFParams);
		bmoCurrency = (BmoCurrency)pmCurrency.get(bmoQuote.getCurrencyId().toInteger());
		
		// Empresa de la Oportunidad
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoOpportunity.getCompanyId().toInteger());

		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
		String customer = "", orderBy = "";
		if (bmoCustomer.getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
			customer =  bmoCustomer.getDisplayName().toHtml();
			orderBy = " ASC";
		} else {
			customer = bmoCustomer.getLegalname().toHtml();
			orderBy = " DESC";
		}	
		
		String sqlAditional  = "", address = "", userAuthorization = "", dateAuthorization = "";
		
		sqlAditional = " SELECT * FROM customeraddress " +
							" LEFT JOIN cities ON (city_cityid = cuad_cityid) " +
							" LEFT JOIN states ON (stat_stateid = city_stateid) " +
		    				" WHERE cuad_customerid = " + bmoOpportunity.getCustomerId().toInteger() +
		    				" ORDER BY cuad_type " +orderBy;
		
		pmConn.doFetch(sqlAditional);
		if (pmConn.next()) { 
			address = pmConn.getString("customeraddress", "cuad_street") +
						" #" + pmConn.getString("customeraddress", "cuad_number") + 
						" " + pmConn.getString("customeraddress", "cuad_neighborhood") + 
						", C.P. " + pmConn.getString("customeraddress", "cuad_zip") +
						", " + pmConn.getString("cities", "city_name") +
						", " + pmConn.getString("states", "stat_name");
		}
		
		// Contacto clientes desde Cotizacion
		BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
		PmCustomerContact pmCustomerContact = new PmCustomerContact(sFParams);
		if (bmoQuote.getCustomerContactId().toInteger() > 0)
			bmoCustomerContact = (BmoCustomerContact)pmCustomerContact.get(bmoQuote.getCustomerContactId().toInteger());
		
		
	%>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td align="left" width="5%" rowspan="9" valign="top">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
				</td>
				<td colspan="4" class="reportTitleCell" style="text-align:center">
					<%= bmoCompany.getName().toHtml() %>
					<br>
					<span style="font-size: 12px">
						<u>Servicios de Consultor&iacute;a VISUAL ERP</u>
						<br>
					
						<%= title %>
					</span>
				</td>
			</tr>
		</table>
		<div style="font-size: 12px" align="right" >
			<br>
			A <%= FlexUtil.dateToLongDate(sFParams, bmoOpportunity.getStartDate().toString()) %>
		</div>
		<br><p>
		<div style="font-size: 12px" align="left">
			<b>
				<br>
				<%= bmoCustomerContact.getFullName().toString()%>
				<%= bmoCustomerContact.getFatherLastName().toString()%>
				<%= bmoCustomerContact.getMotherLastName().toString()%>
				<br>
				<%= bmoCustomerContact.getPosition().toString() %>
				<br>
				<%= customer %>
			</b>
			<p><br><p>
			Estimado <%= bmoCustomerContact.getFullName().toString()%>,
			<p><br><p>
			Con base en el inter&eacute;s de <b><%= customer %></b> de contar con nuestra cotizaci&oacute;n, 
			Visual M&eacute;xico presenta esta propuesta econ&oacute;mica en relaci&oacute;n a:
		</div>
		<p>
		<div align="center"  style="font-size: 12px">
			<div style="border: 2px solid; border-radius: 4px; width: 300px" align="center">
				<b>
					VISUAL 8.0
					<br>
					CURSOS DE CAPACITACI&Oacute;N
				</b>
			</div>
		</div>
		<br><p>
		<div  style="font-size: 12px">
			Agradecemos de antemano la confianza depositada en Visual M&eacute;xico, 
			sin m&aacute;s por el momento y en espera de su favorable respuesta, 
			quedamos a su disposici&oacute;n para cualquier duda o aclaraci&oacute;n.
		</div>
		<br><p><br>
		<div align="center">
			Atentamente,
			<br><p>
			<b>
				<%= bmoUser.getFirstname().toString() %>
				<%= bmoUser.getFatherlastname().toString() %>
				<%= bmoUser.getMotherlastname().toString() %>
			</b>
		</div>
		
		<p style="page-break-after: always">&nbsp;</p>

		<div style="font-size: 14px">
			<b>Alcance:</b>
		</div>
		<p>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<th class="reportHeaderCell" align="left">
					Cursos de Capacitaci&oacute;n:
				</th>
			</tr>
			<tr>
				<td class="reportCellEven">
					- Planeaci&oacute;n de producci&oacute;n: Incluye MRP (Material Requirements Planning)
				</td>
			</tr>
			<tr>
				<td class="reportCellEven">
					- Captura la Mano de Obra
				</td>
			</tr>
			<tr>
				<td class="reportCellEven">
					- Transferencia entre ubicaciones
				</td>
			</tr>
			<tr>
				<td class="reportCellEven">
					- Emisi&oacute;n / Devoluci&oacute;n de Materia Prima
				</td>
			</tr>
			<tr>
				<td class="reportCellEven">
					- Recepci&oacute;n / Devoluci&oacute;n de Producto Terminado
				</td>
			</tr>
		</table>
		
		<div style="font-size: 14px">
			<b>Temario:</b>
		</div>
		<p>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<th class="reportHeaderCell" align="left">
					Taller
				</th>
				<th class="reportHeaderCell" align="left">
					Temas
				</th>
				<th class="reportHeaderCell" align="left">
					Departamento
				</th>
			</tr>
			<tr>
				<td class="reportCellEven" align="left" rowspan="13">
					<b>Planeaci&oacute;n y Producci&oacute;n</b>
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					MRP
				</td>
				<td class="reportCellEven" align="left"  rowspan="7">
					Planeaci&oacute;n
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Ordenes de trabajo (MRP)
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Planeaci&oacute;n de OT
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Asignaciones
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					MPS
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Forecast
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Revisi&oacute;n de Reportes relacionados al &aacute;rea
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					&Oacuterdenes de compra (MRP)
				</td>
				<td class="reportCellEven" align="left" rowspan="2">
					Compras
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Maestro de servicios
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Operaci&oacute;n de las &Oacute;rdenes de Trabajo y lista de materiales
				</td>
				<td class="reportCellEven" align="left" rowspan="3">
					Producci&oacute;n
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Captura de Mano de Obra
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Revisi&oacute;n de Reportes relacionados al &aacute;rea
				</td>
			</tr>
			
			<tr>
				<td class="reportCellEven" align="left" rowspan="5">
					<b>Almac&eacute;n</b>
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Almacenes y Ubicaciones
				</td>
				<td class="reportCellEven" align="left" rowspan="5">
					Almac&eacute;n
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" align="left">
					Movimientos de Almac&eacute;n
				</td>
			</tr>
			<tr>
				<td class="reportCellEven"  align="left">
					Emisi&oacute;n y Recepci&oacute;n
				</td>
			</tr>
			<tr>
				<td class="reportCellEven"  align="left">
					Revisi&oacute;n de Reportes relacionados al &aacute;rea
				</td>
			</tr>
		</table>
		
		<p style="page-break-after: always">&nbsp;</p>
		
		<div style="font-size: 14px">
			<b>Propuesta Financiera:</b>
		</div>
		<p>
		<div  style="font-size: 12px">
			A continuaci&oacute;n presentamos nuestra propuesta econ&oacute;mica en base a nuestra experiencia:
		</div>
		<p>
		<table border="0" cellspacing="0" width="50%" cellpadding="0" style="font-size: 12px" align="center">
			<tr>
				<th class="reportHeaderCellCenter">
					Concepto
				</th>
				<th class="reportHeaderCellCenter">
					D&iacute;as
				</th>
			</tr>
			<%
			double sumQuantityItems = 0;
			
			// Grupos de la Cotizacion
			BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
			PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId().getName(), bmoQuote.getId());
			Iterator<BmObject> quoteGroups = pmQuoteGroup.list(bmFilter).iterator();
			int i = 1;
			while (quoteGroups.hasNext()) {
				bmoQuoteGroup = (BmoQuoteGroup)quoteGroups.next();

				// Items de Grupos de la Cotizacion
				
				BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
				PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
				BmFilter bmFilterQ = new BmFilter();
				bmFilterQ.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(), bmoQuoteGroup.getId());
				ArrayList list = pmQuoteItem.list(bmFilterQ);
				Iterator<BmObject> items = list.iterator();
				int index = 1, count = list.size();
				while(items.hasNext()) {
					bmoQuoteItem = (BmoQuoteItem)items.next();	
					sumQuantityItems += bmoQuoteItem.getQuantity().toDouble();
					%>
					<tr>	   	                      
						<% 	if (bmoQuoteItem.getProductId().toInteger() > 0) { %>
								<td class="reportCellEven" align ="center">  
								<% 	if(bmoQuoteItem.getBmoProduct().getDisplayName().toString().equals("")) { %>
										<%= bmoQuoteItem.getBmoProduct().getName().toHtml() %>
								<% 	} else { %>
										<%= bmoQuoteItem.getBmoProduct().getDisplayName().toHtml() %>
								<% 	} %>
								</td>
						<% 	} else { %>
								<td class="reportCellEven" align ="center" >  
									<%= bmoQuoteItem.getName().toHtml() %>
								</td>
						<% 	} %>
						<td class="reportCellEven" align="center">
							<%= bmoQuoteItem.getQuantity().toDouble() %>
						</td>
					</tr>
				<% 
						index++;
					}// fin de items
					%>
		<% 	} //fin de quoteGroups
			%> 
			<tr>
				<th class="reportHeaderCellCenter" align="center">
					TOTAL:
				</th>
				<th class="reportHeaderCellCenter" align="center">
					<%= sumQuantityItems%>
				</th>
			</tr>
			<tr>
				<th class="reportHeaderCellCenter" align="center">
					MONTO <%= bmoCurrency.getCode().toString()%>
				</th>
				<th class="reportHeaderCellCenter" align="center">
					<%= SFServerUtil.formatCurrency(bmoQuote.getAmount().toDouble()) %>
				</th>
			</tr>
		</table>
		
		<br><p>
		<div style="font-size: 14px">
			<b>Bases de la Propuesta.</b>
		</div>
		<p>
		<ul style="font-size: 12px">
			<li>
				Los Desarrollos Adicionales que <b><%= customer%></b> llegase a requerir por la naturaleza de su operaci&oacute;n, 
				no est&aacute;n considerados dentro de la presente propuesta. En caso de requerir cualquier tipo de desarrollo, 
				deber&aacute; crearse una especificaci&oacute;n t&eacute;cnica, la cual ser&aacute; validada por ambas partes con el fin de contar con un alcance y 
				poder cotizar el desarrollo requerido antes de ser ejecutado.
			</li>
			<br>
			<li>
			 	El "d&iacute;a" de servicios comprende ocho horas incluyendo una hora de comida.
			</li>
		 	<br>
			<li>
				<b>El plan de pagos propuesto queda la siguiente manera: 50% de Anticipo para comenzar el proyecto y el 50% restante al concluir el proyecto.</b>
			</li>
			<br>
			<li>
				Los precios est&aacute;n expresados en d&oacute;lares americanos y no incluyen impuestos.
			</li>
			<br>
			<li>
				En su momento los pagos podr&aacute;n ser realizados en moneda nacional al tipo de cambio del Diario del Banco de M&eacute;xico.
			</li>
			<br>
			<li>
				Los gastos de viajes fuera de la ciudad de Guadalajara y de la Ciudad de M&eacute;xico incurridos durante el proceso de 
				implementaci&oacute;n son con cargo a <b><%= customer%></b>
			</li>
			<br>
			<li>
				El apoyo adicional de Consultor&iacute;a ser&aacute; por solicitud de <b><%= customer%></b>,
				previa aprobaci&oacute;n del mismo para su posterior facturaci&oacute;n.
			</li>
		</ul>
		
		<br><p>
		<div style="font-size: 14px">
			<b>Cotizaci&oacute;n Confidencialidad y vigencia  de la propuesta.</b>
		</div>
		<p>
		<div  style="font-size: 12px">
			La propuesta es exclusiva y confidencial para <b><%= customer%></b> y tiene vigencia por 15 d&iacute;as naturales.
		</div>
		<br><p>
		<table border="0" cellspacing="0" width="80%" cellpadding="0" style="font-size: 12px" align="center">
			<caption><b>Aprobaci&oacute;n Cliente:</b></caption>
			<tr>
				<td class="reportCellEven" width="25%" style="height: 40px">
					Firma
				</td>
				<td class="reportCellEven">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" style="height: 40px">
					Nombre
				</td>
				<td class="reportCellEven">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" style="height: 40px">
					Puesto
				</td>
				<td class="reportCellEven">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td class="reportCellEven" style="height: 40px">
					Fecha
				</td>
				<td class="reportCellEven">
					&nbsp;
				</td>
			</tr>
		</table>	
	<%	
		pmConn.close();
	} //Fin Si tiene permiso para ver
		} catch (Exception e) { 
				String errorLabel = "Error de Formato";
				String errorText = "El Formato no se puede desplegar";
				String errorException = e.toString();
				
				response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		%>
			<%= errorException %>
	<%
		}
	%>

</body>
</html>
