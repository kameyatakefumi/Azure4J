package com.mycompany.azure4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.net.URI;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class CustomVisionService {

    public static void main(String[] args) throws TwitterException, URISyntaxException, IOException {

        List<Status> tweets = searchTwitter("#三国志大戦登用 -rt -bot", "2017-10-28");
        for (Status tweet : tweets) {

            System.out.println("ID:" + tweet.getId());

            for (MediaEntity entity : tweet.getMediaEntities()) {

                System.out.println(entity.getMediaURL());

                String jsonString = getProbability(entity.getMediaURL());
                JSONObject json = new JSONObject(jsonString);

                for (Object object : json.getJSONArray("Predictions").toList()) {
                    HashMap prediction = (HashMap) object;
                    System.out.println(prediction.get("Tag") + ":" + prediction.get("Probability"));
                }

            }
            System.out.println("----------------------------------------------");
        }
    }

    public static List<Status> searchTwitter(String word, String since) throws TwitterException {

        Query query = new Query(word);
        query.setSince(since);

        Twitter twitter = new TwitterFactory().getInstance();
        QueryResult result = twitter.search(query);
        List<Status> tweets = result.getTweets();

        return tweets;
    }

    // Custom Vision Services
    public static final String URL = "";
    public static final String PREDICTION_KEY = "";
    public static final String CONTENT_TYPE = "application/json";

    public static String getProbability(String mediaURL) throws URISyntaxException, IOException {

        URIBuilder uriBuilder = new URIBuilder(URL);

        URI uri = uriBuilder.build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", CONTENT_TYPE);
        request.setHeader("Prediction-Key", PREDICTION_KEY);

        StringEntity requestEntity = new StringEntity("{\"url\":\"" + mediaURL + "\"}");
        request.setEntity(requestEntity);

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String jsonString = EntityUtils.toString(entity);
            return jsonString;
        }

        return null;
    }

}
