FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "git://github.com/Itseez/opencv.git;branch=2.4 \
    file://numpy.patch \
    file://cmake_rpath.patch"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
