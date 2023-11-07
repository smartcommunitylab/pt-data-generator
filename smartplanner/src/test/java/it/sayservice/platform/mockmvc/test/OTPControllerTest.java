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

package it.sayservice.platform.mockmvc.test;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.otp.OTPHandler;
import it.sayservice.platform.smartplanner.otp.OTPManager;
import it.sayservice.platform.smartplanner.otp.TransitScheduleResults;
import it.sayservice.platform.smartplanner.utils.RecurrentUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebAppContext.class })
@WebAppConfiguration
public class OTPControllerTest {

	private MockMvc mockMvc;
	private String router = "trentino";

	@Autowired
	private ConfigurationManager configurationManager;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private OTPManager otpManager;
	@Autowired
	private OTPHandler otpHandler;

	@Before
	public void setUp() throws Exception {

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		for (String router : configurationManager.getRouterKeys()) {
			otpHandler.clean();
			otpManager.clean();
			mockMvc.perform(get("/" + router + "/configuration/init")).andExpect(status().isOk());
//			otpHandler.init(configurationManager.getRouter(router));
//			otpManager.init(router);
//			otpManager.preinit(false);
		}
	}

	@Test
	public void testStops() throws Exception {

		// call manager directly.
		String res = otpManager.getRoutes(router, "12");
		System.out.println(res);

		// call controller directly.
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/getroutes/12")).andExpect(status().isOk()).andReturn();

		assertTrue(result.getResponse().getContentAsString().equalsIgnoreCase(res));

	}

//	@Test
	public void testGTFSDownload() throws Exception {
		// call controller directly.
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/gtfs/zip/12")).andExpect(status().isOk()).andReturn();

	}

	@Test
	public void testLimitedTimeTable() throws Exception {

		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);

		long now = System.currentTimeMillis();

		String res = otpManager.getLimitedTimeTable(router, "12", "229_12", now, 3);

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/getlimitedtimetable/12/229_12/3")).andExpect(status().isOk())
				.andReturn();

		System.err.println(result.getResponse().getContentAsString());
		assertTrue(result.getResponse().getContentAsString().equalsIgnoreCase(res));

	}

	@Test
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
		String res2 = otpManager.getTransitSchedule(router, "12", "08A", from, to, TransitScheduleResults.ALL, false);

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/getTransitTimes/12/08A/" + from + "/" + to))
				.andExpect(status().isOk()).andReturn();
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

	@Test
	public void testTrainSchedule() throws Exception {
		long from = 1361919600000L;
		long to = 1362005999999L;

		String trainBVG = otpManager.getTransitSchedule(router, "5", "BV_R1_G", from, to, TransitScheduleResults.ALL,
				true);

		System.out.println(trainBVG);

		String trainTBG = otpManager.getTransitSchedule(router, "6", "TB_R2_G", from, to, TransitScheduleResults.ALL,
				true);

		System.out.println(trainTBG);

		String trainTBR = otpManager.getTransitSchedule(router, "6", "TB_R2_R", from, to, TransitScheduleResults.ALL,
				true);

		System.out.println(trainTBR);

		String busTrento = otpManager.getTransitSchedule(router, "12", "02", from, to, TransitScheduleResults.ALL, true);

		System.out.println(busTrento);
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

}
