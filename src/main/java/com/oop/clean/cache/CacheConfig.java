package com.oop.clean.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableCaching
public class CacheConfig {

    private CacheManager createCacheManager(){
        net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
        configuration.diskStore(new DiskStoreConfiguration().path("java.io.tmpdir"));
        // Cluster
        this.setClusteringConfig(configuration);
        return CacheManager.create(configuration);
    }

    private void setClusteringConfig(net.sf.ehcache.config.Configuration configuration) {
        // Multicast
        FactoryConfiguration factoryConfig = new FactoryConfiguration()
                .className("net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory")
                .properties("peerDiscovery=automatic,multicastGroupAddress=230.0.0.1,multicastGroupPort");
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {

        net.sf.ehcache.CacheManager manager = this.createCacheManager();

        Cache getMenuCache = new Cache(new CacheConfiguration()
                .maxEntriesLocalHeap(1000)
                .maxEntriesLocalDisk(10000)
                .eternal(false)
                .timeToIdleSeconds(1800)
                .timeToLiveSeconds(1800)
                .memoryStoreEvictionPolicy("LFU")
                .transactionalMode(CacheConfiguration.TransactionalMode.OFF)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP))
                .name("getMenu")
        );
        manager.addCache(getMenuCache);

        return new EhCacheCacheManager(manager);
    }
}
