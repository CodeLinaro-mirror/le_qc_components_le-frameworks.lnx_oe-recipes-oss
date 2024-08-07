inherit autotools pkgconfig useradd systemd

DESCRIPTION = "Android Binder support"
HOMEPAGE = "http://developer.android.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "liblog libcutils libutils system-core-headers libselinux glib-2.0"

FILESEXTRAPATHS:prepend = "${WORKSPACE}/frameworks/:"
SRC_URI   = "file://binder"
SRC_URI  += "file://servicemanager_auto.service \
             file://CVE-2020-0136.patch \
"

S = "${WORKDIR}/binder"

PACKAGECONFIG ??= "glib ${@bb.utils.filter('DISTRO_FEATURES','systemd', d)}"

PACKAGECONFIG[glib]    = "--with-glib, --without-glib, glib-2.0"
PACKAGECONFIG[systemd] = "--with-systemd, --without-systemd, systemd"

PACKAGECONFIG:trustedvm = "glib"

# This recipe assumes kernel always compile for default arch even when
# multilib compilation is enabled. If kernel is 64bit and binder is compiled
# for 32bit due to multilib settings default 64bit IPC need to be supported
# as kernel is 64bit. Only when kernel is 32bit, 32bit IPC need to be enabled.
EXTRA_OECONF:append:arm = " \
    ${@bb.utils.contains('MULTILIB_VARIANTS', 'lib32','','--enable-32bit-binder-ipc',d)} \
"

# sdmsteppe uses 64bit IPC though userspace is 32bit.
EXTRA_OECONF:remove:sdmsteppe = "--enable-32bit-binder-ipc"

do_install:append:sa525m() {
    rm -rf ${D}${systemd_unitdir}/system/servicemanager.service
    install -m 0664 ${WORKDIR}/servicemanager_auto.service ${D}${systemd_unitdir}/system/servicemanager.service
}
FILES:${PN} += "${systemd_unitdir}/system/"
SYSTEMD_SERVICE:${PN} += " ${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'servicemanager.service', '', d)}"
