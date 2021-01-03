FROM adoptopenjdk/openjdk8-openj9
VOLUME /tmp

ARG JAR_FILE=build/libs/halo.jar
ARG PORT=8520
ARG TIME_ZONE=Asia/Shanghai

ENV TZ=${TIME_ZONE}
ENV JAVA_OPTS="-Xms256m -Xmx256m"

COPY ${JAR_FILE} key3.jar

EXPOSE ${PORT}

ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -server -jar key3.jar
