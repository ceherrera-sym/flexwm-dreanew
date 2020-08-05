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
 
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.shared.*"%>
<%@page import="javax.script.*"%>


<%@include file="/inc/login_opt.jsp" %>
<%
	PmConn pmConn = new PmConn(sFParams);

	try {
		// Inicializar variables
		PmProgram pmProgram = new PmProgram(sFParams);
		BmoProgram bmoProgram = new BmoProgram();
		PmProfile pmProfile = new PmProfile(sFParams);
		BmoProfile bmoProfile = new BmoProfile();
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		PmProgramProfile pmProgramProfile = new PmProgramProfile(sFParams);
		PmProfileUser pmProfileUser = new PmProfileUser(sFParams);
		BmUpdateResult bmUpdateResult = new BmUpdateResult();
		String sql = "";
		
		pmConn.open();
%>
		
<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL() %>css/<%= defaultCss %>"> 
</head>

<body>
<p>
	Migración Modulo de Seguridad:
</p>

<p><b>Modulos</b></p>
<%
	sql = "SELECT * FROM modules";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		bmUpdateResult = new BmUpdateResult();
		bmoProgram = new BmoProgram();
		bmoProgram.getCode().setValue(pmConn.getString("modu_code"));
		bmoProgram.getName().setValue(pmConn.getString("modu_name"));
		bmoProgram.getType().setValue(BmoProgram.TYPE_PROGRAM);
		bmoProgram.getMenuId().setValue(pmConn.getInt("modu_menuid"));
		bmoProgram.getMenuIndex().setValue(pmConn.getString("modu_index"));
		if (pmConn.getInt("modu_enablefiles") > 0)  
			bmoProgram.getEnableFiles().setValue(pmConn.getInt("modu_enablefiles"));
		
		if (pmConn.getInt("modu_enableformats") > 0) 
			bmoProgram.getEnableFormats().setValue(pmConn.getInt("modu_enableformats"));
		
		if (pmConn.getInt("modu_enableaudit") > 0) 
			bmoProgram.getEnableAudit().setValue(pmConn.getInt("modu_enableaudit"));
		
		if (pmConn.getInt("modu_enablehelp") > 0) 
			bmoProgram.getEnableHelp().setValue(pmConn.getInt("modu_enablehelp"));
		
		if (pmConn.getInt("modu_enablemobile") > 0) 
			bmoProgram.getEnableMobile().setValue(pmConn.getInt("modu_enablemobile"));

		pmProgram.save(bmoProgram, bmUpdateResult);
		
		%><li>Registro almacenado, errores? <%= bmUpdateResult.errorsToString() %></li><%
	}
%>

<p><b>Perfiles</b></p>
<%
	sql = "SELECT * FROM groups";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		bmUpdateResult = new BmUpdateResult();
		bmoProfile = new BmoProfile();
		bmoProfile.getName().setValue(pmConn.getString("grup_name"));
		bmoProfile.getDescription().setValue(pmConn.getString("grup_description"));
		pmProfile.save(bmoProfile, bmUpdateResult);
		
		%><li>Registro almacenado, errores? <%= bmUpdateResult.errorsToString() %></li><%
	}
%>

<p><b>Accesos a Programa</b></p>
<%
	
	sql = "SELECT modu_moduleid, grup_groupid, modu_code, grup_name, modu_name, sfca_read, sfca_menu, sfca_write, sfca_delete, sfca_print, sfca_disclosure FROM sfcomponentaccess "
		+ "	left join sfcomponents on (sfca_sfcomponentid = sfcm_sfcomponentid) "
		+ "	left join groups on (sfca_groupid = grup_groupid) "
		+ "	right join modules on (modu_sfcomponentid = sfcm_sfcomponentid) ";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		bmUpdateResult = new BmUpdateResult();
		
		try {
			bmoProgram = (BmoProgram)pmProgram.getBy(pmConn.getString("modu_code"), bmoProgram.getCode().getName());
			bmoProfile = (BmoProfile)pmProfile.getBy(pmConn.getString("grup_name"), bmoProfile.getName().getName());		
			
			BmoProgramProfile bmoProgramProfile = new BmoProgramProfile();
			bmoProgramProfile.getProgramId().setValue(bmoProgram.getId());
			bmoProgramProfile.getProfileId().setValue(bmoProfile.getId());
			bmoProgramProfile.getRead().setValue(pmConn.getInt("sfca_read"));
			bmoProgramProfile.getMenu().setValue(pmConn.getInt("sfca_menu"));
			bmoProgramProfile.getWrite().setValue(pmConn.getInt("sfca_write"));
			bmoProgramProfile.getDelete().setValue(pmConn.getInt("sfca_delete"));
			bmoProgramProfile.getPrint().setValue(pmConn.getInt("sfca_print"));
			if (pmConn.getString("sfca_disclosure").equals("A"))
				bmoProgramProfile.getAllData().setValue(1);
			else 
				bmoProgramProfile.getAllData().setValue(0);
			pmProgramProfile.save(bmoProgramProfile, bmUpdateResult);
			
			
			%><li>Registro almacenado, errores? <%= bmUpdateResult.errorsToString() %></li><%
				
		} catch(Exception e) {
			%><li>Exception? <%= e.toString() %></li><%
		}
	}
%>

<p><b>Usuarios de Perfil</b></p>
<%
	sql = "SELECT * FROM usergroups "
		+ " LEFT JOIN groups ON (usgp_groupid = grup_groupid)";
	pmConn.doFetch(sql);
	while (pmConn.next()) {
		bmUpdateResult = new BmUpdateResult();
		
		try { 
			bmoProfile = (BmoProfile)pmProfile.getBy(pmConn.getString("grup_name"), bmoProfile.getName().getName());	
			
			bmoProfileUser = new BmoProfileUser();
			bmoProfileUser.getProfileId().setValue(bmoProfile.getId());
			bmoProfileUser.getUserId().setValue(pmConn.getInt("usgp_userid"));
			pmProfileUser.save(bmoProfileUser, bmUpdateResult);
			
			%><li>Registro almacenado, errores? <%= bmUpdateResult.errorsToString() %></li><%
		} catch(Exception e) {
			%><li>Exception? <%= e.toString() %></li><%
		}
	}
%>

<%		
	
		} catch (Exception e) {
	%>
		<p>Error <%= e.toString() %>...</p>
	<%
	}  finally {
		pmConn.close();
	}
%>  
  </body>
</html>