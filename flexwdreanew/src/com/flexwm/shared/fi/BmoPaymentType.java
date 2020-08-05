/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoPaymentType extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField codeSAT, name, description;

	public BmoPaymentType() {
		super("com.flexwm.server.fi.PmPaymentType", "paymenttypes", "paymenttypeid", "PAYT", "Formas de Pago");
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		codeSAT = setField("codesat", "", "Clave SAT", 5, Types.VARCHAR, true, BmFieldType.CODE, true);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCodeSAT(),
				getName(),
				getDescription()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCodeSAT(),
				getName()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCodeSAT(),
				getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCodeSAT()), 
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCodeSAT(), BmOrder.ASC)));
	}

	public BmField getCodeSAT() {
		return codeSAT;
	}

	public BmField getName() {
		return name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setCodeSAT(BmField codeSAT) {
		this.codeSAT = codeSAT;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}
}
