/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cv;

import com.flexwm.client.cv.UiCourseProgram.UiProgramForm.CourseUpdater;
import com.flexwm.shared.cv.BmoCourse;
import com.flexwm.shared.cv.BmoCourseProgram;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmException;


/**
 * @author smuniz
 *
 */

public class UiCourse extends UiList {

	BmoCourseProgram bmoCourseProgram;
	BmoCourse bmoCourse;

	int programId;
	protected CourseUpdater courseUpdater;

	public UiCourse(UiParams uiParams, Panel defaultPanel, BmoCourseProgram bmoCourseProgram, int programId, CourseUpdater courseUpdater) {
		super(uiParams, defaultPanel, new BmoCourse());
		bmoCourse = (BmoCourse)getBmObject();
		this.bmoCourseProgram = bmoCourseProgram;
		this.programId = programId;
		this.courseUpdater = courseUpdater;
	}

	@Override
	public void create() {
		UiCourseForm uiCourseForm = new UiCourseForm(getUiParams(), 0);
		uiCourseForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCourseForm uiCourseForm = new UiCourseForm(getUiParams(), bmObject.getId());
		uiCourseForm.show();
	}

	private class UiCourseForm extends UiFormDialog {
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		TextBox instructorTextBox = new TextBox();
		TextArea objectivesTextArea = new TextArea();
		TextBox lenghtTextBox = new TextBox();

		public UiCourseForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCourse(), id);
		}

		@Override
		public void populateFields() {
			bmoCourse = (BmoCourse)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoCourse.getName());
			formFlexTable.addField(2, 0, instructorTextBox, bmoCourse.getInstructor());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoCourse.getDescription());
			formFlexTable.addField(4, 0, objectivesTextArea, bmoCourse.getObjectives());
			formFlexTable.addField(5, 0, lenghtTextBox, bmoCourse.getLenght());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCourse.setId(id);
			if(programId > 0) bmoCourse.getProgramId().setValue(programId);
			bmoCourse.getName().setValue(nameTextBox.getText());
			bmoCourse.getDescription().setValue(descriptionTextArea.getText());
			bmoCourse.getObjectives().setValue(objectivesTextArea.getText());
			bmoCourse.getInstructor().setValue(instructorTextBox.getText());
			bmoCourse.getLenght().setValue(lenghtTextBox.getText());
			return bmoCourse;
		}

		@Override
		public void close() {
			list();

			if (courseUpdater != null)
				courseUpdater.update();
		}
	}
}