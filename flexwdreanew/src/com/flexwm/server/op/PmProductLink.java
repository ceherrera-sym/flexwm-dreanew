/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author smuniz
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductLink;


public class PmProductLink extends PmObject {
	BmoProductLink bmoProductLink;

	public PmProductLink(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProductLink = new BmoProductLink();
		setBmObject(bmoProductLink);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProductLink.getProductLinkedId(), bmoProductLink.getBmoProductLinked()),
				new PmJoin(bmoProductLink.getBmoProductLinked().getProductFamilyId(), bmoProductLink.getBmoProductLinked().getBmoProductFamily()),
				new PmJoin(bmoProductLink.getBmoProductLinked().getProductGroupId(), bmoProductLink.getBmoProductLinked().getBmoProductGroup()),
				new PmJoin(bmoProductLink.getBmoProductLinked().getUnitId(), bmoProductLink.getBmoProductLinked().getBmoUnit())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoProductLink = (BmoProductLink)autoPopulate(pmConn, new BmoProductLink());

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoProductLink.setBmoProductLinked((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoProductLink.setBmoProductLinked(bmoProduct);

		return bmoProductLink;
	}
}
