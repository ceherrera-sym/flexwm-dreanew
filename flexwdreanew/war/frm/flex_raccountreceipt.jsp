<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.symgae.shared.BmoFlex"%>
<%@page import="com.flexwm.server.PmFlexConfig"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="java.sql.Types"%>
<%@page import="com.symgae.server.PmConn"%>
<%@include file="../inc/login_opt.jsp" %>

<%
String title = "Cotizaci&oacute;n";


BmoRaccount bmoRaccount = new BmoRaccount();
BmoProgram bmoProgram = new BmoProgram();
PmProgram pmProgram  = new PmProgram(sFParams);
bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoRaccount.getProgramCode());

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
    <title>:::Recibo N.F.:::</title>
    <link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
     <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
</head>
<body class="default">
<%

NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);

PmConn pmConn = new PmConn(sFParams);
PmConn pmConn2 = new PmConn(sFParams);

String sql = "";
try {
	int raccountId = 0, projectId = 0;
	String projectName = "", projectDate = "", projectCity = "", clientName = "";
	if (isExternal) raccountId = decryptId;
	else raccountId = Integer.parseInt(request.getParameter("foreignId"));
	
    
    PmRaccount pmRaccount = new PmRaccount(sFParams);
    bmoRaccount = (BmoRaccount)pmRaccount.get(raccountId);
    
    
    pmConn.open();
    pmConn2.open();
    
    clientName = bmoRaccount.getBmoCustomer().getFatherlastname().toString() + " " +
	             bmoRaccount.getBmoCustomer().getMotherlastname().toString() + " " +
	             bmoRaccount.getBmoCustomer().getFirstname().toString();
    String logoURL ="";
    if (bmoRaccount.getOrderId().toInteger() > 0) {
	    //Obtener el projecto desde el pedido
	    pmConn.doFetch(" select orde_projectid from orders " +
	                   " where orde_orderid  = " + bmoRaccount.getOrderId().toInteger());
	    if (pmConn.next()) projectId = pmConn.getInt("orde_projectid");
	    
	    BmoProject bmoProject = new BmoProject();
	    PmProject pmProject = new PmProject(sFParams);
	    bmoProject = (BmoProject)pmProject.get(projectId);
	    
	    projectName = bmoProject.getName().toString();
	    projectCity = bmoProject.getBmoVenue().getBmoCity().getName().toString() + "," +
                      bmoProject.getBmoVenue().getBmoCity().getBmoState().getCode().toString() + ", " +
                      bmoProject.getBmoVenue().getBmoCity().getBmoState().getBmoCountry().getCode().toString();
	    projectDate = bmoProject.getStartDate().toString();
	    
	    BmoCompany bmoCompany = new BmoCompany();
		PmCompany pmCompany = new PmCompany(sFParams);
		bmoCompany = (BmoCompany)pmCompany.get(bmoProject.getCompanyId().toInteger());
		
		// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
		
		if (!bmoCompany.getLogo().toString().equals(""))
			logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
		else 
			logoURL = sFParams.getMainImageUrl();
	    
	    
    }    
 %>   
    
    
    
    
	

<table border="0" cellspacing="0" width="100%" cellpadding="0">
    <tr>
        <td align="right" width="" rowspan="6" valign="top">
			<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
        </td>        
    </tr>
</table>

    <%
    String nowMonth = SFServerUtil.nowToString(sFParams, "MM");
    
    switch (Integer.parseInt(nowMonth)) {
                case 1:
                    nowMonth = "Enero";
                    break;
                case 2:
                    nowMonth = "Febrero";
                    break;
                case 3:
                    nowMonth = "Marzo";
                    break;
                case 4:
                    nowMonth = "Abril";
                    break;
                case 5:
                    nowMonth = "Mayo";
                    break;
                case 6:
                    nowMonth = "Junio";
                    break;
                case 7:
                    nowMonth = "Julio";
                    break;
                case 8:
                    nowMonth = "Agosto";
                    break;
                case 9:
                    nowMonth = "Septiembre";
                    break;
                case 10:
                    nowMonth = "Octubre";
                    break;
                case 11:
                    nowMonth = "Noviembre";
                    break;
                case 12:
                    nowMonth = "Diciembre";
                    break;
                default:
                    nowMonth = "n/d";
             }

%>
<p class="documentCaption" align="right"> 
El d&iacute;a <%= SFServerUtil.nowToString(sFParams, "dd") %> del mes de <%= nowMonth %> 
del a&ntilde;o <%= SFServerUtil.nowToString(sFParams, "YYYY") %> en Guadalajara, Jalisco<br>
</p>

<br><br><br><br><br>
<br><br><br><br><br>  
<table border="0" cellspacing="0" width="100%" cellpadding="0">
<td width="20%">&nbsp;</td>
<td align="center">
<p class="document" align="justify">
            Recibimos del cliente:<b><%= clientName %></b> la cantidad de: <b><%= formatCurrency.format(bmoRaccount.getAmount().toDouble()) %></b> 
            correspondientes al de pago de la CXC con clave:<b><%= bmoRaccount.getCode().toString() %> </b> por concepto del evento:<b> <%= projectName %> </b> con fecha: <b><%= projectDate %></b>
</p>
</td>
<td width="20%">&nbsp;</td>
</table>
<br><br><br><br><br>
<br><br><br><br><br>

<table border="0" cellspacing="0" width="100%" cellpadding="0">       
  <tr>
    <td class="contracSubTitle" align="right">
        <b>DREA PRODUCCIONES S. DE R.L. DE C.V.</b>
    </td>
  </tr>
  <tr>  
    <td class="contractTitleCaption" align="right">            
            PROLONGACI&Oacute;N 5 DE MAYO 8850-3
            PARQUE INDUSTRIAL EL CANCHIM II<br>
            COL.SAN JUAN OCOT&Aacute;N, C.P. 45019
            ZAPOPAN JALISCO M&Eacute;XICO<br>  
            (33)3632.7475 | (33)3634.7688 | (33) 3631.3805
    </td>
  </tr>
  <tr>  
    <td class="contracSubTitle" align="right">            
            <b>www.drea.com.mx</b>
    </td>
  </tr>
</table>
<%  } catch (Exception e) { 
    String errorLabel = "Error de Recibo NF";
    String errorText = "El recibo no pudo ser desplegado exitosamente.";
    String errorException = e.toString();
    
    response.sendRedirect("/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
    } finally {
    	pmConn2.close();
        pmConn.close();
    }
%>
</body>
</html>
