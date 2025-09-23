package com.josdem.vetlog.configuration

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
open class HazelcastTestConfig {
  private val instanceName = "test-hazelcast"

  @Bean
  open fun hazelcastInstance(): HazelcastInstance {
    Hazelcast.getHazelcastInstanceByName(instanceName)?.let { existingInstance ->
      return existingInstance
    }
    // Otherwise, create a new instance
    val config = Config().apply { this.instanceName = this@HazelcastTestConfig.instanceName }

    return Hazelcast.newHazelcastInstance(config)
  }
}
