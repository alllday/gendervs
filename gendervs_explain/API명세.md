# API 명세

- **회원**
    
    
    - 회원가입 (user + user_profile 동시 등록)
        - **URL**: `POST /api/users/signup`
        
        ```json
        {
        "username": "genvsuser1",
        "password": "Qwerty1234!",
        "email": "[genvs1@example.com](mailto:genvs1@example.com)",
        "phone": "01012345678",      // 클라에서는 평문, 서버에서 암호화
        "nickname": "젠더왕",
        "gender": "M",               // 'M' or 'F'
        "birth": "1998-09-12"
        }
        ```
        
        | 필드명 | 타입 | 필수 | 설명 | 비고 |
        | --- | --- | --- | --- | --- |
        | username | string | Y | 로그인 아이디 | 중복불가, 4~20자 |
        | password | string | Y | 비밀번호 | 해시저장, 8~32자 |
        | email | string | Y | 이메일 | 중복불가 |
        | phone | string | Y | 휴대폰번호(암호화) | 중복확인(해시), AES256저장 |
        | nickname | string | Y | 닉네임 | 중복불가, 2~30자 |
        | gender | string | Y | 성별 | 'M'/'F' |
        | birth | string | Y | 생년월일 | YYYY-MM-DD |
        
        ### ▶️ 서버 처리 로직
        
        1. username, email, nickname, phone 중복 체크 (phone은 해시로)
        2. password 해시 저장 (`password_hash`)
        3. phone은 AES256 암호화 및 SHA256 해시값 저장
        4. user insert, user_profile insert (트랜잭션)
        5. 이메일/휴대폰 인증은 가입 이후 별도 플로우(권장)
        
        ### ▶️ Response
        
        - **Status Code**: `201 Created`
        - **Body**
            
            ```json
            
            {
              "userId": 101,
              "username": "genvsuser1",
              "nickname": "젠더왕"
            }
            
            ```
            
        
        ### ▶️ 에러 예시
        
        - **409 Conflict (중복)**
            
            ```json
            
            { "code": "DUPLICATE_USERNAME", "message": "이미 사용 중인 아이디입니다." }
            ```
            
            ```json
            
            { "code": "DUPLICATE_NICKNAME", "message": "이미 사용 중인 닉네임입니다." }
            ```
            
            ```json
            
            { "code": "DUPLICATE_PHONE", "message": "이미 등록된 휴대폰 번호입니다." }
            ```
            
        - **400 Bad Request (입력값 오류)**
            
            ```json
            
            { "code": "INVALID_BIRTH", "message": "생년월일 형식이 잘못되었습니다." }
            ```
            
        
        ### 🟦 [참고]
        
        - **이메일/휴대폰 인증**은 별도 API로 설계 (예:`/api/user/email/verify`,`/api/user/phone/verify`)
        - **닉네임/아이디/이메일/휴대폰**은 중복 체크용 별도 API로 실시간 검증 지원 추천
        - **프로필 정보 수정**도 `/api/user/me/profile` 등에서 PATCH/PUT으로 제공 가능
    - 이메일 인증코드 발송/검증
        
        ## 📧 **이메일 인증 API 명세**
        
        1. **인증코드 발송** (사용자가 이메일 입력 → 인증코드 전송)
        2. **인증코드 검증** (사용자가 받은 인증코드 입력 → 서버에서 코드 일치/만료 체크)
        
        ---
        
        ### 1. 이메일 인증코드 발송
        
        - **URL**: `POST /api/auth/email/send`
        - **Request Body**
            
            ```json
            
            {
              "email": "user@example.com"
            }
            ```
            
        - **Response**
            
            ```json
            
            {
              "result": "sent"
            }
            ```
            
        - **Status Code**: 200 OK
        - **에러 예시**
            
            ```json
            
            {
              "code": "DUPLICATE_EMAIL",
              "message": "이미 가입된 이메일입니다."
            }
            
            ```
            
        
        **비고**
        
        - 서버에서 인증코드(예: 6자리 랜덤 숫자) 생성 → 해당 이메일로 발송
        - 인증코드는 Redis/DB에 `email, code, 만료시간` 으로 저장
        - 너무 자주 요청시 rate limit 적용 권장
        
        ---
        
        ### 2. 이메일 인증코드 검증
        
        - **URL**: `POST /api/auth/email/verify`
        - **Request Body**
            
            ```json
            
            {
              "email": "user@example.com",
              "code": "573811"
            }
            ```
            
        - **Response (성공)**
            
            ```json
            
            {
              "result": "verified"
            }
            ```
            
        - **Status Code**: 200 OK
        - **에러 예시**
            
            ```json
            
            {
              "code": "INVALID_CODE",
              "message": "인증코드가 올바르지 않습니다."
            
            ```
            
            ```json
            
            {
              "code": "CODE_EXPIRED",
              "message": "인증코드가 만료되었습니다."
            }
            ```
            
        
        **비고**
        
        - 서버에서 DB/Redis에서 `email`로 최신 인증코드와 만료시간 확인
        - 일치 & 만료 안됐으면 성공 → 유저 테이블의 `email_verified = true`로 변경
        - 실패시 에러 응답
    - 휴대폰인증
        
        # 📱 **휴대폰 인증 API 명세**
        
        구조는 거의 동일, 문자(SMS)로 인증코드 발송
        
        (보통 SMS 전송 서비스 API 연동 필요: KISA, CoolSMS, Naver SENS, 카카오 알림톡 등)
        
        ---
        
        ### 1. 휴대폰 인증코드 발송
        
        - **URL**: `POST /api/auth/phone/send`
        - **Request Body**
            
            ```json
            
            {
              "phone": "01012345678"
            }
            ```
            
        - **Response**
            
            ```json
            
            {
              "result": "sent"
            }
            ```
            
        - **에러 예시**
            
            ```json
            
            {
              "code": "DUPLICATE_PHONE",
              "message": "이미 가입된 휴대폰 번호입니다."
            }
            ```
            
        
        ---
        
        ### 2. 휴대폰 인증코드 검증
        
        - **URL**: `POST /api/auth/phone/verify`
        - **Request Body**
            
            ```json
            
            {
              "phone": "01012345678",
              "code": "743281"
            }
            ```
            
        - **Response (성공)**
            
            ```json
            
            {
              "result": "verified"
            }
            ```
            
        - **에러 예시**
            
            ```json
            
            {
              "code": "INVALID_CODE",
              "message": "인증코드가 올바르지 않습니다."
            }
            ```
            
            ```json
            
            {
              "code": "CODE_EXPIRED",
              "message": "인증코드가 만료되었습니다."
            }
            ```
            
        
        **비고**
        
        - 인증 성공 시, user 테이블의 `phone_verified = true`로 변경
        
        # 🔥 추가 설계 팁
        
        - 인증코드는 **시간 제한(예: 5분)**
        - 너무 자주 발송 금지(1분 제한 등)
        - 실패 횟수 제한, 만료 처리
        - 인증코드 암호화/서버 저장 위치(메모리, Redis, DB 등) 고민
        - 탈퇴/재가입 등에서 인증 상태 클리어 처리
        
        ## 1️⃣ **인증코드 발송 API (`/send`)**
        
        ### ⬤ **성공**
        
        - **Status**: `200 OK`
            
            인증코드 정상 발송 (실패했어도 “이미 가입된 이메일/폰” 등의 명확한 사유가 있다면 그건 별도 에러로 분리)
            
        
        ### ⬤ **예외/에러**
        
        | 상황 | 상태코드 | 설명/예시 |
        | --- | --- | --- |
        | 이미 가입된 이메일/폰 | `409 Conflict` | (회원가입 단계에서) 이미 존재하는 자원에 인증코드 발송 불가 |
        | 과도한 요청(스팸 방지) | `429 Too Many Requests` | 일정 시간 내 요청 횟수 초과, rate limit 적용 |
        | 형식 오류, 파라미터 누락 | `400 Bad Request` | 잘못된 email/phone, 누락 등 |
        | 서버 내부 오류 | `500 Internal Server Error` | 외부 SMS 서비스 오류, 기타 예외 등 |
        
        ## 2️⃣ **인증코드 검증 API (`/verify`)**
        
        ### ⬤ **성공**
        
        - **Status**: `200 OK`
            
            인증코드 일치 → 인증 성공
            
        
        ### ⬤ **예외/에러**
        
        | 상황 | 상태코드 | 설명/예시 |
        | --- | --- | --- |
        | 인증코드 불일치 | `400 Bad Request` | (일반적으로 인증 실패는 클라 입력 실수 → 400 권장) |
        | 인증코드 만료 | `400 Bad Request` | 코드 자체는 맞지만, 유효기간이 지남 |
        | 인증코드 발송 이력 없음 | `404 Not Found` | 해당 email/phone에 대해 인증요청 이력이 없음 |
        | 시도 횟수 초과(락) | `429 Too Many Requests` | 인증실패 횟수 초과, 잠금상태 등 |
        | 서버 내부 오류 | `500 Internal Server Error` | DB/redis 장애, 외부 연동오류 등 |
    - 중복체크
        
        
        ## 1️⃣ **1단계: 기본정보 입력**
        
        ### **A. 실시간 단일 중복체크 API (프론트 실시간/버튼형 둘 다 가능)**
        
        - **아이디(로그인ID) 중복체크**
            - **URL:** `GET /api/users/check-username?username=abc123`
            - **Response:**
                
                ```json
                
                { "available": true }
                ```
                
                ```json
                
                { "available": false }
                ```
                
        - **닉네임 중복체크**
            - **URL:** `GET /api/user/check-nickname?nickname=개발왕`
            - **Response: 위와 동일**
            
        
        ---
        
        ### **B. 통합형 중복체크 API (1단계 [다음] 버튼 클릭 시 호출)**
        
        - **URL:** `POST /api/user/check-duplicate`
        - **Request Body**
            
            ```json
            
            {
              "username": "abc123",
              "nickname": "개발왕"
            }
            ```
            
        - **Response**
            
            ```json
            
            {
              "username": true,
              "nickname": false
            }
            ```
            
            - `true` = 사용 가능, `false` = 중복
        - **Status**
            - `200 OK`
            - `400 Bad Request` (파라미터 누락/형식오류)
        - **에러 예시**
            
            ```json
            
            {
              "code": "INVALID_PARAM",
              "message": "닉네임은 2자 이상이어야 합니다."
            }
            ```
            
        
        ---
        
        ## 2️⃣ **2단계: 이메일 인증**
        
        ### **실시간/버튼형 단일 중복체크 API**
        
        - **URL:** `GET /api/users/check-email?email=user@example.com`
        - **Response**
            
            ```json
            
            { "available": true }
            ```
            
            ```json
            
            { "available": false }
            ```
            
        - **코드 발송 API와 통합 가능**
            - `POST /api/auth/email/send` 요청 시 내부적으로 중복체크 후 처리
        
        ---
        
        ## 3️⃣ **3단계: 휴대폰 인증**
        
        ### **실시간/버튼형 단일 중복체크 API**
        
        - **URL:** `GET /api/users/check-phone?phone=01012345678`
        - **Response : 위와 동일**
        - **코드 발송 API와 통합 가능**
            - `POST /api/auth/phone/send` 요청 시 내부적으로 중복체크 후 처리
        
        ---
        
        ## 4️⃣ **4단계: 최종 회원가입(서버 내부 처리)**
        
        - 1,2,3단계에서 입력받은 모든 값을 insert
        - **insert 시 DB의 unique 제약조건이 모든 동시성(동시 insert/race condition)까지 완벽하게 막아주기 때문에 최종 insert 전 중복체크(SELECT)는 “필수”가 아니다**
        
        ---
        
        ### 🔥 **요약**
        
        - **1단계:**
            - 실시간 단일 체크(UX) + 통합형 체크(최종 검증)
        - **2, 3단계:**
            - 실시간/버튼형 단일 체크만 사용(코드 발송 전 중복 확인)
        - **4단계:**
            - 서버에서 insert
        
    - 마이페이지
        - 기본정보 조회
            
            ## 1️⃣ **URL**
            
            ```
            GET /api/users/profile
            (본인 정보 확인)
            
            GET /api/users/profile/{user_id}
            (타인 정보 확인)
            ```
            
            ---
            
            ## 2️⃣쿼리파라미터 (request 예시)
            
            ```
            GET /api/users/profile
            
            GET /api/users/profile/32
            ```
            
            ---
            
            ## 3️⃣ **response 예시**
            
            ```json
            {
              "user_id": 42,
              "username" : "allday", // 아이디
              "nickname": "Alice", // 닉네임
              "email": "alice@example.com",
              "created_at": "2024-01-01T12:34:56Z",
              "last_login": "2025-07-18T09:00:00Z",
              "score": "123" // 스코어로 받고 백엔드에서 계산해서 등급 산출
            }
            
            {
              "user_id": 32,
              "nickname": "Alice", // 닉네임
              "created_at": "2024-01-01T12:34:56Z",
              "score": "123" // 스코어로 받고 백엔드에서 계산해서 등급 산출
            }
            ```
            
            ---
            
            ## 4️⃣ **응답 필드 설명 (ERD 기준)**
            
            | 필드명 | ERD 컬럼명(테이블) | 설명 |
            | --- | --- | --- |
            | user_id | user.user_id | 회원 고유 ID |
            | username | user.username | 로그인 아이디 |
            | nickname | user_profile.nickname | 닉네임 |
            | email | user.email | 이메일 주소 |
            | created_at | user.created_at | 가입일 |
            | last_login | user.last_login | 최근 로그인 일시 |
            | score | user_profile.score | 영향력 점수 |
            
            ---
            
            ## 5️⃣ **에러 응답 예시**
            
            - **존재하지 않는 회원(Not Found 404)**
            - **권한 없음(타인 정보 접근 등)(Forbidden 403)**
            - **서버 오류(Server Error 500)**
            
            ---
            
            ## 6️⃣ **특이사항**
            
            - **로그인을 해야** 프로필을 조회할 수 있도록 인증 필요(토큰 등)
            
        - 활동내역
            - 참여중인 논제
                
                ## 1️⃣ **URL**
                
                ```
                GET /api/users/profile/participated-topics
                ```
                
                ---
                
                ## 2️⃣ 쿼리파라미터 (request 예시)
                
                ```
                GET /api/users/me/participated-topics?category=연애&search=데이트
                ```
                
                | 파라미터 | 타입 | 설명 | 예시 |
                | --- | --- | --- | --- |
                | category | string | 논제 카테고리 필터 | 연애, 사회, 시사 등 |
                | search | string | 제목 검색 키워드 | "데이트" |
                | page | int | 페이지 번호 | `page=1` |
                | size | int | 페이지당 개수 | `size=10` |
                
                ---
                
                ## 3️⃣ **response 예시**
                
                ```json
                {
                  "items": [
                    {
                      "no": 1,
                      "topic_code": "P8F3K2",
                      "category": "연애",
                      "title": "데이트 비용, 누가 더 내야 하는가?",
                      "position_code": "B",
                      "position_text": "남자",
                      "joined_at": "2025-07-12T18:00:00+09:00",
                      "participant_count": 432
                    },
                   {
                      "no": 2,
                      "topic_code": "P7S3Q1",
                      "category": "사회",
                      "title": "남녀 임금격차, 존재하나?",
                      "position_code": "A",
                      "position_text": "존재한다",
                      "joined_at": "2025-07-05T15:32:10+09:00",
                      "participant_count": 853
                    }
                  ]
                }
                ```
                
                ---
                
                ## 4️⃣ **응답 필드 설명 (ERD 기준)**
                
                | 필드명 | ERD 컬럼 | 설명 |
                | --- | --- | --- |
                | no | - | joined_at 최신순 내림차(번호1부터 시작) |
                | topic_code | - | 논제 PK(topic.topic_id) 변환 |
                | category | topic.category | 논제 카테고리 |
                | title | topic.title | 논제 제목 |
                | position_code | topic_position.position_code | 내가 선택한 입장코드 |
                | position_text | topic_position.position_text | 입장코드설명 |
                | joined_at | user_position.created_at | 내가 입장 선택한 일 |
                | participant_count | topic.participate | 해당 논제 전체 참여자 수 |
                | total | (페이징) | 전체 참여 논제 수(검색·필터 적용 결과) |
                | page | (페이징) | 현재 페이지 번호 |
                | size | (페이징) | 페이지당 리스트 개수 |
                
                ---
                
                ## 5️⃣ **에러 응답 예시**
                
                - **잘못된 파라미터(Bad Request 400)**
                - **권한 없음(타인 정보 접근 등)(Forbidden 403)**
                - **서버 오류(500)**
                
                ---
                
                ## 6️⃣ **특이사항**
                
                - 제목검색과, 카테고리 필터만 존재
                - 기본 정렬은 입장선택일 최신순
                - 논제PK를 논제코드로 변환(논제PK 노출 위험 down + 길이 최소화), 알고리즘은 미정
                - 이거 포함 아래 2개(내가쓴 ~)도 번호는 그냥 자동생성되는걸로 사용
                
            - 내가 쓴 게시글
                
                ## 1️⃣ **URL**
                
                ```
                GET /api/users/profile/posts
                ```
                
                ---
                
                ## 2️⃣ 쿼리파라미터 (request 예시)
                
                ```
                GET /api/users/profile/posts?page=0&size=10&category=TOPIC&search=데이트&search_type=제목&order=recent
                ```
                
                | 파라미터 | 타입 | 설명 | 예시 |
                | --- | --- | --- | --- |
                | page | int | 페이지 번호(0부터) | 0 |
                | size | int | 페이지 크기 | 10 |
                | category | string | 게시글 카테고리 필터 | `TOPIC`(논제글), `SUGGESTION`(건의글), `ALL`(기본) |
                | search | string | 키워드 검색(부분일치) | `데이트` |
                | search_type | string | 검색 필드 | 제목, 논제코드 |
                | order | string | 정렬 기준**(여기뿐만 아니라, 높/낮은 순, 최신/오래된 순 이거 어떻게 할지 결정)** | `recent`(작성일↓, 기본), `oldest`(작성일↑), `views`(조회수↓), `votes`(추천-비추천↓) |
                
                ---
                
                ## 3️⃣ **response 예시**
                
                ```json
                {
                  "total": 2,
                  "page": 0,
                  "size": 10,
                  "items": [
                    {
                      "post_code": "P8F3K2",                 // 게시글PK 변환 코드
                      "category": "TOPIC",                   // 게시글 카테고리
                      "topic_code": "T1021",                 // 논제코드(논제글인경우)
                      "title": "데이트 비용, 누가 더 내야 하는가?",
                      "vote_score": 7,                       // (추천수 - 비추천수)
                      "view_count": 1234,
                      "created_at": "2025-07-12T18:00:00+09:00"
                    },
                    {
                      "post_code": "P7ZD11",
                      "category": "SUGGESTION",
                      "topic_code": null,                    // 논제외 글인경우
                      "title": "모바일 글쓰기 UI 개선 제안",
                      "vote_score": 2,
                      "view_count": 356,
                      "created_at": "2025-07-05T09:12:34+09:00"
                    }
                  ]
                }
                ```
                
                ---
                
                ## 4️⃣ **응답 필드 설명 (ERD 기준)**
                
                | 필드명 | 타입 | 설명 | ERD 기준 |
                | --- | --- | --- | --- |
                | total | int | 필터 적용된 총 게시글 수 | - |
                | page | int | 현재 페이지 번호 | - |
                | size | int | 페이지 크기 | - |
                | post_code | string | 게시글 PK 변환 코드 | `post.post_id` → 코드 변환 |
                | category | string | 게시글 카테고리 | `post.category` (`TOPIC`, `SUGGESTION` …) |
                | topic_code | string/null | 논제 코드 | `topic.topic_id` → 코드 변환 |
                | title | string | 게시글 제목 | `post.title` |
                | vote_score | int | 추천수-비추천수 | `post.like- post.dislike`  |
                | view_count | int | 조회수 | `post.view_count` |
                | created_at | datetime | 작성일시 | `post.created_at` |
                
                ---
                
                ## 5️⃣ **에러 응답 예시**
                
                - **잘못된 파라미터(Bad Request 400)**
                - **로그인 필요(Unauthorized 401)**
                - **서버 오류(500)**
                
                ---
                
                ## 6️⃣ **특이사항**
                
                - post_code / topic_code 변환 어떻게 할지~
                - 코드 클릭 시, 상세화면으로 이동
                
            - 내가 쓴 댓글
                
                ## 1️⃣ **URL**
                
                ```
                GET /api/users/profile/comments
                ```
                
                ---
                
                ## 2️⃣ 쿼리파라미터 (request 예시)
                
                ```
                GET /api/users/profile/comments?page=0&size=10&search=P8F3K2&search_type=P8F3K2&order=recent
                ```
                
                | 파라미터 | 타입 | 설명 | 예시 |
                | --- | --- | --- | --- |
                | page | int | 페이지 번호(0부터, 기본 0) | 0 |
                | size | int | 페이지 크기(기본 20) | 10 |
                | search | string | 키워드 검색(부분일치) | 결제 |
                | search_type | string | 검색 필드 | content |
                | order | string | 정렬 기준 | recent |
                - search_type : 내용(content), 논제코드(topic_code), 게시글코드(post_code)
                - 정렬은 미정
                
                ---
                
                ## 3️⃣ **response 예시**
                
                ```json
                {
                  "total": 2,
                  "page": 0,
                  "size": 10,
                  "items": [
                    {
                      "content": "결제 수수료는 판매자와 구매자 모두에게 공평해야 합니다.",
                      "vote_score": 5,
                      "topic_code": "T1021",
                      "post_code": "P8F3K2",
                      "created_at": "2025-07-12T18:25:40+09:00"
                    },
                    {
                      "content": "해당 통계 출처를 링크로 남겨주세요.",
                      "vote_score": 1,
                      "topic_code": "T1021",
                      "post_code": "P8F3K2",
                      "created_at": "2025-07-11T09:03:12+09:00"
                    }
                  ]
                }
                ```
                
                ---
                
                ## 4️⃣ **응답 필드 설명 (ERD 기준)**
                
                | 필드명 | 타입 | 설명 | ERD 기준 |
                | --- | --- | --- | --- |
                | total | int | 조건 적용된 총 댓글 수 | - |
                | page | int | 현재 페이지 번호 | - |
                | size | int | 페이지 크기 | - |
                | content | string | 댓글 내용 | `comment.content` |
                | vote_score | int | 추천수-비추천수 | `comment.like- comment.dislike` |
                | topic_code | string | 논제 코드 | `topic.topic_id` → 코드 변환 |
                | post_code | string | 게시글 코드 | `post.post_id` → 코드 변환 |
                | created_at | datetime | 댓글 작성일시 | `comment.created_at` |
                
                ---
                
                ## 5️⃣ **에러 응답 예시**
                
                - **잘못된 파라미터(Bad Request 400)**
                - **로그인 필요(Unauthorized 401)**
                - **서버 오류(500)**
                
                ---
                
                ## 6️⃣ **특이사항**
                
                - 코드 클릭 시, 상세화면으로 이동
                - 내용 글자수 상한 결정(프론트 보고 결정)
                
            
            (활동내역은 설정을 통해 타인에게 공개/비공개 가능)
            
        - ~~설정 (=마이페이지 정보 변경, 일단 보류 - 마이페이지 정보 미확정)~~
            
            **1️⃣ URL:** `PATCH /api/user-profiles`
            
            ### 2️⃣ **Request Body 예시**
            
            ```json
            
            {
              "nickname": "새닉네임",
              "current_password": "oldPw123!",
              "new_password": "newPw456!",
              "new_password_confirm": "newPw456!"
            }
            ```
            
            - **nickname**: 새 닉네임(옵션)
            - **current_password**: 현재 비밀번호(필수)
            - **new_password**: 새 비밀번호(옵션, 비번 변경 시)
            - **new_password_confirm**: 새 비밀번호 확인(옵션)
            
            ### **백엔드 처리 로직**
            
            - **현재 비밀번호** 반드시 일치하는지 체크(보안!)
            - **닉네임**: 값 있으면 변경, 중복/유효성 체크
            - **새 비밀번호**: 값 있으면
                - new_password와 new_password_confirm 일치 여부 확인
                - 비밀번호 정책(길이, 조합 등) 통과 시 변경
            
            ### 3️⃣**Response 예시**
            
            - **200 OK**
            
            ```json
            
            {
              "success": true,
              "message": "회원 정보가 성공적으로 변경되었습니다.",
              "user_id": 7
            }
            
            ```
            
            ### 4️⃣**에러 응답 예시**
            
            - 현재 비밀번호 불일치(**HTTP status:** 403 Forbidden)
            - 새 비밀번호 불일치(**HTTP status:** 400 Bad Request)
            - 닉네임 중복(**HTTP status:** 409 Conflict)
            - 서버 내부 오류(**HTTP status:** 500 Internal Server Error)
    - 탈퇴
        
        **1️⃣ URL:** `DELETE /api/users`
        
        ## 2️⃣ **Request Body 예시**
        
        ```json
        
        {
          "current_password": "oldPw123!"
        }
        ```
        
        - **current_password**: 본인 확인용(필수, 보안 강화)
        
        ---
        
        ## 3️⃣ **백엔드 처리 로직**
        
        - **현재 비밀번호** 반드시 일치하는지 확인
        - **is_active = false** 또는 **status = 'deleted'**로 소프트 딜리트 처리
        - 실제 DB 삭제는 정책/배치 처리에 따라 별도 수행
        
        ---
        
        ## 4️⃣ **Response 예시**
        
        - **성공 (200 OK)**
        
        ```json
        
        {
          "success": true,
          "message": "회원 탈퇴가 성공적으로 처리되었습니다."
        }
        ```
        
        ---
        
        ## 5️⃣ **에러 응답 예시**
        
        **현재 비밀번호 불일치 (403 Forbidden)**
        
        ```json
        {
          "success": false,
          "code": "FORBIDDEN",
          "message": "비밀번호가 일치하지 않습니다."
        }
        ```
        
        - **이미 탈퇴된 계정 (409 Conflict)**
        
        ```json
        {
          "success": false,
          "code": "CONFLICT",
          "message": "이미 탈퇴 처리된 계정입니다."
        }
        ```
        
        - **인증 실패(미로그인, 토큰 만료 등, 401 Unauthorized)**
        
        ```json
        {
          "success": false,
          "code": "UNAUTHORIZED",
          "message": "로그인이 필요합니다."
        }
        ```
        
        - **입력값 오류 (400 Bad Request)**
        
        ```json
        {
          "success": false,
          "code": "BAD_REQUEST",
          "message": "입력값이 올바르지 않습니다."
        }
        ```
        
        - **서버 내부 오류(HTTP status: 500 Internal Server Error)**
        
        ```json
        {
          "success": false,
          "code": "INTERNAL_SERVER_ERROR",
          "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        }
        ```
        
    - 상대 변경(관리자용)
        
        **1️⃣URL:** `PATCH /api/users/{user_id}/status`
        
        ### 2️⃣ **Request Body 예시**
        
        ```json
        
        {
          "status": "hidden",
          "period": 7, // user의 hidden의 경우, 프론트에서 기간 선택 (7일,30일,1년 등)
          "reason": "욕설/비방 등 운영정책 위반"   // (선택) 상태 변경 사유
        }
        ```
        
        ### status
        
        - `"active"` : 정상
        - `"hidden"` : 활동 정지, period 추가
        - `"deleted"` : 영구 정지 = 삭제
        - 서비스 정책에 따라 추가 가능
        
        ### 3️⃣ **Response 예시**
        
        ```json
        
        {
          "success": true,
          "message": "논제 상태가 성공적으로 변경되었습니다.",
          "user_id": 41,
          "status": "hidden",
          "period": 7,
          "created_at": "2024-07-10T19:42:20Z",
          "suspend_until":"2024-07-17T19:42:20Z"
        }
        ```
        
        ### **4️⃣ 에러 응답 예시**
        
        - 권한 없음-관리자만 가능(**HTTP status:** 403 Forbidden)
        - 존재하지 않는 유저(**HTTP status:** 404 Not Found)
        - 잘못된 값 입력(**HTTP status:** 400 Bad Request)
        - 서버 내부 오류(**HTTP status:** 500 Internal Server Error)
    

---

- **논제**
    
    
    - 목록조회
        
        **1️⃣ URL:** `GET /api/topics`
        
        2️⃣ **쿼리 파라미터**
        
        | 파라미터 | 타입 | 설명 | 예시 |
        | --- | --- | --- | --- |
        | category | string | 논제 카테고리 | `category=military` |
        | sort | string | 정렬 기준 | `sort=createdAt` |
        | search | string | 검색 | `search=여성` |
        | search_type | string | 검색 필드 | `search_type=title` |
        | page | int | 페이지 번호 | `page=1` |
        | size | int | 페이지당 개수 | `size=20` |
        - 카테고리 8개
        - 정렬 기준 5개 - 최신순, 추천순, 조회순, 게시글순, 참여순
        - 페이지 번호 1부터 시작, 개수 기본 20 → 입력안할시 디폴트
        - 검색 파라미터 : 제목(디폴트), 작성자, 내용
        
        **예시 요청**
        
        ```json
        GET /api/topic?category=military&sort=createdAt,desc&search="군대"
        ```
        
         
        
        ### 3️⃣ **Response 예시**
        
        ```json
        
        {
          "content": [
            {
              "topic_id": 41,
              "title": "여성도 군대 가야 할까?",
              "category": "military",
              "created_by": 7,
              "created_at": "2024-06-29T18:40:17Z",
              "topic_view": 1372,
              "post_count": 84,
              "participant_count": 91,
              "like_count": 229,
              "author": { 
        	      "user_id": 7,
        	      "nickname": "국방토론러" 
              }
            },
            {
              "topic_id": 38,
              "title": "군대 내 남녀평등, 현실적으로 가능할까?",
              "category": "military",
              "created_by": 12,
              "created_at": "2024-06-28T15:12:40Z",
              "topic_view": 1021,
              "post_count": 65,
              "participant_count": 63,
              "like_count": 142,
              "author": { 
        	      "user_id": 7,
        	      "nickname": "젠더워치" 
              }
            }
            // ... (최대 size개)
          ],
          "total_pages": 2,
          "total_elements": 29,
          "page": 1,
          "size": 20
        }
        
        ```
        
        ### 4️⃣ **응답 필드 설명 (ERD 컬럼명 기준)**
        
        | 필드명 | ERD 컬럼명/테이블 | 설명 |
        | --- | --- | --- |
        | topic_id | topic.topic_id | 논제(토픽) 고유 ID |
        | title | topic.title | 논제 제목 |
        | category | topic.category | 논제 카테고리 |
        | created_at | topic.created_at | 등록일시 |
        | topic_view | topic.topic_view | 논제 조회수 |
        | post_count | (post 집계) | 논제 관련 전체 게시글 수 |
        | participant_count | (user_position 집계) | 논제별 입장 선택/참여자 수 |
        | like_count | (vote 집계) | 논제 추천(좋아요) 수 |
        | author.user_id | user_profile.user_id | 논제 작성자 ID |
        | author.nickname | user_profile.nickname | 논제 작성자 닉네임 |
        | total_pages | (페이징) | 전체 페이지 수 |
        | total_elements | (페이징) | 전체 논제(검색 결과) 개수 |
        | page | (페이징) | 현재 페이지 번호 |
        | size | (페이징) | 페이지당 논제 개수 |
        
    - 상세조회(게시글목록은 별도의 api)
        
        **1️⃣ URL:** `GET /api/topics/{topicId}`
        
        ---
        
        ### 2️⃣ **Response 예시**
        
        ```json
        {
          "topic_id": 41,
          "title": "여성도 군대 가야 할까?",
          "description": "군 복무 의무를 남녀 모두에게 부여해야 한다는 주장에 대해 다양한 입장과 근거를 제시해보세요.",
          "category": "military",
          "created_at": "2024-06-29T18:40:17Z",
          "updated_at": "2024-06-29T19:02:12Z",
          "topic_view": 1372,
          "is_editable": true,
          "status": "active",
        
          "positions": [
            { "position_id": 1, "position_code": "A", "position_text": "필요하다" },
            { "position_id": 2, "position_code": "B", "position_text": "필요없다" },
            { "position_id": 3, "position_code": "C", "position_text": "기타" }
          ],
        
          "comment_count": 84,         // 논제 전체 댓글(참여수 아님)
          "participant_count": 91,     // 논제별 입장 선택한 회원수
          "like_count": 229,           // 논제 추천수
        
          "author": {
            "user_id": 7,
            "nickname": "국방토론러"
          }
        }
        ```
        
        ---
        
        ## 3️⃣ **필드 설명 (ERD 기준)**
        
        | 필드명 | ERD 컬럼/테이블 | 설명 |
        | --- | --- | --- |
        | topic_id | topic.topic_id | 논제(토픽) 고유 ID |
        | title | topic.title | 논제 제목 |
        | description | topic.description | 논제 설명 |
        | category | topic.category | 논제 카테고리 |
        | created_at | topic.created_at | 등록 일시 |
        | updated_at | topic.updated_at | 수정 일시 |
        | topic_view | topic.topic_view | 논제 조회수 |
        | is_editable | topic.is_editable | 논제 수정 가능 여부 |
        | status | topic.status | 논제 상태(active/deleted 등) |
        | positions[] | topic_position.* | 논제별 입장(A/B/C 등) 리스트 |
        | comment_count | (comment 집계) | 논제 전체의 게시글+댓글 수 |
        | participant_count | (user_position 집계) | 논제 입장 선택한 회원수 |
        | like_count | (vote 집계) | 논제 추천(좋아요) 수 |
        | author.user_id | user.user_id | 논제 작성자 회원 ID |
        | author.nickname | user_profile.nickname | 논제 작성자 닉네임 |
        
        ### positions
        
        | 필드명 | ERD 컬럼/테이블 | 설명 |
        | --- | --- | --- |
        | position_id | topic_position.position_id | 입장(포지션) 고유 ID |
        | position_code | topic_position.position_code | 입장 코드(A/B/C 등, 논제별 부여) |
        | position_text | topic_position.position_text | 입장 설명(예: 찬성/반대/기타 등) |
        
        ## **4️⃣ 에러 응답 예시**
        
        - **존재하지 않는 논제(404 Not Found)**
            
            ```json
            {
              "success": false,
              "code": "NOT_FOUND",
              "message": "존재하지 않는 논제입니다."
            
            ```
            
        - **잘못된 파라미터(400 Bad Request)**
            
            ```json
            {
              "success": false,
              "code": "BAD_REQUEST",
              "message": "지원하지 않는 값이 입력되었습니다."
            }
            ```
            
        - **서버 내부 오류(500 Internal Server Error)**
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
        
    - 등록
        
        **1️⃣ URL:** `POST /api/topics`
        
        ### **2️⃣ Request Body 예시 (JSON)**
        
        ```json
        
        {
          "title": "여성도 군대 가야 할까?",
          "description": "군 복무 의무를 남녀 모두에게 부여해야 한다는 주장에 대해 다양한 입장과 근거를 제시해보세요.",
          "category": "military",
          "positions": [
            { "position_code": "A", "position_text": "필요하다" },
            { "position_code": "B", "position_text": "필요없다" },
            { "position_code": "C", "position_text": "기타" }
          ]
        }
        ```
        
        - **title**: 논제 제목 (`topic.title`)
        - **description**: 논제 설명 (`topic.description`)
        - **category**: 논제 카테고리 (`topic.category`)
        - **positions**: 논제별 입장 리스트 (`topic_position`에 INSERT)
        
        > 작성자 정보(created_by)는 로그인 세션/토큰에서 추출 (별도 입력X, 서버에서 처리)
        > 
        
        ### **3️⃣ Response 예시**
        
        - **201 Created (Status code는 HTTP header에 포함)**
        - Response body
        
        ```json
        
        {
          "success": true,
          "message": "논제가 성공적으로 등록되었습니다.",
          "topic_id": 41
        }
        ```
        
        - 필수/유효성 체크
            1. **title, description, category, positions**: 모두 필수
            2. 중복 논제 제목 금지
            3. 사용자가 로그인 상태인지 확인
            
        
        ### **4️⃣ 에러 응답 예시**
        
        - 비로그인(**HTTP status:** 401 Unauthorized)
            
            ```json
            {
              "success": false,
              "code": "UNAUTHORIZED",
              "message": "로그인이 필요합니다."
            }
            ```
            
        - 필수값 누락, 잘못된 값 입력(**HTTP status:** 400 Bad Request)
            
            ```json
            
            {
              "success": false,
              "message": "필수 입력값이 누락되었습니다. 또는 잘못된 값이 입력되었습니다",
              "code": "BAD_REQUEST"
            }
            ```
            
        - 중복 논제(**HTTP status:** 409 Conflict)
            
            ```json
            
            {  
            	"success": false,
            	"message": "이미 등록된 논제 제목입니다."
            	"code": "CONFLICT"
            }
            ```
            
        - 서버 내부 오류(**HTTP status:** 500 Internal Server Error)
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
        
    - 수정
        
        **1️⃣URL:** `PATCH /api/topics/{topic_id}`
        
        (수정조건이 충족되지 않으면 아예 수정버튼단계에서 막기)
        
        ### 2️⃣ **Request Body 예시**
        
        ```json
        
        {
          "title": "여성 군복무 의무화, 필요할까?",
          "description": "최근 여성의 군복무 의무화 논의가 활발합니다. 다양한 입장과 근거를 공유해주세요.",
          "positions": [
            { "position_id": 1, "position_code": "A", "position_text": "필요하다" },
            { "position_id": 2, "position_code": "B", "position_text": "필요없다" },
            { "position_id": 3, "position_code": "C", "position_text": "기타" }
          ]
        }
        
        ```
        
        - **title**: 수정할 논제 제목
        - **description**: 논제 설명
        - **positions**: 입장 목록 (입장 설명/코드 수정 포함)
        
        ### **3️⃣ Response 예시**
        
        - **200 OK**
        
        ```json
        
        {
          "success": true,
          "message": "논제가 성공적으로 수정되었습니다.",
          "topic_id": 41
        }
        ```
        
        - 필수/유효성 체크
            1. 중복 논제 제목 금지
        
        ### **4️⃣ 에러 응답 예시**
        
        - 비로그인(**HTTP status:** 401 Unauthorized)
            
            ```json
            {
              "success": false,
              "code": "UNAUTHORIZED",
              "message": "로그인이 필요합니다."
            }
            ```
            
        - 권한 없음-작성자만 가능(**HTTP status:** 403 Forbidden)
            
            ```json
            {
              "success": false,
              "code": "FORBIDDEN",
              "message": "논제 수정 권한이 없습니다."
            }
            ```
            
        - 존재하지 않는 논제(**HTTP status:** 404 Not Found)
            
            ```json
            {
              "success": false,
              "code": "NOT_FOUND",
              "message": "존재하지 않는 논제입니다."
            }
            ```
            
        - 논제 수정 불가 상태(**HTTP status:** 409 Conflict)
            
            ```json
            {
              "success": false,
              "code": "CONFLICT",
              "message": "이미 토론이 시작되어 논제를 수정할 수 없습니다."
            }
            ```
            
        - 필수값 누락, 잘못된 값 입력(**HTTP status:** 400 Bad Request)
            
            ```json
            
            {
              "success": false,
              "message": "필수 입력값이 누락되었습니다. 또는 잘못된 값이 입력되었습니다",
              "code": "BAD_REQUEST"
            }
            ```
            
        - 중복 논제(**HTTP status:** 409 Conflict)
            
            ```json
            
            {  
            	"success": false,
            	"message": "이미 등록된 논제 제목입니다."
            	"code": "CONFLICT"
            }
            ```
            
        - 서버 내부 오류(**HTTP status:** 500 Internal Server Error)
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
    - 삭제
        
        **1️⃣URL:** `DELETE /api/topics/{topic_id}`
        
        ### 2️⃣ **Response 예시**
        
        - **204 No content**
        
        ```json
        
        {
          "success": true,
          "message": "논제가 성공적으로 삭제되었습니다.",
          "topic_id": 41
          "status": "deleted"
        }
        ```
        
        → 실제로는 topic의 status를 ‘delete’로 변경 (DB에서 물리적으로 삭제하지 않고, 비노출 처리)
        
        ### **3️⃣ 에러 응답 예시**
        
        - 비로그인(**HTTP status:** 401 Unauthorized)
            
            ```json
            {
              "success": false,
              "code": "UNAUTHORIZED",
              "message": "로그인이 필요합니다."
            }
            ```
            
        - 권한 없음-작성자,관리자만 가능(**HTTP status:** 403 Forbidden)
            
            ```json
            {
              "success": false,
              "code": "FORBIDDEN",
              "message": "논제 삭제 권한이 없습니다."
            }
            ```
            
        - 존재하지 않는 논제(**HTTP status:** 404 Not Found)
            
            ```json
            {
              "success": false,
              "code": "NOT_FOUND",
              "message": "존재하지 않는 논제입니다."
            }
            ```
            
        - 논제 삭제 불가 상태(**HTTP status:** 409 Conflict)
            
            ```json
            {
              "success": false,
              "code": "CONFLICT",
              "message": "이미 토론이 시작된 논제는 삭제할 수 없습니다."
            }
            ```
            
        - 서버 내부 오류(**HTTP status:** 500 Internal Server Error)
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
        
    - 상태 변경(관리자용)
        
        **1️⃣URL:** `PATCH /api/topics/{topic_id}/status`
        
        ### 2️⃣ **Request Body 예시**
        
        ```json
        
        {
          "status": "hidden",
          "reason": "욕설/비방 등 운영정책 위반"   // (선택) 상태 변경 사유
        }
        ```
        
        ### status
        
        - `"active"` : 정상 운영/노출
        - `"hidden"` : 블라인드(운영진 숨김)
        - `"deleted"` : (실질적 삭제, 소프트 딜리트)
        - 서비스 정책에 따라 추가 가능
        
        ### 3️⃣ **Response 예시**
        
        ```json
        
        {
          "success": true,
          "message": "논제 상태가 성공적으로 변경되었습니다.",
          "topic_id": 41,
          "status": "hidden",
          "created_at": "2024-07-10T19:42:20Z"
        }
        ```
        
        ### **4️⃣ 에러 응답 예시**
        
        - 권한 없음-관리자만 가능(**HTTP status:** 403 Forbidden)
            
            ```json
            {
              "success": false,
              "code": "FORBIDDEN",
              "message": "논제 상태 변경 권한이 없습니다."
            }
            ```
            
        - 존재하지 않는 논제(**HTTP status:** 404 Not Found)
            
            ```json
            {
              "success": false,
              "code": "NOT_FOUND",
              "message": "존재하지 않는 논제입니다."
            }
            ```
            
        - 잘못된 값 입력(**HTTP status:** 400 Bad Request)
            
            ```json
            
            {
              "success": false,
              "message": "잘못된 값이 입력되었습니다",
              "code": "BAD_REQUEST"
            }
            ```
            
        - 서버 내부 오류(**HTTP status:** 500 Internal Server Error)
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
        
    

---

- **입장**
    
    
    - 입장선택
        
        **1️⃣URL:** `POST /api/topics/{topic_id}/position`
        
        ## **2️⃣ Request Body 예시**
        
        ```json
        {
          "position": "A"
        }
        ```
        
        ## **3️⃣ Response 예시**
        
        ```json
        {
          "success": true,
          "message": "입장이 성공적으로 선택되었습니다.",
          "topic_id": 41,
          "user_id": 101,
          "user_position_id": 1234,
          "position": "A",
          "selected_at": "2025-07-09T23:00:00Z"
        }
        ```
        
        ## **4️⃣ 에러 응답 예시**
        
        - **이미 입장 선택한 경우 (HTTP status: 409 Conflict)**
            
            ```json
            {
              "success": false,
              "code": "ALREADY_SELECTED",
              "message": "이미 입장을 선택한 상태입니다. 입장 변경을 이용하세요."
            }
            ```
            
        - **존재하지 않는 논제/입장 (HTTP status: 404 Not Found)**
            
            ```json
            {
              "success": false,
              "code": "NOT_FOUND",
              "message": "존재하지 않는 논제 또는 입장입니다."
            }
            ```
            
        - **잘못된 값 입력 (HTTP status: 400 Bad Request)**
            
            ```json
            {
              "success": false,
              "code": "BAD_REQUEST",
              "message": "잘못된 값이 입력되었습니다."
            }
            ```
            
        - **권한 없음/로그인 필요 (HTTP status: 401 Unauthorized)**
            
            ```json
            {
              "success": false,
              "code": "UNAUTHORIZED",
              "message": "로그인이 필요합니다."
            }
            ```
            
        - **서버 내부 오류(HTTP status: 500 Internal Server Error)**
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
        
    - 입장변경
        
        **1️⃣URL:** `POST /api/topics/{topic_id}/position`
        
        ## **2️⃣ Request Body 예시**
        
        ```json
        {
          "position_id": 2,               // 변경할 입장 PK (topic_position.position_id)
          "reason": "이 글에 설득당해서 바꿔요.",    // 변경 이유 (선택)
          "reason_post_id": 341           // 참조 게시글 PK (선택, 없으면 null)
        }
        ```
        
        - 기존 is_current = true row는 is_current = false로 변경, 새 row는 is_current = true로 등록
        - UI상에서는 A,B,C 처럼 입장 코드(position_code)를 보여주지만 API통신은 position_id 이용
        
        ## **3️⃣ Response 예시**
        
        ```json
        {
          "success": true,
          "message": "입장이 성공적으로 변경되었습니다.",
          "user_id": 42,
          "topic_id": 12,
          "position_id": 2,
          "is_current": true,
          "selected_at": "2024-07-10T16:32:18Z",
          "reason": "이 글에 설득당해서 바꿔요.",
          "reason_post_id": 341
        }
        ```
        
        ## **4️⃣ 에러 응답 예시**
        
        - **존재하지 않는 논제/입장 (404 Not Found)**
            
            ```json
            {
              "success": false,
              "code": "NOT_FOUND",
              "message": "존재하지 않는 논제 또는 입장입니다."
            }
            ```
            
        - **이미 현재 해당 입장인 경우(409 Conflict)**
            
            ```json
            {
              "success": false,
              "code": "CONFLICT",
              "message": "이미 선택한 입장입니다."
            }
            ```
            
        - **잘못된 값 입력 (400 Bad Request)**
            
            ```json
            {
              "success": false,
              "code": "BAD_REQUEST",
              "message": "잘못된 값이 입력되었습니다."
            }
            ```
            
        - **권한 없음/로그인 필요 (401 Unauthorized)**
            
            ```json
            {
              "success": false,
              "code": "UNAUTHORIZED",
              "message": "로그인이 필요합니다."
            }
            ```
            
        - **서버 내부 오류(500 Internal Server Error)**
            
            ```json
            {
              "success": false,
              "code": "INTERNAL_SERVER_ERROR",
              "message": "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            }
            ```
            
        
    

---

- **게시글**
    
    
    - 목록조회
        
        ## **1️⃣ URL:**
        
        `GET /api/posts`
        
        ---
        
        ## **2️⃣ 쿼리 파라미터**
        
        | 파라미터 | 타입 | 설명 | 예시 |
        | --- | --- | --- | --- |
        | category | string | 게시글 카테고리(논제/공지/건의 등) | category=공지 |
        | topic_id | int | 논제 ID(논제 카테고리만) | topic_id=41 |
        | position_id | int | 입장 ID(논제 카테고리만) | position_id=2 |
        | sort | string | 정렬 기준 | sort=createdAt,desc |
        | search | string | 검색 키워드 | search=갈등 |
        | search_type | string | 검색 필드(title/content/nickname) | search_type=title |
        | page | int | 페이지 번호(기본 1) | page=1 |
        | size | int | 페이지당 개수(기본 20) | size=20 |
        - **정렬 기준** :`createdAt`(최신순, 기본), `influence`(영향순), `like`(추천순), `view`(조회순), `comment`(댓글순)
        - **검색 필드**:`title`(제목, 기본), `content`(본문), `nickname`(작성자 닉네임)
        
        ---
        
        ### **예시 요청**
        
        **A. 전체(공지/건의 등)**
        
        ```
        GET /api/posts?category=공지&sort=createdAt,desc&page=1&size=20
        ```
        
        **B. 논제별/입장별**
        
        ```
        GET /api/posts?category=논제&topic_id=41&position_id=2&sort=like,desc&page=1&size=20
        ```
        
        ---
        
        ## **3️⃣ Response 예시**
        
        ```json
        {
          "content": [
            {
              "post_id": 201,
              "category": "논제",
              "topic_id": 41,
              "position_id": 2,
              "position_code": "B",
              "user_id": 8,
              "nickname": "공감남",
              "title": "이런 공감 처음입니다",
              "created_at": "2025-07-10T13:41:00Z",
              "post_view": 1202,
              "comment_count": 44,
              "like_count": 92,
              "influence_score": 31
            },
            { // 논제 이외의 카테고리 게시글 조회에는 필드 3개 null + influence_score=0
              ~~"post_id": 171,
              "category": "공지",
              "topic_id": null,
              "position_id": null,
              "position_code": null,
              "user_id": 1,
              "nickname": "운영자",
              "title": "공지사항 안내",
              "created_at": "2025-07-09T09:10:00Z",
              "post_view": 352,
              "comment_count": 0,
              "like_count": 12,
              "influence_score": 0~~
            }
            // ... (최대 size개)
          ],
          "total_pages": 5,
          "total_elements": 86,
          "page": 1,
          "size": 20
        }
        ```
        
        ---
        
        ## **4️⃣ 응답 필드 설명 (ERD 컬럼명 기준)**
        
        | 필드명 | ERD 컬럼명/테이블 | 설명 |
        | --- | --- | --- |
        | post_id | post.post_id | 게시글 고유 ID |
        | category | post.category | 카테고리(논제/공지/건의) |
        | topic_id | post.topic_id | 논제 ID(논제글만 값 있음) |
        | position_id | post.position_id | 입장 ID(논제글만) |
        | position_code | topic_position.position_code | 입장 코드(논제글만) |
        | user_id | post.user_id | 작성자 ID |
        | nickname | user_profile.nickname | 작성자 닉네임 |
        | title | post.title | 게시글 제목 |
        | created_at | post.created_at | 등록일시 |
        | post_view | post.post_view | 조회수 |
        | comment_count | (comment 집계) | 댓글 수 |
        | like_count | (vote 집계) | 추천(좋아요) 수 |
        | influence_score | post.influence_score | 영향력 점수 |
        | total_pages | (페이징) | 전체 페이지 수 |
        | total_elements | (페이징) | 전체 게시글 개수 |
        | page | (페이징) | 현재 페이지 번호 |
        | size | (페이징) | 페이지당 게시글 개수 |
        
        ---
        
        ## **5️⃣ 에러 응답 예시**
        
        **→ 논제 상세조회랑 동일**
        
    - 상세조회(댓글 목록은 별도의 api)
        
        ## **1️⃣ URL:**
        
        `GET /api/posts/{post_id}`
        
        ---
        
        ## **2️⃣ Response 예시**
        
        ```json
        {
          "post_id": 341,
          "topic_id": 41,
          "position_id": 1,
          "position_code": "A",
          "position_text": "필요하다",
          "title": "여성 징병제 찬성 이유",
          "content": "여성도 국방 의무를 수행해야 하는 이유는...",
          "created_at": "2024-06-29T19:50:15Z",
          "updated_at": "2024-06-29T21:10:02Z",
          "post_view": 324,
          "influence_score": 12,
          "status": "active",
          "is_editable": true,
        
          "comment_count": 23,
          "like_count": 45,
        
          "author": {
            "user_id": 15,
            "nickname": "젠더어벤져스"
          },
        
          "attachments": [
            {
              "attachment_id": 17,
              "file_url": "https://cdn.site.com/uploads/202407/post_341_17.jpg",
              "file_type": "image",
              "uploaded_at": "2024-06-29T19:52:10Z"
            }
            // ...
          ]
        }
        ```
        
        ---
        
        ## **3️⃣ 응답 필드 설명**
        
        | 필드명 | ERD 컬럼명/테이블 | 설명 |
        | --- | --- | --- |
        | post_id | post.post_id | 게시글 고유 ID |
        | topic_id | post.topic_id | 논제 ID |
        | position_id | post.position_id | 입장(포지션) ID |
        | position_code | topic_position.position_code | 입장 코드(A/B/C) |
        | position_text | topic_position.position_text | 입장 설명 |
        | title | post.title | 게시글 제목 |
        | content | post.content | 게시글 본문 |
        | created_at | post.created_at | 게시글 작성일시 |
        | updated_at | post.updated_at | 게시글 수정일시 |
        | post_view | post.post_view | 조회수 |
        | influence_score | post.influence_score | 영향력 점수 |
        | status | post.status | 게시글 상태(정상/삭제 등) |
        | is_editable | post.is_editable | 수정 가능 여부(작성자 본인) |
        | comment_count | (comment 집계) | 게시글의 댓글 수 |
        | like_count | (vote 집계) | 게시글의 추천 수 |
        | author.user_id | user_profile.user_id | 작성자 ID |
        | author.nickname | user_profile.nickname | 작성자 닉네임 |
        | attachments | post_attachment.* | 첨부파일 리스트 |
        
        ## **attachments**
        
        | 필드명 | ERD 컬럼명/테이블 | 설명 |
        | --- | --- | --- |
        | attachment_id | post_attachment.attachment_id | 첨부파일 고유 ID |
        | file_url | post_attachment.file_url | 파일 URL/경로 |
        | file_type | post_attachment.file_type | 파일 타입(image, video 등) |
        | uploaded_at | post_attachment.uploaded_at | 업로드 일시 |
        
        ---
        
        ## **4️⃣ 에러 응답 예시**
        
        **→ 논제 상세조회랑 동일(논제→게시글)**
        
    - 등록
        
        **1️⃣ URL:** `POST /api/posts`
        
        ### **2️⃣ Request Body 예시**
        
        ```json
        {
          "topic_id": 41,                // 논제 ID (논제 카테고리만 필수)
          "position_id": 2,              // 입장 ID (논제 카테고리만 필수)
          "category": "논제",             // 카테고리(공지, 건의, 논제 등)
          "title": "여성 징병제 찬성 이유",
          "content": "여성도 군복무를 해야하는 이유는...",
          "attachments": [
            { "file_url": "https://url.com/image1.jpg", "file_type": "image" }
            // ... 여러 개 가능
          ]
        }
        ```
        
        - **user_id는 프론트에서 입력받지 않고, 로그인 세션/JWT 토큰 등에서 서버가 자동 추출해서 저장** (따로 입력 받지 않음!)
        
        ### **3️⃣ Response 예시**
        
        - **201 Created**
        
        ```json
        {
          "success": true,
          "message": "게시글이 성공적으로 등록되었습니다.",
          "post_id": 378,
          "user_id": 7,
          "category": "논제",
          "topic_id": 41,
          "position_id": 2,
          "title": "여성 징병제 찬성 이유",
          "content": "여성도 군복무를 해야하는 이유는...",
          "created_at": "2024-07-10T18:38:44Z",
          "post_view": 0,
          "influence_score": 0,
          "status": "active",
          "is_editable": true,
          "attachments": [
            {
              "attachment_id": 2022,
              "file_url": "https://url.com/image1.jpg",
              "file_type": "image",
              "uploaded_at": "2024-07-10T18:38:44Z"
            }
            // ... 등록된 모든 첨부파일 리스트
          ]
        }
        ```
        
        ### 4️⃣ **에러 응답 예시**
        
        - **카테고리 미입력/잘못 입력(HTTP 400)**
            
            ```json
            {
              "success": false,
              "code": "CATEGORY",
              "message": "카테고리를 선택해야 합니다."
            }
            ```
            
        - **카테고리가 '논제'일 경우, topic_id/position_id 필수(HTTP 400)**
            
            ```json
            {
              "success": false,
              "code": "TOPIC_INFO_REQUIRED",
              "message": "논제 게시글은 논제/입장을 선택해야 합니다."
            }
            ```
            
        - **존재하지 않는 논제/입장(HTTP 404)**
        - **권한 없음(HTTP 401)**
        - **서버 오류(HTTP 500)**
        
    - 수정
        
        ### 1️⃣ **URL**
        
        ```
        PATCH /api/posts/{post_id}
        ```
        
        ---
        
        ### 2️⃣ **Request Body 예시**
        
        ```json
        {
          "title": "수정된 게시글 제목",
          "content": "수정된 게시글 내용입니다.",
          "attachments": [
            { "file_url": "https://url.com/image2.jpg", "file_type": "image" }
          ],
        }
        ```
        
        - 수정 시, 카테고리는 변경 불가, 논제 게시글의 경우 논제 변경도 불가
        
        ---
        
        ### 4️⃣ **Response 예시**
        
        ```json
        {
          "success": true,
          "message": "게시글이 성공적으로 수정되었습니다.",
          "post_id": 378,
          "title": "수정된 게시글 제목",
          "content": "수정된 게시글 내용입니다.",
          "updated_at": "2024-07-10T19:03:21Z",
          "attachments": [
            {
              "attachment_id": 2024,
              "file_url": "https://url.com/image2.jpg",
              "file_type": "image",
              "uploaded_at": "2024-07-10T19:03:21Z"
            }
          ],
        }
        ```
        
        ---
        
        ### 4️⃣ **에러 응답 예시**
        
        - **권한 없음(HTTP 401)**
        - **존재하지 않는 게시글(HTTP 404)**
        - **잘못된 파라미터/필드(HTTP 400)**
        - **서버 오류(HTTP 500)**
        
    - 삭제
        
        ### 1️⃣ **URL**
        
        ```
        DELETE /api/posts/{post_id}
        ```
        
        ---
        
        ### 2️⃣ **Response 예시**
        
        ```json
        {
          "success": true,
          "message": "게시글이 성공적으로 삭제되었습니다.",
          "post_id": 378,
          "status": "deleted"
        }
        ```
        
        ---
        
        ### 3️⃣ **에러 응답 예시**
        
        - **권한 없음(HTTP 403)**
        - **존재하지 않는 게시글(HTTP 404)**
        - **서버 오류(HTTP 500)**
        
    - 상태 변경(관리자용)
        
        ### 1️⃣ **URL**
        
        ```
        PATCH /api/posts/{post_id}/status
        ```
        
        ---
        
        ### 2️⃣ **Request Body 예시**
        
        ```json
        {
          "status": "hidden",       
          "reason": "욕설/비방 등 운영정책 위반"   
        }
        ```
        
        ### status
        
        - `"active"` : 정상 운영/노출
        - `"hidden"` : 블라인드(운영진 숨김)
        - `"deleted"` : (실질적 삭제, 소프트 딜리트)
        - 서비스 정책에 따라 추가 가능
        
        ---
        
        ### 3️⃣ **Response 예시**
        
        ```json
        {
          "success": true,
          "message": "게시글 상태가 성공적으로 변경되었습니다.",
          "post_id": 378,
          "status": "hidden",
          "created_at": "2024-07-10T19:42:20Z"
        }
        ```
        
        ---
        
        ### 4️⃣ **에러 응답 예시**
        
        - **권한 없음(HTTP 403)**
        - **존재하지 않는 게시글(HTTP 404)**
        - **잘못된 값(HTTP 400)**
        - **이미 요청한 상태와 동일(HTTP 409)**
            
            ```json
            {
              "success": false,
              "code": "ALREADY_APPLIED",
              "message": "이미 적용된 상태입니다."
            }
            ```
            
        - **서버 오류(HTTP 500)**
        
    

---

- **댓글**
    
    
    - 등록
        
        ## 1️⃣ URL
        
        ```
        POST /api/posts/{post_id}/comments
        ```
        
        - 특정 게시글(post)에 댓글 또는 대댓글(무한 depth, 트리형 구조) 작성
        - **parent_comment_id**가 없으면 최상위 댓글, 있으면 해당 댓글의 자식(대댓글)
        
        ---
        
        ## 2️⃣ 요청 파라미터
        
        - **Path Parameter**
            - `post_id` (Long): 댓글을 작성할 게시글 ID
        - **Request Body (JSON)**
            
            
            | 필드명 | 타입 | 필수 | 설명 |
            | --- | --- | --- | --- |
            | content | String | O | 댓글/대댓글 내용 |
            | origin_position_id | Long | O | 작성 당시 입장 ID |
            | parent_comment_id | Long | X | 부모 댓글 ID (최상위 댓글이면 null 또는 생략) |
        
        ---
        
        ## 3️⃣ 요청 예시
        
        ### 1) 최상위 댓글 작성
        
        ```json
        POST /api/posts/123/comments
        
        {
          "content": "이 논제에 대해 의견 남깁니다.",
          "origin_position_id": 1,
          "parent_comment_id": null
        }
        ```
        
        ### 2) 대댓글 작성 (댓글 ID 45의 답글) →
        
        ```json
        POST /api/posts/123/comments
        
        {
          "content": "저도 동의합니다!",
          "origin_position_id": 2,
          "parent_comment_id": 45
        }
        ```
        
        → 답글버튼을 눌렀을 때, 해당 답글의 parent_comment_id만 백엔드에서 불러오면 나머지는 프론트 문제!
        
        ---
        
        ## 4️⃣ 응답
        
        - **201 Created**
        - **Response Body (JSON)**
            
            
            | 필드명 | 타입 | 설명 |
            | --- | --- | --- |
            | comment_id | Long | 생성된 댓글 ID |
            | post_id | Long | 게시글 ID |
            | parent_comment_id | Long | 부모 댓글 ID (최상위면 null) |
            | origin_position_id | Long | 작성 당시 입장 ID |
            | content | String | 댓글 내용 |
            | user | Object | 작성자 정보 (user_id, nickname 등) |
            | created_at | DateTime | 작성 시각 |
            | depth | Int | 댓글 트리 depth (최상위 1, 하위 n) |
        
        ### 예시
        
        ```json
        {
          "comment_id": 101,
          "post_id": 123,
          "parent_comment_id": 45,
          "origin_position_id": 2,
          "content": "저도 동의합니다!",
          "user": {
            "user_id": 7,
            "nickname": "개발자킹"
          },
          "created_at": "2025-07-14T20:23:00Z",
          "depth": 2 // 
        }
        ```
        
        ---
        
        ## 5️⃣ 에러 응답 예시
        
        - **권한 없음(HTTP 403)**
        - **존재하지 않는 게시글/댓글(HTTP 404)**
        - **잘못된 값(HTTP 400)**
        - **서버 오류(HTTP 500)**
        
    - 전체 조회(트리형)
        
        ## 1️⃣ URL
        
        ```
        GET /api/posts/{post_id}/comments
        ```
        
        ---
        
        ## 2️⃣ 요청 파라미터
        
        ### Path Parameter
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | post_id | Long | O | 게시글 ID |
        
        ### Query Parameter (옵션)
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | sort | String | X | 정렬기준 (`latest`, `best`, etc.) |
        | page | Int | X | 페이지 번호(페이징 사용시) |
        | size | Int | X | 페이지 크기(페이징 사용시) |
        
        ---
        
        ## 3️⃣ 응답
        
        ### **응답 예시**
        
        ```json
        {
        	"best_comments": [
            {
              "comment_id": 31,
              "parent_comment_id": 8,
              "content": "이 대댓글이 베스트!",
              "origin_position_code": "A",
              "user": { "user_id": 7, "nickname": "하이라이트" },
              "parent_user": { "user_id": 3, "nickname": "루트댓글러" },
              "like_count": 46,
              "created_at": "2025-07-15T14:01:00Z",
              "depth": 3
              // children: [] (베스트 영역에선 하위 댓글은 굳이 안 포함)
            }
            // .. 베스트 댓글 : 3~5개, 추천순
          ],
        
        	"comments" : [
        		{
        	    "comment_id": 1,
        	    "post_id": 123,
        	    "parent_comment_id": null,
        	    "content": "저는 찬성합니다.",
        	    "origin_position_id": 1,
        	    "origin_position_code": "A",
        	    "user": { "user_id": 9, "nickname": "Alice" },
        	    "parent_user": null,
        	    "depth": 1,
        	    "like_count": 5,
        	    "created_at": "2025-07-14T22:10:00Z",
        	    "children": [
        	      {
        	        "comment_id": 2,
        	        "post_id": 123,
        	        "parent_comment_id": 1,
        	        "content": "어떤 점이 찬성인가요?",
        	        "origin_position_id": 2,
        	        "origin_position_code": "B",
        	        "user": { "user_id": 10, "nickname": "Bob" },
        	        "parent_user": { "user_id": 9, "nickname": "Alice" },
        	        "depth": 2,
        	        "like_count": 2,
        	        "created_at": "2025-07-14T22:12:00Z",
        	        "children": [
        	          {
        	            "comment_id": 3,
        	            "post_id": 123,
        	            "parent_comment_id": 2,
        	            "content": "저도 반대 의견이에요.",
        	            "origin_position_id": 2,
        	            "origin_position_code": "B",
        	            "user": { "user_id": 11, "nickname": "Carol" },
        	            "parent_user": { "user_id": 10, "nickname": "Bob" },
        	            "depth": 3,
        	            "like_count": 0,
        	            "created_at": "2025-07-14T22:14:00Z",
        	            "children": []
        	          }
        	        ]
        	      }
        	    ]
        	  },
        	  {
        	    "comment_id": 4,
        	    "post_id": 123,
        	    "parent_comment_id": null,
        	    "content": "저는 중립이에요.",
        	    "origin_position_id": 3,
        	    "origin_position_code": "C",
        	    "user": { "user_id": 12, "nickname": "Dave" },
        	    "parent_user": null,
        	    "depth": 1,
        	    "like_count": 0,
        	    "created_at": "2025-07-14T22:15:00Z",
        	    "children": []
        	  }
        	  // ..	  
        	],
        	"page": 1,
          "size": 20,
          "total_pages": 5,
          "total_elements": 95
        }
        ```
        
        ---
        
        ### **표(요약)**
        
        | 필드명 | ERD 컬럼/테이블 | 설명 |
        | --- | --- | --- |
        | comment_id | comment.comment_id | 댓글 고유 ID |
        | post_id | comment.post_id | 게시글 고유 ID |
        | parent_comment_id | comment.parent_comment_id | 부모 댓글 ID (최상위 댓글이면 null) |
        | content | comment.content | 댓글/대댓글 내용 |
        | origin_position_id | comment.origin_position_id | 댓글 작성 당시 입장 ID |
        | origin_position_code | topic_position.position_code | 댓글 작성 당시 입장 코드 ("A", "B" 등) |
        | user | comment.user_id (참조) | 댓글 작성자 정보(user_id, nickname) |
        | user.user_id | comment.user_id → user.user_id | 댓글 작성자 ID |
        | user.nickname | user_profile.nickname | 댓글 작성자 닉네임 |
        | parent_user | comment.parent_comment_id → user.user_id | 부모 댓글 작성자 정보(user_id, nickname) |
        | parent_user.user_id | 부모 댓글의 comment.user_id | 부모 댓글 작성자 ID |
        | parent_user.nickname | user_profile.nickname | 부모 댓글 작성자 닉네임 |
        | depth | comment.depth | 트리 깊이(최상위=1, 자식=2, …) |
        | created_at | comment.created_at | 댓글 등록일시 |
        | children | (comment 재귀) | 하위 댓글 리스트(트리 구조) |
        
        ---
        
        ## 4️⃣ 에러 응답 예시
        
        - **존재하지 않는 게시글(HTTP 404)**
        - **잘못된 값(HTTP 400)**
        - **서버 오류(HTTP 500)**
        
    - 단일 조회(댓글로 이동 기능 → anchor)
        
        
        ## 1️⃣ **URL**
        
        ```
        GET /api/comments/{comment_id}/page
        ```
        
        ---
        
        ## 2️⃣ **응답**
        
        - **200**
        
        | 필드명 | ERD 컬럼/테이블 | 설명 |
        | --- | --- | --- |
        | comment_id | comment.comment_id | 요청한 댓글 ID |
        | page | (계산값) | 해당 댓글이 속한 페이지 번호(1-base) |
        
        ---
        
        ### **응답 예시**
        
        ```json
        {
          "comment_id": 33,
          "page": 3,
        }
        ```
        
        ---
        
        ## 3️⃣ 에러 응답 예시
        
        - **존재하지 않는 댓글(HTTP 404)**
        - **잘못된 값(HTTP 400)**
        - **서버 오류(HTTP 500)**
        
        ---
        
        ## **로직 설명/팁**
        
        - **서버에서 페이지번호 계산 방법(예시):**
            1. 정렬 기준에 따라 전체 댓글 쿼리에서 comment_id가 몇 번째인지(ROW_NUMBER)
            2. 페이지 크기(page_size)로 나눠서 → 페이지 번호 반환
                
                (예: 33번째 댓글, page_size=20이면 2페이지(21~40), index_in_page=13)
                
        - **프론트에서**
            - `/post/123?page=3#comment-33` 형태로 이동/스크롤
        
    - 수정
        
        ## 1️⃣ URL
        
        ```
        PATCH /api/comments/{comment_id}
        ```
        
        ---
        
        ## 2️⃣ 요청 파라미터
        
        ### Path Variable
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | comment_id | Long | O | 수정할 댓글 ID |
        
        ### Request Body (JSON)
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | content | String | O | 수정할 댓글/대댓글 내용 |
        
        ---
        
        ### **예시**
        
        ```json
        PATCH /api/comments/33
        
        {
          "content": "수정된 댓글 내용입니다."
        }
        ```
        
        ---
        
        ## 3️⃣ 응답
        
        - **200 OK**
        
        ### **응답 필드 예시**
        
        ```json
        {
        	"success": true,
          "comment_id": 33,
          "content": "수정된 댓글 내용입니다.",
        }
        ```
        
        > 일단 최소응답으로 해보기
        > 
        
        ---
        
        ## 4️⃣ 에러 응답 예시
        
        - **존재하지 않는 댓글(HTTP 404)**
        - **잘못된 값&빈 값(HTTP 400)**
        - **권한 없음(HTTP 403)**
        - **서버 오류(HTTP 500)**
        
        ---
        
        ## 특이사항
        
        - 상호작용(추천,대댓글) 발생 시 수정 불가
        - **작성자/관리자만 수정 가능**(관리자 정책 필요시)
        - XSS, 욕설, 중복 필터 등 서버단에서 재검증 필요
        
    - 삭제
        
        ## 1️⃣ URL
        
        ```
        DELETE /api/comments/{comment_id}
        ```
        
        ---
        
        ## 2️⃣ 요청 파라미터
        
        ### Path Variable
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | comment_id | Long | O | 삭제할 댓글 ID |
        
        ```
        DELETE /api/comments/33
        ```
        
        ---
        
        ## 3️⃣ 응답
        
        ```json
        {
          "success": true,
          "comment_id": 33,
          "status": "delete"
        }
        ```
        
        ---
        
        ## 4️⃣ 에러 응답 예시
        
        - **존재하지 않는 댓글(HTTP 404)**
        - **권한 없음(HTTP 403)**
        - **서버 오류(HTTP 500)**
        
        ---
        
        ## 특이사항
        
        - **대댓글이 없는 댓글은 단순 status=delete → UI노출 X**
        - **대댓글이 있는 댓글은 "삭제된 댓글입니다.”로 내용변경 + 대댓글 유지**
        - **삭제된 댓글이면 추천/비추천, 대댓글(답글) “기능” 정지**
            
            <aside>
            💡
            
            프론트 : status='deleted'면 추천/대댓글 버튼 숨기기 or 비활성화
            
            백엔드 : status 체크해서, 등록/추천 시 status='deleted'면 403 등으로 거절
            
            </aside>
            
        
    - 상태 변경(관리자용)
        
        ## 1️⃣ URL
        
        ```
        PATCH /api/comments/{comment_id}/status
        ```
        
        ---
        
        ## 2️⃣ 요청 파라미터
        
        ### Path Variable
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | comment_id | Long | O | 상태를 변경할 댓글 ID |
        
        ### Request Body (JSON)
        
        | 이름 | 타입 | 필수 | 설명 |
        | --- | --- | --- | --- |
        | status | String | O | 변경할 상태값(예: 'active', 'deleted', 'hidden', ...) |
        | reason | String | X | 변경 사유(선택, 로그 기록용) |
        
        ---
        
        ### **예시**
        
        ```json
        PATCH /api/comments/33/status
        
        {
          "status": "delete",
          "reason": "욕설 및 비방"
        }
        ```
        
        ### status
        
        - `"active"` : 정상 운영/노출
        - `"hidden"` : 블라인드(운영진 숨김)
        - `"deleted"` : (실질적 삭제, 소프트 딜리트)
        - 서비스 정책에 따라 추가 가능
        
        ---
        
        ## 3️⃣ 응답
        
        ```json
        {
          "success": true,
          "comment_id": 33,
          "status": "delete",
          "updated_at": "2025-07-16T10:30:00Z"
        }
        ```
        
        ---
        
        ## 4️⃣ 에러 예시
        
        - **존재하지 않는 댓글(HTTP 404)**
        - **잘못된 값(HTTP 400)**
        - **권한 없음(HTTP 403)**
        - **서버 오류(HTTP 500)**
        
        ---
        
        ## 특이사항
        
        - admin_log 테이블에 저장
        
    

---

- **추천**
    
    ## 1️⃣ URL
    
    | 대상 | 메서드 | URL | 설명 |
    | --- | --- | --- | --- |
    | **논제** | POST | `/api/topics/{topic_id}/like` | 논제 추천(좋아요) |
    |  | DELETE | `/api/topics/{topic_id}/like` | 논제 추천 취소 |
    |  | POST | `/api/topics/{topic_id}/dislike` | 논제 비추천(싫어요) |
    |  | DELETE | `/api/topics/{topic_id}/dislike` | 논제 비추천 취소 |
    | **게시글** | POST | `/api/posts/{post_id}/like` | 게시글 추천 |
    |  | DELETE | `/api/posts/{post_id}/like` | 게시글 추천 취소 |
    |  | POST | `/api/posts/{post_id}/dislike` | 게시글 비추천 |
    |  | DELETE | `/api/posts/{post_id}/dislike` | 게시글 비추천 취소 |
    | **댓글** | POST | `/api/comments/{comment_id}/like` | 댓글 추천 |
    |  | DELETE | `/api/comments/{comment_id}/like` | 댓글 추천 취소 |
    |  | POST | `/api/comments/{comment_id}/dislike` | 댓글 비추천 |
    |  | DELETE | `/api/comments/{comment_id}/dislike` | 댓글 비추천 취소 |
    
    ---
    
    ## 2️⃣ 응답
    
    ```json
    {
      "success": true,
      "like_count": 27, // 좋아요의 경우
      // "dislike_count": 2 // 싫어요의 경우
    }
    ```
    
    ---
    
    ## 3️⃣ 에러 예시
    
    - **존재하지 않음(HTTP 404)**
    - **로그인 필요(HTTP 401)**
    - **서버 오류(HTTP 500)**
    
    ---
    
    ## 특이사항
    
    - 동시에 추천+비추천 불가
    - 본인이 누른 추천/비추천 버튼에 눌림 표시 + 다시 누르면 취소
    

---

- **신고**
    
    ## 1️⃣ URL
    
    | 대상 | 메서드 | URL | 설명 |
    | --- | --- | --- | --- |
    | **논제** | POST | `/api/topics/{topic_id}/report` | 논제 신고 등록 |
    | **게시글** | POST | `/api/posts/{post_id}/report` | 게시글 신고 등록 |
    | **댓글** | POST | `/api/comments/{comment_id}/report` | 댓글 신고 등록 |
    | **유저** | POST | `/api/users/{user_id}/report` | 유저 신고 등록 |
    
    ---
    
    ## 2️⃣ 요청 바디
    
    ```json
    {
      "reason_code": "욕설",       // 사유 코드(필수, ENUM)
      "reason_text": "논리적으로 설명하는게 아니라 그냥 비하만 해요"    // 상세 설명(선택)
    }
    ```
    
    - `reason_code`: 프론트에서 신고 사유 선택(ENUM, ex: 욕설, 도배, 기타 등)
    - `reason_text`: 사용자가 직접 입력하는 상세 설명(선택)
    
    ---
    
    ## 3️⃣ 응답
    
    ```json
    {
      "success": true,
      "report_id": 104   // 생성된 신고의 고유 ID
    }
    ```
    
    ---
    
    ## 4️⃣ 에러 예시
    
    - **존재하지 않음(HTTP 404)**
    - **로그인 필요(HTTP 401)**
    - **중복 신고(HTTP 409)**
    - **서버 오류(HTTP 500)**
    
    ---
    
    ## 특이사항
    
    - **동일 유저는 같은 대상에 한 번만 신고 가능(중복 신고 방지)**
    - **reason_code**(신고 사유)는 ENUM으로 통합 관리 (대상별로 노출 사유만 다르게)
    - **운영자/관리자만 신고 현황/처리 가능**
    - 신고 등록 시 실시간 알림/관리자 검토/자동 블라인드 연계 가능
    - 신고 건수가 일정 수 넘으면 일단 숨김 처리 후 관리자 검토
    

---

- **관리자**
    - 신고 목록 조회
        
        ## 1️⃣ **URL**
        
        ```
        GET /api/reports
        ```
        
        ---
        
        ## 2️⃣ **쿼리파라미터 (request 예시)**
        
        - **신고대상별:**
            - target_type : `user`, `topic`, `post`, `comment`
        - **정렬:**
            - sort: `latest`(최신순), `oldest`(오래된순), `count`(신고건순)
        - **페이징:**
            - page, size
        
        ### **예시**
        
        ```
        /api/admin/reports?target_type=post&sort=latest&page=1&size=20
        ```
        
        - target_type: 신고대상 필터
        - sort: 정렬(최신순/오래된순/신고건수순)
        - page/size: 페이징
        
        ---
        
        ## 3️⃣ **response 예시**
        
        ```json
        {
          "content": [
            {
              "target_id": 101,
              "target_type": "comment",
              "report_count": 4,
              "last_reported_at": "2025-07-18T14:23:12Z" // 가장 최근 신고일시
            },
            {
              "target_id": 77,
              "target_type": "post",
              "report_count": 2,
              "last_reported_at": "2025-07-18T10:17:34Z"
            }
            //...
          ],
          "page": 1,
          "size": 20,
          "total_pages": 3,
          "total_elements": 55
        }
        ```
        
        ---
        
        ## 4️⃣ **응답 필드 설명 (ERD 기준)**
        
        | 필드명 | ERD 컬럼/테이블 | 설명 |
        | --- | --- | --- |
        | target_id | report.target_id | 신고대상 고유 ID |
        | target_type | report.target_type | 신고대상 종류('user','topic','post','comment') |
        | report_count | (report 집계) | 해당 대상의 총 신고건수 |
        | last_reported_at | report.reported_at | 최근 신고 일시(가장 마지막 신고의 reported_at) |
        | page | (페이징) | 현재 페이지 번호 |
        | size | (페이징) | 페이지 당 개수 |
        | total_pages | (페이징) | 전체 페이지 수 |
        | total_elements | (페이징) | 전체 신고대상 개수 |
        
        ---
        
        ## 5️⃣ **에러 응답 예시**
        
        - **잘못된 파라미터 400**
        - **권한 없음 403**
        - **서버 에러 500**
        
        ---
        
        ## 6️⃣ **특이사항**
        
        - target_type, sort 모두 선택(필터/정렬) 가능. 둘 다 미입력 시 전체+최신순 기본.
        - report_count는 **동일 대상(target_id, target_type) 기준** 모든 신고건의 합계
            
            
    - 신고 목록 상세 조회
        
        ## 1️⃣ **URL**
        
        ```
        GET /api/reports/{target_type}/{target_id}
        ```
        
        ---
        
        ## 2️⃣ **쿼리파라미터 (request 예시)**
        
        - target_type : `user`, `topic`, `post`, `comment`
        - target_id : 고유 PK
        - 페이징 **:** page, size
        
        ### **예시**
        
        ```
        /api/admin/reports/post/412?page=1&size=5
        ```
        
        ---
        
        ## 3️⃣ **response 예시**
        
        ```json
        {
        	"reports": {
            "total": 12,
            "page": 0,
            "size": 5,
            "items": [
              {
                "report_id": 551,
                "reason_code": "욕설",
                "reason_text": "모욕적 표현",
                "reported_at": "2025-08-10T20:12:45+09:00"
              },
              {
                "report_id": 544,
                "reason_code": "조작",
                "reason_text": "사실과 다른 정보",
                "reported_at": "2025-08-10T18:03:11+09:00"
              }
              //...
            ]
          },
          "target": {
            "type": "post",
            "id": 8342, 
            "current_status": "HIDDEN",                  // 대상 현재 상태
            "admin_detail_api": "/api/admin/posts/8342", // 원본 상세(관리자용)
          },
         
        }
        ```
        
        ---
        
        ## 4️⃣ **응답 필드 설명 (ERD 기준)**
        
        | 필드명 | ERD 컬럼/테이블 | 설명 |
        | --- | --- | --- |
        | reports.total | (report 집계) | 해당 대상에 접수된 전체 신고 건수 |
        | reports.page | (페이징) | 현재 페이지 번호(0부터 시작) |
        | reports.size | (페이징) | 한 페이지에 포함된 신고 건수 |
        | reports.items[].report_id | report.report_id | 신고 고유 ID |
        | reports.items[].reason_code | report.reason_code | 신고 사유 |
        | reports.items[].reason_text | report.reason_text | 신고 상세 사유 |
        | reports.items[].reported_at | report.reported_at | 신고가 접수된 일시 |
        | target.type | report.target_type | 신고 대상 타입 (`user` / `topic` / `post` / `comment`) |
        | target.id | report.target_id | 신고 대상의 PK |
        | target.current_status | (대상 테이블 status 컬럼) | 현재 대상 상태 (`ACTIVE`, `HIDDEN`, `DELETED` 등) |
        | target.admin_detail_api | (URL 매핑) | 해당 대상의 관리자 상세 조회 API 경로 |
        
        ---
        
        ## 5️⃣ **에러 응답 예시**
        
        - **400 Bad Request** (잘못된 파라미터)
        - **403 Forbidden** (관리자 권한 없음)
        - **404 Not Found** (대상 없음)
        - **500 Internal Server Error** (서버 오류)
        
        ---
        
        ## 6️⃣ **특이사항**
        
        - 모든 응답은 **권한 검증 후 반환**하며, 비관리자 접근 시 403.
        
    - 관리자용 상세조회
        - `/api/**admin**/~`  : admin 추가
        - 기존 에러응답에 404 제외하고 403추가
        
        → 나머지 기존과 동일
        
    - 상태변경(각 파트에서 해결)
    

---

- **통계 및 랭킹(일단 나중에~)**

---

- 정해지지 않은 부분
    1. 정렬 어떻게 할거임? 조회/추천/댓글/참여 .. 등 최신순,오래된순,높은순,낮은순 다구현할거임?