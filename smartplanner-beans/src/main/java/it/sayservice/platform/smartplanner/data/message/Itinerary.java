/**
 *    Copyright 2011-2016 SAYservice s.r.l.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.sayservice.platform.smartplanner.data.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An itinerary between two locations.<br/>
 * <br/>
 * An example in json format:<br/>
 * <br/> 
 * {"duration":1553000,"from":{"name":"Via Milano","lat":"46.06180521073646","lon":"11.129310376891326","stopId":{"id":"","agencyId":""},"stopCode":"null"},"to":{"name":"Via alla Cascata","lat":"46.0685896","lon":"11.1509119","stopId":{"id":"","agencyId":""},"stopCode":"null"},"greenPoint":null,"startime":1355390817000,"endtime":1355392370000,"leg":[{"duration":122000,"from":{"name":"Via Milano","lat":"46.06180521073646","lon":"11.129310376891326","stopId":{"id":"","agencyId":""},"stopCode":"null"},"to":{"name":"dei Mille  \"Villa Igea\"","lat":"46.062506","lon":"11.127984","stopId":{"id":"21275x","agencyId":"12"},"stopCode":"null"},"transport":{"type":"WALK","agencyId":"null","routeId":"","tripId":"null"},"startime":1355390817000,"endtime":1355390939000,"legId":"null_null","legGeometery":{"length":7,"levels":"null","points":"gmcxGeu|bAPZHLIL_BhCILc@x@"},"alertStrikeList":[],"alertDelayList":[],"alertParkingList":[],"alertCapacityList":null},{"duration":120000,"from":{"name":"dei Mille  \"Villa Igea\"","lat":"46.062506","lon":"11.127984","stopId":{"id":"21275x","agencyId":"12"},"stopCode":"null"},"to":{"name":"S.Francesco  Porta Nuova","lat":"46.067075","lon":"11.126226","stopId":{"id":"21595x","agencyId":"12"},"stopCode":"null"},"transport":{"type":"BUS","agencyId":"12","routeId":"7","tripId":"07R-Feriale_025"},"startime":1355390940000,"endtime":1355391060000,"legId":"12_07R-Feriale_025","legGeometery":{"length":11,"levels":"null","points":"mqcxGul|bAA?uBdCUf@aAm@sJcECAaAc@eCzNsAs@g@W"},"alertStrikeList":[],"alertDelayList":[],"alertParkingList":[],"alertCapacityList":null}
 */



public class Itinerary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4313807067301934095L;
	
	/**
	 * Start location
	 */
	private Position from;
	
	/**
	 * End location
	 */
	private Position to;
	
	/**
	 * Start time
	 */
	private long startime;
	
	/**
	 * End time
	 */
	private long endtime;
	
	/**
	 * Itinerary duration
	 */
	private long duration;
	
	/**
	 * Walk duration
	 */
	private long walkingDuration;
	
	/**
	 * List of legs the itineraty is composed by
	 */
	private List<Leg> leg;
	
	/**
	 * Generated by a request;
	 */
	private boolean promoted = false;
	
	/**
	 * Additional data
	 */
	private Map<String, Object> customData;
	
	
	public Itinerary(Position from, Position to,
			long startime, long endtime, long duration, long walkDuration,
			List<it.sayservice.platform.smartplanner.data.message.Leg> leg) {
		super();
		this.from = from;
		this.to = to;
		this.startime = startime;
		this.endtime = endtime;
		this.duration = duration;
		this.walkingDuration = walkDuration;
		this.leg = leg;
		customData = new HashMap<String, Object>();
	}

	public Itinerary() {
		super();
		customData = new HashMap<String, Object>();
		// TODO Auto-generated constructor stub
	}

	public Position getFrom() {
		return from;
	}

	public void setFrom(Position from) {
		this.from = from;
	}

	public Position getTo() {
		return to;
	}

	public void setTo(Position to) {
		this.to = to;
	}

	public long getStartime() {
		return startime;
	}

	public void setStartime(long startime) {
		this.startime = startime;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public List<Leg> getLeg() {
		return leg;
	}

	public void setLeg(List<Leg> leg) {
		this.leg = leg;
	}
	public long getWalkingDuration() {
		return walkingDuration;
	}

	public void setWalkingDuration(long walkingDuration) {
		this.walkingDuration = walkingDuration;
	}

	public boolean isPromoted() {
		return promoted;
	}

	public void setPromoted(boolean promoted) {
		this.promoted = promoted;
	}

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public void setCustomData(Map<String, Object> customData) {
		this.customData = customData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + (int) (endtime ^ (endtime >>> 32));
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((leg == null) ? 0 : leg.hashCode());
		result = prime * result + (int) (startime ^ (startime >>> 32));
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + (int) (walkingDuration ^ (walkingDuration >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Itinerary other = (Itinerary) obj;
		if (duration != other.duration)
			return false;
		if (endtime != other.endtime)
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (leg == null) {
			if (other.leg != null)
				return false;
		} else if (!leg.equals(other.leg))
			return false;
		if (startime != other.startime)
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (walkingDuration != other.walkingDuration)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Itinerary [from=" + from + ", to=" + to + ", startime=" + startime
				+ ", endtime=" + endtime + ", duration=" + duration
				+ ", walkingDuration=" + walkingDuration + ", leg=" + leg + "]";
	}
	
}