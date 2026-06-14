# uaf_tcache

## Objectif

Exploiter une **use-after-free** : un pointeur vers `Note` reste utilisable après `free()`.

Astuce : `Note` et `Profile` ont la **même taille**, donc après `delete_note()`, un `create_profile()` va souvent réutiliser le même chunk (tcache).

## Build local

```bash
make
./challenge
```

## Docker

```bash
docker build -t ctf-uaf-tcache .
docker run --rm -p 1337:1337 ctf-uaf-tcache
nc 127.0.0.1 1337
```
