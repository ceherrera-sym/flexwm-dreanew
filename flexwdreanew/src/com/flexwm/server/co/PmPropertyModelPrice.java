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

import com.flexwm.shared.co.BmoPropertyModelPrice;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


/**
 * @author smuniz
 *
 */

public class PmPropertyModelPrice extends PmObject{
	BmoPropertyModelPrice bmoPropertyModelPrice;

	public PmPropertyModelPrice(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoPropertyModelPrice = new BmoPropertyModelPrice();
		setBmObject(bmoPropertyModelPrice); 

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoPropertyModelPrice = (BmoPropertyModelPrice)autoPopulate(pmConn, new BmoPropertyModelPrice());
		return bmoPropertyModelPrice;
	}

}

