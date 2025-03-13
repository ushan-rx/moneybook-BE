# 💰 MoneyBook - Personal Finance & Expense Management  

## 🚀 Overview  
MoneyBook is a **personal finance management application** that helps users track and manage **both personal and shared expenses**. It supports **bill splitting with friends, spend groups, and group transactions** while providing **smart financial insights** to help users settle expenses efficiently.  

## ✨ Features  
### 👤 Personal Finance Tracking  
- Record **income & expenses**  
- Categorize transactions for better insights  
- View spending summaries & analytics  

### 🤝 Shared Expense Management  
- Split expenses with friends  
- Create **spend groups** and **track group transactions**  
- Smart recommendations for **settling shared expenses efficiently**  

### 🔐 Security & Authentication  
- Secured with **OAuth2 & Spring Security**  
- Encrypted **OTP & QR-based** authentication for transactions  

### ⚡ Performance & Real-Time Updates  
- Uses **Redis** for caching, **real-time notifications**, and **temporary OTP storage**  
- Optimized queries for **fast transaction processing**  

## 🛠 Tech Stack  
- **Backend:** Spring Boot (REST API), Spring Security, Spring Data JPA  
- **Frontend:** Next.js (BFF), React, TanStack Query, Zustand, Tailwind CSS  
- **Database:** PostgreSQL  
- **Caching & Realtime:** Redis (Redis Streams, WebSockets)  
- **Authentication:** OAuth2  

## 🔧 Installation & Setup  
### 📌 Prerequisites  
Ensure you have the following installed:  
- **Java 17+**  
- **Node.js 16+**  
- **PostgreSQL** (Local or Cloud-based)  
- **Redis** (For caching and real-time notifications)  

### 🎨 Frontend Setup  
```sh
git clone https://github.com/ushan-rx/MoneyBook-FE.git
cd MoneyBook-FE
npm install
npm run dev
```

Environment Variables (.env or application.properties file):

```plaintext
NEXT_PUBLIC_API_BASE_URL=https://your-api-url.com
SPRING_DATASOURCE_URL=your_postgresql_url
REDIS_HOST=your_redis_host
OAUTH2_CLIENT_ID=your_oauth2_client_id
OAUTH2_CLIENT_SECRET=your_oauth2_client_secret
```

### ⚡ Backend Setup  
```sh
git clone https://github.com/ushan-rx/moneybook-BE.git
cd moneybook-BE
./mvnw clean install
java -jar target/moneybook-0.0.1-SNAPSHOT.jar
```
