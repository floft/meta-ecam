FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PRINC := "${@int(PRINC) + 1}"

# Sourceforge no longer hosts this 2.4.3 download, only 2.4.9
SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/opencv/OpenCV-${PV}.tar.bz2/c0a5af4ff9d0d540684c0bf00ef35dbe/OpenCV-${PV}.tar.bz2 \
           file://opencv-fix-pkgconfig-generation.patch \
           file://uyvy.patch \
"
