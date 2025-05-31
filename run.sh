#!/bin/bash

# Build the application
echo "Building the application..."
mvn clean package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    
    # Run the application
    echo "Starting the application..."
    java -jar target/designEx-0.0.1-SNAPSHOT.jar
else
    echo "Build failed. Please check the errors above."
    exit 1
fi 