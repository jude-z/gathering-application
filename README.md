# Gathering Server

A community gathering and meetup platform backend built with Spring Boot. Users can create and join gatherings, schedule meetings, chat in real-time, and receive push notifications.

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.4.2
- **Database:** MySQL, Redis
- **ORM:** Spring Data JPA, QueryDSL
- **Messaging:** RabbitMQ (chat), Firebase Cloud Messaging (push notifications)
- **WebSocket:** STOMP over SockJS
- **Storage:** AWS S3
- **Security:** Spring Security + JWT
- **Build:** Gradle (multi-module)
- **Deployment:** Docker, GitHub Actions, AWS EC2

## Project Structure

```
gathering-server/
├── api/             # REST controllers and web layer
├── domain/          # JPA entities and domain models
├── infra/           # Repositories (JPA, QueryDSL, JDBC), Redis, FCM config
├── util/            # Utility classes and interfaces
├── common/          # Shared configurations
├── mail-server/     # Email service (separate Spring Boot app)
├── chat-server/     # Chat module
└── src/             # Core services, security, WebSocket, RabbitMQ config
```

## Features

- **User Management** — Registration, JWT authentication, email verification, profile management
- **Gatherings** — Create/join community groups with category filtering and pagination
- **Meetings** — Schedule events within gatherings with attendance tracking
- **Real-time Chat** — WebSocket (STOMP) chat rooms with RabbitMQ message routing and read status tracking
- **Push Notifications** — Firebase FCM with topic-based subscriptions
- **Board** — Discussion posts with image attachments
- **Likes & Recommendations** — Like gatherings and get top-10 recommendations
- **Image Upload** — AWS S3 integration for profile and gathering images
- **Email Notifications** — Async email processing via dedicated mail server
- **SSE** — Server-Sent Events for real-time failure notifications

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/sign-up` | Register a new user |
| POST | `/auth/sign-in` | Login |
| POST | `/auth/id-check` | Check username availability |
| POST | `/auth/nickname-check` | Check nickname availability |
| PUT | `/auth/update/{userId}` | Update profile |
| GET | `/auth/user/{userId}` | Get user details |
| POST | `/auth/email-certification` | Send email verification |
| POST | `/auth/check-certification` | Verify email code |
| POST | `/auth/generateToken` | Refresh JWT token |

### Gatherings
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering` | Create a gathering |
| PUT | `/gathering/{id}` | Update a gathering |
| GET | `/gathering/{id}` | Get gathering details |
| GET | `/gatherings` | List all gatherings |
| GET | `/gathering` | Filter by category (paginated) |
| PATCH | `/gathering/{id}/participate` | Join a gathering |
| PATCH | `/gathering/{id}/disParticipate` | Leave a gathering |
| PATCH | `/gathering/{id}/permit/{enrollmentId}` | Approve enrollment |
| GET | `/gathering/participated/{id}` | List participants |

### Meetings
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering/{id}/meeting` | Create a meeting |
| GET | `/gathering/{id}/meeting/{meetingId}` | Get meeting details |
| PUT | `/gathering/{id}/meeting/{meetingId}` | Update a meeting |
| DELETE | `/gathering/{id}/meeting/{meetingId}` | Delete a meeting |
| GET | `/gathering/{id}/meetings` | List meetings |
| POST | `/gathering/{id}/meeting/{meetingId}/attend` | Attend a meeting |
| POST | `/gathering/{id}/meeting/{meetingId}/disAttend` | Cancel attendance |

### Chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering/{id}/chat` | Create a chat room |
| GET | `/gathering/{id}/chats` | List chat rooms in a gathering |
| GET | `/my/chats` | List my chat rooms |
| POST | `/chat/attend/{chatId}` | Join a chat room |
| POST | `/chat/disAttend/{chatId}` | Leave a chat room |
| GET | `/messages/{chatId}` | Get unread messages |
| POST | `/chat/{chatId}` | Mark messages as read |

**WebSocket:** Connect via `/connect` (SockJS), publish to `/publish/chatRoom/{chatRoomId}`

### Board
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/gathering/{id}/board` | Create a post |
| GET | `/gathering/{id}/board/{boardId}` | Get a post |
| GET | `/gathering/{id}/boards` | List posts |

### Likes & Recommendations
| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/gathering/{id}/like` | Like a gathering |
| PATCH | `/gathering/{id}/dislike` | Unlike a gathering |
| POST | `/gatherings/like` | Get liked gatherings |
| GET | `/recommend` | Get top 10 recommendations |

### Alarms
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/alarm` | Get alarms |
| PATCH | `/alarm/{id}` | Mark as checked |
| DELETE | `/alarm/{id}` | Delete an alarm |

### Images
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/image/{imageUrl}` | Download an image |
| GET | `/gathering/{id}/image` | Get gathering images |

## Architecture

```
Controller → Service → Repository → Entity
                ↕              ↕
           RabbitMQ         QueryDSL
           Firebase          Redis
            AWS S3
```

- **Multi-module Gradle** — Separation of concerns across API, Domain, Infrastructure, and Utility layers
- **Event-driven** — RabbitMQ for async chat message routing
- **Distributed scheduling** — ShedLock for safe scheduled task execution
- **Custom annotations** — `@Username` for JWT-based user resolution from security context

## Getting Started

### Prerequisites
- Java 21
- MySQL
- Redis
- RabbitMQ
- AWS S3 bucket
- Firebase project (for FCM)

### Run Locally

```bash
# Clone the repository
git clone https://github.com/<your-username>/gathering-server.git
cd gathering-server

# Configure application properties
# Set up api/src/main/resources/application.yml with your database, Redis, RabbitMQ, AWS, and Firebase credentials

# Build and run
./gradlew clean build -x test
java -jar api/build/libs/*.jar
```

### Docker

```bash
docker build -t gathering-server .
docker run -p 80:80 gathering-server
```

## Deployment

CI/CD is configured via GitHub Actions (`.github/workflows/deploy.yml`):
1. Triggers on push to `master`
2. Builds with JDK 21 (Amazon Corretto)
3. Injects secrets for configuration files
4. Deploys to AWS EC2 via SCP + SSH
