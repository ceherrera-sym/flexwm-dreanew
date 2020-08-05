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

public class BmoFactorType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String ACCESS_SAVE = "SAVEFACTOR";
	private BmField code;
	
	public BmoFactorType() {
		super("com.flexwm.server.fi.PmFactorType", "factortypes", "factortypeid", "FATY", "Tipo de Factor");
		code = setField("code", "", "Codigo SAT", 6, Types.VARCHAR, false, BmFieldType.CODE, true);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode()
				));
	}
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode())
				));
	}
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}
	
	
}
