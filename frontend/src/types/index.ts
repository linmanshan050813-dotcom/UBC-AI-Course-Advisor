/**
 * Type definitions for Phase 0
 * Will be expanded in later phases
 */

/**
 * Health Status Interface
 * Represents the health check response from the backend.
 */
export interface HealthStatus {
  status: string;
  phase: number;
}
