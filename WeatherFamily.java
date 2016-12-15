import realtimeweb.weatherservice.*;
import realtimeweb.weatherservice.domain.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Date;

import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.smtp.*;


public class WeatherFamily {
// 
//  This program has a list of cities that it will look up
//       find the forecast for the cities and email to the interested person.
//
//	This is working with the sample data provided using cities that exist 
//	in the JSON data.
//
//  My goal is to modify this to get real information on the cites that my Grandpa is
//     interested in and send him a daily email, so he can just check the email instead
//     of checking multiple sites. 
//	
//	The cities I'd like to check are Sylmar CA, San Diego CA, and Curaglia Switzerland.
//	
	 
	public static void main(String[] args) {
   
		// Array of cities
    	ArrayList<String> cities = new ArrayList<String>();
    	
    	String EmailMessage = new String();
    	
    	// Set the city names to look for in this method
 	   	setCities(cities);
    	
 	   	// create WeatherService object
        WeatherService nws = new WeatherService("cache.json");
        
        for (String City:cities) {
        	System.out.println("Forecast for: " + City);
	        //	
	        //  Add to EmailMessage
	        //
        	EmailMessage = EmailMessage+"Forecast for: " + City + "\n";
        	
        	Report current = nws.getReport(City);
	        ArrayList<Forecast> forecasts = current.getForecasts();
	        for (Forecast fc : forecasts){
	        	System.out.print(fc.getPeriodName()); 				// Friday Night
	        	System.out.print(":\t" + fc.getTemperature()); 		// 78
	        	System.out.println(" and " + fc.getDescription()); 	// "Sunny"
	        
	        //	
	        //  Add to EmailMessage
	        //	
	        	EmailMessage = EmailMessage + fc.getPeriodName();
	        	EmailMessage = EmailMessage + ":\t" + fc.getTemperature();
	        	EmailMessage = EmailMessage + " and " + fc.getDescription()+ "\n";
	        	
	        }
	        EmailMessage = EmailMessage + "\n-------------------------------------\n";
        }
        try {
         	Distribution(EmailMessage);  
        } catch (Exception exc)
        {
        	System.out.println("Error Sending Email"); 
        }   
    }

	private static void setCities(ArrayList cities) 
	{
		// Set Cities to look for.
		cities.add("miami, fl");
		cities.add("newark, de");
		cities.add("seattle, wa");
		cities.add("minneapolis,mn");
	}

	private static void Distribution(String EmailMessage) throws Exception {
		
		// Define variables so I can create a nice looking date/time
			long yourmilliseconds = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");    
			Date resultdate = new Date(yourmilliseconds);
			String displayDate = sdf.format(resultdate);
		
		// Use a junk gmail account I have to send 	
			Properties props = System.getProperties();
	        props.put("mail.smtps.host","smtp.gmail.com");
	        props.put("mail.smtps.auth","true");

	        Session session = Session.getInstance(props, null);
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("fuvallin@gmail.com"));;
	        msg.setRecipients(Message.RecipientType.TO,
	  
	        InternetAddress.parse("sbeeli@gmail.com", false));
	        msg.setSubject("Family Weather Forecast "+displayDate);
	        msg.setText(EmailMessage);

	        msg.setHeader("X-Mailer", "test program");
	        msg.setSentDate(new Date());
	        SMTPTransport t =
	            (SMTPTransport)session.getTransport("smtps");
	        t.connect("smtp.gmail.com", "fuvallin@gmail.com", "sxxxxxx9");
	        t.sendMessage(msg, msg.getAllRecipients());
	        
	        System.out.println("Response: " + t.getLastServerResponse());
	        
	        // I added this to check the return code
	        if (t.getLastReturnCode()== 250) 
	        	{
	        	System.out.println("Email sent at " + displayDate);
	        	}
	        
	        t.close();
	}

}
