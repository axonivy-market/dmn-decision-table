FROM maven:3.5-jdk-11

RUN addgroup --gid 1000 build && adduser --uid 1000 --gid 1000 --disabled-password --gecos "" build
