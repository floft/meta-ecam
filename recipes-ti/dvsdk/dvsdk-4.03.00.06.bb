DESCRIPTION = "DVSDK TI DSP drivers"
HOMEPAGE = "http://www.ti.com/tool/linuxdvsdk-dm37x"
SECTION = "drivers"
LICENSE = "TI"
DEPENDS = "virtual/kernel gst-ffmpeg gst-plugins-bad gst-plugins-good gst-plugins-ugly"
PR = "r1"
SRC_URI = "file://0001-Cmemk-Memory-Location.patch \
           file://0002-Build-drivers-not-examples.patch \
           file://0003-v4l2-define-changes.patch"
LIC_FILES_CHKSUM = "file://gstreamer-ti_svnr919/COPYING;md5=c8a292be1d17ba07ecbf7b49049cbf22"

DVSDK_INSTALL_DIR = "/usr/local/dvsdk"
S = "${DVSDK_INSTALL_DIR}/"

inherit update-rc.d
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "loadmodule-rc"
INITSCRIPT_PARAMS_${PN} = "defaults"

# NOTES:
# * You must download DVSDK from the website above and install somewhere such
#   as in /usr/local/dvsdk and change DVSDK_INSTALL_DIR accordingly. It will
#   probably only install on Ubuntu 10.04 32-bit.
# * Then, you need to `sudo chown -R user:user /usr/local/dvsdk' before
#   building.
# * You must run `bitbake -f -c compile virtual/kernel' if source doesn't exist.
# * Check to verify that the .../linux-gumstix/3.2-r2 path is correct for
#   kernel source code.
# * If running this multiple times, you might need to `bitbake -c clean dvsdk'
#   and in the DVSDK directory, something like `git add -A; git reset --hard
#   HEAD' to set it back to the original files if you set it up in git or
#   reinstall DVSDK.

do_configure() {
    # Couldn't put this in the patch since the directories will differ and
    # other makefiles include this file. If you need to change the kernel path
    # and have already compiled DVSDK, just move this to do_compile() so you
    # don't have to do a `bitbake -c clean dvsdk'
    sed -ri "
        s#^(DVSDK_INSTALL_DIR=).*\$#\1${DVSDK_INSTALL_DIR}#
        s#^(EXEC_DIR=).*\$#\1${D}#
        s#^(LINUXKERNEL_INSTALL_DIR=).*\$#\1${TMPDIR}/work/overo-poky-linux-gnueabi/linux-gumstix/3.2-r2/git#" \
        "${DVSDK_INSTALL_DIR}/Rules.make"

    # If these were generated on somebody else's system, the paths will be
    # completely wrong.
    find "${DVSDK_INSTALL_DIR}" -name package.mak -delete

    # It doesn't create these automatically, and since I'm using git to reset
    # to the original state, and it doesn't keep track of empty directories...
    # List from: `find /usr/local/dvsdk -depth -name lib -empty' but for some
    # reason only this one is needed. Probably since it's not in a .../package/
    # directory.
    mkdir -p "${DVSDK_INSTALL_DIR}/c6accel_1_01_00_07/soc/c6accelw/lib"
}

do_compile() {
    # -Wl,-O1 apparently isn't implemented with this embedded compiler.
    oe_runmake all LDFLAGS="${LDFLAGS//-Wl,-*/}" \
        BUILD_LDFLAGS="${BUILD_LDFLAGS//-Wl,-*/}" \
        TARGET_LDFLAGS="${TARGET_LDFLAGS//-Wl,-*/}"
}

do_install() {
    oe_runmake install
    
    # DVSDK puts this file here, and unless we remove it update-rc.d won't set
    # any additional symlinks
    rm "${D}/etc/rc3.d/S99loadmodule-rc"
    rmdir "${D}/etc/rc3.d"
}

FILES_${PN} = "/usr/lib/gstreamer-0.10/* /usr/share/ti/* /usr/share/ti/c6run-apps/* /usr/share/ti/c6run-apps/*/*/* /usr/share/ti/ti-codecs-server/* /usr/share/ti/c6accel-apps/* /usr/share/ti/c6accel-apps/test_files/* /usr/share/ti/ti-dmai-apps/* /usr/share/ti/ti-dsplink-examples/* /lib/modules/3.2.0-svn430/kernel/drivers/dsp/* /etc/init.d/* /etc/rc*.d/*"
FILES_${PN}-dev = ""
FILES_${PN}-dbg = "/usr/lib/gstreamer-0.10/.debug/* /usr/share/ti/c6run-apps/.debug /usr/share/ti/c6run-apps/.debug/* /usr/share/ti/c6accel-apps/.debug /usr/share/ti/c6accel-apps/.debug/* /usr/share/ti/ti-dmai-apps/.debug/* /usr/share/ti/ti-dsplink-examples/.debug/*"

# Ignore GNU_HASH warnings that show up as errors
INSANE_SKIP_${PN} = "ldflags"

# This will fail to build if we have more than one job, unfortunately.
PARALLEL_MAKE = ""
BBCLASSEXTEND = "native"

# First for `bitbake dvsdk', second for including in the image file
PROVIDES = "dvsdk"
RPROVIDES_${PN} = "dvsdk"
