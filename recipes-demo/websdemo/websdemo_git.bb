DESCRIPTION = "This is an open source embedded web server useful as a demo app that actually does something"
LICENSE = "GPLv2"
PR = "r0"

# These flags are required to configure the software
CFLAGS += " -DLINUX -DOS=LINUX -DWEBS -DUEMF -DUSER_MANAGEMENT_SUPPORT -DDIGEST_ACCESS_SUPPORT"

LIC_FILES_CHKSUM = "file://license.txt;md5=2ae61543257b9d9a653abe3fc0ddca22"
SRCREV = "7b8a49cd173bd2de2a153241afe5a2655beff491"
PV = "1.3.1+git${SRCPV}"
S = "${WORKDIR}/git"

SRC_URI = "git://github.com/challinan/webserver-demo.git;protocol=git \
	   file://change-web-root.patch                              \
	   file://websdemo-glibc-update.patch                        \
          "
do_install() {
	oe_runmake DESTDIR=${D} install
	echo "Unpacking data files"
	tar xzf websdemo-data.tgz
	install -d -m 0755 ${D}/${localstatedir}/www/web
	install -d -m 0755 ${D}/${localstatedir}/www/web/Error
	install -m 0644 ${S}/webpages/*.gif ${D}/${localstatedir}/www/web 
	install -m 0644 ${S}/webpages/*.jpg ${D}/${localstatedir}/www/web 
	install -m 0644 ${S}/webpages/*.htm ${D}/${localstatedir}/www/web 
	install -m 0644 ${S}/webpages/*.asp ${D}/${localstatedir}/www/web 
	install -m 0644 ${S}/webpages/Error/* ${D}/${localstatedir}/www/web/Error 
}

pkg_postinst_${PN} () {
#!/bin/sh
if [ "x$D" != "x" ]; then
        exit 1
fi

echo "Adding this hostname-IP to hosts"

HOSTNAME=`hostname`

if [ -x /sbin/ifconfig ]; then
	IPADDR=`ifconfig eth0 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1`
elif [ -x /bin/ip ]; then
	IPADDR=`ip addr show dev eth0 | grep inet | egrep -o '[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+'`
fi

echo "$IPADDR  $HOSTNAME"
echo "$IPADDR  $HOSTNAME" >>/etc/hosts

# Remove busybox-httpd because we need port #80
# Note, the "right" way to do this is to undefine HTTPD in busybox config
if [ -e /etc/init.d/busybox-httpd ]; then
	echo "Removing busybox-httpd"
	# opkg-cl remove busybox-httpd
	update-rc.d -f busybox-httpd remove
fi 
}

