#       Copyright 2017-2020 IBM Corp All Rights Reserved

#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at

#       http://www.apache.org/licenses/LICENSE-2.0

#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

# FROM websphere-liberty:microProfile3
FROM openliberty/open-liberty:kernel-java11-openj9-ubi

FROM maven:3.6-jdk-11-slim AS build
COPY . /usr/
RUN mvn -f /usr/pom.xml clean package

# Following line is a workaround for an issue where sometimes the server somehow loads the built-in server.xml,
# rather than the one I copy into the image.  That shouldn't be possible, but alas, it appears to be some Docker bug.
RUN rm /config/server.xml
ENV OPENJ9_SCC=false

COPY --from=build /usr/target/trader-1.0-SNAPSHOT.war /opt/ol/wlp/usr/servers/defaultServer/apps/TraderUI.war
COPY --chown=1001:0 server.xml /config/server.xml
COPY --chown=1001:0 jvm.options /config/jvm.options
COPY --chown=1001:0 server/target/server-1.0-SNAPSHOT.war /config/apps/looper.war
COPY --chown=1001:0 client/target/client-1.0-SNAPSHOT.jar /loopctl.jar
COPY --chown=1001:0 key.p12 /config/resources/security/key.p12
COPY --chown=1001:0 trust.p12 /config/resources/security/trust.p12
COPY --chown=1001:0 keystore.xml /config/configDropins/defaults/keystore.xml
COPY --chown=1001:0 client/loopctl.sh /loopctl.sh

EXPOSE 9080
USER root
RUN chmod 777 /opt/ol/wlp/usr/servers/defaultServer
RUN yum -y install shadow-utils
RUN groupadd -g 1000590000 appgrp && useradd -l -r -d /home/appuser -u 1000590000 -g appgrp appuser && chown -R appuser:appgrp /opt/ol/wlp && chown -R appuser:appgrp /logs
USER appuser
COPY ibm-cloud-apm-dc-configpack.tar /opt/
COPY javametrics.liberty.icam-1.2.1.esa /opt/
RUN mkdir -p /opt/ol/wlp/usr/extension/lib/features/
RUN cd /tmp && jar xvf /opt/javametrics.liberty.icam-1.2.1.esa && mv /tmp/wlp/liberty_dc /opt/ol/wlp/usr/extension/ && mv /tmp/OSGI-INF/SUBSYSTEM.MF /opt/ol/wlp/usr/extension/lib/features/javametrics.liberty.icam-1.2.1.mf
COPY silent_config_liberty_dc.txt /opt/ol/wlp/usr/extension/liberty_dc/bin/
USER root
RUN chmod 777 /opt/ol/wlp/usr/extension/*
RUN chmod 777 /opt/ol/wlp/usr/extension/lib/*
RUN chmod 777 /opt/ol/wlp/usr/extension/liberty_dc/*
RUN chmod 777 /opt/ol/wlp/usr/extension/liberty_dc/bin/*
USER appuser
RUN /opt/ol/wlp/usr/extension/liberty_dc/bin/config_unified_dc.sh -silent

RUN configure.sh
