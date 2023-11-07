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

import java.net.UnknownHostException;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

import com.mongodb.MongoClient;

import it.sayservice.platform.smartplanner.cache.CacheManager;
import it.sayservice.platform.smartplanner.config.MongoTemplateBeanFactoryPostProcessor;
import it.sayservice.platform.smartplanner.model.AreaPoint;
import it.sayservice.platform.smartplanner.model.BikeStation;
import it.sayservice.platform.smartplanner.model.CarStation;
import it.sayservice.platform.smartplanner.model.Stop;
import it.sayservice.platform.smartplanner.model.StreetLocation;
import it.sayservice.platform.smartplanner.model.TaxiStation;
import it.sayservice.platform.smartplanner.utils.Constants;

@Configuration
@ComponentScan(basePackages = { "it.sayservice.platform.smartplanner.utils",
		"it.sayservice.platform.smartplanner.geocoder", "it.sayservice.platform.smartplanner.configurations",
		"it.sayservice.platform.smartplanner.utils", "it.sayservice.platform.smartplanner.otp" })

public class TestConfig {

//	@Bean(name = "mongoTemplate")
//	public MongoTemplate getMongoTemplate() throws UnknownHostException {
//		MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(), "smartplanner-test");
//
//		// drop existing database.
//		mongoTemplate.getDb().dropDatabase();
//
//		// create collections.
//		if (!mongoTemplate.collectionExists(BikeStation.class)) {
//			mongoTemplate.createCollection(BikeStation.class);
//		}
//		if (!mongoTemplate.collectionExists(CarStation.class)) {
//			mongoTemplate.createCollection(CarStation.class);
//		}
//		if (!mongoTemplate.collectionExists(TaxiStation.class)) {
//			mongoTemplate.createCollection(TaxiStation.class);
//		}
//		if (!mongoTemplate.collectionExists(StreetLocation.class)) {
//			mongoTemplate.createCollection(StreetLocation.class);
//		}
//		// configure the client ...
//		mongoTemplate.indexOps(BikeStation.class).ensureIndex(new GeospatialIndex("location"));
//		mongoTemplate.indexOps(CarStation.class).ensureIndex(new GeospatialIndex("location"));
//		mongoTemplate.indexOps(TaxiStation.class).ensureIndex(new GeospatialIndex("location"));
//		mongoTemplate.indexOps(AreaPoint.class).ensureIndex(new GeospatialIndex("location"));
//		mongoTemplate.indexOps(StreetLocation.class).ensureIndex(new GeospatialIndex("location"));
//		mongoTemplate.indexOps(Constants.STOPS).ensureIndex(new GeospatialIndex("coordinates"));
//		mongoTemplate.indexOps(Stop.class).ensureIndex(new GeospatialIndex("coordinates"));
//
//		return mongoTemplate;
//	}

	@Bean
	public MongoTemplateBeanFactoryPostProcessor getMongoTemplateBeanFactoryPostProcessor() {
		MongoTemplateBeanFactoryPostProcessor mongoTemplateBeanFactoryPostProcessor = new MongoTemplateBeanFactoryPostProcessor(
				System.getenv("routers"));
		return mongoTemplateBeanFactoryPostProcessor;
	}

	@Bean
	public CacheManager getCacheManager() {
		return Mockito.mock(CacheManager.class);

	}

}