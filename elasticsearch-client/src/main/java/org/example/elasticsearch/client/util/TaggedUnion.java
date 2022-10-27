package org.example.elasticsearch.client.util;

public interface TaggedUnion<Tag extends Enum<?>, BaseType> {

    /**
     * Get the of the kind of variant held by this object.
     *
     * @return the variant kind
     */
    Tag _kind();

    BaseType _get();
}