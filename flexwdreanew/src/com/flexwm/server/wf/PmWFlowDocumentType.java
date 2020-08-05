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
import com.flexwm.server.wf.PmWFlowType;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlowDocumentType;
import com.flexwm.shared.wf.BmoWFlowType;

public class PmWFlowDocumentType extends PmObject {
	BmoWFlowDocumentType bmoWFlowDocumentType;
	
	public PmWFlowDocumentType(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowDocumentType = new BmoWFlowDocumentType();
		setBmObject(bmoWFlowDocumentType);
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowDocumentType.getWFlowTypeId(), bmoWFlowDocumentType.getBmoWFlowType()),
				new PmJoin(bmoWFlowDocumentType.getBmoWFlowType().getWFlowCategoryId(), bmoWFlowDocumentType.getBmoWFlowType().getBmoWFlowCategory())
				)));
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowDocumentType bmoWFlowDocumentType = (BmoWFlowDocumentType)autoPopulate(pmConn, new BmoWFlowDocumentType());		
		
		// BmoWFlowDocumentType
		BmoWFlowType bmoWFlowType = new BmoWFlowType();
		int wflowTypeId = (int)pmConn.getInt(bmoWFlowType.getIdFieldName());
		if (wflowTypeId > 0) bmoWFlowDocumentType.setBmoWFlowType((BmoWFlowType) new PmWFlowType(getSFParams()).populate(pmConn));
		else bmoWFlowDocumentType.setBmoWFlowType(bmoWFlowType);
		
		return bmoWFlowDocumentType;
	}
}
