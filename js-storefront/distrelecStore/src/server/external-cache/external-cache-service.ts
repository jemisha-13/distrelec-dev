import { BlobDeleteIfExistsResponse, BlobItem, BlobUploadCommonResponse } from '@azure/storage-blob';
import { RenderingEntry } from '@spartacus/setup/ssr';

export interface ExternalCacheService {
  upload(key: string, html: string): Promise<BlobUploadCommonResponse>;

  get(key: string): Promise<RenderingEntry | undefined>;

  delete(key: string): Promise<BlobDeleteIfExistsResponse>;

  getList(): Promise<BlobItem[]>;
}
