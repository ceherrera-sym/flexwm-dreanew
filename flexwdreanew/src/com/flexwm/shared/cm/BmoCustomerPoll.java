/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author jhernandez
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoCustomerPoll extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField projectId, datePoll, service, image,
	performance,quality,general,recommendation,observation;


	public BmoCustomerPoll() {
		super("com.flexwm.server.cm.PmCustomerPoll", "customerpolls", "customerpollid", "CUPO", "Encuesta");

		// Campo de datos
		projectId= setField("projectid", "", "Proyecto", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		datePoll = setField("polldate", "", "Fecha", 20, Types.DATE, true, BmFieldType.DATE, false);
		service = setField("service", "", "Servicio", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		image = setField("image", "", "Imagen", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);		
		performance = setField("performance", "", "Desempeño", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		quality = setField("quality", "", "Calidad", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		general = setField("general", "", "En general", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		recommendation = setField("recommendation", "", "Recomendación", 8, Types.DOUBLE, true, BmFieldType.NUMBER, false);
		observation = setField("observations", "", "Observaciones", 1024, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getRecommendation()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getRecommendation())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getRecommendation(), BmOrder.ASC)));
	}



	/**
	 * @return the projectId
	 */
	public BmField getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(BmField projectId) {
		this.projectId = projectId;
	}


	/**
	 * @return the datePoll
	 */
	public BmField getDatePoll() {
		return datePoll;
	}

	/**
	 * @param datePoll the datePoll to set
	 */
	public void setDatePoll(BmField datePoll) {
		this.datePoll = datePoll;
	}

	/**
	 * @return the service
	 */
	public BmField getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(BmField service) {
		this.service = service;
	}

	/**
	 * @return the image
	 */
	public BmField getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BmField image) {
		this.image = image;
	}

	/**
	 * @return the performance
	 */
	public BmField getPerformance() {
		return performance;
	}

	/**
	 * @param performance the performance to set
	 */
	public void setPerformance(BmField performance) {
		this.performance = performance;
	}

	/**
	 * @return the quality
	 */
	public BmField getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(BmField quality) {
		this.quality = quality;
	}

	/**
	 * @return the general
	 */
	public BmField getGeneral() {
		return general;
	}

	/**
	 * @param general the general to set
	 */
	public void setGeneral(BmField general) {
		this.general = general;
	}

	/**
	 * @return the recommendation
	 */
	public BmField getRecommendation() {
		return recommendation;
	}

	/**
	 * @param recommendation the recommendation to set
	 */
	public void setRecommendation(BmField recommendation) {
		this.recommendation = recommendation;
	}

	/**
	 * @return the observation
	 */
	public BmField getObservation() {
		return observation;
	}

	/**
	 * @param observation the observation to set
	 */
	public void setObservation(BmField observation) {
		this.observation = observation;
	}


}
