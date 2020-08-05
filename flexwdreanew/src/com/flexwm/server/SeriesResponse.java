package com.flexwm.server;

import java.util.List;

public class SeriesResponse {

	// SieAPI Tipo de cambio Banco de Mexico
	// (Archivos relacionados en com.flexwm.server: DataSerie, Response, Serie, Series, SeriesResponse)

	private List<Serie>series;

	public List<Serie> getSeries() {
		return series;
	}

	public void setSeries(List<Serie> series) {
		this.series = series;
	}

}
