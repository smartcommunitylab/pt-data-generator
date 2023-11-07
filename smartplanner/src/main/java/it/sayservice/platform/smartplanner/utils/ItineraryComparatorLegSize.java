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

import java.util.Comparator;

import it.sayservice.platform.smartplanner.data.message.Itinerary;

/**
 * itinerary leg size comparator.
 * 
 * @author nawazk
 *
 */
public class ItineraryComparatorLegSize implements Comparator<Itinerary> {
	@Override
	public final int compare(final Itinerary object1, final Itinerary object2) {
		Integer size1 = object1.getLeg().size();
		Integer size2 = object2.getLeg().size();
		return size1.compareTo(size2);
	}
}
