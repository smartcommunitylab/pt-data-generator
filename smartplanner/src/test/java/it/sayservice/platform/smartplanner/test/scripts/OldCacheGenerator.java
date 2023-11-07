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

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import com.mongodb.Mongo;

import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.configurations.MongoRouterMapper;
import it.sayservice.platform.smartplanner.data.message.otpbeans.Route;
import it.sayservice.platform.smartplanner.data.message.otpbeans.TransitTimeTable;
import it.sayservice.platform.smartplanner.otp.OTPHandler;
import it.sayservice.platform.smartplanner.otp.OTPManager;
import it.sayservice.platform.smartplanner.otp.OTPStorage;
import it.sayservice.platform.smartplanner.otp.TransitScheduleResults;
import it.sayservice.platform.smartplanner.otp.schedule.WeekdayException;
import it.sayservice.platform.smartplanner.otp.schedule.WeekdayFilter;
import it.sayservice.platform.smartplanner.utils.RecurrentUtil;
import junit.framework.TestCase;

public class OldCacheGenerator extends TestCase {

	private OTPHandler handler;
	private OTPStorage storage;
	private OTPManager manager;
	private MongoRouterMapper mongoRouterMapper;
	private ConfigurationManager configurationManager;
	private String router = "trentino";

	@Override
	protected void setUp() throws Exception {

		// System.out.println(System.currentTimeMillis() - 1000 * 60 * 60 * 3);
		// System.out.println(System.currentTimeMillis() + 1000 * 60 * 60 * 3);
		// System.out.println(System.currentTimeMillis());
		// System.out.println(System.currentTimeMillis() + RecurrentUtil.DAY -
		// 1000 * 60);
		// System.exit(0);

		super.setUp();
		handler = new OTPHandler(router, "http://127.0.0.1:7070");
		MongoTemplate template = new MongoTemplate(new Mongo(), "trentino");
		storage = new OTPStorage(template);
		mongoRouterMapper = new MongoRouterMapper(template, router);
		
		configurationManager = new ConfigurationManager(router);
		
		manager = new OTPManager(handler, storage, mongoRouterMapper, configurationManager);
		
		// planner = new Planner();
		manager.init(router);

	}

	public void test() throws Exception {
		buildCache("5", true);
		buildCache("6", true);
		buildCache("10", false);
		buildCache("12", false);
		buildCache("16", false);
		buildCache("17", false);

		//
		// String rid6[] = {"TB_R2_G","TB_R2_R"};
		// buildCache("6", rid6, true);

		// String rid10[] = {"555","556"};
		// buildCache("10", rid10, false);

		// String rid12[] =
		// {"01","02","03A","03R","04A","04R","05A","05R","06A","06R","07A","07R","08A","08R","09A","09R","10A","10R","11A","11R","12A","12R","13A","13R","14A","14R","15A","15R","16A","16R","17A","17R","_A","_B","C","Da","NPA"};
		// buildCache("12", rid12,false);
		//
		// String rid16[] =
		// {"346_Rov","347_Rov","376_Rov","377_Rov","379_Rov","380_Rov","383_Rov","384_Rov","385_Rov","386_Rov","389_Rov","390_Rov","391_Rov","392_Rov","445_Rov","446_Rov","447_Rov","448_Rov","449_Rov","451_Rov","452_Rov","453_Rov","456_Rov","457_Rov","470_Rov","471_Rov","481_Rov","482_Rov","488_Rov"};
		// buildCache("16", rid16,false);
		//
		// String rid17[] =
		// {"335_ExUr","168_ExUr","329_ExUr","321_ExUr","151_ExUr","636_ExUr","107_ExUr","232_ExUr","379_ExUr","216_ExUr","71_ExUr","55_ExUr","384_ExUr","387_ExUr","62_ExUr","248_ExUr","205_ExUr","343_ExUr","520_ExUr","300_ExUr","153_ExUr","231_ExUr","159_ExUr","551_ExUr","637_ExUr","640_ExUr","367_ExUr","247_ExUr","325_ExUr","314_ExUr","306_ExUr","638_ExUr","290_ExUr","420_ExUr","342_ExUr","418_ExUr","240_ExUr","555_ExUr","201_ExUr","90_ExUr","215_ExUr","102_ExUr","123_ExUr","336_ExUr","179_ExUr","8_ExUr","561_ExUr","189_ExUr","67_ExUr","252_ExUr","611_ExUr","439_ExUr","251_ExUr","154_ExUr","181_ExUr","581_ExUr","368_ExUr","233_ExUr","286_ExUr","6_ExUr","358_ExUr","381_ExUr","175_ExUr","155_ExUr","253_ExUr","330_ExUr","549_ExUr","229_ExUr","556_ExUr","108_ExUr","241_ExUr","350_ExUr","346_ExUr","187_ExUr","356_ExUr","503_ExUr","309_ExUr","307_ExUr","110_ExUr","188_ExUr","196_ExUr","417_ExUr","80_ExUr","203_ExUr","639_ExUr","395_ExUr","386_ExUr","184_ExUr","619_ExUr","375_ExUr","332_ExUr","68_ExUr","242_ExUr","81_ExUr","376_ExUr","169_ExUr","550_ExUr","382_ExUr","366_ExUr","423_ExUr","287_ExUr","563_ExUr","200_ExUr","72_ExUr","362_ExUr","84_ExUr","103_ExUr","565_ExUr","160_ExUr","185_ExUr","326_ExUr","190_ExUr","156_ExUr","391_ExUr","327_ExUr","507_ExUr","580_ExUr","331_ExUr","186_ExUr","157_ExUr","237_ExUr","245_ExUr","178_ExUr","117_ExUr","194_ExUr","170_ExUr","349_ExUr","234_ExUr","74_ExUr","230_ExUr","566_ExUr","250_ExUr","204_ExUr","320_ExUr","315_ExUr","357_ExUr","288_ExUr","494_ExUr","118_ExUr","63_ExUr","87_ExUr","560_ExUr","324_ExUr","152_ExUr","64_ExUr","134_ExUr","86_ExUr","383_ExUr","289_ExUr","345_ExUr","354_ExUr","523_ExUr","583_ExUr","124_ExUr","365_ExUr","158_ExUr","618_ExUr","328_ExUr","369_ExUr","333_ExUr","305_ExUr","121_ExUr","562_ExUr","522_ExUr","65_ExUr","378_ExUr","238_ExUr","79_ExUr","133_ExUr","249_ExUr","191_ExUr","198_ExUr","363_ExUr","552_ExUr","78_ExUr","291_ExUr","195_ExUr","180_ExUr","243_ExUr","182_ExUr","364_ExUr","396_ExUr","109_ExUr","132_ExUr","177_ExUr","635_ExUr","340_ExUr","167_ExUr","557_ExUr","85_ExUr","554_ExUr","120_ExUr","521_ExUr","553_ExUr","69_ExUr","3_ExUr","7_ExUr","106_ExUr","244_ExUr","56_ExUr","99_ExUr","495_ExUr","613_ExUr","199_ExUr","70_ExUr","66_ExUr","98_ExUr","111_ExUr","392_ExUr","60_ExUr","239_ExUr","334_ExUr","344_ExUr","337_ExUr","91_ExUr","634_ExUr","564_ExUr","176_ExUr","73_ExUr","197_ExUr","135_ExUr"};
		// buildCache("17", rid17,false);

	}

	public void buildCache(String agencyId, boolean tripsIds) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String d = "C:/tmp/results/" + agencyId;
		File dir = new File(d);
		if (!dir.exists()) {
			dir.mkdir();
		}

		Map<String, WeekdayFilter> weekdayFilter = handler.readAgencyWeekDay(router, agencyId);
		Map<String, WeekdayException> weekdayException = handler.readAgencyWeekDayExceptions(router, agencyId);

		Multimap<String, String> daysMap = ArrayListMultimap.create();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		for (String eq : weekdayFilter.keySet()) {
			// System.err.println(eq);
			WeekdayFilter filter = weekdayFilter.get(eq);
			String from = filter.getFromDate();
			String to = filter.getToDate();
			// System.out.println(eq + " = " + from + " - > " + to);

			Calendar fromDate = new GregorianCalendar();
			Calendar toDate = new GregorianCalendar();

			fromDate.setTime(df.parse(from));
			toDate.setTime(df.parse(to));
			Calendar date = new GregorianCalendar();
			date.setTime(fromDate.getTime());
			String prevDay = null;
			while (date.compareTo(toDate) <= 0) {
				String day = df.format(date.getTime());

				boolean sameDay = day.equals(prevDay);

				if (!sameDay) {
					int dotw = convertDayOfTheWeek(date.get(Calendar.DAY_OF_WEEK));
					if (filter.getDays()[dotw]) {
						daysMap.put(day, eq);
					}
				}
				prevDay = day;
				date.setTime(new Date(date.getTime().getTime() + (RecurrentUtil.DAY)));
			}

		}

		for (String key : weekdayException.keySet()) {
			WeekdayException ex = weekdayException.get(key);
			for (String toAdd : ex.getAdded()) {
				daysMap.put(toAdd, key);
			}
			for (String toRemove : ex.getRemoved()) {
				daysMap.remove(toRemove, key);
			}
		}

		// for (String day: daysMap.keySet()) {
		// System.out.println(day + " -> " + daysMap.get(day));
		// }

		Multimap<String, String> reversedDaysMap = ArrayListMultimap.create();
		for (String day : daysMap.keySet()) {
			// System.out.println(day + " -> " + daysMap.get(day));
			String dayKey = getEqString(daysMap.get(day).toString(), agencyId);
			reversedDaysMap.put(dayKey, day);
		}

		// System.out.println(reversedDaysMap.keySet().size());
		// System.exit(0);

		SortedSet<String> calendarData = new TreeSet<String>();
		for (String key : reversedDaysMap.keySet()) {
			// System.out.println(day + " -> " + reversedDaysMap.get(day));
			for (String day : reversedDaysMap.get(key)) {
				calendarData.add("\"" + day + "\":\"" + key + "\"");
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for (String line : calendarData) {
			sb.append(line + ",\n");
		}
		sb.replace(sb.length() - 2, sb.length() - 1, "");
		sb.append("};");

		FileWriter fw = new FileWriter(d + "/calendar.js");
		fw.write(sb.toString());
		fw.close();

		for (String key : reversedDaysMap.keySet()) {
			String randomDay = (String) ((List) reversedDaysMap.get(key)).get(0);
			// System.err.println(key + " -> " + randomDay);

			Calendar randomDate = new GregorianCalendar();

			randomDate.setTime(df.parse(randomDay));

			long from = randomDate.getTimeInMillis();
			long to = from + RecurrentUtil.DAY - 1000 * 60;

			List<Route> allRoutes = handler.getRoutes(router);

			for (Route route : allRoutes) {
				String id = null;
				if (route.getId().getAgency().equals(agencyId)) {
					id = route.getId().getId();
				} else {
					continue;
				}
				String res2 = manager.getTransitSchedule(router, agencyId, id, from, to, TransitScheduleResults.TIMES,
						tripsIds);

				TransitTimeTable ttt = mapper.readValue(res2, TransitTimeTable.class);
				fw = new FileWriter(d + "/" + id + "_" + key + ".js");
				if (ttt.getTimes().get(0).size() != 0) {
					fw.write(res2);
				}
				fw.close();
			}

		}

		System.out.println("DONE " + agencyId);
	}

	private int convertDayOfTheWeek(int day) {
		int conv = day - 2;
		if (conv < 0) {
			conv = 6;
		}
		return conv;
	}

	private String getEqString(String eqs, String agencyId) {
		String eq = eqs;
		eq = eq.replaceAll(agencyId, "").replaceAll("[_ ]", "").replaceAll(",", ";").replaceAll("[\\[\\]]", "");
		// eq = eq.replace(agencyId, "");
		// eq = agencyId + "_" + eq;
		// System.err.println(eqs + " -> " + eq);
		return Hashing.sha1().hashString(eq, Charset.forName("UTF-8")).toString();
		// return eq;

	}

}
