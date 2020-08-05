/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoProductKitItem;


public class PmProductKitItem extends PmObject {
	BmoProductKitItem bmoProductKitItem;

	public PmProductKitItem(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProductKitItem = new BmoProductKitItem();
		setBmObject(bmoProductKitItem);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoProductKitItem.getProductId(), bmoProductKitItem.getBmoProduct()),
				new PmJoin(bmoProductKitItem.getBmoProduct().getProductFamilyId(), bmoProductKitItem.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoProductKitItem.getBmoProduct().getProductGroupId(), bmoProductKitItem.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoProductKitItem.getBmoProduct().getUnitId(), bmoProductKitItem.getBmoProduct().getBmoUnit())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoProductKitItem = (BmoProductKitItem)autoPopulate(pmConn, new BmoProductKitItem());

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productId > 0) bmoProductKitItem.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoProductKitItem.setBmoProduct(bmoProduct);

		return bmoProductKitItem;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoProductKitItem = (BmoProductKitItem)bmObject;

		BmoProduct bmoProduct = new BmoProduct();
		PmProduct pmProduct = new PmProduct(getSFParams());

		// Obtener Producto
		if (bmoProductKitItem.getProductId().toInteger() > 0 ) {
			bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoProductKitItem.getProductId().toInteger());

			// Validar que la cantidad no sea fraccion si el producto es SERIE
			// Si es Sin Rastreo, verificar por la Unidad del producto
			if (!pmProduct.applyFraction(bmoProduct, bmoProductKitItem.getQuantity().toDouble()))
				bmUpdateResult.addError(bmoProductKitItem.getQuantity().getName(), "<b>La Cantidad del Producto no acepta decimales.</b>");
		}

		if (!bmUpdateResult.hasErrors())
			super.save(pmConn, bmoProductKitItem, bmUpdateResult);

		return bmUpdateResult;
	}
}
