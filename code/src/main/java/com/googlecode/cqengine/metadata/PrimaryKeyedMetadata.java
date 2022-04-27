package com.googlecode.cqengine.metadata;

import com.googlecode.cqengine.attribute.Attribute;

public interface PrimaryKeyedMetadata<O,PK> {

    Attribute<O,PK> getPKAttribute();

}
