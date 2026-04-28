package com.vertex.core.dto.base;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface EntityModel<ID extends Serializable> extends Serializable {

    ID getId();
}
