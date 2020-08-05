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
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.fi.BmoPaccountType"%>
<%@page import="com.flexwm.shared.fi.BmoPaccount"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reportes Comisiones en Pedidos";
	BmoOrder bmoOrder = new BmoOrder();
    String sql = "", where = "", whereUserId = "", sqlCurrency = "";
    
    String ordeStatus = "", paymentStatus = "", deliveryStatus = "", lockStatus  = "", lockStartDate = "", lockEndDate = "";
    String filters = "", fullName = "", whereExtra="", whereProduct="", whereProductFamily = "", whereProductGroup = "", productFamilyId = "", productGroupId = "";
    int programId = 0, customerId = 0, cols= 0, areaId = 0, orderId = 0, industryId = 0, userId = 0, productId = 0, showProductExtra = 0, currencyId = 0;
    
    BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());
	
	PmCurrency pmCurrencyExchange =new PmCurrency(sFParams);
	
    // Obtener parametros       
	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
    if (request.getParameter("orde_orderid") != null) orderId = Integer.parseInt(request.getParameter("orde_orderid"));    
    if (request.getParameter("orde_customerid") != null) customerId = Integer.parseInt(request.getParameter("orde_customerid"));    
    if (request.getParameter("orde_status") != null) ordeStatus = request.getParameter("orde_status");
    if (request.getParameter("orde_lockstatus") != null) lockStatus = request.getParameter("orde_lockstatus");
    if (request.getParameter("orde_deliverystatus") != null) deliveryStatus = request.getParameter("orde_deliverystatus");
    if (request.getParameter("orde_paymentstatus") != null) paymentStatus = request.getParameter("orde_paymentstatus");
    if (request.getParameter("orde_lockstart") != null) lockStartDate = request.getParameter("orde_lockstart");
    if (request.getParameter("orde_lockend") != null) lockEndDate = request.getParameter("orde_lockend");
    if (request.getParameter("area_areaid") != null) areaId = Integer.parseInt(request.getParameter("area_areaid"));  
    if (request.getParameter("cust_industryid") != null) industryId = Integer.parseInt(request.getParameter("cust_industryid"));  
    if (request.getParameter("userid") != null) userId = Integer.parseInt(request.getParameter("userid"));   
   	if (request.getParameter("prod_productid") != null) productId = Integer.parseInt(request.getParameter("prod_productid"));
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
   	if (request.getParameter("showProductExtra") != null) showProductExtra = Integer.parseInt(request.getParameter("showProductExtra"));
   	if (request.getParameter("orde_currencyid") != null) currencyId = Integer.parseInt(request.getParameter("orde_currencyid"));
   
   	
    // Filtros listados
    if (orderId > 0) {
    	where += " AND orde_orderid = " + orderId;
    	filters += "<i>Pedido: </i>" + request.getParameter("orde_orderidLabel") + ", ";
    }
    
    if (userId > 0) {
		whereUserId += " AND wflu_userid = " + userId;
    	filters += "<i>Usuario: </i>" + request.getParameter("useridLabel") + ", ";
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
    
    if (!productFamilyId.equals("")) {
   		whereProductFamily += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
   		filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
   	}
   	
   	if (!productGroupId.equals("")) {
   		whereProductGroup += SFServerUtil.parseFiltersToSql("comi_productgroupid", productGroupId);
   		filters += "<i>Grupo Prod.: </i>" + request.getParameter("comi_productgroupidLabel") + ", ";
   	}

	//Obtener la paridad de la moneda
  	double nowParity = 0;
  	double defaultParity = 0;
  	PmCurrency pmCurrency = new PmCurrency(sFParams);
  	BmoCurrency bmoCurrency = new BmoCurrency();

  	if (currencyId > 0) {
  		bmoCurrency = (BmoCurrency) pmCurrency.get(currencyId);
  		defaultParity = bmoCurrency.getParity().toDouble();

  		filters += "<i>Moneda: </i>" + request.getParameter("orde_currencyidLabel")
  				+ " : <i>Tipo de Cambio Actual : </i>" + defaultParity + ", ";
  	} else {
  		filters += "<i>Moneda: </i> Todas ";
  		sqlCurrency = "SELECT cure_currencyid, cure_name FROM " + SQLUtil.formatKind(sFParams, " currencies ");

  	}
    
    if (sFParams.getSelectedCompanyId() > 0)
    	filters += "<i>Empresa: </i>" + 
	   	"" + sFParams.getBmoSelectedCompany().getName().toString() + ", ";
    
    // Obtener disclosure de datos
    String disclosureFilters = new PmOrder(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    
    
    PmConn pmOrderItem = new PmConn(sFParams);
    pmOrderItem.open();
    
       
    PmConn pmConn = new PmConn(sFParams);
    pmConn.open();
    
    PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
	
	PmConn pmConn3 = new PmConn(sFParams);
   	pmConn3.open();
   	
   	PmConn pmConn4 = new PmConn(sFParams);
   	pmConn4.open();
	
	PmConn pmCurrencyWhile = new PmConn(sFParams);
	pmCurrencyWhile.open();
   
    BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)pmProgram.get(programId);
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
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))){ %>
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
			
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if(sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))){ %>
<head>
    <title>:::<%= title %>:::</title>
    <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

<table border="0" cellspacing="0" cellpading="0" width="100%">
    <tr>
        <td align="left" width="80" rowspan="2" valign="top">   
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
        </td>
        <td class="reportTitle" align="left" colspan="2">
            <%= title %>
        </td>
    </tr>
    <tr>
        <td class="reportSubTitle">
            <b>Filtros:</b> <%= filters %>
            <br>            
        </td>
    <td class="reportDate" align="right">
            Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
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
			if (pmCurrencyWhile.getInt("cure_currencyid") != currencyIdWhile) {
				currencyIdWhile = pmCurrencyWhile.getInt("cure_currencyid");
				y = 0;
		%>
		<tr>
			<td class="reportHeaderCellCenter" colspan="19">
				<%=HtmlUtil.stringToHtml(pmCurrencyWhile.getString("cure_name"))%>
			</td>
		</tr>
		<%
			}
		
					double sumCommission = 0;
					double sumTotalItem = 0;
					double sumTotalCommission = 0;
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orders ") +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (orde_customerid = cust_customerid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +
						  " WHERE orde_orderid > 0 " + where +
						  " AND orde_currencyid = " + pmCurrencyWhile.getInt("cure_currencyid") +
						  " AND orde_wflowid IN (" +
						  " SELECT wflu_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
						  " WHERE wflu_commission = 1 " +	
							whereUserId +
						  " )" +					    
						  " AND orde_orderid IN (" +
						  " SELECT ordg_orderid FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +						  
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
						  " WHERE ordi_commission = 1 " +		   
						  " ) " +
					      " ORDER BY orde_orderid ";
					pmConn.doFetch(sql);
					while(pmConn.next()) {
						double ordeTotal = pmConn.getDouble("orde_total");
						sumCommission = 0;
%>						
						<TR>
							<td class="reportGroupCell" colspan="7">
								<%= HtmlUtil.stringToHtml(pmConn.getString("orders","orde_code"))%> |							
								<%= HtmlUtil.stringToHtml(pmConn.getString("orders", "orde_name"))%> |
								<%= HtmlUtil.stringToHtml(pmConn.getString("orders", "orde_lockstart"))%> |
								<%= HtmlUtil.stringToHtml(pmConn.getString("customers", "cust_code"))%>
								<%= HtmlUtil.stringToHtml(pmConn.getString("customers", "cust_displayname"))%> |
								<%= HtmlUtil.stringToHtml(pmConn.getString("currencies", "cure_code"))%> |
								<%= HtmlUtil.stringToHtml("" + pmConn.getDouble("orde_currencyparity"))%> |						
								<%= SFServerUtil.formatCurrency(ordeTotal) %>
							</td>
						</TR>
						<tr>
							<td class="reportHeaderCellCenter">#</td>
							<td class="reportHeaderCell">Usuario</td>
						    <td class="reportHeaderCell">Grupo Producto</td>
						    <td class="reportHeaderCell">Grupo Usuario</td>			    
						    <td class="reportHeaderCellRight">C&aacute;lculo</td>
						    <td class="reportHeaderCellCenter">%</td>
						    <td class="reportHeaderCellRight">Monto</td>
						</tr>
<%						
						int i = 1;
						//Listar los usuarios de flujo con comision
						sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
						      	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (wflu_userid = user_userid) " +
					          	" WHERE wflu_wflowid = " + pmConn.getInt("orde_wflowid") + 
					          	" AND wflu_commission = 1 " +		
					        	whereUserId +
					          	" ORDER BY wflu_wflowuserid ";
						pmConn2.doFetch(sql);
						while(pmConn2.next()) {
							//Obtener la clase del producto 
							sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " commissions ") +
								  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (comi_profileid = prof_profileid) " +
							      " WHERE comi_profileid = " + pmConn2.getInt("wflu_profileid") + whereProductGroup;
							pmConn3.doFetch(sql);
							while (pmConn3.next()) {					
								
								sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
									  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
							          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
							          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
							          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
							          " WHERE orde_orderid = " + pmConn.getInt("orde_orderid") +					      
							          " AND prod_productgroupid = " + pmConn3.getInt("comi_productgroupid") +
							          " AND ordi_commission = 1 " + 
							          " GROUP BY prgp_productgroupid ";					
								pmOrderItem.doFetch(sql);
								while (pmOrderItem.next()) { %>
									<tr class="reportCellEven">
										<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>	 
										<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getString("users", "user_code"), BmFieldType.STRING) %>
										<%= HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("productgroups", "prgp_name"), BmFieldType.STRING) %>								
										<%= HtmlUtil.formatReportCell(sFParams, pmConn3.getString("profiles", "prof_name"), BmFieldType.STRING) %>										
										<%
											//Calcular el monto que les corresponde de comisión
											
											//Obtener la suma del total del item
											double sumPrice = 0;
											sql = " SELECT SUM(ordi_amount) AS sumPrice FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +								
												  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
											      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
										          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
										          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
										          " WHERE orde_orderid = " + pmConn.getInt("orde_orderid") +					      
										          " AND prod_productgroupid = " + pmConn3.getInt("comi_productgroupid") +
										          " AND ordi_commission = 1 " + where +
										          " GROUP BY prgp_productgroupid ";
											pmConn4.doFetch(sql);
											if (pmConn4.next()) {
												sumPrice = pmConn4.getDouble("sumPrice");	
											}
										    
											//Obtener el porcentaje de comisión
											double percentage = pmConn3.getDouble("comi_percentage");
											
											//Calcular la comisión
											double totalCommission = 0;
											
											if(percentage > 0 && sumPrice > 0) {
												totalCommission = sumPrice * (percentage / 100);
											}
											
											sumCommission += totalCommission;
											sumTotalCommission += totalCommission;
											sumTotalItem += sumPrice;
										%>
										<%= HtmlUtil.formatReportCell(sFParams, "" + sumPrice, BmFieldType.CURRENCY) %>
										<%= HtmlUtil.formatReportCell(sFParams, pmConn2.getDouble("wflu_percentagecommission") + "%", BmFieldType.NUMBER) %>
										<%= HtmlUtil.formatReportCell(sFParams, "" + totalCommission, BmFieldType.CURRENCY) %>
									</tr>
											
					<%			i++;			
							}
						}	
					}
			%>			
					<tr class="reportCellEven reportCellCode">
						<td colspan="6">&nbsp;</td>					
						<%= HtmlUtil.formatReportCell(sFParams, "" + sumCommission, BmFieldType.CURRENCY) %>
					</tr>
			<%		
				}
			%>				
				<tr>
					<td colspan="7">&nbsp;</td>
				</tr>
				<tr class="reportCellEven reportCellCode">
					<td colspan="6">&nbsp;</td>			
					<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalCommission, BmFieldType.CURRENCY) %>
				</tr>
				<tr>
					<td colspan="7">&nbsp;</td>
				</tr>	
		<%	
			} 
		%>
		
<%		
	} else {
		
			double sumTotalItem = 0;
			double sumCommission = 0;
			double sumTotalCommission = 0;
			sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orders ") +
				  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (orde_customerid = cust_customerid) " +
				  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (orde_currencyid = cure_currencyid) " +	
				  	" WHERE orde_orderid > 0 " + where +
				  	" AND orde_wflowid IN (" +
				  	" SELECT wflu_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
				  	" WHERE wflu_commission = 1 " +	
					whereUserId +
				  	" ) " +
				  	" AND orde_orderid IN (" +
				  	" SELECT ordg_orderid FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +						  
				  	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +		  
				  	" WHERE ordi_commission = 1 " +		   
				  	" ) " +
			      	" ORDER BY orde_orderid ";			      
			pmConn.doFetch(sql);
			while(pmConn.next()) {
				//Conversion a la moneda destino(seleccion del filtro)
	          	int currencyIdOrigin = 0, currencyIdDestiny = 0;
	          	double parityOrigin = 0, parityDestiny = 0;
	          	currencyIdOrigin = pmConn.getInt("orde_currencyid");
	          	parityOrigin = pmConn.getDouble("orde_currencyparity");
	          	currencyIdDestiny = currencyId;
	          	parityDestiny = defaultParity;
	          	double ordeTotal = pmConn.getDouble("orde_total"); 
	          	ordeTotal = pmCurrencyExchange.currencyExchange(ordeTotal, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
	          	sumCommission = 0;
%>				
				<TR>
					<td class="reportGroupCell" colspan="7">
						<%= HtmlUtil.stringToHtml(pmConn.getString("orders","orde_code"))%> |							
						<%= HtmlUtil.stringToHtml(pmConn.getString("orders", "orde_name"))%> |
						<%= HtmlUtil.stringToHtml(pmConn.getString("orders", "orde_lockstart"))%> |
						<%= HtmlUtil.stringToHtml(pmConn.getString("customers", "cust_code"))%>
						<%= HtmlUtil.stringToHtml(pmConn.getString("customers", "cust_displayname"))%> |
						<%= HtmlUtil.stringToHtml(pmConn.getString("currencies", "cure_code"))%> |
						<%= HtmlUtil.stringToHtml("" + pmConn.getDouble("orde_currencyparity"))%> |						
						<%= SFServerUtil.formatCurrency(ordeTotal) %>
					</td>
				</TR>
				<tr>
					<td class="reportHeaderCellCenter">#</td>
					<td class="reportHeaderCell">Usuario</td>
				    <td class="reportHeaderCell">Grupo Producto</td>
				    <td class="reportHeaderCell">Grupo Usuario</td>			    
				    <td class="reportHeaderCellRight">C&aacute;lculo</td>
				    <td class="reportHeaderCellCenter">%</td>
				    <td class="reportHeaderCellRight">Monto</td>
				</tr>
<%				
				int i = 1;				
				//Listar los usuarios de flujo con comision
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " wflowusers ") +
				      	" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (wflu_userid = user_userid) " +
			          	" WHERE wflu_wflowid = " + pmConn.getInt("orde_wflowid") + 
			          	" AND wflu_commission = 1 " + 
			        	whereUserId +
			          	" ORDER BY wflu_wflowuserid";
				pmConn2.doFetch(sql);
				while(pmConn2.next()) {		
					//Obtener la clase del producto 
					sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " commissions ") +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " profiles")+" ON (comi_profileid = prof_profileid) " +
					      " WHERE comi_profileid = " + pmConn2.getInt("wflu_profileid") + 
					      " ORDER BY comi_profileid, comi_productgroupid ";
					pmConn3.doFetch(sql);
					while (pmConn3.next()) {					
						
						sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " orderitems ") +
							  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
					          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
					          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
					          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +					         
					          " WHERE orde_orderid = " + pmConn.getInt("orde_orderid") +					      
					          " AND prod_productgroupid = " + pmConn3.getInt("comi_productgroupid") +
					          " AND ordi_commission = 1 " + 
					          " GROUP BY prgp_productgroupid ";					
						pmOrderItem.doFetch(sql);
						while (pmOrderItem.next()) { 
%>
							<tr class="reportCellEven">
								<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
								<%
									fullName = pmConn2.getString("user_firstname") + " " + pmConn2.getString("user_fatherlastname");
								%>	 
								<%= HtmlUtil.formatReportCell(sFParams, fullName, BmFieldType.STRING) %>
								<%= HtmlUtil.formatReportCell(sFParams, pmOrderItem.getString("productgroups", "prgp_name"), BmFieldType.STRING) %>								
								<%= HtmlUtil.formatReportCell(sFParams, pmConn3.getString("profiles", "prof_name"), BmFieldType.STRING) %>
								<%
									//Calcular el monto que les corresponde de comisión
									
									//Obtener la suma del total del item
									double sumPrice = 0;
									sql = " SELECT SUM(ordi_amount) AS sumPrice FROM " + SQLUtil.formatKind(sFParams, " orderitems " )+								
										  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " ordergroups")+" ON (ordi_ordergroupid = ordg_ordergroupid) " +
									      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (ordg_orderid = orde_orderid) " +
								          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " products")+" ON (ordi_productid = prod_productid) " +
								          " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
								          " WHERE orde_orderid = " + pmConn.getInt("orde_orderid") +					      
								          " AND prod_productgroupid = " + pmConn3.getInt("comi_productgroupid") +
								          " AND ordi_commission = 1 " + where +
								          " GROUP BY prgp_productgroupid ";
									pmConn4.doFetch(sql);
									if (pmConn4.next()) {
										sumPrice = pmConn4.getDouble("sumPrice");	
									}
								    
									//Obtener el porcentaje de comisión
									double percentage = pmConn3.getDouble("comi_percentage");
									
									//Calcular la comisión
									double totalCommission = 0;
									
									if(percentage > 0 && sumPrice > 0) {
										totalCommission = sumPrice * (percentage / 100);
									}	
									
									//Conversion a la moneda destino(seleccion del filtro)
						          	currencyIdOrigin = 0;
						          	currencyIdDestiny = 0;
						          	parityOrigin = 0;
						          	parityDestiny = 0;
						          	currencyIdOrigin = pmOrderItem.getInt("orde_currencyid");
						          	parityOrigin = pmOrderItem.getDouble("orde_currencyparity");
						          	currencyIdDestiny = currencyId;
						          	parityDestiny = defaultParity;
						          	
						          	sumPrice = pmCurrencyExchange.currencyExchange(sumPrice, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						          	totalCommission = pmCurrencyExchange.currencyExchange(totalCommission, currencyIdOrigin, parityOrigin, currencyIdDestiny, parityDestiny);
						          	
						          	sumCommission += totalCommission;
						          	sumTotalCommission += totalCommission;
								%>
								<%= HtmlUtil.formatReportCell(sFParams, "" + sumPrice, BmFieldType.CURRENCY) %>
								<%= HtmlUtil.formatReportCell(sFParams, percentage + "%", BmFieldType.NUMBER) %>
								<%= HtmlUtil.formatReportCell(sFParams, "" + totalCommission, BmFieldType.CURRENCY) %>
							</tr>							
<%					i++;			
					}	
				}					
			}
%>
		<tr class="reportCellEven reportCellCode">
			<td colspan="6">&nbsp;</td>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumCommission, BmFieldType.CURRENCY) %>
		</tr>				
<%				
		}	
%>		
		<tr>
			<td colspan="7">&nbsp;</td>
		</tr>	
		<tr class="reportCellEven reportCellCode">
			<td colspan="6">&nbsp;</td>			
			<%= HtmlUtil.formatReportCell(sFParams, "" + sumTotalCommission, BmFieldType.CURRENCY) %>
		</tr>	
		<tr>
			<td colspan="7">&nbsp;</td>
		</tr>	
		<%
	}
%>
		
</table>		
<%		
	} //Fin carga de datos
	pmCurrencyWhile.close();
	pmConn4.close();
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
   	pmOrderItem.close();
   	
%>  
<% if (print.equals("1")) { %>
    <script>
        //window.print();
    </script>
    <% } %>
  </body>
</html>
