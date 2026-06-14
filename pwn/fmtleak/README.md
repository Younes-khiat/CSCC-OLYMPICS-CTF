# fmtleak

## Objectif

Exploiter une **format string** pour écrire dans un pointeur de fonction (`fp`) afin qu’il pointe vers `win()`.

Le programme passe 4 pointeurs vers `fp` en arguments de `printf` :

- `&fp`
- `&fp+2`
- `&fp+4`
- `&fp+6`

Donc tu peux faire 4 écritures 16-bit via `%hn`.

## Build local

```bash
make
./challenge
```

## Docker

```bash
docker build -t ctf-fmtleak .
docker run --rm -p 1337:1337 ctf-fmtleak
nc 127.0.0.1 1337
```
