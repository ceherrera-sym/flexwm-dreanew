
<%@page import="com.flexwm.shared.fi.BmoRaccountType"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.server.fi.PmBankMovConcept"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovConcept"%>
<%@page import="com.flexwm.shared.fi.BmoBankMovType"%>
<%@include file="../inc/imports.jsp" %>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.server.cm.PmProjectGuideline"%>
<%@page import="com.flexwm.shared.cm.BmoProjectGuideline"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login_opt.jsp" %>

<%
	String programTitle = "Proceso Borrar los depositos de CxC";
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
		PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(sFParams);
		PmRaccount pmRaccount = new PmRaccount(sFParams);
	
		BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
		BmoRaccount bmoRaccForeing = new BmoRaccount();
	
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
			 
			 sql = " SELECT * FROM raccounts " +
				   " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +				   	 
				   " WHERE ract_category = '" + BmoRaccountType.CATEGORY_OTHER + "'" +
				   " AND ract_type = '" + BmoRaccountType.TYPE_DEPOSIT + "'" ;
				   //" AND racc_raccountid >= 12001 " +
				   //" AND racc_raccountid <= 13000 ";
			 System.out.println("sql " + sql);
			 pmConn.doFetch(sql);
			 while (pmConn.next()) {
				 
				 sql = " SELECT * FROM bankmovconcepts " +			 	   
				 	   " LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
				 	   " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
				       " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
					   " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
					   " WHERE bkmc_foreignid = " + pmConn.getInt("racc_raccountid") +
					   " AND bkmc_raccountid > 0 " +					   
					   " AND bkmt_category = '" + BmoBankMovType.CATEGORY_CXC + "'" +			       
					   " AND ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "'" +
				       " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'";
			    pmConn2.doFetch(sql);   
				if (pmConn2.next()) {
					int bankmovconceptid = pmConn2.getInt("bkmc_bankmovconceptid");
					
					sql = " SELECT * FROM bankmovconcepts " +			 	   
					 	  " LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
					 	  " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
					      " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
						  " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
						  " WHERE bkmc_raccountid = " + pmConn.getInt("racc_raccountid");
					pmConn2.doFetch(sql);
					if (pmConn2.next()) {
						
					} else { 
						 bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.get(bankmovconceptid);
					 	 
							 
							System.out.println("Categoria " + bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getCategory().getSelectedOption().getLabel());
							
							
							System.out.println("Foreign " + bmoBankMovConcept.getForeignId().toInteger());
							
							if (bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXC)) {
							
								if (bmoBankMovConcept.getForeignId().toInteger() > 0) {
									
									sql = " SELECT * FROM raccounts " +
									      " WHERE racc_raccountid = " + bmoBankMovConcept.getForeignId().toInteger();
									pmConn2.doFetch(sql);
									if (pmConn2.next()) {
										
										int foreignId = bmoBankMovConcept.getForeignId().toInteger();
										
										//Borrar el Foreign
										bmoBankMovConcept.getForeignId().setValue(0);
										//bmoBankMovConcept.getDepositRaccountItemId().setValue(0);
										
										pmBankMovConcept.saveSimple(bmoBankMovConcept, bmUpdateResult);
									
										//Eliminar la CXC Foreign					
										bmoRaccForeing = (BmoRaccount)pmRaccount.get(foreignId);					
										bmoRaccForeing.getStatus().setValue(BmoRaccount.STATUS_REVISION);
										pmRaccount.saveSimple(bmoRaccForeing, bmUpdateResult);
										
										//Eliminar la aplicacion
										sql = "DELETE FROM raccountassignments WHERE rass_raccountid = " + bmoRaccForeing.getId();
										pmConn2.doUpdate(sql);
										
										//Eliminar el item
										sql = "DELETE FROM raccountitems WHERE rait_raccountid = " + bmoRaccForeing.getId();
										pmConn2.doUpdate(sql);
										
										//Eliminar la CxC					
										sql = "DELETE FROM raccounts WHERE racc_raccountid = " + bmoRaccForeing.getId();
										pmConn2.doUpdate(sql);
									}	
								}	
							}
					}	
				}	 
				 
			 }
				   
				   
			 
			/* sql = " SELECT * FROM bankmovconcepts " +			 	   
			 	   " LEFT JOIN raccounts ON (bkmc_raccountid = racc_raccountid) " +
			 	   " LEFT JOIN raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) " +
			       " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				   " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
			       " WHERE bkmc_raccountid > 0 " +
				   " AND racc_raccountid >= 1 " +
			       " AND racc_raccountid <= 300 " +
				   " AND bkmc_foreignid > 0 " +
				   " AND bkmt_category = '" + BmoBankMovType.CATEGORY_CXC + "'" +			       
				   " AND ract_category = '" + BmoRaccountType.CATEGORY_ORDER + "'" +
			       " AND ract_type = '" + BmoRaccountType.TYPE_WITHDRAW + "'";
			pmConn.doFetch(sql);
			while (pmConn.next()) {
				bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.get(pmConn.getInt("bkmc_bankmovconceptid"));
				
				int raccForeingId = bmoBankMovConcept.getForeignId().toInteger();
				
				if (raccForeingId > 0) {
				
					System.out.println("raccForeingId " + raccForeingId);
					
					//Borrar el Foreign
					bmoBankMovConcept.getForeignId().setValue(0);
					bmoBankMovConcept.getDepositRaccountItemId().setValue(0);
					
					pmBankMovConcept.saveSimple(bmoBankMovConcept, bmUpdateResult);
					
					//Eliminar la CXC Foreign					
					bmoRaccForeing = (BmoRaccount)pmRaccount.get(raccForeingId);					
					bmoRaccForeing.getStatus().setValue(BmoRaccount.STATUS_REVISION);
					pmRaccount.saveSimple(bmoRaccForeing, bmUpdateResult);
					
					System.out.println("Estatus " + bmoRaccForeing.getStatus().getSelectedOption().getLabel());
										
					pmRaccount.delete(bmoRaccForeing, bmUpdateResult);
				}	
				
			}
			 */
		     
		 	/*if (!bmUpdateResult.hasErrors()) {
		 		pmConn.commit();*/
			 	%>
			 		<h2><b><font color="green">&#10004; Proceso Terminado &#10004;</font></b></h2>
		 		<%	
		 	//}
		%>
		<%= bmUpdateResult.errorsToString() %> 		
		<%
		} catch (Exception e) {
			throw new SFException(e.toString());
		
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		%>
	</body>
</html>