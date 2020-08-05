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

<%@page import="java.util.Iterator"%>
<%@page import="com.symgae.shared.BmFilter"%>
<%@page import="com.symgae.shared.BmObject"%>
<%@page import="com.flexwm.shared.op.BmoSupplier"%>
<%@page import="com.flexwm.server.op.PmSupplier"%>
<%@page import="com.flexwm.shared.op.BmoSupplierEmail"%>
<%@page import="com.flexwm.server.op.PmSupplierEmail"%>
<%@page import="com.flexwm.shared.op.BmoSupplierPhone"%>
<%@page import="com.flexwm.server.op.PmSupplierPhone"%>
<%@page import="com.flexwm.shared.op.BmoSupplierAddress"%>
<%@page import="com.flexwm.server.op.PmSupplierAddress"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>

<%@include file="../inc/login.jsp" %>



<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
</head>

<body class="default">

<table border="0" cellspacing="0" cellpading="0" width="100%">
	<tr>
		<td align="left" width="80" rowspan="2" valign="top">	
			<img src="<%= sFParams.getMainImageUrl() %>">
		</td>
		<td class="reportTitle" align="left">
			&nbsp;
		</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
	<tr>
	<tr>
		<td colspan="3">
	<%
	
		int idStart = 0, idEnd = 0;
		if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
		if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));
	
			// IMPORTANTE:
			//SI VAS A MIGRAR DE NUEVO, SI SON "NOT NULL" LOS CAMPOS, AGREGAR VALIDACIONES COMENTADAS
			PmConn pmConn = new PmConn(sFParams);
			BmUpdateResult bmUpdateResult = new BmUpdateResult();
			
			try {
				pmConn.open();
				pmConn.disableAutoCommit();
				
				String sql = "SELECT * FROM suppliers " +
								" WHERE supl_supplierid >= " + idStart + 
								" AND  supl_supplierid <= " + idEnd ;
				pmConn.doFetch(sql);
				int i = 1;
				while (pmConn.next()) { %>
					<br><br>
					<%= i %>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
					<%= pmConn.getString("supl_code") %> - <%= pmConn.getString("supl_name") %>
					<%
					
					// telefonos
					if ((!pmConn.getString("suppliers", "supl_officephone1").equals("")) 
							||  (!pmConn.getString("suppliers", "supl_officephone2").equals("")) ){
						PmSupplierPhone pmSupplierPhone = new PmSupplierPhone(sFParams);
						BmoSupplierPhone bmoSupplierPhone = new BmoSupplierPhone();
						bmoSupplierPhone.getType().setValue("" + BmoSupplierPhone.TYPE_WORK);
						//String phone = "000-000-0000";
						//if (!pmConn.getString("suppliers", "supl_officephone1").equals(""))
								//phone = pmConn.getString("suppliers", "supl_officephone1");
						bmoSupplierPhone.getNumber().setValue("" + pmConn.getString("suppliers", "supl_officephone1"));
						bmoSupplierPhone.getFax().setValue("" + pmConn.getString("suppliers", "supl_officephone2"));
						bmoSupplierPhone.getExtension().setValue("");
						bmoSupplierPhone.getSupplierId().setValue(pmConn.getInt("suppliers", "supl_supplierid"));
						pmSupplierPhone.save(pmConn, bmoSupplierPhone, bmUpdateResult);
						
						if (!bmUpdateResult.hasErrors()) {
							%>
								<br>
								<b>Proceso Telefonos <font color="green">Terminado</font></b>
							<%
						} else {
							%>
							<br>
							<b>Proceso Telefonos: <font color="red">ERROR</font>  </b>
					<%	}
					} else {%>
						<br>
						<b>Proceso Telefonos: <font color="blue">No tiene Telefonos</font> </b>
				<%	}
					
					
					// Direcciones
					if ( (!pmConn.getString("suppliers", "supl_address1").equals("")) 
							||  (!pmConn.getString("suppliers", "supl_address2").equals("")) 
							||  (!pmConn.getString("suppliers", "supl_zip").equals(""))
							||  (pmConn.getInt("suppliers", "supl_cityid") > 0) ) {
						
						PmSupplierAddress pmSupplierAddress = new PmSupplierAddress(sFParams);
						BmoSupplierAddress bmoSupplierAddress = new BmoSupplierAddress();
						
						bmoSupplierAddress.getType().setValue("" + BmoSupplierAddress.TYPE_WORK);
						
						//String street = "Calle";
						//if (!pmConn.getString("suppliers", "supl_address1").equals(""))
							//street = pmConn.getString("suppliers", "supl_address1");
						bmoSupplierAddress.getStreet().setValue("" + pmConn.getString("suppliers", "supl_address1"));
						
						//String neighborhood = "Colonia";
						//if (!pmConn.getString("suppliers", "supl_address2").equals(""))
							//neighborhood = pmConn.getString("suppliers", "supl_address2");
						bmoSupplierAddress.getNeighborhood().setValue("" + pmConn.getString("suppliers", "supl_address2"));
						
						bmoSupplierAddress.getNumber().setValue("0");
						
						//String zip = "00000";
						//if (!pmConn.getString("suppliers", "supl_zip").equals(""))
							//zip = pmConn.getString("suppliers", "supl_zip");
						bmoSupplierAddress.getZip().setValue("" + pmConn.getString("suppliers", "supl_zip"));
						
						bmoSupplierAddress.getDescription().setValue("");
						
						//int city = 0;
						//if (pmConn.getInt("supl_cityid") > 0)
							//city = pmConn.getString("suppliers", "supl_cityid");
						bmoSupplierAddress.getCityId().setValue("" + pmConn.getInt("suppliers", "supl_cityid"));
						
						bmoSupplierAddress.getSupplierId().setValue(pmConn.getInt("suppliers", "supl_supplierid"));
						
						pmSupplierAddress.save(pmConn, bmoSupplierAddress, bmUpdateResult);
						
						if (!bmUpdateResult.hasErrors()) {
							%>
								<br>
								<b>Proceso Direcciones <font color="green">Terminado</font></b>
							<%
						} else {
							%>
							<br>
							<b>Proceso Direcciones: <font color="red">ERROR</font> </b>
	
					<%	}
				}else {%>
					<br>
					<b>Proceso Direcciones: <font color="blue">No tiene Direccion</font></b>
			<%	}

				 i++;	
				}	
						
								
				if (!bmUpdateResult.hasErrors()) {
					pmConn.commit();
				%>
					<br><br><br>
					<h1><b><font color="green">&#10004; Proceso Terminado &#10004;</font></b></h1>
				<%
				}else {
					%>
					<br><br>
					<h1><p style="background: red"><font color="black"><b>&#10008; El proceso se cancelo, se encontraron ERRORES, revise bitacora &#10008;</b></font></p></h1>
			<%	}
									
	
			}catch (Exception e) {
				pmConn.rollback();
				throw new SFException("Proceso migracion de info proveedores a tabs: "+e.toString());
			}finally {
				pmConn.close();
			}
		
%>
		</td>
	</tr>
</table>

  </body>
</html>