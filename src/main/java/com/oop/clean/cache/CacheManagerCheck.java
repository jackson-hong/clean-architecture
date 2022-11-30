package com.oop.clean.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheManagerCheck implements CommandLineRunner {

//    private final CacheManager cacheManager;

    @Override
    public void run(String... args) throws Exception {

    }
}
