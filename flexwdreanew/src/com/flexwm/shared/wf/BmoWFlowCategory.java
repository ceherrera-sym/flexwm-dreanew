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

public class BmoWFlowCategory extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, emailReminders, 
	expires, expireDays, programId, effectWFlowCategoryId, gCalendarSync, gCalendarId, status,companyId,daysRemindExpired;

	public static char STATUS_ACTIVE = 'A';
	public static char STATUS_INACTIVE = 'N';
	
	public BmoWFlowCategory() {
		super("com.flexwm.server.wf.PmWFlowCategory", "wflowcategories", "wflowcategoryid", "WFCA", "Categorías WFlow");		
		name = setField("name", "", "Nombre Cat.", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		emailReminders = setField("emailreminders", "", "Notificaciones", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		expires = setField("expires", "", "Expira?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		expireDays = setField("expiredays", "", "Dias Expiración", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		daysRemindExpired = setField("daysremindexpired", "0", "Dias Not. Exp.", 5, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		gCalendarSync = setField("gcalendarsync", "", "Calendario Google", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

		gCalendarId = setField("gcalendarid", "", "ID Cal. Google", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		programId = setField("programid", "", "Programa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		effectWFlowCategoryId = setField("effectwflowcategoryid", "", "Categoría Efecto", 8, Types.INTEGER, true, BmFieldType.ID, false);
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
				getEmailReminders(),
				getgCalendarSync(),
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

	
	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
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

	public BmField getProgramId() {
		return programId;
	}

	public void setProgramId(BmField programId) {
		this.programId = programId;
	}

	public BmField getEffectWFlowCategoryId() {
		return effectWFlowCategoryId;
	}

	public void setEffectWFlowCategoryId(BmField effectWFlowCategoryId) {
		this.effectWFlowCategoryId = effectWFlowCategoryId;
	}

	public BmField getEmailReminders() {
		return emailReminders;
	}

	public void setEmailReminders(BmField emailReminders) {
		this.emailReminders = emailReminders;
	}

	public BmField getgCalendarId() {
		return gCalendarId;
	}

	public void setgCalendarId(BmField gCalendarId) {
		this.gCalendarId = gCalendarId;
	}

	public BmField getExpires() {
		return expires;
	}

	public void setExpires(BmField expires) {
		this.expires = expires;
	}

	public BmField getExpireDays() {
		return expireDays;
	}

	public void setExpireDays(BmField expireDays) {
		this.expireDays = expireDays;
	}

	public BmField getgCalendarSync() {
		return gCalendarSync;
	}

	public void setgCalendarSync(BmField gCalendarSync) {
		this.gCalendarSync = gCalendarSync;
	}

	public BmField getStatus() {
		return status;
	}

	public void setStatus(BmField status) {
		this.status = status;
	}

	public BmField getDaysRemindExpired() {
		return daysRemindExpired;
	}

	public void setDaysRemindExpired(BmField daysRemindExpired) {
		this.daysRemindExpired = daysRemindExpired;
	}
	
	
}
