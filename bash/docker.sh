#!/bin/bash

# Function to stop all compose-related containers for the YAML files in the current
# folder.

# It uses the `docker-compose down` command to stop and remove the containers.

# @return: Exits the script with an error message if no YAML files are found in the
# current folder.

# Otherwise, stops and removes the compose-related containers for each YAML file.
down() {
    local yml_files=($(find "$(pwd)" -maxdepth 1 -name "*.yml"))

    # Check if any YAML files are found in the current folder.
    if [ ${#yml_files[@]} -eq 0 ]; then
        echo "No yml files found in the current folder."
        exit 1
    fi

    local output=""
    for file in "${yml_files[@]}"; do
        output=$(sudo docker-compose -f "$file" down 2>&1)
        if [[ $output == *"No resource found to remove"* ]]; then
            echo "No compose files are running for $(basename "$file")."
        else
            echo "All compose-related containers are removed for $(basename "$file")."
        fi
    done
}

# Function to start all compose-related containers for the YAML files in the current
# folder.

# It uses the `docker-compose up -d` command to start the containers in detached
# mode.

# @return: Exits the script with an error message if no YAML files are found in the
# current folder.

# Otherwise, starts the compose-related containers for each YAML file.
up() {
    local yml_files=($(find "$(pwd)" -maxdepth 1 -name "*.yml"))

    # Check if any YAML files are found in the current folder.
    if [ ${#yml_files[@]} -eq 0 ]; then
        echo "No yml files found in the current folder."
        exit 1
    fi

    for file in "${yml_files[@]}"; do
        sudo docker-compose -f "$file" up -d
        echo "All compose-related containers are started for $(basename "$file")."
    done
}

# Function to restart all compose-related containers for the YAML files in the
# current folder.

# It uses the `docker-compose restart` command to restart the containers.

# @return: Exits the script with an error message if no YAML files are found in the
# current folder.

# Otherwise, restarts the compose-related containers for each YAML file.
restart() {
    local yml_files=($(find "$(pwd)" -maxdepth 1 -name "*.yml"))

    # Check if any YAML files are found in the current folder.
    if [ ${#yml_files[@]} -eq 0 ]; then
        echo "No yml files found in the current folder."
        exit 1
    fi

    for file in "${yml_files[@]}"; do
        sudo docker-compose -f "$file" restart
        echo "All compose-related containers are restarted for $(basename "$file")."
    done
}

# Function to stop all compose-related containers for the YAML files in the current
# folder.

# It uses the `docker-compose stop` command to stop the containers.

# @return: Exits the script with an error message if no YAML files are found in the
# current folder.

# Otherwise, stops the compose-related containers for each YAML file.
stop() {
    local yml_files=($(find "$(pwd)" -maxdepth 1 -name "*.yml"))

    # Check if any YAML files are found in the current folder.
    if [ ${#yml_files[@]} -eq 0 ]; then
        echo "No yml files found in the current folder."
        exit 1
    fi

    for file in "${yml_files[@]}"; do
        sudo docker-compose -f "$file" stop
        echo "All compose-related containers are stopped for $(basename "$file")."
    done
}

# Function to pull and start all compose-related containers for the YAML files in
# the current folder.

# It calls the `down` and `up` functions to achieve this.

# @return: Exits the script with an error message if no YAML files are found in the
# current folder.

# Otherwise, pulls and starts the compose-related containers for each YAML file.
pull() {
    down
    up
}

# Function to start all compose-related containers for the YAML files in the current
# folder.

# It uses the `docker-compose start` command to start the containers.

# @return: Exits the script with an error message if no YAML files are found in the
# current folder.

# Otherwise, starts the compose-related containers for each YAML file.
start() {
    local yml_files=($(find "$(pwd)" -maxdepth 1 -name "*.yml"))

    # Check if any YAML files are found in the current folder.
    if [ ${#yml_files[@]} -eq 0 ]; then
        echo "No yml files found in the current folder."
        exit 1
    fi

    for file in "${yml_files[@]}"; do
        sudo docker-compose -f "$file" start
        echo "All compose-related containers are started for $(basename "$file")."
    done
}

# Main function to handle the user's choice of action.
# It calls the appropriate function based on the provided argument.

# @param $1: The action to perform (down, up, restart, stop, pull, start).
# @return: Exits the script with an error message if an invalid option is chosen.
# Otherwise, calls the corresponding function based on the provided argument.
main() {
    if [ "$1" == "down" ]; then
        down
    elif [ "$1" == "up" ]; then
        up
    elif [ "$1" == "restart" ]; then
        restart
    elif [ "$1" == "stop" ]; then
        stop
    elif [ "$1" == "pull" ]; then
        pull
    elif [ "$1" == "start" ]; then
        start
    else
     echo "Invalid option. Please choose one of the following: down, up, restart, \
stop, pull, start."
    fi
}

# Check if the script is being executed directly (not sourced).
# If so, call the main function with the provided arguments.
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi