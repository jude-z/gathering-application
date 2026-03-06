# Gathering-Server

A backend server for a community gathering and meetup platform. Users can create/join gatherings, schedule meetings, chat in real-time, and receive push notifications.

<br />

## ⚙ Tech Stack

### Back-end
- **Java 21** / **Spring Boot 3.4.2**
- **Spring Security** + JWT Authentication (JJWT 0.11.5)
- **Spring Data JPA** + MySQL
- **Redis** (caching & session)
- **RabbitMQ** (async chat messaging)
- **Firebase Cloud Messaging** (push notifications)
- **WebSocket** (STOMP over SockJS)
- **AWS S3** (image storage)

### Infra
- **AWS EC2** (deployment)
- **Docker** (containerization, Amazon Corretto 21 Alpine)
- **GitHub Actions** (CI/CD)

### Tools
- **GitHub** (version control)
- **Notion** (documentation)

<br />

## 🛠️ Project Architecture

### Single-Module Structure
```
gathering-server/
└── src/main/java/spring/myproject/
    ├── controller/      # 13 REST controllers
    ├── service/         # 14 business logic services
    ├── entity/          # 22 JPA entities
    ├── repository/      # Data access (JPA repositories)
    ├── dto/             # Request/Response DTOs
    ├── config/          # Security, WebSocket, Async, Redis, ShedLock configs
    ├── common/          # Filters, handlers, exceptions, annotations, utilities
    ├── rabbitmq/        # Event-driven chat messaging (publisher/consumer)
    └── utils/           # Helpers and mappers
```

### Layered Architecture
```
Client (REST / WebSocket)
         │
    Controller ──→ Service ──→ Repository ──→ MySQL
         │            │
    WebSocket    ┌────┴────┐
    (STOMP)      │         │
              RabbitMQ   AWS S3
              Firebase   Redis
```

<br />

## ✨ Features

| Feature | Description |
|---------|-------------|
| **User Management** | Registration, JWT auth, email verification, profile management |
| **Gatherings** | Create/join community groups with category filtering & pagination |
| **Meetings** | Schedule events within gatherings with attendance tracking |
| **Real-time Chat** | WebSocket (STOMP) chat rooms with RabbitMQ message routing & read status tracking |
| **Push Notifications** | Firebase FCM with topic-based subscriptions per gathering |
| **Board** | Discussion posts with multiple image attachments |
| **Likes & Recommendations** | Like gatherings, daily-scored top-10 recommendations |
| **Image Upload** | AWS S3 integration for profile, gathering, and board images |
| **Alarms** | User notification system with checked/unchecked filtering |
| **SSE** | Server-Sent Events for real-time failure notifications |
| **Distributed Scheduling** | ShedLock for safe scheduled task execution across instances |
| **Caching** | Redis-based caching with scheduled daily eviction |

<br />

## 📡 API Endpoints

### Authentication (`/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/sign-up` | Register new user (multipart) |
| POST | `/auth/sign-in` | Login (returns JWT + refresh cookie) |
| POST | `/auth/id-check` | Check username availability |
| POST | `/auth/nickname-check` | Check nickname availability |
| PUT | `/auth/update/{userId}` | Update profile (multipart) |
| GET | `/auth/user/{userId}` | Get user details |
| POST | `/auth/email-certification` | Send email verification |
| POST | `/auth/check-certification` | Verify email code |
| POST | `/auth/generateToken` | Refresh JWT from cookie |

### Gatherings (`/gathering`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering` | Create a gathering (multipart) |
| PUT | `/gathering/{id}` | Update a gathering |
| GET | `/gathering/{id}` | Get gathering details |
| GET | `/gatherings` | List all gatherings |
| GET | `/gathering?category&pageNum&pageSize` | Filter by category (paginated) |
| GET | `/gathering/participated/{id}` | List participants (paginated) |
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

### Attendance
| Method | Endpoint | Description |
|--------|----------|-------------|
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
| POST | `/gathering/{id}/board` | Create a post (multipart, multiple files) |
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
| GET | `/alarm?page&checked` | Get alarms (filtered) |
| PATCH | `/alarm/{id}` | Mark as checked |
| DELETE | `/alarm/{id}` | Delete alarm |

### Images
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/image/{imageUrl}` | Download image from S3 |
| GET | `/gathering/{id}/image` | Get gathering images (paginated) |

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
       │                             ├── Recommend
       │                             ├── Topic ── UserTopic / FCMTokenTopic
       │                             └── Category
       ├── FCMToken
       ├── Alarm
       └── Certification
```

**22 entities:** User, Gathering, Meeting, Enrollment, Attend, ChatRoom, ChatMessage, ChatParticipant, ReadStatus, Board, Image, Like, Alarm, Recommend, Category, Certification, FCMToken, Topic, UserTopic, FCMTokenTopic, Fail, Role (enum)

<br />

## 🔌 Real-time Chat Architecture

```
Client (STOMP)
    │
    ▼
StompController (@MessageMapping)
    │
    ▼
SendMessageEventHandler
    │
    ▼
ChatPublisher ──→ RabbitMQ (topic_exchange / chat_queue)
    │
    ▼
ChatConsumer (@RabbitListener)
    │
    ▼
SimpMessageSendingOperations ──→ WebSocket broadcast to /chatRoom/{id}
```

- **Queue:** `chat_queue`
- **Exchange:** `topic_exchange` (TopicExchange)
- **Routing Key:** `chat`

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
# Place Firebase service account JSON in src/main/resources/

./gradlew clean build -x test
java -jar build/libs/*SNAPSHOT.jar
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
3. Injects secrets for config files (application.yml, application-aws.yml, Firebase JSON)
4. Deploys to AWS EC2 via SCP + SSH

