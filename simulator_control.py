# This Python script automates the tuning process for a Java predator-prey simulation using the Google Gemini AI API.
# While the script is functional in compiling, running, parsing simulation data, and applying attribute adjustments suggested by Gemini,
# current iterations have not yet demonstrated effective improvements in achieving simulation stability or resolving ecosystem imbalances.
# The script logs each iteration and provides mechanisms for codebase upload and population balance checks to aid in the tuning effort.

# Author: Mahdi Razzaque
# Version: 14.02.2025

import subprocess
import re
import google.generativeai as genai
import json
import time  # Import the time module
from typing import Dict, Any
from google.generativeai.generative_models import ChatSession  # Import ChatSession
import os
import io

JAVA_MAIN_FILE = "Main.java"
JAVA_CLASS_NAME = "Main"
SIMULATOR_JAVA_FILE = "Simulator.java" # Define Simulator.java file
ANIMAL_FILES = ["Wolf.java", "Bobcat.java", "Squirrel.java", "Grouse.java"] # List of animal files
PLANT_FILES = ["Seeds.java", "Berries.java"] # List of plant files
MAX_ITERATIONS = 10  # Define maximum iterations to prevent infinite loops
STABILITY_THRESHOLD_STEPS = 10 # Define steps to check for stability (currently not used in the stability check, but can be used later)
LOG_FILE_NAME = "attribute_tuning_log.txt" # Define log file name
REPO_MIX_OUTPUT_FILE = "repomix-output.txt" # Define Repomix output file name
REPO_MIX_COMMAND = r"C:\Users\mahdi\AppData\Roaming\npm\repomix.cmd" # Define Repomix command - adjust if needed
USE_REPO_MIX = True # <----- DISABLE REPOMIX PROCESSING and UPLOAD - SET TO FALSE

def compile_java():
    """Compiles the Main.java file."""
    try:
        process = subprocess.run(['javac', JAVA_MAIN_FILE, SIMULATOR_JAVA_FILE] + ANIMAL_FILES + PLANT_FILES, # Compile all files
                                   check=True,
                                   capture_output=True,
                                   text=True)
        print("Java compilation successful.")
        return True
    except subprocess.CalledProcessError as e:
        print(f"Java compilation failed! Error:\n{e.stderr}")
        return False

def run_simulation():
    """Runs simulation, captures output, parses ALL entity counts (including male/female) from LAST FIELD STATE in output."""
    entity_counts: Dict[str, Any] = {}
    try:
        process = subprocess.run(['java', JAVA_CLASS_NAME],
                                   check=True,
                                   capture_output=True,
                                   text=True)
        output = process.stdout

        animal_counts_regex = re.compile(r"\| (\w+) +\| +(\d+) +\| +(\d+) +\| +(\d+) +\|")
        plant_counts_regex = re.compile(r"\| (\w+)\s*\| +(\d+) +\|")

        plant_section_started = False
        plant_header_processed = False
        last_plant_counts = {}

        for line in output.splitlines():
            animal_match = animal_counts_regex.match(line)
            if animal_match:
                entity_type = animal_match.group(1).strip()
                total_count = int(animal_match.group(2).strip())
                male_count = int(animal_match.group(3).strip())
                female_count = int(animal_match.group(4).strip())
                entity_counts[entity_type] = {
                    "total": total_count,
                    "male": male_count,
                    "female": female_count
                }
                plant_section_started = False

            if "| Plant" in line:
                plant_section_started = True
                plant_header_processed = False
                last_plant_counts = {}
                continue

            if plant_section_started:
                if not plant_header_processed and line.startswith("+---"):
                    plant_header_processed = True
                    continue
                if plant_header_processed:
                    plant_match = plant_counts_regex.match(line)
                    if plant_match:
                        entity_type = plant_match.group(1).strip()
                        total_count = int(plant_match.group(2).strip())
                        last_plant_counts[entity_type] = {"total": total_count}
                    elif line.startswith("+---"):
                        entity_counts.update(last_plant_counts)
                        plant_section_started = False

        return entity_counts, output

    except subprocess.CalledProcessError as e:
        print(f"Java simulation failed! Error:\n{e.stderr}")
        return None, None

GEMINI_API_KEY = ''
genai.configure(api_key=GEMINI_API_KEY)
generation_config = genai.GenerationConfig(
    temperature=0.7
)
model = genai.GenerativeModel(model_name="gemini-2.0-flash-thinking-exp-01-21", # <----- CHANGED MODEL HERE
                             generation_config=generation_config)


def generate_repomix_output():
    """Generates repomix output and returns the file content directly."""
    if not USE_REPO_MIX: # <----- CHECK USE_REPO_MIX FLAG
        return None # Return None if not using Repomix

    try:
        process = subprocess.run([REPO_MIX_COMMAND],  # Run repomix command
                                   check=True,
                                   capture_output=True,
                                   text=True)
        print("Repomix output file generated successfully by repomix command.")

        # --- Read the CONTENT of repomix-output.txt ---
        try:
            with io.open(REPO_MIX_OUTPUT_FILE, 'r', encoding='utf-8') as f_codebase: # <----- ADD encoding='utf-8'
                codebase_content = f_codebase.read() # Read the actual codebase content
            print("Codebase content read from repomix-output.txt.")
            return codebase_content  # Return codebase content

        except IOError as e_read: # <----- CORRECT to IOError (Python's IO Exception)
            error_message_read = f"Error reading Repomix output file '{REPO_MIX_OUTPUT_FILE}': {e_read}"
            print(error_message_read)
            return error_message_read  # Return error message if reading fails

    except FileNotFoundError as e_not_found:
        error_message_repo_mix = f"Error: Repomix command '{REPO_MIX_COMMAND}' not found. Make sure it's installed and in your PATH."
        print(error_message_repo_mix)
        return error_message_repo_mix # Return error message if repomix command not found

    except subprocess.CalledProcessError as e_process:
        print(f"Repomix execution failed! Error:\n{e_process.stderr}")
        return None

def get_gemini_attribute_suggestions(entity_counts, chat: ChatSession, last_attributes=None, iteration_num=None, codebase_context=None): # ADD chat: ChatSession
    """
    Sends data to Gemini API in a chat session, gets response, removes backticks and "json", parses JSON.
    """
    prompt_text = f"""
    Predator-prey simulation attribute adjustment task. This is iteration {iteration_num}.

    Simulation Goal: Achieve population stability and ecosystem balance.

    Important Constraints (Do NOT violate):
    - Plant growth rates and reproduction rates MUST be > 0.

    Population Balance Analysis: Check for imbalances, especially plant dominance.

    Current Entity Counts:
    ```
    """
    for entity, counts in entity_counts.items():
        prompt_text += f"- {entity}: Total={counts['total']}"
        if "male" in counts:
            prompt_text += f", Male={counts['male']}, Female={counts['female']}"
        prompt_text += "\n"
    prompt_text += "```\n"

    if last_attributes:
        prompt_text += f"\nAttributes used in the previous simulation run (Iteration {iteration_num - 1}):\n```\n"
        for key, value in last_attributes.items():
            prompt_text += f"- {key}: {value}\n"
        prompt_text += "```\n"

    prompt_text += f"""
    Here is the codebase of the predator-prey simulation, as a single text file named `repomix-output.txt` in the same directory as this prompt:
    ```
    {codebase_context}
    ```

    Analyze this code to understand the simulation's logic, especially focusing on:
    - Potential division by zero errors, particularly related to plant growth and reproduction rates in `Plant.java`, `Seeds.java`, and `Berries.java`.
    - Animal and plant entities and their attributes.
    - How creation and breeding probabilities affect animal populations.
    - How plant attributes like growth rate, reproduction rate, lifespan, and spread rate affect plant populations and indirectly animal populations and overall ecosystem balance.
    - How population imbalances, including plant dominance, might arise from the current attribute settings.

    **Attribute Ranges and Constraints:**

    // === Animal Attributes ===

    1. Breeding Age:
       - Purpose: Minimum age at which an animal can breed.
       - Constraints: Must be ≥ 0; set in subclass constructors.

    2. Maximum Age:
       - Purpose: Age at which the animal dies of old age.
       - Constraints: Must be > breeding age.

    3. Breeding Probability:
       - Purpose: Chance (0.0–1.0) of breeding per step.
       - Constraints: Double between 0.0–1.0.

    4. Maximum Litter Size:
       - Purpose: Max offspring per breeding event.
       - Constraints: ≥ 1.

    // === Plant Attributes ===

    1. Growth Rate:
       - Purpose: Steps between growth stage increments.
       - Constraints: ≥ 0 (to avoid division by zero).

    2. Reproduction Rate:
       - Purpose: Steps between reproduction attempts.
       - Constraints: ≥ 0.

    3. Lifespan:
       - Purpose: Total steps before death (-1 = immortal).
       - Constraints: No upper limit except memory.

    4. Spread Rate:
       - Purpose: Steps between spreading to adjacent tiles.
       - Constraints: ≥ 0 (0 disables spreading).

    5. Growth Start/End Hour:
       - Purpose: Time window (0–23) for growth.
       - Constraints: Valid 24-hour format.

    // === Creation Probability (All Entities) ===
    - Purpose: Likelihood (0.0–1.0) of spawning during initialization.
    - Examples:
      - Wolf: 0.005
      - Berries: 0.05
      - Seeds: 0.08
    - Constraints:
      - Double between 0.0 (never) and 1.0 (always).
      - Sum of probabilities per location should ideally ≤ 1.0.

    // === Global Constraints ===
    1. Division by Zero: Plant growth/reproduction/spread rates must be ≥ 0 if used in modulo operations.
    2. Immortality: Plants with lifespan = -1 bypass age checks.
    3. Gender Logic: Animals require a mate of opposite gender nearby to breed.


    Based on the current entity counts, the population balance analysis, and your code understanding, and considering the attribute ranges and constraints, suggest adjustments in JSON format to these attributes to improve long-term population stability and ecosystem balance. Aim to prevent any population from collapsing, avoid division by zero errors, and reduce extreme population imbalances, especially plant population dominance which is indicated when plant populations are way higher than animal populations. Pay special attention to Grouse and plant populations, and remember the constraint about plant growth and reproduction rates not being zero. If there are population imbalances, especially plant dominance, prioritize adjustments that address these imbalances by controlling plant populations (decreasing their creation, growth, or reproduction rates) or boosting animal populations (especially herbivores like Squirrels and Grouse, or their predators if herbivore populations are too high), to restore a healthier balance between plants and animals.

    Attributes to adjust (JSON response should ONLY include these):
    ```json
    {{
      "WOLF_CREATION_PROBABILITY": ...,
      "BOBCAT_CREATION_PROBABILITY": ...,
      "SQUIRREL_CREATION_PROBABILITY": ...,
      "GROUSE_CREATION_PROBABILITY": ...,
      "SEEDS_CREATION_PROBABILITY": ...,
      "BERRIES_CREATION_PROBABILITY": ...,
      "WOLF_BREEDING_PROBABILITY": ...,
      "BOBCAT_BREEDING_PROBABILITY": ...,
      "SQUIRREL_BREEDING_PROBABILITY": ...,
      "GROUSE_BREEDING_PROBABILITY": ...,
      "WOLF_BREEDING_AGE": ...,
      "BOBCAT_BREEDING_AGE": ...,
      "SQUIRREL_BREEDING_AGE": ...,
      "GROUSE_BREEDING_AGE": ...,
      "WOLF_MAX_AGE": ...,
      "BOBCAT_MAX_AGE": ...,
      "SQUIRREL_MAX_AGE": ...,
      "GROUSE_MAX_AGE": ...,
      "WOLF_MAX_LITTER_SIZE": ...,
      "BOBCAT_MAX_LITTER_SIZE": ...,
      "SQUIRREL_MAX_LITTER_SIZE": ...,
      "GROUSE_MAX_LITTER_SIZE": ...,
      "SEEDS_GROWTH_RATE": ...,
      "SEEDS_REPRODUCTION_RATE": ...,
      "SEEDS_LIFE_SPAN": ...,
      "SEEDS_SPREAD_RATE": ...,
      "SEEDS_GROWTH_START_HOUR": ...,
      "SEEDS_GROWTH_END_HOUR": ...,
      "BERRIES_GROWTH_RATE": ...,
      "BERRIES_REPRODUCTION_RATE": ...,
      "BERRIES_LIFE_SPAN": ...,
      "BERRIES_SPREAD_RATE": ...,
      "BERRIES_GROWTH_START_HOUR": ...,
      "BERRIES_GROWTH_END_HOUR": ...,
      "reasoning": "..."
    }}
    ```

    Explain your reasoning for each suggested adjustment in the "reasoning" field, and be concise. Specifically mention if you considered the division by zero constraint for plant growth and reproduction rates, and if you addressed any population imbalances, especially potential plant dominance.
    """

    try:
        response = chat.send_message(prompt_text) # Send message using chat object
        gemini_output = response.text

        gemini_output_stripped = gemini_output.strip().strip('`').strip()
        if gemini_output_stripped.lower().startswith("json"):
            gemini_output_stripped = gemini_output_stripped[4:].lstrip()

        try:
            suggested_attributes = json.loads(gemini_output_stripped)
            print("\nGemini Suggested Attributes (Parsed JSON):")
            for key, value in suggested_attributes.items():
                if key != "reasoning": # Don't print reasoning in attribute list
                    print(f"- {key}: {value}")
            if "reasoning" in suggested_attributes: # Print reasoning if available
                print(f"\nReasoning from Gemini: {suggested_attributes['reasoning']}")
            return suggested_attributes

        except json.JSONDecodeError as e:
            print(f"Error parsing JSON response from Gemini (after stripping backticks and 'json'): {e}")
            print("Raw Gemini Output that caused JSON error (after stripping all): \n", gemini_output_stripped)
            return None

    except Exception as e:
        print(f"Error communicating with Gemini API: {e}")
        return None

def modify_java_source(suggested_attributes, log_file):
    """
    Modifies the Java source files to update attribute values based on Gemini suggestions - DEBUGGING VERSION.
    """
    try:
        # --- Modify Simulator.java for creation probabilities ---
        #print("[MODIFY JAVA DEBUG] Modifying Simulator.java...") # <----- DEBUG PRINT - Function start
        with open(SIMULATOR_JAVA_FILE, 'r') as f:
            simulator_content = f.read()

        log_file.write("\n--- Attribute Modifications: ---\n") # Log attribute modifications
        for attribute, value in suggested_attributes.items():
            if attribute.endswith("_CREATION_PROBABILITY"):
                variable_name = attribute
                if value is not None: # <----- CHECK FOR None BEFORE CONVERSION
                    try:
                        new_value = float(value)
                        regex = rf"private static double {variable_name} = (\d+\.\d+);"
                        replacement = rf"private static double {variable_name} = {new_value};"
                        simulator_content = re.sub(regex, replacement, simulator_content)
                        log_file.write(f"Updated {variable_name} in {SIMULATOR_JAVA_FILE} to {new_value}\n")
                        print(f"Updated {variable_name} in {SIMULATOR_JAVA_FILE} to {new_value}")
                    except ValueError:
                        log_file.write(f"Warning: Invalid value '{value}' for attribute '{attribute}'. Skipping.\n")
                        print(f"Warning: Invalid value '{value}' for attribute '{attribute}'. Skipping.") # Corrected to Python print
                        quit()
                else:
                    log_file.write(f"Warning: No value provided for attribute '{attribute}'. Skipping.\n") # Log warning for None value
                    print(f"Warning: No value provided for attribute '{attribute}'. Skipping.") # Corrected to Python print

        try: # <----- TRY-EXCEPT BLOCK FOR FILE WRITING
            with open(SIMULATOR_JAVA_FILE, 'w') as f:
                f.write(simulator_content)
            #print(f"[MODIFY JAVA DEBUG] Successfully wrote to {SIMULATOR_JAVA_FILE}") # <----- DEBUG PRINT - File write success
        except IOError as e_write: # <----- CATCH IO Errors during writing
            print(f"[MODIFY JAVA DEBUG] ERROR writing to {SIMULATOR_JAVA_FILE}: {e_write}") # <----- DEBUG PRINT - File write ERROR
            quit()
            return False # Indicate modification failure

        # --- Modify Animal class files for breeding probabilities ---
        animal_breeding_attributes = {
            "WOLF_BREEDING_PROBABILITY": "Wolf.java",
            "BOBCAT_BREEDING_PROBABILITY": "Bobcat.java",
            "SQUIRREL_BREEDING_PROBABILITY": "Squirrel.java",
            "GROUSE_BREEDING_PROBABILITY": "Grouse.java"
        }

        for attribute, filename in animal_breeding_attributes.items():
            if attribute in suggested_attributes:
                #print(f"[MODIFY JAVA DEBUG] Modifying {filename} for {attribute}...") # <----- DEBUG PRINT - Animal file start
                try:
                    with open(filename, 'r') as f:
                        animal_file_content = f.read()

                    variable_name = "BREEDING_PROBABILITY" # Breeding probability variable name is consistent
                    regex = rf"private static final double {variable_name} = (\d+\.\d+);"
                    new_value = float(suggested_attributes[attribute]) # Get value from suggested_attributes and ensure float
                    replacement = rf"private static final double {variable_name} = {new_value};"
                    animal_file_content = re.sub(regex, replacement, animal_file_content)
                    log_file.write(f"Updated {variable_name} in {filename} to {new_value}\n")
                    print(f"Updated {variable_name} in {filename} to {new_value}")

                    try: # <----- TRY-EXCEPT BLOCK FOR FILE WRITING
                        with open(filename, 'w') as f:
                            f.write(animal_file_content)
                        #print(f"[MODIFY JAVA DEBUG] Successfully wrote to {filename}") # <----- DEBUG PRINT - File write success
                    except IOError as e_write: # <----- CATCH IO Errors during writing
                        print(f"[MODIFY JAVA DEBUG] ERROR writing to {filename}: {e_write}") # <----- DEBUG PRINT - File write ERROR
                        return False # Indicate modification failure
                    #print(f"[MODIFY JAVA DEBUG] Finished modifying {filename} for {attribute} - Success") # <----- DEBUG PRINT - Animal file end (success)

                except FileNotFoundError as e_not_found:
                    print(f"Error: Java source file not found: {filename}")
                    log_file.write(f"Error: Java source file not found: {filename}\n")
                    print("[MODIFY JAVA DEBUG] Function Finished - FileNotFoundError") # <----- DEBUG PRINT - Function end (FileNotFoundError)
                    quit()
                    return False
                except Exception as e:
                    print(f"Error modifying Java source files: {e}")
                    log_file.write(f"Error modifying Java source files: {e}\n")
                    print("[MODIFY JAVA DEBUG] Function Finished - General Exception") # <----- DEBUG PRINT - Function end (General Exception)
                    quit()
                    return False

        # --- Modify Plant class files for plant attributes ---
        plant_attributes_files = {
            "SEEDS_GROWTH_RATE": "Seeds.java",
            "SEEDS_REPRODUCTION_RATE": "Seeds.java",
            "BERRIES_GROWTH_RATE": "Berries.java",
            "BERRIES_REPRODUCTION_RATE": "Berries.java"
        }

        for attribute, filename in plant_attributes_files.items():
            if attribute in suggested_attributes:
                #print(f"[MODIFY JAVA DEBUG] Modifying {filename} for {attribute}...") # <----- DEBUG PRINT - Plant file start
                try:
                    with open(filename, 'r') as f:
                        plant_file_content = f.read()

                    variable_name_parts = attribute.split("_") # Split attribute name to get actual variable name
                    variable_name = variable_name_parts[1] + "_" + variable_name_parts[2] # e.g., GROWTH_RATE from SEEDS_GROWTH_RATE
                    regex = rf"private static final int {variable_name} = (\d+);" # Regex for plant attribute
                    new_value = int(suggested_attributes[attribute]) # Plant attributes are integers
                    replacement = rf"private static final int {variable_name} = {new_value};"
                    plant_file_content = re.sub(regex, replacement, plant_file_content)
                    log_file.write(f"Updated {attribute} in {filename} to {new_value}\n")
                    print(f"Updated {attribute} in {filename} to {new_value}")

                    try: # <----- TRY-EXCEPT BLOCK FOR FILE WRITING
                        with open(filename, 'w') as f:
                            f.write(plant_file_content)
                        #print(f"[MODIFY JAVA DEBUG] Successfully wrote to {filename}") # <----- DEBUG PRINT - File write success
                    except IOError as e_write: # <----- CATCH IO Errors during writing
                        print(f"[MODIFY JAVA DEBUG] ERROR writing to {filename}: {e_write}") # <----- DEBUG PRINT - File write ERROR
                        return False # Indicate modification failure
                    #print(f"[MODIFY JAVA DEBUG] Finished modifying {filename} for {attribute} - Success") # <----- DEBUG PRINT - Plant file end (success)

                except FileNotFoundError as e_not_found:
                    print(f"Error: Java source file not found: {filename}")
                    log_file.write(f"Error: Java source file not found: {filename}\n")
                    print("[MODIFY JAVA DEBUG] Function Finished - FileNotFoundError") # <----- DEBUG PRINT - Function end (FileNotFoundError)
                    return False
                except Exception as e:
                    print(f"Error modifying Java source files: {e}")
                    log_file.write(f"Error modifying Java source files: {e}\n")
                    print("[MODIFY JAVA DEBUG] Function Finished - General Exception") # <----- DEBUG PRINT - Function end (General Exception)
                    return False

        print("Java source files modified successfully.")
        log_file.write("Java source files modified successfully.\n")
        #print("[MODIFY JAVA DEBUG] Function Finished - Success reported") # <----- DEBUG PRINT - Function end (success)
        return True

    except FileNotFoundError as e_not_found:
        print(f"Error: One or more Java source files not found.")
        log_file.write("Error: One or more Java source files not found.\n")
        print("[MODIFY JAVA DEBUG] Function Finished - FileNotFoundError") # <----- DEBUG PRINT - Function end (FileNotFoundError)
        return False
    except Exception as e:
        print(f"Error modifying Java source files: {e}")
        log_file.write("Error modifying Java source files: {e}\n")
        print("[MODIFY JAVA DEBUG] Function Finished - General Exception") # <----- DEBUG PRINT - Function end (General Exception)
        return False


def check_population_balance(entity_counts): # <----- REPLACED is_viable WITH check_population_balance
    """
    Checks population balance, now considering plant dominance. Returns a warning message if imbalance is detected, otherwise None.
    """
    all_population_values = [counts['total'] for entity, counts in entity_counts.items() if entity in ["Wolves", "Bobcats", "Squirrels", "Grouse", "Seeds", "Berries"]] # Include plants in balance check
    if not all_population_values: # Avoid division by zero if no populations exist
        return "Warning: No entities present to check population balance."

    average_population = sum(all_population_values) / len(all_population_values)
    imbalanced_entities = [entity for entity, counts in entity_counts.items() if counts['total'] > 5 * average_population] # 5x threshold

    if imbalanced_entities:
        return f"  [Population Balance Check] Warning: High population imbalance detected for: {', '.join(imbalanced_entities)}. Average population (all entities): {average_population:.2f}" # Return warning message
    elif entity_counts.get("Seeds", {}).get("total", 0) > 1000 and entity_counts.get("Berries", {}).get("total", 0) > 1000 and average_population > 0 and (entity_counts.get("Seeds", {}).get("total", 0) + entity_counts.get("Berries", {}).get("total", 0)) > 7 * (sum(counts['total'] for entity, counts in entity_counts.items() if entity in ["Wolves", "Bobcats", "Squirrels", "Grouse"])): # Check plant dominance, adjust 7x threshold as needed # <----- THRESHOLD ADJUSTED TO 7x
        return "Warning: Plant populations (Seeds, Berries) are significantly dominating the ecosystem." # Return plant dominance warning
    else:
        return None # No significant imbalance


def check_stability(entity_counts): # <----- RETAINED check_stability FOR VIABILITY CHECK
    """
    Checks if the simulation is viable (at least one of each animal alive).
    Separate from balance check.
    """
    unstable_entities = []
    animal_entities = ["Wolves", "Bobcats", "Squirrels", "Grouse"] # List of animal entities to check

    for entity_type in animal_entities:
        if entity_type in entity_counts and entity_counts[entity_type]["total"] == 0:
            unstable_entities.append(entity_type)

    if unstable_entities:
        print(f"  [Viability Check] Unstable: {', '.join(unstable_entities)} populations dropped to zero.") # Changed to Viability Check
        return False # Simulation is unstable
    else:
        print("  [Viability Check] Viable: All animal populations are present.") # Changed to Viability Check
        return True # Simulation is stable


if __name__ == "__main__":
    last_attributes_gemini_response = None # To store Gemini response for iterative prompting
    is_stable = False # Flag to track stability
    iteration = 0 # Initialize iteration counter

    with open(LOG_FILE_NAME, 'w', encoding='utf-8') as log_file: # Open log file in write mode at the beginning, ENCODING ADDED
        log_file.write("--- Simulation Tuning Log ---\n\n") # Write log header

        codebase_context = None # Initialize codebase_context to None
        if USE_REPO_MIX: # <----- ONLY GENERATE AND READ REPOMIX OUTPUT IF USE_REPO_MIX IS TRUE
            if not generate_repomix_output(): # Generate Repomix output at first iteration only
                log_file.write("Repomix output generation failed. Aborting.\n") # Log Repomix failure
                print("Repomix output generation failed. Aborting.")
                exit() # Exit if Repomix fails initially

            try: # --- Read Repomix output (once, at the beginning) ---
                with open(REPO_MIX_OUTPUT_FILE, 'r', encoding='utf-8') as f_repo_mix:
                    codebase_context = f_repo_mix.read() # Read codebase context from file
            except IOException as e:
                codebase_context = "Error reading codebase representation: " + str(e)
                log_file.write(codebase_context + "\n")
                print(codebase_context)
                codebase_context = "" # If can't read, set to empty string to avoid errors


        chat = model.start_chat() # Start chat session WITHOUT context in start_chat() # <----- START CHAT SESSION HERE
        # Send initial message with codebase context at the BEGINNING of the chat session
        if USE_REPO_MIX and codebase_context: # <----- ONLY SEND CODEBASE CONTEXT IF USE_REPO_MIX IS TRUE AND codebase_context IS AVAILABLE
            first_response = chat.send_message(f"Here is the codebase for a predator-prey simulation, please analyse it to understand the code:\n```{codebase_context}```\n") # Send codebase context as initial user message # <----- SEND CODEBASE AS FIRST MESSAGE
            log_file.write(f"Gemini Initial Response (Codebase Context):\n{first_response.text}\n") # Log initial response
        else:
            first_response = chat.send_message(f"For this and all future iterations, I will NOT be providing the codebase. Please rely on your general knowledge of predator-prey simulations and the information provided in each prompt to suggest attribute adjustments.") # Inform Gemini that codebase context is not available
            log_file.write(f"Gemini Initial Response (No Codebase Context):\n{first_response.text}\n") # Log initial response


        while iteration < MAX_ITERATIONS and not is_stable: # Loop for max iterations OR until stable
            iteration += 1 # Increment iteration at the start
            print(f"\n----- Iteration {iteration} -----")
            log_file.write(f"\n----- Iteration {iteration} -----\n") # Log iteration number
            start_time = time.time() # Start time for iteration


            if compile_java():
                entity_counts, simulation_output = run_simulation()
                if entity_counts:
                    print("\nInitial Entity Counts (Before Gemini Suggestions):")
                    log_file.write("\nInitial Entity Counts (Before Gemini Suggestions):\n") # Log entity counts

                    for entity, counts in entity_counts.items(): # Print entity counts AFTER balance check, so balance warning is more prominent
                        count_str = f"- {entity}: Total={counts['total']}"
                        if "male" in counts:
                            count_str += f", Male={counts['male']}, Female={counts['female']}" # Add gender if available
                        else:
                            pass # Plant counts don't have genders, avoid else print in log
                        print(count_str, end = "") # Keep printing on same line
                        log_file.write(count_str + "\n") # Log count string
                        if "male" in counts:
                            print() # Newline for animals
                        else:
                            print() # Newline for plants

                    
                    # --- Population Balance Analysis --- # <----- ADDED POPULATION BALANCE ANALYSIS
                    balance_warning = check_population_balance(entity_counts) # Check population balance, get warning message if imbalanced # <----- CALL NEW BALANCE CHECK FUNCTION

                    if balance_warning: # If there's a balance warning, print it
                        print(f"  [Population Balance Check] {balance_warning}") # Print balance warning
                        log_file.write(f"  [Population Balance Check] {balance_warning}\n") # Log imbalance warning # Updated message
                    else: # If balance is ok
                        print("  [Population Balance Check] Population balance is within acceptable limits (all entities).") # Updated message
                        log_file.write("  [Population Balance Check] Population balance is within acceptable limits (all entities).\n") # Log balance ok # Updated message

                    is_stable_in_step = check_stability(entity_counts) # Check stability after simulation run
                    if is_stable_in_step: # If stable in this step, break the loop - CURRENTLY JUST CHECKS FOR ONE STEP
                        is_stable = True # Set overall stability flag
                        print("\n🎉 Simulation reached stability! 🎉")
                        log_file.write("\n🎉 Simulation reached stability! 🎉\n") # Log stability
                        break # Exit loop if stable

                    suggested_attributes = get_gemini_attribute_suggestions(entity_counts, chat, last_attributes_gemini_response, iteration, codebase_context if USE_REPO_MIX else None) # Pass chat object to Gemini function, pass codebase_context CONDITIONALLY # <----- PASS CHAT OBJECT
                    if suggested_attributes:
                        # Ensure plant growth and reproduction rates are not set to 0
                        if "SEEDS_GROWTH_RATE" in suggested_attributes and suggested_attributes["SEEDS_GROWTH_RATE"] == 0:
                            suggested_attributes["SEEDS_GROWTH_RATE"] = 1
                            print("Warning: Gemini suggested SEEDS_GROWTH_RATE=0, corrected to 1.")
                            log_file.write("Warning: Gemini suggested SEEDS_GROWTH_RATE=0, corrected to 1.\n")
                        if "BERRIES_GROWTH_RATE" in suggested_attributes and suggested_attributes["BERRIES_GROWTH_RATE"] == 0:
                            suggested_attributes["BERRIES_GROWTH_RATE"] = 1
                            print("Warning: Gemini suggested BERRIES_GROWTH_RATE=0, corrected to 1.")
                            log_file.write("Warning: Gemini suggested BERRIES_GROWTH_RATE=0, corrected to 1.\n")
                        if "SEEDS_REPRODUCTION_RATE" in suggested_attributes and suggested_attributes["SEEDS_REPRODUCTION_RATE"] == 0:
                            suggested_attributes["SEEDS_REPRODUCTION_RATE"] = 1
                            print("Warning: Gemini suggested SEEDS_REPRODUCTION_RATE=0, corrected to 1.")
                            log_file.write("Warning: Gemini suggested SEEDS_REPRODUCTION_RATE=0, corrected to 1.\n")
                        if "BERRIES_REPRODUCTION_RATE" in suggested_attributes and suggested_attributes["BERRIES_REPRODUCTION_RATE"] == 0:
                            suggested_attributes["BERRIES_REPRODUCTION_RATE"] = 1
                            print("Warning: Gemini suggested BERRIES_REPRODUCTION_RATE=0, corrected to 1.")
                            log_file.write("Warning: Gemini suggested BERRIES_REPRODUCTION_RATE=0, corrected to 1.\n")

                        print("\nGemini Suggested Attribute Adjustments:") # Print Gemini suggestions AFTER plant rate corrections
                        log_file.write("\nGemini Suggested Attribute Adjustments:\n") # Log suggested attributes
                        for key, value in suggested_attributes.items():
                             if key != "reasoning": # Don't log reasoning in attribute list
                                log_file.write(f"- {key}: {value}\n") # Log each attribute suggestion
                        if "reasoning" in suggested_attributes: # Log reasoning if available
                            reasoning_text = suggested_attributes['reasoning']
                            print(f"\nReasoning from Gemini: {reasoning_text}")
                            log_file.write(f"\nReasoning from Gemini: {reasoning_text}\n") # Log reasoning


                        if modify_java_source(suggested_attributes, log_file): # Modify Java source if suggestions are available, pass log_file
                            if compile_java(): # Recompile after modification
                                print("\nRecompilation successful after source modification.")
                                log_file.write("Recompilation successful after source modification.\n") # Log recompile success
                            else:
                                print("\nRecompilation FAILED after source modification. Check Java errors.")
                                log_file.write("Recompilation FAILED after source modification. Check Java errors.\n") # Log recompile fail
                        else:
                            print("\nFailed to modify Java source files.")
                            log_file.write("Failed to modify Java source files.\n") # Log modify java source fail


                        last_attributes_gemini_response = suggested_attributes # Store Gemini's suggestions for next prompt

                    else:
                        print("\nCould not get attribute suggestions from Gemini API.")
                        log_file.write("Could not get attribute suggestions from Gemini API.\n") # Log API fail

                else:
                    print("Simulation run failed or output parsing issue.")
                    log_file.write("Simulation run failed or output parsing issue.\n") # Log sim run fail
            else:
                print("Compilation was unsuccessful. Cannot run simulation.")
                log_file.write("Compilation was unsuccessful. Cannot run simulation.\n") # Log compilation fail

            iteration_time = time.time() - start_time
            print(f"  Iteration {iteration} Time: {iteration_time:.2f} seconds") # Print iteration time
            log_file.write(f"Iteration {iteration} Time: {iteration_time:.2f} seconds\n") # Log iteration time


        if not is_stable: # If loop finishes without achieving stability
            print(f"\n❌ Stability not reached within {MAX_ITERATIONS} iterations. ❌  Timed out after {MAX_ITERATIONS} iterations.") # TIMEOUT MESSAGE ADDED
            log_file.write(f"\n❌ Stability not reached within {MAX_ITERATIONS} iterations. ❌ Timed out after {MAX_ITERATIONS} iterations.\n") # Log timeout
        else:
            print(f"\n✅ Stability achieved in {iteration} iterations! ✅") # Success message with iteration count
            log_file.write(f"\n✅ Stability achieved in {iteration} iterations! ✅\n") # Log success with iteration count

    print(f"\nLog file saved to: {LOG_FILE_NAME}") # Inform user about log file