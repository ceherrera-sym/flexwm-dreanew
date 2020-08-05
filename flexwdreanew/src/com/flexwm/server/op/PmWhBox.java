package com.flexwm.server.op;

import com.flexwm.shared.op.BmoWhBox;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWhBox extends PmObject {
	BmoWhBox bmoWhBox;

	public PmWhBox(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhBox = new BmoWhBox();
		setBmObject(bmoWhBox);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoWhBox());
	}

	// Busca caja por clave
	public BmoWhBox searchBoxByCode(PmConn pmConn, String code) throws SFException {
		BmoWhBox bmoWhBox = new BmoWhBox();
		String sql = "";

		// Busca la clave del producto dentro de los items del recibo de orden de compra
		sql = "SELECT whbx_whboxid FROM whboxes " +
				" WHERE whbx_code LIKE '" + code + "'"; 
		System.out.println(sql);
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			bmoWhBox = (BmoWhBox)this.get(pmConn, pmConn.getInt("whbx_whboxid"));	
		}

		return bmoWhBox;
	}

}
