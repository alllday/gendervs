# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**GenderVS** is a structured gender issue discussion platform designed to facilitate productive debates on gender-related topics. The platform moves away from unproductive arguments toward structured, respectful discourse through a topic→position→argument→comment flow.

### Core Domain Concepts

- **Topics**: Discussion subjects with 3 position options (긍정/부정/중립)
- **User Positions**: User stance selection history for each topic with position change tracking
- **Posts**: Arguments supporting chosen positions with multimedia attachments
- **Comments**: Multi-level responses (unlimited depth) with position tracking
- **Votes**: Like/dislike system with polymorphic target support (topic/post/comment)
- **Influence Score**: Metric tracking user persuasion effectiveness

## Development Commands

```bash
# Build and run
./gradlew bootRun

# Build project
./gradlew build

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "ClassName.methodName"
```

## Architecture

### Technology Stack
- **Backend**: Spring Boot 3.5.4, Spring Security, Spring Data JPA
- **Database**: PostgreSQL with JPA/Hibernate
- **Build Tool**: Gradle
- **Java Version**: 17

### Domain Architecture

The application follows a layered architecture with clear domain boundaries:

**Core Entities (13 domain objects):**
1. **User** - Account credentials and authentication
2. **UserProfile** - Demographics (nickname, gender, birth, score)
3. **Topic** - Discussion subjects with category
4. **UserPosition** - Position selection history (User-Topic-PositionCode junction with tracking)
5. **Post** - Arguments/opinions with topic and position
6. **PostAttachment** - Multimedia files for posts
7. **Comment** - Nested responses with unlimited depth
8. **Vote** - Polymorphic like/dislike for topic/post/comment
9. **Report** - User reports for content moderation
10. **ReportTarget** - Polymorphic report targets
11. **TermsAgreement** - User consent tracking
12. **AdminLog** - Administrative action history
13. **BlacklistWord** - Content filtering

**Core Entity Relationships:**
- `User` ←→ `UserProfile` (1:1) - Shared PK via @MapsId
- `User` → `Topic` (1:N) - Topic creation
- `User` + `Topic` → `UserPosition` (N:N history) - Position selection tracking with isCurrent flag
- `Topic` → `Post` (1:N) - Posts belong to topics
- `User` → `Post` (1:N) - User-created posts
- `Post` + `PositionCode` - Posts have position affiliation
- `Post` → `PostAttachment` (1:N) - Media attachments
- `Post` → `Comment` (1:N) - Post comments
- `Comment` → `Comment` (self-referencing) - Nested replies via parentComment
- `User` → `Vote` (1:N) - User votes
- `Vote` - Polymorphic (targetType + targetId) for Topic/Post/Comment

### Key Domain Rules

1. **Position System**: Three positions available - POSITIVE(긍정), NEGATIVE(부정), NEUTRAL(중립)
2. **Position-Based Posting**: Users can only create posts for their currently selected position on a topic
3. **Vote Restrictions**: Post voting limited to same-position users; topic/comment voting unrestricted
4. **Position Change Tracking**:
   - Historical tracking via `UserPosition` with `isCurrent` flag
   - Position changes can reference a `reasonPost` that influenced the change
   - Optional `reason` text field for explaining position change
5. **Soft Delete Pattern**: `ContentStatus` enum (ACTIVE/DELETED/SUSPENDED) for logical deletion
6. **Edit Restrictions**: Content becomes read-only (`isEditable=false`) after interaction or time limit

### Database Design

**Security Features:**
- Phone encryption (AES256) + hash (SHA256) for uniqueness
- Password hashing
- Unique constraints prevent duplicate voting/reporting

**Performance Considerations:**
- Denormalized counters (likeCount, dislikeCount, commentCount, postView, topicView, participateCount, influenceScore)
- Status-based soft deletes via `ContentStatus` enum for data integrity
- Batch processing for view counts via Redis (planned)
- QueryDSL configured for type-safe queries

### Critical Business Logic

**Content Editability:**
- Editable only if: `is_editable=true AND created_at + N_minutes > current_time`
- Interaction triggers lock editing permissions

**Position Management:**
- Users maintain position history per topic via `UserPosition` entity
- Each position change creates new `UserPosition` record (old ones marked `isCurrent=false`)
- Position changes can reference influencing post via `reasonPost` field
- Comments track user's position at time of writing via `positionCode` field

**Influence Scoring:**
- Tracks user persuasion effectiveness via `UserProfile.score` and `Post.influenceScore`
- Incremented when a post is referenced as `reasonPost` in position changes

## Development Notes

- Database password is hardcoded in `application.yml` - should be externalized for production
- Entities use Lombok (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor) for boilerplate reduction
- JPA relationships are mostly unidirectional to avoid circular dependencies
- Hibernate DDL auto-update is enabled for development
- Hibernate annotations used: @CreationTimestamp, @UpdateTimestamp for automatic timestamp management
- QueryDSL configured for type-safe query building (Q-classes generated in build/generated/)
- Spring Security configured for authentication and authorization

## Project Structure

```
src/main/java/gendervs/gendervs1/
├── domain/
│   ├── entity/             # JPA entities (13 domain objects)
│   │   ├── User.java
│   │   ├── UserProfile.java
│   │   ├── Topic.java
│   │   ├── UserPosition.java
│   │   ├── Post.java
│   │   ├── PostAttachment.java
│   │   ├── Comment.java
│   │   ├── Vote.java
│   │   ├── Report.java
│   │   ├── ReportTarget.java
│   │   ├── TermsAgreement.java
│   │   ├── AdminLog.java
│   │   └── BlacklistWord.java
│   └── enums/              # Enum types
│       ├── PositionCode.java       # POSITIVE, NEGATIVE, NEUTRAL
│       ├── TopicCategory.java      # 8 categories
│       ├── ContentStatus.java      # ACTIVE, DELETED, SUSPENDED
│       ├── PostCategory.java
│       ├── VoteType.java           # LIKE, DISLIKE
│       ├── TargetType.java         # TOPIC, POST, COMMENT
│       ├── TermsType.java
│       ├── ReasonCode.java
│       ├── ActionType.java
│       └── FileType.java
├── dto/                    # Data transfer objects
│   └── OperationResponse.java      # Standardized CUD response
├── repository/             # Spring Data JPA repositories
│   ├── topic/              # Topic-related repositories (empty)
│   └── UserRepository.java
├── service/                # Business logic layer
│   ├── topic/              # Topic-related services (empty)
│   └── AuthService.java
├── controller/             # REST API controllers
│   ├── LoginController.java
│   └── MainController.java
└── config/                 # Spring configuration
    ├── SecurityConfig.java
    └── QuerydslConfig.java
```

**Implementation Status:**
- ✅ Core entities defined
- ✅ Enums defined
- ✅ Basic authentication (login) implemented
- ✅ QueryDSL and Security configured
- ⏳ Topic CRUD operations (in progress)
- ⏳ Post CRUD operations (pending)
- ⏳ Comment CRUD operations (pending)
- ⏳ Vote operations (pending)

Specification documents are located in `gendervs_explain/` directory containing requirements, ERD, and API specifications.