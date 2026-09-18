inherit autotools pkgconfig useradd

DESCRIPTION = "Android Binder support"
HOMEPAGE = "http://developer.android.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "liblog libcutils libutils system-core-headers glib-2.0"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'libselinux', '', d)}"

FILESEXTRAPATHS:prepend = "${WORKSPACE}/frameworks/:"
SRC_URI   = "file://binder"

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

EXTRA_OECONF += " \
    --with-rootprefix=${root_prefix} \
"
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', '--enable-selinux', '', d)}"

do_install:append() {
   if ${@bb.utils.contains('EXTRA_OECONF', '--with-systemd', 'true', 'false', d)}; then
       install -d ${D}${systemd_unitdir}/system/
       install -d ${D}${systemd_unitdir}/system/sysinit.target.wants/
       install -d ${D}${systemd_unitdir}/system/local-fs.target.wants/

       # enable the service for sysinit.target
       ln -sf ${systemd_unitdir}/system/servicemanager.service \
           ${D}${systemd_unitdir}/system/sysinit.target.wants/servicemanager.service
       ln -sf ${systemd_unitdir}/system/binderfs.service \
           ${D}${systemd_unitdir}/system/local-fs.target.wants/binderfs.service
   fi
}
FILES:${PN} += "${systemd_unitdir}/system/"
