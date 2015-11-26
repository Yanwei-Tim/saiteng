#ifndef __LIB_FRAMEWORK_H__
#define __LIB_FRAMEWORK_H__
#include "yatengine.h"
#include "yatephone.h"
using namespace TelEngine;

namespace
{
class FrameworkModule;

class FrameworkModule : public Module
{
public:
	FrameworkModule();
	~FrameworkModule();
protected:
	virtual void initialize();
	virtual bool received(Message &msg, int id);
};

};

#endif
