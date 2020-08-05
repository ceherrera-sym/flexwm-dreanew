/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.server.op.PmOrderType;
import com.flexwm.shared.co.BmoPropertyType;
import com.flexwm.shared.op.BmoOrderType;
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

public class PmPropertyType extends PmObject{
	BmoPropertyType bmoPropertyType;

	public PmPropertyType(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoPropertyType = new BmoPropertyType();
		setBmObject(bmoPropertyType); 
		
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPropertyType.getOrderTypeId(), bmoPropertyType.getBmoOrderType())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoPropertyType = (BmoPropertyType)autoPopulate(pmConn, new BmoPropertyType());		

		//BmoOrderType
		BmoOrderType bmoOrderType = new BmoOrderType();
		int orderTypeId = (int)pmConn.getInt(bmoOrderType.getIdFieldName());
		if (orderTypeId > 0) 
			bmoPropertyType.setBmoOrderType((BmoOrderType) new PmOrderType(getSFParams()).populate(pmConn));
		else bmoPropertyType.setBmoOrderType(bmoOrderType);

		return bmoPropertyType;
	}	

}
