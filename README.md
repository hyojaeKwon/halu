# 뉴스레터 서비스 (Newsletter Service)

주기적으로 발행되는 뉴스레터를 관리하고 조회하는 Spring Boot 기반의 백엔드 애플리케이션입니다.

## ✨ 주요 기능

- 키워드를 사용한 뉴스레터 검색
- 날짜 또는 조회수를 기준으로 한 정렬
- 뉴스레터 상세 내용 조회
- Markdown 형식의 콘텐츠를 HTML로 렌더링하여 제공

## 🛠️ 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle
- **Database**: AWS DynamoDB
- **Template Engine**: Thymeleaf

## ⚙️ 시작하기

### 사전 준비

- JDK 17 이상
- Gradle
- AWS 계정 및 DynamoDB 테이블

### 설치 및 실행

1.  **레포지토리 클론**

2.  **환경 변수 설정**

    이 애플리케이션은 AWS DynamoDB와 연동됩니다. 실행하기 전에 아래 환경 변수를 설정해야 합니다.

    ```bash
    export AWS_ACCESS_KEY=<YOUR_AWS_ACCESS_KEY>
    export AWS_SECRET_KEY=<YOUR_AWS_SECRET_KEY>
    export AWS_DYNAMODB_ENDPOINT=<YOUR_DYNAMODB_ENDPOINT> # 예: https://dynamodb.ap-northeast-2.amazonaws.com
    ```
    
    `application.yaml` 파일의 `aws.dynamodb.table` 속성에 실제 사용하는 DynamoDB 테이블 이름을 설정해야 합니다.

3.  **애플리케이션 실행**

    아래 명령어를 사용하여 애플리케이션을 빌드하고 실행합니다.

    ```bash
    # 실행 권한 부여 (최초 1회)
    chmod +x ./gradlew

    # 애플리케이션 실행
    ./gradlew bootRun
    ```

    실행 후 `http://localhost:8080`으로 접속하여 확인할 수 있습니다.

## 🌐 API 및 화면 안내

이 애플리케이션은 REST API가 아닌, 서버 사이드 렌더링(SSR)을 통해 HTML 페이지를 제공합니다.

- **`GET /`**: 메인 페이지
  - 뉴스레터 전체 목록을 보여주며, 검색 및 정렬 기능을 제공합니다.
  - **Query Parameters**:
    - `query` (optional): 검색할 키워드
    - `sort` (optional): 정렬 기준 (`hits` 또는 `date`)

- **`GET /news`**: 메인 페이지와 동일한 기능을 제공합니다.

- **`GET /news/{id}`**: 뉴스레터 상세 페이지
  - 특정 ID를 가진 뉴스레터의 상세 내용을 보여줍니다.
