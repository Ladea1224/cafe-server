# Cafe Diary API

Java 27, Spring Boot, `JdbcTemplate`, MySQL, and Amazon S3로 만든 카페 방문 기록장 API입니다. JPA, Hibernate, MyBatis는 사용하지 않습니다.

## 로컬 실행

1. Java 27과 MySQL을 준비합니다.
2. MySQL에서 데이터베이스와 접속 계정을 만듭니다. 앱 시작 시 `src/main/resources/schema.sql`이 같은 스키마를 생성합니다.

```sql
CREATE DATABASE cafe_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'cafe_diary'@'localhost' IDENTIFIED BY '원하는-비밀번호';
GRANT ALL PRIVILEGES ON cafe_diary.* TO 'cafe_diary'@'localhost';
```

3. `.env.example`을 복사해 `.env`를 만들고, 로컬 DB의 사용자명과 비밀번호를 입력합니다. Spring Boot가 `.env`를 설정 파일로 읽습니다. `.env`는 `.gitignore`에 포함되어 Git에 올라가지 않습니다.

```text
DB_URL=jdbc:mysql://localhost:3306/cafe_diary?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=로컬-DB-비밀번호
```

실행:

```powershell
Copy-Item .env.example .env
# .env 파일을 편집해 DB_USERNAME과 DB_PASSWORD를 입력합니다.
.\gradlew.bat bootRun
```

DB 접속은 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`로 바꿀 수 있습니다. RDS로 전환할 때는 `DB_URL`을 RDS endpoint의 JDBC URL로 지정하면 됩니다. 예: `jdbc:mysql://db.example.ap-northeast-2.rds.amazonaws.com:3306/cafe_diary?serverTimezone=Asia/Seoul&characterEncoding=UTF-8`.

## S3 설정

- `AWS_REGION`, `S3_BUCKET`을 설정합니다. `S3_PUBLIC_BASE_URL`은 선택 항목이며 CloudFront 또는 사용자 지정 도메인을 쓸 때 `https://images.example.com`처럼 지정합니다.
- 로컬에서는 `.env`의 `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` 또는 AWS CLI 프로파일을 사용할 수 있습니다. `.env` 키가 비어 있으면 AWS SDK 기본 자격 증명 체인을 사용하며, 배포 환경에서는 인스턴스/태스크 IAM 역할을 권장합니다. 자격 증명을 소스나 커밋된 설정 파일에 넣지 마세요.
- 실행 주체에는 버킷의 `cafes/*` 경로에 대한 `s3:PutObject`, `s3:DeleteObject` 권한이 필요합니다.
- 반환되는 URL은 브라우저에서 직접 읽을 수 있어야 합니다. S3 버킷 정책 또는 CloudFront를 통해 사진 읽기 권한을 설정하세요. S3 공개 URL을 쓸 경우 버킷의 퍼블릭 액세스 차단 설정도 공개 읽기가 가능하도록 조정해야 합니다. 앱은 객체 ACL을 설정하지 않습니다.
- 업로드는 JPEG, PNG, GIF, WebP를 받습니다. 기본 파일 제한은 사진 10MB이며 `PHOTO_MAX_FILE_SIZE`, `PHOTO_MAX_REQUEST_SIZE`로 조정할 수 있습니다.

## CORS

기본 허용 origin은 `http://localhost:3000`과 `http://localhost:5173`입니다. 프론트엔드 주소에 맞춰 `CORS_ALLOWED_ORIGINS`에 쉼표로 구분한 origin을 지정하세요.

## API

모든 성공 응답의 카페 객체는 아래 필드를 사용합니다. `visitedAt`은 `YYYY-MM-DD` 문자열이며, 사진이 없으면 `photoUrl`은 `null`입니다.

```json
{
  "id": 1,
  "name": "모퉁이 커피",
  "address": "서울시 중구 ...",
  "rating": 4.5,
  "visitedAt": "2026-11-06",
  "memo": "라떼가 좋았어요",
  "photoUrl": "https://your-public-photo-bucket.s3.ap-northeast-2.amazonaws.com/cafes/...jpg"
}
```

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/cafes` | 방문 기록 목록 (방문일 최신순) |
| GET | `/api/cafes/{id}` | 방문 기록 상세 |
| POST | `/api/cafes` | 방문 기록 등록 (성공 시 201) |
| PUT | `/api/cafes/{id}` | 방문 기록 수정 |
| DELETE | `/api/cafes/{id}` | 방문 기록 삭제 (성공 시 204) |

등록과 수정은 `multipart/form-data`입니다. `data` 파트는 `application/json`, `photo` 파트는 선택 이미지 파일입니다. 사진 없이 수정하면 기존 `photoUrl`이 유지됩니다. 사진을 새로 올려 교체하면 이전 S3 객체를 삭제하고, 카페 삭제 시 연결된 S3 객체도 삭제합니다.

`cafe.json` 예시:

```json
{
  "name": "모퉁이 커피",
  "address": "서울시 중구 ...",
  "rating": 4.5,
  "visitedAt": "2026-11-06",
  "memo": "라떼가 좋았어요"
}
```

PowerShell에서 등록:

```powershell
curl.exe -X POST http://localhost:8080/api/cafes `
  -F "data=@cafe.json;type=application/json" `
  -F "photo=@coffee.jpg;type=image/jpeg"
```

사진 없이 등록하거나 수정하려면 `photo` 파트를 빼면 됩니다. 예를 들어 수정은 아래처럼 호출합니다.

```powershell
curl.exe -X PUT http://localhost:8080/api/cafes/1 `
  -F "data=@cafe.json;type=application/json"
```

목록/상세 조회는 `curl http://localhost:8080/api/cafes` 및 `curl http://localhost:8080/api/cafes/1`로 호출하고, 삭제는 `curl.exe -X DELETE http://localhost:8080/api/cafes/1`로 호출합니다.
