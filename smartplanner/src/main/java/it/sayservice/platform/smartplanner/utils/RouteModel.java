package it.sayservice.platform.smartplanner.utils;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteModel {

	private Map<String, AgencyModel> agencies;

	public List<AgencyModel> getAgencies() {
		return new ArrayList<RouteModel.AgencyModel>(agencies.values());
	}

	public void setAgencies(List<AgencyModel> agencies) {
		this.agencies = new HashMap<String, RouteModel.AgencyModel>();
		for (AgencyModel a : agencies) {
			this.agencies.put(a.getAgencyId(), a);
		}
	}

	public AgencyModel agency(String agencyId) {
		return this.agencies.get(agencyId);
	}

	public static class AgencyModel {
		private String agencyId;
		private List<String> routeIds;
		private Map<String, String> routeMappings;
		private Map<String, String> routeNames;
		private List<String> ignoreRoutes;
		private List<String> wheelChairBoardings;
		private List<String> wheelChairBoardingsExceptionRoutes;

		public String getAgencyId() {
			return agencyId;
		}

		public void setAgencyId(String agencyId) {
			this.agencyId = agencyId;
		}

		public List<String> getRouteIds() {
			return routeIds;
		}

		public void setRouteIds(List<String> routeIds) {
			this.routeIds = routeIds;
		}

		public Map<String, String> getRouteMappings() {
			return routeMappings;
		}

		public void setRouteMappings(Map<String, String> routeMappings) {
			this.routeMappings = routeMappings;
		}

		public Map<String, String> getRouteNames() {
			return routeNames;
		}

		public void setRouteNames(Map<String, String> routeNames) {
			this.routeNames = routeNames;
		}

		public List<String> getIgnoreRoutes() {
			return ignoreRoutes;
		}

		public void setIgnoreRoutes(List<String> ignoreRoutes) {
			this.ignoreRoutes = ignoreRoutes;
		}

		public List<String> getWheelChairBoardings() {
			return wheelChairBoardings;
		}

		public void setWheelChairBoardings(List<String> wheelChairBoardings) {
			this.wheelChairBoardings = wheelChairBoardings;
		}

		public List<String> getWheelChairBoardingsExceptionRoutes() {
			return wheelChairBoardingsExceptionRoutes;
		}

		public void setWheelChairBoardingsExceptionRoutes(List<String> wheelChairBoardingsExceptionRoutes) {
			this.wheelChairBoardingsExceptionRoutes = wheelChairBoardingsExceptionRoutes;
		}

	}
}
