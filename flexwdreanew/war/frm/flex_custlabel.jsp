<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.ev.*"%>
<%@page import="com.flexwm.shared.ev.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.server.co.*"%>
<%@page import="com.flexwm.shared.co.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.symgae.shared.sf.BmoCompany"%>
<%@page import="com.symgae.server.sf.PmCompany"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import= "com.symgae.server.SFServerUtil"%>
<%@page import= "java.util.Calendar"%>
<%@page import="java.sql.Types"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat "%>

<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "";
	
	BmoPropertySale bmoPropertySale = new BmoPropertySale(); 	
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoPropertySale.getProgramCode());
%>

<html>
	<% 
	//Imprimir
	String print = "0", permissionPrint = "";
	if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
	
	//Exportar a Excel
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
		<title>:::<%= appTitle %>:::</title>
	    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
	     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
	</head>
	<body class="default" <%= permissionPrint %>>
	
		<%
			try {				
			    // Si es llamada externa, utilizar llave desencriptada
			    int propertySaleId = 0;
			    if (isExternal) propertySaleId = decryptId;
			    else propertySaleId = Integer.parseInt(request.getParameter("foreignId"));	 
			    
			    
			    //Venta
				PmPropertySale pmPropertySale = new PmPropertySale(sFParams);
				bmoPropertySale = (BmoPropertySale)pmPropertySale.get(propertySaleId);
					
				//Pedido 
				BmoOrder bmoOrder = new BmoOrder();
				PmOrder pmOrder = new PmOrder(sFParams);
				bmoOrder = (BmoOrder)pmOrder.get(bmoPropertySale.getOrderId().toInteger());
				
				//Pedido-Inmueble		
				BmoOrderProperty bmoOrderProperty = new BmoOrderProperty();
				PmOrderProperty pmOrderProperty = new PmOrderProperty(sFParams);
				bmoOrderProperty = (BmoOrderProperty)pmOrderProperty.getBy(bmoOrder.getId(), bmoOrderProperty.getOrderId().getName());
				
				//Desarrollo
				BmoDevelopment bmoDevelopment = new BmoDevelopment();
				PmDevelopment pmDevelopment = new PmDevelopment(sFParams);
				bmoDevelopment= (BmoDevelopment)pmDevelopment.get(bmoPropertySale.getBmoProperty().getBmoDevelopmentBlock().getBmoDevelopmentPhase().getDevelopmentId().toInteger());
				
				//Cliente
				BmoCustomer bmoCustomer = new BmoCustomer();
				PmCustomer pmCustomer = new PmCustomer(sFParams);
				bmoCustomer = (BmoCustomer)pmCustomer.get(bmoPropertySale.getCustomerId().toInteger());
				
				//Vivienda
				BmoProperty bmoProperty = new BmoProperty();
				PmProperty pmProperty = new PmProperty(sFParams);
				bmoProperty = (BmoProperty)pmProperty.get(bmoPropertySale.getPropertyId().toInteger());
				
				//Manzana
				BmoDevelopmentBlock bmoDevelopmentBlock = new BmoDevelopmentBlock();
				PmDevelopmentBlock pmDevelopmentBlock = new PmDevelopmentBlock(sFParams);
				bmoDevelopmentBlock = (BmoDevelopmentBlock)pmDevelopmentBlock.get(bmoProperty.getDevelopmentBlockId().toInteger());
				
				//Modelo
				BmoPropertyModel bmoPropertyModel = new BmoPropertyModel();
				PmPropertyModel pmPropertyModel = new PmPropertyModel(sFParams);
				bmoPropertyModel = (BmoPropertyModel)pmPropertyModel.get(bmoPropertySale.getBmoProperty().getPropertyModelId().toInteger());
				
		%>
		
		<table border="0" cellpadding="0" cellspacing="0" width="100%">
		    <tr class="documentText" style="font-size: 14px">
		        <td>
		        	<%= bmoCustomer.getCode().toString() %> <%= bmoCustomer.getDisplayName().toString() %>
		        	<br>
		        	<%= bmoProperty.getStreet().toString() %> #<%= bmoProperty.getNumber().toString() %> 
		        	L-<%= bmoProperty.getLot().toString() %> <%= bmoDevelopmentBlock.getCode().toString() %>
		        	<br>
		        	Fracc. <%= bmoDevelopment.getName().toString() %>
		        </td>
		    </tr>
		</table>
		
		<p>&nbsp;</p>
		
		<%	} catch (Exception e) { 
			String errorLabel = "Error de Formato";
			String errorText = "El Formato no pudo ser desplegado exitosamente.";
			String errorException = e.toString();
			
			response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
			}
		
		%>
	</body>
</html>



