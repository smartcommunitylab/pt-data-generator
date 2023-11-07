package it.sayservice.platform.smartplanner.test.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.collect.Iterables;
import com.google.gdata.util.io.base.UnicodeReader;

public class FareGenerator {

	private static String gtfsLocation = "C:/tmp/otp/gen/05.06.17/16/";
	private static List<String> zoneIds = new ArrayList<String>();
	private static final String UTF8_BOM = "\uFEFF";

	public static void main(String args[]) throws IOException {
		FareGenerator fareGenerator = new FareGenerator();
		// read stop file, know all zone ids.
		String stopFile = gtfsLocation + System.getProperty("file.separator") + "stops.txt";
		List<String[]> linesStop = fareGenerator.readFileGetLines(stopFile);
		for (int i = 1; i < linesStop.size(); i++) {
			if (!zoneIds.contains(linesStop.get(i)[6])) {
				zoneIds.add(linesStop.get(i)[6]);
			}
		}
		// read fare_attr file and learn all attributes.
		for (int i=0; i < zoneIds.size(); i++) {
			for (String zoneId: zoneIds) {
				System.out.println("URBAN_ROVERETO_70MINS,," + zoneIds.get(i)  + "," + zoneId + ",");
				System.out.println("URBAN_ROVERETO_180MINS,," + zoneIds.get(i)  + "," + zoneId + ",");
				System.out.println("URBAN_ROVERETO_GIORNALIERO,," + zoneIds.get(i)  + "," + zoneId + ",");
			}
			System.out.print("\n\n");
		}

		// for each zone id create attribute for same zone and for all other
		// zone.
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

}
