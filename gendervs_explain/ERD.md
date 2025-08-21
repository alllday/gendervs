# ERD

| 테이블명 | 설명 |
| --- | --- |
| users | 회원(일반/관리자), 인증, 정보 등 계정 관리 |
| terms_agreements | 약관 동의 내역 |
| topics | 논제(질문/주제). 카테고리, 설명, 공식/사용자 논제 구분 |
| topic_positions | 논제별 입장(A, B, C) |
| user_positions | 논제별 회원 입장 선택/변경 이력 |
| posts | 게시글(논제/입장별), 이미지/동영상 첨부 지원 |
| post_attachments | 게시글 첨부파일 |
| comments | 게시글/댓글/대댓글(1단계) |
| votes | 논제/게시글/댓글/대댓글 추천/비추천 기록 |
| reports | 신고 내역 |
| blacklist_words | 금칙어/비속어 |
| admin_logs | 관리자 활동 로그 |
| user_profiles | 회원 개별 프로필/설정 |

---

### users

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| user_id | SERIAL PRIMARY KEY | 회원 고유 ID |
| login_id | VARCHAR(20) NOT NULL UNIQUE | 아이디(로그인 ID) |
| password_hash | VARCHAR(255) NOT NULL | 비밀번호 해시 |
| email | VARCHAR(100) NOT NULL UNIQUE | 이메일 |
| phone_encrypted | VARCHAR(255) NOT NULL UNIQUE | 휴대폰 번호(실제 번호를 복호화 가능한 암호화 저장, AES256) |
| phone_hash | CHAR(64) NOT NULL UNIQUE | 휴대폰 번호(해시-중복확인용, SHA256 등) |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 가입일시 |
| updated_at | TIMESTAMP | 정보 수정일시 |
| status | BOOLEAN DEFAULT TRUE | 활성 회원 여부(탈퇴시 false) |
| suspend_until | TIMESTAMP | 정지만료일 |
| is_admin | BOOLEAN DEFAULT FALSE | 관리자 여부 |
| last_login | TIMESTAMP  | 최근 로그인 |

---

### terms_agreements

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| agreement_id | SERIAL PRIMARY KEY | 약관동의 고유 ID |
| user_id | INT REFERENCES user(user_id) ON DELETE CASCADE | 회원 ID |
| terms_type | VARCHAR(20) NOT NULL | 약관 종류(ENUM) |
| agreed_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 동의 일시 |

---

### topics

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| topic_id | SERIAL PRIMARY KEY | 논제 고유 ID |
| title | VARCHAR(100) NOT NULL | 논제 제목 |
| description | TEXT NOT NULL | 논제 설명 |
| category | VARCHAR(20) NOT NULL | 카테고리 |
| user_id | INT REFERENCES user(user_id) | 작성자(회원/관리자) |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 등록일시 |
| updated_at | TIMESTAMP | 수정일시 |
| topic_view | INTEGER DEFAULT 0 | 조회수 |
| like_count | INTEGER DEFAULT 0 | 추천수 |
| dislike_count | INTEGER DEFAULT 0 | 비추천수 |
| participate_count | INTEGER DEFAULT 0 | 참여수 |
| post_count | INTEGER DEFAULT 0 | 게시글수 |
| status | BOOLEAN DEFAULT TRUE | 논제 상태(정상/삭제 등) |
| is_editable  | BOOLEAN DEFAULT TRUE | 수정가능여부 |

---

### topic_positions

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| position_id | SERIAL PRIMARY KEY | 입장 고유 ID |
| topic_id | INT REFERENCES topic(topic_id) ON DELETE CASCADE | 논제 ID |
| position_code | CHAR(1) NOT NULL | 입장 코드(A/B/C) |
| position_text | VARCHAR(20) NOT NULL | 입장 설명 |

---

### user_positions

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| user_position_id | SERIAL PRIMARY KEY | 고유 ID |
| user_id | INT REFERENCES user(user_id) ON DELETE CASCADE | 회원 ID |
| topic_id | INT REFERENCES topic(topic_id) ON DELETE CASCADE | 논제 ID |
| position_id | INT REFERENCES topic_position(position_id) | 입장 ID |
| selected_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 입장 선택 일시 (현재 시간 - 최근 row의 selected_at = 변경 경과 시간) |
| is_current | BOOLEAN DEFAULT TRUE | 현재 입장 여부 (입장변경시 기존 row는 false + 새로운 row 생성) |
| reason | TEXT DEFAULT NULL | 변경 이유 |
| reason_post_id | INT REFERENCES post(post_id) DEFAULT NULL | 참조 게시글 |

---

### posts

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| post_id | SERIAL PRIMARY KEY | 게시글 고유 ID |
| topic_id | INT REFERENCES topic(topic_id) ON DELETE CASCADE | 논제 ID |
| position_id | INT REFERENCES topic_position(position_id) | 입장 ID |
| user_id | INT REFERENCES user(user_id) ON DELETE CASCADE | 작성자 ID |
| category | VARCHAR(20) NOT NULL | 카테고리(공지,건의,논제…) |
| title | VARCHAR(100) NOT NULL | 게시글 제목 |
| content | TEXT NOT NULL | 게시글 내용 |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 작성일시 |
| updated_at | TIMESTAMP | 수정일시 |
| post_view | INTEGER DEFAULT 0 | 조회수 |
| like_count | INTEGER DEFAULT 0 | 추천수 |
| dislike_count | INTEGER DEFAULT 0 | 비추천수 |
| comment_count | INTEGER DEFAULT 0 | 댓글수 |
| influence_score | INT DEFAULT 0 | 영향력 지수 |
| status | VARCHAR(10) DEFAULT 'active' | 게시글 상태(정상/삭제 등) |
| is_editable  | BOOLEAN DEFAULT TRUE | 수정가능여부 |
- topic_id, position_id, influence_score → 논제 게시글 외에 NULL

---

### post_attachments

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| attachment_id | SERIAL PRIMARY KEY | 첨부파일 고유 ID |
| post_id | INT REFERENCES post(post_id) ON DELETE CASCADE | 게시글 ID |
| file_url | VARCHAR(255) NOT NULL | 파일 URL/경로 |
| file_type | VARCHAR(10) NOT NULL | 파일 타입(image, video 등) |
| uploaded_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 업로드 일시 |

---

### comments

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| comment_id | SERIAL PRIMARY KEY | 댓글 고유 ID |
| post_id | INT REFERENCES post(post_id) ON DELETE CASCADE | 게시글 ID |
| parent_comment_id | INT REFERENCES comment(comment_id) DEFAULT  NULL | 부모 댓글 ID |
| user_id | INT REFERENCES user(user_id) ON DELETE CASCADE | 작성자 ID |
| content | TEXT NOT NULL | 댓글 내용 |
| depth | INT DEFAULT 1 | 계층 단계(1단계=최상위) |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 작성일시 |
| updated_at | TIMESTAMP | 수정일시 |
| like_count | INTEGER DEFAULT 0 | 추천수 |
| dislike_count | INTEGER DEFAULT 0 | 비추천수 |
| status | VARCHAR(10) DEFAULT 'active' | 상태(정상/삭제 등) |
| origin_position | CHAR(1) NOT NULL | 작성 당시 입장 코드 |
| is_editable  | BOOLEAN DEFAULT TRUE | 수정가능여부 |
- origin_position : 작성 당시 작성자의 해당 논제 입장 코드

---

### votes

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| vote_id | SERIAL PRIMARY KEY | 추천/비추천 고유 ID |
| user_id | INT REFERENCES user(user_id) ON DELETE CASCADE | 회원 ID |
| target_type | VARCHAR(10) NOT NULL | 대상 종류('topic','post','comment') |
| target_id | INT NOT NULL | 대상 고유 ID |
| vote_type | VARCHAR(10) NOT NULL | 추천/비추천(like/dislike) |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 추천/비추천 일시 |
- **vote 테이블에 UNIQUE 제약조건**
    
    (`user_id`, `target_type`, `target_id`) → 한 유저는 한 컨텐츠에 한 번만 “추천 or 비추천”만 가능
    

---

### reports

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| report_id | SERIAL PRIMARY KEY | 신고 고유 ID |
| user_id | INT REFERENCES user(user_id) ON DELETE CASCADE | 신고자 ID |
| target_type | VARCHAR(10) NOT NULL | 신고 대상 종류('topic','post','comment') |
| target_id | INT NOT NULL | 대상 고유 ID |
| reason_code | VARCHAR(20) NOT NULL | 신고 사유 (욕설, 도배 … 등) |
| reason_text | VARCHAR(100) | 상세 사유 |
| reported_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 신고 일시 |
| processed | BOOLEAN DEFAULT FALSE | 처리 여부 |
| processed_at | TIMESTAMP | 처리 일시 |
| processor_id | INT REFERENCES users(user_id) | 처리 관리자 ID |
- **report 테이블에 UNIQUE 제약조건**
    
    (`reporter_id`, `target_type`, `target_id`) → 한 유저가 한 컨텐츠에 대한 중복 신고 방지
    
    **→같은 유저가 같은 대상에 여러 번 신고 시도 시, DB에서 에러가 발생** (중복 INSERT 차단).
    
    에러 응답만 처리하면 ok(위 vote테이블 경우도 동일)
    

---

### blacklist_words

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| word_id | SERIAL PRIMARY KEY | 금칙어 고유 ID |
| word | VARCHAR(30) UNIQUE | 금칙어 |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 등록 일시 |

---

### admin_logs

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| log_id | SERIAL PRIMARY KEY | 로그 고유 ID |
| admin_id | INT REFERENCES user(user_id) | 관리자 ID |
| action_type | VARCHAR(20) NOT NULL | 활동 유형(삭제,정지 등) |
| target_type | VARCHAR(10) NOT NULL | 대상 종류('topic','post',’user’ 등) |
| target_id | INT NOT NULL | 대상 고유 ID |
| reason | VARCHAR(100) | 사유 |
| created_at | TIMESTAMP DEFAULT CURRENT_TIMESTAMP | 일시 |

---

### user_profiles

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| user_id | INT REFERENCES user(user_id) PRIMARY KEY | 회원 고유 ID |
| nickname | VARCHAR(30) NOT NULL UNIQUE | 닉네임 |
| score | INT DEFAULT 0 | 영향력 점수 |
| gender | CHAR(1) NOT NULL | 성별 |
| birth | DATE NOT NULL | 생년월일 |
| updated_at | TIMESTAMP | 수정 일시 |

---

### **ERD**

![postgres - public.png](ERD%201e6eb37f5fd380a8b881f53f3965b0b9/postgres_-_public.png)

---

- **메모**
    1. status의 존재 이유
    
    →  “**연관된 데이터**(게시글, 댓글, 추천, 신고, 입장 등)”가 **많기 때문에**
    
    바로 **물리적으로 삭제하지 않고 논리적으로만 비활성화**(soft delete, 플래그 처리).
    
    만약 완전히 삭제시, 모든 연관 데이터가 FK 오류/삭제/고아 데이터가 됨. 
    
    그래서 status=false로 “숨김/종료/비활성화”만 하고, 데이터는 유지. 
    
    실제 “물리 삭제”는 유예 기간 뒤, 혹은 별도 정책에 따라 배치로 일괄 처리
    
    (추천/비추천의 경우 현재 상태가 중요하지, 로그는 중요하지 않다고 생각해 hard delete로 운영)
    
    ### **배치 일괄삭제 vs. 즉시(실시간) 삭제 차이**
    
    | 구분 | 실시간 삭제 | 배치(일괄) 삭제 |
    | --- | --- | --- |
    | **삭제 시점** | API 호출 즉시 | 주기적으로 모아서 한 번에 |
    | **데이터 복구** | 즉시 불가(바로 지워짐) | 배치 전엔 복구 가능 |
    | **연관 데이터** | FK 오류/고아 데이터 위험 | 비활성화 기간에 정리 가능 |
    | **정책 반영** | 단순/빠름 | “유예 기간”, “복구 신청” 등 정책 구현에 유리 |
    | **리소스 관리** | 요청 순간 DB 부하 집중 | DB 작업 시간/부하 분산 |
    | **감사/로깅** | 기록이 바로 사라짐 | 이력/로그 남기기 쉬움 |
    
    1. 왜 파일 자체(바이너리)를 DB에 저장하지 않을까?
    - **DB에 바이너리(BLOB)로 저장하면**
        - 백업/복구, 용량 관리, 속도, 비용 등에서 불리
        - 대형 파일은 쿼리·처리·백업 속도가 매우 느려짐
    - 실무에서는 파일(이미지, 동영상, 첨부 등)은
        
        **파일 스토리지**(S3, NAS, 클라우드, 별도 파일서버)에 저장하고
        
        DB에는 **위치(경로, URL, 파일명, UUID 등)만 저장**
        
    
    1. Redis 기반 중복 조회 방지 
    - **회원**은 user_id로 “거의 완벽한 1조회수 중복방지” 가능
    - **비회원**은 IP/세션/쿠키 등 “근접치로 최대한” 중복을 막음
        
        (완벽한 중복방지는 불가, 실무에선 감안하고 운영)
        
    
    → 조회수는 Redis에 모았다가 일정 주기마다 DB에 한꺼번에 더하기
    
    1. 논제, 게시글, 댓글 : “상호작용 발생 시 수정 금지” + “초기작성 후 N분 이내엔 자유수정”
    
    → 상호작용 발생 전이라도 작성 후 N분이 지나면 수정 불가, 상호작용이 있으면 N분 안이더라도 수정 불가
    
    **수정이 가능한 조건:**
    
    ① 아직 상호작용이 없음(is_editable=true) **AND** ② 현재 시각 < 작성시각(created_at) + N분
    
    1. 삭제 → 삭제요청 기준
    - 논제 : 참여수 ≥ 30 or 추천수 ≥ 10 or 게시글 수 ≥ 1
    - 게시글 : 댓글 ≥ 5 or 추천수 ≥ 10
    - 댓글 : 언제나 삭제가능, but  “삭제된 댓글입니다”로 대체 후 대댓글은 남겨두기
    
    (일단은 보수적으로 잡고 운영해보면서 적절한 수치 찾기 + 공지나 글쓰기 전에 삭제 제한 명시)
    
    1. 
    
    조회수