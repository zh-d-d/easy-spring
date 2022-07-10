package org.springframework.http;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @author zhangdd on 2022/7/9
 */
public class MediaType extends MimeType implements Serializable {

    public static final MediaType ALL;

    /**
     * Public constant media type for {@code application/json}.
     */
    public static final MediaType APPLICATION_JSON;

    public static final MediaType APPLICATION_OCTET_STREAM;

    public static final MediaType TEXT_EVENT_STREAM;

    private static final String PARAM_QUALITY_FACTOR = "q";

    static {
        ALL = new MediaType("*", "*");
        APPLICATION_JSON = new MediaType("application", "json");
        APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
        TEXT_EVENT_STREAM = new MediaType("text", "event-stream");
    }

    public MediaType(String type) {
        super(type);
    }

    public MediaType(String type, String subtype) {
        super(type, subtype, Collections.emptyMap());
    }

    public MediaType(MediaType other, @Nullable Map<String, String> parameters) {
        super(other.getType(), other.getSubtype(), parameters);
    }

    public MediaType(String type, String subtype, @Nullable Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    public MediaType(MimeType mimeType) {
        super(mimeType);
        getParameters().forEach(this::checkParameters);
    }

    /**
     * Return the quality factor, as indicated by a q parameter, if any. Defaults to
     * 1.0
     */
    public double getQualityValue() {
        String qualityFactor = getParameter(PARAM_QUALITY_FACTOR);
        return null != qualityFactor ? Double.parseDouble(qualityFactor) : 1D;
    }

    /**
     * Return a replica of this instance with the quality value of the given MediaType.
     */
    public MediaType copyQualityValue(MediaType mediaType) {
        if (!mediaType.getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        Map<String, String> params = new LinkedHashMap<>(getParameters());
        params.put(PARAM_QUALITY_FACTOR, mediaType.getParameters().get(PARAM_QUALITY_FACTOR));
        return new MediaType(this, params);
    }

    /**
     * Return a replica of this instance with its quality value removed.
     */
    public MediaType removeQualityValue() {
        if (!getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        Map<String, String> params = new LinkedHashMap<>(getParameters());
        params.remove(PARAM_QUALITY_FACTOR);
        return new MediaType(this, params);
    }

    public static MediaType parseMediaType(String mediaType) {
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType(mediaType);
        }
        catch (InvalidMimeTypeException ex) {
            throw new InvalidMediaTypeException(ex);
        }
        try {
            return new MediaType(type);
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidMediaTypeException(mediaType, ex.getMessage());
        }
    }




    public static void sortBySpecificityAndQuality(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(MediaType.SPECIFICITY_COMPARATOR.thenComparing(MediaType.QUALITY_VALUE_COMPARATOR));
        }
    }

    public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR = (mediaType1, mediaType2) -> {
        double quality1 = mediaType1.getQualityValue();
        double quality2 = mediaType2.getQualityValue();
        int qualityComparison = Double.compare(quality2, quality1);
        if (qualityComparison != 0) {
            return qualityComparison;  // audio/*;q=0.7 < audio/*;q=0.3
        } else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) {  // */* < audio/*
            return 1;
        } else if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) {  // audio/* > */*
            return -1;
        } else if (!mediaType1.getType().equals(mediaType2.getType())) {  // audio/basic == text/html
            return 0;
        } else {  // mediaType1.getType().equals(mediaType2.getType())
            if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) {  // audio/* < audio/basic
                return 1;
            } else if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) {  // audio/basic > audio/*
                return -1;
            } else if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) {  // audio/basic == audio/wave
                return 0;
            } else {
                int paramsSize1 = mediaType1.getParameters().size();
                int paramsSize2 = mediaType2.getParameters().size();
                return Integer.compare(paramsSize2, paramsSize1);  // audio/basic;level=1 < audio/basic
            }
        }
    };

    public static final Comparator<MediaType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MediaType>() {
        @Override
        protected int compareParameters(MediaType mediaType1, MediaType mediaType2) {
            double quality1 = mediaType1.getQualityValue();
            double quality2 = mediaType2.getQualityValue();
            int qualityComparison = Double.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison;
            }
            return super.compareParameters(mediaType1, mediaType2);
        }
    };
}
