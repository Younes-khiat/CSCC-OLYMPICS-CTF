FROM debian:latest

RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY securebank /app/securebank
COPY flag.txt /app/flag.txt

RUN chmod +x /app/securebank

EXPOSE 4444

CMD ["nc", "-lk", "-p", "4444", "-e", "./securebank"]