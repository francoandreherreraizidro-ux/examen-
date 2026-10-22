# API Contract: [Service or Domain Name]

---

## 📋 Metadata

| Attribute | Details |
|---|---|
| **Contract ID** | API-XXX |
| **Domain** | [e.g., Authentication / Billing / Users] |
| **Status** | `DRAFT` <!-- Options: DRAFT | REVIEW | APPROVED | DEPRECATED --> |
| **Version** | v1 |
| **Base URL** | `/api/v1` |
| **Parent Spec** | [Link to SPEC-XXX] |

---

## 1. Authentication & Common Headers

### Request Headers
| Header | Type | Required | Description |
|---|---|---|---|
| `Authorization` | String | Yes | Bearer `<token>` |
| `Content-Type` | String | Yes | `application/json` |
| `X-Request-ID` | UUID | No | Client trace identifier |

### Common Error Responses
| HTTP Code | Error Body Structure | Reason |
|---|---|---|
| `400 Bad Request` | `{"error": "INVALID_PAYLOAD", "details": [...]}` | Validation failure |
| `401 Unauthorized` | `{"error": "UNAUTHORIZED", "message": "..."}` | Missing or invalid auth |
| `403 Forbidden` | `{"error": "FORBIDDEN", "message": "..."}` | Insufficient permissions |
| `404 Not Found` | `{"error": "NOT_FOUND", "message": "..."}` | Resource does not exist |
| `500 Internal Error`| `{"error": "INTERNAL_SERVER_ERROR", "id": "..."}` | Unhandled server error |

---

## 2. Endpoints

### 2.1 `[HTTP_METHOD]` `/api/v1/[endpoint-path]`

#### Description
> High-level summary of what this endpoint does.

#### Request Parameters
- **Query Params**:
  - `page` (integer, optional, default: 1): Page number
  - `limit` (integer, optional, default: 20): Items per page

#### Request Body
```json
{
  "field_name": "example_string",
  "count": 10,
  "is_active": true
}
```

#### Field Schema Table
| Field | Type | Required | Constraints | Description |
|---|---|---|---|---|
| `field_name` | string | Yes | 3-50 chars, alphanumeric | Unique title |
| `count` | integer | No | Min 1, Max 100 | Quantity |
| `is_active` | boolean | Yes | default: true | Active flag |

#### Success Response: `200 OK` (or `201 Created`)
```json
{
  "data": {
    "id": "uuid-1234-5678",
    "field_name": "example_string",
    "count": 10,
    "is_active": true,
    "created_at": "2026-10-21T20:30:00Z"
  }
}
```

---

## 3. Data Type Definitions

```typescript
export interface ExampleResource {
  id: string;
  fieldName: string;
  count: number;
  isActive: boolean;
  createdAt: string;
}
```
