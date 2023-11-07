/**
 * Copyright 2011-2016 SAYservice s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package it.sayservice.platform.smartplanner.test.scripts;

import java.util.ArrayList;
import java.util.List;

public class GTFSModel {

	private List<String> agency;

	private List<String> routes;
	private List<String> trips;
	private List<String> transfers;
	private List<String> stops;
	private List<String> stopTimes;
	private List<String> shapes;
	private List<String> calendars;
	private List<String> calendarDates;
	private List<String> fareRules;
	private List<String> fareAttributes;

	public List<String> getAgency() {
		if (agency == null)
			agency = new ArrayList<String>();
		return agency;
	}

	public void setAgency(List<String> agency) {
		this.agency = agency;
	}

	public List<String> getRoutes() {
		if (routes == null)
			routes = new ArrayList<String>();
		return routes;
	}

	public void setRoutes(List<String> routes) {
		this.routes = routes;
	}

	public List<String> getTrips() {
		if (trips == null)
			trips = new ArrayList<String>();
		return trips;
	}

	public void setTrips(List<String> trips) {
		this.trips = trips;
	}

	public List<String> getTransfers() {
		if (transfers == null)
			transfers = new ArrayList<String>();
		return transfers;
	}

	public void setTransfers(List<String> transfers) {
		this.transfers = transfers;
	}

	public List<String> getStops() {
		if (stops == null)
			stops = new ArrayList<String>();
		return stops;
	}

	public void setStops(List<String> stops) {
		this.stops = stops;
	}

	public List<String> getStopTimes() {
		if (stopTimes == null)
			stopTimes = new ArrayList<String>();
		return stopTimes;
	}

	public void setStopTimes(List<String> stopTimes) {
		this.stopTimes = stopTimes;
	}

	public List<String> getShapes() {
		if (shapes == null)
			shapes = new ArrayList<String>();
		return shapes;
	}

	public void setShapes(List<String> shapes) {
		this.shapes = shapes;
	}

	public List<String> getCalendars() {
		if (calendars == null)
			calendars = new ArrayList<String>();
		return calendars;
	}

	public void setCalendars(List<String> calendars) {
		this.calendars = calendars;
	}

	public List<String> getCalendarDates() {
		if (calendarDates == null)
			calendarDates = new ArrayList<String>();
		return calendarDates;
	}

	public void setCalendarDates(List<String> calendarDates) {
		this.calendarDates = calendarDates;
	}

	public List<String> getFareRules() {
		if (fareRules == null)
			fareRules = new ArrayList<String>();
		return fareRules;
	}

	public void setFareRules(List<String> fareRules) {
		this.fareRules = fareRules;
	}

	public List<String> getFareAttributes() {
		if (fareAttributes == null)
			fareAttributes = new ArrayList<String>();
		return fareAttributes;
	}

	public void setFareAttributes(List<String> fareAttributes) {
		this.fareAttributes = fareAttributes;
	}

}
