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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import it.sayservice.platform.smartplanner.data.message.StopId;
import it.sayservice.platform.smartplanner.model.BikeStation;
import it.sayservice.platform.smartplanner.model.CarStation;

public class ConvertStationsXML2JSON {

	public static void main(String[] args) throws Exception {
		convertBikes();
		convertCars();
	}

	public static void convertBikes() throws Exception {

		File dir = new File("src/test/resources/bike");
		File files[] = dir.listFiles();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.INDENT_OUTPUT, true);
		for (File file : files) {
			if (!file.getName().endsWith("xml")) {
				continue;
			}
			System.out.println("Loading " + file.getName());
			List<BikeStation> conv = Lists.newArrayList();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("station");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					boolean monitored = false;
					try {
						monitored = Boolean.parseBoolean(getTagValue("monitored", eElement));
					} catch (NullPointerException e) {
					}

					BikeStation bs = new BikeStation(
							new StopId(getTagValue("agencyId", eElement), getTagValue("id", eElement)),
							getTagValue("id", eElement), getTagValue("fullName", eElement),
							getTagValue("type", eElement), Double.parseDouble(getTagValue("lat", eElement)),
							Double.parseDouble(getTagValue("lon", eElement)),
							Integer.parseInt(getTagValue("sharedVehicles", eElement)),
							Integer.parseInt(getTagValue("posts", eElement)));
					bs.setMonitored(monitored);

					bs.setId(bs.getStationId().getId());
					conv.add(bs);
				}
			}
			Files.write(mapper.writeValueAsString(conv), new File(file.getPath().replace("xml", "json")),
					Charsets.UTF_8);
		}
	}

	public static void convertCars() throws Exception {

		File dir = new File("src/test/resources/car");
		File files[] = dir.listFiles();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.INDENT_OUTPUT, true);
		for (File file : files) {
			if (!file.getName().endsWith("xml")) {
				continue;
			}
			System.out.println("Loading " + file.getName());
			List<CarStation> convS = Lists.newArrayList();
			List<CarStation> convP = Lists.newArrayList();
			if (file.getName().startsWith("cs")) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("station");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						boolean monitored = false;
						try {
							monitored = Boolean.parseBoolean(getTagValue("monitored", eElement));
						} catch (NullPointerException e) {
						}

						CarStation cs = new CarStation(
								new StopId(getTagValue("agencyId", eElement), getTagValue("id", eElement)),
								getTagValue("fullName", eElement), getTagValue("type", eElement),
								Double.parseDouble(getTagValue("lat", eElement)),
								Double.parseDouble(getTagValue("lon", eElement)),
								Integer.parseInt(getTagValue("sharedVehicles", eElement)), -1, monitored);

						cs.setId(cs.getStationId().getId());
						convS.add(cs);
					}
				}
				Files.write(mapper.writeValueAsString(convS), new File(file.getPath().replace("xml", "json")),
						Charsets.UTF_8);
			} else {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("station");

				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						// check for car-rental
						boolean monitored = false;
						try {
							monitored = Boolean.parseBoolean(getTagValue("monitored", eElement));
						} catch (NullPointerException e) {
						}
						boolean parkAndRide = false;
						try {
							parkAndRide = Boolean.parseBoolean(getTagValue("parkAndRide", eElement));
						} catch (NullPointerException e) {
						}
						String fixedCost = "";
						String costDefinition = "";
						try {
							fixedCost = getTagValue("fixedCost", eElement);
						} catch (NullPointerException e) {
						}
						try {
							costDefinition = getTagValue("costDefinition", eElement);
						} catch (NullPointerException e) {
						}

						CarStation cs = new CarStation(
								new StopId(getTagValue("agencyId", eElement), getTagValue("id", eElement)),
								getTagValue("fullName", eElement), getTagValue("type", eElement),
								Double.parseDouble(getTagValue("lat", eElement)),
								Double.parseDouble(getTagValue("lon", eElement)), -1,
								Integer.parseInt(getTagValue("posts", eElement)), monitored);
						cs.setParkAndRide(parkAndRide);
						cs.setFixedCost(fixedCost);
						cs.setCostDefinition(costDefinition);

						cs.setId(cs.getStationId().getId());
						convP.add(cs);
					}
				}
				Files.write(mapper.writeValueAsString(convP), new File(file.getPath().replace("xml", "json")),
						Charsets.UTF_8);
			}
		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

}
