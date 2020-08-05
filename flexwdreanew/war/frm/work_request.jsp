<%@page import="java.text.NumberFormat"%>
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
<%@page import="com.flexwm.server.cm.PmOpportunity"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunity"%>
<%@page import="com.flexwm.server.cm.PmOpportunityDetail"%>
<%@page import="com.flexwm.shared.cm.BmoOpportunityDetail"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
	try {
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
		PmConn pmConnOppoDet= new PmConn(sFParams);
		pmConnOppoDet.open();
		boolean opde = false;
		
	    // Obtener parametros
	    int opportunityId = 0;
	    if (isExternal) opportunityId = decryptId;
	    else opportunityId = Integer.parseInt(request.getParameter("foreignId"));    
	
		BmoOpportunity bmoOpportunity = new BmoOpportunity();
		PmOpportunity pmOpportunity = new PmOpportunity(sFParams);
		bmoOpportunity = (BmoOpportunity)pmOpportunity.get(opportunityId);
		
		BmoOpportunityDetail bmoOpportunityDetail = new BmoOpportunityDetail();
		PmOpportunityDetail pmOpportunityDetail = new PmOpportunityDetail(sFParams);
		
		String sqlOpde = "SELECT * FROM opportunitydetails WHERE opde_opportunityid = " + bmoOpportunity.getId(); 
		pmConnOppoDet.doFetch(sqlOpde);
		if(pmConnOppoDet.next()) opde = true;
		
		if(opde)
			bmoOpportunityDetail = (BmoOpportunityDetail)pmOpportunityDetail.getBy(opportunityId, bmoOpportunityDetail.getOpportunityId().getName());
				
		pmConnOppoDet.close();
		
		BmoWFlowType bmoProjectWFlowType = new BmoWFlowType();
		PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
		bmoProjectWFlowType = (BmoWFlowType)pmWFlowType.get(bmoOpportunity.getForeignWFlowTypeId().toInteger());
		
		BmoQuote bmoQuote = new BmoQuote(); 
		PmQuote pmQuote = new PmQuote(sFParams);
		
		// Si es llamada externa, utilizar llave desencriptada, en caso contrario utilizar llave de la oportunidad
		bmoQuote = (BmoQuote)pmQuote.get(bmoOpportunity.getQuoteId().toInteger());
		
		String customer = "";
		if (bmoOpportunity.getBmoCustomer().getCustomertype().toChar() == BmoCustomer.TYPE_PERSON) {
			customer =  bmoOpportunity.getBmoCustomer().getFirstname().toHtml() + " " +
						bmoOpportunity.getBmoCustomer().getFatherlastname().toHtml() + " " +
						bmoOpportunity.getBmoCustomer().getMotherlastname().toHtml();
		} else {
			customer = bmoOpportunity.getBmoCustomer().getLegalname().toHtml();
		}
		
		if (bmoQuote.getStatus().toChar() != BmoQuote.STATUS_AUTHORIZED) throw new Exception("La Cotizacion no esta autorizada - no se puede desplegar.");
		
		if (!(bmoOpportunity.getWFlowTypeId().toInteger() > 0)) throw new Exception("El pedido no cuenta con el tipo de efecto - no se puede desplegar.");
%>

<html>
<head>
    <title>:::Cotizaci&oacute;n:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
    <link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
    <meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
   <tr>
   <td align="left" width="" rowspan="5" valign="top">
		<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
    </td>
    <td colspan="4" class="documentSubTitle">
        &nbsp;Cotizaci&oacute;n
    </td>
  </tr>
  <tr>       
      <td class="documentLabel">
          &nbsp;Empresa/Cliente: 
      </td>
      <td class="documentText">&nbsp;
			<%= customer %>
      </td>
      <td class="documentLabel">
          &nbsp;Fecha/Hora Evento: 
      </td>
      <td class="documentText">&nbsp;
      		<% if (!bmoOpportunity.getStartDate().toString().equals("")) { %>
      			de: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getStartDate().toString()) %>      				
            <% } %>
            <% if (!bmoOpportunity.getEndDate().toString().equals("")) { %>
				&nbsp;a:&nbsp; <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoOpportunity.getEndDate().toString()) %>
			<% } %>
      </td>
  </tr>
  <tr>
      <td class="documentLabel">
          &nbsp;Evento: 
      </td>
      <td class="documentText">
            &nbsp; <%= bmoOpportunity.getCode().toHtml() + " - " + bmoOpportunity.getName().toHtml() %> 
      </td>
      <td class="documentLabel">
          &nbsp;Ciudad: 
      </td>
      <td class="documentText">
            &nbsp; <%= bmoOpportunityDetail.getBmoVenue().getBmoCity().getName().toHtml() %>,
            <%= bmoOpportunityDetail.getBmoVenue().getBmoCity().getBmoState().getCode().toHtml() %>,
            <%= bmoOpportunityDetail.getBmoVenue().getBmoCity().getBmoState().getBmoCountry().getCode().toHtml() %> 
      </td>
  </tr>
  <tr>
      <td class="documentLabel">
          &nbsp;Lugar: 
      </td>
      <td class="documentText">
            &nbsp; <%= bmoOpportunityDetail.getBmoVenue().getName().toHtml()%> 
      </td>
      <td class="documentLabel">
          &nbsp;Tipo de Evento:
      </td>
      <td class="documentText">
            &nbsp; <%= bmoProjectWFlowType.getName().toHtml() %>
      </td>
  </tr>
  <tr>
      <td class="documentLabel">
          &nbsp;Ejecutivo Comercial: 
      </td>
      <td class="documentText">
            &nbsp;<%= bmoOpportunity.getBmoUser().getFirstname().toHtml()%> <%= bmoOpportunity.getBmoUser().getFatherlastname().toHtml()%>
      </td>
      <td class="documentLabel">
          &nbsp;Fecha Cotizaci&oacute;n:
      </td>
      <td class="documentText">
            &nbsp; <%= SFServerUtil.nowToString(sFParams, sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString()) %>
      </td>
  </tr>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0">
    <tr>       
        <td class="documentSubTitle" align="left">Rubro&nbsp;&nbsp;&nbsp;</td>
        <td class="documentSubTitle" align="left">Descripci&oacute;n</td>
        <td class="documentSubTitle" align="center">Cantidad</td>
        <td class="documentSubTitle" align="center">D&iacute;as</td>
        <td class="documentSubTitle" align="right">Precio&nbsp;</td>
        <td class="documentSubTitle" align="right">&nbsp;Total&nbsp;</td>
    </tr>
    <tr>
        <td colspan="6">&nbsp;</td>
    </tr>
    <tr>    
    <%
        String sql = "";
        double subTotal = 0, iva = 0;
        double subTotalGeneral = 0;
              BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
              PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(sFParams);
              BmFilter bmFilter = new BmFilter();
              bmFilter.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getQuoteId().getName(), bmoQuote.getId());
              Iterator<BmObject> quoteGroups = pmQuoteGroup.list(bmFilter).iterator();
              int i = 1;
              while (quoteGroups.hasNext()) { 
                   bmoQuoteGroup = (BmoQuoteGroup)quoteGroups.next();%>
                    <tr>
                        <td class="documentSubTitle" colspan="5">
                            &nbsp;<%= i++ %>. <%= bmoQuoteGroup.getName().toHtml() %>
                        </td>
                        <td class="documentSubTitle" align="right" colspan="">
                            <%= formatCurrency.format(bmoQuoteGroup.getAmount().toDouble()) %>&nbsp;
                        </td>
                    </tr> 
             <%	  
            	  BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
	              PmQuoteItem pmQuoteItem = new PmQuoteItem(sFParams);
	              BmFilter bmFilterQ = new BmFilter();
	              bmFilterQ.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId().getName(), bmoQuoteGroup.getId());
	              Iterator<BmObject> items = pmQuoteItem.list(bmFilterQ).iterator();
	              while(items.hasNext()) {
	                  bmoQuoteItem = (BmoQuoteItem)items.next();	                  
	                 %>      
	                  <tr>	
	                      <td>
	                           &nbsp;
	                      </td>                      
	                      <td class="documentText" align ="left" colspan="">  
	                        <%= bmoQuoteItem.getName().toHtml() %>
							&nbsp;<%= bmoQuoteItem.getBmoProduct().getModel().toHtml() %>
							<br><span class="documentSubText"><%= bmoQuoteItem.getDescription().toHtml() %></>
	                      </td>
	                      <td class="documentText" align="center">  &nbsp;
	                        <%if (bmoQuoteGroup.getShowQuantity().toBoolean()) { %> 
	                        	<%= bmoQuoteItem.getQuantity().toDouble() %>
                            <% } %>  
	                      </td>
	                      <td class="documentText" align="center">  &nbsp;
	                        <%if (bmoQuoteGroup.getShowQuantity().toBoolean()) { %>  
	                        	<%= bmoQuoteItem.getDays().toDouble() %>
                            <% } %>  	                        	
	                      </td>
	                      <td class="documentText" align="right">
	                         &nbsp;
	                        <%if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
                                <%= formatCurrency.format(bmoQuoteItem.getPrice().toDouble()) %>
                            <% } %>&nbsp;    
                          </td>
	                      <td class="documentText" align="right">
	                         &nbsp;
	                        <%if (bmoQuoteGroup.getShowPrice().toBoolean()) { %>  
                                <%= formatCurrency.format(bmoQuoteItem.getAmount().toDouble()) %>
                            <% } %>&nbsp;    
                          </td>
	                  <tr>
	             <% } %>	            
				<tr>
                  <td colspan="6" class="">&nbsp;</td>
              </tr>
                <% } %> 
                <tr>
                    <td class="documentSubTitle" colspan="6">
                        &nbsp;Recursos
                    </td>
                </tr> 
			<%	  
        	  BmoQuoteEquipment bmoQuoteEquipment = new BmoQuoteEquipment();
              PmQuoteEquipment pmQuoteEquipment = new PmQuoteEquipment(sFParams);
              BmFilter bmFilterEq = new BmFilter();
              bmFilterEq.setValueFilter(bmoQuoteEquipment.getKind(), bmoQuoteEquipment.getQuoteId().getName(), bmoQuote.getId());
              Iterator<BmObject> items = pmQuoteEquipment.list(bmFilterEq).iterator();
              while(items.hasNext()) {
                  bmoQuoteEquipment = (BmoQuoteEquipment)items.next();	                  
                 %>      
                  <tr>	
                      <td>
                           &nbsp;
                      </td>                      
                      <td class="documentText" align ="left" colspan="">  
						&nbsp;<%= bmoQuoteEquipment.getName().toHtml() %>
						&nbsp;<%= bmoQuoteEquipment.getDescription().toHtml() %>
                      </td>
                      <td class="documentText" align="center">  
                        <%if (bmoQuote.getShowEquipmentQuantity().toBoolean()) { %> 
                        	<%= bmoQuoteEquipment.getQuantity().toInteger() %>
                        <% } %>  
                      </td>
                      <td class="documentText" align="center">  
                        <%if (bmoQuote.getShowEquipmentQuantity().toBoolean()) { %>  
                        	<%= bmoQuoteEquipment.getDays().toDouble() %>
                        <% } %>  	                        	
                      </td>
                      <td class="documentText" align="right">
                         &nbsp;
                        <%if (bmoQuote.getShowEquipmentPrice().toBoolean()) { %>  
                            <%= formatCurrency.format(bmoQuoteEquipment.getPrice().toDouble()) %>
                        <% } %>&nbsp;    
                      </td>
                      <td class="documentText" align="right">
                         &nbsp;
                            <%= formatCurrency.format(bmoQuoteEquipment.getAmount().toDouble()) %>
                      </td>
                  <tr>
             <% } %>
	             <tr>
                  <td colspan="6" class="">&nbsp;</td>
              </tr>
   				<tr>
                    <td class="documentSubTitle" colspan="5">
                        &nbsp;Personal
                    </td>
                    <td class="documentSubTitle" align="right">
                    	<%                    	
                    		//Obtener el total del staff
                    		PmConn pmConn = new PmConn(sFParams);
                    		pmConn.open();
                    		
                    		double totalStaff = 0;
                    		
                    		sql = " SELECT SUM(qost_amount) AS totalstaff FROM quotestaff " +
                    		      " WHERE qost_quoteid = " +  bmoQuote.getId();
                    		
                    		pmConn.doFetch(sql);
                    		if (pmConn.next()) { 
                    			totalStaff = pmConn.getDouble("totalstaff");                    	    
                    		
                    	%>
                    		<%= formatCurrency.format(totalStaff) %>
                    		
                    	<% }
                    		
                    	  pmConn.close(); 
                        %>
                    </td>
                </tr> 
			<%	  
        	  BmoQuoteStaff bmoQuoteStaff = new BmoQuoteStaff();
              PmQuoteStaff pmQuoteStaff = new PmQuoteStaff(sFParams);
              BmFilter bmFilterSt = new BmFilter();
              bmFilterSt.setValueFilter(bmoQuoteStaff.getKind(), bmoQuoteStaff.getQuoteId().getName(), bmoQuote.getId());
              Iterator<BmObject> itemsStaff = pmQuoteStaff.list(bmFilterSt).iterator();
              while(itemsStaff.hasNext()) {
                  bmoQuoteStaff = (BmoQuoteStaff)itemsStaff.next();	                  
                 %>      
                  <tr>	
                      <td>
                           &nbsp;
                      </td>                      
                      <td class="documentText" align ="left" colspan="">  
						&nbsp;<%= bmoQuoteStaff.getName().toHtml() %>
						&nbsp;<%= bmoQuoteStaff.getDescription().toHtml() %>
                      </td>
                      <td class="documentText" align="center">  
                      	&nbsp;
                        <%if (bmoQuote.getShowStaffQuantity().toBoolean()) { %> 
                        	<%= bmoQuoteStaff.getQuantity().toInteger() %>
                        <% } %>  
                      </td>
                      <td class="documentText" align="center">  
                      	&nbsp;
                        <%if (bmoQuote.getShowStaffQuantity().toBoolean()) { %>  
                        	<%= bmoQuoteStaff.getDays().toDouble() %>
                        <% } %>  	                        	
                      </td>
                      <td class="documentText" align="right">
                         &nbsp;
                        <%if (bmoQuote.getShowStaffPrice().toBoolean()) { %>  
                        	<%= formatCurrency.format(bmoQuoteStaff.getPrice().toDouble()) %>
                        <% } %>    
                      </td>
                      <td class="documentText" align="right">
                         &nbsp;
                            <%= formatCurrency.format(bmoQuoteStaff.getAmount().toDouble()) %>
                      </td>
                  <tr>
             <% } %>
			<tr>
                  <td colspan="6" class="">&nbsp;</td>
              </tr>
              <tr>
              	<td rowspan="4">&nbsp; </td>
				   <td colspan="3" rowspan="4"  valign="top" align="left" class="detailStart">
				   		<p class="documentComments"><b>Notas:</b> <br>
				   		<% if(bmoOpportunityDetail.getGuests().toInteger() > 0){ %>
				   			Asistentes: <%= bmoOpportunityDetail.getGuests().toInteger() %> <br>
				   		<%	} %>
						<%= bmoQuote.getDescription().toHtml() %></p>
				   </td>                                  
                   <td class="documentLabel" align="right">
                       &nbsp;Subtotal:
                   </td>
                   <td align="right">
                        <div class="documentTotal"><%=  formatCurrency.format(bmoQuote.getAmount().toDouble()) %>&nbsp;</div>
                   </td>
              </tr>    
              <tr>                                                                       
                   <td class="documentLabel" align="right">
                   		<% if (bmoQuote.getDiscount().toDouble() > 0) { %>
							&nbsp;Descuento:
                   		<% } %>
                   </td>
                   <td align="right">
                   		<% if (bmoQuote.getDiscount().toDouble() > 0) { %>
                        	<div class="documentTotal"><%=  formatCurrency.format(bmoQuote.getDiscount().toDouble()) %>&nbsp;</div>
                   		<% } %>
                   </td>
              </tr>  
              <tr>                                                                        
                   <td class="documentLabel" align="right">
                   		<% if (bmoQuote.getTax().toDouble() > 0) { %>
                   			&nbsp;IVA:
                   		<% } %>
                   </td>
                   <td align="right">
						<% if (bmoQuote.getTax().toDouble() > 0) { %>
                        	<div class="documentTotal"><%=  formatCurrency.format(bmoQuote.getTax().toDouble()) %>&nbsp;</div>
                   		<% } %>
                   </td>
              </tr>  
              <tr>                                                                        
                   <td class="documentLabel" align="right">
                       &nbsp;Total:
                   </td>
                   <td align="right">
                        <div class="documentTotal"><%=  formatCurrency.format(bmoQuote.getTotal().toDouble()) %>&nbsp;</div>
                   </td>
              </tr> 
                 <tr>
                      <td class="" colspan="6">
                          &nbsp;
                      </td>
           		</tr> 
                 <tr>
                      <td class="documentSubTitle" colspan="6">
                          &nbsp;
                      </td>
           		</tr> 
</table>

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
    <tr align="center">
        <td align="center"><br><br>
            _______________________________________
        </td>
        <td align="center"><br><br>
            _______________________________________
        </td>
    </tr>
    <tr>
        <td align="center" class="documentTitleCaption">
        	Ejecutivo Comercial:
        	<br>
            <%= bmoOpportunity.getBmoUser().getFirstname().toHtml()%> <%= bmoOpportunity.getBmoUser().getFatherlastname().toHtml()%>
        </td>
        <td align="center" class="documentTitleCaption">
        	Acepto: <br>
			<%= bmoOpportunity.getBmoCustomer().getDisplayName().toHtml() %>
        </td>
    </tr> 
</table>

<% 	} catch (Exception e) { 
		String errorLabel = "Error de Cotizacion";
		String errorText = "La Cotizacion no puede ser desplegada.";
		String errorException = e.toString();
		
		//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

	<%= errorException %>

<%
	}
%>

</body>
</html>