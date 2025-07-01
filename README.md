# ❤️‍🔥 HealthHub - Backend

> **April 2025 – May 2025**  
> Your Personalized Health & Meal Plan Companion – Powered by Spring Boot & Smart APIs

---
<!-- ![HealthHub Banner](https://i.imgur.com/klXo4Wa.png)  Replace with your own banner image if needed -->
<div align="center">
  <img src="https://forthebadge.com/images/badges/built-with-love.svg" />&nbsp;
  <img src="https://forthebadge.com/images/badges/uses-brains.svg" />&nbsp;
  <img src="https://forthebadge.com/images/badges/powered-by-responsibility.svg"/>
</div>
<br/>

---

## 🚀 Overview

**HealthHub** is a AI Powerd personalized health and meal planning platform designed to help users lead a healthier lifestyle through smart meal planning, tailored to their specific needs. With a focus on user well-being, this platform helps users manage their health goals by providing custom meal plans based on dietary restrictions, available ingredients, and personal health objectives.

---

## 🌱 Key Features

🔥 **Personalized Meal Plans**  
Generate plans tailored to health goals, dietary needs, and available ingredients.

⏰ **Daily Meal Reminders**  
Automated email notifications keep users on track with their schedule.

📆 **Active Plan Tracking**  
Activate and monitor live meal plans straight from the dashboard.

✍️ **Meal Customization**  
Edit, add, or remove meals per day with full flexibility.

🎯 **Health Goal Integration**  
Smart meal generation based on goals: **Weight Loss**, **Muscle Gain**, or **Wellness**.

🧠 **Doctor-Written Articles**  
Browse informative, **verified health content** written by real and **trusted doctors** for better guidance and learning.

---

## 💬 Why HealthHub?

HealthHub bridges the gap between technology and personal wellness. With meal personalization, progress tracking, and medically accurate articles — users stay healthy, informed, and in control.

---

## 🛠️ Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-4169E1?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-635BFF?style=for-the-badge&logo=redis&logoColor=white)
![LLaMA 3](https://img.shields.io/badge/LLaMA_3-3B82F6?style=for-the-badge&logo=meta&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)


---

## 🐳 Docker & 🔧 Deployment Instructions
pull the public image from Docker Hub:
```bash
docker pull soumyajit2005/healthhub
```
```bash
docker run -p 8080:8080 soumyajit2005/healthhub
```

🌐 Deployment
This backend is hosted live on Render using Docker.

📍 API Base URL:
```bash
https://healthhub-7656.onrender.com/auth/signup
```

🐦 Try Endpoints in Postman:
🔗 with https://healthhub-7656.onrender.com/auth/signup
> 🔗 View the full API reference in [Postman Collection](https://www.postman.com/newsly-0222/workspace/healthhub)

---

## ⚠️ Note on AI Integration
Generate Meal Plan Feature:

This feature uses LLaMA 3.2 1B via Olama with:
```
spring.ai.model=llama3.2:1b
```
🧠 The model runs locally, and will not work out-of-the-box on a deployed server.

✅ To use the meal plan generation, you must download and run the LLaMA 3.2 1B model on your local machine.

🔗 Follow Olama’s documentation to install and run the model locally: https://github.com/ollama/ollama.

---

## ⚙️ Setup & Run Locally

### 📦 Clone the Repository

```bash
git clone https://github.com/leo-soumyajit/HealthHub.git
cd HealthHub
```
🛠 Configure Database Connection
Edit the application.properties file:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/<your_db_name>
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
```
🛠 Configure Redis Cache
To enable Redis caching in your Spring Boot application, add the following to your application.properties:
```bash
# Redis Configuration
spring.cache.type=redis
spring.data.redis.host=<your host>
spring.data.redis.port=<your port>
spring.data.redis.username=default
spring.data.redis.password=<your password>
```
▶ Run the Application
```bash
./mvnw spring-boot:run
```

### 📄 Access Swagger API Docs
Once the server is running, open in browser:
```bash
http://localhost:7000/swagger-ui/index.html
```
---

