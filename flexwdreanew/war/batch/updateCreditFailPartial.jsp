
<%@page import="com.flexwm.shared.cr.BmoCredit"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Creditos con Pagos Parciales";
	String programDescription = programTitle;
	String populateData = "", action = "";
	
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

PmConn pmConn = new PmConn(sFParams);
pmConn.open();

PmConn pmConn2 = new PmConn(sFParams);
pmConn2.open();

PmConn pmConn3 = new PmConn(sFParams);
pmConn3.open();


NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

String status = "";
int idStart = 0, idEnd = 0;
if (request.getParameter("status") != null) status = request.getParameter("status");
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));

BmUpdateResult bmUpdateResult = new BmUpdateResult();

try {		
		String sql = "";
		
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		
		//Obtener el Lunes de esta semana		
		Calendar nowWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));
		nowWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		nowWeek.add(Calendar.WEEK_OF_YEAR, 0);		
		
		String datePayout = FlexUtil.calendarToString(sFParams, nowWeek);
		
		sql = " SELECT * FROM credits " +
		      " WHERE cred_paymentstatus = '" + BmoCredit.PAYMENTSTATUS_NORMAL + "'";
		pmConn2.doFetch(sql);
		while(pmConn2.next()) {
			//Listas las cuentas por cobrar
			sql = " SELECT * FROM raccounts " +
				  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
			      " LEFT JOIN orders ON (racc_orderid = orde_orderid) " +
			      " WHERE racc_duedate < '" +  datePayout + "'" + 
			      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
			      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
			      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
			      " AND racc_failure = 0 " +
			      " AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "'" +
			      " AND racc_orderid = " + pmConn2.getInt("cred_orderid") +
			      " GROUP BY racc_orderid ";			
			pmConn3.doFetch(sql);			
			while(pmConn3.next()) {
				bmoOrder = (BmoOrder)pmOrder.get(pmConn3.getInt("racc_orderid"));
				
				//Generar Penalidad
// 				if (!pmRaccount.hasPenalty(pmConn, bmoOrder, bmUpdateResult)) {
// 					pmRaccount.createPenalty(pmConn, bmoOrder, pmConn3.getInt("racc_raccountid"), bmUpdateResult);
// 				} else {
// 					 BmoCredit bmoCredit = new BmoCredit();
// 					 PmCredit pmCredit = new PmCredit(sFParams);
// 					 bmoCredit = (BmoCredit)pmCredit.getBy(bmoOrder.getId(), bmoCredit.getOrderId().getName());
					 
// 					 bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);
					 
// 					 pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
// 				}
			}
		}	

		//Ligar las penalizaciones creadas a la CXC de origen
		
		sql = " SELECT * FROM raccounts " +
			  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
		      " LEFT JOIN orders ON (racc_orderid = orde_orderid) " +		       
		      " WHERE orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
		      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
		      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
		      " AND racc_failure = 1 " +				      				      
		      " GROUP BY racc_orderid ";
		pmConn2.doFetch(sql);
		System.out.println("sql " + sql);
		while (pmConn2.next()) {
			//Obtener el Lunes de esta semana		
			nowWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), SFServerUtil.nowToString(sFParams, pmConn2.getString("racc_datecreate")));
			nowWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			nowWeek.add(Calendar.WEEK_OF_YEAR, 0);
			
			datePayout = FlexUtil.calendarToString(sFParams, nowWeek);
			
			sql = " SELECT * FROM raccounts " +
				  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
				  " LEFT JOIN orders ON (racc_orderid = orde_orderid) " +
				  " WHERE racc_orderid = " + pmConn2.getInt("racc_orderid") +
				  " AND racc_duedate = '" +  datePayout + "'" + 
			      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
			      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
			      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
			      " AND racc_failure = 0 ";
			pmConn3.doFetch(sql);
			if (pmConn3.next()) {
				sql = " UPDATE raccounts SET racc_relatedraccountid = " + pmConn3.getInt("racc_raccountid") + 
					  " WHERE racc_raccountid = " + pmConn2.getInt("racc_raccountid");
				pmConn.doUpdate(sql);
			}
		}
     
 	
%>
<%= bmUpdateResult.errorsToString() %> 		
<%
} catch (Exception e) {
	throw new SFException(e.toString());

} finally {
	pmConn3.close();
	pmConn2.close();
	pmConn.close();
	
	
}
	


%>

</table>
</body>
</html>
