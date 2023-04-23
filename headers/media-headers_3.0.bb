inherit autotools pkgconfig

DESCRIPTION = "Common multimedia headers installation"

HOMEPAGE = "http://developer.android.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend = "${WORKSPACE}/frameworks/:"
SRC_URI = "file://include/media"

S = "${WORKDIR}/include/media"

EXTRA_OECONF:append:kona = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"
EXTRA_OECONF:append:sdxlemur = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"
EXTRA_OECONF:append:qrbx210-rbx = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"
EXTRA_OECONF:append:sdmsteppe = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"
EXTRA_OECONF:append:neo = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"
EXTRA_OECONF:append:waipio = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"
EXTRA_OECONF:append:kalama = " BOARD_SUPPORTS_ANDROID_Q_AUDIO=true"

do_compile[noexec] = "1"

ALLOW_EMPTY:${PN} = "1"
