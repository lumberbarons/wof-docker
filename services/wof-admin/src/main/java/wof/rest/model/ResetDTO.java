package wof.rest.model;

import lombok.Data;

@Data
public class ResetDTO {
    private String resetKey;
    private String password;
}
