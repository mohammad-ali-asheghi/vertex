package com.vertex.core.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface DocumentDto {

    record Response(
            Long id,
            String name,
            String extension,
            Long size,
            String mimeType,
            LocalDateTime uploadDate,
            String downloadUrl
    ) implements Serializable {
    }

    record Search(
            String query,
            String extension
    ) implements Serializable {
    }

    record SystemDoc(
            String id,
            String title,
            String category,
            String description,
            String version,
            String status,
            String markdownContent
    ) implements Serializable {
    }
}
