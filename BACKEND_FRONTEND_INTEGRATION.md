# Backend-Frontend Integration Setup

## Overview
This document describes the working integration between the Nuxt 3 frontend and Spring Boot backend with Firebase Authentication.

## Architecture

### Frontend (Nuxt 3)
- **Location**: `/Secondbrain`
- **Port**: `http://localhost:3000`
- **Framework**: Nuxt 4.2.1 with Vue 3.5.24

### Backend (Spring Boot)
- **Location**: `/backend`
- **Port**: `http://localhost:8080`
- **Framework**: Spring Boot 4.0.0 with Java 17

## Authentication Flow

1. **User logs in** on the frontend using Firebase Authentication
2. **Frontend gets ID token** from Firebase (client-side)
3. **Frontend sends requests** to backend with `Authorization: Bearer <token>` header
4. **Backend verifies token** using Firebase Admin SDK
5. **Backend extracts UID** from verified token
6. **Backend processes request** with authenticated user context

## Key Components

### Frontend

#### `composables/useBackend.js`
```javascript
- getAuthToken() - Gets Firebase ID token for current user
- apiCall(endpoint, options) - Makes authenticated API calls to backend
- Automatically handles both JSON and text responses
- Automatically adds Authorization header
```

#### `firebase/config.js`
- Initializes Firebase client SDK
- Exports projectAuth and projectFirestore

#### `app/pages/test-backend.vue`
- Test page for backend integration
- URL: http://localhost:3000/test-backend
- Tests `/api/hello` endpoint

### Backend

#### `FirebaseConfig.java`
```java
- Initializes Firebase Admin SDK on startup
- Uses firebase-key.json from resources folder
- @PostConstruct ensures initialization before any requests
```

#### `FirebaseTokenFilter.java`
```java
- Intercepts all /api/* requests
- Handles CORS headers (allows http://localhost:3000)
- Verifies Firebase ID tokens
- Extracts UID and stores in request attributes
- Returns 401 if token is missing or invalid
```

#### `TestController.java`
```java
- GET /api/hello - Returns "Hello user: <uid>"
- GET /api/test - Returns JSON with message, uid, and timestamp
- Both endpoints require authentication
```

#### `WebConfig.java`
- Configures CORS to allow requests from Nuxt frontend
- Allows credentials and all standard HTTP methods

#### `SecurityConfig.java`
- Registers FirebaseTokenFilter for all /api/* routes

## Dependencies

### Backend (pom.xml)
```xml
- spring-boot-starter-webmvc
- firebase-admin (9.2.0)
- spring-boot-devtools
```

### Frontend (package.json)
```json
- firebase (12.6.0)
- vuefire (3.2.2)
```

## File Structure

```
/backend
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java
│   ├── FirebaseConfig.java          ← Firebase Admin initialization
│   ├── FirebaseTokenFilter.java     ← Token verification + CORS
│   ├── SecurityConfig.java          ← Filter registration
│   ├── TestController.java          ← API endpoints
│   └── WebConfig.java               ← CORS configuration
└── src/main/resources/
    └── firebase-key.json            ← Firebase service account key

/Secondbrain
├── firebase/
│   └── config.js                    ← Firebase client initialization
├── composables/
│   ├── useBackend.js                ← API call helper
│   ├── useLogin.js
│   └── useSignup.js
└── app/pages/
    ├── index.vue                    ← Auth playground
    └── test-backend.vue             ← Backend test page
```

## API Endpoints

### GET /api/hello
**Description**: Simple greeting endpoint  
**Authentication**: Required  
**Response**: Plain text  
**Example**:
```
Request:
GET http://localhost:8080/api/hello
Authorization: Bearer <firebase-token>

Response:
Hello user: abc123xyz789...
```

### GET /api/test
**Description**: Test endpoint with JSON response  
**Authentication**: Required  
**Response**: JSON  
**Example**:
```
Request:
GET http://localhost:8080/api/test
Authorization: Bearer <firebase-token>

Response:
{
  "message": "Backend connection successful!",
  "uid": "abc123xyz789...",
  "timestamp": 1700000000000
}
```

## How to Use

### Making API Calls from Frontend

```javascript
// In any Vue component
const { apiCall } = useBackend()

// Make authenticated request
const response = await apiCall('/api/hello')
console.log(response) // "Hello user: <uid>"

// With options
const data = await apiCall('/api/someEndpoint', {
  method: 'POST',
  body: JSON.stringify({ key: 'value' })
})
```

### Creating New Protected Endpoints

1. **Add endpoint in controller**:
```java
@GetMapping("/api/yourEndpoint")
public String yourEndpoint(HttpServletRequest request) {
    String uid = (String) request.getAttribute("uid");
    // Your logic here
    return "Response";
}
```

2. **Call from frontend**:
```javascript
const result = await apiCall('/api/yourEndpoint')
```

That's it! The filter automatically handles authentication.

## Testing

### Test the Integration
1. Start backend: `cd backend && mvn spring-boot:run`
2. Start frontend: `cd Secondbrain && npm run dev`
3. Navigate to: http://localhost:3000/test-backend
4. Login if needed
5. Click "Test /api/hello"
6. Should see: `✓ Success! Response: Hello user: <your-uid>`

### Backend Console Output
When a request comes in, you'll see:
```
🔍 Incoming request: GET /api/hello
🔍 Authorization header: Present
🔍 Token (first 20 chars): eyJhbGciOiJSUzI1NiIs...
✅ Token verified! UID: abc123xyz789...
```

## Important Notes

### CORS Configuration
- Backend allows requests from `http://localhost:3000` only
- Modify `FirebaseTokenFilter.java` and `WebConfig.java` for production URLs

### Firebase Service Account Key
- Located at: `backend/src/main/resources/firebase-key.json`
- **NEVER commit this file to Git** (add to .gitignore)
- Download from Firebase Console → Project Settings → Service Accounts

### Token Expiration
- Firebase ID tokens expire after 1 hour
- Frontend automatically refreshes tokens when calling `getAuthToken()`
- If user sees 401 errors, they need to re-login

### Production Deployment
- Update CORS origins to your production domains
- Use environment variables for sensitive configuration
- Consider using a reverse proxy (nginx) to avoid CORS entirely

## Troubleshooting

### 401 Unauthorized
- User not logged in → Check `projectAuth.currentUser`
- Token expired → User needs to re-login
- Firebase Admin not initialized → Check backend logs for "🔥 Firebase Admin Initialized"

### CORS Errors
- Backend not allowing origin → Check `FirebaseTokenFilter.java` CORS headers
- Preflight failing → OPTIONS requests must return 200 (already handled)

### "FirebaseApp doesn't exist" Error
- Firebase Admin SDK not initialized → Check `@PostConstruct` annotation uses `jakarta.annotation` not `javax.annotation`
- firebase-key.json missing → Check `src/main/resources/firebase-key.json` exists

## Next Steps

To add more features:
1. Create new endpoints in `TestController.java` or new controller classes
2. Use `request.getAttribute("uid")` to access authenticated user's UID
3. Call endpoints from frontend using `apiCall('/api/your-endpoint')`
4. All `/api/*` routes are automatically protected by the filter
