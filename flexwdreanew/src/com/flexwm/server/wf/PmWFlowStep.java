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
import com.flexwm.server.cv.PmActivity;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cv.BmoActivity;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowAction;
import com.flexwm.shared.wf.BmoWFlowFunnel;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.flexwm.shared.wf.BmoWFlowPhase;
import com.flexwm.shared.wf.BmoWFlowStep;
import com.flexwm.shared.wf.BmoWFlowUser;
import com.flexwm.shared.wf.BmoWFlowValidation;


public class PmWFlowStep extends PmObject {
	BmoWFlowStep bmoWFlowStep;

	public PmWFlowStep(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowStep = new BmoWFlowStep();
		setBmObject(bmoWFlowStep);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowStep.getProfileId(), bmoWFlowStep.getBmoProfile()),
				new PmJoin(bmoWFlowStep.getUserId(), bmoWFlowStep.getBmoUser()),
				new PmJoin(bmoWFlowStep.getBmoUser().getLocationId(), bmoWFlowStep.getBmoUser().getBmoLocation()),
				new PmJoin(bmoWFlowStep.getBmoUser().getAreaId(), bmoWFlowStep.getBmoUser().getBmoArea()),
				new PmJoin(bmoWFlowStep.getWFlowId(), bmoWFlowStep.getBmoWFlow()),
				new PmJoin(bmoWFlowStep.getBmoWFlow().getWFlowTypeId(), bmoWFlowStep.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowStep.getWFlowPhaseId(), bmoWFlowStep.getBmoWFlowPhase()),
				new PmJoin(bmoWFlowStep.getWFlowActionId(), bmoWFlowStep.getBmoWFlowAction()),
				new PmJoin(bmoWFlowStep.getWFlowFunnelId(), bmoWFlowStep.getBmoWFlowFunnel())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)autoPopulate(pmConn, new BmoWFlowStep());		

		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if ((int)pmConn.getInt(bmoProfile.getIdFieldName()) > 0) bmoWFlowStep.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmoWFlowStep.setBmoProfile(bmoProfile);

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if ((int)pmConn.getInt(bmoUser.getIdFieldName()) > 0) bmoWFlowStep.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoWFlowStep.setBmoUser(bmoUser);

		// BmoWFlowPhase
		BmoWFlowPhase bmoWFlowPhase = new BmoWFlowPhase();
		if ((int)pmConn.getInt(bmoWFlowPhase.getIdFieldName()) > 0) bmoWFlowStep.setBmoWFlowPhase((BmoWFlowPhase) new PmWFlowPhase(getSFParams()).populate(pmConn));
		else bmoWFlowStep.setBmoWFlowPhase(bmoWFlowPhase);

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		if ((int)pmConn.getInt(bmoWFlow.getIdFieldName()) > 0) bmoWFlowStep.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else bmoWFlowStep.setBmoWFlow(bmoWFlow);

		// BmoWFlowAction
		BmoWFlowAction bmoWFlowAction = new BmoWFlowAction();
		if ((int)pmConn.getInt(bmoWFlowAction.getIdFieldName()) > 0) bmoWFlowStep.setBmoWFlowAction((BmoWFlowAction) new PmWFlowAction(getSFParams()).populate(pmConn));
		else bmoWFlowStep.setBmoWFlowAction(bmoWFlowAction);

		// BmoWFlowFunnel
		BmoWFlowFunnel bmoWFlowFunnel = new BmoWFlowFunnel();
		int wFlowFunnelId = (int)pmConn.getInt(bmoWFlowFunnel.getIdFieldName());
		if (wFlowFunnelId > 0) bmoWFlowStep.setBmoWFlowFunnel((BmoWFlowFunnel) new PmWFlowFunnel(getSFParams()).populate(pmConn));
		else bmoWFlowStep.setBmoWFlowFunnel(bmoWFlowFunnel);

		return bmoWFlowStep;
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";

		if (getSFParams().restrictData(bmoWFlowStep.getProgramCode())) {
			// Filtro por asignacion de proyectos
			filters = " ( wfsp_userid = " + getSFParams().getLoginInfo().getUserId()
					+ "  OR wfsp_usercreateid = " + getSFParams().getLoginInfo().getUserId()
					+ ") OR ("
					+ "	( " +
					" 		wfsp_wflowid IN ( " +
					" 			SELECT wflw_wflowid FROM wflowusers  " +
					" 			LEFT JOIN wflows on (wflu_wflowid = wflw_wflowid) " +
					" 			WHERE wflu_userid in (" +
					" select user_userid from users " +
					" where " + 
					" user_userid = " + getSFParams().getLoginInfo().getUserId() +
					" or user_userid in ( " +
					" 	select u2.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" where u1.user_userid = " + getSFParams().getLoginInfo().getUserId() +
					" ) " +
					" or user_userid in ( " +
					" select u3.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" where u1.user_userid = " + getSFParams().getLoginInfo().getUserId() +
					" ) " +
					" or user_userid in ( " +
					" select u4.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" where u1.user_userid = " + getSFParams().getLoginInfo().getUserId() +
					" ) " +
					" or user_userid in ( " +
					" select u5.user_userid from users u1 " +
					" left join users u2 on (u2.user_parentid = u1.user_userid) " +
					" left join users u3 on (u3.user_parentid = u2.user_userid) " +
					" left join users u4 on (u4.user_parentid = u3.user_userid) " +
					" left join users u5 on (u5.user_parentid = u4.user_userid) " +
					" where u1.user_userid = " + getSFParams().getLoginInfo().getUserId() +
					" ) " + 
					" ) "
					+ " ) "
					+ "	) "
					+ " )"; 
		}
		return filters;
	}



	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		bmoWFlowStep = (BmoWFlowStep)bmObject;
		boolean newRecord = false;

		// Asigna valor si es nuevo
		if (!(bmoWFlowStep.getId() > 0)) {
			newRecord = true;
		}

		// Obtiene WFlow
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowStep.getWFlowId().toInteger());

		// Si no esta asignada la fase, asigna la primera
		if (!(bmoWFlowStep.getWFlowPhaseId().toInteger() > 0)) {
			bmoWFlowStep.getWFlowPhaseId().setValue(new PmWFlowPhase(getSFParams()).getFirstPhaseId(pmConn, bmoWFlow.getBmoWFlowType().getWFlowCategoryId().toInteger()));
		}

		// Si no esta el grupo, lo toma del usuario
		if (!(bmoWFlowStep.getProfileId().toInteger() > 0)) {
			if (bmoWFlowStep.getUserId().toInteger() > 0) {
				bmoWFlowStep.getProfileId().setValue(new PmProfile(getSFParams()).getFirstProfileId(pmConn, bmoWFlow.getUserId().toInteger()));
			} else {
				bmUpdateResult.addError(bmoWFlowStep.getUserId().getName(), "Debe seleccionar Usuario o Grupo.");
			}
		}

		// Si no tiene secuencia, se asigna en automatico
		// Si no esta el grupo, lo toma del usuario
		if (!(bmoWFlowStep.getSequence().toInteger() > 0)) {
			bmoWFlowStep.getSequence().setValue(nextSequence(pmConn, bmoWFlowStep.getWFlowId().toInteger()));
		}

		// Si no esta asignado el inicio de la tarea, asignarlo
		if (bmoWFlowStep.getStartdate().toString().equals(""))
			bmoWFlowStep.getStartdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

		// Si esta al 100% y no esta asignado la fecha fin, asignarla
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoWFlowStep.getEnddate().toString().equals(""))
			bmoWFlowStep.getEnddate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

		// Si el avance es menor a 100, y esta asingada la fecha fin, borrar fecha de fin
		if (bmoWFlowStep.getProgress().toInteger() < 100 && !bmoWFlowStep.getEnddate().toString().equals(""))
			bmoWFlowStep.getEnddate().setValue("");

		// Asignar Estatus a la tarea segun fecha de Recordatorio
		setStatusRemindDate(bmoWFlowStep);

		// Agregar bitacora de flujos
		String comments = "Cambio en Tarea: " + bmoWFlowStep.getName().toString() + ", " + 
				"Fase: " + bmoWFlowStep.getBmoWFlowPhase().getName().toString() + ", " + 
				"Avance: " + bmoWFlowStep.getProgress().toString() + "% " + 
				"Comentarios: " + bmoWFlowStep.getComments().toString();

		//customerId
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlowStep.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, comments);

		// Mandar notificacion de los comentarios a los usuarios de flujo
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableEmailReminderComments().toBoolean() ) {
			if (bmoWFlowStep.getEmailReminderComments().toBoolean() && !bmoWFlowStep.getComments().toString().equals("")) {
				sendMailComments(pmConn, bmoWFlowStep);
			}
			bmoWFlowStep.getEmailReminderComments().setValue(0);
		}

		// Agregar bitacora interna de la tarea, si hay comentario
		if (!bmoWFlowStep.getComments().toString().equals("")) {
			String commentLog = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()) +
					" (" + getSFParams().getLoginInfo().getBmoUser().getCode() + ") " + 
					bmoWFlowStep.getProgress().toString() + "%: " + 
					bmoWFlowStep.getComments().toString() +
					"\n\n" + bmoWFlowStep.getCommentLog().toString();
			bmoWFlowStep.getCommentLog().setValue(commentLog);
			bmoWFlowStep.getComments().setValue("");
		}

		super.save(pmConn, bmoWFlowStep, bmUpdateResult);

		// Se calcula todo el flujo si el movimiento es en una tarea de sistema wflow
		if (bmoWFlowStep.getType().toChar() == BmoWFlowStep.TYPE_WFLOW) {
			// Se activa la acción (si aplica)
			activateActionClass(pmConn, bmoWFlowStep, bmUpdateResult);

		} else {
			System.out.println("Es tarea de tipo Usuario");

			// Es de tipo usuario; si es nueva manda correo de activacion
			if (newRecord) {
				sendMailActivation(pmConn, bmoWFlowStep);
			} else {
				// Determina si hay cambio de estatus para enviar notificacion al usuario creador
				if (bmoWFlowStep.getUserCreateId().toInteger() != bmoWFlowStep.getUserId().toInteger()) {
					// Si el avance es 100 envia notificacion
					if (bmoWFlowStep.getProgress().equals(100)) {
						System.out.println("Es tarea terminada");

						sendMailCompletion(pmConn, bmoWFlowStep);
					}
				}
			}
		}

		// TODO: en lugar de calcular todo el wflow, solo lo que afecta esta tarea
		pmWFlow.calculate(pmConn, bmoWFlow, bmUpdateResult);

		// Se asegura que el objeto ultimo de bmUpdateResult sea el del registro creado
		super.save(pmConn, bmoWFlowStep, bmUpdateResult);

		return bmUpdateResult;
	}

	// Actualiza estatus dependiendo de las fechas
	public void setStatusRemindDate(BmoWFlowStep bmoWFlowStep) throws SFException {
		// Estatus segun progreso y fecha de recordatorio
		if (bmoWFlowStep.getProgress().toInteger() < 100) {
			if (!bmoWFlowStep.getRemindDate().equals("")) {

				if (SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), 
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), bmoWFlowStep.getRemindDate().toString())) {
					bmoWFlowStep.getStatus().setValue(BmoWFlowStep.STATUS_TOEXPIRED);
				} 
				// Revisar que la fecha actual sea diferente y la tarea este activada
				if (!SFServerUtil.isBefore(getSFParams().getDateFormat(), getSFParams().getTimeZone(), 
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()), bmoWFlowStep.getRemindDate().toString())
						&& bmoWFlowStep.getEnabled().toBoolean() ) {
					// Si es la misma fecha, colocar Por Vencer
					if (bmoWFlowStep.getRemindDate().toString().equals(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()))) {
						bmoWFlowStep.getStatus().setValue(BmoWFlowStep.STATUS_TOEXPIRED);
					} else {
						bmoWFlowStep.getStatus().setValue(BmoWFlowStep.STATUS_EXPIRED);
					}
				}
			} else {
				bmoWFlowStep.getStatus().setValue(BmoWFlowStep.STATUS_TOEXPIRED);
			}
		} else {
			bmoWFlowStep.getStatus().setValue(BmoWFlowStep.STATUS_FINALIZED	);
		}
	}

	// Almacena la tarea, ajusta las fechas
	public BmUpdateResult saveCheckDates(PmConn pmConn, BmoWFlow bmoWFlow, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {	
		BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)bmObject;

		// Si no esta asignado el inicio de la tarea, asignarlo
		if (bmoWFlowStep.getStartdate().toString().equals(""))
			bmoWFlowStep.getStartdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

		// Si esta al 100% y no esta asignado la fecha fin, asignarla
		if (bmoWFlowStep.getProgress().toInteger() == 100 && bmoWFlowStep.getEnddate().toString().equals(""))
			bmoWFlowStep.getEnddate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));

		// Si el avance es menor a 100, y esta asingada la fecha fin, borrar fecha de fin
		if (bmoWFlowStep.getProgress().toInteger() < 100 && !bmoWFlowStep.getEnddate().toString().equals(""))
			bmoWFlowStep.getEnddate().setValue("");

		// Si la fecha de hoy es anterior a la fecha del flujo, pone la fecha del flujo
		if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), 
				getSFParams().getTimeZone(), 
				SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()), 
				bmoWFlow.getStartDate().toString())) {
			bmoWFlowStep.getStartdate().setValue(bmoWFlow.getStartDate().toString());
		}else {
			// Cambiar fecha a las tareas que NO estan activas y NO tienen avance
			if (bmoWFlowStep.getProgress().toInteger() == 0 
					&& bmoWFlowStep.getEnabled().toBoolean() == false ) {
				bmoWFlowStep.getStartdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			} else {
				//bmoWFlowStep.getStartdate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			}
		}

		// Actualizar estatus dependiendo de las fechas
		setStatusRemindDate(bmoWFlowStep);

		super.save(pmConn, bmoWFlowStep, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {
		BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)bmObject;

		try {
			// Validacion especial
			if (bmoWFlowStep.getProgress().toInteger() == 100) {
				// Tiene clase de validacion?
				if (bmoWFlowStep.getWFlowValidationId().toInteger() > 0) {
					try {
						PmWFlowValidation pmWFlowValidation = new PmWFlowValidation(getSFParams());
						BmoWFlowValidation bmoWFlowValidation = (BmoWFlowValidation)pmWFlowValidation.get(bmoWFlowStep.getWFlowValidationId().toInteger());

						IWFlowValidate wFlowValidate = (IWFlowValidate) Class.forName(bmoWFlowValidation.getClassName().toString()).newInstance();
						wFlowValidate.validate(getSFParams(), bmoWFlowStep, bmUpdateResult);
					} catch (InstantiationException e) {
						throw new SFException(this.getClass().getName() + "-validate(): Error de instancia: " + e.toString());
					} catch (IllegalAccessException e) {
						throw new SFException(this.getClass().getName() + "-validate(): Error de acceso: " + e.toString());
					} catch (ClassNotFoundException e) {
						throw new SFException(this.getClass().getName() + "-validate(): Error de clase no encontrada: " + e.toString());
					}
				}
			}
		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
		}
	}

	// Accion de la activacion de la tarea
	private void activateActionClass(PmConn pmConn, BmoWFlowStep bmoWFlowStep, BmUpdateResult bmUpdateResult) throws SFException {
		// Tiene clase de accion?
		if (bmoWFlowStep.getWFlowActionId().toInteger() > 0) {
			try {
				PmWFlowAction pmWFlowAction = new PmWFlowAction(getSFParams());
				BmoWFlowAction bmoWFlowAction = (BmoWFlowAction)pmWFlowAction.get(pmConn, bmoWFlowStep.getWFlowActionId().toInteger());

				IWFlowAction wFlowAction = (IWFlowAction) Class.forName(bmoWFlowAction.getClassName().toString()).newInstance();
				wFlowAction.action(getSFParams(), pmConn, bmoWFlowStep, bmUpdateResult);

			} catch (InstantiationException e) {
				throw new SFException(this.getClass().getName() + "-actionClass(): Error de instancia: " + e.toString());
			} catch (IllegalAccessException e) {
				throw new SFException(this.getClass().getName() + "-actionClass(): Error de acceso: " + e.toString());
			} catch (ClassNotFoundException e) {
				throw new SFException(this.getClass().getName() + "-actionClass(): Error de clase no encontrada: " + e.toString());
			} catch (SFException e) {
				System.out.println(e.toString());
				throw new SFException(this.getClass().getName() + "-actionClass(): Error de Framework: " + e.toString());
			}
		}
	}

	// Determina si debe habilitar una tarea
	public boolean shallEnable(PmConn pmConn, int wFlowId, int childId) throws SFPmException {
		boolean enabled = false;

		String sql = "SELECT wfsp_wflowid, wfsz_childid, AVG(wfsp_progress) FROM wflowstepdeps " + 
				" LEFT JOIN wflowsteps ON (wfsz_wflowstepid = wfsp_wflowstepid) " + 
				" WHERE wfsz_childid = " + childId + " AND wfsp_wflowid = " + wFlowId + " GROUP BY wfsz_childid ";

		pmConn.doFetch(sql);

		// Si es rama de otro paso, revisar el avance de los anteriores
		if (pmConn.next()) {
			int average = pmConn.getInt(3);
			if (average == 100) enabled = true;
		} else {
			// No es rama de ninguno, por lo tanto tiene que estar activado
			enabled = true;
		}
		return enabled;
	}

	// Envia la notificacion cuando una tarea haya sido activada
	public void sendMailActivation(PmConn pmConn, BmoWFlowStep bmoWFlowStep) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		// Valida que tanto la categoria como el paso tengan habilitado el envio de notificaciones
		if (bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()
				&& bmoWFlowStep.getEmailReminders().toBoolean()) {

			// Si esta asignado el usuario, le envia el correo
			if (bmoWFlowStep.getUserId().toInteger() > 0) {
				PmUser pmUser = new PmUser(getSFParams());
				BmoUser bmoUser = (BmoUser)pmUser.get(pmConn, bmoWFlowStep.getUserId().toInteger());

				if (bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
					mailList.add(new SFMailAddress(bmoUser.getEmail().toString(), 
							bmoUser.getFirstname().toString() 
							+ " " + bmoUser.getFatherlastname().toString()));

					printDevLog("Enviando correo de activacion de tarea a : " + bmoUser.getEmail().toString());
				} else
					printDevLog("Usuario inactivo : " + bmoUser.getEmail().toString());
			} else {
				// No esta asignado el usuario, Obtiene la lista de correos del grupo de la tarea
				mailList = getStepMailList(bmoWFlowStep.getId(), bmoWFlowStep.getWFlowId().toInteger());
			}

			String subject =  " Tarea Nueva: " 
					+ bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
					+ "." + bmoWFlowStep.getSequence().toString() 
					+ " " + bmoWFlowStep.getName().toString() 
					+ " (" + bmoWFlowStep.getBmoWFlow().getCode().toString()
					+ " " + bmoWFlowStep.getBmoWFlow().getName().toString() + ")";

			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
					"Tarea Activada", 
					" 	<p style=\"font-size:12px\"> "
							+ " <b>WFlow:</b> " + bmoWFlowStep.getBmoWFlow().getCode().toString() + " " + bmoWFlowStep.getBmoWFlow().getName().toString()
							+ " <br> "
							+ "	<b>Tarea:</b> " + bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
							+ 				"." + bmoWFlowStep.getSequence().toString() 
							+ 				" " + bmoWFlowStep.getName().toString()
							+ "	<br> "
							+ "	<b>Descripcion:</b> " + bmoWFlowStep.getDescription().toString()
							+ "	<br> "
							+ "	</p> "
							+ "	<p align=\"center\" style=\"font-size:12px\"> "
							+ "		Favor de dar Seguimiento a la Tarea <a href=\""
							+ getSFParams().getAppURL() + "start.jsp?startprogram=" + bmoWFlowStep.getBmoWFlow().getCallerCode().toString() +"&foreignid=" + bmoWFlowStep.getBmoWFlow().getCallerId().toString() + "\">Aqui</a>. "
							+ "	</p> "
					);

			// Quitar emails repetidos
			ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
			for (SFMailAddress event : mailList) {
				boolean isFound = false;
				// Revisar si el email existe en noRepeat
				for (SFMailAddress e : mailListNoRepeat) {
					if (e.getName().equals(event.getName()) || (e.equals(event))) {
						isFound = true;        
						break;
					}
				}
				// Si no encontro ninguno añadirlo a la nueva lista
				if (!isFound) mailListNoRepeat.add(event);
			}

			// Si hay destinatarios, enviar los correos
			if (mailListNoRepeat.size() > 0) 
				SFSendMail.send(getSFParams(),
						mailListNoRepeat, 
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getBmoSFConfig().getAppTitle().toString(), 
						subject, 
						msgBody);
		}
	}

	// Envia los recordatorios via email a los grupos de la tarea
	//	private void sendMailReminder(BmoWFlowStep bmoWFlowStep) throws SFException {
	//		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
	//
	//		// Valida que en la categoria y la tarea aplique envio de recordatorios
	//		if (bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()
	//				&& bmoWFlowStep.getEmailReminders().toBoolean()) {
	//
	//			// Si esta asignado el usuario, le envia el correo
	//			if (bmoWFlowStep.getUserId().toInteger() > 0) {
	//				// Si esta activo añadir a la lista
	//				if (bmoWFlowStep.getBmoUser().getStatus().equals(BmoUser.STATUS_ACTIVE)) {
	//					mailList.add(new SFMailAddress(bmoWFlowStep.getBmoUser().getEmail().toString(), 
	//							bmoWFlowStep.getBmoUser().getFirstname().toString() 
	//							+ " " + bmoWFlowStep.getBmoUser().getFatherlastname().toString()));
	//				} else
	//					printDevLog("Usuario inactivo : " + bmoWFlowStep.getBmoUser().getEmail().toString());
	//			} else {
	//				// No esta asignado el usuario, Obtiene la lista de correos del grupo de la tarea
	//				mailList = getStepMailList(bmoWFlowStep.getId(), bmoWFlowStep.getWFlowId().toInteger());
	//			}
	//
	//			// Si hay mas de un destinatario, preparar y enviar correo
	//			if (mailList.size() > 0 ) {
	//				String subject = " Tarea: " 
	//						+ bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
	//						+ "." + bmoWFlowStep.getSequence().toString() 
	//						+ " " + bmoWFlowStep.getName().toString() 
	//						+ " (" + bmoWFlowStep.getBmoWFlow().getCode().toString()
	//						+ " " + bmoWFlowStep.getBmoWFlow().getName().toString() + ")";
	//
	//				String msgBody = HtmlUtil.mailBodyFormat(
	//						getSFParams(), 
	//						"Recordatorio de Tarea", 
	//						" 	<p style=\"font-size:12px\"> "
	//								+ " <b>WFlow:</b> " + bmoWFlowStep.getBmoWFlow().getCode().toString() + " " + bmoWFlowStep.getBmoWFlow().getName().toString()
	//								+ " <br> "
	//								+ "	<b>Tarea:</b> " + bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
	//								+ 				"." + bmoWFlowStep.getSequence().toString() 
	//								+ 				" " + bmoWFlowStep.getName().toString()
	//								+ "	<br> "
	//								+ "	<b>Descripcion:</b> " + bmoWFlowStep.getDescription().toString()
	//								+ "	<br> "
	//								+ "	</p> "
	//								+ "	<p align=\"center\" style=\"font-size:12px\"> "
	//								+ "		Favor de dar Seguimiento a la Tarea <a href=\""
	//								+ getSFParams().getAppURL() + "start.jsp?startprogram=" + bmoWFlowStep.getBmoWFlow().getCallerCode().toString() +"&foreignid=" + bmoWFlowStep.getBmoWFlow().getCallerId().toString() + "\">Aqui</a>. "
	//								+ "	</p> "
	//						);
	//				
	//				// Quitar emails repetidos
	//				ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
	//				for (SFMailAddress event : mailList) {
	//				    boolean isFound = false;
	//				    // Revisar si el email existe en noRepeat
	//				    for (SFMailAddress e : mailListNoRepeat) {
	//				        if (e.getName().equals(event.getName()) || (e.equals(event))) {
	//				            isFound = true;        
	//				            break;
	//				        }
	//				    }
	//				    // Si no encontro ninguno añadirlo a la nueva lista
	//				    if (!isFound) mailListNoRepeat.add(event);
	//				}
	//
	//				// Si es produccion, envia correo
	//				if (getSFParams().isProduction()) {
	//					SFSendMail.send(getSFParams(),
	//							mailListNoRepeat, 
	//							getSFParams().getBmoSFConfig().getEmail().toString(), 
	//							getSFParams().getBmoSFConfig().getAppTitle().toString(), 
	//							subject, 
	//							msgBody);
	//				} else {
	//					System.out.println(this.getClass().getName() + "-sendMailReminder(): Se envia correo: "
	//							+ "De: " + getSFParams().getBmoSFConfig().getEmail().toString()
	//							+ "Asunto: " + subject
	//							+ "Cuerpo: " + msgBody
	//							);
	//				}
	//			}
	//		}
	//	}

	// Envia la notificacion cuando una tarea de tipo Usuario haya sido completada
	private void sendMailCompletion(PmConn pmConn, BmoWFlowStep bmoWFlowStep) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		BmoActivity bmoActivity = new BmoActivity();
		PmActivity pmActivity = new PmActivity(getSFParams());
		BmoProgram bmoProgram= new BmoProgram();

		//Se obtiene el modulo de Activity
		bmoProgram = getSFParams().getBmoProgram(bmoActivity.getProgramCode());
		// Se obtiene el id del modulo de la categoría
		int programId = bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getProgramId().toInteger();
		//Si el id del modulo de la categoria es igual al id del modulo de activity busca la actividad de ese flujo
		if(bmoProgram.getId() == programId)
		{
			bmoActivity =  (BmoActivity) pmActivity.getBy(pmConn,bmoWFlowStep.getWFlowId().toInteger(), bmoActivity.getWFlowId().getName());
		}

		// Valida que tanto la categoria como el paso tengan habilitado el envio de notificaciones
		if (bmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()
				&& bmoWFlowStep.getEmailReminders().toBoolean()) {

			// Si es de tipo usuario y diferente creador a asignado
			if (bmoWFlowStep.getType().equals(BmoWFlowStep.TYPE_USER)) {

				if (bmoWFlowStep.getUserId().toInteger() > 0) {
					// Si esta activo añadir a la lista
					if (bmoWFlowStep.getBmoUser().getStatus().equals(BmoUser.STATUS_ACTIVE)) {
						// Agrega al usuario resposable de la tarea
						mailList.add(new SFMailAddress(bmoWFlowStep.getBmoUser().getEmail().toString(), 
								bmoWFlowStep.getBmoUser().getFirstname().toString() 
								+ " " + bmoWFlowStep.getBmoUser().getFatherlastname().toString()));
					} else
						printDevLog("Usuario inactivo : " + bmoWFlowStep.getBmoUser().getEmail().toString());
				}

				// Agrega al creador si es distinto al actual responsable de la tarea
				if (bmoWFlowStep.getUserCreateId().toInteger() != bmoWFlowStep.getUserId().toInteger()) {
					PmUser pmUser = new PmUser(getSFParams());
					BmoUser bmoUserCreate = (BmoUser)pmUser.get(pmConn, bmoWFlowStep.getUserCreateId().toInteger());

					// Si esta activo añadir a la lista
					if (bmoUserCreate.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
						printDevLog("Enviando correo de terminacion de tarea a : " + bmoUserCreate.getEmail().toString());

						mailList.add(new SFMailAddress(bmoUserCreate.getEmail().toString(), 
								bmoUserCreate.getFirstname().toString() 
								+ " " + bmoUserCreate.getFatherlastname().toString()));
					} else
						printDevLog("Usuario inactivo : " + bmoWFlowStep.getBmoUser().getEmail().toString());	
				}
				// Valida que la actividad aplique envio de recordatorios
				if (bmoActivity.getEmailReminders().toBoolean()) {
					// Obtiene el usuario a enviar correo
					PmUser pmUser = new PmUser(getSFParams());
					BmoUser bmoUserActivity = new BmoUser();
					bmoUserActivity = (BmoUser) pmUser.get(bmoActivity.getUserId().toInteger());
					// Si esta activo añadir a la lista
					if (bmoWFlowStep.getBmoUser().getStatus().equals(BmoUser.STATUS_ACTIVE)) {
						printDevLog("Enviando correo de terminacion de tarea al responsable de la actividad: " + bmoUserActivity.getEmail().toString());
						//agrega a la lista de correo el usuario responsable de la actividad
						mailList.add(new SFMailAddress(bmoUserActivity.getEmail().toString(),
								bmoUserActivity.getFirstname().toString() + " " + bmoUserActivity.getFatherlastname().toString()));
					}
				}

				String subject = " Tarea Finalizada: " 
						+ bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
						+ "." + bmoWFlowStep.getSequence().toString() 
						+ " " + bmoWFlowStep.getName().toString() 
						+ " (" + bmoWFlowStep.getBmoWFlow().getCode().toString()
						+ " " + bmoWFlowStep.getBmoWFlow().getName().toString() + ")";

				String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
						"Tarea Finalizada", 
						" 	<p style=\"font-size:12px\"> "
								+ " <b>WFlow:</b> " + bmoWFlowStep.getBmoWFlow().getCode().toString() + " " + bmoWFlowStep.getBmoWFlow().getName().toString()
								+ " <br> "
								+ "	<b>Tarea:</b> " + bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
								+ 				"." + bmoWFlowStep.getSequence().toString() 
								+ 				" " + bmoWFlowStep.getName().toString()
								+ "	<br> "
								+ "	<b>Descripcion:</b> " + bmoWFlowStep.getDescription().toString()
								+ "	<br> "
								+ "	</p> "
								+ "	<p align=\"center\" style=\"font-size:12px\"> "
								+ "		Puede ver la Tarea <a href=\""
								+ getSFParams().getAppURL() + "start.jsp?startprogram=" + bmoWFlowStep.getBmoWFlow().getCallerCode().toString() +"&foreignid=" + bmoWFlowStep.getBmoWFlow().getCallerId().toString() + "\">Aqui</a>. "
								+ "	</p> "
						);

				// Quitar emails repetidos
				ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
				for (SFMailAddress event : mailList) {
					boolean isFound = false;
					// Revisar si el email existe en noRepeat
					for (SFMailAddress e : mailListNoRepeat) {
						if (e.getName().equals(event.getName()) || (e.equals(event))) {
							isFound = true;        
							break;
						}
					}
					// Si no encontro ninguno añadirlo a la nueva lista
					if (!isFound) mailListNoRepeat.add(event);
				}

				// Si hay destinatarios, enviar los correos
				if (mailListNoRepeat.size() > 0) 
					SFSendMail.send(getSFParams(),
							mailListNoRepeat, 
							getSFParams().getBmoSFConfig().getEmail().toString(), 
							getSFParams().getBmoSFConfig().getAppTitle().toString(), 
							subject, 
							msgBody);

			}
		}
	}

	// Preparar y enviar notificaciones de todos los pasos abiertos
	public void prepareReminders() throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());

		String sql = "SELECT user_userid from users " + 
				" WHERE ( user_userid in (select wfsp_userid from wflowsteps LEFT JOIN wflows ON (wfsp_wflowid = wflw_wflowid) " +
				" WHERE wfsp_enabled = true  AND wfsp_progress < 100  AND wfsp_emailreminders = 1  AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' "
				+ " AND wfsp_reminddate <= '"+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) +"' )) "
				+ " OR "
				+ " (user_userid " + 
				"In (select pfus_userid from profileusers where pfus_profileid " + 
				"in (SELECT wfsp_profileid AS profile FROM wflowsteps " + 
				"LEFT JOIN wflows ON (wfsp_wflowid = wflw_wflowid)  " + 
				"WHERE wfsp_enabled = true  AND wfsp_progress < 100  " + 
				"AND wfsp_emailreminders = 1  AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' " + 
				"AND wfsp_reminddate <= '"+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "' AND wfsp_userid  IS NULL )))";
		printDevLog("Consulta usuarios en Tareas Activas : " + sql);
		try {
			pmConn.open();
			pmConn2.open();
			pmConn.doFetch(sql);

			while (pmConn.next()) {
				ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
				PmUser pmUser = new PmUser(getSFParams());
				BmoUser bmoUser = (BmoUser)pmUser.get( pmConn.getInt("user_userid"));
				sql = "SELECT wfsp_wflowstepid from wflowsteps LEFT JOIN wflows ON (wfsp_wflowid = wflw_wflowid)"
						+ "  WHERE (wfsp_enabled = true  AND wfsp_progress < 100  AND wfsp_emailreminders = 1 "
						+ " AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' AND wfsp_reminddate <= '"
						+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) +"' AND wfsp_userid = "
						+ bmoUser.getId() + ")"
						+ " OR "
						+ " ( wfsp_enabled = true  AND wfsp_progress < 100 " + 
						" AND wfsp_emailreminders = 1  AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' " +
					    " AND wfsp_reminddate <= '" +  SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "' AND wfsp_userid  IS NULL" + 
						" AND wfsp_profileid in (select pfus_profileid from profileusers where pfus_userid = " +  bmoUser.getId() + "))";
				printDevLog("Consulta  Tareas Activas por usuario : " + sql); 
				pmConn2.doFetch(sql);
				
				//Subject de la notificación
				String subject = " Tareas Activas " + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat());
				String msgBody = "";
				//Para acumular tareas
				String wflowSteps = "";

				if (bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
					mailList.add(new SFMailAddress(bmoUser.getEmail().toString(), 
							bmoUser.getFirstname().toString() 
							+ " " + bmoUser.getFatherlastname().toString()));
				}
				while (pmConn2.next()) {
					BmoWFlowStep nextBmoWFlowStep = (BmoWFlowStep)this.get(pmConn2.getInt("wfsp_wflowstepid"));
				
					
					// Valida que en la categoria y la tarea aplique envio de recordatorios
					if (nextBmoWFlowStep.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory().getEmailReminders().toBoolean()
							&& nextBmoWFlowStep.getEmailReminders().toBoolean()) {
						// Si esta activo añadir a la lista


						// Si hay mas de un destinatario, preparar y enviar correo
						if (mailList.size() > 0 ) {
//							if (!wflowSteps.equals(""))wflowSteps +=  "<br><br>";
							wflowSteps += "<p  style=\"font-size:12px\"> ";
									if (!wflowSteps.equals(""))wflowSteps +=  "<br><br>";
							wflowSteps += " <b>WFlow:</b> " + nextBmoWFlowStep.getBmoWFlow().getCode().toString() + " " + nextBmoWFlowStep.getBmoWFlow().getName().toString()
									+ "	<b>Tarea:</b> " + nextBmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
									+ 				"." + nextBmoWFlowStep.getSequence().toString() 
									+ 				" " + nextBmoWFlowStep.getName().toString()												
									+ "	<b>Descripcion:</b> " + nextBmoWFlowStep.getDescription().toString()
									+ "	<b>Favor de dar Seguimiento a la Tarea <a href=\""
									+ getSFParams().getAppURL() + "start.jsp?startprogram=" + nextBmoWFlowStep.getBmoWFlow().getCallerCode().toString() +"&foreignid=" + nextBmoWFlowStep.getBmoWFlow().getCallerId().toString() + "\">Aqui</a>.<b> "
									+ "</p>";
						}
					}
				}
				
				msgBody =  HtmlUtil.mailBodyFormat(getSFParams(), "Recordatorio de Tareas", wflowSteps);


				if (getSFParams().isProduction()) {
					if (!wflowSteps.equals("")) {
						try {
							SFSendMail.send(getSFParams(),
									mailList, 
									getSFParams().getBmoSFConfig().getEmail().toString(), 
									getSFParams().getBmoSFConfig().getAppTitle().toString(), 
									subject, 
									msgBody);
						} catch (Exception e) {
							throw new SFException(this.getClass().getName() + " - sendMailReminder() - Error al enviar email: " + e.toString());
						}
					} else {
						System.out.println("No hay tareas pendientes");
					}
				}else {
					System.out.println(this.getClass().getName() + "-sendMailReminder(): Se envia correo: "
							+ "De: " + getSFParams().getBmoSFConfig().getEmail().toString()
							+ "Asunto: " + subject
							+ "Cuerpo: " + wflowSteps
							);
				}
			}			
			
		} catch (SFPmException e) {
			throw new SFException("PmWFlowStep-sendReminders() ERROR: " + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
		}
	}
//	private void sendRemindNullUser() throws SFException {
//		PmConn pmConn = new PmConn(getSFParams());
//		PmConn pmConn2 = new PmConn(getSFParams());
//		String sql = "SELECT user_userid FROM users WHERE user_userid IN "
//				+ " (SELECT pfus_userid FROM profileusers where pfus_profileid IN "
//				+ " (SELECT wfsp_profileid AS profile FROM wflowsteps LEFT JOIN wflows "
//				+ " ON (wfsp_wflowid = wflw_wflowid)  WHERE wfsp_enabled = true  AND wfsp_progress < 100 "
//				+ " AND wfsp_emailreminders = 1  AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "' AND wfsp_reminddate <= "
//				+ "'" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "' "
//				+ " AND wfsp_userid  IS NULL )) ";
//		pmConn.open();
//		pmConn2.open();
//		
//		pmConn.doFetch(sql);
//		 
//		while (pmConn.next()) {
//			System.err.println(">>>>>>>>>>>>>>> " + pmConn.getInt("user_userid") );
//			sql = " SELECT wfsp_wflowstepid,wfsp_profileid  AS profile FROM wflowsteps " + 
//					" LEFT JOIN wflows ON (wfsp_wflowid = wflw_wflowid) " + 
//					" WHERE wfsp_enabled = true  AND wfsp_progress < 100 " + 
//					" AND wfsp_emailreminders = 1  AND wflw_status = '" + BmoWFlow.STATUS_ACTIVE + "'" +
//				    " AND wfsp_reminddate <= '" +  SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "' AND wfsp_userid  IS NULL" + 
//					" AND wfsp_profileid in (select pfus_profileid from profileusers where pfus_userid = " + pmConn.getInt("user_userid") + ")";
//			pmConn2.doFetch(sql);
//			
//			while (pmConn2.next()) {
//				BmoWFlowStep nextBmoWFlowStep = (BmoWFlowStep)this.get(pmConn2.getInt("wfsp_wflowstepid"));
//				System.err.println("XXXXXXXXXX " + nextBmoWFlowStep.getBmoWFlow().getName() );
//				System.err.println("XXXXXXXXXX " + nextBmoWFlowStep.getName());
//			}
//			
//			
//		}
//		
//		pmConn.close();
//		pmConn2.close();
//	}
	// Actualizar usuarios de las tareas segun asignacion de colaborador al flujo
	public void updateWFlowStepUsers(PmConn pmConn, BmoWFlow bmoWFlow, int profileId, int userId, BmUpdateResult bmUpdateResult) throws SFException {
		PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
		PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());

		// Revisa si existen tareas ya creadas, en cuyo caso se detiene la creacion automatica
		BmFilter filterWFlow = new BmFilter();
		filterWFlow.setValueFilter(bmoWFlowStep.getKind(), bmoWFlowStep.getWFlowId(), bmoWFlow.getId());
		Iterator<BmObject> wFlowStepList = this.list(pmConn, filterWFlow).iterator();

		// Por cada step type, genera un step asociado a este flujo
		while (wFlowStepList.hasNext()) {
			BmoWFlowStep bmoWFlowStep = (BmoWFlowStep)wFlowStepList.next();

			// Si es del mismo grupo la tarea al grupo que se esta modificando, hacer el cambio directo
			if (bmoWFlowStep.getProfileId().toInteger() == profileId) {

				// Asigna el usuario recien creado
				bmoWFlowStep.getUserId().setValue(userId);

				// Se esta eliminando usuario, buscar asignacion de usuario en los grupos, para dejar otro usuario
				if (!(userId > 0)) {
					System.out.println("Se esta buscando asignar un usuario de la lista!");

					BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
					ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

					// Filtrar por flujo
					BmFilter filterByWFlow = new BmFilter();
					filterByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoWFlow.getId());
					filterList.add(filterByWFlow);

					// Filtrar por grupo
					BmFilter filterByGroup = new BmFilter();
					filterByGroup.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getProfileId(), profileId);
					filterList.add(filterByGroup);

					Iterator<BmObject> wFlowUserList = pmWFlowUser.list(pmConn, filterList).iterator();

					// Se encontro otro usuario asignado como colaborador del grupo faltante
					if (wFlowUserList.hasNext()) {
						bmoWFlowStep.getUserId().setValue(((BmoWFlowUser)wFlowUserList.next()).getUserId().toInteger());
					} else {
						// No se encontro colaborador; buscar usuario que pertenezca al grupo faltante
						bmoWFlowStep.getUserId().setValue(firstWFlowUserInGroup(pmConn, bmoWFlow.getId(), profileId));
					}
				}

				this.saveSimple(pmConn, bmoWFlowStep, bmUpdateResult);

			} else {
				// Es un grupo diferente. Si el usuario pertenece a ese grupo, asignarlo tambien si no tiene asignación
				if (!(bmoWFlowStep.getUserId().toInteger() > 0)) {
					if (pmProfileUser.userInProfile(pmConn, bmoWFlowStep.getProfileId().toInteger(), userId)) {
						bmoWFlowStep.getUserId().setValue(userId);
					}

					this.saveSimple(pmConn, bmoWFlowStep, bmUpdateResult);
				} else {
					// Como ultima validacion, verifica que existan usuarios del grupo actual en colaboradores, si no, elimina asignacion usuario
					if (!wFlowUserExists(pmConn, bmoWFlow.getId(), bmoWFlowStep.getUserId().toInteger())) {
						bmoWFlowStep.getUserId().setValue(-1);
						this.saveSimple(pmConn, bmoWFlowStep, bmUpdateResult);
					}
				}
			}
		}
	}

	// Verifica que el usuario exista en el flujo
	private boolean wFlowUserExists(PmConn pmConn, int wFlowId, int userId) throws SFException {
		pmConn.doFetch("SELECT * FROM wflowusers "
				+ " WHERE wflu_wflowid = " + wFlowId + ""
				+ " AND wflu_userid = " + userId + "");

		return pmConn.next();
	}

	// Algun usuario del grupo, en el flujo
	private int firstWFlowUserInGroup(PmConn pmConn, int wFlowId, int profileId) throws SFException {
		pmConn.doFetch("SELECT * FROM wflowusers "
				+ " WHERE wflu_wflowid = " + wFlowId + ""
				+ " AND wflu_userid IN ( SELECT pfus_userid FROM profileusers WHERE pfus_profileid = " + profileId + ")");

		if (pmConn.next()) {
			return pmConn.getInt("wflu_userid");
		}
		else return -1;
	}

	// Obtiene la ultima secuencia del flujo
	private int nextSequence(PmConn pmConn, int wFlowId) throws SFException {
		pmConn.doFetch("SELECT MAX(wfsp_sequence) as S FROM wflowsteps "
				+ " WHERE wfsp_wflowid = " + wFlowId + "");

		if (pmConn.next()) {
			return (pmConn.getInt("S") + 1);
		}
		else return 1;
	}

	// Prepara lista de destinatarios del correo
	private ArrayList<SFMailAddress> getStepMailList(int wFlowStepId, int wflowId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		String mailString = "";

		// Revisar usuarios asignados a grupos de la tarea y al wflow en cuestion 
		String sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname FROM profileusers " +
				" LEFT JOIN users ON (pfus_userid = user_userid) " +
				" WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "'" +
				" AND (pfus_profileid IN (SELECT wfsp_profileid FROM wflowsteps WHERE wfsp_wflowstepid = " + wFlowStepId + ") " +
				" AND pfus_userid IN (SELECT wflu_userid FROM wflowusers WHERE wflu_wflowid = " + wflowId + ")) ";

		System.out.println("Buscar usuarios asignados al Grupo de la Tarea: SQL: " + sql);

		try {
			pmConn.open();
			pmConn.doFetch(sql);

			int mailCount = 0;
			// Si existen colaboradores en el wflow
			while (pmConn.next()) {
				String name = pmConn.getString(2) + " " + pmConn.getString(3) + " " + pmConn.getString(4);
				SFMailAddress mail = new SFMailAddress(pmConn.getString(1), name);
				mailList.add(mail);
				mailCount++;

				mailString += pmConn.getString(1) + ", "; 
			}

			// Si no hay colaboradores en el wflow, tomar los colaboradores de los grupos
			if (mailCount == 0) {

				System.out.println("No hay destinatarios del correo - se busca enviar a todos los usuarios del grupo de la tarea.");

				sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname " +
						" FROM profileusers " +
						" LEFT JOIN users on (pfus_userid = user_userid) " +
						" WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "' " +
						" AND pfus_profileid in (select wfsp_profileid from wflowsteps where wfsp_wflowstepid = " + wFlowStepId + ") ";
				pmConn.doFetch(sql);
				while (pmConn.next()) {
					String name = pmConn.getString(2) + " " + pmConn.getString(3) + " " + pmConn.getString(4);
					SFMailAddress mail = new SFMailAddress(pmConn.getString(1), name);
					mailList.add(mail);
					mailCount++;
					mailString += pmConn.getString(1) + ", "; 
				}
			}

			// No hay ni usuarios del wflow ni del grupo asignado a la tarea
			if (mailCount == 0) 
				System.out.println("PmWFlowStep-getMailStep(): No hay destinatarios del correo - no hay usuarios asignados al grupo de la tarea o al flujo.");

			if (!getSFParams().isProduction()) 
				System.out.println(this.getClass().getName() + "-getStepMailList(): La lista de correos es: " + mailString);

		} catch (SFPmException e) {
			throw new SFException("PmWFlowStep-sendReminders() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return mailList;
	}

	// Envia la notificacion cuando una tarea haya sido activada
	public void sendMailComments(PmConn pmConn, BmoWFlowStep bmoWFlowStep) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		// Valida que tanto la categoria como el paso tengan habilitado el envio de notificaciones
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableEmailReminderComments().toBoolean()
				&& bmoWFlowStep.getEmailReminderComments().toBoolean()) {

			// Obtiene la lista de correos de los usuarios de flujo
			mailList = getWFlowUsersMailList(bmoWFlowStep.getWFlowId().toInteger());

			String subject = getSFParams().getAppCode() 
					+ " Comentarios de la Tarea: " 
					+ bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
					+ "." + bmoWFlowStep.getSequence().toString() 
					+ " " + bmoWFlowStep.getName().toString() 
					+ " (" + bmoWFlowStep.getBmoWFlow().getCode().toString()
					+ " " + bmoWFlowStep.getBmoWFlow().getName().toString() + ")";

			String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), 
					"Comentarios de la Tarea", 
					" 	<p style=\"font-size:12px\"> "
							+ " <b>WFlow:</b> " + bmoWFlowStep.getBmoWFlow().getCode().toString() + " " + bmoWFlowStep.getBmoWFlow().getName().toString()
							+ " <br> "
							+ "	<b>Tarea:</b> " + bmoWFlowStep.getBmoWFlowPhase().getSequence().toString() 
							+ 				"." + bmoWFlowStep.getSequence().toString() 
							+ 				" " + bmoWFlowStep.getName().toString()
							+ "	<br> "
							+ "	<b>Comentarios:</b> " + bmoWFlowStep.getComments().toString()
							//							+ "	<br> "
							//							+ "	</p> "
							//							+ "	<p align=\"center\" style=\"font-size:12px\"> "
							//							+ "		Favor de dar Seguimiento a la Tarea <a href=\""
							//							+ getSFParams().getAppURL() + "start.jsp?startprogram=" + bmoWFlowStep.getBmoWFlow().getCallerCode().toString() +"&foreignid=" + bmoWFlowStep.getBmoWFlow().getCallerId().toString() + "\">Aqui</a>. "
							//							+ "	</p> "
					);

			// Quitar emails repetidos
			ArrayList<SFMailAddress> mailListNoRepeat = new ArrayList<SFMailAddress>();
			for (SFMailAddress event : mailList) {
				boolean isFound = false;
				// Revisar si el email existe en noRepeat
				for (SFMailAddress e : mailListNoRepeat) {
					if (e.getEmail().equals(event.getEmail()) || (e.equals(event))) {
						isFound = true;        
						break;
					}
				}
				// Si no encontro ninguno añadirlo a la nueva lista
				if (!isFound) mailListNoRepeat.add(event);
			}

			// Si hay destinatarios, enviar los correos
			if (mailListNoRepeat.size() > 0) {
				SFSendMail.send(getSFParams(),
						mailListNoRepeat, 
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getBmoSFConfig().getAppTitle().toString(), 
						subject, 
						msgBody);
			}
		}
	}

	// Prepara lista de destinatarios del correo, Usuarios de Flujo
	private ArrayList<SFMailAddress> getWFlowUsersMailList(int wflowId) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
		String mailString = "";

		// Revisar usuarios asignados a grupos de la tarea y al wflow en cuestion 
		String sql = "SELECT user_email, user_firstname, user_fatherlastname, user_motherlastname FROM wflowusers " +
				" LEFT JOIN users ON (wflu_userid = user_userid) " +
				" WHERE user_status = '" + BmoUser.STATUS_ACTIVE + "'" +
				" AND wflu_wflowid = " + wflowId + "";

		printDevLog("Buscar usuarios de flujo - SQL: " + sql);

		try {
			pmConn.open();
			pmConn.doFetch(sql);

			// Si existen colaboradores en el wflow
			while (pmConn.next()) {
				String name = pmConn.getString(2) + " " + pmConn.getString(3) + " " + pmConn.getString(4);
				SFMailAddress mail = new SFMailAddress(pmConn.getString(1), name);
				mailList.add(mail);
				mailString += pmConn.getString(1) + ", "; 
			}

			printDevLog(this.getClass().getName() + "-getWFlowUsersMailList(): La lista de correos es: " + mailString);
		} catch (SFPmException e) {
			throw new SFException("PmWFlowStep-getWFlowUsersMailList() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}

		return mailList;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowStep = (BmoWFlowStep) bmObject;

		// Agregar bitacora de flujos
		String comments = "Tarea Eliminada: " + bmoWFlowStep.getName().toString() + ", " + "Fase: "
				+ bmoWFlowStep.getBmoWFlowPhase().getName().toString() + ", " + "Avance: "
				+ bmoWFlowStep.getProgress().toString() + "% " + "Comentarios: "
				+ bmoWFlowStep.getComments().toString();

		// Obtener el flujo
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		BmoWFlow bmoWFlow = (BmoWFlow) pmWFlow.get(pmConn, bmoWFlowStep.getWFlowId().toInteger());

		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlowStep.getWFlowId().toInteger(),  BmoWFlowLog.TYPE_WFLOW, comments);

		// TODO: en lugar de calcular todo el wflow, solo lo que afecta esta tarea
		pmWFlow.calculate(pmConn, bmoWFlow, bmUpdateResult);
		// Elimina la tarea
		super.delete(pmConn, bmoWFlowStep, bmUpdateResult);

		return bmUpdateResult;		
	}
}
