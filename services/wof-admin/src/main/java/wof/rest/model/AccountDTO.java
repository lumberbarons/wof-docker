package wof.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AccountDTO {
    private String id;
    private String email;
    private String name;
    private List<String> authorities;
}
