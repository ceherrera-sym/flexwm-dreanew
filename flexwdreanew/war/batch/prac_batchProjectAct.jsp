<%@page import="com.flexwm.server.cm.PmProjectStep"%>
<%@page import="com.flexwm.shared.cm.BmoProjectStep"%>
<%@page import="com.flexwm.server.cm.PmProjectActivities"%>
<%@page import="com.flexwm.shared.cm.BmoProjectActivities"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@page import="com.symgae.shared.SFException"%>
<%@include file="../inc/login.jsp" %>

    
    <%	String programTitle = "Importaci&oacuten de Tareas";
    
      int wflowId = 0;
      int wflowTypeId = 0;
      int projectStepId = 0;
      String action = "", errors = "",populateData = "",errorSave = "";
      if(request.getParameter("wflowId") != null) wflowId = Integer.parseInt(request.getParameter("wflowId"));
      if(request.getParameter("wflowTypeId") != null) wflowTypeId = Integer.parseInt(request.getParameter("wflowTypeId"));
      if(request.getParameter("projectStepId") != null) projectStepId = Integer.parseInt(request.getParameter("projectStepId"));
      if (request.getParameter("action") != null) 
  		action = request.getParameter("action");
      if (request.getParameter("populateData") != null) 
  		populateData = request.getParameter("populateData");
      if(request.getParameter("errorsave") != null)
    		 errorSave = request.getParameter("errorsave");
    
      %>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="UTF-8">
	<title><%=programTitle %></title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">
</head>
<body>
	<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		<tr>
			<td align="left" width="" rowspan="5" valign="top">
		   	 	<img border="0" width="<%=SFParams.LOGO_WIDTH%>" height="<%=SFParams.LOGO_HEIGHT%>" src="<%= sFParams.getMainImageUrl() %>" >
			</td>
			<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
			</td>
		</tr>
	</table>
	<% if (action.equalsIgnoreCase("")) { %>
	<table width="100%" height="50%" border="0" align="center"
		class="detailStart">
		<tr class="reportSubTitle">
			<td colspan="1" align="left" width="99%" height="35"
				class="reportCellEven">
				<li>Se recomienda elaborar preparaci&oacute;n en archivo de
					texto, seleccionar y pegar en el recuadro siguiente.</li>
				<li><b>Los campos con '*' son obligatorios. Colocar la
						palabra "empty" para los campos vacíos.</b></li>
				<li>Formato (| = tab): Nombre* | Grupo | Recurso | H. Est* </li>
			</td>
		</tr>
		<TR>
			<td colspan="3">
				<TABLE cellpadding="0" cellspacing="0" border="0" width="80%">
					<FORM action="?&wflowId=<%=wflowId %>&wflowTypeId=<%=wflowTypeId%>&projectStepId=<%=projectStepId%>" method="post" name="listFilter">
						<input type="hidden" name="action" value="revision">
						<tr class="" height="35">
							<td colspan="10" align="center" width="100%"><textarea
									name="populateData" width="100%" cols="100" rows="30"></textarea>
							</td>
						</tr>
						<tr class="">
							<td align="center" colspan="4" height="35" valign="middle">
								<input class="formSaveButton" type="submit" value="SIGUIENTE">
							</td>
						</tr>
					</FORM>
				</TABLE>
			</TD>
		</TR>
	</TABLE>

	<%}  else if (action.equalsIgnoreCase("revision")) {
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		
		String msg = "";
		String sql = "";
		String s = "";
		int i = 1;		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
// 		String prac_project = "";	
		String prac_name = "";
		String prac_profile = "";
		String prac_user = "";
		String prac_startDate = "";
		String prac_endDate = "";
		String prac_hEst = "";
	

		 errors = "";
		try{%>

			<table width="80%" border="0" align="center" class="">
				<TR>
					<td colspan="4">
						<table cellpadding="0" cellspacing="0" border="1" rules=all
							width="90%">
							<tr valign="middle" align="center" class="">
								<td class="reportHeaderCell">&nbsp;#</td>
<!-- 								<td class="reportHeaderCell">Proyecto</td> -->
								<td class="reportHeaderCell">Nombre</td>
								<td class="reportHeaderCell">Grupo</td>
								<td class="reportHeaderCell">Recurso</td>
								<td class="reportHeaderCell">Fecha Inicio</td>								
								<td class="reportHeaderCell">H. Est</td>
								<td class="reportHeaderCell">Errors</td>
							</tr>
					
			
		
			<%	while (inputData.hasMoreTokens() && i < 2000) {
				errors = "";
    		    s = inputData.nextToken();
    			StringTokenizer tabs = new StringTokenizer(s, "\t");       
//     			prac_project = tabs.nextToken();
    			prac_name = tabs.nextToken();
    			prac_profile = tabs.nextToken();
    			prac_user = tabs.nextToken();    			
    			prac_hEst = tabs.nextToken();
    			//Quitar espacios
//     			prac_project = prac_project.trim();
    			prac_profile = prac_profile.trim();
    			prac_user = prac_user.trim();    			
    			prac_hEst = prac_hEst.trim();
    			
    			
//     			if(!prac_project.equals("empty")){
//     				sql = "SELECT * FROM projectsstep WHERE spro_code LIKE '" + prac_project +"'"	;
//     				pmConn.doFetch(sql);
//     				if(!pmConn.next()){
//     					errors += "No se encontro el Proyecto " +  prac_project;
//     				}
//     			}else{
//     				errors += "Se debe indicar a que Proyecto pertenece la Tarea";
//     			}
    			if(prac_name.equalsIgnoreCase("empty")){
    				errors = " ,Se debe dar un Nombre a la tarea ";
    			}
    			//Validar grupo
    			if(!prac_profile.equals("empty")){
    				sql = "SELECT * FROM profiles WHERE prof_name LIKE '" + prac_profile + "'"	;
    				pmConn.doFetch(sql);
    				if(!pmConn.next()){
    					errors += " ,No se encontro el Grupo " +  prac_profile;
    				}
    			}
    			//validar usuario
    			if(!prac_user.equals("empty")){
    				sql = "SELECT * FROM users WHERE user_code LIKE '" + prac_user + "'"	;
    				pmConn.doFetch(sql);
    				if(!pmConn.next()){
    					errors += " ,No se encontro el Recurso " +  prac_user;
    				}
    			}
    			try{
    				Integer.parseInt(prac_hEst);
    			}catch (Exception e){
    				errors += " ,se necesita un numero entero "; 
    			}
    			prac_startDate = SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat());
    			%>
    			<TR class="reportCellEven" width="100%">
		    		<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>
<%-- 		    		<%=HtmlUtil.formatReportCell(sFParams, prac_project, BmFieldType.STRING)%> --%>
		    		<%=HtmlUtil.formatReportCell(sFParams, prac_name, BmFieldType.STRING)%>
		    		<%=HtmlUtil.formatReportCell(sFParams, prac_profile, BmFieldType.STRING)%>
		    		<%=HtmlUtil.formatReportCell(sFParams, prac_user, BmFieldType.STRING)%>
		    		<%=HtmlUtil.formatReportCell(sFParams, prac_startDate, BmFieldType.STRING)%>					
					<%=HtmlUtil.formatReportCell(sFParams, prac_hEst, BmFieldType.STRING)%>		
		    		<td><font color="red"><%= errors %></font></prcp_price>
		    	</TR>
		<%}%>
		</table>
		<TABLE cellpadding="0" cellspacing="0" border="0"  width="100%">
				<FORM action="?&wflowId=<%=wflowId %>&wflowTypeId=<%=wflowTypeId%>&projectStepId=<%=projectStepId%>" method="POST" name="listFilter">	
				<input type="hidden" name="action" value="populate">
				<input type="hidden" name="populateData" value="<%= populateData %>">			
				<tr class="">
				    <td align="center" colspan="4" height="35" valign="middle">
				    <% if (errors.equals("")) { %>
				        <input type="submit" class="formSaveButton" value="GUARDAR">
				    <% } %>    
				    </td>
				</tr>			    
				</FORM>								
			</TABLE>
		<%}catch (Exception e){
			pmConn.rollback();
			throw new SFException(e.toString());	
		  } finally {
				pmConn.close();				
		  }
		%>
	<%} else if (action.equals("populate"))	{
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmConn pmConn = new PmConn(sFParams);
		BmoProjectActivities bmoProjectActivities;
		PmProjectActivities pmProjectActivities = new PmProjectActivities(sFParams);
		pmConn.open();
		
		String s = "";
// 		String prac_project = "";	
		String prac_name = "";
		String prac_profile = "";
		String prac_user = "";
		String prac_startDate = "";
		String prac_endDate = "";
		String prac_hEst = "";
		
		
		// Llaves foraneas
		
		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
	
		try{
			pmConn.disableAutoCommit();
			int i = 1;
			
			while (inputData.hasMoreTokens() && i < 20000) {
				BmoProjectStep bmoProjectStep = new BmoProjectStep();
				PmProjectStep pmProjectStep =  new PmProjectStep(sFParams);
				s = inputData.nextToken();
				int prac_profileId = 0;
				int prac_userId = 0;
// 				int prac_proyectId = 0;
				String sql = "";
				errors = "";
				//bmUpdateResult = new BmUpdateResult();
				bmoProjectActivities = new BmoProjectActivities();
				StringTokenizer tabs = new StringTokenizer(s, "\t");
				
// 				prac_project = tabs.nextToken();
    			prac_name = tabs.nextToken();
    			prac_profile = tabs.nextToken();
    			prac_user = tabs.nextToken();    			
    			prac_hEst = tabs.nextToken();
    			//Quitar espacios
//     			prac_project = prac_project.trim();
    			prac_profile = prac_profile.trim();
    			prac_user = prac_user.trim();    			
    			prac_hEst = prac_hEst.trim();
    			
    			prac_startDate = SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat());
//     			if(!prac_project.equals("empty")){
//     				sql = "SELECT spro_projectstepid FROM projectsstep WHERE spro_code LIKE '" + prac_project +"'"	;
//     				pmConn.doFetch(sql);
//     				if(pmConn.next()){
//     					prac_proyectId = pmConn.getInt("spro_projectstepid");
//     				}
//     			}
    			
    			if(!prac_profile.equals("empty")){
    				sql = "SELECT prof_profileid FROM profiles WHERE prof_name LIKE '" + prac_profile + "'"	;
    				pmConn.doFetch(sql);
    				if(pmConn.next()){
    					prac_profileId = pmConn.getInt("prof_profileid");
    				}
    			}
    			
    			if(!prac_user.equals("empty")){
    				sql = "SELECT user_userid FROM users WHERE user_code LIKE '" + prac_user + "'"	;
    				pmConn.doFetch(sql);
    				if(pmConn.next()){
    					prac_userId = pmConn.getInt("user_userid");
    				}
    			}
    			
    			bmoProjectStep = (BmoProjectStep)pmProjectStep.get(projectStepId);
    			
    			bmoProjectActivities.getName().setValue(prac_name);
    			if(prac_profileId > 0)
    				bmoProjectActivities.getProfileId().setValue(prac_profileId);
    			if(prac_userId > 0)
    				bmoProjectActivities.getUserId().setValue(prac_userId);
    			bmoProjectActivities.getStepProjectId().setValue(projectStepId);
    			bmoProjectActivities.getStartDate().setValue(prac_startDate);
    			bmoProjectActivities.getEstimatedHours().setValue(prac_hEst);
    			bmoProjectActivities.getWFlowId().setValue(bmoProjectStep.getWFlowId().toInteger());
    			bmoProjectActivities.getWFlowTypeId().setValue(bmoProjectStep.getWFlowTypeId().toInteger());
    			bmoProjectActivities.getProgress().setValue(0);
    			
    			
    		    pmProjectActivities.save(pmConn, bmoProjectActivities, bmUpdateResult);
    		       		     			

			}
			 if(bmUpdateResult.hasErrors()){
				 errorSave = bmUpdateResult.errorsToString();
			 }else{
				 pmConn.commit();
			 }
			
		}catch (Exception e){
			pmConn.rollback();
			throw new SFException(e.toString());
		}finally{
			pmConn.close();
		}
		response.sendRedirect("prac_batchProjectAct.jsp?action=complete&errorsave=" + errorSave);
      }else if (action.equals("complete") ) { %>
			<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>		
			
				<td colspan="4" align="center" class="reportTitle">
				    &nbsp;
				    <% if (errorSave.equals("")) { %>
						Datos Cargados OK.
					<% } else { %>
						Existen Errores de Carga: <%= errorSave %>
					<% } %>
					<br>
					<a href="?">Reiniciar</a>
				</td>
			</tr>
			</table>
	<% } %>
</body>
</html>