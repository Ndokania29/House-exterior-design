# 🏠 Exterior AI Design - Smart House Transformation

> **Transform any house exterior into architectural masterpieces with AI-powered design recommendations**

Ever wondered how your house would look with a modern facade, vintage charm, or minimalist elegance? This AI-powered system does exactly that - and more! Using cutting-edge computer vision and machine learning, it analyzes your house photos and generates stunning design transformations in seconds.

## ✨ What Makes This Special?

- **🎯 Intelligent Segmentation**: Uses Meta's Segment Anything Model (SAM) to precisely identify architectural elements
- **🎨 Style Library**: 15+ curated design styles with 200+ material combinations
- **🔧 Smart Classification**: Automatically detects walls, roofs, windows, doors, and more
- **⚡ Real-time Processing**: Get professional design recommendations in few seconds
- **🌐 Full-stack Solution**: Spring Boot backend with Python AI processing

## 🚀 Features

### Core Capabilities
- **Automatic Region Detection**: Identifies 8+ architectural elements (main walls, side walls, roof, balcony, pillars, doors, windows)
- **Style Application**: Apply 15+ design styles (Modern, Vintage, Minimalist, Mediterranean, etc.)
- **Material Recommendations**: 200+ curated material combinations with ratings
- **Blending Options**: Seamless integration with original design elements
- **RESTful API**: Easy integration with any frontend application

### Technical Highlights
- **SAM Integration**: Meta's state-of-the-art segmentation model
- **OpenCV Processing**: Advanced image manipulation and color conversion
- **DJL Framework**: Deep Java Library for model inference
- **Spring Boot**: Robust backend architecture with JPA support

## 🛠️ Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2.3**
- **Spring Data JPA** for data persistence
- **MySQL** database
- **OpenCV 4.9.0** for image processing
- **DJL 0.26.0** for deep learning inference

### AI/ML
- **Python 3.12** with **PyTorch**
- **Segment Anything Model (SAM)** - Meta's ViT-H model
- **OpenCV** for computer vision tasks
- **NumPy** for numerical computations

### Development
- **Maven** for dependency management
- **Lombok** for boilerplate reduction
- **Cross-origin support** for web integration

## 📊 Project Statistics

- **Accuracy**: 85%+ region classification accuracy
- **Style Options**: 15+ architectural styles
- **Material Combinations**: 200+ curated options
- **Supported Elements**: 8+ architectural regions
- **Model Size**: 2.4GB SAM ViT-H model

## 🎯 Use Cases

- **Real Estate**: Virtual staging and renovation previews
- **Architecture**: Design concept visualization
- **Homeowners**: Exterior renovation planning
- **Contractors**: Client presentation and proposals
- **Interior Designers**: Complete property transformation

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Python 3.12+
- MySQL 8.0+
- 4GB+ RAM (for SAM model)

### Backend Setup
```bash
cd Back
mvn clean install
mvn spring-boot:run
```

### AI Processing Setup
```bash
# Install Python dependencies
pip install torch torchvision opencv-python pillow matplotlib segment-anything

# Download SAM model
# Place sam_vit_h_4b8939.pth in project root
```

### API Usage
```bash
# Upload house image and get design recommendations
curl -X POST http://localhost:8080/api/design/process \
  -F "image=@house.jpg" \
  -F "style=Modern" \
  -F "applyBlending=true" \
  -F "blendingAlpha=0.5"
```

## 📁 Project Structure

```
designEx/
├── 📁 .git/                          # Git version control                  
├── 📁 .mvn/                          # Maven wrapper files
├── 📁 target/                        # Maven build output
├── 📁 src/                           # Main source code
│   └── 📁 main/
│       ├── 📁 java/
│       │   └── 📁 com/exteriorp/designEx/
│       │       ├── 📁 controller/    # REST API controllers
│       │       ├── 📁 model/         # Data models/entities
│       │       ├── 📁 service/       # Business logic services
│       │       ├── 📁 config/        # Configuration classes
│       │       └── ExteriorDesignApplication.java  # Main Spring Boot app
│       └── 📁 resources/             # Configuration files
├── 📁 python/                        # Python AI/ML components
│   ├── scripts/
│   │   └── seg.py                    # Segmentation script
│   ├── seg.py                        # Main segmentation script
│   └── requirements.txt               # Python dependencies
├── 📁 data/                          # Data storage
│   └── 📁 styles/
│       └── complete_style_library.json  # Style definitions (248KB)
├── 📁 models/                        # AI model storage
│   ├── sam_vit_h_4b8939.pth         # SAM model (2.4GB)
│   └── README.txt
├── 📁 images/                        # Image processing
│   ├── 📁 uploads/                   # User uploaded images
│   └── 📁 results/                   # Generated results
├── 📁 output/                        # Output files
├── 📁 docs/                          # Documentation
├── 📄 pom.xml                        # Maven project configuration
├── 📄 seg.py                         # Main segmentation script (root)
├── 📄 run.bat                        # Windows run script
├── 📄 run.sh                         # Linux/Mac run script
├── 📄 Procfile                       # Heroku deployment
├── 📄 system.properties              # System configuration
├── 📄 img.png                        # Sample image
├── 📄 README.md                      # Project documentation
├── 📄 HELP.md                        # Help documentation
└── 📄 LICENSE                        # License file
```

## 🎨 Available Styles

- **Modern**: Sleek, contemporary designs
- **Vintage**: Classic, timeless elegance
- **Minimalist**: Clean, simple aesthetics
- **Mediterranean**: Warm, coastal vibes
- **Industrial**: Urban, raw materials
- **Scandinavian**: Light, natural elements
- **Colonial**: Traditional, refined
- **Contemporary**: Current trends
- **Art Deco**: Geometric, luxurious
  
## 🔧 Configuration

### Application Properties
```properties
spring.application.name=house-design-backend
app.upload.dir=uploads/
```

### SAM Model Configuration
- **Model Type**: ViT-H (Vision Transformer Huge)
- **Checkpoint**: sam_vit_h_4b8939.pth
- **Device**: CUDA (GPU) / CPU fallback
- **Input Size**: 1024x1024 pixels

##Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
---



