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

package it.sayservice.platform.smartplanner.test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.configurations.MongoRouterMapper;
import it.sayservice.platform.smartplanner.controllers.PlannerCtrl;
import it.sayservice.platform.smartplanner.otp.OTPHandler;
import it.sayservice.platform.smartplanner.otp.OTPManager;
import it.sayservice.platform.smartplanner.otp.OTPStorage;
import it.sayservice.platform.smartplanner.otp.TransitScheduleResults;
import it.sayservice.platform.smartplanner.utils.RecurrentUtil;
import junit.framework.TestCase;

public class OTPTest extends TestCase {

	private OTPHandler handler;
	private OTPStorage storage;
	private OTPManager manager;
	private MongoRouterMapper mongoRouterMapper;
	private ConfigurationManager configurationManager;
	private PlannerCtrl planner;
	private String router = "trentino";

	@Override
	protected void setUp() throws Exception {
		// Calendar cal = new GregorianCalendar();
		// cal.setTimeInMillis(System.currentTimeMillis());
		// cal.set(Calendar.YEAR,2011);
		// cal.set(Calendar.MONTH,10);
		// cal.set(Calendar.DAY_OF_MONTH, 25);
		// System.out.println(cal.getTimeInMillis());
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// String date = sdf.format(cal.getTime());
		// System.err.println(date);
		// // System.exit(0);

		super.setUp();
		handler = new OTPHandler(router, "http://127.0.0.1:7070");
		MongoTemplate template = new MongoTemplate(new Mongo(), "trentino");
		storage = new OTPStorage(template);
		mongoRouterMapper = new MongoRouterMapper(template, router);
		
		configurationManager = new ConfigurationManager(router);
		
		manager = new OTPManager(handler, storage, mongoRouterMapper, configurationManager);
		manager.preinit(true);
		manager.init(router);

	}

	// public void _test() throws Exception {
	//// List<Parking> parkings =
	// planner.getParkingsByAgency("COMUNE_DI_TRENTO");
	// List<Parking> parkings = planner.getParkings();
	// for (Parking p: parkings) {
	// System.out.println(p.getName() + " -> " + p.getSlotsAvailable() + " -> "
	// + p.isMonitored());
	// }
	// }

	public void _testStops() throws Exception {
		// String res = manager.getStops("12", "05A");
		// String res = manager.getStops("17", "3_ExUr");
		String res = manager.getRoutes(router, "12");
		System.out.println(res);
	}

	public void _testLimitedTimeTable() throws Exception {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);

		long now = System.currentTimeMillis();
		// long now = 1365430827960L;

		// generateDelay("12", "01", "01-Feriale_046", now - 1000 * 60 * 60 *
		// 24, now + 1000 * 60 * 60 * 24, CreatorType.SERVICE);
		// generateDelay("12", "01", "01-Feriale_046", now - 1000 * 60 * 60 *
		// 24, now + 1000 * 60 * 60 * 24, CreatorType.USER);

		// Thread.sleep(10000);

		// String res = manager.getLimitedTimeTable("12", "21300x", now, 3);
		// String res = manager.getLimitedTimeTable("446_Rov", "20125p", now,
		// 3);
		String res = manager.getLimitedTimeTable(router, "12", "229", now, 3);

		// String res = manager.getLimitedTimeTable("6","Trento_6",
		// cal.getTimeInMillis(), 3);
		System.out.println(res);

	}

	public void testSchedule() throws Exception {
		System.out.println("DONE");

		// long from = 1361919600000L;
		// long to = 1362005999999L;

		// 23/2
		// long from = 1361709186142L;
		// long to = from;

		// xmas 012
		// long from = 1356446356893L - 1000 * 60 * 60 * 24 * 0;
		// long to = from;

		// midsummer
		// long from = 1376524801000L + 1000 * 60 * 60 * 24 * 0;
		// long to = 1376611199000L + 1000 * 60 * 60 * 24 * 0;

		// 11/7/13
		long from = 1373504400000L - 1000 * 60 * 60 * 24 * 7;
		long to = 1373587199000L - 1000 * 60 * 60 * 24 * 7;

		// 2011
		// long from = 1322212119691L;
		// long to = from;

		// from = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 4;
		// long from = 1365430827960L;
		// to = System.currentTimeMillis(); // - 1000 * 60 * 60 * 24;

		from = System.currentTimeMillis(); // - 1000 * 60 * 60 * 24 * 2;
		to = from + 1000 * 60 * 60 * 24;

		// long from = 1361916000895L;
		// long to = 1362005999999L;

		// System.err.println(from);

		// generateDelay("12", "01", "01-Feriale_046", from - 1000 * 60 * 60 *
		// 24, from + 1000 * 60 * 60 * 24, CreatorType.SERVICE);
		// generateDelay("12", "01", "01-Feriale_046", from - 1000 * 60 * 60 *
		// 24, from + 1000 * 60 * 60 * 24 + 1, CreatorType.USER);

		// String res2 = manager.getTransitSchedule("346_ExUr", from, to);
		// String res2 = manager.getTransitSchedule("251_ExUr", from, to);

		// from = 1370869673000L;
		// to = 1370956073000L;

		// String rid[] =
		// {"BV_R1_G","BV_R1_R","TB_R2_G","TB_R2_G","01","02","03A","03R","04A","04R","05A","05R","06A","06R","07A","07R","08A","08R","09A","09R","10A","10R","11A","11R","12A","12R","13A","13R","14A","14R","15A","15R","16A","16R","17A","17R","_A","_B","C","Da","NPA"};
		// String rid[] = {"BV_R1_G"};
		//
		// for (String id: rid) {
		// System.out.println(id);
		// String res2 = manager.getTransitSchedule(id, from, to,
		// TransitScheduleResults.DELAYS,true);
		// System.out.println(res2);
		// }

		from = getToday();
		to = from + RecurrentUtil.DAY - 1000;

		// from = 1378767840000L;
		// to = 1378847040000L;

		System.err.println(from);
		System.err.println(to);

		// String res2 = manager.getTransitSchedule("BV_R1_G", from, to,
		// TransitScheduleResults.ALL, true);
		String res2 = manager.getTransitSchedule(router, "12", "08A", from, to, TransitScheduleResults.ALL, false);
		// String res3 = manager.getTransitSchedule("01", from, to,
		// TransitScheduleResults.TIMES,false);
		// String res4 = manager.getTransitSchedule("01", from, to,
		// TransitScheduleResults.DELAYS,false);

		System.out.println(res2);
		// System.out.println("___________________________________");
		// System.out.println(res3);
		// System.out.println("___________________________________");
		// System.out.println(res4);

		// System.err.println(System.currentTimeMillis());
		// System.err.println(System.currentTimeMillis() + 1000 * 60 * 30);
	}

	private long getToday() throws ParseException {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTimeInMillis();
	}

	public void _testTrainSchedule() throws Exception {
		long from = 1361919600000L;
		long to = 1362005999999L;

		String trainBVG = manager.getTransitSchedule(router, "5", "BV_R1_G", from, to, TransitScheduleResults.ALL,
				true);

		System.out.println(trainBVG);

		String trainTBG = manager.getTransitSchedule(router, "6", "TB_R2_G", from, to, TransitScheduleResults.ALL,
				true);

		System.out.println(trainTBG);

		String trainTBR = manager.getTransitSchedule(router, "6", "TB_R2_R", from, to, TransitScheduleResults.ALL,
				true);

		System.out.println(trainTBR);

		String busTrento = manager.getTransitSchedule(router, "12", "02", from, to, TransitScheduleResults.ALL, true);

		System.out.println(busTrento);
	}

	// private void testDelay() {
	// generateDelay("12", "01", "01-Feriale_040", 1365430827960, 1365430827960
	// + 1000 * 60 * 60);
	// }

	// private void generateDelay(String agencyId, String routeId, String
	// tripId, long from, long to, CreatorType type) throws ParseException {
	// PlannerController planner = new PlannerController("config.properties");
	// SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mmaa",
	// Locale.ITALY);
	// AlertDelay alertD = new AlertDelay();
	// Transport tp1 = new Transport(TType.BUS, agencyId, routeId, routeId,
	// tripId);
	// alertD.setTransport(tp1);
	// alertD.setCreatorType(type);
	// alertD.setDelay(1 * 60 * 1000); // 5mins.
	//
	// alertD.setFrom(from);
	// alertD.setTo(to);
	//
	// alertD.setId(tp1.getTripId() + "_" + from + "_" + to);
	// System.err.println(alertD.getId());
	// planner.updateAD(alertD);
	//
	// }

}
