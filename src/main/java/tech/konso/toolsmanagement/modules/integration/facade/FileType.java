package tech.konso.toolsmanagement.modules.integration.facade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

@AllArgsConstructor
@Getter
public enum FileType {
    PHOTO_PERSON(MediaType.IMAGE_JPEG_VALUE),
    PHOTO_TOOL(MediaType.IMAGE_JPEG_VALUE);

    private final String contentType;
}
