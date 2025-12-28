import axios from 'axios';
import type { SampleRequest, SampleResponse, PageResponse } from '../types/sample';

// Use base URL from Vite (includes context path for deployment)
// In production when served from same origin: use base path from Vite
// In development: use full URL to backend
const getApiBaseUrl = () => {
  // If explicitly set via environment variable, use it
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL;
  }
  
  // In production, use the base path (context path) from Vite
  // This ensures API calls work when deployed to a subpath like /geological-sample-api/
  if (import.meta.env.PROD) {
    return import.meta.env.BASE_URL || '';
  }
  
  // In development, use full URL to backend
  return 'http://localhost:8080';
};

const API_BASE_URL = getApiBaseUrl();

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Server responded with error status
      const message = error.response.data?.message || error.response.data?.error || 'An error occurred';
      return Promise.reject(new Error(message));
    } else if (error.request) {
      // Request was made but no response received
      return Promise.reject(new Error('Network error: Unable to connect to the server'));
    } else {
      // Something else happened
      return Promise.reject(new Error(error.message || 'An unexpected error occurred'));
    }
  }
);

export const sampleApi = {
  /**
   * Get all samples with pagination
   */
  getAllSamples: async (
    page: number = 0,
    size: number = 20,
    sort: string = 'id'
  ): Promise<PageResponse<SampleResponse>> => {
    const response = await apiClient.get<PageResponse<SampleResponse>>('/api/v1/samples', {
      params: { page, size, sort },
    });
    return response.data;
  },

  /**
   * Get a single sample by ID
   */
  getSampleById: async (id: string): Promise<SampleResponse> => {
    const response = await apiClient.get<SampleResponse>(`/api/v1/samples/${id}`);
    return response.data;
  },

  /**
   * Create a new sample
   */
  createSample: async (data: SampleRequest): Promise<SampleResponse> => {
    const response = await apiClient.post<SampleResponse>('/api/v1/samples', data);
    return response.data;
  },

  /**
   * Update an existing sample
   */
  updateSample: async (id: string, data: SampleRequest): Promise<SampleResponse> => {
    const response = await apiClient.put<SampleResponse>(`/api/v1/samples/${id}`, data);
    return response.data;
  },

  /**
   * Delete a sample
   */
  deleteSample: async (id: string): Promise<void> => {
    await apiClient.delete(`/api/v1/samples/${id}`);
  },
};
