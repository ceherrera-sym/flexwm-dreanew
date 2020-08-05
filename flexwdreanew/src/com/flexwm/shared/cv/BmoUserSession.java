/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.cv;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoUser;

/**
 * @author smuniz
 *
 */

public class BmoUserSession extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField userId, trainingSessionId, attended, additionalSessionId;
	BmoUser bmoUser = new BmoUser();
	BmoTrainingSession bmoTrainingSession = new BmoTrainingSession();
		
	public BmoUserSession(){
		super("com.flexwm.server.cv.PmUserSession", "usersessions", "usersessionid", "USES", "Sesiones de Usuario");
		
		//Campo de Datos
		userId = setField("userid", "", "Usuario", 8, Types.INTEGER, true, BmFieldType.ID, false);
		trainingSessionId = setField("trainingsessionid", "", "Sesión", 8, Types.INTEGER, true, BmFieldType.ID, false);
		attended = setField("attended", "", "Atendido", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		additionalSessionId = setField("additionalsessionid", "", "Sesión Adicional", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoUser.getEmail(),		
				bmoTrainingSession.getBmoCourse().getBmoCourseProgram().getName(),
				getAttended()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoUser().getEmail()), 
				new BmSearchField(getBmoTrainingSession().getLocationName())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoUser().getEmail(), BmOrder.ASC)));
	}

	/**
	 * @return the userId
	 */
	public BmField getUserId() {
		return userId;
	}


	/**
	 * @param userId the userId to set
	 */
	public void setUserId(BmField userId) {
		this.userId = userId;
	}


	/**
	 * @return the sessionId
	 */
	public BmField getTrainingSessionId() {
		return trainingSessionId;
	}


	/**
	 * @param sessionId the sessionId to set
	 */
	public void setTrainingSessionId(BmField sessionId) {
		this.trainingSessionId = sessionId;
	}


	/**
	 * @return the attended
	 */
	public BmField getAttended() {
		return attended;
	}


	/**
	 * @param attended the attended to set
	 */
	public void setAttended(BmField attended) {
		this.attended = attended;
	}


	/**
	 * @return the additionalSessionId
	 */
	public BmField getAdditionalSessionId() {
		return additionalSessionId;
	}


	/**
	 * @param additionalSessionId the additionalSessionId to set
	 */
	public void setAdditionalSessionId(BmField additionalSessionId) {
		this.additionalSessionId = additionalSessionId;
	}


	/**
	 * @return the bmoUser
	 */
	public BmoUser getBmoUser() {
		return bmoUser;
	}


	/**
	 * @param bmoUser the bmoUser to set
	 */
	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}


	/**
	 * @return the bmoTrainingSession
	 */
	public BmoTrainingSession getBmoTrainingSession() {
		return bmoTrainingSession;
	}


	/**
	 * @param bmoSession the bmoTrainingSession to set
	 */
	public void setBmoTrainingSession(BmoTrainingSession bmoTrainingSession) {
		this.bmoTrainingSession = bmoTrainingSession;
	}

	
	
}
