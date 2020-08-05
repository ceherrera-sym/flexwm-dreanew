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


public class BmoOpportunityCompetition extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmoOpportunity boBmoOpportunity = new BmoOpportunity();
	private BmField opportunityId, competitionId, compId;
	private BmoCompetition bmoCompetition = new BmoCompetition();
	private BmoOpportunity bmoOpportunity = new BmoOpportunity();

	public BmoOpportunityCompetition() {
		super("com.flexwm.server.cm.PmOpportunityCompetition", "opportunitycompetition", "compid", "OPCM", "Competencia");
		opportunityId = setField("opportunityid", "", "Opportunidad", 10, Types.INTEGER, false, BmFieldType.ID, false);
		competitionId = setField("competitionid", "", "Competencia", 8, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCompetition().getName()
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)
				));
	}

	public BmField getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(BmField opportunityId) {
		this.opportunityId = opportunityId;
	}

	public BmField getCompetitionId() {
		return competitionId;
	}

	public void setCompetitionId(BmField competitionId) {
		this.competitionId = competitionId;
	}

	public BmField getCompId() {
		return compId;
	}

	public void setCompId(BmField compId) {
		this.compId = compId;
	}

	public BmoOpportunity getBoBmoOpportunity() {
		return boBmoOpportunity;
	}

	public void setBoBmoOpportunity(BmoOpportunity boBmoOpportunity) {
		this.boBmoOpportunity = boBmoOpportunity;
	}

	public BmoCompetition getBmoCompetition() {
		return bmoCompetition;
	}

	public void setBmoCompetition(BmoCompetition bmoCompetition) {
		this.bmoCompetition = bmoCompetition;
	}

	public BmoOpportunity getBmoOpportunity() {
		return bmoOpportunity;
	}

	public void setBmoOpportunity(BmoOpportunity bmoOpportunity) {
		this.bmoOpportunity = bmoOpportunity;
	}

	
}
