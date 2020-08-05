<!--
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ 
-->
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "";
	
	BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoRequisitionReceipt.getProgramCode());
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
		
	//No cargar datos en caso de que se imprima/exporte y no tenga permisos	 
	%>
<head>
    <title>:::Recibo de Orden de Compra:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
	<link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default" <%= permissionPrint %>>

<%
	try {
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	    
	    // Obtener parametros
	    int receiptRequisitionId = 0;
	    if (isExternal) receiptRequisitionId = decryptId;
	    else if (request.getParameter("foreignId") != null) receiptRequisitionId = Integer.parseInt(request.getParameter("foreignId"));
	    
	    
	    PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(sFParams);
	    bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(receiptRequisitionId);
	    
	    BmoRequisition bmoRequisition = new BmoRequisition();
	    PmRequisition pmRequisition = new PmRequisition(sFParams);
	    bmoRequisition = (BmoRequisition)pmRequisition.get(bmoRequisitionReceipt.getRequisitionId().toInteger());
	    
	    BmoWhSection bmoWhSection = new BmoWhSection();
	    PmWhSection pmWhSection = new PmWhSection(sFParams);
	    if(bmoRequisitionReceipt.getWhSectionId().toInteger() > 0)
	    	bmoWhSection = (BmoWhSection)pmWhSection.get(bmoRequisitionReceipt.getWhSectionId().toInteger());
	    	    
	    BmoSupplier bmoSupplier = new BmoSupplier();
	    PmSupplier pmSupplier = new PmSupplier(sFParams);
	    bmoSupplier = (BmoSupplier)pmSupplier.get(bmoRequisitionReceipt.getSupplierId().toInteger());

	    BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoRequisition.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL ="";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
		
	
%>

<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
      <tr>
            <td align="left" width="" rowspan="10" valign="top">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
            </td>
            <td colspan="4" class="reportTitleCell">
            	Recibo de Orden de Compra: <%= bmoRequisitionReceipt.getCode().toString() %>
            </td>
      </tr>     
      <tr>
	      <th align = "left" class="reportCellEven">Orden de Compra:</th>
	      	<td class="reportCellEven">
	      	<%= bmoRequisition.getCode().toString() %> - <%= bmoRequisition.getName().toString() %>           
	      </td>
	      <th align = "left" class="reportCellEven">Tipo de Recibo:</th>
	      	<td class="reportCellEven">
	      	<%= bmoRequisitionReceipt.getType().getSelectedOption().getLabel() %>                 
	      </td>
      </tr>
      <tr>
	      <th align = "left" class="reportCellEven">Proveedor:</th>
	      <td class="reportCellEven">
	      		<%= bmoSupplier.getCode().toString() %> 
	      		<%= bmoSupplier.getName().toString() %>         
	      </td>
	      <th align = "left" class="reportCellEven">Fecha de Recibo:</th>
	      	<td class="reportCellEven">
	      	<%= bmoRequisitionReceipt.getReceiptDate().toString() %>                 
	      </td>
      </tr>
      <tr>
	      <th align = "left" class="reportCellEven">Estatus:</th>
	      <td class="reportCellEven">
	          <%= bmoRequisitionReceipt.getStatus().getSelectedOption().getLabel() %>         
	      </td>
	      <th align = "left" class="reportCellEven">Estatus de Pago:</th>
	      <td class="reportCellEven">
	          <%= bmoRequisitionReceipt.getPayment().getSelectedOption().getLabel() %>       
	      </td>

      </tr>
      <% if(bmoRequisitionReceipt.getWhSectionId().toInteger() > 0){ %>
	      <tr>
		      <th align = "left" class="reportCellEven">Almac&eacute;n:</th>
		      <td class="reportCellEven">
		          	<%= bmoWhSection.getName().toString() %>         
		      </td>
		      <th align = "left" class="reportCellEven">&nbsp;</th>
		      <td class="reportCellEven">
		      		&nbsp;       
		      </td>
		  </tr>
	  <% }%>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
        <tr>
            <td colspan="9" class="reportHeaderCell">
                Items                
            </td>
        </tr>
        <TR class="">                    
            <th class="reportCellEven" align="left">Cantidad</th>
            <th class="reportCellEven" align="left">Nombre</th>
            <th class="reportCellEven" align="right">Dias</th>
            <th class="reportCellEven" align="right">Precio</th>           
            <th class="reportCellEven" align="right">Total</th>
        </TR>
			<%
				BmoRequisitionReceiptItem bmoRequisitionReceiptItem = new BmoRequisitionReceiptItem();
				BmFilter filterRequisitionReceiptItem = new BmFilter();
				filterRequisitionReceiptItem.setValueFilter(bmoRequisitionReceiptItem.getKind(), bmoRequisitionReceiptItem.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
				Iterator<BmObject> listRequisitionReceiptItem = new PmRequisitionReceiptItem(sFParams).list(filterRequisitionReceiptItem).iterator();
				while (listRequisitionReceiptItem.hasNext()) {
					bmoRequisitionReceiptItem = (BmoRequisitionReceiptItem)listRequisitionReceiptItem.next();
			%>
           <TR class=""> 
               <td class="reportCellEven" align="left">
                    <%= bmoRequisitionReceiptItem.getQuantity().toString() %>                  
               </td>
               <td class="reportCellEven" align="left">
                    <%= bmoRequisitionReceiptItem.getName().toString() %>                 
               </td>   
               <td class="reportCellEven" align="right">
               	<%= bmoRequisitionReceiptItem.getDays().toDouble() %>                 
               </td> 
               <td class="reportCellEven" align="right">
                    <%= formatCurrency.format(bmoRequisitionReceiptItem.getPrice().toDouble()) %>                 
               </td> 
               <td class="reportCellEven" align="right">
                    <%= formatCurrency.format(bmoRequisitionReceiptItem.getAmount().toDouble()) %>                 
               </td> 
          </TR>
          <%
				}
           %>
            <tr>
	            <td>&nbsp;</td>
	        </tr>
	        <tr>
		        <td colspan="3" rowspan="3"  valign="top" align="left">
			   		<p class="documentComments"><b>&nbsp;</b><br> 
			   		&nbsp;</p>
			    </td> 
	            <th class="" align="right">Monto:</th>
	            <td class="reportCellEven" align="right"> <%= formatCurrency.format(bmoRequisitionReceipt.getAmount().toDouble()) %> </td>
	        </tr>
	        <tr>
	            <th class="" align="right">IVA:</th>
	            <td class="reportCellEven" align="right"> <%= formatCurrency.format(bmoRequisitionReceipt.getTax().toDouble()) %> </td>
	        </tr>
	        <tr>
	            <th class="" align="right">Total:</th>
	            <td class="reportCellEven" align="right"> 
	            	<b><%= formatCurrency.format(bmoRequisitionReceipt.getTotal().toDouble()) %> </b>
	            </td>
	       </tr>
</table> 
<p class="documentTitleCaption" align="left"> 
Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %>
</p>


<%  
	} catch (Exception e) { 
	    String errorLabel = "Error del CheckList";
	    String errorText = "El checkList no pudo ser desplegado exitosamente.";
	    String errorException = e.toString();
	    
	    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    }

%>