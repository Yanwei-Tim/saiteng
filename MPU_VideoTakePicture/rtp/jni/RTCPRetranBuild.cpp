#include "RTCPRetranBuild.h"
#include "pjlib.h"

RTCPRetranBuilder::RTCPRetranBuilder(char *rtcp, int len, int ssrc, int csrc)
: m_pBuf(rtcp), m_iLength(len), m_iPos(0), m_iSSRC(ssrc), m_iCSRC(csrc)
{
	m_iLen = 0;
	m_pNext = 0;
}

bool RTCPRetranBuilder::InitBuild()
{
	if (m_iLength <= sizeof(rtcp_retran_header))
	{
		return false;
	}
	rtcp_retran_header *retran = (rtcp_retran_header*) m_pBuf;
	retran->version = 2;
	retran->subtype = 0;
	retran->p = 0;
	retran->pt = 204;
	retran->length = 4;
	retran->ssrc = pj_htonl(m_iSSRC);
	retran->csrc = pj_htonl(m_iCSRC);
	retran->name = FOUR_CHARS_TO_INT('k', 'a', 'e', 's');
	m_iLen = retran->length;
	m_iPos += sizeof(rtcp_retran_header);
	return true;
}

rtcp_retran_unit* RTCPRetranBuilder::BuildNextPacket(int seq)
{
	if (m_iLength - m_iPos >= sizeof(rtcp_retran_unit))
	{
		rtcp_retran_unit *unit = (rtcp_retran_unit*)(m_pBuf + m_iPos);
		unit->seq = pj_htons(seq);
		unit->mask = 0;
		m_iPos += sizeof(rtcp_retran_unit);
		m_iLen ++;
		return unit;
	}
	return NULL;
}

int RTCPRetranBuilder::EndBuild()
{
	rtcp_retran_header *retran = (rtcp_retran_header*) m_pBuf;
	retran->length = pj_htons(m_iLen-1);
	return m_iPos;
}

// [seq, inc]
bool RTCPRetranBuilder::SetBitMask(rtcp_retran_unit *unit, pj_uint16_t inc)
{
	pj_uint16_t offset = inc - pj_ntohs(unit->seq);
	if (offset >= LOSS_LENGTH)
	{
		return false;
	}
	unit->mask |= (1<<(offset-1));
	return true;
}

int* RTCPRetranBuilder::GetBitMask(rtcp_retran_unit* unit)
{
	static int region[LOSS_LENGTH];
	region[0] = pj_ntohs(unit->seq);
	for (int i = 1; i < LOSS_LENGTH; i ++)
	{
		if (unit->mask & (1<<(i-1)))
		{
			region[i] = pj_uint16_t(pj_ntohs(unit->seq) + i);
		}
		else
		{
			region[i] = -1;
		}
	}
	return region;
}

rtcp_retran_header* RTCPRetranBuilder::GetRetranHeader()
{
	if (m_iLength >= sizeof(rtcp_retran_header))
	{
		return (rtcp_retran_header*) m_pBuf;
	}
	return NULL;
}

rtcp_retran_unit* RTCPRetranBuilder::GetNextPacket()
{
	char* end = m_pBuf + m_iLength;
	if (m_pNext == NULL)
	{
		m_pNext = (rtcp_retran_unit*)(m_pBuf + sizeof(rtcp_retran_header));
		if ((char*)m_pNext+sizeof(rtcp_retran_unit) > (char*)end)
		{
			m_pNext = NULL;
		}
	}
	else
	{
		m_pNext ++;
		if ((char*)m_pNext+sizeof(rtcp_retran_unit) > (char*)end)
		{
			m_pNext = NULL;
		}
	}
	return m_pNext;
}