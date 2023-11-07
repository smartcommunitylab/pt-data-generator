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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Iterables;
import com.google.gdata.util.io.base.UnicodeReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

public class GTFSModifier {
	private static final String UTF8_BOM = "\uFEFF";
	private static Properties properties = new Properties();
	private static String configName = "config.properties";
	private static HashMap<String, String> mappedRouteId = new HashMap<String, String>();
	private static String exUrbanAgencyId = "17";
	private static List<String> exceptionStopsList = new ArrayList<String>(Arrays.asList("Fiave' - Loc.Doss"));

	public static void main(String[] args) throws IOException {

		// Mongo m = new Mongo("localhost"); // default port 27017
		// DB db = m.getDB("smart-planner-15x");
		// mapRouteLongName(m, db);
		// mapRoutes(m, db);
		// modifyRouteIDBasedOnDirection(m, db);
		// createScheduleFromTrip(m, db);
		// appendShapeId(m, db);
		// createRouteIdShortNameMap(m, db);
		// findMissingShapes(m, db);
		// modifyTripId();
		// fixStopFile();
		 createDateException();
//		datePartionar("2022-09-12", "2023-06-10");
		
		System.out.println("Done");
	}

	private static void createDateException() {

		Calendar cl = Calendar.getInstance();
		String calDatesFile = "src\\test\\resources\\folgaria\\calendar_dates.txt";

		String serviceId = "solo_nei_giorni_scolastici_winter"; //scolastico_escluso_sabato_winter
		List<Date> dates = new ArrayList<Date>();

		String str_date = "20220912";
		String end_date = "20230609";
		try {
			// read exception file
			List<String[]> calDatesLines = readFileGetLines(calDatesFile);
			List<String> offeredDates = new ArrayList<String>();

			for (String[] line : calDatesLines) {
				if (line[0].equalsIgnoreCase(serviceId) && line[2].equalsIgnoreCase("1")) {
					offeredDates.add(line[1]);
				}
			}

			DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date startDate = (Date) formatter.parse(str_date);
			Date endDate;

			endDate = (Date) formatter.parse(end_date);

			long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long curTime = startDate.getTime();
			while (curTime <= endTime) {
				dates.add(new Date(curTime));
				curTime += interval;
			}
			for (int i = 0; i < dates.size(); i++) {
				
				Date lDate = (Date) dates.get(i);
				cl.setTime(lDate);
				String ds = formatter.format(lDate);
				if (offeredDates.contains(ds) && cl.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ) {
					System.out.println("scolastico_escluso_sabato_winter," + ds + ",1");
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void fixStopFile() throws IOException {
		String stopFilePath = "C:/projects/marco/annotated.timetable/sayservice.it/src/test/resources/gtfs/17/stops.txt";

		List<String[]> stoplines = readFileGetLines(stopFilePath);

		for (int i = 0; i < stoplines.size(); i++) {

			String stopName = stoplines.get(i)[2];
			if (exceptionStopsList.contains(stopName)) {
				continue;
			}
			stopName = stopName.replace(" )", ")");
			stopName = stopName.replace(" (", "(");
			stopName = stopName.replace("à", "a'");
			stopName = stopName.replace("è", "e'");
			stopName = stopName.replace("ì", "i'");
			stopName = stopName.replace(". ", ".");
			if (stoplines.get(i)[2].indexOf("-") != -1) {
				stopName = stopName.replace("-", "(");
				stopName = stopName + ")";
			}

			System.out.println(stoplines.get(i)[0] + "," + stoplines.get(i)[1] + ","
					+ new String(stopName.getBytes(), "UTF-8") + "," + stoplines.get(i)[3] + "," + stoplines.get(i)[4]
					+ "," + stoplines.get(i)[5] + "," + stoplines.get(i)[6]);
		}

	}

	private static void modifyTripId() throws IOException {
		String tripFile = "src\\test\\resources\\brenerro\\trips.txt";

		String appendString = "$2015061420151212";
		List<String[]> trips = readFileGetLines(tripFile);

		for (String[] words : trips) {

			System.out.println(words[0] + "," + words[1] + "_SUMMER" + "," + words[2] + appendString + "," + words[3]
					+ "," + words[4] + "," + words[5]);

		}

		// String csvFile =
		// "src\\test\\resources\\brenerro\\ORARI_BRENERRO_RITORNO.csv";
		//
		// List<String[]> csvFileLines = readFileGetLines(csvFile);
		//
		// String[] heading = csvFileLines.get(0);
		// String firstLine = "";
		// for (String s: heading) {
		// firstLine = firstLine + s + appendString + ",";
		// }
		//
		// System.out.println(firstLine);

	}

	private static void mapRouteLongName(Mongo m, DB db) throws IOException {

		/** read config.properties and initialize map **/
		InputStream inputStream = GTFSModifier.class.getResourceAsStream("/" + configName);
		properties.load(inputStream);
		String exUrbanString = properties.getProperty("EXURBAN");
		List<String> exUrbanList = new ArrayList<String>();
		StringTokenizer exUrbanRoutes = new StringTokenizer(exUrbanString, ",");
		while (exUrbanRoutes.hasMoreElements()) {
			exUrbanList.add(exUrbanRoutes.nextToken());
		}

		for (String exUrbanRouteId : exUrbanList) {
			String temp = properties.getProperty(exUrbanRouteId + "_" + exUrbanAgencyId + "_0");
			if (temp != null) {
				mappedRouteId.put(exUrbanRouteId + "_" + exUrbanAgencyId + "_0", temp);
			}
			temp = properties.getProperty(exUrbanRouteId + "_" + exUrbanAgencyId + "_1");
			if (temp != null) {
				mappedRouteId.put(exUrbanRouteId + "_" + exUrbanAgencyId + "_1", temp);
			}
		}

		// read route file and replace routeLongName.
		// modify routes.txt
		String routeFile = "D:/deleted/check-gtfs/nov-29/extra-urbano/routes.txt";
		String content = null;
		File file = new File("D:/deleted/check-gtfs/nov-29/extra-urbano/eroutes.txt");
		// single leg file
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.flush();

		// read trips.txt(trips,serviceId).
		List<String[]> routes = readFileGetLines(routeFile);

		for (String[] words : routes) {
			try {

				if (words[0].equalsIgnoreCase("route_id")) {
					content = words[0] + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + "\n";
				} else {

					content = words[0] + "," + words[1] + "," + words[2] + "," + mappedRouteId.get(words[0]) + ","
							+ words[4] + "\n";
				}

				bw.write(content);

			} catch (Exception e) {
				System.out.println("Error parsing route: " + words[0] + "," + words[1] + "," + words[2]);
			}
		}

		bw.close();
	}

	private static void mapRoutes(Mongo m, DB db) throws IOException {
		DBCollection coll = db.getCollection("stops");
		String content = null;

		HashMap<String, String> map = new HashMap<String, String>();

		/** TRENTO **/
		/*
		 * map.put("393_12_0", "_A"); map.put("423_12_0", "_B");
		 * map.put("454_12_0", "C"); map.put("394_12_0", "01");
		 * map.put("395_12_0", "02"); map.put("396_12_0", "03A");
		 * map.put("396_12_1", "03R"); map.put("398_12_0", "04A");
		 * map.put("398_12_1", "04R"); map.put("400_12_0", "05A");
		 * map.put("400_12_1", "05R"); map.put("419_12_0", "06A");
		 * map.put("419_12_1", "06R"); map.put("402_12_0", "07A");
		 * map.put("402_12_1", "07R"); map.put("404_12_0", "08A");
		 * map.put("404_12_1", "08R"); map.put("406_12_0", "09A");
		 * map.put("406_12_1", "09R"); map.put("408_12_0", "10A");
		 * map.put("408_12_1", "10R"); map.put("424_12_0", "11A");
		 * map.put("424_12_1", "11R"); map.put("411_12_0", "12A");
		 * map.put("411_12_1", "12R"); map.put("466_12_0", "13A");
		 * map.put("466_12_1", "13R"); map.put("415_12_0", "14A");
		 * map.put("415_12_1", "14R"); map.put("478_12_0", "15A");
		 * map.put("478_12_1", "15R"); map.put("484_12_0", "16A");
		 * map.put("484_12_1", "16R"); map.put("417_12_0", "17A");
		 * map.put("417_12_1", "17R"); map.put("500_12_0", "Da");
		 * map.put("500_12_1", "Dr"); map.put("460_12_0", "NPA");
		 * map.put("531_12_1", "FUTSA"); map.put("531_12_0", "FUTSR");
		 */

		/** ROVERETO **/
		map.put("486_16_0", "AA");
		map.put("486_16_1", "AR");
		map.put("488_16_0", "01A");
		map.put("488_16_1", "01R");
		map.put("490_16_0", "02A");
		map.put("490_16_1", "02R");
		map.put("519_16_0", "03A");
		map.put("519_16_1", "03R");
		map.put("517_16_0", "04A");
		map.put("517_16_1", "04R");
		map.put("497_16_0", "05");
		map.put("496_16_0", "06A");
		map.put("496_16_1", "06R");
		map.put("512_16_0", "07R");
		map.put("512_16_1", "07A");
		map.put("504_16_0", "N2A");
		map.put("504_16_1", "N2R");
		map.put("506_16_1", "N1A");
		map.put("506_16_0", "N1R");
		map.put("507_16_1", "N3A");
		map.put("507_16_0", "N3R");
		map.put("510_16_1", "N5A");
		map.put("510_16_0", "N5R");
		map.put("511_16_0", "N6");

		// change location for routes.txt and trips.txt
		String tripFile = "D:/deleted/gtfs/marco/google_transit_urbano/rovereto-gtfs/routes.txt";

		// read trips.txt(trips,serviceId).
		List<String[]> trips = readFileGetLines(tripFile);

		for (String[] words : trips) {
			try {

				if (words[0].equalsIgnoreCase("route_id")) {
					// routes contents.
					content = words[0] + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + ","
							+ words[5] + "," + words[6] + "\n";

					// trips content.
					// content = words[0] + "," + words[1] + "," + words[2] +
					// ","
					// + words[3] + "," + words[4] + "," + words[5] + "\n";
				} else {
					// routes contents
					content = map.get(words[0]) + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4]
							+ "," + words[5] + "," + words[6] + "\n";

					// trips contents.
					// content = map.get(words[0]) + "," + words[1] + ","
					// + words[2] + "," + words[3] + "," + words[4] + ","
					// + words[5] + "\n";

				}

				File file = new File("D:/deleted/gtfs/marco/google_transit_urbano/rovereto-gtfs/eroutes.txt");
				// single leg file
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();

			} catch (Exception e) {
				System.out.println("Error parsing trip: " + words[0] + "," + words[1] + "," + words[2]);
			}
		}

	}

	private static void modifyRouteIDBasedOnDirection(Mongo m, DB db) throws IOException {
		DBCollection coll = db.getCollection("stops");
		String content = null;
		String tripFile = "D:/deleted/check-gtfs/nov-29/extra-urbano/trips.txt";

		// read trips.txt(trips,serviceId).
		List<String[]> trips = readFileGetLines(tripFile);
		File file = new File("D:/deleted/check-gtfs/nov-29/extra-urbano/etrips.txt");
		// single leg file
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);

		for (String[] words : trips) {
			try {

				if (words[0].equalsIgnoreCase("route_id")) {
					content = words[0] + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + ","
							+ words[5] + "\n";
				} else {

					content = words[0] + "_" + words[4] + "," + words[1] + "," + words[2] + "," + words[3] + ","
							+ words[4] + "," + words[5] + "\n";

				}
				bw.write(content);
			} catch (Exception e) {
				System.out.println("Error parsing trip: " + words[0] + "," + words[1] + "," + words[2]);
			}
		}

		bw.close();

		// modify routes.txt
		String routeFile = "D:/deleted/check-gtfs/nov-29/extra-urbano/routes.txt";
		file = new File("D:/deleted/check-gtfs/nov-29/extra-urbano/eroutes.txt");
		// single leg file
		if (!file.exists()) {
			file.createNewFile();
		}

		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
		bw.flush();

		// read trips.txt(trips,serviceId).
		List<String[]> routes = readFileGetLines(routeFile);

		for (String[] words : routes) {
			try {

				if (words[0].equalsIgnoreCase("route_id")) {
					content = words[0] + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + "\n";
				} else {

					content = words[0] + "_0" + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + "\n"
							+ words[0] + "_1" + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4]
							+ "\n";
				}

				bw.write(content);

			} catch (Exception e) {
				System.out.println("Error parsing route: " + words[0] + "," + words[1] + "," + words[2]);
			}
		}

		bw.close();

	}

	private static void findMissingShapes(Mongo m, DB db) throws IOException {
		// TODO Auto-generated method stub
		List<String> notFound = new ArrayList<String>();
		List<String> map = new ArrayList<String>();
		String tripFile = "C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/extra-urbano/trips.txt";
		String shapeFile = "C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/extra-urbano/shapes.txt";
		String routeFile = "C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/extra-urbano/routes.txt";
		List<String[]> shapes = readFileGetLines(shapeFile);
		List<String[]> trips = readFileGetLines(tripFile);
		List<String[]> routes = readFileGetLines(routeFile);

		for (String[] words : trips) {
			boolean found = false;
			try {

				if (words[0].equalsIgnoreCase("routeId")) {
					continue;
				}
				// get routeShortName from route.txt.
				for (int i = 0; i < shapes.size(); i++) {
					// already ordered by occurence.
					String[] shapeLeg = shapes.get(i);
					if (shapeLeg[0].equalsIgnoreCase(words[5])) {
						found = true;
						break;
					}
				}
				if (!found) {
					if (!notFound.contains(words[0])) {
						notFound.add(words[0]);
					}
				}

			} catch (Exception e) {
				System.out.println("Error parsing trip: " + words[0] + "," + words[1] + "," + words[2]);

			}
		}

		String contentRoute = "(";
		for (String noShape : notFound) {
			contentRoute = contentRoute + "\'" + noShape + "\',";
			for (String[] routeLeg : routes) {
				if (routeLeg[0].equalsIgnoreCase(noShape)) {
					if (!map.contains("$linemap{\'" + routeLeg[0] + "\'} = [\'" + routeLeg[2] + "\'];\n")) {
						map.add("$linemap{\'" + routeLeg[0] + "\'} = [\'" + routeLeg[2] + "\'];\n");
					}
				}
			}
		}
		contentRoute = contentRoute + ")";

		String contentMap = "";
		for (String mapString : map) {
			contentMap = contentMap + mapString;
		}

		System.out.println(contentRoute + "\n" + contentMap);

	}

	private static void createRouteIdShortNameMap(Mongo m, DB db) throws IOException {

		List<String> routeString = new ArrayList<String>();
		List<String> map = new ArrayList<String>();
		String tripFile = "C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/trips.txt";
		String routeFile = "C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/routes.txt";
		File file = new File(
				"C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/script.txt");

		// read trips.txt(trips,serviceId).
		List<String[]> routes = readFileGetLines(routeFile);
		List<String[]> trips = readFileGetLines(tripFile);
		for (String[] words : trips) {
			try {

				if (words[0].equalsIgnoreCase("routeId")) {
					continue;
				}
				// get routeShortName from route.txt.
				for (int i = 0; i < routes.size(); i++) {
					// already ordered by occurence.
					String[] routeLeg = routes.get(i);
					if (routeLeg[0].equalsIgnoreCase(words[0])) {

						if (!routeString.contains("\'" + routeLeg[0] + "\',")) {
							routeString.add("\'" + routeLeg[0] + "\',");
						}

						if (!map.contains("$linemap{\'" + routeLeg[0] + "\'} = [\'" + routeLeg[2] + "\'];\n")) {
							map.add("$linemap{\'" + routeLeg[0] + "\'} = [\'" + routeLeg[2] + "\'];\n");
						}

					}
				}

			} catch (Exception e) {
				System.out.println("Error parsing trip: " + words[0] + "," + words[1] + "," + words[2]);

			}
		}
		String contentRoute = "(";
		for (String route : routeString) {
			contentRoute = contentRoute + route;
		}
		contentRoute = contentRoute + ")";

		String contentMap = "(";
		for (String mapString : map) {
			contentMap = contentMap + mapString;
		}
		contentMap = contentMap + ")";
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(contentRoute + "\n" + contentMap);
		bw.close();

	}

	private static void appendShapeId(Mongo m, DB db) throws IOException {

		String content;
		String tripFile = "C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/trips.txt";
		// read trips.txt(trips,serviceId).
		List<String[]> trips = readFileGetLines(tripFile);
		for (String[] words : trips) {
			try {
				File file = new File(
						"C:/Users/nawazk/Desktop/important/e-territory/multimodal-jp/testagency/extra-urbano/official-without-shapes/etrips.txt");

				if (words[0].equalsIgnoreCase("route_id")) {
					content = words[0] + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + ","
							+ words[5] + "\n";
				} else {
					content = words[0] + "," + words[1] + "," + words[2] + "," + words[3] + "," + words[4] + ","
							+ words[2] + "S\n";
				}
				// single leg file
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();

			} catch (Exception e) {
				System.out.println("Error parsing trip: " + words[0] + "," + words[1] + "," + words[2]);

			}
		}
	}

	private static void createScheduleFromTrip(Mongo m, DB db) throws IOException {

		DBCollection coll = db.getCollection("stops");
		String content = "";

		File file = new File("D:/deleted/gtfs/marco/google_transit_urbano/rovereto-gtfs/Mstop_times.txt");
		// single leg file
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);

		String tripFile = "D:/deleted/gtfs/marco/google_transit_urbano/rovereto-gtfs/trips.txt";
		String stopTimesFile = "D:/deleted/gtfs/marco/google_transit_urbano/rovereto-gtfs/stop_times.txt";

		// read trips.txt(trips,serviceId).
		List<String[]> trips = readFileGetLines(tripFile);
		List<String[]> stopTimes = readFileGetLines(stopTimesFile);

		System.out.println(trips.size() + "->>" + stopTimes.size());

		HashMap<String, List<String[]>> map = new HashMap<String, List<String[]>>();

		for (int i = 0; i < stopTimes.size(); i++) {
			List<String[]> list = map.get(stopTimes.get(i)[0]);
			if (list == null) {
				list = new ArrayList<String[]>();
				map.put(stopTimes.get(i)[0], list);
			}
			list.add(stopTimes.get(i));
		}

		for (String[] words : trips) {
			try {
				String tripId = words[2].trim();
				// fetch schedule for trips.

				List<String[]> list = map.get(tripId);

				if (list == null || list.isEmpty()) {
					System.err.println();
				} else {
					for (int i = 0; i < list.size(); i++) {
						// already ordered by occurence.
						String[] scheduleLeg = list.get(i);
						content = scheduleLeg[0] + "," + scheduleLeg[1] + "," + scheduleLeg[2] + "," + scheduleLeg[3]
								+ "," + scheduleLeg[4] + "\n";
						bw.write(content);

					}
				}

			} catch (Exception e) {
				System.out.println("Error parsing trip: " + words[0] + "," + words[1] + "," + words[2]);
			}
		}

		bw.close();

	}

	private static List<String[]> readFileGetLines(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fileName));
		UnicodeReader ur = new UnicodeReader(fis, "UTF-8");

		List<String[]> lines = new ArrayList<String[]>();
		for (CSVRecord record : CSVFormat.DEFAULT.parse(ur)) {
			String[] line = Iterables.toArray(record, String.class);
			lines.add(line);
		}
		lines.get(0)[0] = lines.get(0)[0].replaceAll(UTF8_BOM, "");

		return lines;
	}

	public static Object getObjectByField(DB db, String key, String value, DBCollection collection,
			Class destinationClass) {
		Object result = null;

		QueryBuilder qb = QueryBuilder.start(key).is(value);

		BasicDBObject dbObject = (BasicDBObject) collection.findOne(qb.get());

		if (dbObject != null) {
			dbObject.remove("_id");

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.convertValue(dbObject, destinationClass);
		}

		return result;
	}
	
	public  static Map<String,List<Date>> datePartionar(String startDate,String endDate) {
	    // Define map and initialize
	    Map<String,List<Date>> res = new HashMap<>();
	    res.put("saturdays",new ArrayList<Date>());
	    
	    try {
	        // parse date and initialize calender Date
	        SimpleDateFormat dfs= new SimpleDateFormat("yyyy-MM-dd");
	        SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
	        Calendar c1 = Calendar.getInstance();
	        Calendar c2 = Calendar.getInstance();
	        c1.setTime(dfs.parse(startDate));
	        c2.setTime(dfs.parse(endDate));
	        String pstart = "";

	        while(!c1.after(c2)){
	            int dayOfWeek = c1.get(Calendar.DAY_OF_WEEK);
	            if(dayOfWeek == Calendar.SATURDAY){
	                res.get("saturdays").add(c1.getTime());
	                System.out.println("solo_al_sabato_scolastico_winter," + sd1.format(c1.getTime()) + ",1");
	            }
	            c1.add(Calendar.DATE, 1);
	        }
	    }
	    catch (ParseException pe){
	        pe.printStackTrace();
	        return  null;
	    }

	    return res;
	}

}
