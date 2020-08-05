package com.flexwm.shared.co;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoContractTerm extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField name,startDate,endDate,deadLine;
	
	public BmoContractTerm() {
		super("com.flexwm.server.co.PmContractTerm", "contractterms", "contracttermid", "CRTR", "Plazo de Contrato");
		
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		deadLine = setField("deadline", "", "Plazo", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);		
		
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getStartDate(),
				getEndDate(),
				getDeadLine()));
	}
	
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getStartDate(),
				getEndDate(),
				getDeadLine()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC),
				new BmOrder(getKind(), getStartDate(), BmOrder.ASC)));
	}
	
	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(BmField deadLine) {
		this.deadLine = deadLine;
	}
	
	

}
