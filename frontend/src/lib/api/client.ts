import createClient from "openapi-fetch"

// Placeholder for OpenAPI paths. Will be generated later using openapi-typescript.
export interface paths {}

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

export const client = createClient<paths>({
  baseUrl: baseURL,
})

// Middleware to centralize base URL, token, 401, errors and AbortSignal
client.use({
  onRequest({ request }) {
    const token = localStorage.getItem("auth_token")
    if (token) {
      request.headers.set("Authorization", `Bearer ${token}`)
    }
    return request
  },
  async onResponse({ response }) {
    if (response.status === 401) {
      // Clear token and trigger redirect or event
      localStorage.removeItem("auth_token")
      console.warn("Unauthorized request - clearing token")
    }
    
    if (!response.ok) {
      // Log or handle HTTP errors centralizing error handling
      console.error(`HTTP error! status: ${response.status}`)
    }
    
    return response
  },
})
