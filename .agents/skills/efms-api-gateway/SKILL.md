---
name: efms-api-gateway
description: API Gateway for EFMS, handling routing and security.
---

# EFMS API Gateway

The API Gateway is the central entry point for all requests to the EFMS backend.

## Responsibilities
- **Routing**: Proxying requests to the appropriate microservice (Identity, Core).
- **Authentication**: Validating JWT tokens from the frontend.
- **Cross-Cutting Concerns**: CORS, shared logging, and global error handling.

## Routing Mapping
Requests are mapped to the downstream services based on the URL prefix:
- `/api/identity/**` -> `efms-identity-service`
- `/api/core/**` -> `efms-core-service`

## Security
- **JWT Secret**: Shared between the Gateway and Backend services (or derived from a central identity server).
- **Authorization**: The Gateway performs basic JWT validity checks, while detailed permission checks are handled by the Identity and Core services.

## Guidelines
- Package name: `com.linhdv.efms_api_gateway`.
- All endpoints must include a valid JWT unless they are explicitly whitelisted (e.g., login).
- Ensure consistent error response format.
