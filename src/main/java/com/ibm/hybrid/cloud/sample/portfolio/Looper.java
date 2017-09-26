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

//JAX-RS 2.0 (JSR 339)
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;


@ApplicationPath("/")
@Path("/")
/** Runs a set of Porfolio REST API calls in a loop. */
public class Looper extends Application {
	private static final String ID        = "Looper";
	private static final String SYMBOL1   = "IBM";
	private static final String SYMBOL2   = "AAPL";
	private static final String SYMBOL3   = "GOOG";

	public static void main(String[] args) {
		if (args.length == 1) try {
			Looper looper = new Looper();
			looper.loop(Integer.parseInt(args[0]));
		} catch (Throwable t) {
			t.printStackTrace();
		} else {
			System.out.println("Usage: Looper <count>");
		}
	}

    @GET
    @Path("/")
	@Produces("text/plain")
	public String loop(@QueryParam("count") Integer count) {
    	if (count==null) count=1; //isn't autoboxing cool?
    	StringBuffer response = new StringBuffer();
		long beginning = System.currentTimeMillis();

		for (int index=1; index<=count; index++) {
			response.append("Iteration #"+index+"\n");

			long start = System.currentTimeMillis();

			response.append(iteration());

			long end = System.currentTimeMillis();

			response.append("Elapsed time for iteration #"+index+": "+(end-start)+" ms\n");
		}

		long ending = System.currentTimeMillis();
		double average = ((double) (ending-beginning))/((double) count);

		response.append("Overall average time per iteration: "+average+" ms\n");

		return response.toString();
	}

	public StringBuffer iteration() {
    	StringBuffer response = new StringBuffer();

    	response.append("1:  "+PortfolioServices.getPortfolios()+"\n"); //Summary of all portfolios

    	response.append("2:  "+PortfolioServices.createPortfolio(ID)+"\n"); //Create a new portfolio

    	response.append("3:  "+PortfolioServices.updatePortfolio(ID, SYMBOL1, 1)+"\n"); //Buy stock for this portfolio

    	response.append("4:  "+PortfolioServices.updatePortfolio(ID, SYMBOL2, 2)+"\n"); //Buy stock for this portfolio

    	response.append("5:  "+PortfolioServices.updatePortfolio(ID, SYMBOL3, 3)+"\n"); //Buy stock for this portfolio

    	response.append("6:  "+PortfolioServices.getPortfolio(ID)+"\n"); //Get details of this portfolio

    	response.append("7:  "+PortfolioServices.getPortfolios()+"\n"); //Summary of all portfolios

    	response.append("8:  "+PortfolioServices.updatePortfolio(ID, SYMBOL1, 6)+"\n"); //Buy more of this stock for this portfolio

    	response.append("9:  "+PortfolioServices.updatePortfolio(ID, SYMBOL3, -3)+"\n"); //Sell all of this stock for this portfolio

    	response.append("10: "+PortfolioServices.getPortfolio(ID)+"\n"); //Get details of this portfolio

    	response.append("11: "+PortfolioServices.deletePortfolio(ID)+"\n"); //Remove this portfolio

    	response.append("12: "+PortfolioServices.getPortfolios()+"\n"); //Summary of all portfolios

		return response;
	}
}
