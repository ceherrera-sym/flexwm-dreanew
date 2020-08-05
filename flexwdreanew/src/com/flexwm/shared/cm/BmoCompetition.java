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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoCompetition extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField competitionId, name;

	public BmoCompetition() {
		super("com.flexwm.server.cm.PmCompetition", "competition", "competitionid", "CMPT", "Competencia");
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		competitionId = setField("competitionid", "", "Competencia", 5, Types.INTEGER, true, BmFieldType.ID, true);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCompetitionId(),
				getName()
				));	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCompetitionId(),
				getName()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCompetitionId(),
				getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCompetitionId()), 
				new BmSearchField(getName())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName() , BmOrder.ASC)));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getCompetitionId() {
		return competitionId;
	}

	public void setCompetitionId(BmField competitionId) {
		this.competitionId = competitionId;
	}
	
	

}
