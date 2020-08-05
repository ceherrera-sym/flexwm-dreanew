/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmProfile;
import com.symgae.server.sf.PmUser;
import com.symgae.server.sf.PmProfileUser;
import com.flexwm.server.PmFlexConfig;
import com.flexwm.server.wf.PmWFlowUser;
import com.symgae.server.PmConn;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowUser;


public class PmWFlowUser extends PmObject {
	BmoWFlowUser bmoWFlowUser;

	public PmWFlowUser(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowUser = new BmoWFlowUser();
		setBmObject(bmoWFlowUser);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowUser.getWFlowId(), bmoWFlowUser.getBmoWFlow()),
				new PmJoin(bmoWFlowUser.getBmoWFlow().getWFlowTypeId(), bmoWFlowUser.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWFlowUser.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowUser.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowUser.getBmoWFlow().getWFlowPhaseId(), bmoWFlowUser.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWFlowUser.getBmoWFlow().getWFlowFunnelId(), bmoWFlowUser.getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin(bmoWFlowUser.getProfileId(), bmoWFlowUser.getBmoProfile()),
				new PmJoin(bmoWFlowUser.getUserId(), bmoWFlowUser.getBmoUser()),
				new PmJoin(bmoWFlowUser.getBmoUser().getLocationId(), bmoWFlowUser.getBmoUser().getBmoLocation()),
				new PmJoin(bmoWFlowUser.getBmoUser().getAreaId(), bmoWFlowUser.getBmoUser().getBmoArea())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowUser = (BmoWFlowUser)autoPopulate(pmConn, new BmoWFlowUser());

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		if (pmConn.getInt(bmoWFlow.getIdFieldName()) > 0) 
			bmoWFlowUser.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else 
			bmoWFlowUser.setBmoWFlow(bmoWFlow);
		
		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if (pmConn.getInt(bmoUser.getIdFieldName()) > 0) 
			bmoWFlowUser.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else 
			bmoWFlowUser.setBmoUser(bmoUser);

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoWFlowUser.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoWFlowUser.setBmoProfile(bmoProfile);

		return bmoWFlowUser;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowUser = (BmoWFlowUser)bmObject;	
		
		BmoWFlowUser bmoWFlowUserPrevious = new BmoWFlowUser();
		PmWFlowUser pmFlowUser = new PmWFlowUser(getSFParams());
		// leer valor anterior para cambio de bitacora
		if (bmoWFlowUser.getId() > 0)
			bmoWFlowUserPrevious = (BmoWFlowUser)pmFlowUser.get(pmConn, bmoWFlowUser.getId());
		
		//Validar que no se pueda repetir el perfil en una flujo 
		if(!(bmoWFlowUser.getId()>0)) {

			String contador = " SELECT COUNT(prof_profileid) AS contador FROM wflowusers " + 
					" LEFT JOIN wflows ON (wflu_wflowid = wflw_wflowid)  LEFT JOIN " + 
					" wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid)  LEFT JOIN " + 
					" wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid)  LEFT JOIN" + 
					" wflowphases ON (wflw_wflowphaseid = wfph_wflowphaseid)  LEFT JOIN " + 
					" wflowfunnels ON (wflw_wflowfunnelid = wflf_wflowfunnelid)  LEFT JOIN " + 
					" profiles on (wflu_profileid = prof_profileid)  " + 
					" LEFT JOIN users on (wflu_userid = user_userid) "+
					" WHERE (  ( prof_profileid = "+bmoWFlowUser.getProfileId().toInteger()+"  "
							+ " AND user_userid = "+bmoWFlowUser.getUserId().toInteger()+")  ) "+
							" AND wflu_wflowid = "+bmoWFlowUser.getWFlowId().toInteger()+";";

			pmConn.doFetch(contador);
			while(pmConn.next()) {
				if(pmConn.getInt("contador")>0) {
					bmUpdateResult.addMsg("<b>Ya existe éste usuario en éste perfil.</b>");
				
				}else {
					break;
				}
			}
		}else {
			String sql2= " SELECT wflu_wflowuserid FROM wflowusers " 
										+ " LEFT JOIN wflows ON (wflu_wflowid = wflw_wflowid)  "
										+ " LEFT JOIN  wflowtypes ON (wflw_wflowtypeid = wfty_wflowtypeid)  "
										+ " LEFT JOIN wflowcategories ON (wfty_wflowcategoryid = wfca_wflowcategoryid) "
										+ " LEFT JOIN  wflowphases ON (wflw_wflowphaseid = wfph_wflowphaseid) "
										+ " LEFT JOIN  wflowfunnels ON (wflw_wflowfunnelid = wflf_wflowfunnelid)  "
										+ " LEFT JOIN  profiles on (wflu_profileid = prof_profileid) " 
										+ " LEFT JOIN users on (wflu_userid = user_userid) " 
										+ " WHERE  prof_profileid = "+bmoWFlowUser.getProfileId().toInteger()+
										" AND user_userid = "+bmoWFlowUser.getUserId().toInteger()+
										" AND wflu_wflowid = "+bmoWFlowUser.getWFlowId().toInteger()+" ;";	
												
			pmConn.doFetch(sql2);
			PmConn pmConn2 = new PmConn(getSFParams());
			pmConn2.open();
			while(pmConn.next()) {
				int id = pmConn.getInt("wflu_wflowuserid");
			
				if(id!=bmoWFlowUser.getId()) {
					bmUpdateResult.addMsg("<b>Ya existe éste usuario en éste perfil.</b>");
					break;
				}
						}
			pmConn2.close();
		}
			
		// Obtener el usuario
		if (!(bmoWFlowUser.getUserId().toInteger() > 0))
			bmUpdateResult.addMsg("<b>No se puede modificar el Colaborador: verificar que esté asignado el Usuario correspondiente.</b>");

		BmoUser bmoUser = new BmoUser();
		PmUser pmUser = new PmUser(getSFParams());
		bmoUser = (BmoUser)pmUser.get(pmConn, bmoWFlowUser.getUserId().toInteger());
		bmoWFlowUser.setBmoUser(bmoUser);

		// Revisar si hay bloqueo de asignacion en el usuario que se busca agregar, si es nuevo registro
		if (!(bmoWFlowUser.getId() > 0)) {
			PmWFlowUserSelect pmWFlowUserSelect = new PmWFlowUserSelect(getSFParams());
			if (!pmWFlowUserSelect.canAssign(pmConn, bmoUser))
				bmUpdateResult.addError(bmoWFlowUser.getUserId().getName(), 
						"No tiene permisos para asignar Colaborardores del Departamento " + bmoUser.getBmoArea().getName());
		}

		// Si esta apartado en la fecha se asigna como conflicto
		if (isLocked(pmConn, bmoWFlowUser)) 
			bmoWFlowUser.getLockStatus().setValue(BmoWFlowUser.LOCKSTATUS_CONFLICT);

		super.save(pmConn, bmoWFlowUser, bmUpdateResult);

		if (!bmUpdateResult.hasErrors()) {
//			PmCommission pmCommission = new PmCommission(getSFParams());

			// Si maneja comision el producto activar el checkbox
			/*if(pmCommission.hasCommissionByGRUP(pmConn, bmoWFlowUser.getBmoProfile(), bmUpdateResult)) {
				bmoWFlowUser.getCommission().setValue(true);

				//Asignar el porcentage que le corresponde
				double percentage = pmCommission.getPercentageCommissionByGRUP(pmConn, bmoWFlowUser.getBmoProfile(), bmUpdateResult);
				bmoWFlowUser.getPercentageCommission().setValue(percentage);
			}*/

			// Actualizar registros por asignación de usuario
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = new BmoWFlow();
			bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowUser.getWFlowId().toInteger());
			
			// Revisa si el usuario esta disponible en las fechas
			PmWFlowUserBlockDate pmWFlowUserBlockDate = new PmWFlowUserBlockDate(getSFParams());
			if (pmWFlowUserBlockDate.userInBlockedDates(pmConn, bmoWFlowUser.getUserId().toInteger(), bmoWFlow.getStartDate().toString(), bmoWFlow.getEndDate().toString(), bmUpdateResult)) {
				bmUpdateResult.addError(bmoWFlowUser.getLockStart().getName(), "El Usuario tiene bloqueo manual en esas Fechas.");
			}
			// Generar bitacora	si esta cambiando el usuario		
			if (bmoWFlowUserPrevious.getUserId().toInteger() != bmoWFlowUser.getUserId().toInteger()) {
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW,
						"Se Agrega/Modifica asignación del Colaborador: " + bmoWFlowUser.getBmoUser().getEmail());
			}
			// Actualizar asignacion de usuario de tareas
			if (bmoWFlowUser.getAssignSteps().toBoolean()) {
				PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());
				pmWFlowStep.updateWFlowStepUsers(pmConn, bmoWFlow, bmoWFlowUser.getProfileId().toInteger(), bmoWFlowUser.getUserId().toInteger(), bmUpdateResult);
			}

			// Actualiza calendarios google si esta permitido en la categoria
			if (bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarSync().toBoolean())
				updateGoogleCalendar(pmConn, bmoWFlow, bmoWFlowUser, bmUpdateResult);

			//Actualizar el cobrador en las CxC
			// MultiEmpresa: g100
			int collectProfileId = 0;
			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getMultiCompany().toBoolean() ) {
				// Buscar perfil de cobranza por multiempresa
				PmFlexConfig pmFlexConfig = new PmFlexConfig(getSFParams());
				collectProfileId = pmFlexConfig.getCollectProfileId(pmConn, bmoWFlowUser.getBmoWFlow().getCompanyId().toInteger());
			} else {
				// Buscar perfil de cobranza asignado en conf. flex
				collectProfileId = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
			}
			if (collectProfileId > 0) {
				if (bmoWFlowUser.getProfileId().toInteger() == collectProfileId) {
					updateCollectorInRaccount(pmConn, bmoWFlowUser, bmUpdateResult);
				}
			}

			//Actualizar el cobrador en las CxC
//			if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger() > 0) {
//				int collectGroupId = ((BmoFlexConfig) getSFParams().getBmoAppConfig()).getCollectProfileId().toInteger();
//				if (bmoWFlowUser.getProfileId().toInteger() == collectGroupId) {
//					updateCollectorInRaccount(pmConn, bmoWFlowUser, bmUpdateResult);
//				}
//			}

			// Se vuelve a almacenar para actualizar gEventId
			super.save(pmConn, bmoWFlowUser, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Actualiza calendarios google
	public void updateGoogleCalendar(PmConn pmConn, BmoWFlow bmoWFlow, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException {
		// Revisa que este asignada la fecha de inicio y que el flujo tenga habilitado calendarios google
		if (bmoWFlowUser.getLockStart().toString().length() > 0
				&& bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarSync().toBoolean()) {
			// No hay errores, proceder a crear calendario google
			updateUserGEvent(pmConn, bmoWFlowUser.getBmoUser().getGoogleid().toString(), bmoWFlow, bmoWFlowUser, bmUpdateResult);

			// Tambien crear el calendario publico de usuario para el usuario de sistema
			updatePublicGEvent(pmConn, bmoWFlow, bmoWFlowUser, bmUpdateResult);	

			// Tambien crear el calendario para el usuario de sistema
			updateAreaGEvent(pmConn, bmoWFlow, bmoWFlowUser, bmUpdateResult);	
		}
	}

	//Actualizar el cobrador en las CxC
	public void updateCollectorInRaccount(PmConn pmConn, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		// CxC pendientes y que no tengan pagos
		sql = " UPDATE " + formatKind("raccounts") + 
				" LEFT JOIN " + formatKind("orders") + " ON (racc_orderid = orde_orderid) " +			  
				" SET racc_collectuserid = " + bmoWFlowUser.getUserId().toInteger() +
				" WHERE orde_wflowid = " + bmoWFlowUser.getWFlowId().toInteger() +
				" AND racc_payments = 0 " +
				" AND racc_paymentstatus = '" + BmoRaccount.PAYMENTSTATUS_PENDING + "'";
		pmConn.doUpdate(sql);
	}

	// Obtener el usuario asignado del grupo
	public BmoUser getUserByGroup(PmConn pmConn, int wFlowId, BmoProfile bmoProfile, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowUser = new BmoWFlowUser();
		pmConn.doFetch("SELECT wflu_wflowuserid FROM " + formatKind("wflowusers")
		+ " WHERE wflu_wflowid = " + wFlowId 
		+ " AND wflu_userid > 0 "
		+ " AND wflu_profileid = " + bmoProfile.getId());
		if (pmConn.next()) {
			bmoWFlowUser = (BmoWFlowUser)this.get(pmConn, pmConn.getInt("wflu_wflowuserid"));
		} else {
			bmUpdateResult.addMsg("El Grupo " + bmoProfile.getName().toString() + " no tiene Usuario/Colaborador Vinculado.");			
		}

		return bmoWFlowUser.getBmoUser();
	}

	// Busca usuarios de flujo sin usuario asignado
	public BmoWFlowUser getUnassignedByGroup(PmConn pmConn, int wFlowId, int profileId) throws SFException {
		bmoWFlowUser = new BmoWFlowUser();

		pmConn.doFetch("SELECT wflu_wflowuserid FROM " + formatKind("wflowusers")
		+ " WHERE wflu_wflowid = " + wFlowId 
		+ " AND wflu_profileid = " + profileId
		+ " AND wflu_userid IS NULL");
		if (pmConn.next()) {
			bmoWFlowUser = (BmoWFlowUser)this.get(pmConn, pmConn.getInt("wflu_wflowuserid"));
		}

		return bmoWFlowUser;
	}

	// Busca usuarios de flujo con o sin usuario asignado
	public BmoWFlowUser getByGroup(PmConn pmConn, int wFlowId, int userId, int profileId) throws SFException {
		bmoWFlowUser = new BmoWFlowUser();

		pmConn.doFetch("SELECT wflu_wflowuserid FROM " + formatKind("wflowusers")
		+ " WHERE wflu_wflowid = " + wFlowId 
		+ " AND wflu_profileid = " + profileId
		+ ((userId > 0) ? " AND wflu_userid = " + userId : ""));
		if (pmConn.next()) {
			bmoWFlowUser = (BmoWFlowUser)this.get(pmConn, pmConn.getInt("wflu_wflowuserid"));
		}

		return bmoWFlowUser;
	}

	// Determinar si esta bloqueado el usuario
	public boolean userIsAssigned(PmConn pmConn, int wFlowId, int userId) throws SFPmException{
		String sql = "SELECT wflu_wflowuserid FROM " + formatKind("wflowusers")
		+ " WHERE "
		+ " wflu_wflowid = " + wFlowId
		+ " AND wflu_userid = " + userId;
		pmConn.doFetch(sql);

		return pmConn.next();		
	}

	// Recibe un usuario, intenta asignarlo a algun registro faltante, dependiendo si hay faltantes
	public void updateUnassignedWithUser(PmConn pmConn, int wFlowId, int userId, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowUser = new BmoWFlowUser();
		boolean hasAssigned = false;

		PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());
		BmoProfileUser bmoProfileUser = new BmoProfileUser();
		BmFilter groupsByUserFilter = new BmFilter();
		groupsByUserFilter.setValueFilter(bmoProfileUser.getKind(), bmoProfileUser.getUserId(), userId);
		Iterator<BmObject> userGroupIterator = pmProfileUser.list(pmConn, groupsByUserFilter).iterator();

		while (userGroupIterator.hasNext() && !hasAssigned) {
			bmoProfileUser = (BmoProfileUser)userGroupIterator.next();
			bmoWFlowUser = getUnassignedByGroup(pmConn, wFlowId, bmoProfileUser.getProfileId().toInteger());
			if (bmoWFlowUser.getId() > 0) {
				bmoWFlowUser.getUserId().setValue(bmoProfileUser.getUserId().toInteger());
				this.save(pmConn, bmoWFlowUser, bmUpdateResult);
				hasAssigned = true;
			}
		}
	}

	// Determinar si esta bloqueado el usuario
	public boolean isLocked(PmConn pmConn, BmoWFlowUser bmoWFlowUser) throws SFPmException{
		String sql = "SELECT wflu_wflowuserid FROM " + formatKind("wflowusers")
		+ " WHERE "
		+ " wflu_wflowuserid <> " + bmoWFlowUser.getId() 
		+ " AND wflu_userid = " + bmoWFlowUser.getUserId().toInteger()
		+ " AND wflu_lockstatus = '" + BmoWFlowUser.LOCKSTATUS_LOCKED + "'" 
		+ " AND ("
		+ "		('" + bmoWFlowUser.getLockStart().toString() + "' BETWEEN wflu_lockstart AND wflu_lockend)"
		+ "		OR"
		+ "		('" + bmoWFlowUser.getLockEnd().toString() + "' BETWEEN wflu_lockstart AND wflu_lockend)"
		+ "		)";

		pmConn.doFetch(sql);

		return pmConn.next();		
	}

	private void updateUserGEvent(PmConn pmConn, String googleId, BmoWFlow bmoWFlow, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		// Actualiza calendario google personal del usuario si tiene id de google
		if (!googleId.equals("")) {
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), googleId);
			bmoWFlowUser.getUserGEventId().setValue(
					gCalendar.updateEvent("",
							bmoWFlowUser.getUserGEventId().toString(), 
							bmoWFlow.getCode().toString(), 
							bmoWFlow.getName().toString() + ", " + bmoWFlowUser.getBmoUser().getCode().toString(), 
							this.wFlowUsersToString(pmConn, bmoWFlow),
							bmoWFlowUser.getLockStart().toString(), 
							bmoWFlowUser.getLockEnd().toString()));

			// Guardar el la modificacion al WFlow
			super.save(pmConn, bmoWFlowUser, bmUpdateResult);
		}
		*/
	}

	private void updatePublicGEvent(PmConn pmConn, BmoWFlow bmoWFlow, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		// Actualiza calendario google publico si tiene asignado un id de calendario
		if (!bmoWFlowUser.getBmoUser().getPublicGCalendarId().toString().equals("")) {
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
			bmoWFlowUser.getPublicGEventId().setValue(
					gCalendar.updateEvent(
							bmoWFlowUser.getBmoUser().getPublicGCalendarId().toString(), 
							bmoWFlowUser.getPublicGEventId().toString(), 
							bmoWFlow.getCode().toString(), 
							bmoWFlow.getName().toString() + ", " + bmoWFlowUser.getBmoUser().getCode().toString(), 
							this.wFlowUsersToString(pmConn, bmoWFlow),
							bmoWFlowUser.getLockStart().toString(), 
							bmoWFlowUser.getLockEnd().toString()));

			// Guardar el la modificacion al WFlow
			super.save(pmConn, bmoWFlowUser, bmUpdateResult);
		}
		*/
	}

	private void updateAreaGEvent(PmConn pmConn, BmoWFlow bmoWFlow, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		// Actualiza calendario google publico por area si esta asignado calendario al area
		if (!bmoWFlowUser.getBmoUser().getBmoArea().getgCalendarId().toString().equals("")) {
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
			bmoWFlowUser.getAreaGEventId().setValue(
					gCalendar.updateEvent(
							bmoWFlowUser.getBmoUser().getBmoArea().getgCalendarId().toString(), 
							bmoWFlowUser.getAreaGEventId().toString(), 
							bmoWFlow.getCode().toString(), 
							bmoWFlow.getName().toString() + ", " + bmoWFlowUser.getBmoUser().getCode().toString(), 
							this.wFlowUsersToString(pmConn, bmoWFlow),
							bmoWFlowUser.getLockStart().toString(), 
							bmoWFlowUser.getLockEnd().toString()));

			// Guardar el la modificacion al WFlow
			super.save(pmConn, bmoWFlowUser, bmUpdateResult);
		}
		*/
	}

	private void deleteUserGEvent(PmConn pmConn, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		String googleId = bmoWFlowUser.getBmoUser().getGoogleid().toString();		

		if (!googleId.equals("")) {
			SFGCalendar gCalendar = new SFGCalendar(getSFParams(), googleId);
			gCalendar.deleteEvent(bmoWFlowUser.getUserGEventId().toString());
		}
		*/
	}

	private void deletePublicGEvent(PmConn pmConn, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		SFGCalendar gCalendar = new SFGCalendar(getSFParams(), 
				getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
		gCalendar.deleteEvent(
				bmoWFlowUser.getBmoUser().getPublicGCalendarId().toString(), 
				bmoWFlowUser.getPublicGEventId().toString());
				*/
	}

	private void deleteAreaGEvent(PmConn pmConn, BmoWFlowUser bmoWFlowUser, BmUpdateResult bmUpdateResult) throws SFException{
		printDevLog(this.getClass().getName() + " Se intento hacer operaciones con Google Calendar - desactivado.");
		/*
		SFGCalendar gCalendar = new SFGCalendar(getSFParams(), getSFParams().getBmoSFConfig().getwFlowCalendarUserId().toInteger());
		gCalendar.deleteEvent(bmoWFlowUser.getBmoUser().getBmoArea().getgCalendarId().toString(), 
				bmoWFlowUser.getAreaGEventId().toString());
				*/
	}

	public String wFlowUsersToString(PmConn pmConn, BmoWFlow bmoWFlow) throws SFException {
		String wFlowUserString = "Colaboradores: ";

		BmFilter filterByWFlow = new BmFilter();
		filterByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWFlow.getId());
		Iterator<BmObject> wFlowUserList = this.list(pmConn, filterByWFlow).iterator();
		while (wFlowUserList.hasNext()) {
			BmoWFlowUser bmoWFlowUser = (BmoWFlowUser)wFlowUserList.next();
			wFlowUserString += bmoWFlowUser.getBmoUser().getFirstname().toString() 
					+ " " + bmoWFlowUser.getBmoUser().getFatherlastname().toString();
			if (wFlowUserList.hasNext())
				wFlowUserString += ", ";
			else
				wFlowUserString += ".";
		}

		return wFlowUserString;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowUser = (BmoWFlowUser)bmObject;

		if (bmoWFlowUser.getRequired().toBoolean()) 
			bmUpdateResult.addError(bmoWFlowUser.getRequired().getName(), "No se puede eliminar el Registro: es Requerido.");

		// Elimina calendario google, si no hay errores
		if (!bmUpdateResult.hasErrors()) {
			// Obtiene todos los datos del registro
			PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
			bmoWFlowUser = (BmoWFlowUser)pmWFlowUser.get(pmConn, bmoWFlowUser.getId());

			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = new BmoWFlow();
			bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowUser.getWFlowId().toInteger());

			// Insertar cliente si es que el modulo del flujo existe el campo cliente
			//int customerId = pmWFlow.getCustomerId(pmWFlow.returnBmObjectFromWFlow(pmConn, bmoWFlow.getId()));

			//customerId
			// Generar bitacora
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW,
					"Se Elimina asignación del Colaborador " + bmoWFlowUser.getBmoUser().getEmail());

			super.delete(pmConn, bmoWFlowUser, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) {
				// Actualiza / elimina asignación de usuarios
				PmWFlowStep pmWFlowStep = new PmWFlowStep(getSFParams());
				pmWFlowStep.updateWFlowStepUsers(pmConn, bmoWFlow, bmoWFlowUser.getProfileId().toInteger(), -1, bmUpdateResult);

				// Si el flujo tiene sincronizacion con calendarios de google, eliminarlos
				if (bmoWFlow.getBmoWFlowType().getBmoWFlowCategory().getgCalendarSync().toBoolean()) { 

					// Elimina calendario personal
					deleteUserGEvent(pmConn, bmoWFlowUser, bmUpdateResult);

					// Eliminar tambien calendario de usuario publico si fue creado
					deletePublicGEvent(pmConn, bmoWFlowUser, bmUpdateResult);

					// Eliminar tambien calendario de area publico si fue creado
					deleteAreaGEvent(pmConn, bmoWFlowUser, bmUpdateResult);
				}
			}
		}

		return bmUpdateResult;
	}

}
