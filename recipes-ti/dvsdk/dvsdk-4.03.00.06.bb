DESCRIPTION = "DVSDK TI DSP drivers"
SECTION = "base"
DEPENDS = ""
PROVIDES = "dvsdk"
HOMEPAGE = "http://www.ti.com/tool/linuxdvsdk-dm37x"
LICENSE = "TI"
LIC_FILES_CHKSUM = "file://gstreamer-ti_svnr919/COPYING;md5=c8a292be1d17ba07ecbf7b49049cbf22"
SRC_URI = ""
PR = "r1"

DVSDK_INSTALL_DIR = "/home/garrett/dvsdk_work"
S = "${DVSDK_INSTALL_DIR}/"

# Note: must run `bitbake -f -c compile virtual/kernel' if source doesn't exist

do_compile() {
    # Couldn't put this in the patch since the directorys will differ and other
    # makefiles include this file
    sed -ri "
        s#^(DVSDK_INSTALL_DIR=).*\$#\1${DVSDK_INSTALL_DIR}#
        s#^(EXEC_DIR=).*\$#\1${D}#
        s#^(LINUXKERNEL_INSTALL_DIR=).*\$#\1${TMPDIR}/work/overo-poky-linux-gnueabi/linux-gumstix/3.2-r1/git#" \
        "${DVSDK_INSTALL_DIR}/Rules.make"

    #    s#^(CSTOOL_DIR=).*\$#\1/home/garrett/yocto/build/tmp/sysroots/i686-linux/usr/bin/cortexa8hf-vfp-neon-poky-linux-gnueabi#
    #    s#^(CSTOOL_PREFIX=).*\$#\1\$(CSTOOL_DIR)/arm-poky-linux-gnueabi-#
    #    s#^(CSTOOL_PATH=).*\$#\1\$(CSTOOL_DIR)#" \
    # Hard-coded names
    #sed -i "s#arm-arago-linux-gnueabi-#arm-poky-linux-gnueabi-#g" \
    #    "${DVSDK_INSTALL_DIR}/Makefile" \
    #    "${DVSDK_INSTALL_DIR}/dsplink_1_65_01_05_eng/dsplink/make/Linux/omap3530_2.6.mk"
    
    # Doesn't always apply if it's just in SRC_URI? If it doesn't work, and there isn't
    # a certain string in Makefile, assume we've already applied it.
    patch -p1 -i "${WORKDIR}/0001-Applied-smaller-patch.patch" || \
        grep "components: cmem" "${DVSDK_INSTALL_DIR}/Makefile" &>/dev/null

    # If these were generated on somebody else's system, the paths will be completely wrong.
    find "${DVSDK_INSTALL_DIR}" -name package.mak -delete

    # Yes, this needs -j1. LDFLAGS contained "-Wl,-O1" which apparently isn't implemented.
    make -j1 LDFLAGS= BUILD_LDFLAGS= TARGET_LDFLAGS=
}

do_install () {
    make install
}

PARALLEL_MAKE = ""
BBCLASSEXTEND = "native"
