<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ -->
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.ac.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.shared.sf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.ac.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	try {
		
	String title = "";

	BmoSessionSale bmoSessionSale = new BmoSessionSale();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoSessionSale.getProgramCode());

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
	    <title>:::Venta de Sesion:::</title>
	    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
		<link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
	 	<link rel="stylesheet" type="text/css" href="../../css/<%= defaultCss %>"> 
		<link rel="stylesheet" type="text/css" href="../../css/flexwm.css"> 
	</head>
	
	<body class="default" <%= permissionPrint %>>
	
	<%
			NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		    
		    // Obtener parametros
		    int sessionSaleId = 0;
		    if (isExternal) sessionSaleId = decryptId;
		    else if (request.getParameter("foreignId") != null) sessionSaleId = Integer.parseInt(request.getParameter("foreignId"));
		    
		    //Venta de Sesion
		    PmSessionSale pmSessionSale = new PmSessionSale(sFParams);
		    bmoSessionSale = (BmoSessionSale)pmSessionSale.get(sessionSaleId);
		    
		    //Pedido
		    BmoOrder bmoOrder = new BmoOrder();
		    PmOrder pmOrder = new PmOrder(sFParams);
		    if (bmoSessionSale.getOrderId().toInteger() > 0)
		    	bmoOrder = (BmoOrder)pmOrder.get(bmoSessionSale.getOrderId().toInteger());
			   
		    //Empresa
		    BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			if (bmoOrder.getCompanyId().toInteger() > 0)
				bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
			
			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL ="";
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else 
				logoURL = sFParams.getMainImageUrl();
			
			//Ciudad Empresa
			BmoCity bmoCompanyCity = new BmoCity();
			PmCity pmCity = new PmCity(sFParams);
			if (bmoCompany.getCityId().toInteger() > 0) 
				bmoCompanyCity = (BmoCity)pmCity.get(bmoCompany.getCityId().toInteger());
			
			//Cliente
			BmoCustomer bmoCustomer = new BmoCustomer();
		    PmCustomer pmCustomer = new PmCustomer(sFParams);
		    if (bmoSessionSale.getSalesUserId().toInteger() > 0)
		    	bmoCustomer = (BmoCustomer)pmCustomer.get(bmoOrder.getCustomerId().toInteger());
		    
		    //Cliente - Padre/Tutor
			BmoCustomerRelative bmoCustomerRelative = new BmoCustomerRelative();
		    PmCustomerRelative pmCustomerRelative = new PmCustomerRelative(sFParams);
		    
		    ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmFilter filterCurl = new BmFilter();
			BmFilter filterCurlResponsible = new BmFilter();

			filterCurl.setValueFilter(bmoCustomerRelative.getKind(), bmoCustomerRelative.getCustomerId(), bmoCustomer.getId());
			filterList.add(filterCurl);
			filterCurlResponsible.setValueFilter(bmoCustomerRelative.getKind(), bmoCustomerRelative.getResponsible(), 1);
			filterList.add(filterCurlResponsible);
			
			Iterator<BmObject> curl = new PmCustomerRelative(sFParams).list(filterList).iterator();

			while(curl.hasNext()) {
				bmoCustomerRelative = (BmoCustomerRelative)curl.next();
			}
		    
			
			//Vendedor
			BmoUser bmoUser = new BmoUser();
		    PmUser pmUser = new PmUser(sFParams);
		    if (bmoSessionSale.getSalesUserId().toInteger() > 0)
			    bmoUser = (BmoUser)pmUser.get(bmoSessionSale.getSalesUserId().toInteger());
		    
		    
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr  class="reportCellEven">
			<td align="left" width="4%" align="center">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td class="reportTitleCell">
				<%= bmoCompany.getLegalName().toString().toUpperCase() %><br>
				LIBERACI&Oacute;N DE RESPONSABILIDAD
			</td>
			<td class="reportTitleCell" style="text-align: right">
				<%= bmoSessionSale.getCode().toString() %>&nbsp;
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td class="" colspan="2">
				<br>
			</td>
		</tr>
		<tr>
			<td class="" colspan="2">
				En este documento con FECHA <b><%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%></b>
				reporto que mi hijo(a) se encuentra en perfecciones condiciones de salud para realizar las
				actividades dentro de las instalaciones de Flotis Kinderpool y lo libero de cualquier responsabilidad que surgiera  por las
				actividades realizadas.<br><br>
			</td>
		</tr>
		<tr>
			<th align="left" width="30%">
			Aceptando que mi HIJO(a):
			</th>
			<td class="reportCellEven">
				<%= bmoCustomer.getDisplayName().toString()%>
			</td>
			
		</tr>
		<tr>
			<td class="" colspan="2">
				queda bajo mi cuidado o el de alg&uacute;n representante mientras se 
				encuentre dentro de las instalaciones.<br><br>
			</td>
		</tr>
		<tr>
			<th align="left" width="30%">
				Yo, PADRE/TUTOR:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomerRelative.getFullName().toString()%>
				<%= bmoCustomerRelative.getFatherLastName().toString()%>
				<%= bmoCustomerRelative.getMotherLastName().toString()%>
			</td>
		</tr>
		<tr>
			<td class="" colspan="2">
				He recibido, le&iacute;do y aceptado todos los puntos  del reglamento de la Academia de Nataci&oacute;n
				FLOTIS KINDERPOOL S. de R.L. de C.V. Y <%= bmoCompany.getLegalName().toString().toUpperCase() %>
				<br><br>
			</td>
		</tr>
	</table>
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	    <tr align="center">
	        <td align="center"><br><br><br>
	        	___________________
	        </td>
	    </tr>
	    <tr>     
	        <td align="center" class="documentTitleCaption">               
	            Firma del PADRE/TUTOR 
	        </td>
	    </tr>
	</table>   
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="contractTitleCaption" align="right">
					<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
					<%= bmoCompany.getStreet().toString().toUpperCase() %> <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
					<%= bmoCompany.getNeighborhood().toString().toUpperCase() %>, C.P. <%= bmoCompany.getZip().toString().toUpperCase() %><br>
					<%= bmoCompanyCity.getName().toString().toUpperCase() %>, <%= bmoCompanyCity.getBmoState().getName().toString().toUpperCase() %>, 
					<%= bmoCompanyCity.getBmoState().getBmoCountry().getName().toString().toUpperCase() %>.<br>
					Tel: <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
					E-mail: <%= bmoCompany.getEmail().toString() %>
					<br>
					<%= bmoCompany.getWww().toString() %><br>
				
			</td>
		</tr>
	</table>

	<p style="page-break-after: always"></p>
	
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr  class="reportCellEven">
			<td align="left" width="4%" align="center">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td class="reportTitleCell">
				<%= bmoCompany.getLegalName().toString().toUpperCase() %><br>
				REGLAMENTO
			</td>
			<td class="reportTitleCell" style="text-align: right">
				<%= bmoSessionSale.getCode().toString() %>&nbsp;
			</td>
		</tr>
	</table>
	<br><br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr> 
		    <td class="" align="left">
			    <ol align="justify" type="a">
					<li>
						En Flotis Kindepool estamos comprometidos con el medio ambiente, mucho agradeceremos  el cuidado del agua.
					</li>
					<li>
						Es olbigatorio ducharse antes de ingresar a la alberca.
					</li>
					<li>
						Es responsabilidad &uacute;nicamente del padre informar a la instituci&oacute;n sobre cualquier indicaci&oacute;n especial del alumno.
					</li>
					<li>
						Para el caso de clases grupales, estos estar&aacute;n formados por un m&aacute;ximo de 5 alumnos.
					</li>
					<li>
						El horario del alumno quedar&aacute; establecido al momento de la inscripci&oacute;n. No se realizar&aacute;n cambios durante el mes en curso.
					</li>
					<li>
						El alumno no puede permanecer dentro de la alberca si no se encuentra su maestro.
					</li>
					<li>
						Las clases particulares y grupales ser&aacute;n en la hora fijada, si el alumno llega tarde podr&aacute; entrar tomando s&oacute;lo el tiempo restante. Podr&aacute; 
						extender horario dependiendo de la disponibilidad del lugar de la clase siguiente.
					</li>
					<li>
						Se pide por respeto a la clase siguiente, recoger al alumno puntualmente al t&eacute;rmino de su clase, si tienen que salir mientras el alumno est&aacute; en clase favor de tomar su tiempo.
					</li>
					<li>
						Para ingresar a su clase, el ni&ntilde;o deber&aacute; portar el uniforme completo (gorra, traje de ba&ntilde;o y googles).
					</li>
					<li>
						Flotis Kinderpool no se hace responsable por objetos extraviados y/u olvidados dentro de las instalaciones.
					</li>
					<li>
						A fin de respetar el lugar y el horario del alumno, deber&aacute; cubrir su mensualidad dentro de los primero 5 d&iacute;as de cada mes.
					</li>
					<li>
						Flotis Kinderpool no se hace responsable por accidentes dentro de las instalaciones.
					</li>
					<li>
						Queda prohibido cambiar a los alumnos fuera del &aacute;rea de vestidores.
					</li>
					<li>
						Queda prohibido ingresar al &aacute;rea de alberca con alimentos.
					</li>
					<li>
						No se permite ingresar a las instalaciones con envases de vidrio.
					</li>
					<li>
						Queda prohibido correr en el &aacute;rea de alberca.
					</li>
					<li>
						No correr dentro de los ba&ntilde;os.
					</li>
					<li>
						No tirar basura.
					</li>
					<li>
						Que prohibido que los ni&ntilde;os se suban o golpeen los cristales en lo que esperan su clase.
					</li>
					<li>
						En Flotis Kinderpool cuidamos tu planeta por lo que cada notificaci&oacute;n  oficial ser&aacute; entregada v&iacute;a correo electr&oacute;nico a la o las 
						direcciones que el cliente haya proporcionado en la boleta de inscripci&oacute;n.
					</li>
				</ol>                 
		   </td>
	    </tr>
	</table>
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<td class="" colspan="2">
				En caso de no respetar los puntos que conforman este reglamento Flotis Kinderpool se reserva el derecho de admisi&oacute;n.
				<br><br>
			<td>
		</tr>
		<tr>
			<th class="" align="left" width="30%">
				FECHA:
			</th>
			<td class="reportCellEven">
				<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
			</td>
		</tr>
		<tr>
			<th class="" align="left">
				Nombre de ALUMNO:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomer.getDisplayName().toString()%>
			</td>
		</tr>
		<tr>
			<th class="" align="left">
				Nombre de PADRE/TUTOR:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomerRelative.getFullName().toString()%>
				<%= bmoCustomerRelative.getFatherLastName().toString()%>
				<%= bmoCustomerRelative.getMotherLastName().toString()%>
			</td>
		</tr>
	</table>
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	    <tr align="center">
	        <td align="center"><br><br><br>
	        	___________________
	        </td>
	    </tr>
	    <tr>     
	        <td align="center" class="documentTitleCaption">               
	            Firma del PADRE/TUTOR 
	        </td>
	    </tr>
	</table>	
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="contractTitleCaption" align="right">
				<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
				<%= bmoCompany.getStreet().toString().toUpperCase() %> <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
				<%= bmoCompany.getNeighborhood().toString().toUpperCase() %>, C.P. <%= bmoCompany.getZip().toString().toUpperCase() %><br>
				<%= bmoCompanyCity.getName().toString().toUpperCase() %>, <%= bmoCompanyCity.getBmoState().getName().toString().toUpperCase() %>, 
				<%= bmoCompanyCity.getBmoState().getBmoCountry().getName().toString().toUpperCase() %>.<br>
				Tel: <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
				E-mail: <%= bmoCompany.getEmail().toString() %>
				<br>
				<%= bmoCompany.getWww().toString() %><br>
			</td>
		</tr>
	</table>
	

	<p style="page-break-after: always"></p>
	
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr  class="reportCellEven">
			<td align="left" width="4%" align="center">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td class="reportTitleCell">
				<%= bmoCompany.getLegalName().toString().toUpperCase() %><br>
				POL&Iacute;TICA DE PAGO
			</td>
			<td class="reportTitleCell" style="text-align: right">
				<%= bmoSessionSale.getCode().toString() %>&nbsp;
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td>
				<br>
			</td>
		</tr>
		<tr> 
		    <td align="left">
			    <ol align="justify" type="1">
					<li>
						Si el pago de la mensualidad acordada, seg&uacute;n el n&uacute;mero de clases y el paquete contratado, es cubierto duramte los primero 10 d&iacute;as del mes,
						tal como dice el reglamento firmado al momento de inscripci&oacute;n, no sufrir&aacute; alteraci&oacute;n alguna y los ni&ntilde;os tendr&aacute;n derecho a reposici&oacute;n de clases.
					</li>
					<li>
						Si por alguna raz&oacute;n la mensualidad es cubierta entre el d&iacute;a 11 y el 20 de mes, se respetar&aacute; el horario acordado y se hara un cargo adicional del 5% de la mensualidad.
						Bajo esta situaci&oacute;n, no ser&aacute; obligaci&oacute;n de Kindepool reponer clases tomadas durante el mes.
					</li>
					<li>
						Si por alguna raz&oacute;n la mensualidad es cubierta entre el d&iacute;a 20 y el &uacute;ltimo d&iacute;a del mes, se respetar&aacute; el horario acordado y se hara un cargo adicional del 10% de la mensualidad.
						Bajo esta situaci&oacute;n, no ser&aacute; obligaci&oacute;n de Kindepool reponer clases tomadas durante el mes.
					</li>
					<li>
						En caso de no ser aplicado el pago durante el mes, el ni&ntilde;o autom&aacute;ticamente perder&aacute; su horario.
					</li>
				</ol>                 
		   </td>
	    </tr>
	</table>
	<br><br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<th class="" align="left" width="30%">
				FECHA:
			</th>
			<td class="reportCellEven">
				<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
			</td>
		</tr>
		<tr>
			<th class="" align="left">
				Nombre de ALUMNO:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomer.getDisplayName().toString()%>
			</td>
		</tr>
		<tr>
			<th class="" align="left">
				Nombre de PADRE/TUTOR:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomerRelative.getFullName().toString()%>
				<%= bmoCustomerRelative.getFatherLastName().toString()%>
				<%= bmoCustomerRelative.getMotherLastName().toString()%>	
			</td>
		</tr>

		</tr>
	</table>
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	    <tr align="center">
	        <td align="center"><br><br><br>
	        	___________________
	        </td>
	    </tr>
	    <tr>     
	        <td align="center" class="documentTitleCaption">               
	            Firma del PADRE/TUTOR 
	        </td>
	    </tr>
	</table> 
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="contractTitleCaption" align="right">
				<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
				<%= bmoCompany.getStreet().toString().toUpperCase() %> <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
				<%= bmoCompany.getNeighborhood().toString().toUpperCase() %>, C.P. <%= bmoCompany.getZip().toString().toUpperCase() %><br>
				<%= bmoCompanyCity.getName().toString().toUpperCase() %>, <%= bmoCompanyCity.getBmoState().getName().toString().toUpperCase() %>, <%= bmoCompanyCity.getBmoState().getBmoCountry().getName().toString().toUpperCase() %>.<br>
				Tel: <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
				E-mail: <%= bmoCompany.getEmail().toString() %>
				<br>
				<%= bmoCompany.getWww().toString() %><br>
			</td>
		</tr>
	</table> 
	
<p style="page-break-after: always"></p>
	
	
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr  class="reportCellEven">
			<td align="left" width="4%" align="center">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
			</td>
			<td class="reportTitleCell">
				<%= bmoCompany.getLegalName().toString().toUpperCase() %><br>
				FORMULARIO DE AUTORIZACI&Oacute;N DE IM&Aacute;GENES PARA MENORES DE EDAD
			</td>
			<td class="reportTitleCell" style="text-align: right">
				<%= bmoSessionSale.getCode().toString() %>&nbsp;
			</td>
		</tr>
	</table>
	<br>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td colspan="2">
				<br>
			</td>
		</tr>
		<tr>
			<th class="" align="left" width="40%">
				Yo, siendo padre/tutor legal del ALUMNO:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomer.getDisplayName().toString()%>
			</td>
		</tr>
		<tr>
		    <td class="" colspan="2">
		    	autorizo el uso de im&aacute;genes, representaciones y grabaciones (anotadas debajo) del menor de edad arriba nombrado por <%= bmoCompany.getLegalName().toString().toUpperCase() %> a sus
		    	representantes a su entera discreci&oacute;n. Yo, con mi firma abajo, reconozco que tales fotograf&iacute;as, pel&iacute;culas, grabaciones y videos ser&aacute;n de propiedad de 
		    	FLOTIS KINDERPOOL S. de R.L. de C.V. y <%= bmoCompany.getLegalName().toString().toUpperCase() %> y que tendr&aacute; derecho de vender, duplicar, reproducir y hacer otros usos en medios de
		    	comunicaci&oacute;n tales como se describe a continuaci&oacute;n quedando libre y exento de cualquier tipo de reclamo.
	    	</td>
		</tr>
		<tr> 
		    <td align="left" colspan="2">
		    <br>
			    <ol align="justify" type=".">
					<li>
						Fotograf&iacute;as
					</li>
					<li>
						Cintas de Video
					</li>
					<li>
						Im&aacute;genes y videos digitales
					</li>
				</ol>                 
		   </td>
	    </tr>
	    <tr>
		    <td class="" colspan="2">
		    	De mi hijo(a) para su uso en materiales promocionales y educativos de la siguiente manera:
	    	</td>
		</tr>
		<tr> 
		    <td align="left" colspan="2">
		    <br>
			    <ol align="justify" type=".">
					<li>
						Publicaciones impresas o materiales.
					</li>
					<li>
						Publicaciones eletr&oacute;nicas o presentaciones.
					</li>
					<li>
						Sitios Web www.flotiskinderpool.com y de socios comerciales del ramo.
					</li>
					<li>
						P&aacute;ginas de redes sociales como Facebok, Instrgram, Twitter, etc.
					</li>
				</ol>                 
		   </td>
	    </tr>
	    <tr>
		    <td class="" colspan="2">
		    	Estoy de acuerdo a que mi hijo(a), el nombre y la identidad pueda ser revelada s&oacute;lo por su primer nombre, incial del apellido y la edad de la siguiente manera.
		    </td>
		</tr>
		<tr>
		    <td class="" colspan="2">
			    <br>
			    ___________________________,________________ ________________ meses/a&ntilde;os.
			    <br><br>
		    </td>
		</tr>
		<tr>
		    <td class="" colspan="2">
		    	Yo autorizo el uso de estas im&aacute;genes indefinidamente sin compensaci&oacute;n alguna para m&iacute;. Todos los negativos, positivos, grabados, reproducciones digitales y cintas
		    	de video ser&aacute;n propiedad de KINDERPOOL S. de R.L. de C.V. y <%= bmoCompany.getLegalName().toString().toUpperCase() %>
		    </td>
		</tr>
	</table>
	<br><br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="font-size: 12px">
		<tr>
			<th class="" align="left" width="30%">
				FECHA:
			</th>
			<td class="reportCellEven">
				<%= FlexUtil.dateToLongDate(sFParams, SFServerUtil.nowToString(sFParams, "yyyy-MM-dd"))%>
			</td>
		</tr>
		<tr>
			<th class="" align="left">
				Nombre de ALUMNO:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomer.getDisplayName().toString()%>
			</td>
		</tr>
		<tr>
			<th class="" align="left">
				Nombre  de PADRE/TUTOR:
			</th>
			<td class="reportCellEven">
				<%= bmoCustomerRelative.getFullName().toString()%>
				<%= bmoCustomerRelative.getFatherLastName().toString()%>
				<%= bmoCustomerRelative.getMotherLastName().toString()%>
				
			</td>
		</tr>
	</table>
	<br>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	    <tr align="center">
	        <td align="center"><br><br><br>
	        	___________________
	        </td>
	    </tr>
	    <tr>     
	        <td align="center" class="documentTitleCaption">               
	            Firma del PADRE/TUTOR 
	        </td>
	    </tr>
	</table>
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="">
				<br><br>
			</td>
		</tr>
		<tr>
			<td class="contractTitleCaption" align="right">
				<b><%= bmoCompany.getLegalName().toString().toUpperCase() %></b><br>
				<%= bmoCompany.getStreet().toString().toUpperCase() %> <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
				<%= bmoCompany.getNeighborhood().toString().toUpperCase() %>, C.P. <%= bmoCompany.getZip().toString().toUpperCase() %><br>
				<%= bmoCompanyCity.getName().toString().toUpperCase() %>, <%= bmoCompanyCity.getBmoState().getName().toString().toUpperCase() %>, <%= bmoCompanyCity.getBmoState().getBmoCountry().getName().toString().toUpperCase() %>.<br>
				Tel: <%= bmoCompany.getNumber().toString().toUpperCase() %><br>
				E-mail: <%= bmoCompany.getEmail().toString() %>
				<br>
				<%= bmoCompany.getWww().toString() %><br>
			</td>
		</tr>
	</table> 
	<%  
		} catch (Exception e) { 
		    String errorLabel = "Error de Formato";
		    String errorText = "El formato no pudo ser desplegado exitosamente.";
		    String errorException = e.toString();
		    
		    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
	    }	
	%>
