#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

static void setup(void) {
  setvbuf(stdin, NULL, _IONBF, 0);
  setvbuf(stdout, NULL, _IONBF, 0);
  alarm(60);
}

static void safe(void) { puts("safe()"); }

__attribute__((noreturn)) static void win(void) {
  puts("OK. Voici ton flag :");
  execl("/bin/cat", "cat", "flag.txt", NULL);
  _exit(0);
}

int main(void) {
  setup();

  void (*fp)(void) = safe;

  puts("fmtleak — format string => écris dans un pointeur de fonction");
  printf("fp @ %p\n", (void *)&fp);
  printf("win @ %p\n", (void *)win);
  puts("Entre une ligne (format string) :");

  char buf[256];
  if (!fgets(buf, sizeof(buf), stdin)) {
    return 0;
  }

  printf(buf,
         &fp,
         (unsigned short *)((char *)&fp + 2),
         (unsigned short *)((char *)&fp + 4),
         (unsigned short *)((char *)&fp + 6),
         0x41);

  puts("\nAppel du pointeur...");
  printf("fp = %p\n", (void *)fp);
  fp();
  puts("fin.");
  return 0;
}
