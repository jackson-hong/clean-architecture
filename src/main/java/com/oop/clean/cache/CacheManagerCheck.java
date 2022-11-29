package com.oop.clean.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheManagerCheck implements SpringApplicationRunListener {

    private final CacheManager cacheManager;


}
