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

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Simple MongoDB client based on the MongoDB Java driver API.
 */
public class MyMongoClient {

	/**
	 * CLI call.
	 * 
	 * @param argv
	 *            command line arguments
	 * @throws MongoException
	 * @throws UnknownHostException
	 */
	public static void main(String[] argv) throws UnknownHostException, MongoException {
		Mongo m = new Mongo("localhost"); // default port 27017
		DB db = m.getDB("test");

		// get collection names
		Set<String> colls = db.getCollectionNames();
		for (String s : colls) {
			out(s);
		}

		// insert a simple doc
		DBCollection coll = db.getCollection("bikeStation");
		DBObject doc = new BasicDBObject();
		doc.put("date", new Date());
		coll.save(doc);
	}

	private static final void out(Object o) {
		System.out.println(o);
	}

}
