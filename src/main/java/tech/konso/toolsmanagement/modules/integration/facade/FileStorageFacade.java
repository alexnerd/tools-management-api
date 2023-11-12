package tech.konso.toolsmanagement.modules.integration.facade;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import tech.konso.toolsmanagement.modules.integration.facade.dto.UploadResponse;

import java.util.UUID;

/**
 * Abstract facade for file storage service. Hides implementation logic.
 */
public interface FileStorageFacade {
    /**
     * Download file by id and type
     *
     * @param id       file in file storage
     * @param fileType file type for choosing bucket in file storage
     * @return InputStreamResource with searching file
     */
    InputStreamResource download(UUID id, FileType fileType);

    /**
     * Upload file to file storage service
     *
     * @param multipartFile to upload to file storage service
     * @param fileType      file type for choosing bucket in file storage
     * @return {@link UploadResponse} object response for uploading file with id and errors
     */
    UploadResponse upload(MultipartFile multipartFile, FileType fileType);
}
