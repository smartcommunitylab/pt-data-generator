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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mongodb.Mongo;

import it.sayservice.platform.smartplanner.cache.CacheManager;
import it.sayservice.platform.smartplanner.cache.RoutesDBHelper;
import it.sayservice.platform.smartplanner.cache.annotated.AnnotatedReader;
import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.configurations.MongoRouterMapper;
import it.sayservice.platform.smartplanner.controllers.PlannerCtrl;
import it.sayservice.platform.smartplanner.otp.OTPHandler;
import it.sayservice.platform.smartplanner.otp.OTPManager;
import it.sayservice.platform.smartplanner.otp.OTPStorage;

public class CacheGenerator {

	private static OTPHandler handler;
	private static OTPStorage storage;
	private static MongoRouterMapper mongoRouterMapper;
	private static ConfigurationManager configurationManager;
	private static OTPManager manager;
	private static PlannerCtrl planner;
	private static String router = "trentino";

	public static void main(String args[]) throws Exception {
		handler = new OTPHandler(router, "http://127.0.0.1:7575");
		MongoTemplate template = new MongoTemplate(new Mongo("mongo:27017"), router);
		storage = new OTPStorage(template);
		mongoRouterMapper = new MongoRouterMapper(template, router);

		configurationManager = new ConfigurationManager(router);

		manager = new OTPManager(handler, storage, mongoRouterMapper, configurationManager);
		manager.preinit(true);
		planner = new PlannerCtrl();
		manager.init(router);
		CacheManager cb = new CacheManager(router, manager, handler);
		AnnotatedReader ar = new AnnotatedReader(router, handler);

		// trains.
		cb.updateCache(router, "5", true, false);
		cb.updateCache(router, "6", true, false);
		cb.updateCache(router, "10", true, false);
		// trento.
		ar.generateCache(router, "12", true);

		ArrayList<String> agencyList = new ArrayList<String>() {
			{
				add("5");
				add("6");
				add("10");
				add("12");
			}
		};

		RoutesDBHelper helper = new RoutesDBHelper(router, "trento", false);
		helper.update(router, agencyList);
		helper.optimize();
		helper.zip(router);
		helper = new RoutesDBHelper(router, "trento", true);
		helper.update(router, agencyList);
		helper.optimize();
		helper.zip(router);

	}

}
