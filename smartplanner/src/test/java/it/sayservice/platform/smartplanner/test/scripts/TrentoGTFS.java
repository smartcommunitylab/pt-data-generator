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

import it.sayservice.platform.smartplanner.test.scripts.RouteModel.AgencyModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.io.Files;

public class TrentoGTFS {

	private static boolean splitCalendar = false;
	private static boolean transfer = false;
	private static boolean fares = false;

	private Set<String> agencies = null;
	private Map<String, Set<String>> routeIdMap = null;
	private Set<String> ignoreRoutes = null;
	private Map<String, String> routeAgencyMap = null;
	private Map<String, String> serviceIdMap = null;
	private Map<String, String> shapeIdMap = null;
	private HashMultimap<String, String> stopIdMap = null;
	private Map<String, String> tripIdMap = null;
	private Map<String, String> exceptionalTripMap = null;

	private RouteModel routeModel;
	private File newFolder = null;
	private File oldFolder = null;
	private File outputFolder = null;

	private Map<String, GTFSModel> gtfsModels = null;

	private Map<String, String> tripIdDirectionMap = null;
	private Map<String, String> tripPropsDirectionMap = null;

	private Map<String, List<String>> agencyZoneMap = new HashMap<String, List<String>>();

	public TrentoGTFS(String pathToNewZip, String pathToOldZip, String output, String... agencies)
			throws Exception {
		super();
		
		// agencies to consider
		this.agencies = new HashSet<String>(Arrays.asList(agencies));
		// models of the agencies
		this.gtfsModels = new HashMap<String, GTFSModel>();
		for (String agency : this.agencies) {
			this.gtfsModels.put(agency, new GTFSModel());
		}
		// route model read from configuration
		this.routeModel = readRouteModel();
		// unzip the new GTFS file
		this.newFolder = unzip(pathToNewZip);
		// if old GTFS is provided, unzip it and preprocess
		if (pathToOldZip != null) {
			this.oldFolder = unzip(pathToOldZip);
			processOldDirections();
		}
		this.outputFolder = new File(output);
		// initialize mapping from the configuration
		initRouteIdMap();
		// read exceptional trips per agency in map.
		this.exceptionalTripMap = readExceptionalTrips();

	}

	public void process() throws Exception {
		// replicate agencies
		copyAgency();
		// split routes
		splitRoutes();
		// process trips
		splitTrips();
		// process calendar
		if (splitCalendar) {
			splitCalendar();
		}
		// process calendar dates
		splitCalendarDates();
		// process shapes
		splitShapes();
		// process stop times and stops
		splitStopTimes();
		// process transfers
		if (transfer) {
			splitTransfers();
		}
		// process fares.
		if (fares) {
			processFares();
		}

		// write GTFS
		for (String agency : gtfsModels.keySet()) {
			writeAgency(agency, gtfsModels.get(agency));
		}
		newFolder.deleteOnExit();
	}

	private void processFares() throws IOException {

		String fareRuleheading = "fare_id,route_id,origin_id,destination_id,contains_id";
		String fareAttributeHeading = "fare_id,price,currency_type,payment_method,transfers,transfer_duration";

		for (String agency : gtfsModels.keySet()) {
			if (agency.equalsIgnoreCase("17")) {

				File fareRuleFile = new File(
						"src/test/resources/fare/tariffegtfsextraurbano/fare_rules_extraurbano.txt");
				List<String> linesFareRules = Files.asCharSource(fareRuleFile, Charsets.UTF_8).readLines();
				List<String> zoneIds = agencyZoneMap.get(agency);
				List<String> fareIds = new ArrayList<String>();
				gtfsModels.get(agency).getFareRules().add(fareRuleheading);
				for (int i = 1; i < linesFareRules.size(); i++) {
					String line = linesFareRules.get(i);
					String[] elems = line.split(",");
					// expect line have valid zoneIds...
					if (zoneIds.contains(elems[2]) && zoneIds.contains(elems[3])) {
						fareIds.add(elems[0]);
						gtfsModels.get(agency).getFareRules().add(line.substring(0, line.lastIndexOf(",")));
					}
				}
				File fareAttributeFile = new File(
						"src/test/resources/fare/tariffegtfsextraurbano/fare_attributes_extraurbano.txt");

				gtfsModels.get(agency).getFareAttributes().add(fareAttributeHeading);
				List<String> linesFareAttributes = Files.asCharSource(fareAttributeFile, Charsets.UTF_8).readLines();
				for (int i = 1; i < linesFareAttributes.size(); i++) {
					String line = linesFareAttributes.get(i);
					String[] elems = line.split(",");
					// expect line have valid zoneIds...
					if (!fareIds.contains(elems[0])) {
						continue;
					}
					gtfsModels.get(agency).getFareAttributes().add(line);
				}
			} else if (agency.equalsIgnoreCase("12")) {
				File fareRuleFile = new File("src/test/resources/fare/12/fare_rules.txt");
				List<String> linesFareRules = Files.asCharSource(fareRuleFile, Charsets.UTF_8).readLines();
				for (int i = 0; i < linesFareRules.size(); i++) {
					String line = linesFareRules.get(i);
					gtfsModels.get(agency).getFareRules().add(line);
				}
				File fareAttributeFile = new File("src/test/resources/fare/12/fare_attributes.txt");
				List<String> linesFareAttributes = Files.asCharSource(fareAttributeFile, Charsets.UTF_8).readLines();
				for (int i = 0; i < linesFareAttributes.size(); i++) {
					String line = linesFareAttributes.get(i);
					gtfsModels.get(agency).getFareAttributes().add(line);
				}

			} else if (agency.equalsIgnoreCase("16")) {
				File fareRuleFile = new File("src/test/resources/fare/16/fare_rules.txt");
				List<String> linesFareRules = Files.asCharSource(fareRuleFile, Charsets.UTF_8).readLines();
				for (int i = 0; i < linesFareRules.size(); i++) {
					String line = linesFareRules.get(i);
					gtfsModels.get(agency).getFareRules().add(line);
				}
				File fareAttributeFile = new File("src/test/resources/fare/16/fare_attributes.txt");
				List<String> linesFareAttributes = Files.asCharSource(fareAttributeFile, Charsets.UTF_8).readLines();
				for (int i = 0; i < linesFareAttributes.size(); i++) {
					String line = linesFareAttributes.get(i);
					gtfsModels.get(agency).getFareAttributes().add(line);
				}

			}

		}

	}

	/**
	 * Create zip file for the specified agency using the {@link GTFSModel}
	 * 
	 * @param agency
	 * @param gtfsModel
	 * @throws IOException
	 */
	private void writeAgency(String agency, GTFSModel gtfsModel) throws IOException {
		File agencyDir = new File(newFolder, agency);
		agencyDir.mkdir();

		File agencyFile = new File(agencyDir, "agency.txt");
		Files.asCharSink(agencyFile, Charsets.UTF_8).writeLines(gtfsModel.getAgency());

		File calfile = null;
		if (splitCalendar) {
			calfile = new File(agencyDir, "calendar.txt");
			Files.asCharSink(calfile, Charsets.UTF_8).writeLines(gtfsModel.getCalendars());
		}

		File caldatesfile = new File(agencyDir, "calendar_dates.txt");
		Files.asCharSink(caldatesfile, Charsets.UTF_8).writeLines(gtfsModel.getCalendarDates());
		File routesfile = new File(agencyDir, "routes.txt");
		Files.asCharSink(routesfile, Charsets.UTF_8).writeLines(gtfsModel.getRoutes());
		File shapefile = new File(agencyDir, "shapes.txt");
		Files.asCharSink(shapefile, Charsets.UTF_8).writeLines(gtfsModel.getShapes());
		File stoptimesfile = new File(agencyDir, "stop_times.txt");
		Files.asCharSink(stoptimesfile, Charsets.UTF_8).writeLines(gtfsModel.getStopTimes());
		File stopsfile = new File(agencyDir, "stops.txt");
		Files.asCharSink(stopsfile, Charsets.UTF_8).writeLines(gtfsModel.getStops());
		File tripfile = new File(agencyDir, "trips.txt");
		Files.asCharSink(tripfile, Charsets.UTF_8).writeLines(gtfsModel.getTrips());
		// fare.
		//		boolean writeFareFiles = false;
		File fareRulefile = null;
		File fareAttributesfile = null;
		if (fares) {
			fareRulefile = new File(agencyDir, "fare_rules.txt");
			fareAttributesfile = new File(agencyDir, "fare_attributes.txt");
			if (!gtfsModel.getFareRules().isEmpty() && !gtfsModel.getFareAttributes().isEmpty()) {
				Files.asCharSink(fareRulefile, Charsets.UTF_8).writeLines(gtfsModel.getFareRules());
				Files.asCharSink(fareAttributesfile, Charsets.UTF_8).writeLines(gtfsModel.getFareAttributes());
				//				writeFareFiles = true;
			}
		}

		File outFile = new File(outputFolder, agency + ".zip");
		ZipOutputStream zos = null;
		try {
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(outFile);
			zos = new ZipOutputStream(fos);
			writeFileToZip(agencyFile, zos, buffer);
			if (splitCalendar) {
				writeFileToZip(calfile, zos, buffer);
			}
			writeFileToZip(caldatesfile, zos, buffer);
			writeFileToZip(routesfile, zos, buffer);
			writeFileToZip(shapefile, zos, buffer);
			writeFileToZip(stoptimesfile, zos, buffer);
			writeFileToZip(stopsfile, zos, buffer);
			writeFileToZip(tripfile, zos, buffer);
			if (fares) {
				writeFileToZip(fareRulefile, zos, buffer);
				writeFileToZip(fareAttributesfile, zos, buffer);
			}

		} finally {
			if (zos != null) {
				zos.closeEntry();
				zos.close();
			}
		}
		agencyDir.delete();
	}

	protected void writeFileToZip(File agencyFile, ZipOutputStream zos, byte[] buffer)
			throws IOException, FileNotFoundException {
		FileInputStream in = null;
		try {
			ZipEntry ze = new ZipEntry(agencyFile.getName());
			zos.putNextEntry(ze);
			in = new FileInputStream(agencyFile);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
		} finally {
			in.close();
		}
	}

	/**
	 * create direction mapping for old trips: - for each old tripId map it to
	 * the direction - for each pair routeId-headsign map it to the direction
	 * The mappings are defined with tripIdDirectionMap and
	 * tripPropsDirectionMap
	 * 
	 * @throws IOException
	 */
	private void processOldDirections() throws IOException {
		File file = new File(oldFolder, "trips.txt");
		List<String> lines = Files.asCharSource(file, Charsets.UTF_8).readLines();
		this.tripIdDirectionMap = new HashMap<String, String>();
		this.tripPropsDirectionMap = new HashMap<String, String>();

		Map<String, Integer> columns = new HashMap<String, Integer>();
		String[] headings = lines.get(0).split(",");
		for (int i = 0; i < headings.length; i++) {
			columns.put(headings[i], i);
		}

		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			// expect line have format routeId,...
			String routeId = line.substring(0, line.indexOf(','));

			String[] elems = StringUtils.commaDelimitedListToStringArray(line);
			String headsign = elems[columns.get("trip_headsign")];
			String tripId = elems[columns.get("trip_id")];
			String direction = elems[columns.get("direction_id")];
			String shape = elems[columns.get("shape_id")];

			String key = createPropertyKey(routeId, headsign, shape);
			tripIdDirectionMap.put(tripId, direction);

			if (tripPropsDirectionMap.containsKey(key) && !direction.equals(tripPropsDirectionMap.get(key))) {
				// throw new
				// IllegalArgumentException("Opposite direction for the same descriptor: "+tripId);
				continue;
			}
			tripPropsDirectionMap.put(key, direction);
		}
	}

	protected String createPropertyKey(String routeId, String headsign, String shape) {
		return routeId + "|" + headsign + "|" + shape;
	}

	protected String createPropertyKey(String routeId, String shape) {
		return routeId + "|" + shape;
	}

	/**
	 * Create stop times for different agencies using a single stop_times input.
	 * Split is based on the agency of the tripId. The stopId is replaced with
	 * <stopId>_<agency> Stops are mapped onto the agencies it is used for
	 * (stopIdMap).
	 * 
	 * @throws IOException
	 */
	private void splitStopTimes() throws IOException {
		stopIdMap = HashMultimap.create();
		List<String> lines = file2lines("stop_times.txt");
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getStopTimes().add(lines.get(0));
		}
		Map<String, Integer> columns = new HashMap<String, Integer>();
		String[] headings = lines.get(0).split(",");
		for (int i = 0; i < headings.length; i++) {
			columns.put(headings[i], i);
		}

		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] elems = StringUtils.commaDelimitedListToStringArray(line);
			String stopId = elems[columns.get("stop_id")].replace("\"", "");
			String tripId = line.substring(0, line.indexOf(',')).replace("\"", "");
			String agency = tripIdMap.get(tripId);
			if (agency == null)
				continue;
			stopIdMap.put(stopId, agency);
			StringBuilder newline = new StringBuilder();
			for (int j = 0; j < headings.length; j++) {
				if (headings[j].equals("stop_id")) {
					newline.append(stopId + "_" + agency);
				} else {
					newline.append(elems[j]);
				}
				if (j < headings.length - 1)
					newline.append(',');
			}

			gtfsModels.get(agency).getStopTimes().add(newline.toString());
		}
		splitStops();
	}

	/**
	 * Split stops to agencies. The stopIds are mapped onto the agencies that
	 * use them (see {@link #splitStopTimes()}) method. The stopId is replaced
	 * with <stopId>_<agency>
	 * 
	 * @throws IOException
	 */
	private void splitStops() throws IOException {
		List<String> lines = file2lines("stops.txt");
		List<String> wheelChairBoardingStops = routeModel.agency("12").getWheelChairBoardings();

		Map<String, Integer> columns = new HashMap<String, Integer>();
		String[] headings = lines.get(0).split(",");
		StringBuilder newheadings = new StringBuilder();
		for (int i = 0; i < headings.length; i++) {
			columns.put(headings[i], i);
			newheadings.append(headings[i]);
			newheadings.append(',');
		}
//		newheadings.append("wheelchair_boarding");
		String newheadingString = newheadings.toString();
		newheadingString = newheadingString.substring(0, newheadingString.length());
		List<String> zoneIds = new ArrayList<String>();
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getStops().add(newheadingString);
			agencyZoneMap.put(agency, zoneIds);
		}

		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String stopId = line.substring(0, line.indexOf(',')).replace("\"", "");
			StringBuilder newline = new StringBuilder();
			String[] elems = StringUtils.commaDelimitedListToStringArray(line);
			if (fares) {
				if (!zoneIds.contains(elems[6]) && stopIdMap.containsKey(elems[0])) {
					zoneIds.add(elems[6]);
				}
			}
			for (int j = 1; j < headings.length; j++) {
				newline.append(',');
				newline.append(elems[j]);
			}
			
//			if (elems[1] != null && wheelChairBoardingStops.contains(elems[1])) {
//				newline.append(",1");
//			} else {
//				newline.append(",2");
//			}
		
			String newlineString = newline.toString();
			// expect line have format shapeId,...
			if (stopIdMap.containsKey(stopId)) {
				for (String agency : stopIdMap.get(stopId)) {
					gtfsModels.get(agency).getStops().add(stopId + "_" + agency + newlineString);
				}
			}
		}
	}

	/**
	 * Transfers are copied to all agencies
	 * 
	 * @throws IOException
	 */
	private void splitTransfers() throws IOException {
		List<String> lines = file2lines("transfers.txt");
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getTransfers().add(lines.get(0));
		}
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			// expect line have format shapeId,...
			for (String agency : gtfsModels.keySet()) {
				gtfsModels.get(agency).getTransfers().add(line);
			}
		}
	}

	/**
	 * Split shape ids to different agencies. The split is based on the use of
	 * the shape by the trip Id of the agencies.
	 * 
	 * @throws IOException
	 */
	private void splitShapes() throws IOException {
		List<String> lines = file2lines("shapes.txt");
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getShapes().add(lines.get(0));
		}
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			// expect line have format shapeId,...
			String shapeId = line.substring(0, line.indexOf(','));
			String agency = shapeIdMap.get(shapeId);
			if (agency == null) {
				System.err.println("Unmapped shape " + shapeId);
				continue;
			}
			gtfsModels.get(agency).getShapes().add(line);
		}
	}

	/**
	 * Split calendar to different agencies. The split is based on the use of
	 * the service id by the trip Id of the agencies.
	 * 
	 * @throws IOException
	 */
	private void splitCalendar() throws IOException {
		List<String> lines = file2lines("calendar.txt");
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getCalendars().add(lines.get(0));
		}
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			// expect line have format serviceId,...
			String serviceId = line.substring(0, line.indexOf(','));
			String agency = serviceIdMap.get(serviceId);
			if (agency == null)
//				continue;
			gtfsModels.get(agency).getCalendars().add(line);
		}
	}

	/**
	 * Split calendar dates to different agencies. The split is based on the use
	 * of the service id by the trip Id of the agencies.
	 * 
	 * @throws IOException
	 */
	private void splitCalendarDates() throws IOException {
		List<String> lines = file2lines("calendar_dates.txt");
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getCalendarDates().add(lines.get(0));
		}
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			// expect line have format serviceId,...
			String serviceId = line.substring(0, line.indexOf(','));
			String agency = serviceIdMap.get(serviceId);
			if (agency == null)
				continue;
			gtfsModels.get(agency).getCalendarDates().add(line);
		}
	}

	/**
	 * Split trips to different agencies. The operation - populates mappings for
	 * serviceIds, shapeIds, and tripIds used by other functions. - replaces the
	 * routeId with its mapped representation from configuration - updates
	 * direction using the information from the old GTFS.
	 * 
	 * @throws IOException
	 */
	private void splitTrips() throws IOException {
		serviceIdMap = new HashMap<String, String>();
		shapeIdMap = new HashMap<String, String>();
		tripIdMap = new HashMap<String, String>();
		List<String> lines = file2lines("trips.txt");
		// add 'wheelchair_accessible'.
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getTrips().add(lines.get(0)); // + ",wheelchair_accessible"
		}

		Map<String, Integer> columns = new HashMap<String, Integer>();
		String[] headings = lines.get(0).split(",");
		for (int i = 0; i < headings.length; i++) {
			columns.put(headings[i], i);
		}
		 
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			// expect line have format routeId,...
			String routeId = line.substring(0, line.indexOf(',')).replace("\"", "");
			if (ignoreRoutes.contains(routeId))
				continue;

			String[] elems = StringUtils.commaDelimitedListToStringArray(line);
			String serviceId = elems[columns.get("service_id")];
			String shapeId = elems[columns.get("shape_id")];
			String headsign = null;
			if (columns.containsKey("trip_headsign")) {
				headsign = elems[columns.get("trip_headsign")];
			}
			String tripId = elems[columns.get("trip_id")].replace("\"", "");
			String direction = elems[columns.get("direction_id")].replace("\"", "");
			// if trip is in exceptional list, avoid taking direction_id from
			// gtfs.
			if (!exceptionalTripMap.containsKey(tripId)) {
				// update direction from the old GTFS
				if (tripIdDirectionMap != null) {
					String props = null;
					if (columns.containsKey("trip_headsign")) {
						props = createPropertyKey(routeId, headsign, shapeId);
					} else {
						props = createPropertyKey(routeId, shapeId);
					}

					if (tripIdDirectionMap.containsKey(tripId))
						direction = tripIdDirectionMap.get(tripId);
					else if (tripPropsDirectionMap.containsKey(props))
						direction = tripPropsDirectionMap.get(props);
					else {
						System.err.println("Unmapped direction for trip " + tripId);
						// throw new
						// IllegalArgumentException("Unmapped direction for trip "+tripId);
					}
				}
			} else {
				direction = exceptionalTripMap.get(tripId);
			}

			String agency = routeAgencyMap.get(routeId);
			serviceIdMap.put(serviceId, agency);
			if (StringUtils.hasText(shapeId))
				shapeIdMap.put(shapeId, agency);
			tripIdMap.put(tripId, agency);

			Set<String> mappings = routeIdMap.get(routeId);
			if (mappings == null) {
				System.err.println("No mapping for new route " + routeId);
				continue;
			}

			// identify new routeId
			String key = routeId + "_" + agency + "_" + direction;
			AgencyModel am = routeModel.agency(agency);
			String newRouteId = null;
			if (am.getRouteMappings() != null && am.getRouteMappings().containsKey(key)) {
				newRouteId = am.getRouteMappings().get(key);
			} else {
				newRouteId = key;
			}

			StringBuilder newline = new StringBuilder();
			newline.append(newRouteId);
			for (int j = 1; j < elems.length; j++) {
				newline.append(',');
				if (headings[j].equals("direction_id")) {
					newline.append(direction);
				} else {
					newline.append(elems[j]);
				}
			}
			// add wheelchair accessible.
//			if (routeModel.agency("12").getWheelChairBoardingsExceptionRoutes().contains(routeId)) {
//				newline.append(",2");	
//			} else {
//				newline.append(",1");	
//			}
			
			gtfsModels.get(agency).getTrips().add(newline.toString());
		}
	}

	/**
	 * Split routes by agencies. RouteId is replaced with the value specified in
	 * the configuration. Agency is updated
	 * 
	 * @throws IOException
	 */
	protected void splitRoutes() throws IOException {
		HashMultimap<String, String> processed = HashMultimap.create();

		List<String> lines = file2lines("routes.txt");
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getRoutes().add(lines.get(0));
		}

		Map<String, Integer> columns = new HashMap<String, Integer>();
		String[] headings = lines.get(0).split(",");
		for (int i = 0; i < headings.length; i++) {
			columns.put(headings[i], i);
		}

		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] elems = StringUtils.commaDelimitedListToStringArray(line);
			// expect line have format routeId,...
			String routeId = elems[0].replace("\"", "");
			if (ignoreRoutes.contains(routeId))
				continue;

			// String restLine = line.substring(line.indexOf(','));
			Set<String> mappings = routeIdMap.get(routeId);
			if (mappings == null) {
				System.err.println("No mapping for new route " + routeId);
				continue;
			}
				
			String agencyId = routeAgencyMap.get(routeId);
			StringBuilder newline = new StringBuilder();
			for (int j = 1; j < elems.length; j++) {
				newline.append(',');
				if (headings[j].equals("agency_id")) {
					newline.append(agencyId);
				} else {
					newline.append(elems[j]);
				}
			}
			String restline = newline.toString();

			for (String m : mappings) {
				if (processed.get(agencyId).contains(m))
					continue;
				processed.get(agencyId).add(m);
				gtfsModels.get(routeAgencyMap.get(routeId)).getRoutes().add(m + restline);
			}
		}
	}

	/**
	 * Agency file is copied as is, replacing only agency id
	 * 
	 * @throws IOException
	 */
	protected void copyAgency() throws IOException {
		// copy agency
		List<String> lines = file2lines("agency.txt");
		String rl = lines.get(1).substring(lines.get(1).indexOf(','));
		for (String agency : gtfsModels.keySet()) {
			gtfsModels.get(agency).getAgency().add(lines.get(0));
			gtfsModels.get(agency).getAgency().add(agency + rl);
		}
	}

	protected List<String> file2lines(String fname) throws IOException {
		File file = new File(newFolder, fname);
		List<String> lines = Files.asCharSource(file, Charsets.UTF_8).readLines();
		return lines;
	}

	/**
	 * Define mappings for routes: - map routeId onto the values defined in
	 * mapping (1 source may result in two: direct and return) - map routeId to
	 * agency - populate routes to ignore
	 */
	private void initRouteIdMap() {
		routeIdMap = new HashMap<String, Set<String>>();
		routeAgencyMap = new HashMap<String, String>();
		ignoreRoutes = new HashSet<String>();
		for (AgencyModel am : routeModel.getAgencies()) {
			if (!agencies.contains(am.getAgencyId()))
				continue;

			if (am.getIgnoreRoutes() != null) {
				ignoreRoutes.addAll(am.getIgnoreRoutes());
			}

			for (String route : am.getRouteIds()) {
				Set<String> set = new HashSet<String>();

				String akey = route + "_" + am.getAgencyId() + "_0";
				String amapping = am.getRouteMappings() == null ? null : am.getRouteMappings().get(akey);
				if (amapping == null && am.getRouteNames() != null && am.getRouteNames().containsKey(akey))
					amapping = akey;
				if (amapping != null) {
					set.add(amapping);
				} else {
					set.add(akey);
				}

				String rkey = route + "_" + am.getAgencyId() + "_1";
				String rmapping = am.getRouteMappings() == null ? null : am.getRouteMappings().get(rkey);
				if (rmapping == null && am.getRouteNames() != null && am.getRouteNames().containsKey(rkey))
					rmapping = rkey;
				if (rmapping != null) {
					set.add(rmapping);
				} else {
					set.add(rkey);
				}

				if (set.isEmpty())
					throw new IllegalArgumentException("No mapping for route " + route);
				routeIdMap.put(route, set);
				assert !routeAgencyMap.containsKey(route);
				routeAgencyMap.put(route, am.getAgencyId());
			}
		}
	}

	private RouteModel readRouteModel() throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().readValue(
				Thread.currentThread().getContextClassLoader().getResourceAsStream("tn-routemodel.json"),
				RouteModel.class);
	}

	private File unzip(String pathToNewZip) throws IOException {
		File dir = Files.createTempDir();

		byte[] buffer = new byte[1024];
		// get the zip file content
		ZipInputStream zis = new ZipInputStream(new FileInputStream(pathToNewZip));
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {

			String fileName = ze.getName();
			File newFile = new File(dir, fileName);
			// create all non exists folders
			// else you will hit FileNotFoundException for compressed folder
			new File(newFile.getParent()).mkdirs();

			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		return dir;
	}

	private Map<String, String> readExceptionalTrips() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> exTrips = new HashMap<String, String>();
		InputStream exceptionTripsStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("exception-trips.json");
		if (exceptionTripsStream != null) {
			exTrips = new ObjectMapper().readValue(exceptionTripsStream, new TypeReference<HashMap<String, String>>() {
			});
		}

		return exTrips;
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new BasicParser();

		Options options = new Options();

		Option option = new Option("f", "gtfs", true, "Zip file containing GTFS data");
		option.setArgName("GTFS");
		option.setRequired(true);
		options.addOption(option);

		option = new Option("a", "agencies", true, "List of agency IDs");
		option.setArgName("AGENCIES");
		option.setRequired(true);
		option.setArgs(100);
		options.addOption(option);

		option = new Option("o", "output", true, "Output directory, default current directory");
		option.setArgName("DIR");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("h", "help", false, "Print this help message");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("z", "old", true, "Old zip file with GTFS for direction references");
		option.setArgName("OLDGTFS");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("c", "calendar", true, "GTFS has calendar file");
		option.setArgName("CALENDAR");
		option.setRequired(true);
		options.addOption(option);

		option = new Option("r", "fare", true, "GTFS has fares information");
		option.setArgName("FARE");
		option.setRequired(true);
		options.addOption(option);

		option = new Option("t", "transfer", true, "GTFS has calendar file");
		option.setArgName("TRANSFER");
		option.setRequired(true);
		options.addOption(option);

		CommandLine cl = null;
		try {
			cl = parser.parse(options, args);
		} catch (Exception e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("TTGTFSSplitAlign", "Split and align the GTFS files for different agencies", options,
					"", true);
			return;
		}
		String output = cl.hasOption('o') ? cl.getOptionValue('o') : ".";

		// set static variables.
		splitCalendar = (cl.getOptionValue('c').equalsIgnoreCase("true") ? true : false);
		transfer = (cl.getOptionValue("t").equalsIgnoreCase("true") ? true : false);
		fares = (cl.getOptionValue("r").equalsIgnoreCase("true") ? true : false);

		TrentoGTFS processor = new TrentoGTFS(cl.getOptionValue('f'), cl.getOptionValue('z'), output,
				cl.getOptionValues('a'));
		processor.process();
	}
}
