- Copy&Paste the jboss EAP 6.1.1 GA zip archive jboss-eap-6.1.1.zip to each box. 
- Extract the zip file.
- Configuartions in domain
  - Add security-realm
     -  ManagementRealm
        - \<bin\>/add-user.sh
          - admin/RedHat1!
     -  ApplicationRealm
        - \<bin\>/add-user.sh
  - domain.xml
    - add a cluster user/password to messaging subsystem in profile full-ha
~~~
       <cluster-user>admin</cluster-user>
       <cluster-password>adminpassword</cluster-password>
~~~
- Configuration in host
  - host-slave.xml
    - Define host name. 
      - Add a attribute "name" to the root element in the host.xml 
         - \<host name="wanf1host" xmlns="urn:jboss:domain:1.4"\>
         - \<host name="wanf2host" xmlns="urn:jboss:domain:1.4"\>
    - Add authentication
      - Set password to the value of the secret in ManagementRealm
~~~
<management>
        <security-realms>
            <security-realm name="ManagementRealm">
                <server-identities>
                     <!-- Replace this with either a base64 password of your own, or use a vault with a vault expression -->
                     <secret value="UmVkSGF0MSE="/>
                </server-identities>
~~~
      - To get the encrypted string of the password, you can run "echo -n 'RedHat1!' | openssl enc -base64". 
      - Add attribute username to remote element for connecting to domain controller.
~~~
   <remote host="${jboss.domain.master.address}" port="${jboss.domain.master.port:9999}" username="admin" security-realm="ManagementRealm"/>
    </domain-controller>
~~~
    - Configure JVM options for enabling GC
        - Configure JVM options for separate server.
~~~
<server name="server-one" group="main-server-group">
                <jvm name="default">
                <jvm-options>
                   <option value="-Xloggc:/root/Server1GC.log"/>
                   <option value="-XX:+PrintGCDetails"/>
                   <option value="-XX:+PrintGCDateStamps"/>
                </jvm-options>
            </jvm>
        </server>
        <server name="server-two" group="other-server-group">
            <!-- server-two avoids port conflicts by incrementing the ports in
                 the default socket-group declared in the server-group -->
           <jvm name="default">
                <jvm-options>
                   <option value="-Xloggc:/root/Server-Two-gc.log"/>
                   <option value="-XX:+PrintGCDetails"/>
                   <option value="-XX:+PrintGCDateStamps"/>
                </jvm-options>
            </jvm>
           <socket-bindings port-offset="150"/>
        </server>
    </servers>
~~~
        -  Using the JVM option by referring to a JVM configuration in \<jvms\>
~~~
    <jvms>
           <jvm name="default1">
            <heap size="64m" max-size="256m"/>
            <permgen size="256m" max-size="256m"/>
            <jvm-options>
                   <option value="-Xloggc:/root/erverGC11.log"/>
                   <option value="-XX:+PrintGCDetails"/>
                   <option value="-XX:+PrintGCDateStamps"/>
              </jvm-options>

         </jvm>
      </jvms>

    <servers>
        <server name="server-one" group="main-server-group"/>
        <server name="server-two" group="other-server-group">
            <!-- server-two avoids port conflicts by incrementing the ports in
                 the default socket-group declared in the server-group -->
           <jvm name="default1">
            </jvm>
           <socket-bindings port-offset="150"/>
        </server>
    </servers>
~~~
- Startup commands:
   - host-master
~~~
./domain.sh -host-config=host-master.xml -Djboss.bind.address=10.66.218.10  -Djboss.bind.address.management=10.66.218.10
~~~
   - wanf2host
~~~
./domain.sh -Djboss.domain.master.address=10.66.218.10 --host-config=host-slave.xml -Djboss.bind.address=10.66.218.107 -Djboss.bind.address.management=10.66.218.107
~~~
    - wanf3host
~~~
./domain.sh -Djboss.domain.master.address=10.66.218.10 --host-config=host-slave.xml -Djboss.bind.address=10.66.218.108 -Djboss.bind.address.management=10.66.218.108
~~~