/*
       Copyright 2017 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ibm.hybrid.cloud.sample.portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//JSON-P (JSR 353).  The replaces my old usage of IBM's JSON4J (package com.ibm.json.java)
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonStructure;

public class PortfolioServices {
	private static final String PORTFOLIO_SERVICE = "http://portfolio-service:9080/portfolio";

	public static JsonArray getPortfolios() {
		JsonArray portfolios = null;

		try {
			portfolios = (JsonArray) invokeREST("GET", PORTFOLIO_SERVICE);
		} catch (Throwable t) {
			t.printStackTrace();

			//return an empty (but not null) array if anything went wrong
			JsonArrayBuilder builder = Json.createArrayBuilder();
			portfolios = builder.build();
		}

		return portfolios;
	}

	public static JsonObject getPortfolio(String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST("GET", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject createPortfolio(String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST("POST", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject updatePortfolio(String owner, String symbol, int shares) {
		JsonObject portfolio = null;

		try {
			String uri = PORTFOLIO_SERVICE+"/"+owner+"?symbol="+symbol+"&shares="+shares;
			portfolio = (JsonObject) invokeREST("PUT", uri);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject deletePortfolio(String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST("DELETE", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	private static JsonStructure invokeREST(String verb, String uri) throws IOException {
		System.out.println(verb+" "+uri);

		URL url = new URL(uri);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(verb);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		InputStream stream = conn.getInputStream();

//		JSONObject json = JSONObject.parse(stream); //JSON4J
		JsonStructure json = Json.createReader(stream).read();

		stream.close();

		return json; //I use JsonStructure here so I can return a JsonObject or a JsonArray
	}
}
