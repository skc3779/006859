package readinglist;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@SpringBootApplication
@Slf4j
public class ReadingListApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		log.error("error...............................");
        log.info("info.................................");
        SpringApplication.run(ReadingListApplication.class, args);
	}
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.info("/login");
        registry.addViewController("/login").setViewName("login");
    }
	
	@Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        log.info("/argument");
        argumentResolvers.add(new ReaderHandlerMethodArgumentResolver());
    }
    
}
