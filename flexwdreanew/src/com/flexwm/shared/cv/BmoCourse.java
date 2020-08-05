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

public class BmoCourse extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField name, description, instructor, objectives, lenght, programId;
	BmoCourseProgram bmoCourseProgram = new BmoCourseProgram();
		
	public BmoCourse(){
		super("com.flexwm.server.cv.PmCourse", "courses", "courseid", "COUR", "Cursos");
		
		//Campo de Datos
		programId = setField("programid", "", "Programa", 8, Types.INTEGER, false, BmFieldType.ID, false);
		name = setField("name", "", "Nombre", 100, Types.VARCHAR, false, BmFieldType.STRING, false);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		instructor = setField("instructor", "", "Instructor", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		objectives = setField("objectives", "", "Objetivos", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		lenght = setField("lenght", "", "Duración", 20, Types.DOUBLE, true, BmFieldType.NUMBER, false);
	}
	

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),		
				getInstructor(),
				getDescription(),
				getLenght()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription()),
				new BmSearchField(getInstructor())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}


	/**
	 * @return the name
	 */
	public BmField getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(BmField name) {
		this.name = name;
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
	 * @return the instructor
	 */
	public BmField getInstructor() {
		return instructor;
	}


	/**
	 * @param instructor the instructor to set
	 */
	public void setInstructor(BmField instructor) {
		this.instructor = instructor;
	}


	/**
	 * @return the objectives
	 */
	public BmField getObjectives() {
		return objectives;
	}


	/**
	 * @param objectives the objectives to set
	 */
	public void setObjectives(BmField objectives) {
		this.objectives = objectives;
	}


	/**
	 * @return the lenght
	 */
	public BmField getLenght() {
		return lenght;
	}


	/**
	 * @param lenght the lenght to set
	 */
	public void setLenght(BmField lenght) {
		this.lenght = lenght;
	}


	/**
	 * @return the programId
	 */
	public BmField getProgramId() {
		return programId;
	}


	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(BmField programId) {
		this.programId = programId;
	}


	/**
	 * @return the bmoProgram
	 */
	public BmoCourseProgram getBmoCourseProgram() {
		return bmoCourseProgram;
	}


	/**
	 * @param bmoProgram the bmoProgram to set
	 */
	public void setBmoCourseProgram(BmoCourseProgram bmoCourseProgram) {
		this.bmoCourseProgram = bmoCourseProgram;
	}

	
	
}
