import { FILE_VALIDATION_CONFIG, GatewayProxied, IFileValidation } from 'smarteditcommons';
import {
    ErrorContext,
    FileMimeTypeService,
    FileValidatorByProperty,
    FileValidatorFactory
} from '@smart/utils';
import { Injectable } from "@angular/core";

const BYTE = 1024;

@Injectable()
@GatewayProxied('buildAcceptedFileTypesList', 'validate')
export class DistFileValidationService extends IFileValidation {
    private readonly validators: FileValidatorByProperty[] = [
        {
            subject: 'size',
            message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_SIZE_INVALID,
            validate: (size: number, maxSize: number): boolean => size <= maxSize * BYTE * BYTE
        }
    ];

    constructor(
        private readonly fileMimeTypeService: FileMimeTypeService,
        private readonly fileValidatorFactory: FileValidatorFactory
    ) {
        super();
    }

    /**
     * Validates the specified file object against custom validator and its mimetype.
     * It appends the errors to the error context array provided or it creates a new error context array.
     *
     * @param file The web API file object to be validated.
     * @param context The contextual error array to append the errors to. It is an output parameter.
     * @returns A promise that resolves if the file is valid otherwise it rejects with a list of errors.
     */
    public async validate(
        file: File,
        maxUploadFileSize: number,
        errorsContext: ErrorContext[]
    ): Promise<ErrorContext[] | void> {
        const distMaxUploadFileSize = maxUploadFileSize < 1 ? maxUploadFileSize : 1;
        this.fileValidatorFactory
            .build(this.validators)
            .validate(file, distMaxUploadFileSize, errorsContext);
        try {
            await this.fileMimeTypeService.isFileMimeTypeValid(file);
            if (errorsContext.length > 0) {
                return Promise.reject(errorsContext);
            }
            return Promise.resolve();
        } catch {
            errorsContext.push({
                subject: 'type',
                message: FILE_VALIDATION_CONFIG.I18N_KEYS.FILE_TYPE_INVALID
            });
            return Promise.reject(errorsContext);
        }
    }
}
