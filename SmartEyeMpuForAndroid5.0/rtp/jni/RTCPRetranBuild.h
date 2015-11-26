#ifndef RTCP_RETRAN_BUILD_H
#define RTCP_RETRAN_BUILD_H
#include "pj/types.h"

#define LOSS_LENGTH	17
#define FOUR_CHARS_TO_INT(a, b, c, d) (a<<24 | b<<16 | c<<8 | d)

typedef struct _rtcp_retran_header
{
#if defined(PJ_IS_BIG_ENDIAN) && PJ_IS_BIG_ENDIAN!=0
	unsigned	    version:2;	/**< packet type            */
	unsigned	    p:1;	/**< padding flag           */
	unsigned	    subtype:5;	/**< varies by payload type */
	unsigned	    pt:8;	/**< payload type           */
#else
	unsigned	    subtype:5;	/**< varies by payload type */
	unsigned	    p:1;	/**< padding flag           */
	unsigned	    version:2;	/**< packet type            */
	unsigned	    pt:8;	/**< payload type           */
#endif
	unsigned	    length:16;	/**< packet length          */
	pj_uint32_t	    ssrc;	/**< SSRC identification    */
	pj_uint32_t		name; /*seak*/
	pj_uint32_t		csrc;
} rtcp_retran_header;

typedef struct _rtcp_retran_unit
{
	pj_uint16_t seq;
	pj_uint16_t mask;
} rtcp_retran_unit;

class RTCPRetranBuilder
{
private:
	char *m_pBuf;
	int m_iLength;
	int m_iPos;
	int m_iSSRC;
	int m_iCSRC;
	int m_iLen;
	rtcp_retran_unit *m_pNext;
public:
	RTCPRetranBuilder(char *rtcp, int len, int ssrc = 0, int csrc = 0);
	bool InitBuild();
	rtcp_retran_unit* BuildNextPacket(int seq);
	int EndBuild();
	static bool SetBitMask(rtcp_retran_unit* unit, pj_uint16_t nextseq);

	rtcp_retran_header* GetRetranHeader();
	rtcp_retran_unit* GetNextPacket();
	static int* GetBitMask(rtcp_retran_unit* unit);
};

#endif