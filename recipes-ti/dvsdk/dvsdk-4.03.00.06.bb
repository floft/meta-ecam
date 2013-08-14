DESCRIPTION = "DVSDK TI DSP drivers"
SECTION = "base"
DEPENDS = "virtual/kernel"
HOMEPAGE = "http://www.ti.com/tool/linuxdvsdk-dm37x"
LICENSE = "TI"
LIC_FILES_CHKSUM = "file://gstreamer-ti_svnr919/COPYING;md5=c8a292be1d17ba07ecbf7b49049cbf22"

SRC_URI = "file://mods.patch"

#S = "${WORKDIR}/"
S = "${DVSDK_INSTALL_DIR}/"

PR = "r1"

#EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' \
#    'CFLAGS=${CFLAGS}' 'BUILDDIR=${S}'"

do_compile() {
    # Couldn't put this in the patch since the install directory will differ
    sed -i '/DVSDK_INSTALL_DIR=/d' "${DVSDK_INSTALL_DIR}/Rules.make"

    export DVSDK_INSTALL_DIR=/home/garrett/dvsdk2
    export LINUXKERNEL_INSTALL_DIR="${TMPDIR}/work/${PACKAGE_ARCH}-poky-${TARGET_OS}/linux-gumstix/3.2-r1/git"

    oe_runmake all -j1 EXEC_DIR="${D}" DESTDIR="${D}" SBINDIR="${sbindir}" \
        MANDIR="${mandir}" INCLUDEDIR="${includedir}"
}

do_install () {
    oe_runmake install EXEC_DIR="${D}" DESTDIR="${D}" SBINDIR="${sbindir}" \
        MANDIR="${mandir}" INCLUDEDIR="${includedir}"
}

PARALLEL_MAKE = ""
BBCLASSEXTEND = "native"
