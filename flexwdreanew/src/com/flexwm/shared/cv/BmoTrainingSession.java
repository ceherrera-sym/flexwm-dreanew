/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.cv;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

/**
 * @author smuniz
 *
 */

public class BmoTrainingSession extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField courseId, locationName, date, time, description, timeEnd;
	BmoCourse bmoCourse = new BmoCourse();
		
	public BmoTrainingSession(){
		super("com.flexwm.server.cv.PmTrainingSession", "trainingsessions", "trainingsessionid", "TRSE", "Sesiones");
		
		//Campo de Datos
		courseId = setField("courseid", "", "Curso", 8, Types.INTEGER, false, BmFieldType.ID, false);
		locationName = setField("locationname", "", "Lugar", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		date = setField("date", "", "Fecha", 20, Types.DATE, false, BmFieldType.DATE, false);
		time = setField("time", "", "Hora Inicio", 10, Types.VARCHAR, true, BmFieldType.STRING, false);
		timeEnd = setField("timeend", "", "Hora Conclusión", 10, Types.VARCHAR, true, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 500, Types.VARCHAR, true, BmFieldType.STRING, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				bmoCourse.getName(),
				bmoCourse.getBmoCourseProgram().getName(),
				getLocationName(),
				getDate(),
				getTime()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getLocationName()), 
				new BmSearchField(bmoCourse.getName())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	/**
	 * @return the courseId
	 */
	public BmField getCourseId() {
		return courseId;
	}

	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(BmField courseId) {
		this.courseId = courseId;
	}

	/**
	 * @return the locationName
	 */
	public BmField getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName the locationName to set
	 */
	public void setLocationName(BmField locationName) {
		this.locationName = locationName;
	}

	/**
	 * @return the date
	 */
	public BmField getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(BmField date) {
		this.date = date;
	}

	/**
	 * @return the time
	 */
	public BmField getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(BmField time) {
		this.time = time;
	}

	/**
	 * @return the description
	 */
	public BmField getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(BmField description) {
		this.description = description;
	}

	/**
	 * @return the timeEnd
	 */
	public BmField getTimeEnd() {
		return timeEnd;
	}

	/**
	 * @param timeEnd the timeEnd to set
	 */
	public void setTimeEnd(BmField timeEnd) {
		this.timeEnd = timeEnd;
	}

	/**
	 * @return the bmoCourse
	 */
	public BmoCourse getBmoCourse() {
		return bmoCourse;
	}

	/**
	 * @param bmoCourse the bmoCourse to set
	 */
	public void setBmoCourse(BmoCourse bmoCourse) {
		this.bmoCourse = bmoCourse;
	}

	
	
}
