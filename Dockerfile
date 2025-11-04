FROM openjdk:8
LABEL authors="joao"

ENTRYPOINT ["top", "-b"]