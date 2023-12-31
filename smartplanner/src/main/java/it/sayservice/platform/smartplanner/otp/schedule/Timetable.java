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

package it.sayservice.platform.smartplanner.otp.schedule;

import java.util.ArrayList;
import java.util.List;

public class Timetable {

	private String routeId;
	private List<String> stopsIds;
	private List<TripSchedule> schedules;
	private boolean correct = false;

	public Timetable() {
		stopsIds = new ArrayList<String>();
		schedules = new ArrayList<TripSchedule>();
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public List<String> getStopsIds() {
		return stopsIds;
	}

	public void setStopsIds(List<String> stopsIds) {
		this.stopsIds = stopsIds;
	}

	public List<TripSchedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<TripSchedule> schedules) {
		this.schedules = schedules;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

}
