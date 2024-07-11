package benchmark;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, WebMvcAutoConfiguration.class })
@ComponentScan(basePackages = "dzung.trie.spell_checker")
public class BenchmarkConfig {
    @Bean
    public MyService myService() {
        return new MyService();
    }
}
