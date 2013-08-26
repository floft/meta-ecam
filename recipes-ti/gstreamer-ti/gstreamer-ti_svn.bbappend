FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PRINC := "${@int(PRINC) + 1}"

SRC_URI += "file://encode_thread.patch"

#
# A hack since this patch hasn't been added yet.
#  https://lists.yoctoproject.org/pipermail/meta-ti/2013-January/001994.html
#
INSANE_SKIP_${PN}-dev = "staticdev"
