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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import it.sayservice.platform.smartplanner.areainfo.AreaPointKMLProcessor;
import it.sayservice.platform.smartplanner.areainfo.AreaPointProcessor.AreaPointIdentityMapper;
import it.sayservice.platform.smartplanner.areainfo.CostData;
import it.sayservice.platform.smartplanner.areainfo.SearchTime;
import it.sayservice.platform.smartplanner.areainfo.SearchTime.SearchTimeSlot;
import it.sayservice.platform.smartplanner.model.AreaPoint;
import it.sayservice.platform.smartplanner.utils.ODFParser;

public class RoveretoAreaInfoGenerator {

	public static void generateRoveretoPoints() throws Exception {
		// read main data
		InputStream is = RoveretoAreaInfoGenerator.class.getResourceAsStream("/rovereto/dati.kml");

		AreaPointKMLProcessor arePointProc = new AreaPointKMLProcessor();
		List<AreaPoint> list = arePointProc.read(is, new AreaPointIdentityMapper() {
			@Override
			public String getId(Map<String, Object> objectData) {
				return (String) objectData.get("ID_GRUPPO");
			}

			@Override
			public String getArea(Map<String, Object> data) {
				return data.get("MACROZONA") + "_" + data.get("SUB_MACRO");
			}

			@Override
			public String getCostZone(Map<String, Object> data) {
				return (String) data.get("TARIFFA");
			}

		});

		Writer w = new OutputStreamWriter(new FileOutputStream("src/main/resources/areainfo/rovereto-points.json"));
		w.write(new ObjectMapper().writeValueAsString(list));
		w.flush();
		w.close();
	}

	public static void generateRoveretoInfo() throws Exception {
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

		Map<String, List<List<String>>> map = ODFParser.parseTable("/rovereto/soste/OCC_RATE.ods", 19);

		String key = "searchTime";
		fillInSearchTime(result, map, key, "MON", Calendar.MONDAY);
		fillInSearchTime(result, map, key, "TUE", Calendar.TUESDAY);
		fillInSearchTime(result, map, key, "WED", Calendar.WEDNESDAY);
		fillInSearchTime(result, map, key, "THU", Calendar.THURSDAY);
		fillInSearchTime(result, map, key, "FRI", Calendar.FRIDAY);
		fillInSearchTime(result, map, key, "SAT", Calendar.SATURDAY);
		fillInSearchTime(result, map, key, "SUN", Calendar.SUNDAY);

		Writer w = new OutputStreamWriter(new FileOutputStream("src/main/resources/areainfo/rovereto-data.json"));
		w.write(new ObjectMapper().writeValueAsString(result));
		w.flush();
		w.close();

	}

	public static void generateRoveretoCosts() throws Exception {
		Map<String, CostData> result = new HashMap<String, CostData>();

		Map<String, List<List<String>>> costFile = ODFParser.parseTable("/rovereto/costi.ods", 3);
		List<List<String>> costs = costFile.values().iterator().next();
		for (List<String> line : costs) {
			CostData cd = new CostData();
			cd.setFixedCost(line.get(1));
			cd.setCostDefinition(line.get(2));
			result.put(line.get(0), cd);
		}
		Writer w = new OutputStreamWriter(new FileOutputStream("src/main/resources/areainfo/rovereto-costs.json"));
		w.write(new ObjectMapper().writeValueAsString(result));
		w.flush();
		w.close();

	}

	protected static void fillInSearchTime(Map<String, Map<String, Object>> result, Map<String, List<List<String>>> map,
			String key, String day, int dayOfWeek) {
		List<List<String>> table = map.get(day);
		for (List<String> line : table) {
			String area = line.get(0);
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
					timeSlots[i] = rateToTimeSlot(line.get(11));
				if (i >= 7 && i < 9)
					timeSlots[i] = rateToTimeSlot(line.get(12));
				if (i >= 9 && i < 11)
					timeSlots[i] = rateToTimeSlot(line.get(13));
				if (i >= 11 && i < 13)
					timeSlots[i] = rateToTimeSlot(line.get(14));
				if (i >= 13 && i < 15)
					timeSlots[i] = rateToTimeSlot(line.get(15));
				if (i >= 15 && i < 17)
					timeSlots[i] = rateToTimeSlot(line.get(16));
				if (i >= 17 && i < 19)
					timeSlots[i] = rateToTimeSlot(line.get(17));
				if (i >= 19 && i < 24)
					timeSlots[i] = rateToTimeSlot(line.get(18));
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

	public static void main(String[] args) throws Exception {
		generateRoveretoPoints();
		generateRoveretoInfo();
		generateRoveretoCosts();
	}
}
