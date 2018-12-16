package wof.rest.model;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class EnvelopeDTO<T> {
    private final Map<String, Object> meta = new HashMap<>();
    private final T data;

    public EnvelopeDTO(T data, Page page) {
        this.data = data;

        meta.put("totalElements", page.getTotalElements());
        meta.put("totalPages", page.getTotalPages());

        if(page.hasNext()) {
            meta.put("nextPage", page.getNumber() + 1);
        }

        if(page.hasPrevious()) {
            meta.put("nextPage", page.getNumber() - 1);
        }
    }
}
