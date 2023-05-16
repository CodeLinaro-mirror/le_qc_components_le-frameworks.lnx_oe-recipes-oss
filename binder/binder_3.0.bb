inherit autotools pkgconfig useradd systemd

DESCRIPTION = "Android Binder support"
HOMEPAGE = "http://developer.android.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

DEPENDS += "liblog libcutils libutils system-core-headers libselinux glib-2.0"

FILESEXTRAPATHS:prepend = "${WORKSPACE}/frameworks/:"
SRC_URI   = "file://binder"

S = "${WORKDIR}/binder"

WITH_SYSTEMD ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemd', '',d)}"
WITH_SYSTEMD:sxrneo = ""
WITH_SYSTEMD:trustedvm = ""

EXTRA_OECONF += "--with-glib \
                 ${WITH_SYSTEMD} \
                "

# This recipe assumes kernel always compile for default arch even when
# multilib compilation is enabled. If kernel is 64bit and binder is compiled
# for 32bit due to multilib settings default 64bit IPC need to be supported
# as kernel is 64bit. Only when kernel is 32bit, 32bit IPC need to be enabled.
EXTRA_OECONF:append:arm = " \
    ${@bb.utils.contains('MULTILIB_VARIANTS', 'lib32','','--enable-32bit-binder-ipc',d)} \
"

# sdmsteppe uses 64bit IPC though userspace is 32bit.
EXTRA_OECONF:remove:sdmsteppe = "--enable-32bit-binder-ipc"

FILES:${PN} += "${systemd_unitdir}/system/"
SYSTEMD_SERVICE_${PN} += "binderfs.service servicemanager.service"
