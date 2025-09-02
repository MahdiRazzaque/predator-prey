# Predator-Prey Simulation

This project is a discrete-time simulation of a predator-prey ecosystem. It models a self-contained environment populated by various species of animals and plants, each governed by a set of configurable attributes. The simulation features dynamic environmental factors, such as weather and disease, that influence the behavior and survival of the entities.

A key component of this repository is the inclusion of a Python script (`simulator_control.py`) that leverages the Google Gemini API to automate the tuning of simulation parameters, aiming to achieve a stable ecosystem.

### Interesting Techniques

*   **Polymorphic Entity Management**: The simulation uses an abstract `Entity` superclass to manage all actors (animals and plants). This allows the main simulation loop to process a heterogeneous list of objects through a shared `act()` method, simplifying the core logic. Subclasses like [`Animal`](./Animal.java) and [`Plant`](./Plant.java) extend this base functionality.
*   **API Resilience**: The [`Weather`](./Weather.java) class fetches real-time weather data. It includes a fallback mechanism that defaults to randomly generated weather conditions if the external API call fails. This ensures the simulation remains functional even without a network connection or if the API service is down.
*   **Type-Safe Enumerations**: Genders are handled using a `Gender.java` enum, which provides compile-time safety and a clean, readable way to manage male and female individuals and their breeding logic.
*   **Decoupled View**: The simulation logic in [`Simulator`](./Simulator.java) is separated from the UI rendering in [`SimulatorView`](./SimulatorView.java), following a pattern similar to Model-View-Controller. This separation makes it easier to modify the simulation's rules without affecting the visualization code.

### Technologies and Libraries

*   **Java SE**: The core simulation is built with standard Java without major external frameworks.
*   **WeatherAPI**: The optional weather system integrates with [weatherapi.com](https://www.weatherapi.com/) for real-time weather data. [3]
*   **Google Gemini**: The [`simulator_control.py`](./simulator_control.py) script uses the [Google Gemini API](https://ai.google.dev/) to analyze simulation output and suggest adjustments to entity attributes for achieving population balance. [1]

### Project Structure

```
.
├── report/
├── .gitattributes
├── .gitignore
├── Animal.java
├── Berries.java
├── Bobcat.java
├── Disease.java
├── Entity.java
├── Field.java
├── FieldStats.java
├── Flu.java
├── Gender.java
├── Grouse.java
├── Location.java
├── Main.java
├── Plant.java
├── Rabies.java
├── Randomizer.java
├── Seeds.java
├── simulator_control.py
├── Simulator.java
├── SimulatorView.java
├── Squirrel.java
├── Time.java
├── Weather.java
└── Wolf.java
```

*   **`report/`**: Contains the LaTeX source for the project report, detailing the simulation's design and implementation.
*   **`simulator_control.py`**: An automation script for tuning simulation parameters using an external AI model.
