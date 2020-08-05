<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/imports.jsp" %>
<%@include file="../inc/login.jsp" %>
<%
	String errorLabel = request.getParameter("errorLabel");
	String errorText = request.getParameter("errorText");
	String errorSave = request.getParameter("errorsave");
	String errorException = request.getParameter("errorException");
	String programTitle = "Importacion de Usuarios";
	String programDescription = "Importacion de Usuarios";
	String populateData = "", action = "";
	if (request.getParameter("populateData") != null) populateData = request.getParameter("populateData");
	if (request.getParameter("action") != null) action = request.getParameter("action");
%>
<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<%
	
	if (action.equals("revision")) { 
		PmConn pmConn = new PmConn(sFParams);
		pmConn.open();
		String sql = "";
%>
	<table width="80%" border="0" align="center"  class="">
	<%
		String msg = "";
		String s = "";
		int i = 1;		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		
		String code = "";
		String mail = "";
		String name = "";
		String fatherName = "";
		String motherName = "";
		String birthdate = "";
		String bloodtype = "";
		String phone = "";
		String mobil = "";
		String campany = "";
		String area = "";
		String location = "";
		String parent = "";
		String startdate = "";
		String endDate = "";
		String employeenumber = "";
		String rfc = "";
		String curp = "";
		String nss = "";
		String billable = "";
		String rate = "";
		String description = "";
		String interfas = "";
		String starprogram = "";
		String publicgcalendarid = "";
		String status = "";
		String errors = "";
		
	%>
		
	    <tr>
	        <td colspan="4">  
            	<table cellpadding="0" cellspacing="0" border="1" rules=all width="90%">
            		<tr valign="middle" align="center" class="">
            			<td class="reportHeaderCell">&nbsp;#</td>
            			<td class="reportHeaderCell">Codigo</td>
            			<td class="reportHeaderCell">Email</td>
            			<td class="reportHeaderCell">Nombre</td>
            			<td class="reportHeaderCell">A. Paterno</td>
            			<td class="reportHeaderCell">A. Materno</td>
            			<td class="reportHeaderCell">Fecha de Nac.</td>
            			<td class="reportHeaderCell">Tipo de sangre</td>
            			<td class="reportHeaderCell">Telefono</td>
            			<td class="reportHeaderCell">Movil</td>
            			<td class="reportHeaderCell">Empresa</td>
            			<td class="reportHeaderCell">Departamento</td>
            			<td class="reportHeaderCell">Ubucación</td>
            			<td class="reportHeaderCell">Superior</td>
            			<td class="reportHeaderCell">Fecha Ing</td>
            			<td class="reportHeaderCell">Fecha Baja</td>
            			<td class="reportHeaderCell"># Empleado</td>
            			<td class="reportHeaderCell">RFC</td>
            			<td class="reportHeaderCell">CURP</td>
            			<td class="reportHeaderCell">IMSS</td>
            			<td class="reportHeaderCell">Facturable?</td>
            			<td class="reportHeaderCell">Tarifa Hr </td>
            			<td class="reportHeaderCell">Notas</td>
            			<td class="reportHeaderCell">Interfaz</td>
            			<td class="reportHeaderCell">Programa de inicio</td>
            			<td class="reportHeaderCell">ID Cal. Google </td>
            			<td class="reportHeaderCell">Estatus</td>
            			 <td class="reportHeaderCell">Errores</td>
            		</tr>
      				<%
      					while (inputData.hasMoreTokens() && i < 20000) {
      						errors = "";
      						s = inputData.nextToken();
      	        			StringTokenizer tabs = new StringTokenizer(s, "\t");     
      	        			
      	        			 code = (tabs.nextToken()).trim();
      	        			 mail = (tabs.nextToken()).trim();
      	        			 name = (tabs.nextToken()).trim();
      	        			 fatherName = (tabs.nextToken()).trim();
      	        			 motherName = (tabs.nextToken()).trim();
      	        			 birthdate = (tabs.nextToken()).trim();
      	        			 bloodtype = (tabs.nextToken()).trim();
      	        			 phone = (tabs.nextToken()).trim();
      	        			 mobil = (tabs.nextToken()).trim();
      	        			 campany = (tabs.nextToken()).trim();
      	        			 area = (tabs.nextToken()).trim();
      	        			 location = (tabs.nextToken()).trim();
      	        			 parent = (tabs.nextToken()).trim();
      	        			 startdate = (tabs.nextToken()).trim();
      	        			 endDate = (tabs.nextToken()).trim();
      	        			 employeenumber = (tabs.nextToken()).trim();
      	        			 rfc = (tabs.nextToken()).trim();
      	        			 curp = (tabs.nextToken()).trim();
      	        			 nss = (tabs.nextToken()).trim();
      	        			 billable = (tabs.nextToken()).trim();
      	        			 rate = (tabs.nextToken()).trim();
      	        			 description = (tabs.nextToken()).trim();
      	        			 interfas =  (tabs.nextToken()).trim();
      	        			 starprogram = (tabs.nextToken()).trim();
      	        			 publicgcalendarid = (tabs.nextToken()).trim();
      	        			 status = (tabs.nextToken()).trim();
      	        			
      	        			 if (!campany.equalsIgnoreCase("empty")){
	      	        			 sql = "select comp_companyid from companies where comp_name = '" + campany + "'";
	      	        			 pmConn.doFetch(sql);
	      	        			 if (!pmConn.next()){      	        				
	      	        				 errors += "No se encontro la empresa en el sistema ";
	      	        			 }
      	        			 }
      	        			 sql = "select area_areaid from areas where area_name = '" + area + "'";
      	        			 pmConn.doFetch(sql);
      	        			 if (!pmConn.next()){    
      	        				 if (!errors.equals(""))errors += ", ";
      	        				 errors += "No se encontro el departamento en el sistema ";
      	        			 }
      	        			 
      	        			 sql = "select loct_locationid from locations where loct_name = '" + location + "'";
      	        			 pmConn.doFetch(sql);
      	        			 if (!pmConn.next()){    
//       	        				 if (!errors.equals(""))errors += ", ";
      	        				 errors += "No se encontro la ubicacion en el sistema " + location;
      	        			 }
//       	        			 if (!parent.equalsIgnoreCase("empty")){
// 	      	        			 sql = "select user_userid from users where user_code = '" + parent + "'";
// 	      	        			 pmConn.doFetch(sql);
// 	      	        			 if (!pmConn.next()){    
// 	     	        				 if (!errors.equals(""))errors += ", ";
// 	     	        				 errors += "No se encontro el usuario en el sistema " + parent;
// 	     	        			 }
//       	        			 }
//       	        			 if (!starprogram.equalsIgnoreCase("empty")){
// 	      	        			sql = "select prog_programid from programs where prog_name = '" + starprogram + "'";
// 	     	        			pmConn.doFetch(sql);
// 	     	        			if (!pmConn.next()){    
// 	    	        				if (!errors.equals(""))errors += ", ";
// 	    	        				 errors += "No se encontro el programa en el sistema " + starprogram;
// 	    	        			}
//       	        			 }
      	        			 
      				%>
      				<tr class="reportCellEven" width="100%">
      					<%=HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER)%>    					
    					<%=HtmlUtil.formatReportCell(sFParams, code, BmFieldType.STRING)%>
    					<%=HtmlUtil.formatReportCell(sFParams, mail, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, name, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, fatherName, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, motherName, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, birthdate, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, bloodtype, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, phone, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, mobil, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, campany, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, area, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, location, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, parent, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, startdate, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, endDate, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, employeenumber, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, rfc, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, curp, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, nss, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, billable, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, rate, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, description, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, interfas, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, starprogram, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, publicgcalendarid, BmFieldType.STRING)%>
      					<%=HtmlUtil.formatReportCell(sFParams, status, BmFieldType.STRING)%>
      					 <td><font color="red"><%= errors %></font></td>
      				</tr>
      				<%
      						i++;
      					}
      				%>
            			
            			
            	</table>
            </td>
        </tr>       	
	        
	</table>
	<table cellpadding="0" cellspacing="0" border="0"  width="100%">
		<FORM action="flex_revisionUsers.jsp" method="POST" name="listFilter">	
		<input type="hidden" name="action" value="populate">
		<input type="hidden" name="populateData" value="<%= populateData %>">			
		<tr class="">
		    <td align="center" colspan="4" height="35" valign="middle">
<%-- 		    <% if (errors.equals("")) { %> --%>
		        <input type="submit" value="Cargar Usuarios">
<%-- 		    <% } %>     --%>
		    </td>
		</tr>			    
		</FORM>								
	</table>
<%
		pmConn.close();
	} else if (action.equals("populate")) {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		PmUser pmUser = new PmUser(sFParams);
		PmConn pmConn = new PmConn(sFParams);
		PmConn pmConn2 = new PmConn(sFParams);
		String s = "";
		
		pmConn.open();
		pmConn2.open();
		
		pmConn2.disableAutoCommit();
		
		String code = "";
		String mail = "";
		String name = "";
		String fatherName = "";
		String motherName = "";
		String birthdate = "";
		String bloodtype = "";
		String phone = "";
		String mobil = "";
		String campany = "";
		String area = "";
		String location = "";
		String parent = "";
		String startdate = "";
		String endDate = "";
		String employeenumber = "";
		String rfc = "";
		String curp = "";
		String nss = "";
		String billable = "";
		String rate = "";
		String description = "";
		String interfas = "";
		String starprogram = "";
		String publicgcalendarid = "";
		String status = "";
		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");
		
		
		int i = 1;
		while (inputData.hasMoreTokens() && i < 2000) { 
			String sql = "";
			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");
			
			BmoUser nextBmoUser = new BmoUser();
			
			 code = (tabs.nextToken()).trim();
  			 mail = (tabs.nextToken()).trim();
  			 name = (tabs.nextToken()).trim();
  			 fatherName = (tabs.nextToken()).trim();
  			 motherName = (tabs.nextToken()).trim();
  			 birthdate = (tabs.nextToken()).trim();
  			 bloodtype = (tabs.nextToken()).trim();
  			 phone = (tabs.nextToken()).trim();
  			 mobil = (tabs.nextToken()).trim();
  			 campany = (tabs.nextToken()).trim();
  			 area = (tabs.nextToken()).trim();
  			 location = (tabs.nextToken()).trim();
  			 parent = (tabs.nextToken()).trim();
  			 startdate = (tabs.nextToken()).trim();
  			 endDate = (tabs.nextToken()).trim();
  			 employeenumber = (tabs.nextToken()).trim();
  			 rfc = (tabs.nextToken()).trim();
  			 curp = (tabs.nextToken()).trim();
  			 nss = (tabs.nextToken()).trim();
  			 billable = (tabs.nextToken()).trim();
  			 rate = (tabs.nextToken()).trim();
  			 description = (tabs.nextToken()).trim();
  			 interfas =  (tabs.nextToken()).trim();
  			 starprogram = (tabs.nextToken()).trim();
  			 publicgcalendarid = (tabs.nextToken()).trim();
  			 status = (tabs.nextToken()).trim();
  			 
  			 
  			int companyId = 0;
  			if (!campany.equalsIgnoreCase("empty")){
	  			 sql = "select comp_companyid from companies where comp_name = '" + campany + "'";
	  			 pmConn.doFetch(sql);  			 
	  			 if (pmConn.next()){  
	  				 companyId = pmConn.getInt("comp_companyid");
	  			 }
  			}
  			 
  			 int areaId = 0;
  			 sql = "select area_areaid from areas where area_name = '" + area + "'";
  			 pmConn.doFetch(sql);
  			 if (pmConn.next()){    
  				areaId = pmConn.getInt("area_areaid");
  			 }
  			 int locationId = 0;
  			 sql = "select loct_locationid from locations where loct_name = '" + location + "'";
  			 pmConn.doFetch(sql);
  			 if (pmConn.next()){   
  				locationId = pmConn.getInt("loct_locationid");
  			 }
  			 int parentId = 0;
  			 if (!parent.equalsIgnoreCase("empty")){
      			 sql = "select user_userid from users where user_code = '" + parent + "'";
      			 pmConn2.doFetch(sql);
      			 if (pmConn.next()){    
      				 parentId = pmConn2.getInt("user_userid");
     			 }
  			 }
  			 int programId = 0;
  			 if (!starprogram.equalsIgnoreCase("empty")){
      			sql = "select prog_programid from programs where prog_name = '" + starprogram + "'";
     			pmConn.doFetch(sql);
     			if (pmConn.next()){    
    				programId = pmConn.getInt("prog_programid");
    			}
  			 }					
  			 
  			pmConn.doFetch("select user_userid from users where user_code = '" + code + "' OR user_email = '" + mail +  "'");  		
  			 
  			if (!pmConn.next()){
  				
		  			nextBmoUser.getCode().setValue(code);
		  			nextBmoUser.getEmail().setValue(mail);
		  			nextBmoUser.getFirstname().setValue(name);
		  			nextBmoUser.getFatherlastname().setValue(fatherName);
		  			nextBmoUser.getMotherlastname().setValue(motherName);
		  			if (!birthdate.equalsIgnoreCase("empty"))
		  			 nextBmoUser.getBirthdate().setValue(birthdate);
		  			if (!bloodtype.equalsIgnoreCase("empty"))
		  			 nextBmoUser.getBloodType().setValue(bloodtype);
		  			if (phone.equalsIgnoreCase("empty"))
		  				nextBmoUser.getPhone().setValue(phone);
		  			if (mobil.equalsIgnoreCase("empty"))
		  				nextBmoUser.getMobile().setValue(mobil);
		  			if (companyId > 0)
		  				nextBmoUser.getCompanyId().setValue(companyId);
		  			nextBmoUser.getAreaId().setValue(areaId);
		  			nextBmoUser.getLocationId().setValue(locationId);
		  			nextBmoUser.getParentId().setValue(parentId);
		  			if (!startdate.equalsIgnoreCase("empty"))
						nextBmoUser.getStartDate().setValue(startdate);
		  			if (!endDate.equalsIgnoreCase("empty"))
						nextBmoUser.getEndDate().setValue(endDate);
		  			if (!employeenumber.equalsIgnoreCase("empty"))
		  				nextBmoUser.getEmployeeNumber().setValue(employeenumber);
		  			if (!rfc.equalsIgnoreCase("empty"))
		  				nextBmoUser.getRfc().setValue(rfc);
		  			if (!curp.equalsIgnoreCase("empty"))
		  				nextBmoUser.getCurp().setValue(curp);
		  			if (!nss.equalsIgnoreCase("empty"))
		  				nextBmoUser.getSocialNumber().setValue(nss);
		  			if (billable.equalsIgnoreCase("empty"))
		  				billable = "0";
		  			nextBmoUser.getBillable().setValue(billable);
		  			
		  			if (!rate.equalsIgnoreCase("empty"))
		  				nextBmoUser.getRate().setValue(rate);
		  			if (programId > 0)
		  				nextBmoUser.getStartProgramId().setValue(programId);
		  			
		  			if (!publicgcalendarid.equalsIgnoreCase("empty"))
		  				nextBmoUser.getPublicGCalendarId().setValue(publicgcalendarid);
		  			
		  			nextBmoUser.getStatus().setValue(status);
		  			
		  			pmUser.save(pmConn2, nextBmoUser, bmUpdateResult);
  			}
  			 i++;
		}
		
		if (!bmUpdateResult.hasErrors())
			pmConn2.commit();
		
		response.sendRedirect(sFParams.getAppURL() + "batch/flex_revisionUsers.jsp?action=complete&errorsave=" + bmUpdateResult.errorsToString());
%>
	
<%	
		pmConn.close();
		pmConn2.close();
	} else if (action.equals("complete")) {
%>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	<tr>		
		<td colspan="4" align="center" class="reportTitle">
		    &nbsp;La Carga esta completa
		    <%= errorSave %>
		</td>
	</tr>
	</table>

<%
	}
%>