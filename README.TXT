Project: predator-prey-v2
Authors: Michael Kölling, David J. Barnes, Mahdi Razzaque and Ozgur Dorunay

This project is part of the material for chapter 10 of the book

   Objects First with Java - A Practical Introduction using BlueJ
   Seventh edition
   David J. Barnes and Michael Kölling

A predator-prey simulation involving various entities within
an enclosed rectangular field.

This is the second version of the simulation. This version
makes use of inheritance for the animals.

Key Features:

    *   Multiple Species: Wolves, Bobcats, Squirrels, Grouse, Seeds, and Berries.
    *   Predation: Wolves hunt Squirrels and Grouse; Bobcats hunt Squirrels.
    *   Plant Growth: Seeds and Berries with individual growth, reproduction, and lifespan.
    *   Time of Day: Grouse activity is influenced by day and night cycles.
    *   Weather: Real-time weather data from an external API (weatherapi.com) affects plant growth.
    *   Disease: Simulation of disease spread and impact on animal populations.

How to start:
    Create a Simulator object.
    Then call one of:
        + simulateOneStep - for a single step.
        + simulate - and supply a number (say 10) for that many steps.
        + runLongSimulation - for a simulation of 700 steps.

Key Classes:

    *   Simulator: Main class for controlling the simulation.
    *   SimulatorView: Provides a graphical view of the simulation.
    *   Field: Represents the simulation grid.
    *   Entity: Abstract class for all simulation participants (animals and plants).
    *   Animal: Abstract class for animals with breeding and feeding behaviour.
    *   Plant: Abstract class for plants with growth, reproduction, and spread behaviour.
    *   Wolf, Bobcat, Squirrel, Grouse: Concrete animal classes.
    *   Seeds, Berries: Concrete plant classes.
    *   Time: Manages the simulation's time.
    *   Weather: Fetches and manages weather conditions.
    *   Disease: Abstract class for simulating disease dynamics.
    *   Flu, Rabies: Concrete disease classes.

Additional Notes:

    *   The weather API key is hardcoded in the Weather class for simplicity. In a production environment, it should be stored as an environment variable.
    *   Disease dynamics are still under development.
    *   Population balance can be difficult to achieve and may require careful tuning of initial parameters.