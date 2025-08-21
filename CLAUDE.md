# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**GenderVS** is a structured gender issue discussion platform designed to facilitate productive debates on gender-related topics. The platform moves away from unproductive arguments toward structured, respectful discourse through a topic→position→argument→comment flow.

### Core Domain Concepts

- **Topics**: Discussion subjects with 3 positions (A, B, C)
- **Positions**: User stance selection (A vs B vs C format)
- **Posts**: Arguments supporting chosen positions with multimedia attachments
- **Comments**: Multi-level responses (unlimited depth) with position tracking
- **Votes**: Like/dislike system with position-based restrictions
- **Influence Score**: Metric tracking persuasion effectiveness

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

**Core Entities Relationships:**
- `User` ←→ `UserProfile` (1:1) - Account + demographic data
- `User` → `Topic` (1:N) - Topic creation
- `Topic` → `TopicPosition` (1:N) - Available positions (A,B,C)
- `User` → `TopicPosition` (N:N) - users (1) ─── (N) user_positions (N) ─── (1) topic_positions
- `Topic` (1) → `Post` (N)    
- `Position` (1) → `Post` (N)  
- `User` (1) → `Post` (N)
- `Post` → `Comment` (1:N) with self-referencing for replies
- `Vote` - Polymorphic voting on topics/posts/comments

### Key Domain Rules

1. **Position-Based Posting**: Users can only create posts for their selected position
2. **Vote Restrictions**: Post voting limited to same-position users; topic/comment voting unrestricted
3. **Position Change Tracking**: Historical tracking with 1-hour cooldown
4. **Soft Delete Pattern**: Status flags for logical deletion across entities
5. **Edit Restrictions**: Content becomes read-only after interaction (votes/comments) or time limit

### Database Design

**Security Features:**
- Phone encryption (AES256) + hash (SHA256) for uniqueness
- Password hashing
- Unique constraints prevent duplicate voting/reporting

**Performance Considerations:**
- Denormalized counters (likeCount, commentCount, etc.)
- Status-based soft deletes for data integrity
- Batch processing for view counts via Redis (planned)

### Critical Business Logic

**Content Editability:**
- Editable only if: `is_editable=true AND created_at + N_minutes > current_time`
- Interaction triggers lock editing permissions

**Position Management:**
- Users maintain position history per topic
- Position changes reset vote history for previous position
- Comments retain original position marker (`origin_position`)

**Influence Scoring:**
- Tracks user persuasion effectiveness
- Incremented when posts cause position changes

## Development Notes

- Database password is hardcoded in `application.yml` - should be externalized for production
- Entities use Lombok for boilerplate reduction
- JPA relationships are mostly unidirectional to avoid circular dependencies
- Hibernate DDL auto-update is enabled for development

## Project Structure

```
src/main/java/gendervs/gendervs1/
├── domain/entity/          # JPA entities (13 domain objects)
├── dto/                    # Data transfer objects
├── repository/             # JPA repositories  
├── service/               # Business logic
├── controller/            # REST controllers
└── config/                # Configuration classes
```

Specification documents are located in `gendervs_explain/` directory containing requirements, ERD, and API specifications.