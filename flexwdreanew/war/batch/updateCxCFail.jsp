<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Proceso de Cron Manual";
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

BmUpdateResult bmUpdateResult = new BmUpdateResult();
NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

PmCurrency pmCurrency = new PmCurrency(sFParams);

String status = "";
int idStart = 0, idEnd = 0;
if (request.getParameter("status") != null) status = request.getParameter("status");
if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));


try {
	
		String nowDate = "";
		if (request.getParameter("nowDate") != null) nowDate = request.getParameter("nowDate");
	
		String sql = "";
		
		BmoRaccount bmoRaccWeek = new BmoRaccount();
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		
		PmOrder pmOrder = new PmOrder(sFParams);
		BmoOrder bmoOrder = new BmoOrder();
		
		/*//Obtener el Lunes de esta semana		
		Calendar payWeek = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));
		//Obtener el día
		int nowDay = payWeek.get(Calendar.DAY_OF_WEEK);
		
		//System.out.println("nowDay " + nowDay);
		//Sunday
		if (nowDay == 1) nowDay = 6;
		//Monday
		else if (nowDay == 2) nowDay = 0;
		//Thusday
		else if (nowDay == 3) nowDay = 1;
		//Weendays
		else if (nowDay == 4) nowDay = 2;
		//Thurday
		else if (nowDay == 5) nowDay = 3;
		//Friday
		else if (nowDay == 6) nowDay = 4;
		//Saturday
		else if (nowDay == 7) nowDay = 5;
		
		payWeek.add(Calendar.DAY_OF_WEEK, -nowDay);
		//payWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);*/
		
		/*String datePayout = "";
		if (nowDate.equals(""))
			datePayout = FlexUtil.calendarToString(sFParams, payWeek);
		else*/
		String datePayout = nowDate;
		String where = "";
		
		if (idStart > 0)
	    	 where += " AND racc_orderid >= " + idStart;
	     
	     if (idEnd > 0)
	    	 where += " AND racc_orderid <= " + idEnd;
		
		//Listas las cuentas por cobrar
		sql = " SELECT * FROM raccounts " +
			  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
		      " LEFT JOIN orders ON (racc_orderid = orde_orderid) " +
		      " WHERE racc_duedate <= '" +  datePayout + "'" + 
		      " AND orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "'" +
		      " AND racc_status = '" + BmoRaccount.STATUS_AUTHORIZED + "'" +
		      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
		      " AND racc_failure = 0 " + where +		      
		      " AND racc_paymentstatus <> '" + BmoRaccount.PAYMENTSTATUS_TOTAL + "'" +				       
		      " ORDER BY racc_orderid ";		
		pmConn2.doFetch(sql);
		while(pmConn2.next()) {
			bmoOrder = (BmoOrder)pmOrder.get(pmConn2.getInt("racc_orderid"));
			
			//bmoRaccWeek = (BmoRaccount)pmRaccount.get(pmConn2.getInt("racc_raccountid"));
			
			//if(!bmoRaccWeek.getPaymentStatus().equals(BmoRaccount.PAYMENTSTATUS_TOTAL)) {						
				//Generar Penalidad
				if (!pmRaccount.hasPenalty(pmConn, bmoOrder, bmUpdateResult)) {							
					pmRaccount.createPenalty(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
				} else {
					 BmoCredit bmoCredit = new BmoCredit();
					 PmCredit pmCredit = new PmCredit(sFParams);
					 bmoCredit = (BmoCredit)pmCredit.getBy(bmoOrder.getId(), bmoCredit.getOrderId().getName());
					 
					 bmoCredit.getPaymentStatus().setValue(BmoCredit.PAYMENTSTATUS_INPROBLEM);
					 
					 pmCredit.saveSimple(pmConn, bmoCredit, bmUpdateResult);
					 
					 //Ligar la Penalizacion ya creada a la CxC que fallo esta semana
					 pmRaccount.updatePenaltyFromNewFail(pmConn, bmoOrder, pmConn2.getInt("racc_raccountid"), bmUpdateResult);
				}					
			/*} else {
				//Manejo de la Semana 10
				
				//Obtener la Primera fecha de pago del crédito
				sql = " SELECT COUNT(*) AS items FROM raccounts " +
				      " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
				      " WHERE racc_orderid = " + bmoOrder.getId() +
				      " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'" +
				      " AND racc_duedate <= '" + datePayout + "'" +
				      " ORDER BY racc_raccountid ";
				pmConn3.doFetch(sql);
				if (pmConn3.next()) {
					
					int items = pmConn3.getInt("items");
					
					if (items >= 9) {
						
						//Obtener el pago individual
						double payment = pmConn2.getDouble("racc_total");
						
						//Calcular el monto que debe estar pagado al día de hoy
						payment = payment * items;
						
						sql = " SELECT SUM(racc_total) AS payment FROM raccounts  " +
						      " WHERE racc_orderid = " + bmoOrder.getId() +
						      " AND racc_duedate <= '" + datePayout + "'";
						pmConn3.doFetch(sql);
						if (pmConn3.next()) {									
							if (pmConn3.getDouble("payment") >= payment) {
								sql = " UPDATE raccounts SET racc_receivedate = '" + datePayout + "'" +
								      " WHERE racc_orderid = " + bmoOrder.getId() + 
								      " AND racc_duedate >= '" + datePayout + "'";
								pmConn3.doUpdate(sql);
							}
						}
					}
				}
			}*/
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
