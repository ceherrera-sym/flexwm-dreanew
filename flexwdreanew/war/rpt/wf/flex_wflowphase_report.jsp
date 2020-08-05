
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

<%@page import="java.util.concurrent.Phaser"%>
<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowStep"%>
<%@page import="com.flexwm.server.wf.PmWFlowStep"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import= "com.google.gwt.user.datepicker.client.CalendarUtil" %>
<%@page import= "java.util.GregorianCalendar" %>
<%@page import ="java.text.DateFormat" %>
<%@page import ="java.text.SimpleDateFormat" %>
<%@include file="/inc/login.jsp" %>


<%
	// Inicializar variables
 	String title = "Reporte de Tiempos por Fase";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
	BmoWFlowStep bmoWFlowStep = new BmoWFlowStep();
   	String sql = "", where = "", startDate = "", startEndDate = "",statusProperty = "", filters = "", wflowFunnelId = "",remindateend = "", remindate = "",status = "";
   	int profileId = 0,developmentId=0, wflowPhaseId = 0,developmentPhaseId=0, wflowCategoryId = 0, wflowTypeId = 0, userId = 0, progress=0, programId = 0, enabled = 0;
 // Obtener parametros
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wfsp_profileid") != null) profileId = Integer.parseInt(request.getParameter("wfsp_profileid"));
   	if (request.getParameter("wfsp_wflowphaseid") != null) wflowPhaseId = Integer.parseInt(request.getParameter("wfsp_wflowphaseid"));
   	if (request.getParameter("wfph_wflowcategoryid") != null) wflowCategoryId = Integer.parseInt(request.getParameter("wfph_wflowcategoryid"));
   	if (request.getParameter("wflowtypeid") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflowtypeid"));
   	if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) {
   		if (request.getParameter("wfsp_wflowfunnelid") != null) wflowFunnelId = request.getParameter("wfsp_wflowfunnelid");
   	}
   	if (request.getParameter("wfsp_enabled") != null) enabled = Integer.parseInt(request.getParameter("wfsp_enabled"));
   	if (request.getParameter("userid") != null) userId = Integer.parseInt(request.getParameter("userid"));
   	if (request.getParameter("wfsp_progress") != null) progress = Integer.parseInt(request.getParameter("wfsp_progress"));
   	if (request.getParameter("wfsp_startdate") != null) startDate = request.getParameter("wfsp_startdate");
   	if (request.getParameter("startenddate") != null) startEndDate = request.getParameter("startenddate");
   	if (request.getParameter("remindate") != null) remindate = request.getParameter("remindate");
	if (request.getParameter("remindateend") != null) remindateend = request.getParameter("remindateend");
	if (request.getParameter("wfsp_status") != null) status = request.getParameter("wfsp_status");
	if (request.getParameter("prsa_status") != null) statusProperty = request.getParameter("prsa_status");
	if (request.getParameter("prsa_propertyid") != null) statusProperty = request.getParameter("prsa_propertyid");
	if (request.getParameter("prsa_status") != null) statusProperty = request.getParameter("prsa_status");
	if (request.getParameter("deve_developmentid") != null) developmentId =  Integer.parseInt(request.getParameter("deve_developmentid"));
	if (request.getParameter("dvph_developmentphaseid") != null) developmentPhaseId = Integer.parseInt( request.getParameter("dvph_developmentphaseid"));

	
   	   	
   	if (profileId > 0) {
        where += " AND wfsp_profileid = " + profileId;
        filters += "<i>Grupo: </i>" + request.getParameter("wfsp_profileidLabel") + ", ";
    }
	
   	if (wflowPhaseId > 0) {
        where += " AND wfsp_wflowphaseid = " + wflowPhaseId;
        filters += "<i>Fase: </i>" + request.getParameter("wfsp_wflowphaseidLabel") + ", ";
    }
   	
   	if (wflowCategoryId > 0) {
        where += " AND wfca_wflowcategoryid = " + wflowCategoryId;
        filters += "<i>Categoria: </i>" + request.getParameter("wfph_wflowcategoryidLabel") + ", ";
    }
   	
   	if (wflowTypeId > 0) {
        where += " AND wfty_wflowtypeid = " + wflowTypeId;
        filters += "<i>Tipo: </i>" + request.getParameter("wflowtypeidLabel") + ", ";
    }
   	
   	if (sFParams.isFieldEnabled(bmoWFlowStep.getWFlowFunnelId())) {
	   	if (!wflowFunnelId.equals("")) {
	    	where = SFServerUtil.parseFiltersToSql("wfsp_wflowfunnelid", wflowFunnelId);
	   		filters += "<i>Funnel: </i>" + request.getParameter("wfsp_wflowfunnelidLabel") + ", ";
	   	}
   	}

   	if (!(startDate.equals(""))) {
		where += " AND wfsp_startdate >= '" + startDate + " 00:00'";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
    
    if (!(startEndDate.equals(""))) {
		where += " AND wfsp_startdate <= '" + startEndDate + " 23:59'";
   		filters += "<i>Fecha Inicio Final: </i>" + startEndDate + ", ";
   	}
    if(!(remindate.equals(""))) {
    	where += " AND wfsp_reminddate >= '" + remindate + "'";
    	filters += "<i>Fecha Recordar Inicio: </i>" + remindate + ", ";
    }   
    if (!(remindateend.equals(""))) {
    	where += " AND wfsp_reminddate <= '" + remindateend + "'";
    	filters += "<i>Fecha Recordar Final: </i>" + remindateend + ", ";
    }
   	
   	if (userId > 0) {
        where += " AND user_userid = " + userId;
        filters += "<i>Usuario: </i>" + request.getParameter("useridLabel") + ", ";
    }
   	
   	if (enabled == 0) {
        where += " AND wfsp_enabled = " + enabled;
        filters += "<i>Activo: </i> No, ";
    } else if (enabled == 1) {
    	where += " AND wfsp_enabled = " + enabled;
    	filters += "<i>Activo: </i> Si, ";
    } else {
    	filters += "<i>Activo: </i> Todos, ";
    }
   	
   	if (progress >= 0) {
   		if (progress == 99) {
   			where += " AND wfsp_progress <= " + progress;
   			filters += "<i>Avance: </i>, < 100%";
   		}else {
   			where += " AND wfsp_progress = " + progress;
   			filters += "<i>Avance: </i>, " + progress + "%";
   		}
   		
   	} else {
   		filters += "<i>Avance: </i> Todos, ";
   	}
   	if (!(status.equals(""))){
   		where += " AND wfsp_status = '" + status + "'";
   		filters += "<i>Estatus </i>, " + request.getParameter("wfsp_statusLabel") ;
   	}
   	if (!(statusProperty.equals(""))) {
        where += " AND prsa_status = '" + statusProperty + "'";
        filters += "<i> Estatus de Venta: </i>" + request.getParameter("prsa_statusLabel") + ", ";
    }
   	if (developmentId>0) {
        where += " AND deve_developmentid = '" + developmentId + "'";
        filters += "<i> Desarrollo: </i>" + request.getParameter("deve_developmentidLabel") + ", ";
    }
   	if (developmentPhaseId>0) {
        where += " AND dvph_developmentphaseid = " + developmentPhaseId + "";
        filters += "<i> Etapa de desarrollo:  </i>" + request.getParameter("dvph_developmentphaseidLabel") + ", ";
    }
   	
   
   	//Conexiones
   	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
   	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
   	PmConn pmConn3 = new PmConn(sFParams);
	pmConn3.open();
   	PmConn pmConn4 = new PmConn(sFParams);
	pmConn4.open();
	//abro conexion para inciar el conteo consulta general
    PmConn pmConnCount= new PmConn(sFParams);
	pmConnCount.open();
	
	
	
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
	
	<body class="default" <%= permissionPrint %>>
	
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
	<table class="report" border="0">                          
	        <tr class="">
	           <td class="reportHeaderCell">#</td>
	           <td class="reportHeaderCell">Venta</td>
	           <td class="reportHeaderCell">Cliente</td>
	           <td class="reportHeaderCell">Fecha Alta Cliente</td>
	           <td class="reportHeaderCell">Fase</td>
	           <td class="reportHeaderCell">F. Inicio</td>
	           	           <td class="reportHeaderCell">Inmueble</td>
	           
	           <td class="reportHeaderCell">F. Fin</td>
	           <td class="reportHeaderCell">Tiempo en Días</td>
	   		   <td class="reportHeaderCell">Fecha Hab.</td>
	           <td class="reportHeaderCell">Fecha Entrega</td>
	           
	          
	       </tr>
	<%
	sql = " SELECT  count(*) as contador FROM (SELECT  COUNT(*)  AS contador FROM wflowsteps  "+
			" LEFT JOIN wflows on (wfsp_wflowid = wflw_wflowid)  "+
			" LEFT JOIN wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) "+
			" LEFT JOIN wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid)  "+
			" LEFT JOIN wflowphases on (wfsp_wflowphaseid = wfph_wflowphaseid)  "+
			" LEFT JOIN wflowactions on (wfsp_wflowactionid = wfac_wflowactionid) "+
			" LEFT JOIN wflowfunnels on (wfsp_wflowfunnelid = wflf_wflowfunnelid) "+
			" LEFT JOIN orders on (orde_wflowid = wflw_wflowid) "+
			" LEFT JOIN propertysales on (prsa_orderid = orde_orderid)"+
			" LEFT JOIN customers on (orde_customerid = cust_customerid )"+
			" LEFT JOIN properties on (prty_propertyid = prsa_propertyid)"+
			" LEFT JOIN propertymodels on (ptym_propertymodelid = prty_propertymodelid)"+
			" LEFT JOIN developments on (ptym_developmentid = deve_developmentid)"+
			" LEFT JOIN developmentblocks on ( dvbl_developmentblockid =prty_developmentblockid ) "+
			" LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)"+
			" WHERE prsa_propertysaleid > 0 "+
			where+
			" ) as alias  ;";
			pmConnCount.doFetch(sql);
			int count =0;
			if(pmConnCount.next())
				count=pmConnCount.getInt("contador");
			System.out.println("contador DE REGISTROS --> "+count);
	
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
		<%=messageTooLargeList %>
		<% 
	}else{
	sql = " SELECT  distinct (prsa_code)    FROM wflowsteps  "+
			" LEFT JOIN wflows on (wfsp_wflowid = wflw_wflowid)  "+
			" LEFT JOIN wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) "+
			" LEFT JOIN wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid)  "+
			" LEFT JOIN wflowphases on (wfsp_wflowphaseid = wfph_wflowphaseid)  "+
			" LEFT JOIN wflowactions on (wfsp_wflowactionid = wfac_wflowactionid) "+
			" LEFT JOIN wflowfunnels on (wfsp_wflowfunnelid = wflf_wflowfunnelid) "+
			" LEFT JOIN orders on (orde_wflowid = wflw_wflowid) "+
			" LEFT JOIN propertysales on (prsa_orderid = orde_orderid)"+
			" LEFT JOIN customers on (orde_customerid = cust_customerid )"+
			" LEFT JOIN properties on (prty_propertyid = prsa_propertyid)"+
			" LEFT JOIN propertymodels on (ptym_propertymodelid = prty_propertymodelid)"+
			" LEFT JOIN developments on (ptym_developmentid = deve_developmentid)"+
			" LEFT JOIN developmentblocks on ( dvbl_developmentblockid =prty_developmentblockid ) "+
			" LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)"+
			" WHERE prsa_propertysaleid > 0"+
			where+
			"  ;";
 			System.out.println("SQL1"+sql);
		pmConn.doFetch(sql);
		int i = 1;
		while(pmConn.next()){
			 int phaseCon = 0;
			%>
					<%
			String sql2 = " SELECT  distinct (wfph_sequence) as number FROM wflowsteps  "+
		" LEFT JOIN wflows on (wfsp_wflowid = wflw_wflowid)"+
		" LEFT JOIN wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) "+
		" LEFT JOIN wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid) "+
		" LEFT JOIN wflowphases on (wfsp_wflowphaseid = wfph_wflowphaseid) "+
		" LEFT JOIN propertysales on(prsa_wflowtypeid = wfty_wflowtypeid)"+
		" WHERE  prsa_code LIKE '"+pmConn.getString("prsa_code")+"'; ";	
// 		System.out.println("SQL2"+sql2);
		pmConn2.doFetch(sql2);
		Date fin =null;
		Date inicio  = null;
		while(pmConn2.next()){
			String sl4 = " SELECT  MIN(wfsp_startdate) AS inicio, max(wfsp_enddate) as Fin FROM wflowsteps"+
					 " left JOIN wflows on (wfsp_wflowid = wflw_wflowid) "+
					" LEFT JOIN wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid)  "+
					" LEFT JOIN wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid) "+
					" LEFT JOIN wflowphases on (wfsp_wflowphaseid = wfph_wflowphaseid) "+
					" LEFT JOIN wflowactions on (wfsp_wflowactionid = wfac_wflowactionid) "+
					" LEFT JOIN wflowfunnels on (wfsp_wflowfunnelid = wflf_wflowfunnelid) "+
					" LEFT JOIN orders on (orde_wflowid = wflw_wflowid) "+
					" LEFT JOIN propertysales on (prsa_orderid = orde_orderid) "+
					" LEFT JOIN customers on (orde_customerid = cust_customerid ) "+
					" LEFT JOIN properties on (prty_propertyid = prsa_propertyid)"+
					" LEFT JOIN propertymodels on (ptym_propertymodelid = prty_propertymodelid)"+
					" LEFT JOIN developments on (ptym_developmentid = deve_developmentid)"+
					" LEFT JOIN developmentblocks on ( dvbl_developmentblockid =prty_developmentblockid ) "+
					" LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)"+
					"  WHERE (  ( prsa_code LIKE '"+pmConn.getString("prsa_code")+"' AND 	wfph_sequence = "+pmConn2.getInt("number")+")  )  AND wfsp_enabled =1 ORDER BY wflw_code ASC limit 1;";
// 					System.out.println("SQL3"+sl4);
			pmConn4.doFetch(sl4);
		int dias =0;
		String fechaactua = "";
		String inicioString = "";
		String finString = "";
		
		Date fa = null;
				while(pmConn4.next()){
					int previousDate=0 ;
				inicio =pmConn4.getDate("inicio");
				fin =pmConn4.getDate("fin");
				
			if(inicio == null) {
				inicioString = "-";
				
						
			}else{
				inicioString = ""+inicio;
			}
			if(fin == null &&  inicio!= null ){
				String f="";
				String dateNow =SFServerUtil.nowToString(sFParams, sFParams.getDateFormat());
				finString = ""+dateNow;	

			}					
			else{
				if(fin == null &&  inicio== null)
				{
					finString="-";
				}
				if(fin!=null)
				finString = ""+fin;
			}
			
			if(finString !="-" &&  inicioString!="-"){

				dias =  SFServerUtil.daysBetween(sFParams.getDateFormat(), inicio.toString(),finString.toString());
					
			}
				}
				
			String sql3 = " SELECT prty_code,prsa_code,cust_code,cust_datecreate,wfph_name,prsa_enddate,prty_finishdate  FROM wflowsteps  "+
					" left JOIN wflows on (wfsp_wflowid = wflw_wflowid)  "+
					"  LEFT JOIN wflowtypes on (wflw_wflowtypeid = wfty_wflowtypeid) "+
					" LEFT JOIN wflowcategories on (wfty_wflowcategoryid = wfca_wflowcategoryid)  "+
					" LEFT JOIN wflowphases on (wfsp_wflowphaseid = wfph_wflowphaseid)  "+
					" LEFT JOIN wflowactions on (wfsp_wflowactionid = wfac_wflowactionid) "+
					" LEFT JOIN wflowfunnels on (wfsp_wflowfunnelid = wflf_wflowfunnelid) "+
					" LEFT JOIN orders on (orde_wflowid = wflw_wflowid) "+
					" LEFT JOIN propertysales on (prsa_orderid = orde_orderid)"+
					" LEFT JOIN customers on (orde_customerid = cust_customerid )"+
					" LEFT JOIN properties on(prty_propertyid = prsa_propertyid)"+
					" LEFT JOIN propertymodels on (ptym_propertymodelid = prty_propertymodelid)"+
					" LEFT JOIN developments on (ptym_developmentid = deve_developmentid)"+
					" LEFT JOIN developmentblocks on ( dvbl_developmentblockid =prty_developmentblockid ) "+
					" LEFT JOIN developmentphases on(dvph_developmentphaseid = dvbl_developmentphaseid)"+
					" WHERE prsa_code LIKE '"+pmConn.getString("prsa_code")+"' AND wfph_sequence = "+pmConn2.getInt("number")+" "+
					where+
					" limit 1 ";
 					System.out.println("sql4*"+sql3);
			pmConn3.doFetch(sql3);
			
			while(pmConn3.next()){
				phaseCon ++;
				%>
				<TR >				
					<%if(phaseCon > 1){ %>
						<%= HtmlUtil.formatReportCell(sFParams, " " ,BmFieldType.NUMBER) %>						
						
					<%} else { %>
						<%= HtmlUtil.formatReportCell(sFParams, ""+i ,BmFieldType.NUMBER) %>	
					<%} %>	
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("propertysales", "prsa_code") , BmFieldType.STRING)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("customers", "cust_code"),BmFieldType.CODE)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("customers", "cust_datecreate"),BmFieldType.STRING)) %>		
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("wflowphases", "wfph_name"),  BmFieldType.STRING)) %>		
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, (inicioString).toString(), BmFieldType.DATETIME)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("properties", "prty_code"),  BmFieldType.STRING)) %>		
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, (finString).toString(), BmFieldType.DATETIME)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, ""+(dias), BmFieldType.NUMBER)) %>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("properties", "prty_finishdate"),  BmFieldType.STRING)) %>		
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmConn3.getString("propertysales", "prsa_enddate"),  BmFieldType.STRING)) %>		
					
					
								
								
				</TR>
			
					
				
			<%
			}	
			
		}
		i++;
		}
		
		%>
		
		</TABLE>  
		<% 
	pmConnCount.close();
    pmConn.close();  
	pmConn2.close();  	
	pmConn3.close();  	
	pmConn4.close();
	
   
%>  


<% if (print.equals("1")) { %>
	<script>
// 		window.print();
	</script>
<% }
System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
+ " Reporte: "+title
+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
}
}// FIN DEL CONTADOR
%>
  </body>
</html>