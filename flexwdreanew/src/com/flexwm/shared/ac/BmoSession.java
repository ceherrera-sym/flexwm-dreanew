/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.ac;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoUser;


public class BmoSession extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField description, startDate, endDate, reservations, available, 
	isSeries, seriesStart, seriesEnd, seriesParentId, seriesApplyAll, 
	seriesMonday, seriesTuesday, seriesWednesday, seriesThursday, seriesFriday, seriesSaturday, seriesSunday,
	googleEventId, userGoogleEventId, userId, sessionTypeId, autoAssign,companyId;

	public static String ACTION_AUTOASSIGN = "AUTOSSIGN";
	public static String ACTION_CHECKSERIES = "CHECKSERIES";
	public static String ACTION_CHECKDATES = "CHECKDATES";
	public static String ACTION_NEWSESSIONSALE = "NEWSESSIONSALE";
	public static String ACTION_NEWSESSIONCUST = "NEWSESSIONCUST";
	public static String ACTION_SESSNOTATTENDED = "SESSNOTATTENDED";	
	public static String ACTION_GETORDER = "GETORDER";
	public static String ACTION_CHECKSESSIONTIME = "SESSTIME";
	public static String ACTION_CHECKSINGUP = "CHECKSINGUP";
	public static String ACTION_GETSINGUP = "GETSINGUP";
	public static String ACTION_NEWSESSIONWITHSALE = "NEWSESSIONWITHSALE";

	BmoSessionType bmoSessionType = new BmoSessionType();
	BmoUser bmoUser = new BmoUser();
	BmoCompany bmoCompany = new BmoCompany();

	public BmoSession() {
		super("com.flexwm.server.ac.PmSession","sessions", "sessionid", "SESS", "Sesiones");

		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		startDate = setField("startdate", "", "Inicio", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		endDate = setField("enddate", "", "Fin", 20, Types.TIMESTAMP, false, BmFieldType.DATETIME, false);
		reservations = setField("reservations", "", "Reservaciones", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		available = setField("available", "1", "Disponible", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		isSeries = setField("isseries", "0", "Es Serie?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesStart = setField("seriesstart", "", "Inicio Serie", 20, Types.DATE, true, BmFieldType.DATE, false);
		seriesEnd = setField("seriesend", "", "Fin Serie", 20, Types.DATE, true, BmFieldType.DATE, false);
		seriesApplyAll = setField("seriesapplyall", "", "Cambiar Serie?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesParentId = setField("seriesparentid", "", "ID Serie", 8, Types.INTEGER, true, BmFieldType.ID, false);
		seriesMonday = setField("seriesmonday", "1", "Lunes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesTuesday = setField("seriestuesday", "1", "Martes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesWednesday = setField("serieswednesday", "1", "Miércoles", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesThursday = setField("seriesthursday", "1", "Jueves", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesFriday = setField("seriesFriday", "1", "Viernes", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesSaturday = setField("seriessaturday", "", "Sábado", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		seriesSunday = setField("seriessunday", "", "Domingo", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		googleEventId = setField("googleeventid", "", "ID Evento Google", 100, Types.VARCHAR, true, BmFieldType.STRING, false);
		userGoogleEventId = setField("usergoogleeventid", "", "ID Evento Google Usuario", 100, Types.VARCHAR, true, BmFieldType.STRING, false);

		//AutoAsignar
		autoAssign = setField("autossign", "", "Auto Asignar", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);


		userId = setField("userid", "", "Instructor", 8, Types.INTEGER, false, BmFieldType.ID, false);
		sessionTypeId = setField("sessiontypeid", "", "Tipo Sesión", 8, Types.INTEGER, false, BmFieldType.ID, false);
		companyId = setField("companyid", "", "Empresa", 20, Types.INTEGER, false, BmFieldType.ID, false);
		
		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoSessionType().getName(),
				getBmoUser().getCode(),
				getBmoUser().getBmoLocation().getName(),
				getStartDate(),
				getBmoSessionType().getDuration(),
				getBmoSessionType().getCapacity(),
				getReservations(),
				getAvailable()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoUser().getCode(),
				getBmoUser().getBmoLocation().getName(),
				getStartDate()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoSessionType().getName()),
				new BmSearchField(getBmoSessionType().getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getStartDate(), BmOrder.ASC)));
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getSessionTypeId() {
		return sessionTypeId;
	}

	public void setSessionTypeId(BmField sessionTypeId) {
		this.sessionTypeId = sessionTypeId;
	}

	public BmoSessionType getBmoSessionType() {
		return bmoSessionType;
	}

	public void setBmoSessionType(BmoSessionType bmoSessionType) {
		this.bmoSessionType = bmoSessionType;
	}

	public BmField getStartDate() {
		return startDate;
	}

	public void setStartDate(BmField startDate) {
		this.startDate = startDate;
	}

	public BmField getEndDate() {
		return endDate;
	}

	public void setEndDate(BmField endDate) {
		this.endDate = endDate;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmField getReservations() {
		return reservations;
	}

	public void setReservations(BmField reservations) {
		this.reservations = reservations;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmField getAvailable() {
		return available;
	}

	public void setAvailable(BmField available) {
		this.available = available;
	}

	public BmField getIsSeries() {
		return isSeries;
	}

	public void setIsSeries(BmField isSeries) {
		this.isSeries = isSeries;
	}

	public BmField getSeriesStart() {
		return seriesStart;
	}

	public void setSeriesStart(BmField seriesStart) {
		this.seriesStart = seriesStart;
	}

	public BmField getSeriesEnd() {
		return seriesEnd;
	}

	public void setSeriesEnd(BmField seriesEnd) {
		this.seriesEnd = seriesEnd;
	}

	public BmField getSeriesApplyAll() {
		return seriesApplyAll;
	}

	public void setSeriesApplyAll(BmField seriesApplyAll) {
		this.seriesApplyAll = seriesApplyAll;
	}

	public BmField getSeriesParentId() {
		return seriesParentId;
	}

	public void setSeriesParentId(BmField seriesParentId) {
		this.seriesParentId = seriesParentId;
	}

	public BmField getSeriesMonday() {
		return seriesMonday;
	}

	public void setSeriesMonday(BmField seriesMonday) {
		this.seriesMonday = seriesMonday;
	}

	public BmField getSeriesTuesday() {
		return seriesTuesday;
	}

	public void setSeriesTuesday(BmField seriesTuesday) {
		this.seriesTuesday = seriesTuesday;
	}

	public BmField getSeriesWednesday() {
		return seriesWednesday;
	}

	public void setSeriesWednesday(BmField seriesWednesday) {
		this.seriesWednesday = seriesWednesday;
	}

	public BmField getSeriesThursday() {
		return seriesThursday;
	}

	public void setSeriesThursday(BmField seriesThursday) {
		this.seriesThursday = seriesThursday;
	}

	public BmField getSeriesFriday() {
		return seriesFriday;
	}

	public void setSeriesFriday(BmField seriesFriday) {
		this.seriesFriday = seriesFriday;
	}

	public BmField getSeriesSaturday() {
		return seriesSaturday;
	}

	public void setSeriesSaturday(BmField seriesSaturday) {
		this.seriesSaturday = seriesSaturday;
	}

	public BmField getSeriesSunday() {
		return seriesSunday;
	}

	public void setSeriesSunday(BmField seriesSunday) {
		this.seriesSunday = seriesSunday;
	}

	public BmField getGoogleEventId() {
		return googleEventId;
	}

	public void setGoogleEventId(BmField googleEventId) {
		this.googleEventId = googleEventId;
	}

	public BmField getUserGoogleEventId() {
		return userGoogleEventId;
	}

	public void setUserGoogleEventId(BmField userGoogleEventId) {
		this.userGoogleEventId = userGoogleEventId;
	}

	public BmField getAutoAssign() {
		return autoAssign;
	}

	public void setAutoAssign(BmField autoAssign) {
		this.autoAssign = autoAssign;
	}

	public BmField getCompanyId() {
		return companyId;
	}

	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmoCompany getBmoCompany() {
		return bmoCompany;
	}

	public void setBmoCompany(BmoCompany bmoCompany) {
		this.bmoCompany = bmoCompany;
	}
	
}
