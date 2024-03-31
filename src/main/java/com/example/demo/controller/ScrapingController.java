package com.example.demo.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

@Controller
public class ScrapingController {
	
	@GetMapping("/scraping")
	public String openText(@RequestParam( name= "url",defaultValue="https://www.google.com/") String linkUrl,Model model) throws IOException {
		
	
		Document doc = Jsoup.connect(linkUrl).get();
		
		Elements cassetteItem = doc.select(".cassetteitem");
		List<String> propertyNameList = new LinkedList<String>();
		List<String> addressList = new LinkedList<String>();
		List<String> informationList = new LinkedList<String>();
		List<String[]> coordinatesList = new LinkedList<String[]>();
		List<String> travelTimeList = new LinkedList<String>();
		List<String> photoImageList = new LinkedList<String>();
		
		for (Element element : cassetteItem) { 
//			System.out.println(element.text());
			String propertyName = element.select(".cassetteitem_content-title").text();
			String address = element.select(".cassetteitem_detail-col1").text();
			String information = element.select(".js-cassette_link").text();
//			
			Element photo = element.selectFirst(".cassetteitem_object-item").selectFirst("img");
			String photoImage = photo.attr("rel");
//			
			if (!element.select(".cassetteitem_transfer-body").isEmpty()) {
				String travelTime = element.select(".cassetteitem_transfer-body").text();
				travelTimeList.add(travelTime);
			}
			
			
			propertyNameList.add(propertyName);
			addressList.add(address);
			informationList.add(information);
			photoImageList.add(photoImage);
			
			OkHttpClient client = new OkHttpClient();
			String apiKey = System.getenv("YOPL_OPEN_API_KEY");
			String place = address;
			String url = "https://map.yahooapis.jp/geocode/V1/geoCoder?appid="+apiKey+"&query="+place+"&output=json";
			Request request = new Request.Builder()
			   .url(url)
			   .build();

			Response response = client.newCall(request).execute();
			String jsonStr =response.body().string();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jsonStr);
			JsonNode firstFeatureNode = rootNode.get("Feature").get(0);


			String coordinates = firstFeatureNode.get("Geometry").get("Coordinates").asText();
			String[] coordinatesArray = coordinates.split(",");

			coordinatesList.add(coordinatesArray);
		}
		model.addAttribute("propertyNameList", propertyNameList);
		model.addAttribute("addressList", addressList);
		model.addAttribute("coordinatesList", coordinatesList);
		model.addAttribute("informationList", informationList);
		model.addAttribute("travelTimeList", travelTimeList);
		model.addAttribute("photoImageList", photoImageList);
		
		return "scraping";
		
	}
}
