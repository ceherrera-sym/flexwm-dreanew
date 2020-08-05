package com.flexwm.server;

import java.util.List;

public class Serie {

	// SieAPI Tipo de cambio Banco de Mexico
	// (Archivos relacionados en com.flexwm.server: DataSerie, Response, Serie, Series, SeriesResponse)
	private String idSerie;
	private String titulo;

	private List<DataSerie>datos;

	public String getIdSerie() {
		return idSerie;
	}

	public void setIdSerie(String idSerie) {
		this.idSerie = idSerie;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public List<DataSerie> getDatos() {
		return datos;
	}

	public void setDatos(List<DataSerie> datos) {
		this.datos = datos;
	}

}
