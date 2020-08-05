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
import com.symgae.server.sf.PmProfile;
import com.flexwm.server.wf.PmWFlowCategory;
import com.symgae.server.PmConn;
import com.symgae.shared.SFParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.flexwm.shared.wf.BmoWFlowCategory;
import com.flexwm.shared.wf.BmoWFlowCategoryProfile;

public class PmWFlowCategoryProfile extends PmObject {
	BmoWFlowCategoryProfile bmoWFlowGroup;
	
	public PmWFlowCategoryProfile(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowGroup = new BmoWFlowCategoryProfile();
		setBmObject(bmoWFlowGroup);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowGroup.getProfileId(), bmoWFlowGroup.getBmoProfile()),
				new PmJoin(bmoWFlowGroup.getWFlowCategoryId(), bmoWFlowGroup.getBmoWFlowCategory())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWFlowGroup =  (BmoWFlowCategoryProfile)autoPopulate(pmConn, new BmoWFlowCategoryProfile());
		
		// BmoWFlowGroup
		BmoProfile bmoProfile = new BmoProfile();
		int wflowGroupId = (int)pmConn.getInt(bmoWFlowGroup.getIdFieldName());
		if (wflowGroupId > 0) bmoWFlowGroup.setBmoProfile((BmoProfile) new PmProfile(getSFParams()).populate(pmConn));
		else bmoWFlowGroup.setBmoProfile(bmoProfile);
		
		// BmoWFlowCategory
		BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();
		int wflowCategoryId = (int)pmConn.getInt(bmoWFlowGroup.getIdFieldName());
		if (wflowCategoryId > 0) bmoWFlowGroup.setBmoWFlowCategory((BmoWFlowCategory) new PmWFlowCategory(getSFParams()).populate(pmConn));
		else bmoWFlowGroup.setBmoWFlowCategory(bmoWFlowCategory);
		
		return bmoWFlowGroup;
	}
}
