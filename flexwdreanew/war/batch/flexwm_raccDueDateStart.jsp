
<%@page import="com.flexwm.server.op.PmReqPayType"%>
<%@page import="com.flexwm.shared.op.BmoReqPayType"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String programTitle = "Proceso Calcular Fechas de vencimiento en CxC";
	String programDescription = programTitle;
	String populateData = "", action = "";
%>

<html>
	<head>
		<title>:::<%= programTitle %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%> %>css/<%= defaultCss %>">
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
				
				BmUpdateResult bmUpdateResult = new BmUpdateResult();
				
				String status = "";
				int idStart = 0, idEnd = 0;
				if (request.getParameter("idstart") != null) idStart = Integer.parseInt(request.getParameter("idstart"));
				if (request.getParameter("idend") != null) idEnd = Integer.parseInt(request.getParameter("idend"));
				
				try {		
			
			 String sql = "";
			 pmConn.disableAutoCommit();
			 pmConn2.disableAutoCommit();
			 
				     int i = 1;
				     
				     BmoRaccount bmoRaccount = new BmoRaccount();
				     PmRaccount pmRaccount = new PmRaccount(sFParams);
				     BmoReqPayType bmoReqPayType = new BmoReqPayType();
				     PmReqPayType pmReqPayType = new PmReqPayType(sFParams);
				     
				     sql = "SELECT racc_raccountid, racc_code, racc_receivedate, racc_duedatestart, tracc.rqpt_days AS raccDaysTP, tclie.rqpt_days AS custDaysTP " +
				     		" FROM raccounts " +
				    		" LEFT JOIN customers ON (cust_customerid = racc_customerid) " +
				    		" LEFT JOIN reqpaytypes tracc ON (tracc.rqpt_reqpaytypeid = racc_reqpaytypeid) " +
				    		" LEFT JOIN reqpaytypes tclie ON (tclie.rqpt_reqpaytypeid = cust_reqpaytypeid) " +
				    		" LEFT JOIN raccounttypes ON (ract_raccounttypeid = racc_raccounttypeid) " + 
				    		" WHERE racc_raccountid > 0 " +
				    		" AND ract_raccounttypeid <> 4 " + // Descartar abonos del esquema anterior
				    		" ORDER BY racc_raccountid ";

				     pmConn2.doFetch(sql);
				     
				     int raccountId = 0;
				    	 
				     while (pmConn2.next()) {
		    		 raccountId = pmConn2.getInt("racc_raccountid");
			    	 bmoRaccount  = (BmoRaccount)pmRaccount.get(raccountId);
			    	 String dueDateStartOrigin = "";
			    	 dueDateStartOrigin = bmoRaccount.getDueDateStart().toString();
// 			    	 System.out.println("----  raccCode: "+bmoRaccount.getCode() + " ----");
// 			    	 System.out.println("F. registro: "+bmoRaccount.getReceiveDate().toString());
// 			    	 System.out.println("F. Prog: "+bmoRaccount.getDueDate().toString());
// 			    	 System.out.println("F. Vencimiento: "+bmoRaccount.getDueDateStart().toString());
			
			    	 // 
			    	 int raccDaysTP = -1, custDaysTP = -1;
			    	 raccDaysTP = pmConn2.getInt("raccDaysTP");
			    	 custDaysTP = pmConn2.getInt("custDaysTP");
// 			    	 System.out.println("raccDaysTP: "+raccDaysTP);
// 			    	 System.out.println("custDaysTP: "+custDaysTP);

				    	 // La cxc tiene termino de pago
					if (raccDaysTP >= 0) {
						String dueDateStart = SFServerUtil.addDays(sFParams.getDateFormat(),
								bmoRaccount.getReceiveDate().toString(), raccDaysTP);
// 						System.out.println("dueDateStart 1: " + dueDateStart);
						bmoRaccount.getDueDateStart().setValue(dueDateStart);
// 						System.out.println("F. Vencimiento 1: " + bmoRaccount.getDueDateStart().toString());

					}
					// En caso de que NO tenga Termino Pago, tomarlo del Cliente
					else if (raccDaysTP < 0 && custDaysTP >= 0) {
						String dueDateStart = SFServerUtil.addDays(sFParams.getDateFormat(),
								bmoRaccount.getReceiveDate().toString(), custDaysTP);
// 						System.out.println("dueDateStart 2: " + dueDateStart);
						bmoRaccount.getDueDateStart().setValue(dueDateStart);
// 						System.out.println("F. Vencimiento 2: " + bmoRaccount.getDueDateStart().toString());
					}

					if (!dueDateStartOrigin.equals(bmoRaccount.getDueDateStart().toString())) {
						pmRaccount.saveSimple(pmConn, bmoRaccount, bmUpdateResult);
// 						System.out.println("aaa");
					} else {
// 						System.out.println("bbb");
					}
				}

				if (!bmUpdateResult.hasErrors()) {
					pmConn.commit();
		%>
		 		<h2><b><font color="green">&#10004; Proceso Terminado &#10004;</font></b></h2>
	 		<%	
		 	}
		%>
		<%= bmUpdateResult.errorsToString() %> 		
		<%
		} catch (Exception e) {		
			pmConn.rollback();
			throw new SFException(e.toString());
		
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		%>
	</body>
</html>


