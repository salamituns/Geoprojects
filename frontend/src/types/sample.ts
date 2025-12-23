/**
 * TypeScript type definitions matching the backend DTOs
 */

export enum SampleType {
  ROCK = 'ROCK',
  MINERAL = 'MINERAL',
  SOIL = 'SOIL',
  FOSSIL = 'FOSSIL',
  SEDIMENT = 'SEDIMENT',
  OTHER = 'OTHER',
}

export interface SampleRequest {
  sampleIdentifier: string;
  sampleName: string;
  sampleType: SampleType;
  collectionDate: string; // ISO date string (YYYY-MM-DD)
  latitude?: number;
  longitude?: number;
  locationName?: string;
  collectorName: string;
  description?: string;
  storageLocation?: string;
}

export interface SampleResponse {
  id: string; // UUID as string
  sampleIdentifier: string;
  sampleName: string;
  sampleType: SampleType;
  collectionDate: string; // ISO date string
  latitude?: number;
  longitude?: number;
  locationName?: string;
  collectorName: string;
  description?: string;
  storageLocation?: string;
  createdAt: string; // ISO datetime string
  updatedAt: string; // ISO datetime string
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
}
