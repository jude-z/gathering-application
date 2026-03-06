# Gathering Server

A backend server for a community gathering and meetup platform. Users can create/join gatherings, schedule meetings, chat in real-time, and receive push notifications.

<br />

## ⚙ Tech Stack

### Back-end
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Java.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringBoot.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringSecurity.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringDataJPA.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Mysql.png?raw=true" width="80">
</div>

- **Java 21** / **Spring Boot 3.4.2**
- **Spring Security** + JWT Authentication
- **Spring Data JPA** + QueryDSL
- **MySQL** / **Redis** (caching & session)
- **RabbitMQ** (async chat messaging)
- **Firebase Cloud Messaging** (push notifications)
- **WebSocket** (STOMP over SockJS)
- **AWS S3** (image storage)

### Infra
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/AWSEC2.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Mysql.png?raw=true" width="80">
</div>

- **AWS EC2** (deployment)
- **Docker** (containerization)
- **GitHub Actions** (CI/CD)

### Tools
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Github.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Notion.png?raw=true" width="80">
</div>

<br />

## 🛠️ Project Architecture

```
gathering-server/
├── api/             # REST controllers, security, WebSocket config
├── domain/          # JPA entities and domain models
├── infra/           # Repositories (JPA, QueryDSL, JDBC), Redis, FCM config
├── util/            # Pagination utilities and helpers
├── common/          # Shared configurations
├── mail-server/     # Email service module
└── src/             # Core services, business logic, RabbitMQ config
```

```
Controller → Service → Repository → Database (MySQL)
                ↕            ↕
           RabbitMQ       QueryDSL
           Firebase        Redis
            AWS S3
```

<br />

## ✨ Features

| Feature | Description |
|---------|-------------|
| **User Management** | Registration, JWT auth, email verification, profile management |
| **Gatherings** | Create/join community groups with category filtering & pagination |
| **Meetings** | Schedule events within gatherings with attendance tracking |
| **Real-time Chat** | WebSocket (STOMP) chat rooms with RabbitMQ message routing & read status |
| **Push Notifications** | Firebase FCM with topic-based subscriptions |
| **Board** | Discussion posts with image attachments |
| **Likes & Recommendations** | Like gatherings, get top-10 recommendations |
| **Image Upload** | AWS S3 integration for profile, gathering, and board images |
| **Alarms** | User notification system with checked/unchecked status |
| **SSE** | Server-Sent Events for real-time failure notifications |

<br />

## 📡 API Endpoints

### Authentication (`/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/sign-up` | Register new user (multipart) |
| POST | `/auth/sign-in` | Login |
| POST | `/auth/id-check` | Check username availability |
| POST | `/auth/nickname-check` | Check nickname availability |
| PUT | `/auth/update/{userId}` | Update profile (multipart) |
| GET | `/auth/user/{userId}` | Get user details |
| POST | `/auth/email-certification` | Send email verification |
| POST | `/auth/check-certification` | Verify email code |
| POST | `/auth/generateToken` | Refresh JWT token |

### Gatherings (`/gathering`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering` | Create a gathering (multipart) |
| PUT | `/gathering/{id}` | Update a gathering |
| GET | `/gathering/{id}` | Get gathering details |
| GET | `/gatherings` | List all gatherings |
| GET | `/gathering?category&pageNum&pageSize` | Filter by category (paginated) |
| GET | `/gathering/participated/{id}` | List participants |
| POST | `/gatherings/like` | Get liked gatherings (paginated) |

### Enrollment
| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/gathering/{id}/participate` | Join a gathering |
| PATCH | `/gathering/{id}/disParticipate` | Leave a gathering |
| PATCH | `/gathering/{id}/permit/{enrollmentId}` | Approve enrollment |

### Meetings (`/gathering/{id}/meeting`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering/{id}/meeting` | Create a meeting (multipart) |
| GET | `/gathering/{id}/meeting/{meetingId}` | Get meeting details |
| PUT | `/gathering/{id}/meeting/{meetingId}` | Update a meeting |
| DELETE | `/gathering/{id}/meeting/{meetingId}` | Delete a meeting |
| GET | `/gathering/{id}/meetings` | List meetings (paginated) |
| POST | `/gathering/{id}/meeting/{meetingId}/attend` | Attend a meeting |
| POST | `/gathering/{id}/meeting/{meetingId}/disAttend` | Cancel attendance |

### Chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering/{id}/chat` | Create a chat room |
| GET | `/gathering/{id}/chats` | List chat rooms in gathering |
| GET | `/gathering/{id}/able/chats` | Available chat rooms to join |
| GET | `/gathering/{id}/participate/chats` | Joined chat rooms |
| GET | `/my/chats` | List my chat rooms |
| POST | `/chat/attend/{chatId}` | Join a chat room |
| POST | `/chat/disAttend/{chatId}` | Leave a chat room |
| GET | `/messages/{chatId}` | Get unread messages |
| POST | `/chat/{chatId}` | Mark messages as read |
| GET | `/chat/{chatId}` | Get chat details |
| GET | `/chat/participant/{chatId}` | List chat participants |

**WebSocket:** Connect via `/connect` (SockJS), publish to `/publish/chatRoom/{chatRoomId}`

### Board (`/gathering/{id}/board`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering/{id}/board` | Create a post (multipart) |
| GET | `/gathering/{id}/board/{boardId}` | Get a post |
| GET | `/gathering/{id}/boards` | List posts (paginated) |

### Likes & Recommendations
| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/gathering/{id}/like` | Like a gathering |
| PATCH | `/gathering/{id}/dislike` | Unlike a gathering |
| GET | `/recommend` | Get top 10 recommendations |

### Alarms (`/alarm`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/alarm` | Get alarms |
| PATCH | `/alarm/{id}` | Mark as checked |
| DELETE | `/alarm/{id}` | Delete alarm |

### Images
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/image/{imageUrl}` | Download image |
| GET | `/gathering/{id}/image` | Get gathering images |

### SSE & Health
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/subscribe/{clientId}` | SSE subscription |
| DELETE | `/close/{clientId}` | Close SSE connection |
| GET | `/health` | Health check |

<br />

## 🗂️ Domain Models

```
User ──┬── Enrollment ── Gathering ──┬── Meeting ── Attend
       │                             ├── ChatRoom ── ChatParticipant ── ChatMessage ── ReadStatus
       │                             ├── Board ── Image
       │                             ├── Like
       │                             ├── Topic ── UserTopic / FCMTokenTopic
       │                             └── Category
       ├── FCMToken
       ├── Alarm
       └── Certification
```

**22 entities** including: User, Gathering, Meeting, Enrollment, Attend, ChatRoom, ChatMessage, ChatParticipant, ReadStatus, Board, Image, Like, Alarm, Recommend, Category, Certification, FCMToken, Topic, UserTopic, FCMTokenTopic, Fail

<br />

## 🚀 Getting Started

### Prerequisites
- Java 21
- MySQL
- Redis
- RabbitMQ
- AWS S3 bucket
- Firebase project (for FCM)

### Run Locally
```bash
git clone https://github.com/<your-username>/gathering-server.git
cd gathering-server

# Configure application.yml with your DB, Redis, RabbitMQ, AWS, Firebase credentials
# Place firebase service account JSON in resources

./gradlew clean build -x test
java -jar api/build/libs/*.jar
```

### Docker
```bash
docker build -t gathering-server .
docker run -p 80:80 gathering-server
```

<br />

## 🔄 Deployment

CI/CD via GitHub Actions (`.github/workflows/deploy.yml`):
1. Triggers on push to `master`
2. Builds with JDK 21 (Amazon Corretto)
3. Injects secrets for config files (application.yml, AWS, Firebase)
4. Deploys to AWS EC2 via SCP + SSH

<br />

## 💁‍♂️ Team
<img src="https://github.com/user-attachments/assets/7671baa8-20d3-4eeb-b3f7-e23bab3eb5be" width="200" height="300"/>
