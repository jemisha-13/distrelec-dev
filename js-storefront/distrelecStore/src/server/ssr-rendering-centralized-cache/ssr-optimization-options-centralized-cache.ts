import { SsrOptimizationOptions } from '@spartacus/setup/ssr';

export enum ExternalCacheStorageType {
  AZURE_BLOB_STORAGE = 'azureblob',
}

export enum ExternalCacheStatus {
  CACHE_EXISTS = 1,
  RENDERING_IN_PROCESS = 2,
  CACHE_NOT_EXISTS = 3,
  ERROR = 4,
}

export const IS_RENDERING_CONTENT = '';

export interface SsrOptimizationOptionsCentralizedCache extends SsrOptimizationOptions {
  externalCache?: boolean;
  compressCache?: boolean;
}

export interface BlobStorageConfig {
  blobConnectionString: string;
  blobContainerName: string;
  deploymentTimestamp: number;
}
