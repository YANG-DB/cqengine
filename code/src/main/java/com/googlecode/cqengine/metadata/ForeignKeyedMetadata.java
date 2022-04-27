package com.googlecode.cqengine.metadata;

import com.googlecode.cqengine.attribute.Attribute;

public interface ForeignKeyedMetadata {

    Attribute getFKAttributeByClass(Class type);

}
