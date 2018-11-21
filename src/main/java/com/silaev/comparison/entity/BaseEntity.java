package com.silaev.comparison.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.io.Serializable;

/**
 * Keeps common fields.
 */
@Data
abstract class BaseEntity implements Serializable {
    @Id
    private String id;

    @Version
    private Long version;
}
