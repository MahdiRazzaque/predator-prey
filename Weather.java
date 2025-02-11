import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Weather {

    private static final String API_URL = "http://api.weatherapi.com/v1/current.json";
    private final String apiKey;
    private final String location = "London";

    public Weather() {
        this.apiKey = "14119c62d6b340b6a20143053251102";
    }

    public String fetchWeatherData() throws Exception {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
        String urlString = String.format("%s?key=%s&q=%s", API_URL, apiKey, encodedLocation);

        System.out.println(urlString);

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("API request failed with status code: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } finally {
            connection.disconnect();
        }
        System.out.println(response.toString());
        return response.toString();
    }

    public boolean isValidResponse(String response) {
        return !response.contains("\"error\":");
    }

    public String getWeatherDescription(String jsonData) {
        try {
            // Find the "text" key
            String textKey = "\"text\":\"";
            int textIndex = jsonData.indexOf(textKey);
            if (textIndex == -1) {
                System.err.println("Could not find 'text' in JSON data.");
                return "Could not determine weather.";
            }

            // Extract the weather text value
            int startIndex = textIndex + textKey.length();
            int endIndex = jsonData.indexOf("\"", startIndex);  // Find the closing quote

            if (endIndex == -1) {
                System.err.println("Could not find end of text value.");
                return "Could not determine weather.";
            }

            return jsonData.substring(startIndex, endIndex);

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return "An error occurred.";
        }
    }

    public String getWeatherText() {
        try {
            String weatherData = fetchWeatherData();

            if (!isValidResponse(weatherData))
                return "Invalid API response.";

            return mapConditionTextToWeatherType(getWeatherDescription(weatherData)); // map it!

        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }

    public String getWeatherEmoji() {
        try {
            String weatherData = fetchWeatherData();

            if (!isValidResponse(weatherData))
                return "";

            String conditionText = getWeatherDescription(weatherData);

            return mapConditionTextToWeatherEmoji(conditionText);

        } catch (Exception e) {
            return "";
        }
    }

    public String getWeatherTextAndEmoji() {
        return getWeatherText() + " " + getWeatherEmoji();
    }

    public String mapConditionTextToWeatherType(String conditionText) {
        String lowerCaseDescription = conditionText.toLowerCase();
        if (lowerCaseDescription.contains("sunny") || lowerCaseDescription.contains("clear")) {
            return "Sunny";
        } else if (lowerCaseDescription.contains("rain") || lowerCaseDescription.contains("drizzle") || lowerCaseDescription.contains("torrential")) {
            return "Rainy";
        } else if (lowerCaseDescription.contains("snow") || lowerCaseDescription.contains("sleet") || lowerCaseDescription.contains("ice") || lowerCaseDescription.contains("blizzard")) {
            return "Snowy";
        } else if (lowerCaseDescription.contains("cloud") || lowerCaseDescription.contains("overcast") || lowerCaseDescription.contains("mist") || lowerCaseDescription.contains("fog")) {
            return "Cloudy";
        } else {
            return "Unknown";
        }
    }

    private String mapConditionTextToWeatherEmoji(String conditionText) {
        String lowerCaseDescription = conditionText.toLowerCase();
        if (lowerCaseDescription.contains("sunny") || lowerCaseDescription.contains("clear")) {
            return "☀️";
        } else if (lowerCaseDescription.contains("rain") || lowerCaseDescription.contains("drizzle") || lowerCaseDescription.contains("torrential")) {
            return "🌧️";
        } else if (lowerCaseDescription.contains("snow") || lowerCaseDescription.contains("sleet") || lowerCaseDescription.contains("ice") || lowerCaseDescription.contains("blizzard")) {
            return "❄️";
        } else if (lowerCaseDescription.contains("cloud") || lowerCaseDescription.contains("overcast") || lowerCaseDescription.contains("mist") || lowerCaseDescription.contains("fog")) {
            return "☁️";
        } else {
            return "";
        }
    }
}