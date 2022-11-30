package com.oop.clean.cache;

import lombok.RequiredArgsConstructor;
import net.sf.ehcache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CacheController {

    private final EhCacheCacheManager cacheManager;
}
