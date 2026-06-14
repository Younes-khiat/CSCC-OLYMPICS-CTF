#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

static void setup(void) {
  setvbuf(stdin, NULL, _IONBF, 0);
  setvbuf(stdout, NULL, _IONBF, 0);
  alarm(60);
}

static void read_n(char *dst, size_t n) {
  size_t got = 0;
  while (got < n) {
    ssize_t r = read(STDIN_FILENO, dst + got, n - got);
    if (r <= 0)
      break;
    got += (size_t)r;
  }
}

static unsigned long read_u64(void) {
  char buf[32];
  memset(buf, 0, sizeof(buf));
  if (!fgets(buf, sizeof(buf), stdin))
    exit(0);
  return strtoul(buf, NULL, 0);
}

typedef struct Note {
  void (*print)(struct Note *self);
  char data[0x50];
} Note;

typedef struct Profile {
  void (*action)(void);
  char name[0x50];
} Profile;

static Note *g_note = NULL;
static Profile *g_prof = NULL;

static void note_print(Note *self) {
  puts("NOTE:");
  write(STDOUT_FILENO, self->data, strnlen(self->data, sizeof(self->data)));
  puts("");
}

static void profile_action(void) { puts("profile_action()"); }

__attribute__((noreturn)) static void win(void) {
  puts("OK. Voici ton flag :");
  execl("/bin/cat", "cat", "flag.txt", NULL);
  _exit(0);
}

static void menu(void) {
  puts("");
  puts("1) create_note");
  puts("2) delete_note");
  puts("3) edit_note");
  puts("4) show_note");
  puts("5) create_profile");
  puts("6) show_profile");
  puts("7) quit");
  printf("> ");
}

static void create_note(void) {
  if (g_note) {
    puts("note deja existe");
    return;
  }
  g_note = (Note *)malloc(sizeof(Note));
  g_note->print = note_print;
  memset(g_note->data, 0, sizeof(g_note->data));
  puts("data (max 0x50 bytes, raw) :");
  read_n(g_note->data, sizeof(g_note->data));
  puts("ok");
}

static void delete_note(void) {
  if (!g_note) {
    puts("no note");
    return;
  }
  free(g_note);
  // BUG volontaire: on ne met pas g_note a NULL -> UAF
  puts("deleted");
}

static void edit_note(void) {
  if (!g_note) {
    puts("no note");
    return;
  }
  puts("new bytes (sizeof(Note) raw) :");
  // BUG volontaire: on permet d'écraser aussi le pointeur de fonction
  // + UAF si note free/reuse
  read_n((char *)g_note, sizeof(Note));
  puts("ok");
}

static void show_note(void) {
  if (!g_note) {
    puts("no note");
    return;
  }
  g_note->print(g_note); // UAF si vtable/pointeur corrompu
}

static void create_profile(void) {
  if (g_prof) {
    puts("profile deja existe");
    return;
  }
  g_prof = (Profile *)malloc(sizeof(Profile)); // même taille que Note
  g_prof->action = profile_action;
  memset(g_prof->name, 0, sizeof(g_prof->name));
  puts("name (max 0x50 bytes, raw) :");
  read_n(g_prof->name, sizeof(g_prof->name));
  puts("ok");
}

static void show_profile(void) {
  if (!g_prof) {
    puts("no profile");
    return;
  }
  puts("PROFILE:");
  write(STDOUT_FILENO, g_prof->name, strnlen(g_prof->name, sizeof(g_prof->name)));
  puts("");
  puts("action...");
  g_prof->action();
}

int main(void) {
  setup();
  puts("uaf_tcache — UAF via chunk reuse (tcache)");
  printf("win @ %p\n", (void *)win);
  for (;;) {
    menu();
    unsigned long c = read_u64();
    switch (c) {
    case 1:
      create_note();
      break;
    case 2:
      delete_note();
      break;
    case 3:
      edit_note();
      break;
    case 4:
      show_note();
      break;
    case 5:
      create_profile();
      break;
    case 6:
      show_profile();
      break;
    case 7:
      puts("bye");
      return 0;
    default:
      puts("?");
      break;
    }
  }
}
