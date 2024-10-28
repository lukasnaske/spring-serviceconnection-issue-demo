Run `gradle test -i` to see the issue in person. 

Application cannot start due to circular dependency:
```java
    ***************************
    APPLICATION FAILED TO START
    ***************************

    Description:

    The dependencies of some of the beans in the application context form a cycle:

       entityManagerFactory defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]
          ↓
       dataSourceScriptDatabaseInitializer defined in class path resource [org/springframework/boot/autoconfigure/sql/init/DataSourceInitializationConfiguration.class]
    ┌─────┐
    |  dataSource defined in class path resource [org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]
    ↑     ↓
    |  startupTimeMetrics defined in class path resource [org/springframework/boot/actuate/autoconfigure/metrics/startup/StartupTimeMetricsListenerAutoConfiguration.class]
    ↑     ↓
    |  simpleMeterRegistry defined in class path resource [org/springframework/boot/actuate/autoconfigure/metrics/export/simple/SimpleMetricsExportAutoConfiguration.class]
    ↑     ↓
    |  metricsHealths defined in class path resource [com/example/testcontainer/issue/demo/MetricsConfig.class]
    ↑     ↓
    |  healthEndpoint defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]
    ↑     ↓
    |  healthContributorRegistry defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]
    ↑     ↓
    |  dbHealthContributor defined in class path resource [org/springframework/boot/actuate/autoconfigure/jdbc/DataSourceHealthContributorAutoConfiguration.class]
    └─────┘
```

From my investigation it appears to be connected to the `BeforeTestcontainerUsedEvent` sent while initialising the datasource within `org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory.ContainerConnectionDetails#getContainer`, which then triggers further bean creations.
```java
		protected final C getContainer() {
			Assert.state(this.container != null,
					"Container cannot be obtained before the connection details bean has been initialized");
			this.eventPublisher.publishEvent(new BeforeTestcontainerUsedEvent(this));
			return this.container;
		}

```
