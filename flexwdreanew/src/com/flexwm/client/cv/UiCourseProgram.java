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

import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoArea;
import com.flexwm.shared.cv.BmoCourse;
import com.flexwm.shared.cv.BmoCourseProgram;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;


/**
 * @author smuniz
 *
 */

public class UiCourseProgram extends UiList {
	BmoCourseProgram bmoCourseProgram;

	public UiCourseProgram(UiParams uiParams) {
		super(uiParams, new BmoCourseProgram());
		bmoCourseProgram = (BmoCourseProgram)getBmObject();
	}

	@Override
	public void postShow() {
		//
	}

	@Override
	public void create() {
		UiProgramForm uiProgramForm = new UiProgramForm(getUiParams(), 0);
		uiProgramForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiProgramForm uiProgramForm = new UiProgramForm(getUiParams(), bmObject.getId());
		uiProgramForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiProgramForm uiProgramForm = new UiProgramForm(getUiParams(), bmObject.getId());
		uiProgramForm.show();
	}

	public class UiProgramForm extends UiFormDialog {
		UiSuggestBox areaIdUiSuggestBox = new UiSuggestBox(new BmoArea());
		TextBox nameTextArea = new TextBox();
		TextArea descriptionTextArea = new TextArea();

		BmoCourseProgram bmoCourseProgram;
		String itemSection = "Items";
		CourseUpdater courseUpdater = new CourseUpdater();

		public UiProgramForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCourseProgram(), id); 
		}

		@Override
		public void populateFields() {
			bmoCourseProgram = (BmoCourseProgram)getBmObject();
			formFlexTable.addField(1, 0, nameTextArea, bmoCourseProgram.getName());
			formFlexTable.addField(2, 0, areaIdUiSuggestBox, bmoCourseProgram.getAreaId());
			formFlexTable.addField(3, 0, descriptionTextArea, bmoCourseProgram.getDescription());

			if (!newRecord) {
				// Items
				formFlexTable.addSectionLabel(4, 0, itemSection, 2);
				BmoCourse bmoCourse = new BmoCourse();
				FlowPanel courseFP = new FlowPanel();
				BmFilter filterCourse = new BmFilter();
				filterCourse.setValueFilter(bmoCourse.getKind(), bmoCourse.getProgramId(), bmoCourseProgram.getId());
				getUiParams().setForceFilter(bmoCourse.getProgramCode(), filterCourse);
				UiCourse uiCourse = new UiCourse(getUiParams(), courseFP, bmoCourseProgram, bmoCourseProgram.getId(), courseUpdater);
				setUiType(bmoCourse.getProgramCode(), UiParams.MINIMALIST);
				uiCourse.show();
				formFlexTable.addPanel(5, 0, courseFP, 2);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCourseProgram.setId(id);
			bmoCourseProgram.getName().setValue(nameTextArea.getText());
			bmoCourseProgram.getAreaId().setValue(areaIdUiSuggestBox.getSelectedId()); 
			bmoCourseProgram.getDescription().setValue(descriptionTextArea.getText());

			return bmoCourseProgram;
		}

		@Override
		public void close() {
			list();
		}

		public class CourseUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}
