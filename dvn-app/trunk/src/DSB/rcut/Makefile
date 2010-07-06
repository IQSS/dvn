# Insert copyright statement here

CC	= gcc
CFLAGS	= -O2 -Wall -pedantic -lm

all: install

build: all

clean:
	rm -f rcut

install: rcut
	install -m 0775 -d $(VDC_DIR)/bin
	install -m 0777 rcut $(VDC_DIR)/bin

rcut: rcut.c
	$(CC) $(CFLAGS) rcut.c -o rcut
