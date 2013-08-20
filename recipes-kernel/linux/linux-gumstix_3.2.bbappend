FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

PRINC := "${@int(PRINC) + 1}"

COMPATIBLE_MACHINE_overo = "overo"

BOOT_SPLASH = ""

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/gumstix/linux.git;branch=omap-3.2;protocol=git \
           file://0001-ecam-camera-changes.patch \
           file://defconfig \
           file://libertas-async-fwload.patch \
          "

