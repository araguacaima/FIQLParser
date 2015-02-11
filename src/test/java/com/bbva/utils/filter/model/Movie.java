package com.bbva.utils.filter.model;

import java.math.BigDecimal;

/**
 * Created by Alejandro on 19/11/2014.
 */
public class Movie {
	private Director director;
	private Actor actor;
	private Genres genres;
	private String name;
	private int year;
	private String distributionDate;
	private BigDecimal budget;

	public Director getDirector() {
		return director;
	}

	public void setDirector(final Director director) {
		this.director = director;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(final Actor actor) {
		this.actor = actor;
	}

	public Genres getGenres() {
		return genres;
	}

	public void setGenres(final Genres genres) {
		this.genres = genres;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getYear() {
		return year;
	}

	public void setYear(final int year) {
		this.year = year;
	}

	public String getDistributionDate() {
		return distributionDate;
	}

	public void setDistributionDate(final String distributionDate) {
		this.distributionDate = distributionDate;
	}

	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(final BigDecimal budget) {
		this.budget = budget;
	}

}
