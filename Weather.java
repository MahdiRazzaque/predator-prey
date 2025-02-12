import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Weather {
    private final Random random = new Random();
    private static final String API_URL = "http://api.weatherapi.com/v1/current.json";
    private final String apiKey;
    private final String location = "London";

    private Simulator simulator;
    private int lastStepUpdated = 0;
    private final int stepIntervalUpdate = 5;
    private String currentWeatherText;
    private String currentWeatherEmoji;

    public Weather(Simulator simulator) {
        this.apiKey = "14119c62d6b340b6a20143053251102";
        this.simulator = simulator;
    }

    public String getWeatherText() {
        if(!isEnoughStepsPassed()) {
            return currentWeatherText;
        }

        try {
            String weatherData = fetchWeatherData();

            if (!isValidResponse(weatherData)) {
                getRandomWeather();
                return currentWeatherText;
            }


            currentWeatherText = mapConditionTextToWeatherType(getWeatherDescription(weatherData));
            return currentWeatherText;

        } catch (Exception e) {
            getRandomWeather();
            return currentWeatherText;
        }
    }

    public String getWeatherEmoji() {
        if(!isEnoughStepsPassed())
            return currentWeatherEmoji;

        currentWeatherEmoji = mapWeatherTextToWeatherEmoji(currentWeatherText);

        if(currentWeatherEmoji.equals("Unknown"))
            getRandomWeather();

        return currentWeatherEmoji;
    }

    public String getWeatherTextAndEmoji() {
        return getWeatherText() + " " + getWeatherEmoji();
    }

    public void getRandomWeather() {
        if(!isEnoughStepsPassed())
            return;

        String[] weatherTexts = {"Sunny", "Rainy", "Snowy", "Cloudy"};

        currentWeatherText = weatherTexts[random.nextInt(weatherTexts.length)];
        currentWeatherEmoji = mapWeatherTextToWeatherEmoji(currentWeatherText);
    }

    private boolean isEnoughStepsPassed() {
        int step = simulator.getStep();
        System.out.println("Steps in weather: " + step);
        if(step < lastStepUpdated)
            return false;

        lastStepUpdated = step;

        return step % stepIntervalUpdate == 0;
    }

    private String fetchWeatherData() throws Exception {
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

    private boolean isValidResponse(String response) {
        return !response.contains("\"error\":");
    }

    private String getWeatherDescription(String jsonData) {
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

    private String mapConditionTextToWeatherType(String conditionText) {
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

    private String mapWeatherTextToWeatherEmoji(String weatherText) {
        return switch (weatherText) {
            case "Sunny" -> "☀️";
            case "Rainy" -> "🌧️";
            case "Snowy" -> "❄️";
            case "Cloudy" -> "☁️";
            default -> "Unknown";
        };
    }
}