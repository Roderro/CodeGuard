FROM openjdk:11-jre-slim

RUN apt-get update && \
    apt-get install -y net-tools && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /SMMPPSim
COPY . /SMMPPSim/
EXPOSE 2775
CMD ["java", "-Djava.net.preferIPv4Stack=true", "-Djava.util.logging.config.file=conf/logging.properties", "-jar", "smppsim.jar", "conf/smppsim.props"]