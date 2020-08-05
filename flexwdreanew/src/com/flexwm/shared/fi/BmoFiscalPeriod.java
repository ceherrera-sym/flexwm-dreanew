/**
 * 
 */
package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;

/**
 * @author smuniz
 *
 */

public class BmoFiscalPeriod extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, startDate, endDate, status, companyId;
	private BmoCompany bmoCompany = new BmoCompany();

	public static final char STATUS_OPEN = 'O';
	public static final char STATUS_CLOSED = 'C';

	public static final String ACCESS_CHANGENOCHECK = "BKACHS";

	public BmoFiscalPeriod() {
		super("com.flexwm.server.fi.PmFiscalPeriod", "fiscalperiods", "fiscalperiodid", "FIPE","Peridos Operativos");

		//Campo de Datos		
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.DATE, false, BmFieldType.DATE, false);
		endDate = setField("enddate", "", "Fin", 20, Types.DATE, false, BmFieldType.DATE, false);
		status = setField("status", "", "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_OPEN, "Abierto", "./icons/wflu_lockstatus_open.png"),
				new BmFieldOption(STATUS_CLOSED, "Cerrado", "./icons/wflu_lockstatus_locked.png")
				)));
		companyId = setField("companyid", "", "Empresa", 4, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getStartDate(),
				getEndDate(),
				getStatus(),
				getBmoCompany().getName()
				));
	}

	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getStartDate(),
				getEndDate()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
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

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}

}
