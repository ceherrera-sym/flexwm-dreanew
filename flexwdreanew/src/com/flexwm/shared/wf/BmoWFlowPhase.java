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

public class BmoWFlowPhase extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, sequence, wFlowCategoryId;

	public BmoWFlowPhase() {
		super("com.flexwm.server.wf.PmWFlowPhase", "wflowphases", "wflowphaseid", "WFPH", "Fases WFlow");
		
		// Campos de datos
		name = setField("name", "", "Nombre Fase", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		sequence = setField("sequence", "", "Sec. Fase", 8, Types.INTEGER, true, BmFieldType.STRING, false);
		wFlowCategoryId = setField("wflowcategoryid", "", "Categoría Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				this.getSequence(),
				this.getName(),
				this.getDescription()
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
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getSequence(), BmOrder.ASC)));
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

	public BmField getSequence() {
		return sequence;
	}

	public void setSequence(BmField sequence) {
		this.sequence = sequence;
	}

	public BmField getWFlowCategoryId() {
		return wFlowCategoryId;
	}

	public void setWFlowCategoryId(BmField wFlowCategoryId) {
		this.wFlowCategoryId = wFlowCategoryId;
	}
}
