FROM debian:latest

RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY challenge /app/challenge
COPY flag.txt /app/flag.txt

RUN chmod +x /app/challenge

EXPOSE 4441

CMD ["nc", "-lk", "-p", "4441", "-e", "./challenge"]
