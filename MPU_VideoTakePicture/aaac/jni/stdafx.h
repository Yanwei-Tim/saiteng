// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

#pragma once

#ifndef __linux__ 
#include "targetver.h"

#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
#define AAAC 1
// Windows Header Files:
#include <windows.h>
#else
//----------------------linux module------------------------------------
#include<string.h>
#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from
#define strncpy_s(src, len,dst,n) strncpy(src,dst,len)
#define strcpy_s(src, len,dst) strcpy(src,dst)
#define memcpy_s(dst,m,src,n)	memcpy(dst,src,m)
#define sscanf_s sscanf
#define sprintf_s snprintf
#define timeGetTime() ((int)(time()*1000LL))
#define __int64 long long
#endif


// TODO: reference additional headers your program requires here

#define  AAAC 1
#include "AAAC.h"
