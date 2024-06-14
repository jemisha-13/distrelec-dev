import {
  BlobDeleteIfExistsResponse,
  BlobItem,
  BlobServiceClient,
  BlobUploadCommonResponse,
  ContainerClient,
} from '@azure/storage-blob';
import * as zlib from 'zlib';

import { DefaultExpressServerLogger, ExpressServerLogger, RenderingEntry } from '@spartacus/setup/ssr';
import { ExternalCacheService } from '../external-cache-service';
import {
  BlobStorageConfig,
  SsrOptimizationOptionsCentralizedCache,
} from '../../ssr-rendering-centralized-cache/ssr-optimization-options-centralized-cache';

/**
 * This service has method to upload, delete and get azure blob storage
 */
export class AzureBlobStorageService implements ExternalCacheService {
  private readonly logger: ExpressServerLogger;
  private readonly containerName: string;
  private readonly deploymentDate: Date;

  private blobServiceClient: BlobServiceClient;
  private containerClient: ContainerClient;
  private containerExists = false;

  constructor(
    private ssrOptions: SsrOptimizationOptionsCentralizedCache,
    private blobStorageOptions: BlobStorageConfig,
  ) {
    this.containerName = blobStorageOptions.blobContainerName;
    this.deploymentDate = new Date(blobStorageOptions.deploymentTimestamp);
    this.blobServiceClient = BlobServiceClient.fromConnectionString(blobStorageOptions.blobConnectionString);
    this.containerClient = this.blobServiceClient.getContainerClient(this.containerName);
    this.logger = new DefaultExpressServerLogger();
  }

  async upload(key: string, html: string): Promise<BlobUploadCommonResponse> {
    await this.ensureContainerExists();
    const blobName = this.appendFileExtension(key);
    const blockBlobClient = this.containerClient.getBlockBlobClient(blobName);

    let contentBuffer: Buffer;
    if (this.ssrOptions.compressCache) {
      contentBuffer = zlib.gzipSync(html);
    } else {
      contentBuffer = Buffer.from(html, 'utf-8');
    }

    return blockBlobClient.uploadData(contentBuffer);
  }

  async get(key: string): Promise<RenderingEntry | undefined> {
    await this.ensureContainerExists();
    const renderEntry: RenderingEntry = {};
    const blobName = this.appendFileExtension(key);

    try {
      const blockBlobClient = this.containerClient.getBlockBlobClient(blobName);
      const properties = await blockBlobClient.getProperties();
      const contentLength = properties.contentLength;
      const lastModifiedDate = properties.lastModified;

      if (lastModifiedDate < this.deploymentDate) {
        this.logger.debug('Cache entry is older than deployment date, removing', {
          blobName,
          modifiedDate: lastModifiedDate,
          deploymentDate: this.deploymentDate,
        });
        this.delete(key);
        return undefined;
      }

      if (contentLength === 0) {
        const currentDate = new Date();
        const emptyBlobTTL = this.ssrOptions.maxRenderTime ?? 1000 * 60;
        const renderingMaxTTL = new Date(currentDate.getTime() - emptyBlobTTL);

        if (lastModifiedDate !== undefined && lastModifiedDate >= renderingMaxTTL && renderingMaxTTL <= currentDate) {
          renderEntry.rendering = true;
          renderEntry.time = lastModifiedDate.getTime();
          return renderEntry;
        } else {
          this.logger.debug(
            `Pending rendering has not been completed within specified TTL (${emptyBlobTTL}), creating new cache`,
            { blobName },
          );
          renderEntry.rendering = false;
        }
      }

      const downloadBlockBlobResponse = await blockBlobClient.downloadToBuffer();
      let buffer: Buffer;
      if (this.ssrOptions.compressCache) {
        buffer = zlib.gunzipSync(downloadBlockBlobResponse);
      } else {
        buffer = Buffer.from(downloadBlockBlobResponse);
      }

      renderEntry.html = buffer.toString('utf-8');
      renderEntry.time = lastModifiedDate.getTime();
      return renderEntry;
    } catch (error: any) {
      if (error.statusCode !== 404) {
        // Review logs and extend handling here as required
        this.logError('Error in getting cached response from blob storage', error, { key: blobName });
      }

      return undefined;
    }
  }

  async delete(key: string): Promise<BlobDeleteIfExistsResponse> {
    await this.ensureContainerExists();
    const blobName = this.appendFileExtension(key);
    const blockBlobClient = this.containerClient.getBlockBlobClient(blobName);
    return await blockBlobClient.deleteIfExists();
  }

  async getList(): Promise<BlobItem[]> {
    await this.ensureContainerExists();

    const blobs: BlobItem[] = [];

    try {
      const iter = this.containerClient.listBlobsFlat();
      for await (const blob of iter) {
        blobs.push(blob);
      }
    } catch (error) {
      this.logger.error('Error listing blobs:', { error });
    }

    return blobs;
  }

  private async ensureContainerExists(): Promise<void> {
    if (this.containerExists) {
      return;
    }

    try {
      await this.containerClient.createIfNotExists();
      this.containerExists = true;
    } catch (error) {
      this.logError('Error ensuring container exists', error, { containerName: this.containerName });
    }
  }

  /**
   * Appends an html file extension, so we can handle downloaded files more convenient.
   */
  private appendFileExtension(key: string): string {
    return key + (this.ssrOptions.compressCache ? '.html.gz' : '.html');
  }

  private logError(message: string, error: any, additionalContext: any): void {
    this.logger.debug(message, {
      ...additionalContext,
      errorDetails: {
        name: error?.name ?? '',
        status: error?.statusCode ?? '',
        code: error?.details?.errorCode ?? '',
        message: error?.message ?? '',
      },
      stackTrace: error.stack,
    });
  }
}
