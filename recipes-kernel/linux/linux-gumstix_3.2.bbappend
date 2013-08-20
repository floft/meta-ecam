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
SRC_URI[defconfig.md5sum] = "b4ef4ea04ddf3d2e25787f8801b90638"
SRC_URI[defconfig.sha256sum] = "0a6dcb5af801f7e6d4a1855b0441b9b5e9f84eba12c3b826221a2007d8dc275c"
SRC_URI[libertas.md5sum] = "93b0c112972c72b8880c0f5c08ebac8c"
SRC_URI[libertas.sha256sum] = "cbd213d42808c9f226dd6740c20f666190c2d4330a89b837170fd5330f876019"
