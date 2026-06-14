CC ?= gcc

BIN := challenge
SRC := challenge.c

CFLAGS := -O0 -g -Wall -Wextra -Wpedantic \
  -fno-stack-protector -no-pie -fno-pie -z noexecstack
LDFLAGS :=

.PHONY: all clean

all: $(BIN)

$(BIN): $(SRC)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

clean:
	rm -f $(BIN)
