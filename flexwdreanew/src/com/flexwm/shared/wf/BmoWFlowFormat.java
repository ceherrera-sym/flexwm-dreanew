package com.flexwm.shared.wf;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;


import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoWFlowFormat extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BmField code, name, description, link, hasconsecutive, multipleprint, sequence, publishValidateClass, wflowTypeId,
					companyId;
	
	public static String ACTION_PUBLISHVALIDATE = "PUBLISHVALIDATE";
	
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();
	
	public BmoWFlowFormat() {
		super("com.flexwm.server.wf.PmWFlowFormat", "wflowformats", "wflowformatid", "WFFT", "Formatos Wflow");
		
		
		code = setField("code", "", "Clave Formato", 10, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		link = setField("link", "", "Liga", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		hasconsecutive = setField("hasconsecutive", "", "Consecutivo", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		multipleprint = setField("multipleprint", "", "Varias impresiones", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		publishValidateClass = setField("publishvalidateclass", "", "Clase Validación", 50, Types.VARCHAR, true, BmFieldType.STRING, false);		
		sequence = setField("sequence", "", "Orden", 5, Types.INTEGER, false, BmFieldType.NUMBER, false);
		wflowTypeId = setField("wflowtypeid", "", "Tipo de Flujo", 10, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 10, Types.INTEGER, true, BmFieldType.ID, false);
				
	}
	
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getName(), 
				getDescription(),				
				getLink()
			));
	}	

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode().getName(), getCode().getLabel()),
				new BmSearchField(getName().getName(), getName().getLabel()), 
				new BmSearchField(getLink().getName(), getLink().getLabel()), 
				new BmSearchField(getDescription().getName(), getDescription().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}


	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}


	public BmField getCompanyId() {
		return companyId;
	}


	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}


	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
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

	public BmField getLink() {
		return link;
	}

	public void setLink(BmField link) {
		this.link = link;
	}

	public BmField getHasconsecutive() {
		return hasconsecutive;
	}

	public void setHasconsecutive(BmField hasconsecutive) {
		this.hasconsecutive = hasconsecutive;
	}

	public BmField getMultipleprint() {
		return multipleprint;
	}

	public void setMultipleprint(BmField multipleprint) {
		this.multipleprint = multipleprint;
	}

	public BmField getSequence() {
		return sequence;
	}

	public void setSequence(BmField sequence) {
		this.sequence = sequence;
	}

	public BmField getPublishValidateClass() {
		return publishValidateClass;
	}

	public void setPublishValidateClass(BmField publishValidateClass) {
		this.publishValidateClass = publishValidateClass;
	}

	public BmField getWflowTypeId() {
		return wflowTypeId;
	}

	public void setWflowTypeId(BmField wflowTypeId) {
		this.wflowTypeId = wflowTypeId;
	}

	
}
