# ret2win

## Objectif

Overflow stack pour rediriger l’exécution vers la fonction `win()` (ret2win).

## Build local

```bash
make
./challenge
```

## Docker

```bash
docker build -t ctf-ret2win .
docker run --rm -p 1337:1337 ctf-ret2win
nc 127.0.0.1 1337
```

## Indices

- Pas de canary
- Pas de PIE
- Le décalage RIP est typiquement \(64 + 8 = 72\) bytes sur amd64.
