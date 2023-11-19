package tech.konso.toolsmanagement.modules.integration.facade.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tech.konso.toolsmanagement.modules.integration.facade.FileStorageFacade;
import tech.konso.toolsmanagement.modules.integration.facade.FileType;
import tech.konso.toolsmanagement.modules.integration.facade.dto.UploadResponse;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.UUID;

/**
 * Implementation for file storage facade
 */
@Slf4j
@Service(value = "file-storage-facade-impl")
public class FileStorageFacadeImpl implements FileStorageFacade {

    @Autowired
    @Qualifier("integration-file-storage-api")
    private WebClient client;

    private static final String BASE_PATH = "/v1";
    private static final String FILE_BY_UUID_URL = BASE_PATH + "/{uuid}";

    /**
     * Upload file to file storage service
     *
     * @param multipartFile to upload to file storage service
     * @param fileType      file type for choosing bucket in file storage
     * @return {@link UploadResponse} object response for uploading file with id and errors
     */
    @Override
    public UploadResponse upload(MultipartFile multipartFile, FileType fileType) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("attachment", multipartFile.getResource());

        return client.post()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH)
                        .queryParam("fileType", fileType)
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData("attachment", multipartFile.getResource()))
                .retrieve()
                .bodyToMono(UploadResponse.class)
                .doOnError(e -> {
                    log.error("Error upload photo to file storage", e);
                    throw new BPException.ServiceUnavailable("Error upload photo to file storage");
                })
                .block();
    }

    /**
     * Download file by id and type
     *
     * @param id       file in file storage
     * @param fileType file type for choosing bucket in file storage
     * @return InputStreamResource with searching file
     */
    @Override
    public InputStreamResource download(UUID id, FileType fileType) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path(FILE_BY_UUID_URL)
                        .queryParam("fileType", fileType.name())
                        .build(id))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, rs -> Mono.error(new BPException.NotFound("File not found " + id)))
                .onStatus(HttpStatus.BAD_REQUEST::equals, rs -> Mono.error(new BPException.BadRequest("Error retrieving file from storage " + id)))
                .bodyToMono(InputStreamResource.class)
                .doOnError(e -> {
                    log.error("File storage service unavailable. Try to et photo by id {}", id, e);
                    throw new BPException.ServiceUnavailable("File storage service unavailable. Try to et photo by id: " + id);
                })
                .block();
    }
}
