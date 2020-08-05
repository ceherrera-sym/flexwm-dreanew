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
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmArea;
import com.symgae.server.sf.PmProfile;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlowUserSelect;


public class PmWFlowUserSelect extends PmObject {
	BmoWFlowUserSelect bmoWFlowUserSelect;

	public PmWFlowUserSelect(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowUserSelect = new BmoWFlowUserSelect();
		setBmObject(bmoWFlowUserSelect);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowUserSelect.getAreaId(), bmoWFlowUserSelect.getBmoArea()),
				new PmJoin(bmoWFlowUserSelect.getProfileId(), bmoWFlowUserSelect.getBmoProfile())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowUserSelect = (BmoWFlowUserSelect)autoPopulate(pmConn, new BmoWFlowUserSelect());

		// BmoArea
		BmoArea bmoArea = new BmoArea();
		if (pmConn.getInt(bmoArea.getIdFieldName()) > 0) 
			bmoWFlowUserSelect.setBmoArea((BmoArea) new PmArea(getSFParams()).populate(pmConn));
		else 
			bmoWFlowUserSelect.setBmoArea(bmoArea);
		
		// BmoProfile
		BmoProfile bmoProfile = new BmoProfile();
		if (pmConn.getInt(bmoProfile.getIdFieldName()) > 0) 
			bmoWFlowUserSelect.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else 
			bmoWFlowUserSelect.setBmoProfile(bmoProfile);

		return bmoWFlowUserSelect;
	}

	public boolean canAssign(PmConn pmConn, BmoUser bmoUser) throws SFPmException{
		boolean canAssign = false;
		String sql = "";
		// Determinar si hay restricción en el area del usuario
		sql = "SELECT * FROM wflowuserselect WHERE wfus_areaid = " + bmoUser.getAreaId().toInteger();
		pmConn.doFetch(sql);
		int count = 0;
		
		// Si hay registros ligados a esa area, validar que el usuario tenga privilegios
		while (pmConn.next()) {
			int profileId = pmConn.getInt("wfus_profileid");
			
			// Revisar que el usuario ligado exista en un grupo
			PmConn pmConn2 = new PmConn(getSFParams());
			pmConn2.open();
			sql = "SELECT * FROM profileusers WHERE pfus_userid = " + getSFParams().getLoginInfo().getUserId() + " "
					+ " AND pfus_profileid = " + profileId;
			pmConn2.doFetch(sql);
			// Si el usuario esta en el grupo requerido, si tiene acceso
			if (pmConn2.next()) canAssign = true;
			pmConn2.close();
			count++;
		}
		
		if (count == 0) canAssign = true;
		
		return canAssign;		
	}
}
