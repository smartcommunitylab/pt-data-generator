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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.gdata.util.io.base.UnicodeReader;

import it.sayservice.platform.smartplanner.utils.Location;
import it.sayservice.platform.smartplanner.utils.PolylineEncoder;

/**
 * Utility class for CRUD shape files.
 * 
 * @author nawazk
 * 
 */
public class ShapeCreator {

	private static String gtfsLocation = "C:\\tmp\\otp\\gen\\10";
	private static HashMap<String, List<String[]>> stopTimesTxt = new HashMap<String, List<String[]>>();
	private static HashMap<String, String> stopsTxt = new HashMap<String, String>();
	private static HashMap<String, List<String[]>> shapesText = new HashMap<String, List<String[]>>();
	private static final String UTF8_BOM = "\uFEFF";
	private static HashMap<String, String> tripIdShapeIdMap = new HashMap<String, String>();
	private static String[] gtfs = { "12", "16", "10", "5", "6", "17" };
	// private static Planner planner = new Planner("config.properties");
	// private static SimpleDateFormat formatter = new
	// SimpleDateFormat("MM/dd/yyyy", Locale.ITALY);
	// private static Calendar calendar = Calendar.getInstance();
	// private static String date = formatter.format(calendar.getTime());
	// private static GeocodeAPIsManager apiManager = new GeocodeAPIsManager();

	/**
	 * constructor.
	 */
	public ShapeCreator() {
		//init();
	}

	private void init() {
		try {
			// stopTimes.
			String stoptimesTFile = gtfsLocation + System.getProperty("file.separator") + "stop_times.txt";
			List<String[]> linesST = readFileGetLines(stoptimesTFile);

			for (int i = 0; i < linesST.size(); i++) {
				List<String[]> list = stopTimesTxt.get(linesST.get(i)[0]);
				if (list == null) {
					list = new ArrayList<String[]>();
					stopTimesTxt.put(linesST.get(i)[0], list);
				}
				list.add(linesST.get(i));
			}
			// stops.
			String stopFile = gtfsLocation + System.getProperty("file.separator") + "stops.txt";
			List<String[]> linesStop = readFileGetLines(stopFile);
			for (int i = 0; i < linesStop.size(); i++) {
				stopsTxt.put(linesStop.get(i)[0], linesStop.get(i)[3] + "," + linesStop.get(i)[4]);

			}

			// stops.
			String shapeFile = gtfsLocation + System.getProperty("file.separator") + "shapes.txt";
			List<String[]> linesSSeq = readFileGetLines(shapeFile);
			for (int i = 0; i < linesSSeq.size(); i++) {
				List<String[]> list = shapesText.get(linesSSeq.get(i)[0]);
				if (list == null) {
					list = new ArrayList<String[]>();
					shapesText.put(linesSSeq.get(i)[0], list);
				}
				list.add(linesSSeq.get(i));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String[]> readFileGetLines(String fileName) throws IOException {
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

	/**
	 * Main.
	 */
	public static void main(final String args[]) {
		try {

			// boolean generateShapeFromPolyLine = false;
			// boolean generateShapeFromTrip = false;
			boolean fixTripWithFixedShape = false;
			boolean generatePolyFromExistingShape = false;
			boolean generateShapeFileFromPoints = true;
			boolean generateShapeFileFromPointsInReverseOrder = false;
			boolean countShapeIds = false;
		
			if (countShapeIds) {
				String pathToGTFSZip = "C:/tools-sw-fw/otp-refactored/trentino/cache/schedules";
				for (String gtfsName : gtfs) {
					File shapes = new File(pathToGTFSZip + System.getProperty("file.separator") + gtfsName, "shapes.txt");
					List<String> linesShapes = Files.asCharSource(shapes, Charsets.UTF_8).readLines();
					List <String> shapeList = new ArrayList<String>();
					for (int i=1; i < linesShapes.size(); i++) {
						try {
							String[] elems = linesShapes.get(i).split(",");
							if (elems != null && !shapeList.contains(elems[0])) {
								shapeList.add(elems[0]);
							}	
						}catch (ArrayIndexOutOfBoundsException e) {
							System.err.println(e);
						}
						
					}
					
					System.err.println("Agency " + gtfsName + " contains " + (shapeList.size() - 1) + " shapeIds");
					for (String shpId: shapeList) {
						System.out.println(shpId);
					}
					
				}
				
			}
			
			ShapeCreator shapeCreator = new ShapeCreator();
			// List<Location> locations = new ArrayList<Location>();

			if (generateShapeFileFromPointsInReverseOrder) {
				String shapeId = "21102019FUNBUSANDATA";
				// stopTimes.
				String coordinatesFile = "src\\test\\resources\\shapes\\coordinates.txt";
				int seq = 0;
				List<String[]> linesCoordinates = shapeCreator.readFileGetLines(coordinatesFile);
				for (int i = linesCoordinates.size() - 1; i > -1; i--) {
					System.out.println(shapeId + "," + linesCoordinates.get(i)[0] + "," + linesCoordinates.get(i)[1]
							+ "," + ++seq);
				}
			}
			
			if (generateShapeFileFromPoints) {
				String shapeId = "21102019FUNBUSANDATA";
				// stopTimes.
				String coordinatesFile = "src\\test\\resources\\shapes\\coordinates.txt";
				int seq = 0;
				List<String[]> linesCoordinates = shapeCreator.readFileGetLines(coordinatesFile);
				for (int i = 0; i < linesCoordinates.size(); i++) {
					System.out.println(shapeId + "," + linesCoordinates.get(i)[0] + "," + linesCoordinates.get(i)[1]
							+ "," + ++seq);
				}
			}

			if (generatePolyFromExistingShape) {
				shapeCreator.init();
				List<Location> shapeLegLocation = new ArrayList<Location>();
				String shapeId = "trento_male";
				// take shape sequence from shapeMap.
				List<String[]> listShapeSequences = shapesText.get(shapeId);
				if (listShapeSequences == null || listShapeSequences.isEmpty()) {
					System.err.println(shapeId + ": not present");
				} else {
					for (int i = 0; i < listShapeSequences.size(); i++) {
						// already ordered.
						String[] currentShapeLeg = listShapeSequences.get(i);
						String lat = currentShapeLeg[1];
						String lon = currentShapeLeg[2];
						System.out.println(lat + "," + lon);
						// prepare list of Location.
						shapeLegLocation.add(new Location(Double.valueOf(lat), Double.valueOf(lon)));
					}
				}
				// generate polyline.
				System.err.print(PolylineEncoder.encode(shapeLegLocation));
			}

			if (fixTripWithFixedShape) {
				// initialize map of trip->correctedShapeId.
				String fixedTripFile = "src\\test\\resources\\shapes\\fixed-trips-shapes.txt";
				List<String[]> linesFixedTS = shapeCreator.readFileGetLines(fixedTripFile);
				for (int i = 0; i < linesFixedTS.size(); i++) {
					tripIdShapeIdMap.put(linesFixedTS.get(i)[2], linesFixedTS.get(i)[5]);
				}
				// read trips.txt.
				String tripFile = gtfsLocation + System.getProperty("file.separator") + "trips.txt";
				List<String[]> linesTrips = shapeCreator.readFileGetLines(tripFile);
				for (int i = 0; i < linesTrips.size(); i++) {
					String fixedShapeId = tripIdShapeIdMap.get(linesTrips.get(i)[2]);
					if (fixedShapeId == null || fixedShapeId.isEmpty()) {
						fixedShapeId = linesTrips.get(i)[5];
					}
					System.out.println(linesTrips.get(i)[0] + "," + linesTrips.get(i)[1] + "," + linesTrips.get(i)[2]
							+ "," + linesTrips.get(i)[3] + "," + linesTrips.get(i)[4] + "," + fixedShapeId);
				}

			}

			/**
			 * // generate shape from polyline. if (generateShapeFromPolyLine) {
			 * String polyline = ""; int seq = 0;
			 * locations.addAll(shapeCreator.decodePoly(polyline)); // write
			 * shapes.Txt file. for (Location shapePoint : locations) { //
			 * shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence
			 * System.out.println("tripId" + "CustomShape" + "," +
			 * shapePoint.getLatitude() + "," + shapePoint.getLongitude() + ","
			 * + ++seq); }
			 * 
			 * }
			 */
			/**
			 * // generate shape from Trip(StopTimes). if
			 * (generateShapeFromTrip){ // read tripId. String tripId =
			 * "0002601282015020220150609";
			 * 
			 * // get stopTimes for trip. List<String[]> list =
			 * stopTimesTxt.get(tripId); if (list == null || list.isEmpty()) {
			 * System.err.println(tripId + ": not present"); } else { for (int i
			 * = 0; i < list.size(); i++) { // already ordered. String[]
			 * currentscheduleLeg = list.get(i); if (i + 1 >= list.size()) {
			 * break; } String[] nextScheduleLeg = list.get(i + 1);
			 * 
			 * // getnearbyStreet. String[] startPoint =
			 * stopsTxt.get(currentscheduleLeg[3]).split(","); // Position
			 * startPosition = //
			 * shapeCreator.queryLocation(Double.valueOf(startPoint[0]), //
			 * Double.valueOf(startPoint[1])); String[] endPoint =
			 * stopsTxt.get(nextScheduleLeg[3]).split(","); // Position
			 * endPosition = //
			 * shapeCreator.queryLocation(Double.valueOf(endPoint[0]), //
			 * Double.valueOf(endPoint[1]));
			 * 
			 * String source = stopsTxt.get(currentscheduleLeg[3]); // if
			 * (startPosition != null && startPosition.toLatLon() // != null //
			 * && !startPosition.toLatLon().isEmpty()) { // source =
			 * startPosition.toLatLon(); // } String destination =
			 * stopsTxt.get(nextScheduleLeg[3]); // if (endPosition != null &&
			 * endPosition.toLatLon() != // null // &&
			 * !endPosition.toLatLon().isEmpty()) { // destination =
			 * endPosition.toLatLon(); // } // invoke OTP (Get 'car' mode
			 * polyLine) java.util.List<Itinerary> its =
			 * planner.planTrip(source, destination, date, null, "2:25am",
			 * TType.CAR, RType.fastest, null); if (its != null &&
			 * !its.isEmpty()) { String polyLine =
			 * its.get(0).getLeg().get(0).getLegGeometery().getPoints(); if
			 * (polyLine != null && !polyLine.isEmpty()) {
			 * locations.addAll(shapeCreator.decodePoly(polyLine)); } } else {
			 * // String[] coordinatesStartPoint = // startPoint.split(","); //
			 * String[] coordinatesEndPoint = // endPoint.split(",");
			 * locations.add(new Location(Double.valueOf(startPoint[0]),
			 * Double.valueOf(startPoint[1]))); locations.add(new
			 * Location(Double.valueOf(endPoint[0]),
			 * Double.valueOf(endPoint[1]))); } }
			 * 
			 * }
			 * 
			 * int sequence = 0; // write shapes.Txt file. for (Location
			 * shapePoint : locations) { //
			 * shape_id,shape_pt_lat,shape_pt_lon,shape_pt_sequence
			 * System.out.println(tripId + "CustomShape" + "," +
			 * shapePoint.getLatitude() + "," + shapePoint.getLongitude() + ","
			 * + ++sequence); }
			 * 
			 * System.out.println(PolylineEncoder.encode(locations));
			 * 
			 * }
			 **/

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printShape() {
		try {
			// Open the file that is the first command line parameter
			FileInputStream fstream = new FileInputStream("src/main/resources/shapes.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int index = 438;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// create tokens
				String[] elements = strLine.split(",");
				System.out.println(elements[0] + "," + elements[1] + "," + elements[2] + "," + index);
				index--;
			}
			// Close the input stream
			in.close();
		} catch (Exception e) { // Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Utility method for decoding polyline
	 * 
	 * @param encoded
	 * @return
	 */
	public ArrayList<Location> decodePoly(String encoded) {

		ArrayList<Location> poly = new ArrayList<Location>();

		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;
			Location p = new Location((((double) lat / 1E5)), (((double) lng / 1E5)));
			poly.add(p);
		}
		return poly;
	}

	/**
	 * Encode a polyline with Google polyline encoding method
	 * 
	 * @param polyline
	 *            the polyline
	 * @param precision
	 *            1 for a 6 digits encoding, 10 for a 5 digits encoding.
	 * @return the encoded polyline, as a String
	 */
	public String encode(List<Location> polyline, int precision) {
		StringBuffer encodedPoints = new StringBuffer();
		int prev_lat = 0, prev_lng = 0;
		for (Location trackpoint : polyline) {
			int lat = (int) (trackpoint.getLatitude() / precision);
			int lng = (int) (trackpoint.getLongitude() / precision);
			encodedPoints.append(encodeSignedNumber(lat - prev_lat));
			encodedPoints.append(encodeSignedNumber(lng - prev_lng));
			prev_lat = lat;
			prev_lng = lng;
		}
		return encodedPoints.toString();
	}

	private static StringBuffer encodeSignedNumber(int num) {
		int sgn_num = num << 1;
		if (num < 0) {
			sgn_num = ~(sgn_num);
		}
		return (encodeNumber(sgn_num));
	}

	private static StringBuffer encodeNumber(int num) {
		StringBuffer encodeString = new StringBuffer();
		while (num >= 0x20) {
			int nextValue = (0x20 | (num & 0x1f)) + 63;
			encodeString.append((char) (nextValue));
			num >>= 5;
		}
		num += 63;
		encodeString.append((char) (num));
		return encodeString;
	}
	/**
	 * private Position queryLocation(double lat, double lon) { String
	 * requestAPI = "location=" + lat + "," + lon +
	 * "&radius=50&sensor=false&types=route"; Position suggestedPosition =
	 * planner.geocodeAPIsManager.nearbySearchGoogle(requestAPI, null,
	 * MediaType.APPLICATION_JSON); // save suggested position in StreeLocation
	 * db. if (suggestedPosition != null) { StreetLocation temp = new
	 * StreetLocation(suggestedPosition.getStopId().getId(),
	 * suggestedPosition.getStopCode(), suggestedPosition.getName(), lat, lon,
	 * Double.valueOf(suggestedPosition.getLat()),
	 * Double.valueOf(suggestedPosition.getLon()));
	 * planner.getRepoM().getStreetLocRepo().save(temp); return
	 * suggestedPosition; } return null; }
	 **/

}
