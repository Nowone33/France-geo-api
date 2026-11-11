package fr.nowone.francegeoapi.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableRole.class)
@JsonDeserialize(as = ImmutableRole.class)
public interface Role {
    Long getId();
    String getName();
}
