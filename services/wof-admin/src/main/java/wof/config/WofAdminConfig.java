package wof.config;

import wof.rest.model.mapper.ModelMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WofAdminConfig {

    @Bean
    public ModelMapper getUserMapper() {
        return Mappers.getMapper(ModelMapper.class);
    }
}
