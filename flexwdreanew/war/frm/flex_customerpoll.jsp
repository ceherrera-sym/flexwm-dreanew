<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
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
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowCategory"%>
<%@page import="com.flexwm.server.wf.PmWFlowCategory"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
	PmConn pmConn = new PmConn(sFParams);
	try {
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);		
		
		String sql = "", customerName = "", errors="";
		int countProject = 0;
		
		pmConn.open();
		
	    // Obtener parametros
	    int projectId = 0;
	    if (isExternal) projectId = decryptId;
	    else projectId = Integer.parseInt(request.getParameter("foreignId"));
	     
	    
	    //Validar si existe una encuesta liga al proyecto.
	    sql = "select count(cupo_projectid) as countprojects from customerpolls " +
	          " where cupo_projectid = " + projectId;
	    pmConn.doFetch(sql) ;
	    if (pmConn.next()) {
	    	countProject = pmConn.getInt("countprojects");
	    } 
	    
	    String title = "";

		BmoProject bmoProject = new BmoProject(); 	
		BmoProgram bmoProgram = new BmoProgram();
		PmProgram pmProgram  = new PmProgram(sFParams);
		bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoProject.getProgramCode());
	    
		BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoProject.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		String logoURL = "";
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else
			logoURL = sFParams.getMainImageUrl();
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
       <title>:::Encuesta de Servicio:::</title>
       <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
    <link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
</head>
<body class="default">
 
<%
   if (request.getParameter("errors") != null) errors = request.getParameter("errors");

   if (errors.length() > 0) {
%>
    <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
       <tr>
            <td colspan="5" align="center" height="" class="formErrorTitleLabel">
              <%= errors %> . . .<br> 	         
	          favor de verificar . . .	          
	        </td>
       </tr>
    </table>   
           
<%
    }
%>

	<% if (!(countProject > 0)) { %>
 <form name="cupoinfo" method="post" action="flex_cupoupdate.jsp">
   
    <%   
    	
        PmProject pmProject = new PmProject(sFParams);
        bmoProject = (BmoProject)pmProject.get(projectId);
        
    	BmoWFlowType bmoProjectWFlowType = new BmoWFlowType();
        PmWFlowType pmWFlowType = new PmWFlowType(sFParams);
        bmoProjectWFlowType = (BmoWFlowType)pmWFlowType.get(bmoProject.getWFlowTypeId().toInteger());
        
        BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
        PmWFlowCategory pmWFlowCategory = new PmWFlowCategory(sFParams);
        if(bmoProjectWFlowType.getWFlowCategoryId().toInteger() > 0)
        	bmoWFlowCategory = (BmoWFlowCategory)pmWFlowCategory.get(bmoProjectWFlowType.getWFlowCategoryId().toInteger());
        
    	//Obtener el nombre del cliente
    	customerName = bmoProject.getBmoCustomer().getDisplayFieldList().toString();
    	
    %>
    	<input type="hidden" name="cupo_projectid" value="<%= projectId%>">
    	
    	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
    	   <tr>
    	   <td align="left" width="" rowspan="5" valign="top">
				<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
    	    </td>
    	    <td colspan="4" class="documentSubTitle">
    	        &nbsp;Encuesta de Servicio
    	    </td>
    	  </tr>
    	  <tr>       
    	      <td class="documentLabel">
    	          &nbsp;Empresa/Cliente:
    	      </td>
    	      <td class="documentText">&nbsp;
    	            <%= customerName %>
    	      </td>
    	      <td class="documentLabel">
    	          &nbsp;Fecha/Hora Evento: 
    	      </td>
    	      <td class="documentText">
    	            &nbsp;de: <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoProject.getStartDate().toString()) %>
    	            &nbsp;a:&nbsp; <%= SFServerUtil.formatDate(sFParams, sFParams.getDateTimeFormat(), sFParams.getBmoSFConfig().getPrintDateTimeFormat().toString(), bmoProject.getEndDate().toString()) %> 
    	      </td>
    	  </tr>
    	  <tr>
    	      <td class="documentLabel">
    	          &nbsp;Evento: 
    	      </td>
    	      <td class="documentText">
    	            &nbsp; <%= bmoProject.getCode().toString() + " - " + bmoProject.getName().toString() %> 
    	      </td>
    	      <td class="documentLabel">
    	          &nbsp;Ciudad: 
    	      </td>
    	      <td class="documentText">
    	            &nbsp; <%= bmoProject.getBmoVenue().getBmoCity().getName().toString() %>,
    	            <%= bmoProject.getBmoVenue().getBmoCity().getBmoState().getCode().toString() %>,
    	            <%= bmoProject.getBmoVenue().getBmoCity().getBmoState().getBmoCountry().getCode().toString() %> 
    	      </td>
    	  </tr>
    	  <tr>
    	      <td class="documentLabel">
    	          &nbsp;Lugar: 
    	      </td>
    	      <td class="documentText">
    	            &nbsp; <%= bmoProject.getBmoVenue().getName().toString()%> 
    	      </td>
    	      <td class="documentLabel">
    	          &nbsp;Tipo de Evento:
    	      </td>
    	      <td class="documentText">
    	            &nbsp; <%= bmoProjectWFlowType.getName().toString() %>
    	      </td>
    	  </tr>
    	  <tr>
    	      <td class="documentLabel">
    	          &nbsp;Ejecutivo Comercial: 
    	      </td>
    	      <td class="documentText">
    	            &nbsp;<%= bmoProject.getBmoUser().getFirstname().toString()%> <%= bmoProject.getBmoUser().getFatherlastname().toString()%>
    	      </td>
    	      <td class="documentLabel">
                  &nbsp;N&uacute;mero Invitados 
              </td>
              <td class="documentText">
                    &nbsp;<%= bmoProject.getGuests().toInteger()%>
              </td>
    	      
    	  </tr>
    	</table>
    	<br>
    	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
           <tr>           
           <td colspan="6" class="documentSubTitle">
                &nbsp;Detalle de la encuesta de servicio
           </td>
          </tr>
       </table>
       <br>
       <% int i = 1; %>
       <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
           <tr>           
	           <td colspan="5" class="documentLabel">
	                &nbsp;<%= i++%>.- El servicio y ejecuci&oacute;n por parte de DREA con respecto a lo establecido en su contrato fue:
	           </td>
           </tr>
           <tr>
           		<td colspan="4">&nbsp;</td>
           </tr>
           <tr valign="top">
               	<td align="center">                   
               		<input type="radio" name="requestservice1" value="10">SUPERIOR A LA EXPECTATIVA
           		</td>
           		<td align="center">
           			<input type="radio" name="requestservice1" value="9.5">CUMPLI&Oacute; CON LA EXPECTATIVA                   
                </td>
                <td align="center">
                   	<input type="radio" name="requestservice1" value="0">POR DEBAJO DE LA EXPECTATIVA
                </td>         
           </tr>
           <tr>
           		<td colspan="4">&nbsp;</td>
           </tr>
           <tr>           
               	<td colspan="4" class="documentLabel">
               		&nbsp;<%= i++%>.- La imagen, puntualidad y servicio que recibi&oacute; por parte del equipo de producci&oacute;n y montaje fue:
            	</td>               
           </tr>
           <tr>
            	<td colspan="4">&nbsp;</td>
           </tr>
           <tr>  
               <td align="center">                   
               		<input type="radio" name="requestimage1" value="10">SUPERIOR A LA EXPECTATIVA
               </td>
               <td align="center">
                   <input type="radio" name="requestimage1" value="9.5">CUMPLI&Oacute; CON LA EXPECTATIVA                   
               </td>
               <td align="center">
                   <input type="radio" name="requestimage1" value="0">POR DEBAJO DE LA EXPECTATIVA
               </td>                     
           </tr>
           <tr>
            	<td colspan="4">&nbsp;</td>
           </tr>
           <% if(!(bmoWFlowCategory.getName().toString().equals("Proyecto Corporativo"))){ %>
	           <tr>           
	               <td colspan="4" class="documentLabel">
	                    &nbsp;<%= i++%>.- El desempe&ntilde;o, imagen, actitud y atenci&oacute;n que recibi&oacute; por parte del DJ fue:
	               </td>               
	           </tr>
	           <tr>
	            <td colspan="4">&nbsp;</td>
	           </tr>
	           <tr>   
	               <td align="center">                   
	                   <input type="radio" name="requestperformance1" value="10">SUPERIOR A LA EXPECTATIVA
	               </td>
	               <td align="center">
	                   <input type="radio" name="requestperformance1" value="9.5">CUMPLI&Oacute; CON LA EXPECTATIVA                   
	               </td>
	               <td align="center">
	                   <input type="radio" name="requestperformance1" value="0">POR DEBAJO DE LA EXPECTATIVA
	               </td>
                   <td align="center">
	                   <input type="radio" name="requestperformance1" value="0.1">N/A
	               </td> 
	           </tr>
           <% } %>
           <tr>
            	<td colspan="4">&nbsp;</td>
           </tr>
           <tr>           
               <td colspan="4" class="documentLabel">
                    &nbsp;<%= i++%>.- La apariencia, calidad y estado f&iacute;sico del equipo de audio, video e iluminaci&oacute;n en general fue:
               </td>
           </tr>
           <tr>
            	<td colspan="4">&nbsp;</td>
           </tr>
           <tr> 
               <td align="center">                   
                   <input type="radio" name="requestquality1" value="10">SUPERIOR A LA EXPECTATIVA
               </td>
               <td align="center">
                   <input type="radio" name="requestquality1" value="9.5">CUMPLI&Oacute; CON LA EXPECTATIVA                   
               </td>
               <td align="center">
                   <input type="radio" name="requestquality1" value="0">POR DEBAJO DE LA EXPECTATIVA
               </td>       
           </tr>
           <tr>
            	<td colspan="4">&nbsp;</td>
           </tr>
           <tr>           
	           <td colspan="4" class="documentLabel">
	                &nbsp;<%= i++%>.- &iquest;C&oacute;mo calificar&iacute;a el evento en general?
	           </td>
	       </tr>
	       <tr>
	        	<td colspan="4">&nbsp;</td>
	       </tr>
	       <tr> 
	           <td align="center">                   
	               <input type="radio" name="requestgeneral1" value="10">SUPERIOR A LA EXPECTATIVA
	           </td>
	           <td align="center">
	               <input type="radio" name="requestgeneral1" value="9.5">CUMPLI&Oacute; CON LA EXPECTATIVA                   
	           </td>
	           <td align="center">
	               <input type="radio" name="requestgeneral1" value="0">POR DEBAJO DE LA EXPECTATIVA
	           </td>       
	       </tr>
	       <tr>
	        	<td colspan="4">&nbsp;</td>
	       </tr> 
           <tr>           
               <td colspan="4" class="documentLabel">
                    &nbsp;<%= i++%>.- Acorde a su experiencia en general del evento, &iquest;Volver&iacute;a a considerar a DREA para un futuro o lo recomendar&iacute;a ?
               </td>
           </tr>
           <tr>
           		<td colspan="4">&nbsp;</td>
           </tr>
           <tr>   
               <td align="center">                   
                   <input type="radio" name="requestrecommendation1" value="10">SI
               </td>
               <td align="center">
                   <input type="radio" name="requestrecommendation1" value="-5">NO                   
               </td>                             
           </tr>
           <tr>
                <td class="documentLabel" colspan="4">
                <br>
                &nbsp;Observaciones
                </td>
           </tr>      
           <tr>
               <td colspan="4">
                    &nbsp;<textarea class="formLargeTextAreaField" name="requestobservation"></textarea> 
               </td>               
           </tr>
       </table>
       <br>
       <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
            <tr>
                <td colspan="5" align="center">
                    <input type="submit" name="savepoll" value="Enviar" class="formSaveButton">
               </td>
            </tr>            
       </table>      
</form>         
<%    	
    } else { %>
    <table border="0" cellspacing="0" width="100%" cellpadding="0">
	  <tr valign="top">
	    <td align="left" width="" align="center" rowspan="2">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
	    </td>
	    <td align="left" width="60%" colspan="">
	        DREA PRODUCCIONES S. DE R.L. DE C.V.
	    </td>			    
	  </tr>		   	    
	  <tr>
	    <td class="documentText" align="left">
	            PROLONGACI&Oacute;N 5 DE MAYO 8850-3 PARQUE INDUSTRIAL EL CANCHIM II<br>          
	            COL.SAN JUAN OCOT&Aacute;N, C.P. 45019 ZAPOPAN JALISCO M&Eacute;XICO            
	            <br>  
	            (33)3632.7475 | (33)3634.7688 | (33) 3631.3805            
	            <br>  
	            www.drea.com.mx     
	    </td>
	  </tr>
		 </table>
		 <table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">                
		      <TR class="">                    
		          <td class="documentSubTitle" align="left" colspan="2">&nbsp;</td>
		      </TR>		  
		</table>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">                
		      <TR class="">                    
		          <td class="" align="left" colspan="2">&nbsp;</td>
		      </TR>         
		</table>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		    <tr>
			      <td colspan="2" rowspan="2"  valign="top" align="center" class="detailStart">
		            <p class="document" align="center">&nbsp;<br> 
		            &nbsp;Ya existe una encuesta de servicio ligada al proyecto<br>
		            Gracias
		            </p>
		       </td>
		   </tr>       
		</table>
		<%    }

%>	
 
<% 	} catch (Exception e) { 
		String errorLabel = "Error de encuesta";
		String errorText = "La encuesta no puede ser desplegada.";
		String errorException = e.toString();
		
		//response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);

%>

	<%= errorException %>

<%
	} finally {
		pmConn.close();
	}
%>

</body>
</html>