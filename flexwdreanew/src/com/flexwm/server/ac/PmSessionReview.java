/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.ac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

import com.flexwm.shared.ac.BmoProgramSessionLevel;
import com.flexwm.shared.ac.BmoProgramSessionSubLevel;
import com.flexwm.shared.ac.BmoProgramSessionSubLevelType;
import com.flexwm.shared.ac.BmoSessionReview;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;


public class PmSessionReview extends PmObject{
	BmoSessionReview bmoSessionReview;


	public PmSessionReview(SFParams sfParams) throws SFPmException{
		super(sfParams);
		bmoSessionReview = new BmoSessionReview();
		setBmObject(bmoSessionReview); 

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoSessionReview.getProgramSessionId(), bmoSessionReview.getBmoProgramSessionLevel().getBmoProgramSession()),
				new PmJoin(bmoSessionReview.getProgramSessionLevelId(), bmoSessionReview.getBmoProgramSessionLevel()),
				new PmJoin(bmoSessionReview.getUserId(), bmoSessionReview.getBmoUser()),
				new PmJoin(bmoSessionReview.getBmoUser().getAreaId(), bmoSessionReview.getBmoUser().getBmoArea()),
				new PmJoin(bmoSessionReview.getBmoUser().getLocationId(), bmoSessionReview.getBmoUser().getBmoLocation())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoSessionReview = (BmoSessionReview)autoPopulate(pmConn, new BmoSessionReview());

		// BmoUser
		BmoUser bmoUser = new BmoUser();
		if (pmConn.getInt(bmoUser.getIdFieldName()) > 0) 
			bmoSessionReview.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else 
			bmoSessionReview.setBmoUser(bmoUser);
		
		// BmoProgramSessionLevel
		BmoProgramSessionLevel bmoProgramSessionLevel = new BmoProgramSessionLevel();
		if (pmConn.getInt(bmoProgramSessionLevel.getIdFieldName()) > 0) 
			bmoSessionReview.setBmoProgramSessionLevel((BmoProgramSessionLevel) new PmProgramSessionLevel(getSFParams()).populate(pmConn));
		else 
			bmoSessionReview.setBmoProgramSessionLevel(bmoProgramSessionLevel);

		return bmoSessionReview;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoSessionReview bmoSessionReview = (BmoSessionReview)bmObject;

		// Se almacena de forma preliminar para asignar Clave
		if (!(bmoSessionReview.getId() > 0)) {
			super.save(pmConn, bmoSessionReview, bmUpdateResult);

			//Obtener el Id
			bmoSessionReview.setId(bmUpdateResult.getId());
			
			// Crear los subNiveles del tipo de Programa
			this.createProgramSessionSubLevel(pmConn, bmoSessionReview, bmUpdateResult);
		}
		
		if (!bmUpdateResult.hasErrors())
			super.save(pmConn, bmoSessionReview, bmUpdateResult);

		return bmUpdateResult;
	}

	public void createProgramSessionSubLevel(PmConn pmConn, BmoSessionReview bmoSessionReview, BmUpdateResult bmUpdateResult) throws SFException  {

		BmoProgramSessionSubLevel bmoProgramSessionSubLevel = new BmoProgramSessionSubLevel();
		PmProgramSessionSubLevel pmProgramSessionSubLevel = new PmProgramSessionSubLevel(getSFParams());

		// Obtener los subNiveles del Nivel del Programa
		BmoProgramSessionSubLevelType bmoProgramSessionSubLevelType = new BmoProgramSessionSubLevelType();
		PmProgramSessionSubLevelType pmProgramSessionSubLevelType =  new PmProgramSessionSubLevelType(getSFParams());
		BmFilter bmFilterSubLevelType = new BmFilter();
		bmFilterSubLevelType.setValueFilter(bmoProgramSessionSubLevelType.getKind(), 
				bmoProgramSessionSubLevelType.getProgramSessionLevelId() , 
				bmoSessionReview.getProgramSessionLevelId().toInteger());
		Iterator<BmObject> subLevelTypeList = pmProgramSessionSubLevelType.list(pmConn, bmFilterSubLevelType).iterator();

		while (subLevelTypeList.hasNext()) {
			bmoProgramSessionSubLevelType = (BmoProgramSessionSubLevelType)subLevelTypeList.next();
			bmoProgramSessionSubLevel = new BmoProgramSessionSubLevel();
			bmoProgramSessionSubLevel.getSequence().setValue(bmoProgramSessionSubLevelType.getSequence().toInteger());
			bmoProgramSessionSubLevel.getName().setValue(bmoProgramSessionSubLevelType.getName().toString());
			bmoProgramSessionSubLevel.getDescription().setValue(bmoProgramSessionSubLevelType.getDescription().toString());
			bmoProgramSessionSubLevel.getObservation().setValue("");
			bmoProgramSessionSubLevel.getProgress().setValue(false);
			bmoProgramSessionSubLevel.getSessionReviewId().setValue(bmoSessionReview.getId());
			bmoProgramSessionSubLevel.getProgramSessionLevelId().setValue(bmoSessionReview.getProgramSessionLevelId().toInteger());
			pmProgramSessionSubLevel.save(pmConn, bmoProgramSessionSubLevel, bmUpdateResult);
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoSessionReview = (BmoSessionReview)bmObject;

		// Eliminar SubNiveles
		PmProgramSessionSubLevel pmProgramSessionSubLevel = new PmProgramSessionSubLevel(getSFParams());
		BmoProgramSessionSubLevel bmoProgramSessionSubLevel = new BmoProgramSessionSubLevel();
		BmFilter filterBySessionReview = new BmFilter();
		filterBySessionReview.setValueFilter(bmoProgramSessionSubLevel.getKind(), bmoProgramSessionSubLevel.getSessionReviewId(), bmoSessionReview.getId());
		ListIterator<BmObject> subLevelList = pmProgramSessionSubLevel.list(pmConn, filterBySessionReview).listIterator();
		while (subLevelList.hasNext()) {
			bmoProgramSessionSubLevel = (BmoProgramSessionSubLevel)subLevelList.next();
			pmProgramSessionSubLevel.delete(pmConn, bmoProgramSessionSubLevel, bmUpdateResult);
		}	

		// Eliminar Evaluacion
		super.delete(pmConn, bmoSessionReview, bmUpdateResult);

		return bmUpdateResult;
	}
}

