package com.vertex.core.dto.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(of = {"id"})
public class BaseModel implements Serializable {
    private Long id;
}
