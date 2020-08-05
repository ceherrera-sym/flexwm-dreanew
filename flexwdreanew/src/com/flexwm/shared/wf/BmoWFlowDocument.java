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
import com.symgae.shared.sf.BmoFileType;
import com.flexwm.shared.wf.BmoWFlow;


public class BmoWFlowDocument extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField code, name, file, fileLink, isUp, required, wFlowId, fileTypeId;
	private BmoWFlow bmoWFlow = new BmoWFlow();
	private BmoFileType bmoFileType = new BmoFileType();
	
	public static String CODE_PREFIX = "WD-";

	public static String ACTION_SENDEMAIL = "WFDSEN";
	
	public BmoWFlowDocument() {
		super("com.flexwm.server.wf.PmWFlowDocument", "wflowdocuments", "wflowdocumentid", "WFDO", "Documentos WFlow");
		code = setField("code", "", "Clave Dcto.", 10, Types.VARCHAR, false, BmFieldType.CODE, false);
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		file = setField("file", "", "Doc.", 500, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
		isUp = setField("isup", "", "Carga", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		required = setField("required", "", "Req?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		fileLink = setField("filelink", "", "Liga Ext.", 500, Types.VARCHAR, true, BmFieldType.WWW, false);
		
		wFlowId = setField("wflowid", "", "Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
		fileTypeId = setField("filetypeid", "", "Tipo de Archivo", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWFlow().getCode(),
				getBmoWFlow().getName(),
				getCode(),
				getName(),
				getBmoFileType().getName(),
				getRequired(),
				getIsUp(),
				getFileLink(),
				getFile()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoWFlow().getName()),
				new BmSearchField(getBmoWFlow().getCode()),
				new BmSearchField(getName())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoWFlow().getCode(), BmOrder.ASC),
				new BmOrder(getKind(), getName(), BmOrder.ASC)
				));
	}
	
	public String getCodeFormat() {
		if (getId() > 0) return CODE_PREFIX + getId();
		else return "";
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

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

	public BmField getIsUp() {
		return isUp;
	}

	public void setIsUp(BmField isUp) {
		this.isUp = isUp;
	}

	public BmField getWFlowId() {
		return wFlowId;
	}

	public void setWFlowId(BmField wFlowId) {
		this.wFlowId = wFlowId;
	}

	public BmoWFlow getBmoWFlow() {
		return bmoWFlow;
	}

	public void setBmoWFlow(BmoWFlow bmoWFlow) {
		this.bmoWFlow = bmoWFlow;
	}

	public BmField getRequired() {
		return required;
	}

	public void setRequired(BmField required) {
		this.required = required;
	}

	public BmField getFileTypeId() {
		return fileTypeId;
	}

	public void setFileTypeId(BmField fileTypeId) {
		this.fileTypeId = fileTypeId;
	}

	public BmoFileType getBmoFileType() {
		return bmoFileType;
	}

	public void setBmoFileType(BmoFileType bmoFileType) {
		this.bmoFileType = bmoFileType;
	}

	public BmField getFileLink() {
		return fileLink;
	}

	public void setFileLink(BmField fileLink) {
		this.fileLink = fileLink;
	}
}
