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
<%@page
	import="com.google.web.bindery.autobean.shared.AutoBeanFactory.NoWrap"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.ac.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.ac.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login_opt.jsp"%>

<%
	String title = "Evaluación";
	PmConn pmConnResponsible = new PmConn(sFParams);
	pmConnResponsible.open();

	try {
		BmoSessionReview bmoSessionReview = new BmoSessionReview();
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram = new PmProgram(sFParams);
		bmoProgram = (BmoProgram) sFParams.getBmoProgram(bmoSessionReview.getProgramCode());
		BmoProgramSessionLevel bmoProgramSessionLevel = new BmoProgramSessionLevel();
%>

<html>
<%
	// Imprimir
		String print = "0", permissionPrint = "";
		if ((String) request.getParameter("print") != null)
			print = (String) request.getParameter("print");

		// Exportar a Excel
		String exportExcel = "0";
		if ((String) request.getParameter("exportexcel") != null)
			exportExcel = (String) request.getParameter("exportexcel");
		if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "inline; filename=\"" + title + ".xls\"");
		}

		//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
		//En caso de que mande a imprimir, deshabilita contenido
		if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) {
%>
<style>
body {
	user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	-webkit-user-select: none;
	-ie-user-select: none;
	-khtml-user-select: none;
	-ms-user-select: none;
	-webkit-touch-callout: none
}
</style>
<style type="text/css" media="print">
* {
	display: none;
}
</style>
<%
	permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if (print.equals("1") || exportExcel.equals("1")) {
%>
<script>
	alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
</script>
<%
	}
		}
%>
<head>
<title>:::<%=title%>:::
</title>
<link rel="stylesheet" type="text/css"
	href="<%=sFParams.getAppURL()%>css/<%=defaultCss%>">
<link rel="stylesheet" type="text/css"
	href="<%=sFParams.getAppURL()%>css/flexwm.css">
</head>
<body class="default" <%=permissionPrint%>>

	<%
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);

			// Obtener parametros
			int sessionReviewId = 0;
			if (isExternal)
				sessionReviewId = decryptId;
			else if (request.getParameter("foreignId") != null)
				sessionReviewId = Integer.parseInt(request.getParameter("foreignId"));

			// Evaluacion
			PmSessionReview pmSessionReview = new PmSessionReview(sFParams);
			bmoSessionReview = (BmoSessionReview) pmSessionReview.get(sessionReviewId);

			// Nivel del Programa
			// 			BmoPro bmose = new BmoCompany();
			// 			PmCompany pmCompany = new PmCompany(sFParams);
			// 			bmoCompany = (BmoCompany) pmCompany.get(bmoSessionSale.getCompanyId().toInteger());

			// Venta 
			PmSessionSale pmSessionSale = new PmSessionSale(sFParams);
			BmoSessionSale bmoSessionSale = (BmoSessionSale) pmSessionSale
					.get(bmoSessionReview.getSessionSaleId().toInteger());

			// Cliente
			BmoCustomer bmoCustomer = new BmoCustomer();
			PmCustomer pmCustomer = new PmCustomer(sFParams);
			bmoCustomer = (BmoCustomer) pmCustomer.get(bmoSessionSale.getCustomerId().toInteger());

			// Obtener el instructor
			PmUser pmUser = new PmUser(sFParams);
			BmoUser bmoUser = (BmoUser) pmUser.get(bmoSessionReview.getUserId().toInteger());

			// Empresa
			BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			bmoCompany = (BmoCompany) pmCompany.get(bmoSessionSale.getCompanyId().toInteger());

			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL ="";
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else 
				logoURL = sFParams.getMainImageUrl();

			// Ciudad de la Empresa
			BmoCity bmoCompanyCity = new BmoCity();
			PmCity pmCity = new PmCity(sFParams);
			if (bmoCompany.getCityId().toInteger() > 0)
				bmoCompanyCity = (BmoCity) pmCity.get(bmoCompany.getCityId().toInteger());

			// Responsable del Cliente
			BmoCustomerRelative bmoCustomerRelative = new BmoCustomerRelative();
			PmCustomerRelative pmCustomerRelative = new PmCustomerRelative(sFParams);

			
			pmConnResponsible.disableAutoCommit();

			String sql = "SELECT * FROM customerrelatives " + " WHERE curl_customerid = " + bmoCustomer.getId()
					+ " AND curl_responsible = " + true;
			pmConnResponsible.doFetch(sql);
			String responsible = "";
			while (pmConnResponsible.next()) {

				if (pmConnResponsible.getString("curl_type").equals("" + BmoCustomerRelative.TYPE_MOTHER)) {
					responsible = pmConnResponsible.getString("curl_fullname") + " "
							+ pmConnResponsible.getString("curl_fatherlastname") + " "
							+ pmConnResponsible.getString("curl_motherlastname");

					break;
				} else {
					responsible = pmConnResponsible.getString("curl_fullname") + " "
							+ pmConnResponsible.getString("curl_fatherlastname") + " "
							+ pmConnResponsible.getString("curl_motherlastname");
				}
			}
	%>

	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		style="font-size: 12px">
		<tr>
			<td align="left" width="6%" rowspan="2" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=logoURL%>"></td>
			<td class="contracSubTitle" align="center"><b><%=bmoCompany.getLegalName().toString().toUpperCase()%></b>
			</td>
		</tr>
		<tr>
			<td class="contractTitleCaption" align="center"><%=bmoCompany.getStreet().toString().toUpperCase()%>
				<%=bmoCompany.getNumber().toString().toUpperCase()%><br> <%=bmoCompany.getNeighborhood().toString().toUpperCase()%>,
				C.P. <%=bmoCompany.getZip().toString().toUpperCase()%><br> <%=bmoCompanyCity.getName().toString().toUpperCase()%>,
				<%=bmoCompanyCity.getBmoState().getName().toString().toUpperCase()%>,
				<%=bmoCompanyCity.getBmoState().getBmoCountry().getName().toString().toUpperCase()%>.<br>
				TEL: <%=bmoCompany.getNumber().toString().toUpperCase()%><br> <b><%=bmoCompany.getWww().toString()%></b></td>
		</tr>
	</table>

	<br>

	<div align="right" style="font-size: 14px">
		<%=FlexUtil.dateToLongDate(sFParams, bmoSessionReview.getDateReview().toString())%>
		<br>
		<%=bmoCompanyCity.getName().toHtml()%>,
		<%=bmoCompanyCity.getBmoState().getName().toHtml()%>
	</div>
	<div style="font-size: 14px">
		Sres.:
		<%=responsible%>
		<p>
			Por medio de la presente hacemos entrega de la evaluaci&oacute;n y
			observaciones del desempe&ntilde;o dentro de la academia de
			nataci&oacute;n. <br>Quedamos a sus &oacute;rdenes para
			cualquier duda o aclaraci&oacute;n.
		<p>
	</div>

	<div style="font-size: 14px">
		Su hijo est&aacute; cursando el Nivel:
		<b> 
			<%=bmoSessionReview.getBmoProgramSessionLevel().getSequence().toHtml()%>.-
			<%=bmoSessionReview.getBmoProgramSessionLevel().getDescription().toHtml()%>
		</b>
		<p>
			Caracter&iacute;sticas del Nivel: <br>
			<%
				BmoProgramSessionSubLevel bmoProgramSessionSubLevel = new BmoProgramSessionSubLevel();
					BmFilter bmFilterSessionReviewId = new BmFilter();
					bmFilterSessionReviewId.setValueFilter(bmoProgramSessionSubLevel.getKind(),
							bmoProgramSessionSubLevel.getSessionReviewId().getName(), 
							bmoSessionReview.getId());
					Iterator<BmObject> list = new PmProgramSessionSubLevel(sFParams).list(bmFilterSessionReviewId).iterator();

					String description = "";
					while (list.hasNext()) {
						bmoProgramSessionSubLevel = (BmoProgramSessionSubLevel) list.next();
						description += " - " + bmoProgramSessionSubLevel.getDescription().toHtml() + "<br>";
					}
			%>
			<b><%=description%></b>
	</div>
	<br>
	<table border="0" style="width: 100%; font-size: 14px">
		<tr>
			<th class="reportHeaderCell" colspan="3" style="text-align: center">
				<%=bmoCustomer.getFirstname().toHtml()%> <%=bmoCustomer.getFatherlastname().toHtml()%>
				<%=bmoCustomer.getFatherlastname().toHtml()%>
			</th>
		</tr>
		<tr>
			<th class="reportCellEven" align="left">
			<%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgramSessionLevel.getProgramCode(),
						bmoProgramSessionLevel.getSequence()))%>:
				<%=bmoSessionReview.getBmoProgramSessionLevel().getSequence().toHtml()%>
				<%=bmoSessionReview.getBmoProgramSessionLevel().getName().toHtml()%>
			</th>
			<th class="reportCellEven" align="left"><%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(
						bmoProgramSessionSubLevel.getProgramCode(), bmoProgramSessionSubLevel.getProgress()))%>
			</th>
			<th class="reportCellEven" align="left"><%=HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(
						bmoProgramSessionSubLevel.getProgramCode(), bmoProgramSessionSubLevel.getObservation()))%>
			</th>
		</tr>
		<%
			Iterator<BmObject> list2 = new PmProgramSessionSubLevel(sFParams).list(bmFilterSessionReviewId).iterator();
				while (list2.hasNext()) {
					bmoProgramSessionSubLevel = (BmoProgramSessionSubLevel) list2.next();
					%>
					<tr>
						<td class="reportCellEven" align="left"><%=bmoProgramSessionSubLevel.getName().toHtml()%>
						</td>
						<td class="reportCellEven" align="left">
							<%
								if (bmoProgramSessionSubLevel.getProgress().toBoolean()) {
							%> 		Lo Logra <%
								} else {
							%> 		En Proceso <%
								}
							%>
						</td>
						<td class="reportCellEven" align="left"><%=bmoProgramSessionSubLevel.getObservation().toHtml()%>
						</td>
					</tr>
		<%		} %>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td align="left" class="detailStart" colspan="3">
				<p class="documentComments">
					<b> <%=HtmlUtil.stringToHtml(
						sFParams.getFieldFormTitle(bmoSessionReview.getProgramCode(), bmoSessionReview.getComments()))%>:
					</b> <br>
					<%=bmoSessionReview.getComments().toHtml()%></p>
			</td>
		</tr>
	</table>
	<%
		} catch (Exception e) {
			String errorLabel = "Error en la Formato";
			String errorText = "El Formato '" + title + "' no pudo ser desplegado exitosamente.";
			String errorException = e.toString();

			response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText
					+ "&errorException=" + errorException);
		}
	finally{
		pmConnResponsible.close();
	}
	%>