/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ac;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.ac.BmoProgramSession;
import com.flexwm.shared.ac.BmoProgramSessionLevel;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author smuniz
 *
 */

public class PmProgramSessionLevel extends PmObject {
	BmoProgramSessionLevel bmoProgramSessionLevel;

	public PmProgramSessionLevel(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProgramSessionLevel = new BmoProgramSessionLevel();
		setBmObject(bmoProgramSessionLevel);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(new PmJoin(bmoProgramSessionLevel.getProgramSessionId(), 
				bmoProgramSessionLevel.getBmoProgramSession()))));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoProgramSessionLevel = (BmoProgramSessionLevel)autoPopulate(pmConn, new BmoProgramSessionLevel());

		// BmoProgramSesion
		BmoProgramSession bmoProgramSession = new BmoProgramSession();
		int productFamilyId = (int)pmConn.getInt(bmoProgramSession.getIdFieldName());
		if (productFamilyId > 0) bmoProgramSessionLevel.setBmoProgramSession((BmoProgramSession) new PmProgramSession(getSFParams()).populate(pmConn));
		else bmoProgramSessionLevel.setBmoProgramSession(bmoProgramSession);

		return bmoProgramSessionLevel;
	}
}
