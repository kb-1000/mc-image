#!/usr/bin/env bash
rm -f libstb.a
clang -Istb -flto=full -c -o stb.o -O2 -DNDEBUG -march=native -mtune=native stb.c
llvm-ar rcs libstb.a stb.o
