FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

PRINC := "${@int(PRINC) + 1}"

COMPATIBLE_MACHINE_overo = "overo"

BOOT_SPLASH = ""

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/gumstix/linux.git;branch=omap-3.2;protocol=git \
           file://0001-ecam-camera-changes.patch;name=camera \
           file://defconfig;name=defconfig \
           file://libertas-async-fwload.patch;name=libertas \
          "

SRC_URI[camera.md5sum] = "7cede0ff465db6c48d06a0cba5ede7a8"
SRC_URI[camera.sha256sum] = "d49f3a8fa1cdea4b934ebe78014df4f42717da625d4845895c3ccb157c9102ff"
SRC_URI[defconfig.md5sum] = "54c06ea26e1c881de8f455ea76b3289f"
SRC_URI[defconfig.sha256sum] = "bd8ea7fc8efd7c5507ff707312ce8e96f78e974f40b92bdfa772c03f40636b8d"
SRC_URI[libertas.md5sum] = "93b0c112972c72b8880c0f5c08ebac8c"
SRC_URI[libertas.sha256sum] = "cbd213d42808c9f226dd6740c20f666190c2d4330a89b837170fd5330f876019"

# Should probably go in a machine config file, but it'll work here.
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "ecam-driver"
module_autoload_ecam-driver = "v4l2_driver"
