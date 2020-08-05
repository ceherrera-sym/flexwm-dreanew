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
 
<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.flexwm.shared.op.BmoReqPayType"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.flexwm.server.cm.PmCustomerPoll"%>
<%@page import="com.google.gwt.logging.client.DefaultLevel.Severe"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.Catch"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerPoll"%>
<%@page import="com.flexwm.server.cm.PmProject"%>
<%@page import="com.flexwm.shared.cm.BmoProject"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowCategory"%>
<%@page import="com.flexwm.server.wf.PmWFlowCategory"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowType"%>
<%@page import="com.flexwm.server.wf.PmWFlowType"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String title = "";

	BmoProject bmoProject = new BmoProject(); 	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoProject.getProgramCode());

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
    <title>:::Encuesta de Servicio:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<%
    
    double requestservice = 0, requestimage = 0, requestperformance = 0, requestquality = 0,
        requestGeneral = 0, requestRecommendation = 0;
    String requestobservation = "";
    int projectId = 0;
    if (request.getParameter("cupo_projectid") != null) projectId = Integer.parseInt(request.getParameter("cupo_projectid"));
%>    
<form name="errorForm" action="flex_customerpoll.jsp?foreignId=<%=projectId%>"  method="post">

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

   BmUpdateResult bmUpdateResult = new BmUpdateResult();
    
    try {
    	
    	String errors = "";
        int i = 1;

	    if (request.getParameter("requestservice1") != null) requestservice = Double.parseDouble(request.getParameter("requestservice1"));
	    else errors += " Pregunta "  + (i) +  " / "; 
    	i++;
	    
	    if (request.getParameter("requestimage1") != null) requestimage = Double.parseDouble(request.getParameter("requestimage1"));
	    else errors += " Pregunta " + (i) + " / ";
	    i++;
	    
	    if(!(bmoWFlowCategory.getName().toString().equals("Proyecto Corporativo"))){
	    	if (request.getParameter("requestperformance1") != null) requestperformance = Double.parseDouble(request.getParameter("requestperformance1"));
	    	else errors += " Pregunta " + (i) + " / ";
	    	i++;
	    }
	    
	    if (request.getParameter("requestquality1") != null) requestquality = Double.parseDouble(request.getParameter("requestquality1"));    
	    else errors += " Pregunta "+ (i) + " / "; 
	    i++;

	    if (request.getParameter("requestgeneral1") != null) requestGeneral = Double.parseDouble(request.getParameter("requestgeneral1"));    
	    else errors += " Pregunta " + (i) + " / ";
	    i++;
	    
	    if (request.getParameter("requestrecommendation1") != null) requestRecommendation = Double.parseDouble(request.getParameter("requestrecommendation1"));
	    else errors += " Pregunta " + (i) +  " / ";

	    if (request.getParameter("requestobservation") != null) requestobservation = request.getParameter("requestobservation");
	    


    	if (errors.length() > 0) bmUpdateResult.addError("", "La encuesta no esta completa." + errors);
    	/*
	    System.out.println("requestservice: "+requestservice);
	    System.out.println("requestimage: "+requestimage);
	    System.out.println("requestperformance: "+requestperformance);
	    System.out.println("requestquality: "+requestquality);
	    System.out.println("requestGeneral: "+requestGeneral);
	    System.out.println("requestRecommendation: "+requestRecommendation);
    	*/
    	PmCustomerPoll pmCustomerPoll = new PmCustomerPoll(sFParams);    	
        BmoCustomerPoll bmoCustomerPoll = new BmoCustomerPoll();
                
        
        bmoCustomerPoll.getProjectId().setValue(projectId);
        bmoCustomerPoll.getService().setValue(requestservice);
        bmoCustomerPoll.getImage().setValue(requestimage);
        if(!(bmoWFlowCategory.getName().toString().equals("Proyecto Corporativo"))){
        	if (request.getParameter("requestperformance1") != null)
        		bmoCustomerPoll.getPerformance().setValue(requestperformance);
        }
        bmoCustomerPoll.getQuality().setValue(requestquality);
        bmoCustomerPoll.getGeneral().setValue(requestGeneral);
        bmoCustomerPoll.getRecommendation().setValue(requestRecommendation);
        bmoCustomerPoll.getObservation().setValue(requestobservation);

        bmoCustomerPoll.getDatePoll().setValue(SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));
        
        bmUpdateResult = pmCustomerPoll.saveSimple(bmoCustomerPoll, bmUpdateResult);
        
        if (bmUpdateResult.errorsToString().length() > 0) {        	
        	throw new SFException(bmUpdateResult.errorsToString());
        } else {  %>
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
	                  &nbsp;La encuesta de servicio ha sido enviada con exito<br>
	                  Gracias
	                  </p>
	             </td>
            </tr>       
</table>
        
       <% 	   
        }
        		
    } catch (SFException e) { %>
    <body onload="document.errorForm.submit()">
        <form name="errorForm" action="flex_customerpoll.jsp?foreignId=<%=projectId%>"  method="post">
    	<input type="hidden" name="errors" value="<%= bmUpdateResult.errorsToString()%>">
      </form>
    </body>
    </html>		
<%
    }
%>    
    
    
