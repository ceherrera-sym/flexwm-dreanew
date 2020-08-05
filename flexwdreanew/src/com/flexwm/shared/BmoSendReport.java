/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;

public class BmoSendReport extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, profileId, link ;
	BmoProfile bmoProfile;
	
	public BmoSendReport() {
		super("com.flexwm.server.PmSendReport", "sendreports", "sendreportid", "SDRP", "Envio Reportes");
		
		// Campo de datos		
		name = setField("name", "", "Nombre", 30, Types.VARCHAR, false, BmFieldType.STRING, false);
		profileId = setField("profileid", "", "Perfil", 11, Types.INTEGER, false, BmFieldType.ID, false);		
		link = setField("link", "", "Liga Jsp", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		
		bmoProfile = new BmoProfile();
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(						 
						getName(),
						getBmoProfile().getName(),
						getLink()
						));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(				 
				new BmSearchField(getName())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmField getLink() {
		return link;
	}

	public void setLink(BmField link) {
		this.link = link;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}
}
