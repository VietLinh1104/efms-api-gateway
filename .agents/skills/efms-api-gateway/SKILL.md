---
name: efms-api-gateway
description: API Gateway for EFMS, handling routing and security.
---

# EFMS API Gateway

The API Gateway is the central entry point for all requests to the EFMS backend (runs on default port `8080`).

## Responsibilities
- **Dynamic Routing**: Proxying API requests to the appropriate downstream microservice (Identity, Core) using Spring Cloud Gateway mappings.
- **Authentication Validation**: Intercepting requests to validate the signature and expiration of JWT tokens issued by the Identity Service.
- **Cross-Cutting Concerns**: Managing CORS, centralized logging, rate limiting, and global error handling before requests even hit the inner services.

## Routing Mapping
Requests are mapped dynamically based on the URL prefix from the client:
- `http://localhost:8080/api/identity/**` -> Forwards to **`efms-identity-service`**
- `http://localhost:8080/api/core/**` -> Forwards to **`efms-core-service`**

## Security Mechanism
- **JWT Decoding**: The Gateway validates the JWT Secret (shared or central).
- **Authorization Flow**: The Gateway performs structural and basic validity checks of the JWT token. Once verified, it passes the request (potentially appending userId/headers) downstream. Detailed action-level permission checks (RBAC) are handled by the inner Identity and Core services, not the Gateway.

## Guidelines & Code Structure
- **Package**: `com.linhdv.efms_api_gateway`
- **Key Components**:
  - `config/`: CORS Configurations, Route Locator setups.
  - `filter/`: Custom Gateway Filters (e.g., `AuthenticationFilter` to check headers and JWT).
  - `exception/`: Global Exception Handlers to translate Gateway timeout or unauthorized errors into standardized `ApiResponse` formats.
- **Endpoints Rule**: All endpoints routed through the gateway require a valid JWT `Authorization` header, EXCEPT explicitly whitelisted endpoints (like `/api/identity/auth/logig`, `/api/identity/auth/register`).
