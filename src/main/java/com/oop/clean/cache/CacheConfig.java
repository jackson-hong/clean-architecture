package com.oop.clean.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.distribution.MulticastKeepaliveHeartbeatSender;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {

        net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
        // path는 DiskStoreConfiguration 클래스의 ENV enum 참조하거나 PhysicalPath로 설정
        configuration.diskStore(new DiskStoreConfiguration().path("java.io.tmpdir"));

        /** Cluster 설정 */
        // 노드(피어) 자동 발견 Multicast 설정
        FactoryConfiguration factoryConfig = new FactoryConfiguration()
                .className("net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory")
                .properties("peerDiscovery=automatic,multicastGroupAddress=230.0.0.1,multicastGroupPort=44464,timeToLive=32")
                .propertySeparator(",");

        // 클러스터에 있는 다른 노드에서 발생한 메시지 수신 리스너 설정
        String hostname = System.getProperty("java.rmi.server.hostname"); // [-Djava.rmi.server.hostname=서버자신IP] 설정 필요
        FactoryConfiguration listenerFactoryConfig = new FactoryConfiguration()
                .className("net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory")
                .properties("hostName=localhost,socketTimeoutMillis=2000") // 방화벽등 port 지정이 필요한 경우 설정 추가(ex. port=12345)
                .propertySeparator(",");

        configuration.addCacheManagerPeerProviderFactory(factoryConfig);
        configuration.addCacheManagerPeerListenerFactory(listenerFactoryConfig);

        MulticastKeepaliveHeartbeatSender.setHeartBeatInterval(30000);


        net.sf.ehcache.CacheManager manager = net.sf.ehcache.CacheManager.create(configuration);

        // 캐시 변경 내역 전송 설정
        CacheConfiguration.CacheEventListenerFactoryConfiguration eventListenerFactoryConfig = new CacheConfiguration.CacheEventListenerFactoryConfiguration()
                .className("net.sf.ehcache.distribution.RMICacheReplicatorFactory")
                .properties("replicateAsynchronously=true,replicatePuts=false,replicateUpdates=true,replicateUpdatesViaCopy=false,replicateRemovals=true")
                .propertySeparator(",");


        CacheConfiguration getCacheConfig = new CacheConfiguration()
                .maxEntriesLocalHeap(1000)
                .maxEntriesLocalDisk(10000)
                .eternal(false)
                .timeToIdleSeconds(1800)
                .timeToLiveSeconds(1800)
                .memoryStoreEvictionPolicy("LRU")
                .transactionalMode(CacheConfiguration.TransactionalMode.OFF)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP))
                .name("getCache");
        // cacheEventListenerFactory 설정
        getCacheConfig = getCacheConfig.cacheEventListenerFactory(eventListenerFactoryConfig);
        Cache getAuthenticatedMenuByUriCache = new Cache(getCacheConfig);

        // 캐시 추가
        manager.addCache(getAuthenticatedMenuByUriCache);

        return new EhCacheCacheManager(manager);
    }
}
