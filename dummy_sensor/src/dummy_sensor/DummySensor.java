package dummy_sensor;

// Required imports
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

// Convert Java Map to JSON
import com.google.gson.Gson;

public class DummySensor {

	public static void main(String[] args) 
	{
		final DummySensor ds = new DummySensor();
		
		Timer t = new Timer();
		
		// Every 5 seconds, make a POST to the given URL.
		t.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() 
		    {
		    	try
				{
					ds.makePOST(ds.generatePayload(), "https://postman-echo.com/post");
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
		    }
		}, 1000,5000);
		
		
	}

	// Generate the dummy payload.
	private String generatePayload()
	{
		Map<String, Object> keyValuePairs = new HashMap<String, Object>();
		keyValuePairs.put("sensorID", 1234);
		keyValuePairs.put("sensorPayload", 96);
		keyValuePairs.put("UTC", Instant.now().toEpochMilli());
		
		Gson gson = new Gson(); 

		return gson.toJson(keyValuePairs); 
	}
	
	// Make a POST request with given requestBody and URL.
	private void makePOST(String requestBody, String URL) throws IOException
	{
		URL url = new URL(URL);
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("POST");
		OutputStreamWriter out = new OutputStreamWriter(
				httpCon.getOutputStream());
		System.out.println("POST " + URL + " " + requestBody);
		out.write(requestBody);
		System.out.println(httpCon.getResponseCode() + " " + httpCon.getResponseMessage());
		out.close();
	}
	
}
