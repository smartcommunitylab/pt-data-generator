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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.data.message.Itinerary;
import it.sayservice.platform.smartplanner.data.message.Leg;
import it.sayservice.platform.smartplanner.data.message.SimpleLeg;
import it.sayservice.platform.smartplanner.data.message.StopId;
import it.sayservice.platform.smartplanner.data.message.TType;
import it.sayservice.platform.smartplanner.data.message.Transport;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertDelay;
import it.sayservice.platform.smartplanner.data.message.alerts.AlertParking;
import it.sayservice.platform.smartplanner.data.message.alerts.CreatorType;
import it.sayservice.platform.smartplanner.model.BikeStation;
import it.sayservice.platform.smartplanner.model.CarStation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebAppContext.class })
@WebAppConfiguration
public class PlannerControllerTest {

	private MockMvc mockMvc;
	private String date = "04/14/2016";
	private String departureTime = "2:48pm";
	private String routeType = "fastest";
	long interval = 7200000L;
	String recurrence = String.valueOf(Calendar.DAY_OF_WEEK);
	long startTime, endTime;
	Calendar calendar = Calendar.getInstance();
	{
		startTime = calendar.getTimeInMillis(); // Long.parseLong("1385506800000");
		calendar.add(Calendar.DATE, 5);
		endTime = calendar.getTimeInMillis();
	}
	private ObjectMapper mapper = new ObjectMapper();
	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mmaa", Locale.ITALY);
	String dateTime = formatter.format(System.currentTimeMillis());
	private String time = dateTime.substring(dateTime.indexOf(" ") + 1);

	@Autowired
	private ConfigurationManager configurationManager;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() throws Exception {

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		for (String router : configurationManager.getRouterKeys()) {
			mockMvc.perform(get("/" + router + "/configuration/init")).andExpect(status().isOk());
		}
	}

	@Test
	public void testTransit() throws Exception {
		// transit
		TType mode = TType.TRANSIT;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.0652807,11.1490763")
						.param("to", "46.0735007,11.1188073").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();

		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testBusOnly() throws Exception {
		// bus only.
		TType mode = TType.BUS;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.0652807,11.1490763")
						.param("to", "46.0735007,11.1188073").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();

		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testTrainOnly() throws Exception {
		// train only.
		TType mode = TType.TRAIN;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testBikeOnly() throws Exception {
		//bicycle.
		TType mode = TType.BICYCLE;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testCarOnly() throws Exception {
		//car.
		TType mode = TType.CAR;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testCarWithParking() throws Exception {
		//car with parking
		TType mode = TType.CARWITHPARKING;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testRentalBike() throws Exception {
		//rented bike without target station.
		TType mode = TType.SHAREDBIKE_WITHOUT_STATION;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testRentalBikeNearbyPlaces() throws Exception {
		//rented bike without target station.
		TType mode = TType.SHAREDBIKE_WITHOUT_STATION;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testRentalCar() throws Exception {
		//rented car without target station.
		TType mode = TType.SHAREDCAR_WITHOUT_STATION;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testBikeRentalViaStation() throws Exception {
		TType mode = TType.SHAREDBIKE;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testCarRentalViaStation() throws Exception {
		TType mode = TType.SHAREDCAR;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.071875,11.119716")
						.param("to", "46.054949,11.134291").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testRecurrenceFeatureTransit() throws Exception {
		// transit
		TType mode = TType.TRANSIT;
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}

		Assert.notEmpty(output);
	}

	@Test
	public void testRecurrenceFeatureCar() throws Exception {
		TType mode = TType.CAR;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}

		Assert.notEmpty(output);

	}

	@Test
	public void testRecurrenceFeatureBike() throws Exception {

		TType mode = TType.BICYCLE;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}
		Assert.notEmpty(output);
	}

	@Test
	public void testRecurrenceFeatureCarWithParking() throws Exception {
		TType mode = TType.CARWITHPARKING;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}
		Assert.notEmpty(output);

	}

	@Test
	public void testRecurrenceFeatureCarRentalWithStation() throws Exception {
		TType mode = TType.SHAREDCAR;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}
		Assert.notEmpty(output);
	}

	@Test
	public void testRecurrenceFeatureCarRentalWithoutStation() throws Exception {

		TType mode = TType.SHAREDCAR_WITHOUT_STATION;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}
		Assert.notEmpty(output);
	}

	@Test
	public void testRecurrenceFeatureBikeRentalWithStation() throws Exception {
		TType mode = TType.SHAREDBIKE;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}
		Assert.notEmpty(output);
	}

	@Test
	public void testRecurrenceFeatureBikeRentalWithoutStation() throws Exception {

		TType mode = TType.SHAREDBIKE_WITHOUT_STATION;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/recurrentJourney").param("recurrence", recurrence)
						.param("from", "46.066948,11.153047").param("to", "46.069957,11.115861").param("time", "2:25pm")
						.param("interval", String.valueOf(interval)).param("transportType", mode.name())
						.param("routeType", routeType).param("fromDate", String.valueOf(startTime))
						.param("toDate", String.valueOf(endTime)))
				.andExpect(status().isOk()).andReturn();

		List<?> legs = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<SimpleLeg> output = new ArrayList<SimpleLeg>();

		for (Object leg : legs) {
			SimpleLeg simpleLeg = mapper.convertValue(leg, SimpleLeg.class);
			output.add(simpleLeg);
		}

		Assert.notEmpty(output);

	}

	@Test
	public void testInvalidRentalStationTrip() throws Exception {

		TType mode = TType.SHAREDCAR_WITHOUT_STATION;
		// start within Rovereto.
		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "45.891124,11.03680")
						.param("to", "45.889452,11.044357").param("date", date).param("departureTime", departureTime)
						.param("transportType", mode.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				assertFalse(leg.getTransport().getType().name().equalsIgnoreCase("BICYCLE"));
			}
		}

	}

//	@Test
	public void testRedundantTransit() throws Exception {
		// check if the planner behavior is redundant in latest version.
		boolean[] bus = new boolean[6];
		String[] rsNames = new String[] { "A", "1", "2", "4", "7", "8" };
		TType mode = TType.TRANSIT;

		org.springframework.test.web.servlet.MvcResult result = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.078232098524595,11.118754148483276")
						.param("to", "46.065489356722395,11.131371259689331").param("date", date)
						.param("departureTime", departureTime).param("transportType", mode.name())
						.param("routeType", routeType).param("numOfItn", "1").param("maxWalkDistance", "500"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary it : output) {
			for (Leg leg : it.getLeg()) {
				Transport transport = leg.getTransport();
				if (transport.getType().equals(TType.BUS)) {
					// System.out.println(transport.getRouteShortName());
					for (int s = 0; s < rsNames.length; s++) {
						if (rsNames[s].equalsIgnoreCase(transport.getRouteShortName())) {
							bus[s] = true;
						}
					}
				}
			}

		}

		for (Boolean busRS : bus) {
			assertTrue(busRS);
		}
		its.clear();
	}

	/**@Test
	public void analysis() throws Exception {
	
		java.util.List<Itinerary> its = new ArrayList<Itinerary>();
	
		// monitored parameters
		int totalItns = 0;
		int totalPlans = 0;
		int emptyPlans = 0;
		int delayPlans = 0;
		int carMode = 0;
		int transitMode = 0;
		int bikeMode = 0;
		int walkMode = 0;
		int emptyPlanCarMode = 0;
		int emptyPlanTransitMode = 0;
	
		// variables
		double kilometers = 0;
	
		// parse the text files and extract following.
		String from = null;
		String to = null;
		String departureDate = null;
		String time = null;
		String mode = null;
		TType modeT = null;
	
		// Open the file that is the first
		FileInputStream fstream = new FileInputStream("D:/deleted/crazy-week/logs-analysis/otp_url_tn.log");
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int index = 0;
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// create tokens
			String[] elements = strLine.split("&");
	
			if (elements.length > 1) {
				System.out.println("request " + index + " ==> " + elements[1] + "," + elements[2] + "," + elements[3]
						+ "," + elements[4] + "," + elements[5] + "," + elements[6]);
				index++;
				from = elements[1].substring(elements[1].indexOf("=") + 1);
				to = elements[2].substring(elements[2].indexOf("=") + 1);
				time = elements[5].substring(elements[5].indexOf("=") + 1);
				departureDate = elements[6].substring(elements[6].indexOf("=") + 1);
				if (elements[3].indexOf(",") != -1)
					mode = elements[3].substring(elements[3].indexOf("=") + 1, elements[3].indexOf(","));
				else
					mode = elements[3].substring(elements[3].indexOf("=") + 1);
				modeT = TType.getMode(mode);
	
				if (modeT.name().equalsIgnoreCase("CAR")) {
					modeT = TType.CARWITHPARKING;
				}
	
				if (modeT.name().equalsIgnoreCase("TRANSIT")) {
					transitMode++;
				} else if (modeT.name().equalsIgnoreCase("CAR")
						|| modeT.name().equalsIgnoreCase(TType.CARWITHPARKING.name())) {
					carMode++;
				} else if (modeT.name().equalsIgnoreCase("BICYCLE")) {
					bikeMode++;
				} else if (modeT.name().equalsIgnoreCase("WALK")) {
					walkMode++;
				}
	
				if (from.equalsIgnoreCase("null,null") || to.equalsIgnoreCase("null,null")) {
					System.err.println("SKIPPED");
					continue;
				}
	
				// create request and invoke open trip planner.
				its = planner.planTrip(from, to, departureDate, null, time, modeT, routeT, null);
	
				// parse response.
				for (Itinerary it : its) {
					// hit counts for plan with duration over 1 hour.
					if (it.getDuration() > 3600000 & it.getLeg().size() > 1) { // avoid walkOnly plans.
						delayPlans++;
	
						double lat1 = Double.parseDouble(from.substring(0, from.indexOf(",")));
						double lon1 = Double.parseDouble(from.substring(from.indexOf(",") + 1));
	
						double lat2 = Double.parseDouble(to.substring(0, to.indexOf(",")));
						double lon2 = Double.parseDouble(to.substring(to.indexOf(",") + 1));
	
						kilometers = calculateHarvesineDistance(lat1, lon1, lat2, lon2);
	
						// calculate Harvesine distance between coordinates.
						System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDD");
						System.out.println("DDDDDDD DELAYED PLAN DDDDDD");
						System.out.println("request " + index + " ==> " + elements[1] + "," + elements[2] + ","
								+ elements[3] + "," + elements[4] + "," + elements[5] + "," + elements[6]);
						System.out.println("distance in kilometer (over the curve): " + kilometers);
						System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDD");
	
						if (kilometers < 5) {
							File longDurationPlanUri = new File(
									"D:/deleted/crazy-week/logs-analysis/longdurationplanuri.log");
							FileWriter outFile = new FileWriter(longDurationPlanUri, true);
							outFile.write("request " + index + " ==> " + elements[1] + "," + elements[2] + ","
									+ elements[3] + "," + elements[4] + "," + elements[5] + "," + elements[6]
									+ " kilometeres: " + kilometers + " Duration: " + it.getDuration() / (1000 * 60)
									+ "mins\n");
							outFile.close(); //closes the file
						}
					}
				}
				// hit counts for empty response.
				if (its.isEmpty()) {
					emptyPlans++;
					System.out.println("???????????????????????????");
					System.out.println("??????? EMTPY PLAN ????????");
					System.out.println("request " + index + " ==> " + elements[1] + "," + elements[2] + ","
							+ elements[3] + "," + elements[4] + "," + elements[5] + "," + elements[6]);
					System.out.println("???????????????????????????");
					if (modeT.name().equalsIgnoreCase("CAR")
							|| modeT.name().equalsIgnoreCase(TType.CARWITHPARKING.name())) {
						emptyPlanCarMode++;
						File emptyCarPlanUri = new File("D:/deleted/crazy-week/logs-analysis/emptycarplanuri.log");
						FileWriter outFile = new FileWriter(emptyCarPlanUri, true);
						outFile.write("request " + index + " ==> " + elements[1] + "," + elements[2] + "," + elements[3]
								+ "," + elements[4] + "," + elements[5] + "," + elements[6] + "\n"); //writes to file
						outFile.close(); //closes the file
					} else if (modeT.name().equalsIgnoreCase("TRANSIT")) {
						emptyPlanTransitMode++;
					}
				} else {
					// total number of filled responses
					totalItns++;
					// total number of plans in filled responses.
					totalPlans = totalPlans + its.size();
				}
	
				// reset
				from = null;
				to = null;
				departureDate = null;
				time = null;
				mode = null;
			}
		}
	
		System.out.println("#############################################");
		System.out.println("############# RESULT ########################");
		System.out.println("#############################################");
		System.out.println("Number of invocations: " + index);
		System.out.println("Number of empty response: " + emptyPlans);
		System.out.println("Number of non empty responses: " + totalItns);
		System.out.println("Number of plans in non empty responses: " + totalPlans);
		System.out.println("Number of delayed plans (duration > hour): " + delayPlans);
		System.out.println("Number of car mode requests: " + carMode);
		System.out.println("Number of transit mode requests: " + transitMode);
		System.out.println("Number of bicylce mode requests: " + bikeMode);
		System.out.println("Number of walk mode requests: " + walkMode);
		System.out.println("Number of empty responses (car mode): " + emptyPlanCarMode);
		System.out.println("Number of empty responses (transit mode): " + emptyPlanTransitMode);
	}**/

	/**
	* Harvesine Formula to get distance between coordinates.
	* @param x1
	* @param y1
	* @param x2
	* @param y2
	* @return distance
	 * @throws Exception 
	*/
	//	double calculateHarvesineDistance(double lat1, double lon1, double lat2, double lon2) {
	//		/**
	//		* R = earth’s radius (mean radius = 6,371km)
	//		* Δlat = lat2− lat1
	//		* Δlong = long2− long1
	//		* a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
	//		* c = 2.atan2(√a, √(1−a))
	//		* d = R.c
	//		*/
	//		double distance = 0;
	//		final int R = 6371; // Radius of the earth km.
	//		Double latDistance = toRad(lat2 - lat1);
	//		Double lonDistance = toRad(lon2 - lon1);
	//		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	//				+ Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	//		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	//		distance = R * c;
	//		System.out.println("The distance between two lat and long is::" + distance);
	//		return distance;
	//	}
	//
	//	private static Double toRad(Double value) {
	//		return value * Math.PI / 180;
	//	}

	@Test
	public void testAlertBikeStation() throws Exception {

		List<AlertParking> alert = new ArrayList<AlertParking>();

		String date = dateTime.substring(0, dateTime.indexOf(" "));

		// get bike station near piazza venezia.
		org.springframework.test.web.servlet.MvcResult result1 = mockMvc
				.perform(get("/trentino/rest/getBikeStations")).andExpect(status().isOk()).andReturn();

		List<?> stations = mapper.readValue(result1.getResponse().getContentAsString(), List.class);

		BikeStation bs = null;
		for (Object station : stations) {

			BikeStation temp = mapper.convertValue(station, BikeStation.class);
			if (temp.getStationId().getId().equalsIgnoreCase("Stazione FFSS - Ospedale - Trento")) {
				bs = temp;
				break;
			}
		}

		// plan.
		TType modeSBWS = TType.SHAREDBIKE_WITHOUT_STATION;
		org.springframework.test.web.servlet.MvcResult result2 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.066948,11.153047")
						.param("to", "46.069957,11.115861").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeSBWS.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result2.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				if (leg.getTransport().getType().name().equalsIgnoreCase(TType.BICYCLE.name()) && alert.isEmpty()) {
					alert = leg.getAlertParkingList();
					break;
				}
			}
			if (!alert.isEmpty()) {
				break;
			}
		}

		// check for alert within itinerary.
		assertTrue(alert.isEmpty());
		its.clear();
		output.clear();

		// modify station using AlertParking.(number of bikes=1)
		AlertParking apBike = new AlertParking();
		StopId placeB1 = new StopId("BIKE_SHARING_TOBIKE_TRENTO", bs.getStationId().getId());
		apBike.setCreatorType(CreatorType.SERVICE);
		apBike.setId(String.valueOf(startTime));
		apBike.setPlace(placeB1);
		apBike.setNoOfvehicles(1); // -1 INCASE OF NO UPDATE
		apBike.setPlacesAvailable(20); // -1 INCASE OF NO UPDATE
		apBike.setDescription("test bike station");

		// CREATE
		org.springframework.test.web.servlet.MvcResult result3 = mockMvc
				.perform(post("/trentino/rest/updateAP").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(apBike)))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result3.getResponse().getContentAsString().indexOf("Created") > -1);
		System.out.println("response " + result3.getResponse().getContentAsString());

		// plan and check for alert.
		org.springframework.test.web.servlet.MvcResult result4 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.066948,11.153047")
						.param("to", "46.069957,11.115861").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeSBWS.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		its = mapper.readValue(result4.getResponse().getContentAsString(), List.class);

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				if (leg.getTransport().getType().name().equalsIgnoreCase(TType.BICYCLE.name()) && alert.isEmpty()) {
					alert = leg.getAlertParkingList();
					break;
				}
			}
			if (!alert.isEmpty()) {
				break;
			}
		}

		assertTrue(!alert.isEmpty());
		its.clear();
		alert.clear();

		// UPDATE
		apBike.setNoOfvehicles(0);
		apBike.setPlacesAvailable(0);

		org.springframework.test.web.servlet.MvcResult result6 = mockMvc
				.perform(post("/trentino/rest/updateAP").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(apBike)))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result6.getResponse().getContentAsString().indexOf("Updated") > -1);
		System.out.println("response " + result6.getResponse().getContentAsString());

		// plan and check for alert.
		org.springframework.test.web.servlet.MvcResult result7 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.066948,11.153047")
						.param("to", "46.069957,11.115861").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeSBWS.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		its = mapper.readValue(result7.getResponse().getContentAsString(), List.class);

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				if (leg.getTransport().getType().name().equalsIgnoreCase(TType.BICYCLE.name()) && alert.isEmpty()) {
					alert = leg.getAlertParkingList();
					break;
				}
			}
			if (!alert.isEmpty()) {
				break;
			}
		}

		// should be empty since alert validation period is over.
		assertTrue(alert.isEmpty());

	}

	@Test
	public void testAlertCarStation() throws Exception {

		List<AlertParking> alert = new ArrayList<AlertParking>();

		String date = dateTime.substring(0, dateTime.indexOf(" "));

		// get bike station near piazza venezia.
		org.springframework.test.web.servlet.MvcResult result1 = mockMvc
				.perform(get("/trentino/rest/getCarStations")).andExpect(status().isOk()).andReturn();

		List<?> stations = mapper.readValue(result1.getResponse().getContentAsString(), List.class);

		// get car station near povo.
		CarStation cs = null;
		for (Object station : stations) {

			CarStation temp = mapper.convertValue(station, CarStation.class);
			if (temp.getId().equalsIgnoreCase("Povo@CAR_SHARING_TRENTO")) {
				cs = temp;
				break;
			}
		}

		// plan.
		// rented bike without target station.
		TType modeSCWS = TType.SHAREDCAR_WITHOUT_STATION;
		org.springframework.test.web.servlet.MvcResult result2 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.066948,11.153047")
						.param("to", "46.069957,11.115861").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeSCWS.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result2.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				if (leg.getTransport().getType().name().equalsIgnoreCase(TType.CAR.name()) && alert.isEmpty()) {
					alert = leg.getAlertParkingList();
					break;
				}
			}
			if (!alert.isEmpty()) {
				break;
			}
		}

		// check for alert within itinerary.
		assertTrue(alert.isEmpty());
		its.clear();
		output.clear();

		// modify station using AlertParking.(number of bikes=1)
		AlertParking apCar = new AlertParking();
		StopId placeB1 = new StopId("CAR_SHARING_TRENTO", cs.getStationId().getId());
		apCar.setPlace(placeB1);
		apCar.setNoOfvehicles(1); // -1 INCASE OF NO UPDATE
		apCar.setPlacesAvailable(20); // -1 INCASE OF NO UPDATE
		apCar.setDescription("test car station");
		// CREATE.
		org.springframework.test.web.servlet.MvcResult result3 = mockMvc
				.perform(post("/trentino/rest/updateAP").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(apCar)))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result3.getResponse().getContentAsString().indexOf("Created") > -1);
		System.out.println("response " + result3.getResponse().getContentAsString());

		// plan and check for alert.
		org.springframework.test.web.servlet.MvcResult result4 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.066948,11.153047")
						.param("to", "46.069957,11.115861").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeSCWS.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		its = mapper.readValue(result4.getResponse().getContentAsString(), List.class);

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				if (leg.getTransport().getType().name().equalsIgnoreCase(TType.CAR.name()) && alert.isEmpty()) {
					alert = leg.getAlertParkingList();
					break;
				}
			}
			if (!alert.isEmpty()) {
				break;
			}
		}

		assertTrue(!alert.isEmpty());
		alert.clear();
		its.clear();

		// UPDATE
		apCar.setNoOfvehicles(0);
		apCar.setPlacesAvailable(0);

		org.springframework.test.web.servlet.MvcResult result5 = mockMvc
				.perform(post("/trentino/rest/updateAP").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(apCar)))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result5.getResponse().getContentAsString().indexOf("Updated") > -1);
		System.out.println("response " + result5.getResponse().getContentAsString());

		// plan and check for alert.
		org.springframework.test.web.servlet.MvcResult result6 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.066948,11.153047")
						.param("to", "46.069957,11.115861").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeSCWS.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		its = mapper.readValue(result6.getResponse().getContentAsString(), List.class);

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary i : output) {
			for (Leg leg : i.getLeg()) {
				if (leg.getTransport().getType().name().equalsIgnoreCase(TType.CAR.name()) && alert.isEmpty()) {
					alert = leg.getAlertParkingList();
					break;
				}
			}
			if (!alert.isEmpty()) {
				break;
			}
		}

		assertTrue(alert.isEmpty());

	}

	//  @Test
	//	public void testAlertDelayUpdate() throws Exception {
	//
	//		// plan transit povo to lavis.
	//		// generate delay for bus such that it is less than starting time of train to lavis.
	//		// check alertDelay in output.
	//		// generate delay for such that it is greater than starting time of train to lavis.
	//		// check for different itinerary.
	//
	//		List<AlertDelay> alert = new ArrayList<AlertDelay>();
	//		Transport tp1 = null;
	//
	//		// p1
	//		TType modeT = TType.TRANSIT;
	//		org.springframework.test.web.servlet.MvcResult result1 = mockMvc
	//				.perform(get("/trentino/rest/plan").param("from", "46.140786,11.108580")
	//						.param("to", "46.066069,11.154585").param("date", date).param("departureTime", departureTime)
	//						.param("transportType", modeT.name()).param("routeType", routeType).param("numOfItn", "1"))
	//				.andExpect(status().isOk()).andReturn();
	//		List<?> its = mapper.readValue(result1.getResponse().getContentAsString(), List.class);
	//		List<Itinerary> output = new ArrayList<Itinerary>();
	//
	//		for (Object it : its) {
	//			Itinerary itn = mapper.convertValue(it, Itinerary.class);
	//			output.add(itn);
	//		}
	//
	//		for (Itinerary it : output) {
	//			for (Leg lg : it.getLeg()) {
	//				if (lg.getTransport().getType().name().equalsIgnoreCase(TType.BUS.name())) {
	//					if (tp1 == null) {
	//						tp1 = lg.getTransport();
	//						break;
	//					}
	//				}
	//			}
	//			if (tp1 != null) {
	//				break;
	//			}
	//		}
	//		its.clear();
	//		output.clear();
	//
	//		// create alert delay.
	//		AlertDelay alertD = new AlertDelay();
	//		alertD.setTransport(tp1);
	//		alertD.setCreatorType(CreatorType.SERVICE);
	//		alertD.setDelay(1 * 60 * 1000); // 1min.
	//		// time same as that of request.
	//		Date sd1 = (Date) formatter.parse(date + " " + time);
	//
	//		// from
	//		long from1 = sd1.getTime();
	//		alertD.setFrom(from1);
	//		calendar.setTimeInMillis(from1);
	//		System.out.println("from->" + from1 + " = " + formatter.format(calendar.getTime()));
	//
	//		// valid for for hour.
	//		long to1 = sd1.getTime() + (4 * 1000 * 60 * 60);
	//		calendar.setTimeInMillis(to1);
	//		System.out.println("to->" + to1 + " = " + formatter.format(calendar.getTime()));
	//		alertD.setTo(to1);
	//
	//		alertD.setId(tp1.getTripId() + "_" + from1 + "_" + to1);
	//
	//		org.springframework.test.web.servlet.MvcResult result2 = mockMvc
	//				.perform(post("/trentino/rest/updateAD").contentType(MediaType.APPLICATION_JSON)
	//						.content(mapper.writeValueAsBytes(alertD)))
	//				.andExpect(status().isOk()).andReturn();
	//
	//		assertTrue(result2.getResponse().getContentAsString().indexOf("Created") > -1);
	//		System.out.println("response " + result2.getResponse().getContentAsString());
	//
	//		org.springframework.test.web.servlet.MvcResult result3 = mockMvc
	//				.perform(get("/trentino/rest/plan").param("from", "46.140786,11.108580")
	//						.param("to", "46.066069,11.154585").param("date", date).param("departureTime", departureTime)
	//						.param("transportType", modeT.name()).param("routeType", routeType).param("numOfItn", "1"))
	//				.andExpect(status().isOk()).andReturn();
	//		
	//		its = mapper.readValue(result3.getResponse().getContentAsString(), List.class);
	//		
	//		for (Object it : its) {
	//			Itinerary itn = mapper.convertValue(it, Itinerary.class);
	//			output.add(itn);
	//		}
	//
	//		for (Itinerary it : output) {
	//			for (Leg lg : it.getLeg()) {
	//				if (lg.getTransport().getType().name().equalsIgnoreCase(TType.BUS.name())) {
	//					alert = lg.getAlertDelayList();
	//					break;
	//				}
	//			}
	//			if (!alert.isEmpty()) {
	//				break;
	//			}
	//		}
	//		// check
	//		assertTrue(!alert.isEmpty());
	//
	//		its.clear();
	//		output.clear();
	//		alert.clear();
	//
	//		// update alertDelay.
	//		alertD.setDelay(50 * 60 * 1000); // 20mins.
	//
	//		org.springframework.test.web.servlet.MvcResult result4 = mockMvc
	//				.perform(post("/trentino/rest/updateAD").contentType(MediaType.APPLICATION_JSON)
	//						.content(mapper.writeValueAsBytes(alertD)))
	//				.andExpect(status().isOk()).andReturn();
	//
	//		assertTrue(result4.getResponse().getContentAsString().indexOf("Ureated") > -1);
	//		System.out.println("response " + result4.getResponse().getContentAsString());
	//
	//		org.springframework.test.web.servlet.MvcResult result5 = mockMvc
	//				.perform(get("/trentino/rest/plan").param("from", "46.140786,11.108580")
	//						.param("to", "46.066069,11.154585").param("date", date).param("departureTime", departureTime)
	//						.param("transportType", modeT.name()).param("routeType", routeType).param("numOfItn", "1"))
	//				.andExpect(status().isOk()).andReturn();
	//		its = mapper.readValue(result5.getResponse().getContentAsString(), List.class);
	//		
	//		for (Object it : its) {
	//			Itinerary itn = mapper.convertValue(it, Itinerary.class);
	//			output.add(itn);
	//		}
	//
	//		for (Itinerary it : output) {
	//			for (Leg lg : it.getLeg()) {
	//				if (lg.getTransport().getType().name().equalsIgnoreCase(TType.BUS.name())) {
	//					alert = lg.getAlertDelayList();
	//				}
	//			}
	//			if (!alert.isEmpty()) {
	//				break;
	//			}
	//		}
	//
	//		// check
	//		assertTrue(alert.isEmpty());
	//		its.clear();
	//		output.clear();
	//		alert.clear();
	//
	//		alertD.setDelay(0); // 0mins.
	//
	//		org.springframework.test.web.servlet.MvcResult result6 = mockMvc
	//				.perform(post("/trentino/rest/updateAD").contentType(MediaType.APPLICATION_JSON)
	//						.content(mapper.writeValueAsBytes(alertD)))
	//				.andExpect(status().isOk()).andReturn();
	//
	//		assertTrue(result6.getResponse().getContentAsString().indexOf("Ureated") > -1);
	//		System.out.println("response " + result6.getResponse().getContentAsString());
	//
	//		org.springframework.test.web.servlet.MvcResult result7 = mockMvc
	//				.perform(get("/trentino/rest/plan").param("from", "46.140786,11.108580")
	//						.param("to", "46.066069,11.154585").param("date", date).param("departureTime", departureTime)
	//						.param("transportType", modeT.name()).param("routeType", routeType).param("numOfItn", "1"))
	//				.andExpect(status().isOk()).andReturn();
	//		its = mapper.readValue(result7.getResponse().getContentAsString(), List.class);
	//	
	//		for (Object it : its) {
	//			Itinerary itn = mapper.convertValue(it, Itinerary.class);
	//			output.add(itn);
	//		}
	//
	//		for (Itinerary it : output) {
	//			for (Leg lg : it.getLeg()) {
	//				if (lg.getTransport().getType().name().equalsIgnoreCase(TType.BUS.name())) {
	//					alert = lg.getAlertDelayList();
	//				}
	//			}
	//			if (!alert.isEmpty()) {
	//				break;
	//			}
	//		}
	//		// no alert in case delay is < 0.
	//		assertTrue(alert.isEmpty());
	//	}

	@Test
	public void testAlertDelayTrainBV() throws Exception {

		// plan transit bolzano to trento with strange tripId R20473BT.
		// generate delay for bus such that it is less than
		// check alertDelay in output.

		List<AlertDelay> alert = new ArrayList<AlertDelay>();
		Transport tp1 = null;

		// p1
		TType modeT = TType.TRANSIT;
		org.springframework.test.web.servlet.MvcResult result1 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.498370,11.354244")
						.param("to", "46.071929,11.119571").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeT.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		List<?> its = mapper.readValue(result1.getResponse().getContentAsString(), List.class);
		List<Itinerary> output = new ArrayList<Itinerary>();

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary it : output) {
			for (Leg lg : it.getLeg()) {
				if (lg.getTransport().getType().name().equalsIgnoreCase(TType.TRAIN.name())) {
					if (tp1 == null) {
						tp1 = lg.getTransport();
						tp1.setTripId(
								lg.getTransport().getTripId().substring(0, lg.getTransport().getTripId().indexOf("$")));
						break;
					}
				}
			}
			if (tp1 != null) {
				break;
			}
		}
		its.clear();
		output.clear();

		// create alert delay.
		AlertDelay alertD = new AlertDelay();
		alertD.setTransport(tp1);
		alertD.setCreatorType(CreatorType.SERVICE);
		alertD.setDelay(1 * 60 * 1000); // 1min.
		// time same as that of request.
		Date sd1 = (Date) formatter.parse(date + " 6:00am");

		// from
		long from1 = sd1.getTime();
		alertD.setFrom(from1);
		calendar.setTimeInMillis(from1);
		System.out.println("from->" + from1 + " = " + formatter.format(calendar.getTime()));

		// valid for for hour.
		long to1 = sd1.getTime() + (10 * 1000 * 60 * 60);
		calendar.setTimeInMillis(to1);
		System.out.println("to->" + to1 + " = " + formatter.format(calendar.getTime()));
		alertD.setTo(to1);

		//		alertD.setId(tp1.getTripId() + "_" + from1 + "_" + to1);

		org.springframework.test.web.servlet.MvcResult result2 = mockMvc
				.perform(post("/trentino/rest/updateAD").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(alertD)))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result2.getResponse().getContentAsString().indexOf("Created") > -1);
		System.out.println("response " + result2.getResponse().getContentAsString());

		org.springframework.test.web.servlet.MvcResult result3 = mockMvc
				.perform(get("/trentino/rest/plan").param("from", "46.498370,11.354244")
						.param("to", "46.071929,11.119571").param("date", date).param("departureTime", departureTime)
						.param("transportType", modeT.name()).param("routeType", routeType).param("numOfItn", "1"))
				.andExpect(status().isOk()).andReturn();
		its = mapper.readValue(result3.getResponse().getContentAsString(), List.class);

		for (Object it : its) {
			Itinerary itn = mapper.convertValue(it, Itinerary.class);
			output.add(itn);
		}

		for (Itinerary it : output) {
			for (Leg lg : it.getLeg()) {
				if (lg.getTransport().getType().name().equalsIgnoreCase(TType.TRAIN.name())) {
					alert = lg.getAlertDelayList();
					break;
				}
			}
			if (tp1 != null) {
				break;
			}
		}

		// check
		assertTrue(!alert.isEmpty());

		alert.clear();
		its.clear();
		output.clear();
	}

}