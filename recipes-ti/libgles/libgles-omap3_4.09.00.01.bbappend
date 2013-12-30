FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
PRINC := "${@int(PRINC) + 1}"

# Fixes not being able to find the toolchain in
#   GFX_Linux_SDK/OGLES2/SDKPackage/Builds/OGLES2/LinuxARMV7/make_platform.mak
SRC_URI += "file://toolchain.patch"

# Hack from: http://lists.openembedded.org/pipermail/openembedded-core/2013-April/077746.html
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

# Build for overo
BINLOCATION_overo = "${S}/gfx_rel_es5.x"
DEFAULT_PREFERENCE = "1"
