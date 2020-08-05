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

<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.flexwm.server.op.PmProduct"%>
<%@page import="com.flexwm.shared.op.BmoProductPrice"%>
<%@page import="com.flexwm.server.op.PmProductPrice"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
    // Inicializar variables
    String title = "Reporte de Precios Por Producto";
	BmoProductPrice bmoProductPrice = new BmoProductPrice();
    String sql = "", whereApplies = "", where = "",whereCompany = "",whereCompany2 = "",whereOrderTypeid = "",whereDateStart = "",whereDateEnd = "";
    
    String prodStatus = "", prodType = "", prodTrack = "",prod_name ="", productFamilyId = "", productGroupId = "";
    String filters = "", dateStart = "",dateEnd = "",fullName = "", dateMaintenanceStart = "", dateMaintenanceEnd = "", 
    		dateNextMaintenanceStart = "", dateNextMaintenanceEnd = "";

    int supplierId = 0, unitId = 0, cols= 0 ,company= 0, prpc_companyid = 0,prpc_marketid = 0,prpc_ordertypeid = 0,prpc_wFlowTypeId = 0;
    
    int programId = 0;
    
    // Obtener parametros       
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));       
    if (request.getParameter("prod_supplierid") != null) supplierId = Integer.parseInt(request.getParameter("prod_supplierid"));    
    if (request.getParameter("prod_productfamilyid") != null) productFamilyId = request.getParameter("prod_productfamilyid");
    if (request.getParameter("prod_productgroupid") != null) productGroupId = request.getParameter("prod_productgroupid");
    if (request.getParameter("prod_unitid") != null) unitId = Integer.parseInt(request.getParameter("prod_unitid"));
    if (request.getParameter("prod_type") != null) prodType = request.getParameter("prod_type");
    if (request.getParameter("prod_track") != null) prodTrack = request.getParameter("prod_track"); 
     if (request.getParameter("prpc_companyid") != null) prpc_companyid = Integer.parseInt(request.getParameter("prpc_companyid")); 
    if (request.getParameter("prpc_marketid") != null) prpc_marketid = Integer.parseInt(request.getParameter("prpc_marketid"));	
    if (request.getParameter("prpc_ordertypeid") != null) prpc_ordertypeid = Integer.parseInt(request.getParameter("prpc_ordertypeid"));
    if (request.getParameter("prpc_wflowtypeid") != null) prpc_wFlowTypeId = Integer.parseInt(request.getParameter("prpc_wflowtypeid"));    
    if (request.getParameter("datestart") != null) dateStart = request.getParameter("datestart");
    if (request.getParameter("dateend") != null) dateEnd = request.getParameter("dateend");
    
    // Filtros listados
    if (supplierId > 0) {
    	where += " AND supl_supplierid = " + supplierId;
        filters += "<i>Proveedor: </i>" + request.getParameter("prod_supplieridLabel") + ", ";
    }
    
    
    if (!productFamilyId.equals("")) {
    	where += SFServerUtil.parseFiltersToSql("prod_productfamilyid", productFamilyId);
        filters += "<i>Familia: </i>" + request.getParameter("prod_productfamilyidLabel") + ", ";
    }
    
    if (!productGroupId.equals("")) {
    	where += SFServerUtil.parseFiltersToSql("prod_productgroupid", productGroupId);
        filters += "<i>Grupo: </i>" + request.getParameter("prod_productgroupidLabel") + ", ";
    }
    
    if (unitId > 0) {
        where += " AND prod_unitid = " + unitId;
        filters += "<i>Unidad: </i>" + request.getParameter("prod_unitidLabel") + ", ";
    }
    
    if (!prodType.equals("")) {
        where += " AND prod_type like '" + prodType + "'";
        filters += "<i>Tipo: </i>" + request.getParameter("prod_typeLabel") + ", ";
    }
    
    if (!prodTrack.equals("")) {
        where += " AND prod_track like '" + prodTrack + "'";
        filters += "<i>Rastreo: </i>" + request.getParameter("prod_trackLabel") + ", ";
    }
    
    if (!prodTrack.equals("")) {
        where += " AND prod_track like '" + prodTrack + "'";
        filters += "<i>Rastreo: </i>" + request.getParameter("prod_trackLabel") + ", ";
    }
    
   
    if (prpc_companyid > 0 ) {
//     	whereCompany += " AND prpc_companyid = " + prpc_companyid ; 
        whereCompany += " AND (prpc_companyid = "+ prpc_companyid +") ";
    	filters += "<i>Empresa: </i>" + request.getParameter("prpc_companyidLabel") + ", ";
    }
    
    if (prpc_marketid > 0) {
    	whereApplies += " AND prpc_marketid = " + prpc_marketid ;
        filters += "<i>Mercado: </i>" + request.getParameter("prpc_marketidLabel") + ", ";
    }
    
    if (prpc_ordertypeid > 0) {
    	whereApplies += " AND prpc_ordertypeid = " + prpc_ordertypeid ;
        filters += "<i>Tipo Pedido: </i>" + request.getParameter("prpc_ordertypeidLabel") + ", ";
    }
    
    if (prpc_wFlowTypeId > 0) {
    	whereApplies += " AND prpc_wflowtypeid = " + prpc_wFlowTypeId ;
        filters += "<i>Tipo Flujo: </i>" + request.getParameter("prpc_wflowtypeidLabel") + ", ";
    }
   
    if (!dateStart.equals("")) {
    	whereApplies += " AND prpc_startdate >= '" + dateStart + "'";
        filters += "<i>Fecha Inicio: </i>" + dateStart+ ", ";
    }
    if (!dateEnd.equals("")) {
    	whereApplies += " AND prpc_startdate <= '" + dateStart + "'";
        filters += "<i>Fecha Fin: </i>" + dateEnd+ ", ";
    }
  
    // Obtener disclosure de datos
    String disclosureFilters = new PmProduct(sFParams).getDisclosureFilters();
    if (disclosureFilters.length() > 0)
    	where += " AND " + disclosureFilters;
    
    sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " products ") + 
    		" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = prod_supplierid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productfamilies")+" ON (prod_productfamilyid = prfm_productfamilyid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " productgroups")+" ON (prod_productgroupid = prgp_productgroupid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " units")+" ON (prod_unitid = unit_unitid) " +
            " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = prod_currencyid) WHERE prod_productid > 0 " +           
             where + " GROUP BY  prod_productid ORDER BY prod_productid ASC";
    
      PmConn pmPrice= new PmConn(sFParams);
      pmPrice.open();
      
      PmConn pmProductPrice = new PmConn(sFParams);
      pmProductPrice.open();
      
      PmConn pmCountProduct = new PmConn(sFParams);
      pmCountProduct.open();

// 		System.out.println("SQL_Prodcutos:"+sql);
      pmProductPrice.doFetch(sql);
      BmoProduct bmoProduct = new BmoProduct();

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
	
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
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
    <title>:::<%= appTitle %>:::</title>
    <link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
</head>

<body class="default" <%= permissionPrint %> style="padding-right: 10px">

<table bprodr="0" cellspacing="0" cellpading="0" width="100%">
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
<table class="report" > 
     <% //por cada PRODUCTO imprime sus precios
     	while(pmProductPrice.next()) {
     		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, "productprices")+
     				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "products") + " ON (prod_productid = prpc_productid) " +
     				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "markets") + " ON (prpc_marketid = mrkt_marketid) " +
     				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "ordertypes") + " ON (ortp_ordertypeid = prpc_ordertypeid) " +
     				" LEFT JOIN " + SQLUtil.formatKind(sFParams, "wflowtypes") + " ON (wfty_wflowtypeid = prpc_wflowtypeid) " +
                    " LEFT JOIN " + SQLUtil.formatKind(sFParams, "currencies") + " ON (cure_currencyid = prpc_currencyid) " +             
                    " LEFT JOIN " + SQLUtil.formatKind(sFParams, "companies") + " ON (comp_companyid = prpc_companyid) " +                  
                    " WHERE prpc_productid = " + pmProductPrice.getInt("prod_productid") + " " + whereCompany;                  
            
                    
     		sql +=  whereApplies +
                " ORDER by prpc_startdate DESC, prod_productid ASC";
//      		System.out.println("SQL_Precios:"+sql);
           pmPrice.doFetch(sql);
           int i = 0, prod_productid = 0;
           while(pmPrice.next()) {
        	   
               //Estatus
              bmoProduct.getType().setValue(pmPrice.getString("products", "prod_type"));
              bmoProduct.getTrack().setValue(pmPrice.getString("products", "prod_track"));
//               String descripcion = pmPrice.getString("products", "prod_description");
              
              if((pmProductPrice.getInt("products", "prod_productid") != prod_productid)){
            	  prod_productid = pmProductPrice.getInt("products", "prod_productid");
        %>      
        		  <tr>
        		  		
	        		  <%if (sFParams.isFieldEnabled(bmoProduct.getCode())) {%>
	        		  <td colspan="25" class="reportGroupCell">
						<%= pmPrice.getString("prod_code")%> <%
					 	if(!pmPrice.getString("prod_displayname").equals("")) {
					 		prod_name = pmPrice.getString("prod_displayname");
					 	} else {
					 		prod_name = pmPrice.getString("prod_name");
					 	}
					 %> <%=HtmlUtil.stringToHtml(prod_name).toUpperCase()%> 
	        		  </td>
	        		  <%}%>
        		  </tr>
				
        		  <tr class="">
	        		  <td class="reportHeaderCellCenter">#</td>
	        		  <%if (sFParams.isFieldEnabled(bmoProductPrice.getCompanyId())) {%> 
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getCompanyId())) %>
						</td>
					  <%}%>
					  <%if (sFParams.isFieldEnabled(bmoProductPrice.getOrderTypeId())) {%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getOrderTypeId())) %>
						</td>
					  <%}%>
					   <%if (sFParams.isFieldEnabled(bmoProductPrice.getWFlowTypeId())) {%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getWFlowTypeId())) %>
						</td>
					  <%}%>
					  <%if (sFParams.isFieldEnabled(bmoProductPrice.getMarketId())) {%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getMarketId())) %>
						</td>
					  <%}%>
	        		  <%if (sFParams.isFieldEnabled(bmoProductPrice.getPrice())) {%>
						<td class="reportHeaderCell" style="text-align:right;" >
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getPrice())) %>
						</td>
					  <%}%>
					   <%if (sFParams.isFieldEnabled(bmoProductPrice.getCurrencyId())) {%>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getCurrencyId())) %>
						</td>
					  <%}%>
					  <%if (sFParams.isFieldEnabled(bmoProductPrice.getStartDate())) { %>
						<td class="reportHeaderCell">
							<%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoProductPrice.getStartDate())) %>
						</td>
				  	<%}%>
        		  </tr>
			<% } %>
				<tr class="reportCellEven">				
			        <%= HtmlUtil.formatReportCell(sFParams, "" + (i + 1),BmFieldType.NUMBER) %>
			        <%	if (sFParams.isFieldEnabled(bmoProductPrice.getCompanyId())) { %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPrice.getString("companies", "comp_name"), BmFieldType.STRING)) %>
			     	<%	} %>
			        <%	if (sFParams.isFieldEnabled(bmoProductPrice.getOrderTypeId())) { %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPrice.getString("ordertypes", "ortp_name"), BmFieldType.STRING)) %>
			        <%	} %>
			        <%	if (sFParams.isFieldEnabled(bmoProductPrice.getWFlowTypeId())) { %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPrice.getString("wflowtypes", "wfty_name"), BmFieldType.STRING)) %>
			        <%	} %>
			         <%	if (sFParams.isFieldEnabled(bmoProductPrice.getMarketId())) { %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPrice.getString("markets", "mrkt_name"), BmFieldType.STRING)) %>
			        <%	} %>
			        <%	if (sFParams.isFieldEnabled(bmoProductPrice.getPrice())) { %>
			        <%= HtmlUtil.formatReportCell(sFParams, pmPrice.getString("productprices", "prpc_price"), BmFieldType.CURRENCY) %>
			        <%	} %>
			        <%	if (sFParams.isFieldEnabled(bmoProductPrice.getCurrencyId())) { %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPrice.getString("currencies", "cure_code"), BmFieldType.CODE)) %>
			        <%	} %>
			        <%	if (sFParams.isFieldEnabled(bmoProductPrice.getStartDate())) { %>
			        <%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPrice.getString("productprices", "prpc_startdate"), BmFieldType.STRING)) %>
			        <%	} %>
			     </tr>
        <%
           i++;
           } //pmPrrices
     }//pmProducts 
     %>    
</table>
<%
	}// Fin de if(no carga datos)
	pmProductPrice.close();
 	pmPrice.close(); 	
    pmCountProduct.close();
%>  
<% if (print.equals("1")) { %>
    <script>
        //window.print();
    </script>
    <% } %>
  </body>
</html>
