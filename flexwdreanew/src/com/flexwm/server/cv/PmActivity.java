/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.cv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import com.flexwm.server.FlexUtil;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.cv.BmoActivity;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;


public class PmActivity extends PmObject {
	BmoActivity bmoActivity;


	public PmActivity(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoActivity = new BmoActivity();
		setBmObject(bmoActivity);	

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoActivity.getUserId(), bmoActivity.getBmoUser()),
				new PmJoin(bmoActivity.getBmoUser().getAreaId(), bmoActivity.getBmoUser().getBmoArea()),
				new PmJoin(bmoActivity.getBmoUser().getLocationId(), bmoActivity.getBmoUser().getBmoLocation()),
				new PmJoin(bmoActivity.getWFlowId(), bmoActivity.getBmoWFlow()),
				new PmJoin(bmoActivity.getBmoWFlow().getWFlowTypeId(), bmoActivity.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoActivity.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoActivity.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoActivity.getBmoWFlow().getWFlowPhaseId(), bmoActivity.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoActivity.getBmoWFlow().getWFlowFunnelId(), bmoActivity.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoActivity = (BmoActivity)autoPopulate(pmConn, new BmoActivity());
		
		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if (pmConn.getInt(bmoUser.getIdFieldName()) > 0) bmoActivity.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoActivity.setBmoUser(bmoUser);
		
		return bmoActivity;			
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoActivity bmoActivity = (BmoActivity)bmObject;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoActivity.getId() > 0)) {			
			super.save(pmConn, bmoActivity, bmUpdateResult);
			
			// Si no esta asignada ya la clave por algun objeto foraneo, crear nueva
			if (!(bmoActivity.getCode().toString().length() > 0)) {
				// La clave debe tener 4 digitos
				String code = FlexUtil.codeFormatDigits(bmUpdateResult.getId(), 4, BmoActivity.CODE_PREFIX);			
				bmoActivity.getCode().setValue(code);
			}	
		}
		
		// Si no esta asignado el tipo de wflow, toma el primero
		if (!(bmoActivity.getWFlowTypeId().toInteger() > 0)) {
			int wFlowTypeId = new PmWFlowType(getSFParams()).getFirstWFlowTypeId(pmConn, bmoActivity.getProgramCode());
			if (wFlowTypeId > 0) {
				bmoActivity.getWFlowTypeId().setValue(wFlowTypeId);
			} else {
				bmUpdateResult.addError(bmoActivity.getCode().getName(), "Debe crearse Tipo de WFlow para Actividades.");
			}
			
		}

		// Crea el WFlow y asigna el ID recien creado
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		bmoActivity.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoActivity.getWFlowTypeId().toInteger(), bmoActivity.getWFlowId().toInteger(), 
				bmoActivity.getProgramCode(), bmoActivity.getId(), bmoActivity.getUserId().toInteger(), -1, -1,
				bmoActivity.getCode().toString(), bmoActivity.getName().toString(), bmoActivity.getDescription().toString(), 
				bmoActivity.getStartdate().toString(), bmoActivity.getEnddate().toString(), BmoWFlow.STATUS_ACTIVE, bmUpdateResult).getId());

		super.save(pmConn, bmoActivity, bmUpdateResult);

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoActivity bmoActivity = (BmoActivity)bmObject;

		super.delete(pmConn, bmoActivity, bmUpdateResult);

		if (!bmUpdateResult.hasErrors()) {
			// Eliminar flujos
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			BmoWFlow bmoWFlow = new BmoWFlow();
			BmFilter filterByActivity = new BmFilter();
			filterByActivity.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoActivity.getId());			
			BmFilter filterWFlowCategory = new BmFilter();
			filterWFlowCategory.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoActivity.getProgramCode());
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			filterList.add(filterByActivity);
			filterList.add(filterWFlowCategory);
			ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
			while (wFlowList.hasNext()) {
				bmoWFlow = (BmoWFlow)wFlowList.next();
				pmWFlow.delete(pmConn,  bmoWFlow, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}
	
	//Enviar recordatorios de Actividades
	public void prepareReminders() throws SFException{
		PmConn pmConn = new PmConn(getSFParams());
		String sql = 
				"SELECT acti_activityid FROM activities " + 
				" LEFT JOIN users ON (acti_userid = user_userid)" + 
				" WHERE acti_status = '"+ BmoActivity.STATUS_ACTIVE+"'  " +
				" AND acti_reminddate <= '" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()) + "'"+
				" AND acti_emailreminders = 1  " + 
				" AND user_status = '"+BmoUser.STATUS_ACTIVE+"'";
				
				
		if (!getSFParams().isProduction()) 
			System.out.println(this.getClass().getName() + "-prepareReminders(): " + sql);

		try {
			pmConn.open();
			pmConn.doFetch(sql);

			while (pmConn.next()) {
				int activityId = pmConn.getInt(1);	
							
				BmoActivity bmoActivity = (BmoActivity)this.get(activityId);

				// Enviar recordatorio por cada paso que lo requiera
				this.sendMailReminder(bmoActivity);
			}

		} catch (SFPmException e) {
			throw new SFException("PmWFlowStep-sendReminders() ERROR: " + e.toString());
		} finally {
			pmConn.close();
		}
	}
	
	//Envia los recordatorios via email al responsable de la atividad
	private void sendMailReminder(BmoActivity bmoActivity) throws SFException {
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		// Valida que la actividad aplique envio de recordatorios
		if (bmoActivity.getEmailReminders().toBoolean()) {

			// Obtiene el usuario a enviar correo
			PmUser pmUser = new PmUser(getSFParams());
			BmoUser bmoUser = new BmoUser();
			bmoUser = (BmoUser) pmUser.get(bmoActivity.getUserId().toInteger());

			// Si esta activo añadir a la lista
			if (bmoUser.getStatus().equals(BmoUser.STATUS_ACTIVE)) {
				// Lista de correo del usuario responsable de la actividad
				mailList.add(new SFMailAddress(bmoUser.getEmail().toString(),
						bmoUser.getFirstname().toString() + " " + bmoUser.getFatherlastname().toString()));
			} else
				printDevLog("Usuario inactivo : " + bmoUser.getEmail().toString());
			
			// Si hay mas de un destinatario, preparar y enviar correo
			if (mailList.size() > 0) {
				String subject = getSFParams().getAppCode() + " Recordatorio de la Actividad: "
						+ bmoActivity.getCode().toString() + bmoActivity.getName().toString();

				String msgBody = HtmlUtil.mailBodyFormat(getSFParams(), "Recordatorio de Actividad",
						" 	<p style=\"font-size:12px\"> " + " <b>Actividad:</b> " + bmoActivity.getCode().toString()
								+ " " + bmoActivity.getName().toString() + " <br> " + "	<b>Descripcion:</b> "
								+ bmoActivity.getDescription().toString() + "	<br> " + "	</p> "
								+ "	<p align=\"center\" style=\"font-size:12px\"> "
								+ "		Favor de dar Seguimiento a la Actividad <a href=\"" 
								+ getSFParams().getAppURL()
								+ "/start.jsp?startprogram=" + bmoActivity.getProgramCode() + "&foreignid="
								+ bmoActivity.getId() + "\">Aqu&iacute;</a>. " + "	</p> ");
				
			
				// Si es produccion, envia correo
				if (getSFParams().isProduction()) {
					SFSendMail.send(getSFParams(), mailList, getSFParams().getBmoSFConfig().getEmail().toString(),
							getSFParams().getBmoSFConfig().getAppTitle().toString(), subject, msgBody);
				} else {
					System.out.println(this.getClass().getName() + "-sendMailReminder(): Se envia correo: " + "De: "
							+ getSFParams().getBmoSFConfig().getEmail().toString() + " Para: "+mailList.get(0).getEmail()+ " Asunto: " + subject + "Cuerpo: "
							+ msgBody);
				}

			}
		}

	}
}

