package com.josdem.vetlog.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class HazelcastConfiguration {

   private static final int MAX_HEAP_PERCENTAGE = 80; // Max 80% heap usage
   private static final int TTL_SECONDS = -1; // Entries do not expire

   @Bean
   public HazelcastInstance hazelcastInstance() {
      // Eviction configuration
      EvictionConfig evictionConfig = new EvictionConfig()
            .setEvictionPolicy( EvictionPolicy.LRU )
            .setMaxSizePolicy( MaxSizePolicy.USED_HEAP_PERCENTAGE )
            .setSize( MAX_HEAP_PERCENTAGE );

      // Map configuration
      MapConfig mapConfig = new MapConfig()
            .setName( "configuration" )
            .setEvictionConfig( evictionConfig )
            .setTimeToLiveSeconds( TTL_SECONDS )
            .setBackupCount( 0 ); // single node

      // Hazelcast instance configuration
      Config config = new Config();
      config.setInstanceName( "hazelcast-instance" );
      config.addMapConfig( mapConfig );

      // Single-node network config
      config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled( true ); // as per your request
      config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled( false );

      return Hazelcast.newHazelcastInstance( config );
   }
}