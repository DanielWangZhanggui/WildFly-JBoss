FROM daniel/jdk:openjdk-1.7
USER root
MAINTAINER zhanwang@redhat.com

ENV MAVEN_MIRROR http://mirror.olnevhost.net/pub/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz
ENV MAVEN_BIN apache-maven.tar.gz

RUN mkdir -p /opt/programs
RUN curl -sf -o /opt/programs/MAVEN_BIN -L $MAVEN_MIRROR
RUN yum install -y tar
RUN tar xvf MAVEN_BIN
RUN cd apache-maven-3.3.3/bin
RUN mvn clean install
