// Eclipse-collections module Main artifact Module descriptor
module tools.jackson.datatype.eclipsecollections
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires tools.jackson.datatype.primitive_collections_base;

    requires org.eclipse.collections.api;
    requires org.eclipse.collections.impl;

    exports tools.jackson.datatype.eclipsecollections;
    exports tools.jackson.datatype.eclipsecollections.deser;
    exports tools.jackson.datatype.eclipsecollections.deser.bag;
    exports tools.jackson.datatype.eclipsecollections.deser.list;
    exports tools.jackson.datatype.eclipsecollections.deser.map;
    exports tools.jackson.datatype.eclipsecollections.deser.pair;
    exports tools.jackson.datatype.eclipsecollections.deser.set;
    exports tools.jackson.datatype.eclipsecollections.ser;
    exports tools.jackson.datatype.eclipsecollections.ser.map;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.eclipsecollections.EclipseCollectionsModule;
}
