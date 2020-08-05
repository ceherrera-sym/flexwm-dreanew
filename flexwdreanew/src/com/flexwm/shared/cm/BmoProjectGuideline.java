/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoProjectGuideline extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField guidelines;
	private BmField projectId;

	public BmoProjectGuideline() {
		super("com.flexwm.server.cm.PmProjectGuideline", "projectguidelines", "projectguidelineid", "PJGI", "Orden del Día");

		// Campo de datos
		guidelines = setField("guidelines", "", "Orden del dia", 1999999, Types.BLOB, true, BmFieldType.HTML, false);
		projectId = setField("projectid", "", "Proyecto", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getProjectId()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getProjectId())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getProjectId(), BmOrder.ASC)));
	}

	public BmField getGuidelines() {
		return guidelines;
	}

	public void setGuidelines(BmField guidelines) {
		this.guidelines = guidelines;
	}

	public BmField getProjectId() {
		return projectId;
	}

	public void setProjectId(BmField projectId) {
		this.projectId = projectId;
	}
}