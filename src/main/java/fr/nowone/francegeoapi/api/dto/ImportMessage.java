package fr.nowone.francegeoapi.api.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableImportMessage.class)
@JsonDeserialize(as = ImmutableImportMessage.class)
public interface ImportMessage {
    String getType(); // "REGION", "DEPARTEMENT", "COMMUNE"
    @Nullable
    String getParentCode();
}
