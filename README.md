meta-websdemo
==================

This is a useful demonstration recipe and application for demoing debug tools.

websdemo produces a simple stand-alone embedded web server based on the 
old GoAhead web server of years past.  I've used it for years as an
alternative to hello world demos ;)

It's a bit quirky, and needs a few things to run.

First it needs to find its own ip address in the hosts file.  The recipe does
this based either on ifconfig or ip utilities.

Second, it needs several settings on the make command line. This is accomplished
by defining symbols in Eclipse.  Those settings can be found in the recipe also.
See the recipe definition of CFLAGS_append for details.
