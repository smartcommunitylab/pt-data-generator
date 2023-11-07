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

package it.sayservice.platform.smartplanner.areainfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import it.sayservice.platform.smartplanner.model.AreaPoint;

public class AreaPointJSONProcessor implements AreaPointProcessor {

	@Override
	public List<AreaPoint> read(InputStream is, AreaPointIdentityMapper mapper) throws Exception {
		List<AreaPoint> result = new ArrayList<AreaPoint>();
		ObjectMapper om = new ObjectMapper();
		List<?> list = om.readValue(is, List.class);
		for (Object o : list) {
			AreaPoint point = om.convertValue(o, AreaPoint.class);
			result.add(point);
		}
		return result;
	}

	@Override
	public List<AreaPoint> read(InputStream is) throws Exception {
		return read(is, null);
	}
}
