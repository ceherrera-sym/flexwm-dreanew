<%@page import="com.flexwm.server.FlexUtil"%>
<%@page import= "java.util.Date"%>
<%@page import= "java.util.Calendar"%>
<%@page import= "java.util.Locale"%>
<%@page import= "com.symgae.server.SFServerUtil"%>
<%@page import= "com.flexwm.shared.fi.BmoRaccount"%>
<%@page import= "com.flexwm.server.fi.PmRaccount"%>
<%@page import= "com.flexwm.shared.co.BmoPropertySale"%>
<%@page import= "com.flexwm.server.co.PmPropertySale"%>
<%@page import= "com.flexwm.shared.cm.BmoCustomer"%>
<%@page import= "com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.op.BmoRequisition"%>
<%@page import="com.flexwm.shared.op.BmoRequisitionType"%>
<%@page import="com.flexwm.shared.op.BmoSupplier"%>
<%@page import= "com.flexwm.shared.op.BmoOrder"%>
<%@page import= "com.flexwm.server.op.PmOrder"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="/inc/login.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<%
	// Inicializar variables
 	String title = "Reportes de Ventas Administraci&oacute;n Comercial";
	System.out.println("\n Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
	+ " Reporte: "+title
	+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
   	Locale locale = new Locale("es", "MX");
	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(locale);
   	
   	BmoProgram bmoProgramPrsa = new BmoProgram();
   	BmoOrder bmoOrder = new BmoOrder();

	PmProgram pmProgram = new PmProgram(sFParams);
   	BmoPropertySale bmoPropertySale = new BmoPropertySale();
	
	PmCustomer pmCustomerRecommendedBy = new PmCustomer(sFParams);

	String sql = "", where = "", filters = "";
   	int programId = 0;
   	int wFlowPhaseId = 0;
   	int wFlowTypeId = 0;
   	int developmentId = 0;
   	int developmentPhaseId = 0;
   	int developmentBlockId = 0;
 	int propertyModelId = 0;
 	int propertyTypeId = 0;
 	String prsaStatus = "";
 	String prsaType= "";
 	int bvblIsTemporally = 0;
 	int isHabitability = 0;
//  	int profileId = 0;
 	String prsaStartDate = "", prsaStartDateEnd = "";
   	String prsaEndDate = "", prsaEndDateEnd = "", cancellDateEnd ="", cancellDate = "";
   	String byPhase = "";
   	String sqlPhase = "";
   	String whereGroup = "";
   	int prsaSalesUserId= 0;
   	String phaseStartDate = "";
   	String phaseEndDate = "", paymentStatus = "",tags = "";
   
   	
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
   	if (request.getParameter("wflw_wflowphaseid") != null) wFlowPhaseId = Integer.parseInt(request.getParameter("wflw_wflowphaseid"));
   	if (request.getParameter("prsa_wflowtypeid") != null) wFlowTypeId = Integer.parseInt(request.getParameter("prsa_wflowtypeid"));
   	if (request.getParameter("dvph_developmentid") != null) developmentId = Integer.parseInt(request.getParameter("dvph_developmentid"));
   	if (request.getParameter("dvbl_developmentphaseid") != null) developmentPhaseId = Integer.parseInt(request.getParameter("dvbl_developmentphaseid"));
   	if (request.getParameter("prty_developmentblockid") != null) developmentBlockId = Integer.parseInt(request.getParameter("prty_developmentblockid"));
   	if (request.getParameter("prty_propertymodelid") != null) propertyModelId = Integer.parseInt(request.getParameter("prty_propertymodelid"));
   	if (request.getParameter("ptyp_propertytypeid") != null) propertyTypeId = Integer.parseInt(request.getParameter("ptyp_propertytypeid"));
   	if (request.getParameter("prsa_status") != null) prsaStatus = request.getParameter("prsa_status");
   	if (request.getParameter("prsa_type") != null) prsaType = request.getParameter("prsa_type");
   	if (request.getParameter("dvbl_istemporally") != null) bvblIsTemporally = Integer.parseInt(request.getParameter("dvbl_istemporally"));
   	if (request.getParameter("prty_habitability") != null) isHabitability = Integer.parseInt(request.getParameter("prty_habitability"));
   	if (request.getParameter("prsa_startdate") != null) prsaStartDate = request.getParameter("prsa_startdate");
   	if (request.getParameter("saledateend") != null) prsaStartDateEnd = request.getParameter("saledateend");
   	if (request.getParameter("prsa_enddate") != null) prsaEndDate = request.getParameter("prsa_enddate");
   	if (request.getParameter("deliverydateend") != null) prsaEndDateEnd = request.getParameter("deliverydateend");
   	if (request.getParameter("phasestartdate") != null) phaseStartDate = request.getParameter("phasestartdate");
   	if (request.getParameter("phaseenddate") != null) phaseEndDate = request.getParameter("phaseenddate");
   	if (request.getParameter("prsa_salesuserid") != null) prsaSalesUserId = Integer.parseInt(request.getParameter("prsa_salesuserid"));
//    	if (request.getParameter("profileid") != null) profileId = Integer.parseInt(request.getParameter("profileid"));
   	if (request.getParameter("byphase") != null) byPhase = request.getParameter("byphase");
   	
   	if (request.getParameter("prsa_cancelldate") != null) cancellDate = request.getParameter("prsa_cancelldate");
   	if (request.getParameter("cancelldateend") != null) cancellDateEnd = request.getParameter("cancelldateend");
    if (request.getParameter("paymentStatus") != null) paymentStatus = request.getParameter("paymentStatus");
    if (request.getParameter("prsa_tags") != null) tags = request.getParameter("prsa_tags");
	// Construir filtros 
	// Filtros listados

		    if (programId > 0) {
				where += " AND wfca_programid = " + programId;
		   	}
		    
		    if (wFlowPhaseId > 0) {
		    	if(byPhase.equals("all")){
		    		filters += "<i>Por Fase: </i> Todas, <i>Fase de Flujo: </i> Todas, ";
		    	}else if (byPhase.equals("finally")) {
					where += " AND wfsp_progress = 100";
					where += " AND wfsp_wflowphaseid = " + wFlowPhaseId;
			   		filters += "<i>Por Fase: </i>" + request.getParameter("byphaseLabel") + ", ";
			   		
			   	} else if (byPhase.equals("before")) {   		
					where += " AND wflw_wflowphaseid < " + wFlowPhaseId;
			   		filters += "<i>Por Fase: </i>" + request.getParameter("byphaseLabel") + ", ";
			   		
			   	} else if (byPhase.equals("present")) {
			   		where += " AND wfsp_progress < 100";
			   		where += " AND wflw_wflowphaseid = " + wFlowPhaseId;
			   		filters += "<i>Por Fase: </i>" + request.getParameter("byphaseLabel") + ", ";
			   	}
			    
		    	if(!byPhase.equals("all")) 
		    		filters += "<i>Fase de Flujo: </i>" + request.getParameter("wflw_wflowphaseidLabel") + ", ";
		    }
// 		    if(!tags.equals("0")){
// 				where += " AND prsa_tags LIKE '%:"+tags+":%' ";
// 				filters += "<i>Tags: </i>" + request.getParameter("prsa_tagsLabel") + ", ";				
				
// 			}
		     if(!tags.equals("")){
				where += FlexUtil.parseTagsFiltersToSql("prsa_tags", tags);
				filters += "<i>Tags: </i>" + request.getParameter("prsa_tagsLabel") + ", ";
			}
// 		    if (profileId > 0) {
// 				whereGroup += " AND wfsp_profileid = " + profileId;
// 			}
		    
		    if (wFlowTypeId > 0) {
				where += " AND wfty_wflowtypeid = " + wFlowTypeId;
		   		filters += "<i>Medio Titulaci&oacute;n: </i>" + request.getParameter("prsa_wflowtypeidLabel") + ", ";
		   	}
		    
		    if (developmentId > 0) {
				where += " AND deve_developmentid = " + developmentId;
		   		filters += "<i>Desarrollo: </i>" + request.getParameter("dvph_developmentidLabel") + ", ";
		   	}
		    
		    if (developmentPhaseId > 0) {
				where += " AND dvph_developmentphaseid = " + developmentPhaseId;
		   		filters += "<i>Etapa Desarrollo: </i>" + request.getParameter("dvbl_developmentphaseidLabel") + ", ";
		   	}
		    
		    if (developmentBlockId > 0) {
				where += " AND dvbl_developmentblockid = " + developmentBlockId;
		   		filters += "<i>Manzana: </i>" + request.getParameter("prty_developmentblockidLabel") + ", ";
		   	}
		    
		    if (propertyModelId > 0) {
				where += " AND ptym_propertymodelid = " + propertyModelId;
		   		filters += "<i>Modelo: </i>" + request.getParameter("prty_propertymodelidLabel") + ", ";
		   	}
		    
		    if (propertyTypeId > 0) {
				where += " AND ptyp_propertytypeid = " + propertyTypeId;
		   		filters += "<i>Tipo de Inmueble: </i>" + request.getParameter("ptyp_propertytypeidLabel") + ", ";
		   	}
		    
		    if (!(prsaStatus.equals(""))) {
				//where += " AND prsa_status = '" + prsaStatus + "'";
		        where += SFServerUtil.parseFiltersToSql("prsa_status", prsaStatus);
		   		filters += "<i>Estatus Venta: </i>" + request.getParameter("prsa_statusLabel") + ", ";
		   	}
		    
		    if (!(prsaType.equals(""))) {
				where += " AND prsa_type = '" + prsaType + "'";
		   		filters += "<i>Tipo de Venta: </i>" + request.getParameter("prsa_typeLabel") + ", ";
		   	}
		    
		    if (isHabitability != 2) {
				where += " AND prty_habitability = " + isHabitability;
					filters += "<i> Habitabilidad: </i>" + request.getParameter("prty_habitabilityLabel") + ", ";
		   	} else {
		   		filters += "<i> Habitabilidad: Todas</i>, ";
		   	}
		    
		    if (bvblIsTemporally != 2) {
				where += " AND dvbl_istemporally = " + bvblIsTemporally;
		   		filters += "<i> Manzanas Temporales: </i>" + request.getParameter("dvbl_istemporallyLabel") + ", ";
		   	}else {
		   		filters += "<i> Manzanas Temporales: Todas</i>,  ";
		   	}
		    
		    if (!(prsaStartDate.equals(""))) {
				where += " AND prsa_startdate >= '" + prsaStartDate + " 00:00' ";
		   		filters += "<i>Fecha Venta: </i>" + request.getParameter("prsa_startdate") + ", ";
		   	}
		    
		    if (!(prsaStartDateEnd.equals(""))) {
				where += " AND prsa_startdate <= '" + prsaStartDateEnd + " 23:59' ";
		   		filters += "<i>Fecha Venta Fin: </i>" + request.getParameter("saledateend") + ", ";
		   	}
		    
		    if (!(prsaEndDate.equals(""))) {
				where += " AND prsa_enddate >= '" + prsaEndDate + " 00:00' ";
		   		filters += "<i>Fecha Entrega: </i>" + request.getParameter("prsa_enddate") + ", ";
		   	}
		    
		    if (!(prsaEndDateEnd.equals(""))) {
				where += " AND prsa_enddate <= '" + prsaEndDateEnd + " 23:59' ";
		   		filters += "<i>Fecha Entrega Fin: </i>" + request.getParameter("deliverydateend") + ", ";
		   	}
		    
		    if (!(phaseStartDate.equals(""))) {
				where += " AND wfsp_enddate >= '" + phaseStartDate + " 00:00' ";
		   		filters += "<i>Fecha Fase: </i>" + phaseStartDate + ", ";
		   	}
		    
		    if (!(phaseEndDate.equals(""))) {
				where += " AND wfsp_enddate <= '" + phaseEndDate + " 23:59' ";
		   		filters += "<i>Fecha Fase Fin: </i>" + phaseEndDate + ", ";
		   	}
		    
		    if (!(cancellDate.equals(""))) {
				where += " AND prsa_cancelldate >= '" + cancellDate + " 00:00' ";
		   		filters += "<i>Fecha Cancelaci&oacute;n: </i>" + cancellDate + ", ";
		   	}
		    
		    if (!(cancellDateEnd.equals(""))) {
				where += " AND prsa_cancelldate <= '" + cancellDateEnd + " 23:59' ";
		   		filters += "<i>F. Cancelaci&oacute;n Fin: </i>" + cancellDateEnd + ", ";
		   	}
		    
		    if (!paymentStatus.equals("")) {
		   		where += SFServerUtil.parseFiltersToSql("orde_paymentstatus", paymentStatus);
		   		filters += "<i>Estatus Pago: </i>" + request.getParameter("paymentStatusLabel") + ", ";
		   	}
		    
		  /*  if (prsaSalesUserId > 0) {
				//where += " AND prsa_salesuserid = " + prsaSalesUserId;
				if (sFParams.restrictData(bmoPropertySale.getProgramCode())) {
					where += " AND prsa_salesuserid = " + prsaSalesUserId;
				} else {
					where += " AND ( " +
								" prsa_salesuserid = " + prsaSalesUserId +
								" OR prsa_wflowid IN ( " +
									 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  " +
									 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows on (wflu_wflowid = wflw_wflowid) " +
									 " WHERE wflu_userid = " + prsaSalesUserId + 
							 		 " AND (wflw_callercode = '" + bmoPropertySale.getProgramCode().toString() + "' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' )" + 								 " ) " + 
								 " ) " + 
							 " )";
				}
		   		filters += "<i>Vendedor: </i>" + request.getParameter("prsa_salesuseridLabel") + ", ";
		   	}
		  */
		    //inicio
		       if (prsaSalesUserId > 0) {
		//where += " AND prsa_salesuserid = " + prsaSalesUserId;
		if (sFParams.restrictData(bmoPropertySale.getProgramCode())) {
			where += " AND prsa_salesuserid = " + prsaSalesUserId;
		} else {
			where += " AND ( " +
						" prsa_salesuserid = " + prsaSalesUserId +
						" OR prsa_wflowid IN ( " +
							 " SELECT wflw_wflowid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  ") +
							 " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" on (wflu_wflowid = wflw_wflowid) " +
							 " WHERE wflu_userid = " + prsaSalesUserId + 
							 " AND (wflw_callercode = '" + bmoPropertySale.getProgramCode().toString() + 
							 	"' OR wflw_callercode = '" + bmoOrder.getProgramCode().toString() + "' )" + 
						 " ) " + 
					 " )";
		}
   		filters += "<i>Vendedor: </i>" + request.getParameter("prsa_salesuseridLabel") + ", ";

   	}
		    //fin
		    
		    // Obtener disclosure de datos
		    String disclosureFilters = new PmPropertySale(sFParams).getDisclosureFilters();
		    if (disclosureFilters.length() > 0)
		    	where += " AND " + disclosureFilters;
		    
		    /*
		    //Filtro de permisos propios
		    if (sFParams.restrictData(bmoPropertySale.getProgramCode()) || (prsaSalesUserId > 0)) {
				if (!(prsaSalesUserId > 0)) {
					prsaSalesUserId = sFParams.getLoginInfo().getUserId();
				}
				
		   		filters += "<i>Vendedor: </i>" + request.getParameter("prsa_salesuseridLabel") + ", ";

				// Filtro por asignacion de venta propiedads
				where += " AND ( prsa_salesuserid in (" +
						" select user_userid FROM " + SQLUtil.formatKind(sFParams, " users " +
						" where " + 
						" user_userid = " + prsaSalesUserId +
						" or user_userid in ( " +
						" select u2.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u2 on (u2.user_parentid = u1.user_userid) " +
						" where u1.user_userid = " + prsaSalesUserId +
						" ) " +
						" or user_userid in ( " +
						" select u3.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u2 on (u2.user_parentid = u1.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u3 on (u3.user_parentid = u2.user_userid) " +
						" where u1.user_userid = " + prsaSalesUserId +
						" ) " +
						" or user_userid in ( " +
						" select u4.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u2 on (u2.user_parentid = u1.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u3 on (u3.user_parentid = u2.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u4 on (u4.user_parentid = u3.user_userid) " +
						" where u1.user_userid = " + prsaSalesUserId +
						" ) " +
						" or user_userid in ( " +
						" select u5.user_userid FROM " + SQLUtil.formatKind(sFParams, " users u1 " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u2 on (u2.user_parentid = u1.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u3 on (u3.user_parentid = u2.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u4 on (u4.user_parentid = u3.user_userid) " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " users u5 on (u5.user_parentid = u4.user_userid) " +
						" where u1.user_userid = " + prsaSalesUserId +
						" ) " + 
						" ) " +
						" OR " +
						" ( " +
						" prsa_orderid IN ( " +
						" SELECT wflw_callerid FROM " + SQLUtil.formatKind(sFParams, " wflowusers  " +
						" LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows on (wflu_wflowid = wflw_wflowid) " +
						" WHERE wflu_userid = " + prsaSalesUserId +
						" AND (wflw_callercode = 'PRSA' OR wflw_callercode = 'ORDE') " +
						"   ) " +
						" ) " +
						" ) ";
			}
		    */
		  //abro conexion para inciar el conteo consulta general
		    PmConn pmConnCount= new PmConn(sFParams);
	   		pmConnCount.open();
	   		int count =0;
		       
		    if (!byPhase.equals("finally")) {
		    	sql = " SELECT count(contador) AS contador FROM(Select Count(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " wflows ") +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" ON (wflw_wflowphaseid = wfph_wflowphaseid) " +
						  " INNER JOIN " + SQLUtil.formatKind(sFParams, "wflowsteps")+" ON (wfph_wflowphaseid = wfsp_wflowphaseid) " +
			    		  " INNER JOIN" + SQLUtil.formatKind(sFParams, " propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +	
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = prsa_propertyid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentregistry")+" ON(dvrg_developmentregistryid = prty_developmentregistryid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = ptym_developmentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmenttypes")+" ON(devt_developmenttypeid = deve_developmenttypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u1 ON(u1.user_userid = prsa_salesuserid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = prsa_customerid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON(refe_referralid = cust_referralid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = prsa_orderid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON(wfca_wflowcategoryid = wfty_wflowcategoryid) " +
				          " WHERE prsa_propertysaleid > 0 " + where +		    	 
				          " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
						  " ORDER BY prsa_code ASC)AS alias";

				//ejecuto el sql DEL CONTADOR
				pmConnCount.doFetch(sql);
				if(pmConnCount.next())
					count=pmConnCount.getInt("contador");
			
				System.out.println("contador DE REGISTROS p--> "+count);
 
			    sql = " SELECT prsa_code, prty_code, cust_code, cust_nss, cust_displayname, cust_legalname, cust_customertype, prsa_startdate, prsa_cancelldate, wfty_name,prsa_status," +
			    	  " dvbl_code, prty_street, prty_number, ptym_name, prsa_orderid, prty_habitability, prty_progress, prty_finishdate, orde_total,wflw_wflowid, " +
					  " prsa_description, wfph_name, wfsp_progress, cust_recommendedby, prsa_wflowid, orde_paymentstatus, " +
					  " u1.user_code AS promo, u2.user_code AS coord, u3.user_code AS gerente, u4.user_code AS gerentecom, u5.user_code AS direc " +
					  " FROM " + SQLUtil.formatKind(sFParams, " wflows ") +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" ON (wflw_wflowphaseid = wfph_wflowphaseid) " +
					  " INNER JOIN " + SQLUtil.formatKind(sFParams, "wflowsteps")+" ON (wfph_wflowphaseid = wfsp_wflowphaseid) " +
		    		  " INNER JOIN" + SQLUtil.formatKind(sFParams, " propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +	
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = prsa_propertyid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentregistry")+" ON(dvrg_developmentregistryid = prty_developmentregistryid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = ptym_developmentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmenttypes")+" ON(devt_developmenttypeid = deve_developmenttypeid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u1 ON(u1.user_userid = prsa_salesuserid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = prsa_customerid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON(refe_referralid = cust_referralid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = prsa_orderid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON(wfca_wflowcategoryid = wfty_wflowcategoryid) " +
			          " WHERE prsa_propertysaleid > 0 " + where +		    	 
			          " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
					  " ORDER BY prsa_code ASC";
		    } else {
		    	sql = " SELECT count(contador) AS contador FROM(Select Count(*) AS contador FROM " + SQLUtil.formatKind(sFParams, " wflowsteps ") +
		    			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (wfsp_wflowid = wflw_wflowid) " +
		    			  " INNER JOIN" + SQLUtil.formatKind(sFParams, " propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +
		    			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" ON (wflw_wflowphaseid = wfph_wflowphaseid) " +		    		  	
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = prsa_propertyid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentregistry")+" ON(dvrg_developmentregistryid = prty_developmentregistryid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = ptym_developmentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmenttypes")+" ON(devt_developmenttypeid = deve_developmenttypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u1 ON(u1.user_userid = prsa_salesuserid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = prsa_customerid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON(refe_referralid = cust_referralid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = prsa_orderid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
						  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON(wfca_wflowcategoryid = wfty_wflowcategoryid) " +
					      " WHERE wfsp_progress = 100 " + where +
					      " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
					      " ORDER BY wflw_wflowid )AS alias";
							
							//ejecuto el sql DEL CONTADOR
							pmConnCount.doFetch(sql);
							if(pmConnCount.next())
								count=pmConnCount.getInt("contador");

							System.out.println("contador DE REGISTROS --> "+count);
		    	sql = " SELECT prsa_propertysaleid,prsa_code, prty_code, cust_code, cust_nss, cust_displayname, cust_legalname, cust_customertype, prsa_startdate, prsa_cancelldate, wfty_name,prsa_status," +
			    	  " dvbl_code, prty_street, prty_number, ptym_name, prsa_orderid, prty_habitability, prty_progress, prty_finishdate, orde_total,wflw_wflowid, " +
					  " prsa_description, wfph_name, wfsp_progress, cust_recommendedby, prsa_wflowid, orde_paymentstatus, " +
					  " u1.user_code AS promo, u2.user_code AS coord, u3.user_code AS gerente, u4.user_code AS gerentecom, u5.user_code AS direc, " +
					  " max(wfsp_enddate) AS enddate FROM " + SQLUtil.formatKind(sFParams, " wflowsteps ") +
	    			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflows")+" ON (wfsp_wflowid = wflw_wflowid) " +
	    			  " INNER JOIN" + SQLUtil.formatKind(sFParams, " propertysales")+" ON (wflw_wflowid = prsa_wflowid) " +
	    			  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowphases")+" ON (wflw_wflowphaseid = wfph_wflowphaseid) " +		    		  	
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " properties")+" ON(prty_propertyid = prsa_propertyid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prty_propertymodelid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertytypes")+" ON(ptyp_propertytypeid = ptym_propertytypeid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentblocks")+" ON(dvbl_developmentblockid = prty_developmentblockid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentphases")+" ON(dvph_developmentphaseid = dvbl_developmentphaseid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmentregistry")+" ON(dvrg_developmentregistryid = prty_developmentregistryid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developments")+" ON(deve_developmentid = ptym_developmentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " developmenttypes")+" ON(devt_developmenttypeid = deve_developmenttypeid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u1 ON(u1.user_userid = prsa_salesuserid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u2 ON(u2.user_userid = u1.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u3 ON(u3.user_userid= u2.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u4 ON(u4.user_userid= u3.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" u5 ON(u5.user_userid = u4.user_parentid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON(cust_customerid = prsa_customerid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON(refe_referralid = cust_referralid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = prsa_orderid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowtypes")+" ON(wfty_wflowtypeid = prsa_wflowtypeid) " +
					  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " wflowcategories")+" ON(wfca_wflowcategoryid = wfty_wflowcategoryid) " +
				      " WHERE wfsp_progress = 100 " + where +
				      " GROUP BY wflw_wflowid, wfsp_wflowphaseid " +
				      " ORDER BY wflw_wflowid ";
				      
		    }
		    //System.out.println("sql " + sql);
		    
		    PmConn pmPropertySale = new PmConn(sFParams);
		    pmPropertySale.open();     
		    
		    PmConn pmPhase = new PmConn(sFParams);
		    pmPhase.open();   
		    
		    PmConn pmConn = new PmConn(sFParams);
		    pmConn.open();
		    
		    PmConn pmConnCustRecommendedBy = new PmConn(sFParams);
			pmConnCustRecommendedBy.open();
			
			PmConn pmBitacora = new PmConn(sFParams);
			pmBitacora.open();
		    
		    BmoProgram bmoProgram = new BmoProgram();			
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
		
	//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
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
		
		<body class="default" <%= permissionPrint %> style="padding-right: 10px">
		
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
					<!--<b>Agrupado Por:</b> Vendedor, <b>Ordenado por:</b> Clave -->
				</td>
			<td class="reportDate" align="right">
					Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
				</td>
			</tr>
		</table>
		<table class="report" border="0">
			<tr><td colspan="4">&nbsp;</td></tr>
		</table>
		<%
		//if que muestra el mensajede error
		if(count>sFParams.getBmoSFConfig().getMaxRecords().toInteger()){
			%>
			
					<%=messageTooLargeList %>
			<% 
		}else{
		%>
		<table class="report" border="0">
			<tr class="">		
				<!-- General-->
				<td class="reportHeaderCell" width="3%">#</td>
				<td class="reportHeaderCell">Venta</td>
				<td class="reportHeaderCell">Inmueble</td>
				<td class="reportHeaderCell">Cliente</td>
				<td class="reportHeaderCell">Estatus</td>

				<td class="reportHeaderCell">F. Venta</td>
				<td class="reportHeaderCell">F. Cancelaci&oacute;n</td>

				<td class="reportHeaderCell">Medio Tit.</td>
				<td class="reportHeaderCell">Promotor</td>
				<td class="reportHeaderCell">Coordinador</td>
				<td class="reportHeaderCell">Gerente</td>
				<td class="reportHeaderCell">RG</td>
				<td class="reportHeaderCell">Bit&aacute;cora</td>
				<!-- Precio-->
				<td class="reportHeaderCellRight">Valor Vivienda</td>
				<td class="reportHeaderCell">Promoci&oacute;n</td>
				<td class="reportHeaderCell">Anticipo</td>

			<tr>
		<%
			int i = 1;
			pmPropertySale.doFetch(sql);
			while (pmPropertySale.next()) {
		%>
				<tr class="reportCellEven">
				<!-- General-->
					<%= HtmlUtil.formatReportCell(sFParams, "" + i, BmFieldType.NUMBER) %>
					<%= HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("propertysales","prsa_code"), BmFieldType.CODE) %> 
					<%= HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("properties","prty_code"), BmFieldType.CODE) %> 
					<% 
					String customer = "";
					if (pmPropertySale.getString("customers","cust_customertype").equals("C")) {
						customer = pmPropertySale.getString("customers","cust_code") + " " +
								pmPropertySale.getString("customers","cust_legalname");
					} else {
						customer = pmPropertySale.getString("customers","cust_code") + " " +
								pmPropertySale.getString("customers","cust_displayname");
					}
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, customer, BmFieldType.STRING)) %>
					
					<%
						bmoPropertySale.getStatus().setValue(pmPropertySale.getString("propertysales","prsa_status")); 
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bmoPropertySale.getStatus().getSelectedOption().getLabel(), BmFieldType.STRING)) %> 
					
					<%= HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("propertysales","prsa_startdate"), BmFieldType.DATE) %> 
					<%= HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("propertysales","prsa_cancelldate"), BmFieldType.DATE) %> 
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("wflowtypes","wfty_name"), BmFieldType.STRING)) %> 
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("u1","promo"), BmFieldType.CODE)) %> 
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("u2","coord"), BmFieldType.CODE)) %> 
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, pmPropertySale.getString("u3","gerente"), BmFieldType.CODE)) %> 
					<% 
						String custRecommendedBy = "";
						if(pmPropertySale.getInt("customers", "cust_recommendedby") > 0){
							
							String custR = "SELECT cust_code, cust_displayname FROM " + SQLUtil.formatKind(sFParams, " customers")+" WHERE cust_customerid = " + pmPropertySale.getInt("customers", "cust_recommendedby");
							pmConnCustRecommendedBy.doFetch(custR);
							if(pmConnCustRecommendedBy.next())
								custRecommendedBy = pmConnCustRecommendedBy.getString("cust_code") + " - " + pmConnCustRecommendedBy.getString("cust_displayname");
							
						}
							
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, custRecommendedBy, BmFieldType.STRING)) %> 
					
					<% 
						String bitacora = "";
// 						if(profileId > 0){
							
// 							String bitacoraGrupo = "SELECT wfsp_commentlog FROM " + SQLUtil.formatKind(sFParams, " wflowsteps ") +
// 							" WHERE wfsp_wflowid = " + pmPropertySale.getInt("propertysales","prsa_wflowid") +
// 							" AND wfsp_profileid = " + profileId;
// 							//System.out.println("BITACORA: "+ bitacoraGrupo);
// 							pmBitacora.doFetch(bitacoraGrupo);
// 							if(pmBitacora.next())
// 								bitacora = pmBitacora.getString("wfsp_commentlog");
							
// 						}
						
					%>
					<%= HtmlUtil.stringToHtml(HtmlUtil.formatReportCell(sFParams, bitacora, BmFieldType.STRING)) %> 

					<%= HtmlUtil.formatReportCell(sFParams, "" + pmPropertySale.getDouble("orde_total"), BmFieldType.CURRENCY) %>

					<td class="reportCellText">
					<%
						//PAQUETES DEL PEDIDO DE LA VENTA
						String sqlPrsaPaquetes = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " orderpropertymodelextras ") +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON(orde_orderid = orpx_orderid) " +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodelextras")+" ON(prmx_propertymodelextraid = orpx_propertymodelextraid) " +
												" LEFT JOIN " + SQLUtil.formatKind(sFParams, " propertymodels")+" ON(ptym_propertymodelid = prmx_propertymodelid) " +
												" WHERE orde_orderid = " + pmPropertySale.getInt("propertysales", "prsa_orderid");
										
									
						pmConn.doFetch(sqlPrsaPaquetes);
						String namePaquete = "", prmxName = "";
						double totalPaquete = 0;
						while(pmConn.next()){
							totalPaquete += pmConn.getDouble("orpx_amount");

							namePaquete = pmConn.getString("propertymodelextras", "prmx_name");
							if (!pmConn.getString("orderpropertymodelextras", "orpx_comments").equals(""))
								namePaquete += ": " + pmConn.getString("orderpropertymodelextras", "orpx_comments");
							
							%>
								<li><%= HtmlUtil.stringToHtml(namePaquete)%></li>
							<%
						}
									
					%>
					</td>
					<td class="reportCellText">
						<%
							//OC Comisiones - Monto 
							String sqlAnticipo = "SELECT * FROM " + SQLUtil.formatKind(sFParams, " requisitions ") +
													" LEFT JOIN " + SQLUtil.formatKind(sFParams, " requisitiontypes")+" ON(rqtp_requisitiontypeid = reqi_requisitiontypeid) " +
													" LEFT JOIN " + SQLUtil.formatKind(sFParams, " suppliers")+" ON (supl_supplierid = reqi_supplierid)  " +
													" WHERE reqi_orderid > 0 " +
													" AND reqi_orderid = " + pmPropertySale.getInt("propertysales", "prsa_orderid") +
													" AND reqi_status = '" + BmoRequisition.STATUS_AUTHORIZED + "' " +
													" AND reqi_paymentstatus = '" +BmoRequisition.PAYMENTSTATUS_TOTAL + "' " +
												    " AND rqtp_type = '" + BmoRequisitionType.TYPE_COMMISION + "' " +
												    " AND supl_type = '" + BmoSupplier.TYPE_BROKER + "' " +
												    " ORDER BY reqi_requisitionid ASC";
											
										
							pmConn.doFetch(sqlAnticipo);
							String oc = "", supl = "";
							double montoOC = 0;
							while(pmConn.next()){
								oc = pmConn.getString("reqi_code") + " " + pmConn.getString("reqi_name");
								supl = pmConn.getString("supl_code") + " " + pmConn.getString("supl_name");
								montoOC = pmConn.getDouble("reqi_amount");
							%>
								<li><%= HtmlUtil.stringToHtml(oc)%> | <%= HtmlUtil.stringToHtml(supl)%> | <%= formatCurrency.format(montoOC)%></li>
							<%
							}
						%>
					</td>
				</tr>                              
		
		<%
				i++;
			}
		
		}// FIN DEL CONTADOR

		
	}
	pmConnCustRecommendedBy.close();
	pmBitacora.close();
	pmConn.close();
	pmPhase.close();
	pmPropertySale.close();
	pmConnCount.close();
%>
		</table>    

		<% if (print.equals("1")) { %>
		<script>
			//window.print();
		</script>
		<% }
		System.out.println("\n  Fin reporte- Fecha: " +  SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) 
		+ " Reporte: "+title
		+ " Usuario: " + sFParams.getLoginInfo().getEmailAddress()); 
		%>

  </body>
</html>