import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Represents the weather in the simulation, fetching data from an external API or using random weather conditions.
 * This class handles retrieving weather information, mapping it to simple weather types, and providing emoji representations.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Weather {
    private final Random random = new Random(); // Random number generator for weather variations.
    // The URL for fetching weather data from the external API.
    private static final String API_URL = "http://api.weatherapi.com/v1/current.json";
    // The API key used to authenticate with the weather API.  Hardcoded here for ease of submission,
    // but ideally, this would be read from a system environment variable for security.
    private final String apiKey = "14119c62d6b340b6a20143053251102";
    private final String location = "London"; // The default location for which weather data is fetched.

    private Simulator simulator; // The simulator instance to access simulation steps.
    private int lastStepUpdated = 0; // The simulation step when the weather was last updated.
    private final int stepIntervalUpdate = 100; // The interval (in steps) between weather updates.
    private String currentWeatherText; // The current weather condition as text (e.g., "Sunny", "Rainy").
    private String currentWeatherEmoji; // The emoji representation of the current weather condition.

    /**
     * Constructs a Weather object, initialising the API key and the simulator instance.
     * @param simulator The simulator instance to which the weather applies.
     */
    public Weather(Simulator simulator) {
        this.simulator = simulator;
    }

    /**
     * Gets the current weather text.
     * If enough simulation steps have passed since the last update, it attempts to fetch weather data from an external API.
     * If the API request fails or returns invalid data, it falls back to using a random weather condition.
     * @return The current weather text (e.g., "Sunny", "Rainy", "Snowy", "Cloudy").
     */
    public String getWeatherText() {
        if(!isEnoughStepsPassed())
            return currentWeatherText; // Returns the cached weather text if not enough steps have passed.

        try {
            String weatherData = fetchWeatherData(); // Fetches weather data from the API.

            if (!isValidResponse(weatherData) || weatherData.contains("Could not determine weather")) {
                getRandomWeather(); // If the API response is invalid, get a random weather condition.
                return currentWeatherText; // Return the (potentially new) weather text.
            }

            currentWeatherText = mapConditionTextToWeatherType(getWeatherDescription(weatherData)); // Maps the API weather description to a simplified text.
            return currentWeatherText; // Returns the weather text derived from the API data.

        } catch (Exception e) {
            getRandomWeather(); // If any exception occurs, get a random weather condition.
            return currentWeatherText; // Returns the (potentially new) weather text.
        }
    }

    /**
     * Gets the current weather emoji corresponding to the current weather text.
     * If enough simulation steps have passed since the last update, it updates the weather emoji.
     * If the weather text is unknown, it falls back to using a random weather condition.
     * @return The current weather emoji (e.g., "☀️", "🌧️", "❄️", "☁️").
     */
    public String getWeatherEmoji() {
        if(!isEnoughStepsPassed())
            return currentWeatherEmoji; // Returns the cached weather emoji if not enough steps have passed.

        currentWeatherEmoji = mapWeatherTextToWeatherEmoji(currentWeatherText); // Maps the current weather text to its emoji representation.

        if(currentWeatherEmoji.equals("Unknown"))
            getRandomWeather(); // If the emoji is unknown, get a random weather condition.

        return currentWeatherEmoji; // Returns the current weather emoji (potentially updated).
    }

    /**
     * Gets the current weather text and emoji combined into a single string.
     * @return The current weather text and emoji.
     */
    public String getWeatherTextAndEmoji() {
        return getWeatherText() + " " + getWeatherEmoji(); // Concatenates the weather text and emoji.
    }

    /**
     * Sets a random weather condition for the simulation.
     * If enough simulation steps have passed since the last update, it updates the weather text
     * and emoji with a randomly selected condition.
     */
    public void getRandomWeather() {
        if(!isEnoughStepsPassed())
            return; // Returns early if not enough steps have passed since the last update.

        String[] weatherTexts = {"Sunny", "Rainy", "Snowy", "Cloudy"}; // Array of possible weather conditions.

        currentWeatherText = weatherTexts[random.nextInt(weatherTexts.length)]; // Randomly selects a weather condition from the array.
        currentWeatherEmoji = mapWeatherTextToWeatherEmoji(currentWeatherText); // Updates the weather emoji based on the new weather text.
    }

    /**
     * Checks if enough simulation steps have passed since the last weather update, based on the configured `stepIntervalUpdate`.
     * This prevents excessive API calls or random weather changes on every simulation step.
     * @return {@code true} if enough steps have passed, {@code false} otherwise.
     */
    private boolean isEnoughStepsPassed() {
        int step = simulator.getStep(); // Gets the current simulation step.

        if(step < lastStepUpdated)
            return false; // Handles cases where the simulation might have moved backwards in time.

        lastStepUpdated = step; // Updates the last updated step.
        return step % stepIntervalUpdate == 0; // Checks if the update interval has been reached.
    }

    /**
     * Fetches weather data from an external API.
     * @return The JSON response string from the API.
     * @throws Exception If the API request fails.
     */
    private String fetchWeatherData() throws Exception {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString()); // Encodes the location for use in the URL.
        String urlString = String.format("%s?key=%s&q=%s", API_URL, apiKey, encodedLocation); // Constructs the complete API URL.

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("API request failed with status code: " + responseCode); // Throws an exception if the API request fails.
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line); // Appends each line of the response to the StringBuilder.
            }
        } finally {
            connection.disconnect(); // Closes the connection to the API.
        }
        return response.toString(); // Returns the JSON response string.
    }

    /**
     * Checks if the API response is valid (i.e., does not contain an error).
     * A simple check to see if the response received from the weather API contains an "error" key,
     * indicating a problem with the request or the API itself.
     * @param response The JSON response string from the API.
     * @return {@code true} if the response is valid (does not contain "error"), {@code false} otherwise.
     */
    private boolean isValidResponse(String response) {
        return !response.contains("\"error\":"); // Returns true if the response does not contain an "error" key.
    }

    /**
     * Extracts the weather description from the JSON data.
     * This method attempts to locate and extract the "text" value from the JSON response,
     * which provides a textual description of the current weather condition.  It uses basic string manipulation to avoid external libraries,
     * due to uncertainty about how BlueJ would handle external dependencies.
     * @param jsonData The JSON data string.
     * @return The weather description, or a default message if it cannot be determined.
     */
    private String getWeatherDescription(String jsonData) {
        try {
            // Find the "text" key
            String textKey = "\"text\":\"";
            int textIndex = jsonData.indexOf(textKey);
            if (textIndex == -1) {
                System.err.println("Could not find 'text' in JSON data.");
                return "Could not determine weather."; // Returns a default message if the "text" key is not found.
            }

            // Extract the weather text value
            int startIndex = textIndex + textKey.length();
            int endIndex = jsonData.indexOf("\"", startIndex);  // Find the closing quote

            if (endIndex == -1) {
                System.err.println("Could not find end of text value.");
                return "Could not determine weather."; // Returns a default message if the closing quote is not found.
            }

            return jsonData.substring(startIndex, endIndex); // Extracts and returns the weather description.

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return "Could not determine weather."; // Returns a default message if an error occurs
        }
    }

    /**
     * Maps a condition text from the weather API to a simplified weather type.
     * This method simplifies the potentially complex descriptions provided by the weather API into one of a few basic categories.
     * @param conditionText The condition text from the weather API.
     * @return A simplified weather type (e.g., "Sunny", "Rainy", "Snowy", "Cloudy", "Unknown").
     */
    private String mapConditionTextToWeatherType(String conditionText) {
        String lowerCaseDescription = conditionText.toLowerCase();
        if (lowerCaseDescription.contains("sunny") || lowerCaseDescription.contains("clear")) {
            return "Sunny"; // Returns "Sunny" if the description contains "sunny" or "clear".
        } else if (lowerCaseDescription.contains("rain") || lowerCaseDescription.contains("drizzle") || lowerCaseDescription.contains("torrential")) {
            return "Rainy"; // Returns "Rainy" if the description contains any rain-related terms.
        } else if (lowerCaseDescription.contains("snow") || lowerCaseDescription.contains("sleet") || lowerCaseDescription.contains("ice") || lowerCaseDescription.contains("blizzard")) {
            return "Snowy"; // Returns "Snowy" if the description contains any snow-related terms.
        } else if (lowerCaseDescription.contains("cloud") || lowerCaseDescription.contains("overcast") || lowerCaseDescription.contains("mist") || lowerCaseDescription.contains("fog")) {
            return "Cloudy"; // Returns "Cloudy" if the description contains any cloud-related terms.
        } else {
            return "Unknown"; // Returns "Unknown" if no match is found.
        }
    }

    /**
     * Maps a weather text to its corresponding emoji representation.
     * Provides a simple mapping between the simplified weather types and their representative emojis for display in the simulation.
     * @param weatherText The weather text (e.g., "Sunny", "Rainy", "Snowy", "Cloudy").
     * @return The corresponding emoji representation, or "Unknown" if no mapping is found.
     */
    private String mapWeatherTextToWeatherEmoji(String weatherText) {
        return switch (weatherText) {
            case "Sunny" -> "☀️"; // Maps "Sunny" to the sun emoji.
            case "Rainy" -> "🌧️"; // Maps "Rainy" to the rain emoji.
            case "Snowy" -> "❄️"; // Maps "Snowy" to the snow emoji.
            case "Cloudy" -> "☁️"; // Maps "Cloudy" to the cloud emoji.
            default -> "Unknown"; // Returns "Unknown" if no mapping is found.
        };
    }
}