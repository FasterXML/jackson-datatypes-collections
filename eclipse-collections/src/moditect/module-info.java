// Generated 20-Mar-2019 using Moditect maven plugin
module jackson.datatype.eclipse.collections {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires jackson.datatype.primitive.collections.base;

    requires org.eclipse.collections.api;
    requires org.eclipse.collections.impl;

    exports com.fasterxml.jackson.datatype.eclipsecollections;
    exports com.fasterxml.jackson.datatype.eclipsecollections.deser;
    exports com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;
    exports com.fasterxml.jackson.datatype.eclipsecollections.deser.list;
    exports com.fasterxml.jackson.datatype.eclipsecollections.deser.map;
    exports com.fasterxml.jackson.datatype.eclipsecollections.deser.pair;
    exports com.fasterxml.jackson.datatype.eclipsecollections.deser.set;
    exports com.fasterxml.jackson.datatype.eclipsecollections.ser;
    exports com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

    provides com.fasterxml.jackson.databind.JacksonModule with
        com.fasterxml.jackson.datatype.eclipsecollections.EclipseCollectionsModule;
}
