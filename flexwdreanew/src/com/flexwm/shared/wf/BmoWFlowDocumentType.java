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
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.flexwm.shared.wf.BmoWFlowType;


public class BmoWFlowDocumentType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, required, wFlowTypeId, fileTypeId;
	private BmoWFlowType bmoWFlowType = new BmoWFlowType();

	public BmoWFlowDocumentType() {
		super("com.flexwm.server.wf.PmWFlowDocumentType", "wflowdocumenttypes", "wflowdocumenttypeid", "WFDT", "Tipos Documento");
		
		code = setField("code", "", "Clave Tipo Doc.", 10, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		required = setField("required", "", "Requerido?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

		fileTypeId = setField("filetypeid", "", "Tipo de Archivo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {		
		return new ArrayList<BmField>(Arrays.asList(
				getCode(),
				getName(),
				getRequired()
				));
	}	

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getName(), BmOrder.ASC)
				));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmoWFlowType getBmoWFlowType() {
		return bmoWFlowType;
	}

	public void setBmoWFlowType(BmoWFlowType bmoWFlowType) {
		this.bmoWFlowType = bmoWFlowType;
	}

	public BmField getWFlowTypeId() {
		return wFlowTypeId;
	}

	public void setWFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmField getRequired() {
		return required;
	}

	public void setRequired(BmField required) {
		this.required = required;
	}

	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getFileTypeId() {
		return fileTypeId;
	}

	public void setFileTypeId(BmField fileTypeId) {
		this.fileTypeId = fileTypeId;
	}
}
