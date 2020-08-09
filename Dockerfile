FROM oracle/graalvm-ce:20.1.0-java11 as graalvm
RUN gu install native-image

COPY . /home/app/cli
WORKDIR /home/app/cli

RUN native-image --no-server -cp build/libs/cli-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/cli/cli /app/cli
ENTRYPOINT ["/app/cli"]
