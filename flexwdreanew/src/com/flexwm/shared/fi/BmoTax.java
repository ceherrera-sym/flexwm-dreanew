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

public class BmoTax extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField code,descrition;
	public static final String ACCESS_SAVE = "SAVETAX";
	
	public BmoTax() {
		super("com.flexwm.server.fi.PmTax","taxs","taxid","TAXS","Impuestos");
		code = setField("code", "", "Codigo SAT", 3, Types.VARCHAR, false, BmFieldType.CODE, true);
		descrition = setField("descrition", "", "Descripción", 10, Types.VARCHAR, false, BmFieldType.STRING, false);
	}
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getDescrition()
				));
	}
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()),
				new BmSearchField(getDescrition())
				));
	}
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
	}

	public BmField getCode() {
		return code;
	}
	public void setCode(BmField code) {
		this.code = code;
	}
	public BmField getDescrition() {
		return descrition;
	}
	public void setDescrition(BmField descrition) {
		this.descrition = descrition;
	}
	
	
	
}
