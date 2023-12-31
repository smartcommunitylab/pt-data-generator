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


package it.sayservice.platform.smartplanner;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import it.sayservice.platform.smartplanner.config.MultipleRouterContextInitializer;
import it.sayservice.platform.smartplanner.configurations.ConfigurationManager;
import it.sayservice.platform.smartplanner.controllers.ConfigCtrl;
import it.sayservice.platform.smartplanner.otp.OTPHandler;
import it.sayservice.platform.smartplanner.otp.OTPManager;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { MongoDataAutoConfiguration.class, MongoAutoConfiguration.class,
		MongoRepositoriesAutoConfiguration.class })
@EnableWebMvc
public class SmartPlannerApplication {

	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = new SpringApplicationBuilder(SmartPlannerApplication.class)
				.initializers(new MultipleRouterContextInitializer()).showBanner(false).run(args);

		ConfigurationManager configurationManager = context.getBean(ConfigurationManager.class);

		List<String> routers = configurationManager.getRouterKeys();

		if (context.getBean("value", Boolean.class)) {
			/** INITIALIZATION. **/
			ConfigCtrl configurationController = context.getBean(ConfigCtrl.class);
			for (String router : routers) {
				configurationController.init(router);
			}
		} else {
			for (String router : routers) {
				OTPManager otpManager = context.getBean(OTPManager.class);
				OTPHandler otpHandler = context.getBean(OTPHandler.class);
				otpHandler.clean();
				otpManager.clean();
				otpHandler.init(configurationManager.getRouter(router));
				otpManager.init(router);
				otpManager.preinit(false);
			}
		}
	}

	@Bean
	public Boolean value(@Value("${initialize:false}") Boolean value) {
		return value;
	}

}