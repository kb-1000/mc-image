#pragma once

#include "lwjgl_malloc.h"

#ifdef __linux__
#include <alloca.h>
#endif

// only add those used by MC

#define STBI_MALLOC(sz)    org_lwjgl_malloc(sz)
#define STBI_REALLOC(p,sz) org_lwjgl_realloc(p,sz)
#define STBI_FREE(p)       org_lwjgl_free(p)
#define STBI_FAILURE_USERMSG
#define STBI_ASSERT(x)
#ifdef LWJGL_WINDOWS
    #define STBI_WINDOWS_UTF8
#endif
#include "stb_image.h"

#define STBIR_MALLOC(size,c) org_lwjgl_malloc(size)
#define STBIR_FREE(ptr,c)    org_lwjgl_free(ptr)
#define STBIR_ASSERT(x)
#include "stb_image_resize.h"


#define STBIW_MALLOC(sz)    org_lwjgl_malloc(sz)
#define STBIW_REALLOC(p,sz) org_lwjgl_realloc(p,sz)
#define STBIW_FREE(p)       org_lwjgl_free(p)
#define STBIW_ASSERT(x)
#ifdef LWJGL_WINDOWS
    #define STBIW_WINDOWS_UTF8
    #define STBI_MSC_SECURE_CRT
#endif
#include "stb_image_write.h"

#define STBTT_malloc(x,u)  ((void)(u),org_lwjgl_malloc(x))
#define STBTT_free(x,u)    ((void)(u),org_lwjgl_free(x))
#define STBTT_assert
#define STBRP_ASSERT
#include "stb_rect_pack.h"
#include "stb_truetype.h"

#ifndef STB_VORBIS_IMPLEMENTATION
#define STB_VORBIS_HEADER_ONLY
#endif
#include "stb_vorbis.c"
