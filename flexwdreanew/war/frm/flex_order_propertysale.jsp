<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.op.PmOrderGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderGroup"%>
<%@page import="com.flexwm.shared.op.BmoOrderItem"%>
<%@page import="com.flexwm.shared.op.BmoOrderEquipment"%>
<%@page import="com.flexwm.shared.op.BmoOrderStaff"%>
<%@page import="com.flexwm.server.op.PmOrderItem"%>
<%@page import="com.flexwm.server.op.PmOrderEquipment"%>
<%@page import="com.flexwm.server.op.PmOrderStaff"%>
<%@page import="com.flexwm.server.op.PmOrder"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.op.PmOrderType"%>
<%@page import="com.flexwm.shared.op.BmoOrderType"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.server.co.PmPropertySale"%>
<%@page import="com.flexwm.shared.co.BmoPropertySale"%>
<%@page import="com.flexwm.server.co.PmOrderProperty"%>
<%@page import="com.flexwm.shared.co.BmoOrderProperty"%>
<%@page import="com.flexwm.shared.co.BmoDevelopmentBlock"%>
<%@page import="com.flexwm.server.co.PmOrderPropertyModelExtra"%>
<%@page import="com.flexwm.shared.co.BmoOrderPropertyModelExtra"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "Hoja de Pedido";
	
	BmoOrderType bmoOrderType = new BmoOrderType();
	PmOrderType pmOrderType = new PmOrderType(sFParams);
	bmoOrderType = (BmoOrderType)pmOrderType.get(((BmoFlexConfig)sFParams.getBmoAppConfig()).getDefaultOrderTypeId().toInteger());	
	
	BmoOrder bmoOrder = new BmoOrder();
	PmOrder pmOrder = new PmOrder(sFParams);

	
	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	BmoWFlowType bmoPropertySaleWFlowType = new BmoWFlowType();
	BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
	BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
	BmoOrderPropertyModelExtra bmoOrderPropertyModelExtra = new BmoOrderPropertyModelExtra();
	BmoRaccount bmoRaccount= new BmoRaccount();

	
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
		
	    // Obtener parametros
	    int orderId = 0;
	    if (isExternal) orderId = decryptId;
	    else orderId = Integer.parseInt(request.getParameter("foreignId"));  
	    
		BmoWFlowType bmoWFlowType = new BmoWFlowType();  
		BmoUser bmoUser = new BmoUser();
	
	    if (bmoOrderType.getType().equals("" + BmoOrderType.TYPE_PROPERTY)) {
			//System.out.println("Inmueble");

			PmPropertySale pmPropertySale= new PmPropertySale(sFParams);
			PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
			PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
			
			bmoPropertySale = (BmoPropertySale)pmPropertySale.get(orderId);
			bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
			bmoPropertySaleWFlowType = (BmoWFlowType)pmWFlowType.get(bmoPropertySale.getWFlowTypeId().toInteger());
			bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());

			// Empresa
			BmoCompany bmoCompany = new BmoCompany();
			PmCompany pmCompany = new PmCompany(sFParams);
			bmoCompany = (BmoCompany)pmCompany.get(bmoOrder.getCompanyId().toInteger());
			
			// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
			String logoURL ="";
			if (!bmoCompany.getLogo().toString().equals(""))
				logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
			else 
				logoURL = sFParams.getMainImageUrl();
			
			String customer = "";
			if (bmoPropertySale.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
				customer =  bmoPropertySale.getBmoCustomer().getCode().toHtml() + " " +
							bmoPropertySale.getBmoCustomer().getFirstname().toHtml() + " " +
							bmoPropertySale.getBmoCustomer().getFatherlastname().toHtml() + " " +
							bmoPropertySale.getBmoCustomer().getMotherlastname().toHtml();
			} else {
				customer = bmoPropertySale.getBmoCustomer().getCode().toHtml() + " - " +bmoPropertySale.getBmoCustomer().getLegalname().toHtml();
			}
			
			
	%>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
				<tr>
					<td align="left" width="" rowspan="6" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
					</td>
					<td colspan="4" class="reportHeaderCell">
						Pedido <%= bmoOrder.getCode().toHtml()%>
					</td>
				</tr>
				<tr>       
					<th class="reportCellEven" align="left">
						Cliente: 
					</th>
					<td class="reportCellEven" align="left">
						<%= customer%>
					</td>
					<th class="reportCellEven" align="left">
						Inmueble: 
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoPropertySale.getBmoProperty().getCode().toHtml()%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						Venta Inmueble: 
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoPropertySale.getCode().toHtml() %> 
					</td>
					<th class="reportCellEven" align="left">
						Tipo de Cr&eacute;dito:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoPropertySaleWFlowType.getName().toHtml() %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						Ejecutivo Comercial: 
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoPropertySale.getBmoSalesUser().getFirstname().toHtml()%> <%= bmoPropertySale.getBmoSalesUser().getFatherlastname().toHtml()%>
					</td>
					<th class="reportCellEven" align="left">
						Fecha Impresi&oacute;n:
					</th>
					<td class="reportCellEven" align="left">
						<%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						Moneda:
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoOrder.getBmoCurrency().getCode().toHtml() %> - <%= bmoOrder.getBmoCurrency().getName().toHtml() %>
					</td>
					<th class="reportCellEven" align="left">
						Estatus Pedido: 
					</th>
					<td class="reportCellEven" align="left">
						<%= bmoOrder.getStatus().getSelectedOption().getLabeltoHtml()%>
					</td>
				</tr>
				<tr>
					<th class="reportCellEven" align="left">
						Estatus Entrega: 
					</th>
					<td class="reportCellEven" align="left" colspan="3">
						<%= bmoOrder.getDeliveryStatus().getSelectedOption().getLabeltoHtml()%>
					</td>
				</tr>
			</table>
			<br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size:12px">
	        	<tr>       
			        <td class="reportHeaderCell" align="left" width="10%" colspan="9">
			        	Inmueble
			        </td>
	        	</tr>
			    <tr>       
			        <th class="reportCellEven" align="left"  width="12%">Clave</th>
			        <th class="reportCellEven" align="left" width="10%">Mza</th>
			        <th class="reportCellEven" align="left" width="8%">Lote</th>
			        <th class="reportCellEven" align="left" width="20%">Calle y N&uacute;mero</th>
			        <th class="reportCellEven" align="right" width="10%">Precio</th>
			        <th class="reportCellEven" align="right" width="10%">$ T. Ex.</th>
			        <th class="reportCellEven" align="right" width="10%">$ C. Ex.</th>
			        <th class="reportCellEven" align="right" width="10%">$ Otros</th>
			        <th class="reportCellEven" align="right" width="10%">Total</th>
			    </tr>
			    <tr>       
			        <td class="reportCellEven" align="left" >
			        	<%= bmoOrderProperty.getBmoProperty().getCode().toHtml()%>
			        </td>
			        <td class="reportCellEven" align="left">
		        		<%= bmoOrderProperty.getBmoProperty().getBmoDevelopmentBlock().getCode().toHtml()%>
			        </td>
			        <td class="reportCellEven" align="left">
		        		<%= bmoOrderProperty.getBmoProperty().getLot().toHtml()%>
			        </td>
			        <td class="reportCellEven" align="left">
	        			<%= bmoOrderProperty.getBmoProperty().getStreet().toHtml()%> 
	        			#<%= bmoOrderProperty.getBmoProperty().getNumber().toHtml()%>
			        </td>
			        <td class="reportCellEven" align="right">
			        	<%= formatCurrency.format(bmoOrderProperty.getPrice().toDouble())%>
			        </td>
			        <td class="reportCellEven" align="right">
			        	<%= formatCurrency.format(bmoOrderProperty.getExtraLand().toDouble())%>
			        </td>
			        <td class="reportCellEven" align="right">
			        	<%= formatCurrency.format(bmoOrderProperty.getExtraConstruction().toDouble())%>
			        </td>
			        <td class="reportCellEven" align="right">
			        	<%= formatCurrency.format(bmoOrderProperty.getExtraOther().toDouble())%>
			        </td>
			        <td class="reportCellEven" align="right">
                        <%= formatCurrency.format(bmoOrderProperty.getAmount().toDouble()) %>
			        </td>
			    </tr>	
		    </table>
		    <br><br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size:12px">
	        	<tr>       
			        <td class="reportHeaderCell" align="left"  width="10%" colspan="6">
			        	Extras
			        </td>
		    	</tr>
			    <tr>       
			        <th class="reportCellEven" align="left" width="12%">Cantidad</th>
			        <th class="reportCellEven" align="left" width="30%">Extra</th>
			        <th class="reportCellEven" align="left" width="33%">Comentarios</th>
			        <th class="reportCellEven" align="right" width="15%">Precio</th>
			        <th class="reportCellEven" align="right" width="15%">Total</th>
			    </tr>
			    <%	  
			    	  double totalExtras = 0;
		              PmOrderPropertyModelExtra pmOrderPropertyModelExtra = new PmOrderPropertyModelExtra(sFParams);
		              BmFilter bmFilterOrpx = new BmFilter();
		              bmFilterOrpx.setValueFilter(bmoOrderPropertyModelExtra.getKind(), bmoOrderPropertyModelExtra.getOrderId().getName(), bmoOrder.getId());
		              Iterator<BmObject> extras = pmOrderPropertyModelExtra.list(bmFilterOrpx).iterator();
		              while(extras.hasNext()) {
		            	  bmoOrderPropertyModelExtra = (BmoOrderPropertyModelExtra)extras.next();	
		            	  totalExtras += bmoOrderPropertyModelExtra.getAmount().toDouble();
		                 %>      
		                  <tr>	      
		                      <td class="reportCellEven" align ="left" > 
									<%= bmoOrderPropertyModelExtra.getQuantity().toHtml() %>
		                      </td>
		                      <td class="reportCellEven" align ="left"> 
									<%= bmoOrderPropertyModelExtra.getBmoPropertyModelExtra().getName().toHtml() %>
		                      </td>
		                      <td class="reportCellEven" align="left">  
									<%= bmoOrderPropertyModelExtra.getComments().toHtml() %>
		                      </td>
		                      <td class="reportCellEven" align="right">  
	                        		<%= formatCurrency.format(bmoOrderPropertyModelExtra.getPrice().toDouble())%>
		                      </td>
		                      <td class="reportCellEven" align="right">
		                           	<%= formatCurrency.format(bmoOrderPropertyModelExtra.getAmount().toDouble()) %>
		                      </td>
		                  <tr>
		                  <% } %>
			    <tr >
				    <th class="reportCellEven" align="right" colspan="4">
				    	Total:
		           	</th>	
				    <td class="reportCellEven" align="right">
				    	<b><%= formatCurrency.format(totalExtras) %></b>
	               	</td>	
			    </tr>
            </table>
            <br>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size:12px">
	        	<tr>       
			        <td class="reportHeaderCell" align="left"  width="10%" colspan="10">
			        	CxC
			        </td>
		    	</tr>
			    <tr>       
			        <th class="reportCellEven" align="left"  width="12%">Concepto</th>
			        <th class="reportCellEven" align="left" width="12%">Factura</th>
			        <th class="reportCellEven" align="left" width="10%">Folio</th>
			        <th class="reportCellEven" align="left" width="15%">Descripci&oacute;n</th>
			        <th class="reportCellEven" align="left" width="8%">F. Ingreso</th>
			        <th class="reportCellEven" align="left" width="8%">F. Vencimiento</th>
			        <th class="reportCellEven" align="left" width="5%">Estatus</th>
			        <th class="reportCellEven" align="right" width="10%">Cargo</th>
			        <th class="reportCellEven" align="right" width="10%">Abono</th>
			        <th class="reportCellEven" align="right" width="10%">Saldo</th>

			    </tr>
			    <%	  
			    double totalAmount = 0, totalPayments = 0, totalBalance = 0;
	              PmRaccount pmRaccount = new PmRaccount(sFParams);
	              BmFilter bmFilterRacc = new BmFilter();
	              bmFilterRacc.setValueFilter(bmoRaccount.getKind(), bmoRaccount.getOrderId().getName(), bmoOrder.getId());
	              Iterator<BmObject> racc = pmRaccount.list(bmFilterRacc).iterator();
	              while(racc.hasNext()) {
	            	  bmoRaccount = (BmoRaccount)racc.next();	
	            	  totalAmount += bmoRaccount.getAmount().toDouble();
	            	  totalPayments += bmoRaccount.getPayments().toDouble();
	            	  totalBalance += bmoRaccount.getBalance().toDouble();
	                 %>      
	                  <tr>	      
	                      <td class="reportCellEven" align ="left"> 
	                      	<%= bmoRaccount.getBmoRaccountType().getName().toHtml() %>
	                      </td>
	                      <td class="reportCellEven" align ="left"> 
	                      	<%= bmoRaccount.getInvoiceno().toHtml() %>
	                      </td>
	                      <td class="reportCellEven" align="left">  
							<%= bmoRaccount.getFolio().toHtml() %>
	                      </td>
	                      <td class="reportCellEven" align="left">  
							<%= bmoRaccount.getDescription().toHtml() %>
	                      </td>
	                      <td class="reportCellEven" align="left">  
							<%= bmoRaccount.getReceiveDate().toHtml() %>
	                      </td>
	                      <td class="reportCellEven" align="left">  
							<%= bmoRaccount.getDueDate().toHtml() %>
						  </td>
	                      <td class="reportCellEven" align="left">  
							<%= bmoRaccount.getStatus().getSelectedOption().getLabel() %>
	                      </td>                     
	                      <td class="reportCellEven" align="right">  
                			<%= formatCurrency.format(bmoRaccount.getAmount().toDouble())%>
	                      </td>
	                      <td class="reportCellEven" align="right">
                           	<%= formatCurrency.format(bmoRaccount.getPayments().toDouble()) %>
	                      </td>
	                      <td class="reportCellEven" align="right">
                         	<%= formatCurrency.format(bmoRaccount.getBalance().toDouble()) %>
             			  </td>
	                  <tr>
	             <% } %>
			    
	             <tr >
	             	<th class="reportCellEven" align="right" colspan="7">
				    	Total:
		           	</th>	
				    <td class="reportCellEven" align="right">
				    	<b><%= formatCurrency.format(totalAmount) %></b>
	            	</td>
	            	<td class="reportCellEven" align="right">
	            		<b><%= formatCurrency.format(totalPayments) %></b>
	            	</td>
	            	<td class="reportCellEven" align="right">
	            		<b><%= formatCurrency.format(totalBalance) %></b>
	            	</td>
	             </tr>
	        </table>  
			<%
		}
		
		} catch (Exception e) { 
				String errorLabel = "Error de Pedido";
				String errorText = "El Pedido no puede ser desplegado.";
				String errorException = e.toString();
				
				//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		
		%>
		
			<%= errorException %>
		
		<%
			}
		

		
		%>

</body>
</html>