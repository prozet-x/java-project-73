FROM gradle:7.4.0-jdk17

COPY ./ .

RUN gradle installDist

EXPOSE 8080

CMD ./build/install/app/bin/app
