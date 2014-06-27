###Environment
httpd mod_cluster 10.66.218.108
EAP 6 hostA 10.66.218.107
EAP 6 hostB 10.66.218.10

###Httpd
- yum install httpd
- copy the .so files from jboss-eap-native-webserver-connectors to /etc/http/modules
- mod_cluster.conf

~~~
# mod_proxy_balancer should be disabled when mod_cluster is used
LoadModule proxy_cluster_module modules/mod_proxy_cluster.so
LoadModule slotmem_module modules/mod_slotmem.so
LoadModule manager_module modules/mod_manager.so
LoadModule advertise_module modules/mod_advertise.so

MemManagerFile /var/cache/mod_cluster


Listen 10.66.218.108:6666

 <VirtualHost *:6666>
# See http://docs.jboss.org/mod_cluster/1.2.0/html_single/#d0e485
    CreateBalancers 1
#ProxyPass        /togoogle http://www.baidu.com
#ProxyPassReverse /togoogle http://www.baidu.com

ProxyPass / balancer://mycluster stickysession=JSESSIONID|jsessionid nofailover=on
ProxyPassReverse / balancer://mycluster
ProxyPreserveHost on

    <Directory />
        Order deny,allow
        Allow from all
    </Directory>
    <Location /mod_cluster-manager>
        SetHandler mod_cluster-manager
        Order deny,allow
        Allow from all
    </Location>
          KeepAliveTimeout 60
          MaxKeepAliveRequests 0
          AdvertiseFrequency 5
          ManagerBalancerName mycluster
          ServerAdvertise Off
          EnableMCPMReceive On
</VirtualHost>
~~~
- service httpd restart
- verify if the mod_cluster works or not via http://10.66.218.108/mod_cluster-manager.
  - Please don't forget to clean cookies when can't see the mod_cluster-manager page.
###EAP 6
#### Standalone
~~~
<subsystem xmlns="urn:jboss:domain:modcluster:1.1">
            <mod-cluster-config advertise-socket="modcluster" connector="ajp"  proxy-list="10.66.218.108:6666">
              .
            </mod-cluster-config>
</subsystem>
~~~
- startup command in hostA
~~~
./standalone.sh -c standalone-ha.xml -Djboss.bind.address=10.66.218.107 -Djboss.bind.address.management=10.66.218.107
~~~
- startup command in hostB
~~~
./standalone.sh -c standalone-ha.xml -Djboss.bind.address=10.66.218.10 -Djboss.bind.address.management=10.66.218.10
~~~
- Note
~~~
Use -Djboss.bind.address to specify which IP will be bound for the EAP, mod_cluster will store it as a destination in the cluster.
~~~

- Test 
 - Deploy a same application to each EAP. For instance, the web context is helloWeb
 - ModCluster page shows there's one Web Context enabled in each EAP node.
 - Type "http://10.66.218.108:6666/helloWeb" in Web browser.

- Link 
   - Regarding to ModCluster balancing policy, refer to [ModCluster Subsystem configuration](http://docs.jboss.org/mod_cluster/1.2.0/html_single/#ModClusterSubSystemConfiguration)
   - http://docs.jboss.org/mod_cluster/1.2.0/html_single/
   - http://www.snip2code.com/Snippet/9830/mod_cluster-and-ProxyPass---static-from-