/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.wf;

import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.sf.PmFileType;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoFileType;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowDocument;
import com.flexwm.shared.wf.BmoWFlowLog;


public class PmWFlowDocument extends PmObject {
	BmoWFlowDocument bmoWFlowDocument;

	public PmWFlowDocument(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWFlowDocument = new BmoWFlowDocument();
		setBmObject(bmoWFlowDocument);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWFlowDocument.getWFlowId(), bmoWFlowDocument.getBmoWFlow()),
				new PmJoin(bmoWFlowDocument.getBmoWFlow().getWFlowTypeId(), bmoWFlowDocument.getBmoWFlow().getBmoWFlowType()),
				new PmJoin(bmoWFlowDocument.getBmoWFlow().getBmoWFlowType().getWFlowCategoryId(), bmoWFlowDocument.getBmoWFlow().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWFlowDocument.getBmoWFlow().getWFlowPhaseId(), bmoWFlowDocument.getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWFlowDocument.getFileTypeId(), bmoWFlowDocument.getBmoFileType()),
				new PmJoin(bmoWFlowDocument.getBmoWFlow().getWFlowFunnelId(), bmoWFlowDocument.getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoWFlowDocument bmoWFlowDocument = (BmoWFlowDocument)autoPopulate(pmConn, new BmoWFlowDocument());		

		// BmoWFlow
		BmoWFlow bmoWFlow = new BmoWFlow();
		if ((int)pmConn.getInt(bmoWFlow.getIdFieldName()) > 0) 
			bmoWFlowDocument.setBmoWFlow((BmoWFlow) new PmWFlow(getSFParams()).populate(pmConn));
		else 
			bmoWFlowDocument.setBmoWFlow(bmoWFlow);

		// BmoFileType
		BmoFileType bmoFileType = new BmoFileType();
		if ((int)pmConn.getInt(bmoFileType.getIdFieldName()) > 0) 
			bmoWFlowDocument.setBmoFileType((BmoFileType) new PmFileType(getSFParams()).populate(pmConn));
		else 
			bmoWFlowDocument.setBmoFileType(bmoFileType);

		return bmoWFlowDocument;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowDocument = (BmoWFlowDocument)bmObject;

		// Se almacena de forma preliminar para asignar ID
		if (!(bmoWFlowDocument.getId() > 0)) {
			super.save(pmConn, bmoWFlowDocument, bmUpdateResult);

			bmoWFlowDocument.setId(bmUpdateResult.getId());

			// Si no esta asignada ya la clave por algun objeto foraneo, crear nueva
			if (!(bmoWFlowDocument.getCode().toString().length() > 0))
				bmoWFlowDocument.getCode().setValue(bmoWFlowDocument.getCodeFormat());
		}

		// Revisar si tiene asignacion de archivo
		if (!bmoWFlowDocument.getFile().toString().equals("")
				|| !bmoWFlowDocument.getFileLink().toString().equals(""))
			bmoWFlowDocument.getIsUp().setValue(true);
		else
			bmoWFlowDocument.getIsUp().setValue(false);	

		super.save(pmConn, bmoWFlowDocument, bmUpdateResult);

		// Actualiza estatus de documentos
		PmWFlow pmWFlow = new PmWFlow(getSFParams());
		BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowDocument.getWFlowId().toInteger());
		pmWFlow.checkRequiredDocuments(pmConn, bmoWFlow, bmUpdateResult);

		// Generar bitacora
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		// Verificar si tiene archivo arriba
		if (bmoWFlowDocument.getIsUp().toBoolean()) {
			String link = "";

			// Toma los valores del archivo subido o de liga inserta
			if (bmoWFlowDocument.getFile().toString().equals("")) {
				link = bmoWFlowDocument.getFileLink().toString();
			} else {
				link = HtmlUtil.parseImageLink(getSFParams(), bmoWFlowDocument.getFile());
			}
			pmWFlowLog.addLinkLog(pmConn, bmUpdateResult, bmoWFlow.getId(),	BmoWFlowLog.TYPE_WFLOW, 
					"Se Modificó el Documento: " +  bmoWFlowDocument.getCode().toString() + " - " + bmoWFlowDocument.getName().toString(), 
					link);
		} else {
			// No tiene, se genera la bitacora informativa
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW,
					"Se Modificó el Documento: " +  bmoWFlowDocument.getCode().toString() + " - " + bmoWFlowDocument.getName().toString());
		}

		return super.save(pmConn, bmoWFlowDocument, bmUpdateResult);
	}

	// Obtiene el documento por clave, en un flujo
	public BmoWFlowDocument getByCode(PmConn pmConn, int wFlowId, String code) throws SFException {
		BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();

		pmConn.doFetch("SELECT * FROM wflowdocuments "
				+ " WHERE wfdo_wflowid = " + wFlowId + " "
				+ " AND wfdo_code LIKE '" + code + "'");
		if (pmConn.next()) {
			int wFlowDocumentId = pmConn.getInt("wfdo_wflowdocumentid");
			bmoWFlowDocument = (BmoWFlowDocument)this.get(pmConn, wFlowDocumentId);
		}

		return bmoWFlowDocument;
	}

	@Override
	public BmUpdateResult delete(BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWFlowDocument = (BmoWFlowDocument)bmObject;
		PmConn pmConn = new PmConn(getSFParams());
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			if (!bmoWFlowDocument.getRequired().toBoolean()) {
				super.delete(pmConn, bmObject, bmUpdateResult);

				// Generar bitacora
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				BmoWFlow bmoWFlow = (BmoWFlow)pmWFlow.get(pmConn, bmoWFlowDocument.getWFlowId().toInteger());
				PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
				
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoWFlow.getId(), BmoWFlowLog.TYPE_WFLOW,
						"Se eliminó el Documento: " + bmoWFlowDocument.getName().toString());

				if (!bmUpdateResult.hasErrors()) {
					pmConn.commit();

					// se desctiva la eliminacion del blob, por si hay mas referencias al copiar el wflowdocument
					//					BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
					//				    BlobKey blobKey = new BlobKey(bmoWFlowDocument.getFile().toString());
					//				    blobstoreService.delete(blobKey);
				}

			} else {
				bmUpdateResult.addError(bmoWFlowDocument.getName().getName(), "No se puede eliminar el Documento: es Requerido.");
			}

		} catch (Exception e) {
			bmUpdateResult.addError(bmObject.getProgramCode(), e.toString());
		} finally {
			pmConn.close();
		}
		return bmUpdateResult;
	}
}
