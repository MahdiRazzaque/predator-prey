# Predator-Prey Simulation

This project is a discrete-time simulation of a predator-prey ecosystem, developed as part of a first-year programming module at King's College London. It models a self-contained environment populated by various species of animals and plants, each governed by a set of configurable attributes. The simulation features dynamic environmental factors, such as weather and disease, that influence the behaviour and survival of the entities.

A key component of this repository is the inclusion of a Python script, [`simulator_control.py`](./simulator_control.py), that leverages the Google Gemini API to automate the tuning of simulation parameters, aiming to achieve a stable ecosystem.

### Key Features

*   **Diverse Ecosystem**: The simulation includes multiple interacting species such as wolves, bobcats, squirrels, and grouse, alongside plant life like seeds and berries. Each entity has unique, configurable attributes for behaviour, breeding, and survival.
*   **Dynamic Environment**: A real-time weather system, implemented in [`Weather.java`](./Weather.java), fetches data from an external API to influence entity actions, such as plant growth.
*   **Disease Mechanics**: A flexible disease system, with a superclass in [`Disease.java`](./Disease.java), allows for infections like Flu and Rabies to spread among animal populations, impacting their health and survival.
*   **Lifecycle and Breeding**: Animals are assigned a gender using the [`Gender.java`](./Gender.java) enum, have distinct breeding ages, and finite lifespans, which contributes to more realistic population dynamics.
*   **Time Simulation**: The simulation incorporates a day/night cycle, managed by [`Time.java`](./Time.java), which directly affects the behaviour patterns of certain species.

### AI-Powered Parameter Tuning

The repository includes [`simulator_control.py`](./simulator_control.py), a Python script that automates the fine-tuning of simulation parameters. This script orchestrates an iterative process:

1.  It compiles and executes the Java simulation.
2.  The script then parses the final population data from the simulation's output.
3.  This data, along with a representation of the entire codebase, is submitted to the Google Gemini API.
4.  Gemini analyses the ecosystem's state and suggests adjustments to entity attributes, such as breeding probabilities and creation rates, to improve stability.
5.  The script programmatically modifies the Java source files with these new values and begins the next iteration.

This approach experiments with using a large language model to solve complex system balancing problems by treating the simulation's parameters as a configurable space to be explored.

### Interesting Techniques

*   **Polymorphic Entity Management**: An abstract `Entity` superclass is used to manage all actors (animals and plants). This allows the main simulation loop in [`Simulator.java`](./Simulator.java) to process a heterogeneous list of objects through a shared `act()` method, simplifying the core logic.
*   **API Resilience**: The [`Weather.java`](./Weather.java) class includes a fallback mechanism that defaults to randomly generated weather conditions if the external API call fails. This ensures the simulation remains functional without a network connection or if the API service is unavailable.
*   **Type-Safe Enumerations**: Genders are handled using a [`Gender.java`](./Gender.java) enum, which provides compile-time safety and a clean, readable approach to managing male and female individuals and their breeding logic.

### Technologies and Libraries

*   **Java SE**: The core simulation is built with standard Java.
*   **WeatherAPI**: The weather system integrates with [weatherapi.com](https://www.weatherapi.com/) for real-time weather data.
*   **Google Gemini API**: The [`simulator_control.py`](./simulator_control.py) script uses the [Google Gemini API](https://ai.google.dev/) to analyse simulation output and suggest adjustments to entity attributes.

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
