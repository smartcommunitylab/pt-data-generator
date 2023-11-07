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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MethodNotSupportedException;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.ResourceUtils;

import it.sayservice.platform.smartplanner.areainfo.AreaPointProcessor;
import it.sayservice.platform.smartplanner.areainfo.CostData;
import it.sayservice.platform.smartplanner.areainfo.SearchTime;
import it.sayservice.platform.smartplanner.areainfo.SearchTime.SearchTimeSlot;
import it.sayservice.platform.smartplanner.model.AreaPoint;
import it.sayservice.platform.smartplanner.utils.ODFParser;

public class TrentoAreaInfoGenerator {

	public static void generateTrentoStreetData() throws Exception {
		// generate points
		String urlString = "http://vas-dev.smartcampuslab.it/tm-trento/rest/via";
		URL url = ResourceUtils.getURL(urlString);
		InputStream is = url.openStream();
		ObjectMapper mapper = new ObjectMapper();
		List<?> list = mapper.readValue(is, List.class);

		Writer w = new OutputStreamWriter(new FileOutputStream("src/test/resources/trento/dati.json"));
		w.write(mapper.writeValueAsString(list));
		w.flush();
		w.close();

		// generate cost data
		urlString = "http://vas-dev.smartcampuslab.it/tm-trento/rest/area";
		url = ResourceUtils.getURL(urlString);
		is = url.openStream();
		list = mapper.readValue(is, List.class);

		w = new OutputStreamWriter(new FileOutputStream("src/test/resources/trento/aree.json"));
		w.write(mapper.writeValueAsString(list));
		w.flush();
		w.close();

	}

	public static void generateTrentoPoints() throws Exception {
		// read main data
		InputStream is = TrentoAreaInfoGenerator.class.getResourceAsStream("/trento/dati.json");

		AreaPointProcessor arePointProc = new AreaPointTrentoProcessor();
		List<AreaPoint> list = arePointProc.read(is);

		Writer w = new OutputStreamWriter(new FileOutputStream("src/main/resources/areainfo/trento-points.json"));
		w.write(new ObjectMapper().writeValueAsString(list));
		w.flush();
		w.close();
	}

	public static void generateTrentoInfo() throws Exception {
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

		Map<String, List<List<String>>> map = ODFParser.parseTable("/trento/OCC_RATE.ods", 11);

		String key = "searchTime";
		fillInSearchTime(result, map, key, "MON", Calendar.MONDAY);
		fillInSearchTime(result, map, key, "TUE", Calendar.TUESDAY);
		fillInSearchTime(result, map, key, "WED", Calendar.WEDNESDAY);
		fillInSearchTime(result, map, key, "THU", Calendar.THURSDAY);
		fillInSearchTime(result, map, key, "FRI", Calendar.FRIDAY);
		fillInSearchTime(result, map, key, "SAT", Calendar.SATURDAY);
		fillInSearchTime(result, map, key, "SUN", Calendar.SUNDAY);

		Writer w = new OutputStreamWriter(new FileOutputStream("src/main/resources/areainfo/trento-data.json"));
		w.write(new ObjectMapper().writeValueAsString(result));
		w.flush();
		w.close();

	}

	public static void generateTrentoCosts() throws Exception {
		Map<String, CostData> result = new HashMap<String, CostData>();
		ObjectMapper om = new ObjectMapper();
		List<?> list = om.readValue(TrentoAreaInfoGenerator.class.getResourceAsStream("/trento/aree.json"), List.class);
		for (Object o : list) {
			Map<String, Object> area = om.convertValue(o, Map.class);
			CostData cd = new CostData();
			Double cost = (Double) area.get("fee");
			cd.setFixedCost(String.format("%.2f", cost));
			cd.setCostDefinition(String.format("€. %s/ora", cd.getFixedCost()));
			String id = (String) area.get("id");
			String name = (String) area.get("name");
			System.err.println(name + "," + id);
			result.put(id, cd);
		}

		Writer w = new OutputStreamWriter(new FileOutputStream("src/main/resources/areainfo/trento-costs.json"));
		w.write(new ObjectMapper().writeValueAsString(result));
		w.flush();
		w.close();

	}

	protected static void fillInSearchTime(Map<String, Map<String, Object>> result, Map<String, List<List<String>>> map,
			String key, String day, int dayOfWeek) {
		List<List<String>> table = map.get(day);
		for (List<String> line : table) {
			String area = line.get(1);
			Map<String, Object> areaData = result.get(area);
			if (areaData == null) {
				areaData = new HashMap<String, Object>();
				result.put(area, areaData);
			}
			SearchTime st = (SearchTime) areaData.get(key);
			if (st == null) {
				st = new SearchTime();
				areaData.put(key, st);
			}
			SearchTimeSlot[] timeSlots = new SearchTimeSlot[24];
			st.getDayMap().put(dayOfWeek, timeSlots);
			// HERE hardcoded according to the current format
			for (int i = 0; i < timeSlots.length; i++) {
				if (i < 7)
					timeSlots[i] = rateToTimeSlot(line.get(2));
				if (i >= 7 && i < 9)
					timeSlots[i] = rateToTimeSlot(line.get(3));
				if (i >= 9 && i < 11)
					timeSlots[i] = rateToTimeSlot(line.get(4));
				if (i >= 11 && i < 13)
					timeSlots[i] = rateToTimeSlot(line.get(5));
				if (i >= 13 && i < 15)
					timeSlots[i] = rateToTimeSlot(line.get(6));
				if (i >= 15 && i < 17)
					timeSlots[i] = rateToTimeSlot(line.get(7));
				if (i >= 17 && i < 19)
					timeSlots[i] = rateToTimeSlot(line.get(8));
				if (i >= 19 && i < 22)
					timeSlots[i] = rateToTimeSlot(line.get(9));
				if (i >= 22 && i < 24)
					timeSlots[i] = rateToTimeSlot(line.get(10));
			}
		}
	}

	private static SearchTimeSlot rateToTimeSlot(String string) {
		// assume percent symbol at the end
		double value = Double.parseDouble(string.replace(',', '.').substring(0, string.length() - 1));
		if (value < 60)
			return new SearchTimeSlot(0, 0);
		if (value >= 60 && value < 70)
			return new SearchTimeSlot(0, 1);
		if (value >= 70 && value < 80)
			return new SearchTimeSlot(1, 3);
		if (value >= 80 && value < 90)
			return new SearchTimeSlot(2, 5);
		if (value >= 90 && value < 100)
			return new SearchTimeSlot(3, 10);
		if (value >= 100)
			return new SearchTimeSlot(5, 15);
		return null;
	}

	private static class AreaPointTrentoProcessor implements AreaPointProcessor {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List<AreaPoint> read(InputStream is) throws Exception {
			List<AreaPoint> result = new ArrayList<AreaPoint>();
			ObjectMapper om = new ObjectMapper();
			List<?> list = om.readValue(is, List.class);
			for (Object o : list) {
				Map<String, Object> point = om.convertValue(o, Map.class);
				AreaPoint ap = new AreaPoint();

				ap.setId((String) point.get("id"));
				ap.setAreaId((String) point.get("areaId"));
				ap.setCostZoneId(ap.getAreaId());
				List<Map<String, Double>> geoList = (List<Map<String, Double>>) ((Map) point.get("geometry"))
						.get("points");
				ap.setPosition(extractLocation(geoList));

				result.add(ap);
			}
			return result;
		}

		private double[] extractLocation(List<Map<String, Double>> positions) {
			// centroid
			double sumx = 0, sumy = 0;
			for (Map<String, Double> p : positions) {
				sumx += p.get("lat");
				sumy += p.get("lng");
			}
			return new double[] { sumx / positions.size(), sumy / positions.size() };
		}

		@Override
		public List<AreaPoint> read(InputStream is, AreaPointIdentityMapper mapper) throws Exception {
			throw new MethodNotSupportedException();
		}

	}

	public static void main(String[] args) throws Exception {
		// generateTrentoStreetData();
		generateTrentoPoints();
		generateTrentoCosts();
		generateTrentoInfo();
	}
}
