##Install and configure ActiveMQ Resource Adapter in JBoss Cluster

- We can use Activemq Resource Adapter to integrate with J2EE contain. Here I'll show you how to integrate ActiveMQ with JBoss Cluster. The key to achieve the requirement is deploy a network of brokers to allow the activemq in each node to connect to each other.

- Currently, there're some articles describing how to use activemq as an embedded broker or connecting to a remote broker, [Integration of JBoss AS 7 with ActiveMQ](https://community.jboss.org/wiki/IntegrationOfJBossAS7WithActiveMQ) for example. In fact, that depends on whether we set ServerUrl to "vm://brokername" or like other "<protocol>://<host>:<port>". Or in another word, ServerUrl is a tranportConnector thing.
###Configurations in Activemq resource Adapter.
- To allow the activemq in each Jboss node to connect to each other, we need to start an activemq broker inside of each JBoss. So here we should enable the config-property BrokerXmlConfig in activemq-ra.rar/META-INF/ra.xml
~~~
1 Copy&Paste the activemq-ra.rar to a folder, for example, resource.
2 Extract the content by using the command: jar xf activemq-ra.rar
3 Set the config-proper-value of the config-property BrokerXmlConfig in META-INF/ra.xml is xbean:broker-config.xml -- xbean:broker-config.xml means it'll use the broker-config.xml in CLASSPATH to as a deployment descriptor to start a broker. Here xbean:broker-config.xml means it'll use xbean:broker-config.xml from the root for the resource adapter archive.
~~~
- Define the networkConnectors.
  - Add an element <networkConnectors> to the broker-config.xml. Such as, you want to allow the broker(tcp://10.66.218.10:61616) to connect to another broker(tcp://10.66.218.107:61616). You can use the following configuration as below:
~~~
<networkConnectors>
                  <networkConnector uri="static:(tcp://10.66.218.10:61616,tcp://10.66.218.107:61616)"/> 
</networkConnectors>
~~~
  - Here I just use static reference to take an example, more advanced feature could be found in page [Networks of Brokers](http://activemq.apache.org/networks-of-brokers.html)
- To allow clients to connect to the broker,  we can add a <transportConnector> in the broker-config.xml, for instance, <transportConnector uri="tcp://10.66.218.10:61616"/>	
- Recreate a new activemq-ra.rar file by using "jar cf activemq-ra.rar ./*", then deploy it to JBoss.
- Note: you need to deploy a resouce adapter for your each node. So you need to configure a resource-adapter for your each node.
###Configurations in JBoss side.
- Declare the resource adapter in standalone-full-ha.xml
~~~
 <subsystem xmlns="urn:jboss:domain:resource-adapters:1.1">
            <resource-adapters>
                <resource-adapter id="activemq-ra.rar">
                    <archive>
                        activemq-ra.rar
                    </archive>
                    <transaction-support>XATransaction</transaction-support>
                    <config-property name="Password">
                        defaultPassword
                    </config-property>
                    <config-property name="UserName">
                        defaultUser
                    </config-property>
                    <config-property name="ServerUrl">
                        tcp://10.66.218.10:61616?jms.rmIdFromConnectionId=true
                    </config-property>
                    <connection-definitions>
                        <connection-definition class-name="org.apache.activemq.ra.ActiveMQManagedConnectionFactory" jndi-name="java:/activemq/ConnectionFactory" enabled="true" pool-name="ActiveMQConnectionFactoryPool">
                            <xa-pool>
                                <min-pool-size>1</min-pool-size>
                                <max-pool-size>20</max-pool-size>
                                <prefill>false</prefill>
                                <is-same-rm-override>false</is-same-rm-override>
                            </xa-pool>
                            <recovery>
                                <recover-credential>
                                    <user-name>defaultUser</user-name>
                                    <password>defaultPassword</password>
                                </recover-credential>
                            </recovery>
                        </connection-definition>
                    </connection-definitions>
                    <admin-objects>
                        <admin-object class-name="org.apache.activemq.command.ActiveMQQueue" jndi-name="java:/queue/HELLOWORLDMDBQueue" use-java-context="true" pool-name="HELLOWORLDMDBQueue">
                            <config-property name="PhysicalName">
                                HELLOWORLDMDBQueue
                            </config-property>
                        </admin-object>
                        <admin-object class-name="org.apache.activemq.command.ActiveMQTopic" jndi-name="java:/topic/HELLOWORLDMDBTopic" use-java-context="true" pool-name="HELLOWORLDMDBTopic">
                            <config-property name="PhysicalName">
                                HELLOWORLDMDBTopic
                            </config-property>
                        </admin-object>
                    </admin-objects>
                </resource-adapter>
            </resource-adapters>
        </subsystem>
~~~
- Note: we need to set the archive to the resource adapter archive file name, and we set ServerUrl to connect your local broker which refers to the <transportConnector> in the broker-config.xml file.
- To make mdb use activemq resource adapter, modify:
~~~
<mdb>
          <resource-adapter-ref resource-adapter-name="${ejb.resource-adapter-name:hornetq-ra}"/>
          <bean-instance-pool-ref pool-name="mdb-strict-max-pool"/>
</mdb>
~~~
in subsystem ejb3 to:
~~~
<mdb>
        <resource-adapter-ref resource-adapter-name="activemq-rar.rar"/>
        <bean-instance-pool-ref pool-name="mdb-strict-max-pool"/>
</mdb>
~~~
###Verification
- Start nodeA 
~~~
./standalone.sh -c standalone-full-ha.xml -Djboss.bind.address=10.66.218.107 -Djboss.bind.address.management=10.66.218.107
~~~
- Start nodeB
~~~
./standalone.sh -c standalone-full-ha.xml -Djboss.bind.address=10.66.218.10 -Djboss.bind.address.management=10.66.218.10
~~~
- Deloy helloworld-mdb in [jboss-as-quickstarts](https://github.com/wildfly/quickstart), but before this, we should do some modification to adapt it for the activemq resource adapter.
  - Delete the following HornetQ XML configuration file hornetq-jms.xml from the helloworld-mdb project:
  - Edit the annotations on the HelloWorldQueueMDB message driven bean class, so that it integrates with the ActiveMQ resource adapter (instead of HornetQ). Open the HelloWorldQueueMDB.java file and make the modifications highlighted in the following extract:
  ~~~
  import org.jboss.ejb3.annotation.ResourceAdapter;
...
@MessageDriven(name = "HelloWorldQueueMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "HELLOWORLDMDBQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@ResourceAdapter(value="activemq-rar.rar")
public class HelloWorldQueueMDB implements MessageListener {
  ~~~
  - Add the maven dependency for the @ResourceAdapter annotation
  ~~~
  <dependency>
            <groupId>org.jboss.ejb3</groupId>
            <artifactId>jboss-ejb3-ext-api</artifactId>
            <version>2.1.0</version>
            <scope>provided</scope>
  </dependency>
  ~~~
  - Build the project helloworld-mdb with "mvn clean pacakge", then deploy the jboss-as-helloworld.war to each node.
  - Sending 5 messages to the network brokers via "http://10.66.218.10:8080/jboss-as-helloworld-mdb/HelloWorldMDBServletClient"
  - You can see the 5 messages are consumed by the two mdbs in the jboss cluster.
