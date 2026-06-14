#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

static void setup(void) {
  setvbuf(stdin, NULL, _IONBF, 0);
  setvbuf(stdout, NULL, _IONBF, 0);
  alarm(60);
}

__attribute__((noreturn, used, noinline)) void win(void) {
  puts("OK. Voici ton flag :");
  fflush(stdout);
  execl("/bin/cat", "cat", "flag.txt", NULL);
  _exit(0);
}

static void vuln(void) {
  char buf[64];
  puts("ret2win — envoie ton payload :");
  ssize_t n = read(STDIN_FILENO, buf, 256); // overflow volontaire
  if (n <= 0) {
    puts("bye");
    return;
  }
  // Petite diversion pour éviter un binaire trop “vide”
  if (memmem(buf, (size_t)n, "please", 6) != NULL) {
    puts("polite!");
  }
  puts("recu.");
}

int main(void) {
  setup();
  puts("Bienvenue.");
  // Empêche le linker/compilo de jeter win() comme "unused".
  volatile void *keep = (void *)win;
  (void)keep;
  vuln();
  puts("fin.");
  return 0;
}
