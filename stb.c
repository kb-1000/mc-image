#include "lwjgl_malloc.h"

mallocPROC        org_lwjgl_malloc;
callocPROC        org_lwjgl_calloc;
reallocPROC       org_lwjgl_realloc;
freePROC          org_lwjgl_free;

aligned_allocPROC org_lwjgl_aligned_alloc;
aligned_freePROC  org_lwjgl_aligned_free;

#define STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_RESIZE_IMPLEMENTATION
#define STB_IMAGE_WRITE_IMPLEMENTATION
#define STB_TRUETYPE_IMPLEMENTATION
#define STB_VORBIS_IMPLEMENTATION
#define STB_RECT_PACK_IMPLEMENTATION
#include "stb.h"

void nstb_vorbis_get_info(stb_vorbis *f, stb_vorbis_info *__result) {
    *__result = stb_vorbis_get_info(f);
}
