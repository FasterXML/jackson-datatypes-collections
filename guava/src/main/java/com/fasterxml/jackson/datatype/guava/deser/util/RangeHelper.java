package com.fasterxml.jackson.datatype.guava.deser.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;

/**
 * @since 2.10
 */
public class RangeHelper
{
    public static class RangeProperties implements java.io.Serializable
    {
        private static final long serialVersionUID = 2L;

        public final String lowerEndpoint, upperEndpoint;
        public final String lowerBoundType, upperBoundType;

        public RangeProperties(String lowerEP, String upperEP,
                String lowerBT, String upperBT) {
            lowerEndpoint = lowerEP;
            upperEndpoint = upperEP;
            lowerBoundType = lowerBT;
            upperBoundType = upperBT;
        }
    }

    private final static RangeProperties STD_NAMES = getPropertyNames(null, null);

    public static RangeProperties standardNames() {
        return STD_NAMES;
    }

    public static RangeProperties getPropertyNames(MapperConfig<?> config, PropertyNamingStrategy pns) {
        return new RangeProperties(
                _find(config, pns, "lowerEndpoint"),
                _find(config, pns, "upperEndpoint"),
                _find(config, pns, "lowerBoundType"),
                _find(config, pns, "upperBoundType")
        );
    }

    private static String _find(MapperConfig<?> config, PropertyNamingStrategy pns, String origName) {
        return (pns == null) ? origName : pns.nameForField(config, null, origName);
    }
}
