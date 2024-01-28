package myPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Scanner;

import org.apache.jasper.tagplugins.jstl.core.Url;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServelet
 */
public class MyServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//API Setup
		String city = request.getParameter("city");
		String apiKey = "a15ccaca6785856a38fc87af1280e28e";
		
		//Openweather API url
		String appUrl = "https://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+ apiKey;
		
		//API Integration
		URL url = new URL(appUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");	
		
		//Reading the Data from Network
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		
		//Storing in StringBuilder
		StringBuilder responseContent = new StringBuilder();
		
		//To take input from Reader, will create scanner object
		Scanner sc = new Scanner(reader);
		
		while(sc.hasNext()) {
			responseContent.append(sc.nextLine());
		}
		sc.close();
	
		//TypeCasting = Parsing the data into JSON {JavaScript Object Notation]
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		
		//Date & Time
        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
        String date = new Date(dateTimestamp).toString();
        
        //Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		
        //Set the data as request attributes {for sending to the JSP page}
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        
        connection.disconnect();
        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
		//doGet(request, response);
	}

}
