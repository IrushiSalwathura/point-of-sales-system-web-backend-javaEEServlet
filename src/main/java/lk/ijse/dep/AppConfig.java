package lk.ijse.dep;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = "lk.ijse.dep")
@Configuration
@Import(JPAConfig.class)
public class AppConfig {
}
