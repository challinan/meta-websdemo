# websdemo_1.3.1.bb
DESCRIPTION = "This is an open source embedded web server"
LICENSE = "GPLv2"
PR = "r0"

# These flags are required to configure the software
CFLAGS_append = " -DLINUX -DOS=LINUX -DWEBS -DUEMF -DUSER_MANAGEMENT_SUPPORT -DDIGEST_ACCESS_SUPPORT"

LIC_FILES_CHKSUM = "file://license.txt;md5=2ae61543257b9d9a653abe3fc0ddca22"

SRC_URI = "                                                       \
	https://s3.amazonaws.com/mel5-etc/websdemo-1.3.1.tar.gz   \
	file://change-web-root.patch                              \
	file://websdemo-glibc-update.patch                        \
"

SRC_URI[md5sum] = "2ab431fd6e437b47a8aba80e52c87355"
SRC_URI[sha256sum] = "38069a8e979ee87a86367b8bdb463572d43c9bcd18b3ba0dca2c65f7072b5818"

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

