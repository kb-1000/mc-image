#pragma once

#include <stdint.h>
#include <stdlib.h>

#define LWJGL_MALLOC(function) org_lwjgl_##function

// Overridable memory management functions

typedef void* (*mallocPROC)         (size_t);
typedef void* (*callocPROC)         (size_t num, size_t size);
typedef void* (*reallocPROC)        (void *ptr, size_t size);
typedef void  (*freePROC)           (void *ptr);
typedef void* (*aligned_allocPROC)  (size_t alignment, size_t size);
typedef void  (*aligned_freePROC)   (void *ptr);

extern mallocPROC        org_lwjgl_malloc;
extern callocPROC        org_lwjgl_calloc;
extern reallocPROC       org_lwjgl_realloc;
extern freePROC          org_lwjgl_free;

extern aligned_allocPROC org_lwjgl_aligned_alloc;
extern aligned_freePROC  org_lwjgl_aligned_free;

