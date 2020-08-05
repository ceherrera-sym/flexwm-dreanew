//package com.flexwm.server.cm;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import com.flexwm.server.wf.PmWFlowLog;
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectStep;
//import com.flexwm.shared.wf.BmoWFlowLog;
//import com.symgae.server.HtmlUtil;
//import com.symgae.server.PmConn;
//import com.symgae.server.PmJoin;
//import com.symgae.server.PmObject;
//import com.symgae.server.SFSendMail;
//import com.symgae.server.SFServerUtil;
//import com.symgae.server.sf.PmProfile;
//import com.symgae.server.sf.PmUser;
//import com.symgae.shared.BmObject;
//import com.symgae.shared.BmUpdateResult;
//import com.symgae.shared.SFException;
//import com.symgae.shared.SFMailAddress;
//import com.symgae.shared.SFParams;
//import com.symgae.shared.SFPmException;
//import com.symgae.shared.sf.BmoProfile;
//import com.symgae.shared.sf.BmoUser;
//
//public class PmProjectActivities extends PmObject{
//	BmoProjectActivities bmoProjectActivities;
//	
//	public PmProjectActivities(SFParams sFParams) throws SFPmException {
//		super(sFParams);
//		bmoProjectActivities = new BmoProjectActivities();
//		setBmObject(bmoProjectActivities);
//
//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
//				new PmJoin(bmoProjectActivities.getUserId(), bmoProjectActivities.getBmoUser()),
//				new PmJoin(bmoProjectActivities.getBmoUser().getAreaId(), bmoProjectActivities.getBmoUser().getBmoArea()),
//				new PmJoin(bmoProjectActivities.getBmoUser().getLocationId(), bmoProjectActivities.getBmoUser().getBmoLocation()),
//				new PmJoin(bmoProjectActivities.getProfileId(), bmoProjectActivities.getBmoProfile()),
//				new PmJoin(bmoProjectActivities.getStepProjectId(), bmoProjectActivities.getBmoProjectStep()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getCustomerId(),bmoProjectActivities.getBmoProjectStep().getBmoCustomer()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getBmoCustomer().getTerritoryId(), bmoProjectActivities.getBmoProjectStep().getBmoCustomer().getBmoTerritory()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getBmoCustomer().getReqPayTypeId(), bmoProjectActivities.getBmoProjectStep().getBmoCustomer().getBmoReqPayType()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getWFlowId(), bmoProjectActivities.getBmoProjectStep().getBmoWFlow()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getWFlowTypeId(), bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getBmoWFlowType()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getWFlowPhaseId(), bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getBmoWFlowPhase()),
//				new PmJoin(bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getWFlowFunnelId(), bmoProjectActivities.getBmoProjectStep().getBmoWFlow().getBmoWFlowFunnel())
//				)));		
//
//	}
//	@Override
//	public BmObject populate(PmConn pmConn) throws SFException {
//		BmoProjectActivities bmoProjectActivities = (BmoProjectActivities) autoPopulate(pmConn, new BmoProjectActivities());
//		
//		BmoProfile bmoProfile = new BmoProfile();
//		int profileId = (int)pmConn.getInt(bmoProjectActivities.getIdFieldName());
//		if (profileId > 0 )bmoProjectActivities.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
//		else bmoProjectActivities.setBmoProfile(bmoProfile);
//		
//		BmoUser bmoUser = new BmoUser();
//		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
//		if (userId > 0) bmoProjectActivities.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
//		else bmoProjectActivities.setBmoUser(bmoUser);		
//		
//		BmoProjectStep bmoProjectStep = new BmoProjectStep();
//		int projectStepid = (int)pmConn.getInt(bmoProjectStep.getIdFieldName());
//		if (projectStepid > 0) bmoProjectActivities.setBmoProjectStep((BmoProjectStep) new PmProjectStep(getSFParams()).populate(pmConn));
//		else bmoProjectActivities.setBmoProjectStep(bmoProjectStep);	
//	
//		return bmoProjectActivities;
//
//	}
//	@Override
//	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
//		BmoProjectActivities bmoProjectActivities = (BmoProjectActivities)bmObject;
//		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
//		
//		//Version guardada
//		BmoProjectActivities bmoProjectActivitiesPrev  = new BmoProjectActivities();
//		
//		
//		// Se almacena de forma preliminar para asignar Clave
//		if (!(bmoProjectActivities.getId() > 0)) {
//			
//			//ASignar numero de tarea
//			bmoProjectActivities.getNumber().setValue(getSteps(pmConn,bmoProjectActivities.getStepProjectId().toInteger()));
//			super.save(pmConn, bmoProjectActivities, bmUpdateResult);
//			bmoProjectActivities.setId(bmUpdateResult.getId());
//			pmWFlowLog.addDataLog(pmConn,
//					bmUpdateResult, bmoProjectActivities.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, "Se Creo la Actividad del Proyecto - "
//							+ bmoProjectActivities.getName().toString(), "");			
//			
//		}else {
//			bmoProjectActivitiesPrev = (BmoProjectActivities)get(bmoProjectActivities.getId());
//			
//			//		se cambia el avance 
//			if(!bmoProjectActivities.getProgress().toString().equals(bmoProjectActivitiesPrev.getProgress().toString())) {
//				if(bmoProjectActivities.getBmoProjectStep().getStatus().equals(BmoProjectStep.STATUS_AUTHORIZED)) {
//					//Validar dependencia de actividades
//					validateDep(pmConn, bmUpdateResult, bmoProjectActivitiesPrev.getId()).errorsToString();	
//					//Validar datos obligatorios
//					validateData(pmConn,bmUpdateResult,bmoProjectActivities);
//					if(!bmUpdateResult.hasErrors()) {
//						pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//								"Se cambio el avance de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(), " ");
//					}	
//				}else {
//					bmUpdateResult.addError(bmoProjectActivities.getProgress().getName(),"El Proyecto aún no está Autorizado");
//				}
//				
//			}
//			if(bmoProjectActivities.getUserId().toInteger() > 0) {
//				//cambio de recurso
//				if(!bmoProjectActivities.getUserId().toString().equals(bmoProjectActivitiesPrev.getUserId().toString())) {
//					BmoProjectStep bmoProjectStep = new BmoProjectStep();
//					PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//					bmoProjectStep = (BmoProjectStep)pmProjectStep.get(bmoProjectActivitiesPrev.getStepProjectId().toInteger());
//					if(bmoProjectActivitiesPrev.getUserId().toInteger() > 0) {
//						pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//								"Se cambio el recurso de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(),"");
//						if(bmoProjectStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()) {
//							printDevLog("Mandando Email a el usuario seleccionado");
//							sendMailUser(pmConn,bmoProjectActivities.getUserId().toInteger(),bmoProjectActivities);
//						}else {
//							printDevLog("No se mando Email a el usuario seleccionado");
//						}
//					}else {
//						pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//								"Se asigno el recurso de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(),"");
//						if(bmoProjectStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()) {
//							printDevLog("Mandando Email a el usuario seleccionado");
//							sendMailUser(pmConn,bmoProjectActivities.getUserId().toInteger(),bmoProjectActivities);
//						}else {
//							printDevLog("No se mando Email a el usuario seleccionado");
//						}
//					}
//				}
//			}
//			
//			//cambio de Grupo
//			if(bmoProjectActivities.getProfileId().toInteger() > 0) {
//				if(!bmoProjectActivities.getProfileId().toString().equals(bmoProjectActivitiesPrev.getProfileId().toString())) {
//					if(bmoProjectActivitiesPrev.getProfileId().toInteger() > 0) {
//						pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//								"Se cambio el grupo de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(),"");
//					}else {
//						pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//								"Se asigno el grupo de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(),"");
//					}
//				}
//			}
//			//cambio de fecha inicio
//			if(!bmoProjectActivities.getStartDate().toString().equals(bmoProjectActivitiesPrev.getStartDate().toString())) {
//
//				pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//						"Se cambio la fecha de inicio de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(),"");
//
//			}
//			//cambio fecha fin
//			if(!bmoProjectActivities.getEndDate().toString().equals(bmoProjectActivitiesPrev.getEndDate().toString())) {
//				if(!SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), bmoProjectActivities.getEndDate().toString(),
//						SFServerUtil.addDays(getSFParams().getDateFormat(), bmoProjectActivitiesPrev.getBmoProjectStep().getLockEnd().toString(), 1))) {
//					bmUpdateResult.addError(bmoProjectActivities.getEndDate().getName(), "La F.Fin no puede ser mayor a la F.Fin del Proyecto.");
//				}else if(SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), bmoProjectActivities.getEndDate().toString(),
//						SFServerUtil.addDays(getSFParams().getDateFormat(), bmoProjectActivities.getStartDate().toString(), 1))) {
//					bmUpdateResult.addError(bmoProjectActivities.getEndDate().getName(), "La F.Fin no puede ser menor a la F.Inicio.");
//				}else {
//					pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivitiesPrev.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, 
//							"Se cambio la fecha fin de la actividad No." + bmoProjectActivitiesPrev.getNumber() + " " + bmoProjectActivitiesPrev.getName(),"");
//				}
//			}
//			super.save(pmConn, bmoProjectActivities, bmUpdateResult);		
//				if(!bmUpdateResult.hasErrors())
//					bmoProjectActivities = (BmoProjectActivities)bmUpdateResult.getBmObject();				
//
//					activeActivities(pmConn, bmoProjectActivities);	
//
//					updateDependecies(pmConn,bmoProjectActivities.getId());	
//			
//		}
//		
//		return bmUpdateResult;
//	}
//	@Override
//	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
//		bmoProjectActivities = (BmoProjectActivities)bmObject;
//		
//		if(bmoProjectActivities.getProgress().toInteger() > 0)
//			bmUpdateResult.addError(bmoProjectActivities.getProgress().getName(), "No se puede eliminar la Tarea, ya fue avanzada");		
//		
//		if(!bmUpdateResult.hasErrors()) {
//			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
//			pmWFlowLog.addDataLog(bmUpdateResult, bmoProjectActivities.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA, 
//					"Se elimino la actividad No." + bmoProjectActivities.getNumber() + " " + bmoProjectActivities.getName(), "");
//		}
//		
//		checkDependences(pmConn,bmUpdateResult,bmoProjectActivities.getId());
//		
//		super.delete(pmConn, bmoProjectActivities, bmUpdateResult);
//		
//		return bmUpdateResult;
//	}
//	//llena campo de dependencia para mostrar en listado
//	public void updateDependecies(PmConn pmConn,int id) throws SFException{
//		boolean isAdvanced = true;
//	
//		String sql = "SELECT psdp_projectactivitieid FROM projectactivitiesdeps WHERE psdp_childprojectactivityid = " 
//				+ id +" ORDER BY psdp_projectactivitieid ASC";
//		String result = "";
//		BmoProjectActivities bmoProjectActivities = new BmoProjectActivities();
//		
//		pmConn.doFetch(sql);
//		while(pmConn.next()) {			
//			bmoProjectActivities = (BmoProjectActivities)this.get(pmConn.getInt("psdp_projectactivitieid"));
//			result += bmoProjectActivities.getNumber() + " ,";
//			if(isAdvanced) {
//				if(!(bmoProjectActivities.getProgress().toInteger() == 100))
//					isAdvanced = false;
//			}
//		}
//		sql = "UPDATE projectactivities set prac_dependencies = '"+ result + "' WHERE prac_projectactivitiesid = " +id;
//		pmConn.doUpdate(sql);
//
//		if(!isAdvanced) {
//			sql = "UPDATE projectactivities SET prac_status ='" + BmoProjectActivities.STATUS_INACTIVE + "' WHERE prac_projectactivitiesid = " + id;
//			pmConn.doUpdate(sql);
//		}
//	}
//	//activar tareas si dependen de la actual
//	public void  activeActivities(PmConn pmConn,BmoProjectActivities bmoProjectActivitie) throws SFException	{
//		//si el progreso es 100
//		if(bmoProjectActivitie.getProgress().toInteger() == 100) {
//			PmConn pmConn2 = new PmConn(getSFParams());
//			pmConn2.open();
//			BmoProjectActivities bmoProjectActivities2 = new BmoProjectActivities();
//			printDevLog("ID :::::::: " + bmoProjectActivitie.getId());
//			pmConn.doFetch("SELECT * FROM projectactivitiesdeps WHERE psdp_projectactivitieid = " +  bmoProjectActivitie.getId() 
//			+ " ORDER BY psdp_childprojectactivityid ASC");
//			while (pmConn.next()) {
//				boolean isAdvanced = true;
//				bmoProjectActivities2 = (BmoProjectActivities)this.get(pmConn.getInt("psdp_childprojectactivityid"));
//				printDevLog("Actividad que depende ::: " + bmoProjectActivities2.getNumber());
//				String sql = "SELECT psdp_projectactivitieid,prac_progress,prac_number FROM projectactivitiesdeps LEFT JOIN projectactivities ON(psdp_projectactivitieid = prac_projectactivitiesid)"
//						+ " WHERE psdp_childprojectactivityid = " 
//						+ bmoProjectActivities2.getId() 
//						+ " AND prac_projectactivitiesid <> " + bmoProjectActivitie.getId() + " ORDER BY psdp_projectactivitieid ASC";
//				printDevLog("Consulta sql ::: " + sql);
//				pmConn2.doFetch( sql);
//				while(pmConn2.next()) {
//					if(pmConn2.getInt("prac_progress") < 100) {
//						isAdvanced = false;
//					}
//					printDevLog("NUMERO DE TAREA ::::: " + pmConn2.getInt("prac_number"));
//					printDevLog("AVANCE DE TAREA ::::: " + pmConn2.getInt("prac_progress"));
//				}
//				if(isAdvanced) {
//					sql = " UPDATE projectactivities SET prac_status = '" + BmoProjectActivities.STATUS_ACTIVE + "' WHERE prac_projectactivitiesid = " + bmoProjectActivities2.getId(); 
//					printDevLog("CONSULTA UPDATE :::: " + sql);
//					pmConn2.doUpdate(sql);
//				}
//				
//			}
//			pmConn2.close();
//		}else {
//			
//				PmConn pmConn2 = new PmConn(getSFParams());
//				pmConn2.open();
//				BmoProjectActivities bmoProjectActivities2 = new BmoProjectActivities();
//			
//				pmConn.doFetch("SELECT * FROM projectactivitiesdeps WHERE psdp_projectactivitieid = " +  bmoProjectActivitie.getId() 
//				+ " ORDER BY psdp_childprojectactivityid ASC");
//				while (pmConn.next()) {				
//					bmoProjectActivities2 = (BmoProjectActivities)this.get(pmConn.getInt("psdp_childprojectactivityid"));			
//					String sql = " UPDATE projectactivities SET prac_status = '" + BmoProjectActivities.STATUS_INACTIVE + "' WHERE prac_projectactivitiesid = " + bmoProjectActivities2.getId(); 
//					printDevLog("CONSULTA UPDATE :::: " + sql);
//					pmConn2.doUpdate(sql);
//				}
//				pmConn2.close();
//			
//		}
//	}
//	//Validar dependencia de actividades
//	private BmUpdateResult validateDep(PmConn pmConn,BmUpdateResult bmUpdateResult,int activityId) throws SFException {
//		String sql = "SELECT psdp_projectactivitieid FROM projectactivitiesdeps WHERE psdp_childprojectactivityid = " + activityId ;
//		
//		pmConn.doFetch(sql);
//		while(pmConn.next()) {
//			BmoProjectActivities bmoProjectActivitiesParent = new BmoProjectActivities();
//			bmoProjectActivitiesParent = (BmoProjectActivities)this.get(pmConn.getInt("psdp_projectactivitieid"));
//			if(!(bmoProjectActivitiesParent.getProgress().toInteger() == 100)){
//				bmUpdateResult.addError(bmoProjectActivities.getProgress().getName(), "La Actividad No. " + bmoProjectActivitiesParent.getNumber() 
//				+ " " +bmoProjectActivitiesParent.getName() + " no ha sido avanzada al 100%" );
//			}			
//			
//		}		
//		return bmUpdateResult;		
//	}
//	//Validar datos obligatorios
//	private BmUpdateResult validateData(PmConn pmConn,BmUpdateResult bmUpdateResult,BmoProjectActivities bmoProjectActivities) {
//		if(!(bmoProjectActivities.getProfileId().toInteger() > 0))
//			bmUpdateResult.addError(bmoProjectActivities.getProfileId().getName(), "Se debe asignar un Perfil");
//		
//		if(!(bmoProjectActivities.getUserId().toInteger() > 0))
//			bmUpdateResult.addError(bmoProjectActivities.getUserId().getName(), "Se debe asignar un Recurso");
//		
//		if(!(bmoProjectActivities.getEstimatedHours().toInteger() > 0))
//			bmUpdateResult.addError(bmoProjectActivities.getEstimatedHours().getName(), "Se debe asignar un estimado de horas");
//	
//		if((bmoProjectActivities.getEndDate().equals("")))
//			bmUpdateResult.addError(bmoProjectActivities.getEndDate().getName(), "Se debe asignar la fecha fin");
//		
//		if((bmoProjectActivities.getStartDate().equals("")))
//			bmUpdateResult.addError(bmoProjectActivities.getStartDate().getName(), "Se debe asignar la fecha inicio");
//		
//		return bmUpdateResult;
//		
//	}
//	//Checar si alguna tarea depende de la que se elimina
//	public BmUpdateResult checkDependences(PmConn pmConn,BmUpdateResult bmUpdateResult,int projectActivityId) throws SFPmException {
//		String sql = "SELECT prac_number,prac_name FROM projectactivitiesdeps LEFT JOIN projectactivities ON(psdp_projectactivitieid = prac_projectactivitiesid) " 
//					 + "WHERE psdp_projectactivitieid =  " + projectActivityId;
//		pmConn.doFetch(sql);
//		
//		while(pmConn.next()) {
//			bmUpdateResult.addMsg("La Actividad No." + pmConn.getString("prac_number") + " - " + pmConn.getString("prac_name") + ", depende de esta Actividad");			
//		}
//		
//		return bmUpdateResult;
//		
//	}
//	//obtener numero de tarea
//	private int getSteps(PmConn pmConn,int stepProyject) throws SFPmException {
//		int result = 0;
//		String sql = "SELECT prac_number FROM projectactivities WHERE prac_stepprojectid = " + stepProyject+" ORDER BY prac_projectactivitiesid ASC";
//		pmConn.doFetch(sql);
//		
//		while(pmConn.next()) {
//			result = pmConn.getInt("prac_number");
//		}
//		
//		if ((result > 0)) {
//			return result + 1 ;
//		}else {
//			return 1;
//		}
//		
//	}
//	public void sendMailUser(PmConn pmConn,int userId,BmoProjectActivities bmoProjectActivities) throws SFException {
//		BmoUser bmoUser = new BmoUser();
//		PmUser pmUser = new PmUser(getSFParams());
//		PmProjectStep pmProjectStep = new PmProjectStep(getSFParams());
//		BmoProjectStep bmoProjectStep = (BmoProjectStep)pmProjectStep.get(bmoProjectActivities.getStepProjectId().toInteger());
//		bmoUser = (BmoUser)pmUser.get(userId);
//		if(bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
//			ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
//
//			mailList.add(new SFMailAddress(bmoUser.getEmail().toString(),
//					bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));		
//
//			String subject = getSFParams().getAppCode() + " Asignaci&oacute de Tarea de Proyecto ";			
//			
//			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Asignaci&oacute;n de Tarea " 	,
//					" 	<p style=\"font-size:12px\"> " 
//							+ " <b>Proyecto:</b> " + bmoProjectStep.getCode().toString()
//							+ " " + bmoProjectStep.getName().toString()
//							+ " <br> " + " <b>Tarea:</b> " + bmoProjectActivities.getNumber().toString() + " " + bmoProjectActivities.getName().toString())
//							+ " <br>"
//							+ "	<p align=\"center\" style=\"font-size:12px\"> "
//							+ "	Favor de dar Seguimiento a la Tarea ";
//			if(getSFParams().isProduction()) {
//				SFSendMail.send(getSFParams(), mailList,  getSFParams().getBmoSFConfig().getEmail().toString(),
//						getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
//			}else {
//				System.out.println(this.getClass().getName() + "-sendMailUser(): Se envia correo: "
//						+ "De: " + getSFParams().getBmoSFConfig().getEmail().toString()
//						+ "Asunto: " + subject
//						+ "Cuerpo: " + msgBody
//						);
//			}
//		}
//
//	}
//
//}
