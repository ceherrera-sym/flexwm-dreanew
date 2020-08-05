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
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="javax.script.*"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerEmail"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerPhone"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerDate"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerRelative"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerAddress"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerCompany"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerSocial"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerContact"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerWeb"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerBankAccount"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerPaymentType"%>
<%@page import="com.flexwm.shared.cm.BmoCustomerNote"%>
<%@page import="com.symgae.shared.sf.BmoTag"%>
<%@page import="com.symgae.server.sf.PmTag"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.shared.SQLUtil" %>
<%@include file="/inc/login.jsp"%>
<%
	// Inicializar variables
	String title = "Reporte de Clientes Detallado";
	Locale locale = new Locale("es", "MX");	

	String sql = "", where = "", filters = "", birthdate = "", birthdateEnd = "";
	String dateNow = "", status = "", customerType = "", startDate = "", endDate = "";
	int programId = 0, customerId = 0, salesman = 0, referralId = 0, territoryId = 0, countOrderTypes = 0,
			industryId = 0, regionId = 0, birthdateByMonth = 0, maritalStatus = 0;

	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram = new PmProgram(sFParams);
	
	BmoCustomer bmoCustomer = new BmoCustomer();
	
	// Obtener parametros
	if (request.getParameter("programId") != null)
		programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("cust_customerid") != null)
		customerId = Integer.parseInt(request.getParameter("cust_customerid"));
	if (request.getParameter("cust_customertype") != null)
		customerType = request.getParameter("cust_customertype");
	if (request.getParameter("cust_salesmanid") != null)
		salesman = Integer.parseInt(request.getParameter("cust_salesmanid"));
	if (request.getParameter("cust_referralid") != null)
		referralId = Integer.parseInt(request.getParameter("cust_referralid"));
	if (request.getParameter("cust_territoryid") != null)
		territoryId = Integer.parseInt(request.getParameter("cust_territoryid"));
    if (request.getParameter("cust_maritalstatusid") != null)
    	maritalStatus = Integer.parseInt(request.getParameter("cust_maritalstatusid"));

	if (request.getParameter("cust_status") != null)
		status = request.getParameter("cust_status");
	if (request.getParameter("cust_industryid") != null)
		industryId = Integer.parseInt(request.getParameter("cust_industryid"));
	if (request.getParameter("cust_regionid") != null)
		regionId = Integer.parseInt(request.getParameter("cust_regionid"));
	if (request.getParameter("birthdateByMonth") != null)
		birthdateByMonth = Integer.parseInt(request.getParameter("birthdateByMonth"));
	if (request.getParameter("cust_birthdate") != null)
		birthdate = request.getParameter("cust_birthdate");
	if (request.getParameter("birthdateEnd") != null)
		birthdateEnd = request.getParameter("birthdateEnd");
	if (request.getParameter("cust_datecreate") != null)
		startDate = request.getParameter("cust_datecreate");
	if (request.getParameter("dateCreateEnd") != null)
		endDate = request.getParameter("dateCreateEnd");
    
	//Modulo principal de Reportes
    bmoProgram = (BmoProgram)pmProgram.get(programId);
	
	// Filtros listados
	if (customerId > 0) {
		where += " AND cust_customerid = " + customerId;
		filters += "<i>Cliente/Prospecto: </i>" + request.getParameter("cust_customeridLabel") + ", ";
	}

	if (!customerType.equals("")) {
		where += " AND cust_customertype = '" + customerType + "' ";
		filters += "<i>Tipo de Cliente: </i>" + request.getParameter("cust_customertypeLabel") + ", ";
	/*	where += SFServerUtil.parseFiltersToSql("cust_customertype", customerType);
		filters += "<i>" + HtmlUtil.stringToHtml(
				sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCustomertype())) +": </i>" +
				request.getParameter("cust_customertypeLabel")+ ", ";*/
	}

	if (salesman > 0) {
		where += " AND cust_salesmanid = " + salesman;
		filters += "<i>Vendedor: </i>" + request.getParameter("cust_salesmanidLabel") + ", ";
	}

	if (referralId > 0) {
		where += " AND cust_referralid = " + referralId;
		filters += "<i>Referencia: </i>" + request.getParameter("cust_referralidLabel") + ", ";
	}

	if (territoryId > 0) {
		where += " AND cust_territoryid = " + territoryId;
		filters += "<i>Territorio: </i>" + request.getParameter("cust_territoryidLabel") + ", ";
	}

	 if (maritalStatus > 0 ) {
	   		where += " AND cust_maritalstatusid = '" + maritalStatus + "' ";
	   		//filters += "<i>Estado Civil: </i>" + request.getParameter("cust_maritalstatusLabel") + ", ";
	   		where += " AND cust_maritalstatusid = " + maritalStatus;
	   		filters += "<i>" + HtmlUtil.stringToHtml(
					sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getMaritalStatusId())) + ": </i>" +
					request.getParameter("cust_maritalstatusidLabel") + ", ";
	  } 
  
	if (industryId > 0) {
		where += " AND cust_industryid = " + industryId;
		filters += "<i>SIC: </i>" + request.getParameter("cust_industryidLabel") + ", ";
	}

	if (regionId > 0) {
		where += " AND cust_regionid = " + regionId;
		filters += "<i>Regi&oacute;n: </i>" + request.getParameter("cust_regionidLabel") + ", ";
	}

	if (!status.equals("")) {
		where += SFServerUtil.parseFiltersToSql("cust_status", status);
		filters += "<i>Estatus: </i>" + request.getParameter("cust_statusLabel") + ", ";
	}

	if (sFParams.getSelectedCompanyId() > 0)
		filters += "<i>Empresa: </i>" + sFParams.getBmoSelectedCompany().getName().toString() + " | "
				+ sFParams.getBmoSelectedCompany().getName().toString() + ", ";
		if (!startDate.equals("")) {
	    	where += " AND cust_datecreate >= '" + startDate + " 00:00'";
	    	filters += "<i>Inicio Fecha Creación: </i>" + startDate + ", ";
	    }
	    
	    if (!endDate.equals("")) {
	    	where += " AND cust_datecreate <= '" + endDate + " 23:59'";
	    	filters += "<i>Fin Fecha Creación: </i>" + endDate + ", ";
	    }
	    
	// Para clientes por mes de fecha de nacimiento
	if (sFParams.isFieldEnabled(bmoCustomer.getBirthdate())) {
		if (birthdateByMonth == 1) {
			filters += "<i>Fecha Nac. por mes: </i>" + request.getParameter("birthdateByMonthLabel") + ", ";
			if (!birthdate.equals("")) {
				where += " AND DATE_FORMAT(cust_birthdate, '%m-%d') >= '" + birthdate.substring(5, 10) + "'";
				filters += "<i>F. Nacimiento: </i>" + request.getParameter("cust_birthdate") + ", ";
			}
	
			if (!birthdateEnd.equals("")) {
				where += " AND DATE_FORMAT(cust_birthdate, '%m-%d') <= '" + birthdateEnd.substring(5, 10) + "'";
				filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
			}
		} else {
			filters += "<i>Fecha Nac. por mes: </i>" + request.getParameter("birthdateByMonthLabel") + ", ";
			if (!birthdate.equals("")) {
				where += " AND cust_birthdate >= '" + birthdate + "'";
				filters += "<i>F. Nacimiento: </i>" + request.getParameter("cust_birthdate") + ", ";
			}
	
			if (!birthdateEnd.equals("")) {
				where += " AND cust_birthdate <= '" + birthdateEnd + "'";
				filters += "<i>F. Nacimiento Fin: </i>" + request.getParameter("birthdateEnd") + ", ";
			}
		}
	}

	// Obtener disclosure de datos
	String disclosureFilters = new PmCustomer(sFParams).getDisclosureFilters();
	if (disclosureFilters.length() > 0)
		where += " AND " + disclosureFilters;
	 //abro conexion para inciar el conteo
    PmConn pmConnCount= new PmConn(sFParams);
    pmConnCount.open();
    
    sql = " SELECT COUNT(*) as contador FROM  " + SQLUtil.formatKind(sFParams, " customers") 
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on(user_userid = cust_salesmanid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" on(titl_titleid = cust_titleid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" on(terr_territoryid = cust_territoryid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" on(cose_consultingserviceid = cust_consultingserviceid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" on(regi_regionid = cust_regionid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" on(refe_referralid = cust_referralid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on(cure_currencyid = cust_currencyid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" on(indu_industryid = cust_industryid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) "
	+ " WHERE cust_customerid > 0 " + where
	+ " ORDER BY cust_customerid, cust_firstname, cust_fatherlastname, cust_motherlastname ";
	int count =0;
	//ejecuto el sql
	pmConnCount.doFetch(sql);
	if(pmConnCount.next())
		count=pmConnCount.getInt("contador");
	System.out.println("contador de reportes    "+count);
	sql = "SELECT * " + " FROM " + SQLUtil.formatKind(sFParams, " customers") 
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on(user_userid = cust_salesmanid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" on(titl_titleid = cust_titleid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" on(terr_territoryid = cust_territoryid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" on(cose_consultingserviceid = cust_consultingserviceid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" on(regi_regionid = cust_regionid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" on(refe_referralid = cust_referralid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on(cure_currencyid = cust_currencyid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" on(indu_industryid = cust_industryid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) "
	+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " maritalstatus")+" ON (mast_maritalstatusid = cust_maritalstatusid) " 
	+ " WHERE cust_customerid > 0 " + where
	+ " ORDER BY cust_customerid, cust_firstname, cust_fatherlastname, cust_motherlastname ";

//System.out.println("sql: "+sql);


	//System.out.println("sql: "+sql);
	//Conexiones
	PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	pmConn.doFetch(sql);

	PmConn pmConnAdic = new PmConn(sFParams);
	pmConnAdic.open();

%>

<html>

<%
	// Imprimir
	String print = "0", permissionPrint = "";
	if ((String) request.getParameter("print") != null)
		print = (String) request.getParameter("print");

	// Exportar a Excel
	String exportExcel = "0";
	if ((String) request.getParameter("exportexcel") != null)
		exportExcel = (String) request.getParameter("exportexcel");
	if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=\"" + title + ".xls\"");
	}

	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menÃº(clic-derecho).
	//En caso de que mande a imprimir, deshabilita contenido
	if (!(sFParams.hasPrint(bmoProgram.getCode().toString()))) {
%>
<style>
body {
	user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	-webkit-user-select: none;
	-ie-user-select: none;
	-khtml-user-select: none;
	-ms-user-select: none;
	-webkit-touch-callout: none
}
</style>
<style type="text/css" media="print">
* {
	display: none;
}
</style>
<%
	permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
		//Mensaje 
		if (print.equals("1") || exportExcel.equals("1")) {
%>
<script>
	alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
</script>
<%
	}
	}

	//No cargar datos en caso de que se imprima/exporte y no tenga permisos
	if (sFParams.hasPrint(bmoProgram.getCode().toString()) || !(exportExcel.equals("1"))) {
%>
<head>
<title>:::<%=title%>:::
</title>
	<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>">

</head>

<body class="default" <%=permissionPrint%> style="padding-right: 10px">

	<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="<%=sFParams.getMainImageUrl()%>"></td>
			<td class="reportTitle" align="left" colspan="2"><%=title%></td>
		</tr>
		<tr>
			<td class="reportSubTitle"><b>Filtros:</b> <%=filters%> <br>
			<b>Ordenado por:</b> Clave del Cliente
			</td>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>
				por: <%=sFParams.getLoginInfo().getEmailAddress()%>
			</td>
		</tr>
	</table>
	<br>
	<%
	//if que muestra el mensajede error
	if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
		%>
		
<%=messageTooLargeList %>
		<% 
	}else{
		sql = "SELECT * " + " FROM " + SQLUtil.formatKind(sFParams, " customers") 
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" on(user_userid = cust_salesmanid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " titles")+" on(titl_titleid = cust_titleid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " territories")+" on(terr_territoryid = cust_territoryid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " consultingservices")+" on(cose_consultingserviceid = cust_consultingserviceid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " regions")+" on(regi_regionid = cust_regionid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" on(refe_referralid = cust_referralid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" on(cure_currencyid = cust_currencyid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" on(indu_industryid = cust_industryid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " reqpaytypes")+" ON (rqpt_reqpaytypeid = cust_reqpaytypeid) "
		+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " maritalstatus")+" ON (mast_maritalstatusid = cust_maritalstatusid) " 
		+ " WHERE cust_customerid > 0 " + where
		+ " ORDER BY cust_customerid, cust_firstname, cust_fatherlastname, cust_motherlastname ";
		pmConn.doFetch(sql);
		int i = 1;
			BmoTag bmoTag = new BmoTag();
			PmTag pmTag = new PmTag(sFParams);

			BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
			BmoCustomerPhone bmoCustomerPhone = new BmoCustomerPhone();
			BmoCustomerDate bmoCustomerDate = new BmoCustomerDate();
			BmoCustomerRelative bmoCustomerRelative = new BmoCustomerRelative();
			BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
			BmoCustomerCompany bmoCustomerCompany = new BmoCustomerCompany();
			BmoCustomerSocial bmoCustomerSocial = new BmoCustomerSocial();
			BmoCustomerContact bmoCustomerContact = new BmoCustomerContact();
			BmoCustomerWeb bmoCustomerWeb = new BmoCustomerWeb();
			BmoCustomerBankAccount bmoCustomerBankAccount = new BmoCustomerBankAccount();
			BmoCustomerPaymentType bmoCustomerPaymentType = new BmoCustomerPaymentType();
			BmoCustomerNote bmoCustomerNote = new BmoCustomerNote();

			// TamaÃ±o dinamico de las tablas de los tabs adicionales
			// Los valores son porque dentro tr-td hay de 1 a 3 tablas.
			// (Ej. si no tienes permiso de ver el modulo o si quitas un modulo(emails) se ajusta el tamaÃ±o de las otras 2 tablas a 50%-50%, 
			// si quitas dos modulos queda una tabla de 100%,
			// si existen los 3 modulos queda de 33%)

			// emails-tel-fecha
			boolean tab1Visible = true;
			String size1Tab = "";
			if (sFParams.hasRead(new BmoCustomerEmail().getProgramCode())
					|| sFParams.hasRead(new BmoCustomerPhone().getProgramCode())
					|| sFParams.hasRead(new BmoCustomerDate().getProgramCode())) {
				size1Tab = "100%";

				if ((sFParams.hasRead(new BmoCustomerEmail().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerPhone().getProgramCode()))
						|| (sFParams.hasRead(new BmoCustomerEmail().getProgramCode())
								&& sFParams.hasRead(new BmoCustomerDate().getProgramCode()))
						|| (sFParams.hasRead(new BmoCustomerPhone().getProgramCode())
								&& sFParams.hasRead(new BmoCustomerDate().getProgramCode()))) {
					size1Tab = "50%";
				}

				if (sFParams.hasRead(new BmoCustomerEmail().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerPhone().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerDate().getProgramCode())) {
					size1Tab = "33%";
				}
			} else {
				tab1Visible = false;
			}

			// Direcciones
			boolean tab2Visible = true;
			String size2Tab = "";
			if (sFParams.hasRead(new BmoCustomerAddress().getProgramCode())) {
				size2Tab = "100%";
			} else {
				tab2Visible = false;
			}

			// Familiares
			boolean tab3Visible = true;
			String size3Tab = "";
			if (sFParams.hasRead(new BmoCustomerRelative().getProgramCode())) {
				size3Tab = "100%";
			} else {
				tab3Visible = false;
			}

			// Contactos
			boolean tab4Visible = true;
			String size4Tab = "";
			if (sFParams.hasRead(new BmoCustomerContact().getProgramCode())) {
				size4Tab = "100%";
			} else {
				tab4Visible = false;
			}

			// redes-sitios-empresas
			boolean tab5Visible = true;
			String size5Tab = "";
			if (sFParams.hasRead(new BmoCustomerSocial().getProgramCode())
					|| sFParams.hasRead(new BmoCustomerWeb().getProgramCode())
					|| sFParams.hasRead(new BmoCustomerCompany().getProgramCode())) {
				size5Tab = "100%";

				if ((sFParams.hasRead(new BmoCustomerSocial().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerWeb().getProgramCode()))
						|| (sFParams.hasRead(new BmoCustomerSocial().getProgramCode())
								&& sFParams.hasRead(new BmoCustomerCompany().getProgramCode()))
						|| (sFParams.hasRead(new BmoCustomerWeb().getProgramCode())
								&& sFParams.hasRead(new BmoCustomerCompany().getProgramCode()))) {
					size5Tab = "50%";
				}

				if (sFParams.hasRead(new BmoCustomerSocial().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerWeb().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerCompany().getProgramCode())) {
					size5Tab = "33%";
				}
			} else {
				tab5Visible = false;
			}

			// bancos-metodos-notas
			boolean tab6Visible = true;
			String size6Tab = "";
			if (sFParams.hasRead(new BmoCustomerBankAccount().getProgramCode())
					|| sFParams.hasRead(new BmoCustomerPaymentType().getProgramCode())
					|| sFParams.hasRead(new BmoCustomerNote().getProgramCode())) {
				size6Tab = "100%";

				if ((sFParams.hasRead(new BmoCustomerBankAccount().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerPaymentType().getProgramCode()))
						|| (sFParams.hasRead(new BmoCustomerBankAccount().getProgramCode())
								&& sFParams.hasRead(new BmoCustomerNote().getProgramCode()))
						|| (sFParams.hasRead(new BmoCustomerPaymentType().getProgramCode())
								&& sFParams.hasRead(new BmoCustomerNote().getProgramCode()))) {
					size6Tab = "50%";
				}

				if (sFParams.hasRead(new BmoCustomerBankAccount().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerPaymentType().getProgramCode())
						&& sFParams.hasRead(new BmoCustomerNote().getProgramCode())) {
					size6Tab = "33%";
				}
			} else {
				tab6Visible = false;
			}

			while (pmConn.next()) {
				bmoCustomer.getStatus().setValue(pmConn.getString("customers", "cust_status"));
				bmoCustomer.getCustomertype().setValue(pmConn.getString("customers", "cust_customertype"));
				bmoCustomer.getMaritalStatusId().setValue(pmConn.getString("customers", "cust_maritalstatusid"));
				bmoCustomer.getLogo().setValue(pmConn.getString("customers", "cust_logo"));
				
				String sqlAdic = "";
				
				String blobKeyParse = HtmlUtil.parseImageLink(sFParams, bmoCustomer.getLogo());
				String blobKeyParseError =  GwtUtil.getProperUrl(sFParams, "/icons/" + bmoProgram.getCode().toString().toLowerCase() + ".png");

// 				if (pmConn.getString("customers", "cust_logo").length() > 0) {
// 					blobKeyParse = pmConn.getString("customers", "cust_logo");
// 					if (pmConn.getString("customers", "cust_logo").indexOf(".") > 0)
// 						blobKeyParse = pmConn.getString("customers", "cust_logo").substring(0,
// 								pmConn.getString("customers", "cust_logo").indexOf("."));
// 				}
	%>
	<table border="0" cellspacing="0" width="100%" cellpadding="0"
		class="report">
		<tr>
			<td align="left" width="10" rowspan="23" valign="top"><img
				border="0" width="100" height="100"
				src="<%=blobKeyParse%>"  onerror="this.src='<%= blobKeyParseError%>'"></td>
			<td colspan="3" class="reportHeaderCell">#<%=i%> - <%=HtmlUtil.stringToHtml(pmConn.getString("customers", "cust_code"))%>
				<%=HtmlUtil.stringToHtml(pmConn.getString("customers", "cust_displayname"))%>
			</td>
			<td class="reportHeaderCell"
				style="text-align: right; font-size: 7pt;">
				<%
					if (pmConn.getString("customers", "cust_datecreate").length() > 0) {
				%>
				<span
				title='Creado: <%=pmConn.getString("customers", "cust_datecreate")%>'>
					Creado: <%=pmConn.getString("customers", "cust_datecreate").substring(0, 10)%>
			</span> <%
 	}
 %> &nbsp;
			</td>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" colspan="4">Datos
				Generales</th>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText">Raz&oacute;n Social:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_legalname"), BmFieldType.STRING))%>
			<th class="reportCellText">Tipo Cliente:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							bmoCustomer.getCustomertype().getSelectedOption().getLabeltoHtml(), BmFieldType.OPTIONS))%>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText">RFC:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_rfc"), BmFieldType.RFC))%>
			<th class="reportCellText">Fecha de Creaci&oacute;n:</th>
			<%=HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_establishmentdate"), BmFieldType.DATE)%>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" colspan="4">Datos
				Contacto</th>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText"><%=HtmlUtil.stringToHtml(
							sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getTitleId()))%>:
			</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("titles", "titl_name"), BmFieldType.STRING))%>
			<th class="reportCellText">Nombre:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_firstname"), BmFieldType.STRING))%>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText">Apellido Paterno:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_fatherlastname"), BmFieldType.STRING))%>
			<th class="reportCellText">Apelido Materno:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_motherlastname"), BmFieldType.STRING))%>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText">Tel&eacute;fono:
			</th>
			<%=HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("customers", "cust_phone"),
							BmFieldType.PHONE)%>
			<th class="reportCellText">Email:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_email"), BmFieldType.EMAIL))%>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText">M&oacute;vil:</th>
			<%=HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("customers", "cust_mobile"),
							BmFieldType.PHONE)%>
			<th class="reportCellText">Cargo:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							"" + pmConn.getString("customers", "cust_position"), BmFieldType.STRING))%>
		</tr>
		<tr>
			<th class="reportCellEven" align="left" colspan="4">Datos
				Comerciales</th>
		</tr>
		<tr class="reportCellEven">
			<th class="reportCellText">Vendedor:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
							pmConn.getString("users", "user_code"), BmFieldType.CODE))%>

			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getCurrencyId())) {
			%>
			<th class="reportCellText">Moneda Pred.:</th>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
												pmConn.getString("currencies", "cure_code") + " - "
														+ pmConn.getString("currencies", "cure_name"),
												BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getTerritoryId())
							|| sFParams.isFieldEnabled(bmoCustomer.getRegionId())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getTerritoryId())) {
			%>
			<th class="reportCellText">Territorio Infor.:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("territories", "terr_name"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getRegionId())) {
			%>
			<th class="reportCellText">Regi&oacute;n:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("regions", "regi_name"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getReferralId())
							|| sFParams.isFieldEnabled(bmoCustomer.getReferralComments())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getReferralId())) {
			%>
			<th class="reportCellText">Referencia:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("referrals", "refe_name"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getReferralComments())) {
			%>
			<th class="reportCellText">Notas Ref.:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_referralcomments"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getRecommendedBy())
							|| sFParams.isFieldEnabled(bmoCustomer.getIndustryId())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getRecommendedBy())) {
			%>
			<th class="reportCellText">Recomendado Por:</th>
			<%
				String recommendedBy = "";
								sqlAdic = "SELECT cust_displayname FROM " + SQLUtil.formatKind(sFParams, " customers")+" WHERE cust_customerid = "
										+ pmConn.getInt("customers", "cust_recommendedby");
								pmConnAdic.doFetch(sqlAdic);
								if (pmConnAdic.next())
									recommendedBy = pmConnAdic.getString("cust_displayname");
			%>
			<%=HtmlUtil.stringToHtml(
									HtmlUtil.formatReportCell(sFParams, "" + recommendedBy, BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>

			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getIndustryId())) {
			%>
			<th class="reportCellText"><%=HtmlUtil.stringToHtml(HtmlUtil.stringToHtml(sFParams
									.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getIndustryId())))%>:
			</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("industries", "indu_name"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getTags())) {
			%>
			<th class="reportCellText">Tags:</th>
			<td class="reportCellText">
				<%
					String tagList = pmConn.getString("customers", "cust_tags");
					if (!pmConn.getString("customers", "cust_tags").equals("")) {
						String[] split = tagList.split("\\:");
						for (int y = 0; y < split.length; y++) {
							String tagId = split[y];
							if (!tagId.equals("")) {
								bmoTag = (BmoTag) pmTag.get(Integer.parseInt(tagId));
								%> 
								<%=HtmlUtil.stringToHtml(bmoTag.getCode().toString())%>|
								<%
				 			}
							}
						}
 %>
			</td>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>

			<th class="reportCellText">Estatus:</th>
			<%= HtmlUtil.formatReportCell(sFParams, HtmlUtil.stringToHtml(bmoCustomer.getStatus().getSelectedOption().getLabeltoHtml()), BmFieldType.OPTIONS)
		%>
		</tr>

		<tr>
			<th class="reportCellEven" align="left" colspan="4">Datos
				Adicionales</th>
		</tr>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getCurp())
							|| sFParams.isFieldEnabled(bmoCustomer.getNss())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getCurp())) {
			%>
			<th class="reportCellText"><%=HtmlUtil.stringToHtml(
									sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getCurp()))%>:
			</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_curp"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getNss())) {
			%>
			<th class="reportCellText"><%=HtmlUtil.stringToHtml(
									sFParams.getFieldFormTitle(bmoProgram.getCode().toString(), bmoCustomer.getNss()))%>:
			</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_nss"), BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getBirthdate())
							|| sFParams.isFieldEnabled(bmoCustomer.getMaritalStatusId())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getBirthdate())) {
			%>
			<th class="reportCellText">Fecha de Nacimiento:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_birthdate"), BmFieldType.DATE))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getMaritalStatusId())) {
			%>
			<th class="reportCellText">Estado Civil:</th>
			<%= HtmlUtil.formatReportCell(sFParams, "" + pmConn.getString("mast_name"), BmFieldType.STRING) %>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getParentId())
							|| sFParams.isFieldEnabled(bmoCustomer.getIncome())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getParentId())) {
			%>
			<th class="reportCellText">Empleador:</th>
			<%
				sqlAdic = "";
								String parent = "";
								sqlAdic = "SELECT cust_code, cust_displayname FROM " + SQLUtil.formatKind(sFParams, " customers")+" WHERE cust_customerid = "
										+ pmConn.getInt("customers", "cust_parentid");
								pmConnAdic.doFetch(sqlAdic);
								if (pmConnAdic.next())
									parent = pmConnAdic.getString("cust_code") + " "
											+ pmConnAdic.getString("cust_displayname");
			%>
			<%=HtmlUtil.stringToHtml(
									HtmlUtil.formatReportCell(sFParams, "" + parent, BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getIncome())) {
			%>
			<th class="reportCellText">Ingresos:</th>
			<%=HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_income"), BmFieldType.CURRENCY,
									"reportCellText")%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getConsultingServiceId())
							|| sFParams.isFieldEnabled(bmoCustomer.getRating())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getConsultingServiceId())) {
			%>
			<th class="reportCellText">Servicios Por:</th>
			<%
				sqlAdic = "";
								String service = "";
								sqlAdic = "SELECT cose_name FROM " + SQLUtil.formatKind(sFParams, " consultingservices")+" WHERE cose_consultingserviceid= "
										+ pmConn.getInt("customers", "cust_consultingserviceid");
								pmConnAdic.doFetch(sqlAdic);
								if (pmConnAdic.next())
									service = pmConnAdic.getString("cose_name");
			%>
			<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, service, BmFieldType.STRING))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getRating())) {
			%>
			<th class="reportCellText">Rating:</th>
			<%=HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_rating"), BmFieldType.STRING)%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getCreditLimit())
							|| sFParams.isFieldEnabled(bmoCustomer.getWww())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getCreditLimit())) {
			%>
			<th class="reportCellText">L&iacute;mite de Cr&eacute;dito:</th>
			<%=HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_creditlimit"), BmFieldType.CURRENCY,
									"reportCellText")%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getWww())) {
			%>
			<th class="reportCellText">Sitio Web:</th>
			<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
									"" + pmConn.getString("customers", "cust_www"), BmFieldType.WWW))%>
			<%
				} else {
			%>
			<td colspan="2">&nbsp;</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		<%
			if (sFParams.isFieldEnabled(bmoCustomer.getReqPayTypeId())) {
		%>
		<tr class="reportCellEven">
			<%
				if (sFParams.isFieldEnabled(bmoCustomer.getReqPayTypeId())) {
			%>
			<th class="reportCellText">T&eacute;rmino de Pago:</th>
			<td class="reportCellText" colspan="3"><%=HtmlUtil.stringToHtml(pmConn.getString("reqpaytypes", "rqpt_name"))%></td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
	</table>
	<br>
	<!-- 
				// Tabs adicionales
			-->

	<table class="" border="0" align="left" width="100%"
		style="font-size: 12px">
		<%
			if (tab1Visible) {
		%>
		<tr>
			<td>
				<%
					if (sFParams.hasRead(new BmoCustomerEmail().getProgramCode())) {
				%>
				<table class="" border="0" align="left" width="<%=size1Tab%>"
					style="font-size: 12px">
					<tr>
						<th class="reportHeaderCell" colspan="2">Emails</th>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">Email</th>
					</tr>

					<%
						// Emails
										String emails = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customeremails")+" WHERE cuem_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(emails);
										while (pmConnAdic.next()) {
											bmoCustomerEmail.getType()
													.setValue(pmConnAdic.getString("customeremails", "cuem_type"));
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										bmoCustomerEmail.getType().getSelectedOption().getLabeltoHtml(),
										BmFieldType.OPTIONS))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customeremails", "cuem_email"), BmFieldType.EMAIL))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %> <%
 	if (sFParams.hasRead(new BmoCustomerPhone().getProgramCode())) {
 %>
				<table class="" border="0" align="left" width="<%=size1Tab%>"
					style="font-size: 12px">
					<tr>
						<th class="reportHeaderCell" colspan="3">Tel&eacute;fonos</th>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">N&uacute;m.</th>
						<th class="reportCellEven" align="left">Ext.</th>
					</tr>

					<%
						// Telefonos
										String phones = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerphones")+" WHERE cuph_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(phones);
										while (pmConnAdic.next()) {
											bmoCustomerPhone.getType()
													.setValue(pmConnAdic.getString("customerphones", "cuph_type"));
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										bmoCustomerPhone.getType().getSelectedOption().getLabeltoHtml(),
										BmFieldType.OPTIONS))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerphones", "cuph_number"), BmFieldType.PHONE)%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerphones", "cuph_extension"),
										BmFieldType.STRING)%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %> <%
 	if (sFParams.hasRead(new BmoCustomerDate().getProgramCode())) {
 %>
				<table class="" border="0" align="left" width="<%=size1Tab%>"
					style="font-size: 12px">
					<tr>
						<th class="reportHeaderCell" colspan="5">Fechas</th>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">Fecha</th>
						<th class="reportCellEven" align="left">Nota</th>
						<th class="reportCellEven" align="left">Notif./D&iacute;as
							antes</th>
					</tr>

					<%
						// Fechas
										String dates = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerdates")+" WHERE cuda_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(dates);
										while (pmConnAdic.next()) {
											bmoCustomerDate.getType().setValue(pmConnAdic.getString("customerdates", "cuda_type"));
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										bmoCustomerDate.getType().getSelectedOption().getLabeltoHtml(),
										BmFieldType.OPTIONS))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerdates", "cuda_relevantdate"),
										BmFieldType.DATE)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerdates", "cuda_description"),
										BmFieldType.STRING))%>

						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										((pmConnAdic.getInt("cuda_emailreminder") > 0)
												? "Si / " + pmConnAdic.getString("customerdates", "cuda_reminddate")
												: "No"),
										BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			}
		%>
		<%
			if (tab2Visible) {
		%>
		<tr>
			<td>
				<%
					if (sFParams.hasRead(new BmoCustomerAddress().getProgramCode())) {
				%>
				<table class="" border="0" align="left" width="<%=size2Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="7">Direcciones</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">Calle</th>
						<th class="reportCellEven" align="left">No.</th>
						<th class="reportCellEven" align="left">Colonia</th>
						<th class="reportCellEven" align="left">C.P.</th>
						<th class="reportCellEven" align="left">Ciudad</th>
						<th class="reportCellEven" align="left">Descripci&oacute;n</th>
					</tr>

					<%
						// Direcciones
										String address = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customeraddress ")
												+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " cities")+" ON (city_cityid = cuad_cityid)" + "WHERE cuad_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(address);
										while (pmConnAdic.next()) {
											bmoCustomerAddress.getType()
													.setValue(pmConnAdic.getString("customeraddress", "cuad_type"));
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										bmoCustomerAddress.getType().getSelectedOption().getLabeltoHtml(),
										BmFieldType.OPTIONS))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customeraddress", "cuad_street"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customeraddress", "cuad_number"),
										BmFieldType.STRING)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customeraddress", "cuad_neighborhood"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customeraddress", "cuad_zip"), BmFieldType.STRING)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("cities", "city_name"), BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customeraddress", "cuad_description"),
										BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			}
		%>
		<%
			if (tab3Visible) {
		%>
		<tr>
			<td>
				<%
					if (sFParams.hasRead(new BmoCustomerRelative().getProgramCode())) {
				%>
				<table class="" border="0" align="left" width="<%=size3Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="8">Familiares</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">Nombre</th>
						<th class="reportCellEven" align="left">Tel&eacute;fono</th>
						<th class="reportCellEven" align="left">Ext.</th>
						<th class="reportCellEven" align="left">M&oacute;vil</th>
						<th class="reportCellEven" align="left">Email</th>
						<th class="reportCellEven" align="left">Responsable</th>
					</tr>

					<%
						// Familiares
										String relatives = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerrelatives ") + "WHERE curl_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(relatives);
										while (pmConnAdic.next()) {
											bmoCustomerRelative.getType()
													.setValue(pmConnAdic.getString("customerrelatives", "curl_type"));
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										bmoCustomerRelative.getType().getSelectedOption().getLabeltoHtml(),
										BmFieldType.OPTIONS))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerrelatives", "curl_fullname") + " "
												+ pmConnAdic.getString("customerrelatives", "curl_fatherlastname") + " "
												+ pmConnAdic.getString("customerrelatives", "curl_motherlastname"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerrelatives", "curl_number"),
										BmFieldType.PHONE)%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerrelatives", "curl_extension"),
										BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerrelatives", "curl_cellphone"),
										BmFieldType.PHONE)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerrelatives", "curl_email"),
										BmFieldType.EMAIL))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										((pmConnAdic.getBoolean("curl_responsible")) ? "Si" : "No"),
										BmFieldType.STRING)%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			}
		%>
		<%
			if (tab4Visible) {
		%>
		<tr>
			<td>
				<%
					if (sFParams.hasRead(new BmoCustomerContact().getProgramCode())) {
				%>
				<table class="" border="0" align="left" width="<%=size4Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="8">Contactos</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Nombre</th>
						<th class="reportCellEven" align="left">Alias</th>
						<th class="reportCellEven" align="left">Cargo</th>
						<th class="reportCellEven" align="left">Email</th>
						<th class="reportCellEven" align="left">Tel&eacute;fono</th>
						<th class="reportCellEven" align="left">Ext.</th>
						<th class="reportCellEven" align="left">M&oacute;vil</th>
						<th class="reportCellEven" align="left">Comentario</th>
					</tr>

					<%
						// Contactos
										String contacts = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customercontacts ") + "WHERE cuco_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(contacts);
										while (pmConnAdic.next()) {
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_fullname") + " "
												+ pmConnAdic.getString("customercontacts", "cuco_fatherlastname") + " "
												+ pmConnAdic.getString("customercontacts", "cuco_motherlastname"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_alias"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_position"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_email"),
										BmFieldType.EMAIL))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_number"),
										BmFieldType.PHONE)%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_extension"),
										BmFieldType.STRING)%>
						<%=HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_cellphone"),
										BmFieldType.PHONE)%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customercontacts", "cuco_commentalias"),
										BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			}
		%>
		<%
			if (tab5Visible) {
		%>
		<tr>
			<td>
				<%
					if (sFParams.hasRead(new BmoCustomerSocial().getProgramCode())) {
				%>
				<table class="" border="0" align="left" width="<%=size5Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="6">Redes Sociales</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">Cuenta</th>
					</tr>

					<%
						// Redes sociales
										String socials = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customersocials ")
												+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " socials")+" ON (soci_socialid = cuso_socialid)"
												+ "WHERE cuso_customerid = " + pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(socials);
										while (pmConnAdic.next()) {
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("socials", "soci_name"), BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customersocials", "cuso_account"),
										BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %> <%
 	if (sFParams.hasRead(new BmoCustomerWeb().getProgramCode())) {
 %>
				<table class="" border="0" align="left" width="<%=size5Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="6">Sitios Web</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Nombre</th>
					</tr>

					<%
						// Sitios web
										String customerWebsSql = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerwebs")+" WHERE cuwb_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(customerWebsSql);
										while (pmConnAdic.next()) {
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerwebs", "cuwb_website"), BmFieldType.WWW))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %> <%
 	if (sFParams.hasRead(new BmoCustomerCompany().getProgramCode())) {
 %>
				<table class="" border="0" align="left" width="<%=size5Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="6">Empresas</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Nombre</th>
					</tr>

					<%
						// Empresas
										String companies = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customercompanies ")
												+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " companies")+" ON (comp_companyid = cucp_companyid)"
												+ "WHERE cucp_customerid = " + pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(companies);
										while (pmConnAdic.next()) {
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("companies", "comp_name"), BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			}
		%>
		<%
			if (tab6Visible) {
		%>
		<tr>
			<td>
				<%
					if (sFParams.hasRead(new BmoCustomerBankAccount().getProgramCode())) {
				%>
				<table class="" border="0" align="left" width="<%=size6Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="6">Bancos</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Banco</th>
						<th class="reportCellEven" align="left">Clabe</th>
						<th class="reportCellEven" align="left">No. Cuenta</th>
						<th class="reportCellEven" align="left">Moneda</th>

					</tr>

					<%
						// Bancos
										String banks = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerbankaccounts ")
												+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = cuba_currencyid)"
												+ "WHERE cuba_customerid = " + pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(banks);
										while (pmConnAdic.next()) {
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerbankaccounts", "cuba_bank"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerbankaccounts", "cuba_clabe"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customerbankaccounts", "cuba_accountnumber"),
										BmFieldType.STRING))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("currencies", "cure_code") + " - "
												+ pmConnAdic.getString("currencies", "cure_name"),
										BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %> <%
 	if (sFParams.hasRead(new BmoCustomerPaymentType().getProgramCode())) {
 %>
				<table class="" border="0" align="left" width="<%=size6Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="6">M&eacute;todos de
							Pago</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">M&eacute;todo de Pago</th>
						<th class="reportCellEven" align="left">Moneda</th>
					</tr>

					<%
						// Metodos de pago
										String paymentType = "SELECT payt_name, cure_code, cure_name FROM " + SQLUtil.formatKind(sFParams, " customerpaymenttypes ")
												+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " currencies")+" ON (cure_currencyid = cupt_currencyid) "
												+ " LEFT JOIN " + SQLUtil.formatKind(sFParams, " paymenttypes")+" ON (payt_paymenttypeid = cupt_paymenttypeid) "
												+ " WHERE cupt_customerid = " + pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(paymentType);
										while (pmConnAdic.next()) {
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("paymenttypes", "payt_name"), BmFieldType.OPTIONS))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("currencies", "cure_code") + " - "
												+ pmConnAdic.getString("currencies", "cure_name"),
										BmFieldType.STRING))%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %> <%
 	if (sFParams.hasRead(new BmoCustomerNote().getProgramCode())) {
 %>
				<table class="" border="0" align="left" width="<%=size6Tab%>"
					style="font-size: 12px">
					<tr>
						<td class="reportHeaderCell" colspan="6">Notas</td>
					</tr>
					<tr>
						<th class="reportCellEven" align="left">Tipo</th>
						<th class="reportCellEven" align="left">Notas</th>
						<th class="reportCellEven" align="left">Doc. adjunto</th>
					</tr>

					<%
						// Notas
										String notes = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " customernotes ") + "WHERE cuno_customerid = "
												+ pmConn.getInt("customers", "cust_customerid");
										pmConnAdic.doFetch(notes);
										while (pmConnAdic.next()) {
											bmoCustomerNote.getType().setValue(pmConnAdic.getString("customernotes", "cuno_type"));
					%>
					<tr>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										bmoCustomerNote.getType().getSelectedOption().getLabeltoHtml(),
										BmFieldType.OPTIONS))%>
						<%=HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams,
										"" + pmConnAdic.getString("customernotes", "cuno_notes"), BmFieldType.STRING))%>
						<%=HtmlUtil.formatReportCell(sFParams,
										((pmConnAdic.getString("customernotes", "cuno_file").length() > 0)
												? "Si"
												: "No"),
										BmFieldType.STRING)%>
					</tr>
					<%
						}
					%>
				</table> <%
 	}
 %>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<%
			}
		%>
	</table>

	<p style="page-break-after: always">&nbsp;</p>

	<%
		i++;
			} // Fin while 	   

		} //Fin de validacion de 5 mil registros

		
	%>
	<%
		if (print.equals("1")) {
	%>
	<script>
		//window.print();
	</script>
	<%
		}
	}// Fin de if(no carga datos)
	pmConn.close();
	pmConnAdic.close();
	pmConnCount.close();
	%>
</body>
</html>

