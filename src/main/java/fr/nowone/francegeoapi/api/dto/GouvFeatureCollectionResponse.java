package fr.nowone.francegeoapi.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;


@Value.Immutable
@JsonSerialize(as = ImmutableGouvFeatureCollectionResponse.class)
@JsonDeserialize(as = ImmutableGouvFeatureCollectionResponse.class)
public interface GouvFeatureCollectionResponse {
    String getType();
    List<GouvFeature> getFeatures();
}
