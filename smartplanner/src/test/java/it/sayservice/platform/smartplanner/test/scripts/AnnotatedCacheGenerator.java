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

import java.util.ArrayList;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

import it.sayservice.platform.smartplanner.cache.RoutesDBHelper;
import it.sayservice.platform.smartplanner.cache.annotated.AnnotatedReader;
import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.configurations.MongoRouterMapper;
import it.sayservice.platform.smartplanner.otp.OTPHandler;
import it.sayservice.platform.smartplanner.otp.OTPManager;
import it.sayservice.platform.smartplanner.otp.OTPStorage;
import junit.framework.TestCase;

public class AnnotatedCacheGenerator extends TestCase {

	private OTPHandler handler;
	private OTPStorage storage;
	private OTPManager manager;
	private MongoRouterMapper mongoRouterMapper;
	private ConfigurationManager configurationManager;
	private String router = "trentino";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handler = new OTPHandler(router, "http://127.0.0.1:7575");
		MongoTemplate template = new MongoTemplate(new Mongo(), "trentino");
		storage = new OTPStorage(template);
		mongoRouterMapper = new MongoRouterMapper(template, router);
		configurationManager = new ConfigurationManager(router);
		
		manager = new OTPManager(handler, storage, mongoRouterMapper, configurationManager);
		manager.preinit(true);
		manager.init(router);

	}

	public void test() throws Exception {
		AnnotatedReader ar = new AnnotatedReader(router, handler);

		ar.generateCache(router, "16", true);

//		RoutesDBHelper helper = new RoutesDBHelper(router, "trento", false);
//		ArrayList<String> agencyList = new ArrayList<String>() {{
//		    add("12");
//		}};
//		helper.update(router, agencyList);
//		helper.optimize();
//		helper.zip(router);
//
//		helper = new RoutesDBHelper(router, "trento", true);
//		helper.update(router, agencyList);
//		helper.optimize();
//		helper.zip(router);

		System.out.println("Done.");
	}

}
