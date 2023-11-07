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

import org.junit.Test;

import it.sayservice.platform.smartplanner.cache.RoutesDBHelper;
import junit.framework.TestCase;

public class DBCacheGenerator extends TestCase {

	private String router = "trentino";

	@Test
	public void test() throws Exception {
		RoutesDBHelper helper = new RoutesDBHelper(router, "trento", false);
		ArrayList<String> agencyIds = new ArrayList<String>() {
			{
				add("5");
				add("6");
				add("10");
				add("12");
			}
		};
		helper.update(router, agencyIds);
		helper.optimize();
		helper.zip(router);
		System.out.println("Done.");

		helper = new RoutesDBHelper(router, "trento", true);
		helper.update(router, agencyIds);
		helper.optimize();
		helper.zip(router);
		System.out.println("Done.");
	}

}
