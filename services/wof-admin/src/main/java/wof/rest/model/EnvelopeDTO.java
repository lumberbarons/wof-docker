package wof.rest.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
public class EnvelopeDTO<T> {
    private final Map<String, Object> meta = new HashMap<>();
    private final T data;
}
