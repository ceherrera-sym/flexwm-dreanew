/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.wf;

import java.util.ArrayList;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowUser;


public class UiWFlowUser extends UiFormList {
	UiListBox wflowUserListBox;
	BmoWFlowUser bmoWFlowUser;
	
	BmoWFlow bmoWFlow;
	
	public UiWFlowUser(UiParams uiParams, Panel defaultPanel, BmoWFlow bmoWFlow) {
		super(uiParams, defaultPanel, new BmoWFlowUser());
		bmoWFlowUser = (BmoWFlowUser)getBmObject();
		this.bmoWFlow = bmoWFlow;
		
		forceFilter = new BmFilter();
		forceFilter.setValueLabelFilter(bmoWFlowUser.getKind(), 
				bmoWFlowUser.getWFlowId().getName(), 
				bmoWFlowUser.getWFlowId().getLabel(), 
				BmFilter.EQUALS, 
				bmoWFlow.getId(),
				bmoWFlowUser.getWFlowId().getName());
		
		// Filtro NOT IN para discrimiar registros ya existentes
		BmoUser bmoUser = new BmoUser();
		BmFilter usersNotInWFlow = new BmFilter();
		usersNotInWFlow.setNotInFilter(bmoWFlowUser.getKind(), 
				bmoUser.getIdFieldName(), 
				bmoWFlowUser.getUserId().getName(),
				bmoWFlowUser.getWFlowId().getName(),
				"" + bmoWFlow.getId()
				);
		
		BmFilter activeUser = new BmFilter();
		activeUser.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
		
		ArrayList<BmFilter> userFilters = new ArrayList<BmFilter>();
		userFilters.add(usersNotInWFlow);
		userFilters.add(activeUser);
		
		wflowUserListBox = new UiListBox(getUiParams(), bmoUser, userFilters);
	}
	
	@Override
	public void populateFields(){
		bmoWFlowUser = (BmoWFlowUser)getBmObject();
		formFlexTable.addField(1, 0, wflowUserListBox, bmoWFlowUser.getUserId());
	}
	
	@Override
	public BmObject populateBObject() throws BmException {
		bmoWFlowUser = new BmoWFlowUser();
		bmoWFlowUser.setId(id);
		bmoWFlowUser.getWFlowId().setValue(bmoWFlow.getId());
		bmoWFlowUser.getUserId().setValue(wflowUserListBox.getSelectedId());
		return bmoWFlowUser;
	}
	
	@Override
	public void displayList() {
		int col = 0;
		listFlexTable.addListTitleCell(0, col++, "Abrir");
		listFlexTable.addListTitleCell(0, col++, getBmObject().getProgramCode(), bmoWFlowUser.getBmoUser().getFirstname());
		listFlexTable.addListTitleCell(0, col++, getBmObject().getProgramCode(), bmoWFlowUser.getBmoUser().getFatherlastname());
		listFlexTable.addListTitleCell(0, col++, getBmObject().getProgramCode(), bmoWFlowUser.getBmoUser().getEmail());
		listFlexTable.addListTitleCell(0, col++, getBmObject().getProgramCode(), bmoWFlowUser.getBmoUser().getBmoArea().getName());
		
		int row = 1;
		while (iterator.hasNext()) {
		    BmoWFlowUser cellBmoWFlowUser = (BmoWFlowUser)iterator.next(); 
			
			Image clickImage = new Image(GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/edit.png"));
			clickImage.addClickHandler(rowClickHandler);
			listFlexTable.setWidget(row, 0, clickImage);
			listFlexTable.getCellFormatter().addStyleName(row, 0, "listCellLink");
			
			int cols = 1;
			listFlexTable.addListCell(row, cols++, getBmObject(), cellBmoWFlowUser.getBmoUser().getFirstname());
			listFlexTable.addListCell(row, cols++, getBmObject(), cellBmoWFlowUser.getBmoUser().getFatherlastname());
			listFlexTable.addListCell(row, cols++, getBmObject(), cellBmoWFlowUser.getBmoUser().getEmail());
			listFlexTable.addListCell(row, cols++, getBmObject(), cellBmoWFlowUser.getBmoUser().getBmoArea().getName());
			
			listFlexTable.formatRow(row);
		    row++;
		}
	}
}