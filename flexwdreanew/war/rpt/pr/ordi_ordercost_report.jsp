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

<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp"%>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
	String title = "Reportes de Rentabilidad de Pedidos";
	BmoOrder bmoOrder = new BmoOrder();
	BmoCustomer bmoCustomer = new BmoCustomer();
	BmoWFlow bmoWFlow = new BmoWFlow();
	PmCurrency pmCurrency = new PmCurrency(sFParams);
	BmoCurrency bmoCurrency = new BmoCurrency();
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	//Tipo de Pedido
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());

	String sql = "", where = "", ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus = "", lockStartDate = "",
			lockEndDate = "", sqlCurrency = "", filters = "", fullName = "", whereExtra = "", whereProduct = "", whereProductFamily = "",
			whereProductGroup = "", productFamilyId = "", productGroupId = "";
	int programId = 0, customerId = 0, cols = 0, areaId = 0, orderId = 0, industryId = 0, userId = 0,
			productId = 0, showProductExtra = 0, currencyId = 0, dynamicColspan = 0, dynamicColspanMinus = 0;
	double nowParity = 0, defaultParity = 0;

	// Obtener parametros       
	if (request.getParameter("programId") != null)
		programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("orde_orderid") != null)
		orderId = Integer.parseInt(request.getParameter("orde_orderid"));
	if (request.getParameter("orde_customerid") != null)
		customerId = Integer.parseInt(request.getParameter("orde_customerid"));
	if (request.getParameter("orde_status") != null)
		ordeStatus = request.getParameter("orde_status");
	if (request.getParameter("orde_lockstatus") != null)
		lockStatus = request.getParameter("orde_lockstatus");
	if (request.getParameter("orde_deliverystatus") != null)
		deliveryStatus = request.getParameter("orde_deliverystatus");
	if (request.getParameter("orde_paymentstatus") != null)
		paymentStatus = request.getParameter("orde_paymentstatus");
	if (request.getParameter("orde_lockstart") != null)
		lockStartDate = request.getParameter("orde_lockstart");
	if (request.getParameter("orde_lockend") != null)
		lockEndDate = request.getParameter("orde_lockend");
	if (request.getParameter("area_areaid") != null)
		areaId = Integer.parseInt(request.getParameter("area_areaid"));
	if (request.getParameter("orde_currencyid") != null)
		currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
	if (request.getParameter("cust_industryid") != null)
		industryId = Integer.parseInt(request.getParameter("cust_industryid"));
	if (request.getParameter("orde_userid") != null)
		userId = Integer.parseInt(request.getParameter("orde_userid"));
	if (request.getParameter("prod_productid") != null)
		productId = Integer.parseInt(request.getParameter("prod_productid"));
	if (request.getParameter("prod_productfamilyid") != null)
		productFamilyId = request.getParameter("prod_productfamilyid");
	if (request.getParameter("prod_productgroupid") != null)
		productGroupId = request.getParameter("prod_productgroupid");
	if (request.getParameter("showProductExtra") != null)
		showProductExtra = Integer.parseInt(request.getParameter("showProductExtra"));

	
	bmoProgram = (BmoProgram)pmProgram.get(programId);

	// Filtros listados
	if (orderId > 0) {
		where += " AND orde_orderid = " + orderId;
		filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
	}

	if (userId > 0) {
		//if (sFParams.restrictData(bmoOrder.getProgramCode())) {
			where += " AND orde_userid = " + userId;
		/*} else {
			where += " AND ( " + " orde_userid = " + userId + " OR orde_wflowid IN ( "
					+ " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (wflu_wflowid = wflw_wflowid) " + " WHERE wflu_userid = " + userId
					+ " AND wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' " + " ) " + " )";
		}*/
		filters += "<i>Vendedor: </i>" + request.getParameter("orde_useridLabel") + ", ";
	}

	if (customerId > 0) {
		where += " AND cust_customerid = " + customerId;
		filters += "<i>Cliente: </i>" + request.getParameter("orde_customeridLabel") + ", ";
	}

	if (industryId > 0) {
		where += " AND cust_industryid = " + industryId;
		filters += "<i>Giro: </i>" + request.getParameter("cust_industryidLabel") + ", ";
	}

	if (areaId > 0) {
		where += " AND area_areaid = " + areaId;
		filters += "<i>Dpto.: </i>" + request.getParameter("area_areaidLabel") + ", ";
	}

	if (!ordeStatus.equals("")) {
		//where += " AND orde_status like '" + status + "'";
		where += SFServerUtil.parseFiltersToSql("orde_status", ordeStatus);
		filters += "<i>Estatus: </i>" + request.getParameter("orde_statusLabel") + ", ";
	}

	if (!deliveryStatus.equals("")) {
		//where += " and orde_deliverystatus like '" + deliveryStatus + "'";
		where += SFServerUtil.parseFiltersToSql("orde_deliverystatus", deliveryStatus);
		filters += "<i>Entrega: </i>" + request.getParameter("orde_deliverystatusLabel") + ", ";
	}

	if (!paymentStatus.equals("")) {
		//where += " AND orde_paymentstatus like '" + paymentStatus + "'";
		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
		filters += "<i>Pago: </i>" + request.getParameter("orde_paymentstatusLabel") + ", ";
	}

	if (!lockStatus.equals("")) {
		where += " AND orde_lockstatus like '" + lockStatus + "'";
		filters += "<i>Apartado: </i>" + request.getParameter("orde_lockstatusLabel") + ", ";
	}

	if (!lockStartDate.equals("")) {
		where += " AND orde_lockstart >= '" + lockStartDate + "' ";
		filters += "<i>Apartado Inicio: </i>" + lockStartDate + ", ";
	}

	if (!lockEndDate.equals("")) {
		where += " AND orde_lockstart <= '" + lockEndDate + "' ";
		filters += "<i>Apartado Final: </i>" + lockEndDate + ", ";
	}

	if (productId > 0) {
		whereProduct = " AND ordi_productid = " + productId;
		filters += "<i>Producto: </i>" + request.getParameter("prod_productidLabel") + ", ";
	}

	if (showProductExtra != 2) {
		if (showProductExtra == 1)
			whereExtra += " AND ordi_productid IS NULL ";
		else
			whereExtra += " AND ordi_productid > 0 ";
		filters += "<i>Mostrar Items: </i>" + request.getParameter("showProductExtraLabel") + ", ";
	} else {
		filters += "<i>Mostrar Items: </i> Todos, ";
	}

	if (!productFamilyId.equals("")) {
		whereProductFamily += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
		filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
	}

	if (!productGroupId.equals("")) {
		whereProductGroup += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
		filters += "<i>Grupo Prod.: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
	}

	if (currencyId > 0) {
		bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
		defaultParity = bmoCurrency.getParity().toDouble();

		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
				+ " | <i>Tipo de Cambio Actual : </i>" + defaultParity;
	} else {
		filters += "<i>Moneda: </i> Todas ";
		sqlCurrency = " SELECT cure_currencyid, cure_code, cure_name, cure_parity FROM " + SQLUtil.formatKind(sFParams, " orders ")
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) "
				+ " WHERE orde_orderid > 0 ";
		if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("") || !productGroupId.equals("")) {
			sqlCurrency += " AND orde_orderid IN ( " + " SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_orderid = orde_orderid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" ON (ordi_ordergroupid = ordg_ordergroupid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) "
					+ " WHERE orde_orderid = orde_orderid " + whereProduct + whereExtra
					+ whereProductFamily + whereProductGroup + " ) ";
		}
	   	sqlCurrency += " GROUP BY cure_currencyid ORDER BY cure_currencyid ASC";
	}

	if (sFParams.getSelectedCompanyId() > 0)
		filters += "<i>Empresa: </i>" + sFParams.getBmoSelectedCompany().getName().toString() + " | "
				+ sFParams.getBmoSelectedCompany().getName().toString() + ", ";

	// Obtener disclosure de datos
	String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
	if (disclosureFilters.length() > 0)
		where += " AND " + disclosureFilters;

	PmConn pmOrderItem = new PmConn(sFParams);
	pmOrderItem.open();

	PmConn PmConnOrdeGroup = new PmConn(sFParams);
	PmConnOrdeGroup.open();

	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();

	PmConn pmProduct = new PmConn(sFParams);
	pmProduct.open();

	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
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

	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
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

	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if (sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) {
%>
<head>
<title>:::<%= title%>:::
</title>
<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>

<body class="default" <%=permissionPrint%> style="padding-right: 10px">
	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left" colspan="2"><%=title%></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%=filters%> <br>
			</td>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>
	<br>
	<table class="report">
	<%
	if (!(currencyId > 0)) {
		int currencyIdWhile = 0, x = 1, y = 0;
		pmCurrencyWhile.doFetch(sqlCurrency);
		while (pmCurrencyWhile.next()) {
			if (pmCurrencyWhile.getInt("currencies", "cure_currencyid") != currencyIdWhile) {
				currencyIdWhile = pmCurrencyWhile.getInt("currencies", "cure_currencyid");
				y = 0;
				%>
				<tr>
					<td class="reportHeaderCellCenter" colspan="22">
						<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
					</td>
				</tr>
				<%
			}
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orders ")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) "
					+ " WHERE orde_orderid > 0 " + " AND orde_currencyid =  "
					+ pmCurrencyWhile.getInt("cure_currencyid") + where;
			if (productId > 0 || showProductExtra != 2 || !productFamilyId.equals("")
					|| !productGroupId.equals("")) {
				sql += " AND orde_orderid IN ( " + " SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ")
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_orderid = orde_orderid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" ON (ordi_ordergroupid = ordg_ordergroupid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) "
						+ " WHERE orde_orderid = orde_orderid " + whereProduct + whereExtra
						+ whereProductFamily + whereProductGroup + " ) ";
			}
			//" ORDER by orde_lockstart ASC, orde_orderid ASC";
			sql += " ORDER by orde_code ASC";
			%>
			<tr class="">
				<td class="reportHeaderCellCenter">#</td>
				<td class="reportHeaderCell">Clave</td>
				<td class="reportHeaderCell">Pedido</td>
				<td class="reportHeaderCell">Cliente</td>
				<td class="reportHeaderCell">Vendedor</td>
				<td class="reportHeaderCell">Giro</td>
				<td class="reportHeaderCell">Estatus</td>
				<td class="reportHeaderCell">F. Apartado</td>
				<td class="reportHeaderCell">F. Apartado Fin</td>
				<td class="reportHeaderCell">Apartado</td>
				<td class="reportHeaderCell">Entrega</td>
				<td class="reportHeaderCell">Pago</td>
				<td class="reportHeaderCell">Moneda</td>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
			    		<td class="reportHeaderCell">Cob. T.C.</td>
			    <% 	}%>
				<td class="reportHeaderCell">Tipo de Cambio</td>
				<td class="reportHeaderCellRight">Subtotal</td>
				<td class="reportHeaderCellRight">Descuentos</td>
				<td class="reportHeaderCellRight">IVA</td>
				<td class="reportHeaderCellRight">Total</td>
				<td class="reportHeaderCellRight">O.C.</td>
				<td class="reportHeaderCellRight">N.D.C</td>
				<td class="reportHeaderCellRight">Costo</td>
			</tr>
			<%
			double sumOrderTotal = 0, subTotalSum = 0, descuentoSum = 0, ivaSum = 0,
			sumOrderTotalSinIva = 0, sumTotalOC = 0, sumTotalNDC = 0, sumCostOrder = 0;

			int i = 0;

			pmOrderItem.doFetch(sql);
			while (pmOrderItem.next()) {
				subTotalSum += pmOrderItem.getDouble("orde_amount");
				descuentoSum += pmOrderItem.getDouble("orde_discount");
				ivaSum += pmOrderItem.getDouble("orde_tax");
				sumOrderTotal += pmOrderItem.getDouble("orde_total");
				sumOrderTotalSinIva += pmOrderItem.getDouble("orde_total") - pmOrderItem.getDouble("orde_tax");
				
				if (pmOrderItem.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
	  		  		fullName = pmOrderItem.getString("cust_code") + " " + pmOrderItem.getString("cust_legalname");	
	  		  	else
	  		  		fullName = pmOrderItem.getString("cust_code") + " " + pmOrderItem.getString("cust_displayname");
				
				//Estatus
				bmoOrder.getStatus().setValue(pmOrderItem.getString("orders", "orde_status"));
				bmoOrder.getDeliveryStatus().setValue(pmOrderItem.getString("orders", "orde_deliverystatus"));
				bmoOrder.getLockStatus().setValue(pmOrderItem.getString("orders", "orde_lockstatus"));
				bmoOrder.getPaymentStatus().setValue(pmOrderItem.getString("orders", "orde_paymentstatus"));
				%>
				<tr class="reportCellEven">
					<%=HtmlUtil.formatReportCell(sFParams, "" + (i + 1), BmFieldType.NUMBER)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_code"), BmFieldType.CODE)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrderItem.getString("orders", "orde_name")), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(fullName), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("users", "user_code"), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrderItem.getString("industries", "indu_name")), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_lockstart"), BmFieldType.DATETIME)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_lockend"), BmFieldType.DATETIME)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getLockStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getDeliveryStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("currencies", "cure_code"), BmFieldType.CODE)%>
					<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
				    		<%= HtmlUtil.formatReportCell(sFParams, (pmOrderItem.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
				    <% 	}%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("currencies", "cure_parity"), BmFieldType.NUMBER)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_amount"), BmFieldType.CURRENCY)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_discount"), BmFieldType.CURRENCY)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_tax"), BmFieldType.CURRENCY)%>
					<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_total"), BmFieldType.CURRENCY)%>
					<%
					//Calcular las OC ligadas al pedido	
					double amount = 0;
					//sql = "";
					double totalOC = 0;

					//Ordenes de Compra con la misma Moneda del pedido
					sql = " SELECT SUM(reqi_total) AS totalOC FROM " + SQLUtil.formatKind(sFParams, " requisitions ") + " WHERE reqi_orderid = "
							+ pmOrderItem.getInt("orde_orderid") + " AND reqi_status <> '"
							+ BmoRequisition.STATUS_CANCELLED + "'" + " AND reqi_currencyid = "
							+ pmOrderItem.getInt("orde_currencyid");
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						totalOC = pmConn.getDouble("totalOC");
					}

					//Ordenes de Compra con diferente Moneda del pedido
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitions ") + " WHERE reqi_orderid = "
							+ pmOrderItem.getInt("orde_orderid") + " AND reqi_status <> '"
							+ BmoRequisition.STATUS_CANCELLED + "'" + " AND reqi_currencyid <> "
							+ pmOrderItem.getInt("orde_currencyid");
					pmConn.doFetch(sql);
					while (pmConn.next()) {
						//Origen
						int currencyIdOrigin = pmConn.getInt("reqi_currencyid");
						double parityOrigin = pmConn.getDouble("reqi_currencyparity");
						//Destino
						int currencyIdDestiny = pmOrderItem.getInt("orde_currencyid");
						double parityDestiny = pmOrderItem.getDouble("orde_currencyparity");

						totalOC += pmCurrency.currencyExchange(pmConn.getDouble("reqi_total"), currencyIdOrigin,
								parityOrigin, currencyIdDestiny, parityDestiny);
					}

					sumTotalOC += totalOC;
					%>
					<%=HtmlUtil.formatReportCell(sFParams, "" + totalOC, BmFieldType.CURRENCY)%>

					<%
					//Calcular las NDC ligadas al pedido
					double totalNDC = 0;

					//Sumar las notas de credito del pedido con la misma moneda del pedido
					sql = " SELECT SUM(rass_amount) AS totalNDC FROM " + SQLUtil.formatKind(sFParams, " raccountassignments ")
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" ON (rass_raccountid = racc_raccountid) "
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ " WHERE racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'"
							+ " AND ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'"
							+ " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'"
							+ " AND rass_foreignraccountid IN ( " + " SELECT racc_raccountid FROM " + SQLUtil.formatKind(sFParams, " raccounts ")
							+ "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ "	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
							+ "	AND racc_orderid = " + pmOrderItem.getInt("orde_orderid")
							+ " AND racc_currencyid = " + pmOrderItem.getInt("orde_currencyparity") + "  ) ";
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						totalNDC += pmConn.getDouble("totalNDC");
					}

					//Sumar las notas de credito del pedido con diferente moneda del pedido
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccountassignments ")
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" ON (rass_raccountid = racc_raccountid) "
							+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ " WHERE racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' "
							+ " AND ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'"
							+ " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'"
							+ " AND rass_foreignraccountid IN ( " + " SELECT racc_raccountid FROM " + SQLUtil.formatKind(sFParams, " raccounts ")
							+ "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
							+ "	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'"
							+ "	AND racc_orderid = " + pmOrderItem.getInt("orde_orderid")
							+ " AND racc_currencyid <> " + pmOrderItem.getInt("orde_currencyparity") + "  ) ";
					pmConn.doFetch(sql);
					while (pmConn.next()) {
						//Origen
						int currencyIdOrigin = pmConn.getInt("racc_currencyid");
						double parityOrigin = pmConn.getDouble("racc_currencyparity");
						//Destino
						int currencyIdDestiny = pmOrderItem.getInt("orde_currencyid");
						double parityDestiny = pmOrderItem.getDouble("orde_currencyparity");

						totalNDC += pmCurrency.currencyExchange(pmConn.getDouble("rass_amount"),
								currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
					}

					sumTotalNDC += totalNDC;
					%>
					<%=HtmlUtil.formatReportCell(sFParams, "" + totalNDC, BmFieldType.CURRENCY)%>
					<%
					double costOrder = 0;

					//Calcular el costo del pedido
					costOrder = pmOrderItem.getDouble("orde_total") - (totalOC + totalNDC);
					sumCostOrder += costOrder;
					%>
					<%=HtmlUtil.formatReportCell(sFParams, "" + costOrder, BmFieldType.CURRENCY)%>
				</tr>
				<%
				i++;

			} //pmOrderItem
			%>
			<tr class="reportCellEven">
				<td colspan="22">&nbsp;</td>
			</tr>
			<tr class="reportCellEven reportCellCode">
				<td colspan="15">&nbsp;</td>
				<%=HtmlUtil.formatReportCell(sFParams, "" + subTotalSum, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + descuentoSum, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + ivaSum, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + sumOrderTotal, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + sumTotalOC, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + sumTotalNDC, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + sumCostOrder, BmFieldType.CURRENCY)%>
			</tr>
	        <tr><td colspan="22">&nbsp;</td></tr>
			<%
		}
		//Cuando existe una moneda seleccionada	
	} else { 		
		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orders ")
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordertypes")+" ON(ortp_ordertypeid = orde_ordertypeid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = orde_customerid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON(indu_industryid = cust_industryid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON(user_userid = orde_userid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " areas")+" ON(area_areaid = user_areaid) "
				+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) "
				+ " WHERE orde_orderid > 0 " + where;
		if (productId > 0 || showProductExtra != 2 
				|| !productFamilyId.equals("") || !productGroupId.equals("")) {
			sql += " AND orde_orderid IN ( " + " SELECT orde_orderid  FROM " + SQLUtil.formatKind(sFParams, " orders ")
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordg_orderid = orde_orderid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orderitems")+" ON (ordi_ordergroupid = ordg_ordergroupid) "
					+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (prod_productid = ordi_productid) "
					+ " WHERE orde_orderid = orde_orderid " + whereProduct + whereExtra
					+ whereProductFamily + whereProductGroup + " ) ";
		}
		//" ORDER by orde_lockstart ASC, orde_orderid ASC";
		sql += " ORDER by orde_code ASC";
		%>
		<tr class="">
			<td class="reportHeaderCellCenter">#</td>
			<td class="reportHeaderCell">Clave</td>
			<td class="reportHeaderCell">Pedido</td>
			<td class="reportHeaderCell">Cliente</td>
			<td class="reportHeaderCell">Vendedor</td>
			<td class="reportHeaderCell">Giro</td>
			<td class="reportHeaderCell">Estatus</td>
			<td class="reportHeaderCell">F. Apartado</td>
			<td class="reportHeaderCell">F. Apartado Fin</td>
			<td class="reportHeaderCell">Apartado</td>
			<td class="reportHeaderCell">Entrega</td>
			<td class="reportHeaderCell">Pago</td>
			<td class="reportHeaderCell">Moneda</td>
			<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
		    		<td class="reportHeaderCell">Cob. T.C.</td>
		    <% 	}%>
			<td class="reportHeaderCell">Tipo de Cambio</td>
			<td class="reportHeaderCellRight">Subtotal</td>
			<td class="reportHeaderCellRight">Descuentos</td>
			<td class="reportHeaderCellRight">IVA</td>
			<td class="reportHeaderCellRight">Total</td>
			<td class="reportHeaderCellRight">O.C.</td>
			<td class="reportHeaderCellRight">N.D.C</td>
			<td class="reportHeaderCellRight">Costo</td>
		</tr>
		<%
		double sumOrderTotal = 0, subTotalSum = 0, descuentoSum = 0, ivaSum = 0, sumOrderTotalSinIva = 0,
		sumTotalOC = 0, sumTotalNDC = 0, sumCostOrder = 0;

		int i = 0;
		//System.out.println("sql " + sql);
		pmOrderItem.doFetch(sql);
		while (pmOrderItem.next()) {
			
			//Conversion a la moneda destino(seleccion del filtro)
	    	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	    	double parityOrigin = 0, parityDestiny = 0;
	    	currencyIdOrigin = pmOrderItem.getInt("orde_currencyid");
	    	parityOrigin = pmOrderItem.getDouble("orde_currencyparity");
	    	currencyIdDestiny = currencyId;
	    	parityDestiny = defaultParity;
			
			if (pmOrderItem.getString("cust_customertype").equals(""+BmoCustomer.TYPE_COMPANY))
  		  		fullName = pmOrderItem.getString("cust_code") + " " + pmOrderItem.getString("cust_legalname");	
  		  	else
  		  		fullName = pmOrderItem.getString("cust_code") + " " + pmOrderItem.getString("cust_displayname");
			
			//Estatus
			bmoOrder.getStatus().setValue(pmOrderItem.getString("orders", "orde_status"));
			bmoOrder.getDeliveryStatus().setValue(pmOrderItem.getString("orders", "orde_deliverystatus"));
			bmoOrder.getLockStatus().setValue(pmOrderItem.getString("orders", "orde_lockstatus"));
			bmoOrder.getPaymentStatus().setValue(pmOrderItem.getString("orders", "orde_paymentstatus"));
			%>
			<tr class="reportCellEven">
				<%=HtmlUtil.formatReportCell(sFParams, "" + (i + 1), BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_code"), BmFieldType.CODE)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrderItem.getString("orders", "orde_name")), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(fullName), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("users", "user_code"), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(pmOrderItem.getString("industries", "indu_name")), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_lockstart"), BmFieldType.DATETIME)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("orders", "orde_lockend"), BmFieldType.DATETIME)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getLockStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getDeliveryStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoOrder.getPaymentStatus().getSelectedOption().getLabel()), BmFieldType.STRING)%>
				<%=HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("currencies", "cure_code"), BmFieldType.CODE)%>
				<%	if (sFParams.isFieldEnabled(bmoOrder.getCoverageParity())) { %>
			    		<%= HtmlUtil.formatReportCell(sFParams, (pmOrderItem.getBoolean("orde_coverageparity") ? "Si" : "No"), BmFieldType.STRING) %>
			    <% 	}%>
				<%	// Poner la paridad actual, SOLO en caso de convertir de MXN a USD
	        	if (currencyIdOrigin != currencyIdDestiny) {
	        		if (bmoCurrency.getCode().toString().equals("USD")) {
		    	%>
							<%= HtmlUtil.formatReportCell(sFParams, "" + defaultParity, BmFieldType.NUMBER) %>
			    <%		} else { %>
							<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrderItem.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
			    <%		}
			    	} else { %>
						<%= HtmlUtil.formatReportCell(sFParams, "" + pmOrderItem.getDouble("orde_currencyparity"), BmFieldType.NUMBER) %>
			    <%	}%>
				<%
				double totalOrder = 0;
		
				//Convertir los pedidos
				double ordeAmount = pmCurrency.currencyExchange(pmOrderItem.getDouble("orde_amount"),
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				double ordeDiscount = pmCurrency.currencyExchange(pmOrderItem.getDouble("orde_discount"),
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				double ordeTax = pmCurrency.currencyExchange(pmOrderItem.getDouble("orde_tax"),
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
				double ordeTotal = pmCurrency.currencyExchange(pmOrderItem.getDouble("orde_total"),
						currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);

				subTotalSum += ordeAmount;
				descuentoSum += ordeDiscount;
				ivaSum += ordeTax;
				sumOrderTotal += ordeTotal;
				totalOrder = ordeTotal;
				%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + ordeAmount, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + ordeDiscount, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + ordeTax, BmFieldType.CURRENCY)%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + ordeTotal, BmFieldType.CURRENCY)%>
				<%

				//Calcular las OC ligadas al pedido	
				double amount = 0;
				//sql = "";
				double totalOC = 0;
	
				//Ordenes de Compra con la misma Moneda del pedido
				sql = " SELECT SUM(reqi_total) AS totalOC FROM " + SQLUtil.formatKind(sFParams, " requisitions ") + " WHERE reqi_orderid = "
						+ pmOrderItem.getInt("orde_orderid") + " AND reqi_status <> '"
						+ BmoRequisition.STATUS_CANCELLED + "'" + " AND reqi_currencyid = " + currencyId;
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					totalOC = pmConn.getDouble("totalOC");
				}
	
				//Ordenes de Compra con diferente Moneda del pedido
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitions ") + " WHERE reqi_orderid = "
						+ pmOrderItem.getInt("orde_orderid") + " AND reqi_status <> '"
						+ BmoRequisition.STATUS_CANCELLED + "'" + " AND reqi_currencyid <> " + currencyId;
				pmConn.doFetch(sql);
				while (pmConn.next()) {
					//Origen
					currencyIdOrigin = pmConn.getInt("reqi_currencyid");
					parityOrigin = pmConn.getDouble("reqi_currencyparity");
	
					totalOC += pmCurrency.currencyExchange(pmConn.getDouble("reqi_total"), currencyIdOrigin,
							parityOrigin, currencyIdDestiny, parityDestiny);
				}
	
				sumTotalOC += totalOC;
				%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + totalOC, BmFieldType.CURRENCY)%>
	
				<%
				//Calcular las NDC ligadas al pedido
				double totalNDC = 0;
	
				//Sumar las notas de credito del pedido con la misma moneda del pedido
				sql = " SELECT SUM(rass_amount) AS totalNDC FROM " + SQLUtil.formatKind(sFParams, " raccountassignments ")
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" ON (rass_raccountid = racc_raccountid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
						+ " WHERE racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' "
						+ " AND ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'"
						+ " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'"
						+ " AND rass_foreignraccountid IN ( " + " SELECT racc_raccountid FROM " + SQLUtil.formatKind(sFParams, " raccounts ")
						+ "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
						+ "	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + "	AND racc_orderid = "
						+ pmOrderItem.getInt("orde_orderid") + " AND racc_currencyid = " + currencyId + "  ) ";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					totalNDC += pmConn.getDouble("totalNDC");
				}
	
				//Sumar las notas de credito del pedido con diferente moneda del pedido
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " raccountassignments ")
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounts")+" ON (rass_raccountid = racc_raccountid) "
						+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
						+ " WHERE racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "' "
						+ " AND ract_category  = '" + BmoRaccountType.CATEGORY_CREDITNOTE + "'"
						+ " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'"
						+ " AND rass_foreignraccountid IN ( " + " SELECT racc_raccountid FROM " + SQLUtil.formatKind(sFParams, " raccounts ")
						+ "	LEFT JOIN " + SQLUtil.formatKind(sFParams, " raccounttypes")+" ON (ract_raccounttypeid = racc_raccounttypeid) "
						+ "	WHERE ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" + "	AND racc_orderid = "
						+ pmOrderItem.getInt("orde_orderid") + " AND racc_currencyid <> " + currencyId + "  ) ";
				pmConn.doFetch(sql);
				while (pmConn.next()) {
					//Origen
					currencyIdOrigin = pmConn.getInt("racc_currencyid");
					parityOrigin = pmConn.getDouble("racc_currencyparity");
	
					totalNDC += pmCurrency.currencyExchange(pmConn.getDouble("rass_amount"), currencyIdOrigin,
							parityOrigin, currencyIdDestiny, parityDestiny);
				}
	
				sumTotalNDC += totalNDC;
				%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + totalNDC, BmFieldType.CURRENCY)%>
				<%
				double costOrder = 0;
	
				//Calcular el costo del pedido
				costOrder = totalOrder - (totalOC + totalNDC);
				sumCostOrder += costOrder;
				%>
				<%=HtmlUtil.formatReportCell(sFParams, "" + costOrder, BmFieldType.CURRENCY)%>
			</tr>
			<%
			i++;

		} //pmOrderItem
		%>
		<tr class="reportCellEven">
			<td colspan="22">&nbsp;</td>
		</tr>
		<tr class="reportCellEven reportCellCode">
			<td colspan="15">&nbsp;</td>
			<%=HtmlUtil.formatReportCell(sFParams, "" + subTotalSum, BmFieldType.CURRENCY)%>
			<%=HtmlUtil.formatReportCell(sFParams, "" + descuentoSum, BmFieldType.CURRENCY)%>
			<%=HtmlUtil.formatReportCell(sFParams, "" + ivaSum, BmFieldType.CURRENCY)%>
			<%=HtmlUtil.formatReportCell(sFParams, "" + sumOrderTotal, BmFieldType.CURRENCY)%>
			<%=HtmlUtil.formatReportCell(sFParams, "" + sumTotalOC, BmFieldType.CURRENCY)%>
			<%=HtmlUtil.formatReportCell(sFParams, "" + sumTotalNDC, BmFieldType.CURRENCY)%>
			<%=HtmlUtil.formatReportCell(sFParams, "" + sumCostOrder, BmFieldType.CURRENCY)%>
			</tr>
			<%
	}
	%>
	</table>
	<%
} // Fin de if(no carga datos)
	pmCurrencyWhile.close();
	pmConn.close();
	PmConnOrdeGroup.close();
	pmOrderItem.close();
	pmProduct.close();

	if (print.equals("1")) {
%>
		<script>
			//window.print();
		</script>
<%
	}
%>
</body>
</html>
