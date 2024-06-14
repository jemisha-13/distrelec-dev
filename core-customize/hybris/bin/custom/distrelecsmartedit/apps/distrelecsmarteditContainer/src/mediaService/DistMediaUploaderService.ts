import { GatewayProxied, ICMSMedia, IMediaToUpload, IRestService, MediaToUpload, RestServiceFactory } from 'smarteditcommons';
import * as lodash from 'lodash';
import { Injectable } from "@angular/core";

@Injectable()
@GatewayProxied('uploadMedia')
export class DistMediaUploaderService extends IMediaToUpload {

    private readonly mediaRestService: IRestService<ICMSMedia>;

    constructor(private restServiceFactory: RestServiceFactory) {
        super();
        this.mediaRestService = this.restServiceFactory.get('/cmswebservices/v1/catalogs/Default/versions/Staged/media');
    }

    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadMedia(media: MediaToUpload): Promise<ICMSMedia> {
        const formData = new FormData();
        lodash.forEach(media, (value, key: string) => {
            formData.append(key, value);
        });

        return this.mediaRestService.save(formData as any, {
            headers: { enctype: 'multipart/form-data', fileSize: '' + media?.file?.size }
        });
    }

}
