package com.namics.distrelec.occ.core.mapping.resolvers;

public interface DistHeadlessResolver<T, V> {

    T resolve(V value);
}
