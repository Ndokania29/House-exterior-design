# AI-Powered House Exterior Design

An intelligent application that uses AI to transform house exterior designs with advanced computer vision and design recommendations.

## Features

- **AI-Powered Design Transformation**: Upload a photo of your house and see design transformations in real-time
- **Style Library**: Choose from multiple design styles to apply to your home exterior
- **Intelligent Recommendations**: Get personalized color and material recommendations based on your house style
- **Adjustable Blend Factor**: Control the intensity of design changes with a simple slider

## Technology Stack

- **Backend**: Spring Boot with Java
- **Frontend**: HTML5, CSS3, JavaScript with Bootstrap 5
- **AI Model**: Meta's Segment Anything Model (SAM) for image segmentation
- **UI**: Modern blue interface with Nunito font family

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Python 3.8+ (for the AI model)

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/house-exterior-design.git
   ```

2. Build the application:
   ```
   mvn clean package
   ```

3. Run the application:
   ```
   java -jar target/designEx-0.0.1-SNAPSHOT.jar
   ```

4. Access the application at:
   ```
   http://localhost:8080
   ```

## Usage

1. Upload a front-facing photo of your house
2. Select a design style from the dropdown menu
3. Adjust the blend factor to control the intensity of the transformation
4. View the transformed image and design recommendations

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- Meta's Segment Anything Model (SAM) for powerful image segmentation
- Bootstrap for responsive UI components 