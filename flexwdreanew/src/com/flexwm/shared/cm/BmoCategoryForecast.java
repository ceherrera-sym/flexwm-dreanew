package com.flexwm.shared.cm;

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

public class BmoCategoryForecast  extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, statusOpportunity;
	public static char STATUS_OP_REVISION = 'R';
	public static char STATUS_OP_WON = 'W';
	public static char STATUS_OP_LOST = 'L';
	public static char STATUS_OP_EXPIRED = 'E';
	public static char STATUS_OP_HOLD = 'H';

	public BmoCategoryForecast() {
		super("com.flexwm.server.cm.PmCategoryForecast", "categoryforecasts", "categoryforecastid", "CAFO", "Forecast");
		name = setField("name", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		statusOpportunity = setField("statusopportunity", "" + STATUS_OP_REVISION , "Estatus", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		statusOpportunity.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_OP_REVISION, "En Revisión", "status_revision.png"),
				new BmFieldOption(STATUS_OP_WON, "Ganada", "status_authorized.png"),
				new BmFieldOption(STATUS_OP_LOST, "Perdida", "status_cancelled.png"),
				new BmFieldOption(STATUS_OP_EXPIRED, "Expirada", "status_expired.png"),
				new BmFieldOption(STATUS_OP_HOLD, "Detenido", "status_on_hold.png")
				)));
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription()
				));
	}

	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
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

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}
	
	public BmField getStatusOpportunity() {
		return statusOpportunity;
	}

	public void setStatusOpportunity(BmField statusOpportunity) {
		this.statusOpportunity = statusOpportunity;
	}
}
