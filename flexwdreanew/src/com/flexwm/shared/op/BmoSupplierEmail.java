/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoSupplierEmail extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, email, supplierId;

	public static char TYPE_WORK = 'W';
	public static char TYPE_PERSONAL = 'P';
	
	public BmoSupplierEmail() {
		super("com.flexwm.server.op.PmSupplierEmail", "supplieremails", "supplieremailid", "SUEM", "Emails");
		
		// Campo de datos
		type = setField("type", "", "Tipo Email", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PERSONAL, "Personal", "./icons/personal.png"),
				new BmFieldOption(TYPE_WORK, "Trabajo", "./icons/work.png")
				)));
		
		email = setField("email", "", "Email", 100, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getType(), 
						getEmail()
						));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getEmail().getName(), getEmail().getLabel())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getEmail(), BmOrder.ASC)));
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getEmail() {
		return email;
	}

	public void setEmail(BmField email) {
		this.email = email;
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}

}
