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

package it.sayservice.platform.smartplanner.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class CSVGTFSGenerator {

	// trip_id,arrival_time,departure_time,stop_id,stop_sequence
	private static final String HEADING = "trip_id,arrival_time,departure_time,stop_id,stop_sequence";
	private static final String TEMPLATE = "%1$s,%2$s:00,%3$s:00,%4$s,%5$s";
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	public void processFiles(String outputDir, String agency, String... files) throws Exception {
		List<String> converted = new ArrayList<String>();
		converted.add(HEADING);
		for (String filename : files) {
			File file = new File(filename);
			List<String> lines = Files.asCharSource(file, Charsets.UTF_8).readLines();
			converted.addAll(convertLines(lines));
		}
		File outputDirFile = new File(outputDir);
		File stopTimesFile = new File(outputDirFile, "stop_times.txt");
		Files.asCharSink(stopTimesFile, Charsets.UTF_8).writeLines(converted);

		File agencyFile = new File(outputDirFile, "agency.txt");
		File calfile = new File(outputDirFile, "calendar.txt");
		File caldatesfile = new File(outputDirFile, "calendar_dates.txt");
		File routesfile = new File(outputDirFile, "routes.txt");
		File shapefile = new File(outputDirFile, "shapes.txt");
		File stoptimesfile = new File(outputDirFile, "stop_times.txt");
		File stopsfile = new File(outputDirFile, "stops.txt");
		File tripfile = new File(outputDirFile, "trips.txt");
		File fareRule = new File(outputDirFile, "fare_rules.txt");
		File fareAttrb = new File(outputDirFile, "fare_attributes.txt");

		File outFile = new File(outputDirFile, agency + ".zip");
		ZipOutputStream zos = null;
		try {
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(outFile);
			zos = new ZipOutputStream(fos);
			writeFileToZip(agencyFile, zos, buffer);
			writeFileToZip(calfile, zos, buffer);
			writeFileToZip(caldatesfile, zos, buffer);
			writeFileToZip(routesfile, zos, buffer);
			writeFileToZip(shapefile, zos, buffer);
			writeFileToZip(stoptimesfile, zos, buffer);
			writeFileToZip(stopsfile, zos, buffer);
			writeFileToZip(tripfile, zos, buffer);
			if (fareRule.exists())
				writeFileToZip(fareRule, zos, buffer);
			if (fareAttrb.exists())
				writeFileToZip(fareAttrb, zos, buffer);
		} finally {
			if (zos != null) {
				zos.closeEntry();
				zos.close();
			}
		}

	}

	protected void writeFileToZip(File file, ZipOutputStream zos, byte[] buffer)
			throws IOException, FileNotFoundException {
		FileInputStream in = null;
		try {
			ZipEntry ze = new ZipEntry(file.getName());
			zos.putNextEntry(ze);
			in = new FileInputStream(file);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
		} finally {
			if (in != null)
				in.close();
		}
	}

	private List<String> convertLines(List<String> lines) throws Exception {
		List<String> converted = new ArrayList<String>();
		String[][] table = new String[lines.size()][];
		for (int i = 0; i < lines.size(); i++) {
			table[i] = StringUtils.commaDelimitedListToStringArray(lines.get(i));
		}
		String[] headings = table[0];
		for (int i = 1; i < headings.length; i++) {
			int counter = 1;
			String nextStation = null;
			for (int j = 1; j < table.length; j++) {
				if (!StringUtils.hasText(table[j][i]))
					continue;

				String station = table[j][0];
				String arrival = formatTime(table[j][i]);
				String departure = arrival;
				if (j < lines.size() - 1) {
					nextStation = StringUtils.hasText(table[j + 1][i]) ? table[j + 1][0] : null;
				} else {
					nextStation = null;
				}

				if (station.equals(nextStation)) {
					departure = formatTime(table[j + 1][i]);
					j++;
				}
				String line = String.format(TEMPLATE, headings[i], arrival, departure, station, counter++);
				converted.add(line);
			}
		}

		return converted;
	}

	private String formatTime(String string) throws ParseException {
		return TIME_FORMAT.format(TIME_FORMAT.parse(string));
	}

	public static void main(String[] args) {
		try {

			if (args[0].equalsIgnoreCase("5")) {
				// BRENNERO.
				new CSVGTFSGenerator().processFiles("5", "5",
						"5/ORARI_BRENERRO_ANDATA.csv",
						"5/ORARI_BRENERRO_RITORNO.csv");
			} else if (args[0].equalsIgnoreCase("6")) {
				// BASSANO.
				new CSVGTFSGenerator().processFiles("6", "6",
						"6/ORARI_VALSUGANA_ANDATA.csv",
						"6/ORARI_VALSUGANA_RITORNO.csv");
			} else if (args[0].equalsIgnoreCase("10")) {
				// FTM.
				new CSVGTFSGenerator().processFiles("10", "10",
						"10/ORARI_FTM_ANDATA.csv",
						"10/ORARI_FTM_RITORNO.csv");
			} else {
				System.out.println("first argument must be 5, 6 or 10");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
