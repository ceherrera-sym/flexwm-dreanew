/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.wf;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.flexwm.shared.wf.BmoWFlowCategory;

public class BmoWFlowType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, comments, hours, billable, wFlowCategoryId, status,companyId;

	private BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();;
	
	public static String ACTION_COPY = "COPY";
	public static String TYPE_WFLOW = "EXTRA";
	public static char STATUS_ACTIVE = 'A';
	public static char STATUS_INACTIVE = 'N';

	
	public BmoWFlowType() {
		super("com.flexwm.server.wf.PmWFlowType", "wflowtypes", "wflowtypeid", "WFTY", "Tipos WFlow");
		
		name = setField("name", "", "Nombre Tipo", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		comments = setField("comments", "", "Comentarios", 10000, Types.VARCHAR, true, BmFieldType.STRING, false);
		hours = setField("hours", "", "Horas", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		billable = setField("billable", "", "Facturable?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		wFlowCategoryId = setField("wflowcategoryid", "", "Categoría", 8, Types.INTEGER, false, BmFieldType.ID, false);
		status = setField("status", "" + STATUS_ACTIVE, "Estatus", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		status.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_ACTIVE, "Activo", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_INACTIVE, "Inactivo", "./icons/status_closed.png")				
				)));		
		companyId = setField("companyid", "", "Empresa", 8, Types.INTEGER, true, BmFieldType.ID, false);

	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getBmoWFlowCategory().getName(), 
				getStatus()
				));
	}	

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
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

	public BmoWFlowCategory getBmoWFlowCategory() {
		return bmoWFlowCategory;
	}

	public void setBmoWFlowCategory(BmoWFlowCategory bmoWFlowCategory) {
		this.bmoWFlowCategory = bmoWFlowCategory;
	}

	public BmField getWFlowCategoryId() {
		return wFlowCategoryId;
	}

	public void setWFlowCategoryId(BmField wFlowCategoryId) {
		this.wFlowCategoryId = wFlowCategoryId;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getHours() {
		return hours;
	}

	public void setHours(BmField hours) {
		this.hours = hours;
	}

	public BmField getBillable() {
		return billable;
	}

	public void setBillable(BmField billable) {
		this.billable = billable;
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
	
	
}
