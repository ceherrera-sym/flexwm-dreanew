/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoPropertySaleDetail extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField notary, wrintingNumber, creditModality, propertySaleId,description,descriptionLog;
	private BmoSupplier bmoSupplier = new BmoSupplier();
	
	public BmoPropertySaleDetail() {
		super("com.flexwm.server.co.PmPropertySaleDetail", "propertysaledetails", "propertysaledetailid", "PRSD", "Detalle");
		
		propertySaleId = setField("propertysaleid", "", "Venta Inm.", 8, Types.INTEGER, false, BmFieldType.ID, false);
		notary = setField("notary", "", "Notario", 8, Types.INTEGER, true, BmFieldType.ID, false);
		wrintingNumber = setField("writingnumber", "", "Núm. de Escritura", 20, Types.VARCHAR, true, BmFieldType.STRING, false);
		creditModality = setField("creditmodality", "", "Modalidad de Crédito", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
		descriptionLog = setField("descriptionlog", "", "Bitácora de descripción", 100000, Types.VARCHAR, true, BmFieldType.STRING, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoSupplier().getName(),
				getWrintingNumber()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoSupplier().getName()),
				new BmSearchField(getWrintingNumber())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}
	
	public BmField getNotary() {
		return notary;
	}
	
	public void setNotary(BmField notary) {
		this.notary = notary;
	}
	
	public BmField getWrintingNumber() {
		return wrintingNumber;
	}
	
	public void setWrintingNumber(BmField wrintingNumber) {
		this.wrintingNumber = wrintingNumber;
	}
	
	public BmField getPropertySaleId() {
		return propertySaleId;
	}
	
	public void setPropertySaleId(BmField propertySaleId) {
		this.propertySaleId = propertySaleId;
	}
	
	public BmoSupplier getBmoSupplier() {
		return bmoSupplier;
	}
	
	public void setBmoSupplier(BmoSupplier bmoSupplier) {
		this.bmoSupplier = bmoSupplier;
	}

	public BmField getCreditModality() {
		return creditModality;
	}

	public void setCreditModality(BmField creditModality) {
		this.creditModality = creditModality;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getDescriptionLog() {
		return descriptionLog;
	}

	public void setDescriptionLog(BmField descriptionLog) {
		this.descriptionLog = descriptionLog;
	}
	
	
}