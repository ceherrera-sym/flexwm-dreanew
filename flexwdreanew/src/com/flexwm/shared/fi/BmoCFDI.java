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


public class BmoCFDI extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField codeSAT, description;

	public BmoCFDI() {
		super("com.flexwm.server.fi.PmCFDI", "cfdi", "cfdiid", "CFDI", "Uso CFDI");
		codeSAT = setField("codesat", "", "Clave SAT", 5, Types.VARCHAR, true, BmFieldType.CODE, true);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCodeSAT(),
				getDescription()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCodeSAT(),
				getDescription()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCodeSAT(),
				getDescription()				
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCodeSAT()), 
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCodeSAT(), BmOrder.ASC)));
	}

	public BmField getCodeSAT() {
		return codeSAT;
	}


	public BmField getDescription() {
		return description;
	}

	public void setCodeSAT(BmField codeSAT) {
		this.codeSAT = codeSAT;
	}


	public void setDescription(BmField description) {
		this.description = description;
	}
}
